/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.MapOfSets;

public final class FieldCacheSanityChecker {
    private boolean estimateRam;

    public void setRamUsageEstimator(boolean flag) {
        this.estimateRam = flag;
    }

    public static Insanity[] checkSanity(FieldCache cache) {
        return FieldCacheSanityChecker.checkSanity(cache.getCacheEntries());
    }

    public static Insanity[] checkSanity(FieldCache.CacheEntry ... cacheEntries) {
        FieldCacheSanityChecker sanityChecker = new FieldCacheSanityChecker();
        sanityChecker.setRamUsageEstimator(true);
        return sanityChecker.check(cacheEntries);
    }

    public Insanity[] check(FieldCache.CacheEntry ... cacheEntries) {
        if (null == cacheEntries || 0 == cacheEntries.length) {
            return new Insanity[0];
        }
        if (this.estimateRam) {
            for (int i = 0; i < cacheEntries.length; ++i) {
                cacheEntries[i].estimateSize();
            }
        }
        MapOfSets<Integer, FieldCache.CacheEntry> valIdToItems = new MapOfSets<Integer, FieldCache.CacheEntry>(new HashMap(17));
        MapOfSets<ReaderField, Integer> readerFieldToValIds = new MapOfSets<ReaderField, Integer>(new HashMap(17));
        HashSet<ReaderField> valMismatchKeys = new HashSet<ReaderField>();
        for (int i = 0; i < cacheEntries.length; ++i) {
            FieldCache.CacheEntry item = cacheEntries[i];
            Object val = item.getValue();
            if (val instanceof Bits || val instanceof FieldCache.CreationPlaceholder) continue;
            ReaderField rf = new ReaderField(item.getReaderKey(), item.getFieldName());
            Integer valId = System.identityHashCode(val);
            valIdToItems.put(valId, item);
            if (1 >= readerFieldToValIds.put(rf, valId)) continue;
            valMismatchKeys.add(rf);
        }
        ArrayList<Insanity> insanity = new ArrayList<Insanity>(valMismatchKeys.size() * 3);
        insanity.addAll(this.checkValueMismatch(valIdToItems, readerFieldToValIds, valMismatchKeys));
        insanity.addAll(this.checkSubreaders(valIdToItems, readerFieldToValIds));
        return insanity.toArray(new Insanity[insanity.size()]);
    }

    private Collection<Insanity> checkValueMismatch(MapOfSets<Integer, FieldCache.CacheEntry> valIdToItems, MapOfSets<ReaderField, Integer> readerFieldToValIds, Set<ReaderField> valMismatchKeys) {
        ArrayList<Insanity> insanity = new ArrayList<Insanity>(valMismatchKeys.size() * 3);
        if (!valMismatchKeys.isEmpty()) {
            Map<ReaderField, Set<Integer>> rfMap = readerFieldToValIds.getMap();
            Map<Integer, Set<FieldCache.CacheEntry>> valMap = valIdToItems.getMap();
            for (ReaderField rf : valMismatchKeys) {
                ArrayList<FieldCache.CacheEntry> badEntries = new ArrayList<FieldCache.CacheEntry>(valMismatchKeys.size() * 2);
                for (Integer value : rfMap.get(rf)) {
                    for (FieldCache.CacheEntry cacheEntry : valMap.get(value)) {
                        badEntries.add(cacheEntry);
                    }
                }
                FieldCache.CacheEntry[] badness = new FieldCache.CacheEntry[badEntries.size()];
                badness = badEntries.toArray(badness);
                insanity.add(new Insanity(InsanityType.VALUEMISMATCH, "Multiple distinct value objects for " + rf.toString(), badness));
            }
        }
        return insanity;
    }

