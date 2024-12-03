/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.antlr.java;

import org.codehaus.groovy.antlr.GroovySourceAST;
import org.codehaus.groovy.antlr.parser.GroovyTokenTypes;
import org.codehaus.groovy.antlr.treewalker.VisitorAdapter;

public class Groovifier
extends VisitorAdapter
implements GroovyTokenTypes {
    private String currentClassName = "";
    private boolean cleanRedundantPublic;

    public Groovifier(String[] tokenNames) {
        this(tokenNames, true);
    }

    public Groovifier(String[] tokenNames, boolean cleanRedundantPublic) {
        this.cleanRedundantPublic = cleanRedundantPublic;
    }

    @Override
    public void visitClassDef(GroovySourceAST t, int visit) {
        if (visit == 1) {
            this.currentClassName = t.childOfType(87).getText();
        }
    }

    @Override
    public void visitDefault(GroovySourceAST t, int visit) {
        if (visit == 1) {
            String methodName;
            if (t.getType() == 116 && this.cleanRedundantPublic) {
                t.setType(28);
            }
            if (t.getType() == 8 && (methodName = t.childOfType(87).getText()) != null && methodName.length() > 0 && methodName.equals(this.currentClassName)) {
                t.setType(46);
            }
        }
    }
}

