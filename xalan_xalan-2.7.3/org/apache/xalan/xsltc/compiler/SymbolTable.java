/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import java.util.Hashtable;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.xalan.xsltc.compiler.AttributeSet;
import org.apache.xalan.xsltc.compiler.DecimalFormatting;
import org.apache.xalan.xsltc.compiler.Key;
import org.apache.xalan.xsltc.compiler.Param;
import org.apache.xalan.xsltc.compiler.QName;
import org.apache.xalan.xsltc.compiler.Stylesheet;
import org.apache.xalan.xsltc.compiler.SyntaxTreeNode;
import org.apache.xalan.xsltc.compiler.Template;
import org.apache.xalan.xsltc.compiler.Variable;
import org.apache.xalan.xsltc.compiler.util.MethodType;

final class SymbolTable {
    private final Hashtable _stylesheets = new Hashtable();
    private final Hashtable _primops = new Hashtable();
    private Hashtable _variables = null;
    private Hashtable _templates = null;
    private Hashtable _attributeSets = null;
    private Hashtable _aliases = null;
    private Hashtable _excludedURI = null;
    private Stack _excludedURIStack = null;
    private Hashtable _decimalFormats = null;
    private Hashtable _keys = null;
    private int _nsCounter = 0;
    private SyntaxTreeNode _current = null;

    SymbolTable() {
    }

    public DecimalFormatting getDecimalFormatting(QName name) {
        if (this._decimalFormats == null) {
            return null;
        }
        return (DecimalFormatting)this._decimalFormats.get(name);
    }

    public void addDecimalFormatting(QName name, DecimalFormatting symbols) {
        if (this._decimalFormats == null) {
            this._decimalFormats = new Hashtable();
        }
        this._decimalFormats.put(name, symbols);
    }

    public Key getKey(QName name) {
        if (this._keys == null) {
            return null;
        }
        return (Key)this._keys.get(name);
    }

    public void addKey(QName name, Key key) {
        if (this._keys == null) {
            this._keys = new Hashtable();
        }
        this._keys.put(name, key);
    }

    public Stylesheet addStylesheet(QName name, Stylesheet node) {
        return this._stylesheets.put(name, node);
    }

    public Stylesheet lookupStylesheet(QName name) {
        return (Stylesheet)this._stylesheets.get(name);
    }

    public Template addTemplate(Template template) {
        QName name = template.getName();
        if (this._templates == null) {
            this._templates = new Hashtable();
        }
        return this._templates.put(name, template);
    }

    public Template lookupTemplate(QName name) {
        if (this._templates == null) {
            return null;
        }
        return (Template)this._templates.get(name);
    }

    public Variable addVariable(Variable variable) {
        if (this._variables == null) {
            this._variables = new Hashtable();
        }
        String name = variable.getName().getStringRep();
        return this._variables.put(name, variable);
    }

    public Param addParam(Param parameter) {
        if (this._variables == null) {
            this._variables = new Hashtable();
        }
        String name = parameter.getName().getStringRep();
        return this._variables.put(name, parameter);
    }

    public Variable lookupVariable(QName qname) {
        if (this._variables == null) {
            return null;
        }
        String name = qname.getStringRep();
        Object obj = this._variables.get(name);
        return obj instanceof Variable ? (Variable)obj : null;
    }

    public Param lookupParam(QName qname) {
        if (this._variables == null) {
            return null;
        }
        String name = qname.getStringRep();
        Object obj = this._variables.get(name);
        return obj instanceof Param ? (Param)obj : null;
    }

    public SyntaxTreeNode lookupName(QName qname) {
        if (this._variables == null) {
            return null;
        }
        String name = qname.getStringRep();
        return (SyntaxTreeNode)this._variables.get(name);
    }

    public AttributeSet addAttributeSet(AttributeSet atts) {
        if (this._attributeSets == null) {
            this._attributeSets = new Hashtable();
        }
        return this._attributeSets.put(atts.getName(), atts);
    }

    public AttributeSet lookupAttributeSet(QName name) {
        if (this._attributeSets == null) {
            return null;
        }
        return (AttributeSet)this._attributeSets.get(name);
    }

    public void addPrimop(String name, MethodType mtype) {
        Vector<MethodType> methods = (Vector<MethodType>)this._primops.get(name);
        if (methods == null) {
            methods = new Vector<MethodType>();
            this._primops.put(name, methods);
        }
        methods.addElement(mtype);
    }

    public Vector lookupPrimop(String name) {
        return (Vector)this._primops.get(name);
    }

    public String generateNamespacePrefix() {
        return "ns" + this._nsCounter++;
    }

    public void setCurrentNode(SyntaxTreeNode node) {
        this._current = node;
    }

    public String lookupNamespace(String prefix) {
        if (this._current == null) {
            return "";
        }
        return this._current.lookupNamespace(prefix);
    }

    public void addPrefixAlias(String prefix, String alias) {
        if (this._aliases == null) {
            this._aliases = new Hashtable();
        }
        this._aliases.put(prefix, alias);
    }

    public String lookupPrefixAlias(String prefix) {
        if (this._aliases == null) {
            return null;
        }
        return (String)this._aliases.get(prefix);
    }

    public void excludeURI(String uri) {
        Integer refcnt;
        if (uri == null) {
            return;
        }
        if (this._excludedURI == null) {
            this._excludedURI = new Hashtable();
        }
        refcnt = (refcnt = (Integer)this._excludedURI.get(uri)) == null ? new Integer(1) : new Integer(refcnt + 1);
        this._excludedURI.put(uri, refcnt);
    }

    public void excludeNamespaces(String prefixes) {
        if (prefixes != null) {
            StringTokenizer tokens = new StringTokenizer(prefixes);
            while (tokens.hasMoreTokens()) {
                String prefix = tokens.nextToken();
                String uri = prefix.equals("#default") ? this.lookupNamespace("") : this.lookupNamespace(prefix);
                if (uri == null) continue;
                this.excludeURI(uri);
            }
        }
    }

    public boolean isExcludedNamespace(String uri) {
        if (uri != null && this._excludedURI != null) {
            Integer refcnt = (Integer)this._excludedURI.get(uri);
            return refcnt != null && refcnt > 0;
        }
        return false;
    }

    public void unExcludeNamespaces(String prefixes) {
        if (this._excludedURI == null) {
            return;
        }
        if (prefixes != null) {
            StringTokenizer tokens = new StringTokenizer(prefixes);
            while (tokens.hasMoreTokens()) {
                String prefix = tokens.nextToken();
                String uri = prefix.equals("#default") ? this.lookupNamespace("") : this.lookupNamespace(prefix);
                Integer refcnt = (Integer)this._excludedURI.get(uri);
                if (refcnt == null) continue;
                this._excludedURI.put(uri, new Integer(refcnt - 1));
            }
        }
    }

    public void pushExcludedNamespacesContext() {
        if (this._excludedURIStack == null) {
            this._excludedURIStack = new Stack();
        }
        this._excludedURIStack.push(this._excludedURI);
        this._excludedURI = null;
    }

    public void popExcludedNamespacesContext() {
        this._excludedURI = (Hashtable)this._excludedURIStack.pop();
        if (this._excludedURIStack.isEmpty()) {
            this._excludedURIStack = null;
        }
    }
}

