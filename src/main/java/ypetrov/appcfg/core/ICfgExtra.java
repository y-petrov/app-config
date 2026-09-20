package ypetrov.appcfg.core;

public interface ICfgExtra {

    /**
     *
     * @return not blank String literal that is used as key in <code>key=value</code> expressions in
     * properties file, env vars definitions etc
     */
    public String key();

    /**
     *
     * @return the String that is obtained from the sources
     */
    public String value();

    /**
     *
     * @return the String that will be used if possible when
     * <code>value</above> is not provided by the sources. Either hard coded in the constructor or explicitly set to <code>null</code>
     * in the <code>enum</code> definition.
     */
    public String defaultValue();

    public void setValue(String value);
}
