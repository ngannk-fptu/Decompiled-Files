/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.tree;

import java.io.PrintStream;
import org.apache.commons.configuration2.tree.ImmutableNode;

public final class TreeUtils {
    private TreeUtils() {
    }

    public static void printTree(PrintStream stream, ImmutableNode result) {
        if (stream != null) {
            TreeUtils.printTree(stream, "", result);
        }
    }

    private static void printTree(PrintStream stream, String indent, ImmutableNode result) {
        StringBuilder buffer = new StringBuilder(indent).append("<").append(result.getNodeName());
        result.getAttributes().forEach((k, v) -> buffer.append(' ').append((String)k).append("='").append(v).append("'"));
        buffer.append(">");
        stream.print(buffer.toString());
        if (result.getValue() != null) {
            stream.print(result.getValue());
        }
        boolean newline = false;
        if (!result.getChildren().isEmpty()) {
            stream.print("\n");
            result.forEach(child -> TreeUtils.printTree(stream, indent + "  ", child));
            newline = true;
        }
        if (newline) {
            stream.print(indent);
        }
        stream.println("</" + result.getNodeName() + ">");
    }
}

