/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene3x;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.codecs.SegmentInfoReader;
import org.apache.lucene.codecs.lucene3x.Lucene3xSegmentInfoFormat;
import org.apache.lucene.codecs.lucene3x.Lucene3xStoredFieldsReader;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.IndexFormatTooNewException;
import org.apache.lucene.index.IndexFormatTooOldException;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.index.SegmentInfoPerCommit;
import org.apache.lucene.index.SegmentInfos;
import org.apache.lucene.store.CompoundFileDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.IOUtils;

@Deprecated
public class Lucene3xSegmentInfoReader
extends SegmentInfoReader {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void readLegacyInfos(SegmentInfos infos, Directory directory, IndexInput input, int format) throws IOException {
        infos.version = input.readLong();
        infos.counter = input.readInt();
        Lucene3xSegmentInfoReader reader = new Lucene3xSegmentInfoReader();
        for (int i = input.readInt(); i > 0; --i) {
            SegmentInfoPerCommit siPerCommit = reader.readLegacySegmentInfo(directory, format, input);
            SegmentInfo si = siPerCommit.info;
            if (si.getVersion() == null) {
                Directory dir = directory;
                if (Lucene3xSegmentInfoFormat.getDocStoreOffset(si) != -1) {
                    if (Lucene3xSegmentInfoFormat.getDocStoreIsCompoundFile(si)) {
                        dir = new CompoundFileDirectory(dir, IndexFileNames.segmentFileName(Lucene3xSegmentInfoFormat.getDocStoreSegment(si), "", "cfx"), IOContext.READONCE, false);
                    }
                } else if (si.getUseCompoundFile()) {
                    dir = new CompoundFileDirectory(dir, IndexFileNames.segmentFileName(si.name, "", "cfs"), IOContext.READONCE, false);
                }
                try {
                    Lucene3xStoredFieldsReader.checkCodeVersion(dir, Lucene3xSegmentInfoFormat.getDocStoreSegment(si));
                }
                finally {
                    if (dir != directory) {
                        dir.close();
                    }
                }
                si.setVersion("3.0");
            } else if (si.getVersion().equals("2.x")) {
                throw new IndexFormatTooOldException("segment " + si.name + " in resource " + input, si.getVersion());
            }
            infos.add(siPerCommit);
        }
        infos.userData = input.readStringStringMap();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SegmentInfo read(Directory directory, String segmentName, IOContext context) throws IOException {
        SegmentInfo segmentInfo;
        block5: {
            IndexInput input;
            block4: {
                String fileName = IndexFileNames.segmentFileName(segmentName, "", "si");
                boolean success = false;
                input = directory.openInput(fileName, context);
                try {
                    SegmentInfo si = this.readUpgradedSegmentInfo(segmentName, directory, input);
                    success = true;
                    segmentInfo = si;
                    if (success) break block4;
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
                break block5;
            }
            input.close();
        }
        return segmentInfo;
    }

    private static void addIfExists(Directory dir, Set<String> files, String fileName) throws IOException {
        if (dir.fileExists(fileName)) {
            files.add(fileName);
        }
    }

    private SegmentInfoPerCommit readLegacySegmentInfo(Directory dir, int format, IndexInput input) throws IOException {
        HashMap<Integer, Long> normGen;
        boolean docStoreIsCompoundFile;
        String docStoreSegment;
        if (format > -9) {
            throw new IndexFormatTooOldException(input, format, -9, -11);
        }
        if (format < -11) {
            throw new IndexFormatTooNewException(input, format, -9, -11);
        }
        String version = format <= -11 ? input.readString() : null;
        String name = input.readString();
        int docCount = input.readInt();
        long delGen = input.readLong();
        int docStoreOffset = input.readInt();
        HashMap<String, String> attributes = new HashMap<String, String>();
        if (docStoreOffset != -1) {
            docStoreSegment = input.readString();
            docStoreIsCompoundFile = input.readByte() == 1;
            attributes.put(Lucene3xSegmentInfoFormat.DS_OFFSET_KEY, Integer.toString(docStoreOffset));
            attributes.put(Lucene3xSegmentInfoFormat.DS_NAME_KEY, docStoreSegment);
            attributes.put(Lucene3xSegmentInfoFormat.DS_COMPOUND_KEY, Boolean.toString(docStoreIsCompoundFile));
        } else {
            docStoreSegment = name;
            docStoreIsCompoundFile = false;
        }
        byte b = input.readByte();
        assert (1 == b) : "expected 1 but was: " + b + " format: " + format;
        int numNormGen = input.readInt();
        if (numNormGen == -1) {
            normGen = null;
        } else {
            normGen = new HashMap<Integer, Long>();
            for (int j = 0; j < numNormGen; ++j) {
                normGen.put(j, input.readLong());
            }
        }
        boolean isCompoundFile = input.readByte() == 1;
        int delCount = input.readInt();
        assert (delCount <= docCount);
        boolean hasProx = input.readByte() == 1;
        Map<String, String> diagnostics = input.readStringStringMap();
        if (format <= -10) {
            byte by = input.readByte();
        }
        HashSet<String> files = new HashSet<String>();
        if (isCompoundFile) {
            files.add(IndexFileNames.segmentFileName(name, "", "cfs"));
        } else {
            Lucene3xSegmentInfoReader.addIfExists(dir, files, IndexFileNames.segmentFileName(name, "", "fnm"));
            Lucene3xSegmentInfoReader.addIfExists(dir, files, IndexFileNames.segmentFileName(name, "", "frq"));
            Lucene3xSegmentInfoReader.addIfExists(dir, files, IndexFileNames.segmentFileName(name, "", "prx"));
            Lucene3xSegmentInfoReader.addIfExists(dir, files, IndexFileNames.segmentFileName(name, "", "tis"));
            Lucene3xSegmentInfoReader.addIfExists(dir, files, IndexFileNames.segmentFileName(name, "", "tii"));
            Lucene3xSegmentInfoReader.addIfExists(dir, files, IndexFileNames.segmentFileName(name, "", "nrm"));
        }
        if (docStoreOffset != -1) {
            if (docStoreIsCompoundFile) {
                files.add(IndexFileNames.segmentFileName(docStoreSegment, "", "cfx"));
            } else {
                files.add(IndexFileNames.segmentFileName(docStoreSegment, "", "fdx"));
                files.add(IndexFileNames.segmentFileName(docStoreSegment, "", "fdt"));
                Lucene3xSegmentInfoReader.addIfExists(dir, files, IndexFileNames.segmentFileName(docStoreSegment, "", "tvx"));
                Lucene3xSegmentInfoReader.addIfExists(dir, files, IndexFileNames.segmentFileName(docStoreSegment, "", "tvf"));
                Lucene3xSegmentInfoReader.addIfExists(dir, files, IndexFileNames.segmentFileName(docStoreSegment, "", "tvd"));
            }
        } else if (!isCompoundFile) {
            files.add(IndexFileNames.segmentFileName(name, "", "fdx"));
            files.add(IndexFileNames.segmentFileName(name, "", "fdt"));
            Lucene3xSegmentInfoReader.addIfExists(dir, files, IndexFileNames.segmentFileName(name, "", "tvx"));
            Lucene3xSegmentInfoReader.addIfExists(dir, files, IndexFileNames.segmentFileName(name, "", "tvf"));
            Lucene3xSegmentInfoReader.addIfExists(dir, files, IndexFileNames.segmentFileName(name, "", "tvd"));
        }
        if (normGen != null) {
            attributes.put(Lucene3xSegmentInfoFormat.NORMGEN_KEY, Integer.toString(numNormGen));
            for (Map.Entry ent : normGen.entrySet()) {
                long gen = (Long)ent.getValue();
                if (gen >= 1L) {
                    files.add(IndexFileNames.fileNameFromGeneration(name, "s" + ent.getKey(), gen));
                    attributes.put(Lucene3xSegmentInfoFormat.NORMGEN_PREFIX + ent.getKey(), Long.toString(gen));
                    continue;
                }
                if (gen != -1L) assert (false);
            }
        }
        SegmentInfo info = new SegmentInfo(dir, version, name, docCount, isCompoundFile, null, diagnostics, Collections.unmodifiableMap(attributes));
        info.setFiles(files);
        SegmentInfoPerCommit infoPerCommit = new SegmentInfoPerCommit(info, delCount, delGen);
        return infoPerCommit;
    }

    private SegmentInfo readUpgradedSegmentInfo(String name, Directory dir, IndexInput input) throws IOException {
        CodecUtil.checkHeader(input, "Lucene3xSegmentInfo", 0, 0);
        String version = input.readString();
        int docCount = input.readInt();
        Map<String, String> attributes = input.readStringStringMap();
        boolean isCompoundFile = input.readByte() == 1;
        Map<String, String> diagnostics = input.readStringStringMap();
        Set<String> files = input.readStringSet();
        SegmentInfo info = new SegmentInfo(dir, version, name, docCount, isCompoundFile, null, diagnostics, Collections.unmodifiableMap(attributes));
        info.setFiles(files);
        return info;
    }
}

