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
 *  org.jgrapht.alg.util.Triple
 */
package org.jgrapht.nio.csv;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.jgrapht.alg.util.Triple;
import org.jgrapht.nio.BaseEventDrivenImporter;
import org.jgrapht.nio.EventDrivenImporter;
import org.jgrapht.nio.ImportEvent;
import org.jgrapht.nio.ImportException;
import org.jgrapht.nio.csv.CSVBaseListener;
import org.jgrapht.nio.csv.CSVFormat;
import org.jgrapht.nio.csv.CSVLexer;
import org.jgrapht.nio.csv.CSVParser;
import org.jgrapht.nio.csv.DSVUtils;

public class CSVEventDrivenImporter
extends BaseEventDrivenImporter<String, Triple<String, String, Double>>
implements EventDrivenImporter<String, Triple<String, String, Double>> {
    private static final char DEFAULT_DELIMITER = ',';
    private CSVFormat format;
    private char delimiter;
    private final Set<CSVFormat.Parameter> parameters;

    public CSVEventDrivenImporter() {
        this(CSVFormat.ADJACENCY_LIST, ',');
    }

    public CSVEventDrivenImporter(CSVFormat format) {
        this(format, ',');
    }

    public CSVEventDrivenImporter(CSVFormat format, char delimiter) {
        this.format = format;
        if (!DSVUtils.isValidDelimiter(delimiter)) {
            throw new IllegalArgumentException("Character cannot be used as a delimiter");
        }
        this.delimiter = delimiter;
        this.parameters = new HashSet<CSVFormat.Parameter>();
    }

    public CSVFormat getFormat() {
        return this.format;
    }

    public void setFormat(CSVFormat format) {
        this.format = format;
    }

    public char getDelimiter() {
        return this.delimiter;
    }

    public void setDelimiter(char delimiter) {
        if (!DSVUtils.isValidDelimiter(delimiter)) {
            throw new IllegalArgumentException("Character cannot be used as a delimiter");
        }
        this.delimiter = delimiter;
    }

    public boolean isParameter(CSVFormat.Parameter p) {
        return this.parameters.contains((Object)p);
    }

    public void setParameter(CSVFormat.Parameter p, boolean value) {
        if (value) {
            this.parameters.add(p);
        } else {
            this.parameters.remove((Object)p);
        }
    }

    @Override
    public void importInput(Reader input) throws ImportException {
        this.notifyImportEvent(ImportEvent.START);
        switch (this.format) {
            case EDGE_LIST: 
            case ADJACENCY_LIST: {
                this.read(input, new AdjacencyListCSVListener());
                break;
            }
            case MATRIX: {
                this.read(input, new MatrixCSVListener());
            }
        }
        this.notifyImportEvent(ImportEvent.END);
    }

    private void read(Reader input, CSVBaseListener listener) throws ImportException {
        try {
            ThrowingErrorListener errorListener = new ThrowingErrorListener();
            CSVLexer lexer = new CSVLexer((CharStream)CharStreams.fromReader((Reader)input));
            lexer.setSep(this.delimiter);
            lexer.removeErrorListeners();
            lexer.addErrorListener((ANTLRErrorListener)errorListener);
            CSVParser parser = new CSVParser((TokenStream)new CommonTokenStream((TokenSource)lexer));
            parser.removeErrorListeners();
            parser.addErrorListener((ANTLRErrorListener)errorListener);
            CSVParser.FileContext graphContext = parser.file();
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk((ParseTreeListener)listener, (ParseTree)graphContext);
        }
        catch (IOException | IllegalArgumentException | ParseCancellationException e) {
            throw new ImportException("Failed to import CSV graph: " + e.getMessage(), e);
        }
    }

    private class AdjacencyListCSVListener
    extends RowCSVListener {
        private boolean assumeEdgeWeights;

        public AdjacencyListCSVListener() {
            this.assumeEdgeWeights = CSVEventDrivenImporter.this.parameters.contains((Object)CSVFormat.Parameter.EDGE_WEIGHTS);
        }

        @Override
        protected void handleRow() {
            String source = (String)this.row.get(0);
            if (source.isEmpty()) {
                throw new ParseCancellationException("Source vertex cannot be empty");
            }
            if (!this.vertices.contains(source)) {
                this.vertices.add(source);
                CSVEventDrivenImporter.this.notifyVertex(source);
            }
            this.row.remove(0);
            int step = this.assumeEdgeWeights ? 2 : 1;
            for (int i = 0; i < this.row.size(); i += step) {
                String target = (String)this.row.get(i);
                if (target.isEmpty()) {
                    throw new ParseCancellationException("Target vertex cannot be empty");
                }
                if (!this.vertices.contains(target)) {
                    this.vertices.add(target);
                    CSVEventDrivenImporter.this.notifyVertex(target);
                }
                Double weight = null;
                if (this.assumeEdgeWeights) {
                    try {
                        weight = Double.parseDouble((String)this.row.get(i + 1));
                    }
                    catch (NumberFormatException nfe) {
                        throw new ParseCancellationException("Failed to parse edge weight");
                    }
                }
                CSVEventDrivenImporter.this.notifyEdge(Triple.of((Object)source, (Object)target, weight));
            }
        }
    }

    private class MatrixCSVListener
    extends RowCSVListener {
        private boolean assumeNodeIds;
        private boolean assumeEdgeWeights;
        private boolean assumeZeroWhenNoEdge;
        private int verticesCount;
        private int currentVertex;
        private String currentVertexName;
        private Map<Integer, String> columnIndex;

        public MatrixCSVListener() {
            this.assumeNodeIds = CSVEventDrivenImporter.this.parameters.contains((Object)CSVFormat.Parameter.MATRIX_FORMAT_NODEID);
            this.assumeEdgeWeights = CSVEventDrivenImporter.this.parameters.contains((Object)CSVFormat.Parameter.EDGE_WEIGHTS);
            this.assumeZeroWhenNoEdge = CSVEventDrivenImporter.this.parameters.contains((Object)CSVFormat.Parameter.MATRIX_FORMAT_ZERO_WHEN_NO_EDGE);
            this.verticesCount = 0;
            this.currentVertex = 1;
            this.currentVertexName = null;
            this.columnIndex = new HashMap<Integer, String>();
        }

        @Override
        protected void handleRow() {
            if (this.assumeNodeIds) {
                if (!this.header) {
                    this.currentVertexName = (String)this.row.get(0);
                }
                this.row.remove(0);
            } else {
                this.currentVertexName = String.valueOf(this.currentVertex);
            }
            if (this.header) {
                if (this.assumeNodeIds) {
                    this.createVerticesFromNodeIds();
                } else {
                    this.createVertices();
                    this.createEdges();
                    ++this.currentVertex;
                }
            } else {
                this.createEdges();
                ++this.currentVertex;
            }
        }

        private void createVerticesFromNodeIds() {
            this.verticesCount = this.row.size();
            if (this.verticesCount < 1) {
                throw new ParseCancellationException("Failed to parse header with vertices");
            }
            int v = 1;
            for (String vertexName : this.row) {
                if (vertexName.trim().isEmpty()) {
                    throw new ParseCancellationException("Failed to parse header with vertices (empty name)");
                }
                if (!this.vertices.contains(vertexName)) {
                    this.vertices.add(vertexName);
                    CSVEventDrivenImporter.this.notifyVertex(vertexName);
                }
                this.columnIndex.put(v, vertexName);
                ++v;
            }
        }

        private void createVertices() {
            this.verticesCount = this.row.size();
            if (this.verticesCount < 1) {
                throw new ParseCancellationException("Failed to parse header with vertices");
            }
            int v = 1;
            for (v = 1; v <= this.verticesCount; ++v) {
                String vertexName = String.valueOf(v);
                if (!this.vertices.contains(vertexName)) {
                    this.vertices.add(vertexName);
                    CSVEventDrivenImporter.this.notifyVertex(vertexName);
                }
                this.columnIndex.put(v, vertexName);
            }
        }

        private void createEdges() {
            if (this.row.size() != this.verticesCount) {
                throw new ParseCancellationException("Row contains fewer than " + this.verticesCount + " entries");
            }
            int target = 1;
            for (String entry : this.row) {
                try {
                    Integer entryAsInteger = Integer.parseInt(entry);
                    if (entryAsInteger == 0) {
                        if (!this.assumeZeroWhenNoEdge && this.assumeEdgeWeights) {
                            CSVEventDrivenImporter.this.notifyEdge(Triple.of((Object)this.currentVertexName, (Object)this.columnIndex.get(target), (Object)0.0));
                        }
                    } else if (this.assumeEdgeWeights) {
                        CSVEventDrivenImporter.this.notifyEdge(Triple.of((Object)this.currentVertexName, (Object)this.columnIndex.get(target), (Object)entryAsInteger));
                    } else {
                        CSVEventDrivenImporter.this.notifyEdge(Triple.of((Object)this.currentVertexName, (Object)this.columnIndex.get(target), null));
                    }
                    ++target;
                }
                catch (NumberFormatException entryAsInteger) {
                    try {
                        Double entryAsDouble = Double.parseDouble(entry);
                        if (!this.assumeEdgeWeights) {
                            throw new ParseCancellationException("Double entry found when expecting no weights");
                        }
                        CSVEventDrivenImporter.this.notifyEdge(Triple.of((Object)this.currentVertexName, (Object)this.columnIndex.get(target), (Object)entryAsDouble));
                    }
                    catch (NumberFormatException numberFormatException) {
                        // empty catch block
                    }
                    ++target;
                }
            }
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

    private abstract class RowCSVListener
    extends CSVBaseListener {
        protected List<String> row = new ArrayList<String>();
        protected Set<String> vertices = new HashSet<String>();
        protected boolean header = false;

        @Override
        public void enterHeader(CSVParser.HeaderContext ctx) {
            this.header = true;
        }

        @Override
        public void exitHeader(CSVParser.HeaderContext ctx) {
            this.header = false;
        }

        @Override
        public void enterRecord(CSVParser.RecordContext ctx) {
            this.row.clear();
        }

        @Override
        public void exitRecord(CSVParser.RecordContext ctx) {
            if (this.row.isEmpty()) {
                throw new ParseCancellationException("Empty CSV record");
            }
            this.handleRow();
        }

        @Override
        public void exitTextField(CSVParser.TextFieldContext ctx) {
            this.row.add(ctx.TEXT().getText());
        }

        @Override
        public void exitStringField(CSVParser.StringFieldContext ctx) {
            this.row.add(DSVUtils.unescapeDSV(ctx.STRING().getText(), CSVEventDrivenImporter.this.delimiter));
        }

        @Override
        public void exitEmptyField(CSVParser.EmptyFieldContext ctx) {
            this.row.add("");
        }

        protected abstract void handleRow();
    }
}

