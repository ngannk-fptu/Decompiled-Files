/*
 * Decompiled with CFR 0.152.
 */
package freemarker.cache;

import freemarker.cache.CacheStorageWithGetSize;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

public class MruCacheStorage
implements CacheStorageWithGetSize {
    private final MruEntry strongHead = new MruEntry();
    private final MruEntry softHead = new MruEntry();
    private final Map map;
    private final ReferenceQueue refQueue;
    private final int strongSizeLimit;
    private final int softSizeLimit;
    private int strongSize;
    private int softSize;

    public MruCacheStorage(int strongSizeLimit, int softSizeLimit) {
        this.softHead.linkAfter(this.strongHead);
        this.map = new HashMap();
        this.refQueue = new ReferenceQueue();
        this.strongSize = 0;
        this.softSize = 0;
        if (strongSizeLimit < 0) {
            throw new IllegalArgumentException("strongSizeLimit < 0");
        }
        if (softSizeLimit < 0) {
            throw new IllegalArgumentException("softSizeLimit < 0");
        }
        this.strongSizeLimit = strongSizeLimit;
        this.softSizeLimit = softSizeLimit;
    }

    @Override
    public Object get(Object key) {
        this.removeClearedReferences();
        MruEntry entry = (MruEntry)this.map.get(key);
        if (entry == null) {
            return null;
        }
        this.relinkEntryAfterStrongHead(entry, null);
        Object value = entry.getValue();
        if (value instanceof MruReference) {
            return ((MruReference)value).get();
        }
        return value;
    }

    @Override
    public void put(Object key, Object value) {
        this.removeClearedReferences();
        MruEntry entry = (MruEntry)this.map.get(key);
        if (entry == null) {
            entry = new MruEntry(key, value);
            this.map.put(key, entry);
            this.linkAfterStrongHead(entry);
        } else {
            this.relinkEntryAfterStrongHead(entry, value);
        }
    }

    @Override
    public void remove(Object key) {
        this.removeClearedReferences();
        this.removeInternal(key);
    }

    private void removeInternal(Object key) {
        MruEntry entry = (MruEntry)this.map.remove(key);
        if (entry != null) {
            this.unlinkEntryAndInspectIfSoft(entry);
        }
    }

    @Override
    public void clear() {
        this.strongHead.makeHead();
        this.softHead.linkAfter(this.strongHead);
        this.map.clear();
        this.softSize = 0;
        this.strongSize = 0;
        while (this.refQueue.poll() != null) {
        }
    }

    private void relinkEntryAfterStrongHead(MruEntry entry, Object newValue) {
        if (this.unlinkEntryAndInspectIfSoft(entry) && newValue == null) {
            MruReference mref = (MruReference)entry.getValue();
            Object strongValue = mref.get();
            if (strongValue != null) {
                entry.setValue(strongValue);
                this.linkAfterStrongHead(entry);
            } else {
                this.map.remove(mref.getKey());
            }
        } else {
            if (newValue != null) {
                entry.setValue(newValue);
            }
            this.linkAfterStrongHead(entry);
        }
    }

    private void linkAfterStrongHead(MruEntry entry) {
        entry.linkAfter(this.strongHead);
        if (this.strongSize == this.strongSizeLimit) {
            MruEntry lruStrong = this.softHead.getPrevious();
            if (lruStrong != this.strongHead) {
                lruStrong.unlink();
                if (this.softSizeLimit > 0) {
                    lruStrong.linkAfter(this.softHead);
                    lruStrong.setValue(new MruReference(lruStrong, this.refQueue));
                    if (this.softSize == this.softSizeLimit) {
                        MruEntry lruSoft = this.strongHead.getPrevious();
                        lruSoft.unlink();
                        this.map.remove(lruSoft.getKey());
                    } else {
                        ++this.softSize;
                    }
                } else {
                    this.map.remove(lruStrong.getKey());
                }
            }
        } else {
            ++this.strongSize;
        }
    }

    private boolean unlinkEntryAndInspectIfSoft(MruEntry entry) {
        entry.unlink();
        if (entry.getValue() instanceof MruReference) {
            --this.softSize;
            return true;
        }
        --this.strongSize;
        return false;
    }

    private void removeClearedReferences() {
        MruReference ref;
        while ((ref = (MruReference)this.refQueue.poll()) != null) {
            this.removeInternal(ref.getKey());
        }
    }

    public int getStrongSizeLimit() {
        return this.strongSizeLimit;
    }

    public int getSoftSizeLimit() {
        return this.softSizeLimit;
    }

    public int getStrongSize() {
        return this.strongSize;
    }

    public int getSoftSize() {
        this.removeClearedReferences();
        return this.softSize;
    }

    @Override
    public int getSize() {
        return this.getSoftSize() + this.getStrongSize();
    }

    private static class MruReference
    extends SoftReference {
        private final Object key;

        MruReference(MruEntry entry, ReferenceQueue queue) {
            super(entry.getValue(), queue);
            this.key = entry.getKey();
        }

        Object getKey() {
            return this.key;
        }
    }

    private static final class MruEntry {
        private MruEntry prev;
        private MruEntry next;
        private final Object key;
        private Object value;

        MruEntry() {
            this.makeHead();
            this.value = null;
            this.key = null;
        }

        MruEntry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        Object getKey() {
            return this.key;
        }

        Object getValue() {
            return this.value;
        }

        void setValue(Object value) {
            this.value = value;
        }

        MruEntry getPrevious() {
            return this.prev;
        }

        void linkAfter(MruEntry entry) {
            this.next = entry.next;
            entry.next = this;
            this.prev = entry;
            this.next.prev = this;
        }

        void unlink() {
            this.next.prev = this.prev;
            this.prev.next = this.next;
            this.prev = null;
            this.next = null;
        }

        void makeHead() {
            this.prev = this.next = this;
        }
    }
}

