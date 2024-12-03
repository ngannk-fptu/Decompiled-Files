/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.bidimap;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.KeyValue;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.OrderedBidiMap;
import org.apache.commons.collections.OrderedIterator;
import org.apache.commons.collections.OrderedMapIterator;
import org.apache.commons.collections.iterators.EmptyOrderedMapIterator;
import org.apache.commons.collections.keyvalue.UnmodifiableMapEntry;

public class TreeBidiMap
implements OrderedBidiMap {
    private static final int KEY = 0;
    private static final int VALUE = 1;
    private static final int MAPENTRY = 2;
    private static final int INVERSEMAPENTRY = 3;
    private static final int SUM_OF_INDICES = 1;
    private static final int FIRST_INDEX = 0;
    private static final int NUMBER_OF_INDICES = 2;
    private static final String[] dataName = new String[]{"key", "value"};
    private Node[] rootNode = new Node[2];
    private int nodeCount = 0;
    private int modifications = 0;
    private Set keySet;
    private Set valuesSet;
    private Set entrySet;
    private Inverse inverse = null;

    public TreeBidiMap() {
    }

    public TreeBidiMap(Map map) {
        this.putAll(map);
    }

    public int size() {
        return this.nodeCount;
    }

    public boolean isEmpty() {
        return this.nodeCount == 0;
    }

    public boolean containsKey(Object key) {
        TreeBidiMap.checkKey(key);
        return this.lookup((Comparable)key, 0) != null;
    }

    public boolean containsValue(Object value) {
        TreeBidiMap.checkValue(value);
        return this.lookup((Comparable)value, 1) != null;
    }

    public Object get(Object key) {
        return this.doGet((Comparable)key, 0);
    }

    public Object put(Object key, Object value) {
        return this.doPut((Comparable)key, (Comparable)value, 0);
    }

    public void putAll(Map map) {
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = it.next();
            this.put(entry.getKey(), entry.getValue());
        }
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

    public Object getKey(Object value) {
        return this.doGet((Comparable)value, 1);
    }

    public Object removeValue(Object value) {
        return this.doRemove((Comparable)value, 1);
    }

    public Object firstKey() {
        if (this.nodeCount == 0) {
            throw new NoSuchElementException("Map is empty");
        }
        return TreeBidiMap.leastNode(this.rootNode[0], 0).getKey();
    }

    public Object lastKey() {
        if (this.nodeCount == 0) {
            throw new NoSuchElementException("Map is empty");
        }
        return TreeBidiMap.greatestNode(this.rootNode[0], 0).getKey();
    }

    public Object nextKey(Object key) {
        TreeBidiMap.checkKey(key);
        Node node = this.nextGreater(this.lookup((Comparable)key, 0), 0);
        return node == null ? null : node.getKey();
    }

    public Object previousKey(Object key) {
        TreeBidiMap.checkKey(key);
        Node node = this.nextSmaller(this.lookup((Comparable)key, 0), 0);
        return node == null ? null : node.getKey();
    }

    public Set keySet() {
        if (this.keySet == null) {
            this.keySet = new View(this, 0, 0);
        }
        return this.keySet;
    }

    public Collection values() {
        if (this.valuesSet == null) {
            this.valuesSet = new View(this, 0, 1);
        }
        return this.valuesSet;
    }

    public Set entrySet() {
        if (this.entrySet == null) {
            this.entrySet = new EntryView(this, 0, 2);
        }
        return this.entrySet;
    }

    public MapIterator mapIterator() {
        if (this.isEmpty()) {
            return EmptyOrderedMapIterator.INSTANCE;
        }
        return new ViewMapIterator(this, 0);
    }

    public OrderedMapIterator orderedMapIterator() {
        if (this.isEmpty()) {
            return EmptyOrderedMapIterator.INSTANCE;
        }
        return new ViewMapIterator(this, 0);
    }

    public BidiMap inverseBidiMap() {
        return this.inverseOrderedBidiMap();
    }

    public OrderedBidiMap inverseOrderedBidiMap() {
        if (this.inverse == null) {
            this.inverse = new Inverse(this);
        }
        return this.inverse;
    }

    public boolean equals(Object obj) {
        return this.doEquals(obj, 0);
    }

    public int hashCode() {
        return this.doHashCode(0);
    }

    public String toString() {
        return this.doToString(0);
    }

    private Object doGet(Comparable obj, int index) {
        TreeBidiMap.checkNonNullComparable(obj, index);
        Node node = this.lookup(obj, index);
        return node == null ? null : node.getData(TreeBidiMap.oppositeIndex(index));
    }

    private Object doPut(Comparable key, Comparable value, int index) {
        Object prev;
        block6: {
            TreeBidiMap.checkKeyAndValue(key, value);
            prev = index == 0 ? this.doGet(key, 0) : this.doGet(value, 1);
            this.doRemove(key, 0);
            this.doRemove(value, 1);
            Node node = this.rootNode[0];
            if (node == null) {
                Node root;
                this.rootNode[0] = root = new Node(key, value);
                this.rootNode[1] = root;
                this.grow();
            } else {
                while (true) {
                    int cmp;
                    if ((cmp = TreeBidiMap.compare(key, node.getData(0))) == 0) {
                        throw new IllegalArgumentException("Cannot store a duplicate key (\"" + key + "\") in this Map");
                    }
                    if (cmp < 0) {
                        if (node.getLeft(0) != null) {
                            node = node.getLeft(0);
                            continue;
                        }
                        Node newNode = new Node(key, value);
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
                Node newNode = new Node(key, value);
                this.insertValue(newNode);
                node.setRight(newNode, 0);
                newNode.setParent(node, 0);
                this.doRedBlackInsert(newNode, 0);
                this.grow();
            }
        }
        return prev;
    }

    private Object doRemove(Comparable o, int index) {
        Node node = this.lookup(o, index);
        Comparable rval = null;
        if (node != null) {
            rval = node.getData(TreeBidiMap.oppositeIndex(index));
            this.doRedBlackDelete(node);
        }
        return rval;
    }

    private Node lookup(Comparable data, int index) {
        Node rval = null;
        Node node = this.rootNode[index];
        while (node != null) {
            int cmp = TreeBidiMap.compare(data, node.getData(index));
            if (cmp == 0) {
                rval = node;
                break;
            }
            node = cmp < 0 ? node.getLeft(index) : node.getRight(index);
        }
        return rval;
    }

    private Node nextGreater(Node node, int index) {
        Node rval = null;
        if (node == null) {
            rval = null;
        } else if (node.getRight(index) != null) {
            rval = TreeBidiMap.leastNode(node.getRight(index), index);
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

    private Node nextSmaller(Node node, int index) {
        Node rval = null;
        if (node == null) {
            rval = null;
        } else if (node.getLeft(index) != null) {
            rval = TreeBidiMap.greatestNode(node.getLeft(index), index);
        } else {
            Node parent = node.getParent(index);
            Node child = node;
            while (parent != null && child == parent.getLeft(index)) {
                child = parent;
                parent = parent.getParent(index);
            }
            rval = parent;
        }
        return rval;
    }

    private static int oppositeIndex(int index) {
        return 1 - index;
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

    private static Node greatestNode(Node node, int index) {
        Node rval = node;
        if (rval != null) {
            while (rval.getRight(index) != null) {
                rval = rval.getRight(index);
            }
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
        return TreeBidiMap.getParent(TreeBidiMap.getParent(node, index), index);
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
        TreeBidiMap.makeRed(currentNode, index);
        while (currentNode != null && currentNode != this.rootNode[index] && TreeBidiMap.isRed(currentNode.getParent(index), index)) {
            Node y;
            if (TreeBidiMap.isLeftChild(TreeBidiMap.getParent(currentNode, index), index)) {
                y = TreeBidiMap.getRightChild(TreeBidiMap.getGrandParent(currentNode, index), index);
                if (TreeBidiMap.isRed(y, index)) {
                    TreeBidiMap.makeBlack(TreeBidiMap.getParent(currentNode, index), index);
                    TreeBidiMap.makeBlack(y, index);
                    TreeBidiMap.makeRed(TreeBidiMap.getGrandParent(currentNode, index), index);
                    currentNode = TreeBidiMap.getGrandParent(currentNode, index);
                    continue;
                }
                if (TreeBidiMap.isRightChild(currentNode, index)) {
                    currentNode = TreeBidiMap.getParent(currentNode, index);
                    this.rotateLeft(currentNode, index);
                }
                TreeBidiMap.makeBlack(TreeBidiMap.getParent(currentNode, index), index);
                TreeBidiMap.makeRed(TreeBidiMap.getGrandParent(currentNode, index), index);
                if (TreeBidiMap.getGrandParent(currentNode, index) == null) continue;
                this.rotateRight(TreeBidiMap.getGrandParent(currentNode, index), index);
                continue;
            }
            y = TreeBidiMap.getLeftChild(TreeBidiMap.getGrandParent(currentNode, index), index);
            if (TreeBidiMap.isRed(y, index)) {
                TreeBidiMap.makeBlack(TreeBidiMap.getParent(currentNode, index), index);
                TreeBidiMap.makeBlack(y, index);
                TreeBidiMap.makeRed(TreeBidiMap.getGrandParent(currentNode, index), index);
                currentNode = TreeBidiMap.getGrandParent(currentNode, index);
                continue;
            }
            if (TreeBidiMap.isLeftChild(currentNode, index)) {
                currentNode = TreeBidiMap.getParent(currentNode, index);
                this.rotateRight(currentNode, index);
            }
            TreeBidiMap.makeBlack(TreeBidiMap.getParent(currentNode, index), index);
            TreeBidiMap.makeRed(TreeBidiMap.getGrandParent(currentNode, index), index);
            if (TreeBidiMap.getGrandParent(currentNode, index) == null) continue;
            this.rotateLeft(TreeBidiMap.getGrandParent(currentNode, index), index);
        }
        TreeBidiMap.makeBlack(this.rootNode[index], index);
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
                if (!TreeBidiMap.isBlack(deletedNode, index)) continue;
                this.doRedBlackDeleteFixup(replacement, index);
                continue;
            }
            if (deletedNode.getParent(index) == null) {
                this.rootNode[index] = null;
                continue;
            }
            if (TreeBidiMap.isBlack(deletedNode, index)) {
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
        while (currentNode != this.rootNode[index] && TreeBidiMap.isBlack(currentNode, index)) {
            Node siblingNode;
            if (TreeBidiMap.isLeftChild(currentNode, index)) {
                siblingNode = TreeBidiMap.getRightChild(TreeBidiMap.getParent(currentNode, index), index);
                if (TreeBidiMap.isRed(siblingNode, index)) {
                    TreeBidiMap.makeBlack(siblingNode, index);
                    TreeBidiMap.makeRed(TreeBidiMap.getParent(currentNode, index), index);
                    this.rotateLeft(TreeBidiMap.getParent(currentNode, index), index);
                    siblingNode = TreeBidiMap.getRightChild(TreeBidiMap.getParent(currentNode, index), index);
                }
                if (TreeBidiMap.isBlack(TreeBidiMap.getLeftChild(siblingNode, index), index) && TreeBidiMap.isBlack(TreeBidiMap.getRightChild(siblingNode, index), index)) {
                    TreeBidiMap.makeRed(siblingNode, index);
                    currentNode = TreeBidiMap.getParent(currentNode, index);
                    continue;
                }
                if (TreeBidiMap.isBlack(TreeBidiMap.getRightChild(siblingNode, index), index)) {
                    TreeBidiMap.makeBlack(TreeBidiMap.getLeftChild(siblingNode, index), index);
                    TreeBidiMap.makeRed(siblingNode, index);
                    this.rotateRight(siblingNode, index);
                    siblingNode = TreeBidiMap.getRightChild(TreeBidiMap.getParent(currentNode, index), index);
                }
                TreeBidiMap.copyColor(TreeBidiMap.getParent(currentNode, index), siblingNode, index);
                TreeBidiMap.makeBlack(TreeBidiMap.getParent(currentNode, index), index);
                TreeBidiMap.makeBlack(TreeBidiMap.getRightChild(siblingNode, index), index);
                this.rotateLeft(TreeBidiMap.getParent(currentNode, index), index);
                currentNode = this.rootNode[index];
                continue;
            }
            siblingNode = TreeBidiMap.getLeftChild(TreeBidiMap.getParent(currentNode, index), index);
            if (TreeBidiMap.isRed(siblingNode, index)) {
                TreeBidiMap.makeBlack(siblingNode, index);
                TreeBidiMap.makeRed(TreeBidiMap.getParent(currentNode, index), index);
                this.rotateRight(TreeBidiMap.getParent(currentNode, index), index);
                siblingNode = TreeBidiMap.getLeftChild(TreeBidiMap.getParent(currentNode, index), index);
            }
            if (TreeBidiMap.isBlack(TreeBidiMap.getRightChild(siblingNode, index), index) && TreeBidiMap.isBlack(TreeBidiMap.getLeftChild(siblingNode, index), index)) {
                TreeBidiMap.makeRed(siblingNode, index);
                currentNode = TreeBidiMap.getParent(currentNode, index);
                continue;
            }
            if (TreeBidiMap.isBlack(TreeBidiMap.getLeftChild(siblingNode, index), index)) {
                TreeBidiMap.makeBlack(TreeBidiMap.getRightChild(siblingNode, index), index);
                TreeBidiMap.makeRed(siblingNode, index);
                this.rotateLeft(siblingNode, index);
                siblingNode = TreeBidiMap.getLeftChild(TreeBidiMap.getParent(currentNode, index), index);
            }
            TreeBidiMap.copyColor(TreeBidiMap.getParent(currentNode, index), siblingNode, index);
            TreeBidiMap.makeBlack(TreeBidiMap.getParent(currentNode, index), index);
            TreeBidiMap.makeBlack(TreeBidiMap.getLeftChild(siblingNode, index), index);
            this.rotateRight(TreeBidiMap.getParent(currentNode, index), index);
            currentNode = this.rootNode[index];
        }
        TreeBidiMap.makeBlack(currentNode, index);
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
        TreeBidiMap.checkNonNullComparable(key, 0);
    }

    private static void checkValue(Object value) {
        TreeBidiMap.checkNonNullComparable(value, 1);
    }

    private static void checkKeyAndValue(Object key, Object value) {
        TreeBidiMap.checkKey(key);
        TreeBidiMap.checkValue(value);
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
                if ((cmp = TreeBidiMap.compare(newNode.getData(1), node.getData(1))) == 0) {
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

    private boolean doEquals(Object obj, int type) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Map)) {
            return false;
        }
        Map other = (Map)obj;
        if (other.size() != this.size()) {
            return false;
        }
        if (this.nodeCount > 0) {
            try {
                ViewMapIterator it = new ViewMapIterator(this, type);
                while (it.hasNext()) {
                    Object key = it.next();
                    Object value = it.getValue();
                    if (value.equals(other.get(key))) continue;
                    return false;
                }
            }
            catch (ClassCastException ex) {
                return false;
            }
            catch (NullPointerException ex) {
                return false;
            }
        }
        return true;
    }

    private int doHashCode(int type) {
        int total = 0;
        if (this.nodeCount > 0) {
            ViewMapIterator it = new ViewMapIterator(this, type);
            while (it.hasNext()) {
                Object key = it.next();
                Object value = it.getValue();
                total += key.hashCode() ^ value.hashCode();
            }
        }
        return total;
    }

    private String doToString(int type) {
        if (this.nodeCount == 0) {
            return "{}";
        }
        StringBuffer buf = new StringBuffer(this.nodeCount * 32);
        buf.append('{');
        ViewMapIterator it = new ViewMapIterator(this, type);
        boolean hasNext = it.hasNext();
        while (hasNext) {
            Object key = it.next();
            Object value = it.getValue();
            buf.append(key == this ? "(this Map)" : key).append('=').append(value == this ? "(this Map)" : value);
            hasNext = it.hasNext();
            if (!hasNext) continue;
            buf.append(", ");
        }
        buf.append('}');
        return buf.toString();
    }

    static class Inverse
    implements OrderedBidiMap {
        private final TreeBidiMap main;
        private Set keySet;
        private Set valuesSet;
        private Set entrySet;

        Inverse(TreeBidiMap main) {
            this.main = main;
        }

        public int size() {
            return this.main.size();
        }

        public boolean isEmpty() {
            return this.main.isEmpty();
        }

        public Object get(Object key) {
            return this.main.getKey(key);
        }

        public Object getKey(Object value) {
            return this.main.get(value);
        }

        public boolean containsKey(Object key) {
            return this.main.containsValue(key);
        }

        public boolean containsValue(Object value) {
            return this.main.containsKey(value);
        }

        public Object firstKey() {
            if (this.main.nodeCount == 0) {
                throw new NoSuchElementException("Map is empty");
            }
            return TreeBidiMap.leastNode(this.main.rootNode[1], 1).getValue();
        }

        public Object lastKey() {
            if (this.main.nodeCount == 0) {
                throw new NoSuchElementException("Map is empty");
            }
            return TreeBidiMap.greatestNode(this.main.rootNode[1], 1).getValue();
        }

        public Object nextKey(Object key) {
            TreeBidiMap.checkKey(key);
            Node node = this.main.nextGreater(this.main.lookup((Comparable)key, 1), 1);
            return node == null ? null : node.getValue();
        }

        public Object previousKey(Object key) {
            TreeBidiMap.checkKey(key);
            Node node = this.main.nextSmaller(this.main.lookup((Comparable)key, 1), 1);
            return node == null ? null : node.getValue();
        }

        public Object put(Object key, Object value) {
            return this.main.doPut((Comparable)value, (Comparable)key, 1);
        }

        public void putAll(Map map) {
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = it.next();
                this.put(entry.getKey(), entry.getValue());
            }
        }

        public Object remove(Object key) {
            return this.main.removeValue(key);
        }

        public Object removeValue(Object value) {
            return this.main.remove(value);
        }

        public void clear() {
            this.main.clear();
        }

        public Set keySet() {
            if (this.keySet == null) {
                this.keySet = new View(this.main, 1, 1);
            }
            return this.keySet;
        }

        public Collection values() {
            if (this.valuesSet == null) {
                this.valuesSet = new View(this.main, 1, 0);
            }
            return this.valuesSet;
        }

        public Set entrySet() {
            if (this.entrySet == null) {
                return new EntryView(this.main, 1, 3);
            }
            return this.entrySet;
        }

        public MapIterator mapIterator() {
            if (this.isEmpty()) {
                return EmptyOrderedMapIterator.INSTANCE;
            }
            return new ViewMapIterator(this.main, 1);
        }

        public OrderedMapIterator orderedMapIterator() {
            if (this.isEmpty()) {
                return EmptyOrderedMapIterator.INSTANCE;
            }
            return new ViewMapIterator(this.main, 1);
        }

        public BidiMap inverseBidiMap() {
            return this.main;
        }

        public OrderedBidiMap inverseOrderedBidiMap() {
            return this.main;
        }

        public boolean equals(Object obj) {
            return this.main.doEquals(obj, 1);
        }

        public int hashCode() {
            return this.main.doHashCode(1);
        }

        public String toString() {
            return this.main.doToString(1);
        }
    }

    static class Node
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
            this.leftNode = new Node[2];
            this.rightNode = new Node[2];
            this.parentNode = new Node[2];
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

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)obj;
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

    static class EntryView
    extends View {
        private final int oppositeType;

        EntryView(TreeBidiMap main, int orderType, int dataType) {
            super(main, orderType, dataType);
            this.oppositeType = TreeBidiMap.oppositeIndex(orderType);
        }

        public boolean contains(Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry entry = (Map.Entry)obj;
            Object value = entry.getValue();
            Node node = this.main.lookup((Comparable)entry.getKey(), this.orderType);
            return node != null && node.getData(this.oppositeType).equals(value);
        }

        public boolean remove(Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry entry = (Map.Entry)obj;
            Object value = entry.getValue();
            Node node = this.main.lookup((Comparable)entry.getKey(), this.orderType);
            if (node != null && node.getData(this.oppositeType).equals(value)) {
                this.main.doRedBlackDelete(node);
                return true;
            }
            return false;
        }
    }

    static class ViewMapIterator
    extends ViewIterator
    implements OrderedMapIterator {
        private final int oppositeType;

        ViewMapIterator(TreeBidiMap main, int orderType) {
            super(main, orderType, orderType);
            this.oppositeType = TreeBidiMap.oppositeIndex(this.dataType);
        }

        public Object getKey() {
            if (this.lastReturnedNode == null) {
                throw new IllegalStateException("Iterator getKey() can only be called after next() and before remove()");
            }
            return this.lastReturnedNode.getData(this.dataType);
        }

        public Object getValue() {
            if (this.lastReturnedNode == null) {
                throw new IllegalStateException("Iterator getValue() can only be called after next() and before remove()");
            }
            return this.lastReturnedNode.getData(this.oppositeType);
        }

        public Object setValue(Object obj) {
            throw new UnsupportedOperationException();
        }
    }

    static class ViewIterator
    implements OrderedIterator {
        protected final TreeBidiMap main;
        protected final int orderType;
        protected final int dataType;
        protected Node lastReturnedNode;
        protected Node nextNode;
        protected Node previousNode;
        private int expectedModifications;

        ViewIterator(TreeBidiMap main, int orderType, int dataType) {
            this.main = main;
            this.orderType = orderType;
            this.dataType = dataType;
            this.expectedModifications = main.modifications;
            this.nextNode = TreeBidiMap.leastNode(main.rootNode[orderType], orderType);
            this.lastReturnedNode = null;
            this.previousNode = null;
        }

        public final boolean hasNext() {
            return this.nextNode != null;
        }

        public final Object next() {
            if (this.nextNode == null) {
                throw new NoSuchElementException();
            }
            if (this.main.modifications != this.expectedModifications) {
                throw new ConcurrentModificationException();
            }
            this.lastReturnedNode = this.nextNode;
            this.previousNode = this.nextNode;
            this.nextNode = this.main.nextGreater(this.nextNode, this.orderType);
            return this.doGetData();
        }

        public boolean hasPrevious() {
            return this.previousNode != null;
        }

        public Object previous() {
            if (this.previousNode == null) {
                throw new NoSuchElementException();
            }
            if (this.main.modifications != this.expectedModifications) {
                throw new ConcurrentModificationException();
            }
            this.nextNode = this.lastReturnedNode;
            if (this.nextNode == null) {
                this.nextNode = this.main.nextGreater(this.previousNode, this.orderType);
            }
            this.lastReturnedNode = this.previousNode;
            this.previousNode = this.main.nextSmaller(this.previousNode, this.orderType);
            return this.doGetData();
        }

        protected Object doGetData() {
            switch (this.dataType) {
                case 0: {
                    return this.lastReturnedNode.getKey();
                }
                case 1: {
                    return this.lastReturnedNode.getValue();
                }
                case 2: {
                    return this.lastReturnedNode;
                }
                case 3: {
                    return new UnmodifiableMapEntry(this.lastReturnedNode.getValue(), this.lastReturnedNode.getKey());
                }
            }
            return null;
        }

        public final void remove() {
            if (this.lastReturnedNode == null) {
                throw new IllegalStateException();
            }
            if (this.main.modifications != this.expectedModifications) {
                throw new ConcurrentModificationException();
            }
            this.main.doRedBlackDelete(this.lastReturnedNode);
            ++this.expectedModifications;
            this.lastReturnedNode = null;
            this.previousNode = this.nextNode == null ? TreeBidiMap.greatestNode(this.main.rootNode[this.orderType], this.orderType) : this.main.nextSmaller(this.nextNode, this.orderType);
        }
    }

    static class View
    extends AbstractSet {
        protected final TreeBidiMap main;
        protected final int orderType;
        protected final int dataType;

        View(TreeBidiMap main, int orderType, int dataType) {
            this.main = main;
            this.orderType = orderType;
            this.dataType = dataType;
        }

        public Iterator iterator() {
            return new ViewIterator(this.main, this.orderType, this.dataType);
        }

        public int size() {
            return this.main.size();
        }

        public boolean contains(Object obj) {
            TreeBidiMap.checkNonNullComparable(obj, this.dataType);
            return this.main.lookup((Comparable)obj, this.dataType) != null;
        }

        public boolean remove(Object obj) {
            return this.main.doRemove((Comparable)obj, this.dataType) != null;
        }

        public void clear() {
            this.main.clear();
        }
    }
}

