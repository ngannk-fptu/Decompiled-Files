/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate;

import java.util.HashMap;
import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.generate.GeneralizedPetersenGraphGenerator;
import org.jgrapht.generate.StarGraphGenerator;
import org.jgrapht.generate.WindmillGraphsGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.util.SupplierUtil;

public class NamedGraphGenerator<V, E> {
    private Map<Integer, V> vertexMap = new HashMap<Integer, V>();

    public static Graph<Integer, DefaultEdge> doyleGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateDoyleGraph(g);
        return g;
    }

    public void generateDoyleGraph(Graph<V, E> targetGraph) {
        this.vertexMap.clear();
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 3; ++j) {
                this.addEdge(targetGraph, this.doyleHash(i, j), this.doyleHash(this.mod(4 * i + 1, 9), this.mod(j - 1, 3)));
                this.addEdge(targetGraph, this.doyleHash(i, j), this.doyleHash(this.mod(4 * i - 1, 9), this.mod(j - 1, 3)));
                this.addEdge(targetGraph, this.doyleHash(i, j), this.doyleHash(this.mod(7 * i + 7, 9), this.mod(j + 1, 3)));
                this.addEdge(targetGraph, this.doyleHash(i, j), this.doyleHash(this.mod(7 * i - 7, 9), this.mod(j + 1, 3)));
            }
        }
    }

    private int doyleHash(int u, int v) {
        return u * 19 + v;
    }

    private int mod(int u, int m) {
        int r = u % m;
        return r < 0 ? r + m : r;
    }

    public static Graph<Integer, DefaultEdge> generalizedPetersenGraph(int n, int k) {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateGeneralizedPetersenGraph(g, n, k);
        return g;
    }

    private void generateGeneralizedPetersenGraph(Graph<V, E> targetGraph, int n, int k) {
        GeneralizedPetersenGraphGenerator<V, E> gpgg = new GeneralizedPetersenGraphGenerator<V, E>(n, k);
        gpgg.generateGraph(targetGraph);
    }

    public static Graph<Integer, DefaultEdge> petersenGraph() {
        return NamedGraphGenerator.generalizedPetersenGraph(5, 2);
    }

    public void generatePetersenGraph(Graph<V, E> targetGraph) {
        this.generateGeneralizedPetersenGraph(targetGraph, 5, 2);
    }

    public static Graph<Integer, DefaultEdge> d\u00fcrerGraph() {
        return NamedGraphGenerator.generalizedPetersenGraph(6, 2);
    }

    public void generateD\u00fcrerGraph(Graph<V, E> targetGraph) {
        this.generateGeneralizedPetersenGraph(targetGraph, 6, 2);
    }

    public static Graph<Integer, DefaultEdge> dodecahedronGraph() {
        return NamedGraphGenerator.generalizedPetersenGraph(10, 2);
    }

    public void generateDodecahedronGraph(Graph<V, E> targetGraph) {
        this.generateGeneralizedPetersenGraph(targetGraph, 10, 2);
    }

    public static Graph<Integer, DefaultEdge> desarguesGraph() {
        return NamedGraphGenerator.generalizedPetersenGraph(10, 3);
    }

    public void generateDesarguesGraph(Graph<V, E> targetGraph) {
        this.generateGeneralizedPetersenGraph(targetGraph, 10, 3);
    }

    public static Graph<Integer, DefaultEdge> nauruGraph() {
        return NamedGraphGenerator.generalizedPetersenGraph(12, 5);
    }

    public void generateNauruGraph(Graph<V, E> targetGraph) {
        this.generateGeneralizedPetersenGraph(targetGraph, 12, 5);
    }

    public static Graph<Integer, DefaultEdge> m\u00f6biusKantorGraph() {
        return NamedGraphGenerator.generalizedPetersenGraph(8, 3);
    }

    public void generateM\u00f6biusKantorGraph(Graph<V, E> targetGraph) {
        this.generateGeneralizedPetersenGraph(targetGraph, 8, 3);
    }

    public static Graph<Integer, DefaultEdge> bullGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateBullGraph(g);
        return g;
    }

    public void generateBullGraph(Graph<V, E> targetGraph) {
        this.vertexMap.clear();
        this.addEdge(targetGraph, 0, 1);
        this.addEdge(targetGraph, 1, 2);
        this.addEdge(targetGraph, 2, 3);
        this.addEdge(targetGraph, 1, 3);
        this.addEdge(targetGraph, 3, 4);
    }

    public static Graph<Integer, DefaultEdge> butterflyGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateButterflyGraph(g);
        return g;
    }

    public void generateButterflyGraph(Graph<V, E> targetGraph) {
        new WindmillGraphsGenerator<V, E>(WindmillGraphsGenerator.Mode.DUTCHWINDMILL, 2, 3).generateGraph(targetGraph);
    }

    public static Graph<Integer, DefaultEdge> clawGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateClawGraph(g);
        return g;
    }

    public void generateClawGraph(Graph<V, E> targetGraph) {
        new StarGraphGenerator<V, E>(4).generateGraph(targetGraph);
    }

    public static Graph<Integer, DefaultEdge> buckyBallGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateBuckyBallGraph(g);
        return g;
    }

    public void generateBuckyBallGraph(Graph<V, E> targetGraph) {
        int[][] edges;
        this.vertexMap.clear();
        for (int[] edge : edges = new int[][]{{0, 2}, {0, 48}, {0, 59}, {1, 3}, {1, 9}, {1, 58}, {2, 3}, {2, 36}, {3, 17}, {4, 6}, {4, 8}, {4, 12}, {5, 7}, {5, 9}, {5, 16}, {6, 7}, {6, 20}, {7, 21}, {8, 9}, {8, 56}, {10, 11}, {10, 12}, {10, 20}, {11, 27}, {11, 47}, {12, 13}, {13, 46}, {13, 54}, {14, 15}, {14, 16}, {14, 21}, {15, 25}, {15, 41}, {16, 17}, {17, 40}, {18, 19}, {18, 20}, {18, 26}, {19, 21}, {19, 24}, {22, 23}, {22, 31}, {22, 34}, {23, 25}, {23, 38}, {24, 25}, {24, 30}, {26, 27}, {26, 30}, {27, 29}, {28, 29}, {28, 31}, {28, 35}, {29, 44}, {30, 31}, {32, 34}, {32, 39}, {32, 50}, {33, 35}, {33, 45}, {33, 51}, {34, 35}, {36, 37}, {36, 40}, {37, 39}, {37, 52}, {38, 39}, {38, 41}, {40, 41}, {42, 43}, {42, 46}, {42, 55}, {43, 45}, {43, 53}, {44, 45}, {44, 47}, {46, 47}, {48, 49}, {48, 52}, {49, 53}, {49, 57}, {50, 51}, {50, 52}, {51, 53}, {54, 55}, {54, 56}, {55, 57}, {56, 58}, {57, 59}, {58, 59}}) {
            this.addEdge(targetGraph, edge[0], edge[1]);
        }
    }

    public static Graph<Integer, DefaultEdge> clebschGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateClebschGraph(g);
        return g;
    }

    public void generateClebschGraph(Graph<V, E> targetGraph) {
        this.vertexMap.clear();
        int x = 0;
        for (int i = 0; i < 8; ++i) {
            this.addEdge(targetGraph, x % 16, (x + 1) % 16);
            this.addEdge(targetGraph, x % 16, (x + 6) % 16);
            this.addEdge(targetGraph, x % 16, (x + 8) % 16);
            this.addEdge(targetGraph, ++x % 16, (x + 3) % 16);
            this.addEdge(targetGraph, x % 16, (x + 2) % 16);
            this.addEdge(targetGraph, x % 16, (x + 8) % 16);
            ++x;
        }
    }

    public static Graph<Integer, DefaultEdge> gr\u00f6tzschGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateGr\u00f6tzschGraph(g);
        return g;
    }

    public void generateGr\u00f6tzschGraph(Graph<V, E> targetGraph) {
        int i;
        this.vertexMap.clear();
        for (i = 1; i < 6; ++i) {
            this.addEdge(targetGraph, 0, i);
        }
        this.addEdge(targetGraph, 10, 6);
        for (i = 6; i < 10; ++i) {
            this.addEdge(targetGraph, i, i + 1);
            this.addEdge(targetGraph, i, i - 4);
        }
        this.addEdge(targetGraph, 10, 1);
        for (i = 7; i < 11; ++i) {
            this.addEdge(targetGraph, i, i - 6);
        }
        this.addEdge(targetGraph, 6, 5);
    }

    public static Graph<Integer, DefaultEdge> bidiakisCubeGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateBidiakisCubeGraph(g);
        return g;
    }

    public void generateBidiakisCubeGraph(Graph<V, E> targetGraph) {
        int[][] edges;
        this.vertexMap.clear();
        for (int[] edge : edges = new int[][]{{0, 1}, {0, 6}, {0, 11}, {1, 2}, {1, 5}, {2, 3}, {2, 10}, {3, 4}, {3, 9}, {4, 5}, {4, 8}, {5, 6}, {6, 7}, {7, 8}, {7, 11}, {8, 9}, {9, 10}, {10, 11}}) {
            this.addEdge(targetGraph, edge[0], edge[1]);
        }
    }

    public static Graph<Integer, DefaultEdge> blanusaFirstSnarkGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateBlanusaFirstSnarkGraph(g);
        return g;
    }

    public void generateBlanusaFirstSnarkGraph(Graph<V, E> targetGraph) {
        int[][] edges;
        this.vertexMap.clear();
        for (int[] edge : edges = new int[][]{{0, 1}, {0, 5}, {0, 16}, {1, 2}, {1, 17}, {2, 3}, {2, 14}, {3, 4}, {3, 8}, {4, 5}, {4, 17}, {5, 6}, {6, 7}, {6, 11}, {7, 8}, {7, 17}, {8, 9}, {9, 10}, {9, 13}, {10, 11}, {10, 15}, {11, 12}, {12, 13}, {12, 16}, {13, 14}, {14, 15}, {15, 16}}) {
            this.addEdge(targetGraph, edge[0], edge[1]);
        }
    }

    public static Graph<Integer, DefaultEdge> blanusaSecondSnarkGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateBlanusaSecondSnarkGraph(g);
        return g;
    }

    public void generateBlanusaSecondSnarkGraph(Graph<V, E> targetGraph) {
        int[][] edges;
        this.vertexMap.clear();
        for (int[] edge : edges = new int[][]{{0, 1}, {0, 2}, {0, 14}, {1, 5}, {1, 11}, {2, 3}, {2, 6}, {3, 4}, {3, 9}, {4, 5}, {4, 7}, {5, 6}, {6, 8}, {7, 8}, {7, 17}, {8, 9}, {9, 15}, {10, 11}, {10, 14}, {10, 16}, {11, 12}, {12, 13}, {12, 17}, {13, 14}, {13, 15}, {15, 16}, {16, 17}}) {
            this.addEdge(targetGraph, edge[0], edge[1]);
        }
    }

    public static Graph<Integer, DefaultEdge> doubleStarSnarkGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateDoubleStarSnarkGraph(g);
        return g;
    }

    public void generateDoubleStarSnarkGraph(Graph<V, E> targetGraph) {
        int[][] edges;
        this.vertexMap.clear();
        for (int[] edge : edges = new int[][]{{0, 1}, {0, 14}, {0, 15}, {1, 2}, {1, 11}, {2, 3}, {2, 7}, {3, 4}, {3, 18}, {4, 5}, {4, 14}, {5, 6}, {5, 10}, {6, 7}, {6, 21}, {7, 8}, {8, 9}, {8, 13}, {9, 10}, {9, 24}, {10, 11}, {11, 12}, {12, 13}, {12, 27}, {13, 14}, {15, 16}, {15, 29}, {16, 20}, {16, 23}, {17, 18}, {17, 25}, {17, 28}, {18, 19}, {19, 23}, {19, 26}, {20, 21}, {20, 28}, {21, 22}, {22, 26}, {22, 29}, {23, 24}, {24, 25}, {25, 29}, {26, 27}, {27, 28}}) {
            this.addEdge(targetGraph, edge[0], edge[1]);
        }
    }

    public static Graph<Integer, DefaultEdge> brinkmannGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateBrinkmannGraph(g);
        return g;
    }

    public void generateBrinkmannGraph(Graph<V, E> targetGraph) {
        int[][] edges;
        this.vertexMap.clear();
        for (int[] edge : edges = new int[][]{{0, 2}, {0, 5}, {0, 7}, {0, 13}, {1, 3}, {1, 6}, {1, 7}, {1, 8}, {2, 4}, {2, 8}, {2, 9}, {3, 5}, {3, 9}, {3, 10}, {4, 6}, {4, 10}, {4, 11}, {5, 11}, {5, 12}, {6, 12}, {6, 13}, {7, 15}, {7, 20}, {8, 14}, {8, 16}, {9, 15}, {9, 17}, {10, 16}, {10, 18}, {11, 17}, {11, 19}, {12, 18}, {12, 20}, {13, 14}, {13, 19}, {14, 17}, {14, 18}, {15, 18}, {15, 19}, {16, 19}, {16, 20}, {17, 20}}) {
            this.addEdge(targetGraph, edge[0], edge[1]);
        }
    }

    public static Graph<Integer, DefaultEdge> gossetGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateGossetGraph(g);
        return g;
    }

    public void generateGossetGraph(Graph<V, E> targetGraph) {
        int[][] edges;
        this.vertexMap.clear();
        for (int[] edge : edges = new int[][]{{0, 1}, {0, 2}, {0, 3}, {0, 4}, {0, 5}, {0, 6}, {0, 7}, {0, 8}, {0, 9}, {0, 10}, {0, 11}, {0, 12}, {0, 13}, {0, 14}, {0, 15}, {0, 16}, {0, 17}, {0, 18}, {0, 19}, {0, 20}, {0, 21}, {0, 28}, {0, 29}, {0, 30}, {0, 31}, {0, 32}, {0, 33}, {1, 2}, {1, 3}, {1, 4}, {1, 5}, {1, 6}, {1, 7}, {1, 8}, {1, 9}, {1, 10}, {1, 11}, {1, 12}, {1, 13}, {1, 14}, {1, 15}, {1, 16}, {1, 22}, {1, 23}, {1, 24}, {1, 25}, {1, 26}, {1, 28}, {1, 34}, {1, 35}, {1, 36}, {1, 37}, {1, 38}, {2, 3}, {2, 4}, {2, 5}, {2, 6}, {2, 7}, {2, 8}, {2, 9}, {2, 10}, {2, 11}, {2, 12}, {2, 17}, {2, 18}, {2, 19}, {2, 20}, {2, 22}, {2, 23}, {2, 24}, {2, 25}, {2, 27}, {2, 29}, {2, 34}, {2, 39}, {2, 40}, {2, 41}, {2, 42}, {3, 4}, {3, 5}, {3, 6}, {3, 7}, {3, 8}, {3, 9}, {3, 13}, {3, 14}, {3, 15}, {3, 17}, {3, 18}, {3, 19}, {3, 21}, {3, 22}, {3, 23}, {3, 24}, {3, 26}, {3, 27}, {3, 30}, {3, 35}, {3, 39}, {3, 43}, {3, 44}, {3, 45}, {4, 5}, {4, 6}, {4, 7}, {4, 10}, {4, 11}, {4, 13}, {4, 14}, {4, 16}, {4, 17}, {4, 18}, {4, 20}, {4, 21}, {4, 22}, {4, 23}, {4, 25}, {4, 26}, {4, 27}, {4, 31}, {4, 36}, {4, 40}, {4, 43}, {4, 46}, {4, 47}, {5, 6}, {5, 8}, {5, 10}, {5, 12}, {5, 13}, {5, 15}, {5, 16}, {5, 17}, {5, 19}, {5, 20}, {5, 21}, {5, 22}, {5, 24}, {5, 25}, {5, 26}, {5, 27}, {5, 32}, {5, 37}, {5, 41}, {5, 44}, {5, 46}, {5, 48}, {6, 9}, {6, 11}, {6, 12}, {6, 14}, {6, 15}, {6, 16}, {6, 18}, {6, 19}, {6, 20}, {6, 21}, {6, 23}, {6, 24}, {6, 25}, {6, 26}, {6, 27}, {6, 33}, {6, 38}, {6, 42}, {6, 45}, {6, 47}, {6, 48}, {7, 8}, {7, 9}, {7, 10}, {7, 11}, {7, 13}, {7, 14}, {7, 17}, {7, 18}, {7, 22}, {7, 23}, {7, 28}, {7, 29}, {7, 30}, {7, 31}, {7, 34}, {7, 35}, {7, 36}, {7, 39}, {7, 40}, {7, 43}, {7, 49}, {7, 50}, {8, 9}, {8, 10}, {8, 12}, {8, 13}, {8, 15}, {8, 17}, {8, 19}, {8, 22}, {8, 24}, {8, 28}, {8, 29}, {8, 30}, {8, 32}, {8, 34}, {8, 35}, {8, 37}, {8, 39}, {8, 41}, {8, 44}, {8, 49}, {8, 51}, {9, 11}, {9, 12}, {9, 14}, {9, 15}, {9, 18}, {9, 19}, {9, 23}, {9, 24}, {9, 28}, {9, 29}, {9, 30}, {9, 33}, {9, 34}, {9, 35}, {9, 38}, {9, 39}, {9, 42}, {9, 45}, {9, 50}, {9, 51}, {10, 11}, {10, 12}, {10, 13}, {10, 16}, {10, 17}, {10, 20}, {10, 22}, {10, 25}, {10, 28}, {10, 29}, {10, 31}, {10, 32}, {10, 34}, {10, 36}, {10, 37}, {10, 40}, {10, 41}, {10, 46}, {10, 49}, {10, 52}, {11, 12}, {11, 14}, {11, 16}, {11, 18}, {11, 20}, {11, 23}, {11, 25}, {11, 28}, {11, 29}, {11, 31}, {11, 33}, {11, 34}, {11, 36}, {11, 38}, {11, 40}, {11, 42}, {11, 47}, {11, 50}, {11, 52}, {12, 15}, {12, 16}, {12, 19}, {12, 20}, {12, 24}, {12, 25}, {12, 28}, {12, 29}, {12, 32}, {12, 33}, {12, 34}, {12, 37}, {12, 38}, {12, 41}, {12, 42}, {12, 48}, {12, 51}, {12, 52}, {13, 14}, {13, 15}, {13, 16}, {13, 17}, {13, 21}, {13, 22}, {13, 26}, {13, 28}, {13, 30}, {13, 31}, {13, 32}, {13, 35}, {13, 36}, {13, 37}, {13, 43}, {13, 44}, {13, 46}, {13, 49}, {13, 53}, {14, 15}, {14, 16}, {14, 18}, {14, 21}, {14, 23}, {14, 26}, {14, 28}, {14, 30}, {14, 31}, {14, 33}, {14, 35}, {14, 36}, {14, 38}, {14, 43}, {14, 45}, {14, 47}, {14, 50}, {14, 53}, {15, 16}, {15, 19}, {15, 21}, {15, 24}, {15, 26}, {15, 28}, {15, 30}, {15, 32}, {15, 33}, {15, 35}, {15, 37}, {15, 38}, {15, 44}, {15, 45}, {15, 48}, {15, 51}, {15, 53}, {16, 20}, {16, 21}, {16, 25}, {16, 26}, {16, 28}, {16, 31}, {16, 32}, {16, 33}, {16, 36}, {16, 37}, {16, 38}, {16, 46}, {16, 47}, {16, 48}, {16, 52}, {16, 53}, {17, 18}, {17, 19}, {17, 20}, {17, 21}, {17, 22}, {17, 27}, {17, 29}, {17, 30}, {17, 31}, {17, 32}, {17, 39}, {17, 40}, {17, 41}, {17, 43}, {17, 44}, {17, 46}, {17, 49}, {17, 54}, {18, 19}, {18, 20}, {18, 21}, {18, 23}, {18, 27}, {18, 29}, {18, 30}, {18, 31}, {18, 33}, {18, 39}, {18, 40}, {18, 42}, {18, 43}, {18, 45}, {18, 47}, {18, 50}, {18, 54}, {19, 20}, {19, 21}, {19, 24}, {19, 27}, {19, 29}, {19, 30}, {19, 32}, {19, 33}, {19, 39}, {19, 41}, {19, 42}, {19, 44}, {19, 45}, {19, 48}, {19, 51}, {19, 54}, {20, 21}, {20, 25}, {20, 27}, {20, 29}, {20, 31}, {20, 32}, {20, 33}, {20, 40}, {20, 41}, {20, 42}, {20, 46}, {20, 47}, {20, 48}, {20, 52}, {20, 54}, {21, 26}, {21, 27}, {21, 30}, {21, 31}, {21, 32}, {21, 33}, {21, 43}, {21, 44}, {21, 45}, {21, 46}, {21, 47}, {21, 48}, {21, 53}, {21, 54}, {22, 23}, {22, 24}, {22, 25}, {22, 26}, {22, 27}, {22, 34}, {22, 35}, {22, 36}, {22, 37}, {22, 39}, {22, 40}, {22, 41}, {22, 43}, {22, 44}, {22, 46}, {22, 49}, {22, 55}, {23, 24}, {23, 25}, {23, 26}, {23, 27}, {23, 34}, {23, 35}, {23, 36}, {23, 38}, {23, 39}, {23, 40}, {23, 42}, {23, 43}, {23, 45}, {23, 47}, {23, 50}, {23, 55}, {24, 25}, {24, 26}, {24, 27}, {24, 34}, {24, 35}, {24, 37}, {24, 38}, {24, 39}, {24, 41}, {24, 42}, {24, 44}, {24, 45}, {24, 48}, {24, 51}, {24, 55}, {25, 26}, {25, 27}, {25, 34}, {25, 36}, {25, 37}, {25, 38}, {25, 40}, {25, 41}, {25, 42}, {25, 46}, {25, 47}, {25, 48}, {25, 52}, {25, 55}, {26, 27}, {26, 35}, {26, 36}, {26, 37}, {26, 38}, {26, 43}, {26, 44}, {26, 45}, {26, 46}, {26, 47}, {26, 48}, {26, 53}, {26, 55}, {27, 39}, {27, 40}, {27, 41}, {27, 42}, {27, 43}, {27, 44}, {27, 45}, {27, 46}, {27, 47}, {27, 48}, {27, 54}, {27, 55}, {28, 29}, {28, 30}, {28, 31}, {28, 32}, {28, 33}, {28, 34}, {28, 35}, {28, 36}, {28, 37}, {28, 38}, {28, 49}, {28, 50}, {28, 51}, {28, 52}, {28, 53}, {29, 30}, {29, 31}, {29, 32}, {29, 33}, {29, 34}, {29, 39}, {29, 40}, {29, 41}, {29, 42}, {29, 49}, {29, 50}, {29, 51}, {29, 52}, {29, 54}, {30, 31}, {30, 32}, {30, 33}, {30, 35}, {30, 39}, {30, 43}, {30, 44}, {30, 45}, {30, 49}, {30, 50}, {30, 51}, {30, 53}, {30, 54}, {31, 32}, {31, 33}, {31, 36}, {31, 40}, {31, 43}, {31, 46}, {31, 47}, {31, 49}, {31, 50}, {31, 52}, {31, 53}, {31, 54}, {32, 33}, {32, 37}, {32, 41}, {32, 44}, {32, 46}, {32, 48}, {32, 49}, {32, 51}, {32, 52}, {32, 53}, {32, 54}, {33, 38}, {33, 42}, {33, 45}, {33, 47}, {33, 48}, {33, 50}, {33, 51}, {33, 52}, {33, 53}, {33, 54}, {34, 35}, {34, 36}, {34, 37}, {34, 38}, {34, 39}, {34, 40}, {34, 41}, {34, 42}, {34, 49}, {34, 50}, {34, 51}, {34, 52}, {34, 55}, {35, 36}, {35, 37}, {35, 38}, {35, 39}, {35, 43}, {35, 44}, {35, 45}, {35, 49}, {35, 50}, {35, 51}, {35, 53}, {35, 55}, {36, 37}, {36, 38}, {36, 40}, {36, 43}, {36, 46}, {36, 47}, {36, 49}, {36, 50}, {36, 52}, {36, 53}, {36, 55}, {37, 38}, {37, 41}, {37, 44}, {37, 46}, {37, 48}, {37, 49}, {37, 51}, {37, 52}, {37, 53}, {37, 55}, {38, 42}, {38, 45}, {38, 47}, {38, 48}, {38, 50}, {38, 51}, {38, 52}, {38, 53}, {38, 55}, {39, 40}, {39, 41}, {39, 42}, {39, 43}, {39, 44}, {39, 45}, {39, 49}, {39, 50}, {39, 51}, {39, 54}, {39, 55}, {40, 41}, {40, 42}, {40, 43}, {40, 46}, {40, 47}, {40, 49}, {40, 50}, {40, 52}, {40, 54}, {40, 55}, {41, 42}, {41, 44}, {41, 46}, {41, 48}, {41, 49}, {41, 51}, {41, 52}, {41, 54}, {41, 55}, {42, 45}, {42, 47}, {42, 48}, {42, 50}, {42, 51}, {42, 52}, {42, 54}, {42, 55}, {43, 44}, {43, 45}, {43, 46}, {43, 47}, {43, 49}, {43, 50}, {43, 53}, {43, 54}, {43, 55}, {44, 45}, {44, 46}, {44, 48}, {44, 49}, {44, 51}, {44, 53}, {44, 54}, {44, 55}, {45, 47}, {45, 48}, {45, 50}, {45, 51}, {45, 53}, {45, 54}, {45, 55}, {46, 47}, {46, 48}, {46, 49}, {46, 52}, {46, 53}, {46, 54}, {46, 55}, {47, 48}, {47, 50}, {47, 52}, {47, 53}, {47, 54}, {47, 55}, {48, 51}, {48, 52}, {48, 53}, {48, 54}, {48, 55}, {49, 50}, {49, 51}, {49, 52}, {49, 53}, {49, 54}, {49, 55}, {50, 51}, {50, 52}, {50, 53}, {50, 54}, {50, 55}, {51, 52}, {51, 53}, {51, 54}, {51, 55}, {52, 53}, {52, 54}, {52, 55}, {53, 54}, {53, 55}, {54, 55}}) {
            this.addEdge(targetGraph, edge[0], edge[1]);
        }
    }

    public static Graph<Integer, DefaultEdge> chvatalGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateChvatalGraph(g);
        return g;
    }

    public void generateChvatalGraph(Graph<V, E> targetGraph) {
        int[][] edges;
        this.vertexMap.clear();
        for (int[] edge : edges = new int[][]{{0, 1}, {0, 4}, {0, 6}, {0, 9}, {1, 2}, {1, 5}, {1, 7}, {2, 3}, {2, 6}, {2, 8}, {3, 4}, {3, 7}, {3, 9}, {4, 5}, {4, 8}, {5, 10}, {5, 11}, {6, 10}, {6, 11}, {7, 8}, {7, 11}, {8, 10}, {9, 10}, {9, 11}}) {
            this.addEdge(targetGraph, edge[0], edge[1]);
        }
    }

    public static Graph<Integer, DefaultEdge> kittellGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateKittellGraph(g);
        return g;
    }

    public void generateKittellGraph(Graph<V, E> targetGraph) {
        int[][] edges;
        this.vertexMap.clear();
        for (int[] edge : edges = new int[][]{{0, 1}, {0, 2}, {0, 4}, {0, 5}, {0, 6}, {0, 7}, {1, 2}, {1, 7}, {1, 10}, {1, 11}, {1, 13}, {2, 4}, {2, 11}, {2, 14}, {3, 4}, {3, 5}, {3, 12}, {3, 14}, {3, 16}, {4, 5}, {4, 14}, {5, 6}, {5, 16}, {6, 7}, {6, 15}, {6, 16}, {6, 17}, {6, 18}, {7, 8}, {7, 13}, {7, 18}, {8, 9}, {8, 13}, {8, 18}, {8, 19}, {9, 10}, {9, 13}, {9, 19}, {9, 20}, {10, 11}, {10, 13}, {10, 20}, {10, 21}, {11, 12}, {11, 14}, {11, 15}, {11, 21}, {12, 14}, {12, 15}, {12, 16}, {15, 16}, {15, 17}, {15, 21}, {15, 22}, {17, 18}, {17, 19}, {17, 22}, {18, 19}, {19, 20}, {19, 22}, {20, 21}, {20, 22}, {21, 22}}) {
            this.addEdge(targetGraph, edge[0], edge[1]);
        }
    }

    public static Graph<Integer, DefaultEdge> coxeterGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateCoxeterGraph(g);
        return g;
    }

    public void generateCoxeterGraph(Graph<V, E> targetGraph) {
        int[][] edges;
        this.vertexMap.clear();
        for (int[] edge : edges = new int[][]{{0, 1}, {0, 23}, {0, 24}, {1, 2}, {1, 12}, {2, 3}, {2, 25}, {3, 4}, {3, 21}, {4, 5}, {4, 17}, {5, 6}, {5, 11}, {6, 7}, {6, 27}, {7, 8}, {7, 24}, {8, 9}, {8, 25}, {9, 10}, {9, 20}, {10, 11}, {10, 26}, {11, 12}, {12, 13}, {13, 14}, {13, 19}, {14, 15}, {14, 27}, {15, 16}, {15, 25}, {16, 17}, {16, 26}, {17, 18}, {18, 19}, {18, 24}, {19, 20}, {20, 21}, {21, 22}, {22, 23}, {22, 27}, {23, 26}}) {
            this.addEdge(targetGraph, edge[0], edge[1]);
        }
    }

    public static Graph<Integer, DefaultEdge> diamondGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateDiamondGraph(g);
        return g;
    }

    public void generateDiamondGraph(Graph<V, E> targetGraph) {
        int[][] edges;
        this.vertexMap.clear();
        for (int[] edge : edges = new int[][]{{0, 1}, {0, 2}, {0, 3}, {1, 2}, {2, 3}}) {
            this.addEdge(targetGraph, edge[0], edge[1]);
        }
    }

    public static Graph<Integer, DefaultEdge> ellinghamHorton54Graph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateEllinghamHorton54Graph(g);
        return g;
    }

    public void generateEllinghamHorton54Graph(Graph<V, E> targetGraph) {
        int[][] edges;
        this.vertexMap.clear();
        for (int[] edge : edges = new int[][]{{0, 1}, {0, 11}, {0, 15}, {1, 2}, {1, 47}, {2, 3}, {2, 13}, {3, 4}, {3, 8}, {4, 5}, {4, 15}, {5, 6}, {5, 10}, {6, 7}, {6, 30}, {7, 8}, {7, 12}, {8, 9}, {9, 10}, {9, 29}, {10, 11}, {11, 12}, {12, 13}, {13, 14}, {14, 15}, {14, 48}, {16, 17}, {16, 21}, {16, 28}, {17, 24}, {17, 29}, {18, 19}, {18, 23}, {18, 30}, {19, 20}, {19, 31}, {20, 21}, {20, 32}, {21, 33}, {22, 23}, {22, 27}, {22, 28}, {23, 29}, {24, 25}, {24, 30}, {25, 26}, {25, 31}, {26, 27}, {26, 32}, {27, 33}, {28, 31}, {32, 52}, {33, 53}, {34, 35}, {34, 39}, {34, 46}, {35, 42}, {35, 47}, {36, 37}, {36, 41}, {36, 48}, {37, 38}, {37, 49}, {38, 39}, {38, 50}, {39, 51}, {40, 41}, {40, 45}, {40, 46}, {41, 47}, {42, 43}, {42, 48}, {43, 44}, {43, 49}, {44, 45}, {44, 50}, {45, 51}, {46, 49}, {50, 52}, {51, 53}, {52, 53}}) {
            this.addEdge(targetGraph, edge[0], edge[1]);
        }
    }

    public static Graph<Integer, DefaultEdge> ellinghamHorton78Graph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateEllinghamHorton78Graph(g);
        return g;
    }

    public void generateEllinghamHorton78Graph(Graph<V, E> targetGraph) {
        int[][] edges;
        this.vertexMap.clear();
        for (int[] edge : edges = new int[][]{{0, 1}, {0, 5}, {0, 60}, {1, 2}, {1, 12}, {2, 3}, {2, 7}, {3, 4}, {3, 14}, {4, 5}, {4, 9}, {5, 6}, {6, 7}, {6, 11}, {7, 15}, {8, 9}, {8, 13}, {8, 22}, {9, 10}, {10, 11}, {10, 72}, {11, 12}, {12, 13}, {13, 14}, {14, 72}, {15, 16}, {15, 20}, {16, 17}, {16, 27}, {17, 18}, {17, 22}, {18, 19}, {18, 29}, {19, 20}, {19, 24}, {20, 21}, {21, 22}, {21, 26}, {23, 24}, {23, 28}, {23, 72}, {24, 25}, {25, 26}, {25, 71}, {26, 27}, {27, 28}, {28, 29}, {29, 69}, {30, 31}, {30, 35}, {30, 52}, {31, 32}, {31, 42}, {32, 33}, {32, 37}, {33, 34}, {33, 43}, {34, 35}, {34, 39}, {35, 36}, {36, 41}, {36, 63}, {37, 65}, {37, 66}, {38, 39}, {38, 59}, {38, 74}, {39, 40}, {40, 41}, {40, 44}, {41, 42}, {42, 74}, {43, 44}, {43, 74}, {44, 45}, {45, 46}, {45, 50}, {46, 47}, {46, 57}, {47, 48}, {47, 52}, {48, 49}, {48, 75}, {49, 50}, {49, 54}, {50, 51}, {51, 52}, {51, 56}, {53, 54}, {53, 58}, {53, 73}, {54, 55}, {55, 56}, {55, 59}, {56, 57}, {57, 58}, {58, 75}, {59, 75}, {60, 61}, {60, 64}, {61, 62}, {61, 71}, {62, 63}, {62, 77}, {63, 67}, {64, 65}, {64, 69}, {65, 77}, {66, 70}, {66, 73}, {67, 68}, {67, 73}, {68, 69}, {68, 76}, {70, 71}, {70, 76}, {76, 77}}) {
            this.addEdge(targetGraph, edge[0], edge[1]);
        }
    }

    public static Graph<Integer, DefaultEdge> erreraGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateErreraGraph(g);
        return g;
    }

    public void generateErreraGraph(Graph<V, E> targetGraph) {
        int[][] edges;
        this.vertexMap.clear();
        for (int[] edge : edges = new int[][]{{0, 1}, {0, 7}, {0, 14}, {0, 15}, {0, 16}, {1, 2}, {1, 9}, {1, 14}, {1, 15}, {2, 3}, {2, 8}, {2, 9}, {2, 10}, {2, 14}, {3, 4}, {3, 9}, {3, 10}, {3, 11}, {4, 5}, {4, 10}, {4, 11}, {4, 12}, {5, 6}, {5, 11}, {5, 12}, {5, 13}, {6, 7}, {6, 8}, {6, 12}, {6, 13}, {6, 16}, {7, 13}, {7, 15}, {7, 16}, {8, 10}, {8, 12}, {8, 14}, {8, 16}, {9, 11}, {9, 13}, {9, 15}, {10, 12}, {11, 13}, {13, 15}, {14, 16}}) {
            this.addEdge(targetGraph, edge[0], edge[1]);
        }
    }

    public static Graph<Integer, DefaultEdge> folkmanGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateFolkmanGraph(g);
        return g;
    }

    public void generateFolkmanGraph(Graph<V, E> targetGraph) {
        int[][] edges;
        this.vertexMap.clear();
        for (int[] edge : edges = new int[][]{{0, 1}, {0, 3}, {0, 13}, {0, 15}, {1, 2}, {1, 6}, {1, 8}, {2, 3}, {2, 17}, {2, 19}, {3, 6}, {3, 8}, {4, 5}, {4, 7}, {4, 17}, {4, 19}, {5, 6}, {5, 10}, {5, 12}, {6, 7}, {7, 10}, {7, 12}, {8, 9}, {8, 11}, {9, 10}, {9, 14}, {9, 16}, {10, 11}, {11, 14}, {11, 16}, {12, 13}, {12, 15}, {13, 14}, {13, 18}, {14, 15}, {15, 18}, {16, 17}, {16, 19}, {17, 18}, {18, 19}}) {
            this.addEdge(targetGraph, edge[0], edge[1]);
        }
    }

    public static Graph<Integer, DefaultEdge> franklinGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateFranklinGraph(g);
        return g;
    }

    public void generateFranklinGraph(Graph<V, E> targetGraph) {
        int[][] edges;
        this.vertexMap.clear();
        for (int[] edge : edges = new int[][]{{0, 1}, {0, 5}, {0, 6}, {1, 2}, {1, 7}, {2, 3}, {2, 8}, {3, 4}, {3, 9}, {4, 5}, {4, 10}, {5, 11}, {6, 7}, {6, 9}, {7, 10}, {8, 9}, {8, 11}, {10, 11}}) {
            this.addEdge(targetGraph, edge[0], edge[1]);
        }
    }

    public static Graph<Integer, DefaultEdge> fruchtGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateFruchtGraph(g);
        return g;
    }

    public void generateFruchtGraph(Graph<V, E> targetGraph) {
        int[][] edges;
        this.vertexMap.clear();
        for (int[] edge : edges = new int[][]{{0, 1}, {0, 6}, {0, 7}, {1, 2}, {1, 7}, {2, 3}, {2, 8}, {3, 4}, {3, 9}, {4, 5}, {4, 9}, {5, 6}, {5, 10}, {6, 10}, {7, 11}, {8, 9}, {8, 11}, {10, 11}}) {
            this.addEdge(targetGraph, edge[0], edge[1]);
        }
    }

    public static Graph<Integer, DefaultEdge> goldnerHararyGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateGoldnerHararyGraph(g);
        return g;
    }

    public void generateGoldnerHararyGraph(Graph<V, E> targetGraph) {
        int[][] edges;
        this.vertexMap.clear();
        for (int[] edge : edges = new int[][]{{0, 1}, {0, 3}, {0, 4}, {1, 2}, {1, 3}, {1, 4}, {1, 5}, {1, 6}, {1, 7}, {1, 10}, {2, 3}, {2, 7}, {3, 4}, {3, 7}, {3, 8}, {3, 9}, {3, 10}, {4, 5}, {4, 9}, {4, 10}, {5, 10}, {6, 7}, {6, 10}, {7, 8}, {7, 10}, {8, 10}, {9, 10}}) {
            this.addEdge(targetGraph, edge[0], edge[1]);
        }
    }

    public static Graph<Integer, DefaultEdge> heawoodGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateHeawoodGraph(g);
        return g;
    }

    public void generateHeawoodGraph(Graph<V, E> targetGraph) {
        int[][] edges;
        this.vertexMap.clear();
        for (int[] edge : edges = new int[][]{{0, 1}, {0, 5}, {0, 13}, {1, 2}, {1, 10}, {2, 3}, {2, 7}, {3, 4}, {3, 12}, {4, 5}, {4, 9}, {5, 6}, {6, 7}, {6, 11}, {7, 8}, {8, 9}, {8, 13}, {9, 10}, {10, 11}, {11, 12}, {12, 13}}) {
            this.addEdge(targetGraph, edge[0], edge[1]);
        }
    }

    public static Graph<Integer, DefaultEdge> herschelGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateHerschelGraph(g);
        return g;
    }

    public void generateHerschelGraph(Graph<V, E> targetGraph) {
        int[][] edges;
        this.vertexMap.clear();
        for (int[] edge : edges = new int[][]{{0, 1}, {0, 3}, {0, 4}, {1, 2}, {1, 5}, {1, 6}, {2, 3}, {2, 7}, {3, 8}, {3, 9}, {4, 5}, {4, 9}, {5, 10}, {6, 7}, {6, 10}, {7, 8}, {8, 10}, {9, 10}}) {
            this.addEdge(targetGraph, edge[0], edge[1]);
        }
    }

    public static Graph<Integer, DefaultEdge> hoffmanGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateHoffmanGraph(g);
        return g;
    }

    public void generateHoffmanGraph(Graph<V, E> targetGraph) {
        int[][] edges;
        this.vertexMap.clear();
        for (int[] edge : edges = new int[][]{{0, 1}, {0, 7}, {0, 8}, {0, 13}, {1, 2}, {1, 9}, {1, 14}, {2, 3}, {2, 8}, {2, 10}, {3, 4}, {3, 9}, {3, 15}, {4, 5}, {4, 10}, {4, 11}, {5, 6}, {5, 12}, {5, 14}, {6, 7}, {6, 11}, {6, 13}, {7, 12}, {7, 15}, {8, 12}, {8, 14}, {9, 11}, {9, 13}, {10, 12}, {10, 15}, {11, 14}, {13, 15}}) {
            this.addEdge(targetGraph, edge[0], edge[1]);
        }
    }

    public static Graph<Integer, DefaultEdge> krackhardtKiteGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateKrackhardtKiteGraph(g);
        return g;
    }

    public void generateKrackhardtKiteGraph(Graph<V, E> targetGraph) {
        int[][] edges;
        this.vertexMap.clear();
        for (int[] edge : edges = new int[][]{{0, 1}, {0, 2}, {0, 3}, {0, 5}, {1, 3}, {1, 4}, {1, 6}, {2, 3}, {2, 5}, {3, 4}, {3, 5}, {3, 6}, {4, 6}, {5, 6}, {5, 7}, {6, 7}, {7, 8}, {8, 9}}) {
            this.addEdge(targetGraph, edge[0], edge[1]);
        }
    }

    public static Graph<Integer, DefaultEdge> klein3RegularGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateKlein3RegularGraph(g);
        return g;
    }

    public void generateKlein3RegularGraph(Graph<V, E> targetGraph) {
        int[][] edges;
        this.vertexMap.clear();
        for (int[] edge : edges = new int[][]{{0, 3}, {0, 53}, {0, 55}, {1, 4}, {1, 30}, {1, 42}, {2, 6}, {2, 44}, {2, 55}, {3, 7}, {3, 10}, {4, 15}, {4, 22}, {5, 8}, {5, 13}, {5, 50}, {6, 9}, {6, 14}, {7, 12}, {7, 18}, {8, 9}, {8, 33}, {9, 12}, {10, 17}, {10, 29}, {11, 16}, {11, 25}, {11, 53}, {12, 19}, {13, 18}, {13, 54}, {14, 21}, {14, 37}, {15, 16}, {15, 17}, {16, 23}, {17, 20}, {18, 40}, {19, 20}, {19, 24}, {20, 27}, {21, 22}, {21, 24}, {22, 26}, {23, 28}, {23, 47}, {24, 31}, {25, 26}, {25, 44}, {26, 32}, {27, 28}, {27, 35}, {28, 33}, {29, 30}, {29, 46}, {30, 54}, {31, 34}, {31, 36}, {32, 34}, {32, 51}, {33, 39}, {34, 40}, {35, 36}, {35, 38}, {36, 43}, {37, 42}, {37, 48}, {38, 41}, {38, 46}, {39, 41}, {39, 44}, {40, 49}, {41, 51}, {42, 50}, {43, 45}, {43, 48}, {45, 47}, {45, 49}, {46, 52}, {47, 50}, {48, 52}, {49, 53}, {51, 54}, {52, 55}}) {
            this.addEdge(targetGraph, edge[0], edge[1]);
        }
    }

    public static Graph<Integer, DefaultEdge> klein7RegularGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateKlein7RegularGraph(g);
        return g;
    }

    public void generateKlein7RegularGraph(Graph<V, E> targetGraph) {
        int[][] edges;
        this.vertexMap.clear();
        int[] arr = new int[]{0, 1, 2, 3, 4, 5, 6};
        this.addCycle(targetGraph, arr);
        for (int[] edge : edges = new int[][]{{0, 2}, {0, 6}, {0, 10}, {0, 11}, {0, 12}, {0, 18}, {1, 3}, {1, 9}, {1, 11}, {1, 20}, {1, 22}, {2, 4}, {2, 10}, {2, 15}, {2, 19}, {3, 5}, {3, 7}, {3, 14}, {3, 22}, {4, 6}, {4, 8}, {4, 19}, {4, 21}, {5, 7}, {5, 11}, {5, 17}, {5, 23}, {6, 8}, {6, 11}, {6, 16}, {6, 18}, {7, 9}, {7, 14}, {7, 15}, {7, 16}, {7, 17}, {8, 10}, {8, 13}, {8, 14}, {8, 16}, {8, 21}, {9, 11}, {9, 13}, {9, 15}, {9, 16}, {9, 20}, {10, 12}, {10, 13}, {10, 14}, {10, 15}, {11, 13}, {11, 23}, {12, 14}, {12, 17}, {12, 18}, {12, 22}, {12, 23}, {13, 15}, {13, 21}, {13, 23}, {14, 16}, {14, 22}, {15, 17}, {15, 19}, {16, 18}, {16, 20}, {17, 18}, {17, 19}, {17, 23}, {18, 19}, {18, 20}, {19, 20}, {19, 21}, {20, 21}, {20, 22}, {21, 22}, {21, 23}, {22, 23}}) {
            this.addEdge(targetGraph, edge[0], edge[1]);
        }
    }

    public static Graph<Integer, DefaultEdge> moserSpindleGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateMoserSpindleGraph(g);
        return g;
    }

    public void generateMoserSpindleGraph(Graph<V, E> targetGraph) {
        int[][] edges;
        this.vertexMap.clear();
        for (int[] edge : edges = new int[][]{{0, 1}, {0, 4}, {0, 5}, {0, 6}, {1, 2}, {1, 5}, {2, 3}, {2, 5}, {3, 4}, {3, 6}, {4, 6}}) {
            this.addEdge(targetGraph, edge[0], edge[1]);
        }
    }

    public static Graph<Integer, DefaultEdge> pappusGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generatePappusGraph(g);
        return g;
    }

    public void generatePappusGraph(Graph<V, E> targetGraph) {
        int[][] edges;
        this.vertexMap.clear();
        for (int[] edge : edges = new int[][]{{0, 1}, {0, 5}, {0, 6}, {1, 2}, {1, 7}, {2, 3}, {2, 8}, {3, 4}, {3, 9}, {4, 5}, {4, 10}, {5, 11}, {6, 13}, {6, 17}, {7, 12}, {7, 14}, {8, 13}, {8, 15}, {9, 14}, {9, 16}, {10, 15}, {10, 17}, {11, 12}, {11, 16}, {12, 15}, {13, 16}, {14, 17}}) {
            this.addEdge(targetGraph, edge[0], edge[1]);
        }
    }

    public static Graph<Integer, DefaultEdge> poussinGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generatePoussinGraph(g);
        return g;
    }

    public void generatePoussinGraph(Graph<V, E> targetGraph) {
        int[][] edges;
        this.vertexMap.clear();
        int[] arr = new int[]{0, 1, 2, 3, 4, 5, 6};
        this.addCycle(targetGraph, arr);
        int[] arr1 = new int[]{9, 10, 11, 12, 13, 14};
        this.addCycle(targetGraph, arr1);
        for (int[] edge : edges = new int[][]{{0, 2}, {0, 4}, {0, 5}, {1, 6}, {1, 7}, {2, 4}, {2, 7}, {2, 8}, {3, 5}, {3, 8}, {3, 9}, {3, 13}, {5, 9}, {5, 10}, {6, 7}, {6, 10}, {6, 11}, {7, 8}, {7, 11}, {7, 12}, {8, 12}, {8, 13}, {9, 13}, {10, 14}, {11, 14}, {12, 14}}) {
            this.addEdge(targetGraph, edge[0], edge[1]);
        }
    }

    public static Graph<Integer, DefaultEdge> schl\u00e4fliGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateSchl\u00e4fliGraph(g);
        return g;
    }

    public void generateSchl\u00e4fliGraph(Graph<V, E> targetGraph) {
        int[][] edges;
        this.vertexMap.clear();
        for (int[] edge : edges = new int[][]{{0, 11}, {0, 12}, {0, 13}, {0, 14}, {0, 15}, {0, 16}, {0, 17}, {0, 18}, {0, 19}, {0, 20}, {0, 21}, {0, 22}, {0, 23}, {0, 24}, {0, 25}, {0, 26}, {1, 3}, {1, 4}, {1, 5}, {1, 6}, {1, 7}, {1, 8}, {1, 9}, {1, 10}, {1, 19}, {1, 20}, {1, 21}, {1, 22}, {1, 23}, {1, 24}, {1, 25}, {1, 26}, {2, 3}, {2, 4}, {2, 5}, {2, 6}, {2, 7}, {2, 8}, {2, 9}, {2, 10}, {2, 11}, {2, 12}, {2, 13}, {2, 14}, {2, 15}, {2, 16}, {2, 17}, {2, 18}, {3, 5}, {3, 6}, {3, 7}, {3, 8}, {3, 9}, {3, 10}, {3, 15}, {3, 16}, {3, 17}, {3, 18}, {3, 23}, {3, 24}, {3, 25}, {3, 26}, {4, 5}, {4, 6}, {4, 7}, {4, 8}, {4, 9}, {4, 10}, {4, 11}, {4, 12}, {4, 13}, {4, 14}, {4, 19}, {4, 20}, {4, 21}, {4, 22}, {5, 7}, {5, 8}, {5, 9}, {5, 10}, {5, 13}, {5, 14}, {5, 17}, {5, 18}, {5, 21}, {5, 22}, {5, 25}, {5, 26}, {6, 7}, {6, 8}, {6, 9}, {6, 10}, {6, 11}, {6, 12}, {6, 15}, {6, 16}, {6, 19}, {6, 20}, {6, 23}, {6, 24}, {7, 9}, {7, 10}, {7, 12}, {7, 14}, {7, 16}, {7, 18}, {7, 20}, {7, 22}, {7, 24}, {7, 26}, {8, 9}, {8, 10}, {8, 11}, {8, 13}, {8, 15}, {8, 17}, {8, 19}, {8, 21}, {8, 23}, {8, 25}, {9, 12}, {9, 13}, {9, 15}, {9, 18}, {9, 19}, {9, 22}, {9, 24}, {9, 25}, {10, 11}, {10, 14}, {10, 16}, {10, 17}, {10, 20}, {10, 21}, {10, 23}, {10, 26}, {11, 12}, {11, 13}, {11, 14}, {11, 15}, {11, 16}, {11, 17}, {11, 19}, {11, 20}, {11, 21}, {11, 23}, {12, 13}, {12, 14}, {12, 15}, {12, 16}, {12, 18}, {12, 19}, {12, 20}, {12, 22}, {12, 24}, {13, 14}, {13, 15}, {13, 17}, {13, 18}, {13, 19}, {13, 21}, {13, 22}, {13, 25}, {14, 16}, {14, 17}, {14, 18}, {14, 20}, {14, 21}, {14, 22}, {14, 26}, {15, 16}, {15, 17}, {15, 18}, {15, 19}, {15, 23}, {15, 24}, {15, 25}, {16, 17}, {16, 18}, {16, 20}, {16, 23}, {16, 24}, {16, 26}, {17, 18}, {17, 21}, {17, 23}, {17, 25}, {17, 26}, {18, 22}, {18, 24}, {18, 25}, {18, 26}, {19, 20}, {19, 21}, {19, 22}, {19, 23}, {19, 24}, {19, 25}, {20, 21}, {20, 22}, {20, 23}, {20, 24}, {20, 26}, {21, 22}, {21, 23}, {21, 25}, {21, 26}, {22, 24}, {22, 25}, {22, 26}, {23, 24}, {23, 25}, {23, 26}, {24, 25}, {24, 26}, {25, 26}}) {
            this.addEdge(targetGraph, edge[0], edge[1]);
        }
    }

    public static Graph<Integer, DefaultEdge> tietzeGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateTietzeGraph(g);
        return g;
    }

    public void generateTietzeGraph(Graph<V, E> targetGraph) {
        int[][] edges;
        this.vertexMap.clear();
        int[] arr = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
        this.addCycle(targetGraph, arr);
        for (int[] edge : edges = new int[][]{{0, 9}, {1, 5}, {2, 7}, {3, 10}, {4, 8}, {6, 11}, {9, 10}, {9, 11}, {10, 11}}) {
            this.addEdge(targetGraph, edge[0], edge[1]);
        }
    }

    public static Graph<Integer, DefaultEdge> thomsenGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateThomsenGraph(g);
        return g;
    }

    public void generateThomsenGraph(Graph<V, E> targetGraph) {
        int[][] edges;
        this.vertexMap.clear();
        for (int[] edge : edges = new int[][]{{0, 3}, {0, 4}, {0, 5}, {1, 3}, {1, 4}, {1, 5}, {2, 3}, {2, 4}, {2, 5}}) {
            this.addEdge(targetGraph, edge[0], edge[1]);
        }
    }

    public static Graph<Integer, DefaultEdge> tutteGraph() {
        Graph<Integer, DefaultEdge> g = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class).buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateTutteGraph(g);
        return g;
    }

    public void generateTutteGraph(Graph<V, E> targetGraph) {
        int[][] edges;
        this.vertexMap.clear();
        for (int[] edge : edges = new int[][]{{0, 1}, {0, 16}, {0, 31}, {1, 2}, {1, 4}, {2, 3}, {2, 5}, {3, 4}, {3, 7}, {4, 9}, {5, 6}, {5, 10}, {6, 7}, {6, 11}, {7, 8}, {8, 9}, {8, 12}, {9, 15}, {10, 11}, {10, 13}, {11, 12}, {12, 14}, {13, 14}, {13, 30}, {14, 15}, {15, 43}, {16, 17}, {16, 19}, {17, 18}, {17, 20}, {18, 19}, {18, 22}, {19, 24}, {20, 21}, {20, 25}, {21, 22}, {21, 26}, {22, 23}, {23, 24}, {23, 27}, {24, 30}, {25, 26}, {25, 28}, {26, 27}, {27, 29}, {28, 29}, {28, 45}, {29, 30}, {31, 32}, {31, 34}, {32, 33}, {32, 35}, {33, 34}, {33, 37}, {34, 39}, {35, 36}, {35, 40}, {36, 37}, {36, 41}, {37, 38}, {38, 39}, {38, 42}, {39, 45}, {40, 41}, {40, 43}, {41, 42}, {42, 44}, {43, 44}, {44, 45}}) {
            this.addEdge(targetGraph, edge[0], edge[1]);
        }
    }

    public void generateZacharyKarateClubGraph(Graph<V, E> targetGraph) {
        int[][] edges;
        this.vertexMap.clear();
        for (int[] edge : edges = new int[][]{{0, 1}, {0, 2}, {0, 3}, {0, 4}, {0, 5}, {0, 6}, {0, 7}, {0, 8}, {0, 10}, {0, 11}, {0, 12}, {0, 13}, {0, 17}, {0, 19}, {0, 21}, {0, 31}, {1, 2}, {1, 3}, {1, 7}, {1, 13}, {1, 17}, {1, 19}, {1, 21}, {1, 30}, {2, 3}, {2, 7}, {2, 8}, {2, 9}, {2, 13}, {2, 27}, {2, 28}, {2, 32}, {3, 7}, {3, 12}, {3, 13}, {4, 6}, {4, 10}, {5, 6}, {5, 10}, {5, 16}, {6, 16}, {8, 30}, {8, 32}, {8, 33}, {9, 33}, {13, 33}, {14, 32}, {14, 33}, {15, 32}, {15, 33}, {18, 32}, {18, 33}, {19, 33}, {20, 32}, {20, 33}, {22, 32}, {22, 33}, {23, 25}, {23, 27}, {23, 29}, {23, 32}, {23, 33}, {24, 25}, {24, 27}, {24, 31}, {25, 31}, {26, 29}, {26, 33}, {27, 33}, {28, 31}, {28, 33}, {29, 32}, {29, 33}, {30, 32}, {30, 33}, {31, 32}, {31, 33}, {32, 33}}) {
            this.addEdge(targetGraph, edge[0], edge[1]);
        }
    }

    private V addVertex(Graph<V, E> targetGraph, int i) {
        return (V)this.vertexMap.computeIfAbsent(i, i1 -> targetGraph.addVertex());
    }

    private void addEdge(Graph<V, E> targetGraph, int i, int j) {
        V u = this.addVertex(targetGraph, i);
        V v = this.addVertex(targetGraph, j);
        targetGraph.addEdge(u, v);
    }

    private void addCycle(Graph<V, E> targetGraph, int[] array) {
        for (int i = 0; i < array.length; ++i) {
            this.addEdge(targetGraph, array[i], array[(i + 1) % array.length]);
        }
    }
}

