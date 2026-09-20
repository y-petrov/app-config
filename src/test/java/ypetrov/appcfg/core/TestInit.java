package ypetrov.appcfg.core;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.junitpioneer.jupiter.SetSystemProperty.SetSystemProperties;
import ypetrov.appcfg.core.testenums.Params01;
import ypetrov.appcfg.core.testenums.Params02;
import ypetrov.appcfg.sources.JvmPropsSource;
import ypetrov.appcfg.sources.OsEnvSource;
import ypetrov.appcfg.sources.PropsFileSource;

import java.util.LinkedHashSet;

import static org.junit.jupiter.api.Assertions.*;

public class TestInit {

    private static LinkedHashSet<ICfgSource<Params01>> sources = new LinkedHashSet<>();
    private static LinkedHashSet<ICfgSource<Params02>> sources2 = new LinkedHashSet<>();

    @BeforeAll
    public static void beforeAll() {
        sources.add(new PropsFileSource<Params01>("./src/test/resources/config01.properties"));
        sources.add(new OsEnvSource<Params01>());
        sources.add(new JvmPropsSource<Params01>());

        sources2.add(new OsEnvSource<Params02>());
    }

    @Test
    public void testInitFail01() {
        assertThrows(IllegalArgumentException.class, () -> AppConfig.initConfig(null, null, false));
    }


    // Params01: A("API_URL", "default.aaaa"), B("SAFE_MODE", null), C("MAX_TRIES", "default.123")
    // config01.properties: API_URL=file.****

    @Test
    @SetEnvironmentVariable(key = "MAX_TRIES", value = "osenv.5")
    @SetSystemProperties(@SetSystemProperty(key = "SAFE_MODE", value = "jvmprops.false"))
    public void testInitCombo() {
        assertDoesNotThrow(() -> AppConfig.initConfig(Params01.class, sources, true)); // Seal initConfig for Params01
        assertTrue("file.****".equals(Params01.A.value()));
        assertTrue("jvmprops.false".equals(Params01.B.value()));
        assertTrue("osenv.5".equals(Params01.C.value()));

        // Init different config
        assertDoesNotThrow(() -> AppConfig.initConfig(Params02.class, sources2, false));

        // Repetitive for already iniy-ed
        assertThrows(IllegalStateException.class, () -> AppConfig.initConfig(Params01.class, sources, false));

        // Do 2nd init for not-sealed Params02
        assertDoesNotThrow(() -> AppConfig.initConfig(Params02.class, sources2, false));
    }

}
