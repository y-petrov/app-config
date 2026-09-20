package ypetrov.appcfg.core;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The core class of app config. Provides a singleton that allows getting config parameter values by
 * their <code>enum</code>. The <code>Config</code> can use the number of value sources.
 *
 * @param <T> the enumeration that contains all required configuration parameters and implements a
 *            few extra methods defined in the interface {@link ICfgExtra}. This enumeration must be
 *            written by the app developer[s].
 */
public class AppConfig<T extends Enum<T> & ICfgExtra> {

    private static final Logger logger = Logger.getLogger(AppConfig.class.getCanonicalName());

    private static final Map<Class, Boolean> sealedConfigs = new HashMap<>();

    private static final Object lock = new Object();

    /**
     * The method initializes a config subsystem.
     *
     * @param <T>             - enum whose members are configuration parameters
     * @param paramsEnumClass - <code>java.lang.Class</code> object that represents enum
     *                        <code>T</code>
     * @param sources         - ordered collection of the unique implementations of <code>IcfgSources</code>
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     */
    public static <T extends Enum<T> & ICfgExtra> void initConfig(Class<T> paramsEnumClass,
                                                                  LinkedHashSet<ICfgSource<T>> sources, boolean sealIt) throws Exception {
        boolean sealed = AppConfig.sealedConfigs.get(paramsEnumClass) != null
                ? AppConfig.sealedConfigs.get(paramsEnumClass)
                : false;
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

                // Seal or not seal initConfig() for this particular enum
                AppConfig.sealedConfigs.put(paramsEnumClass, sealIt);
            }
        } else {
            String msg =
                    String.format("The repetitive init attempt is taken. Provided params enum class is [%s].",
                            paramsEnumClass != null ? paramsEnumClass.getCanonicalName() : "null");
            logger.log(Level.SEVERE, msg);
            throw new IllegalStateException(msg);
        }
    }
}
