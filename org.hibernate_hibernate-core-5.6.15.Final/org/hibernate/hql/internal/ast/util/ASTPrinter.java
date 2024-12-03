/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.collections.AST
 */
package org.hibernate.hql.internal.ast.util;

import antlr.collections.AST;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.hibernate.hql.internal.ast.tree.DisplayableNode;
import org.hibernate.hql.internal.ast.util.ASTUtil;
import org.hibernate.internal.util.StringHelper;

public class ASTPrinter {
    private final String[] tokenTypeNameCache;

    ASTPrinter(Class tokenTypeConstants) {
        this.tokenTypeNameCache = ASTUtil.generateTokenNameCache(tokenTypeConstants);
    }

    public String showAsString(AST ast, String header) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        ps.println(header);
        this.showAst(ast, ps);
        ps.flush();
        return new String(baos.toByteArray());
    }

    public void showAst(AST ast, PrintStream out) {
        this.showAst(ast, new PrintWriter(out));
    }

    public void showAst(AST ast, PrintWriter pw) {
        ArrayList<AST> parents = new ArrayList<AST>();
        this.showAst(parents, pw, ast);
        pw.flush();
    }

    public String getTokenTypeName(int type) {
        String value = this.tokenTypeNameCache[type];
        if (value == null) {
            value = Integer.toString(type);
        }
        return value;
    }

    private void showAst(ArrayList<AST> parents, PrintWriter pw, AST ast) {
        if (ast == null) {
            pw.println("AST is null!");
            return;
        }
        this.indentLine(parents, pw);
        if (ast.getNextSibling() == null) {
            pw.print(" \\-");
        } else {
            pw.print(" +-");
        }
        this.showNode(pw, ast);
        this.showNodeProperties(parents, pw, ast);
        ArrayList<AST> newParents = new ArrayList<AST>(parents);
        newParents.add(ast);
        for (AST child = ast.getFirstChild(); child != null; child = child.getNextSibling()) {
            this.showAst(newParents, pw, child);
        }
        newParents.clear();
    }

    private void indentLine(List<AST> parents, PrintWriter pw) {
        for (AST parent : parents) {
            if (parent.getNextSibling() == null) {
                pw.print("   ");
                continue;
            }
            pw.print(" | ");
        }
    }

    private void showNode(PrintWriter pw, AST ast) {
        String s = this.nodeToString(ast);
        pw.println(s);
    }

    public String nodeToString(AST ast) {
        if (ast == null) {
            return "{node:null}";
        }
        StringBuilder buf = new StringBuilder();
        buf.append("[").append(this.getTokenTypeName(ast.getType())).append("] ");
        buf.append(StringHelper.unqualify(ast.getClass().getName())).append(": ");
        buf.append("'");
        String text = ast.getText();
        if (text == null) {
            text = "{text:null}";
        }
        ASTPrinter.appendEscapedMultibyteChars(text, buf);
        buf.append("'");
        if (ast instanceof DisplayableNode) {
            DisplayableNode displayableNode = (DisplayableNode)ast;
            buf.append(" ").append(displayableNode.getDisplayText());
        }
        return buf.toString();
    }

    private void showNodeProperties(ArrayList<AST> parents, PrintWriter pw, AST ast) {
        LinkedHashMap<String, Object> nodeProperties = this.createNodeProperties(ast);
        ArrayList<AST> parentsAndNode = new ArrayList<AST>(parents);
        parentsAndNode.add(ast);
        for (String propertyName : nodeProperties.keySet()) {
            this.indentLine(parentsAndNode, pw);
            pw.println(this.propertyToString(propertyName, nodeProperties.get(propertyName), ast));
        }
    }

    public LinkedHashMap<String, Object> createNodeProperties(AST ast) {
        return new LinkedHashMap<String, Object>();
    }

    public String propertyToString(String label, Object value, AST ast) {
        return String.format("%s: %s", label, value);
    }

    public static void appendEscapedMultibyteChars(String text, StringBuilder buf) {
        char[] chars;
        for (char aChar : chars = text.toCharArray()) {
            if (aChar > '\u0100') {
                buf.append("\\u");
                buf.append(Integer.toHexString(aChar));
                continue;
            }
            buf.append(aChar);
        }
    }

    public static String escapeMultibyteChars(String text) {
        StringBuilder buf = new StringBuilder();
        ASTPrinter.appendEscapedMultibyteChars(text, buf);
        return buf.toString();
    }
}

