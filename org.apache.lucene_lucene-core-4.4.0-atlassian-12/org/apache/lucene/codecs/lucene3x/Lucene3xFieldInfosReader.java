/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene3x;

import java.io.IOException;
import java.util.Collections;
import org.apache.lucene.codecs.FieldInfosReader;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.IndexFormatTooNewException;
import org.apache.lucene.index.IndexFormatTooOldException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.IOUtils;

@Deprecated
class Lucene3xFieldInfosReader
extends FieldInfosReader {
    static final String FIELD_INFOS_EXTENSION = "fnm";
    static final int FORMAT_START = -2;
    static final int FORMAT_OMIT_POSITIONS = -3;
    static final int FORMAT_MINIMUM = -2;
    static final int FORMAT_CURRENT = -3;
    static final byte IS_INDEXED = 1;
    static final byte STORE_TERMVECTOR = 2;
    static final byte OMIT_NORMS = 16;
    static final byte STORE_PAYLOADS = 32;
    static final byte OMIT_TERM_FREQ_AND_POSITIONS = 64;
    static final byte OMIT_POSITIONS = -128;

    Lucene3xFieldInfosReader() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public FieldInfos read(Directory directory, String segmentName, IOContext iocontext) throws IOException {
        FieldInfos fieldInfos;
        IndexInput input;
        block15: {
            String fileName = IndexFileNames.segmentFileName(segmentName, "", FIELD_INFOS_EXTENSION);
            input = directory.openInput(fileName, iocontext);
            boolean success = false;
            try {
                int format = input.readVInt();
                if (format > -2) {
                    throw new IndexFormatTooOldException(input, format, -2, -3);
                }
                if (format < -3) {
                    throw new IndexFormatTooNewException(input, format, -2, -3);
                }
                int size = input.readVInt();
                FieldInfo[] infos = new FieldInfo[size];
                for (int i = 0; i < size; ++i) {
                    FieldInfo.IndexOptions indexOptions;
                    boolean storePayloads;
                    String name = input.readString();
                    int fieldNumber = i;
                    byte bits = input.readByte();
                    boolean isIndexed = (bits & 1) != 0;
                    boolean storeTermVector = (bits & 2) != 0;
                    boolean omitNorms = (bits & 0x10) != 0;
                    boolean bl = storePayloads = (bits & 0x20) != 0;
                    if (!isIndexed) {
                        indexOptions = null;
                    } else if ((bits & 0x40) != 0) {
                        indexOptions = FieldInfo.IndexOptions.DOCS_ONLY;
                    } else if ((bits & 0xFFFFFF80) != 0) {
                        if (format > -3) throw new CorruptIndexException("Corrupt fieldinfos, OMIT_POSITIONS set but format=" + format + " (resource: " + input + ")");
                        indexOptions = FieldInfo.IndexOptions.DOCS_AND_FREQS;
                    } else {
                        indexOptions = FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS;
                    }
                    if (indexOptions != FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) {
                        storePayloads = false;
                    }
                    infos[i] = new FieldInfo(name, isIndexed, fieldNumber, storeTermVector, omitNorms, storePayloads, indexOptions, null, isIndexed && !omitNorms ? FieldInfo.DocValuesType.NUMERIC : null, Collections.emptyMap());
                }
                if (input.getFilePointer() != input.length()) {
                    throw new CorruptIndexException("did not read all bytes from file \"" + fileName + "\": read " + input.getFilePointer() + " vs size " + input.length() + " (resource: " + input + ")");
                }
                FieldInfos fieldInfos2 = new FieldInfos(infos);
                success = true;
                fieldInfos = fieldInfos2;
                if (!success) break block15;
            }
            catch (Throwable throwable) {
                if (success) {
                    input.close();
                    throw throwable;
                } else {
                    IOUtils.closeWhileHandlingException(input);
                }
                throw throwable;
            }
            input.close();
            return fieldInfos;
        }
        IOUtils.closeWhileHandlingException(input);
        return fieldInfos;
    }
}

