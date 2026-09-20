package ypetrov.appcfg.core.testenums;

import ypetrov.appcfg.core.ICfgExtra;

import java.util.logging.Logger;

public enum Params01 implements ICfgExtra {

    A("API_URL", "default.aaaa"), B("SAFE_MODE", null), C("MAX_TRIES", "default.123");

    private String key = null;
    private String value = null;
    private String defaultValue = null;

    private static final Logger logger = Logger.getLogger(Params01.class.getCanonicalName());

    private Params01(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public String defaultValue() {
        return defaultValue;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.name()).append("=(");
        sb.append("key=[").append(key).append("],value=[");
        sb.append(value).append("],defaultValue=[");
        sb.append(defaultValue).append("]");
        sb.append(")");
        return sb.toString();
    }
}
