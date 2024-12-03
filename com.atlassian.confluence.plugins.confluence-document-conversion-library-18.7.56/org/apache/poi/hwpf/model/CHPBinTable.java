/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hwpf.model.CHPFormattedDiskPage;
import org.apache.poi.hwpf.model.CHPX;
import org.apache.poi.hwpf.model.CharIndexTranslator;
import org.apache.poi.hwpf.model.ComplexFileTable;
import org.apache.poi.hwpf.model.GenericPropertyNode;
import org.apache.poi.hwpf.model.PlexOfCps;
import org.apache.poi.hwpf.model.PropertyModifier;
import org.apache.poi.hwpf.model.PropertyNode;
import org.apache.poi.hwpf.model.TextPiece;
import org.apache.poi.hwpf.model.TextPieceTable;
import org.apache.poi.hwpf.model.io.HWPFFileSystem;
import org.apache.poi.hwpf.sprm.SprmBuffer;
import org.apache.poi.hwpf.sprm.SprmIterator;
import org.apache.poi.hwpf.sprm.SprmOperation;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public class CHPBinTable {
    private static final Logger LOG = LogManager.getLogger(CHPBinTable.class);
    protected List<CHPX> _textRuns = new ArrayList<CHPX>();

    public CHPBinTable() {
    }

    public CHPBinTable(byte[] documentStream, byte[] tableStream, int offset, int size, int fcMin, TextPieceTable tpt) {
        this(documentStream, tableStream, offset, size, tpt);
    }

    public CHPBinTable(byte[] documentStream, byte[] tableStream, int offset, int size, CharIndexTranslator translator) {
        long start = System.currentTimeMillis();
        PlexOfCps bte = new PlexOfCps(tableStream, offset, size, 4);
        int length = bte.length();
        for (int x = 0; x < length; ++x) {
            GenericPropertyNode node = bte.getProperty(x);
            int pageNum = LittleEndian.getInt(node.getBytes());
            int pageOffset = 512 * pageNum;
            CHPFormattedDiskPage cfkp = new CHPFormattedDiskPage(documentStream, pageOffset, translator);
            for (CHPX chpx : cfkp.getCHPXs()) {
                if (chpx == null) continue;
                this._textRuns.add(chpx);
            }
        }
        LOG.atDebug().log("CHPX FKPs loaded in {} ms ({} elements)", (Object)Unbox.box(System.currentTimeMillis() - start), (Object)Unbox.box(this._textRuns.size()));
        if (this._textRuns.isEmpty()) {
            LOG.atWarn().log("CHPX FKPs are empty");
            this._textRuns.add(new CHPX(0, 0, new SprmBuffer(0)));
        }
    }

    /*
     * WARNING - void declaration
     */
    public void rebuild(ComplexFileTable complexFileTable) {
        Object iterator;
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
                boolean hasChp = false;
                SprmBuffer sprmBuffer = sprmBuffers[igrpprl];
                iterator = sprmBuffer.iterator();
                while (((SprmIterator)iterator).hasNext()) {
                    SprmOperation sprmOperation = ((SprmIterator)iterator).next();
                    if (sprmOperation.getType() != 2) continue;
                    hasChp = true;
                    break;
                }
                if (!hasChp) continue;
                SprmBuffer newSprmBuffer = sprmBuffer.copy();
                CHPX chpx = new CHPX(textPiece.getStart(), textPiece.getEnd(), newSprmBuffer);
                this._textRuns.add(chpx);
            }
            LOG.atDebug().log("Merged with CHPX from complex file table in {} ms ({} elements in total)", (Object)Unbox.box(System.currentTimeMillis() - start), (Object)Unbox.box(this._textRuns.size()));
            start = System.currentTimeMillis();
        }
        ArrayList<CHPX> oldChpxSortedByStartPos = new ArrayList<CHPX>(this._textRuns);
        oldChpxSortedByStartPos.sort(PropertyNode.StartComparator);
        LOG.atDebug().log("CHPX sorted by start position in {} ms", (Object)Unbox.box(System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
        IdentityHashMap<CHPX, Integer> chpxToFileOrder = new IdentityHashMap<CHPX, Integer>();
        int counter = 0;
        for (CHPX chpx : this._textRuns) {
            chpxToFileOrder.put(chpx, counter++);
        }
        Comparator chpxFileOrderComparator = (o1, o2) -> {
            Integer i1 = (Integer)chpxToFileOrder.get(o1);
            Integer i2 = (Integer)chpxToFileOrder.get(o2);
            return i1.compareTo(i2);
        };
        LOG.atDebug().log("CHPX's order map created in {} ms", (Object)Unbox.box(System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
        HashSet<Integer> textRunsBoundariesSet = new HashSet<Integer>();
        for (CHPX cHPX : this._textRuns) {
            textRunsBoundariesSet.add(cHPX.getStart());
            textRunsBoundariesSet.add(cHPX.getEnd());
        }
        textRunsBoundariesSet.remove(0);
        ArrayList textRunsBoundariesList = new ArrayList(textRunsBoundariesSet);
        Collections.sort(textRunsBoundariesList);
        LOG.atDebug().log("Texts CHPX boundaries collected in {} ms", (Object)Unbox.box(System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
        LinkedList<CHPX> newChpxs = new LinkedList<CHPX>();
        int lastTextRunStart = 0;
        for (Integer objBoundary : textRunsBoundariesList) {
            CHPX existing;
            Object chpx;
            int boundary = objBoundary;
            int startInclusive = lastTextRunStart;
            lastTextRunStart = boundary;
            int startPosition = CHPBinTable.binarySearch(oldChpxSortedByStartPos, boundary);
            for (startPosition = Math.abs(startPosition); startPosition >= oldChpxSortedByStartPos.size(); --startPosition) {
            }
            while (startPosition > 0 && ((CHPX)oldChpxSortedByStartPos.get(startPosition)).getStart() >= boundary) {
                --startPosition;
            }
            LinkedList<Object> chpxs = new LinkedList<Object>();
            for (int c = startPosition; c < oldChpxSortedByStartPos.size() && boundary >= ((PropertyNode)(chpx = (CHPX)oldChpxSortedByStartPos.get(c))).getStart(); ++c) {
                int right;
                int left = Math.max(startInclusive, ((PropertyNode)chpx).getStart());
                if (left >= (right = Math.min(boundary, ((PropertyNode)chpx).getEnd()))) continue;
                chpxs.add(chpx);
            }
            if (chpxs.isEmpty()) {
                LOG.atWarn().log("Text piece [{}; {}) has no CHPX. Creating new one.", (Object)Unbox.box(startInclusive), (Object)Unbox.box(boundary));
                CHPX chpx2 = new CHPX(startInclusive, boundary, new SprmBuffer(0));
                newChpxs.add(chpx2);
                continue;
            }
            if (chpxs.size() == 1 && (existing = (CHPX)chpxs.get(0)).getStart() == startInclusive && existing.getEnd() == boundary) {
                newChpxs.add(existing);
                continue;
            }
            chpxs.sort(chpxFileOrderComparator);
            SprmBuffer sprmBuffer = new SprmBuffer(0);
            chpx = chpxs.iterator();
            while (chpx.hasNext()) {
                CHPX chpx3 = (CHPX)chpx.next();
                sprmBuffer.append(chpx3.getGrpprl(), 0);
            }
            CHPX newChpx = new CHPX(startInclusive, boundary, sprmBuffer);
            newChpxs.add(newChpx);
        }
        this._textRuns = new ArrayList<CHPX>(newChpxs);
        LOG.atDebug().log("CHPX rebuilt in {} ms ({} elements)", (Object)Unbox.box(System.currentTimeMillis() - start), (Object)Unbox.box(this._textRuns.size()));
        start = System.currentTimeMillis();
        Object var10_19 = null;
        iterator = this._textRuns.iterator();
        while (iterator.hasNext()) {
            void var10_20;
            CHPX current = (CHPX)iterator.next();
            if (var10_20 == null) {
                CHPX cHPX = current;
                continue;
            }
            if (var10_20.getEnd() == current.getStart() && Arrays.equals(var10_20.getGrpprl(), current.getGrpprl())) {
                var10_20.setEnd(current.getEnd());
                iterator.remove();
                continue;
            }
            CHPX cHPX = current;
        }
        LOG.atDebug().log("CHPX compacted in {} ms ({} elements)", (Object)Unbox.box(System.currentTimeMillis() - start), (Object)Unbox.box(this._textRuns.size()));
    }

    private static int binarySearch(List<CHPX> chpxs, int startPosition) {
        int low = 0;
        int high = chpxs.size() - 1;
        while (low <= high) {
            int mid = low + high >>> 1;
            CHPX midVal = chpxs.get(mid);
            int midValue = midVal.getStart();
            if (midValue < startPosition) {
                low = mid + 1;
                continue;
            }
            if (midValue > startPosition) {
                high = mid - 1;
                continue;
            }
            return mid;
        }
        return -(low + 1);
    }

    public void adjustForDelete(int listIndex, int offset, int length) {
        int x;
        int size = this._textRuns.size();
        int endMark = offset + length;
        int endIndex = listIndex;
        CHPX chpx = this._textRuns.get(endIndex);
        while (chpx.getEnd() < endMark) {
            chpx = this._textRuns.get(++endIndex);
        }
        if (listIndex == endIndex) {
            chpx = this._textRuns.get(endIndex);
            chpx.setEnd(chpx.getEnd() - endMark + offset);
        } else {
            chpx = this._textRuns.get(listIndex);
            chpx.setEnd(offset);
            for (x = listIndex + 1; x < endIndex; ++x) {
                chpx = this._textRuns.get(x);
                chpx.setStart(offset);
                chpx.setEnd(offset);
            }
            chpx = this._textRuns.get(endIndex);
            chpx.setEnd(chpx.getEnd() - endMark + offset);
        }
        for (x = endIndex + 1; x < size; ++x) {
            chpx = this._textRuns.get(x);
            chpx.setStart(chpx.getStart() - length);
            chpx.setEnd(chpx.getEnd() - length);
        }
    }

    public void insert(int listIndex, int cpStart, SprmBuffer buf) {
        CHPX insertChpx = new CHPX(0, 0, buf);
        insertChpx.setStart(cpStart);
        insertChpx.setEnd(cpStart);
        if (listIndex == this._textRuns.size()) {
            this._textRuns.add(insertChpx);
        } else {
            CHPX chpx = this._textRuns.get(listIndex);
            if (chpx.getStart() < cpStart) {
                CHPX clone = new CHPX(0, 0, chpx.getSprmBuf());
                clone.setStart(cpStart);
                clone.setEnd(chpx.getEnd());
                chpx.setEnd(cpStart);
                this._textRuns.add(listIndex + 1, insertChpx);
                this._textRuns.add(listIndex + 2, clone);
            } else {
                this._textRuns.add(listIndex, insertChpx);
            }
        }
    }

    public void adjustForInsert(int listIndex, int length) {
        int size = this._textRuns.size();
        CHPX chpx = this._textRuns.get(listIndex);
        chpx.setEnd(chpx.getEnd() + length);
        for (int x = listIndex + 1; x < size; ++x) {
            chpx = this._textRuns.get(x);
            chpx.setStart(chpx.getStart() + length);
            chpx.setEnd(chpx.getEnd() + length);
        }
    }

    public List<CHPX> getTextRuns() {
        return this._textRuns;
    }

    @Deprecated
    public void writeTo(HWPFFileSystem sys, int fcMin, CharIndexTranslator translator) throws IOException {
        ByteArrayOutputStream docStream = sys.getStream("WordDocument");
        ByteArrayOutputStream tableStream = sys.getStream("1Table");
        this.writeTo(docStream, tableStream, fcMin, translator);
    }

    public void writeTo(ByteArrayOutputStream wordDocumentStream, ByteArrayOutputStream tableStream, int fcMin, CharIndexTranslator translator) throws IOException {
        PlexOfCps bte = new PlexOfCps(4);
        int docOffset = wordDocumentStream.size();
        int mod = docOffset % 512;
        if (mod != 0) {
            byte[] padding = new byte[512 - mod];
            wordDocumentStream.write(padding);
        }
        docOffset = wordDocumentStream.size();
        int pageNum = docOffset / 512;
        int endingFc = translator.getByteIndex(this._textRuns.get(this._textRuns.size() - 1).getEnd());
        List<CHPX> overflow = this._textRuns;
        do {
            CHPX startingProp = overflow.get(0);
            int start = translator.getByteIndex(startingProp.getStart());
            CHPFormattedDiskPage cfkp = new CHPFormattedDiskPage();
            cfkp.fill(overflow);
            byte[] bufFkp = cfkp.toByteArray(translator);
            wordDocumentStream.write(bufFkp);
            overflow = cfkp.getOverflow();
            int end = endingFc;
            if (overflow != null) {
                end = translator.getByteIndex(overflow.get(0).getStart());
            }
            byte[] intHolder = new byte[4];
            LittleEndian.putInt(intHolder, 0, pageNum++);
            bte.addProperty(new GenericPropertyNode(start, end, intHolder));
        } while (overflow != null);
        tableStream.write(bte.toByteArray());
    }
}

