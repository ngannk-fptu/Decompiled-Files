/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.abdera.model.Base;
import org.apache.abdera.xpath.XPath;
import org.apache.abdera.xpath.XPathException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AbstractXPath
implements XPath {
    private final Map<String, String> namespaces;

    protected AbstractXPath() {
        this(null);
    }

    protected AbstractXPath(Map<String, String> defaultNamespaces) {
        this.namespaces = defaultNamespaces != null ? defaultNamespaces : this.initDefaultNamespaces();
    }

    protected Map<String, String> initDefaultNamespaces() {
        HashMap<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("a", "http://www.w3.org/2005/Atom");
        namespaces.put("app", "http://www.w3.org/2007/app");
        namespaces.put("xhtml", "http://www.w3.org/1999/xhtml");
        return namespaces;
    }

    @Override
    public Map<String, String> getDefaultNamespaces() {
        return new HashMap<String, String>(this.namespaces);
    }

    @Override
    public List selectNodes(String path, Base base) throws XPathException {
        return this.selectNodes(path, base, this.getDefaultNamespaces());
    }

    @Override
    public Object selectSingleNode(String path, Base base) throws XPathException {
        return this.selectSingleNode(path, base, this.getDefaultNamespaces());
    }

    @Override
    public Object evaluate(String path, Base base) throws XPathException {
        return this.evaluate(path, base, this.getDefaultNamespaces());
    }

    @Override
    public String valueOf(String path, Base base) throws XPathException {
        return this.valueOf(path, base, this.getDefaultNamespaces());
    }

    @Override
    public boolean booleanValueOf(String path, Base base) throws XPathException {
        return this.booleanValueOf(path, base, this.getDefaultNamespaces());
    }

    @Override
    public Number numericValueOf(String path, Base base) throws XPathException {
        return this.numericValueOf(path, base, this.getDefaultNamespaces());
    }
}

