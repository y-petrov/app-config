package ypetrov.appcfg.core.testenums;

import ypetrov.appcfg.core.ICfgExtra;

public enum Params02 implements ICfgExtra {
  W("CLASS", "default.hello"), X("MAJOR", "default.27"), Y("MINOR", null), Z("HOBBY", "default.true");

  private String key = null;
  private String defautValue = null;
  private String value = null;

  private static boolean sealed = false;

  private Params02(String key, String defaultValue) {
    this.key = key;
    this.defautValue = defaultValue;
  }

  @Override
  public String key() {
    return key;
  }

  @Override
  public String defaultValue() {
    return defautValue;
  }

  @Override
  public String value() {
    return value;
  }

  @Override
  public void setValue(String value) {
    if (!sealed) {
      this.value = value;
    }
  }

  public static void seal() {
    sealed = true;
  }
}
