package tglanz.kaftools.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine;

import tglanz.kaftools.cli.commands.ConsumeCommand;
import tglanz.kaftools.cli.commands.HealthCheckCommand;
import tglanz.kaftools.cli.commands.ProduceCommand;

@CommandLine.Command(
        name = "kaftools-cli", description = "A Cli for Kaftools",
        mixinStandardHelpOptions = true,
        subcommands = {
                HealthCheckCommand.class,
                ProduceCommand.class,
                ConsumeCommand.class,
        })
public class Cli {

    private static final Logger logger = LoggerFactory.getLogger(Cli.class);

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Cli()).execute(args);
        System.exit(exitCode);
    }
}