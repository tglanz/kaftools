package tglanz.kaftools.fileconnector;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FileSinkConnector extends SinkConnector {

    private static final Logger logger = LoggerFactory.getLogger(FileSinkConnector.class);

    private FileSinkConnectorConfig config;

    @Override
    public void start(Map<String, String> props) {
        this.config = new FileSinkConnectorConfig(props);
    }

    @Override
    public Class<? extends Task> taskClass() {
        return FileSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        return IntStream.range(0, maxTasks).boxed()
            .map(this::createTaskConfig)
            .collect(Collectors.toList());
    }

    private Map<String, String> createTaskConfig(int taskId) {
        return new FileSinkTaskConfigBuilder()
                .withOutputFileName(this.config.getString(FileSinkConnectorConfig.CONFIG_OUTPUT_FILENAME))
                .withTaskId(taskId)
                .build();
    }

    @Override
    public void stop() {

    }

    @Override
    public ConfigDef config() {
        return FileSinkConnectorConfig.getConfigDef();
    }

    @Override
    public String version() {
        return Constants.VERSION;
    }
}
