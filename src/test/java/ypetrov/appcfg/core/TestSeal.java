package ypetrov.appcfg.core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.LinkedHashSet;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import ypetrov.appcfg.core.testenums.Params01;
import ypetrov.appcfg.sources.OsEnvSource;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestSeal {

  // Just test the reflection works. Must be ran the last
  @Test
  @Order(100)
  public void testSeal() {
    assertDoesNotThrow(() -> Params01.seal());
  }

  // Params01: A("API_URL", "default.aaaa"), B("SAFE_MODE", null), C("MAX_TRIES", "default.123")
  // config01.properties: API_URL=file.****
  @Test
  @SetEnvironmentVariable(key = "MAX_TRIES", value = "osenv.5")
  @Order(1)
  public void testSealE2E() {
    LinkedHashSet<ICfgSource<Params01>> sources = new LinkedHashSet<>();
    sources.add(new OsEnvSource<Params01>());
    
    // 1st init - must be OK
    assertDoesNotThrow(() -> AppConfig.initConfig(Params01.class, sources));
    assertTrue("osenv.5".equals(Params01.C.value()));
    
    // attempt to re-init...
    assertThrows(IllegalStateException.class, () -> AppConfig.initConfig(Params01.class, sources));

    // Direct value set. Must not set.
    Params01.C.setValue("error");
    assertTrue("osenv.5".equals(Params01.C.value()));
  }

}
