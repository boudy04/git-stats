package com.amagdy.gitstats;

import picocli.CommandLine;

@CommandLine.Command(name = "git-stats", mixinStandardHelpOptions = true,
        description = "Analyze git history: authors + file churn.")
public class App implements Runnable {

    @Override
    public void run() {
        new CommandLine(this).usage(System.out);
    }

    public static void main(String[] args) {
        CommandLine cmd = new CommandLine(new App());
        cmd.addSubcommand("authors", new StatsCommand());
        cmd.addSubcommand("files", new StatsCommand());
        System.exit(cmd.execute(args));
    }
}
