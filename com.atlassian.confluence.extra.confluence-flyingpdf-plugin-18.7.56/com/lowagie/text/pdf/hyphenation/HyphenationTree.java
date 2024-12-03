/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf.hyphenation;

import com.lowagie.text.pdf.hyphenation.ByteVector;
import com.lowagie.text.pdf.hyphenation.Hyphenation;
import com.lowagie.text.pdf.hyphenation.PatternConsumer;
import com.lowagie.text.pdf.hyphenation.SimplePatternParser;
import com.lowagie.text.pdf.hyphenation.TernaryTree;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HyphenationTree
extends TernaryTree
implements PatternConsumer {
    private static final long serialVersionUID = -7763254239309429432L;
    protected ByteVector vspace;
    protected Map<String, List> stoplist = new HashMap<String, List>(23);
    protected TernaryTree classmap = new TernaryTree();
    private transient TernaryTree ivalues;

    public HyphenationTree() {
        this.vspace = new ByteVector();
        this.vspace.alloc(1);
    }

    protected int packValues(String values) {
        int n = values.length();
        int m = (n & 1) == 1 ? (n >> 1) + 2 : (n >> 1) + 1;
        int offset = this.vspace.alloc(m);
        byte[] va = this.vspace.getArray();
        for (int i = 0; i < n; ++i) {
            int j = i >> 1;
            byte v = (byte)(values.charAt(i) - 48 + 1 & 0xF);
            va[j + offset] = (i & 1) == 1 ? (byte)(va[j + offset] | v) : (byte)(v << 4);
        }
        va[m - 1 + offset] = 0;
        return offset;
    }

    protected String unpackValues(int k) {
        StringBuilder buf = new StringBuilder();
        byte v = this.vspace.get(k++);
        while (v != 0) {
            char c = (char)((v >>> 4) - 1 + 48);
            buf.append(c);
            c = (char)(v & 0xF);
            if (c == '\u0000') break;
            c = (char)(c - '\u0001' + 48);
            buf.append(c);
            v = this.vspace.get(k++);
        }
        return buf.toString();
    }

    public void loadSimplePatterns(InputStream stream) {
        SimplePatternParser pp = new SimplePatternParser();
        this.ivalues = new TernaryTree();
        pp.parse(stream, this);
        this.trimToSize();
        this.vspace.trimToSize();
        this.classmap.trimToSize();
        this.ivalues = null;
    }

    public String findPattern(String pat) {
        int k = super.find(pat);
        if (k >= 0) {
            return this.unpackValues(k);
        }
        return "";
    }

    protected int hstrcmp(char[] s, int si, char[] t, int ti) {
        while (s[si] == t[ti]) {
            if (s[si] == '\u0000') {
                return 0;
            }
            ++si;
            ++ti;
        }
        if (t[ti] == '\u0000') {
            return 0;
        }
        return s[si] - t[ti];
    }

    protected byte[] getValues(int k) {
        StringBuilder buf = new StringBuilder();
        byte v = this.vspace.get(k++);
        while (v != 0) {
            char c = (char)((v >>> 4) - 1);
            buf.append(c);
            c = (char)(v & 0xF);
            if (c == '\u0000') break;
            c = (char)(c - '\u0001');
            buf.append(c);
            v = this.vspace.get(k++);
        }
        byte[] res = new byte[buf.length()];
        for (int i = 0; i < res.length; ++i) {
            res[i] = (byte)buf.charAt(i);
        }
        return res;
    }

    protected void searchPatterns(char[] word, int index, byte[] il) {
        int i = index;
        char sp = word[i];
        char p = this.root;
        block0: while (p > '\u0000' && p < this.sc.length) {
            byte[] values;
            if (this.sc[p] == '\uffff') {
                if (this.hstrcmp(word, i, this.kv.getArray(), this.lo[p]) == 0) {
                    values = this.getValues(this.eq[p]);
                    int j = index;
                    for (byte value : values) {
                        if (j < il.length && value > il[j]) {
                            il[j] = value;
                        }
                        ++j;
                    }
                }
                return;
            }
            int d = sp - this.sc[p];
            if (d == 0) {
                if (sp == '\u0000') break;
                sp = word[++i];
                char q = p = this.eq[p];
                while (q > '\u0000' && q < this.sc.length && this.sc[q] != '\uffff') {
                    if (this.sc[q] == '\u0000') {
                        values = this.getValues(this.eq[q]);
                        int j = index;
                        for (byte value : values) {
                            if (j < il.length && value > il[j]) {
                                il[j] = value;
                            }
                            ++j;
                        }
                        continue block0;
                    }
                    q = this.lo[q];
                }
                continue;
            }
            p = d < 0 ? this.lo[p] : this.hi[p];
        }
    }

    public Hyphenation hyphenate(String word, int remainCharCount, int pushCharCount) {
        char[] w = word.toCharArray();
        return this.hyphenate(w, 0, w.length, remainCharCount, pushCharCount);
    }

    public Hyphenation hyphenate(char[] w, int offset, int len, int remainCharCount, int pushCharCount) {
        int i;
        char[] word = new char[len + 3];
        char[] c = new char[2];
        int iIgnoreAtBeginning = 0;
        int iLength = len;
        boolean bEndOfLetters = false;
        for (i = 1; i <= len; ++i) {
            c[0] = w[offset + i - 1];
            int nc = this.classmap.find(c, 0);
            if (nc < 0) {
                if (i == 1 + iIgnoreAtBeginning) {
                    ++iIgnoreAtBeginning;
                } else {
                    bEndOfLetters = true;
                }
                --iLength;
                continue;
            }
            if (!bEndOfLetters) {
                word[i - iIgnoreAtBeginning] = (char)nc;
                continue;
            }
            return null;
        }
        len = iLength;
        if (len < remainCharCount + pushCharCount) {
            return null;
        }
        int[] result = new int[len + 1];
        int k = 0;
        String sw = new String(word, 1, len);
        if (this.stoplist.containsKey(sw)) {
            ArrayList hw = (ArrayList)this.stoplist.get(sw);
            int j = 0;
            for (i = 0; i < hw.size(); ++i) {
                Object o = hw.get(i);
                if (!(o instanceof String) || (j += ((String)o).length()) < remainCharCount || j >= len - pushCharCount) continue;
                result[k++] = j + iIgnoreAtBeginning;
            }
        } else {
            word[0] = 46;
            word[len + 1] = 46;
            word[len + 2] = '\u0000';
            byte[] il = new byte[len + 3];
            for (i = 0; i < len + 1; ++i) {
                this.searchPatterns(word, i, il);
            }
            for (i = 0; i < len; ++i) {
                if ((il[i + 1] & 1) != 1 || i < remainCharCount || i > len - pushCharCount) continue;
                result[k++] = i + iIgnoreAtBeginning;
            }
        }
        if (k > 0) {
            int[] res = new int[k];
            System.arraycopy(result, 0, res, 0, k);
            return new Hyphenation(new String(w, offset, len), res);
        }
        return null;
    }

    @Override
    public void addClass(String chargroup) {
        if (chargroup.length() > 0) {
            char equivChar = chargroup.charAt(0);
            char[] key = new char[2];
            key[1] = '\u0000';
            for (int i = 0; i < chargroup.length(); ++i) {
                key[0] = chargroup.charAt(i);
                this.classmap.insert(key, 0, equivChar);
            }
        }
    }

    @Override
    public void addException(String word, ArrayList hyphenatedword) {
        this.stoplist.put(word, hyphenatedword);
    }

    @Override
    public void addPattern(String pattern, String ivalue) {
        int k = this.ivalues.find(ivalue);
        if (k <= 0) {
            k = this.packValues(ivalue);
            this.ivalues.insert(ivalue, (char)k);
        }
        this.insert(pattern, (char)k);
    }

    @Override
    public void printStats() {
        System.out.println("Value space size = " + this.vspace.length());
        super.printStats();
    }
}

