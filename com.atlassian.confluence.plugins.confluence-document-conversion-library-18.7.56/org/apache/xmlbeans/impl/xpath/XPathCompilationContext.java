/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xpath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.impl.common.XMLChar;
import org.apache.xmlbeans.impl.xpath.XPath;
import org.apache.xmlbeans.impl.xpath.XPathStep;

class XPathCompilationContext {
    private String _expr;
    private boolean _sawDeepDot;
    private boolean _lastDeepDot;
    private final String _currentNodeVar;
    protected final Map<String, String> _namespaces = new HashMap<String, String>();
    private final Map<String, String> _externalNamespaces;
    private int _offset;
    private int _line;
    private int _column;

    XPathCompilationContext(Map<String, String> namespaces, String currentNodeVar) {
        this._currentNodeVar = currentNodeVar == null ? "$this" : currentNodeVar;
        this._externalNamespaces = namespaces == null ? new HashMap() : namespaces;
    }

    XPath compile(String expr) throws XPath.XPathCompileException {
        this._offset = 0;
        this._line = 1;
        this._column = 1;
        this._expr = expr;
        return this.tokenizeXPath();
    }

    int currChar() {
        return this.currChar(0);
    }

    int currChar(int offset) {
        return this._offset + offset >= this._expr.length() ? -1 : (int)this._expr.charAt(this._offset + offset);
    }

    void advance() {
        if (this._offset < this._expr.length()) {
            char ch = this._expr.charAt(this._offset);
            ++this._offset;
            ++this._column;
            if (ch == '\r' || ch == '\n') {
                char nextCh;
                ++this._line;
                this._column = 1;
                if (this._offset + 1 < this._expr.length() && ((nextCh = this._expr.charAt(this._offset + 1)) == '\r' || nextCh == '\n') && ch != nextCh) {
                    ++this._offset;
                }
            }
        }
    }

    void advance(int count) {
        assert (count >= 0);
        while (count-- > 0) {
            this.advance();
        }
    }

    boolean isWhitespace() {
        return this.isWhitespace(0);
    }

    boolean isWhitespace(int offset) {
        int ch = this.currChar(offset);
        return ch == 32 || ch == 9 || ch == 10 || ch == 13;
    }

    boolean isNCNameStart() {
        return this.currChar() != -1 && XMLChar.isNCNameStart(this.currChar());
    }

    boolean isNCName() {
        return this.currChar() != -1 && XMLChar.isNCName(this.currChar());
    }

    boolean startsWith(String s, int offset) {
        if (this._offset + offset >= this._expr.length()) {
            return false;
        }
        return this._expr.startsWith(s, this._offset + offset);
    }

    private XPath.XPathCompileException newError(String msg) {
        XmlError err = XmlError.forLocation(msg, 0, null, this._line, this._column, this._offset);
        return new XPath.XPathCompileException(err);
    }

    String lookupPrefix(String prefix) throws XPath.XPathCompileException {
        if (this._namespaces.containsKey(prefix)) {
            return this._namespaces.get(prefix);
        }
        if (this._externalNamespaces.containsKey(prefix)) {
            return this._externalNamespaces.get(prefix);
        }
        switch (prefix != null ? prefix : "") {
            case "xml": {
                return "http://www.w3.org/XML/1998/namespace";
            }
            case "xs": {
                return "http://www.w3.org/2001/XMLSchema";
            }
            case "xsi": {
                return "http://www.w3.org/2001/XMLSchema-instance";
            }
            case "fn": {
                return "http://www.w3.org/2002/11/xquery-functions";
            }
            case "xdt": {
                return "http://www.w3.org/2003/11/xpath-datatypes";
            }
            case "local": {
                return "http://www.w3.org/2003/11/xquery-local-functions";
            }
        }
        throw this.newError("Undefined prefix: " + prefix);
    }

    private boolean parseWhitespace() {
        boolean sawSpace = false;
        while (this.isWhitespace()) {
            this.advance();
            sawSpace = true;
        }
        return sawSpace;
    }

    private boolean tokenize(String ... tokens) {
        int offset = 0;
        for (String s : tokens) {
            assert (s != null && !s.isEmpty());
            while (this.isWhitespace(offset)) {
                ++offset;
            }
            if (!this.startsWith(s, offset)) {
                return false;
            }
            offset += s.length();
        }
        this.advance(offset);
        return true;
    }

