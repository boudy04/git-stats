package com.amagdy.gitstats;

import com.amagdy.gitstats.model.Commit;
import picocli.CommandLine;

import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "files", description = "Top files by churn (commits, lines added/removed).")
public class FilesCommand implements Callable<Integer> {

    @CommandLine.Parameters(index = "0", arity = "0..1", defaultValue = ".",
            description = "Repo path (default: current dir).")
    String repoPath;

    @Override
    public Integer call() {
        try {
            String log = new GitRunner().runGitLog(repoPath);
            LogParser parser = new LogParser();
            List<Commit> commits = parser.parse(log);
            if (commits.isEmpty()) {
                System.out.println("No commits found.");
                return 1;
            }
            List<StatsAggregator.FileStat> stats = new StatsAggregator().fileStats(commits);
            System.out.printf("%-30s %8s %8s %8s%n", "File", "Commits", "+", "-");
            for (StatsAggregator.FileStat s : stats) {
                System.out.printf("%-30s %8d %8d %8d%n", s.path(), s.commits(), s.added(), s.deleted());
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