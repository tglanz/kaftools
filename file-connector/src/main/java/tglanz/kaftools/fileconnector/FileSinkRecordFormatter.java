package tglanz.kaftools.fileconnector;

import org.apache.kafka.connect.sink.SinkRecord;

public interface FileSinkRecordFormatter {
    String format(SinkRecord record);
}
