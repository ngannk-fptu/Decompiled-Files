/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.pool.sizeof.annotations.IgnoreSizeOf;
import net.sf.ehcache.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Element
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1098572221246444544L;
    private static final Logger LOG = LoggerFactory.getLogger((String)Element.class.getName());
    private static final AtomicLongFieldUpdater<Element> HIT_COUNT_UPDATER = AtomicLongFieldUpdater.newUpdater(Element.class, "hitCount");
    private static final boolean ELEMENT_VERSION_AUTO = Boolean.getBoolean("net.sf.ehcache.element.version.auto");
    private static final long NOT_SET_ID = 0L;
    @IgnoreSizeOf
    private final Object key;
    private final Object value;
    private volatile long version;
    private volatile long hitCount;
    private volatile int timeToLive = Integer.MIN_VALUE;
    private volatile int timeToIdle = Integer.MIN_VALUE;
    private transient long creationTime;
    private transient long lastAccessTime;
    private volatile long lastUpdateTime;
    private volatile boolean cacheDefaultLifespan = true;
    private volatile long id = 0L;

    public Element(Serializable key, Serializable value, long version) {
        this((Object)key, (Object)value, version);
    }

    public Element(Object key, Object value, long version) {
        this.key = key;
        this.value = value;
        this.version = version;
        HIT_COUNT_UPDATER.set(this, 0L);
        this.creationTime = this.getCurrentTime();
    }

    @Deprecated
    public Element(Object key, Object value, long version, long creationTime, long lastAccessTime, long nextToLastAccessTime, long lastUpdateTime, long hitCount) {
        this(key, value, version, creationTime, lastAccessTime, lastUpdateTime, hitCount);
    }

    public Element(Object key, Object value, long version, long creationTime, long lastAccessTime, long lastUpdateTime, long hitCount) {
        this.key = key;
        this.value = value;
        this.version = version;
        this.lastUpdateTime = lastUpdateTime;
        HIT_COUNT_UPDATER.set(this, hitCount);
        this.creationTime = creationTime;
        this.lastAccessTime = lastAccessTime;
    }

    public Element(Object key, Object value, long version, long creationTime, long lastAccessTime, long hitCount, boolean cacheDefaultLifespan, int timeToLive, int timeToIdle, long lastUpdateTime) {
        this.key = key;
        this.value = value;
        this.version = version;
        HIT_COUNT_UPDATER.set(this, hitCount);
        this.cacheDefaultLifespan = cacheDefaultLifespan;
        this.timeToLive = timeToLive;
        this.timeToIdle = timeToIdle;
        this.lastUpdateTime = lastUpdateTime;
        this.creationTime = creationTime;
        this.lastAccessTime = lastAccessTime;
    }

    public Element(Object key, Object value, int timeToIdleSeconds, int timeToLiveSeconds) {
        this(key, value);
        this.setTimeToIdle(timeToIdleSeconds);
        this.setTimeToLive(timeToLiveSeconds);
    }

    public Element(Object key, Object value, boolean eternal) {
        this(key, value);
        this.setEternal(eternal);
    }

    @Deprecated
    public Element(Object key, Object value, Boolean eternal, Integer timeToIdleSeconds, Integer timeToLiveSeconds) {
        this.key = key;
        this.value = value;
        if (eternal != null) {
            this.setEternal(eternal);
        }
        if (timeToIdleSeconds != null) {
            this.setTimeToIdle(timeToIdleSeconds);
        }
        if (timeToLiveSeconds != null) {
            this.setTimeToLive(timeToLiveSeconds);
        }
        this.creationTime = this.getCurrentTime();
    }

    public Element(Serializable key, Serializable value) {
        this((Object)key, (Object)value, 1L);
    }

    public Element(Object key, Object value) {
        this(key, value, 1L);
    }

    @Deprecated
    public final Serializable getKey() throws CacheException {
        try {
            return (Serializable)this.getObjectKey();
        }
        catch (ClassCastException e) {
            throw new CacheException("The key " + this.getObjectKey() + " is not Serializable. Consider using Element.getObjectKey()");
        }
    }

    public final Object getObjectKey() {
        return this.key;
    }

    @Deprecated
    public final Serializable getValue() throws CacheException {
        try {
            return (Serializable)this.getObjectValue();
        }
        catch (ClassCastException e) {
            throw new CacheException("The value " + this.getObjectValue() + " for key " + this.getObjectKey() + " is not Serializable. Consider using Element.getObjectValue()");
        }
    }

    public final Object getObjectValue() {
        return this.value;
    }

    public final boolean equals(Object object) {
        if (object == null || !(object instanceof Element)) {
            return false;
        }
        Element element = (Element)object;
        if (this.key == null || element.getObjectKey() == null) {
            return false;
        }
        return this.key.equals(element.getObjectKey());
    }

    public void setTimeToLive(int timeToLiveSeconds) {
        if (timeToLiveSeconds < 0) {
            throw new IllegalArgumentException("timeToLive can't be negative");
        }
        this.cacheDefaultLifespan = false;
        this.timeToLive = timeToLiveSeconds;
    }

    public void setTimeToIdle(int timeToIdleSeconds) {
        if (timeToIdleSeconds < 0) {
            throw new IllegalArgumentException("timeToIdle can't be negative");
        }
        this.cacheDefaultLifespan = false;
        this.timeToIdle = timeToIdleSeconds;
    }

    public final int hashCode() {
        return this.key.hashCode();
    }

    public final void setVersion(long version) {
        this.version = version;
    }

    void setId(long id) {
        if (id == 0L) {
            throw new IllegalArgumentException("Id cannot be set to " + id);
        }
        this.id = id;
    }

    long getId() {
        long v = this.id;
        if (v == 0L) {
            throw new IllegalStateException("Id not set");
        }
        return v;
    }

    boolean hasId() {
        return this.id != 0L;
    }

    @Deprecated
    public final void setCreateTime() {
        this.creationTime = this.getCurrentTime();
    }

    public final long getCreationTime() {
        return this.creationTime;
    }

    public final long getLatestOfCreationAndUpdateTime() {
        if (0L == this.lastUpdateTime) {
            return this.creationTime;
        }
        return this.lastUpdateTime;
    }

    public final long getVersion() {
        return this.version;
    }

    public final long getLastAccessTime() {
        return this.lastAccessTime;
    }

    @Deprecated
    public final long getNextToLastAccessTime() {
        return this.getLastAccessTime();
    }

    public final long getHitCount() {
        return this.hitCount;
    }

    public final void resetAccessStatistics() {
        this.lastAccessTime = this.getCurrentTime();
        HIT_COUNT_UPDATER.set(this, 0L);
    }

    public final void updateAccessStatistics() {
        this.lastAccessTime = this.getCurrentTime();
        HIT_COUNT_UPDATER.incrementAndGet(this);
    }

    public final void updateUpdateStatistics() {
        this.lastUpdateTime = this.getCurrentTime();
        if (ELEMENT_VERSION_AUTO) {
            this.version = this.lastUpdateTime;
        }
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ key = ").append(this.key).append(", value=").append(this.value).append(", version=").append(this.version).append(", hitCount=").append(this.hitCount).append(", CreationTime = ").append(this.getCreationTime()).append(", LastAccessTime = ").append(this.getLastAccessTime()).append(" ]");
        return sb.toString();
    }

    public final Object clone() throws CloneNotSupportedException {
        super.clone();
        try {
            return new Element(Element.deepCopy(this.key), Element.deepCopy(this.value), this.version, this.creationTime, this.lastAccessTime, 0L, this.hitCount);
        }
        catch (IOException e) {
            LOG.error("Error cloning Element with key " + this.key + " during serialization and deserialization of value");
            throw new CloneNotSupportedException();
        }
        catch (ClassNotFoundException e) {
            LOG.error("Error cloning Element with key " + this.key + " during serialization and deserialization of value");
            throw new CloneNotSupportedException();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Object deepCopy(Object oldValue) throws IOException, ClassNotFoundException {
        Serializable newValue = null;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            oos = new ObjectOutputStream(bout);
            oos.writeObject(oldValue);
            ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
            ois = new ObjectInputStream(bin);
            newValue = (Serializable)ois.readObject();
        }
        finally {
            try {
                if (oos != null) {
                    oos.close();
                }
                if (ois != null) {
                    ois.close();
                }
            }
            catch (Exception e) {
                LOG.error("Error closing Stream");
            }
        }
        return newValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final long getSerializedSize() {
        if (!this.isSerializable()) {
            return 0L;
        }
        long size = 0L;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(bout);
            oos.writeObject(this);
            long l = size = (long)bout.size();
            return l;
        }
        catch (IOException e) {
            LOG.debug("Error measuring element size for element with key " + this.key + ". Cause was: " + e.getMessage());
        }
        finally {
            try {
                if (oos != null) {
                    oos.close();
                }
            }
            catch (Exception e) {
                LOG.error("Error closing ObjectOutputStream");
            }
        }
        return size;
    }

    public final boolean isSerializable() {
        return this.isKeySerializable() && (this.value instanceof Serializable || this.value == null);
    }

    public final boolean isKeySerializable() {
        return this.key instanceof Serializable || this.key == null;
    }

    public long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public boolean isExpired() {
        long expirationTime;
        if (!this.isLifespanSet() || this.isEternal()) {
            return false;
        }
        long now = this.getCurrentTime();
        return now > (expirationTime = this.getExpirationTime());
    }

    public boolean isExpired(CacheConfiguration config) {
        if (this.cacheDefaultLifespan) {
            if (config.isEternal()) {
                this.timeToIdle = 0;
                this.timeToLive = 0;
            } else {
                this.timeToIdle = TimeUtil.convertTimeToInt(config.getTimeToIdleSeconds());
                this.timeToLive = TimeUtil.convertTimeToInt(config.getTimeToLiveSeconds());
            }
        }
        return this.isExpired();
    }

    public long getExpirationTime() {
        if (!this.isLifespanSet() || this.isEternal()) {
            return Long.MAX_VALUE;
        }
        long expirationTime = 0L;
        long ttlExpiry = this.creationTime + TimeUtil.toMillis(this.getTimeToLive());
        long mostRecentTime = Math.max(this.creationTime, this.lastAccessTime);
        long ttiExpiry = mostRecentTime + TimeUtil.toMillis(this.getTimeToIdle());
        expirationTime = this.getTimeToLive() != 0 && (this.getTimeToIdle() == 0 || this.lastAccessTime == 0L) ? ttlExpiry : (this.getTimeToLive() == 0 ? ttiExpiry : Math.min(ttlExpiry, ttiExpiry));
        return expirationTime;
    }

    public boolean isEternal() {
        return 0 == this.timeToIdle && 0 == this.timeToLive;
    }

    public void setEternal(boolean eternal) {
        if (eternal) {
            this.cacheDefaultLifespan = false;
            this.timeToIdle = 0;
            this.timeToLive = 0;
        } else if (this.isEternal()) {
            this.cacheDefaultLifespan = false;
            this.timeToIdle = Integer.MIN_VALUE;
            this.timeToLive = Integer.MIN_VALUE;
        }
    }

    public boolean isLifespanSet() {
        return this.timeToIdle != Integer.MIN_VALUE || this.timeToLive != Integer.MIN_VALUE;
    }

    public int getTimeToLive() {
        if (Integer.MIN_VALUE == this.timeToLive) {
            return 0;
        }
        return this.timeToLive;
    }

    public int getTimeToIdle() {
        if (Integer.MIN_VALUE == this.timeToIdle) {
            return 0;
        }
        return this.timeToIdle;
    }

    public boolean usesCacheDefaultLifespan() {
        return this.cacheDefaultLifespan;
    }

    protected void setLifespanDefaults(int tti, int ttl, boolean eternal) {
        if (eternal) {
            this.timeToIdle = 0;
            this.timeToLive = 0;
        } else if (this.isEternal()) {
            this.timeToIdle = Integer.MIN_VALUE;
            this.timeToLive = Integer.MIN_VALUE;
        } else {
            this.timeToIdle = tti;
            this.timeToLive = ttl;
        }
    }

    long getCurrentTime() {
        return System.currentTimeMillis();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(TimeUtil.toSecs(this.creationTime));
        out.writeInt(TimeUtil.toSecs(this.lastAccessTime));
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.creationTime = TimeUtil.toMillis(in.readInt());
        this.lastAccessTime = TimeUtil.toMillis(in.readInt());
    }

    static {
        if (ELEMENT_VERSION_AUTO) {
            LOG.warn("Note that net.sf.ehcache.element.version.auto is set and user provided version will not be honored");
        }
    }
}

