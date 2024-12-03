/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.CharsRef
 *  org.apache.lucene.util.IntsRef
 *  org.apache.lucene.util.fst.Builder
 *  org.apache.lucene.util.fst.CharSequenceOutputs
 *  org.apache.lucene.util.fst.FST
 *  org.apache.lucene.util.fst.FST$Arc
 *  org.apache.lucene.util.fst.FST$BytesReader
 *  org.apache.lucene.util.fst.FST$INPUT_TYPE
 *  org.apache.lucene.util.fst.Outputs
 *  org.apache.lucene.util.fst.Util
 */
package org.apache.lucene.analysis.charfilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.util.fst.CharSequenceOutputs;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.fst.Outputs;
import org.apache.lucene.util.fst.Util;

public class NormalizeCharMap {
    final FST<CharsRef> map;
    final Map<Character, FST.Arc<CharsRef>> cachedRootArcs = new HashMap<Character, FST.Arc<CharsRef>>();

    private NormalizeCharMap(FST<CharsRef> map) {
        this.map = map;
        if (map != null) {
            try {
                FST.Arc scratchArc = new FST.Arc();
                FST.BytesReader fstReader = map.getBytesReader();
                map.getFirstArc(scratchArc);
                if (FST.targetHasArcs((FST.Arc)scratchArc)) {
                    map.readFirstRealTargetArc(scratchArc.target, scratchArc, fstReader);
                    while (true) {
                        assert (scratchArc.label != -1);
                        this.cachedRootArcs.put(Character.valueOf((char)scratchArc.label), (FST.Arc<CharsRef>)new FST.Arc().copyFrom(scratchArc));
                        if (!scratchArc.isLast()) {
                            map.readNextRealArc(scratchArc, fstReader);
                            continue;
                        }
                        break;
                    }
                }
            }
            catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
    }

    public static class Builder {
        private final Map<String, String> pendingPairs = new TreeMap<String, String>();

        public void add(String match, String replacement) {
            if (match.length() == 0) {
                throw new IllegalArgumentException("cannot match the empty string");
            }
            if (this.pendingPairs.containsKey(match)) {
                throw new IllegalArgumentException("match \"" + match + "\" was already added");
            }
            this.pendingPairs.put(match, replacement);
        }

        public NormalizeCharMap build() {
            FST map;
            try {
                CharSequenceOutputs outputs = CharSequenceOutputs.getSingleton();
                org.apache.lucene.util.fst.Builder builder = new org.apache.lucene.util.fst.Builder(FST.INPUT_TYPE.BYTE2, (Outputs)outputs);
                IntsRef scratch = new IntsRef();
                for (Map.Entry<String, String> ent : this.pendingPairs.entrySet()) {
                    builder.add(Util.toUTF16((CharSequence)ent.getKey(), (IntsRef)scratch), (Object)new CharsRef(ent.getValue()));
                }
                map = builder.finish();
                this.pendingPairs.clear();
            }
            catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
            return new NormalizeCharMap(map);
        }
    }
}

