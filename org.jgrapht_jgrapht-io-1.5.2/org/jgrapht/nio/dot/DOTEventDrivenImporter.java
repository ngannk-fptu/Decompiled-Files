/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.antlr.v4.runtime.ANTLRErrorListener
 *  org.antlr.v4.runtime.BaseErrorListener
 *  org.antlr.v4.runtime.CharStream
 *  org.antlr.v4.runtime.CommonTokenFactory
 *  org.antlr.v4.runtime.RecognitionException
 *  org.antlr.v4.runtime.Recognizer
 *  org.antlr.v4.runtime.TokenFactory
 *  org.antlr.v4.runtime.TokenSource
 *  org.antlr.v4.runtime.TokenStream
 *  org.antlr.v4.runtime.UnbufferedCharStream
 *  org.antlr.v4.runtime.UnbufferedTokenStream
 *  org.antlr.v4.runtime.misc.ParseCancellationException
 *  org.apache.commons.text.StringEscapeUtils
 *  org.apache.commons.text.translate.AggregateTranslator
 *  org.apache.commons.text.translate.CharSequenceTranslator
 *  org.apache.commons.text.translate.LookupTranslator
 *  org.jgrapht.alg.util.Pair
 */
package org.jgrapht.nio.dot;

import java.io.Reader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenFactory;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.TokenFactory;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.UnbufferedCharStream;
import org.antlr.v4.runtime.UnbufferedTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.translate.AggregateTranslator;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.apache.commons.text.translate.LookupTranslator;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.BaseEventDrivenImporter;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.EventDrivenImporter;
import org.jgrapht.nio.ImportEvent;
import org.jgrapht.nio.ImportException;
import org.jgrapht.nio.dot.DOTBaseListener;
import org.jgrapht.nio.dot.DOTLexer;
import org.jgrapht.nio.dot.DOTParser;

