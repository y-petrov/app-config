package ypetrov.appcfg.core.sources;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import ypetrov.appcfg.core.testenums.Params01;
import ypetrov.appcfg.sources.OsEnvSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestOsEnvSource {

    @Test
    @SetEnvironmentVariable(key = "API_URL", value = "osenv.*****")
    public void testParams01() {
        OsEnvSource<Params01> osEnvSrcPrm01 = new OsEnvSource<Params01>();

        try {
            osEnvSrcPrm01.loadCfg(Params01.class);
            // Params01.A - key "API_URL", defaultValue "default.aaaa"
            assertTrue("osenv.*****".equals(Params01.A.value()));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
