/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hslf.record.RecordAtom;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LittleEndianByteArrayInputStream;

public class TextSpecInfoRun
implements GenericRecord {
    private static final BitField spellFld = BitFieldFactory.getInstance(1);
    private static final BitField langFld = BitFieldFactory.getInstance(2);
    private static final BitField altLangFld = BitFieldFactory.getInstance(4);
    private static final BitField pp10extFld = BitFieldFactory.getInstance(32);
    private static final BitField bidiFld = BitFieldFactory.getInstance(64);
    private static final BitField smartTagFld = BitFieldFactory.getInstance(512);
    private static final BitField pp10runidFld = BitFieldFactory.getInstance(15);
    private static final BitField grammarErrorFld = BitFieldFactory.getInstance(Integer.MIN_VALUE);
    private static final int[] FLAGS_MASKS = new int[]{1, 2, 4, 32, 64, 512};
    private static final String[] FLAGS_NAMES = new String[]{"SPELL", "LANG", "ALT_LANG", "PP10_EXT", "BIDI", "SMART_TAG"};
    private int length;
    private int mask;
    private short spellInfo = (short)-1;
    private short langId = (short)-1;
    private short altLangId = (short)-1;
    private short bidi = (short)-1;
    private int pp10extMask = -1;
    private byte[] smartTagsBytes;

    public TextSpecInfoRun(int len) {
        this.setLength(len);
        this.setLangId((short)0);
    }

    public TextSpecInfoRun(LittleEndianByteArrayInputStream source) {
        this.length = source.readInt();
        this.mask = source.readInt();
        if (spellFld.isSet(this.mask)) {
            this.spellInfo = source.readShort();
        }
        if (langFld.isSet(this.mask)) {
            this.langId = source.readShort();
        }
        if (altLangFld.isSet(this.mask)) {
            this.altLangId = source.readShort();
        }
        if (bidiFld.isSet(this.mask)) {
            this.bidi = source.readShort();
        }
        if (pp10extFld.isSet(this.mask)) {
            this.pp10extMask = source.readInt();
        }
        if (smartTagFld.isSet(this.mask)) {
            int count = source.readInt();
            this.smartTagsBytes = IOUtils.safelyAllocate(4L + (long)count * 4L, RecordAtom.getMaxRecordLength());
            LittleEndian.putInt(this.smartTagsBytes, 0, count);
            source.readFully(this.smartTagsBytes, 4, count * 4);
        }
    }

    public void writeOut(OutputStream out) throws IOException {
        byte[] buf = new byte[4];
        LittleEndian.putInt(buf, 0, this.length);
        out.write(buf);
        LittleEndian.putInt(buf, 0, this.mask);
        out.write(buf);
        Object[] flds = new Object[]{spellFld, this.spellInfo, "spell info", langFld, this.langId, "lang id", altLangFld, this.altLangId, "alt lang id", bidiFld, this.bidi, "bidi", pp10extFld, this.pp10extMask, "pp10 extension field", smartTagFld, this.smartTagsBytes, "smart tags"};
        for (int i = 0; i < flds.length - 1; i += 3) {
            boolean valid;
            BitField fld = (BitField)flds[i + 0];
            Object valO = flds[i + 1];
            if (!fld.isSet(this.mask)) continue;
            if (valO instanceof byte[]) {
                byte[] bufB = (byte[])valO;
                valid = bufB.length > 0;
                out.write(bufB);
            } else if (valO instanceof Integer) {
                int valI = (Integer)valO;
                valid = valI != -1;
                LittleEndian.putInt(buf, 0, valI);
                out.write(buf);
            } else if (valO instanceof Short) {
                short valS = (Short)valO;
                valid = valS != -1;
                LittleEndian.putShort(buf, 0, valS);
                out.write(buf, 0, 2);
            } else {
                valid = false;
            }
            if (valid) continue;
            Object fval = i + 2 < flds.length ? flds[i + 2] : null;
            throw new IOException(fval + " is activated, but its value is invalid");
        }
    }

    public SpellInfoEnum getSpellInfo() {
        if (this.spellInfo == -1) {
            return null;
        }
        for (SpellInfoEnum si : new SpellInfoEnum[]{SpellInfoEnum.clean, SpellInfoEnum.error, SpellInfoEnum.grammar}) {
            if (!si.bitField.isSet(this.spellInfo)) continue;
            return si;
        }
        return SpellInfoEnum.correct;
    }

    public void setSpellInfo(SpellInfoEnum spellInfo) {
        this.spellInfo = (short)(spellInfo == null ? -1 : (short)spellInfo.bitField.set(0));
        this.mask = spellFld.setBoolean(this.mask, spellInfo != null);
    }

    public short getLangId() {
        return this.langId;
    }

    public void setLangId(short langId) {
        this.langId = langId;
        this.mask = langFld.setBoolean(this.mask, langId != -1);
    }

    public short getAltLangId() {
        return this.altLangId;
    }

    public void setAltLangId(short altLangId) {
        this.altLangId = altLangId;
        this.mask = altLangFld.setBoolean(this.mask, altLangId != -1);
    }

    public int getLength() {
        return this.length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Boolean getBidi() {
        return this.bidi == -1 ? null : Boolean.valueOf(this.bidi != 0);
    }

    public void setBidi(Boolean bidi) {
        this.bidi = (short)(bidi == null ? -1 : (short)(bidi != false ? 1 : 0));
        this.mask = bidiFld.setBoolean(this.mask, bidi != null);
    }

    public byte[] getSmartTagsBytes() {
        return this.smartTagsBytes;
    }

    public void setSmartTagsBytes(byte[] smartTagsBytes) {
        this.smartTagsBytes = smartTagsBytes == null ? null : (byte[])smartTagsBytes.clone();
        this.mask = smartTagFld.setBoolean(this.mask, smartTagsBytes != null);
    }

    public int getPP10RunId() {
        return this.pp10extMask == -1 || !pp10extFld.isSet(this.mask) ? -1 : pp10runidFld.getValue(this.pp10extMask);
    }

    public void setPP10RunId(int pp10RunId) {
        this.pp10extMask = pp10RunId == -1 ? (this.getGrammarError() == null ? -1 : pp10runidFld.clear(this.pp10extMask)) : pp10runidFld.setValue(this.pp10extMask, pp10RunId);
        this.mask = pp10extFld.setBoolean(this.mask, this.pp10extMask != -1);
    }

    public Boolean getGrammarError() {
        return this.pp10extMask == -1 || !pp10extFld.isSet(this.mask) ? null : Boolean.valueOf(grammarErrorFld.isSet(this.pp10extMask));
    }

    public void getGrammarError(Boolean grammarError) {
        this.pp10extMask = grammarError == null ? (this.getPP10RunId() == -1 ? -1 : grammarErrorFld.clear(this.pp10extMask)) : grammarErrorFld.set(this.pp10extMask);
        this.mask = pp10extFld.setBoolean(this.mask, this.pp10extMask != -1);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        LinkedHashMap<String, Supplier<Object>> m = new LinkedHashMap<String, Supplier<Object>>();
        m.put("flags", GenericRecordUtil.getBitsAsString(() -> this.mask, FLAGS_MASKS, FLAGS_NAMES));
        m.put("spellInfo", this::getSpellInfo);
        m.put("langId", this::getLangId);
        m.put("altLangId", this::getAltLangId);
        m.put("bidi", this::getBidi);
        m.put("pp10RunId", this::getPP10RunId);
        m.put("grammarError", this::getGrammarError);
        m.put("smartTags", this::getSmartTagsBytes);
        return Collections.unmodifiableMap(m);
    }

    public static enum SpellInfoEnum {
        error(new BitField(1)),
        clean(new BitField(2)),
        grammar(new BitField(4)),
        correct(new BitField(0));

        final BitField bitField;

        private SpellInfoEnum(BitField bitField) {
            this.bitField = bitField;
        }
    }
}

