package ypetrov.appcfg.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import ypetrov.appcfg.core.ICfgExtra;

public class EnumUpdater {

  /**
   * Updates enum member's <code>value</code> variable if member's <code>key</code> variable is
   * present in <code>props</code>.
   * 
   * @param <T>
   * @param props
   * @param eClazz
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public static <E extends Enum<E> & ICfgExtra> void updateEnum(Properties props, Class<E> eClazz) {
    EnumUpdater.updateEnum(new HashMap((Map) props), eClazz);
  }

  /**
   * Updates enum member's <code>value</code> variable if member's <code>key</code> variable is
   * present in <code>props</code>.
   * 
   * @param <T>
   * @param props
   * @param eClazz
   */
  public static <T extends Enum<T> & ICfgExtra> void updateEnum(Map<String, String> props,
      Class<T> eClazz) {
    T enumConstants[] = eClazz.getEnumConstants();
    for (T eC : enumConstants) {
      String cfgKey = eC.key();
      String srcValue = props.get(cfgKey);

      if (srcValue != null && !srcValue.isBlank()) {
        // The source has value for this key - overwrite whatever value has been set by previous
        // sources
        eC.setValue(srcValue);
      } else if (eC.value() == null && eC.defaultValue() != null) {
        // Use default value
        eC.setValue(eC.defaultValue());
      }
    }
  }
}
