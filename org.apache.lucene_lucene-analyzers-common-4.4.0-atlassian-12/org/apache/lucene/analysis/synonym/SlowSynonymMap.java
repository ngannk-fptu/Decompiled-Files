/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Token
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.synonym;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.util.CharArrayMap;
import org.apache.lucene.util.Version;

@Deprecated
class SlowSynonymMap {
    public CharArrayMap<SlowSynonymMap> submap;
    public Token[] synonyms;
    int flags;
    static final int INCLUDE_ORIG = 1;
    static final int IGNORE_CASE = 2;

    public SlowSynonymMap() {
    }

    public SlowSynonymMap(boolean ignoreCase) {
        if (ignoreCase) {
            this.flags |= 2;
        }
    }

    public boolean includeOrig() {
        return (this.flags & 1) != 0;
    }

    public boolean ignoreCase() {
        return (this.flags & 2) != 0;
    }

    public void add(List<String> singleMatch, List<Token> replacement, boolean includeOrig, boolean mergeExisting) {
        SlowSynonymMap currMap = this;
        for (String str : singleMatch) {
            SlowSynonymMap map;
            if (currMap.submap == null) {
                currMap.submap = new CharArrayMap(Version.LUCENE_40, 1, this.ignoreCase());
            }
            if ((map = currMap.submap.get(str)) == null) {
                map = new SlowSynonymMap();
                map.flags |= this.flags & 2;
                currMap.submap.put(str, map);
            }
            currMap = map;
        }
        if (currMap.synonyms != null && !mergeExisting) {
            throw new IllegalArgumentException("SynonymFilter: there is already a mapping for " + singleMatch);
        }
        List<Token> superset = currMap.synonyms == null ? replacement : SlowSynonymMap.mergeTokens(Arrays.asList(currMap.synonyms), replacement);
        currMap.synonyms = superset.toArray(new Token[superset.size()]);
        if (includeOrig) {
            currMap.flags |= 1;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("<");
        if (this.synonyms != null) {
            sb.append("[");
            for (int i = 0; i < this.synonyms.length; ++i) {
                if (i != 0) {
                    sb.append(',');
                }
                sb.append((CharSequence)this.synonyms[i]);
            }
            if ((this.flags & 1) != 0) {
                sb.append(",ORIG");
            }
            sb.append("],");
        }
        sb.append(this.submap);
        sb.append(">");
        return sb.toString();
    }

    public static List<Token> makeTokens(List<String> strings) {
        ArrayList<Token> ret = new ArrayList<Token>(strings.size());
        for (String str : strings) {
            Token newTok = new Token(str, 0, 0, "SYNONYM");
            ret.add(newTok);
        }
        return ret;
    }

    public static List<Token> mergeTokens(List<Token> lst1, List<Token> lst2) {
        int pos2;
        ArrayList<Token> result = new ArrayList<Token>();
        if (lst1 == null || lst2 == null) {
            if (lst2 != null) {
                result.addAll(lst2);
            }
            if (lst1 != null) {
                result.addAll(lst1);
            }
            return result;
        }
        int pos = 0;
        Iterator<Token> iter1 = lst1.iterator();
        Iterator<Token> iter2 = lst2.iterator();
        Token tok1 = iter1.hasNext() ? iter1.next() : null;
        Token tok2 = iter2.hasNext() ? iter2.next() : null;
        int pos1 = tok1 != null ? tok1.getPositionIncrement() : 0;
        int n = pos2 = tok2 != null ? tok2.getPositionIncrement() : 0;
        while (tok1 != null || tok2 != null) {
            Token tok;
            while (tok1 != null && (pos1 <= pos2 || tok2 == null)) {
                tok = new Token(tok1.startOffset(), tok1.endOffset(), tok1.type());
                tok.copyBuffer(tok1.buffer(), 0, tok1.length());
                tok.setPositionIncrement(pos1 - pos);
                result.add(tok);
                pos = pos1;
                tok1 = iter1.hasNext() ? iter1.next() : null;
                pos1 += tok1 != null ? tok1.getPositionIncrement() : 0;
            }
            while (tok2 != null && (pos2 <= pos1 || tok1 == null)) {
                tok = new Token(tok2.startOffset(), tok2.endOffset(), tok2.type());
                tok.copyBuffer(tok2.buffer(), 0, tok2.length());
                tok.setPositionIncrement(pos2 - pos);
                result.add(tok);
                pos = pos2;
                tok2 = iter2.hasNext() ? iter2.next() : null;
                pos2 += tok2 != null ? tok2.getPositionIncrement() : 0;
            }
        }
        return result;
    }
}