    private String tokenizeNCName() throws XPath.XPathCompileException {
        this.parseWhitespace();
        if (!this.isNCNameStart()) {
            throw this.newError("Expected non-colonized name");
        }
        StringBuilder sb = new StringBuilder();
        sb.append((char)this.currChar());
        this.advance();
        while (this.isNCName()) {
            sb.append((char)this.currChar());
            this.advance();
        }
        return sb.toString();
    }

    private QName getAnyQName() {
        return new QName("", "");
    }

    private QName tokenizeQName() throws XPath.XPathCompileException {
        if (this.tokenize("*")) {
            return this.getAnyQName();
        }
        String ncName = this.tokenizeNCName();
        if (!this.tokenize(":")) {
            return new QName(this.lookupPrefix(""), ncName);
        }
        return new QName(this.lookupPrefix(ncName), this.tokenize("*") ? "" : this.tokenizeNCName());
    }

    private String tokenizeQuotedUri() throws XPath.XPathCompileException {
        int quote;
        if (this.tokenize("\"")) {
            quote = 34;
        } else if (this.tokenize("'")) {
            quote = 39;
        } else {
            throw this.newError("Expected quote (\" or ')");
        }
        StringBuilder sb = new StringBuilder();
        while (true) {
            if (this.currChar() == -1) {
                throw this.newError("Path terminated in URI literal");
            }
            if (this.currChar() == quote) {
                this.advance();
                if (this.currChar() != quote) break;
            }
            sb.append((char)this.currChar());
            this.advance();
        }
        return sb.toString();
    }

    private XPathStep addStep(boolean deep, boolean attr, QName name, XPathStep steps) {
        XPathStep step = new XPathStep(deep, attr, name);
        if (steps == null) {
            return step;
        }
        XPathStep s = steps;
        while (steps._next != null) {
            steps = steps._next;
        }
        steps._next = step;
        step._prev = steps;
        return s;
    }

    private XPathStep tokenizeSteps() throws XPath.XPathCompileException {
        boolean deep;
        if (this.tokenize("/")) {
            throw this.newError("Absolute paths unsupported");
        }
        if (this.tokenize("$", this._currentNodeVar, "//") || this.tokenize(".", "//")) {
            deep = true;
        } else if (this.tokenize("$", this._currentNodeVar, "/") || this.tokenize(".", "/")) {
            deep = false;
        } else {
            if (this.tokenize("$", this._currentNodeVar) || this.tokenize(".")) {
                return this.addStep(false, false, null, null);
            }
            deep = false;
        }
        XPathStep steps = null;
        boolean deepDot = false;
        while (true) {
            if (this.tokenize("attribute", "::") || this.tokenize("@")) {
                steps = this.addStep(deep, true, this.tokenizeQName(), steps);
                break;
            }
            if (this.tokenize(".")) {
                deepDot = deepDot || deep;
            } else {
                this.tokenize("child", "::");
                QName name = this.tokenizeQName();
                steps = this.addStep(deep, false, name, steps);
                deep = false;
            }
            if (this.tokenize("//")) {
                deep = true;
                deepDot = false;
                continue;
            }
            if (!this.tokenize("/")) break;
            if (!deepDot) continue;
            deep = true;
        }
        this._lastDeepDot = deepDot;
        if (this._lastDeepDot) {
            this._lastDeepDot = true;
            steps = this.addStep(true, false, this.getAnyQName(), steps);
        }
        return this.addStep(false, false, null, steps);
    }

