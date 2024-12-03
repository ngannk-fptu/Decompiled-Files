/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene40;

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

@Deprecated
class Lucene40FieldInfosReader
extends FieldInfosReader {
    static final String LEGACY_DV_TYPE_KEY = Lucene40FieldInfosReader.class.getSimpleName() + ".dvtype";
    static final String LEGACY_NORM_TYPE_KEY = Lucene40FieldInfosReader.class.getSimpleName() + ".normtype";

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public FieldInfos read(Directory directory, String segmentName, IOContext iocontext) throws IOException {
        FieldInfos fieldInfos;
        block11: {
            IndexInput input;
            block10: {
                String fileName = IndexFileNames.segmentFileName(segmentName, "", "fnm");
                input = directory.openInput(fileName, iocontext);
                boolean success = false;
                try {
                    CodecUtil.checkHeader(input, "Lucene40FieldInfos", 0, 0);
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
                        if (isIndexed && indexOptions.compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) < 0) {
                            storePayloads = false;
                        }
                        byte val = input.readByte();
                        LegacyDocValuesType oldValuesType = Lucene40FieldInfosReader.getDocValuesType((byte)(val & 0xF));
                        LegacyDocValuesType oldNormsType = Lucene40FieldInfosReader.getDocValuesType((byte)(val >>> 4 & 0xF));
                        Map<String, String> attributes = input.readStringStringMap();
                        if (oldValuesType.mapping != null) {
                            attributes.put(LEGACY_DV_TYPE_KEY, oldValuesType.name());
                        }
                        if (oldNormsType.mapping != null) {
                            if (oldNormsType.mapping != FieldInfo.DocValuesType.NUMERIC) {
                                throw new CorruptIndexException("invalid norm type: " + (Object)((Object)oldNormsType) + " (resource=" + input + ")");
                            }
                            attributes.put(LEGACY_NORM_TYPE_KEY, oldNormsType.name());
                        }
                        infos[i] = new FieldInfo(name, isIndexed, fieldNumber, storeTermVector, omitNorms, storePayloads, indexOptions, oldValuesType.mapping, oldNormsType.mapping, Collections.unmodifiableMap(attributes));
                    }
                    if (input.getFilePointer() != input.length()) {
                        throw new CorruptIndexException("did not read all bytes from file \"" + fileName + "\": read " + input.getFilePointer() + " vs size " + input.length() + " (resource: " + input + ")");
                    }
                    FieldInfos fieldInfos2 = new FieldInfos(infos);
                    success = true;
                    fieldInfos = fieldInfos2;
                    if (!success) break block10;
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
                break block11;
            }
            IOUtils.closeWhileHandlingException(input);
        }
        return fieldInfos;
    }

    private static LegacyDocValuesType getDocValuesType(byte b) {
        return LegacyDocValuesType.values()[b];
    }

    static enum LegacyDocValuesType {
        NONE(null),
        VAR_INTS(FieldInfo.DocValuesType.NUMERIC),
        FLOAT_32(FieldInfo.DocValuesType.NUMERIC),
        FLOAT_64(FieldInfo.DocValuesType.NUMERIC),
        BYTES_FIXED_STRAIGHT(FieldInfo.DocValuesType.BINARY),
        BYTES_FIXED_DEREF(FieldInfo.DocValuesType.BINARY),
        BYTES_VAR_STRAIGHT(FieldInfo.DocValuesType.BINARY),
        BYTES_VAR_DEREF(FieldInfo.DocValuesType.BINARY),
        FIXED_INTS_16(FieldInfo.DocValuesType.NUMERIC),
        FIXED_INTS_32(FieldInfo.DocValuesType.NUMERIC),
        FIXED_INTS_64(FieldInfo.DocValuesType.NUMERIC),
        FIXED_INTS_8(FieldInfo.DocValuesType.NUMERIC),
        BYTES_FIXED_SORTED(FieldInfo.DocValuesType.SORTED),
        BYTES_VAR_SORTED(FieldInfo.DocValuesType.SORTED);

        final FieldInfo.DocValuesType mapping;

        private LegacyDocValuesType(FieldInfo.DocValuesType mapping) {
            this.mapping = mapping;
        }
    }
}

