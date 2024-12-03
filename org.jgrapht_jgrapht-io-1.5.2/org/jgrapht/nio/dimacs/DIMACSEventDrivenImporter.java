/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jgrapht.alg.util.Triple
 */
package org.jgrapht.nio.dimacs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.jgrapht.alg.util.Triple;
import org.jgrapht.nio.BaseEventDrivenImporter;
import org.jgrapht.nio.EventDrivenImporter;
import org.jgrapht.nio.ImportEvent;
import org.jgrapht.nio.ImportException;

public class DIMACSEventDrivenImporter
extends BaseEventDrivenImporter<Integer, Triple<Integer, Integer, Double>>
implements EventDrivenImporter<Integer, Triple<Integer, Integer, Double>> {
    private boolean zeroBasedNumbering = true;
    private boolean renumberVertices = true;
    private Map<String, Integer> vertexMap = new HashMap<String, Integer>();
    private int nextId;

    public DIMACSEventDrivenImporter zeroBasedNumbering(boolean zeroBasedNumbering) {
        this.zeroBasedNumbering = zeroBasedNumbering;
        return this;
    }

    public DIMACSEventDrivenImporter renumberVertices(boolean renumberVertices) {
        this.renumberVertices = renumberVertices;
        return this;
    }

    @Override
    public void importInput(Reader input) {
        BufferedReader in = input instanceof BufferedReader ? (BufferedReader)input : new BufferedReader(input);
        this.nextId = this.zeroBasedNumbering ? 0 : 1;
        this.notifyImportEvent(ImportEvent.START);
        int size = this.readNodeCount(in);
        this.notifyVertexCount(size);
        Object[] cols = this.skipComments(in);
        while (cols != null) {
            if (cols[0].equals("e") || cols[0].equals("a")) {
                Integer target;
                Integer source;
                if (cols.length < 3) {
                    throw new ImportException("Failed to parse edge:" + Arrays.toString(cols));
                }
                try {
                    source = Integer.parseInt((String)cols[1]);
                }
                catch (NumberFormatException e) {
                    throw new ImportException("Failed to parse edge source node:" + e.getMessage(), e);
                }
                try {
                    target = Integer.parseInt((String)cols[2]);
                }
                catch (NumberFormatException e) {
                    throw new ImportException("Failed to parse edge target node:" + e.getMessage(), e);
                }
                Integer from = this.mapVertexToInteger(String.valueOf(source));
                Integer to = this.mapVertexToInteger(String.valueOf(target));
                Double weight = null;
                if (cols.length > 3) {
                    try {
                        weight = Double.parseDouble((String)cols[3]);
                    }
                    catch (NumberFormatException numberFormatException) {
                        // empty catch block
                    }
                }
                this.notifyEdge(Triple.of((Object)from, (Object)to, weight));
            }
            cols = this.skipComments(in);
        }
        this.notifyImportEvent(ImportEvent.END);
    }

    private String[] split(String src) {
        if (src == null) {
            return null;
        }
        return src.split("\\s+");
    }

    private String[] skipComments(BufferedReader input) {
        String[] cols = null;
        try {
            cols = this.split(input.readLine());
            while (cols != null && (cols.length == 0 || cols[0].equals("c") || cols[0].startsWith("%"))) {
                cols = this.split(input.readLine());
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return cols;
    }

    private int readNodeCount(BufferedReader input) throws ImportException {
        String[] cols = this.skipComments(input);
        if (cols[0].equals("p")) {
            Integer nodes;
            if (cols.length < 3) {
                throw new ImportException("Failed to read number of vertices.");
            }
            try {
                nodes = Integer.parseInt(cols[2]);
            }
            catch (NumberFormatException e) {
                throw new ImportException("Failed to read number of vertices.");
            }
            if (nodes < 0) {
                throw new ImportException("Negative number of vertices.");
            }
            return nodes;
        }
        throw new ImportException("Failed to read number of vertices.");
    }

    protected Integer mapVertexToInteger(String id) {
        if (this.renumberVertices) {
            return this.vertexMap.computeIfAbsent(id, keyId -> this.nextId++);
        }
        if (this.zeroBasedNumbering) {
            return Integer.valueOf(id) - 1;
        }
        return Integer.valueOf(id);
    }
}

