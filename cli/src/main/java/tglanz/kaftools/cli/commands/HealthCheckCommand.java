package tglanz.kaftools.cli.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import tglanz.kaftools.cli.Defaults;
import tglanz.kaftools.cli.model.ClusterInfo;
import tglanz.kaftools.cli.model.NodeInfo;

import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

@Command(
        name = "health-check",
        mixinStandardHelpOptions = true,
        description = "Perform health checks (Mostly connectivity and information"
)
public class HealthCheckCommand implements Callable<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(ProduceCommand.class);

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @CommandLine.Option(
            names = {"--bootstrap-servers"}, description = "Servers to connect to",
            defaultValue = Defaults.BOOTSTRAP_SERVERS,
            showDefaultValue = CommandLine.Help.Visibility.ON_DEMAND
    )
    String bootstrapServers;

    private Properties createAdminConfig() {
        var properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, 5000);
        properties.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 3000);
        return properties;
    }

    boolean verifyConnection(Admin admin) throws Exception {
        try {
            admin.describeCluster().clusterId().get();
            return true;
        } catch (ExecutionException ex) {
            return false;
        }
    }

    ClusterInfo getClusterInfo(Admin admin) throws  Exception {
        String clusterId = admin.describeCluster().clusterId().get();

        NodeInfo[] nodesInfo = admin.describeCluster().nodes().get().stream()
                .map(node -> new NodeInfo(
                        node.id(),
                        node.host(),
                        node.port(),
                        node.hasRack(),
                        node.rack())
                ).toArray(NodeInfo[]::new);

        return new ClusterInfo(clusterId, nodesInfo);
    }

    @Override
    public Integer call() throws Exception {
        var adminConfig = createAdminConfig();

        logger.atDebug().log("Creating admin");
        try (var admin = Admin.create(adminConfig)) {
            logger.atDebug().log("Admin successfully created");

            if (!verifyConnection(admin)) {
                logger.error("Unable to make initial contact bootstrap servers");
                return 1;
            }

            var clusterInfo  = getClusterInfo(admin);
            logger.atInfo().log("Cluster info - {}", gson.toJson(clusterInfo));
        }

        return 0;
    }
}
