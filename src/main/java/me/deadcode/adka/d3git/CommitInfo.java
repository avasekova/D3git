package me.deadcode.adka.d3git;

import java.time.Instant;
import java.time.ZoneId;


public class CommitInfo {

    private Instant date;
    private String authorName;
    private String authorEmail;
    private String hash; //?
    private String message;
    //TODO


    public CommitInfo() {
    }

    public CommitInfo(Instant date, String authorName, String authorEmail, String hash, String message) {
        this.date = date;
        this.authorName = authorName;
        this.authorEmail = authorEmail;
        this.hash = hash;
        this.message = message;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommitInfo that = (CommitInfo) o;

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
        return result;
    }

    @Override
    public String toString() {
        return "CommitInfo{" +
                "date=" + date.atZone(ZoneId.systemDefault()) +
                ", authorName='" + authorName + '\'' +
                ", authorEmail='" + authorEmail + '\'' +
                ", hash='" + hash + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
