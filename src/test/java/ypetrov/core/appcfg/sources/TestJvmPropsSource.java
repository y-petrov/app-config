package ypetrov.core.appcfg.sources;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.junitpioneer.jupiter.SetSystemProperty.SetSystemProperties;
import ypetrov.appcfg.sources.JvmPropsSource;
import ypetrov.core.appcfg.testenums.Params01;

public class TestJvmPropsSource {

  @Test
  @SetSystemProperties(@SetSystemProperty(key = "API_URL", value = "jvmprops.zzz"))
  public void testparams01() {
    JvmPropsSource<Params01> jpSrc = new JvmPropsSource<>();
    try {
      jpSrc.loadCfg(Params01.class);
      assertNotNull("jvmprops.zzz".equals(Params01.A.value()));
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

}
