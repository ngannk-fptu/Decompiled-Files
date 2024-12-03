/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.hslf.model.textproperties;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hslf.exceptions.HSLFException;
import org.apache.poi.hslf.model.textproperties.BitMaskTextProp;
import org.apache.poi.hslf.model.textproperties.CharFlagsTextProp;
import org.apache.poi.hslf.model.textproperties.FontAlignmentProp;
import org.apache.poi.hslf.model.textproperties.HSLFTabStopPropCollection;
import org.apache.poi.hslf.model.textproperties.ParagraphFlagsTextProp;
import org.apache.poi.hslf.model.textproperties.TextAlignmentProp;
import org.apache.poi.hslf.model.textproperties.TextProp;
import org.apache.poi.hslf.model.textproperties.WrapFlagsTextProp;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

public class TextPropCollection
implements GenericRecord,
Duplicatable {
    private static final Logger LOG = LogManager.getLogger(TextPropCollection.class);
    private static final TextProp[] paragraphTextPropTypes = new TextProp[]{new ParagraphFlagsTextProp(), new TextProp(2, 128, "bullet.char"), new TextProp(2, 16, "bullet.font"), new TextProp(2, 64, "bullet.size"), new TextProp(4, 32, "bullet.color"), new TextAlignmentProp(), new TextProp(2, 4096, "linespacing"), new TextProp(2, 8192, "spacebefore"), new TextProp(2, 16384, "spaceafter"), new TextProp(2, 256, "text.offset"), new TextProp(2, 1024, "bullet.offset"), new TextProp(2, 32768, "defaultTabSize"), new HSLFTabStopPropCollection(), new FontAlignmentProp(), new WrapFlagsTextProp(), new TextProp(2, 0x200000, "textDirection"), new TextProp(0, 0x800000, "bullet.blip"), new TextProp(0, 0x1000000, "bullet.scheme"), new TextProp(0, 0x2000000, "hasBulletScheme")};
    private static final TextProp[] characterTextPropTypes = new TextProp[]{new TextProp(0, 0x100000, "pp10ext"), new TextProp(0, 0x1000000, "newAsian.font.index"), new TextProp(0, 0x2000000, "cs.font.index"), new TextProp(0, 0x4000000, "pp11ext"), new CharFlagsTextProp(), new TextProp(2, 65536, "font.index"), new TextProp(2, 0x200000, "asian.font.index"), new TextProp(2, 0x400000, "ansi.font.index"), new TextProp(2, 0x800000, "symbol.font.index"), new TextProp(2, 131072, "font.size"), new TextProp(4, 262144, "font.color"), new TextProp(2, 524288, "superscript")};
    private int charactersCovered;
    private short indentLevel;
    private final Map<String, TextProp> textProps = new HashMap<String, TextProp>();
    private int maskSpecial;
    private final TextPropType textPropType;

    public TextPropCollection(int charactersCovered, TextPropType textPropType) {
        this.charactersCovered = charactersCovered;
        this.textPropType = textPropType;
    }

    public TextPropCollection(TextPropCollection other) {
        this.charactersCovered = other.charactersCovered;
        this.indentLevel = other.indentLevel;
        this.maskSpecial = other.maskSpecial;
        this.textPropType = other.textPropType;
        other.textProps.forEach((k, v) -> this.textProps.put((String)k, v.copy()));
    }

    public int getSpecialMask() {
        return this.maskSpecial;
    }

    public int getCharactersCovered() {
        return this.charactersCovered;
    }

    public List<TextProp> getTextPropList() {
        ArrayList<TextProp> orderedList = new ArrayList<TextProp>();
        for (TextProp potProp : this.getPotentialProperties()) {
            TextProp textProp = this.textProps.get(potProp.getName());
            if (textProp == null) continue;
            orderedList.add(textProp);
        }
        return orderedList;
    }

    public final <T extends TextProp> T findByName(String textPropName) {
        return (T)this.textProps.get(textPropName);
    }

    public final <T extends TextProp> T removeByName(String name) {
        return (T)this.textProps.remove(name);
    }

    public final TextPropType getTextPropType() {
        return this.textPropType;
    }

    private TextProp[] getPotentialProperties() {
        return this.textPropType == TextPropType.paragraph ? paragraphTextPropTypes : characterTextPropTypes;
    }

    private <T extends TextProp> T validatePropName(String name) {
        for (TextProp tp : this.getPotentialProperties()) {
            if (!tp.getName().equals(name)) continue;
            return (T)tp;
        }
        String errStr = "No TextProp with name " + name + " is defined to add from. Character and paragraphs have their own properties/names.";
        throw new HSLFException(errStr);
    }

    public final <T extends TextProp> T addWithName(String name) {
        T existing = this.findByName(name);
        if (existing != null) {
            return existing;
        }
        TextProp textProp = ((TextProp)this.validatePropName(name)).copy();
        this.textProps.put(name, textProp);
        return (T)textProp;
    }

    public final void addProp(TextProp textProp) {
        if (textProp == null) {
            throw new HSLFException("TextProp must not be null");
        }
        String propName = textProp.getName();
        this.validatePropName(propName);
        this.textProps.put(propName, textProp);
    }

    public int buildTextPropList(int containsField, byte[] data, int dataOffset) {
        int bytesPassed = 0;
        for (TextProp tp : this.getPotentialProperties()) {
            if ((containsField & tp.getMask()) == 0) continue;
            if (dataOffset + bytesPassed >= data.length) {
                this.maskSpecial |= tp.getMask();
                return bytesPassed;
            }
            TextProp prop = tp.copy();
            int val = 0;
            if (prop instanceof HSLFTabStopPropCollection) {
                ((HSLFTabStopPropCollection)prop).parseProperty(data, dataOffset + bytesPassed);
            } else if (prop.getSize() == 2) {
                val = LittleEndian.getShort(data, dataOffset + bytesPassed);
            } else if (prop.getSize() == 4) {
                val = LittleEndian.getInt(data, dataOffset + bytesPassed);
            } else if (prop.getSize() == 0) {
                this.maskSpecial |= tp.getMask();
                continue;
            }
            if (prop instanceof BitMaskTextProp) {
                ((BitMaskTextProp)prop).setValueWithMask(val, containsField);
            } else if (!(prop instanceof HSLFTabStopPropCollection)) {
                prop.setValue(val);
            }
            bytesPassed += prop.getSize();
            this.addProp(prop);
        }
        return bytesPassed;
    }

    @Override
    public TextPropCollection copy() {
        return new TextPropCollection(this);
    }

    public void updateTextSize(int textSize) {
        this.charactersCovered = textSize;
    }

    public void writeOut(OutputStream o) throws IOException {
        this.writeOut(o, false);
    }

    public void writeOut(OutputStream o, boolean isMasterStyle) throws IOException {
        if (!isMasterStyle) {
            Record.writeLittleEndian(this.charactersCovered, o);
        }
        if (this.textPropType == TextPropType.paragraph && this.indentLevel > -1) {
            Record.writeLittleEndian(this.indentLevel, o);
        }
        int mask = this.maskSpecial;
        for (TextProp textProp : this.textProps.values()) {
            mask |= textProp.getWriteMask();
        }
        Record.writeLittleEndian(mask, o);
        for (TextProp textProp : this.getTextPropList()) {
            int val = textProp.getValue();
            if (textProp instanceof BitMaskTextProp && textProp.getWriteMask() == 0) continue;
            if (textProp.getSize() == 2) {
                Record.writeLittleEndian((short)val, o);
                continue;
            }
            if (textProp.getSize() == 4) {
                Record.writeLittleEndian(val, o);
                continue;
            }
            if (!(textProp instanceof HSLFTabStopPropCollection)) continue;
            ((HSLFTabStopPropCollection)textProp).writeProperty(o);
        }
    }

    public short getIndentLevel() {
        return this.indentLevel;
    }

    public void setIndentLevel(short indentLevel) {
        if (this.textPropType == TextPropType.character) {
            throw new RuntimeException("trying to set an indent on a character collection.");
        }
        this.indentLevel = indentLevel;
    }

    public int hashCode() {
        return Objects.hash(this.charactersCovered, this.maskSpecial, this.indentLevel, this.textProps);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (this.getClass() != other.getClass()) {
            return false;
        }
        TextPropCollection o = (TextPropCollection)other;
        if (o.maskSpecial != this.maskSpecial || o.indentLevel != this.indentLevel) {
            return false;
        }
        return this.textProps.equals(o.textProps);
    }

    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("  chars covered: ").append(this.getCharactersCovered());
        out.append("  special mask flags: 0x").append(HexDump.toHex(this.getSpecialMask())).append("\n");
        if (this.textPropType == TextPropType.paragraph) {
            out.append("  indent level: ").append(this.getIndentLevel()).append("\n");
        }
        for (TextProp p : this.getTextPropList()) {
            out.append("    ");
            out.append(p.toString());
            out.append("\n");
            if (!(p instanceof BitMaskTextProp)) continue;
            BitMaskTextProp bm = (BitMaskTextProp)p;
            int i = 0;
            for (String s : bm.getSubPropNames()) {
                if (bm.getSubPropMatches()[i]) {
                    out.append("          ").append(s).append(" = ").append(bm.getSubValue(i)).append("\n");
                }
                ++i;
            }
        }
        out.append("  bytes that would be written: \n");
        try {
            UnsynchronizedByteArrayOutputStream baos = new UnsynchronizedByteArrayOutputStream();
            this.writeOut((OutputStream)baos);
            byte[] b = baos.toByteArray();
            out.append(HexDump.dump(b, 0L, 0));
        }
        catch (IOException e) {
            LOG.atError().withThrowable(e).log("can't dump TextPropCollection");
        }
        return out.toString();
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        LinkedHashMap<String, Supplier<Object>> m = new LinkedHashMap<String, Supplier<Object>>();
        m.put("charactersCovered", this::getCharactersCovered);
        m.put("indentLevel", this::getIndentLevel);
        this.textProps.forEach((s, t) -> m.put((String)s, () -> t));
        m.put("maskSpecial", this::getSpecialMask);
        m.put("textPropType", this::getTextPropType);
        return Collections.unmodifiableMap(m);
    }

    public static enum TextPropType {
        paragraph,
        character;

    }
}

