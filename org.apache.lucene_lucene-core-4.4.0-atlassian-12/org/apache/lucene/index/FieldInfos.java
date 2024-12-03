/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexableFieldType;

public class FieldInfos
implements Iterable<FieldInfo> {
    private final boolean hasFreq;
    private final boolean hasProx;
    private final boolean hasPayloads;
    private final boolean hasOffsets;
    private final boolean hasVectors;
    private final boolean hasNorms;
    private final boolean hasDocValues;
    private final SortedMap<Integer, FieldInfo> byNumber = new TreeMap<Integer, FieldInfo>();
    private final HashMap<String, FieldInfo> byName = new HashMap();
    private final Collection<FieldInfo> values;

    public FieldInfos(FieldInfo[] infos) {
        boolean hasVectors = false;
        boolean hasProx = false;
        boolean hasPayloads = false;
        boolean hasOffsets = false;
        boolean hasFreq = false;
        boolean hasNorms = false;
        boolean hasDocValues = false;
        for (FieldInfo info : infos) {
            FieldInfo previous = this.byNumber.put(info.number, info);
            if (previous != null) {
                throw new IllegalArgumentException("duplicate field numbers: " + previous.name + " and " + info.name + " have: " + info.number);
            }
            previous = this.byName.put(info.name, info);
            if (previous != null) {
                throw new IllegalArgumentException("duplicate field names: " + previous.number + " and " + info.number + " have: " + info.name);
            }
            hasVectors |= info.hasVectors();
            hasProx |= info.isIndexed() && info.getIndexOptions().compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) >= 0;
            hasFreq |= info.isIndexed() && info.getIndexOptions() != FieldInfo.IndexOptions.DOCS_ONLY;
            hasOffsets |= info.isIndexed() && info.getIndexOptions().compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) >= 0;
            hasNorms |= info.hasNorms();
            hasDocValues |= info.hasDocValues();
            hasPayloads |= info.hasPayloads();
        }
        this.hasVectors = hasVectors;
        this.hasProx = hasProx;
        this.hasPayloads = hasPayloads;
        this.hasOffsets = hasOffsets;
        this.hasFreq = hasFreq;
        this.hasNorms = hasNorms;
        this.hasDocValues = hasDocValues;
        this.values = Collections.unmodifiableCollection(this.byNumber.values());
    }

    public boolean hasFreq() {
        return this.hasFreq;
    }

    public boolean hasProx() {
        return this.hasProx;
    }

    public boolean hasPayloads() {
        return this.hasPayloads;
    }

    public boolean hasOffsets() {
        return this.hasOffsets;
    }

    public boolean hasVectors() {
        return this.hasVectors;
    }

    public boolean hasNorms() {
        return this.hasNorms;
    }

    public boolean hasDocValues() {
        return this.hasDocValues;
    }

    public int size() {
        assert (this.byNumber.size() == this.byName.size());
        return this.byNumber.size();
    }

    @Override
    public Iterator<FieldInfo> iterator() {
        return this.values.iterator();
    }

    public FieldInfo fieldInfo(String fieldName) {
        return this.byName.get(fieldName);
    }

    public FieldInfo fieldInfo(int fieldNumber) {
        return fieldNumber >= 0 ? (FieldInfo)this.byNumber.get(fieldNumber) : null;
    }

    static final class Builder {
        private final HashMap<String, FieldInfo> byName = new HashMap();
        final FieldNumbers globalFieldNumbers;

        Builder() {
            this(new FieldNumbers());
        }

        Builder(FieldNumbers globalFieldNumbers) {
            assert (globalFieldNumbers != null);
            this.globalFieldNumbers = globalFieldNumbers;
        }

        public void add(FieldInfos other) {
            for (FieldInfo fieldInfo : other) {
                this.add(fieldInfo);
            }
        }

        public FieldInfo addOrUpdate(String name, IndexableFieldType fieldType) {
            return this.addOrUpdateInternal(name, -1, fieldType.indexed(), false, fieldType.omitNorms(), false, fieldType.indexOptions(), fieldType.docValueType(), null);
        }

        private FieldInfo addOrUpdateInternal(String name, int preferredFieldNumber, boolean isIndexed, boolean storeTermVector, boolean omitNorms, boolean storePayloads, FieldInfo.IndexOptions indexOptions, FieldInfo.DocValuesType docValues, FieldInfo.DocValuesType normType) {
            FieldInfo fi = this.fieldInfo(name);
            if (fi == null) {
                int fieldNumber = this.globalFieldNumbers.addOrGet(name, preferredFieldNumber, docValues);
                fi = new FieldInfo(name, isIndexed, fieldNumber, storeTermVector, omitNorms, storePayloads, indexOptions, docValues, normType, null);
                assert (!this.byName.containsKey(fi.name));
                assert (this.globalFieldNumbers.containsConsistent(fi.number, fi.name, fi.getDocValuesType()));
                this.byName.put(fi.name, fi);
            } else {
                fi.update(isIndexed, storeTermVector, omitNorms, storePayloads, indexOptions);
                if (docValues != null) {
                    fi.setDocValuesType(docValues);
                }
                if (!fi.omitsNorms() && normType != null) {
                    fi.setNormValueType(normType);
                }
            }
            return fi;
        }

        public FieldInfo add(FieldInfo fi) {
            return this.addOrUpdateInternal(fi.name, fi.number, fi.isIndexed(), fi.hasVectors(), fi.omitsNorms(), fi.hasPayloads(), fi.getIndexOptions(), fi.getDocValuesType(), fi.getNormType());
        }

        public FieldInfo fieldInfo(String fieldName) {
            return this.byName.get(fieldName);
        }

        final FieldInfos finish() {
            return new FieldInfos(this.byName.values().toArray(new FieldInfo[this.byName.size()]));
        }
    }

    static final class FieldNumbers {
        private final Map<Integer, String> numberToName;
        private final Map<String, Integer> nameToNumber = new HashMap<String, Integer>();
        private final Map<String, FieldInfo.DocValuesType> docValuesType;
        private int lowestUnassignedFieldNumber = -1;

        FieldNumbers() {
            this.numberToName = new HashMap<Integer, String>();
            this.docValuesType = new HashMap<String, FieldInfo.DocValuesType>();
        }

        synchronized int addOrGet(String fieldName, int preferredFieldNumber, FieldInfo.DocValuesType dvType) {
            Integer fieldNumber;
            if (dvType != null) {
                FieldInfo.DocValuesType currentDVType = this.docValuesType.get(fieldName);
                if (currentDVType == null) {
                    this.docValuesType.put(fieldName, dvType);
                } else if (currentDVType != null && currentDVType != dvType) {
                    throw new IllegalArgumentException("cannot change DocValues type from " + (Object)((Object)currentDVType) + " to " + (Object)((Object)dvType) + " for field \"" + fieldName + "\"");
                }
            }
            if ((fieldNumber = this.nameToNumber.get(fieldName)) == null) {
                Integer preferredBoxed = preferredFieldNumber;
                if (preferredFieldNumber != -1 && !this.numberToName.containsKey(preferredBoxed)) {
                    fieldNumber = preferredBoxed;
                } else {
                    while (this.numberToName.containsKey(++this.lowestUnassignedFieldNumber)) {
                    }
                    fieldNumber = this.lowestUnassignedFieldNumber;
                }
                this.numberToName.put(fieldNumber, fieldName);
                this.nameToNumber.put(fieldName, fieldNumber);
            }
            return fieldNumber;
        }

        synchronized boolean containsConsistent(Integer number, String name, FieldInfo.DocValuesType dvType) {
            return name.equals(this.numberToName.get(number)) && number.equals(this.nameToNumber.get(name)) && (dvType == null || this.docValuesType.get(name) == null || dvType == this.docValuesType.get(name));
        }

        synchronized void clear() {
            this.numberToName.clear();
            this.nameToNumber.clear();
            this.docValuesType.clear();
        }
    }
}

