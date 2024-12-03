/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene42;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.codecs.FieldInfosReader;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.IOUtils;

final class Lucene42FieldInfosReader
extends FieldInfosReader {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public FieldInfos read(Directory directory, String segmentName, IOContext iocontext) throws IOException {
        FieldInfos fieldInfos;
        block7: {
            IndexInput input;
            block6: {
                String fileName = IndexFileNames.segmentFileName(segmentName, "", "fnm");
                input = directory.openInput(fileName, iocontext);
                boolean success = false;
                try {
                    CodecUtil.checkHeader(input, "Lucene42FieldInfos", 0, 0);
                    int size = input.readVInt();
                    FieldInfo[] infos = new FieldInfo[size];
                    for (int i = 0; i < size; ++i) {
                        boolean storePayloads;
                        String name = input.readString();
                        int fieldNumber = input.readVInt();
                        byte bits = input.readByte();
                        boolean isIndexed = (bits & 1) != 0;
                        boolean storeTermVector = (bits & 2) != 0;
                        boolean omitNorms = (bits & 0x10) != 0;
                        boolean bl = storePayloads = (bits & 0x20) != 0;
                        FieldInfo.IndexOptions indexOptions = !isIndexed ? null : ((bits & 0x40) != 0 ? FieldInfo.IndexOptions.DOCS_ONLY : ((bits & 0xFFFFFF80) != 0 ? FieldInfo.IndexOptions.DOCS_AND_FREQS : ((bits & 4) != 0 ? FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS : FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS)));
                        byte val = input.readByte();
                        FieldInfo.DocValuesType docValuesType = Lucene42FieldInfosReader.getDocValuesType(input, (byte)(val & 0xF));
                        FieldInfo.DocValuesType normsType = Lucene42FieldInfosReader.getDocValuesType(input, (byte)(val >>> 4 & 0xF));
                        Map<String, String> attributes = input.readStringStringMap();
                        infos[i] = new FieldInfo(name, isIndexed, fieldNumber, storeTermVector, omitNorms, storePayloads, indexOptions, docValuesType, normsType, Collections.unmodifiableMap(attributes));
                    }
                    if (input.getFilePointer() != input.length()) {
                        throw new CorruptIndexException("did not read all bytes from file \"" + fileName + "\": read " + input.getFilePointer() + " vs size " + input.length() + " (resource: " + input + ")");
                    }
                    FieldInfos fieldInfos2 = new FieldInfos(infos);
                    success = true;
                    fieldInfos = fieldInfos2;
                    if (!success) break block6;
                }
                catch (Throwable throwable) {
                    if (success) {
                        input.close();
                    } else {
                        IOUtils.closeWhileHandlingException(input);
                    }
                    throw throwable;
                }
                input.close();
                break block7;
            }
            IOUtils.closeWhileHandlingException(input);
        }
        return fieldInfos;
    }

    private static FieldInfo.DocValuesType getDocValuesType(IndexInput input, byte b) throws IOException {
        if (b == 0) {
            return null;
        }
        if (b == 1) {
            return FieldInfo.DocValuesType.NUMERIC;
        }
        if (b == 2) {
            return FieldInfo.DocValuesType.BINARY;
        }
        if (b == 3) {
            return FieldInfo.DocValuesType.SORTED;
        }
        if (b == 4) {
            return FieldInfo.DocValuesType.SORTED_SET;
        }
        throw new CorruptIndexException("invalid docvalues byte: " + b + " (resource=" + input + ")");
    }
}

