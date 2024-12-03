/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.bandana.impl;

import com.atlassian.bandana.BandanaContext;
import java.io.Serializable;

public class PersisterKey
implements Serializable {
    BandanaContext context;
    String key;

    public PersisterKey(BandanaContext context, String key) {
        this.context = context;
        this.key = key;
    }

    public BandanaContext getContext() {
        return this.context;
    }

    public String getKey() {
        return this.key;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PersisterKey)) {
            return false;
        }
        PersisterKey persisterKey = (PersisterKey)o;
        if (this.context != null ? !this.context.equals(persisterKey.context) : persisterKey.context != null) {
            return false;
        }
        return !(this.key != null ? !this.key.equals(persisterKey.key) : persisterKey.key != null);
    }

    public int hashCode() {
        int result = this.context != null ? this.context.hashCode() : 0;
        result = 29 * result + (this.key != null ? this.key.hashCode() : 0);
        return result;
    }
}

