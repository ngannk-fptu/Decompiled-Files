/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.antlr.v4.runtime.ANTLRErrorListener
 *  org.antlr.v4.runtime.BaseErrorListener
 *  org.antlr.v4.runtime.CharStream
 *  org.antlr.v4.runtime.CharStreams
 *  org.antlr.v4.runtime.CommonTokenStream
 *  org.antlr.v4.runtime.RecognitionException
 *  org.antlr.v4.runtime.Recognizer
 *  org.antlr.v4.runtime.TokenSource
 *  org.antlr.v4.runtime.TokenStream
 *  org.antlr.v4.runtime.misc.ParseCancellationException
 *  org.antlr.v4.runtime.tree.ParseTree
 *  org.antlr.v4.runtime.tree.ParseTreeListener
 *  org.antlr.v4.runtime.tree.ParseTreeWalker
 *  org.antlr.v4.runtime.tree.TerminalNode
 *  org.apache.commons.text.StringEscapeUtils
 *  org.jgrapht.alg.util.Triple
 */
package org.jgrapht.nio.json;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.text.StringEscapeUtils;
import org.jgrapht.alg.util.Triple;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.BaseEventDrivenImporter;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.EventDrivenImporter;
import org.jgrapht.nio.ImportEvent;
import org.jgrapht.nio.ImportException;
import org.jgrapht.nio.json.JsonBaseListener;
import org.jgrapht.nio.json.JsonLexer;
import org.jgrapht.nio.json.JsonParser;

