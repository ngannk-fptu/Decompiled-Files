/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.util.CharsRef
 */
package org.apache.lucene.analysis.synonym;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.text.ParseException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.util.CharsRef;

public class WordnetSynonymParser
extends SynonymMap.Builder {
    private final boolean expand;
    private final Analyzer analyzer;

    public WordnetSynonymParser(boolean dedup, boolean expand, Analyzer analyzer) {
        super(dedup);
        this.expand = expand;
        this.analyzer = analyzer;
    }

    public void add(Reader in) throws IOException, ParseException {
        try (LineNumberReader br = new LineNumberReader(in);){
            String line = null;
            String lastSynSetID = "";
            CharsRef[] synset = new CharsRef[8];
            int synsetSize = 0;
            while ((line = br.readLine()) != null) {
                String synSetID = line.substring(2, 11);
                if (!synSetID.equals(lastSynSetID)) {
                    this.addInternal(synset, synsetSize);
                    synsetSize = 0;
                }
                if (synset.length <= synsetSize + 1) {
                    CharsRef[] larger = new CharsRef[synset.length * 2];
                    System.arraycopy(synset, 0, larger, 0, synsetSize);
                    synset = larger;
                }
                synset[synsetSize] = this.parseSynonym(line, synset[synsetSize]);
                ++synsetSize;
                lastSynSetID = synSetID;
            }
            this.addInternal(synset, synsetSize);
        }
    }

    private CharsRef parseSynonym(String line, CharsRef reuse) throws IOException {
        if (reuse == null) {
            reuse = new CharsRef(8);
        }
        int start = line.indexOf(39) + 1;
        int end = line.lastIndexOf(39);
        String text = line.substring(start, end).replace("''", "'");
        return WordnetSynonymParser.analyze(this.analyzer, text, reuse);
    }

    private void addInternal(CharsRef[] synset, int size) {
        if (size <= 1) {
            return;
        }
        if (this.expand) {
            for (int i = 0; i < size; ++i) {
                for (int j = 0; j < size; ++j) {
                    this.add(synset[i], synset[j], false);
                }
            }
        } else {
            for (int i = 0; i < size; ++i) {
                this.add(synset[i], synset[0], false);
            }
        }
    }
}

