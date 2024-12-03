/*
 * Decompiled with CFR 0.152.
 */
package org.egothor.stemmer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import org.egothor.stemmer.Reduce;
import org.egothor.stemmer.Trie;

public class MultiTrie
extends Trie {
    final char EOM = (char)42;
    final String EOM_NODE = "*";
    List<Trie> tries = new ArrayList<Trie>();
    int BY = 1;

    public MultiTrie(DataInput is) throws IOException {
        super(false);
        this.forward = is.readBoolean();
        this.BY = is.readInt();
        for (int i = is.readInt(); i > 0; --i) {
            this.tries.add(new Trie(is));
        }
    }

    public MultiTrie(boolean forward) {
        super(forward);
    }

    @Override
    public CharSequence getFully(CharSequence key) {
        StringBuilder result = new StringBuilder(this.tries.size() * 2);
        for (int i = 0; i < this.tries.size(); ++i) {
            CharSequence r = this.tries.get(i).getFully(key);
            if (r == null || r.length() == 1 && r.charAt(0) == '*') {
                return result;
            }
            result.append(r);
        }
        return result;
    }

    @Override
    public CharSequence getLastOnPath(CharSequence key) {
        StringBuilder result = new StringBuilder(this.tries.size() * 2);
        for (int i = 0; i < this.tries.size(); ++i) {
            CharSequence r = this.tries.get(i).getLastOnPath(key);
            if (r == null || r.length() == 1 && r.charAt(0) == '*') {
                return result;
            }
            result.append(r);
        }
        return result;
    }

    @Override
    public void store(DataOutput os) throws IOException {
        os.writeBoolean(this.forward);
        os.writeInt(this.BY);
        os.writeInt(this.tries.size());
        for (Trie trie : this.tries) {
            trie.store(os);
        }
    }

    @Override
    public void add(CharSequence key, CharSequence cmd) {
        if (cmd.length() == 0) {
            return;
        }
        int levels = cmd.length() / this.BY;
        while (levels >= this.tries.size()) {
            this.tries.add(new Trie(this.forward));
        }
        for (int i = 0; i < levels; ++i) {
            this.tries.get(i).add(key, cmd.subSequence(this.BY * i, this.BY * i + this.BY));
        }
        this.tries.get(levels).add(key, "*");
    }

    @Override
    public Trie reduce(Reduce by) {
        ArrayList<Trie> h = new ArrayList<Trie>();
        for (Trie trie : this.tries) {
            h.add(trie.reduce(by));
        }
        MultiTrie m = new MultiTrie(this.forward);
        m.tries = h;
        return m;
    }

    @Override
    public void printInfo(PrintStream out, CharSequence prefix) {
        int c = 0;
        for (Trie trie : this.tries) {
            trie.printInfo(out, prefix + "[" + ++c + "] ");
        }
    }
}

