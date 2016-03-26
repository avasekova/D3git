package me.deadcode.adka.d3git;


import java.time.Instant;

public class CommitInfoDiff extends CommitInfo {

    private long filesChanged;
    private long insertions;
    private long deletions;

    public CommitInfoDiff() {
    }

    public CommitInfoDiff(Instant date, String authorName, String authorEmail, String hash, String message, long filesChanged, long insertions, long deletions) {
        super(date, authorName, authorEmail, hash, message);
        this.filesChanged = filesChanged;
        this.insertions = insertions;
        this.deletions = deletions;
    }

    public long getFilesChanged() {
        return filesChanged;
    }

    public void setFilesChanged(long filesChanged) {
        this.filesChanged = filesChanged;
    }

    public long getInsertions() {
        return insertions;
    }

    public void setInsertions(long insertions) {
        this.insertions = insertions;
    }

    public long getDeletions() {
        return deletions;
    }

    public void setDeletions(long deletions) {
        this.deletions = deletions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CommitInfoDiff that = (CommitInfoDiff) o;

        if (filesChanged != that.filesChanged) return false;
        if (insertions != that.insertions) return false;
        return deletions == that.deletions;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (filesChanged ^ (filesChanged >>> 32));
        result = 31 * result + (int) (insertions ^ (insertions >>> 32));
        result = 31 * result + (int) (deletions ^ (deletions >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "CommitInfoDiff{" +
                super.toString() +
                ", filesChanged=" + filesChanged +
                ", insertions=" + insertions +
                ", deletions=" + deletions +
                '}';
    }
}
