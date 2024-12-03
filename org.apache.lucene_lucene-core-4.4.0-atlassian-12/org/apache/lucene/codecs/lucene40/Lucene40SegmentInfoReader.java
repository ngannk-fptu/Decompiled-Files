/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene40;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.codecs.SegmentInfoReader;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.IOUtils;

public class Lucene40SegmentInfoReader
extends SegmentInfoReader {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SegmentInfo read(Directory dir, String segment, IOContext context) throws IOException {
        SegmentInfo segmentInfo;
        block7: {
            IndexInput input;
            block6: {
                String fileName = IndexFileNames.segmentFileName(segment, "", "si");
                input = dir.openInput(fileName, context);
                boolean success = false;
                try {
                    CodecUtil.checkHeader(input, "Lucene40SegmentInfo", 0, 0);
                    String version = input.readString();
                    int docCount = input.readInt();
                    if (docCount < 0) {
                        throw new CorruptIndexException("invalid docCount: " + docCount + " (resource=" + input + ")");
                    }
                    boolean isCompoundFile = input.readByte() == 1;
                    Map<String, String> diagnostics = input.readStringStringMap();
                    Map<String, String> attributes = input.readStringStringMap();
                    Set<String> files = input.readStringSet();
                    if (input.getFilePointer() != input.length()) {
                        throw new CorruptIndexException("did not read all bytes from file \"" + fileName + "\": read " + input.getFilePointer() + " vs size " + input.length() + " (resource: " + input + ")");
                    }
                    SegmentInfo si = new SegmentInfo(dir, version, segment, docCount, isCompoundFile, null, diagnostics, Collections.unmodifiableMap(attributes));
                    si.setFiles(files);
                    success = true;
                    segmentInfo = si;
                    if (success) break block6;
                }
                catch (Throwable throwable) {
                    if (!success) {
                        IOUtils.closeWhileHandlingException(input);
                    } else {
                        input.close();
                    }
                    throw throwable;
                }
                IOUtils.closeWhileHandlingException(input);
                break block7;
            }
            input.close();
        }
        return segmentInfo;
    }
}

