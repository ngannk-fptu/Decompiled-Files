/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.zip;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.zip.ZipException;
import org.apache.tools.zip.CentralDirectoryParsingZipExtraField;
import org.apache.tools.zip.ExtraFieldUtils;
import org.apache.tools.zip.GeneralPurposeBit;
import org.apache.tools.zip.UnparseableExtraFieldData;
import org.apache.tools.zip.ZipExtraField;
import org.apache.tools.zip.ZipShort;

public class ZipEntry
extends java.util.zip.ZipEntry
implements Cloneable {
    public static final int PLATFORM_UNIX = 3;
    public static final int PLATFORM_FAT = 0;
    public static final int CRC_UNKNOWN = -1;
    private static final int SHORT_MASK = 65535;
    private static final int SHORT_SHIFT = 16;
    private static final byte[] EMPTY = new byte[0];
    private int method = -1;
    private long size = -1L;
    private int internalAttributes = 0;
    private int platform = 0;
    private long externalAttributes = 0L;
    private ZipExtraField[] extraFields;
    private UnparseableExtraFieldData unparseableExtra = null;
    private String name = null;
    private byte[] rawName = null;
    private GeneralPurposeBit gpb = new GeneralPurposeBit();
    private static final ZipExtraField[] noExtraFields = new ZipExtraField[0];

    public ZipEntry(String name) {
        super(name);
        this.setName(name);
    }

    public ZipEntry(java.util.zip.ZipEntry entry) throws ZipException {
        super(entry);
        this.setName(entry.getName());
        byte[] extra = entry.getExtra();
        if (extra != null) {
            this.setExtraFields(ExtraFieldUtils.parse(extra, true, ExtraFieldUtils.UnparseableExtraField.READ));
        } else {
            this.setExtra();
        }
        this.setMethod(entry.getMethod());
        this.size = entry.getSize();
    }

    public ZipEntry(ZipEntry entry) throws ZipException {
        this((java.util.zip.ZipEntry)entry);
        this.setInternalAttributes(entry.getInternalAttributes());
        this.setExternalAttributes(entry.getExternalAttributes());
        this.setExtraFields(this.getAllExtraFieldsNoCopy());
        this.setPlatform(entry.getPlatform());
        GeneralPurposeBit other = entry.getGeneralPurposeBit();
        this.setGeneralPurposeBit(other == null ? null : (GeneralPurposeBit)other.clone());
    }

    protected ZipEntry() {
        this("");
    }

    public ZipEntry(File inputFile, String entryName) {
        this(inputFile.isDirectory() && !entryName.endsWith("/") ? entryName + "/" : entryName);
        if (inputFile.isFile()) {
            this.setSize(inputFile.length());
        }
        this.setTime(inputFile.lastModified());
    }

    @Override
    public Object clone() {
        ZipEntry e = (ZipEntry)super.clone();
        e.setInternalAttributes(this.getInternalAttributes());
        e.setExternalAttributes(this.getExternalAttributes());
        e.setExtraFields(this.getAllExtraFieldsNoCopy());
        return e;
    }

    @Override
    public int getMethod() {
        return this.method;
    }

    @Override
    public void setMethod(int method) {
        if (method < 0) {
            throw new IllegalArgumentException("ZIP compression method can not be negative: " + method);
        }
        this.method = method;
    }

    public int getInternalAttributes() {
        return this.internalAttributes;
    }

    public void setInternalAttributes(int value) {
        this.internalAttributes = value;
    }

    public long getExternalAttributes() {
        return this.externalAttributes;
    }

    public void setExternalAttributes(long value) {
        this.externalAttributes = value;
    }

    public void setUnixMode(int mode) {
        this.setExternalAttributes(mode << 16 | ((mode & 0x80) == 0 ? 1 : 0) | (this.isDirectory() ? 16 : 0));
        this.platform = 3;
    }

    public int getUnixMode() {
        return this.platform != 3 ? 0 : (int)(this.getExternalAttributes() >> 16 & 0xFFFFL);
    }

    public int getPlatform() {
        return this.platform;
    }

    protected void setPlatform(int platform) {
        this.platform = platform;
    }

    public void setExtraFields(ZipExtraField[] fields) {
        ArrayList<ZipExtraField> newFields = new ArrayList<ZipExtraField>();
        for (ZipExtraField field : fields) {
            if (field instanceof UnparseableExtraFieldData) {
                this.unparseableExtra = (UnparseableExtraFieldData)field;
                continue;
            }
            newFields.add(field);
        }
        this.extraFields = newFields.toArray(new ZipExtraField[0]);
        this.setExtra();
    }

    public ZipExtraField[] getExtraFields() {
        return this.getParseableExtraFields();
    }

    public ZipExtraField[] getExtraFields(boolean includeUnparseable) {
        return includeUnparseable ? this.getAllExtraFields() : this.getParseableExtraFields();
    }

    private ZipExtraField[] getParseableExtraFieldsNoCopy() {
        if (this.extraFields == null) {
            return noExtraFields;
        }
        return this.extraFields;
    }

    private ZipExtraField[] getParseableExtraFields() {
        ZipExtraField[] parseableExtraFields = this.getParseableExtraFieldsNoCopy();
        return parseableExtraFields == this.extraFields ? this.copyOf(parseableExtraFields) : parseableExtraFields;
    }

    private ZipExtraField[] copyOf(ZipExtraField[] src) {
        return this.copyOf(src, src.length);
    }

    private ZipExtraField[] copyOf(ZipExtraField[] src, int length) {
        ZipExtraField[] cpy = new ZipExtraField[length];
        System.arraycopy(src, 0, cpy, 0, Math.min(src.length, length));
        return cpy;
    }

    private ZipExtraField[] getMergedFields() {
        ZipExtraField[] zipExtraFields = this.copyOf(this.extraFields, this.extraFields.length + 1);
        zipExtraFields[this.extraFields.length] = this.unparseableExtra;
        return zipExtraFields;
    }

    private ZipExtraField[] getUnparseableOnly() {
        ZipExtraField[] zipExtraFieldArray;
        if (this.unparseableExtra == null) {
            zipExtraFieldArray = noExtraFields;
        } else {
            ZipExtraField[] zipExtraFieldArray2 = new ZipExtraField[1];
            zipExtraFieldArray = zipExtraFieldArray2;
            zipExtraFieldArray2[0] = this.unparseableExtra;
        }
        return zipExtraFieldArray;
    }

    private ZipExtraField[] getAllExtraFields() {
        ZipExtraField[] allExtraFieldsNoCopy = this.getAllExtraFieldsNoCopy();
        return allExtraFieldsNoCopy == this.extraFields ? this.copyOf(allExtraFieldsNoCopy) : allExtraFieldsNoCopy;
    }

    private ZipExtraField[] getAllExtraFieldsNoCopy() {
        if (this.extraFields == null) {
            return this.getUnparseableOnly();
        }
        return this.unparseableExtra != null ? this.getMergedFields() : this.extraFields;
    }

    public void addExtraField(ZipExtraField ze) {
        if (ze instanceof UnparseableExtraFieldData) {
            this.unparseableExtra = (UnparseableExtraFieldData)ze;
        } else if (this.extraFields == null) {
            this.extraFields = new ZipExtraField[]{ze};
        } else {
            if (this.getExtraField(ze.getHeaderId()) != null) {
                this.removeExtraField(ze.getHeaderId());
            }
            ZipExtraField[] zipExtraFields = this.copyOf(this.extraFields, this.extraFields.length + 1);
            zipExtraFields[this.extraFields.length] = ze;
            this.extraFields = zipExtraFields;
        }
        this.setExtra();
    }

    public void addAsFirstExtraField(ZipExtraField ze) {
        if (ze instanceof UnparseableExtraFieldData) {
            this.unparseableExtra = (UnparseableExtraFieldData)ze;
        } else {
            if (this.getExtraField(ze.getHeaderId()) != null) {
                this.removeExtraField(ze.getHeaderId());
            }
            ZipExtraField[] copy = this.extraFields;
            int newLen = this.extraFields != null ? this.extraFields.length + 1 : 1;
            this.extraFields = new ZipExtraField[newLen];
            this.extraFields[0] = ze;
            if (copy != null) {
                System.arraycopy(copy, 0, this.extraFields, 1, this.extraFields.length - 1);
            }
        }
        this.setExtra();
    }

    public void removeExtraField(ZipShort type) {
        if (this.extraFields == null) {
            throw new NoSuchElementException();
        }
        ArrayList<ZipExtraField> newResult = new ArrayList<ZipExtraField>();
        for (ZipExtraField extraField : this.extraFields) {
            if (type.equals(extraField.getHeaderId())) continue;
            newResult.add(extraField);
        }
        if (this.extraFields.length == newResult.size()) {
            throw new NoSuchElementException();
        }
        this.extraFields = newResult.toArray(new ZipExtraField[0]);
        this.setExtra();
    }

    public void removeUnparseableExtraFieldData() {
        if (this.unparseableExtra == null) {
            throw new NoSuchElementException();
        }
        this.unparseableExtra = null;
        this.setExtra();
    }

    public ZipExtraField getExtraField(ZipShort type) {
        if (this.extraFields != null) {
            for (ZipExtraField extraField : this.extraFields) {
                if (!type.equals(extraField.getHeaderId())) continue;
                return extraField;
            }
        }
        return null;
    }

    public UnparseableExtraFieldData getUnparseableExtraFieldData() {
        return this.unparseableExtra;
    }

    @Override
    public void setExtra(byte[] extra) throws RuntimeException {
        try {
            ZipExtraField[] local = ExtraFieldUtils.parse(extra, true, ExtraFieldUtils.UnparseableExtraField.READ);
            this.mergeExtraFields(local, true);
        }
        catch (ZipException e) {
            throw new RuntimeException("Error parsing extra fields for entry: " + this.getName() + " - " + e.getMessage(), e);
        }
    }

    protected void setExtra() {
        super.setExtra(ExtraFieldUtils.mergeLocalFileDataData(this.getExtraFields(true)));
    }

    public void setCentralDirectoryExtra(byte[] b) {
        try {
            ZipExtraField[] central = ExtraFieldUtils.parse(b, false, ExtraFieldUtils.UnparseableExtraField.READ);
            this.mergeExtraFields(central, false);
        }
        catch (ZipException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public byte[] getLocalFileDataExtra() {
        byte[] extra = this.getExtra();
        return extra != null ? extra : EMPTY;
    }

    public byte[] getCentralDirectoryExtra() {
        return ExtraFieldUtils.mergeCentralDirectoryData(this.getExtraFields(true));
    }

    @Deprecated
    public void setComprSize(long size) {
        this.setCompressedSize(size);
    }

    @Override
    public String getName() {
        return this.name == null ? super.getName() : this.name;
    }

    @Override
    public boolean isDirectory() {
        return this.getName().endsWith("/");
    }

    protected void setName(String name) {
        if (name != null && this.getPlatform() == 0 && !name.contains("/")) {
            name = name.replace('\\', '/');
        }
        this.name = name;
    }

    @Override
    public long getSize() {
        return this.size;
    }

    @Override
    public void setSize(long size) {
        if (size < 0L) {
            throw new IllegalArgumentException("invalid entry size");
        }
        this.size = size;
    }

    protected void setName(String name, byte[] rawName) {
        this.setName(name);
        this.rawName = rawName;
    }

    public byte[] getRawName() {
        if (this.rawName != null) {
            byte[] b = new byte[this.rawName.length];
            System.arraycopy(this.rawName, 0, b, 0, this.rawName.length);
            return b;
        }
        return null;
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }

    public GeneralPurposeBit getGeneralPurposeBit() {
        return this.gpb;
    }

    public void setGeneralPurposeBit(GeneralPurposeBit b) {
        this.gpb = b;
    }

    private void mergeExtraFields(ZipExtraField[] f, boolean local) throws ZipException {
        if (this.extraFields == null) {
            this.setExtraFields(f);
        } else {
            for (ZipExtraField element : f) {
                byte[] b;
                ZipExtraField existing = element instanceof UnparseableExtraFieldData ? this.unparseableExtra : this.getExtraField(element.getHeaderId());
                if (existing == null) {
                    this.addExtraField(element);
                    continue;
                }
                if (local || !(existing instanceof CentralDirectoryParsingZipExtraField)) {
                    b = element.getLocalFileDataData();
                    existing.parseFromLocalFileData(b, 0, b.length);
                    continue;
                }
                b = element.getCentralDirectoryData();
                ((CentralDirectoryParsingZipExtraField)existing).parseFromCentralDirectoryData(b, 0, b.length);
            }
            this.setExtra();
        }
    }

    public Date getLastModifiedDate() {
        return new Date(this.getTime());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        ZipEntry other = (ZipEntry)obj;
        String myName = this.getName();
        String otherName = other.getName();
        if (myName == null ? otherName != null : !myName.equals(otherName)) {
            return false;
        }
        String myComment = this.getComment();
        String otherComment = other.getComment();
        if (myComment == null) {
            myComment = "";
        }
        if (otherComment == null) {
            otherComment = "";
        }
        return this.getTime() == other.getTime() && myComment.equals(otherComment) && this.getInternalAttributes() == other.getInternalAttributes() && this.getPlatform() == other.getPlatform() && this.getExternalAttributes() == other.getExternalAttributes() && this.getMethod() == other.getMethod() && this.getSize() == other.getSize() && this.getCrc() == other.getCrc() && this.getCompressedSize() == other.getCompressedSize() && Arrays.equals(this.getCentralDirectoryExtra(), other.getCentralDirectoryExtra()) && Arrays.equals(this.getLocalFileDataExtra(), other.getLocalFileDataExtra()) && this.gpb.equals(other.gpb);
    }
}

