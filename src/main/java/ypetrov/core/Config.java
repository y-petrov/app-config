package ypetrov.core;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Config<T extends Enum<T> & ICfgExtra> {
	
	private static final Logger logger = Logger.getLogger(Config.class.getCanonicalName());

	private Class<T> cfgParamsEnumClass = null;

	@SuppressWarnings("rawtypes")
	private static Config theConfig = null;
	
	private Set<T> cfgParams = new HashSet<T>();

	private Config() {
		super();
	}

	private static final Object lock = new Object();

	public static <T extends Enum<T> & ICfgExtra> void initConfig(Class<T> paramsEnumClass) throws IllegalArgumentException {
		if (paramsEnumClass == null) {
			logger.log(Level.SEVERE, "Attempt to initialize Config with 'null' as params Enum class");
			throw new IllegalArgumentException("Config can't be initialized with 'null' as params Enum");
		} else {
			synchronized (lock) {
				if (theConfig == null) {
					theConfig = new Config<T>(); 
					theConfig.cfgParamsEnumClass = paramsEnumClass;
				} else {
					if (! theConfig.cfgParamsEnumClass.equals(paramsEnumClass)) {
						logger.log(Level.SEVERE, "Initialization retry with different params Enum class");
						throw new IllegalArgumentException("Initialization retry with different params Enum class");
					}
				}
			}
		}
	}

	public static <T extends Enum<T> & ICfgExtra> Config<T> getConfig() {
		if (theConfig == null) {
			throw new IllegalStateException("The config is not initialized");
		}
		return (Config<T>) theConfig;
	}
	
}
