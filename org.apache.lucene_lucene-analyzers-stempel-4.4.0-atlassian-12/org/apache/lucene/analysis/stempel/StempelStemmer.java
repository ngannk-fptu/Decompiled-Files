/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.stempel;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import org.egothor.stemmer.Diff;
import org.egothor.stemmer.MultiTrie2;
import org.egothor.stemmer.Trie;

public class StempelStemmer {
    private Trie stemmer = null;
    private StringBuilder buffer = new StringBuilder();

    public StempelStemmer(InputStream stemmerTable) throws IOException {
        this(StempelStemmer.load(stemmerTable));
    }

    public StempelStemmer(Trie stemmer) {
        this.stemmer = stemmer;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Trie load(InputStream stemmerTable) throws IOException {
        try (FilterInputStream in = null;){
            in = new DataInputStream(new BufferedInputStream(stemmerTable));
            String method = ((DataInputStream)in).readUTF().toUpperCase(Locale.ROOT);
            if (method.indexOf(77) < 0) {
                Trie trie = new Trie((DataInput)((Object)in));
                return trie;
            }
            MultiTrie2 multiTrie2 = new MultiTrie2((DataInput)((Object)in));
            return multiTrie2;
        }
    }

    public StringBuilder stem(CharSequence word) {
        CharSequence cmd = this.stemmer.getLastOnPath(word);
        if (cmd == null) {
            return null;
        }
        this.buffer.setLength(0);
        this.buffer.append(word);
        Diff.apply(this.buffer, cmd);
        if (this.buffer.length() > 0) {
            return this.buffer;
        }
        return null;
    }
}

