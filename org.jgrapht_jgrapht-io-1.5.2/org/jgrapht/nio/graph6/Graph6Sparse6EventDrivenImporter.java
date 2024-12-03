/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jgrapht.alg.util.Pair
 */
package org.jgrapht.nio.graph6;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.nio.BaseEventDrivenImporter;
import org.jgrapht.nio.EventDrivenImporter;
import org.jgrapht.nio.ImportEvent;
import org.jgrapht.nio.ImportException;

public class Graph6Sparse6EventDrivenImporter
extends BaseEventDrivenImporter<Integer, Pair<Integer, Integer>>
implements EventDrivenImporter<Integer, Pair<Integer, Integer>> {
    private static final String GRAPH_STRING_SEEMS_TO_BE_CORRUPT_INVALID_NUMBER_OF_VERTICES = "Graph string seems to be corrupt. Invalid number of vertices.";

    @Override
    public void importInput(Reader input) throws ImportException {
        BufferedReader in = input instanceof BufferedReader ? (BufferedReader)input : new BufferedReader(input);
        this.notifyImportEvent(ImportEvent.START);
        String g6 = null;
        try {
            g6 = in.readLine();
        }
        catch (IOException e) {
            throw new ImportException("Failed to read graph: " + e.getMessage());
        }
        if (g6.isEmpty()) {
            throw new ImportException("Failed to read graph: empty line");
        }
        g6 = g6.replace("\n", "").replace("\r", "");
        new Parser(g6).parse();
        this.notifyImportEvent(ImportEvent.END);
    }

    private class Parser {
        private Format format = Format.GRAPH6;
        private byte[] bytes;
        private int byteIndex;
        private int bitIndex;
        private int n;

        public Parser(String inputLine) {
            if (inputLine.startsWith(":")) {
                inputLine = inputLine.substring(1, inputLine.length());
                this.format = Format.SPARSE6;
            } else if (inputLine.startsWith(">>sparse6<<:")) {
                inputLine = inputLine.substring(12, inputLine.length());
                this.format = Format.SPARSE6;
            } else if (inputLine.startsWith(">>graph6<<")) {
                inputLine = inputLine.substring(10, inputLine.length());
            }
            this.bytes = inputLine.getBytes();
            this.byteIndex = 0;
            this.bitIndex = 0;
            this.n = 0;
        }

        public void parse() {
            this.validateInput();
            this.readNumberOfVertices();
            Graph6Sparse6EventDrivenImporter.this.notifyVertexCount(this.n);
            for (int i = 0; i < this.n; ++i) {
                Graph6Sparse6EventDrivenImporter.this.notifyVertex(i);
            }
            if (this.format == Format.GRAPH6) {
                this.readGraph6();
            } else {
                this.readSparse6();
            }
        }

        private void readGraph6() throws ImportException {
            int requiredBytes = (int)Math.ceil((double)(this.n * (this.n - 1)) / 12.0) + this.byteIndex;
            if (this.bytes.length < requiredBytes) {
                throw new ImportException("Graph string seems to be corrupt. Not enough data to read graph6 graph");
            }
            for (int i = 0; i < this.n; ++i) {
                for (int j = 0; j < i; ++j) {
                    int bit = this.getBits(1);
                    if (bit != 1) continue;
                    Graph6Sparse6EventDrivenImporter.this.notifyEdge(Pair.of((Object)i, (Object)j));
                }
            }
        }

        private void readSparse6() throws ImportException {
            int k = (int)Math.ceil(Math.log(this.n) / Math.log(2.0));
            int v = 0;
            for (int dataBits = this.bytes.length * 6 - (this.byteIndex * 6 + this.bitIndex); dataBits >= 1 + k; dataBits -= 1 + k) {
                int b = this.getBits(1);
                int x = this.getBits(k);
                if (b == 1) {
                    ++v;
                }
                if (v >= this.n) break;
                if (x > v) {
                    v = x;
                    continue;
                }
                Graph6Sparse6EventDrivenImporter.this.notifyEdge(Pair.of((Object)x, (Object)v));
            }
        }

        private void validateInput() throws ImportException {
            for (byte b : this.bytes) {
                if (b >= 63 && b <= 126) continue;
                throw new ImportException("Graph string seems to be corrupt. Illegal character detected: " + b);
            }
        }

        private void readNumberOfVertices() throws ImportException {
            int n;
            if (this.bytes.length > 8 && this.bytes[0] == 126 && this.bytes[1] == 126) {
                this.byteIndex += 2;
                n = this.getBits(36);
                if (n < 258048) {
                    throw new ImportException(Graph6Sparse6EventDrivenImporter.GRAPH_STRING_SEEMS_TO_BE_CORRUPT_INVALID_NUMBER_OF_VERTICES);
                }
            } else if (this.bytes.length > 4 && this.bytes[0] == 126) {
                ++this.byteIndex;
                n = this.getBits(18);
                if (n < 63 || n > 258047) {
                    throw new ImportException(Graph6Sparse6EventDrivenImporter.GRAPH_STRING_SEEMS_TO_BE_CORRUPT_INVALID_NUMBER_OF_VERTICES);
                }
            } else {
                n = this.getBits(6);
                if (n < 0 || n > 62) {
                    throw new ImportException(Graph6Sparse6EventDrivenImporter.GRAPH_STRING_SEEMS_TO_BE_CORRUPT_INVALID_NUMBER_OF_VERTICES);
                }
            }
            this.n = n;
        }

        private int getBits(int k) throws ImportException {
            int value = 0;
            if (this.bitIndex > 0 || k < 6) {
                int x = Math.min(k, 6 - this.bitIndex);
                int mask = (1 << x) - 1;
                int y = this.bytes[this.byteIndex] - 63 >> 6 - this.bitIndex - x;
                value = (value << k) + (y &= mask);
                k -= x;
                this.bitIndex += x;
                if (this.bitIndex == 6) {
                    ++this.byteIndex;
                    this.bitIndex = 0;
                }
            }
            int blocks = k / 6;
            for (int j = 0; j < blocks; ++j) {
                value = (value << 6) + this.bytes[this.byteIndex] - 63;
                ++this.byteIndex;
                k -= 6;
            }
            if (k > 0) {
                int y = this.bytes[this.byteIndex] - 63;
                value = (value << k) + (y >>= 6 - k);
                this.bitIndex = k;
            }
            return value;
        }
    }

    static enum Format {
        GRAPH6,
        SPARSE6;

    }
}

