/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.antlr.treewalker;

import org.codehaus.groovy.antlr.GroovySourceAST;
import org.codehaus.groovy.antlr.treewalker.TraversalHelper;
import org.codehaus.groovy.antlr.treewalker.Visitor;

public class PreOrderTraversal
extends TraversalHelper {
    public PreOrderTraversal(Visitor visitor) {
        super(visitor);
    }

    @Override
    public void accept(GroovySourceAST currentNode) {
        this.push(currentNode);
        this.openingVisit(currentNode);
        this.acceptChildren(currentNode);
        this.closingVisit(currentNode);
        this.pop();
    }
}

