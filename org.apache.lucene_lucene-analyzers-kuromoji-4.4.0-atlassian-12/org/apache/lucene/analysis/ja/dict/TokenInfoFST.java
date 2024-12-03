/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.fst.FST
 *  org.apache.lucene.util.fst.FST$Arc
 *  org.apache.lucene.util.fst.FST$BytesReader
 */
package org.apache.lucene.analysis.ja.dict;

import java.io.IOException;
import org.apache.lucene.util.fst.FST;

public final class TokenInfoFST {
    private final FST<Long> fst;
    private final int cacheCeiling;
    private final FST.Arc<Long>[] rootCache;
    public final Long NO_OUTPUT;

    public TokenInfoFST(FST<Long> fst, boolean fasterButMoreRam) throws IOException {
        this.fst = fst;
        this.cacheCeiling = fasterButMoreRam ? 40959 : 12543;
        this.NO_OUTPUT = (Long)fst.outputs.getNoOutput();
        this.rootCache = this.cacheRootArcs();
    }

    private FST.Arc<Long>[] cacheRootArcs() throws IOException {
        FST.Arc[] rootCache = new FST.Arc[1 + (this.cacheCeiling - 12352)];
        FST.Arc firstArc = new FST.Arc();
        this.fst.getFirstArc(firstArc);
        FST.Arc arc = new FST.Arc();
        FST.BytesReader fstReader = this.fst.getBytesReader();
        for (int i = 0; i < rootCache.length; ++i) {
            if (this.fst.findTargetArc(12352 + i, firstArc, arc, fstReader) == null) continue;
            rootCache[i] = new FST.Arc().copyFrom(arc);
        }
        return rootCache;
    }

    public FST.Arc<Long> findTargetArc(int ch, FST.Arc<Long> follow, FST.Arc<Long> arc, boolean useCache, FST.BytesReader fstReader) throws IOException {
        if (useCache && ch >= 12352 && ch <= this.cacheCeiling) {
            assert (ch != -1);
            FST.Arc<Long> result = this.rootCache[ch - 12352];
            if (result == null) {
                return null;
            }
            arc.copyFrom(result);
            return arc;
        }
        return this.fst.findTargetArc(ch, follow, arc, fstReader);
    }

    public FST.Arc<Long> getFirstArc(FST.Arc<Long> arc) {
        return this.fst.getFirstArc(arc);
    }

    public FST.BytesReader getBytesReader() {
        return this.fst.getBytesReader();
    }

    FST<Long> getInternalFST() {
        return this.fst;
    }
}

