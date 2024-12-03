/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.lucene.codecs.BlockTreeTermsReader;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.codecs.lucene3x.Lucene3xSegmentInfoFormat;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.SegmentInfoPerCommit;
import org.apache.lucene.index.SegmentInfos;
import org.apache.lucene.index.SegmentReader;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.CommandLineUtil;
import org.apache.lucene.util.FixedBitSet;
import org.apache.lucene.util.OpenBitSet;
import org.apache.lucene.util.StringHelper;

public class CheckIndex {
    private PrintStream infoStream;
    private Directory dir;
    private boolean crossCheckTermVectors;
    private boolean verbose;
    private static boolean assertsOn;

    public CheckIndex(Directory dir) {
        this.dir = dir;
        this.infoStream = null;
    }

    public void setCrossCheckTermVectors(boolean v) {
        this.crossCheckTermVectors = v;
    }

    public boolean getCrossCheckTermVectors() {
        return this.crossCheckTermVectors;
    }

    public void setInfoStream(PrintStream out, boolean verbose) {
        this.infoStream = out;
        this.verbose = verbose;
    }

    public void setInfoStream(PrintStream out) {
        this.setInfoStream(out, false);
    }

    private static void msg(PrintStream out, String msg) {
        if (out != null) {
            out.println(msg);
        }
    }

