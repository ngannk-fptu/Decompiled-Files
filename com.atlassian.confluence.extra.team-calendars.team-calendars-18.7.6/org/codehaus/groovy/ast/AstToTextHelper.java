/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast;

import java.lang.reflect.Modifier;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.Parameter;

public class AstToTextHelper {
    public static String getClassText(ClassNode node) {
        if (node == null) {
            return "<unknown>";
        }
        if (node.getName() == null) {
            return "<unknown>";
        }
        return node.getName();
    }

    public static String getParameterText(Parameter node) {
        if (node == null) {
            return "<unknown>";
        }
        String name = node.getName() == null ? "<unknown>" : node.getName();
        String type = AstToTextHelper.getClassText(node.getType());
        if (node.getInitialExpression() != null) {
            return type + " " + name + " = " + node.getInitialExpression().getText();
        }
        return type + " " + name;
    }

    public static String getParametersText(Parameter[] parameters) {
        if (parameters == null) {
            return "";
        }
        if (parameters.length == 0) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        int max = parameters.length;
        for (int x = 0; x < max; ++x) {
            result.append(AstToTextHelper.getParameterText(parameters[x]));
            if (x >= max - 1) continue;
            result.append(", ");
        }
        return result.toString();
    }

    public static String getThrowsClauseText(ClassNode[] exceptions) {
        if (exceptions == null) {
            return "";
        }
        if (exceptions.length == 0) {
            return "";
        }
        StringBuilder result = new StringBuilder("throws ");
        int max = exceptions.length;
        for (int x = 0; x < max; ++x) {
            result.append(AstToTextHelper.getClassText(exceptions[x]));
            if (x >= max - 1) continue;
            result.append(", ");
        }
        return result.toString();
    }

    public static String getModifiersText(int modifiers) {
        StringBuilder result = new StringBuilder();
        if (Modifier.isPrivate(modifiers)) {
            result.append("private ");
        }
        if (Modifier.isProtected(modifiers)) {
            result.append("protected ");
        }
        if (Modifier.isPublic(modifiers)) {
            result.append("public ");
        }
        if (Modifier.isStatic(modifiers)) {
            result.append("static ");
        }
        if (Modifier.isAbstract(modifiers)) {
            result.append("abstract ");
        }
        if (Modifier.isFinal(modifiers)) {
            result.append("final ");
        }
        if (Modifier.isInterface(modifiers)) {
            result.append("interface ");
        }
        if (Modifier.isNative(modifiers)) {
            result.append("native ");
        }
        if (Modifier.isSynchronized(modifiers)) {
            result.append("synchronized ");
        }
        if (Modifier.isTransient(modifiers)) {
            result.append("transient ");
        }
        if (Modifier.isVolatile(modifiers)) {
            result.append("volatile ");
        }
        return result.toString().trim();
    }
}

