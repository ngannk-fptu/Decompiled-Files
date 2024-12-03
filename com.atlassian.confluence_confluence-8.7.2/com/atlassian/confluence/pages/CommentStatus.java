/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages;

public class CommentStatus {
    static final String STATUS_PROP = "status";
    static final String STATUS_LAST_MODIFIER = "status-lastmodifier";
    static final String STATUS_LAST_MODIFIED_DATE = "status-lastmoddate";
    private Value value;
    private String lastModifier;
    private Long lastModifiedDate;

    private CommentStatus(Builder builder) {
        this.value = builder.value;
        this.lastModifier = builder.lastModifier;
        this.lastModifiedDate = builder.lastModifiedDate;
    }

    public Value getValue() {
        return this.value;
    }

    public String getLastModifier() {
        return this.lastModifier;
    }

    public Long getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public boolean isResolved() {
        return Value.RESOLVED.equals((Object)this.value);
    }

    public boolean isOpen() {
        return this.value == null || Value.OPEN.equals((Object)this.value);
    }

    public boolean isReopened() {
        return Value.REOPENED.equals((Object)this.value);
    }

    public boolean isDangling() {
        return Value.DANGLING.equals((Object)this.value);
    }

    public static class Builder {
        private Value value;
        private String lastModifier;
        private Long lastModifiedDate;

        public Builder setValue(String value) {
            this.value = Value.RESOLVED.getStringValue().equals(value) ? Value.RESOLVED : (Value.REOPENED.getStringValue().equals(value) ? Value.REOPENED : (Value.DANGLING.getStringValue().equals(value) ? Value.DANGLING : Value.OPEN));
            return this;
        }

        public Builder setValue(Value value) {
            this.value = value;
            return this;
        }

        public Builder setLastModifider(String lastModifier) {
            this.lastModifier = lastModifier;
            return this;
        }

        public Builder setLastModifiedDate(Long lastModifiedDate) {
            this.lastModifiedDate = lastModifiedDate;
            return this;
        }

        public CommentStatus build() {
            return new CommentStatus(this);
        }
    }

    public static enum Value {
        OPEN("open"),
        RESOLVED("resolved"),
        REOPENED("reopened"),
        DANGLING("dangling");

        private final String value;

        private Value(String value) {
            this.value = value;
        }

        public String getStringValue() {
            return this.value;
        }
    }
}

