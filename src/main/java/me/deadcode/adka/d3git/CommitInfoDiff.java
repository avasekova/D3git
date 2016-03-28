package me.deadcode.adka.d3git;


import java.time.Instant;
import java.time.ZoneId;

public class CommitInfoDiff {

    private Instant date;
    private String authorName;
    private String authorEmail;
    private String hash; //?
    private String message;
    private long filesChanged;
    private long insertions;
    private long deletions;

    public CommitInfoDiff() {
    }

    public CommitInfoDiff(Instant date, String authorName, String authorEmail, String hash, String message, long filesChanged, long insertions, long deletions) {
        this.date = date;
        this.authorName = authorName;
        this.authorEmail = authorEmail;
        this.hash = hash;
        this.message = message;
        this.filesChanged = filesChanged;
        this.insertions = insertions;
        this.deletions = deletions;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

        CommitInfoDiff that = (CommitInfoDiff) o;

        if (filesChanged != that.filesChanged) return false;
        if (insertions != that.insertions) return false;
        if (deletions != that.deletions) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (authorName != null ? !authorName.equals(that.authorName) : that.authorName != null) return false;
        if (authorEmail != null ? !authorEmail.equals(that.authorEmail) : that.authorEmail != null) return false;
        if (hash != null ? !hash.equals(that.hash) : that.hash != null) return false;
        return message != null ? message.equals(that.message) : that.message == null;

    }

    @Override
    public int hashCode() {
        int result = date != null ? date.hashCode() : 0;
        result = 31 * result + (authorName != null ? authorName.hashCode() : 0);
        result = 31 * result + (authorEmail != null ? authorEmail.hashCode() : 0);
        result = 31 * result + (hash != null ? hash.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (int) (filesChanged ^ (filesChanged >>> 32));
        result = 31 * result + (int) (insertions ^ (insertions >>> 32));
        result = 31 * result + (int) (deletions ^ (deletions >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "CommitInfoDiff{" +
                "date=" + date.atZone(ZoneId.systemDefault()) +
                ", authorName='" + authorName + '\'' +
                ", authorEmail='" + authorEmail + '\'' +
                ", hash='" + hash + '\'' +
                ", message='" + message + '\'' +
                ", filesChanged=" + filesChanged +
                ", insertions=" + insertions +
                ", deletions=" + deletions +
                '}';
    }
}
