/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Supplier;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;

public abstract class PageBreakRecord
extends StandardRecord {
    private static final int[] EMPTY_INT_ARRAY = new int[0];
    private final ArrayList<Break> _breaks = new ArrayList();
    private final Map<Integer, Break> _breakMap = new HashMap<Integer, Break>();

    protected PageBreakRecord() {
    }

    protected PageBreakRecord(PageBreakRecord other) {
        this._breaks.addAll(other._breaks);
        this.initMap();
    }

    protected PageBreakRecord(RecordInputStream in) {
        int nBreaks = in.readShort();
        this._breaks.ensureCapacity(nBreaks + 2);
        for (int k = 0; k < nBreaks; ++k) {
            this._breaks.add(new Break(in));
        }
        this.initMap();
    }

    private void initMap() {
        this._breaks.forEach(br -> this._breakMap.put(((Break)br).main, (Break)br));
    }

    public boolean isEmpty() {
        return this._breaks.isEmpty();
    }

    @Override
    protected int getDataSize() {
        return 2 + this._breaks.size() * 6;
    }

    @Override
    public final void serialize(LittleEndianOutput out) {
        int nBreaks = this._breaks.size();
        out.writeShort(nBreaks);
        for (Break aBreak : this._breaks) {
            aBreak.serialize(out);
        }
    }

    public int getNumBreaks() {
        return this._breaks.size();
    }

    public final Iterator<Break> getBreaksIterator() {
        return this._breaks.iterator();
    }

    public final Spliterator<Break> getBreaksSpliterator() {
        return this._breaks.spliterator();
    }

    public void addBreak(int main, int subFrom, int subTo) {
        Integer key = main;
        Break region = this._breakMap.get(key);
        if (region == null) {
            region = new Break(main, subFrom, subTo);
            this._breakMap.put(key, region);
            this._breaks.add(region);
        } else {
            region.main = main;
            region.subFrom = subFrom;
            region.subTo = subTo;
        }
    }

    public final void removeBreak(int main) {
        Integer rowKey = main;
        Break region = this._breakMap.get(rowKey);
        this._breaks.remove(region);
        this._breakMap.remove(rowKey);
    }

    public final Break getBreak(int main) {
        Integer rowKey = main;
        return this._breakMap.get(rowKey);
    }

    public final int[] getBreaks() {
        int count = this.getNumBreaks();
        if (count < 1) {
            return EMPTY_INT_ARRAY;
        }
        int[] result = new int[count];
        for (int i = 0; i < count; ++i) {
            Break breakItem = this._breaks.get(i);
            result[i] = breakItem.main;
        }
        return result;
    }

    @Override
    public abstract PageBreakRecord copy();

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("numBreaks", this::getNumBreaks, "breaks", () -> this._breaks);
    }

    public static final class Break
    implements GenericRecord {
        public static final int ENCODED_SIZE = 6;
        private int main;
        private int subFrom;
        private int subTo;

        public Break(Break other) {
            this.main = other.main;
            this.subFrom = other.subFrom;
            this.subTo = other.subTo;
        }

        public Break(int main, int subFrom, int subTo) {
            this.main = main;
            this.subFrom = subFrom;
            this.subTo = subTo;
        }

        public Break(RecordInputStream in) {
            this.main = in.readUShort() - 1;
            this.subFrom = in.readUShort();
            this.subTo = in.readUShort();
        }

        public int getMain() {
            return this.main;
        }

        public int getSubFrom() {
            return this.subFrom;
        }

        public int getSubTo() {
            return this.subTo;
        }

        public void serialize(LittleEndianOutput out) {
            out.writeShort(this.main + 1);
            out.writeShort(this.subFrom);
            out.writeShort(this.subTo);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("main", () -> this.main, "subFrom", () -> this.subFrom, "subTo", () -> this.subTo);
        }
    }
}

