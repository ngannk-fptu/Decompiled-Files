/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xpath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xpath.XPath;
import org.apache.xmlbeans.impl.xpath.XPathStep;

public class XPathExecutionContext {
    private XPath _xpath;
    private final ArrayList<QName> _stack = new ArrayList();
    private PathContext[] _paths;
    public static final int HIT = 1;
    public static final int DESCEND = 2;
    public static final int ATTRS = 4;

    public final void init(XPath xpath) {
        if (this._xpath != xpath) {
            this._xpath = xpath;
            this._paths = new PathContext[xpath._selector._paths.length];
            Arrays.setAll(this._paths, i -> new PathContext());
        }
        this._stack.clear();
        for (int i2 = 0; i2 < this._paths.length; ++i2) {
            this._paths[i2].init(xpath._selector._paths[i2]);
        }
    }

    public final int start() {
        int result = 0;
        for (PathContext path : this._paths) {
            result |= path.start();
        }
        return result;
    }

    public final int element(QName name) {
        assert (name != null);
        this._stack.add(name);
        int result = 0;
        for (PathContext path : this._paths) {
            result |= path.element(name);
        }
        return result;
    }

    public final boolean attr(QName name) {
        boolean hit = false;
        for (PathContext path : this._paths) {
            hit |= path.attr(name);
        }
        return hit;
    }

    public final void end() {
        this._stack.remove(this._stack.size() - 1);
        for (PathContext path : this._paths) {
            path.end();
        }
    }

    private final class PathContext {
        private XPathStep _curr;
        private final List<XPathStep> _prev = new ArrayList<XPathStep>();

        private PathContext() {
        }

        void init(XPathStep steps) {
            this._curr = steps;
            this._prev.clear();
        }

        private QName top(int i) {
            return (QName)XPathExecutionContext.this._stack.get(XPathExecutionContext.this._stack.size() - 1 - i);
        }

        private void backtrack() {
            assert (this._curr != null);
            if (this._curr._hasBacktrack) {
                this._curr = this._curr._backtrack;
                return;
            }
            assert (!this._curr._deep);
            this._curr = this._curr._prev;
            block0: while (!this._curr._deep) {
                int t = 0;
                XPathStep s = this._curr;
                while (!s._deep) {
                    if (s.match(this.top(t++))) {
                        s = s._prev;
                        continue;
                    }
                    this._curr = this._curr._prev;
                    continue block0;
                }
                break block0;
            }
        }

        int start() {
            assert (this._curr != null);
            assert (this._curr._prev == null);
            if (this._curr._name != null) {
                return this._curr._flags;
            }
            this._curr = null;
            return 1;
        }

        int element(QName name) {
            this._prev.add(this._curr);
            if (this._curr == null) {
                return 0;
            }
            assert (this._curr._name != null);
            if (!this._curr._attr && this._curr.match(name)) {
                this._curr = this._curr._next;
                if (this._curr._name != null) {
                    return this._curr._flags;
                }
                this.backtrack();
                return this._curr == null ? 1 : 1 | this._curr._flags;
            }
            do {
                this.backtrack();
                if (this._curr == null) {
                    return 0;
                }
                if (!this._curr.match(name)) continue;
                this._curr = this._curr._next;
                break;
            } while (!this._curr._deep);
            return this._curr._flags;
        }

        boolean attr(QName name) {
            return this._curr != null && this._curr._attr && this._curr.match(name);
        }

        void end() {
            this._curr = this._prev.remove(this._prev.size() - 1);
        }
    }
}

