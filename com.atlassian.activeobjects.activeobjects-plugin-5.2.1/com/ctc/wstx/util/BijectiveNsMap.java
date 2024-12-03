/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.util;

import com.ctc.wstx.util.DataUtil;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.NamespaceContext;

public final class BijectiveNsMap {
    static final int DEFAULT_ARRAY_SIZE = 32;
    final int mScopeStart;
    String[] mNsStrings;
    int mScopeEnd;

    private BijectiveNsMap(int scopeStart, String[] strs) {
        this.mScopeStart = this.mScopeEnd = scopeStart;
        this.mNsStrings = strs;
    }

    public static BijectiveNsMap createEmpty() {
        String[] strs = new String[32];
        strs[0] = "xml";
        strs[1] = "http://www.w3.org/XML/1998/namespace";
        strs[2] = "xmlns";
        strs[3] = "http://www.w3.org/2000/xmlns/";
        return new BijectiveNsMap(4, strs);
    }

    public BijectiveNsMap createChild() {
        return new BijectiveNsMap(this.mScopeEnd, this.mNsStrings);
    }

    public String findUriByPrefix(String prefix) {
        String[] strs = this.mNsStrings;
        int phash = prefix.hashCode();
        for (int ix = this.mScopeEnd - 2; ix >= 0; ix -= 2) {
            String thisP = strs[ix];
            if (thisP != prefix && (thisP.hashCode() != phash || !thisP.equals(prefix))) continue;
            return strs[ix + 1];
        }
        return null;
    }

    public String findPrefixByUri(String uri) {
        String[] strs = this.mNsStrings;
        int uhash = uri.hashCode();
        block0: for (int ix = this.mScopeEnd - 1; ix > 0; ix -= 2) {
            String thisU = strs[ix];
            if (thisU != uri && (thisU.hashCode() != uhash || !thisU.equals(uri))) continue;
            String prefix = strs[ix - 1];
            if (ix < this.mScopeStart) {
                int phash = prefix.hashCode();
                int end = this.mScopeEnd;
                for (int j = ix + 1; j < end; j += 2) {
                    String thisP = strs[j];
                    if (thisP == prefix || thisP.hashCode() == phash && thisP.equals(prefix)) continue block0;
                }
            }
            return prefix;
        }
        return null;
    }

    public List getPrefixesBoundToUri(String uri, List l) {
        String[] strs = this.mNsStrings;
        int uhash = uri.hashCode();
        block0: for (int ix = this.mScopeEnd - 1; ix > 0; ix -= 2) {
            String thisU = strs[ix];
            if (thisU != uri && (thisU.hashCode() != uhash || !thisU.equals(uri))) continue;
            String prefix = strs[ix - 1];
            if (ix < this.mScopeStart) {
                int phash = prefix.hashCode();
                int end = this.mScopeEnd;
                for (int j = ix + 1; j < end; j += 2) {
                    String thisP = strs[j];
                    if (thisP == prefix || thisP.hashCode() == phash && thisP.equals(prefix)) continue block0;
                }
            }
            if (l == null) {
                l = new ArrayList<String>();
            }
            l.add(prefix);
        }
        return l;
    }

    public int size() {
        return this.mScopeEnd >> 1;
    }

    public int localSize() {
        return this.mScopeEnd - this.mScopeStart >> 1;
    }

    public String addMapping(String prefix, String uri) {
        String[] strs = this.mNsStrings;
        int phash = prefix.hashCode();
        int end = this.mScopeEnd;
        for (int ix = this.mScopeStart; ix < end; ix += 2) {
            String thisP = strs[ix];
            if (thisP != prefix && (thisP.hashCode() != phash || !thisP.equals(prefix))) continue;
            String old = strs[ix + 1];
            strs[ix + 1] = uri;
            return old;
        }
        if (this.mScopeEnd >= strs.length) {
            strs = DataUtil.growArrayBy(strs, strs.length);
            this.mNsStrings = strs;
        }
        strs[this.mScopeEnd++] = prefix;
        strs[this.mScopeEnd++] = uri;
        return null;
    }

    public String addGeneratedMapping(String prefixBase, NamespaceContext ctxt, String uri, int[] seqArr) {
        String prefix;
        String[] strs = this.mNsStrings;
        int seqNr = seqArr[0];
        block0: while (true) {
            prefix = (prefixBase + seqNr).intern();
            ++seqNr;
            int phash = prefix.hashCode();
            for (int ix = this.mScopeEnd - 2; ix >= 0; ix -= 2) {
                String thisP = strs[ix];
                if (thisP == prefix || thisP.hashCode() == phash && thisP.equals(prefix)) continue block0;
            }
            if (ctxt == null || ctxt.getNamespaceURI(prefix) == null) break;
        }
        seqArr[0] = seqNr;
        if (this.mScopeEnd >= strs.length) {
            strs = DataUtil.growArrayBy(strs, strs.length);
            this.mNsStrings = strs;
        }
        strs[this.mScopeEnd++] = prefix;
        strs[this.mScopeEnd++] = uri;
        return prefix;
    }

    public String toString() {
        return "[" + this.getClass().toString() + "; " + this.size() + " entries; of which " + this.localSize() + " local]";
    }
}

