package ypetrov.core;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ypetrov.core.enums.Params01;
import ypetrov.core.enums.Params02;

public class TestInit {
	
	@Test
	public void testInitSuccess() {
		Config.initConfig(Params01.class);
	}

	@Test
	public void testInitFail01() {
		assertThrows(IllegalArgumentException.class, () -> Config.initConfig(null));
	}
	
	@Test
	public void testInitFail02() {
		Config.initConfig(Params01.class);
		assertThrows(IllegalArgumentException.class, () -> Config.initConfig(Params02.class));
	}

}