    public Status checkIndex() throws IOException {
        return this.checkIndex(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Status checkIndex(List<String> onlySegments) throws IOException {
        NumberFormat nf = NumberFormat.getInstance(Locale.ROOT);
        SegmentInfos sis = new SegmentInfos();
        Status result = new Status();
        result.dir = this.dir;
        try {
            sis.read(this.dir);
        }
        catch (Throwable t) {
            CheckIndex.msg(this.infoStream, "ERROR: could not read any segments file in directory");
            result.missingSegments = true;
            if (this.infoStream != null) {
                t.printStackTrace(this.infoStream);
            }
            return result;
        }
        String oldest = Integer.toString(Integer.MAX_VALUE);
        String newest = Integer.toString(Integer.MIN_VALUE);
        String oldSegs = null;
        boolean foundNonNullVersion = false;
        Comparator<String> versionComparator = StringHelper.getVersionComparator();
        for (SegmentInfoPerCommit si : sis) {
            String version = si.info.getVersion();
            if (version == null) {
                oldSegs = "pre-3.1";
                continue;
            }
            foundNonNullVersion = true;
            if (versionComparator.compare(version, oldest) < 0) {
                oldest = version;
            }
            if (versionComparator.compare(version, newest) <= 0) continue;
            newest = version;
        }
        int numSegments = sis.size();
        String segmentsFileName = sis.getSegmentsFileName();
        IndexInput input = null;
        try {
            input = this.dir.openInput(segmentsFileName, IOContext.DEFAULT);
        }
        catch (Throwable t) {
            CheckIndex.msg(this.infoStream, "ERROR: could not open segments file in directory");
            if (this.infoStream != null) {
                t.printStackTrace(this.infoStream);
            }
            result.cantOpenSegments = true;
            return result;
        }
        int format = 0;
        try {
            format = input.readInt();
        }
        catch (Throwable t) {
            CheckIndex.msg(this.infoStream, "ERROR: could not read segment file version in directory");
            if (this.infoStream != null) {
                t.printStackTrace(this.infoStream);
            }
            result.missingSegmentVersion = true;
            Status status = result;
            return status;
        }
        finally {
            if (input != null) {
                input.close();
            }
        }
        String sFormat = "";
        boolean skip = false;
        result.segmentsFileName = segmentsFileName;
        result.numSegments = numSegments;
        result.userData = sis.getUserData();
        String userDataString = sis.getUserData().size() > 0 ? " userData=" + sis.getUserData() : "";
        String versionString = null;
        versionString = oldSegs != null ? (foundNonNullVersion ? "versions=[" + oldSegs + " .. " + newest + "]" : "version=" + oldSegs) : (oldest.equals(newest) ? "version=" + oldest : "versions=[" + oldest + " .. " + newest + "]");
        CheckIndex.msg(this.infoStream, "Segments file=" + segmentsFileName + " numSegments=" + numSegments + " " + versionString + " format=" + sFormat + userDataString);
        if (onlySegments != null) {
            result.partial = true;
            if (this.infoStream != null) {
                this.infoStream.print("\nChecking only these segments:");
                for (String s : onlySegments) {
                    this.infoStream.print(" " + s);
                }
            }
            result.segmentsChecked.addAll(onlySegments);
            CheckIndex.msg(this.infoStream, ":");
        }
        if (skip) {
            CheckIndex.msg(this.infoStream, "\nERROR: this index appears to be created by a newer version of Lucene than this tool was compiled on; please re-compile this tool on the matching version of Lucene; exiting");
            result.toolOutOfDate = true;
            return result;
        }
        result.newSegments = sis.clone();
        result.newSegments.clear();
        result.maxSegmentName = -1;
        for (int i = 0; i < numSegments; ++i) {
            SegmentInfoPerCommit info = sis.info(i);
            int segmentName = Integer.parseInt(info.info.name.substring(1), 36);
            if (segmentName > result.maxSegmentName) {
                result.maxSegmentName = segmentName;
            }
            if (onlySegments != null && !onlySegments.contains(info.info.name)) continue;
            Status.SegmentInfoStatus segInfoStat = new Status.SegmentInfoStatus();
            result.segmentInfos.add(segInfoStat);
            CheckIndex.msg(this.infoStream, "  " + (1 + i) + " of " + numSegments + ": name=" + info.info.name + " docCount=" + info.info.getDocCount());
            segInfoStat.name = info.info.name;
            segInfoStat.docCount = info.info.getDocCount();
            int toLoseDocCount = info.info.getDocCount();
            try (IndexReader reader = null;){
                Bits liveDocs;
                int numDocs;
                Map<String, String> atts;
                Codec codec = info.info.getCodec();
                CheckIndex.msg(this.infoStream, "    codec=" + codec);
                segInfoStat.codec = codec;
                CheckIndex.msg(this.infoStream, "    compound=" + info.info.getUseCompoundFile());
                segInfoStat.compound = info.info.getUseCompoundFile();
                CheckIndex.msg(this.infoStream, "    numFiles=" + info.files().size());
                segInfoStat.numFiles = info.files().size();
                segInfoStat.sizeMB = (double)info.sizeInBytes() / 1048576.0;
                if (info.info.getAttribute(Lucene3xSegmentInfoFormat.DS_OFFSET_KEY) == null) {
                    CheckIndex.msg(this.infoStream, "    size (MB)=" + nf.format(segInfoStat.sizeMB));
                }
                Map<String, String> diagnostics = info.info.getDiagnostics();
                segInfoStat.diagnostics = diagnostics;
                if (diagnostics.size() > 0) {
                    CheckIndex.msg(this.infoStream, "    diagnostics = " + diagnostics);
                }
                if ((atts = info.info.attributes()) != null && !atts.isEmpty()) {
                    CheckIndex.msg(this.infoStream, "    attributes = " + atts);
                }
                if (!info.hasDeletions()) {
                    CheckIndex.msg(this.infoStream, "    no deletions");
                    segInfoStat.hasDeletions = false;
                } else {
                    CheckIndex.msg(this.infoStream, "    has deletions [delGen=" + info.getDelGen() + "]");
                    segInfoStat.hasDeletions = true;
                    segInfoStat.deletionsGen = info.getDelGen();
                }
                if (this.infoStream != null) {
                    this.infoStream.print("    test: open reader.........");
                }
                reader = new SegmentReader(info, 1, IOContext.DEFAULT);
                segInfoStat.openReaderPassed = true;
                toLoseDocCount = numDocs = reader.numDocs();
                if (reader.hasDeletions()) {
                    if (reader.numDocs() != info.info.getDocCount() - info.getDelCount()) {
                        throw new RuntimeException("delete count mismatch: info=" + (info.info.getDocCount() - info.getDelCount()) + " vs reader=" + reader.numDocs());
                    }
                    if (info.info.getDocCount() - reader.numDocs() > reader.maxDoc()) {
                        throw new RuntimeException("too many deleted docs: maxDoc()=" + reader.maxDoc() + " vs del count=" + (info.info.getDocCount() - reader.numDocs()));
                    }
                    if (info.info.getDocCount() - numDocs != info.getDelCount()) {
                        throw new RuntimeException("delete count mismatch: info=" + info.getDelCount() + " vs reader=" + (info.info.getDocCount() - numDocs));
                    }
                    liveDocs = ((AtomicReader)reader).getLiveDocs();
                    if (liveDocs == null) {
                        throw new RuntimeException("segment should have deletions, but liveDocs is null");
                    }
                    int numLive = 0;
                    for (int j = 0; j < liveDocs.length(); ++j) {
                        if (!liveDocs.get(j)) continue;
                        ++numLive;
                    }
                    if (numLive != numDocs) {
                        throw new RuntimeException("liveDocs count mismatch: info=" + numDocs + ", vs bits=" + numLive);
                    }
                    segInfoStat.numDeleted = info.info.getDocCount() - numDocs;
                    CheckIndex.msg(this.infoStream, "OK [" + segInfoStat.numDeleted + " deleted docs]");
                } else {
                    if (info.getDelCount() != 0) {
                        throw new RuntimeException("delete count mismatch: info=" + info.getDelCount() + " vs reader=" + (info.info.getDocCount() - numDocs));
                    }
                    liveDocs = ((AtomicReader)reader).getLiveDocs();
                    if (liveDocs != null) {
                        for (int j = 0; j < liveDocs.length(); ++j) {
                            if (liveDocs.get(j)) continue;
                            throw new RuntimeException("liveDocs mismatch: info says no deletions but doc " + j + " is deleted.");
                        }
                    }
                    CheckIndex.msg(this.infoStream, "OK");
                }
                if (reader.maxDoc() != info.info.getDocCount()) {
                    throw new RuntimeException("SegmentReader.maxDoc() " + reader.maxDoc() + " != SegmentInfos.docCount " + info.info.getDocCount());
                }
                if (this.infoStream != null) {
                    this.infoStream.print("    test: fields..............");
                }
                FieldInfos fieldInfos = ((AtomicReader)reader).getFieldInfos();
                CheckIndex.msg(this.infoStream, "OK [" + fieldInfos.size() + " fields]");
                segInfoStat.numFields = fieldInfos.size();
                segInfoStat.fieldNormStatus = CheckIndex.testFieldNorms((AtomicReader)reader, this.infoStream);
                segInfoStat.termIndexStatus = CheckIndex.testPostings((AtomicReader)reader, this.infoStream, this.verbose);
                segInfoStat.storedFieldStatus = CheckIndex.testStoredFields((AtomicReader)reader, this.infoStream);
                segInfoStat.termVectorStatus = CheckIndex.testTermVectors((AtomicReader)reader, this.infoStream, this.verbose, this.crossCheckTermVectors);
                segInfoStat.docValuesStatus = CheckIndex.testDocValues((AtomicReader)reader, this.infoStream);
                if (segInfoStat.fieldNormStatus.error != null) {
                    throw new RuntimeException("Field Norm test failed");
                }
                if (segInfoStat.termIndexStatus.error != null) {
                    throw new RuntimeException("Term Index test failed");
                }
                if (segInfoStat.storedFieldStatus.error != null) {
                    throw new RuntimeException("Stored Field test failed");
                }
                if (segInfoStat.termVectorStatus.error != null) {
                    throw new RuntimeException("Term Vector test failed");
                }
                if (segInfoStat.docValuesStatus.error != null) {
                    throw new RuntimeException("DocValues test failed");
                }
                CheckIndex.msg(this.infoStream, "");
            }
            result.newSegments.add(info.clone());
        }
        if (0 == result.numBadSegments) {
            result.clean = true;
        } else {
            CheckIndex.msg(this.infoStream, "WARNING: " + result.numBadSegments + " broken segments (containing " + result.totLoseDocCount + " documents) detected");
        }
        if (!(result.validCounter = result.maxSegmentName < sis.counter)) {
            result.clean = false;
            result.newSegments.counter = result.maxSegmentName + 1;
            CheckIndex.msg(this.infoStream, "ERROR: Next segment name counter " + sis.counter + " is not greater than max segment name " + result.maxSegmentName);
        }
        if (result.clean) {
            CheckIndex.msg(this.infoStream, "No problems were detected with this index.\n");
        }
        return result;
    }

    public static Status.FieldNormStatus testFieldNorms(AtomicReader reader, PrintStream infoStream) {
        Status.FieldNormStatus status;
        block7: {
            status = new Status.FieldNormStatus();
            try {
                if (infoStream != null) {
                    infoStream.print("    test: field norms.........");
                }
                for (FieldInfo info : reader.getFieldInfos()) {
                    if (info.hasNorms()) {
                        assert (reader.hasNorms(info.name));
                        CheckIndex.checkNorms(info, reader, infoStream);
                        ++status.totFields;
                        continue;
                    }
                    assert (!reader.hasNorms(info.name));
                    if (reader.getNormValues(info.name) == null) continue;
                    throw new RuntimeException("field: " + info.name + " should omit norms but has them!");
                }
                CheckIndex.msg(infoStream, "OK [" + status.totFields + " fields]");
            }
            catch (Throwable e) {
                CheckIndex.msg(infoStream, "ERROR [" + String.valueOf(e.getMessage()) + "]");
                status.error = e;
                if (infoStream == null) break block7;
                e.printStackTrace(infoStream);
            }
        }
        return status;
    }

    private static Status.TermIndexStatus checkFields(Fields fields, Bits liveDocs, int maxDoc, FieldInfos fieldInfos, boolean doPrint, boolean isVectors, PrintStream infoStream, boolean verbose) throws IOException {
        long uniqueTermCountAllFields;
        Status.TermIndexStatus status = new Status.TermIndexStatus();
        int computedFieldCount = 0;
        if (fields == null) {
            CheckIndex.msg(infoStream, "OK [no fields/terms]");
            return status;
        }
        DocsEnum docs = null;
        DocsEnum docsAndFreqs = null;
        DocsAndPositionsEnum postings = null;
        String lastField = null;
        for (String field : fields) {
            int seekCount;
            long v;
            BytesRef term;
            if (lastField != null && field.compareTo(lastField) <= 0) {
                throw new RuntimeException("fields out of order: lastField=" + lastField + " field=" + field);
            }
            lastField = field;
            FieldInfo fieldInfo = fieldInfos.fieldInfo(field);
            if (fieldInfo == null) {
                throw new RuntimeException("fieldsEnum inconsistent with fieldInfos, no fieldInfos for: " + field);
            }
            if (!fieldInfo.isIndexed()) {
                throw new RuntimeException("fieldsEnum inconsistent with fieldInfos, isIndexed == false for: " + field);
            }
            ++computedFieldCount;
            Terms terms = fields.terms(field);
            if (terms == null) continue;
            boolean hasPositions = terms.hasPositions();
            boolean hasOffsets = terms.hasOffsets();
            boolean hasFreqs = isVectors || fieldInfo.getIndexOptions().compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS) >= 0;
            TermsEnum termsEnum = terms.iterator(null);
            boolean hasOrd = true;
            long termCountStart = status.delTermCount + status.termCount;
            BytesRef lastTerm = null;
            Comparator<BytesRef> termComp = terms.getComparator();
            long sumTotalTermFreq = 0L;
            long sumDocFreq = 0L;
            FixedBitSet visitedDocs = new FixedBitSet(maxDoc);
            block3: while ((term = termsEnum.next()) != null) {
                int docID;
                int skipDocID;
                int idx;
                boolean hasTotalTermFreq;
                int doc;
                assert (term.isValid());
                if (lastTerm == null) {
                    lastTerm = BytesRef.deepCopyOf(term);
                } else {
                    if (termComp.compare(lastTerm, term) >= 0) {
                        throw new RuntimeException("terms out of order: lastTerm=" + lastTerm + " term=" + term);
                    }
                    lastTerm.copyBytes(term);
                }
                int docFreq = termsEnum.docFreq();
                if (docFreq <= 0) {
                    throw new RuntimeException("docfreq: " + docFreq + " is out of bounds");
                }
                sumDocFreq += (long)docFreq;
                docs = termsEnum.docs(liveDocs, docs);
                postings = termsEnum.docsAndPositions(liveDocs, postings);
                if (hasOrd) {
                    long ordExpected;
                    long ord = -1L;
                    try {
                        ord = termsEnum.ord();
                    }
                    catch (UnsupportedOperationException uoe) {
                        hasOrd = false;
                    }
                    if (hasOrd && ord != (ordExpected = status.delTermCount + status.termCount - termCountStart)) {
                        throw new RuntimeException("ord mismatch: TermsEnum has ord=" + ord + " vs actual=" + ordExpected);
                    }
                }
                DocsEnum docs2 = postings != null ? postings : docs;
                int lastDoc = -1;
                int docCount = 0;
                long totalTermFreq = 0L;
                while ((doc = docs2.nextDoc()) != Integer.MAX_VALUE) {
                    ++status.totFreq;
                    visitedDocs.set(doc);
                    int freq = -1;
                    if (hasFreqs) {
                        freq = docs2.freq();
                        if (freq <= 0) {
                            throw new RuntimeException("term " + term + ": doc " + doc + ": freq " + freq + " is out of bounds");
                        }
                        status.totPos += (long)freq;
                        totalTermFreq += (long)freq;
                    }
                    ++docCount;
                    if (doc <= lastDoc) {
                        throw new RuntimeException("term " + term + ": doc " + doc + " <= lastDoc " + lastDoc);
                    }
                    if (doc >= maxDoc) {
                        throw new RuntimeException("term " + term + ": doc " + doc + " >= maxDoc " + maxDoc);
                    }
                    lastDoc = doc;
                    int lastPos = -1;
                    int lastOffset = 0;
                    if (!hasPositions) continue;
                    for (int j = 0; j < freq; ++j) {
                        int pos = postings.nextPosition();
                        if (pos < 0) {
                            throw new RuntimeException("term " + term + ": doc " + doc + ": pos " + pos + " is out of bounds");
                        }
                        if (pos < lastPos) {
                            throw new RuntimeException("term " + term + ": doc " + doc + ": pos " + pos + " < lastPos " + lastPos);
                        }
                        lastPos = pos;
                        BytesRef payload = postings.getPayload();
                        if (payload != null) assert (payload.isValid());
                        if (payload != null && payload.length < 1) {
                            throw new RuntimeException("term " + term + ": doc " + doc + ": pos " + pos + " payload length is out of bounds " + payload.length);
                        }
                        if (!hasOffsets) continue;
                        int startOffset = postings.startOffset();
                        int endOffset = postings.endOffset();
                        if (!isVectors) {
                            if (startOffset < 0) {
                                throw new RuntimeException("term " + term + ": doc " + doc + ": pos " + pos + ": startOffset " + startOffset + " is out of bounds");
                            }
                            if (startOffset < lastOffset) {
                                throw new RuntimeException("term " + term + ": doc " + doc + ": pos " + pos + ": startOffset " + startOffset + " < lastStartOffset " + lastOffset);
                            }
                            if (endOffset < 0) {
                                throw new RuntimeException("term " + term + ": doc " + doc + ": pos " + pos + ": endOffset " + endOffset + " is out of bounds");
                            }
                            if (endOffset < startOffset) {
                                throw new RuntimeException("term " + term + ": doc " + doc + ": pos " + pos + ": endOffset " + endOffset + " < startOffset " + startOffset);
                            }
                        }
                        lastOffset = startOffset;
                    }
                }
                if (docCount != 0) {
                    ++status.termCount;
                } else {
                    ++status.delTermCount;
                }
                long totalTermFreq2 = termsEnum.totalTermFreq();
                boolean bl = hasTotalTermFreq = hasFreqs && totalTermFreq2 != -1L;
                if (liveDocs != null) {
                    if (hasFreqs) {
                        DocsEnum docsNoDel = termsEnum.docs(null, docsAndFreqs);
                        docCount = 0;
                        totalTermFreq = 0L;
                        while (docsNoDel.nextDoc() != Integer.MAX_VALUE) {
                            visitedDocs.set(docsNoDel.docID());
                            ++docCount;
                            totalTermFreq += (long)docsNoDel.freq();
                        }
                    } else {
                        DocsEnum docsNoDel = termsEnum.docs(null, docs, 0);
                        docCount = 0;
                        totalTermFreq = -1L;
                        while (docsNoDel.nextDoc() != Integer.MAX_VALUE) {
                            visitedDocs.set(docsNoDel.docID());
                            ++docCount;
                        }
                    }
                }
                if (docCount != docFreq) {
                    throw new RuntimeException("term " + term + " docFreq=" + docFreq + " != tot docs w/o deletions " + docCount);
                }
                if (hasTotalTermFreq) {
                    if (totalTermFreq2 <= 0L) {
                        throw new RuntimeException("totalTermFreq: " + totalTermFreq2 + " is out of bounds");
                    }
                    sumTotalTermFreq += totalTermFreq;
                    if (totalTermFreq != totalTermFreq2) {
                        throw new RuntimeException("term " + term + " totalTermFreq=" + totalTermFreq2 + " != recomputed totalTermFreq=" + totalTermFreq);
                    }
                }
                if (hasPositions) {
                    for (idx = 0; idx < 7; ++idx) {
                        skipDocID = (int)((long)(idx + 1) * (long)maxDoc / 8L);
                        docID = (postings = termsEnum.docsAndPositions(liveDocs, postings)).advance(skipDocID);
                        if (docID == Integer.MAX_VALUE) continue block3;
                        if (docID < skipDocID) {
                            throw new RuntimeException("term " + term + ": advance(docID=" + skipDocID + ") returned docID=" + docID);
                        }
                        int freq = postings.freq();
                        if (freq <= 0) {
                            throw new RuntimeException("termFreq " + freq + " is out of bounds");
                        }
                        int lastPosition = -1;
                        int lastOffset = 0;
                        for (int posUpto = 0; posUpto < freq; ++posUpto) {
                            int pos = postings.nextPosition();
                            if (pos < 0) {
                                throw new RuntimeException("position " + pos + " is out of bounds");
                            }
                            if (pos < lastPosition) {
                                throw new RuntimeException("position " + pos + " is < lastPosition " + lastPosition);
                            }
                            lastPosition = pos;
                            if (!hasOffsets) continue;
                            int startOffset = postings.startOffset();
                            int endOffset = postings.endOffset();
                            if (!isVectors) {
                                if (startOffset < 0) {
                                    throw new RuntimeException("term " + term + ": doc " + docID + ": pos " + pos + ": startOffset " + startOffset + " is out of bounds");
                                }
                                if (startOffset < lastOffset) {
                                    throw new RuntimeException("term " + term + ": doc " + docID + ": pos " + pos + ": startOffset " + startOffset + " < lastStartOffset " + lastOffset);
                                }
                                if (endOffset < 0) {
                                    throw new RuntimeException("term " + term + ": doc " + docID + ": pos " + pos + ": endOffset " + endOffset + " is out of bounds");
                                }
                                if (endOffset < startOffset) {
                                    throw new RuntimeException("term " + term + ": doc " + docID + ": pos " + pos + ": endOffset " + endOffset + " < startOffset " + startOffset);
                                }
                            }
                            lastOffset = startOffset;
                        }
                        int nextDocID = postings.nextDoc();
                        if (nextDocID == Integer.MAX_VALUE) continue block3;
                        if (nextDocID > docID) continue;
                        throw new RuntimeException("term " + term + ": advance(docID=" + skipDocID + "), then .next() returned docID=" + nextDocID + " vs prev docID=" + docID);
                    }
                    continue;
                }
                for (idx = 0; idx < 7; ++idx) {
                    skipDocID = (int)((long)(idx + 1) * (long)maxDoc / 8L);
                    docID = (docs = termsEnum.docs(liveDocs, docs, 0)).advance(skipDocID);
                    if (docID == Integer.MAX_VALUE) continue block3;
                    if (docID < skipDocID) {
                        throw new RuntimeException("term " + term + ": advance(docID=" + skipDocID + ") returned docID=" + docID);
                    }
                    int nextDocID = docs.nextDoc();
                    if (nextDocID == Integer.MAX_VALUE) continue block3;
                    if (nextDocID > docID) continue;
                    throw new RuntimeException("term " + term + ": advance(docID=" + skipDocID + "), then .next() returned docID=" + nextDocID + " vs prev docID=" + docID);
                }
            }
            Terms fieldTerms = fields.terms(field);
            if (fieldTerms == null) continue;
            if (fieldTerms instanceof BlockTreeTermsReader.FieldReader) {
                BlockTreeTermsReader.Stats stats = ((BlockTreeTermsReader.FieldReader)fieldTerms).computeStats();
                assert (stats != null);
                if (status.blockTreeStats == null) {
                    status.blockTreeStats = new HashMap<String, BlockTreeTermsReader.Stats>();
                }
                status.blockTreeStats.put(field, stats);
            }
            if (sumTotalTermFreq != 0L && (v = fields.terms(field).getSumTotalTermFreq()) != -1L && sumTotalTermFreq != v) {
                throw new RuntimeException("sumTotalTermFreq for field " + field + "=" + v + " != recomputed sumTotalTermFreq=" + sumTotalTermFreq);
            }
            if (sumDocFreq != 0L && (v = fields.terms(field).getSumDocFreq()) != -1L && sumDocFreq != v) {
                throw new RuntimeException("sumDocFreq for field " + field + "=" + v + " != recomputed sumDocFreq=" + sumDocFreq);
            }
            if (fieldTerms != null && (v = fieldTerms.getDocCount()) != -1 && visitedDocs.cardinality() != v) {
                throw new RuntimeException("docCount for field " + field + "=" + v + " != recomputed docCount=" + visitedDocs.cardinality());
            }
            if (lastTerm != null) {
                if (termsEnum.seekCeil(lastTerm) != TermsEnum.SeekStatus.FOUND) {
                    throw new RuntimeException("seek to last term " + lastTerm + " failed");
                }
                int expectedDocFreq = termsEnum.docFreq();
                DocsEnum d = termsEnum.docs(null, null, 0);
                int docFreq = 0;
                while (d.nextDoc() != Integer.MAX_VALUE) {
                    ++docFreq;
                }
                if (docFreq != expectedDocFreq) {
                    throw new RuntimeException("docFreq for last term " + lastTerm + "=" + expectedDocFreq + " != recomputed docFreq=" + docFreq);
                }
            }
            long termCount = -1L;
            if (status.delTermCount + status.termCount - termCountStart > 0L && (termCount = fields.terms(field).size()) != -1L && termCount != status.delTermCount + status.termCount - termCountStart) {
                throw new RuntimeException("termCount mismatch " + (status.delTermCount + termCount) + " vs " + (status.termCount - termCountStart));
            }
            if (!hasOrd || status.termCount - termCountStart <= 0L || (seekCount = (int)Math.min(10000L, termCount)) <= 0) continue;
            BytesRef[] seekTerms = new BytesRef[seekCount];
            for (int i = seekCount - 1; i >= 0; --i) {
                long ord = (long)i * (termCount / (long)seekCount);
                termsEnum.seekExact(ord);
                seekTerms[i] = BytesRef.deepCopyOf(termsEnum.term());
            }
            long totDocCount = 0L;
            for (int i = seekCount - 1; i >= 0; --i) {
                if (termsEnum.seekCeil(seekTerms[i]) != TermsEnum.SeekStatus.FOUND) {
                    throw new RuntimeException("seek to existing term " + seekTerms[i] + " failed");
                }
                if ((docs = termsEnum.docs(liveDocs, docs, 0)) == null) {
                    throw new RuntimeException("null DocsEnum from to existing term " + seekTerms[i]);
                }
                while (docs.nextDoc() != Integer.MAX_VALUE) {
                    ++totDocCount;
                }
            }
            long totDocCountNoDeletes = 0L;
            long totDocFreq = 0L;
            for (int i = 0; i < seekCount; ++i) {
                if (!termsEnum.seekExact(seekTerms[i], true)) {
                    throw new RuntimeException("seek to existing term " + seekTerms[i] + " failed");
                }
                totDocFreq += (long)termsEnum.docFreq();
                if ((docs = termsEnum.docs(null, docs, 0)) == null) {
                    throw new RuntimeException("null DocsEnum from to existing term " + seekTerms[i]);
                }
                while (docs.nextDoc() != Integer.MAX_VALUE) {
                    ++totDocCountNoDeletes;
                }
            }
            if (totDocCount > totDocCountNoDeletes) {
                throw new RuntimeException("more postings with deletes=" + totDocCount + " than without=" + totDocCountNoDeletes);
            }
            if (totDocCountNoDeletes == totDocFreq) continue;
            throw new RuntimeException("docfreqs=" + totDocFreq + " != recomputed docfreqs=" + totDocCountNoDeletes);
        }
        int fieldCount = fields.size();
        if (fieldCount != -1) {
            if (fieldCount < 0) {
                throw new RuntimeException("invalid fieldCount: " + fieldCount);
            }
            if (fieldCount != computedFieldCount) {
                throw new RuntimeException("fieldCount mismatch " + fieldCount + " vs recomputed field count " + computedFieldCount);
            }
        }
        if ((uniqueTermCountAllFields = fields.getUniqueTermCount()) != -1L && status.termCount + status.delTermCount != uniqueTermCountAllFields) {
            throw new RuntimeException("termCount mismatch " + uniqueTermCountAllFields + " vs " + (status.termCount + status.delTermCount));
        }
        if (doPrint) {
            CheckIndex.msg(infoStream, "OK [" + status.termCount + " terms; " + status.totFreq + " terms/docs pairs; " + status.totPos + " tokens]");
        }
        if (verbose && status.blockTreeStats != null && infoStream != null && status.termCount > 0L) {
            for (Map.Entry<String, BlockTreeTermsReader.Stats> ent : status.blockTreeStats.entrySet()) {
                infoStream.println("      field \"" + ent.getKey() + "\":");
                infoStream.println("      " + ent.getValue().toString().replace("\n", "\n      "));
            }
        }
        return status;
    }

    public static Status.TermIndexStatus testPostings(AtomicReader reader, PrintStream infoStream) {
        return CheckIndex.testPostings(reader, infoStream, false);
    }

    public static Status.TermIndexStatus testPostings(AtomicReader reader, PrintStream infoStream, boolean verbose) {
        Status.TermIndexStatus status;
        block5: {
            int maxDoc = reader.maxDoc();
            Bits liveDocs = reader.getLiveDocs();
            try {
                if (infoStream != null) {
                    infoStream.print("    test: terms, freq, prox...");
                }
                Fields fields = reader.fields();
                FieldInfos fieldInfos = reader.getFieldInfos();
                status = CheckIndex.checkFields(fields, liveDocs, maxDoc, fieldInfos, true, false, infoStream, verbose);
                if (liveDocs != null) {
                    if (infoStream != null) {
                        infoStream.print("    test (ignoring deletes): terms, freq, prox...");
                    }
                    CheckIndex.checkFields(fields, null, maxDoc, fieldInfos, true, false, infoStream, verbose);
                }
            }
            catch (Throwable e) {
                CheckIndex.msg(infoStream, "ERROR: " + e);
                status = new Status.TermIndexStatus();
                status.error = e;
                if (infoStream == null) break block5;
                e.printStackTrace(infoStream);
            }
        }
        return status;
    }

    public static Status.StoredFieldStatus testStoredFields(AtomicReader reader, PrintStream infoStream) {
        Status.StoredFieldStatus status;
        block5: {
            status = new Status.StoredFieldStatus();
            try {
                if (infoStream != null) {
                    infoStream.print("    test: stored fields.......");
                }
                Bits liveDocs = reader.getLiveDocs();
                for (int j = 0; j < reader.maxDoc(); ++j) {
                    Document doc = reader.document(j);
                    if (liveDocs != null && !liveDocs.get(j)) continue;
                    ++status.docCount;
                    status.totFields += (long)doc.getFields().size();
                }
                if (status.docCount != reader.numDocs()) {
                    throw new RuntimeException("docCount=" + status.docCount + " but saw " + status.docCount + " undeleted docs");
                }
                CheckIndex.msg(infoStream, "OK [" + status.totFields + " total field count; avg " + NumberFormat.getInstance(Locale.ROOT).format((float)status.totFields / (float)status.docCount) + " fields per doc]");
            }
            catch (Throwable e) {
                CheckIndex.msg(infoStream, "ERROR [" + String.valueOf(e.getMessage()) + "]");
                status.error = e;
                if (infoStream == null) break block5;
                e.printStackTrace(infoStream);
            }
        }
        return status;
    }

    public static Status.DocValuesStatus testDocValues(AtomicReader reader, PrintStream infoStream) {
        Status.DocValuesStatus status;
        block5: {
            status = new Status.DocValuesStatus();
            try {
                if (infoStream != null) {
                    infoStream.print("    test: docvalues...........");
                }
                for (FieldInfo fieldInfo : reader.getFieldInfos()) {
                    if (fieldInfo.hasDocValues()) {
                        ++status.totalValueFields;
                        CheckIndex.checkDocValues(fieldInfo, reader, infoStream);
                        continue;
                    }
                    if (reader.getBinaryDocValues(fieldInfo.name) == null && reader.getNumericDocValues(fieldInfo.name) == null && reader.getSortedDocValues(fieldInfo.name) == null && reader.getSortedSetDocValues(fieldInfo.name) == null) continue;
                    throw new RuntimeException("field: " + fieldInfo.name + " has docvalues but should omit them!");
                }
                CheckIndex.msg(infoStream, "OK [" + status.docCount + " total doc count; " + status.totalValueFields + " docvalues fields]");
            }
            catch (Throwable e) {
                CheckIndex.msg(infoStream, "ERROR [" + String.valueOf(e.getMessage()) + "]");
                status.error = e;
                if (infoStream == null) break block5;
                e.printStackTrace(infoStream);
            }
        }
        return status;
    }

    private static void checkBinaryDocValues(String fieldName, AtomicReader reader, BinaryDocValues dv) {
        BytesRef scratch = new BytesRef();
        for (int i = 0; i < reader.maxDoc(); ++i) {
            dv.get(i, scratch);
            assert (scratch.isValid());
        }
    }

    private static void checkSortedDocValues(String fieldName, AtomicReader reader, SortedDocValues dv) {
        CheckIndex.checkBinaryDocValues(fieldName, reader, dv);
        int maxOrd = dv.getValueCount() - 1;
        FixedBitSet seenOrds = new FixedBitSet(dv.getValueCount());
        int maxOrd2 = -1;
        for (int i = 0; i < reader.maxDoc(); ++i) {
            int ord = dv.getOrd(i);
            if (ord < 0 || ord > maxOrd) {
                throw new RuntimeException("ord out of bounds: " + ord);
            }
            maxOrd2 = Math.max(maxOrd2, ord);
            seenOrds.set(ord);
        }
        if (maxOrd != maxOrd2) {
            throw new RuntimeException("dv for field: " + fieldName + " reports wrong maxOrd=" + maxOrd + " but this is not the case: " + maxOrd2);
        }
        if (seenOrds.cardinality() != dv.getValueCount()) {
            throw new RuntimeException("dv for field: " + fieldName + " has holes in its ords, valueCount=" + dv.getValueCount() + " but only used: " + seenOrds.cardinality());
        }
        BytesRef lastValue = null;
        BytesRef scratch = new BytesRef();
        for (int i = 0; i <= maxOrd; ++i) {
            dv.lookupOrd(i, scratch);
            assert (scratch.isValid());
            if (lastValue != null && scratch.compareTo(lastValue) <= 0) {
                throw new RuntimeException("dv for field: " + fieldName + " has ords out of order: " + lastValue + " >=" + scratch);
            }
            lastValue = BytesRef.deepCopyOf(scratch);
        }
    }

    private static void checkSortedSetDocValues(String fieldName, AtomicReader reader, SortedSetDocValues dv) {
        long maxOrd = dv.getValueCount() - 1L;
        OpenBitSet seenOrds = new OpenBitSet(dv.getValueCount());
        long maxOrd2 = -1L;
        for (int i = 0; i < reader.maxDoc(); ++i) {
            long ord;
            dv.setDocument(i);
            long lastOrd = -1L;
            while ((ord = dv.nextOrd()) != -1L) {
                if (ord <= lastOrd) {
                    throw new RuntimeException("ords out of order: " + ord + " <= " + lastOrd + " for doc: " + i);
                }
                if (ord < 0L || ord > maxOrd) {
                    throw new RuntimeException("ord out of bounds: " + ord);
                }
                lastOrd = ord;
                maxOrd2 = Math.max(maxOrd2, ord);
                seenOrds.set(ord);
            }
        }
        if (maxOrd != maxOrd2) {
            throw new RuntimeException("dv for field: " + fieldName + " reports wrong maxOrd=" + maxOrd + " but this is not the case: " + maxOrd2);
        }
        if (seenOrds.cardinality() != dv.getValueCount()) {
            throw new RuntimeException("dv for field: " + fieldName + " has holes in its ords, valueCount=" + dv.getValueCount() + " but only used: " + seenOrds.cardinality());
        }
        BytesRef lastValue = null;
        BytesRef scratch = new BytesRef();
        for (long i = 0L; i <= maxOrd; ++i) {
            dv.lookupOrd(i, scratch);
            assert (scratch.isValid());
            if (lastValue != null && scratch.compareTo(lastValue) <= 0) {
                throw new RuntimeException("dv for field: " + fieldName + " has ords out of order: " + lastValue + " >=" + scratch);
            }
            lastValue = BytesRef.deepCopyOf(scratch);
        }
    }

    private static void checkNumericDocValues(String fieldName, AtomicReader reader, NumericDocValues ndv) {
        for (int i = 0; i < reader.maxDoc(); ++i) {
            ndv.get(i);
        }
    }

    private static void checkDocValues(FieldInfo fi, AtomicReader reader, PrintStream infoStream) throws Exception {
        switch (fi.getDocValuesType()) {
            case SORTED: {
                CheckIndex.checkSortedDocValues(fi.name, reader, reader.getSortedDocValues(fi.name));
                if (reader.getBinaryDocValues(fi.name) == null && reader.getNumericDocValues(fi.name) == null && reader.getSortedSetDocValues(fi.name) == null) break;
                throw new RuntimeException(fi.name + " returns multiple docvalues types!");
            }
            case SORTED_SET: {
                CheckIndex.checkSortedSetDocValues(fi.name, reader, reader.getSortedSetDocValues(fi.name));
                if (reader.getBinaryDocValues(fi.name) == null && reader.getNumericDocValues(fi.name) == null && reader.getSortedDocValues(fi.name) == null) break;
                throw new RuntimeException(fi.name + " returns multiple docvalues types!");
            }
            case BINARY: {
                CheckIndex.checkBinaryDocValues(fi.name, reader, reader.getBinaryDocValues(fi.name));
                if (reader.getNumericDocValues(fi.name) == null && reader.getSortedDocValues(fi.name) == null && reader.getSortedSetDocValues(fi.name) == null) break;
                throw new RuntimeException(fi.name + " returns multiple docvalues types!");
            }
            case NUMERIC: {
                CheckIndex.checkNumericDocValues(fi.name, reader, reader.getNumericDocValues(fi.name));
                if (reader.getBinaryDocValues(fi.name) == null && reader.getSortedDocValues(fi.name) == null && reader.getSortedSetDocValues(fi.name) == null) break;
                throw new RuntimeException(fi.name + " returns multiple docvalues types!");
            }
            default: {
                throw new AssertionError();
            }
        }
    }

    private static void checkNorms(FieldInfo fi, AtomicReader reader, PrintStream infoStream) throws IOException {
        switch (fi.getNormType()) {
            case NUMERIC: {
                CheckIndex.checkNumericDocValues(fi.name, reader, reader.getNormValues(fi.name));
                break;
            }
            default: {
                throw new AssertionError((Object)("wtf: " + (Object)((Object)fi.getNormType())));
            }
        }
    }

    public static Status.TermVectorStatus testTermVectors(AtomicReader reader, PrintStream infoStream) {
        return CheckIndex.testTermVectors(reader, infoStream, false, false);
    }

    public static Status.TermVectorStatus testTermVectors(AtomicReader reader, PrintStream infoStream, boolean verbose, boolean crossCheckTermVectors) {
        Status.TermVectorStatus status;
        block33: {
            status = new Status.TermVectorStatus();
            FieldInfos fieldInfos = reader.getFieldInfos();
            FixedBitSet onlyDocIsDeleted = new FixedBitSet(1);
            try {
                if (infoStream != null) {
                    infoStream.print("    test: term vectors........");
                }
                DocsEnum docs = null;
                DocsAndPositionsEnum postings = null;
                DocsEnum postingsDocs = null;
                DocsAndPositionsEnum postingsPostings = null;
                Bits liveDocs = reader.getLiveDocs();
                Fields postingsFields = crossCheckTermVectors ? reader.fields() : null;
                TermsEnum termsEnum = null;
                TermsEnum postingsTermsEnum = null;
                for (int j = 0; j < reader.maxDoc(); ++j) {
                    boolean doStats;
                    Fields tfv = reader.getTermVectors(j);
                    if (tfv == null) continue;
                    CheckIndex.checkFields(tfv, null, 1, fieldInfos, false, true, infoStream, verbose);
                    CheckIndex.checkFields(tfv, onlyDocIsDeleted, 1, fieldInfos, false, true, infoStream, verbose);
                    boolean bl = doStats = liveDocs == null || liveDocs.get(j);
                    if (doStats) {
                        ++status.docCount;
                    }
                    for (String field : tfv) {
                        FieldInfo fieldInfo;
                        if (doStats) {
                            ++status.totVectors;
                        }
                        if (!(fieldInfo = fieldInfos.fieldInfo(field)).hasVectors()) {
                            throw new RuntimeException("docID=" + j + " has term vectors for field=" + field + " but FieldInfo has storeTermVector=false");
                        }
                        if (!crossCheckTermVectors) continue;
                        Terms terms = tfv.terms(field);
                        termsEnum = terms.iterator(termsEnum);
                        boolean postingsHasFreq = fieldInfo.getIndexOptions().compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS) >= 0;
                        boolean postingsHasPayload = fieldInfo.hasPayloads();
                        boolean vectorsHasPayload = terms.hasPayloads();
                        Terms postingsTerms = postingsFields.terms(field);
                        if (postingsTerms == null) {
                            throw new RuntimeException("vector field=" + field + " does not exist in postings; doc=" + j);
                        }
                        postingsTermsEnum = postingsTerms.iterator(postingsTermsEnum);
                        boolean hasProx = terms.hasOffsets() || terms.hasPositions();
                        BytesRef term = null;
                        while ((term = termsEnum.next()) != null) {
                            DocsEnum docs2;
                            if (hasProx) {
                                postings = termsEnum.docsAndPositions(null, postings);
                                assert (postings != null);
                                docs = null;
                            } else {
                                docs = termsEnum.docs(null, docs);
                                assert (docs != null);
                                postings = null;
                            }
                            if (hasProx) {
                                assert (postings != null);
                                docs2 = postings;
                            } else {
                                assert (docs != null);
                                docs2 = docs;
                            }
                            if (!postingsTermsEnum.seekExact(term, true)) {
                                throw new RuntimeException("vector term=" + term + " field=" + field + " does not exist in postings; doc=" + j);
                            }
                            if ((postingsPostings = postingsTermsEnum.docsAndPositions(null, postingsPostings)) == null && (postingsDocs = postingsTermsEnum.docs(null, postingsDocs)) == null) {
                                throw new RuntimeException("vector term=" + term + " field=" + field + " does not exist in postings; doc=" + j);
                            }
                            DocsEnum postingsDocs2 = postingsPostings != null ? postingsPostings : postingsDocs;
                            int advanceDoc = postingsDocs2.advance(j);
                            if (advanceDoc != j) {
                                throw new RuntimeException("vector term=" + term + " field=" + field + ": doc=" + j + " was not found in postings (got: " + advanceDoc + ")");
                            }
                            int doc = docs2.nextDoc();
                            if (doc != 0) {
                                throw new RuntimeException("vector for doc " + j + " didn't return docID=0: got docID=" + doc);
                            }
                            if (!postingsHasFreq) continue;
                            int tf = docs2.freq();
                            if (postingsHasFreq && postingsDocs2.freq() != tf) {
                                throw new RuntimeException("vector term=" + term + " field=" + field + " doc=" + j + ": freq=" + tf + " differs from postings freq=" + postingsDocs2.freq());
                            }
                            if (!hasProx) continue;
                            for (int i = 0; i < tf; ++i) {
                                BytesRef payload;
                                int pos = postings.nextPosition();
                                if (postingsPostings != null) {
                                    int postingsPos = postingsPostings.nextPosition();
                                    if (terms.hasPositions() && pos != postingsPos) {
                                        throw new RuntimeException("vector term=" + term + " field=" + field + " doc=" + j + ": pos=" + pos + " differs from postings pos=" + postingsPos);
                                    }
                                }
                                int startOffset = postings.startOffset();
                                int endOffset = postings.endOffset();
                                if (postingsPostings != null) {
                                    int postingsStartOffset = postingsPostings.startOffset();
                                    int postingsEndOffset = postingsPostings.endOffset();
                                    if (startOffset != -1 && postingsStartOffset != -1 && startOffset != postingsStartOffset) {
                                        throw new RuntimeException("vector term=" + term + " field=" + field + " doc=" + j + ": startOffset=" + startOffset + " differs from postings startOffset=" + postingsStartOffset);
                                    }
                                    if (endOffset != -1 && postingsEndOffset != -1 && endOffset != postingsEndOffset) {
                                        throw new RuntimeException("vector term=" + term + " field=" + field + " doc=" + j + ": endOffset=" + endOffset + " differs from postings endOffset=" + postingsEndOffset);
                                    }
                                }
                                if ((payload = postings.getPayload()) != null) assert (vectorsHasPayload);
                                if (!postingsHasPayload || !vectorsHasPayload) continue;
                                assert (postingsPostings != null);
                                if (payload == null) {
                                    if (postingsPostings.getPayload() == null) continue;
                                    throw new RuntimeException("vector term=" + term + " field=" + field + " doc=" + j + " has no payload but postings does: " + postingsPostings.getPayload());
                                }
                                if (postingsPostings.getPayload() == null) {
                                    throw new RuntimeException("vector term=" + term + " field=" + field + " doc=" + j + " has payload=" + payload + " but postings does not.");
                                }
                                BytesRef postingsPayload = postingsPostings.getPayload();
                                if (payload.equals(postingsPayload)) continue;
                                throw new RuntimeException("vector term=" + term + " field=" + field + " doc=" + j + " has payload=" + payload + " but differs from postings payload=" + postingsPayload);
                            }
                        }
                    }
                }
                float vectorAvg = status.docCount == 0 ? 0.0f : (float)status.totVectors / (float)status.docCount;
                CheckIndex.msg(infoStream, "OK [" + status.totVectors + " total vector count; avg " + NumberFormat.getInstance(Locale.ROOT).format(vectorAvg) + " term/freq vector fields per doc]");
            }
            catch (Throwable e) {
                CheckIndex.msg(infoStream, "ERROR [" + String.valueOf(e.getMessage()) + "]");
                status.error = e;
                if (infoStream == null) break block33;
                e.printStackTrace(infoStream);
            }
        }
        return status;
    }

