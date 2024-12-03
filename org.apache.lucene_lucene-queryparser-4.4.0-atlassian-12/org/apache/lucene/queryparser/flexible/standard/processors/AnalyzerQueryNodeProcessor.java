/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.CachingTokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute
 */
package org.apache.lucene.queryparser.flexible.standard.processors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CachingTokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.FuzzyQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.GroupQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.NoTokenFoundQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QuotedFieldQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.RangeQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.TextableQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.TokenizedPhraseQueryNode;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorImpl;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import org.apache.lucene.queryparser.flexible.standard.nodes.MultiPhraseQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.RegexpQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.StandardBooleanQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.WildcardQueryNode;

public class AnalyzerQueryNodeProcessor
extends QueryNodeProcessorImpl {
    private Analyzer analyzer;
    private boolean positionIncrementsEnabled;

    @Override
    public QueryNode process(QueryNode queryTree) throws QueryNodeException {
        Analyzer analyzer = this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.ANALYZER);
        if (analyzer != null) {
            this.analyzer = analyzer;
            this.positionIncrementsEnabled = false;
            Boolean positionIncrementsEnabled = this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.ENABLE_POSITION_INCREMENTS);
            if (positionIncrementsEnabled != null) {
                this.positionIncrementsEnabled = positionIncrementsEnabled;
            }
            if (this.analyzer != null) {
                return super.process(queryTree);
            }
        }
        return queryTree;
    }

    @Override
    protected QueryNode postProcessNode(QueryNode node) throws QueryNodeException {
        if (!(!(node instanceof TextableQueryNode) || node instanceof WildcardQueryNode || node instanceof FuzzyQueryNode || node instanceof RegexpQueryNode || node.getParent() instanceof RangeQueryNode)) {
            TokenStream source;
            FieldQueryNode fieldNode = (FieldQueryNode)node;
            String text = fieldNode.getTextAsString();
            String field = fieldNode.getFieldAsString();
            try {
                source = this.analyzer.tokenStream(field, text);
                source.reset();
            }
            catch (IOException e1) {
                throw new RuntimeException(e1);
            }
            CachingTokenFilter buffer = new CachingTokenFilter(source);
            PositionIncrementAttribute posIncrAtt = null;
            int numTokens = 0;
            int positionCount = 0;
            boolean severalTokensAtSamePosition = false;
            if (buffer.hasAttribute(PositionIncrementAttribute.class)) {
                posIncrAtt = (PositionIncrementAttribute)buffer.getAttribute(PositionIncrementAttribute.class);
            }
            try {
                while (buffer.incrementToken()) {
                    int positionIncrement;
                    ++numTokens;
                    int n = positionIncrement = posIncrAtt != null ? posIncrAtt.getPositionIncrement() : 1;
                    if (positionIncrement != 0) {
                        positionCount += positionIncrement;
                        continue;
                    }
                    severalTokensAtSamePosition = true;
                }
            }
            catch (IOException positionIncrement) {
                // empty catch block
            }
            try {
                buffer.reset();
                source.close();
            }
            catch (IOException positionIncrement) {
                // empty catch block
            }
            if (!buffer.hasAttribute(CharTermAttribute.class)) {
                return new NoTokenFoundQueryNode();
            }
            CharTermAttribute termAtt = (CharTermAttribute)buffer.getAttribute(CharTermAttribute.class);
            if (numTokens == 0) {
                return new NoTokenFoundQueryNode();
            }
            if (numTokens == 1) {
                String term = null;
                try {
                    boolean hasNext = buffer.incrementToken();
                    assert (hasNext);
                    term = termAtt.toString();
                }
                catch (IOException hasNext) {
                    // empty catch block
                }
                fieldNode.setText(term);
                return fieldNode;
            }
            if (severalTokensAtSamePosition || !(node instanceof QuotedFieldQueryNode)) {
                if (positionCount == 1 || !(node instanceof QuotedFieldQueryNode)) {
                    LinkedList<QueryNode> children = new LinkedList<QueryNode>();
                    for (int i = 0; i < numTokens; ++i) {
                        String term = null;
                        try {
                            boolean hasNext = buffer.incrementToken();
                            assert (hasNext);
                            term = termAtt.toString();
                        }
                        catch (IOException hasNext) {
                            // empty catch block
                        }
                        children.add(new FieldQueryNode(field, term, -1, -1));
                    }
                    return new GroupQueryNode(new StandardBooleanQueryNode(children, positionCount == 1));
                }
                MultiPhraseQueryNode mpq = new MultiPhraseQueryNode();
                ArrayList<FieldQueryNode> multiTerms = new ArrayList<FieldQueryNode>();
                int position = -1;
                int termGroupCount = 0;
                for (int i = 0; i < numTokens; ++i) {
                    Object term = null;
                    int positionIncrement = 1;
                    try {
                        boolean hasNext = buffer.incrementToken();
                        assert (hasNext);
                        term = termAtt.toString();
                        if (posIncrAtt != null) {
                            positionIncrement = posIncrAtt.getPositionIncrement();
                        }
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                    if (positionIncrement > 0 && multiTerms.size() > 0) {
                        for (FieldQueryNode termNode : multiTerms) {
                            if (this.positionIncrementsEnabled) {
                                termNode.setPositionIncrement(position);
                            } else {
                                termNode.setPositionIncrement(termGroupCount);
                            }
                            mpq.add(termNode);
                        }
                        ++termGroupCount;
                        multiTerms.clear();
                    }
                    position += positionIncrement;
                    multiTerms.add(new FieldQueryNode(field, (CharSequence)term, -1, -1));
                }
                for (FieldQueryNode termNode : multiTerms) {
                    if (this.positionIncrementsEnabled) {
                        termNode.setPositionIncrement(position);
                    } else {
                        termNode.setPositionIncrement(termGroupCount);
                    }
                    mpq.add(termNode);
                }
                return mpq;
            }
            TokenizedPhraseQueryNode pq = new TokenizedPhraseQueryNode();
            int position = -1;
            for (int i = 0; i < numTokens; ++i) {
                String term = null;
                int positionIncrement = 1;
                try {
                    boolean hasNext = buffer.incrementToken();
                    assert (hasNext);
                    term = termAtt.toString();
                    if (posIncrAtt != null) {
                        positionIncrement = posIncrAtt.getPositionIncrement();
                    }
                }
                catch (IOException hasNext) {
                    // empty catch block
                }
                FieldQueryNode newFieldNode = new FieldQueryNode(field, term, -1, -1);
                if (this.positionIncrementsEnabled) {
                    newFieldNode.setPositionIncrement(position += positionIncrement);
                } else {
                    newFieldNode.setPositionIncrement(i);
                }
                pq.add(newFieldNode);
            }
            return pq;
        }
        return node;
    }

    @Override
    protected QueryNode preProcessNode(QueryNode node) throws QueryNodeException {
        return node;
    }

    @Override
    protected List<QueryNode> setChildrenOrder(List<QueryNode> children) throws QueryNodeException {
        return children;
    }
}

