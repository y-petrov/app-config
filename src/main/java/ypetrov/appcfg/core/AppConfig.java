package ypetrov.appcfg.core;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The core class of app config. Provides a singleton that allows getting config parameter values by
 * their <code>enum</code>. The <code>Config</code> can use the number of value sources.
 * 
 * @param <T> the enumeration that contains all required configuration parameters and implements a
 *        few extra methods defined in the interface {@link ICfgExtra}. This enumeration must be
 *        written by the app developer[s].
 */
public class AppConfig<T extends Enum<T> & ICfgExtra> {

  private static final Logger logger = Logger.getLogger(AppConfig.class.getCanonicalName());

  private static boolean sealed = false;

  private static final Object lock = new Object();

  /**
   * The method initializes a config subsystem.
   * 
   * @param <T> - enum whose members are configuration parameters
   * @param paramsEnumClass - <code>java.lang.Class</code> object that represents enum
   *        <code>T</code>
   * @param sources - ordered collection of the unique implementations of <code>IcfgSources</code>
   * @throws IllegalArgumentException
   * @throws IllegalStateException
   */
  public static <T extends Enum<T> & ICfgExtra> void initConfig(Class<T> paramsEnumClass,
      LinkedHashSet<ICfgSource<T>> sources) throws Exception {
    if (!sealed) {
      if (paramsEnumClass == null) {
        logger.log(Level.SEVERE, "Attempt to initialize Config with 'null' as params Enum class");
        throw new IllegalArgumentException(
            "Config can't be initialized with 'null' as params Enum");
      } else if (sources == null || sources.size() == 0) {
        logger.log(Level.SEVERE, "No configuration parameters sources are provided.");
        throw new IllegalArgumentException("No configuration parameters sources are provided.");
      }
      synchronized (lock) {
        for (ICfgSource<T> src : sources) {
          src.loadCfg(paramsEnumClass);
        }

        // Disable 'initConfig()'
        AppConfig.sealed = true;

        // Disable value setter in all config enum members - works only if config enum is properly
        // done. Have to use reflection as enum's static method can't be called from *generic T*
        Method m = paramsEnumClass.getDeclaredMethod("seal");
        m.invoke(paramsEnumClass);
      }
    } else {
      String msg = String.format("The repetitive init attempt is taken. Provided params enum lass is [%s].",
          paramsEnumClass != null ? paramsEnumClass.getCanonicalName() : "null");
      logger.log(Level.SEVERE, msg);
      throw new IllegalStateException(msg);
    }
  }
}
