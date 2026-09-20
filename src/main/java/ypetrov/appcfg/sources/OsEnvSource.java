package ypetrov.appcfg.sources;

import ypetrov.appcfg.core.ICfgExtra;
import ypetrov.appcfg.core.ICfgSource;
import ypetrov.appcfg.utils.EnumUpdater;

import java.util.Map;

/**
 * Uses operating system environment as the source of config data for the provided Enum
 *
 * @param <E>
 */
public class OsEnvSource<E extends Enum<E> & ICfgExtra> implements ICfgSource<E> {

    @Override
    public void loadCfg(Class<E> classE) throws Exception {
        Map<String, String> osEnv = System.getenv();
        EnumUpdater.updateEnum(osEnv, classE);
    }
}
