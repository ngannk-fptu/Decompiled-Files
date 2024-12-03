/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.nio.dot;

import java.util.regex.Pattern;

class DOTUtils {
    static final String DONT_ALLOW_MULTIPLE_EDGES_KEYWORD = "strict";
    static final String DIRECTED_GRAPH_KEYWORD = "digraph";
    static final String UNDIRECTED_GRAPH_KEYWORD = "graph";
    static final String DIRECTED_GRAPH_EDGEOP = "->";
    static final String UNDIRECTED_GRAPH_EDGEOP = "--";
    private static final Pattern ALPHA_DIG = Pattern.compile("[a-zA-Z_][\\w]*");
    private static final Pattern DOUBLE_QUOTE = Pattern.compile("\".*\"");
    private static final Pattern DOT_NUMBER = Pattern.compile("[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)");
    private static final Pattern HTML = Pattern.compile("<.*>");

    DOTUtils() {
    }

    static boolean isValidID(String idCandidate) {
        return ALPHA_DIG.matcher(idCandidate).matches() || DOUBLE_QUOTE.matcher(idCandidate).matches() || DOT_NUMBER.matcher(idCandidate).matches() || HTML.matcher(idCandidate).matches();
    }
}

