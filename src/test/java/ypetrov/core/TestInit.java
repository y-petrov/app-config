package ypetrov.core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.LinkedHashSet;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import ypetrov.appcfg.core.AppConfig;
import ypetrov.appcfg.core.ICfgSource;
import ypetrov.appcfg.sources.OsEnvSource;
import ypetrov.core.appcfg.testenums.Params01;
import ypetrov.core.appcfg.testenums.Params02;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestInit {

  @Test
  @Order(1)
  public void testInitFail01() {
    assertThrows(IllegalArgumentException.class,
        () -> AppConfig.initConfig(null, new LinkedHashSet<ICfgSource<Params01>>()));
  }

  @Test
  @Order(2)
  public void testInitSuccess() {
    LinkedHashSet<ICfgSource<Params01>> srcs1 = new LinkedHashSet<ICfgSource<Params01>>();
    srcs1.add(new OsEnvSource<Params01>());
    assertDoesNotThrow(() -> AppConfig.initConfig(Params01.class, srcs1));
  }

  @Test
  @Order(3)
  public void testInitFail02() {
    // AppConfig is already initialized by testInitSuccess(). Now attempt for 2nd init

    LinkedHashSet<ICfgSource<Params02>> srcs2 = new LinkedHashSet<ICfgSource<Params02>>();
    srcs2.add(new OsEnvSource<Params02>());
    assertThrows(IllegalStateException.class, () -> AppConfig.initConfig(Params02.class, srcs2));
  }

}
