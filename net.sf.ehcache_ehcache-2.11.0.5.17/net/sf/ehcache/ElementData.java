/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.terracotta.toolkit.internal.cache.TimestampedValue
 */
package net.sf.ehcache;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import net.sf.ehcache.Element;
import net.sf.ehcache.util.TimeUtil;
import org.terracotta.toolkit.internal.cache.TimestampedValue;

public abstract class ElementData
implements Externalizable,
TimestampedValue {
    protected volatile Object value;
    protected volatile long version;
    protected volatile long creationTime;
    protected volatile long lastAccessTime;
    protected volatile long hitCount;
    protected volatile boolean cacheDefaultLifespan;
    protected volatile long lastUpdateTime;

    public ElementData() {
    }

    public ElementData(Element element) {
        this.value = element.getValue();
        this.version = element.getVersion();
        this.creationTime = element.getCreationTime();
        this.lastAccessTime = element.getLastAccessTime();
        this.hitCount = element.getHitCount();
        this.cacheDefaultLifespan = element.usesCacheDefaultLifespan();
        this.lastUpdateTime = element.getLastUpdateTime();
    }

    public abstract Element createElement(Object var1);

    protected abstract void writeAttributes(ObjectOutput var1) throws IOException;

    protected abstract void readAttributes(ObjectInput var1) throws IOException;

    @Override
    public void writeExternal(ObjectOutput oos) throws IOException {
        oos.writeObject(this.value);
        oos.writeLong(this.version);
        oos.writeLong(this.creationTime);
        oos.writeLong(this.lastAccessTime);
        oos.writeLong(this.hitCount);
        oos.writeBoolean(this.cacheDefaultLifespan);
        oos.writeLong(this.lastUpdateTime);
        this.writeAttributes(oos);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.value = in.readObject();
        this.version = in.readLong();
        this.creationTime = in.readLong();
        this.lastAccessTime = in.readLong();
        this.hitCount = in.readLong();
        this.cacheDefaultLifespan = in.readBoolean();
        this.lastUpdateTime = in.readLong();
        this.readAttributes(in);
    }

    public Object getValue() {
        return this.value;
    }

    public int getLastAccessedTime() {
        return TimeUtil.toSecs(this.lastAccessTime);
    }

    protected void setLastAccessedTimeInternal(int usedAtTime) {
        this.lastAccessTime = TimeUtil.toMillis(usedAtTime);
    }

    public int getCreateTime() {
        return TimeUtil.toSecs(this.creationTime);
    }

    public void updateTimestamps(int createTime, int lastAccessedTime) {
        this.setLastAccessedTimeInternal(lastAccessedTime);
    }
}

