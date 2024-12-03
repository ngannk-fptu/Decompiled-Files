/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hwpf.model.CharIndexTranslator;
import org.apache.poi.hwpf.model.GenericPropertyNode;
import org.apache.poi.hwpf.model.PieceDescriptor;
import org.apache.poi.hwpf.model.PlexOfCps;
import org.apache.poi.hwpf.model.TextPiece;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;

@Internal
public class TextPieceTable
implements CharIndexTranslator {
    private static final Logger LOG = LogManager.getLogger(TextPieceTable.class);
    private static final int DEFAULT_MAX_RECORD_LENGTH = 100000000;
    private static int MAX_RECORD_LENGTH = 100000000;
    int _cpMin;
    protected ArrayList<TextPiece> _textPieces = new ArrayList();
    protected ArrayList<TextPiece> _textPiecesFCOrder = new ArrayList();

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    public TextPieceTable() {
    }

    public TextPieceTable(byte[] documentStream, byte[] tableStream, int offset, int size, int fcMin) {
        PlexOfCps pieceTable = new PlexOfCps(tableStream, offset, size, PieceDescriptor.getSizeInBytes());
        int length = pieceTable.length();
        PieceDescriptor[] pieces = new PieceDescriptor[length];
        for (int x = 0; x < length; ++x) {
            GenericPropertyNode node = pieceTable.getProperty(x);
            pieces[x] = new PieceDescriptor(node.getBytes(), 0);
        }
        this._cpMin = pieces[0].getFilePosition() - fcMin;
        for (PieceDescriptor piece : pieces) {
            int start = piece.getFilePosition() - fcMin;
            if (start >= this._cpMin) continue;
            this._cpMin = start;
        }
        for (int x = 0; x < pieces.length; ++x) {
            int start = pieces[x].getFilePosition();
            GenericPropertyNode node = pieceTable.getProperty(x);
            int nodeStartChars = node.getStart();
            int nodeEndChars = node.getEnd();
            boolean unicode = pieces[x].isUnicode();
            int multiple = 1;
            if (unicode) {
                multiple = 2;
            }
            int textSizeChars = nodeEndChars - nodeStartChars;
            int textSizeBytes = textSizeChars * multiple;
            byte[] buf = IOUtils.safelyClone(documentStream, start, textSizeBytes, MAX_RECORD_LENGTH);
            TextPiece newTextPiece = this.newTextPiece(nodeStartChars, nodeEndChars, buf, pieces[x]);
            this._textPieces.add(newTextPiece);
        }
        Collections.sort(this._textPieces);
        this._textPiecesFCOrder = new ArrayList<TextPiece>(this._textPieces);
        this._textPiecesFCOrder.sort(TextPieceTable.byFilePosition());
    }

    protected TextPiece newTextPiece(int nodeStartChars, int nodeEndChars, byte[] buf, PieceDescriptor pd) {
        return new TextPiece(nodeStartChars, nodeEndChars, buf, pd);
    }

    public void add(TextPiece piece) {
        this._textPieces.add(piece);
        this._textPiecesFCOrder.add(piece);
        Collections.sort(this._textPieces);
        this._textPiecesFCOrder.sort(TextPieceTable.byFilePosition());
    }

    public int adjustForInsert(int listIndex, int length) {
        int size = this._textPieces.size();
        TextPiece tp = this._textPieces.get(listIndex);
        tp.setEnd(tp.getEnd() + length);
        for (int x = listIndex + 1; x < size; ++x) {
            tp = this._textPieces.get(x);
            tp.setStart(tp.getStart() + length);
            tp.setEnd(tp.getEnd() + length);
        }
        return length;
    }

    public boolean equals(Object o) {
        if (!(o instanceof TextPieceTable)) {
            return false;
        }
        TextPieceTable tpt = (TextPieceTable)o;
        int size = tpt._textPieces.size();
        if (size == this._textPieces.size()) {
            for (int x = 0; x < size; ++x) {
                if (tpt._textPieces.get(x).equals(this._textPieces.get(x))) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public int getByteIndex(int charPos) {
        int byteCount = 0;
        for (TextPiece tp : this._textPieces) {
            if (charPos >= tp.getEnd()) {
                byteCount = tp.getPieceDescriptor().getFilePosition() + (tp.getEnd() - tp.getStart()) * (tp.isUnicode() ? 2 : 1);
                if (charPos != tp.getEnd()) continue;
                break;
            }
            if (charPos >= tp.getEnd()) continue;
            int left = charPos - tp.getStart();
            byteCount = tp.getPieceDescriptor().getFilePosition() + left * (tp.isUnicode() ? 2 : 1);
            break;
        }
        return byteCount;
    }

    @Deprecated
    public int getCharIndex(int bytePos) {
        return this.getCharIndex(bytePos, 0);
    }

    @Deprecated
    public int getCharIndex(int startBytePos, int startCP) {
        int charCount = 0;
        int bytePos = this.lookIndexForward(startBytePos);
        for (TextPiece tp : this._textPieces) {
            int pieceStart = tp.getPieceDescriptor().getFilePosition();
            int bytesLength = tp.bytesLength();
            int pieceEnd = pieceStart + bytesLength;
            int toAdd = bytePos < pieceStart || bytePos > pieceEnd ? bytesLength : (bytePos > pieceStart && bytePos < pieceEnd ? bytePos - pieceStart : bytesLength - (pieceEnd - bytePos));
            charCount = tp.isUnicode() ? (charCount += toAdd / 2) : (charCount += toAdd);
            if (bytePos < pieceStart || bytePos > pieceEnd || charCount < startCP) continue;
            break;
        }
        return charCount;
    }

    @Override
    public int[][] getCharIndexRanges(int startBytePosInclusive, int endBytePosExclusive) {
        TextPiece textPiece;
        int tpStart;
        LinkedList<int[]> result = new LinkedList<int[]>();
        Iterator<TextPiece> iterator = this._textPiecesFCOrder.iterator();
        while (iterator.hasNext() && endBytePosExclusive > (tpStart = (textPiece = iterator.next()).getPieceDescriptor().getFilePosition())) {
            int rangeEndBytes;
            int rangeStartBytes;
            int tpEnd = textPiece.getPieceDescriptor().getFilePosition() + textPiece.bytesLength();
            if (startBytePosInclusive > tpEnd || (rangeStartBytes = Math.max(tpStart, startBytePosInclusive)) > (rangeEndBytes = Math.min(tpEnd, endBytePosExclusive))) continue;
            int encodingMultiplier = this.getEncodingMultiplier(textPiece);
            int rangeStartCp = textPiece.getStart() + (rangeStartBytes - tpStart) / encodingMultiplier;
            int rangeLengthBytes = rangeEndBytes - rangeStartBytes;
            int rangeEndCp = rangeStartCp + rangeLengthBytes / encodingMultiplier;
            result.add(new int[]{rangeStartCp, rangeEndCp});
        }
        return (int[][])result.toArray((T[])new int[result.size()][]);
    }

    protected int getEncodingMultiplier(TextPiece textPiece) {
        return textPiece.isUnicode() ? 2 : 1;
    }

    public int getCpMin() {
        return this._cpMin;
    }

    public StringBuilder getText() {
        long start = System.currentTimeMillis();
        StringBuilder docText = new StringBuilder();
        for (TextPiece textPiece : this._textPieces) {
            String toAppend = textPiece.getStringBuilder().toString();
            int toAppendLength = toAppend.length();
            if (toAppendLength != textPiece.getEnd() - textPiece.getStart()) {
                LOG.atWarn().log("Text piece has boundaries [{}; {}) but length {}", (Object)Unbox.box(textPiece.getStart()), (Object)Unbox.box(textPiece.getEnd()), (Object)Unbox.box(textPiece.getEnd() - textPiece.getStart()));
            }
            docText.replace(textPiece.getStart(), textPiece.getStart() + toAppendLength, toAppend);
        }
        LOG.atDebug().log("Document text were rebuilt in {} ms ({} chars)", (Object)Unbox.box(System.currentTimeMillis() - start), (Object)Unbox.box(docText.length()));
        return docText;
    }

    public List<TextPiece> getTextPieces() {
        return this._textPieces;
    }

    public int hashCode() {
        return this._textPieces.hashCode();
    }

    @Override
    public boolean isIndexInTable(int bytePos) {
        for (TextPiece tp : this._textPiecesFCOrder) {
            int pieceStart = tp.getPieceDescriptor().getFilePosition();
            if (bytePos > pieceStart + tp.bytesLength()) continue;
            return pieceStart <= bytePos;
        }
        return false;
    }

    boolean isIndexInTable(int startBytePos, int endBytePos) {
        for (TextPiece tp : this._textPiecesFCOrder) {
            int right;
            int pieceStart = tp.getPieceDescriptor().getFilePosition();
            if (startBytePos >= pieceStart + tp.bytesLength()) continue;
            int left = Math.max(startBytePos, pieceStart);
            return left < (right = Math.min(endBytePos, pieceStart + tp.bytesLength()));
        }
        return false;
    }

    @Override
    public int lookIndexBackward(int startBytePos) {
        int bytePos = startBytePos;
        int lastEnd = 0;
        for (TextPiece tp : this._textPiecesFCOrder) {
            int pieceStart = tp.getPieceDescriptor().getFilePosition();
            if (bytePos > pieceStart + tp.bytesLength()) {
                lastEnd = pieceStart + tp.bytesLength();
                continue;
            }
            if (pieceStart <= bytePos) break;
            bytePos = lastEnd;
            break;
        }
        return bytePos;
    }

    @Override
    public int lookIndexForward(int startBytePos) {
        if (this._textPiecesFCOrder.isEmpty()) {
            throw new IllegalStateException("Text pieces table is empty");
        }
        if (this._textPiecesFCOrder.get(0).getPieceDescriptor().getFilePosition() > startBytePos) {
            return this._textPiecesFCOrder.get(0).getPieceDescriptor().getFilePosition();
        }
        if (this._textPiecesFCOrder.get(this._textPiecesFCOrder.size() - 1).getPieceDescriptor().getFilePosition() <= startBytePos) {
            return startBytePos;
        }
        int low = 0;
        int high = this._textPiecesFCOrder.size() - 1;
        while (low <= high) {
            int mid = low + high >>> 1;
            TextPiece textPiece = this._textPiecesFCOrder.get(mid);
            int midVal = textPiece.getPieceDescriptor().getFilePosition();
            if (midVal < startBytePos) {
                low = mid + 1;
                continue;
            }
            if (midVal > startBytePos) {
                high = mid - 1;
                continue;
            }
            return textPiece.getPieceDescriptor().getFilePosition();
        }
        assert (low == high);
        assert (this._textPiecesFCOrder.get(low).getPieceDescriptor().getFilePosition() < startBytePos);
        assert (this._textPiecesFCOrder.get(low + 1).getPieceDescriptor().getFilePosition() > startBytePos);
        return this._textPiecesFCOrder.get(low + 1).getPieceDescriptor().getFilePosition();
    }

    public byte[] writeTo(ByteArrayOutputStream docStream) throws IOException {
        PlexOfCps textPlex = new PlexOfCps(PieceDescriptor.getSizeInBytes());
        for (TextPiece next : this._textPieces) {
            PieceDescriptor pd = next.getPieceDescriptor();
            int offset = docStream.size();
            int mod = offset % 512;
            if (mod != 0) {
                mod = 512 - mod;
                byte[] buf = IOUtils.safelyAllocate(mod, MAX_RECORD_LENGTH);
                docStream.write(buf);
            }
            pd.setFilePosition(docStream.size());
            docStream.write(next.getRawBytes());
            int nodeStart = next.getStart();
            int nodeEnd = next.getEnd();
            textPlex.addProperty(new GenericPropertyNode(nodeStart, nodeEnd, pd.toByteArray()));
        }
        return textPlex.toByteArray();
    }

    static Comparator<TextPiece> byFilePosition() {
        return Comparator.comparing(t -> t.getPieceDescriptor().getFilePosition());
    }
}

