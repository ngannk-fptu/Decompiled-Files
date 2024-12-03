/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.apache.poi.common.usermodel.fonts.FontHeader;
import org.apache.poi.common.usermodel.fonts.FontInfo;
import org.apache.poi.common.usermodel.fonts.FontPitch;
import org.apache.poi.hslf.record.FontEmbeddedData;
import org.apache.poi.hslf.record.FontEntityAtom;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.RecordAtom;
import org.apache.poi.hslf.record.RecordContainer;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.hslf.usermodel.HSLFFontInfo;
import org.apache.poi.util.IOUtils;

public final class FontCollection
extends RecordContainer {
    private final Map<Integer, HSLFFontInfo> fonts = new LinkedHashMap<Integer, HSLFFontInfo>();
    private byte[] _header;

    FontCollection(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        for (Record r : this._children = Record.findChildRecords(source, start + 8, len - 8)) {
            if (r instanceof FontEntityAtom) {
                HSLFFontInfo fi = new HSLFFontInfo((FontEntityAtom)r);
                this.fonts.put(fi.getIndex(), fi);
                continue;
            }
            if (r instanceof FontEmbeddedData) {
                FontEmbeddedData fed = (FontEmbeddedData)r;
                FontHeader fontHeader = fed.getFontHeader();
                HSLFFontInfo fi = this.addFont(fontHeader);
                fi.addFacet(fed);
                continue;
            }
            LOG.atWarn().log("FontCollection child wasn't a FontEntityAtom, was {}", (Object)r.getClass().getSimpleName());
        }
    }

    @Override
    public long getRecordType() {
        return RecordTypes.FontCollection.typeID;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        this.writeOut(this._header[0], this._header[1], this.getRecordType(), this._children, out);
    }

    public HSLFFontInfo addFont(FontInfo fontInfo) {
        HSLFFontInfo fi = this.getFontInfo(fontInfo.getTypeface(), fontInfo.getCharset());
        if (fi != null) {
            return fi;
        }
        fi = new HSLFFontInfo(fontInfo);
        fi.setIndex(this.fonts.size());
        this.fonts.put(fi.getIndex(), fi);
        FontEntityAtom fnt = fi.createRecord();
        this.appendChildRecord(fnt);
        return fi;
    }

    public HSLFFontInfo addFont(InputStream fontData) throws IOException {
        FontHeader fontHeader = new FontHeader();
        InputStream is = fontHeader.bufferInit(fontData);
        HSLFFontInfo fi = this.addFont(fontHeader);
        FontEntityAtom fea = fi.getFontEntityAtom();
        assert (fea != null);
        fea.setCharSet(fontHeader.getCharsetByte());
        fea.setPitchAndFamily(FontPitch.getNativeId(fontHeader.getPitch(), fontHeader.getFamily()));
        fea.setFontFlags(1);
        fea.setFontType(12);
        RecordAtom after = fea;
        int insertIdx = FontCollection.getFacetIndex(fontHeader.isItalic(), fontHeader.isBold());
        FontEmbeddedData newChild = null;
        for (FontEmbeddedData fed : fi.getFacets()) {
            FontHeader fh = fed.getFontHeader();
            int curIdx = FontCollection.getFacetIndex(fh.isItalic(), fh.isBold());
            if (curIdx == insertIdx) {
                newChild = fed;
                break;
            }
            if (curIdx > insertIdx) break;
            after = fed;
        }
        if (newChild == null) {
            newChild = new FontEmbeddedData();
            this.addChildAfter(newChild, after);
            fi.addFacet(newChild);
        }
        newChild.setFontData(IOUtils.toByteArray(is));
        return fi;
    }

    private static int getFacetIndex(boolean isItalic, boolean isBold) {
        return (isItalic ? 2 : 0) | (isBold ? 1 : 0);
    }

    public HSLFFontInfo getFontInfo(String typeface) {
        return this.getFontInfo(typeface, null);
    }

    public HSLFFontInfo getFontInfo(String typeface, FontCharset charset) {
        return this.fonts.values().stream().filter(FontCollection.findFont(typeface, charset)).findFirst().orElse(null);
    }

    private static Predicate<HSLFFontInfo> findFont(String typeface, FontCharset charset) {
        return fi -> typeface.equals(fi.getTypeface()) && (charset == null || charset.equals((Object)fi.getCharset()));
    }

    public HSLFFontInfo getFontInfo(int index) {
        for (HSLFFontInfo fi : this.fonts.values()) {
            if (fi.getIndex() != index) continue;
            return fi;
        }
        return null;
    }

    public int getNumberOfFonts() {
        return this.fonts.size();
    }

    public List<HSLFFontInfo> getFonts() {
        return new ArrayList<HSLFFontInfo>(this.fonts.values());
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return null;
    }
}