    private Collection<Insanity> checkSubreaders(MapOfSets<Integer, FieldCache.CacheEntry> valIdToItems, MapOfSets<ReaderField, Integer> readerFieldToValIds) {
        Collection<Object> kids;
        ArrayList<Insanity> insanity = new ArrayList<Insanity>(23);
        HashMap badChildren = new HashMap(17);
        MapOfSets<ReaderField, ReaderField> badKids = new MapOfSets<ReaderField, ReaderField>(badChildren);
        Map<Integer, Set<FieldCache.CacheEntry>> viToItemSets = valIdToItems.getMap();
        Map<ReaderField, Set<Integer>> rfToValIdSets = readerFieldToValIds.getMap();
        HashSet<ReaderField> seen = new HashSet<ReaderField>(17);
        Set<ReaderField> readerFields = rfToValIdSets.keySet();
        for (ReaderField rf : readerFields) {
            if (seen.contains(rf)) continue;
            kids = this.getAllDescendantReaderKeys(rf.readerKey);
            for (Object object : kids) {
                ReaderField readerField = new ReaderField(object, rf.fieldName);
                if (badChildren.containsKey(readerField)) {
                    badKids.put(rf, readerField);
                    badKids.putAll(rf, (Collection)badChildren.get(readerField));
                    badChildren.remove(readerField);
                } else if (rfToValIdSets.containsKey(readerField)) {
                    badKids.put(rf, readerField);
                }
                seen.add(readerField);
            }
            seen.add(rf);
        }
        for (ReaderField parent : badChildren.keySet()) {
            kids = (Set)badChildren.get(parent);
            ArrayList badEntries = new ArrayList(kids.size() * 2);
            for (Integer n : rfToValIdSets.get(parent)) {
                badEntries.addAll(viToItemSets.get(n));
            }
            for (ReaderField readerField : kids) {
                for (Integer value : rfToValIdSets.get(readerField)) {
                    badEntries.addAll(viToItemSets.get(value));
                }
            }
            FieldCache.CacheEntry[] cacheEntryArray2 = new FieldCache.CacheEntry[badEntries.size()];
            cacheEntryArray2 = badEntries.toArray(cacheEntryArray2);
            insanity.add(new Insanity(InsanityType.SUBREADER, "Found caches for descendants of " + parent.toString(), cacheEntryArray2));
        }
        return insanity;
    }

    private List<Object> getAllDescendantReaderKeys(Object seed) {
        ArrayList<Object> all = new ArrayList<Object>(17);
        all.add(seed);
        for (int i = 0; i < all.size(); ++i) {
            Object obj = all.get(i);
            if (!(obj instanceof IndexReader)) continue;
            try {
                List<IndexReaderContext> childs = ((IndexReader)obj).getContext().children();
                if (childs == null) continue;
                for (IndexReaderContext ctx : childs) {
                    all.add(ctx.reader().getCoreCacheKey());
                }
                continue;
            }
            catch (AlreadyClosedException alreadyClosedException) {
                // empty catch block
            }
        }
        return all.subList(1, all.size());
    }

    public static final class InsanityType {
        private final String label;
        public static final InsanityType SUBREADER = new InsanityType("SUBREADER");
        public static final InsanityType VALUEMISMATCH = new InsanityType("VALUEMISMATCH");
        public static final InsanityType EXPECTED = new InsanityType("EXPECTED");

        private InsanityType(String label) {
            this.label = label;
        }

        public String toString() {
            return this.label;
        }
    }

    public static final class Insanity {
        private final InsanityType type;
        private final String msg;
        private final FieldCache.CacheEntry[] entries;

        public Insanity(InsanityType type, String msg, FieldCache.CacheEntry ... entries) {
            if (null == type) {
                throw new IllegalArgumentException("Insanity requires non-null InsanityType");
            }
            if (null == entries || 0 == entries.length) {
                throw new IllegalArgumentException("Insanity requires non-null/non-empty CacheEntry[]");
            }
            this.type = type;
            this.msg = msg;
            this.entries = entries;
        }

        public InsanityType getType() {
            return this.type;
        }

        public String getMsg() {
            return this.msg;
        }

        public FieldCache.CacheEntry[] getCacheEntries() {
            return this.entries;
        }

        public String toString() {
            StringBuilder buf = new StringBuilder();
            buf.append(this.getType()).append(": ");
            String m = this.getMsg();
            if (null != m) {
                buf.append(m);
            }
            buf.append('\n');
            FieldCache.CacheEntry[] ce = this.getCacheEntries();
            for (int i = 0; i < ce.length; ++i) {
                buf.append('\t').append(ce[i].toString()).append('\n');
            }
            return buf.toString();
        }
    }

    private static final class ReaderField {
        public final Object readerKey;
        public final String fieldName;

        public ReaderField(Object readerKey, String fieldName) {
            this.readerKey = readerKey;
            this.fieldName = fieldName;
        }

        public int hashCode() {
            return System.identityHashCode(this.readerKey) * this.fieldName.hashCode();
        }

        public boolean equals(Object that) {
            if (!(that instanceof ReaderField)) {
                return false;
            }
            ReaderField other = (ReaderField)that;
            return this.readerKey == other.readerKey && this.fieldName.equals(other.fieldName);
        }

        public String toString() {
            return this.readerKey.toString() + "+" + this.fieldName;
        }
    }
}

