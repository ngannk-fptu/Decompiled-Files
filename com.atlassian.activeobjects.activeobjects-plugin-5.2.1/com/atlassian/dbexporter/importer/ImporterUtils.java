/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dbexporter.importer;

import com.atlassian.dbexporter.node.NodeParser;
import java.util.Objects;

public final class ImporterUtils {
    private ImporterUtils() {
    }

    public static void checkStartNode(NodeParser node, String nodeName) {
        ImporterUtils.checkNode(node, nodeName, !node.isClosed());
    }

    public static boolean isNodeNotClosed(NodeParser node, String nodeName) {
        Objects.requireNonNull(node);
        Objects.requireNonNull(nodeName);
        return !node.isClosed() && nodeName.equals(node.getName());
    }

    public static void checkEndNode(NodeParser node, String nodeName) {
        ImporterUtils.checkNode(node, nodeName, node.isClosed());
    }

    private static void checkNode(NodeParser node, String nodeName, boolean closed) {
        Objects.requireNonNull(node);
        if (!node.getName().equals(nodeName)) {
            throw new IllegalStateException(String.format("%s is not named '%s' as expected", node, nodeName));
        }
        if (!closed) {
            throw new IllegalStateException(String.format("%s is not closed (%s) as expected", node, nodeName));
        }
    }
}