    public void fixIndex(Status result, Codec codec) throws IOException {
        if (result.partial) {
            throw new IllegalArgumentException("can only fix an index that was fully checked (this status checked a subset of segments)");
        }
        result.newSegments.changed();
        result.newSegments.commit(result.dir);
    }

    private static boolean testAsserts() {
        assertsOn = true;
        return true;
    }

    private static boolean assertsOn() {
        assert (CheckIndex.testAsserts());
        return assertsOn;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        boolean doFix = false;
        boolean doCrossCheckTermVectors = false;
        Codec codec = Codec.getDefault();
        boolean verbose = false;
        ArrayList<String> onlySegments = new ArrayList<String>();
        String indexPath = null;
        String dirImpl = null;
        for (int i = 0; i < args.length; ++i) {
            String arg = args[i];
            if ("-fix".equals(arg)) {
                doFix = true;
                continue;
            }
            if ("-crossCheckTermVectors".equals(arg)) {
                doCrossCheckTermVectors = true;
                continue;
            }
            if ("-codec".equals(arg)) {
                if (i == args.length - 1) {
                    System.out.println("ERROR: missing name for -codec option");
                    System.exit(1);
                }
                codec = Codec.forName(args[++i]);
                continue;
            }
            if (arg.equals("-verbose")) {
                verbose = true;
                continue;
            }
            if (arg.equals("-segment")) {
                if (i == args.length - 1) {
                    System.out.println("ERROR: missing name for -segment option");
                    System.exit(1);
                }
                onlySegments.add(args[++i]);
                continue;
            }
            if ("-dir-impl".equals(arg)) {
                if (i == args.length - 1) {
                    System.out.println("ERROR: missing value for -dir-impl option");
                    System.exit(1);
                }
                dirImpl = args[++i];
                continue;
            }
            if (indexPath != null) {
                System.out.println("ERROR: unexpected extra argument '" + args[i] + "'");
                System.exit(1);
            }
            indexPath = args[i];
        }
        if (indexPath == null) {
            System.out.println("\nERROR: index path not specified");
            System.out.println("\nUsage: java org.apache.lucene.index.CheckIndex pathToIndex [-fix] [-crossCheckTermVectors] [-segment X] [-segment Y] [-dir-impl X]\n\n  -fix: actually write a new segments_N file, removing any problematic segments\n  -crossCheckTermVectors: verifies that term vectors match postings; THIS IS VERY SLOW!\n  -codec X: when fixing, codec to write the new segments_N file with\n  -verbose: print additional details\n  -segment X: only check the specified segments.  This can be specified multiple\n              times, to check more than one segment, eg '-segment _2 -segment _a'.\n              You can't use this with the -fix option\n  -dir-impl X: use a specific " + FSDirectory.class.getSimpleName() + " implementation. If no package is specified the " + FSDirectory.class.getPackage().getName() + " package will be used.\n\n**WARNING**: -fix should only be used on an emergency basis as it will cause\ndocuments (perhaps many) to be permanently removed from the index.  Always make\na backup copy of your index before running this!  Do not run this tool on an index\nthat is actively being written to.  You have been warned!\n\nRun without -fix, this tool will open the index, report version information\nand report any exceptions it hits and what action it would take if -fix were\nspecified.  With -fix, this tool will remove any segments that have issues and\nwrite a new segments_N file.  This means all documents contained in the affected\nsegments will be removed.\n\nThis tool exits with exit code 1 if the index cannot be opened or has any\ncorruption, else 0.\n");
            System.exit(1);
        }
        if (!CheckIndex.assertsOn()) {
            System.out.println("\nNOTE: testing will be more thorough if you run java with '-ea:org.apache.lucene...', so assertions are enabled");
        }
        if (onlySegments.size() == 0) {
            onlySegments = null;
        } else if (doFix) {
            System.out.println("ERROR: cannot specify both -fix and -segment");
            System.exit(1);
        }
        System.out.println("\nOpening index @ " + indexPath + "\n");
        FSDirectory dir = null;
        try {
            dir = dirImpl == null ? FSDirectory.open(new File(indexPath)) : CommandLineUtil.newFSDirectory(dirImpl, new File(indexPath));
        }
        catch (Throwable t) {
            System.out.println("ERROR: could not open directory \"" + indexPath + "\"; exiting");
            t.printStackTrace(System.out);
            System.exit(1);
        }
        CheckIndex checker = new CheckIndex(dir);
        checker.setCrossCheckTermVectors(doCrossCheckTermVectors);
        checker.setInfoStream(System.out, verbose);
        Status result = checker.checkIndex(onlySegments);
        if (result.missingSegments) {
            System.exit(1);
        }
        if (!result.clean) {
            if (!doFix) {
                System.out.println("WARNING: would write new segments file, and " + result.totLoseDocCount + " documents would be lost, if -fix were specified\n");
            } else {
                System.out.println("WARNING: " + result.totLoseDocCount + " documents will be lost\n");
                System.out.println("NOTE: will write new segments file in 5 seconds; this will remove " + result.totLoseDocCount + " docs from the index. THIS IS YOUR LAST CHANCE TO CTRL+C!");
                for (int s = 0; s < 5; ++s) {
                    Thread.sleep(1000L);
                    System.out.println("  " + (5 - s) + "...");
                }
                System.out.println("Writing...");
                checker.fixIndex(result, codec);
                System.out.println("OK");
                System.out.println("Wrote new segments file \"" + result.newSegments.getSegmentsFileName() + "\"");
            }
        }
        System.out.println("");
        int exitCode = result.clean ? 0 : 1;
        System.exit(exitCode);
    }

