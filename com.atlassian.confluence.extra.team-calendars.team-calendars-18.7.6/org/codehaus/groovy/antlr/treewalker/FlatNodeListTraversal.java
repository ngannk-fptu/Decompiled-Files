/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.antlr.treewalker;

import groovyjarjarantlr.collections.AST;
import java.util.List;
import org.codehaus.groovy.antlr.GroovySourceAST;
import org.codehaus.groovy.antlr.treewalker.NodeCollector;
import org.codehaus.groovy.antlr.treewalker.PreOrderTraversal;
import org.codehaus.groovy.antlr.treewalker.TraversalHelper;
import org.codehaus.groovy.antlr.treewalker.Visitor;

public class FlatNodeListTraversal
extends TraversalHelper {
    public FlatNodeListTraversal(Visitor visitor) {
        super(visitor);
    }

    @Override
    public AST process(AST t) {
        GroovySourceAST node = (GroovySourceAST)t;
        NodeCollector collector = new NodeCollector();
        PreOrderTraversal internalTraversal = new PreOrderTraversal(collector);
        internalTraversal.process(t);
        List listOfAllNodesInThisAST = collector.getNodes();
        this.setUp(node);
        for (GroovySourceAST currentNode : listOfAllNodesInThisAST) {
            this.accept(currentNode);
        }
        this.tearDown(node);
        return null;
    }

    @Override
    protected void accept(GroovySourceAST currentNode) {
        this.openingVisit(currentNode);
        this.closingVisit(currentNode);
    }
}

