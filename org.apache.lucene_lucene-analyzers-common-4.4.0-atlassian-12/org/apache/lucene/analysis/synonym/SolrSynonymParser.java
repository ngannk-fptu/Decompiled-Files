/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.util.CharsRef
 */
package org.apache.lucene.analysis.synonym;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.util.CharsRef;

public class SolrSynonymParser
extends SynonymMap.Builder {
    private final boolean expand;
    private final Analyzer analyzer;

    public SolrSynonymParser(boolean dedup, boolean expand, Analyzer analyzer) {
        super(dedup);
        this.expand = expand;
        this.analyzer = analyzer;
    }

    public void add(Reader in) throws IOException, ParseException {
        try (LineNumberReader br = new LineNumberReader(in);){
            this.addInternal(br);
        }
    }

    private void addInternal(BufferedReader in) throws IOException {
        String line = null;
        while ((line = in.readLine()) != null) {
            CharsRef[] outputs;
            CharsRef[] inputs;
            String[] inputStrings;
            if (line.length() == 0 || line.charAt(0) == '#') continue;
            String[] sides = SolrSynonymParser.split(line, "=>");
            if (sides.length > 1) {
                if (sides.length != 2) {
                    throw new IllegalArgumentException("more than one explicit mapping specified on the same line");
                }
                inputStrings = SolrSynonymParser.split(sides[0], ",");
                inputs = new CharsRef[inputStrings.length];
                for (int i = 0; i < inputs.length; ++i) {
                    inputs[i] = SolrSynonymParser.analyze(this.analyzer, this.unescape(inputStrings[i]).trim(), new CharsRef());
                }
                String[] outputStrings = SolrSynonymParser.split(sides[1], ",");
                outputs = new CharsRef[outputStrings.length];
                for (int i = 0; i < outputs.length; ++i) {
                    outputs[i] = SolrSynonymParser.analyze(this.analyzer, this.unescape(outputStrings[i]).trim(), new CharsRef());
                }
            } else {
                inputStrings = SolrSynonymParser.split(line, ",");
                inputs = new CharsRef[inputStrings.length];
                for (int i = 0; i < inputs.length; ++i) {
                    inputs[i] = SolrSynonymParser.analyze(this.analyzer, this.unescape(inputStrings[i]).trim(), new CharsRef());
                }
                outputs = this.expand ? inputs : new CharsRef[]{inputs[0]};
            }
            for (int i = 0; i < inputs.length; ++i) {
                for (int j = 0; j < outputs.length; ++j) {
                    this.add(inputs[i], outputs[j], false);
                }
            }
        }
    }

    private static String[] split(String s, String separator) {
        ArrayList<String> list = new ArrayList<String>(2);
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        int end = s.length();
        while (pos < end) {
            char ch;
            if (s.startsWith(separator, pos)) {
                if (sb.length() > 0) {
                    list.add(sb.toString());
                    sb = new StringBuilder();
                }
                pos += separator.length();
                continue;
            }
            if ((ch = s.charAt(pos++)) == '\\') {
                sb.append(ch);
                if (pos >= end) break;
                ch = s.charAt(pos++);
            }
            sb.append(ch);
        }
        if (sb.length() > 0) {
            list.add(sb.toString());
        }
        return list.toArray(new String[list.size()]);
    }

    private String unescape(String s) {
        if (s.indexOf("\\") >= 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < s.length(); ++i) {
                char ch = s.charAt(i);
                if (ch == '\\' && i < s.length() - 1) {
                    sb.append(s.charAt(++i));
                    continue;
                }
                sb.append(ch);
            }
            return sb.toString();
        }
        return s;
    }
}

