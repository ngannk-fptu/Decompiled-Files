/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.util;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.jetty.util.AbstractTrie;

class ArrayTrie<V>
extends AbstractTrie<V> {
    public static int MAX_CAPACITY = 65535;
    private static final int ROW_SIZE = 48;
    private static final int BIG_ROW_INSENSITIVE = 22;
    private static final int BIG_ROW_SENSITIVE = 48;
    private static final int X = Integer.MIN_VALUE;
    private static final int[] LOOKUP_INSENSITIVE = new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, -1, -2, -3, -4, -5, -6, -7, -8, -9, -10, -11, 43, 44, 45, 46, 47, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 37, 38, 39, 40, 41, 42, -12, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, -13, -14, -15, -16, 36, -17, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, -18, -19, -20, -21, Integer.MIN_VALUE};
    private static final int[] LOOKUP_SENSITIVE = new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, -1, -2, -3, -4, -5, -6, -7, -8, -9, -10, -11, 43, 44, 45, 46, 47, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 37, 38, 39, 40, 41, 42, -12, -22, -23, -24, -25, -26, -27, -28, -29, -30, -31, -32, -33, -34, -35, -36, -37, -38, -39, -40, -41, -42, -43, -44, -45, -46, -47, -13, -14, -15, -16, 36, -17, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, -18, -19, -20, -21, Integer.MIN_VALUE};
    private final char[] _table;
    private final int[] _lookup;
    private final Node<V>[] _node;
    private final int _bigRowSize;
    private char _rows;

    public static <V> ArrayTrie<V> from(int capacity, boolean caseSensitive, Map<String, V> contents) {
        if (capacity < 0) {
            return null;
        }
        if (capacity > MAX_CAPACITY) {
            return null;
        }
        ArrayTrie<V> trie = new ArrayTrie<V>(caseSensitive, capacity);
        if (contents != null && !trie.putAll(contents)) {
            return null;
        }
        return trie;
    }

    ArrayTrie(int capacity) {
        this(false, capacity);
    }

    ArrayTrie(boolean caseSensitive, int capacity) {
        super(caseSensitive);
        int n = this._bigRowSize = caseSensitive ? 48 : 22;
        if (capacity > MAX_CAPACITY) {
            throw new IllegalArgumentException("Capacity " + capacity + " > " + MAX_CAPACITY);
        }
        this._lookup = !caseSensitive ? LOOKUP_INSENSITIVE : LOOKUP_SENSITIVE;
        this._table = new char[capacity * 48];
        this._node = new Node[capacity];
    }

    @Override
    public void clear() {
        this._rows = '\u0000';
        Arrays.fill(this._table, '\u0000');
        Arrays.fill(this._node, null);
    }

    @Override
    public boolean put(String key, V value) {
        int row = 0;
        int limit = key.length();
        for (int i = 0; i < limit; ++i) {
            char[] big;
            Node<V> node;
            int idx;
            int column;
            char c = key.charAt(i);
            int n = column = c > '\u007f' ? Integer.MIN_VALUE : this._lookup[c];
            if (column >= 0) {
                idx = row * 48 + column;
                if ((row = this._table[idx]) != 0) continue;
                if (this._rows == this._node.length - 1) {
                    return false;
                }
                this._table[idx] = this._rows = (char)(this._rows + '\u0001');
                row = this._rows;
                continue;
            }
            if (column != Integer.MIN_VALUE) {
                idx = -column;
                node = this._node[row];
                if (node == null) {
                    node = this._node[row] = new Node();
                }
                int n2 = row = (big = node._bigRow) == null || idx >= big.length ? 0 : big[idx];
                if (row != 0) continue;
                if (this._rows == this._node.length - 1) {
                    return false;
                }
                if (big == null) {
                    big = node._bigRow = new char[idx + 1];
                } else if (idx >= big.length) {
                    big = node._bigRow = Arrays.copyOf(big, idx + 1);
                }
                big[idx] = this._rows = (char)(this._rows + '\u0001');
                row = this._rows;
                continue;
            }
            int last = row;
            row = 0;
            node = this._node[last];
            if (node != null && (big = node._bigRow) != null) {
                for (int idx2 = this._bigRowSize; idx2 < big.length; idx2 += 2) {
                    if (big[idx2] != c) continue;
                    row = big[idx2 + 1];
                    break;
                }
            }
            if (row != 0) continue;
            if (this._rows == this._node.length - 1) {
                return false;
            }
            if (node == null) {
                node = this._node[last] = new Node();
            }
            big = (big = node._bigRow) == null ? (node._bigRow = new char[this._bigRowSize + 2]) : (node._bigRow = Arrays.copyOf(big, Math.max(big.length, this._bigRowSize) + 2));
            big[big.length - 2] = c;
            big[big.length - 1] = this._rows = (char)(this._rows + '\u0001');
            row = this._rows;
        }
        Node<V> node = this._node[row];
        if (node == null) {
            node = this._node[row] = new Node();
        }
        node._key = key;
        node._value = value;
        return true;
    }

    private int lookup(int row, char c) {
        char[] big;
        int column;
        if (c < '\u0080' && (column = this._lookup[c]) != Integer.MIN_VALUE) {
            if (column >= 0) {
                int idx = row * 48 + column;
                row = this._table[idx];
            } else {
                Node<V> node = this._node[row];
                char[] big2 = node == null ? null : this._node[row]._bigRow;
                int idx = -column;
                if (big2 == null || idx >= big2.length) {
                    return -1;
                }
                row = big2[idx];
            }
            return row == 0 ? -1 : row;
        }
        Node<V> node = this._node[row];
        char[] cArray = big = node == null ? null : node._bigRow;
        if (big != null) {
            for (int i = this._bigRowSize; i < big.length; i += 2) {
                if (big[i] != c) continue;
                return big[i + 1];
            }
        }
        return -1;
    }

    @Override
    public V get(String s, int offset, int len) {
        int row = 0;
        for (int i = 0; i < len; ++i) {
            char c = s.charAt(offset + i);
            if ((row = this.lookup(row, c)) >= 0) continue;
            return null;
        }
        Node<V> node = this._node[row];
        return node == null ? null : (V)node._value;
    }

    @Override
    public V get(ByteBuffer b, int offset, int len) {
        int row = 0;
        for (int i = 0; i < len; ++i) {
            byte c = b.get(offset + i);
            if ((row = this.lookup(row, (char)(c & 0xFF))) >= 0) continue;
            return null;
        }
        Node<V> node = this._node[row];
        return node == null ? null : (V)node._value;
    }

    @Override
    public V getBest(byte[] b, int offset, int len) {
        return this.getBest(0, b, offset, len);
    }

    @Override
    public V getBest(ByteBuffer b, int offset, int len) {
        if (b.hasArray()) {
            return this.getBest(0, b.array(), b.arrayOffset() + b.position() + offset, len);
        }
        return this.getBest(0, b, offset, len);
    }

    @Override
    public V getBest(String s, int offset, int len) {
        return this.getBest(0, s, offset, len);
    }

    private V getBest(int row, String s, int offset, int len) {
        char c;
        int next;
        int pos = offset;
        for (int i = 0; i < len && (next = this.lookup(row, c = s.charAt(pos++))) >= 0; ++i) {
            Node<V> node = this._node[row];
            if (node != null && node._key != null) {
                V best = this.getBest(next, s, offset + i + 1, len - i - 1);
                if (best != null) {
                    return best;
                }
                return node._value;
            }
            row = next;
        }
        Node<V> node = this._node[row];
        return node == null ? null : (V)node._value;
    }

    private V getBest(int row, byte[] b, int offset, int len) {
        byte c;
        int next;
        for (int i = 0; i < len && (next = this.lookup(row, (char)((c = b[offset + i]) & 0xFF))) >= 0; ++i) {
            Node<V> node = this._node[row];
            if (node != null && node._key != null) {
                V best = this.getBest(next, b, offset + i + 1, len - i - 1);
                if (best != null) {
                    return best;
                }
                return node._value;
            }
            row = next;
        }
        Node<V> node = this._node[row];
        return node == null ? null : (V)node._value;
    }

    private V getBest(int row, ByteBuffer b, int offset, int len) {
        Node<V> node;
        int pos = b.position() + offset;
        for (int i = 0; i < len; ++i) {
            byte c;
            int next;
            if (pos >= b.limit()) {
                return null;
            }
            if ((next = this.lookup(row, (char)((c = b.get(pos++)) & 0xFF))) < 0) break;
            Node<V> node2 = this._node[row];
            if (node2 != null && node2._key != null) {
                V best = this.getBest(next, b, offset + i + 1, len - i - 1);
                if (best != null) {
                    return best;
                }
                return node2._value;
            }
            row = next;
        }
        return (node = this._node[row]) == null ? null : (V)node._value;
    }

    public String toString() {
        return "AT@" + Integer.toHexString(this.hashCode()) + "{cs=" + this.isCaseSensitive() + ";c=" + this._table.length / 48 + ";" + Arrays.stream(this._node).filter(n -> n != null && n._key != null).map(Node::toString).collect(Collectors.joining(",")) + "}";
    }

    @Override
    public Set<String> keySet() {
        return Arrays.stream(this._node).filter(Objects::nonNull).map(n -> n._key).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    public int size() {
        return this.keySet().size();
    }

    @Override
    public boolean isEmpty() {
        return this.keySet().isEmpty();
    }

    public void dumpStdErr() {
        int i;
        int c;
        System.err.print("row:");
        block0: for (c = 0; c < 48; ++c) {
            for (i = 0; i < 127; ++i) {
                if (this._lookup[i] != c) continue;
                System.err.printf("  %s", Character.valueOf((char)i));
                continue block0;
            }
        }
        System.err.println();
        System.err.print("big:");
        block2: for (c = 0; c < this._bigRowSize; ++c) {
            for (i = 0; i < 127; ++i) {
                if (-this._lookup[i] != c) continue;
                System.err.printf("  %s", Character.valueOf((char)i));
                continue block2;
            }
        }
        System.err.println();
        for (int row = 0; row <= this._rows; ++row) {
            System.err.printf("%3x:", row);
            for (int c2 = 0; c2 < 48; ++c2) {
                char ch = this._table[row * 48 + c2];
                if (ch == '\u0000') {
                    System.err.print("  .");
                    continue;
                }
                System.err.printf("%3x", ch);
            }
            Node<V> node = this._node[row];
            if (node != null) {
                int c3;
                System.err.printf(" : %s%n", node);
                char[] bigRow = node._bigRow;
                if (bigRow == null) continue;
                System.err.print("   :");
                for (c3 = 0; c3 < Math.min(this._bigRowSize, bigRow.length); ++c3) {
                    char ch = bigRow[c3];
                    if (ch == '\u0000') {
                        System.err.print("  _");
                        continue;
                    }
                    System.err.printf("%3x", ch);
                }
                for (c3 = this._bigRowSize; c3 < bigRow.length; c3 += 2) {
                    System.err.printf(" %s>%x", Character.valueOf(bigRow[c3]), (int)bigRow[c3 + 1]);
                }
                System.err.println();
                continue;
            }
            System.err.println();
        }
        System.err.println();
    }

    private static class Node<V> {
        String _key;
        V _value;
        char[] _bigRow;

        private Node() {
        }

        public String toString() {
            return this._key + "=" + this._value;
        }
    }
}

