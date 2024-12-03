/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.util;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Iterator;

public final class NamespaceSupport {
    public static final String XMLNS = "http://www.w3.org/XML/1998/namespace";
    private static final Iterable<String> EMPTY_ENUMERATION = new ArrayList<String>();
    private Context[] contexts;
    private Context currentContext;
    private int contextPos;

    public NamespaceSupport() {
        this.reset();
    }

    public NamespaceSupport(NamespaceSupport that) {
        this.contexts = new Context[that.contexts.length];
        this.currentContext = null;
        this.contextPos = that.contextPos;
        Context currentParent = null;
        for (int i = 0; i < that.contexts.length; ++i) {
            Context thisContext;
            Context thatContext = that.contexts[i];
            if (thatContext == null) {
                this.contexts[i] = null;
                continue;
            }
            this.contexts[i] = thisContext = new Context(thatContext, currentParent);
            if (that.currentContext == thatContext) {
                this.currentContext = thisContext;
            }
            currentParent = thisContext;
        }
    }

    public void reset() {
        this.contexts = new Context[32];
        this.contextPos = 0;
        this.contexts[this.contextPos] = this.currentContext = new Context();
        this.currentContext.declarePrefix("xml", XMLNS);
    }

    public void pushContext() {
        int max = this.contexts.length;
        ++this.contextPos;
        if (this.contextPos >= max) {
            Context[] newContexts = new Context[max * 2];
            System.arraycopy(this.contexts, 0, newContexts, 0, max);
            this.contexts = newContexts;
        }
        this.currentContext = this.contexts[this.contextPos];
        if (this.currentContext == null) {
            this.contexts[this.contextPos] = this.currentContext = new Context();
        }
        if (this.contextPos > 0) {
            this.currentContext.setParent(this.contexts[this.contextPos - 1]);
        }
    }

    public void popContext() {
        --this.contextPos;
        if (this.contextPos < 0) {
            throw new EmptyStackException();
        }
        this.currentContext = this.contexts[this.contextPos];
    }

    public void slideContextUp() {
        --this.contextPos;
        this.currentContext = this.contexts[this.contextPos];
    }

    public void slideContextDown() {
        ++this.contextPos;
        if (this.contexts[this.contextPos] == null) {
            this.contexts[this.contextPos] = this.contexts[this.contextPos - 1];
        }
        this.currentContext = this.contexts[this.contextPos];
    }

    public boolean declarePrefix(String prefix, String uri) {
        if (prefix.equals("xml") && !uri.equals(XMLNS) || prefix.equals("xmlns")) {
            return false;
        }
        this.currentContext.declarePrefix(prefix, uri);
        return true;
    }

    public String[] processName(String qName, String[] parts, boolean isAttribute) {
        String[] myParts = this.currentContext.processName(qName, isAttribute);
        if (myParts == null) {
            return null;
        }
        parts[0] = myParts[0];
        parts[1] = myParts[1];
        parts[2] = myParts[2];
        return parts;
    }

    public String getURI(String prefix) {
        return this.currentContext.getURI(prefix);
    }

    public Iterable<String> getPrefixes() {
        return this.currentContext.getPrefixes();
    }

    public String getPrefix(String uri) {
        return this.currentContext.getPrefix(uri);
    }

    public Iterator getPrefixes(String uri) {
        ArrayList<String> prefixes = new ArrayList<String>();
        for (String prefix : this.getPrefixes()) {
            if (!uri.equals(this.getURI(prefix))) continue;
            prefixes.add(prefix);
        }
        return prefixes.iterator();
    }

    public Iterable<String> getDeclaredPrefixes() {
        return this.currentContext.getDeclaredPrefixes();
    }

    static final class Context {
        HashMap prefixTable;
        HashMap uriTable;
        HashMap elementNameTable;
        HashMap attributeNameTable;
        String defaultNS = null;
        private ArrayList declarations = null;
        private boolean tablesDirty = false;
        private Context parent = null;

        Context() {
            this.copyTables();
        }