public class DOTEventDrivenImporter
extends BaseEventDrivenImporter<String, Pair<String, String>>
implements EventDrivenImporter<String, Pair<String, String>> {
    public static final String DEFAULT_GRAPH_ID_KEY = "ID";
    private final CharSequenceTranslator unescapeId;
    private boolean notifyVertexAttributesOutOfOrder;
    private boolean notifyEdgeAttributesOutOfOrder;

    public DOTEventDrivenImporter() {
        this(true, true);
    }

    public DOTEventDrivenImporter(boolean notifyVertexAttributesOutOfOrder, boolean notifyEdgeAttributesOutOfOrder) {
        HashMap<String, String> lookupMap = new HashMap<String, String>();
        lookupMap.put("\\\\", "\\");
        lookupMap.put("\\\"", "\"");
        lookupMap.put("\\'", "'");
        lookupMap.put("\\", "");
        this.unescapeId = new AggregateTranslator(new CharSequenceTranslator[]{new LookupTranslator(lookupMap)});
        this.notifyVertexAttributesOutOfOrder = notifyVertexAttributesOutOfOrder;
        this.notifyEdgeAttributesOutOfOrder = notifyEdgeAttributesOutOfOrder;
    }

    @Override
    public void importInput(Reader in) throws ImportException {
        try {
            DOTLexer lexer = new DOTLexer((CharStream)new UnbufferedCharStream(in));
            lexer.setTokenFactory((TokenFactory)new CommonTokenFactory(true));
            lexer.removeErrorListeners();
            ThrowingErrorListener errorListener = new ThrowingErrorListener();
            lexer.addErrorListener((ANTLRErrorListener)errorListener);
            DOTParser parser = new DOTParser((TokenStream)new UnbufferedTokenStream((TokenSource)lexer));
            parser.removeErrorListeners();
            parser.addErrorListener((ANTLRErrorListener)errorListener);
            parser.setBuildParseTree(false);
            parser.addParseListener(new NotifyDOTListener());
            this.notifyImportEvent(ImportEvent.START);
            parser.graph();
            this.notifyImportEvent(ImportEvent.END);
        }
        catch (IllegalArgumentException | ParseCancellationException e) {
            throw new ImportException("Failed to import DOT graph: " + e.getMessage(), e);
        }
    }

    private String unescapeId(String input) {
        int quote = 34;
        if (input.charAt(0) != '\"' || input.charAt(input.length() - 1) != '\"') {
            return input;
        }
        String noQuotes = input.subSequence(1, input.length() - 1).toString();
        String unescaped = this.unescapeId.translate((CharSequence)noQuotes);
        return unescaped;
    }

    private static String unescapeHtmlString(String input) {
        if (input.charAt(0) != '<' || input.charAt(input.length() - 1) != '>') {
            return input;
        }
        String noQuotes = input.subSequence(1, input.length() - 1).toString();
        String unescaped = StringEscapeUtils.unescapeXml((String)noQuotes);
        return unescaped;
    }

    private class ThrowingErrorListener
    extends BaseErrorListener {
        private ThrowingErrorListener() {
        }

        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) throws ParseCancellationException {
            throw new ParseCancellationException("line " + line + ":" + charPositionInLine + " " + msg);
        }
    }

    private class NotifyDOTListener
    extends DOTBaseListener {
        private Set<String> vertices = new HashSet<String>();
        private Deque<SubgraphScope> subgraphScopes;
        private Deque<State> stack = new ArrayDeque<State>();

        public NotifyDOTListener() {
            this.subgraphScopes = new ArrayDeque<SubgraphScope>();
        }

        @Override
        public void enterGraph(DOTParser.GraphContext ctx) {
            this.stack.push(new State());
            this.subgraphScopes.push(new SubgraphScope());
        }

        @Override
        public void exitGraph(DOTParser.GraphContext ctx) {
            if (this.stack.isEmpty() || this.subgraphScopes.isEmpty()) {
                return;
            }
            this.subgraphScopes.pop();
            this.stack.pop();
        }

        @Override
        public void enterGraphHeader(DOTParser.GraphHeaderContext ctx) {
        }

        @Override
        public void exitGraphHeader(DOTParser.GraphHeaderContext ctx) {
        }

        @Override
        public void enterGraphIdentifier(DOTParser.GraphIdentifierContext ctx) {
            this.stack.push(new State());
        }

        @Override
        public void exitGraphIdentifier(DOTParser.GraphIdentifierContext ctx) {
            if (this.stack.isEmpty()) {
                return;
            }
            State s = this.stack.pop();
            State idPartial = s.children.peekFirst();
            if (idPartial != null) {
                DOTEventDrivenImporter.this.notifyGraphAttribute(DOTEventDrivenImporter.DEFAULT_GRAPH_ID_KEY, DefaultAttribute.createAttribute(idPartial.getId()));
            }
            if (!this.stack.isEmpty()) {
                this.stack.element().children.addLast(s);
            }
        }

        @Override
        public void enterAttributeStatement(DOTParser.AttributeStatementContext ctx) {
            this.stack.push(new State());
        }

        @Override
        public void exitAttributeStatement(DOTParser.AttributeStatementContext ctx) {
            if (this.stack.isEmpty() || this.subgraphScopes.isEmpty()) {
                return;
            }
            State s = this.stack.pop();
            State child = s.children.peekFirst();
            if (child != null && child.attrs != null) {
                Map<String, Attribute> attrs = child.attrs;
                SubgraphScope scope = this.subgraphScopes.element();
                if (ctx.NODE() != null) {
                    scope.nodeAttrs.putAll(attrs);
                } else if (ctx.EDGE() != null) {
                    scope.edgeAttrs.putAll(attrs);
                } else if (ctx.GRAPH() != null) {
                    scope.graphAttrs.putAll(attrs);
                }
            }
        }

        @Override
        public void enterAttributesList(DOTParser.AttributesListContext ctx) {
            this.stack.push(new State());
        }

        @Override
        public void exitAttributesList(DOTParser.AttributesListContext ctx) {
            if (this.stack.isEmpty()) {
                return;
            }
            State s = this.stack.pop();
            for (State child : s.children) {
                if (child.attrs == null) continue;
                s.putAll(child.attrs);
            }
            s.children.clear();
            if (!this.stack.isEmpty()) {
                this.stack.element().children.addLast(s);
            }
        }

        @Override
        public void enterAList(DOTParser.AListContext ctx) {
            this.stack.push(new State());
        }

        @Override
        public void exitAList(DOTParser.AListContext ctx) {
            if (this.stack.isEmpty()) {
                return;
            }
            State s = this.stack.pop();
            Iterator it = s.children.iterator();
            while (it.hasNext()) {
                State child = (State)it.next();
                if (child.ids != null && child.ids.size() == 1) {
                    s.put(child.ids.get(0), null);
                } else if (child.ids != null && child.ids.size() >= 2) {
                    s.put(child.ids.get(0), DefaultAttribute.createAttribute(child.ids.get(1)));
                }
                it.remove();
            }
            s.children.clear();
            if (!this.stack.isEmpty()) {
                this.stack.element().children.addLast(s);
            }
        }

        @Override
        public void enterEdgeStatement(DOTParser.EdgeStatementContext ctx) {
            this.stack.push(new State());
        }

        @Override
        public void exitEdgeStatement(DOTParser.EdgeStatementContext ctx) {
            if (this.stack.isEmpty() || this.subgraphScopes.isEmpty()) {
                return;
            }
            State s = this.stack.pop();
            Map<String, Attribute> attrs = null;
            State last = s.children.peekLast();
            if (last != null && last.attrs != null) {
                attrs = last.attrs;
            }
            Iterator it = s.children.iterator();
            State prev = null;
            while (it.hasNext()) {
                State cur = (State)it.next();
                if (cur.attrs != null) break;
                if (prev != null) {
                    for (String sourceVertex : prev.getVertices()) {
                        for (String targetVertex : cur.getVertices()) {
                            HashMap<String, Attribute> edgeAttrs = new HashMap<String, Attribute>(this.subgraphScopes.element().edgeAttrs);
                            if (attrs != null) {
                                edgeAttrs.putAll(attrs);
                            }
                            Pair pe = Pair.of((Object)sourceVertex, (Object)targetVertex);
                            if (DOTEventDrivenImporter.this.notifyEdgeAttributesOutOfOrder) {
                                DOTEventDrivenImporter.this.notifyEdge(pe);
                                for (Map.Entry entry : edgeAttrs.entrySet()) {
                                    DOTEventDrivenImporter.this.notifyEdgeAttribute(pe, (String)entry.getKey(), (Attribute)entry.getValue());
                                }
                                continue;
                            }
                            DOTEventDrivenImporter.this.notifyEdgeWithAttributes(pe, edgeAttrs);
                        }
                    }
                }
                prev = cur;
            }
        }

        @Override
        public void enterIdentifierPairStatement(DOTParser.IdentifierPairStatementContext ctx) {
            this.stack.push(new State());
        }

        @Override
        public void exitIdentifierPairStatement(DOTParser.IdentifierPairStatementContext ctx) {
            if (this.stack.isEmpty() || this.subgraphScopes.isEmpty()) {
                return;
            }
            State s = this.stack.pop();
            State idPairChild = s.children.peekFirst();
            if (idPairChild == null || idPairChild.ids == null) {
                return;
            }
            String key = idPairChild.ids.get(0);
            String value = idPairChild.ids.get(1);
            SubgraphScope scope = this.subgraphScopes.element();
            scope.graphAttrs.put(key, DefaultAttribute.createAttribute(value));
            if (this.subgraphScopes.size() == 1) {
                DOTEventDrivenImporter.this.notifyGraphAttribute(key, DefaultAttribute.createAttribute(value));
            }
        }

        @Override
        public void enterNodeStatement(DOTParser.NodeStatementContext ctx) {
            this.stack.push(new State());
        }

        @Override
        public void exitNodeStatement(DOTParser.NodeStatementContext ctx) {
            if (this.stack.isEmpty() || this.subgraphScopes.isEmpty()) {
                return;
            }
            State s = this.stack.pop();
            Iterator it = s.children.iterator();
            if (!it.hasNext()) {
                return;
            }
            State nodeIdPartialState = (State)it.next();
            String nodeId = nodeIdPartialState.getId();
            Map<Object, Object> attrs = null;
            if (it.hasNext()) {
                attrs = ((State)it.next()).attrs;
            }
            if (attrs == null) {
                attrs = Collections.emptyMap();
            }
            if (!this.vertices.contains(nodeId)) {
                SubgraphScope scope = this.subgraphScopes.element();
                HashMap<String, Attribute> hashMap = new HashMap<String, Attribute>(scope.nodeAttrs);
                hashMap.putAll(attrs);
                if (DOTEventDrivenImporter.this.notifyVertexAttributesOutOfOrder) {
                    DOTEventDrivenImporter.this.notifyVertex(nodeId);
                    for (Map.Entry entry : hashMap.entrySet()) {
                        DOTEventDrivenImporter.this.notifyVertexAttribute(nodeId, (String)entry.getKey(), (Attribute)entry.getValue());
                    }
                } else {
                    DOTEventDrivenImporter.this.notifyVertexWithAttributes(nodeId, hashMap);
                }
                this.vertices.add(nodeId);
                scope.addVertex(nodeId);
            } else {
                for (String string : attrs.keySet()) {
                    DOTEventDrivenImporter.this.notifyVertexAttribute(nodeId, string, (Attribute)attrs.get(string));
                }
            }
            s.addVertex(nodeId);
            s.children.clear();
            if (!this.stack.isEmpty()) {
                this.stack.element().children.addLast(s);
            }
        }

        @Override
        public void enterNodeStatementNoAttributes(DOTParser.NodeStatementNoAttributesContext ctx) {
            this.stack.push(new State());
        }

        @Override
        public void exitNodeStatementNoAttributes(DOTParser.NodeStatementNoAttributesContext ctx) {
            if (this.stack.isEmpty() || this.subgraphScopes.isEmpty()) {
                return;
            }
            State s = this.stack.pop();
            Iterator it = s.children.iterator();
            if (!it.hasNext()) {
                return;
            }
            State nodeIdPartial = (State)it.next();
            String nodeId = nodeIdPartial.getId();
            if (!this.vertices.contains(nodeId)) {
                SubgraphScope scope = this.subgraphScopes.element();
                HashMap<String, Attribute> defaultAttrs = new HashMap<String, Attribute>(scope.nodeAttrs);
                if (DOTEventDrivenImporter.this.notifyVertexAttributesOutOfOrder) {
                    DOTEventDrivenImporter.this.notifyVertex(nodeId);
                    for (Map.Entry entry : defaultAttrs.entrySet()) {
                        DOTEventDrivenImporter.this.notifyVertexAttribute(nodeId, (String)entry.getKey(), (Attribute)entry.getValue());
                    }
                } else {
                    DOTEventDrivenImporter.this.notifyVertexWithAttributes(nodeId, defaultAttrs);
                }
                this.vertices.add(nodeId);
                scope.addVertex(nodeId);
            }
            s.addVertex(nodeId);
            s.children.clear();
            if (!this.stack.isEmpty()) {
                this.stack.element().children.addLast(s);
            }
        }

        @Override
        public void enterNodeIdentifier(DOTParser.NodeIdentifierContext ctx) {
            this.stack.push(new State());
        }

        @Override
        public void exitNodeIdentifier(DOTParser.NodeIdentifierContext ctx) {
            if (this.stack.isEmpty()) {
                return;
            }
            State s = this.stack.pop();
            if (!s.children.isEmpty()) {
                s.addId(s.children.getFirst().getId());
                s.children.clear();
                if (!this.stack.isEmpty()) {
                    this.stack.element().children.addLast(s);
                }
            }
        }

        @Override
        public void enterSubgraphStatement(DOTParser.SubgraphStatementContext ctx) {
            Map<String, Attribute> defaultGraphAttrs = this.subgraphScopes.element().graphAttrs;
            Map<String, Attribute> defaultNodeAttrs = this.subgraphScopes.element().nodeAttrs;
            Map<String, Attribute> defaultEdgeAttrs = this.subgraphScopes.element().edgeAttrs;
            SubgraphScope newState = new SubgraphScope();
            newState.graphAttrs.putAll(defaultGraphAttrs);
            newState.nodeAttrs.putAll(defaultNodeAttrs);
            newState.edgeAttrs.putAll(defaultEdgeAttrs);
            this.subgraphScopes.push(newState);
            State s = new State();
            s.subgraph = newState;
            this.stack.push(s);
        }

        @Override
        public void exitSubgraphStatement(DOTParser.SubgraphStatementContext ctx) {
            if (this.stack.isEmpty() || this.subgraphScopes.isEmpty()) {
                return;
            }
            SubgraphScope scope = this.subgraphScopes.pop();
            State s = this.stack.pop();
            if (scope.vertices != null && this.subgraphScopes.size() > 1) {
                this.subgraphScopes.element().addVertices(scope.vertices);
            }
            s.children.clear();
            if (!this.stack.isEmpty()) {
                this.stack.element().children.addLast(s);
            }
        }

        @Override
        public void enterIdentifierPair(DOTParser.IdentifierPairContext ctx) {
            this.stack.push(new State());
        }

        @Override
        public void exitIdentifierPair(DOTParser.IdentifierPairContext ctx) {
            if (this.stack.isEmpty()) {
                return;
            }
            State s = this.stack.pop();
            Iterator it = s.children.iterator();
            if (it.hasNext()) {
                s.addId(((State)it.next()).getId());
            }
            if (it.hasNext()) {
                s.addId(((State)it.next()).getId());
            }
            if (s.ids != null) {
                s.children.clear();
                if (!this.stack.isEmpty()) {
                    this.stack.element().children.addLast(s);
                }
            }
        }

        @Override
        public void enterIdentifier(DOTParser.IdentifierContext ctx) {
            this.stack.push(new State());
        }

        @Override
        public void exitIdentifier(DOTParser.IdentifierContext ctx) {
            if (this.stack.isEmpty()) {
                return;
            }
            State s = this.stack.pop();
            String id = null;
            if (ctx.Id() != null) {
                id = ctx.Id().toString();
            } else if (ctx.String() != null) {
                id = DOTEventDrivenImporter.this.unescapeId(ctx.String().toString());
            } else if (ctx.HtmlString() != null) {
                id = DOTEventDrivenImporter.unescapeHtmlString(ctx.HtmlString().toString());
            } else if (ctx.Numeral() != null) {
                id = ctx.Numeral().toString();
            }
            if (id != null) {
                s.addId(id);
                if (!this.stack.isEmpty()) {
                    this.stack.element().children.addLast(s);
                }
            }
        }
    }

    private class SubgraphScope {
        Map<String, Attribute> graphAttrs = new HashMap<String, Attribute>();
        Map<String, Attribute> nodeAttrs = new HashMap<String, Attribute>();
        Map<String, Attribute> edgeAttrs = new HashMap<String, Attribute>();
        List<String> vertices = null;

        public void addVertex(String v) {
            if (this.vertices == null) {
                this.vertices = new ArrayList<String>();
            }
            this.vertices.add(v);
        }

        public void addVertices(List<String> v) {
            if (this.vertices == null) {
                this.vertices = new ArrayList<String>();
            }
            this.vertices.addAll(v);
        }
    }

    private class State {
        LinkedList<State> children = new LinkedList();
        List<String> ids = null;
        Map<String, Attribute> attrs = null;
        List<String> vertices = null;
        SubgraphScope subgraph = null;

        public String getId() {
            if (this.ids == null || this.ids.isEmpty()) {
                return "";
            }
            return this.ids.get(0);
        }

        public void addId(String id) {
            if (this.ids == null) {
                this.ids = new ArrayList<String>();
            }
            this.ids.add(id);
        }

        public void put(String key, Attribute value) {
            if (this.attrs == null) {
                this.attrs = new HashMap<String, Attribute>();
            }
            this.attrs.put(key, value);
        }

        public void putAll(Map<String, Attribute> attrs) {
            if (this.attrs == null) {
                this.attrs = new HashMap<String, Attribute>();
            }
            this.attrs.putAll(attrs);
        }

        public void addVertex(String v) {
            if (this.vertices == null) {
                this.vertices = new ArrayList<String>();
            }
            this.vertices.add(v);
        }

        public List<String> getVertices() {
            if (this.vertices != null) {
                return this.vertices;
            }
            if (this.subgraph != null && this.subgraph.vertices != null) {
                return this.subgraph.vertices;
            }
            return Collections.emptyList();
        }
    }
}

