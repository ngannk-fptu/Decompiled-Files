/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.streams.spi;

public interface StreamsKeyProvider {
    public static final String ALL_PROJECTS_KEY = "__all_projects__";

    public Iterable<StreamsKey> getKeys();

    public static class StreamsKey {
        private final String key;
        private final String label;

        public StreamsKey(String key, String label) {
            this.key = key;
            this.label = label;
        }

        public String getKey() {
            return this.key;
        }

        public String getLabel() {
            return this.label;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            StreamsKey that = (StreamsKey)o;
            if (!this.key.equals(that.key)) {
                return false;
            }
            return this.label.equals(that.label);
        }

        public int hashCode() {
            int result = this.key.hashCode();
            result = 31 * result + this.label.hashCode();
            return result;
        }
    }
}