    public static class Status {
        public boolean clean;
        public boolean missingSegments;
        public boolean cantOpenSegments;
        public boolean missingSegmentVersion;
        public String segmentsFileName;
        public int numSegments;
        public List<String> segmentsChecked = new ArrayList<String>();
        public boolean toolOutOfDate;
        public List<SegmentInfoStatus> segmentInfos = new ArrayList<SegmentInfoStatus>();
        public Directory dir;
        SegmentInfos newSegments;
        public int totLoseDocCount;
        public int numBadSegments;
        public boolean partial;
        public int maxSegmentName;
        public boolean validCounter;
        public Map<String, String> userData;

        Status() {
        }

        public static final class DocValuesStatus {
            public int docCount;
            public long totalValueFields;
            public Throwable error = null;

            DocValuesStatus() {
            }
        }

        public static final class TermVectorStatus {
            public int docCount = 0;
            public long totVectors = 0L;
            public Throwable error = null;

            TermVectorStatus() {
            }
        }

        public static final class StoredFieldStatus {
            public int docCount = 0;
            public long totFields = 0L;
            public Throwable error = null;

            StoredFieldStatus() {
            }
        }

        public static final class TermIndexStatus {
            public long termCount = 0L;
            public long delTermCount = 0L;
            public long totFreq = 0L;
            public long totPos = 0L;
            public Throwable error = null;
            public Map<String, BlockTreeTermsReader.Stats> blockTreeStats = null;

            TermIndexStatus() {
            }
        }

        public static final class FieldNormStatus {
            public long totFields = 0L;
            public Throwable error = null;

            private FieldNormStatus() {
            }
        }

        public static class SegmentInfoStatus {
            public String name;
            public Codec codec;
            public int docCount;
            public boolean compound;
            public int numFiles;
            public double sizeMB;
            public int docStoreOffset = -1;
            public String docStoreSegment;
            public boolean docStoreCompoundFile;
            public boolean hasDeletions;
            public long deletionsGen;
            public int numDeleted;
            public boolean openReaderPassed;
            int numFields;
            public Map<String, String> diagnostics;
            public FieldNormStatus fieldNormStatus;
            public TermIndexStatus termIndexStatus;
            public StoredFieldStatus storedFieldStatus;
            public TermVectorStatus termVectorStatus;
            public DocValuesStatus docValuesStatus;

            SegmentInfoStatus() {
            }
        }
    }
}

