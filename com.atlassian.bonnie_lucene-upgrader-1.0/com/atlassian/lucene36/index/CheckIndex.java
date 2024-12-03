/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.document.Document;
import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.index.FieldInfos;
import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.SegmentInfo;
import com.atlassian.lucene36.index.SegmentInfos;
import com.atlassian.lucene36.index.SegmentReader;
import com.atlassian.lucene36.index.SegmentTermDocs;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.index.TermEnum;
import com.atlassian.lucene36.index.TermFreqVector;
import com.atlassian.lucene36.index.TermPositions;
import com.atlassian.lucene36.search.IndexSearcher;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.search.TermQuery;
import com.atlassian.lucene36.store.Directory;
import com.atlassian.lucene36.store.FSDirectory;
import com.atlassian.lucene36.store.IndexInput;
import com.atlassian.lucene36.util.CommandLineUtil;
import com.atlassian.lucene36.util.StringHelper;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CheckIndex {
    private PrintStream infoStream;
    private Directory dir;
    private static boolean assertsOn;

    public CheckIndex(Directory dir) {
        this.dir = dir;
        this.infoStream = null;
    }

    public void setInfoStream(PrintStream out) {
        this.infoStream = out;
    }

    private void msg(String msg) {
        if (this.infoStream != null) {
            this.infoStream.println(msg);
        }
    }

    public Status checkIndex() throws IOException {
        return this.checkIndex(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public Status checkIndex(List<String> onlySegments) throws IOException {
        int format;
        String segmentsFileName;
        int numSegments;
        boolean foundNonNullVersion;
        String oldSegs;
        String newest;
        String oldest;
        Status result;
        SegmentInfos sis;
        NumberFormat nf;
        block76: {
            nf = NumberFormat.getInstance();
            sis = new SegmentInfos();
            result = new Status();
            result.dir = this.dir;
            try {
                sis.read(this.dir);
            }
            catch (Throwable t) {
                this.msg("ERROR: could not read any segments file in directory");
                result.missingSegments = true;
                if (this.infoStream != null) {
                    t.printStackTrace(this.infoStream);
                }
                return result;
            }
            oldest = Integer.toString(Integer.MAX_VALUE);
            newest = Integer.toString(Integer.MIN_VALUE);
            oldSegs = null;
            foundNonNullVersion = false;
            Comparator<String> versionComparator = StringHelper.getVersionComparator();
            for (SegmentInfo si : sis) {
                String version = si.getVersion();
                if (version == null) {
                    oldSegs = "pre-3.1";
                    continue;
                }
                if (version.equals("2.x")) {
                    oldSegs = "2.x";
                    continue;
                }
                foundNonNullVersion = true;
                if (versionComparator.compare(version, oldest) < 0) {
                    oldest = version;
                }
                if (versionComparator.compare(version, newest) <= 0) continue;
                newest = version;
            }
            numSegments = sis.size();
            segmentsFileName = sis.getSegmentsFileName();
            IndexInput input = null;
            try {
                input = this.dir.openInput(segmentsFileName);
            }
            catch (Throwable t) {
                this.msg("ERROR: could not open segments file in directory");
                if (this.infoStream != null) {
                    t.printStackTrace(this.infoStream);
                }
                result.cantOpenSegments = true;
                return result;
            }
            format = 0;
            try {
                try {
                    format = input.readInt();
                }
                catch (Throwable t) {
                    this.msg("ERROR: could not read segment file version in directory");
                    if (this.infoStream != null) {
                        t.printStackTrace(this.infoStream);
                    }
                    result.missingSegmentVersion = true;
                    Status status = result;
                    Object var17_18 = null;
                    if (input == null) return status;
                    input.close();
                    return status;
                }
                Object var17_17 = null;
                if (input == null) break block76;
            }
            catch (Throwable throwable) {
                Object var17_19 = null;
                if (input != null) {
                    input.close();
                }
                throw throwable;
            }
            input.close();
        }
        String sFormat = "";
        boolean skip = false;
        if (format == -1) {
            sFormat = "FORMAT [Lucene Pre-2.1]";
        }
        if (format == -2) {
            sFormat = "FORMAT_LOCKLESS [Lucene 2.1]";
        } else if (format == -3) {
            sFormat = "FORMAT_SINGLE_NORM_FILE [Lucene 2.2]";
        } else if (format == -4) {
            sFormat = "FORMAT_SHARED_DOC_STORE [Lucene 2.3]";
        } else if (format == -5) {
            sFormat = "FORMAT_CHECKSUM [Lucene 2.4]";
        } else if (format == -6) {
            sFormat = "FORMAT_DEL_COUNT [Lucene 2.4]";
        } else if (format == -7) {
            sFormat = "FORMAT_HAS_PROX [Lucene 2.4]";
        } else if (format == -8) {
            sFormat = "FORMAT_USER_DATA [Lucene 2.9]";
        } else if (format == -9) {
            sFormat = "FORMAT_DIAGNOSTICS [Lucene 2.9]";
        } else if (format == -10) {
            sFormat = "FORMAT_HAS_VECTORS [Lucene 3.1]";
        } else if (format == -11) {
            sFormat = "FORMAT_3_1 [Lucene 3.1+]";
        } else {
            if (format == -11) {
                throw new RuntimeException("BUG: You should update this tool!");
            }
            if (format < -11) {
                sFormat = "int=" + format + " [newer version of Lucene than this tool]";
                skip = true;
            } else {
                sFormat = format + " [Lucene 1.3 or prior]";
            }
        }
        result.segmentsFileName = segmentsFileName;
        result.numSegments = numSegments;
        result.segmentFormat = sFormat;
        result.userData = sis.getUserData();
        String userDataString = sis.getUserData().size() > 0 ? " userData=" + sis.getUserData() : "";
        String versionString = null;
        versionString = oldSegs != null ? (foundNonNullVersion ? "versions=[" + oldSegs + " .. " + newest + "]" : "version=" + oldSegs) : (oldest.equals(newest) ? "version=" + oldest : "versions=[" + oldest + " .. " + newest + "]");
        this.msg("Segments file=" + segmentsFileName + " numSegments=" + numSegments + " " + versionString + " format=" + sFormat + userDataString);
        if (onlySegments != null) {
            result.partial = true;
            if (this.infoStream != null) {
                this.infoStream.print("\nChecking only these segments:");
            }
            for (String s : onlySegments) {
                if (this.infoStream == null) continue;
                this.infoStream.print(" " + s);
            }
            result.segmentsChecked.addAll(onlySegments);
            this.msg(":");
        }
        if (skip) {
            this.msg("\nERROR: this index appears to be created by a newer version of Lucene than this tool was compiled on; please re-compile this tool on the matching version of Lucene; exiting");
            result.toolOutOfDate = true;
            return result;
        }
        result.newSegments = (SegmentInfos)sis.clone();
        result.newSegments.clear();
        result.maxSegmentName = -1;
        for (int i = 0; i < numSegments; ++i) {
            SegmentInfo info;
            block77: {
                Object var31_42;
                info = sis.info(i);
                int segmentName = Integer.parseInt(info.name.substring(1), 36);
                if (segmentName > result.maxSegmentName) {
                    result.maxSegmentName = segmentName;
                }
                if (onlySegments != null && !onlySegments.contains(info.name)) continue;
                Status.SegmentInfoStatus segInfoStat = new Status.SegmentInfoStatus();
                result.segmentInfos.add(segInfoStat);
                this.msg("  " + (1 + i) + " of " + numSegments + ": name=" + info.name + " docCount=" + info.docCount);
                segInfoStat.name = info.name;
                segInfoStat.docCount = info.docCount;
                int toLoseDocCount = info.docCount;
                IndexReader reader = null;
                try {
                    try {
                        int numDocs;
                        String delFileName;
                        int docStoreOffset;
                        this.msg("    compound=" + info.getUseCompoundFile());
                        segInfoStat.compound = info.getUseCompoundFile();
                        this.msg("    hasProx=" + info.getHasProx());
                        segInfoStat.hasProx = info.getHasProx();
                        this.msg("    numFiles=" + info.files().size());
                        segInfoStat.numFiles = info.files().size();
                        segInfoStat.sizeMB = (double)info.sizeInBytes(true) / 1048576.0;
                        this.msg("    size (MB)=" + nf.format(segInfoStat.sizeMB));
                        Map<String, String> diagnostics = info.getDiagnostics();
                        segInfoStat.diagnostics = diagnostics;
                        if (diagnostics.size() > 0) {
                            this.msg("    diagnostics = " + diagnostics);
                        }
                        if ((docStoreOffset = info.getDocStoreOffset()) != -1) {
                            this.msg("    docStoreOffset=" + docStoreOffset);
                            segInfoStat.docStoreOffset = docStoreOffset;
                            this.msg("    docStoreSegment=" + info.getDocStoreSegment());
                            segInfoStat.docStoreSegment = info.getDocStoreSegment();
                            this.msg("    docStoreIsCompoundFile=" + info.getDocStoreIsCompoundFile());
                            segInfoStat.docStoreCompoundFile = info.getDocStoreIsCompoundFile();
                        }
                        if ((delFileName = info.getDelFileName()) == null) {
                            this.msg("    no deletions");
                            segInfoStat.hasDeletions = false;
                        } else {
                            this.msg("    has deletions [delFileName=" + delFileName + "]");
                            segInfoStat.hasDeletions = true;
                            segInfoStat.deletionsFileName = delFileName;
                        }
                        if (this.infoStream != null) {
                            this.infoStream.print("    test: open reader.........");
                        }
                        reader = SegmentReader.get(true, info, IndexReader.DEFAULT_TERMS_INDEX_DIVISOR);
                        segInfoStat.openReaderPassed = true;
                        toLoseDocCount = numDocs = ((SegmentReader)reader).numDocs();
                        if (((SegmentReader)reader).hasDeletions()) {
                            if (((SegmentReader)reader).deletedDocs.count() != info.getDelCount()) {
                                throw new RuntimeException("delete count mismatch: info=" + info.getDelCount() + " vs deletedDocs.count()=" + ((SegmentReader)reader).deletedDocs.count());
                            }
                            if (((SegmentReader)reader).deletedDocs.count() > ((SegmentReader)reader).maxDoc()) {
                                throw new RuntimeException("too many deleted docs: maxDoc()=" + ((SegmentReader)reader).maxDoc() + " vs deletedDocs.count()=" + ((SegmentReader)reader).deletedDocs.count());
                            }
                            if (info.docCount - numDocs != info.getDelCount()) {
                                throw new RuntimeException("delete count mismatch: info=" + info.getDelCount() + " vs reader=" + (info.docCount - numDocs));
                            }
                            int numLive = 0;
                            for (int j = 0; j < ((SegmentReader)reader).maxDoc(); ++j) {
                                if (((SegmentReader)reader).isDeleted(j)) continue;
                                ++numLive;
                            }
                            if (numLive != numDocs) {
                                throw new RuntimeException("liveDocs count mismatch: info=" + numDocs + ", vs bits=" + numLive);
                            }
                            segInfoStat.numDeleted = info.docCount - numDocs;
                            this.msg("OK [" + segInfoStat.numDeleted + " deleted docs]");
                        } else {
                            if (info.getDelCount() != 0) {
                                throw new RuntimeException("delete count mismatch: info=" + info.getDelCount() + " vs reader=" + (info.docCount - numDocs));
                            }
                            for (int j = 0; j < ((SegmentReader)reader).maxDoc(); ++j) {
                                if (!((SegmentReader)reader).isDeleted(j)) continue;
                                throw new RuntimeException("liveDocs mismatch: info says no deletions but doc " + j + " is deleted.");
                            }
                            this.msg("OK");
                        }
                        if (((SegmentReader)reader).maxDoc() != info.docCount) {
                            throw new RuntimeException("SegmentReader.maxDoc() " + ((SegmentReader)reader).maxDoc() + " != SegmentInfos.docCount " + info.docCount);
                        }
                        if (this.infoStream != null) {
                            this.infoStream.print("    test: fields..............");
                        }
                        FieldInfos fieldInfos = ((SegmentReader)reader).getFieldInfos();
                        this.msg("OK [" + fieldInfos.size() + " fields]");
                        segInfoStat.numFields = fieldInfos.size();
                        segInfoStat.fieldNormStatus = this.testFieldNorms(fieldInfos, (SegmentReader)reader);
                        segInfoStat.termIndexStatus = this.testTermIndex(info, fieldInfos, (SegmentReader)reader);
                        segInfoStat.storedFieldStatus = this.testStoredFields(info, (SegmentReader)reader, nf);
                        segInfoStat.termVectorStatus = this.testTermVectors(info, (SegmentReader)reader, nf);
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
                        this.msg("");
                    }
                    catch (Throwable t) {
                        this.msg("FAILED");
                        String comment = "fixIndex() would remove reference to this segment";
                        this.msg("    WARNING: " + comment + "; full exception:");
                        if (this.infoStream != null) {
                            t.printStackTrace(this.infoStream);
                        }
                        this.msg("");
                        result.totLoseDocCount += toLoseDocCount;
                        ++result.numBadSegments;
                        var31_42 = null;
                        if (reader == null) continue;
                        reader.close();
                        continue;
                    }
                    var31_42 = null;
                    if (reader == null) break block77;
                }
                catch (Throwable throwable) {
                    var31_42 = null;
                    if (reader != null) {
                        reader.close();
                    }
                    throw throwable;
                }
                reader.close();
            }
            result.newSegments.add((SegmentInfo)info.clone());
        }
        if (0 == result.numBadSegments) {
            result.clean = true;
        } else {
            this.msg("WARNING: " + result.numBadSegments + " broken segments (containing " + result.totLoseDocCount + " documents) detected");
        }
        if (!(result.validCounter = result.maxSegmentName < sis.counter)) {
            result.clean = false;
            result.newSegments.counter = result.maxSegmentName + 1;
            this.msg("ERROR: Next segment name counter " + sis.counter + " is not greater than max segment name " + result.maxSegmentName);
        }
        if (result.clean) {
            this.msg("No problems were detected with this index.\n");
        }
        return result;
    }

    private Status.FieldNormStatus testFieldNorms(FieldInfos fieldInfos, SegmentReader reader) {
        Status.FieldNormStatus status;
        block4: {
            status = new Status.FieldNormStatus();
            try {
                if (this.infoStream != null) {
                    this.infoStream.print("    test: field norms.........");
                }
                byte[] b = new byte[reader.maxDoc()];
                for (FieldInfo fieldInfo : fieldInfos) {
                    if (!reader.hasNorms(fieldInfo.name)) continue;
                    reader.norms(fieldInfo.name, b, 0);
                    ++status.totFields;
                }
                this.msg("OK [" + status.totFields + " fields]");
            }
            catch (Throwable e) {
                this.msg("ERROR [" + String.valueOf(e.getMessage()) + "]");
                status.error = e;
                if (this.infoStream == null) break block4;
                e.printStackTrace(this.infoStream);
            }
        }
        return status;
    }

    private Status.TermIndexStatus testTermIndex(SegmentInfo info, FieldInfos fieldInfos, SegmentReader reader) {
        Status.TermIndexStatus status;
        block29: {
            status = new Status.TermIndexStatus();
            IndexSearcher is = new IndexSearcher(reader);
            try {
                if (this.infoStream != null) {
                    this.infoStream.print("    test: terms, freq, prox...");
                }
                TermEnum termEnum = reader.terms();
                TermPositions termPositions = reader.termPositions();
                MySegmentTermDocs myTermDocs = new MySegmentTermDocs(reader);
                int maxDoc = reader.maxDoc();
                Term lastTerm = null;
                String lastField = null;
                while (termEnum.next()) {
                    int delCount;
                    int docFreq;
                    ++status.termCount;
                    Term term = termEnum.term();
                    if (lastTerm != null && term.compareTo(lastTerm) <= 0) {
                        throw new RuntimeException("terms out of order: lastTerm=" + lastTerm + " term=" + term);
                    }
                    lastTerm = term;
                    if (term.field != lastField) {
                        FieldInfo fi = fieldInfos.fieldInfo(term.field);
                        if (fi == null) {
                            throw new RuntimeException("terms inconsistent with fieldInfos, no fieldInfos for: " + term.field);
                        }
                        if (!fi.isIndexed) {
                            throw new RuntimeException("terms inconsistent with fieldInfos, isIndexed == false for: " + term.field);
                        }
                        lastField = term.field;
                    }
                    if ((docFreq = termEnum.docFreq()) <= 0) {
                        throw new RuntimeException("docfreq: " + docFreq + " is out of bounds");
                    }
                    termPositions.seek(term);
                    int lastDoc = -1;
                    int freq0 = 0;
                    status.totFreq += (long)docFreq;
                    while (termPositions.next()) {
                        ++freq0;
                        int doc = termPositions.doc();
                        int freq = termPositions.freq();
                        if (doc <= lastDoc) {
                            throw new RuntimeException("term " + term + ": doc " + doc + " <= lastDoc " + lastDoc);
                        }
                        if (doc >= maxDoc) {
                            throw new RuntimeException("term " + term + ": doc " + doc + " >= maxDoc " + maxDoc);
                        }
                        lastDoc = doc;
                        if (freq <= 0) {
                            throw new RuntimeException("term " + term + ": doc " + doc + ": freq " + freq + " is out of bounds");
                        }
                        int lastPos = -1;
                        status.totPos += (long)freq;
                        for (int j = 0; j < freq; ++j) {
                            int pos = termPositions.nextPosition();
                            if (pos < -1) {
                                throw new RuntimeException("term " + term + ": doc " + doc + ": pos " + pos + " is out of bounds");
                            }
                            if (pos < lastPos) {
                                throw new RuntimeException("term " + term + ": doc " + doc + ": pos " + pos + " < lastPos " + lastPos);
                            }
                            lastPos = pos;
                        }
                    }
                    for (int idx = 0; idx < 7; ++idx) {
                        int skipDocID = (int)((long)(idx + 1) * (long)maxDoc / 8L);
                        termPositions.seek(term);
                        if (!termPositions.skipTo(skipDocID)) break;
                        int docID = termPositions.doc();
                        if (docID < skipDocID) {
                            throw new RuntimeException("term " + term + ": skipTo(docID=" + skipDocID + ") returned docID=" + docID);
                        }
                        int freq = termPositions.freq();
                        if (freq <= 0) {
                            throw new RuntimeException("termFreq " + freq + " is out of bounds");
                        }
                        int lastPosition = -1;
                        for (int posUpto = 0; posUpto < freq; ++posUpto) {
                            int pos = termPositions.nextPosition();
                            if (pos < -1) {
                                throw new RuntimeException("position " + pos + " is out of bounds");
                            }
                            if (pos < lastPosition) {
                                throw new RuntimeException("position " + pos + " is < lastPosition " + lastPosition);
                            }
                            lastPosition = pos;
                        }
                        if (!termPositions.next()) break;
                        int nextDocID = termPositions.doc();
                        if (nextDocID > docID) continue;
                        throw new RuntimeException("term " + term + ": skipTo(docID=" + skipDocID + "), then .next() returned docID=" + nextDocID + " vs prev docID=" + docID);
                    }
                    if (reader.hasDeletions()) {
                        myTermDocs.seek(term);
                        while (myTermDocs.next()) {
                        }
                        delCount = myTermDocs.delCount;
                    } else {
                        delCount = 0;
                    }
                    if (freq0 + delCount == docFreq) continue;
                    throw new RuntimeException("term " + term + " docFreq=" + docFreq + " != num docs seen " + freq0 + " + num docs deleted " + delCount);
                }
                if (lastTerm != null) {
                    is.search((Query)new TermQuery(lastTerm), 1);
                }
                try {
                    long uniqueTermCountAllFields = reader.getUniqueTermCount();
                    if (status.termCount != uniqueTermCountAllFields) {
                        throw new RuntimeException("termCount mismatch " + uniqueTermCountAllFields + " vs " + status.termCount);
                    }
                }
                catch (UnsupportedOperationException ex) {
                    // empty catch block
                }
                this.msg("OK [" + status.termCount + " terms; " + status.totFreq + " terms/docs pairs; " + status.totPos + " tokens]");
            }
            catch (Throwable e) {
                this.msg("ERROR [" + String.valueOf(e.getMessage()) + "]");
                status.error = e;
                if (this.infoStream == null) break block29;
                e.printStackTrace(this.infoStream);
            }
        }
        return status;
    }

    private Status.StoredFieldStatus testStoredFields(SegmentInfo info, SegmentReader reader, NumberFormat format) {
        Status.StoredFieldStatus status;
        block5: {
            status = new Status.StoredFieldStatus();
            try {
                if (this.infoStream != null) {
                    this.infoStream.print("    test: stored fields.......");
                }
                for (int j = 0; j < info.docCount; ++j) {
                    Document doc = reader.document(j);
                    if (reader.isDeleted(j)) continue;
                    ++status.docCount;
                    status.totFields += (long)doc.getFields().size();
                }
                if (status.docCount != reader.numDocs()) {
                    throw new RuntimeException("docCount=" + status.docCount + " but saw " + status.docCount + " undeleted docs");
                }
                this.msg("OK [" + status.totFields + " total field count; avg " + format.format((float)status.totFields / (float)status.docCount) + " fields per doc]");
            }
            catch (Throwable e) {
                this.msg("ERROR [" + String.valueOf(e.getMessage()) + "]");
                status.error = e;
                if (this.infoStream == null) break block5;
                e.printStackTrace(this.infoStream);
            }
        }
        return status;
    }

    private Status.TermVectorStatus testTermVectors(SegmentInfo info, SegmentReader reader, NumberFormat format) {
        Status.TermVectorStatus status;
        block4: {
            status = new Status.TermVectorStatus();
            try {
                if (this.infoStream != null) {
                    this.infoStream.print("    test: term vectors........");
                }
                for (int j = 0; j < info.docCount; ++j) {
                    if (reader.isDeleted(j)) continue;
                    ++status.docCount;
                    TermFreqVector[] tfv = reader.getTermFreqVectors(j);
                    if (tfv == null) continue;
                    status.totVectors += (long)tfv.length;
                }
                this.msg("OK [" + status.totVectors + " total vector count; avg " + format.format((float)status.totVectors / (float)status.docCount) + " term/freq vector fields per doc]");
            }
            catch (Throwable e) {
                this.msg("ERROR [" + String.valueOf(e.getMessage()) + "]");
                status.error = e;
                if (this.infoStream == null) break block4;
                e.printStackTrace(this.infoStream);
            }
        }
        return status;
    }

    public void fixIndex(Status result) throws IOException {
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
        ArrayList<String> onlySegments = new ArrayList<String>();
        String indexPath = null;
        String dirImpl = null;
        for (int i = 0; i < args.length; ++i) {
            String arg = args[i];
            if ("-fix".equals(arg)) {
                doFix = true;
                ++i;
                continue;
            }
            if (args[i].equals("-segment")) {
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
            System.out.println("\nUsage: java org.apache.lucene.index.CheckIndex pathToIndex [-fix] [-segment X] [-segment Y] [-dir-impl X]\n\n  -fix: actually write a new segments_N file, removing any problematic segments\n  -segment X: only check the specified segments.  This can be specified multiple\n              times, to check more than one segment, eg '-segment _2 -segment _a'.\n              You can't use this with the -fix option\n  -dir-impl X: use a specific " + FSDirectory.class.getSimpleName() + " implementation. " + "If no package is specified the " + FSDirectory.class.getPackage().getName() + " package will be used.\n" + "**WARNING**: -fix should only be used on an emergency basis as it will cause\n" + "documents (perhaps many) to be permanently removed from the index.  Always make\n" + "a backup copy of your index before running this!  Do not run this tool on an index\n" + "that is actively being written to.  You have been warned!\n" + "\n" + "Run without -fix, this tool will open the index, report version information\n" + "and report any exceptions it hits and what action it would take if -fix were\n" + "specified.  With -fix, this tool will remove any segments that have issues and\n" + "write a new segments_N file.  This means all documents contained in the affected\n" + "segments will be removed.\n" + "\n" + "This tool exits with exit code 1 if the index cannot be opened or has any\n" + "corruption, else 0.\n");
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
        checker.setInfoStream(System.out);
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
                checker.fixIndex(result);
                System.out.println("OK");
                System.out.println("Wrote new segments file \"" + result.newSegments.getSegmentsFileName() + "\"");
            }
        }
        System.out.println("");
        int exitCode = result.clean ? 0 : 1;
        System.exit(exitCode);
    }

    private static class MySegmentTermDocs
    extends SegmentTermDocs {
        int delCount;

        MySegmentTermDocs(SegmentReader p) {
            super(p);
        }

        public void seek(Term term) throws IOException {
            super.seek(term);
            this.delCount = 0;
        }

        protected void skippingDoc() throws IOException {
            ++this.delCount;
        }
    }

    public static class Status {
        public boolean clean;
        public boolean missingSegments;
        public boolean cantOpenSegments;
        public boolean missingSegmentVersion;
        public String segmentsFileName;
        public int numSegments;
        public String segmentFormat;
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

        public static final class TermVectorStatus {
            public int docCount = 0;
            public long totVectors = 0L;
            public Throwable error = null;
        }

        public static final class StoredFieldStatus {
            public int docCount = 0;
            public long totFields = 0L;
            public Throwable error = null;
        }

        public static final class TermIndexStatus {
            public long termCount = 0L;
            public long totFreq = 0L;
            public long totPos = 0L;
            public Throwable error = null;
        }

        public static final class FieldNormStatus {
            public long totFields = 0L;
            public Throwable error = null;
        }

        public static class SegmentInfoStatus {
            public String name;
            public int docCount;
            public boolean compound;
            public int numFiles;
            public double sizeMB;
            public int docStoreOffset = -1;
            public String docStoreSegment;
            public boolean docStoreCompoundFile;
            public boolean hasDeletions;
            public String deletionsFileName;
            public int numDeleted;
            public boolean openReaderPassed;
            int numFields;
            public boolean hasProx;
            public Map<String, String> diagnostics;
            public FieldNormStatus fieldNormStatus;
            public TermIndexStatus termIndexStatus;
            public StoredFieldStatus storedFieldStatus;
            public TermVectorStatus termVectorStatus;
        }
    }
}

