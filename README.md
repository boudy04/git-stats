# git-stats

A tiny Java 21 CLI that analyzes a git repository's history and reports:

- **authors** — commits per author (count + % of total)
- **files** — top files by churn (commits, lines added, lines removed)

Built with Maven + picocli + JUnit 5. Required: git on PATH, Java 21.

## Build

```bash
mvn package
java -jar target/git-stats-1.0.0.jar
```

## Usage

```bash
# authors, current repo
java -jar target/git-stats-1.0.0.jar authors

# files, a specific repo
java -jar target/git-stats-1.0.0.jar files /path/to/repo
```

## Sample output

```
Author                    Commits       %
boudy04                         8    80.0%
other-contributor               2    20.0%

File                         Commits        +        -
src/main/.../LogParser.java        5       42       18
...
```

## Test commands

```bash
mvn test                         # run all unit tests
mvn -Dtest=LogParserTest test    # parser only
mvn -Dtest=StatsAggregatorTest test
mvn -q test                      # quiet
mvn verify                       # tests + package
```

## Architecture

`GitRunner` → `git log --numstat` → `LogParser` (Commit records) → `StatsAggregator`
→ `AuthorsCommand` / `FilesCommand` (picocli). Pure logic in `LogParser` and
`StatsAggregator` is unit-tested; the CLI/subprocess layer is thin.