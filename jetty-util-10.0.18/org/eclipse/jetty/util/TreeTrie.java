/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.jetty.util.AbstractTrie;

class TreeTrie<V>
extends AbstractTrie<V> {
    private static final int[] LOOKUP_INSENSITIVE = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 26, -1, 27, 30, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 28, 29, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1};
    private static final int[] LOOKUP_SENSITIVE = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 26, -1, 27, 30, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 28, 29, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1};
    private static final int INDEX = 32;
    private final int[] _lookup;
    private final Node<V> _root;

    public static <V> AbstractTrie<V> from(boolean caseSensitive, Map<String, V> contents) {
        TreeTrie<V> trie = new TreeTrie<V>(caseSensitive);
        if (contents != null && !trie.putAll(contents)) {
            return null;
        }
        return trie;
    }

    TreeTrie() {
        this(false);
    }

    TreeTrie(boolean caseSensitive) {
        super(caseSensitive);
        this._lookup = caseSensitive ? LOOKUP_SENSITIVE : LOOKUP_INSENSITIVE;
        this._root = new Node('\u0000');
    }

    @Override
    public void clear() {
        Arrays.fill(this._root._nextIndex, null);
        this._root._nextOther.clear();
        this._root._key = null;
        this._root._value = null;
    }

    @Override
    public boolean put(String s, V v) {
        Node<V> t = this._root;
        int limit = s.length();
        for (int k = 0; k < limit; ++k) {
            int index;
            char c = s.charAt(k);
            int n = index = c < '\u007f' ? this._lookup[c] : -1;
            if (index >= 0) {
                if (t._nextIndex[index] == null) {
                    t._nextIndex[index] = new Node(c);
                }
                t = t._nextIndex[index];
                continue;
            }
            Node n2 = null;
            int i = t._nextOther.size();
            while (i-- > 0) {
                n2 = t._nextOther.get(i);
                if (n2._c == c) break;
                n2 = null;
            }
            if (n2 == null) {
                n2 = new Node(c);
                t._nextOther.add(n2);
            }
            t = n2;
        }
        t._key = v == null ? null : s;
        t._value = v;
        return true;
    }

    @Override
    public V get(String s, int offset, int len) {
        Node<V> t = this._root;
        for (int i = 0; i < len; ++i) {
            int index;
            char c = s.charAt(offset + i);
            int n = index = c < '\u007f' ? this._lookup[c] : -1;
            if (index >= 0) {
                if (t._nextIndex[index] == null) {
                    return null;
                }
                t = t._nextIndex[index];
                continue;
            }
            Node n2 = null;
            int j = t._nextOther.size();
            while (j-- > 0) {
                n2 = t._nextOther.get(j);
                if (n2._c == c) break;
                n2 = null;
            }
            if (n2 == null) {
                return null;
            }
            t = n2;
        }
        return t._value;
    }

    @Override
    public V get(ByteBuffer b, int offset, int len) {
        Node<V> t = this._root;
        for (int i = 0; i < len; ++i) {
            int index;
            byte c = b.get(offset + i);
            int n = index = c >= 0 && c < 127 ? this._lookup[c] : -1;
            if (index >= 0) {
                if (t._nextIndex[index] == null) {
                    return null;
                }
                t = t._nextIndex[index];
                continue;
            }
            Node n2 = null;
            int j = t._nextOther.size();
            while (j-- > 0) {
                n2 = t._nextOther.get(j);
                if (n2._c == c) break;
                n2 = null;
            }
            if (n2 == null) {
                return null;
            }
            t = n2;
        }
        return t._value;
    }

    @Override
    public V getBest(byte[] b, int offset, int len) {
        return this.getBest(this._root, b, offset, len);
    }

    private V getBest(Node<V> node, byte[] b, int offset, int len) {
        for (int i = 0; i < len; ++i) {
            Node next;
            int index;
            byte c = b[offset + i];
            int n = index = c >= 0 && c < 127 ? this._lookup[c] : -1;
            if (index >= 0) {
                if (node._nextIndex[index] == null) break;
                next = node._nextIndex[index];
            } else {
                Node n2 = null;
                int j = node._nextOther.size();
                while (j-- > 0) {
                    n2 = node._nextOther.get(j);
                    if (n2._c == c) break;
                    n2 = null;
                }
                if (n2 == null) break;
                next = n2;
            }
            if (node._key != null) {
                Object best = this.getBest(next, b, offset + i + 1, len - i - 1);
                if (best == null) break;
                return best;
            }
            node = next;
        }
        return node._value;
    }

    @Override
    public boolean isEmpty() {
        return this.keySet().isEmpty();
    }

    @Override
    public int size() {
        return this.keySet().size();
    }

    @Override
    public V getBest(String s, int offset, int len) {
        return this.getBest(this._root, s, offset, len);
    }

    private V getBest(Node<V> node, String s, int offset, int len) {
        for (int i = 0; i < len; ++i) {
            Node next;
            int index;
            char c = s.charAt(offset + i);
            int n = index = c < '\u007f' ? this._lookup[c] : -1;
            if (index >= 0) {
                if (node._nextIndex[index] == null) break;
                next = node._nextIndex[index];
            } else {
                Node n2 = null;
                int j = node._nextOther.size();
                while (j-- > 0) {
                    n2 = node._nextOther.get(j);
                    if (n2._c == c) break;
                    n2 = null;
                }
                if (n2 == null) break;
                next = n2;
            }
            if (node._key != null) {
                Object best = this.getBest(next, s, offset + i + 1, len - i - 1);
                if (best == null) break;
                return best;
            }
            node = next;
        }
        return node._value;
    }

    @Override
    public V getBest(ByteBuffer b, int offset, int len) {
        if (b.hasArray()) {
            return this.getBest(b.array(), b.arrayOffset() + b.position() + offset, len);
        }
        return this.getBest(this._root, b, offset, len);
    }

    private V getBest(Node<V> node, ByteBuffer b, int offset, int len) {
        int pos = b.position() + offset;
        for (int i = 0; i < len; ++i) {
            Node next;
            byte c;
            int index;
            int n = index = (c = b.get(pos++)) >= 0 && c < 127 ? this._lookup[c] : -1;
            if (index >= 0) {
                if (node._nextIndex[index] == null) break;
                next = node._nextIndex[index];
            } else {
                Node n2 = null;
                int j = node._nextOther.size();
                while (j-- > 0) {
                    n2 = node._nextOther.get(j);
                    if (n2._c == c) break;
                    n2 = null;
                }
                if (n2 == null) break;
                next = n2;
            }
            if (node._key != null) {
                Object best = this.getBest(next, b, offset + i + 1, len - i - 1);
                if (best == null) break;
                return best;
            }
            node = next;
        }
        return node._value;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("TT@").append(Integer.toHexString(this.hashCode())).append('{');
        buf.append("ci=").append(this.isCaseInsensitive()).append(';');
        TreeTrie.toString(buf, this._root, "");
        buf.append('}');
        return buf.toString();
    }

    private static <V> void toString(Appendable out, Node<V> t, String separator) {
        block2: while (t != null) {
            if (t._value != null) {
                try {
                    out.append(separator);
                    separator = ",";
                    out.append(t._key);
                    out.append('=');
                    out.append(t._value.toString());
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            int i = 0;
            while (i < 32) {
                Node n;
                if ((n = t._nextIndex[i++]) == null) continue;
                if (i == 32 && t._nextOther.size() == 0) {
                    t = n;
                    continue block2;
                }
                TreeTrie.toString(out, n, separator);
            }
            i = t._nextOther.size();
            while (i-- > 0) {
                if (i == 0) {
                    t = t._nextOther.get(i);
                    continue block2;
                }
                TreeTrie.toString(out, t._nextOther.get(i), separator);
            }
            break block2;
        }
    }

    @Override
    public Set<String> keySet() {
        HashSet<String> keys = new HashSet<String>();
        TreeTrie.keySet(keys, this._root);
        return keys;
    }

    private static <V> void keySet(Set<String> set, Node<V> t) {
        if (t != null) {
            int i;
            if (t._key != null) {
                set.add(t._key);
            }
            for (i = 0; i < 32; ++i) {
                if (t._nextIndex[i] == null) continue;
                TreeTrie.keySet(set, t._nextIndex[i]);
            }
            i = t._nextOther.size();
            while (i-- > 0) {
                TreeTrie.keySet(set, t._nextOther.get(i));
            }
        }
    }

    private static class Node<V> {
        private final Node<V>[] _nextIndex;
        private final List<Node<V>> _nextOther = new ArrayList<Node<V>>();
        private final char _c;
        private String _key;
        private V _value;

        private Node(char c) {
            this._nextIndex = new Node[32];
            this._c = c;
        }
    }
}

