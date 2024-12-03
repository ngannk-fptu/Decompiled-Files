/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import org.apache.lucene.store.FlushInfo;
import org.apache.lucene.store.MergeInfo;

public class IOContext {
    public final Context context;
    public final MergeInfo mergeInfo;
    public final FlushInfo flushInfo;
    public final boolean readOnce;
    public static final IOContext DEFAULT = new IOContext(Context.DEFAULT);
    public static final IOContext READONCE = new IOContext(true);
    public static final IOContext READ = new IOContext(false);

    public IOContext() {
        this(false);
    }

    public IOContext(FlushInfo flushInfo) {
        assert (flushInfo != null);
        this.context = Context.FLUSH;
        this.mergeInfo = null;
        this.readOnce = false;
        this.flushInfo = flushInfo;
    }

    public IOContext(Context context) {
        this(context, null);
    }

    private IOContext(boolean readOnce) {
        this.context = Context.READ;
        this.mergeInfo = null;
        this.readOnce = readOnce;
        this.flushInfo = null;
    }

    public IOContext(MergeInfo mergeInfo) {
        this(Context.MERGE, mergeInfo);
    }

    private IOContext(Context context, MergeInfo mergeInfo) {
        assert (context != Context.MERGE || mergeInfo != null) : "MergeInfo must not be null if context is MERGE";
        assert (context != Context.FLUSH) : "Use IOContext(FlushInfo) to create a FLUSH IOContext";
        this.context = context;
        this.readOnce = false;
        this.mergeInfo = mergeInfo;
        this.flushInfo = null;
    }

    public IOContext(IOContext ctxt, boolean readOnce) {
        this.context = ctxt.context;
        this.mergeInfo = ctxt.mergeInfo;
        this.flushInfo = ctxt.flushInfo;
        this.readOnce = readOnce;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.context == null ? 0 : this.context.hashCode());
        result = 31 * result + (this.flushInfo == null ? 0 : this.flushInfo.hashCode());
        result = 31 * result + (this.mergeInfo == null ? 0 : this.mergeInfo.hashCode());
        result = 31 * result + (this.readOnce ? 1231 : 1237);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        IOContext other = (IOContext)obj;
        if (this.context != other.context) {
            return false;
        }
        if (this.flushInfo == null ? other.flushInfo != null : !this.flushInfo.equals(other.flushInfo)) {
            return false;
        }
        if (this.mergeInfo == null ? other.mergeInfo != null : !this.mergeInfo.equals(other.mergeInfo)) {
            return false;
        }
        return this.readOnce == other.readOnce;
    }

    public String toString() {
        return "IOContext [context=" + (Object)((Object)this.context) + ", mergeInfo=" + this.mergeInfo + ", flushInfo=" + this.flushInfo + ", readOnce=" + this.readOnce + "]";
    }

    public static enum Context {
        MERGE,
        READ,
        FLUSH,
        DEFAULT;

    }
}

