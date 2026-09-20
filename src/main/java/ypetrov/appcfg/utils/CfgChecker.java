package ypetrov.appcfg.utils;

import ypetrov.appcfg.core.ICfgExtra;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CfgChecker<T extends Enum<T> & ICfgExtra> {

    /**
     * Finds config items that have <code>null</code> in <code>value</value>. In normal situation it
     * must not happen. <code>value</code> is populated either from some provided source, or from
     * <code>defaultValue</code>. If the returned list is <b>not</b> empty then it means that some
     * <b>not-defaultable</b> config param doesn't have value in all used sources.
     *
     * @param enumClass - the object of the <code>java.lang.Class</code> class that represents config
     *                  enum.
     * @return - the list of enum members that have no value
     */
    public List<T> checkNoValue(Class<T> enumClass) {
        List<T> retVal = new LinkedList<T>();
        for (T enumMember : enumClass.getEnumConstants()) {
            if (enumMember.value() == null) {
                retVal.add(enumMember);
            }
        }
        return retVal;
    }

    /**
     * The method checks the uniqueness of the config keys. In normal situation all keys must be
     * unique. But in general it's impossible enforce programmatically as it relies on the
     * implementation of the <code>ICfgExtra</code> interface.
     *
     * @param enumClass - enumClass - the object of the <code>java.lang.Class</code> class that
     *                  represents config enum.
     * @return
     */
    public Map<String, List<T>> checkUniqueKeys(Class<T> enumClass) {
        Map<String, List<T>> retVal = new HashMap<String, List<T>>();

        // Walk through all config items and gather info
        for (T enumMember : enumClass.getEnumConstants()) {
            if (retVal.containsKey(enumMember.key())) {
                List<T> collector = retVal.get(enumMember.key());
                collector.add(enumMember);
            } else {
                List<T> collector = new LinkedList<>();
                collector.add(enumMember);
                retVal.put(enumMember.key(), collector);
            }
        }

        // Clean
        retVal.values().removeIf(value -> value.size() == 1);

        return retVal;
    }
}
