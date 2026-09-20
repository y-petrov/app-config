package ypetrov.appcfg.sources;

import ypetrov.appcfg.core.ICfgExtra;
import ypetrov.appcfg.core.ICfgSource;
import ypetrov.appcfg.utils.EnumUpdater;

import java.util.Properties;

/**
 * Uses the JVM environment (-Dxxxxx command line options) as the source of config data for the provided Enum
 *
 * @param <E>
 */
public class JvmPropsSource<E extends Enum<E> & ICfgExtra> implements ICfgSource<E> {

    @Override
    public void loadCfg(Class<E> classE) throws Exception {
        Properties jvmProps = System.getProperties();
        EnumUpdater.updateEnum(jvmProps, classE);
    }
}
