package com.amagdy.gitstats;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StatsAggregatorTest {

    private final StatsAggregator agg = new StatsAggregator();

    private List<Commit> sample() {
        return List.of(
                new Commit("Alice", "2026-08-01", List.of(
                        new FileChange("a.txt", 3, 1),
                        new FileChange("src/b.txt", 0, 5))),
                new Commit("Bob", "2026-08-03", List.of(new FileChange("a.txt", 10, 0))),
                new Commit("Alice", "2026-08-02", List.of(new FileChange("a.txt", 1, 1)))
        );
    }

    @Test
    void authorStatsCountAndOrder() {
        List<StatsAggregator.AuthorStat> s = agg.authorStats(sample());
        assertEquals(2, s.size());
        assertEquals("Alice", s.get(0).author());
        assertEquals(2, s.get(0).commits());
        assertEquals("Bob", s.get(1).author());
        assertEquals(1, s.get(1).commits());
    }

    @Test
    void authorStatsPercentOfTotal() {
        List<StatsAggregator.AuthorStat> s = agg.authorStats(sample());
        // total commits = 3, Alice=2 => 66.6..
        assertEquals(2.0 / 3.0 * 100.0, s.get(0).percent(), 0.001);
    }

    @Test
    void fileStatsSumChurnAndCount() {
        List<StatsAggregator.FileStat> s = agg.fileStats(sample());
        // a.txt touched in all 3 commits, b.txt in 1
        assertEquals("a.txt", s.get(0).path());
        assertEquals(3, s.get(0).commits());
        assertEquals(14, s.get(0).added());   // 3+10+1
        assertEquals(2, s.get(0).deleted());  // 1+0+1
        assertEquals("src/b.txt", s.get(1).path());
        assertEquals(1, s.get(1).commits());
    }

    @Test
    void activityStatsCountsPerDayAndSorts() {
        List<StatsAggregator.ActivityStat> s = agg.activityStats(sample());
        // 2026-08-01, 2026-08-02, 2026-08-03 each 1 commit
        assertEquals(3, s.size());
        assertEquals("2026-08-01", s.get(0).date());
        assertEquals("2026-08-03", s.get(2).date());
        assertEquals(1, s.get(0).commits());
    }

    @Test
    void dirStatsBucketsTopLevel() {
        List<StatsAggregator.DirStat> s = agg.dirStats(sample());
        // a.txt in root touched in all 3 commits; src/b.txt in 1
        assertEquals("(root)", s.get(0).dir());
        assertEquals(3, s.get(0).commits());
        assertEquals(14, s.get(0).added());   // 3+10+1
        assertEquals(2, s.get(0).deleted());  // 1+0+1
        assertEquals("src", s.get(1).dir());
        assertEquals(1, s.get(1).commits());
    }

    @Test
    void repoSummaryCounts() {
        StatsAggregator.RepoStat s = agg.repoSummary(sample(), 2, 3, 4);
        assertEquals(3, s.totalCommits());
        assertEquals(2, s.contributors());
        assertEquals(2, s.merges());
        assertEquals(3, s.branches());
        assertEquals(4, s.tags());
    }

    @Test
    void emptyInputYieldsEmpty() {
        assertTrue(agg.authorStats(List.of()).isEmpty());
        assertTrue(agg.fileStats(List.of()).isEmpty());
        assertTrue(agg.activityStats(List.of()).isEmpty());
        assertTrue(agg.dirStats(List.of()).isEmpty());
    }
}