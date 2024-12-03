/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.antlr.treewalker;

import java.io.PrintStream;
import org.codehaus.groovy.antlr.GroovySourceAST;
import org.codehaus.groovy.antlr.treewalker.VisitorAdapter;

public class NodePrinter
extends VisitorAdapter {
    private String[] tokenNames;
    private PrintStream out;

    public NodePrinter(PrintStream out, String[] tokenNames) {
        this.tokenNames = tokenNames;
        this.out = out;
    }

    @Override
    public void visitDefault(GroovySourceAST t, int visit) {
        if (visit == 1) {
            this.out.print("<" + this.tokenNames[t.getType()] + ">");
        } else {
            this.out.print("</" + this.tokenNames[t.getType()] + ">");
        }
    }
}

