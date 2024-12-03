/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.connectivity;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.jgrapht.util.AVLTree;
import org.jgrapht.util.DoublyLinkedList;

public class TreeDynamicConnectivity<T> {
    private Map<AVLTree.TreeNode<T>, AVLTree<T>> minToTreeMap = new HashMap<AVLTree.TreeNode<T>, AVLTree<T>>();
    private Map<T, Node> nodeMap = new HashMap<T, Node>();
    private Map<Node, AVLTree<T>> singletonNodes = new HashMap<Node, AVLTree<T>>();

    public boolean add(T element) {
        if (this.contains(element)) {
            return false;
        }
        AVLTree newTree = new AVLTree();
        Node node = new Node(element);
        this.nodeMap.put(element, node);
        this.singletonNodes.put(node, newTree);
        return true;
    }

    public boolean remove(T element) {
        if (!this.contains(element)) {
            return false;
        }
        Node node = this.getNode(element);
        while (!node.isSingleton()) {
            Object targetValue = node.arcs.getLast().target.value;
            this.cut(element, targetValue);
        }
        this.nodeMap.remove(element);
        this.singletonNodes.remove(node);
        return true;
    }

    public boolean contains(T element) {
        return this.nodeMap.containsKey(element);
    }

    public boolean link(T first, T second) {
        this.addIfAbsent(first);
        this.addIfAbsent(second);
        if (this.connected(first, second)) {
            return false;
        }
        Node firstNode = this.getNode(first);
        Node secondNode = this.getNode(second);
        AVLTree<T> firstTree = this.getTree(firstNode);
        AVLTree<T> secondTree = this.getTree(secondNode);
        this.minToTreeMap.remove(firstTree.getMin());
        this.minToTreeMap.remove(secondTree.getMin());
        this.makeRoot(firstTree, firstNode);
        this.makeRoot(secondTree, secondNode);
        AVLTree.TreeNode<T> newFirstOccurrence = secondTree.addMin(first);
        Arc newFirstArc = new Arc(secondNode, newFirstOccurrence);
        if (firstNode.isSingleton()) {
            this.singletonNodes.remove(firstNode);
            firstNode.addArcLast(newFirstArc);
        } else {
            T lastChild = firstTree.getMax().getValue();
            Node lastChildNode = this.getNode(lastChild);
            Arc arcToLastChild = firstNode.getArcTo(lastChildNode);
            firstNode.addArcAfter(arcToLastChild, newFirstArc);
        }
        AVLTree.TreeNode<T> newSecondOccurrence = secondTree.addMax(second);
        Arc newSecondArc = new Arc(firstNode, newSecondOccurrence);
        if (secondNode.isSingleton()) {
            this.singletonNodes.remove(secondNode);
            secondNode.addArcLast(newSecondArc);
        } else {
            T lastChild = secondTree.getMax().getPredecessor().getValue();
            Node lastChildNode = this.getNode(lastChild);
            Arc arcToLastChild = secondNode.getArcTo(lastChildNode);
            secondNode.addArcAfter(arcToLastChild, newSecondArc);
        }
        firstTree.mergeAfter(secondTree);
        this.minToTreeMap.put(firstTree.getMin(), firstTree);
        return true;
    }

    public boolean connected(T first, T second) {
        if (!this.contains(first) || !this.contains(second)) {
            return false;
        }
        Node firstNode = this.getNode(first);
        if (firstNode.isSingleton()) {
            return false;
        }
        Node secondNode = this.getNode(second);
        if (secondNode.isSingleton()) {
            return false;
        }
        return this.getTree(firstNode) == this.getTree(secondNode);
    }

