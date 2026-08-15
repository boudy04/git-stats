package com.amagdy.gitstats;

import com.amagdy.gitstats.model.Commit;
import com.amagdy.gitstats.model.FileChange;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StatsAggregatorTest {

    private final StatsAggregator agg = new StatsAggregator();

    private List<Commit> sample() {
        return List.of(
                new Commit("Alice", List.of(
                        new FileChange("a.txt", 3, 1),
                        new FileChange("b.txt", 0, 5))),
                new Commit("Bob", List.of(new FileChange("a.txt", 10, 0))),
                new Commit("Alice", List.of(new FileChange("a.txt", 1, 1)))
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
        assertEquals("b.txt", s.get(1).path());
        assertEquals(1, s.get(1).commits());
    }

    @Test
    void emptyInputYieldsEmpty() {
        assertTrue(agg.authorStats(List.of()).isEmpty());
        assertTrue(agg.fileStats(List.of()).isEmpty());
    }
}