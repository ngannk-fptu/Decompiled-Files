/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.xpath;

import java.util.List;
import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.jdom2.xpath.XPathDiagnostic;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface XPathExpression<T>
extends Cloneable {
    public XPathExpression<T> clone();

    public String getExpression();

    public Namespace getNamespace(String var1);

    public Namespace[] getNamespaces();

    public Object setVariable(String var1, Namespace var2, Object var3);

    public Object setVariable(String var1, Object var2);

    public Object getVariable(String var1, Namespace var2);

    public Object getVariable(String var1);

    public Filter<T> getFilter();

    public List<T> evaluate(Object var1);

    public T evaluateFirst(Object var1);

    public XPathDiagnostic<T> diagnose(Object var1, boolean var2);
}

