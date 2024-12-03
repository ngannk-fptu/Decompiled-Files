/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.coredirectives;

import com.google.template.soy.soytree.PrintDirectiveNode;

public class CoreDirectiveUtils {
    private CoreDirectiveUtils() {
    }

    public static boolean isCoreDirective(PrintDirectiveNode directiveNode) {
        String directiveName = directiveNode.getName();
        return directiveName.equals("|id") || directiveName.equals("|noAutoescape") || directiveName.equals("|escapeHtml");
    }

    public static boolean isNoAutoescapeOrIdDirective(PrintDirectiveNode directiveNode) {
        String directiveName = directiveNode.getName();
        return directiveName.equals("|id") || directiveName.equals("|noAutoescape");
    }

    public static boolean isEscapeHtmlDirective(PrintDirectiveNode directiveNode) {
        return directiveNode.getName().equals("|escapeHtml");
    }
}

