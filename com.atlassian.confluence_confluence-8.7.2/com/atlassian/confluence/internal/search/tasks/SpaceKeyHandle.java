/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Handle
 */
package com.atlassian.confluence.internal.search.tasks;

import com.atlassian.bonnie.Handle;
import com.atlassian.confluence.internal.search.LuceneIndependent;
import java.util.Objects;

@LuceneIndependent
public class SpaceKeyHandle
implements Handle {
    private final String key;

    public SpaceKeyHandle(String key) {
        this.key = Objects.requireNonNull(key, "Space key must be specified.");
    }

    public String toString() {
        return this.key;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SpaceKeyHandle)) {
            return false;
        }
        SpaceKeyHandle that = (SpaceKeyHandle)o;
        return this.key.equals(that.key);
    }

    public int hashCode() {
        return Objects.hash(this.key);
    }
}

