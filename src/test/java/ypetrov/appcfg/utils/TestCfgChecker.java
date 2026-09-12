package ypetrov.appcfg.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.junitpioneer.jupiter.SetSystemProperty.SetSystemProperties;
import ypetrov.appcfg.core.AppConfig;
import ypetrov.appcfg.core.ICfgExtra;
import ypetrov.appcfg.core.ICfgSource;
import ypetrov.appcfg.core.testenums.Params01;
import ypetrov.appcfg.core.testenums.Params02;
import ypetrov.appcfg.sources.JvmPropsSource;
import ypetrov.appcfg.sources.OsEnvSource;
import ypetrov.appcfg.sources.PropsFileSource;

public class TestCfgChecker {

  private static LinkedHashSet<ICfgSource<Params01>> sources1 = new LinkedHashSet<>();
  private static LinkedHashSet<ICfgSource<Params02>> sources2 = new LinkedHashSet<>();
  private static LinkedHashSet<ICfgSource<BadCfgBadKeys>> sources3 = new LinkedHashSet<>();
  private static LinkedHashSet<ICfgSource<BadCfgNoValue>> sources4 = new LinkedHashSet<>();
 

  @BeforeAll
  public static void beforeAll() {
    sources1.add(new PropsFileSource<Params01>("./src/test/resources/config01.properties"));
    sources1.add(new OsEnvSource<Params01>());
    sources1.add(new JvmPropsSource<Params01>());

    sources2.add(new OsEnvSource<Params02>());

    sources3.add(new OsEnvSource<BadCfgBadKeys>());

    sources4.add(new OsEnvSource<BadCfgNoValue>());
  }

  enum BadCfgBadKeys implements ICfgExtra {
    SAME_KEY1("abcd", null), SAME_KEY2("abcd", null);

    private String key;
    private String defaultValue;
    private String value;
    
    private BadCfgBadKeys(String key, String defaultValue) {
      this.key = key;
      this.defaultValue = defaultValue;
    }

    @Override
    public String key() {
      return key;
    }

    @Override
    public String value() {
      return value;
    }

    @Override
    public String defaultValue() {
      return defaultValue;
    }

    @Override
    public void setValue(String value) {
      this.value = value;
    }
  }

  enum BadCfgNoValue implements ICfgExtra {
    NO_VALUE("abcd", null);

    private String key;
    private String defaultValue;
    private String value;
    
    private BadCfgNoValue(String key, String defaultValue) {
      this.key = key;
      this.defaultValue = defaultValue;
    }

    @Override
    public String key() {
      return key;
    }

    @Override
    public String value() {
      return value;
    }

    @Override
    public String defaultValue() {
      return defaultValue;
    }

    @Override
    public void setValue(String value) {
      this.value = value;
    }
  }

  @Test
  @SetEnvironmentVariable(key = "MAX_TRIES", value = "osenv.5")
  @SetEnvironmentVariable(key = "MINOR", value = "math")
  @SetSystemProperties(@SetSystemProperty(key = "SAFE_MODE", value = "jvmprops.false"))
  public void testCheckNoValueSuccess() throws Exception {
    assertDoesNotThrow(() -> AppConfig.initConfig(Params02.class, sources2));
    CfgChecker<Params02> checker = new CfgChecker<Params02>();
    List<Params02> result = checker.checkNoValue(Params02.class);
    assertTrue(result.size() == 0, "Not defaultable parameters are detected");
  }

  @Test
  @SetEnvironmentVariable(key = "MAX_TRIES", value = "osenv.5")
  @SetSystemProperties(@SetSystemProperty(key = "SAFE_MODE", value = "jvmprops.false"))
  public void testCheckNoValueFailure() {
    assertDoesNotThrow(() -> AppConfig.initConfig(BadCfgNoValue.class, sources4));
    CfgChecker<BadCfgNoValue> checker = new CfgChecker<BadCfgNoValue>();
    List<BadCfgNoValue> result = checker.checkNoValue(BadCfgNoValue.class);
    assertTrue(result.size() >= 1); // The key "MINOR" is default-less and is not defined in the
                                    // sources
  }

  @Test
  @SetEnvironmentVariable(key = "MAX_TRIES", value = "osenv.5")
  @SetSystemProperties(@SetSystemProperty(key = "SAFE_MODE", value = "jvmprops.false"))
  public void testCheckUniqueKeysSuccess() {
    assertDoesNotThrow(() -> AppConfig.initConfig(Params01.class, sources1));
    CfgChecker<Params01> checker = new CfgChecker<Params01>();
    Map<String, List<Params01>> result = checker.checkUniqueKeys(Params01.class);
    assertTrue(result.size() == 0, "Duplicated keys are detected");
  }

  @Test
  public void testCheckUniqueFailure() {
    assertDoesNotThrow(() -> AppConfig.initConfig(BadCfgBadKeys.class, sources3));
    CfgChecker<BadCfgBadKeys> checker = new CfgChecker<BadCfgBadKeys>();
    Map<String, List<BadCfgBadKeys>> result = checker.checkUniqueKeys(BadCfgBadKeys.class);
    assertTrue(result.size() == 1);
  }
}
