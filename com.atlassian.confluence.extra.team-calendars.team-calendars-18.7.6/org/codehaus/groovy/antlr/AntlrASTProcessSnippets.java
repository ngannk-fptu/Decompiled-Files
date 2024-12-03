/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.antlr;

import groovyjarjarantlr.collections.AST;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.codehaus.groovy.antlr.AntlrASTProcessor;
import org.codehaus.groovy.antlr.GroovySourceAST;
import org.codehaus.groovy.antlr.LineColumn;

public class AntlrASTProcessSnippets
implements AntlrASTProcessor {
    @Override
    public AST process(AST t) {
        ArrayList l = new ArrayList();
        this.traverse((GroovySourceAST)t, l, null);
        Iterator itr = l.iterator();
        if (itr.hasNext()) {
            itr.next();
        }
        this.traverse((GroovySourceAST)t, null, itr);
        return t;
    }

    private void traverse(GroovySourceAST t, List l, Iterator itr) {
        while (t != null) {
            GroovySourceAST child;
            if (l != null) {
                l.add(new LineColumn(t.getLine(), t.getColumn()));
            }
            if (itr != null && itr.hasNext()) {
                LineColumn lc = (LineColumn)itr.next();
                if (t.getLineLast() == 0) {
                    int nextLine = lc.getLine();
                    int nextColumn = lc.getColumn();
                    if (nextLine < t.getLine() || nextLine == t.getLine() && nextColumn < t.getColumn()) {
                        nextLine = t.getLine();
                        nextColumn = t.getColumn();
                    }
                    t.setLineLast(nextLine);
                    t.setColumnLast(nextColumn);
                }
            }
            if ((child = (GroovySourceAST)t.getFirstChild()) != null) {
                this.traverse(child, l, itr);
            }
            t = (GroovySourceAST)t.getNextSibling();
        }
    }
}

