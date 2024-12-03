/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdfparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObjectKey;

public class XrefTrailerResolver {
    private final Map<Long, XrefTrailerObj> bytePosToXrefMap = new HashMap<Long, XrefTrailerObj>();
    private XrefTrailerObj curXrefTrailerObj = null;
    private XrefTrailerObj resolvedXrefTrailer = null;
    private static final Log LOG = LogFactory.getLog(XrefTrailerResolver.class);

    public final COSDictionary getFirstTrailer() {
        if (this.bytePosToXrefMap.isEmpty()) {
            return null;
        }
        Set<Long> offsets = this.bytePosToXrefMap.keySet();
        TreeSet<Long> sortedOffset = new TreeSet<Long>(offsets);
        return this.bytePosToXrefMap.get(sortedOffset.first()).trailer;
    }

    public final COSDictionary getLastTrailer() {
        if (this.bytePosToXrefMap.isEmpty()) {
            return null;
        }
        Set<Long> offsets = this.bytePosToXrefMap.keySet();
        TreeSet<Long> sortedOffset = new TreeSet<Long>(offsets);
        return this.bytePosToXrefMap.get(sortedOffset.last()).trailer;
    }

    public final int getTrailerCount() {
        return this.bytePosToXrefMap.size();
    }

    public void nextXrefObj(long startBytePos, XRefType type) {
        this.curXrefTrailerObj = new XrefTrailerObj();
        this.bytePosToXrefMap.put(startBytePos, this.curXrefTrailerObj);
        this.curXrefTrailerObj.xrefType = type;
    }

    public XRefType getXrefType() {
        return this.resolvedXrefTrailer == null ? null : this.resolvedXrefTrailer.xrefType;
    }

    public void setXRef(COSObjectKey objKey, long offset) {
        if (this.curXrefTrailerObj == null) {
            LOG.warn((Object)("Cannot add XRef entry for '" + objKey.getNumber() + "' because XRef start was not signalled."));
            return;
        }
        if (!this.curXrefTrailerObj.xrefTable.containsKey(objKey)) {
            this.curXrefTrailerObj.xrefTable.put(objKey, offset);
        }
    }

    public void setTrailer(COSDictionary trailer) {
        if (this.curXrefTrailerObj == null) {
            LOG.warn((Object)"Cannot add trailer because XRef start was not signalled.");
            return;
        }
        this.curXrefTrailerObj.trailer = trailer;
    }

    public COSDictionary getCurrentTrailer() {
        return this.curXrefTrailerObj.trailer;
    }

    public void setStartxref(long startxrefBytePosValue) {
        if (this.resolvedXrefTrailer != null) {
            LOG.warn((Object)"Method must be called only ones with last startxref value.");
            return;
        }
        this.resolvedXrefTrailer = new XrefTrailerObj();
        this.resolvedXrefTrailer.trailer = new COSDictionary();
        XrefTrailerObj curObj = this.bytePosToXrefMap.get(startxrefBytePosValue);
        ArrayList<Long> xrefSeqBytePos = new ArrayList<Long>();
        if (curObj == null) {
            LOG.warn((Object)("Did not found XRef object at specified startxref position " + startxrefBytePosValue));
            xrefSeqBytePos.addAll(this.bytePosToXrefMap.keySet());
            Collections.sort(xrefSeqBytePos);
        } else {
            long prevBytePos;
            this.resolvedXrefTrailer.xrefType = curObj.xrefType;
            xrefSeqBytePos.add(startxrefBytePosValue);
            while (curObj.trailer != null && (prevBytePos = curObj.trailer.getLong(COSName.PREV, -1L)) != -1L) {
                curObj = this.bytePosToXrefMap.get(prevBytePos);
                if (curObj == null) {
                    LOG.warn((Object)("Did not found XRef object pointed to by 'Prev' key at position " + prevBytePos));
                    break;
                }
                xrefSeqBytePos.add(prevBytePos);
                if (xrefSeqBytePos.size() < this.bytePosToXrefMap.size()) continue;
                break;
            }
            Collections.reverse(xrefSeqBytePos);
        }
        for (Long bPos : xrefSeqBytePos) {
            curObj = this.bytePosToXrefMap.get(bPos);
            if (curObj.trailer != null) {
                this.resolvedXrefTrailer.trailer.addAll(curObj.trailer);
            }
            this.resolvedXrefTrailer.xrefTable.putAll(curObj.xrefTable);
        }
    }

    public COSDictionary getTrailer() {
        return this.resolvedXrefTrailer == null ? null : this.resolvedXrefTrailer.trailer;
    }

    public Map<COSObjectKey, Long> getXrefTable() {
        return this.resolvedXrefTrailer == null ? null : this.resolvedXrefTrailer.xrefTable;
    }

    public Set<Long> getContainedObjectNumbers(int objstmObjNr) {
        if (this.resolvedXrefTrailer == null) {
            return null;
        }
        HashSet<Long> refObjNrs = new HashSet<Long>();
        long cmpVal = -objstmObjNr;
        for (Map.Entry xrefEntry : this.resolvedXrefTrailer.xrefTable.entrySet()) {
            if ((Long)xrefEntry.getValue() != cmpVal) continue;
            refObjNrs.add(((COSObjectKey)xrefEntry.getKey()).getNumber());
        }
        return refObjNrs;
    }

    protected void reset() {
        for (XrefTrailerObj trailerObj : this.bytePosToXrefMap.values()) {
            trailerObj.reset();
        }
        this.curXrefTrailerObj = null;
        this.resolvedXrefTrailer = null;
    }

    public static enum XRefType {
        TABLE,
        STREAM;

    }

    private static class XrefTrailerObj {
        protected COSDictionary trailer = null;
        private XRefType xrefType;
        private final Map<COSObjectKey, Long> xrefTable = new HashMap<COSObjectKey, Long>();

        private XrefTrailerObj() {
            this.xrefType = XRefType.TABLE;
        }

        public void reset() {
            this.xrefTable.clear();
        }
    }
}

