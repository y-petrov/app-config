# app-config
The project provides single configuration point for the Java applications. A configuration is a set of key-value pairs with unique keys and flexible sources of the values. The values are easily accessible in the app code by key.

## 1 Features

- Provides flat (not hierarchical) configuration in form of `key` - `value` pairs
- All values are `java.lang.String` - it's up do app developers to cast them to `int`, `boolean` etc
- Single point of values loading
- Puristic and minimalistic - only foundational APIs of the Java SE Platform are used. Zero external dependencies
- Provides ability to define default values if some config parameter values allow this. E.g. datasource connection pool size allows default value, but external REST API URL does not.
- Use `enum` as config key instead of string literals eliminates hard to find typo errors
- Allows multiple sources of loading config parameter values with prioritizing the sources
- Three sources are already provided
    - Load from `.properties` file
    - Load from OS environment variables
    - Load from JVM properties
- Other sources can be added by implementing the interface `ypetrov.appcfg.core.ICfgSource`
    

## 2 How to use

Assume I'm developing the application `myapp` that needs the following externalized configuration parameters:

- Vendor API URL
- Max number of retries
- Delay between retries
- Initial admin username
- Initial admin password

### 2.1 Define configuration data model

Then the app's config data model will be implemented by the `enum` below:

```java
package example.myapp.config;

import ypetrov.appcfg.core.ICfgExtra;

public enum MyAppCfg implements ICfgExtra {

  // Enumeration members. One member per one configuration parameter.
  VENDOR_API_URL("vendor.api.url", null), // 'null' - no default value
  RETRIES_NUM("retries.num", "3"), //
  RETRIES_DELAY("retries.delay", "5"), //
  INIT_ADMIN_NAME("init.admin.name", "admin"), //
  INIT_ADMIN_PASSWORD("init.admin.password", "admin");

  private String key;
  private String value;
  private String defaultValue;

  // Constructor
  private MyAppCfg(String key, String defaultValue) {
    this.key = key;
    this.defaultValue = defaultValue;
  }

  @Override
  public String key() {
    return key;
  }

  @Override
  public String value() {
    return value;
  }

  @Override
  public String defaultValue() {
    return defaultValue;
  }

  @Override
  public void setValue(String value) {
    this.value = value;
  }
}

```

The method `setValue()` is used by value source classes. The `key` and `defaultValue` are set by the constructors of the enumeration members.

### 2.2 Initialize app-config

To use this model, I need to define what sources of config parameter values will be used. I decide that `myapp` will use the following sources in following order:

1. `config.properties` file in `/home/ypetrov` directory
2. OS environment variables
3. JVM properties

Now, somewhere before very first need in config parameters occurs, let's initialize `ypetrov.appcfg.core.AppConfig`. I do it at the very beginning of my `main()`:

```java
package example.myapp;

import java.util.LinkedHashSet;
import example.myapp.config.MyAppCfg;
import ypetrov.appcfg.core.AppConfig;
import ypetrov.appcfg.core.ICfgSource;
import ypetrov.appcfg.sources.JvmPropsSource;
import ypetrov.appcfg.sources.OsEnvSource;
import ypetrov.appcfg.sources.PropsFileSource;

public class MyAppMain {
  
  public static void main(String[] args) throws Exception {
    
    // App config init.
    // 1. Build ordered collection of unique value sources
    LinkedHashSet<ICfgSource<MyAppCfg>> sources = new LinkedHashSet<>();
    sources.add(new PropsFileSource<MyAppCfg>("/home/ypetrov/config.properties"));
    sources.add(new OsEnvSource<MyAppCfg>());
    sources.add(new JvmPropsSource<MyAppCfg>());
    // 2. Initialize config
    AppConfig.initConfig(MyAppCfg.class, sources);
    // That's it :)
    
    // Other code code code...
  }
}
```

### 2.3 Access config parameters in the application code

