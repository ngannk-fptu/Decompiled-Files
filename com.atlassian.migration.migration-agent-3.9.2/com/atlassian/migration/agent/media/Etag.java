/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.media;

import java.util.Objects;
import org.codehaus.jackson.annotate.JsonProperty;

public class Etag {
    private static final String DELIM = "-";
    @JsonProperty
    private final int length;
    @JsonProperty
    private final CharSequence sha1;

    public Etag(@JsonProperty(value="length") int length, @JsonProperty(value="sha1") CharSequence sha1) {
        this.length = length;
        this.sha1 = sha1;
    }

    public int getLength() {
        return this.length;
    }

    public CharSequence getSha1() {
        return this.sha1;
    }

    public String toString() {
        return this.sha1 + DELIM + this.length;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Etag etag = (Etag)o;
        return this.length == etag.length && Objects.equals(this.sha1, etag.sha1);
    }

    public int hashCode() {
        return Objects.hash(this.length, this.sha1);
    }
}

