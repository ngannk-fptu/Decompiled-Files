/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hslf.exceptions.CorruptPowerPointFileException;
import org.apache.poi.hslf.record.PositionDependentRecordAtom;
import org.apache.poi.hslf.record.RecordAtom;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;

public final class PersistPtrHolder
extends PositionDependentRecordAtom {
    private final byte[] _header;
    private byte[] _ptrData;
    private final long _type;
    private final Map<Integer, Integer> _slideLocations;
    private static final BitField persistIdFld = BitFieldFactory.getInstance(1048575);
    private static final BitField cntPersistFld = BitFieldFactory.getInstance(-1048576);

    @Override
    public long getRecordType() {
        return this._type;
    }

    public int[] getKnownSlideIDs() {
        int[] ids = new int[this._slideLocations.size()];
        int i = 0;
        for (Integer slideId : this._slideLocations.keySet()) {
            ids[i++] = slideId;
        }
        return ids;
    }

    public Map<Integer, Integer> getSlideLocationsLookup() {
        return Collections.unmodifiableMap(this._slideLocations);
    }

    protected PersistPtrHolder(byte[] source, int start, int len) {
        if (len < 8) {
            len = 8;
        }
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._type = LittleEndian.getUShort(this._header, 2);
        this._slideLocations = new HashMap<Integer, Integer>();
        this._ptrData = IOUtils.safelyClone(source, start + 8, len - 8, RecordAtom.getMaxRecordLength());
        int pos = 0;
        while (pos < this._ptrData.length) {
            int info = LittleEndian.getInt(this._ptrData, pos);
            int offset_no = persistIdFld.getValue(info);
            int offset_count = cntPersistFld.getValue(info);
            pos += 4;
            for (int i = 0; i < offset_count; ++i) {
                int sheet_no = offset_no + i;
                int sheet_offset = (int)LittleEndian.getUInt(this._ptrData, pos);
                this._slideLocations.put(sheet_no, sheet_offset);
                pos += 4;
            }
        }
    }

    public void clear() {
        this._slideLocations.clear();
    }

    public void addSlideLookup(int slideID, int posOnDisk) {
        if (this._slideLocations.containsKey(slideID)) {
            throw new CorruptPowerPointFileException("A record with persistId " + slideID + " already exists.");
        }
        this._slideLocations.put(slideID, posOnDisk);
    }

    @Override
    public void updateOtherRecordReferences(Map<Integer, Integer> oldToNewReferencesLookup) {
        for (Map.Entry<Integer, Integer> me : this._slideLocations.entrySet()) {
            Integer oldPos = me.getValue();
            Integer newPos = oldToNewReferencesLookup.get(oldPos);
            if (newPos == null) {
                Integer id = me.getKey();
                LOG.atWarn().log("Couldn't find the new location of the \"slide\" with id {} that used to be at {}. Not updating the position of it, you probably won't be able to find it any more (if you ever could!)", (Object)id, (Object)oldPos);
                continue;
            }
            me.setValue(newPos);
        }
    }

    private void normalizePersistDirectory() {
        int[] infoBlocks = new int[this._slideLocations.size() * 2];
        int lastSlideId = -1;
        int lastPersistIdx = 0;
        int lastIdx = -1;
        int entryCnt = 0;
        int baseSlideId = -1;
        Iterable iter = this._slideLocations.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getKey))::iterator;
        for (Map.Entry me : iter) {
            int nextSlideId = (Integer)me.getKey();
            if (lastSlideId + 1 < nextSlideId) {
                lastPersistIdx = ++lastIdx;
                entryCnt = 0;
                baseSlideId = nextSlideId;
            }
            int infoBlock = persistIdFld.setValue(0, baseSlideId);
            infoBlocks[lastPersistIdx] = infoBlock = cntPersistFld.setValue(infoBlock, ++entryCnt);
            infoBlocks[++lastIdx] = (Integer)me.getValue();
            lastSlideId = nextSlideId;
        }
        this._ptrData = new byte[(lastIdx + 1) * 4];
        for (int idx = 0; idx <= lastIdx; ++idx) {
            LittleEndian.putInt(this._ptrData, idx * 4, infoBlocks[idx]);
        }
        LittleEndian.putInt(this._header, 4, this._ptrData.length);
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        this.normalizePersistDirectory();
        out.write(this._header);
        out.write(this._ptrData);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("slideLocations", this::getSlideLocationsLookup);
    }
}

