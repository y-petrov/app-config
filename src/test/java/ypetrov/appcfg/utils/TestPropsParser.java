package ypetrov.appcfg.utils;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Hashtable;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import ypetrov.appcfg.core.testenums.Params02;

public class TestPropsParser {

  // Parms02:
  // W("CLASS", "default.hello"), //
  // X("MAJOR", "default.27"), //
  // Y("MINOR", null), //
  // Z("HOBBY", "default.true")

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Test
  public void testPprops2Enums() {
    Properties props = new Properties();
    props.setProperty("CLASS", "props.bye");
    // No props for "MAJOR"
    props.setProperty("blah.blah", "123");
    props.setProperty("MINOR", "props.minor");
    // No props for "HOBBY"
    
    EnumUpdater.updateEnum(new Hashtable(props), Params02.class);
    assertTrue("props.bye".equals(Params02.W.value()));
    assertTrue("props.minor".equals(Params02.Y.value()));
    
  }

}
