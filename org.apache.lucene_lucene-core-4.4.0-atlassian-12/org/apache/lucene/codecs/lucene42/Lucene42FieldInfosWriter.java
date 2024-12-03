/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene42;

import java.io.IOException;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.codecs.FieldInfosWriter;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.util.IOUtils;

final class Lucene42FieldInfosWriter
extends FieldInfosWriter {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void write(Directory directory, String segmentName, FieldInfos infos, IOContext context) throws IOException {
        block17: {
            IndexOutput output;
            block16: {
                String fileName = IndexFileNames.segmentFileName(segmentName, "", "fnm");
                output = directory.createOutput(fileName, context);
                boolean success = false;
                try {
                    CodecUtil.writeHeader(output, "Lucene42FieldInfos", 0);
                    output.writeVInt(infos.size());
                    for (FieldInfo fi : infos) {
                        FieldInfo.IndexOptions indexOptions = fi.getIndexOptions();
                        byte bits = 0;
                        if (fi.hasVectors()) {
                            bits = (byte)(bits | 2);
                        }
                        if (fi.omitsNorms()) {
                            bits = (byte)(bits | 0x10);
                        }
                        if (fi.hasPayloads()) {
                            bits = (byte)(bits | 0x20);
                        }
                        if (fi.isIndexed()) {
                            bits = (byte)(bits | 1);
                            assert (indexOptions.compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) >= 0 || !fi.hasPayloads());
                            if (indexOptions == FieldInfo.IndexOptions.DOCS_ONLY) {
                                bits = (byte)(bits | 0x40);
                            } else if (indexOptions == FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) {
                                bits = (byte)(bits | 4);
                            } else if (indexOptions == FieldInfo.IndexOptions.DOCS_AND_FREQS) {
                                bits = (byte)(bits | 0xFFFFFF80);
                            }
                        }
                        output.writeString(fi.name);
                        output.writeVInt(fi.number);
                        output.writeByte(bits);
                        byte dv = Lucene42FieldInfosWriter.docValuesByte(fi.getDocValuesType());
                        byte nrm = Lucene42FieldInfosWriter.docValuesByte(fi.getNormType());
                        assert ((dv & 0xFFFFFFF0) == 0 && (nrm & 0xFFFFFFF0) == 0);
                        byte val = (byte)(0xFF & (nrm << 4 | dv));
                        output.writeByte(val);
                        output.writeStringStringMap(fi.attributes());
                    }
                    success = true;
                    if (!success) break block16;
                }
                catch (Throwable throwable) {
                    if (success) {
                        output.close();
                    } else {
                        IOUtils.closeWhileHandlingException(output);
                    }
                    throw throwable;
                }
                output.close();
                break block17;
            }
            IOUtils.closeWhileHandlingException(output);
        }
    }

    private static byte docValuesByte(FieldInfo.DocValuesType type) {
        if (type == null) {
            return 0;
        }
        if (type == FieldInfo.DocValuesType.NUMERIC) {
            return 1;
        }
        if (type == FieldInfo.DocValuesType.BINARY) {
            return 2;
        }
        if (type == FieldInfo.DocValuesType.SORTED) {
            return 3;
        }
        if (type == FieldInfo.DocValuesType.SORTED_SET) {
            return 4;
        }
        throw new AssertionError();
    }
}

