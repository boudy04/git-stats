package com.amagdy.gitstats;

import picocli.CommandLine;

@CommandLine.Command(name = "git-stats", mixinStandardHelpOptions = true,
        subcommands = {AuthorsCommand.class, FilesCommand.class},
        description = "Analyze git history: authors + file churn.")
public class App implements Runnable {

    @Override
    public void run() {
        new CommandLine(this).usage(System.out);
    }

    public static void main(String[] args) {
        int exit = new CommandLine(new App()).execute(args);
        System.exit(exit);
    }
}