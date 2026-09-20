package ypetrov.appcfg.core;

/**
 * The interface of the configuration parameters loader.
 *
 * @param <T> - the enum's class
 */
public interface ICfgSource<T extends Enum<T> & ICfgExtra> {

    /**
     * The method can update (or not update) the <code>value</code> of the members of the enumeration presented
     * by <code>enumClass</code> class.
     *
     * @param enumClass
     * @return
     * @throws Exception
     */
    public void loadCfg(Class<T> enumClass) throws Exception;

}
