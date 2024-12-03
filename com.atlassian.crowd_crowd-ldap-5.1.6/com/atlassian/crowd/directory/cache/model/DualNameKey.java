/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.cache.model;

import java.io.Serializable;

public class DualNameKey
implements Serializable {
    private final String name1;
    private final String name2;

    public DualNameKey(String name1, String name2) {
        this.name1 = name1;
        this.name2 = name2;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DualNameKey that = (DualNameKey)o;
        if (this.name1 != null ? !this.name1.equals(that.name1) : that.name1 != null) {
            return false;
        }
        return !(this.name2 != null ? !this.name2.equals(that.name2) : that.name2 != null);
    }

    public int hashCode() {
        int result = this.name1 != null ? this.name1.hashCode() : 0;
        result = 31 * result + (this.name2 != null ? this.name2.hashCode() : 0);
        return result;
    }
}

