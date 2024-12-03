/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.FilteredTermEnum;
import java.io.IOException;

public class WildcardTermEnum
extends FilteredTermEnum {
    final Term searchTerm;
    final String field;
    final String text;
    final String pre;
    final int preLen;
    boolean endEnum = false;
    public static final char WILDCARD_STRING = '*';
    public static final char WILDCARD_CHAR = '?';

    public WildcardTermEnum(IndexReader reader, Term term) throws IOException {
        this.searchTerm = term;
        this.field = this.searchTerm.field();
        String searchTermText = this.searchTerm.text();
        int sidx = searchTermText.indexOf(42);
        int cidx = searchTermText.indexOf(63);
        int idx = sidx;
        if (idx == -1) {
            idx = cidx;
        } else if (cidx >= 0) {
            idx = Math.min(idx, cidx);
        }
        this.pre = idx != -1 ? this.searchTerm.text().substring(0, idx) : "";
        this.preLen = this.pre.length();
        this.text = searchTermText.substring(this.preLen);
        this.setEnum(reader.terms(new Term(this.searchTerm.field(), this.pre)));
    }

    protected final boolean termCompare(Term term) {
        String searchText;
        if (this.field == term.field() && (searchText = term.text()).startsWith(this.pre)) {
            return WildcardTermEnum.wildcardEquals(this.text, 0, searchText, this.preLen);
        }
        this.endEnum = true;
        return false;
    }

    public float difference() {
        return 1.0f;
    }

    public final boolean endEnum() {
        return this.endEnum;
    }

    public static final boolean wildcardEquals(String pattern, int patternIdx, String string, int stringIdx) {
        int p = patternIdx;
        int s = stringIdx;
        while (true) {
            boolean pEnd;
            boolean sEnd = s >= string.length();
            boolean bl = pEnd = p >= pattern.length();
            if (sEnd) {
                boolean justWildcardsLeft = true;
                int wildcardSearchPos = p;
                while (wildcardSearchPos < pattern.length() && justWildcardsLeft) {
                    char wildchar = pattern.charAt(wildcardSearchPos);
                    if (wildchar != '?' && wildchar != '*') {
                        justWildcardsLeft = false;
                        continue;
                    }
                    if (wildchar == '?') {
                        return false;
                    }
                    ++wildcardSearchPos;
                }
                if (justWildcardsLeft) {
                    return true;
                }
            }
            if (sEnd || pEnd) break;
            if (pattern.charAt(p) != '?') {
                if (pattern.charAt(p) == '*') {
                    while (p < pattern.length() && pattern.charAt(p) == '*') {
                        ++p;
                    }
                    for (int i = string.length(); i >= s; --i) {
                        if (!WildcardTermEnum.wildcardEquals(pattern, p, string, i)) continue;
                        return true;
                    }
                    break;
                }
                if (pattern.charAt(p) != string.charAt(s)) break;
            }
            ++p;
            ++s;
        }
        return false;
    }
}

