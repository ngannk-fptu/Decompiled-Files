/*
 * Decompiled with CFR 0.152.
 */
package org.egothor.stemmer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import org.egothor.stemmer.MultiTrie;
import org.egothor.stemmer.Reduce;
import org.egothor.stemmer.Trie;

public class MultiTrie2
extends MultiTrie {
    public MultiTrie2(DataInput is) throws IOException {
        super(is);
    }

    public MultiTrie2(boolean forward) {
        super(forward);
    }

    @Override
    public CharSequence getFully(CharSequence key) {
        StringBuilder result = new StringBuilder(this.tries.size() * 2);
        try {
            CharSequence lastkey = key;
            CharSequence[] p = new CharSequence[this.tries.size()];
            char lastch = ' ';
            for (int i = 0; i < this.tries.size(); ++i) {
                CharSequence r = ((Trie)this.tries.get(i)).getFully(lastkey);
                if (r == null || r.length() == 1 && r.charAt(0) == '*') {
                    return result;
                }
                if (this.cannotFollow(lastch, r.charAt(0))) {
                    return result;
                }
                lastch = r.charAt(r.length() - 2);
                p[i] = r;
                if (p[i].charAt(0) == '-') {
                    if (i > 0) {
                        key = this.skip(key, this.lengthPP(p[i - 1]));
                    }
                    key = this.skip(key, this.lengthPP(p[i]));
                }
                result.append(r);
                if (key.length() == 0) continue;
                lastkey = key;
            }
        }
        catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            // empty catch block
        }
        return result;
    }

    @Override
    public CharSequence getLastOnPath(CharSequence key) {
        StringBuilder result = new StringBuilder(this.tries.size() * 2);
        try {
            CharSequence lastkey = key;
            CharSequence[] p = new CharSequence[this.tries.size()];
            char lastch = ' ';
            for (int i = 0; i < this.tries.size(); ++i) {
                CharSequence r = ((Trie)this.tries.get(i)).getLastOnPath(lastkey);
                if (r == null || r.length() == 1 && r.charAt(0) == '*') {
                    return result;
                }
                if (this.cannotFollow(lastch, r.charAt(0))) {
                    return result;
                }
                lastch = r.charAt(r.length() - 2);
                p[i] = r;
                if (p[i].charAt(0) == '-') {
                    if (i > 0) {
                        key = this.skip(key, this.lengthPP(p[i - 1]));
                    }
                    key = this.skip(key, this.lengthPP(p[i]));
                }
                result.append(r);
                if (key.length() == 0) continue;
                lastkey = key;
            }
        }
        catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            // empty catch block
        }
        return result;
    }

    @Override
    public void store(DataOutput os) throws IOException {
        super.store(os);
    }

    @Override
    public void add(CharSequence key, CharSequence cmd) {
        if (cmd.length() == 0) {
            return;
        }
        CharSequence[] p = this.decompose(cmd);
        int levels = p.length;
        while (levels >= this.tries.size()) {
            this.tries.add(new Trie(this.forward));
        }
        CharSequence lastkey = key;
        for (int i = 0; i < levels; ++i) {
            if (key.length() > 0) {
                ((Trie)this.tries.get(i)).add(key, p[i]);
                lastkey = key;
            } else {
                ((Trie)this.tries.get(i)).add(lastkey, p[i]);
            }
            if (p[i].length() <= 0 || p[i].charAt(0) != '-') continue;
            if (i > 0) {
                key = this.skip(key, this.lengthPP(p[i - 1]));
            }
            key = this.skip(key, this.lengthPP(p[i]));
        }
        if (key.length() > 0) {
            ((Trie)this.tries.get(levels)).add(key, "*");
        } else {
            ((Trie)this.tries.get(levels)).add(lastkey, "*");
        }
    }

    public CharSequence[] decompose(CharSequence cmd) {
        int parts = 0;
        int i = 0;
        while (0 <= i && i < cmd.length()) {
            int next = this.dashEven(cmd, i);
            if (i == next) {
                ++parts;
                i = next + 2;
                continue;
            }
            ++parts;
            i = next;
        }
        CharSequence[] part = new CharSequence[parts];
        int x = 0;
        int i2 = 0;
        while (0 <= i2 && i2 < cmd.length()) {
            int next = this.dashEven(cmd, i2);
            if (i2 == next) {
                part[x++] = cmd.subSequence(i2, i2 + 2);
                i2 = next + 2;
                continue;
            }
            part[x++] = next < 0 ? cmd.subSequence(i2, cmd.length()) : cmd.subSequence(i2, next);
            i2 = next;
        }
        return part;
    }

    @Override
    public Trie reduce(Reduce by) {
        ArrayList<Trie> h = new ArrayList<Trie>();
        for (Trie trie : this.tries) {
            h.add(trie.reduce(by));
        }
        MultiTrie2 m = new MultiTrie2(this.forward);
        m.tries = h;
        return m;
    }

    private boolean cannotFollow(char after, char goes) {
        switch (after) {
            case '-': 
            case 'D': {
                return after == goes;
            }
        }
        return false;
    }

    private CharSequence skip(CharSequence in, int count) {
        if (this.forward) {
            return in.subSequence(count, in.length());
        }
        return in.subSequence(0, in.length() - count);
    }

    private int dashEven(CharSequence in, int from) {
        while (from < in.length()) {
            if (in.charAt(from) == '-') {
                return from;
            }
            from += 2;
        }
        return -1;
    }

    private int lengthPP(CharSequence cmd) {
        int len = 0;
        block4: for (int i = 0; i < cmd.length(); ++i) {
            switch (cmd.charAt(i++)) {
                case '-': 
                case 'D': {
                    len += cmd.charAt(i) - 97 + 1;
                    continue block4;
                }
                case 'R': {
                    ++len;
                }
            }
        }
        return len;
    }
}

