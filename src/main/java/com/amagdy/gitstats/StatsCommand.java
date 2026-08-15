package com.amagdy.gitstats;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.util.List;
import java.util.concurrent.Callable;

@Command(description = "Git history stats.")
public class StatsCommand implements Callable<Integer> {

    @Spec
    CommandSpec spec;

    @Parameters(index = "0", arity = "0..1", defaultValue = ".",
            description = "Repo path (default: current dir).")
    String repoPath;

    @Override
    public Integer call() {
        String mode = spec.commandLine().getCommandName();
        try {
            GitRunner git = new GitRunner();
            LogParser parser = new LogParser();
            List<Commit> commits = parser.parse(git.runGitLog(repoPath));
            if (commits.isEmpty()) {
                System.out.println("No commits found.");
                return 1;
            }
            StatsAggregator agg = new StatsAggregator();
            switch (mode) {
                case "files" -> {
                    List<StatsAggregator.FileStat> stats = agg.fileStats(commits);
                    System.out.printf("%-30s %8s %8s %8s%n", "File", "Commits", "+", "-");
                    for (StatsAggregator.FileStat s : stats) {
                        System.out.printf("%-30s %8d %8d %8d%n", s.path(), s.commits(), s.added(), s.deleted());
                    }
                }
                case "activity" -> {
                    List<StatsAggregator.ActivityStat> stats = agg.activityStats(commits);
                    System.out.printf("%-12s %8s%n", "Date", "Commits");
                    for (StatsAggregator.ActivityStat s : stats) {
                        System.out.printf("%-12s %8d%n", s.date(), s.commits());
                    }
                }
                case "dirs" -> {
                    List<StatsAggregator.DirStat> stats = agg.dirStats(commits);
                    System.out.printf("%-25s %8s %8s %8s%n", "Directory", "Commits", "+", "-");
                    for (StatsAggregator.DirStat s : stats) {
                        System.out.printf("%-25s %8d %8d %8d%n", s.dir(), s.commits(), s.added(), s.deleted());
                    }
                }
                case "repo" -> {
                    StatsAggregator.RepoStat s = agg.repoSummary(commits, git.countMerges(repoPath), git.countBranches(repoPath), git.countTags(repoPath));
                    System.out.println("Total commits: " + s.totalCommits());
                    System.out.println("Contributors:  " + s.contributors());
                    System.out.println("Merge commits: " + s.merges());
                    System.out.println("Branches:      " + s.branches());
                    System.out.println("Tags:          " + s.tags());
                }
                default -> {
                    List<StatsAggregator.AuthorStat> stats = agg.authorStats(commits);
                    System.out.printf("%-25s %8s %8s%n", "Author", "Commits", "%");
                    for (StatsAggregator.AuthorStat s : stats) {
                        System.out.printf("%-25s %8d %7.1f%%%n", s.author(), s.commits(), s.percent());
                    }
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