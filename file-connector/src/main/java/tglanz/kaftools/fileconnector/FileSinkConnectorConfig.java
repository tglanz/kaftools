package tglanz.kaftools.fileconnector;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public class FileSinkConnectorConfig extends AbstractConfig {

    public static final String CONFIG_OUTPUT_FILENAME = "output.filename";

    public FileSinkConnectorConfig(Map<String, ?> originals) {
        super(getConfigDef(), originals);
    }

    public static ConfigDef getConfigDef() {
        return new ConfigDef()
                .define(CONFIG_OUTPUT_FILENAME,
                        ConfigDef.Type.STRING,
                        ConfigDef.Importance.HIGH,
                        "File name to write data to");
    }
}
