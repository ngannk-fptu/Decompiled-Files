/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jgrapht.alg.util.Pair
 *  org.jgrapht.alg.util.Triple
 */
package org.jgrapht.nio.graphml;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.alg.util.Triple;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.BaseEventDrivenImporter;
import org.jgrapht.nio.EventDrivenImporter;
import org.jgrapht.nio.ImportEvent;
import org.jgrapht.nio.ImportException;
import org.jgrapht.nio.graphml.SimpleGraphMLEventDrivenImporter;

public class SimpleGraphMLEdgeListImporter
extends BaseEventDrivenImporter<Integer, Triple<Integer, Integer, Double>>
implements EventDrivenImporter<Integer, Triple<Integer, Integer, Double>> {
    private static final String EDGE_WEIGHT_DEFAULT_ATTRIBUTE_NAME = "weight";
    private boolean schemaValidation = true;
    private String edgeWeightAttributeName = "weight";

    public String getEdgeWeightAttributeName() {
        return this.edgeWeightAttributeName;
    }

    public void setEdgeWeightAttributeName(String edgeWeightAttributeName) {
        this.edgeWeightAttributeName = Objects.requireNonNull(edgeWeightAttributeName, "Edge weight attribute name cannot be null");
    }

    public boolean isSchemaValidation() {
        return this.schemaValidation;
    }

    public void setSchemaValidation(boolean schemaValidation) {
        this.schemaValidation = schemaValidation;
    }

    @Override
    public void importInput(Reader input) throws ImportException {
        SimpleGraphMLEventDrivenImporter genericImporter = new SimpleGraphMLEventDrivenImporter();
        genericImporter.setEdgeWeightAttributeName(this.edgeWeightAttributeName);
        genericImporter.setSchemaValidation(this.schemaValidation);
        Consumers consumers = new Consumers();
        genericImporter.addImportEventConsumer(consumers.eventConsumer);
        genericImporter.addVertexConsumer(consumers.vertexConsumer);
        genericImporter.addEdgeConsumer(consumers.edgeConsumer);
        genericImporter.addEdgeAttributeConsumer(consumers.edgeAttributeConsumer);
        genericImporter.importInput(input);
    }

    private class Consumers {
        private int nodeCount = 0;
        private Map<String, Integer> vertexMap;
        private Triple<Integer, Integer, Double> lastIntegerTriple;
        private Triple<String, String, Double> lastTriple;
        public final Consumer<ImportEvent> eventConsumer = e -> {
            if (ImportEvent.END.equals(e) && this.lastTriple != null) {
                SimpleGraphMLEdgeListImporter.this.notifyEdge(this.lastIntegerTriple);
                this.lastTriple = null;
                this.lastIntegerTriple = null;
            }
        };
        public final Consumer<String> vertexConsumer = v -> this.vertexMap.computeIfAbsent((String)v, k -> this.nodeCount++);
        public final BiConsumer<Pair<Triple<String, String, Double>, String>, Attribute> edgeAttributeConsumer = (edgeAndKey, a) -> {
            Triple q = (Triple)edgeAndKey.getFirst();
            String keyName = (String)edgeAndKey.getSecond();
            if (this.lastTriple == q && SimpleGraphMLEdgeListImporter.this.edgeWeightAttributeName.equals(keyName)) {
                this.lastTriple.setThird((Object)((Double)q.getThird()));
                this.lastIntegerTriple.setThird((Object)((Double)q.getThird()));
            }
        };
        public final Consumer<Triple<String, String, Double>> edgeConsumer = q -> {
            if (q != this.lastTriple) {
                if (this.lastTriple != null) {
                    SimpleGraphMLEdgeListImporter.this.notifyEdge(this.lastIntegerTriple);
                }
                this.lastTriple = q;
                this.lastIntegerTriple = this.createIntegerTriple((Triple<String, String, Double>)q);
            }
        };

        public Consumers() {
            this.vertexMap = new HashMap<String, Integer>();
        }

        private Triple<Integer, Integer, Double> createIntegerTriple(Triple<String, String, Double> e) {
            int source = this.vertexMap.computeIfAbsent((String)e.getFirst(), k -> this.nodeCount++);
            int target = this.vertexMap.computeIfAbsent((String)e.getSecond(), k -> this.nodeCount++);
            Double weight = (Double)e.getThird();
            return Triple.of((Object)source, (Object)target, (Object)weight);
        }
    }
}

