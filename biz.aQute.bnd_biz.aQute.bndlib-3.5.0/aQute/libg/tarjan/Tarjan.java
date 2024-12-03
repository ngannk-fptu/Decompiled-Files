/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.tarjan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Tarjan<T> {
    private int index = 0;
    private List<Node> stack = new ArrayList<Node>();
    private Set<Set<T>> scc = new HashSet<Set<T>>();
    private Node root = new Node(null);

    void tarjan(Node v) {
        v.index = this.index;
        v.low = this.index++;
        this.stack.add(0, v);
        for (Node n : v.adjacent) {
            if (n.index == -1) {
                this.tarjan(n);
                v.low = Math.min(v.low, n.low);
                continue;
            }
            if (!this.stack.contains(n)) continue;
            v.low = Math.min(v.low, n.index);
        }
        if (v != this.root && v.low == v.index) {
            Node n;
            HashSet component = new HashSet();
            do {
                n = this.stack.remove(0);
                component.add(n.name);
            } while (n != v);
            this.scc.add(component);
        }
    }

    Set<Set<T>> getResult(Map<T, ? extends Collection<T>> graph) {
        HashMap index = new HashMap();
        for (Map.Entry<T, Collection<T>> entry : graph.entrySet()) {
            Node node = this.getNode(index, entry.getKey());
            this.root.adjacent.add(node);
            for (T adj : entry.getValue()) {
                node.adjacent.add(this.getNode(index, adj));
            }
        }
        this.tarjan(this.root);
        return this.scc;
    }

    private Node getNode(Map<T, Node> index, T key) {
        Node node = index.get(key);
        if (node == null) {
            node = new Node(key);
            index.put(key, node);
        }
        return node;
    }

    public static <T> Collection<? extends Collection<T>> tarjan(Map<T, ? extends Collection<T>> graph) {
        Tarjan<T> tarjan = new Tarjan<T>();
        return tarjan.getResult(graph);
    }

    public class Node {
        final T name;
        final List<Node> adjacent = new ArrayList<Node>();
        int low = -1;
        int index = -1;

        public Node(T name) {
            this.name = name;
        }

        public String toString() {
            return this.name + "{" + this.index + "," + this.low + "}";
        }
    }
}

