package com.amagdy.gitstats;

import picocli.CommandLine;

@CommandLine.Command(name = "git-stats", mixinStandardHelpOptions = true,
        subcommands = {}, description = "Analyze git history: authors + file churn.")
public class App implements Runnable {

    @Override
    public void run() {
        // default: print usage if no subcommand given
        new CommandLine(this).usage(System.out);
    }

    public static void main(String[] args) {
        int exit = new CommandLine(new App()).execute(args);
        System.exit(exit);
    }
}