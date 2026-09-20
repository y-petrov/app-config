package ypetrov.appcfg.core.sources;

import org.junit.jupiter.api.Test;
import ypetrov.appcfg.core.testenums.Params01;
import ypetrov.appcfg.sources.PropsFileSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class TestPropsFileSource {

    @Test
    public void testDefaultCfgFileNotFound() {
        PropsFileSource<Params01> prm01 = new PropsFileSource<Params01>("./not/existing/file.properties");
        assertThrows(IOException.class, () -> prm01.loadCfg(Params01.class));
    }

    @Test
    public void testCfgFileIsLoaded() {
        PropsFileSource<Params01> fileSrcPrm01 =
                new PropsFileSource<Params01>("./target/test-classes/config01.properties");
        try {
            assertDoesNotThrow(() -> fileSrcPrm01.loadCfg(Params01.class));

            /*
             * Params01.A - file overwrites default
             * Params01.B - no file, default is null
             * Params01.C - no file, default is used
             */
            assertTrue(!Params01.A.value().equals(Params01.A.defaultValue()));
            assertTrue(Params01.B.value() == null);
            assertTrue(Params01.C.value().equals(Params01.C.defaultValue()));
        } catch (Exception e) {
            //
        }
    }

}
