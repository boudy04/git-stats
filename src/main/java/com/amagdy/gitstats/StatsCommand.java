package com.amagdy.gitstats;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.util.List;
import java.util.concurrent.Callable;

@Command(description = "Top entries for a git repo.")
public class StatsCommand implements Callable<Integer> {

    @Spec
    CommandSpec spec;

    @Parameters(index = "0", arity = "0..1", defaultValue = ".",
            description = "Repo path (default: current dir).")
    String repoPath;

    @Override
    public Integer call() {
        boolean files = spec.commandLine().getCommandName().equals("files");
        try {
            String log = new GitRunner().runGitLog(repoPath);
            LogParser parser = new LogParser();
            List<Commit> commits = parser.parse(log);
            if (commits.isEmpty()) {
                System.out.println("No commits found.");
                return 1;
            }
            if (files) {
                List<StatsAggregator.FileStat> stats = new StatsAggregator().fileStats(commits);
                System.out.printf("%-30s %8s %8s %8s%n", "File", "Commits", "+", "-");
                for (StatsAggregator.FileStat s : stats) {
                    System.out.printf("%-30s %8d %8d %8d%n", s.path(), s.commits(), s.added(), s.deleted());
                }
            } else {
                List<StatsAggregator.AuthorStat> stats = new StatsAggregator().authorStats(commits);
                System.out.printf("%-25s %8s %8s%n", "Author", "Commits", "%");
                for (StatsAggregator.AuthorStat s : stats) {
                    System.out.printf("%-25s %8d %7.1f%%%n", s.author(), s.commits(), s.percent());
                }
            }
            if (parser.malformedCount() > 0) {
                System.err.println("Warning: " + parser.malformedCount() + " malformed line(s) skipped.");
            }
            return 0;
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
            return 1;
        }
    }
}
