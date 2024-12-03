/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.renderer.radeox.macros;

public class ChangeSetKey {
    private final String author;
    private final String date;

    public ChangeSetKey(String author, String date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null.");
        }
        this.author = author;
        this.date = date;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getDate() {
        return this.date;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ChangeSetKey that = (ChangeSetKey)o;
        if (this.author != null ? !this.author.equals(that.author) : that.author != null) {
            return false;
        }
        return this.date.equals(that.date);
    }

    public int hashCode() {
        int result = this.author != null ? this.author.hashCode() : 0;
        result = 31 * result + this.date.hashCode();
        return result;
    }

    public String toString() {
        return "[author=" + this.author + ", date=" + this.date + "]";
    }
}

