/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.antlr.treewalker;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.antlr.GroovySourceAST;
import org.codehaus.groovy.antlr.treewalker.VisitorAdapter;

public class NodeCollector
extends VisitorAdapter {
    private List nodes = new ArrayList();

    public List getNodes() {
        return this.nodes;
    }

    @Override
    public void visitDefault(GroovySourceAST t, int visit) {
        if (visit == 1) {
            this.nodes.add(t);
        }
    }
}

