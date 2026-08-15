package com.amagdy.gitstats;

import com.amagdy.gitstats.model.Commit;
import picocli.CommandLine;

import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "authors", description = "Commits per author.")
public class AuthorsCommand implements Callable<Integer> {

    @CommandLine.Parameters(index = "0", arity = "0..1", defaultValue = ".",
            description = "Repo path (default: current dir).")
    String repoPath;

    @Override
    public Integer call() {
        try {
            String log = new GitRunner().runGitLog(repoPath);
            List<Commit> commits = new LogParser().parse(log);
            if (commits.isEmpty()) {
                System.out.println("No commits found.");
                return 1;
            }
            List<StatsAggregator.AuthorStat> stats = new StatsAggregator().authorStats(commits);
            System.out.printf("%-25s %8s %8s%n", "Author", "Commits", "%");
            for (StatsAggregator.AuthorStat s : stats) {
                System.out.printf("%-25s %8d %7.1f%%%n", s.author(), s.commits(), s.percent());
            }
            return 0;
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
            return 1;
        }
    }
}