/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hwpf.model.CharIndexTranslator;
import org.apache.poi.hwpf.model.ComplexFileTable;
import org.apache.poi.hwpf.model.GenericPropertyNode;
import org.apache.poi.hwpf.model.PAPFormattedDiskPage;
import org.apache.poi.hwpf.model.PAPX;
import org.apache.poi.hwpf.model.PlexOfCps;
import org.apache.poi.hwpf.model.PropertyModifier;
import org.apache.poi.hwpf.model.PropertyNode;
import org.apache.poi.hwpf.model.TextPiece;
import org.apache.poi.hwpf.sprm.SprmBuffer;
import org.apache.poi.hwpf.sprm.SprmIterator;
import org.apache.poi.hwpf.sprm.SprmOperation;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public class PAPBinTable {
    private static final Logger LOG = LogManager.getLogger(PAPBinTable.class);
    protected final ArrayList<PAPX> _paragraphs = new ArrayList();

    public PAPBinTable() {
    }

    public PAPBinTable(byte[] documentStream, byte[] tableStream, byte[] dataStream, int offset, int size, CharIndexTranslator charIndexTranslator) {
        long start = System.currentTimeMillis();
        PlexOfCps binTable = new PlexOfCps(tableStream, offset, size, 4);
        int length = binTable.length();
        for (int x = 0; x < length; ++x) {
            GenericPropertyNode node = binTable.getProperty(x);
            int pageNum = LittleEndian.getInt(node.getBytes());
            int pageOffset = 512 * pageNum;
            PAPFormattedDiskPage pfkp = new PAPFormattedDiskPage(documentStream, dataStream, pageOffset, charIndexTranslator);
            for (PAPX papx : pfkp.getPAPXs()) {
                if (papx == null) continue;
                this._paragraphs.add(papx);
            }
        }
        LOG.atDebug().log("PAPX tables loaded in {} ms ({} elements)", (Object)Unbox.box(System.currentTimeMillis() - start), (Object)Unbox.box(this._paragraphs.size()));
        if (this._paragraphs.isEmpty()) {
            LOG.atWarn().log("PAPX FKPs are empty");
            this._paragraphs.add(new PAPX(0, 0, new SprmBuffer(2)));
        }
    }

    public void rebuild(StringBuilder docText, ComplexFileTable complexFileTable) {
        PAPBinTable.rebuild(docText, complexFileTable, this._paragraphs);
    }

    static void rebuild(StringBuilder docText, ComplexFileTable complexFileTable, List<PAPX> paragraphs) {
        long start = System.currentTimeMillis();
        if (complexFileTable != null) {
            SprmBuffer[] sprmBuffers = complexFileTable.getGrpprls();
            for (TextPiece textPiece : complexFileTable.getTextPieceTable().getTextPieces()) {
                PropertyModifier prm = textPiece.getPieceDescriptor().getPrm();
                if (!prm.isComplex()) continue;
                short igrpprl = prm.getIgrpprl();
                if (igrpprl < 0 || igrpprl >= sprmBuffers.length) {
                    LOG.atWarn().log("{}'s PRM references to unknown grpprl", (Object)textPiece);
                    continue;
                }
                boolean hasPap = false;
                SprmBuffer sprmBuffer = sprmBuffers[igrpprl];
                SprmIterator iterator = sprmBuffer.iterator();
                while (iterator.hasNext()) {
                    SprmOperation sprmOperation = iterator.next();
                    if (sprmOperation.getType() != 1) continue;
                    hasPap = true;
                    break;
                }
                if (!hasPap) continue;
                SprmBuffer newSprmBuffer = new SprmBuffer(2);
                newSprmBuffer.append(sprmBuffer.toByteArray());
                PAPX papx = new PAPX(textPiece.getStart(), textPiece.getEnd(), newSprmBuffer);
                paragraphs.add(papx);
            }
            LOG.atDebug().log("Merged (?) with PAPX from complex file table in {} ms ({} elements in total)", (Object)Unbox.box(System.currentTimeMillis() - start), (Object)Unbox.box(paragraphs.size()));
            start = System.currentTimeMillis();
        }
        ArrayList<PAPX> oldPapxSortedByEndPos = new ArrayList<PAPX>(paragraphs);
        oldPapxSortedByEndPos.sort(PropertyNode.EndComparator);
        LOG.atDebug().log("PAPX sorted by end position in {} ms", (Object)Unbox.box(System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
        IdentityHashMap<PAPX, Integer> papxToFileOrder = new IdentityHashMap<PAPX, Integer>();
        int counter = 0;
        for (PAPX papx : paragraphs) {
            papxToFileOrder.put(papx, counter++);
        }
        Comparator papxFileOrderComparator = (o1, o2) -> {
            Integer i1 = (Integer)papxToFileOrder.get(o1);
            Integer i2 = (Integer)papxToFileOrder.get(o2);
            return i1.compareTo(i2);
        };
        LOG.atDebug().log("PAPX's order map created in {} ms", (Object)Unbox.box(System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
        LinkedList<PAPX> newPapxs = new LinkedList<PAPX>();
        int lastParStart = 0;
        int lastPapxIndex = 0;
        for (int charIndex = 0; charIndex < docText.length(); ++charIndex) {
            PAPX existing;
            char c = docText.charAt(charIndex);
            if (c != '\r' && c != '\u0007' && c != '\f') continue;
            int startInclusive = lastParStart;
            int endExclusive = charIndex + 1;
            boolean broken = false;
            LinkedList<PAPX> papxs = new LinkedList<PAPX>();
            for (int papxIndex = lastPapxIndex; papxIndex < oldPapxSortedByEndPos.size(); ++papxIndex) {
                broken = false;
                PAPX papx = (PAPX)oldPapxSortedByEndPos.get(papxIndex);
                assert (startInclusive == 0 || papxIndex + 1 == oldPapxSortedByEndPos.size() || papx.getEnd() > startInclusive);
                if (papx.getEnd() - 1 > charIndex) {
                    lastPapxIndex = papxIndex;
                    broken = true;
                    break;
                }
                papxs.add(papx);
            }
            if (!broken) {
                lastPapxIndex = oldPapxSortedByEndPos.size() - 1;
            }
            if (papxs.isEmpty()) {
                LOG.atWarn().log("Paragraph [{}; {}) has no PAPX. Creating new one.", (Object)Unbox.box(startInclusive), (Object)Unbox.box(endExclusive));
                PAPX papx = new PAPX(startInclusive, endExclusive, new SprmBuffer(2));
                newPapxs.add(papx);
                lastParStart = endExclusive;
                continue;
            }
            if (papxs.size() == 1 && (existing = (PAPX)papxs.get(0)).getStart() == startInclusive && existing.getEnd() == endExclusive) {
                newPapxs.add(existing);
                lastParStart = endExclusive;
                continue;
            }
            papxs.sort(papxFileOrderComparator);
            SprmBuffer sprmBuffer = null;
            for (PAPX papx : papxs) {
                if (papx.getGrpprl() == null || papx.getGrpprl().length <= 2) continue;
                if (sprmBuffer == null) {
                    sprmBuffer = papx.getSprmBuf().copy();
                    continue;
                }
                sprmBuffer.append(papx.getGrpprl(), 2);
            }
            PAPX newPapx = new PAPX(startInclusive, endExclusive, sprmBuffer);
            newPapxs.add(newPapx);
            lastParStart = endExclusive;
        }
        paragraphs.clear();
        paragraphs.addAll(newPapxs);
        LOG.atDebug().log("PAPX rebuilded from document text in {} ms ({} elements)", (Object)Unbox.box(System.currentTimeMillis() - start), (Object)Unbox.box(paragraphs.size()));
    }

    public void insert(int listIndex, int cpStart, SprmBuffer buf) {
        PAPX forInsert = new PAPX(0, 0, buf);
        forInsert.setStart(cpStart);
        forInsert.setEnd(cpStart);
        if (listIndex == this._paragraphs.size()) {
            this._paragraphs.add(forInsert);
        } else {
            PAPX currentPap = this._paragraphs.get(listIndex);
            if (currentPap != null && currentPap.getStart() < cpStart) {
                SprmBuffer clonedBuf = currentPap.getSprmBuf().copy();
                PAPX clone = new PAPX(0, 0, clonedBuf);
                clone.setStart(cpStart);
                clone.setEnd(currentPap.getEnd());
                currentPap.setEnd(cpStart);
                this._paragraphs.add(listIndex + 1, forInsert);
                this._paragraphs.add(listIndex + 2, clone);
            } else {
                this._paragraphs.add(listIndex, forInsert);
            }
        }
    }

    public void adjustForDelete(int listIndex, int offset, int length) {
        int x;
        int size = this._paragraphs.size();
        int endMark = offset + length;
        int endIndex = listIndex;
        PAPX papx = this._paragraphs.get(endIndex);
        while (papx.getEnd() < endMark) {
            papx = this._paragraphs.get(++endIndex);
        }
        if (listIndex == endIndex) {
            papx = this._paragraphs.get(endIndex);
            papx.setEnd(papx.getEnd() - endMark + offset);
        } else {
            papx = this._paragraphs.get(listIndex);
            papx.setEnd(offset);
            for (x = listIndex + 1; x < endIndex; ++x) {
                papx = this._paragraphs.get(x);
                papx.setStart(offset);
                papx.setEnd(offset);
            }
            papx = this._paragraphs.get(endIndex);
            papx.setEnd(papx.getEnd() - endMark + offset);
        }
        for (x = endIndex + 1; x < size; ++x) {
            papx = this._paragraphs.get(x);
            papx.setStart(papx.getStart() - length);
            papx.setEnd(papx.getEnd() - length);
        }
    }

    public void adjustForInsert(int listIndex, int length) {
        int size = this._paragraphs.size();
        PAPX papx = this._paragraphs.get(listIndex);
        papx.setEnd(papx.getEnd() + length);
        for (int x = listIndex + 1; x < size; ++x) {
            papx = this._paragraphs.get(x);
            papx.setStart(papx.getStart() + length);
            papx.setEnd(papx.getEnd() + length);
        }
    }

    public ArrayList<PAPX> getParagraphs() {
        return this._paragraphs;
    }

    public void writeTo(ByteArrayOutputStream wordDocumentStream, ByteArrayOutputStream tableStream, CharIndexTranslator translator) throws IOException {
        PlexOfCps binTable = new PlexOfCps(4);
        int docOffset = wordDocumentStream.size();
        int mod = docOffset % 512;
        if (mod != 0) {
            byte[] padding = new byte[512 - mod];
            wordDocumentStream.write(padding);
        }
        docOffset = wordDocumentStream.size();
        int pageNum = docOffset / 512;
        int endingFc = translator.getByteIndex(this._paragraphs.get(this._paragraphs.size() - 1).getEnd());
        ArrayList<PAPX> overflow = this._paragraphs;
        do {
            PAPX startingProp = overflow.get(0);
            int start = translator.getByteIndex(startingProp.getStart());
            PAPFormattedDiskPage pfkp = new PAPFormattedDiskPage();
            pfkp.fill(overflow);
            byte[] bufFkp = pfkp.toByteArray(tableStream, translator);
            wordDocumentStream.write(bufFkp);
            overflow = pfkp.getOverflow();
            int end = endingFc;
            if (overflow != null) {
                end = translator.getByteIndex(overflow.get(0).getStart());
            }
            byte[] intHolder = new byte[4];
            LittleEndian.putInt(intHolder, 0, pageNum++);
            binTable.addProperty(new GenericPropertyNode(start, end, intHolder));
        } while (overflow != null);
        tableStream.write(binTable.toByteArray());
    }
}

