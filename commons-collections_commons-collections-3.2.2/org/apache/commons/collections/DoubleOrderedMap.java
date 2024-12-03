/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.commons.collections.KeyValue;

public final class DoubleOrderedMap
extends AbstractMap {
    private static final int KEY = 0;
    private static final int VALUE = 1;
    private static final int SUM_OF_INDICES = 1;
    private static final int FIRST_INDEX = 0;
    private static final int NUMBER_OF_INDICES = 2;
    private static final String[] dataName = new String[]{"key", "value"};
    private Node[] rootNode = new Node[]{null, null};
    private int nodeCount = 0;
    private int modifications = 0;
    private Set[] setOfKeys = new Set[]{null, null};
    private Set[] setOfEntries = new Set[]{null, null};
    private Collection[] collectionOfValues = new Collection[]{null, null};

    public DoubleOrderedMap() {
    }

    public DoubleOrderedMap(Map map) throws ClassCastException, NullPointerException, IllegalArgumentException {
        this.putAll(map);
    }

    public Object getKeyForValue(Object value) throws ClassCastException, NullPointerException {
        return this.doGet((Comparable)value, 1);
    }

    public Object removeValue(Object value) {
        return this.doRemove((Comparable)value, 1);
    }

    public Set entrySetByValue() {
        if (this.setOfEntries[1] == null) {
            this.setOfEntries[1] = new AbstractSet(){

                public Iterator iterator() {
                    return new DoubleOrderedMapIterator(1){

                        protected Object doGetNext() {
                            return this.lastReturnedNode;
                        }
                    };
                }

                public boolean contains(Object o) {
                    if (!(o instanceof Map.Entry)) {
                        return false;
                    }
                    Map.Entry entry = (Map.Entry)o;
                    Object key = entry.getKey();
                    Node node = DoubleOrderedMap.this.lookup((Comparable)entry.getValue(), 1);
                    return node != null && node.getData(0).equals(key);
                }

                public boolean remove(Object o) {
                    if (!(o instanceof Map.Entry)) {
                        return false;
                    }
                    Map.Entry entry = (Map.Entry)o;
                    Object key = entry.getKey();
                    Node node = DoubleOrderedMap.this.lookup((Comparable)entry.getValue(), 1);
                    if (node != null && node.getData(0).equals(key)) {
                        DoubleOrderedMap.this.doRedBlackDelete(node);
                        return true;
                    }
                    return false;
                }

                public int size() {
                    return DoubleOrderedMap.this.size();
                }

                public void clear() {
                    DoubleOrderedMap.this.clear();
                }

                static /* synthetic */ DoubleOrderedMap access$000(1 x0) {
                    return x0.DoubleOrderedMap.this;
                }
            };
        }
        return this.setOfEntries[1];
    }

    public Set keySetByValue() {
        if (this.setOfKeys[1] == null) {
            this.setOfKeys[1] = new AbstractSet(){

                public Iterator iterator() {
                    return new DoubleOrderedMapIterator(1){

                        protected Object doGetNext() {
                            return this.lastReturnedNode.getData(0);
                        }
                    };
                }

                public int size() {
                    return DoubleOrderedMap.this.size();
                }

                public boolean contains(Object o) {
                    return DoubleOrderedMap.this.containsKey(o);
                }

                public boolean remove(Object o) {
                    int oldnodeCount = DoubleOrderedMap.this.nodeCount;
                    DoubleOrderedMap.this.remove(o);
                    return DoubleOrderedMap.this.nodeCount != oldnodeCount;
                }

                public void clear() {
                    DoubleOrderedMap.this.clear();
                }

                static /* synthetic */ DoubleOrderedMap access$400(2 x0) {
                    return x0.DoubleOrderedMap.this;
                }
            };
        }
        return this.setOfKeys[1];
    }

    public Collection valuesByValue() {
        if (this.collectionOfValues[1] == null) {
            this.collectionOfValues[1] = new AbstractCollection(){

                public Iterator iterator() {
                    return new DoubleOrderedMapIterator(1){

                        protected Object doGetNext() {
                            return this.lastReturnedNode.getData(1);
                        }
                    };
                }

                public int size() {
                    return DoubleOrderedMap.this.size();
                }

                public boolean contains(Object o) {
                    return DoubleOrderedMap.this.containsValue(o);
                }

                public boolean remove(Object o) {
                    int oldnodeCount = DoubleOrderedMap.this.nodeCount;
                    DoubleOrderedMap.this.removeValue(o);
                    return DoubleOrderedMap.this.nodeCount != oldnodeCount;
                }

                public boolean removeAll(Collection c) {
                    boolean modified = false;
                    Iterator iter = c.iterator();
                    while (iter.hasNext()) {
                        if (DoubleOrderedMap.this.removeValue(iter.next()) == null) continue;
                        modified = true;
                    }
                    return modified;
                }

                public void clear() {
                    DoubleOrderedMap.this.clear();
                }

                static /* synthetic */ DoubleOrderedMap access$600(3 x0) {
                    return x0.DoubleOrderedMap.this;
                }
            };
        }
        return this.collectionOfValues[1];
    }

    private Object doRemove(Comparable o, int index) {
        Node node = this.lookup(o, index);
        Comparable rval = null;
        if (node != null) {
            rval = node.getData(this.oppositeIndex(index));
            this.doRedBlackDelete(node);
        }
        return rval;
    }

    private Object doGet(Comparable o, int index) {
        DoubleOrderedMap.checkNonNullComparable(o, index);
        Node node = this.lookup(o, index);
        return node == null ? null : node.getData(this.oppositeIndex(index));
    }

    private int oppositeIndex(int index) {
        return 1 - index;
    }

    private Node lookup(Comparable data, int index) {
        Node rval = null;
        Node node = this.rootNode[index];
        while (node != null) {
            int cmp = DoubleOrderedMap.compare(data, node.getData(index));
            if (cmp == 0) {
                rval = node;
                break;
            }
            node = cmp < 0 ? node.getLeft(index) : node.getRight(index);
        }
        return rval;
    }

    private static int compare(Comparable o1, Comparable o2) {
        return o1.compareTo(o2);
    }

    private static Node leastNode(Node node, int index) {
        Node rval = node;
        if (rval != null) {
            while (rval.getLeft(index) != null) {
                rval = rval.getLeft(index);
            }
        }
        return rval;
    }

    private Node nextGreater(Node node, int index) {
        Node rval = null;
        if (node == null) {
            rval = null;
        } else if (node.getRight(index) != null) {
            rval = DoubleOrderedMap.leastNode(node.getRight(index), index);
        } else {
            Node parent = node.getParent(index);
            Node child = node;
            while (parent != null && child == parent.getRight(index)) {
                child = parent;
                parent = parent.getParent(index);
            }
            rval = parent;
        }
        return rval;
    }

    private static void copyColor(Node from, Node to, int index) {
        if (to != null) {
            if (from == null) {
                to.setBlack(index);
            } else {
                to.copyColor(from, index);
            }
        }
    }

    private static boolean isRed(Node node, int index) {
        return node == null ? false : node.isRed(index);
    }

    private static boolean isBlack(Node node, int index) {
        return node == null ? true : node.isBlack(index);
    }

    private static void makeRed(Node node, int index) {
        if (node != null) {
            node.setRed(index);
        }
    }

    private static void makeBlack(Node node, int index) {
        if (node != null) {
            node.setBlack(index);
        }
    }

    private static Node getGrandParent(Node node, int index) {
        return DoubleOrderedMap.getParent(DoubleOrderedMap.getParent(node, index), index);
    }

    private static Node getParent(Node node, int index) {
        return node == null ? null : node.getParent(index);
    }

    private static Node getRightChild(Node node, int index) {
        return node == null ? null : node.getRight(index);
    }

    private static Node getLeftChild(Node node, int index) {
        return node == null ? null : node.getLeft(index);
    }

    private static boolean isLeftChild(Node node, int index) {
        return node == null ? true : (node.getParent(index) == null ? false : node == node.getParent(index).getLeft(index));
    }

    private static boolean isRightChild(Node node, int index) {
        return node == null ? true : (node.getParent(index) == null ? false : node == node.getParent(index).getRight(index));
    }

    private void rotateLeft(Node node, int index) {
        Node rightChild = node.getRight(index);
        node.setRight(rightChild.getLeft(index), index);
        if (rightChild.getLeft(index) != null) {
            rightChild.getLeft(index).setParent(node, index);
        }
        rightChild.setParent(node.getParent(index), index);
        if (node.getParent(index) == null) {
            this.rootNode[index] = rightChild;
        } else if (node.getParent(index).getLeft(index) == node) {
            node.getParent(index).setLeft(rightChild, index);
        } else {
            node.getParent(index).setRight(rightChild, index);
        }
        rightChild.setLeft(node, index);
        node.setParent(rightChild, index);
    }

    private void rotateRight(Node node, int index) {
        Node leftChild = node.getLeft(index);
        node.setLeft(leftChild.getRight(index), index);
        if (leftChild.getRight(index) != null) {
            leftChild.getRight(index).setParent(node, index);
        }
        leftChild.setParent(node.getParent(index), index);
        if (node.getParent(index) == null) {
            this.rootNode[index] = leftChild;
        } else if (node.getParent(index).getRight(index) == node) {
            node.getParent(index).setRight(leftChild, index);
        } else {
            node.getParent(index).setLeft(leftChild, index);
        }
        leftChild.setRight(node, index);
        node.setParent(leftChild, index);
    }

    private void doRedBlackInsert(Node insertedNode, int index) {
        Node currentNode = insertedNode;
        DoubleOrderedMap.makeRed(currentNode, index);
        while (currentNode != null && currentNode != this.rootNode[index] && DoubleOrderedMap.isRed(currentNode.getParent(index), index)) {
            Node y;
            if (DoubleOrderedMap.isLeftChild(DoubleOrderedMap.getParent(currentNode, index), index)) {
                y = DoubleOrderedMap.getRightChild(DoubleOrderedMap.getGrandParent(currentNode, index), index);
                if (DoubleOrderedMap.isRed(y, index)) {
                    DoubleOrderedMap.makeBlack(DoubleOrderedMap.getParent(currentNode, index), index);
                    DoubleOrderedMap.makeBlack(y, index);
                    DoubleOrderedMap.makeRed(DoubleOrderedMap.getGrandParent(currentNode, index), index);
                    currentNode = DoubleOrderedMap.getGrandParent(currentNode, index);
                    continue;
                }
                if (DoubleOrderedMap.isRightChild(currentNode, index)) {
                    currentNode = DoubleOrderedMap.getParent(currentNode, index);
                    this.rotateLeft(currentNode, index);
                }
                DoubleOrderedMap.makeBlack(DoubleOrderedMap.getParent(currentNode, index), index);
                DoubleOrderedMap.makeRed(DoubleOrderedMap.getGrandParent(currentNode, index), index);
                if (DoubleOrderedMap.getGrandParent(currentNode, index) == null) continue;
                this.rotateRight(DoubleOrderedMap.getGrandParent(currentNode, index), index);
                continue;
            }
            y = DoubleOrderedMap.getLeftChild(DoubleOrderedMap.getGrandParent(currentNode, index), index);
            if (DoubleOrderedMap.isRed(y, index)) {
                DoubleOrderedMap.makeBlack(DoubleOrderedMap.getParent(currentNode, index), index);
                DoubleOrderedMap.makeBlack(y, index);
                DoubleOrderedMap.makeRed(DoubleOrderedMap.getGrandParent(currentNode, index), index);
                currentNode = DoubleOrderedMap.getGrandParent(currentNode, index);
                continue;
            }
            if (DoubleOrderedMap.isLeftChild(currentNode, index)) {
                currentNode = DoubleOrderedMap.getParent(currentNode, index);
                this.rotateRight(currentNode, index);
            }
            DoubleOrderedMap.makeBlack(DoubleOrderedMap.getParent(currentNode, index), index);
            DoubleOrderedMap.makeRed(DoubleOrderedMap.getGrandParent(currentNode, index), index);
            if (DoubleOrderedMap.getGrandParent(currentNode, index) == null) continue;
            this.rotateLeft(DoubleOrderedMap.getGrandParent(currentNode, index), index);
        }
        DoubleOrderedMap.makeBlack(this.rootNode[index], index);
    }

    private void doRedBlackDelete(Node deletedNode) {
        for (int index = 0; index < 2; ++index) {
            Node replacement;
            if (deletedNode.getLeft(index) != null && deletedNode.getRight(index) != null) {
                this.swapPosition(this.nextGreater(deletedNode, index), deletedNode, index);
            }
            Node node = replacement = deletedNode.getLeft(index) != null ? deletedNode.getLeft(index) : deletedNode.getRight(index);
            if (replacement != null) {
                replacement.setParent(deletedNode.getParent(index), index);
                if (deletedNode.getParent(index) == null) {
                    this.rootNode[index] = replacement;
                } else if (deletedNode == deletedNode.getParent(index).getLeft(index)) {
                    deletedNode.getParent(index).setLeft(replacement, index);
                } else {
                    deletedNode.getParent(index).setRight(replacement, index);
                }
                deletedNode.setLeft(null, index);
                deletedNode.setRight(null, index);
                deletedNode.setParent(null, index);
                if (!DoubleOrderedMap.isBlack(deletedNode, index)) continue;
                this.doRedBlackDeleteFixup(replacement, index);
                continue;
            }
            if (deletedNode.getParent(index) == null) {
                this.rootNode[index] = null;
                continue;
            }
            if (DoubleOrderedMap.isBlack(deletedNode, index)) {
                this.doRedBlackDeleteFixup(deletedNode, index);
            }
            if (deletedNode.getParent(index) == null) continue;
            if (deletedNode == deletedNode.getParent(index).getLeft(index)) {
                deletedNode.getParent(index).setLeft(null, index);
            } else {
                deletedNode.getParent(index).setRight(null, index);
            }
            deletedNode.setParent(null, index);
        }
        this.shrink();
    }

    private void doRedBlackDeleteFixup(Node replacementNode, int index) {
        Node currentNode = replacementNode;
        while (currentNode != this.rootNode[index] && DoubleOrderedMap.isBlack(currentNode, index)) {
            Node siblingNode;
            if (DoubleOrderedMap.isLeftChild(currentNode, index)) {
                siblingNode = DoubleOrderedMap.getRightChild(DoubleOrderedMap.getParent(currentNode, index), index);
                if (DoubleOrderedMap.isRed(siblingNode, index)) {
                    DoubleOrderedMap.makeBlack(siblingNode, index);
                    DoubleOrderedMap.makeRed(DoubleOrderedMap.getParent(currentNode, index), index);
                    this.rotateLeft(DoubleOrderedMap.getParent(currentNode, index), index);
                    siblingNode = DoubleOrderedMap.getRightChild(DoubleOrderedMap.getParent(currentNode, index), index);
                }
                if (DoubleOrderedMap.isBlack(DoubleOrderedMap.getLeftChild(siblingNode, index), index) && DoubleOrderedMap.isBlack(DoubleOrderedMap.getRightChild(siblingNode, index), index)) {
                    DoubleOrderedMap.makeRed(siblingNode, index);
                    currentNode = DoubleOrderedMap.getParent(currentNode, index);
                    continue;
                }
                if (DoubleOrderedMap.isBlack(DoubleOrderedMap.getRightChild(siblingNode, index), index)) {
                    DoubleOrderedMap.makeBlack(DoubleOrderedMap.getLeftChild(siblingNode, index), index);
                    DoubleOrderedMap.makeRed(siblingNode, index);
                    this.rotateRight(siblingNode, index);
                    siblingNode = DoubleOrderedMap.getRightChild(DoubleOrderedMap.getParent(currentNode, index), index);
                }
                DoubleOrderedMap.copyColor(DoubleOrderedMap.getParent(currentNode, index), siblingNode, index);
                DoubleOrderedMap.makeBlack(DoubleOrderedMap.getParent(currentNode, index), index);
                DoubleOrderedMap.makeBlack(DoubleOrderedMap.getRightChild(siblingNode, index), index);
                this.rotateLeft(DoubleOrderedMap.getParent(currentNode, index), index);
                currentNode = this.rootNode[index];
                continue;
            }
            siblingNode = DoubleOrderedMap.getLeftChild(DoubleOrderedMap.getParent(currentNode, index), index);
            if (DoubleOrderedMap.isRed(siblingNode, index)) {
                DoubleOrderedMap.makeBlack(siblingNode, index);
                DoubleOrderedMap.makeRed(DoubleOrderedMap.getParent(currentNode, index), index);
                this.rotateRight(DoubleOrderedMap.getParent(currentNode, index), index);
                siblingNode = DoubleOrderedMap.getLeftChild(DoubleOrderedMap.getParent(currentNode, index), index);
            }
            if (DoubleOrderedMap.isBlack(DoubleOrderedMap.getRightChild(siblingNode, index), index) && DoubleOrderedMap.isBlack(DoubleOrderedMap.getLeftChild(siblingNode, index), index)) {
                DoubleOrderedMap.makeRed(siblingNode, index);
                currentNode = DoubleOrderedMap.getParent(currentNode, index);
                continue;
            }
            if (DoubleOrderedMap.isBlack(DoubleOrderedMap.getLeftChild(siblingNode, index), index)) {
                DoubleOrderedMap.makeBlack(DoubleOrderedMap.getRightChild(siblingNode, index), index);
                DoubleOrderedMap.makeRed(siblingNode, index);
                this.rotateLeft(siblingNode, index);
                siblingNode = DoubleOrderedMap.getLeftChild(DoubleOrderedMap.getParent(currentNode, index), index);
            }
            DoubleOrderedMap.copyColor(DoubleOrderedMap.getParent(currentNode, index), siblingNode, index);
            DoubleOrderedMap.makeBlack(DoubleOrderedMap.getParent(currentNode, index), index);
            DoubleOrderedMap.makeBlack(DoubleOrderedMap.getLeftChild(siblingNode, index), index);
            this.rotateRight(DoubleOrderedMap.getParent(currentNode, index), index);
            currentNode = this.rootNode[index];
        }
        DoubleOrderedMap.makeBlack(currentNode, index);
    }

    private void swapPosition(Node x, Node y, int index) {
        boolean yWasLeftChild;
        Node xFormerParent = x.getParent(index);
        Node xFormerLeftChild = x.getLeft(index);
        Node xFormerRightChild = x.getRight(index);
        Node yFormerParent = y.getParent(index);
        Node yFormerLeftChild = y.getLeft(index);
        Node yFormerRightChild = y.getRight(index);
        boolean xWasLeftChild = x.getParent(index) != null && x == x.getParent(index).getLeft(index);
        boolean bl = yWasLeftChild = y.getParent(index) != null && y == y.getParent(index).getLeft(index);
        if (x == yFormerParent) {
            x.setParent(y, index);
            if (yWasLeftChild) {
                y.setLeft(x, index);
                y.setRight(xFormerRightChild, index);
            } else {
                y.setRight(x, index);
                y.setLeft(xFormerLeftChild, index);
            }
        } else {
            x.setParent(yFormerParent, index);
            if (yFormerParent != null) {
                if (yWasLeftChild) {
                    yFormerParent.setLeft(x, index);
                } else {
                    yFormerParent.setRight(x, index);
                }
            }
            y.setLeft(xFormerLeftChild, index);
            y.setRight(xFormerRightChild, index);
        }
        if (y == xFormerParent) {
            y.setParent(x, index);
            if (xWasLeftChild) {
                x.setLeft(y, index);
                x.setRight(yFormerRightChild, index);
            } else {
                x.setRight(y, index);
                x.setLeft(yFormerLeftChild, index);
            }
        } else {
            y.setParent(xFormerParent, index);
            if (xFormerParent != null) {
                if (xWasLeftChild) {
                    xFormerParent.setLeft(y, index);
                } else {
                    xFormerParent.setRight(y, index);
                }
            }
            x.setLeft(yFormerLeftChild, index);
            x.setRight(yFormerRightChild, index);
        }
        if (x.getLeft(index) != null) {
            x.getLeft(index).setParent(x, index);
        }
        if (x.getRight(index) != null) {
            x.getRight(index).setParent(x, index);
        }
        if (y.getLeft(index) != null) {
            y.getLeft(index).setParent(y, index);
        }
        if (y.getRight(index) != null) {
            y.getRight(index).setParent(y, index);
        }
        x.swapColors(y, index);
        if (this.rootNode[index] == x) {
            this.rootNode[index] = y;
        } else if (this.rootNode[index] == y) {
            this.rootNode[index] = x;
        }
    }

    private static void checkNonNullComparable(Object o, int index) {
        if (o == null) {
            throw new NullPointerException(dataName[index] + " cannot be null");
        }
        if (!(o instanceof Comparable)) {
            throw new ClassCastException(dataName[index] + " must be Comparable");
        }
    }

    private static void checkKey(Object key) {
        DoubleOrderedMap.checkNonNullComparable(key, 0);
    }

    private static void checkValue(Object value) {
        DoubleOrderedMap.checkNonNullComparable(value, 1);
    }

    private static void checkKeyAndValue(Object key, Object value) {
        DoubleOrderedMap.checkKey(key);
        DoubleOrderedMap.checkValue(value);
    }

    private void modify() {
        ++this.modifications;
    }

    private void grow() {
        this.modify();
        ++this.nodeCount;
    }

    private void shrink() {
        this.modify();
        --this.nodeCount;
    }

    private void insertValue(Node newNode) throws IllegalArgumentException {
        block4: {
            Node node = this.rootNode[1];
            while (true) {
                int cmp;
                if ((cmp = DoubleOrderedMap.compare(newNode.getData(1), node.getData(1))) == 0) {
                    throw new IllegalArgumentException("Cannot store a duplicate value (\"" + newNode.getData(1) + "\") in this Map");
                }
                if (cmp < 0) {
                    if (node.getLeft(1) != null) {
                        node = node.getLeft(1);
                        continue;
                    }
                    node.setLeft(newNode, 1);
                    newNode.setParent(node, 1);
                    this.doRedBlackInsert(newNode, 1);
                    break block4;
                }
                if (node.getRight(1) == null) break;
                node = node.getRight(1);
            }
            node.setRight(newNode, 1);
            newNode.setParent(node, 1);
            this.doRedBlackInsert(newNode, 1);
        }
    }

    public int size() {
        return this.nodeCount;
    }

    public boolean containsKey(Object key) throws ClassCastException, NullPointerException {
        DoubleOrderedMap.checkKey(key);
        return this.lookup((Comparable)key, 0) != null;
    }

    public boolean containsValue(Object value) {
        DoubleOrderedMap.checkValue(value);
        return this.lookup((Comparable)value, 1) != null;
    }

    public Object get(Object key) throws ClassCastException, NullPointerException {
        return this.doGet((Comparable)key, 0);
    }

    public Object put(Object key, Object value) throws ClassCastException, NullPointerException, IllegalArgumentException {
        block6: {
            DoubleOrderedMap.checkKeyAndValue(key, value);
            Node node = this.rootNode[0];
            if (node == null) {
                Node root;
                this.rootNode[0] = root = new Node((Comparable)key, (Comparable)value);
                this.rootNode[1] = root;
                this.grow();
            } else {
                while (true) {
                    int cmp;
                    if ((cmp = DoubleOrderedMap.compare((Comparable)key, node.getData(0))) == 0) {
                        throw new IllegalArgumentException("Cannot store a duplicate key (\"" + key + "\") in this Map");
                    }
                    if (cmp < 0) {
                        if (node.getLeft(0) != null) {
                            node = node.getLeft(0);
                            continue;
                        }
                        Node newNode = new Node((Comparable)key, (Comparable)value);
                        this.insertValue(newNode);
                        node.setLeft(newNode, 0);
                        newNode.setParent(node, 0);
                        this.doRedBlackInsert(newNode, 0);
                        this.grow();
                        break block6;
                    }
                    if (node.getRight(0) == null) break;
                    node = node.getRight(0);
                }
                Node newNode = new Node((Comparable)key, (Comparable)value);
                this.insertValue(newNode);
                node.setRight(newNode, 0);
                newNode.setParent(node, 0);
                this.doRedBlackInsert(newNode, 0);
                this.grow();
            }
        }
        return null;
    }

    public Object remove(Object key) {
        return this.doRemove((Comparable)key, 0);
    }

    public void clear() {
        this.modify();
        this.nodeCount = 0;
        this.rootNode[0] = null;
        this.rootNode[1] = null;
    }

    public Set keySet() {
        if (this.setOfKeys[0] == null) {
            this.setOfKeys[0] = new AbstractSet(){

                public Iterator iterator() {
                    return new DoubleOrderedMapIterator(0){

                        protected Object doGetNext() {
                            return this.lastReturnedNode.getData(0);
                        }
                    };
                }

                public int size() {
                    return DoubleOrderedMap.this.size();
                }

                public boolean contains(Object o) {
                    return DoubleOrderedMap.this.containsKey(o);
                }

                public boolean remove(Object o) {
                    int oldNodeCount = DoubleOrderedMap.this.nodeCount;
                    DoubleOrderedMap.this.remove(o);
                    return DoubleOrderedMap.this.nodeCount != oldNodeCount;
                }

                public void clear() {
                    DoubleOrderedMap.this.clear();
                }

                static /* synthetic */ DoubleOrderedMap access$1900(4 x0) {
                    return x0.DoubleOrderedMap.this;
                }
            };
        }
        return this.setOfKeys[0];
    }

    public Collection values() {
        if (this.collectionOfValues[0] == null) {
            this.collectionOfValues[0] = new AbstractCollection(){

                public Iterator iterator() {
                    return new DoubleOrderedMapIterator(0){

                        protected Object doGetNext() {
                            return this.lastReturnedNode.getData(1);
                        }
                    };
                }

                public int size() {
                    return DoubleOrderedMap.this.size();
                }

                public boolean contains(Object o) {
                    return DoubleOrderedMap.this.containsValue(o);
                }

                public boolean remove(Object o) {
                    int oldNodeCount = DoubleOrderedMap.this.nodeCount;
                    DoubleOrderedMap.this.removeValue(o);
                    return DoubleOrderedMap.this.nodeCount != oldNodeCount;
                }

                public boolean removeAll(Collection c) {
                    boolean modified = false;
                    Iterator iter = c.iterator();
                    while (iter.hasNext()) {
                        if (DoubleOrderedMap.this.removeValue(iter.next()) == null) continue;
                        modified = true;
                    }
                    return modified;
                }

                public void clear() {
                    DoubleOrderedMap.this.clear();
                }

                static /* synthetic */ DoubleOrderedMap access$2000(5 x0) {
                    return x0.DoubleOrderedMap.this;
                }
            };
        }
        return this.collectionOfValues[0];
    }

    public Set entrySet() {
        if (this.setOfEntries[0] == null) {
            this.setOfEntries[0] = new AbstractSet(){

                public Iterator iterator() {
                    return new DoubleOrderedMapIterator(0){

                        protected Object doGetNext() {
                            return this.lastReturnedNode;
                        }
                    };
                }

                public boolean contains(Object o) {
                    if (!(o instanceof Map.Entry)) {
                        return false;
                    }
                    Map.Entry entry = (Map.Entry)o;
                    Object value = entry.getValue();
                    Node node = DoubleOrderedMap.this.lookup((Comparable)entry.getKey(), 0);
                    return node != null && node.getData(1).equals(value);
                }

                public boolean remove(Object o) {
                    if (!(o instanceof Map.Entry)) {
                        return false;
                    }
                    Map.Entry entry = (Map.Entry)o;
                    Object value = entry.getValue();
                    Node node = DoubleOrderedMap.this.lookup((Comparable)entry.getKey(), 0);
                    if (node != null && node.getData(1).equals(value)) {
                        DoubleOrderedMap.this.doRedBlackDelete(node);
                        return true;
                    }
                    return false;
                }

                public int size() {
                    return DoubleOrderedMap.this.size();
                }

                public void clear() {
                    DoubleOrderedMap.this.clear();
                }

                static /* synthetic */ DoubleOrderedMap access$2100(6 x0) {
                    return x0.DoubleOrderedMap.this;
                }
            };
        }
        return this.setOfEntries[0];
    }

    private static final class Node
    implements Map.Entry,
    KeyValue {
        private Comparable[] data;
        private Node[] leftNode;
        private Node[] rightNode;
        private Node[] parentNode;
        private boolean[] blackColor;
        private int hashcodeValue;
        private boolean calculatedHashCode;

        Node(Comparable key, Comparable value) {
            this.data = new Comparable[]{key, value};
            this.leftNode = new Node[]{null, null};
            this.rightNode = new Node[]{null, null};
            this.parentNode = new Node[]{null, null};
            this.blackColor = new boolean[]{true, true};
            this.calculatedHashCode = false;
        }

        private Comparable getData(int index) {
            return this.data[index];
        }

        private void setLeft(Node node, int index) {
            this.leftNode[index] = node;
        }

        private Node getLeft(int index) {
            return this.leftNode[index];
        }

        private void setRight(Node node, int index) {
            this.rightNode[index] = node;
        }

        private Node getRight(int index) {
            return this.rightNode[index];
        }

        private void setParent(Node node, int index) {
            this.parentNode[index] = node;
        }

        private Node getParent(int index) {
            return this.parentNode[index];
        }

        private void swapColors(Node node, int index) {
            int n = index;
            this.blackColor[n] = this.blackColor[n] ^ node.blackColor[index];
            int n2 = index;
            node.blackColor[n2] = node.blackColor[n2] ^ this.blackColor[index];
            int n3 = index;
            this.blackColor[n3] = this.blackColor[n3] ^ node.blackColor[index];
        }

        private boolean isBlack(int index) {
            return this.blackColor[index];
        }

        private boolean isRed(int index) {
            return !this.blackColor[index];
        }

        private void setBlack(int index) {
            this.blackColor[index] = true;
        }

        private void setRed(int index) {
            this.blackColor[index] = false;
        }

        private void copyColor(Node node, int index) {
            this.blackColor[index] = node.blackColor[index];
        }

        public Object getKey() {
            return this.data[0];
        }

        public Object getValue() {
            return this.data[1];
        }

        public Object setValue(Object ignored) throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Map.Entry.setValue is not supported");
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return this.data[0].equals(e.getKey()) && this.data[1].equals(e.getValue());
        }

        public int hashCode() {
            if (!this.calculatedHashCode) {
                this.hashcodeValue = this.data[0].hashCode() ^ this.data[1].hashCode();
                this.calculatedHashCode = true;
            }
            return this.hashcodeValue;
        }
    }

    private abstract class DoubleOrderedMapIterator
    implements Iterator {
        private int expectedModifications;
        protected Node lastReturnedNode;
        private Node nextNode;
        private int iteratorType;

        DoubleOrderedMapIterator(int type) {
            this.iteratorType = type;
            this.expectedModifications = DoubleOrderedMap.this.modifications;
            this.lastReturnedNode = null;
            this.nextNode = DoubleOrderedMap.leastNode(DoubleOrderedMap.this.rootNode[this.iteratorType], this.iteratorType);
        }

        protected abstract Object doGetNext();

        public final boolean hasNext() {
            return this.nextNode != null;
        }

        public final Object next() throws NoSuchElementException, ConcurrentModificationException {
            if (this.nextNode == null) {
                throw new NoSuchElementException();
            }
            if (DoubleOrderedMap.this.modifications != this.expectedModifications) {
                throw new ConcurrentModificationException();
            }
            this.lastReturnedNode = this.nextNode;
            this.nextNode = DoubleOrderedMap.this.nextGreater(this.nextNode, this.iteratorType);
            return this.doGetNext();
        }

        public final void remove() throws IllegalStateException, ConcurrentModificationException {
            if (this.lastReturnedNode == null) {
                throw new IllegalStateException();
            }
            if (DoubleOrderedMap.this.modifications != this.expectedModifications) {
                throw new ConcurrentModificationException();
            }
            DoubleOrderedMap.this.doRedBlackDelete(this.lastReturnedNode);
            ++this.expectedModifications;
            this.lastReturnedNode = null;
        }
    }
}