    private void computeBacktrack(XPathStep steps) {
        XPathStep s = steps;
        while (s != null) {
            XPathStep t = s._next;
            while (t != null && !t._deep) {
                t = t._next;
            }
            if (!s._deep) {
                XPathStep u = s;
                while (u != t) {
                    u._hasBacktrack = true;
                    u = u._next;
                }
            } else {
                int i;
                int n = 0;
                XPathStep u = s;
                while (u != t && u._name != null && !u.isWild() && !u._attr) {
                    ++n;
                    u = u._next;
                }
                QName[] pattern = new QName[n + 1];
                int[] kmp = new int[n + 1];
                XPathStep v = s;
                for (i = 0; i < n; ++i) {
                    pattern[i] = v._name;
                    v = v._next;
                }
                pattern[n] = this.getAnyQName();
                i = 0;
                kmp[0] = -1;
                int j = -1;
                while (i < n) {
                    while (j > -1 && !pattern[i].equals(pattern[j])) {
                        j = kmp[j];
                    }
                    int n2 = pattern[++i].equals(pattern[++j]) ? kmp[j] : j;
                    kmp[i] = n2;
                }
                i = 0;
                v = s;
                while (v != u) {
                    v._hasBacktrack = true;
                    v._backtrack = s;
                    for (j = kmp[i]; j > 0; --j) {
                        v._backtrack = v._backtrack._next;
                    }
                    ++i;
                    v = v._next;
                }
                v = s;
                if (n > 1) {
                    for (j = kmp[n - 1]; j > 0; --j) {
                        v = v._next;
                    }
                }
                if (u != t && u._attr) {
                    u._hasBacktrack = true;
                    u._backtrack = v;
                    u = u._next;
                }
                if (u != t && u._name == null) {
                    u._hasBacktrack = true;
                    u._backtrack = v;
                }
                assert (s._deep);
                s._hasBacktrack = true;
                s._backtrack = s;
            }
            s = t;
        }
    }

    private void tokenizePath(ArrayList<XPathStep> paths) throws XPath.XPathCompileException {
        this._lastDeepDot = false;
        XPathStep steps = this.tokenizeSteps();
        this.computeBacktrack(steps);
        paths.add(steps);
        if (this._lastDeepDot) {
            this._sawDeepDot = true;
            XPathStep s = null;
            XPathStep t = steps;
            while (t != null) {
                boolean attr = t._next != null && t._next._next == null || t._attr;
                s = this.addStep(t._deep, attr, t._name, s);
                t = t._next;
            }
            this.computeBacktrack(s);
            paths.add(s);
        }
    }

    private XPath.Selector tokenizeSelector() throws XPath.XPathCompileException {
        ArrayList<XPathStep> paths = new ArrayList<XPathStep>();
        this.tokenizePath(paths);
        while (this.tokenize("|")) {
            this.tokenizePath(paths);
        }
        return new XPath.Selector(paths.toArray(new XPathStep[0]));
    }

    private XPath tokenizeXPath() throws XPath.XPathCompileException {
        while (true) {
            if (this.tokenize("declare", "namespace")) {
                if (!this.parseWhitespace()) {
                    throw this.newError("Expected prefix after 'declare namespace'");
                }
                String prefix = this.tokenizeNCName();
                if (!this.tokenize("=")) {
                    throw this.newError("Expected '='");
                }
                String uri = this.tokenizeQuotedUri();
                if (this._namespaces.containsKey(prefix)) {
                    throw this.newError("Redefinition of namespace prefix: " + prefix);
                }
                this._namespaces.put(prefix, uri);
                if (this._externalNamespaces.containsKey(prefix)) {
                    throw this.newError("Redefinition of namespace prefix: " + prefix);
                }
                this._externalNamespaces.put(prefix, uri);
                if (!this.tokenize(";")) {
                    // empty if block
                }
                this._externalNamespaces.put("$xmlbeans!ns_boundary", Integer.toString(this._offset));
                continue;
            }
            if (!this.tokenize("declare", "default", "element", "namespace")) break;
            String uri = this.tokenizeQuotedUri();
            if (this._namespaces.containsKey("")) {
                throw this.newError("Redefinition of default element namespace");
            }
            this._namespaces.put("", uri);
            if (this._externalNamespaces.containsKey("$xmlbeans!default_uri")) {
                throw this.newError("Redefinition of default element namespace : ");
            }
            this._externalNamespaces.put("$xmlbeans!default_uri", uri);
            if (!this.tokenize(";")) {
                throw this.newError("Default Namespace declaration must end with ;");
            }
            this._externalNamespaces.put("$xmlbeans!ns_boundary", Integer.toString(this._offset));
        }
        if (!this._namespaces.containsKey("")) {
            this._namespaces.put("", "");
        }
        XPath.Selector selector = this.tokenizeSelector();
        this.parseWhitespace();
        if (this.currChar() != -1) {
            throw this.newError("Unexpected char '" + (char)this.currChar() + "'");
        }
        return new XPath(selector, this._sawDeepDot);
    }

    private void processNonXpathDecls() {
    }
}

