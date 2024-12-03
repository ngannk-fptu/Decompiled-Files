/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.ha.store.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import org.glassfish.ha.store.annotations.Attribute;
import org.glassfish.ha.store.api.Storeable;

public class SimpleMetadata
implements Storeable {
    private long version = -1L;
    private long lastAccessTime;
    private long maxInactiveInterval;
    private byte[] state;
    private static final String[] attributeNames = new String[]{"state"};
    private static final boolean[] dirtyStatus = new boolean[]{true};

    public SimpleMetadata() {
    }

    public SimpleMetadata(long version, long lastAccesstime, long maxInactiveInterval, byte[] state) {
        this.version = version;
        this.lastAccessTime = lastAccesstime;
        this.maxInactiveInterval = maxInactiveInterval;
        this.state = state;
    }

    public long getVersion() {
        return this.version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public long getLastAccessTime() {
        return this.lastAccessTime;
    }

    public void setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public long getMaxInactiveInterval() {
        return this.maxInactiveInterval;
    }

    public void setMaxInactiveInterval(long maxInactiveInterval) {
        this.maxInactiveInterval = maxInactiveInterval;
    }

    public byte[] getState() {
        return this.state;
    }

    @Attribute(value="state")
    public void setState(byte[] state) {
        this.state = state;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("SimpleMetadata->state");
        if (this.state != null) {
            for (byte b : this.state) {
                sb.append(b + "_");
            }
        } else {
            sb.append("null");
        }
        return "SimpleMetadata{version=" + this.version + ", lastAccessTime=" + this.lastAccessTime + ", maxInactiveInterval=" + this.maxInactiveInterval + ", state.length=" + (this.state == null ? 0 : this.state.length) + ", state=" + sb.toString() + '}';
    }

    @Override
    public long _storeable_getVersion() {
        return this.version;
    }

    @Override
    public void _storeable_setVersion(long val) {
        this.version = val;
    }

    @Override
    public long _storeable_getLastAccessTime() {
        return this.lastAccessTime;
    }

    @Override
    public void _storeable_setLastAccessTime(long val) {
        this.lastAccessTime = val;
    }

    @Override
    public long _storeable_getMaxIdleTime() {
        return this.maxInactiveInterval;
    }

    @Override
    public void _storeable_setMaxIdleTime(long val) {
        this.maxInactiveInterval = val;
    }

    @Override
    public String[] _storeable_getAttributeNames() {
        return attributeNames;
    }

    @Override
    public boolean[] _storeable_getDirtyStatus() {
        return dirtyStatus;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void _storeable_writeState(OutputStream os) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(os);
        try {
            oos.writeInt(this.state.length);
            oos.write(this.state);
        }
        finally {
            try {
                oos.close();
            }
            catch (Exception exception) {}
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void _storeable_readState(InputStream is) throws IOException {
        ObjectInputStream ois = new ObjectInputStream(is);
        try {
            int len = ois.readInt();
            this.state = new byte[len];
            ois.readFully(this.state);
        }
        finally {
            try {
                ois.close();
            }
            catch (Exception exception) {}
        }
    }
}

