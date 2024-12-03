/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.antlr;

import groovyjarjarantlr.CommonAST;
import groovyjarjarantlr.Token;
import groovyjarjarantlr.collections.AST;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.antlr.SourceInfo;

public class GroovySourceAST
extends CommonAST
implements Comparable,
SourceInfo {
    private int line;
    private int col;
    private int lineLast;
    private int colLast;
    private String snippet;

    public GroovySourceAST() {
    }

    public GroovySourceAST(Token t) {
        super(t);
    }

    @Override
    public void initialize(AST ast) {
        super.initialize(ast);
        this.line = ast.getLine();
        this.col = ast.getColumn();
        if (ast instanceof GroovySourceAST) {
            GroovySourceAST node = (GroovySourceAST)ast;
            this.lineLast = node.getLineLast();
            this.colLast = node.getColumnLast();
        }
    }

    @Override
    public void initialize(Token t) {
        super.initialize(t);
        this.line = t.getLine();
        this.col = t.getColumn();
        if (t instanceof SourceInfo) {
            SourceInfo info = (SourceInfo)((Object)t);
            this.lineLast = info.getLineLast();
            this.colLast = info.getColumnLast();
        }
    }

    public void setLast(Token last) {
        this.lineLast = last.getLine();
        this.colLast = last.getColumn();
    }

    @Override
    public int getLineLast() {
        return this.lineLast;
    }

    @Override
    public void setLineLast(int lineLast) {
        this.lineLast = lineLast;
    }

    @Override
    public int getColumnLast() {
        return this.colLast;
    }

    @Override
    public void setColumnLast(int colLast) {
        this.colLast = colLast;
    }

    @Override
    public void setLine(int line) {
        this.line = line;
    }

    @Override
    public int getLine() {
        return this.line;
    }

    @Override
    public void setColumn(int column) {
        this.col = column;
    }

    @Override
    public int getColumn() {
        return this.col;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getSnippet() {
        return this.snippet;
    }

    public int compareTo(Object object) {
        if (object == null) {
            return 0;
        }
        if (!(object instanceof AST)) {
            return 0;
        }
        AST that = (AST)object;
        if (this.getLine() < that.getLine()) {
            return -1;
        }
        if (this.getLine() > that.getLine()) {
            return 1;
        }
        if (this.getColumn() < that.getColumn()) {
            return -1;
        }
        if (this.getColumn() > that.getColumn()) {
            return 1;
        }
        return 0;
    }

    public GroovySourceAST childAt(int position) {
        ArrayList<AST> list = new ArrayList<AST>();
        for (AST child = this.getFirstChild(); child != null; child = child.getNextSibling()) {
            list.add(child);
        }
        try {
            return (GroovySourceAST)list.get(position);
        }
        catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public GroovySourceAST childOfType(int type) {
        for (AST child = this.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getType() != type) continue;
            return (GroovySourceAST)child;
        }
        return null;
    }

    public List<GroovySourceAST> childrenOfType(int type) {
        ArrayList<GroovySourceAST> result = new ArrayList<GroovySourceAST>();
        for (AST child = this.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getType() != type) continue;
            result.add((GroovySourceAST)child);
        }
        return result;
    }
}

