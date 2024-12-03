/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.xpath.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jdom2.Namespace;
import org.jdom2.Verifier;
import org.jdom2.filter.Filter;
import org.jdom2.xpath.XPathDiagnostic;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.util.XPathDiagnosticImpl;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AbstractXPathCompiled<T>
implements XPathExpression<T> {
    private static final NamespaceComparator NSSORT = new NamespaceComparator();
    private final Map<String, Namespace> xnamespaces = new HashMap<String, Namespace>();
    private Map<String, Map<String, Object>> xvariables = new HashMap<String, Map<String, Object>>();
    private final String xquery;
    private final Filter<T> xfilter;

    private static final String getPrefixForURI(String uri, Namespace[] nsa) {
        for (Namespace ns : nsa) {
            if (!ns.getURI().equals(uri)) continue;
            return ns.getPrefix();
        }
        throw new IllegalStateException("No namespace defined with URI " + uri);
    }

    public AbstractXPathCompiled(String query, Filter<T> filter, Map<String, Object> variables, Namespace[] namespaces) {
        if (query == null) {
            throw new NullPointerException("Null query");
        }
        if (filter == null) {
            throw new NullPointerException("Null filter");
        }
        this.xnamespaces.put(Namespace.NO_NAMESPACE.getPrefix(), Namespace.NO_NAMESPACE);
        if (namespaces != null) {
            for (Namespace ns : namespaces) {
                if (ns == null) {
                    throw new NullPointerException("Null namespace");
                }
                Namespace oldns = this.xnamespaces.put(ns.getPrefix(), ns);
                if (oldns == null || oldns == ns) continue;
                if (oldns == Namespace.NO_NAMESPACE) {
                    throw new IllegalArgumentException("The default (no prefix) Namespace URI for XPath queries is always '' and it cannot be redefined to '" + ns.getURI() + "'.");
                }
                throw new IllegalArgumentException("A Namespace with the prefix '" + ns.getPrefix() + "' has already been declared.");
            }
        }
        if (variables != null) {
            for (Map.Entry entry : variables.entrySet()) {
                String qname = (String)entry.getKey();
                if (qname == null) {
                    throw new NullPointerException("Variable with a null name");
                }
                int p = qname.indexOf(58);
                String pfx = p < 0 ? "" : qname.substring(0, p);
                String lname = p < 0 ? qname : qname.substring(p + 1);
                String vpfxmsg = Verifier.checkNamespacePrefix(pfx);
                if (vpfxmsg != null) {
                    throw new IllegalArgumentException("Prefix '" + pfx + "' for variable " + qname + " is illegal: " + vpfxmsg);
                }
                String vnamemsg = Verifier.checkXMLName(lname);
                if (vnamemsg != null) {
                    throw new IllegalArgumentException("Variable name '" + lname + "' for variable " + qname + " is illegal: " + vnamemsg);
                }
                Namespace ns = this.xnamespaces.get(pfx);
                if (ns == null) {
                    throw new IllegalArgumentException("Prefix '" + pfx + "' for variable " + qname + " has not been assigned a Namespace.");
                }
                Map<String, Object> vmap = this.xvariables.get(ns.getURI());
                if (vmap == null) {
                    vmap = new HashMap<String, Object>();
                    this.xvariables.put(ns.getURI(), vmap);
                }
                if (vmap.put(lname, entry.getValue()) == null) continue;
                throw new IllegalArgumentException("Variable with name " + (String)entry.getKey() + "' has already been defined.");
            }
        }
        this.xquery = query;
        this.xfilter = filter;
    }

    @Override
    public XPathExpression<T> clone() {
        AbstractXPathCompiled ret = null;
        try {
            AbstractXPathCompiled c;
            ret = c = (AbstractXPathCompiled)super.clone();
        }
        catch (CloneNotSupportedException cnse) {
            throw new IllegalStateException("Should never be getting a CloneNotSupportedException!", cnse);
        }
        HashMap<String, Map<String, Object>> vmt = new HashMap<String, Map<String, Object>>();
        for (Map.Entry<String, Map<String, Object>> me : this.xvariables.entrySet()) {
            HashMap<String, Object> cmap = new HashMap<String, Object>();
            for (Map.Entry<String, Object> ne : me.getValue().entrySet()) {
                cmap.put(ne.getKey(), ne.getValue());
            }
            vmt.put(me.getKey(), cmap);
        }
        ret.xvariables = vmt;
        return ret;
    }

    @Override
    public final String getExpression() {
        return this.xquery;
    }

    @Override
    public final Namespace getNamespace(String prefix) {
        Namespace ns = this.xnamespaces.get(prefix);
        if (ns == null) {
            throw new IllegalArgumentException("Namespace with prefix '" + prefix + "' has not been declared.");
        }
        return ns;
    }

    @Override
    public Namespace[] getNamespaces() {
        Namespace[] nsa = this.xnamespaces.values().toArray(new Namespace[this.xnamespaces.size()]);
        Arrays.sort(nsa, NSSORT);
        return nsa;
    }

    @Override
    public final Object getVariable(String name, Namespace uri) {
        Map<String, Object> vmap = this.xvariables.get(uri == null ? "" : uri.getURI());
        if (vmap == null) {
            throw new IllegalArgumentException("Variable with name '" + name + "' in namespace '" + uri.getURI() + "' has not been declared.");
        }
        Object ret = vmap.get(name);
        if (ret == null) {
            if (!vmap.containsKey(name)) {
                throw new IllegalArgumentException("Variable with name '" + name + "' in namespace '" + uri.getURI() + "' has not been declared.");
            }
            return null;
        }
        return ret;
    }

    @Override
    public Object getVariable(String qname) {
        if (qname == null) {
            throw new NullPointerException("Cannot get variable value for null qname");
        }
        int pos = qname.indexOf(58);
        if (pos >= 0) {
            return this.getVariable(qname.substring(pos + 1), this.getNamespace(qname.substring(0, pos)));
        }
        return this.getVariable(qname, Namespace.NO_NAMESPACE);
    }

    @Override
    public Object setVariable(String name, Namespace uri, Object value) {
        Object ret = this.getVariable(name, uri);
        this.xvariables.get(uri.getURI()).put(name, value);
        return ret;
    }

    @Override
    public Object setVariable(String qname, Object value) {
        if (qname == null) {
            throw new NullPointerException("Cannot get variable value for null qname");
        }
        int pos = qname.indexOf(58);
        if (pos >= 0) {
            return this.setVariable(qname.substring(pos + 1), this.getNamespace(qname.substring(0, pos)), value);
        }
        return this.setVariable(qname, Namespace.NO_NAMESPACE, value);
    }

    protected Map<String, Object> getVariables() {
        HashMap<String, Object> vars = new HashMap<String, Object>();
        Namespace[] nsa = this.getNamespaces();
        for (Map.Entry<String, Map<String, Object>> ue : this.xvariables.entrySet()) {
            String uri = ue.getKey();
            String pfx = AbstractXPathCompiled.getPrefixForURI(uri, nsa);
            for (Map.Entry<String, Object> ve : ue.getValue().entrySet()) {
                if ("".equals(pfx)) {
                    vars.put(ve.getKey(), ve.getValue());
                    continue;
                }
                vars.put(pfx + ":" + ve.getKey(), ve.getValue());
            }
        }
        return vars;
    }

    @Override
    public final Filter<T> getFilter() {
        return this.xfilter;
    }

    @Override
    public List<T> evaluate(Object context) {
        return this.xfilter.filter(this.evaluateRawAll(context));
    }

    @Override
    public T evaluateFirst(Object context) {
        Object raw = this.evaluateRawFirst(context);
        if (raw == null) {
            return null;
        }
        return this.xfilter.filter(raw);
    }

    @Override
    public XPathDiagnostic<T> diagnose(Object context, boolean firstonly) {
        List<Object> result = firstonly ? Collections.singletonList(this.evaluateRawFirst(context)) : this.evaluateRawAll(context);
        return new XPathDiagnosticImpl(context, this, result, firstonly);
    }

    public String toString() {
        int nscnt = this.xnamespaces.size();
        int vcnt = 0;
        for (Map<String, Object> cmap : this.xvariables.values()) {
            vcnt += cmap.size();
        }
        return String.format("[XPathExpression: %d namespaces and %d variables for query %s]", nscnt, vcnt, this.getExpression());
    }

    protected abstract List<?> evaluateRawAll(Object var1);

    protected abstract Object evaluateRawFirst(Object var1);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class NamespaceComparator
    implements Comparator<Namespace> {
        private NamespaceComparator() {
        }

        @Override
        public int compare(Namespace ns1, Namespace ns2) {
            return ns1.getPrefix().compareTo(ns2.getPrefix());
        }
    }
}

