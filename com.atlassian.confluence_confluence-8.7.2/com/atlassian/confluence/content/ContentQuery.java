/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content;

import java.util.Arrays;
import java.util.Objects;

public class ContentQuery<T> {
    private final String name;
    private final Object[] parameters;

    public ContentQuery(String name, Object ... parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    public String getName() {
        return this.name;
    }

    public Object[] getParameters() {
        return this.parameters;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContentQuery)) {
            return false;
        }
        ContentQuery that = (ContentQuery)o;
        return Objects.equals(this.name, that.name) && Arrays.equals(this.parameters, that.parameters);
    }

    public int hashCode() {
        int result = this.name != null ? this.name.hashCode() : 0;
        result = 31 * result + (this.parameters != null ? Arrays.hashCode(this.parameters) : 0);
        return result;
    }
}

