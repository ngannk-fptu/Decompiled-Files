/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.ja;

import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.analysis.ja.JapaneseTokenizer;
import org.apache.lucene.analysis.ja.dict.ConnectionCosts;
import org.apache.lucene.analysis.ja.dict.Dictionary;

public class GraphvizFormatter {
    private static final String BOS_LABEL = "BOS";
    private static final String EOS_LABEL = "EOS";
    private static final String FONT_NAME = "Helvetica";
    private final ConnectionCosts costs;
    private final Map<String, String> bestPathMap;
    private final StringBuilder sb = new StringBuilder();

    public GraphvizFormatter(ConnectionCosts costs) {
        this.costs = costs;
        this.bestPathMap = new HashMap<String, String>();
        this.sb.append(this.formatHeader());
        this.sb.append("  init [style=invis]\n");
        this.sb.append("  init -> 0.0 [label=\"BOS\"]\n");
    }

    public String finish() {
        this.sb.append(this.formatTrailer());
        return this.sb.toString();
    }

    void onBacktrace(JapaneseTokenizer tok, JapaneseTokenizer.WrappedPositionArray positions, int lastBackTracePos, JapaneseTokenizer.Position endPosData, int fromIDX, char[] fragment, boolean isEnd) {
        this.setBestPathMap(positions, lastBackTracePos, endPosData, fromIDX);
        this.sb.append(this.formatNodes(tok, positions, lastBackTracePos, endPosData, fragment));
        if (isEnd) {
            this.sb.append("  fini [style=invis]\n");
            this.sb.append("  ");
            this.sb.append(this.getNodeID(endPosData.pos, fromIDX));
            this.sb.append(" -> fini [label=\"EOS\"]");
        }
    }

    private void setBestPathMap(JapaneseTokenizer.WrappedPositionArray positions, int startPos, JapaneseTokenizer.Position endPosData, int fromIDX) {
        this.bestPathMap.clear();
        int pos = endPosData.pos;
        int bestIDX = fromIDX;
        while (pos > startPos) {
            JapaneseTokenizer.Position posData = positions.get(pos);
            int backPos = posData.backPos[bestIDX];
            int backIDX = posData.backIndex[bestIDX];
            String toNodeID = this.getNodeID(pos, bestIDX);
            String fromNodeID = this.getNodeID(backPos, backIDX);
            assert (!this.bestPathMap.containsKey(fromNodeID));
            assert (!this.bestPathMap.containsValue(toNodeID));
            this.bestPathMap.put(fromNodeID, toNodeID);
            pos = backPos;
            bestIDX = backIDX;
        }
    }

    private String formatNodes(JapaneseTokenizer tok, JapaneseTokenizer.WrappedPositionArray positions, int startPos, JapaneseTokenizer.Position endPosData, char[] fragment) {
        int idx;
        JapaneseTokenizer.Position posData;
        int pos;
        StringBuilder sb = new StringBuilder();
        for (pos = startPos + 1; pos <= endPosData.pos; ++pos) {
            posData = positions.get(pos);
            for (idx = 0; idx < posData.count; ++idx) {
                sb.append("  ");
                sb.append(this.getNodeID(pos, idx));
                sb.append(" [label=\"");
                sb.append(pos);
                sb.append(": ");
                sb.append(posData.lastRightID[idx]);
                sb.append("\"]\n");
            }
        }
        for (pos = endPosData.pos; pos > startPos; --pos) {
            posData = positions.get(pos);
            for (idx = 0; idx < posData.count; ++idx) {
                JapaneseTokenizer.Position backPosData = positions.get(posData.backPos[idx]);
                String toNodeID = this.getNodeID(pos, idx);
                String fromNodeID = this.getNodeID(posData.backPos[idx], posData.backIndex[idx]);
                sb.append("  ");
                sb.append(fromNodeID);
                sb.append(" -> ");
                sb.append(toNodeID);
                String attrs = toNodeID.equals(this.bestPathMap.get(fromNodeID)) ? " color=\"#40e050\" fontcolor=\"#40a050\" penwidth=3 fontsize=20" : "";
                Dictionary dict = tok.getDict(posData.backType[idx]);
                int wordCost = dict.getWordCost(posData.backID[idx]);
                int bgCost = this.costs.get(backPosData.lastRightID[posData.backIndex[idx]], dict.getLeftId(posData.backID[idx]));
                String surfaceForm = new String(fragment, posData.backPos[idx] - startPos, pos - posData.backPos[idx]);
                sb.append(" [label=\"");
                sb.append(surfaceForm);
                sb.append(' ');
                sb.append(wordCost);
                if (bgCost >= 0) {
                    sb.append('+');
                }
                sb.append(bgCost);
                sb.append("\"");
                sb.append(attrs);
                sb.append("]\n");
            }
        }
        return sb.toString();
    }

    private String formatHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph viterbi {\n");
        sb.append("  graph [ fontsize=30 labelloc=\"t\" label=\"\" splines=true overlap=false rankdir = \"LR\"];\n");
        sb.append("  edge [ fontname=\"Helvetica\" fontcolor=\"red\" color=\"#606060\" ]\n");
        sb.append("  node [ style=\"filled\" fillcolor=\"#e8e8f0\" shape=\"Mrecord\" fontname=\"Helvetica\" ]\n");
        return sb.toString();
    }

    private String formatTrailer() {
        return "}";
    }

    private String getNodeID(int pos, int idx) {
        return pos + "." + idx;
    }
}

