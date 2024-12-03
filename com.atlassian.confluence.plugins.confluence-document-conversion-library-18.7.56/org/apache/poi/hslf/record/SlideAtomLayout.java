/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hslf.exceptions.HSLFException;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public class SlideAtomLayout
implements GenericRecord {
    private SlideLayoutType geometry;
    private byte[] placeholderIDs;

    public SlideLayoutType getGeometryType() {
        return this.geometry;
    }

    public void setGeometryType(SlideLayoutType geom) {
        this.geometry = geom;
    }

    public SlideAtomLayout(byte[] data) {
        if (data.length != 12) {
            throw new HSLFException("SSlideLayoutAtom created with byte array not 12 bytes long - was " + data.length + " bytes in size");
        }
        this.geometry = SlideLayoutType.forNativeID(LittleEndian.getInt(data, 0));
        this.placeholderIDs = Arrays.copyOfRange(data, 4, 12);
    }

    public void writeOut(OutputStream out) throws IOException {
        byte[] buf = new byte[4];
        LittleEndian.putInt(buf, 0, this.geometry.getNativeId());
        out.write(buf);
        out.write(this.placeholderIDs);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("geometry", this::getGeometryType, "placeholderIDs", () -> this.placeholderIDs);
    }

    public static enum SlideLayoutType {
        TITLE_SLIDE(0),
        TITLE_BODY(1),
        MASTER_TITLE(2),
        MASTER_SLIDE(3),
        MASTER_NOTES(4),
        NOTES_TITLE_BODY(5),
        HANDOUT(6),
        TITLE_ONLY(7),
        TWO_COLUMNS(8),
        TWO_ROWS(9),
        COLUMN_TWO_ROWS(10),
        TWO_ROWS_COLUMN(11),
        TITLE_2_ROW_BOTTOM_2_COLUMN_BODY(12),
        TWO_COLUMNS_ROW(13),
        FOUR_OBJECTS(14),
        BIG_OBJECT(15),
        BLANK_SLIDE(16),
        VERTICAL_TITLE_BODY(17),
        VERTICAL_TWO_ROWS(18);

        private int nativeId;

        private SlideLayoutType(int nativeId) {
            this.nativeId = nativeId;
        }

        public int getNativeId() {
            return this.nativeId;
        }

        public static SlideLayoutType forNativeID(int nativeId) {
            for (SlideLayoutType ans : SlideLayoutType.values()) {
                if (ans.nativeId != nativeId) continue;
                return ans;
            }
            return null;
        }
    }
}