Nothing is simpler. Just get value of the `myapp.config.MyAppCfg` enumeration member that you need:

```java
  ...
  String vendorApiUrl = MyAppCfg.VENDOR_API_URL.value();
  ...
```

That's it.

### 2.4 Notes
Now a few comments.

#### 2.4.1 Houston, we have a problem!

After `AppConfig.initConfig()` completes its work it would be a good idea to check all members of config `enum` - what if some member has `null` in its `value`? It's quite possible if neither of used sources has value for some key. How to react on this is up to app designer - abort starting, disable functionality that relies on this config parameter, call 911, whatever else...

#### 2.4.2 Order matters

Ordered collection of sources - the later source object (whose class implements interface `ICfgSource`) is added to the collection, then the later its `loadCfg()` method will be called. It means that later added sources can overwrite the work of previously added sources. If in the example above the `config.properties` file has the line `init.admin.name=bob` and the JVM's shell has `export init.admin.name=john init.admin.password=doe` and JVM has been launched with `-Dinit.admin.password=brown`, then the final values will be:

- `init.admin.name=john` OS env is used after `config.props` file and overwrites file data
- `init.admin.password=brown` JVM props are used after file and OS env and overwrites their settings

#### 2.4.3 Sealing

Another feature of `app-config` is the ability to become immutable after all provided sources are polled and values are set in a proper enumeration members. The `AppConfig`'s method `initConfig()` seals itself at the end of its first initialization and throws alarming exception automatically if repetitive attempts to initialize are taken. But to disable `setValue()` method of enum members a little bit of coding must be done. To understand what must be done, take a look at `public static void seal() {}` in the interface `ICfgExtra`, then take a look how the method is overridden in interface implementation in `ypetrov.core.appcfg.testenums.Params01` in `src/test/java` and last - look how this method is called at the and of `AppConfig.initConfig()`. So, if we are interested in the value protection then the enum `MyAppConfig` above should look like this (the improvements are marked with `!!!`):

```java
package example.myapp.config;

import java.util.logging.Level;
import java.util.logging.Logger;
import ypetrov.appcfg.core.ICfgExtra;

public enum MyAppCfg implements ICfgExtra {

  // Enumeration members. One member per one configuration parameter.
  VENDOR_API_URL("vendor.api.url", null), // 'null' - no default value
  RETRIES_NUM("retries.num", "3"), //
  RETRIES_DELAY("retries.delay", "5"), //
  INIT_ADMIN_NAME("init.admin.name", "admin"), //
  INIT_ADMIN_PASSWORD("init.admin.password", "admin");

  private String key;
  private String value;
  private String defaultValue;

  private static final Logger logger = Logger.getLogger(MyAppCfg.class.getCanonicalName()); // !!!

  private static boolean sealed = false; // !!!

  // Constructor
  private MyAppCfg(String key, String defaultValue) {
    this.key = key;
    this.defaultValue = defaultValue;
  }

  @Override
  public String key() {
    return key;
  }

  @Override
  public String value() {
    return value;
  }

  @Override
  public String defaultValue() {
    return defaultValue;
  }

  @Override
  public void setValue(String value) {
    if (!sealed) { // !!!
      this.value = value;
    } else {
      logger.log(Level.WARNING,
          "Attempt to redefine already set and sealed config parameter name=[{0}],
          key=[{1}], value=[{2}] to the new value [{3}]", new Object[] {
          this.name(), this.key, this == INIT_ADMIN_PASSWORD ? "*****" : this.value,
          value});
    }
  }

  public static void seal() { // !!!
    sealed = true;
  }
}
```

The sealing is optional feature. Don't need it - don't use it.

## 3 Dear contributors (if any appears)!

1. Please use Google Java format
2. Please NO runtime dependencies AT ALL. Let's keep it puristic-minimalistic. If, for example, you need some special source of config values, let's say *sql select* it from remote RDBMS, then create new project, set `app-config` as its dependency and then add as many other dependencies as you need.
