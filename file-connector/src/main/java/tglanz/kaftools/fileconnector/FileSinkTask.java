package tglanz.kaftools.fileconnector;

import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;

public class FileSinkTask extends SinkTask {
    public static String CONFIG_TASK_ID = "task.id";
    public static String CONFIG_OUTPUT_FILENAME = "output.filename";

    private static final Logger logger = LoggerFactory.getLogger(FileSinkTask.class);

    private int id;
    private Writer writer;
    private FileSinkRecordFormatter recordFormatter;
    private boolean shouldFlush;

    @Override
    public String version() {
        return Constants.VERSION;
    }

    @Override
    public void start(Map<String, String> props) {
        this.id = Integer.parseInt(props.get(CONFIG_TASK_ID));

        var outputFileName = props.get(CONFIG_OUTPUT_FILENAME);
        this.writer = createWriter(outputFileName);

        this.recordFormatter = new DefaultFileSinkRecordFormatter(); // TODO: configuration/factory
        this.shouldFlush = true; // TODO: configuration

        logger.debug("Started task id={}", this.id);
    }

    private static Writer createWriter(String fileName) {
        try {
            var fileWriter = new FileWriter(fileName);
            return new BufferedWriter(fileWriter);
        } catch (IOException ex) {
            throw new RuntimeException(String.format(
                    "Failed opening file: %s",
                    fileName));
        }
    }

    private void writeLineOrThrowRuntimeException(String line) {
        try {
            writer.append(String.format("%s%n", line));
        } catch (Exception ex) {
            throw new RuntimeException("Failed appending line", ex);
        }
    }

    @Override
    public void put(Collection<SinkRecord> records) {
        records.stream()
                .map(recordFormatter::format)
                .forEachOrdered(this::writeLineOrThrowRuntimeException);

        flushIfNeeded();
    }

    private void flushIfNeeded() {
        if (!this.shouldFlush) {
            return;
        }

        try {
            writer.flush();
        } catch (IOException ex) {
            throw new RuntimeException("Failed to flush", ex);
        }
    }

    @Override
    public void stop() {
        try {
            writer.close();
        } catch (IOException ex) {
            throw new RuntimeException("Failed closing writer", ex);
        }
    }
}
