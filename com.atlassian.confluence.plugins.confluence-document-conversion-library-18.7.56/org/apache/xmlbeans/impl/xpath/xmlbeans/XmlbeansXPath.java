/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xpath.xmlbeans;

import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.store.Cur;
import org.apache.xmlbeans.impl.xpath.Path;
import org.apache.xmlbeans.impl.xpath.XPath;
import org.apache.xmlbeans.impl.xpath.XPathEngine;
import org.apache.xmlbeans.impl.xpath.XPathFactory;
import org.apache.xmlbeans.impl.xpath.xmlbeans.XmlbeansXPathEngine;

public class XmlbeansXPath
implements Path {
    private final String _pathKey;
    private final String _currentVar;
    private final XPath _compiledPath;

    public XmlbeansXPath(String pathExpr, String currentVar, XPath xpath) {
        this._pathKey = pathExpr;
        this._currentVar = currentVar;
        this._compiledPath = xpath;
    }

    @Override
    public XPathEngine execute(Cur c, XmlOptions options) {
        options = XmlOptions.maskNull(options);
        if (!c.isContainer() || this._compiledPath.sawDeepDot()) {
            Path xpe = XPathFactory.getCompiledPathSaxon(this._pathKey, this._currentVar, null);
            return xpe.execute(c, options);
        }
        return new XmlbeansXPathEngine(this._compiledPath, c);
    }
}

