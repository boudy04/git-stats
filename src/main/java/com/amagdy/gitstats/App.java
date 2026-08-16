package com.amagdy.gitstats;

import picocli.CommandLine;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

@CommandLine.Command(name = "git-stats", mixinStandardHelpOptions = true,
        version = "git-stats 1.0.0",
        description = "Analyze git history: authors, files, activity, dirs, repo.")
public class App implements Runnable {

    @Override
    public void run() {
        new CommandLine(this).usage(System.out);
    }

    public static void main(String[] args) {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err), true, StandardCharsets.UTF_8));
        CommandLine cmd = new CommandLine(new App());
        cmd.addSubcommand("authors", new StatsCommand());
        cmd.addSubcommand("files", new StatsCommand());
        cmd.addSubcommand("activity", new StatsCommand());
        cmd.addSubcommand("dirs", new StatsCommand());
        cmd.addSubcommand("repo", new StatsCommand());
        int code = cmd.execute(args);
        if (code == 0 && args.length == 0) {
            code = 2;
        }
        System.exit(code);
    }
}