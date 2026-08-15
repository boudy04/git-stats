package com.amagdy.gitstats;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StatsAggregator {

    public record AuthorStat(String author, int commits, double percent) {}
    public record FileStat(String path, int commits, int added, int deleted) {}
    public record ActivityStat(String date, int commits) {}
    public record DirStat(String dir, int commits, int added, int deleted) {}
    public record RepoStat(int totalCommits, int contributors, int merges, int branches, int tags) {}

    public List<AuthorStat> authorStats(List<Commit> commits) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (Commit c : commits) {
            counts.merge(c.author(), 1, Integer::sum);
        }
        int total = commits.size();
        List<AuthorStat> out = new ArrayList<>();
        for (var e : counts.entrySet()) {
            double pct = total == 0 ? 0.0 : (e.getValue() * 100.0) / total;
            out.add(new AuthorStat(e.getKey(), e.getValue(), pct));
        }
        out.sort((a, b) -> Integer.compare(b.commits(), a.commits()));
        return out;
    }

    public List<FileStat> fileStats(List<Commit> commits) {
        Map<String, int[]> agg = new LinkedHashMap<>(); // [commits, added, deleted]
        for (Commit c : commits) {
            for (FileChange f : c.changes()) {
                int[] v = agg.computeIfAbsent(f.path(), k -> new int[3]);
                v[0] += 1;
                v[1] += f.added();
                v[2] += f.deleted();
            }
        }
        List<FileStat> out = new ArrayList<>();
        for (var e : agg.entrySet()) {
            int[] v = e.getValue();
            out.add(new FileStat(e.getKey(), v[0], v[1], v[2]));
        }
        out.sort((a, b) -> Integer.compare(b.commits(), a.commits()));
        return out;
    }

    public List<ActivityStat> activityStats(List<Commit> commits) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (Commit c : commits) {
            counts.merge(c.date().isEmpty() ? "(unknown)" : c.date(), 1, Integer::sum);
        }
        List<ActivityStat> out = new ArrayList<>();
        for (var e : counts.entrySet()) {
            out.add(new ActivityStat(e.getKey(), e.getValue()));
        }
        out.sort((a, b) -> a.date().compareTo(b.date()));
        return out;
    }

    public List<DirStat> dirStats(List<Commit> commits) {
        Map<String, int[]> agg = new LinkedHashMap<>(); // [commits, added, deleted]
        for (Commit c : commits) {
            for (FileChange f : c.changes()) {
                String dir = topDir(f.path());
                int[] v = agg.computeIfAbsent(dir, k -> new int[3]);
                v[0] += 1;
                v[1] += f.added();
                v[2] += f.deleted();
            }
        }
        List<DirStat> out = new ArrayList<>();
        for (var e : agg.entrySet()) {
            int[] v = e.getValue();
            out.add(new DirStat(e.getKey(), v[0], v[1], v[2]));
        }
        out.sort((a, b) -> Integer.compare(b.commits(), a.commits()));
        return out;
    }

    public RepoStat repoSummary(List<Commit> commits, int merges, int branches, int tags) {
        int contributors = (int) commits.stream().map(Commit::author).distinct().count();
        return new RepoStat(commits.size(), contributors, merges, branches, tags);
    }

    private static String topDir(String path) {
        int slash = path.indexOf('/');
        return slash < 0 ? "(root)" : path.substring(0, slash);
    }
}