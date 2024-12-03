/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.xpath;

import java.util.List;
import org.jdom2.xpath.XPathExpression;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface XPathDiagnostic<T> {
    public Object getContext();

    public XPathExpression<T> getXPathExpression();

    public List<T> getResult();

    public List<Object> getFilteredResults();

    public List<Object> getRawResults();

    public boolean isFirstOnly();
}

