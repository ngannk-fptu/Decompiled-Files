/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.xpath;

import java.util.List;
import java.util.Map;
import org.apache.abdera.model.Base;
import org.apache.abdera.xpath.XPathException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface XPath {
    public Map<String, String> getDefaultNamespaces();

    public List selectNodes(String var1, Base var2) throws XPathException;

    public Object selectSingleNode(String var1, Base var2) throws XPathException;

    public Object evaluate(String var1, Base var2) throws XPathException;

    public String valueOf(String var1, Base var2) throws XPathException;

    public boolean booleanValueOf(String var1, Base var2) throws XPathException;

    public Number numericValueOf(String var1, Base var2) throws XPathException;

    public List selectNodes(String var1, Base var2, Map<String, String> var3) throws XPathException;

    public Object selectSingleNode(String var1, Base var2, Map<String, String> var3) throws XPathException;

    public Object evaluate(String var1, Base var2, Map<String, String> var3) throws XPathException;

    public String valueOf(String var1, Base var2, Map<String, String> var3) throws XPathException;

    public boolean booleanValueOf(String var1, Base var2, Map<String, String> var3) throws XPathException;

    public Number numericValueOf(String var1, Base var2, Map<String, String> var3) throws XPathException;
}