public class JSONEventDrivenImporter
extends BaseEventDrivenImporter<String, Triple<String, String, Double>>
implements EventDrivenImporter<String, Triple<String, String, Double>> {
    public static final String DEFAULT_VERTICES_COLLECTION_NAME = "nodes";
    public static final String DEFAULT_EDGES_COLLECTION_NAME = "edges";
    private boolean notifyVertexAttributesOutOfOrder;
    private boolean notifyEdgeAttributesOutOfOrder;
    private String verticesCollectionName = "nodes";
    private String edgesCollectionName = "edges";

    public JSONEventDrivenImporter() {
        this(true, true);
    }

    public JSONEventDrivenImporter(boolean notifyVertexAttributesOutOfOrder, boolean notifyEdgeAttributesOutOfOrder) {
        this.notifyVertexAttributesOutOfOrder = notifyVertexAttributesOutOfOrder;
        this.notifyEdgeAttributesOutOfOrder = notifyEdgeAttributesOutOfOrder;
    }

    public String getVerticesCollectionName() {
        return this.verticesCollectionName;
    }

    public void setVerticesCollectionName(String verticesCollectionName) {
        this.verticesCollectionName = Objects.requireNonNull(verticesCollectionName);
    }

    public String getEdgesCollectionName() {
        return this.edgesCollectionName;
    }

    public void setEdgesCollectionName(String edgesCollectionName) {
        this.edgesCollectionName = Objects.requireNonNull(edgesCollectionName);
    }

    @Override
    public void importInput(Reader input) {
        try {
            ThrowingErrorListener errorListener = new ThrowingErrorListener();
            JsonLexer lexer = new JsonLexer((CharStream)CharStreams.fromReader((Reader)input));
            lexer.removeErrorListeners();
            lexer.addErrorListener((ANTLRErrorListener)errorListener);
            JsonParser parser = new JsonParser((TokenStream)new CommonTokenStream((TokenSource)lexer));
            parser.removeErrorListeners();
            parser.addErrorListener((ANTLRErrorListener)errorListener);
            JsonParser.JsonContext graphContext = parser.json();
            ParseTreeWalker walker = new ParseTreeWalker();
            NotifyJsonListener listener = new NotifyJsonListener();
            this.notifyImportEvent(ImportEvent.START);
            walker.walk((ParseTreeListener)listener, (ParseTree)graphContext);
            this.notifyImportEvent(ImportEvent.END);
        }
        catch (IOException | IllegalArgumentException | ParseCancellationException e) {
            throw new ImportException("Failed to import json graph: " + e.getMessage(), e);
        }
    }

    private class ThrowingErrorListener
    extends BaseErrorListener {
        private ThrowingErrorListener() {
        }

        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) throws ParseCancellationException {
            throw new ParseCancellationException("line " + line + ":" + charPositionInLine + " " + msg);
        }
    }

    private class NotifyJsonListener
    extends JsonBaseListener {
        private static final String GRAPH = "graph";
        private static final String ID = "id";
        private static final String WEIGHT = "weight";
        private static final String SOURCE = "source";
        private static final String TARGET = "target";
        private int objectLevel;
        private int arrayLevel;
        private boolean insideNodes;
        private boolean insideNodesArray;
        private boolean insideNode;
        private boolean insideEdges;
        private boolean insideEdgesArray;
        private boolean insideEdge;
        private Deque<String> pairNames;
        private String nodeId;
        private String sourceId;
        private String targetId;
        private Map<String, Attribute> attributes;
        private int singletons;
        private String singletonsUUID;

        private NotifyJsonListener() {
        }

        @Override
        public void enterJson(JsonParser.JsonContext ctx) {
            this.objectLevel = 0;
            this.arrayLevel = 0;
            this.insideNodes = false;
            this.insideNodesArray = false;
            this.insideNode = false;
            this.insideEdges = false;
            this.insideEdgesArray = false;
            this.insideEdge = false;
            this.singletons = 0;
            this.singletonsUUID = UUID.randomUUID().toString();
            this.pairNames = new ArrayDeque<String>();
            this.pairNames.push(GRAPH);
        }

        @Override
        public void enterObj(JsonParser.ObjContext ctx) {
            ++this.objectLevel;
            if (this.objectLevel == 2 && this.arrayLevel == 1) {
                if (this.insideNodesArray) {
                    this.insideNode = true;
                    this.nodeId = null;
                    this.attributes = new HashMap<String, Attribute>();
                } else if (this.insideEdgesArray) {
                    this.insideEdge = true;
                    this.sourceId = null;
                    this.targetId = null;
                    this.attributes = new HashMap<String, Attribute>();
                }
            }
        }

        @Override
        public void exitObj(JsonParser.ObjContext ctx) {
            if (this.objectLevel == 2 && this.arrayLevel == 1) {
                if (this.insideNodesArray) {
                    if (this.nodeId == null) {
                        this.nodeId = "Singleton_" + this.singletonsUUID + "_" + this.singletons++;
                    }
                    if (JSONEventDrivenImporter.this.notifyVertexAttributesOutOfOrder) {
                        JSONEventDrivenImporter.this.notifyVertex(this.nodeId);
                        for (Map.Entry<String, Attribute> entry : this.attributes.entrySet()) {
                            JSONEventDrivenImporter.this.notifyVertexAttribute(this.nodeId, entry.getKey(), entry.getValue());
                        }
                    } else {
                        JSONEventDrivenImporter.this.notifyVertexWithAttributes(this.nodeId, this.attributes);
                    }
                    this.insideNode = false;
                    this.attributes = null;
                } else if (this.insideEdgesArray) {
                    if (this.sourceId != null && this.targetId != null) {
                        AttributeType type;
                        Double weight = 1.0;
                        Attribute attributeWeight = this.attributes.get(WEIGHT);
                        if (attributeWeight != null && ((type = attributeWeight.getType()).equals((Object)AttributeType.INT) || type.equals((Object)AttributeType.FLOAT) || type.equals((Object)AttributeType.DOUBLE))) {
                            weight = Double.parseDouble(attributeWeight.getValue());
                        }
                        Triple et = Triple.of((Object)this.sourceId, (Object)this.targetId, (Object)weight);
                        if (JSONEventDrivenImporter.this.notifyEdgeAttributesOutOfOrder) {
                            JSONEventDrivenImporter.this.notifyEdge(et);
                            for (Map.Entry<String, Attribute> entry : this.attributes.entrySet()) {
                                JSONEventDrivenImporter.this.notifyEdgeAttribute(et, entry.getKey(), entry.getValue());
                            }
                        } else {
                            JSONEventDrivenImporter.this.notifyEdgeWithAttributes(et, this.attributes);
                        }
                    } else {
                        if (this.sourceId == null) {
                            throw new IllegalArgumentException("Edge with missing source detected");
                        }
                        throw new IllegalArgumentException("Edge with missing target detected");
                    }
                    this.insideEdge = false;
                    this.attributes = null;
                }
            }
            --this.objectLevel;
        }

        @Override
        public void enterArray(JsonParser.ArrayContext ctx) {
            ++this.arrayLevel;
            if (this.insideNodes && this.objectLevel == 1 && this.arrayLevel == 1) {
                this.insideNodesArray = true;
            } else if (this.insideEdges && this.objectLevel == 1 && this.arrayLevel == 1) {
                this.insideEdgesArray = true;
            }
        }

        @Override
        public void exitArray(JsonParser.ArrayContext ctx) {
            if (this.insideNodes && this.objectLevel == 1 && this.arrayLevel == 1) {
                this.insideNodesArray = false;
            } else if (this.insideEdges && this.objectLevel == 1 && this.arrayLevel == 1) {
                this.insideEdgesArray = false;
            }
            --this.arrayLevel;
        }

        @Override
        public void enterPair(JsonParser.PairContext ctx) {
            String name = this.unquote(ctx.STRING().getText());
            if (this.objectLevel == 1 && this.arrayLevel == 0) {
                if (JSONEventDrivenImporter.this.verticesCollectionName.equals(name)) {
                    this.insideNodes = true;
                } else if (JSONEventDrivenImporter.this.edgesCollectionName.equals(name)) {
                    this.insideEdges = true;
                }
            }
            this.pairNames.push(name);
        }

        @Override
        public void exitPair(JsonParser.PairContext ctx) {
            String name = this.unquote(ctx.STRING().getText());
            if (this.objectLevel == 1 && this.arrayLevel == 0) {
                if (JSONEventDrivenImporter.this.verticesCollectionName.equals(name)) {
                    this.insideNodes = false;
                } else if (JSONEventDrivenImporter.this.edgesCollectionName.equals(name)) {
                    this.insideEdges = false;
                }
            }
            this.pairNames.pop();
        }

        @Override
        public void enterValue(JsonParser.ValueContext ctx) {
            String name = this.pairNames.element();
            if (this.objectLevel == 2 && this.arrayLevel < 2) {
                if (this.insideNode) {
                    if (ID.equals(name)) {
                        this.nodeId = this.readIdentifier(ctx);
                    } else {
                        this.attributes.put(name, this.readAttribute(ctx));
                    }
                } else if (this.insideEdge) {
                    if (SOURCE.equals(name)) {
                        this.sourceId = this.readIdentifier(ctx);
                    } else if (TARGET.equals(name)) {
                        this.targetId = this.readIdentifier(ctx);
                    } else {
                        this.attributes.put(name, this.readAttribute(ctx));
                    }
                }
            }
        }

        private Attribute readAttribute(JsonParser.ValueContext ctx) {
            String other;
            String stringValue = this.readString(ctx);
            if (stringValue != null) {
                return DefaultAttribute.createAttribute(stringValue);
            }
            TerminalNode tn = ctx.NUMBER();
            if (tn != null) {
                String value = tn.getText();
                try {
                    return DefaultAttribute.createAttribute(Integer.parseInt(value, 10));
                }
                catch (NumberFormatException numberFormatException) {
                    try {
                        return DefaultAttribute.createAttribute(Long.parseLong(value, 10));
                    }
                    catch (NumberFormatException numberFormatException2) {
                        try {
                            return DefaultAttribute.createAttribute(Double.parseDouble(value));
                        }
                        catch (NumberFormatException numberFormatException3) {
                            // empty catch block
                        }
                    }
                }
            }
            if ((other = ctx.getText()) != null) {
                if ("true".equals(other)) {
                    return DefaultAttribute.createAttribute(Boolean.TRUE);
                }
                if ("false".equals(other)) {
                    return DefaultAttribute.createAttribute(Boolean.FALSE);
                }
                if ("null".equals(other)) {
                    return DefaultAttribute.NULL;
                }
                return new DefaultAttribute<String>(other, AttributeType.UNKNOWN);
            }
            return DefaultAttribute.NULL;
        }

        private String unquote(String value) {
            if (value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1);
            }
            value = StringEscapeUtils.unescapeJson((String)value);
            return value;
        }

        private String readString(JsonParser.ValueContext ctx) {
            TerminalNode tn = ctx.STRING();
            if (tn == null) {
                return null;
            }
            return this.unquote(tn.getText());
        }

        private String readIdentifier(JsonParser.ValueContext ctx) {
            TerminalNode tn = ctx.STRING();
            if (tn != null) {
                return this.unquote(tn.getText());
            }
            tn = ctx.NUMBER();
            if (tn == null) {
                return null;
            }
            try {
                return Long.valueOf(tn.getText(), 10).toString();
            }
            catch (NumberFormatException numberFormatException) {
                throw new IllegalArgumentException("Failed to read valid identifier");
            }
        }
    }
}

