/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.document.Document;
import com.atlassian.lucene36.document.Fieldable;
import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.store.Directory;
import com.atlassian.lucene36.store.IndexInput;
import com.atlassian.lucene36.store.IndexOutput;
import com.atlassian.lucene36.util.StringHelper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class FieldInfos
implements Iterable<FieldInfo> {
    public static final int FORMAT_PRE = -1;
    public static final int FORMAT_START = -2;
    public static final int FORMAT_OMIT_POSITIONS = -3;
    static final int CURRENT_FORMAT = -3;
    static final byte IS_INDEXED = 1;
    static final byte STORE_TERMVECTOR = 2;
    static final byte OMIT_NORMS = 16;
    static final byte STORE_PAYLOADS = 32;
    static final byte OMIT_TERM_FREQ_AND_POSITIONS = 64;
    static final byte OMIT_POSITIONS = -128;
    private final ArrayList<FieldInfo> byNumber;
    private final HashMap<String, FieldInfo> byName;
    private int format;

    public FieldInfos() {
        this.byNumber = new ArrayList();
        this.byName = new HashMap();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public FieldInfos(Directory d, String name) throws IOException {
        block8: {
            this.byNumber = new ArrayList();
            this.byName = new HashMap();
            IndexInput input = d.openInput(name);
            try {
                try {
                    this.read(input, name);
                }
                catch (IOException ioe) {
                    if (this.format == -1) {
                        input.seek(0L);
                        input.setModifiedUTF8StringsMode();
                        this.byNumber.clear();
                        this.byName.clear();
                        try {
                            this.read(input, name);
                            break block8;
                        }
                        catch (Throwable t) {
                            throw ioe;
                        }
                    }
                    throw ioe;
                }
            }
            finally {
                input.close();
            }
        }
    }

    public void add(FieldInfos other) {
        for (FieldInfo fieldInfo : other) {
            this.add(fieldInfo);
        }
    }

    public synchronized Object clone() {
        FieldInfos fis = new FieldInfos();
        int numField = this.byNumber.size();
        for (int i = 0; i < numField; ++i) {
            FieldInfo fi = (FieldInfo)this.byNumber.get(i).clone();
            fis.byNumber.add(fi);
            fis.byName.put(fi.name, fi);
        }
        return fis;
    }

    public synchronized void add(Document doc) {
        List<Fieldable> fields = doc.getFields();
        for (Fieldable field : fields) {
            this.add(field.name(), field.isIndexed(), field.isTermVectorStored(), field.getOmitNorms(), false, field.getIndexOptions());
        }
    }

    public boolean hasProx() {
        int numFields = this.byNumber.size();
        for (int i = 0; i < numFields; ++i) {
            FieldInfo fi = this.fieldInfo(i);
            if (!fi.isIndexed || fi.indexOptions != FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) continue;
            return true;
        }
        return false;
    }

    public synchronized void add(String name, boolean isIndexed) {
        this.add(name, isIndexed, false, false);
    }

    public synchronized void add(String name, boolean isIndexed, boolean storeTermVector) {
        this.add(name, isIndexed, storeTermVector, false);
    }

    public synchronized void add(String name, boolean isIndexed, boolean storeTermVector, boolean omitNorms) {
        this.add(name, isIndexed, storeTermVector, omitNorms, false, FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
    }

    public synchronized FieldInfo add(String name, boolean isIndexed, boolean storeTermVector, boolean omitNorms, boolean storePayloads, FieldInfo.IndexOptions indexOptions) {
        FieldInfo fi = this.fieldInfo(name);
        if (fi == null) {
            return this.addInternal(name, isIndexed, storeTermVector, omitNorms, storePayloads, indexOptions);
        }
        fi.update(isIndexed, storeTermVector, omitNorms, storePayloads, indexOptions);
        assert (fi.indexOptions == FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS || !fi.storePayloads);
        return fi;
    }

    public synchronized FieldInfo add(FieldInfo fi) {
        return this.add(fi.name, fi.isIndexed, fi.storeTermVector, fi.omitNorms, fi.storePayloads, fi.indexOptions);
    }

    private FieldInfo addInternal(String name, boolean isIndexed, boolean storeTermVector, boolean omitNorms, boolean storePayloads, FieldInfo.IndexOptions indexOptions) {
        name = StringHelper.intern(name);
        FieldInfo fi = new FieldInfo(name, isIndexed, this.byNumber.size(), storeTermVector, omitNorms, storePayloads, indexOptions);
        this.byNumber.add(fi);
        this.byName.put(name, fi);
        return fi;
    }

    public int fieldNumber(String fieldName) {
        FieldInfo fi = this.fieldInfo(fieldName);
        return fi != null ? fi.number : -1;
    }

    public FieldInfo fieldInfo(String fieldName) {
        return this.byName.get(fieldName);
    }

    public String fieldName(int fieldNumber) {
        FieldInfo fi = this.fieldInfo(fieldNumber);
        return fi != null ? fi.name : "";
    }

    public FieldInfo fieldInfo(int fieldNumber) {
        return fieldNumber >= 0 ? this.byNumber.get(fieldNumber) : null;
    }

    @Override
    public Iterator<FieldInfo> iterator() {
        return this.byNumber.iterator();
    }

    public int size() {
        return this.byNumber.size();
    }

    public boolean hasVectors() {
        boolean hasVectors = false;
        for (int i = 0; i < this.size(); ++i) {
            if (!this.fieldInfo((int)i).storeTermVector) continue;
            hasVectors = true;
            break;
        }
        return hasVectors;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void write(Directory d, String name) throws IOException {
        IndexOutput output = d.createOutput(name);
        try {
            this.write(output);
        }
        finally {
            output.close();
        }
    }

    public void write(IndexOutput output) throws IOException {
        output.writeVInt(-3);
        output.writeVInt(this.size());
        for (int i = 0; i < this.size(); ++i) {
            FieldInfo fi = this.fieldInfo(i);
            assert (fi.indexOptions == FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS || !fi.storePayloads);
            byte bits = 0;
            if (fi.isIndexed) {
                bits = (byte)(bits | 1);
            }
            if (fi.storeTermVector) {
                bits = (byte)(bits | 2);
            }
            if (fi.omitNorms) {
                bits = (byte)(bits | 0x10);
            }
            if (fi.storePayloads) {
                bits = (byte)(bits | 0x20);
            }
            if (fi.indexOptions == FieldInfo.IndexOptions.DOCS_ONLY) {
                bits = (byte)(bits | 0x40);
            } else if (fi.indexOptions == FieldInfo.IndexOptions.DOCS_AND_FREQS) {
                bits = (byte)(bits | 0xFFFFFF80);
            }
            output.writeString(fi.name);
            output.writeByte(bits);
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void read(IndexInput input, String fileName) throws IOException {
        int firstInt = input.readVInt();
        this.format = firstInt < 0 ? firstInt : -1;
        if (this.format != -1 && this.format != -2 && this.format != -3) {
            throw new CorruptIndexException("unrecognized format " + this.format + " in file \"" + fileName + "\"");
        }
        int size = this.format == -1 ? firstInt : input.readVInt();
        for (int i = 0; i < size; ++i) {
            FieldInfo.IndexOptions indexOptions;
            boolean storePayloads;
            String name = StringHelper.intern(input.readString());
            byte bits = input.readByte();
            boolean isIndexed = (bits & 1) != 0;
            boolean storeTermVector = (bits & 2) != 0;
            boolean omitNorms = (bits & 0x10) != 0;
            boolean bl = storePayloads = (bits & 0x20) != 0;
            if ((bits & 0x40) != 0) {
                indexOptions = FieldInfo.IndexOptions.DOCS_ONLY;
            } else if ((bits & 0xFFFFFF80) != 0) {
                if (this.format > -3) throw new CorruptIndexException("Corrupt fieldinfos, OMIT_POSITIONS set but format=" + this.format + " (resource: " + input + ")");
                indexOptions = FieldInfo.IndexOptions.DOCS_AND_FREQS;
            } else {
                indexOptions = FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS;
            }
            if (indexOptions != FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) {
                storePayloads = false;
            }
            this.addInternal(name, isIndexed, storeTermVector, omitNorms, storePayloads, indexOptions);
        }
        if (input.getFilePointer() == input.length()) return;
        throw new CorruptIndexException("did not read all bytes from file \"" + fileName + "\": read " + input.getFilePointer() + " vs size " + input.length() + " (resource: " + input + ")");
    }
}

