package tglanz.kaftools.cli.commands;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import tglanz.kaftools.cli.Defaults;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.concurrent.Callable;

@Command(name = "produce", mixinStandardHelpOptions = true, description = "Initiates an interactive producer")
public class ProduceCommand implements Callable<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(ProduceCommand.class);

    @CommandLine.Option(
            names = {"--bootstrap-servers"}, description = "Servers to connect to",
            defaultValue = Defaults.BOOTSTRAP_SERVERS, showDefaultValue = CommandLine.Help.Visibility.ALWAYS
    )
    private String bootstrapServers;

    @CommandLine.Option(
            names = {"-t", "--topic"}, description = "Topic to direct messages to",
            defaultValue = "default", showDefaultValue = CommandLine.Help.Visibility.ALWAYS
    )
    private String topic;

    @CommandLine.Option(
            names = {"--linger-ms"}, description = "Linger time in milliseconds",
            defaultValue = "0",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS
    )
    private int lingerMs;

    @CommandLine.Option(
            names = {"--retries"}, description = "Retry count",
            defaultValue = "0",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS
    )
    private int retries;

    private Properties createConfig() {
        var properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        properties.put(ProducerConfig.RETRIES_CONFIG, retries);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return properties;
    }

    private ProducerRecord<String, String> createRecord(String message) {
        return new ProducerRecord<>(topic, message);
    }

    @Override
    public Integer call() {
        var config = createConfig();

        logger.atDebug().log("Creating admin");
        try (var producer = new KafkaProducer<String, String>(config)) {
            logger.atDebug().log("Producer created successfully");

            // Endless stream
            new BufferedReader(new InputStreamReader(System.in))
                    .lines()
                    .map(this::createRecord)
                    .forEachOrdered(producer::send);
        }

        return 0;
    }
}
