/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hslf.exceptions.HSLFException;
import org.apache.poi.hslf.record.PositionDependentRecordAtom;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.util.LittleEndian;

public final class UserEditAtom
extends PositionDependentRecordAtom {
    public static final int LAST_VIEW_NONE = 0;
    public static final int LAST_VIEW_SLIDE_VIEW = 1;
    public static final int LAST_VIEW_OUTLINE_VIEW = 2;
    public static final int LAST_VIEW_NOTES = 3;
    private byte[] _header;
    private static final long _type = RecordTypes.UserEditAtom.typeID;
    private short unused;
    private int lastViewedSlideID;
    private int pptVersion;
    private int lastUserEditAtomOffset;
    private int persistPointersOffset;
    private int docPersistRef;
    private int maxPersistWritten;
    private short lastViewType;
    private int encryptSessionPersistIdRef = -1;

    public int getLastViewedSlideID() {
        return this.lastViewedSlideID;
    }

    public short getLastViewType() {
        return this.lastViewType;
    }

    public int getLastUserEditAtomOffset() {
        return this.lastUserEditAtomOffset;
    }

    public int getPersistPointersOffset() {
        return this.persistPointersOffset;
    }

    public int getDocPersistRef() {
        return this.docPersistRef;
    }

    public int getMaxPersistWritten() {
        return this.maxPersistWritten;
    }

    public int getEncryptSessionPersistIdRef() {
        return this.encryptSessionPersistIdRef;
    }

    public void setLastUserEditAtomOffset(int offset) {
        this.lastUserEditAtomOffset = offset;
    }

    public void setPersistPointersOffset(int offset) {
        this.persistPointersOffset = offset;
    }

    public void setLastViewType(short type) {
        this.lastViewType = type;
    }

    public void setMaxPersistWritten(int max) {
        this.maxPersistWritten = max;
    }

    public void setEncryptSessionPersistIdRef(int id) {
        this.encryptSessionPersistIdRef = id;
        LittleEndian.putInt(this._header, 4, id == -1 ? 28 : 32);
    }

    protected UserEditAtom(byte[] source, int start, int len) {
        if (len < 34) {
            len = 34;
        }
        int offset = start;
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this.lastViewedSlideID = LittleEndian.getInt(source, offset += 8);
        this.pptVersion = LittleEndian.getInt(source, offset += 4);
        this.lastUserEditAtomOffset = LittleEndian.getInt(source, offset += 4);
        this.persistPointersOffset = LittleEndian.getInt(source, offset += 4);
        this.docPersistRef = LittleEndian.getInt(source, offset += 4);
        this.maxPersistWritten = LittleEndian.getInt(source, offset += 4);
        this.lastViewType = LittleEndian.getShort(source, offset += 4);
        this.unused = LittleEndian.getShort(source, offset += 2);
        if ((offset += 2) - start < len) {
            this.encryptSessionPersistIdRef = LittleEndian.getInt(source, offset);
            offset += 4;
        }
        if (offset - start != len) {
            throw new HSLFException("Having invalid data in UserEditAtom: len: " + len + ", offset: " + offset + ", start: " + start);
        }
    }

    @Override
    public long getRecordType() {
        return _type;
    }

    @Override
    public void updateOtherRecordReferences(Map<Integer, Integer> oldToNewReferencesLookup) {
        Integer newLocation;
        if (this.lastUserEditAtomOffset != 0) {
            newLocation = oldToNewReferencesLookup.get(this.lastUserEditAtomOffset);
            if (newLocation == null) {
                throw new HSLFException("Couldn't find the new location of the UserEditAtom that used to be at " + this.lastUserEditAtomOffset);
            }
            this.lastUserEditAtomOffset = newLocation;
        }
        if ((newLocation = oldToNewReferencesLookup.get(this.persistPointersOffset)) == null) {
            throw new HSLFException("Couldn't find the new location of the PersistPtr that used to be at " + this.persistPointersOffset);
        }
        this.persistPointersOffset = newLocation;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this._header);
        UserEditAtom.writeLittleEndian(this.lastViewedSlideID, out);
        UserEditAtom.writeLittleEndian(this.pptVersion, out);
        UserEditAtom.writeLittleEndian(this.lastUserEditAtomOffset, out);
        UserEditAtom.writeLittleEndian(this.persistPointersOffset, out);
        UserEditAtom.writeLittleEndian(this.docPersistRef, out);
        UserEditAtom.writeLittleEndian(this.maxPersistWritten, out);
        UserEditAtom.writeLittleEndian(this.lastViewType, out);
        UserEditAtom.writeLittleEndian(this.unused, out);
        if (this.encryptSessionPersistIdRef != -1) {
            UserEditAtom.writeLittleEndian(this.encryptSessionPersistIdRef, out);
        }
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        LinkedHashMap<String, Supplier<Object>> m = new LinkedHashMap<String, Supplier<Object>>();
        m.put("lastViewedSlideID", this::getLastViewedSlideID);
        m.put("pptVersion", () -> this.pptVersion);
        m.put("lastUserEditAtomOffset", this::getLastUserEditAtomOffset);
        m.put("persistPointersOffset", this::getPersistPointersOffset);
        m.put("docPersistRef", this::getDocPersistRef);
        m.put("maxPersistWritten", this::getMaxPersistWritten);
        m.put("lastViewType", this::getLastViewType);
        m.put("encryptSessionPersistIdRef", this::getEncryptSessionPersistIdRef);
        return Collections.unmodifiableMap(m);
    }
}