    public boolean cut(T first, T second) {
        if (!this.connected(first, second)) {
            return false;
        }
        Node firstNode = this.getNode(first);
        Node secondNode = this.getNode(second);
        AVLTree tree = this.getTree(firstNode);
        this.minToTreeMap.remove(tree.getMin());
        Arc arcToSecond = firstNode.getArcTo(secondNode);
        if (arcToSecond == null) {
            throw new IllegalArgumentException(String.format("Elements {%s} and {%s} are not connected", first, second));
        }
        this.makeLastArc(tree, firstNode, arcToSecond);
        AVLTree<T> right = tree.splitAfter(arcToSecond.arcTreeNode);
        tree.removeMax();
        firstNode.removeArc(arcToSecond);
        if (!firstNode.isSingleton()) {
            this.minToTreeMap.put(tree.getMin(), tree);
        } else {
            this.singletonNodes.put(firstNode, tree);
        }
        Arc secondToFirst = secondNode.getArcTo(firstNode);
        right.removeMax();
        secondNode.removeArc(secondToFirst);
        if (!secondNode.isSingleton()) {
            this.minToTreeMap.put(right.getMin(), right);
        } else {
            this.singletonNodes.put(secondNode, right);
        }
        return true;
    }

    private void makeRoot(AVLTree<T> tree, Node node) {
        if (node.arcs.isEmpty()) {
            return;
        }
        this.makeFirstArc(tree, node.arcs.get(0));
    }

    private void makeFirstArc(AVLTree<T> tree, Arc arc) {
        AVLTree<T> right = tree.splitBefore(arc.arcTreeNode);
        tree.mergeBefore(right);
    }

    private void makeLastArc(AVLTree<T> tree, Node node, Arc arc) {
        if (node.arcs.size() == 1) {
            this.makeRoot(tree, node);
        } else {
            Arc nextArc = node.getNextArc(arc);
            this.makeFirstArc(tree, nextArc);
        }
    }

    private Node getNode(T element) {
        return this.nodeMap.get(element);
    }

    private AVLTree<T> getTree(Node node) {
        if (node.isSingleton()) {
            return this.singletonNodes.get(node);
        }
        return this.minToTreeMap.get(node.arcs.get((int)0).arcTreeNode.getTreeMin());
    }

    private void addIfAbsent(T element) {
        if (!this.contains(element)) {
            this.add(element);
        }
    }

    private class Node {
        T value;
        DoublyLinkedList<Arc> arcs;
        Map<Node, Arc> targetMap;

        public Node(T value) {
            this.value = value;
            this.arcs = new DoublyLinkedList();
            this.targetMap = new HashMap<Node, Arc>();
        }

        void removeArc(Arc arc) {
            this.arcs.removeNode(arc.listNode);
            arc.listNode = null;
            this.targetMap.remove(arc.target);
        }

        void addArcLast(Arc arc) {
            arc.listNode = this.arcs.addElementLast(arc);
            this.targetMap.put(arc.target, arc);
        }

        void addArcAfter(Arc arc, Arc newArc) {
            newArc.listNode = this.arcs.addElementBeforeNode(arc.listNode.getNext(), newArc);
            this.targetMap.put(newArc.target, newArc);
        }

        Arc getArcTo(Node node) {
            return this.targetMap.get(node);
        }

        Arc getNextArc(Arc arc) {
            return arc.listNode.getNext().getValue();
        }

        public boolean isSingleton() {
            return this.arcs.isEmpty();
        }

        public String toString() {
            return String.format("{%s} -> [%s]", this.value, this.arcs.stream().map(a -> a.target.value.toString()).collect(Collectors.joining(",")));
        }
    }

    private class Arc {
        Node target;
        DoublyLinkedList.ListNode<Arc> listNode;
        AVLTree.TreeNode<T> arcTreeNode;

        public Arc(Node target, AVLTree.TreeNode<T> arcTreeNode) {
            this.target = target;
            this.arcTreeNode = arcTreeNode;
        }

        public String toString() {
            return String.format("{%s} -> {%s}", this.arcTreeNode.getValue(), this.target.value);
        }
    }
}

