/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xpath;

import java.util.Map;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.impl.xpath.XPathCompilationContext;
import org.apache.xmlbeans.impl.xpath.XPathStep;

public class XPath {
    public static final String _NS_BOUNDARY = "$xmlbeans!ns_boundary";
    public static final String _DEFAULT_ELT_NS = "$xmlbeans!default_uri";
    final Selector _selector;
    private final boolean _sawDeepDot;

    public static XPath compileXPath(String xpath) throws XPathCompileException {
        return XPath.compileXPath(xpath, "$this", null);
    }

    public static XPath compileXPath(String xpath, String currentNodeVar) throws XPathCompileException {
        return XPath.compileXPath(xpath, currentNodeVar, null);
    }

    public static XPath compileXPath(String xpath, Map<String, String> namespaces) throws XPathCompileException {
        return XPath.compileXPath(xpath, "$this", namespaces);
    }

    public static XPath compileXPath(String xpath, String currentNodeVar, Map<String, String> namespaces) throws XPathCompileException {
        return new XPathCompilationContext(namespaces, currentNodeVar).compile(xpath);
    }

    XPath(Selector selector, boolean sawDeepDot) {
        this._selector = selector;
        this._sawDeepDot = sawDeepDot;
    }

    public boolean sawDeepDot() {
        return this._sawDeepDot;
    }

    static final class Selector {
        final XPathStep[] _paths;

        Selector(XPathStep[] paths) {
            this._paths = paths;
        }
    }

    public static class XPathCompileException
    extends XmlException {
        XPathCompileException(XmlError err) {
            super(err.toString(), null, err);
        }
    }
}

