/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.RecognitionException
 *  antlr.Token
 *  antlr.TokenStreamException
 *  org.jboss.logging.Logger
 */
package org.hibernate.graph.internal.parse;

import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenStreamException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.graph.AttributeNode;
import org.hibernate.graph.InvalidGraphException;
import org.hibernate.graph.internal.parse.GeneratedGraphParser;
import org.hibernate.graph.internal.parse.GraphLexer;
import org.hibernate.graph.internal.parse.PathQualifierType;
import org.hibernate.graph.internal.parse.SubGraphGenerator;
import org.hibernate.graph.spi.AttributeNodeImplementor;
import org.hibernate.graph.spi.GraphImplementor;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.Stack;
import org.hibernate.internal.util.collections.StandardStack;
import org.hibernate.internal.util.io.CharSequenceReader;
import org.jboss.logging.Logger;

public class GraphParser
extends GeneratedGraphParser {
    public static final Logger PARSING_LOGGER = Logger.getLogger((String)"org.hibernate.orm.graph.parsing");
    private final SessionFactoryImplementor sessionFactory;
    private final Stack<GraphImplementor<?>> graphStack = new StandardStack();
    private final Stack<AttributeNodeImplementor<?>> attributeNodeStack = new StandardStack();
    private final Stack<SubGraphGenerator> graphSourceStack = new StandardStack<SubGraphGenerator>();

    public static void parseInto(GraphImplementor<?> targetGraph, CharSequence graphString, SessionFactoryImplementor sessionFactory) {
        GraphParser instance = new GraphParser(graphString, sessionFactory);
        instance.graphStack.push(targetGraph);
        try {
            instance.graph();
        }
        catch (RecognitionException | TokenStreamException e) {
            throw new InvalidGraphException("Error parsing graph string");
        }
    }

    private GraphParser(CharSequence charSequence, SessionFactoryImplementor sessionFactory) {
        super(new GraphLexer(new CharSequenceReader(charSequence)));
        this.sessionFactory = sessionFactory;
    }

    @Override
    protected void startAttribute(Token attributeNameToken) {
        String attributeName = attributeNameToken.getText();
        if (PARSING_LOGGER.isDebugEnabled()) {
            PARSING_LOGGER.debugf("%s Start attribute : %s", (Object)StringHelper.repeat(">>", this.attributeNodeStack.depth() + 1), (Object)attributeName);
        }
        AttributeNodeImplementor attributeNode = this.resolveAttributeNode(attributeName);
        this.attributeNodeStack.push(attributeNode);
        this.graphSourceStack.push(PathQualifierType.VALUE.getSubGraphCreator());
    }

    private AttributeNodeImplementor resolveAttributeNode(String attributeName) {
        GraphImplementor<?> currentGraph = this.graphStack.getCurrent();
        assert (currentGraph != null);
        AttributeNode attributeNode = currentGraph.addAttributeNode(attributeName);
        assert (attributeNode != null);
        return attributeNode;
    }

    @Override
    protected void startQualifiedAttribute(Token attributeNameToken, Token qualifierToken) {
        String attributeName = attributeNameToken.getText();
        String qualifierName = qualifierToken.getText();
        if (PARSING_LOGGER.isDebugEnabled()) {
            PARSING_LOGGER.debugf("%s Start qualified attribute : %s.%s", (Object)StringHelper.repeat(">>", this.attributeNodeStack.depth() + 1), (Object)attributeName, (Object)qualifierName);
        }
        AttributeNodeImplementor attributeNode = this.resolveAttributeNode(attributeName);
        this.attributeNodeStack.push(attributeNode);
        PathQualifierType pathQualifierType = this.resolvePathQualifier(qualifierName);
        this.graphSourceStack.push(pathQualifierType.getSubGraphCreator());
    }

    private PathQualifierType resolvePathQualifier(String qualifier) {
        if ("key".equalsIgnoreCase(qualifier)) {
            return PathQualifierType.KEY;
        }
        if ("value".equalsIgnoreCase(qualifier)) {
            return PathQualifierType.VALUE;
        }
        throw new InvalidGraphException("Invalid path qualifier [" + qualifier + "] - expecting `key` or `value`");
    }

    @Override
    protected void finishAttribute() {
        this.graphSourceStack.pop();
        AttributeNodeImplementor<?> popped = this.attributeNodeStack.pop();
        if (PARSING_LOGGER.isDebugEnabled()) {
            PARSING_LOGGER.debugf("%s Finished attribute : %s", (Object)StringHelper.repeat("<<", this.attributeNodeStack.depth() + 1), (Object)popped.getAttributeDescriptor().getName());
        }
    }

    @Override
    protected void startSubGraph(Token subTypeToken) {
        String subTypeName;
        String string = subTypeName = subTypeToken == null ? null : subTypeToken.getText();
        if (PARSING_LOGGER.isDebugEnabled()) {
            PARSING_LOGGER.debugf("%s Starting graph : %s", (Object)StringHelper.repeat(">>", this.attributeNodeStack.depth() + 2), (Object)subTypeName);
        }
        AttributeNodeImplementor<?> attributeNode = this.attributeNodeStack.getCurrent();
        this.graphStack.push(this.graphSourceStack.getCurrent().createSubGraph(attributeNode, subTypeName, this.sessionFactory));
    }

    @Override
    protected void finishSubGraph() {
        GraphImplementor<?> popped = this.graphStack.pop();
        if (PARSING_LOGGER.isDebugEnabled()) {
            PARSING_LOGGER.debugf("%s Finished graph : %s", (Object)StringHelper.repeat("<<", this.attributeNodeStack.depth() + 2), (Object)popped.getGraphedType().getName());
        }
    }

    public void traceIn(String s) throws TokenStreamException {
    }

    public void traceOut(String s) throws TokenStreamException {
    }
}