        Context(Context that, Context newParent) {
            if (that == null) {
                this.copyTables();
                return;
            }
            if (newParent != null && !that.tablesDirty) {
                this.prefixTable = that.prefixTable == that.parent.prefixTable ? newParent.prefixTable : (HashMap)that.prefixTable.clone();
                this.uriTable = that.uriTable == that.parent.uriTable ? newParent.uriTable : (HashMap)that.uriTable.clone();
                this.elementNameTable = that.elementNameTable == that.parent.elementNameTable ? newParent.elementNameTable : (HashMap)that.elementNameTable.clone();
                this.attributeNameTable = that.attributeNameTable == that.parent.attributeNameTable ? newParent.attributeNameTable : (HashMap)that.attributeNameTable.clone();
                this.defaultNS = that.defaultNS == that.parent.defaultNS ? newParent.defaultNS : that.defaultNS;
            } else {
                this.prefixTable = (HashMap)that.prefixTable.clone();
                this.uriTable = (HashMap)that.uriTable.clone();
                this.elementNameTable = (HashMap)that.elementNameTable.clone();
                this.attributeNameTable = (HashMap)that.attributeNameTable.clone();
                this.defaultNS = that.defaultNS;
            }
            this.tablesDirty = that.tablesDirty;
            this.parent = newParent;
            this.declarations = that.declarations == null ? null : (ArrayList)that.declarations.clone();
        }

        void setParent(Context parent) {
            this.parent = parent;
            this.declarations = null;
            this.prefixTable = parent.prefixTable;
            this.uriTable = parent.uriTable;
            this.elementNameTable = parent.elementNameTable;
            this.attributeNameTable = parent.attributeNameTable;
            this.defaultNS = parent.defaultNS;
            this.tablesDirty = false;
        }

        void declarePrefix(String prefix, String uri) {
            if (!this.tablesDirty) {
                this.copyTables();
            }
            if (this.declarations == null) {
                this.declarations = new ArrayList();
            }
            prefix = prefix.intern();
            uri = uri.intern();
            if ("".equals(prefix)) {
                this.defaultNS = "".equals(uri) ? null : uri;
            } else {
                this.prefixTable.put(prefix, uri);
                this.uriTable.put(uri, prefix);
            }
            this.declarations.add(prefix);
        }

        String[] processName(String qName, boolean isAttribute) {
            HashMap table = isAttribute ? this.elementNameTable : this.attributeNameTable;
            String[] name = (String[])table.get(qName);
            if (name != null) {
                return name;
            }
            name = new String[3];
            int index = qName.indexOf(58);
            if (index == -1) {
                name[0] = isAttribute || this.defaultNS == null ? "" : this.defaultNS;
                name[1] = qName.intern();
                name[2] = name[1];
            } else {
                String prefix = qName.substring(0, index);
                String local = qName.substring(index + 1);
                String uri = "".equals(prefix) ? this.defaultNS : (String)this.prefixTable.get(prefix);
                if (uri == null) {
                    return null;
                }
                name[0] = uri;
                name[1] = local.intern();
                name[2] = qName.intern();
            }
            table.put(name[2], name);
            this.tablesDirty = true;
            return name;
        }

        String getURI(String prefix) {
            if ("".equals(prefix)) {
                return this.defaultNS;
            }
            if (this.prefixTable == null) {
                return null;
            }
            return (String)this.prefixTable.get(prefix);
        }

        String getPrefix(String uri) {
            if (this.uriTable == null) {
                return null;
            }
            return (String)this.uriTable.get(uri);
        }

        Iterable<String> getDeclaredPrefixes() {
            if (this.declarations == null) {
                return EMPTY_ENUMERATION;
            }
            return this.declarations;
        }

        Iterable<String> getPrefixes() {
            if (this.prefixTable == null) {
                return EMPTY_ENUMERATION;
            }
            return this.prefixTable.keySet();
        }

        private void copyTables() {
            this.prefixTable = this.prefixTable != null ? (HashMap)this.prefixTable.clone() : new HashMap();
            this.uriTable = this.uriTable != null ? (HashMap)this.uriTable.clone() : new HashMap();
            this.elementNameTable = new HashMap();
            this.attributeNameTable = new HashMap();
            this.tablesDirty = true;
        }
    }
}

