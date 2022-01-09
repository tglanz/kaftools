package tglanz.kaftools.fileconnector;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FileSinkTaskConfigBuilder {
    private Optional<String> outputFileName;
    private Optional<Integer> taskId;

    public FileSinkTaskConfigBuilder withTaskId(int taskId) {
        this.taskId = Optional.of(taskId);
        return this;
    }

    public FileSinkTaskConfigBuilder withOutputFileName(String outputFileName) {
        this.outputFileName = Optional.of(outputFileName);
        return this;
    }

    public Map<String, String> build() {
        return new HashMap<>() {{
            put(FileSinkTask.CONFIG_TASK_ID, taskId.map(v -> v.toString()).orElseThrow());
            put(FileSinkTask.CONFIG_OUTPUT_FILENAME, outputFileName.orElseThrow());
        }};
    }
}
