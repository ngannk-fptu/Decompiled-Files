/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene40;

import java.io.IOException;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.codecs.SegmentInfoWriter;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.util.IOUtils;

public class Lucene40SegmentInfoWriter
extends SegmentInfoWriter {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void write(Directory dir, SegmentInfo si, FieldInfos fis, IOContext ioContext) throws IOException {
        block5: {
            IndexOutput output;
            block4: {
                String fileName = IndexFileNames.segmentFileName(si.name, "", "si");
                si.addFile(fileName);
                output = dir.createOutput(fileName, ioContext);
                boolean success = false;
                try {
                    CodecUtil.writeHeader(output, "Lucene40SegmentInfo", 0);
                    output.writeString(si.getVersion());
                    output.writeInt(si.getDocCount());
                    output.writeByte((byte)(si.getUseCompoundFile() ? 1 : -1));
                    output.writeStringStringMap(si.getDiagnostics());
                    output.writeStringStringMap(si.attributes());
                    output.writeStringSet(si.files());
                    success = true;
                    if (success) break block4;
                }
                catch (Throwable throwable) {
                    if (!success) {
                        IOUtils.closeWhileHandlingException(output);
                        si.dir.deleteFile(fileName);
                    } else {
                        output.close();
                    }
                    throw throwable;
                }
                IOUtils.closeWhileHandlingException(output);
                si.dir.deleteFile(fileName);
                break block5;
            }
            output.close();
        }
    }
}

