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
 *  org.apache.commons.text.StringEscapeUtils
 *  org.jgrapht.alg.util.Triple
 */
package org.jgrapht.nio.gml;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.apache.commons.text.StringEscapeUtils;
import org.jgrapht.alg.util.Triple;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.BaseEventDrivenImporter;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.EventDrivenImporter;
import org.jgrapht.nio.ImportEvent;
import org.jgrapht.nio.ImportException;
import org.jgrapht.nio.gml.GmlBaseListener;
import org.jgrapht.nio.gml.GmlLexer;
import org.jgrapht.nio.gml.GmlParser;

public class GmlEventDrivenImporter
extends BaseEventDrivenImporter<Integer, Triple<Integer, Integer, Double>>
implements EventDrivenImporter<Integer, Triple<Integer, Integer, Double>> {
    @Override
    public void importInput(Reader input) throws ImportException {
        try {
            ThrowingErrorListener errorListener = new ThrowingErrorListener();
            GmlLexer lexer = new GmlLexer((CharStream)CharStreams.fromReader((Reader)input));
            lexer.removeErrorListeners();
            lexer.addErrorListener((ANTLRErrorListener)errorListener);
            GmlParser parser = new GmlParser((TokenStream)new CommonTokenStream((TokenSource)lexer));
            parser.removeErrorListeners();
            parser.addErrorListener((ANTLRErrorListener)errorListener);
            GmlParser.GmlContext graphContext = parser.gml();
            ParseTreeWalker walker = new ParseTreeWalker();
            NotifyGmlListener listener = new NotifyGmlListener();
            this.notifyImportEvent(ImportEvent.START);
            walker.walk((ParseTreeListener)listener, (ParseTree)graphContext);
            listener.notifySingletons();
            this.notifyImportEvent(ImportEvent.END);
        }
        catch (IOException | IllegalArgumentException | ParseCancellationException e) {
            throw new ImportException("Failed to import gml graph: " + e.getMessage(), e);
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

    private class NotifyGmlListener
    extends GmlBaseListener {
        private static final String NODE = "node";
        private static final String EDGE = "edge";
        private static final String GRAPH = "graph";
        private static final String WEIGHT = "weight";
        private static final String ID = "id";
        private static final String SOURCE = "source";
        private static final String TARGET = "target";
        private boolean insideGraph;
        private boolean insideNode;
        private boolean insideEdge;
        private int level;
        private Integer nodeId;
        private Integer sourceId;
        private Integer targetId;
        private Double weight;
        private Map<String, Attribute> attributes;
        private StringBuilder stringBuffer;
        private int maxNodeId;
        private List<Singleton> singletons;

        private NotifyGmlListener() {
        }

        public void notifySingletons() {
            for (Singleton s : this.singletons) {
                ++this.maxNodeId;
                GmlEventDrivenImporter.this.notifyVertex(this.maxNodeId);
                for (String attrKey : s.attributes.keySet()) {
                    GmlEventDrivenImporter.this.notifyVertexAttribute(this.maxNodeId, attrKey, s.attributes.get(attrKey));
                }
            }
        }

        @Override
        public void enterGml(GmlParser.GmlContext ctx) {
            this.insideGraph = false;
            this.insideNode = false;
            this.insideEdge = false;
            this.level = 0;
            this.singletons = new ArrayList<Singleton>();
            this.maxNodeId = 0;
        }

        @Override
        public void enterNumberKeyValue(GmlParser.NumberKeyValueContext ctx) {
            if (!this.insideNode && !this.insideEdge) {
                return;
            }
            if (this.level < 2) {
                return;
            }
            String key = ctx.ID().getText();
            String value = ctx.NUMBER().getText();
            if (this.level == 2) {
                if (this.insideNode) {
                    if (key.equals(ID)) {
                        try {
                            this.nodeId = Integer.parseInt(value);
                        }
                        catch (NumberFormatException numberFormatException) {}
                    } else {
                        this.attributes.put(key, this.parseNumberAttribute(value));
                    }
                } else {
                    assert (this.insideEdge);
                    switch (key) {
                        case "source": {
                            try {
                                this.sourceId = Integer.parseInt(value);
                            }
                            catch (NumberFormatException numberFormatException) {}
                            break;
                        }
                        case "target": {
                            try {
                                this.targetId = Integer.parseInt(value);
                            }
                            catch (NumberFormatException numberFormatException) {}
                            break;
                        }
                        case "weight": {
                            try {
                                this.weight = Double.parseDouble(value);
                            }
                            catch (NumberFormatException numberFormatException) {}
                            break;
                        }
                        default: {
                            this.attributes.put(key, this.parseNumberAttribute(value));
                            break;
                        }
                    }
                }
            } else {
                assert (this.level >= 3);
                this.stringBuffer.append(' ');
                this.stringBuffer.append(key);
                this.stringBuffer.append(' ');
                this.stringBuffer.append(value);
            }
        }

        @Override
        public void enterListKeyValue(GmlParser.ListKeyValueContext ctx) {
            String key = ctx.ID().getText();
            if (this.level == 0 && key.equals(GRAPH)) {
                this.insideGraph = true;
            } else if (this.level == 1 && this.insideGraph && key.equals(NODE)) {
                this.insideNode = true;
                this.nodeId = null;
                this.attributes = new HashMap<String, Attribute>();
            } else if (this.level == 1 && this.insideGraph && key.equals(EDGE)) {
                this.insideEdge = true;
                this.sourceId = null;
                this.targetId = null;
                this.weight = null;
                this.attributes = new HashMap<String, Attribute>();
            } else if (this.insideNode || this.insideEdge) {
                if (this.level == 2) {
                    this.stringBuffer = new StringBuilder();
                    this.stringBuffer.append('[');
                } else if (this.level >= 3) {
                    this.stringBuffer.append(' ');
                    this.stringBuffer.append(key);
                    this.stringBuffer.append(' ');
                    this.stringBuffer.append('[');
                }
            }
            ++this.level;
        }

        @Override
        public void exitListKeyValue(GmlParser.ListKeyValueContext ctx) {
            String key = ctx.ID().getText();
            --this.level;
            if (this.level == 0 && key.equals(GRAPH)) {
                this.insideGraph = false;
            } else if (this.level == 1 && this.insideGraph && key.equals(NODE)) {
                if (this.nodeId == null) {
                    this.singletons.add(new Singleton(this.attributes));
                } else {
                    GmlEventDrivenImporter.this.notifyVertex(this.nodeId);
                    for (String attrKey : this.attributes.keySet()) {
                        GmlEventDrivenImporter.this.notifyVertexAttribute(this.nodeId, attrKey, this.attributes.get(attrKey));
                    }
                    this.maxNodeId = Math.max(this.maxNodeId, this.nodeId);
                }
                this.insideNode = false;
                this.attributes = null;
            } else if (this.level == 1 && this.insideGraph && key.equals(EDGE)) {
                if (this.sourceId != null && this.targetId != null) {
                    Triple et = Triple.of((Object)this.sourceId, (Object)this.targetId, (Object)this.weight);
                    GmlEventDrivenImporter.this.notifyEdge(et);
                    if (this.weight != null) {
                        GmlEventDrivenImporter.this.notifyEdgeAttribute(et, WEIGHT, DefaultAttribute.createAttribute(this.weight));
                    }
                    for (String attrKey : this.attributes.keySet()) {
                        GmlEventDrivenImporter.this.notifyEdgeAttribute(et, attrKey, this.attributes.get(attrKey));
                    }
                }
                this.insideEdge = false;
                this.attributes = null;
            } else if (this.insideNode || this.insideEdge) {
                if (this.level == 2) {
                    this.stringBuffer.append(' ');
                    this.stringBuffer.append(']');
                    this.attributes.put(key, new DefaultAttribute<String>(this.stringBuffer.toString(), AttributeType.UNKNOWN));
                    this.stringBuffer = null;
                } else if (this.level >= 3) {
                    this.stringBuffer.append(' ');
                    this.stringBuffer.append(']');
                }
            }
        }

        @Override
        public void enterStringKeyValue(GmlParser.StringKeyValueContext ctx) {
            if (!this.insideNode && !this.insideEdge) {
                return;
            }
            if (this.level < 2) {
                return;
            }
            String key = ctx.ID().getText();
            String text = ctx.STRING().getText();
            String noQuotes = text.subSequence(1, text.length() - 1).toString();
            String unescapedText = StringEscapeUtils.unescapeJava((String)noQuotes);
            if (this.level == 2) {
                if (key.equals(ID)) {
                    throw new IllegalArgumentException("Invalid type for attribute id: string");
                }
                if (key.equals(SOURCE)) {
                    throw new IllegalArgumentException("Invalid type for attribute source: string");
                }
                if (key.equals(TARGET)) {
                    throw new IllegalArgumentException("Invalid type for attribute target: string");
                }
                if (key.equals(WEIGHT)) {
                    throw new IllegalArgumentException("Invalid type for attribute weight: string");
                }
                this.attributes.put(key, DefaultAttribute.createAttribute(unescapedText));
            } else if (this.level >= 3) {
                this.stringBuffer.append(' ');
                this.stringBuffer.append(key);
                this.stringBuffer.append(' ');
                this.stringBuffer.append(text);
            }
        }

        private Attribute parseNumberAttribute(String value) {
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
                        return DefaultAttribute.createAttribute(value);
                    }
                }
            }
        }
    }

    private class Singleton {
        Map<String, Attribute> attributes;

        public Singleton(Map<String, Attribute> attributes) {
            this.attributes = attributes;
        }
    }
}

