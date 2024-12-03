/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hslf.exceptions.HSLFException;
import org.apache.poi.hslf.model.textproperties.TextPropCollection;
import org.apache.poi.hslf.record.RecordAtom;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.sl.usermodel.TextShape;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LittleEndianOutputStream;

public final class TxMasterStyleAtom
extends RecordAtom {
    private static final Logger LOG = LogManager.getLogger(TxMasterStyleAtom.class);
    public static final int MAX_INDENT = 5;
    private static final long _type = RecordTypes.TxMasterStyleAtom.typeID;
    private final byte[] _header;
    private byte[] _data;
    private List<TextPropCollection> paragraphStyles;
    private List<TextPropCollection> charStyles;

    protected TxMasterStyleAtom(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._data = IOUtils.safelyClone(source, start + 8, len - 8, TxMasterStyleAtom.getMaxRecordLength());
        try {
            this.init();
        }
        catch (Exception e) {
            LOG.atWarn().withThrowable(e).log("Exception when reading available styles");
        }
    }

    @Override
    public long getRecordType() {
        return _type;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this._header);
        out.write(this._data);
    }

    public List<TextPropCollection> getCharacterStyles() {
        return this.charStyles;
    }

    public List<TextPropCollection> getParagraphStyles() {
        return this.paragraphStyles;
    }

    public int getTextType() {
        return LittleEndian.getShort(this._header, 0) >> 4;
    }

    protected void init() {
        int type = this.getTextType();
        int pos = 0;
        short levels = LittleEndian.getShort(this._data, 0);
        pos += 2;
        this.paragraphStyles = new ArrayList<TextPropCollection>(levels);
        this.charStyles = new ArrayList<TextPropCollection>(levels);
        for (short i = 0; i < levels; i = (short)(i + 1)) {
            TextPropCollection prprops = new TextPropCollection(0, TextPropCollection.TextPropType.paragraph);
            if (type >= TextShape.TextPlaceholder.CENTER_BODY.nativeId) {
                short indentLevel = LittleEndian.getShort(this._data, pos);
                prprops.setIndentLevel(indentLevel);
                pos += 2;
            } else {
                prprops.setIndentLevel((short)-1);
            }
            int head = LittleEndian.getInt(this._data, pos);
            pos += 4;
            pos += prprops.buildTextPropList(head, this._data, pos);
            this.paragraphStyles.add(prprops);
            head = LittleEndian.getInt(this._data, pos);
            pos += 4;
            TextPropCollection chprops = new TextPropCollection(0, TextPropCollection.TextPropType.character);
            pos += chprops.buildTextPropList(head, this._data, pos);
            this.charStyles.add(chprops);
        }
    }

    public void updateStyles() {
        int type = this.getTextType();
        try {
            UnsynchronizedByteArrayOutputStream bos = new UnsynchronizedByteArrayOutputStream();
            LittleEndianOutputStream leos = new LittleEndianOutputStream((OutputStream)bos);
            int levels = this.paragraphStyles.size();
            leos.writeShort(levels);
            for (int i = 0; i < levels; ++i) {
                TextPropCollection prdummy = this.paragraphStyles.get(i).copy();
                TextPropCollection chdummy = this.charStyles.get(i).copy();
                if (type >= TextShape.TextPlaceholder.CENTER_BODY.nativeId) {
                    leos.writeShort(prdummy.getIndentLevel());
                }
                prdummy.setIndentLevel((short)-1);
                prdummy.writeOut((OutputStream)bos, true);
                chdummy.writeOut((OutputStream)bos, true);
            }
            this._data = bos.toByteArray();
            leos.close();
            LittleEndian.putInt(this._header, 4, this._data.length);
        }
        catch (IOException e) {
            throw new HSLFException("error in updating master style properties", e);
        }
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("paragraphStyles", this::getParagraphStyles, "charStyles", this::getCharacterStyles);
    }
}

