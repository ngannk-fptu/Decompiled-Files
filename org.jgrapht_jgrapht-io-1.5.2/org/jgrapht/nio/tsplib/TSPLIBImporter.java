/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jgrapht.Graph
 *  org.jgrapht.generate.CompleteGraphGenerator
 *  org.jgrapht.util.CollectionUtil
 */
package org.jgrapht.nio.tsplib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.ToIntBiFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jgrapht.Graph;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.nio.GraphImporter;
import org.jgrapht.nio.ImportException;
import org.jgrapht.util.CollectionUtil;

public class TSPLIBImporter<V, E>
implements GraphImporter<V, E> {
    private static final String NAME = "NAME";
    private static final String TYPE = "TYPE";
    private static final String COMMENT = "COMMENT";
    private static final String DIMENSION = "DIMENSION";
    private static final String CAPACITY = "CAPACITY";
    private static final String EDGE_WEIGHT_TYPE = "EDGE_WEIGHT_TYPE";
    private static final String EDGE_WEIGHT_FORMAT = "EDGE_WEIGHT_FORMAT";
    private static final String EDGE_DATA_FORMAT = "EDGE_DATA_FORMAT";
    private static final String NODE_COORD_TYPE = "NODE_COORD_TYPE";
    private static final String DISPLAY_DATA_TYPE = "DISPLAY_DATA_TYPE";
    private static final String NODE_COORD_SECTION = "NODE_COORD_SECTION";
    private static final String TOUR_SECTION = "TOUR_SECTION";
    private static final List<String> VALID_TYPES = Arrays.asList("TSP", "ATSP", "SOP", "HCP", "CVRP", "TOUR");
    private static final List<String> VALID_EDGE_WEIGHT_TYPES = Arrays.asList("EXPLICIT", "EUC_2D", "EUC_3D", "MAX_2D", "MAX_3D", "MAN_2D", "MAN_3D", "CEIL_2D", "GEO", "ATT", "XRAY1", "XRAY2", "SPECIAL");
    private static final List<String> VALID_EDGE_WEIGHT_FORMATS = Arrays.asList("FUNCTION", "FULL_MATRIX", "UPPER_ROW", "LOWER_ROW", "UPPER_DIAG_ROW", "LOWER_DIAG_ROW", "UPPER_COL", "LOWER_COL", "UPPER_DIAG_COL", "LOWER_DIAG_COL");
    private static final List<String> VALID_EDGE_DATA_FORMATS = Arrays.asList("EDGE_LIST", "ADJ_LIST");
    private static final List<String> VALID_NODE_COORD_TYPES = Arrays.asList("TWOD_COORDS", "THREED_COORDS", "NO_COORDS");
    private static final List<String> VALID_DISPLAY_DATA_TYPE = Arrays.asList("COORD_DISPLAY", "TWOD_DISPLAY", "NO_DISPLAY");
    private int vectorLength = -1;
    private Metadata<V, E> metadata;
    private static final Pattern WHITE_SPACE = Pattern.compile("[ \t]+");
    static final double PI = 3.141592;
    static final double RRR = 6378.388;

    public Metadata<V, E> getMetadata() {
        return this.metadata;
    }

    @Override
    public void importGraph(Graph<V, E> graph, Reader in) {
        this.metadata = null;
        try {
            Iterator<String> lines = TSPLIBImporter.getLineIterator(in);
            this.metadata = this.readContentForGraph(lines, graph);
        }
        catch (Exception e) {
            throw TSPLIBImporter.getImportException(e, "graph");
        }
    }

    private Metadata<V, E> readContentForGraph(Iterator<String> lines, Graph<V, E> graph) {
        if (!graph.getType().isWeighted()) {
            throw new IllegalArgumentException("Graph must be weighted");
        }
        this.vectorLength = -1;
        Metadata data = new Metadata();
        List<Integer> tour = null;
        while (lines.hasNext()) {
            String[] keyValue = lines.next().split(":");
            String key = TSPLIBImporter.getKey(keyValue);
            if (this.readSpecificationSection(key, data.spec, keyValue)) continue;
            if (NODE_COORD_SECTION.equals(key)) {
                this.requireNotSet(data.graph, NODE_COORD_SECTION);
                data.graph = graph;
                data.vertex2node = this.readNodeCoordinateSection(lines, data);
                continue;
            }
            if (!TOUR_SECTION.equals(key)) continue;
            this.requireNotSet(tour, TOUR_SECTION);
            tour = this.readTourSection(lines, data.spec.dimension);
        }
        if (tour != null) {
            data.tour = this.getVertexTour(tour, data.vertex2node);
        }
        return data;
    }

    private Map<V, Node> readNodeCoordinateSection(Iterator<String> lines, Metadata<V, E> data) {
        this.requireSet(data.spec.edgeWeightType, NODE_COORD_SECTION);
        this.requireSet(data.spec.dimension, DIMENSION);
        ToIntBiFunction<Node, Node> edgeWeightFunction = this.getEdgeWeightFunction(data.spec.edgeWeightType);
        List<Node> nodes = this.readNodes(lines, data.spec.dimension);
        HashMap vertex2node = CollectionUtil.newHashMapWithExpectedSize((int)nodes.size());
        Graph graph = data.graph;
        for (Node node : nodes) {
            Object v = graph.addVertex();
            vertex2node.put(v, node);
        }
        new CompleteGraphGenerator().generateGraph(graph, null);
        graph.edgeSet().forEach(e -> {
            Node s = (Node)vertex2node.get(graph.getEdgeSource(e));
            Node t = (Node)vertex2node.get(graph.getEdgeTarget(e));
            double weight = edgeWeightFunction.applyAsInt(s, t);
            graph.setEdgeWeight(e, weight);
        });
        return Collections.unmodifiableMap(vertex2node);
    }

    private ToIntBiFunction<Node, Node> getEdgeWeightFunction(String edgeWeightType) {
        switch (edgeWeightType) {
            case "EUC_2D": {
                this.vectorLength = 2;
                return this::computeEuclideanDistance;
            }
            case "EUC_3D": {
                this.vectorLength = 3;
                return this::computeEuclideanDistance;
            }
            case "MAX_2D": {
                this.vectorLength = 2;
                return this::computeMaximumDistance;
            }
            case "MAX_3D": {
                this.vectorLength = 3;
                return this::computeMaximumDistance;
            }
            case "MAN_2D": {
                this.vectorLength = 2;
                return this::computeManhattanDistance;
            }
            case "MAN_3D": {
                this.vectorLength = 3;
                return this::computeManhattanDistance;
            }
            case "CEIL_2D": {
                this.vectorLength = 2;
                return this::compute2DCeilingEuclideanDistance;
            }
            case "GEO": {
                this.vectorLength = 2;
                return this::compute2DGeographicalDistance;
            }
            case "ATT": {
                this.vectorLength = 2;
                return this::compute2DPseudoEuclideanDistance;
            }
        }
        throw new IllegalStateException("Unsupported EDGE_WEIGHT_TYPE <" + edgeWeightType + ">");
    }

    private List<Node> readNodes(Iterator<String> lines, int dimension) {
        ArrayList<Node> nodes = new ArrayList<Node>(dimension);
        for (int i = 0; i < dimension && lines.hasNext(); ++i) {
            String line = lines.next();
            Node node = this.parseNode(line);
            nodes.add(node);
        }
        return nodes;
    }

    private Node parseNode(String line) {
        String[] elements = WHITE_SPACE.split(line);
        if (elements.length != this.vectorLength + 1) {
            throw new IllegalArgumentException("Unexpected number of elements <" + elements.length + "> in line: " + line);
        }
        int number = Integer.parseInt(elements[0]);
        double[] coordinates = Arrays.stream(elements, 1, elements.length).mapToDouble(Double::parseDouble).toArray();
        return new Node(number, coordinates);
    }

    public List<V> importTour(Metadata<V, E> referenceMetadata, Reader in) {
        this.metadata = null;
        try {
            Iterator<String> lines = TSPLIBImporter.getLineIterator(in);
            this.metadata = this.readContentForTour(lines, referenceMetadata.vertex2node);
            return this.metadata.tour;
        }
        catch (Exception e) {
            throw TSPLIBImporter.getImportException(e, "tour");
        }
    }

    private Metadata<V, E> readContentForTour(Iterator<String> lines, Map<V, Node> vertex2node) {
        Metadata data = new Metadata();
        while (lines.hasNext()) {
            String[] keyValue = lines.next().split(":");
            String key = TSPLIBImporter.getKey(keyValue);
            if (this.readSpecificationSection(key, data.spec, keyValue) || !TOUR_SECTION.equals(key)) continue;
            this.requireNotSet(data.tour, TOUR_SECTION);
            List<Integer> tour = this.readTourSection(lines, data.spec.dimension);
            data.tour = this.getVertexTour(tour, vertex2node);
        }
        data.vertex2node = vertex2node;
        return data;
    }

    private List<Integer> readTourSection(Iterator<String> lines, Integer dimension) {
        String lineContent;
        ArrayList<Integer> tour;
        ArrayList<Integer> arrayList = tour = dimension != null ? new ArrayList<Integer>(dimension) : new ArrayList();
        while (lines.hasNext() && !"-1".equals(lineContent = lines.next())) {
            tour.add(Integer.valueOf(lineContent));
        }
        return tour;
    }

    private List<V> getVertexTour(List<Integer> tour, Map<V, Node> vertex2node) {
        this.requireSet(vertex2node, TOUR_SECTION);
        List<V> orderedVertices = this.getOrderedVertices(vertex2node);
        ArrayList<Object> vertexTour = new ArrayList<Object>(orderedVertices.size());
        for (Integer vertexNumber : tour) {
            Object v;
            Object e = v = vertexNumber < orderedVertices.size() ? (Object)orderedVertices.get(vertexNumber) : null;
            if (v == null) {
                throw new IllegalStateException("Missing vertex with number " + vertexNumber);
            }
            vertexTour.add(v);
        }
        return vertexTour;
    }

    private List<V> getOrderedVertices(Map<V, Node> vertex2node) {
        int maxNumber = vertex2node.values().stream().mapToInt(Node::getNumber).max().getAsInt();
        Object[] orderedVertices = new Object[maxNumber + 1];
        vertex2node.forEach((v, n) -> {
            orderedVertices[n.number] = v;
        });
        return Arrays.asList(orderedVertices);
    }

    private boolean readSpecificationSection(String key, Specification spec, String[] lineElements) {
        switch (key) {
            case "NAME": {
                this.requireNotSet(spec.name, NAME);
                spec.name = this.getValue(lineElements);
                return true;
            }
            case "TYPE": {
                this.requireNotSet(spec.type, TYPE);
                String type = this.getValue(lineElements);
                spec.type = this.requireValidValue(type, VALID_TYPES, TYPE);
                return true;
            }
            case "COMMENT": {
                String comment = this.getValue(lineElements);
                spec.comment.add(comment);
                return true;
            }
            case "DIMENSION": {
                this.requireNotSet(spec.dimension, DIMENSION);
                String dimension = this.getValue(lineElements);
                spec.dimension = this.parseInteger(dimension, DIMENSION);
                return true;
            }
            case "CAPACITY": {
                this.requireNotSet(spec.capacity, CAPACITY);
                String capacity = this.getValue(lineElements);
                spec.capacity = this.parseInteger(capacity, CAPACITY);
                return true;
            }
            case "EDGE_WEIGHT_TYPE": {
                this.requireNotSet(spec.edgeWeightType, EDGE_WEIGHT_TYPE);
                String edgeWeightType = this.getValue(lineElements);
                spec.edgeWeightType = this.requireValidValue(edgeWeightType, VALID_EDGE_WEIGHT_TYPES, EDGE_WEIGHT_TYPE);
                return true;
            }
            case "EDGE_WEIGHT_FORMAT": {
                this.requireNotSet(spec.edgeWeightFormat, EDGE_WEIGHT_FORMAT);
                String edgeWeightFormat = this.getValue(lineElements);
                spec.edgeWeightFormat = this.requireValidValue(edgeWeightFormat, VALID_EDGE_WEIGHT_FORMATS, EDGE_WEIGHT_FORMAT);
                return true;
            }
            case "EDGE_DATA_FORMAT": {
                this.requireNotSet(spec.edgeDataFormat, EDGE_DATA_FORMAT);
                String edgeDataFormat = this.getValue(lineElements);
                spec.edgeDataFormat = this.requireValidValue(edgeDataFormat, VALID_EDGE_DATA_FORMATS, EDGE_DATA_FORMAT);
                return true;
            }
            case "NODE_COORD_TYPE": {
                this.requireNotSet(spec.nodeCoordType, NODE_COORD_TYPE);
                String nodeCoordType = this.getValue(lineElements);
                spec.nodeCoordType = this.requireValidValue(nodeCoordType, VALID_NODE_COORD_TYPES, NODE_COORD_TYPE);
                return true;
            }
            case "DISPLAY_DATA_TYPE": {
                this.requireNotSet(spec.displayDataType, DISPLAY_DATA_TYPE);
                String displayDataType = this.getValue(lineElements);
                spec.displayDataType = this.requireValidValue(displayDataType, VALID_DISPLAY_DATA_TYPE, DISPLAY_DATA_TYPE);
                return true;
            }
        }
        return false;
    }

    private String requireValidValue(String value, List<String> validValues, String valueType) {
        value = this.extractValueBeforeWhitespace(value);
        for (String validValue : validValues) {
            if (!validValue.equalsIgnoreCase(value)) continue;
            return validValue;
        }
        throw new IllegalArgumentException("Invalid " + valueType + " value <" + value + ">");
    }

    private Integer parseInteger(String valueStr, String valueType) {
        valueStr = this.extractValueBeforeWhitespace(valueStr);
        try {
            return Integer.valueOf(valueStr);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid " + valueType + " integer value <" + valueStr + ">", e);
        }
    }

    public String extractValueBeforeWhitespace(String value) {
        return WHITE_SPACE.split(value.strip(), 2)[0];
    }

    private static Iterator<String> getLineIterator(Reader in) {
        BufferedReader reader = new BufferedReader(in);
        return Stream.iterate(TSPLIBImporter.readLine(reader), Objects::nonNull, l -> TSPLIBImporter.readLine(reader)).iterator();
    }

    private static String readLine(BufferedReader reader) {
        try {
            String line = reader.readLine();
            if (line != null) {
                return "EOF".equals(line = line.strip()) ? null : line;
            }
            return null;
        }
        catch (IOException e) {
            throw new IllegalStateException("I/O exception while reading line of TSPLIB file", e);
        }
    }

    private static String getKey(String[] keyValue) {
        return keyValue[0].strip().toUpperCase();
    }

    private String getValue(String[] keyValue) {
        if (keyValue.length < 2) {
            throw new IllegalStateException("Missing value for key " + TSPLIBImporter.getKey(keyValue));
        }
        return keyValue[1].strip();
    }

    private void requireNotSet(Object target, String keyName) {
        if (target != null) {
            throw new IllegalStateException("Multiple values for key " + keyName);
        }
    }

    private void requireSet(Object requirement, String target) {
        if (requirement == null) {
            throw new IllegalStateException("Missing data to read <" + target + ">");
        }
    }

    private static ImportException getImportException(Exception e, String target) {
        return new ImportException("Failed to import " + target + " from TSPLIB-file: " + e.getMessage(), e);
    }

    int computeEuclideanDistance(Node n1, Node n2) {
        return (int)Math.round(this.getL2Distance(n1, n2));
    }

    int computeMaximumDistance(Node n1, Node n2) {
        return (int)Math.round(this.getLInfDistance(n1, n2));
    }

    int computeManhattanDistance(Node n1, Node n2) {
        return (int)Math.round(this.getL1Distance(n1, n2));
    }

    int compute2DCeilingEuclideanDistance(Node n1, Node n2) {
        return (int)Math.ceil(this.getL2Distance(n1, n2));
    }

    int compute2DGeographicalDistance(Node n1, Node n2) {
        double latitude1 = TSPLIBImporter.computeRadiansAngle(n1.getCoordinateValue(0));
        double longitude1 = TSPLIBImporter.computeRadiansAngle(n1.getCoordinateValue(1));
        double latitude2 = TSPLIBImporter.computeRadiansAngle(n2.getCoordinateValue(0));
        double longitude2 = TSPLIBImporter.computeRadiansAngle(n2.getCoordinateValue(1));
        double q1 = Math.cos(longitude1 - longitude2);
        double q2 = Math.cos(latitude1 - latitude2);
        double q3 = Math.cos(latitude1 + latitude2);
        return (int)(6378.388 * Math.acos(0.5 * ((1.0 + q1) * q2 - (1.0 - q1) * q3)) + 1.0);
    }

    private static double computeRadiansAngle(double x) {
        double deg = Math.round(x);
        double min = x - deg;
        return 3.141592 * (deg + 5.0 * min / 3.0) / 180.0;
    }

    int compute2DPseudoEuclideanDistance(Node n1, Node n2) {
        double yd;
        double xd = n1.getCoordinateValue(0) - n2.getCoordinateValue(0);
        double rij = Math.sqrt((xd * xd + (yd = n1.getCoordinateValue(1) - n2.getCoordinateValue(1)) * yd) / 10.0);
        double tij = Math.round(rij);
        if (tij < rij) {
            return (int)(tij + 1.0);
        }
        return (int)tij;
    }

    private double getL1Distance(Node n1, Node n2) {
        double elementSum = 0.0;
        for (int i = 0; i < this.vectorLength; ++i) {
            double delta = n1.getCoordinateValue(i) - n2.getCoordinateValue(i);
            elementSum += Math.abs(delta);
        }
        return elementSum;
    }

    private double getL2Distance(Node n1, Node n2) {
        double elementSum = 0.0;
        for (int i = 0; i < this.vectorLength; ++i) {
            double delta = n1.getCoordinateValue(i) - n2.getCoordinateValue(i);
            elementSum += delta * delta;
        }
        return Math.sqrt(elementSum);
    }

    private double getLInfDistance(Node n1, Node n2) {
        double maxElement = 0.0;
        for (int i = 0; i < this.vectorLength; ++i) {
            double delta = n1.getCoordinateValue(i) - n2.getCoordinateValue(i);
            maxElement = Math.max(maxElement, Math.abs(delta));
        }
        return maxElement;
    }

    public static class Metadata<V, E> {
        private final Specification spec = new Specification();
        private Map<V, Node> vertex2node;
        private Graph<V, E> graph;
        private List<V> tour;
        private Boolean hasDistinctLocations;
        private Boolean hasDistinctNeighborDistances;

        private Metadata() {
        }

        public Specification getSpecification() {
            return this.spec;
        }

        public Map<V, Node> getVertexToNodeMapping() {
            return this.vertex2node;
        }

        public List<V> getTour() {
            return this.tour;
        }

        public boolean hasDistinctNodeLocations() {
            if (this.graph == null) {
                throw new IllegalStateException("No graph imported");
            }
            if (this.hasDistinctLocations == null) {
                this.hasDistinctLocations = Boolean.TRUE;
                HashSet distinctCoordinates = CollectionUtil.newHashSetWithExpectedSize((int)this.vertex2node.size());
                for (Node node : this.vertex2node.values()) {
                    double[] coordinates = node.getCoordinates();
                    Double[] coordinateObj = (Double[])Arrays.stream(coordinates).boxed().toArray(Double[]::new);
                    if (distinctCoordinates.add(Arrays.asList(coordinateObj))) continue;
                    this.hasDistinctLocations = Boolean.FALSE;
                    return this.hasDistinctLocations;
                }
            }
            return this.hasDistinctLocations;
        }

        public boolean hasDistinctNeighborDistances() {
            if (this.graph == null) {
                throw new IllegalStateException("No graph imported");
            }
            if (this.hasDistinctNeighborDistances == null) {
                this.hasDistinctNeighborDistances = Boolean.TRUE;
                Set vertices = this.graph.vertexSet();
                HashSet weights = CollectionUtil.newHashSetWithExpectedSize((int)(vertices.size() - 1));
                for (Object v : vertices) {
                    weights.clear();
                    for (Object edge : this.graph.edgesOf(v)) {
                        if (weights.add(this.graph.getEdgeWeight(edge))) continue;
                        this.hasDistinctNeighborDistances = Boolean.FALSE;
                        return this.hasDistinctNeighborDistances;
                    }
                }
            }
            return this.hasDistinctNeighborDistances;
        }
    }

    public static class Specification {
        private String name;
        private String type;
        private final List<String> comment = new ArrayList<String>();
        private Integer dimension;
        private Integer capacity;
        private String edgeWeightType;
        private String edgeWeightFormat;
        private String edgeDataFormat;
        private String nodeCoordType;
        private String displayDataType;

        Specification() {
        }

        public String getName() {
            return this.name;
        }

        public String getType() {
            return this.type;
        }

        public List<String> getComments() {
            return Collections.unmodifiableList(this.comment);
        }

        public Integer getDimension() {
            return this.dimension;
        }

        public Integer getCapacity() {
            return this.capacity;
        }

        public String getEdgeWeightType() {
            return this.edgeWeightType;
        }

        public String getEdgeWeightFormat() {
            return this.edgeWeightFormat;
        }

        public String getEdgeDataFormat() {
            return this.edgeDataFormat;
        }

        public String getNodeCoordType() {
            return this.nodeCoordType;
        }

        public String getDisplayDataType() {
            return this.displayDataType;
        }
    }

    public static class Node {
        private final int number;
        private final double[] coordinates;

        Node(int number, double[] coordinates) {
            this.number = number;
            this.coordinates = coordinates;
        }

        public int getNumber() {
            return this.number;
        }

        public int getCoordinatesLength() {
            return this.coordinates.length;
        }

        public double getCoordinateValue(int i) {
            return this.coordinates[i];
        }

        public double[] getCoordinates() {
            return Arrays.copyOf(this.coordinates, this.coordinates.length);
        }

        public String toString() {
            return this.number + " " + Arrays.stream(this.coordinates).mapToObj(Double::toString).collect(Collectors.joining(" "));
        }
    }
}

