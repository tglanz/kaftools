package tglanz.kaftools.cli.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import tglanz.kaftools.cli.Defaults;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.Callable;

@Command(name = "consume", mixinStandardHelpOptions = true, description = "Consumes topic messages and log them")
public class ConsumeCommand implements Callable<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(ConsumeCommand.class);

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @CommandLine.Option(
            names = {"--bootstrap-servers"}, description = "Servers to connect to",
            defaultValue = Defaults.BOOTSTRAP_SERVERS,
            showDefaultValue = CommandLine.Help.Visibility.ON_DEMAND
    )
    private String bootstrapServers;

    @CommandLine.Option(
            names = {"-t", "--topic"}, description = "Topic to direct messages to",
            defaultValue = "default", showDefaultValue = CommandLine.Help.Visibility.ALWAYS
    )
    private String topic;

    @CommandLine.Option(
            names = { "-g", "--group"}, description = "Consumer group",
            defaultValue = "default", showDefaultValue = CommandLine.Help.Visibility.ALWAYS
    )
    private String group;

    @CommandLine.Option(
            names = { "--enable-auto-commit"}, description = "Enable auto commit",
            defaultValue = "false", showDefaultValue = CommandLine.Help.Visibility.ALWAYS
    )
    private Boolean enableAutoCommit;

    @CommandLine.Option(
            names = { "--auto-commit-interval"}, description = "Auto commit interval",
            defaultValue = "1000", showDefaultValue = CommandLine.Help.Visibility.ALWAYS
    )
    private int autoCommitInterval;


    private Properties createConfig() {
        var properties = new Properties();
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, group);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitInterval);
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        return properties;
    }

    private ProducerRecord<String, String> createRecord(String message) {
        return new ProducerRecord<>(topic, message);
    }

    @Override
    public Integer call() {
        var config = createConfig();

        logger.atDebug().log("Creating a consumer instance");
        try (var consumer = new KafkaConsumer<String, String>(config)) {
            logger.atDebug().log("Consumer created successfully");

            consumer.subscribe(Arrays.asList(topic));

            while (true) {
                for (var record : consumer.poll(Duration.ofMillis(100))) {
                    logger.atInfo().log(gson.toJson(record));
                }
            }
        }
    }
}
