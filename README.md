# app-config

The project provides single configuration point for the Java applications. A configuration is a set of key-value pairs
with unique keys and flexible sources of the values. The values are easily accessible in the app code by key.

## 1 Features

- Provides flat (not hierarchical) configuration in form of `key` - `value` pairs
- All values are `java.lang.String` - it's up do app developers to cast them to `int`, `boolean` etc
- Single point of values loading
- Puristic and minimalistic - only foundational APIs of the Java SE Platform are used. Zero external dependencies
- Provides ability to define default values if some config parameter values allow this. E.g. datasource connection pool
  size allows default value, but external REST API URL does not.
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

The method `setValue()` is used by value source classes. The `key` and `defaultValue` are set by the constructors of the
enumeration members.

### 2.2 Initialize app-config

To use this model, I need to define what sources of config parameter values will be used. I decide that `myapp` will use
the following sources in following order:

1. `config.properties` file in `/home/ypetrov` directory
2. OS environment variables
3. JVM properties

Now, somewhere before very first need in config parameters occurs, let's initialize `ypetrov.appcfg.core.AppConfig`. I
do it at the very beginning of my `main()`:

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

There are possibilities that the app config data model and its use can bring some bad news. Two such possibilities -
`null` value of some not defaultable config parameter and not unique keys - have some sort of protection in the
`src/main/java` in the class `ypetrov.appcfg.utils.CfgChecker`.

The `null` value of config enum member may happen when member has `null` in the `defaultValue` parameter of its
constructor. The `null` in `defaultValue` is normal situation. A lot of config parameters can't have default value just
by their nature. For example JDBC connection string. But in such cases we rely on the external sources. Bad news is that
we can not guarantee that the value for such parameter is present there. The `CfgChecker`'s method
`public List<T> checkNoValue(Class<T> enumClass)` returns the list of config enum objects whose `value` is `null`. If
the returned list is not empty then we have potential problem. Clearly, the config enum must be initialized by
`AppConfig.initConfig(...)` method before check.

The second problem - the same key is linked to more than one config enum member (typo, just another careless contractor,
whatever...). The method `public Map<String, List<T>> checkUniqueKeys(Class<T> enumClass)` returns the map with not
unique keys. The key of map entry is the `key` of config parameter and the value of the map entry is a list of enum
members that have this `key`. Like the `checkNoValue` above, (a) if the returned map is not empty then we have the
problem and (b) this checker must be ran after `AppConfig.initConfig(...)`.

The `checkNoValue` checker can be used in prod code after config enum is initialized. Like:

```java
  ...
        AppConfig.initConfig(MyConfigEnum .class, theSources);

CfgChecker<MyConfigEnum> checker = new CfgChecker<MyConfigEnum>();
List<MyConfigEnum> noValResult = checker.checkNoValue(MyConfigEnum.class);
    if(noValResult.

size() >0){
        // Take appropriate actions
        }
        ...
```

The use of `checkUniqueKeys` is supposed to be a part of JUnit tests of the application.

#### 2.4.2 Order matters

Ordered collection of sources - the later source object (whose class implements interface `ICfgSource`) is added to the
collection, then the later its `loadCfg()` method will be called. It means that later added sources can overwrite the
work of previously added sources. If in the example above the `config.properties` file has the line
`init.admin.name=bob` and the JVM's shell has `export init.admin.name=john init.admin.password=doe` and JVM has been
launched with `-Dinit.admin.password=brown`, then the final values will be:

- `init.admin.name=john` OS env is used after `config.props` file and overwrites file data
- `init.admin.password=brown` JVM props are used after file and OS env and overwrites their settings

#### 2.4.3 Sealing

Another feature of `app-config` is the ability to prevents repetitive initializations. The `AppConfig`'s method `initConfig()` can seal itself at the end of its first
initialization and throws alarming exception automatically if repetitive attempts to initialize are taken. Or can igonre following calls for the same params enum. To seal or not to seal is determined by the last boolean argument of `initConfig()`.

## 3 Dear contributors (if any appears)!

1. Please use Google Java format
2. Please NO runtime dependencies AT ALL. Let's keep it puristic-minimalistic. Assume, for example, you need new source
   of config values that uses `.yml` (or `.xml`) files flattened to key-value pairs using dot-notation. Then create new
   project, set `app-config` as its dependency and then add as many other dependencies as you need.
