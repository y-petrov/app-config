package ypetrov.appcfg.sources;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import ypetrov.appcfg.core.ICfgExtra;
import ypetrov.appcfg.core.ICfgSource;
import ypetrov.appcfg.utils.EnumUpdater;

/**
 * Uses the .properties file as the source of config data for the provided Enum 
 * @param <E>
 */
public class PropsFileSource<E extends Enum<E> & ICfgExtra> implements ICfgSource<E> {

  private static final Logger logger = Logger.getLogger(PropsFileSource.class.getCanonicalName());

  private String propsFilePath = null;
  
  public PropsFileSource(String propsFilePath) {
    if (propsFilePath != null && propsFilePath.trim().length() > 0) {
      logger.log(Level.INFO, "Config properties file name is set to [{0}]", propsFilePath);
      this.propsFilePath = propsFilePath;
    } else {
      logger.log(Level.SEVERE,
          "Config properties file name can't be null or empty string");
      throw new IllegalArgumentException(
          "Config properties file name can't be null or empty string");
    }
  }

  @Override
  public void loadCfg(Class<E> classE) throws Exception {
    Properties props = new Properties();
    try (InputStream input = new FileInputStream(propsFilePath)) {
      props.load(input);
      EnumUpdater.updateEnum(props, classE);
    } catch (IOException ex) {
      throw ex;
    }
  }

}
