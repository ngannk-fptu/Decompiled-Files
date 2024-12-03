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
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hslf.exceptions.HSLFException;
import org.apache.poi.hslf.model.textproperties.TextPropCollection;
import org.apache.poi.hslf.record.RecordAtom;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;

public final class StyleTextPropAtom
extends RecordAtom {
    public static final long _type = RecordTypes.StyleTextPropAtom.typeID;
    private final byte[] _header;
    private byte[] reserved;
    private byte[] rawContents;
    private boolean initialised;
    private List<TextPropCollection> paragraphStyles;
    private List<TextPropCollection> charStyles;

    public List<TextPropCollection> getParagraphStyles() {
        return this.paragraphStyles;
    }

    public void setParagraphStyles(List<TextPropCollection> ps) {
        this.paragraphStyles = ps;
    }

    public List<TextPropCollection> getCharacterStyles() {
        return this.charStyles;
    }

    public void setCharacterStyles(List<TextPropCollection> cs) {
        this.charStyles = cs;
    }

    public int getParagraphTextLengthCovered() {
        return this.getCharactersCovered(this.paragraphStyles);
    }

    public int getCharacterTextLengthCovered() {
        return this.getCharactersCovered(this.charStyles);
    }

    private int getCharactersCovered(List<TextPropCollection> styles) {
        return styles.stream().mapToInt(TextPropCollection::getCharactersCovered).sum();
    }

    public StyleTextPropAtom(byte[] source, int start, int len) {
        if (len < 18) {
            len = 18;
            if (source.length - start < 18) {
                throw new HSLFException("Not enough data to form a StyleTextPropAtom (min size 18 bytes long) - found " + (source.length - start));
            }
        }
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this.rawContents = IOUtils.safelyClone(source, start + 8, len - 8, StyleTextPropAtom.getMaxRecordLength());
        this.reserved = new byte[0];
        this.paragraphStyles = new ArrayList<TextPropCollection>();
        this.charStyles = new ArrayList<TextPropCollection>();
    }

    public StyleTextPropAtom(int parentTextSize) {
        this._header = new byte[8];
        this.rawContents = new byte[0];
        this.reserved = new byte[0];
        LittleEndian.putInt(this._header, 2, (short)_type);
        LittleEndian.putInt(this._header, 4, 10);
        this.paragraphStyles = new ArrayList<TextPropCollection>();
        this.charStyles = new ArrayList<TextPropCollection>();
        this.addParagraphTextPropCollection(parentTextSize);
        this.addCharacterTextPropCollection(parentTextSize);
        this.initialised = true;
        try {
            this.updateRawContents();
        }
        catch (IOException e) {
            throw new HSLFException(e);
        }
    }

    @Override
    public long getRecordType() {
        return _type;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        this.updateRawContents();
        out.write(this._header);
        out.write(this.rawContents);
        out.write(this.reserved);
    }

    public void setParentTextSize(int size) {
        TextPropCollection thisCollection;
        if (this.initialised) {
            return;
        }
        int pos = 0;
        int textHandled = 0;
        this.paragraphStyles.clear();
        this.charStyles.clear();
        int prsize = size;
        while (pos < this.rawContents.length && textHandled < prsize) {
            int textLen = LittleEndian.getInt(this.rawContents, pos);
            textLen = this.checkTextLength(textLen, textHandled, size);
            textHandled += textLen;
            short indent = LittleEndian.getShort(this.rawContents, pos += 4);
            int paraFlags = LittleEndian.getInt(this.rawContents, pos += 2);
            thisCollection = new TextPropCollection(textLen, TextPropCollection.TextPropType.paragraph);
            thisCollection.setIndentLevel(indent);
            int plSize = thisCollection.buildTextPropList(paraFlags, this.rawContents, pos += 4);
            this.paragraphStyles.add(thisCollection);
            if ((pos += plSize) >= this.rawContents.length || textHandled != size) continue;
            ++prsize;
        }
        if (this.rawContents.length > 0 && textHandled != size + 1) {
            LOG.atWarn().log("Problem reading paragraph style runs: textHandled = {}, text.size+1 = {}", (Object)Unbox.box(textHandled), (Object)Unbox.box(size + 1));
        }
        textHandled = 0;
        int chsize = size;
        while (pos < this.rawContents.length && textHandled < chsize) {
            int textLen = LittleEndian.getInt(this.rawContents, pos);
            textLen = this.checkTextLength(textLen, textHandled, size);
            textHandled += textLen;
            int charFlags = LittleEndian.getInt(this.rawContents, pos += 4);
            thisCollection = new TextPropCollection(textLen, TextPropCollection.TextPropType.character);
            int chSize = thisCollection.buildTextPropList(charFlags, this.rawContents, pos += 4);
            this.charStyles.add(thisCollection);
            if ((pos += chSize) >= this.rawContents.length || textHandled != size) continue;
            ++chsize;
        }
        if (this.rawContents.length > 0 && textHandled != size + 1) {
            LOG.atWarn().log("Problem reading character style runs: textHandled = {}, text.size+1 = {}", (Object)Unbox.box(textHandled), (Object)Unbox.box(size + 1));
        }
        if (pos < this.rawContents.length) {
            this.reserved = IOUtils.safelyClone(this.rawContents, pos, this.rawContents.length - pos, this.rawContents.length);
        }
        this.initialised = true;
    }

    private int checkTextLength(int readLength, int handledSoFar, int overallSize) {
        if (readLength + handledSoFar > overallSize + 1) {
            LOG.atWarn().log("Style length of {} at {} larger than stated size of {}, truncating", (Object)Unbox.box(readLength), (Object)Unbox.box(handledSoFar), (Object)Unbox.box(overallSize));
            return overallSize + 1 - handledSoFar;
        }
        return readLength;
    }

    private void updateRawContents() throws IOException {
        if (this.initialised) {
            try (UnsynchronizedByteArrayOutputStream baos = new UnsynchronizedByteArrayOutputStream();){
                for (TextPropCollection tpc : this.paragraphStyles) {
                    tpc.writeOut((OutputStream)baos);
                }
                for (TextPropCollection tpc : this.charStyles) {
                    tpc.writeOut((OutputStream)baos);
                }
                this.rawContents = baos.toByteArray();
            }
        }
        int newSize = this.rawContents.length + this.reserved.length;
        LittleEndian.putInt(this._header, 4, newSize);
    }

    public void clearStyles() {
        this.paragraphStyles.clear();
        this.charStyles.clear();
        this.reserved = new byte[0];
        this.initialised = true;
    }

    public TextPropCollection addParagraphTextPropCollection(int charactersCovered) {
        TextPropCollection tpc = new TextPropCollection(charactersCovered, TextPropCollection.TextPropType.paragraph);
        this.paragraphStyles.add(tpc);
        return tpc;
    }

    public void addParagraphTextPropCollection(TextPropCollection tpc) {
        this.paragraphStyles.add(tpc);
    }

    public TextPropCollection addCharacterTextPropCollection(int charactersCovered) {
        TextPropCollection tpc = new TextPropCollection(charactersCovered, TextPropCollection.TextPropType.character);
        this.charStyles.add(tpc);
        return tpc;
    }

    public void addCharacterTextPropCollection(TextPropCollection tpc) {
        this.charStyles.add(tpc);
    }

    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("StyleTextPropAtom:\n");
        if (!this.initialised) {
            out.append("Uninitialised, dumping Raw Style Data\n");
        } else {
            out.append("Paragraph properties\n");
            for (TextPropCollection pr : this.getParagraphStyles()) {
                out.append(pr);
            }
            out.append("Character properties\n");
            for (TextPropCollection pr : this.getCharacterStyles()) {
                out.append(pr);
            }
            out.append("Reserved bytes\n");
            out.append(HexDump.dump(this.reserved, 0L, 0));
        }
        out.append("  original byte stream \n");
        byte[] buf = IOUtils.safelyAllocate((long)this.rawContents.length + (long)this.reserved.length, StyleTextPropAtom.getMaxRecordLength());
        System.arraycopy(this.rawContents, 0, buf, 0, this.rawContents.length);
        System.arraycopy(this.reserved, 0, buf, this.rawContents.length, this.reserved.length);
        out.append(HexDump.dump(buf, 0L, 0));
        return out.toString();
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return !this.initialised ? null : GenericRecordUtil.getGenericProperties("paragraphStyles", this::getParagraphStyles, "characterStyles", this::getCharacterStyles);
    }
}

