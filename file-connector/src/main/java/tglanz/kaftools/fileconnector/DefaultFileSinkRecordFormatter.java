package tglanz.kaftools.fileconnector;

import org.apache.kafka.connect.sink.SinkRecord;

public class DefaultFileSinkRecordFormatter implements FileSinkRecordFormatter {
    @Override
    public String format(SinkRecord record) {
        return String.format("Entry\n  - %s\n  - %s:%d\n  - %s\n  - %s",
                record.key(),
                record.timestampType(), record.timestamp(),
                record.headers(),
                record.value());
    }
}
