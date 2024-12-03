/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.txw2;

import java.util.EmptyStackException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

final class NamespaceSupport {
    public static final String XMLNS = "http://www.w3.org/XML/1998/namespace";
    public static final String NSDECL = "http://www.w3.org/xmlns/2000/";
    private static final Enumeration EMPTY_ENUMERATION = new Vector().elements();
    private Context[] contexts;
    private Context currentContext;
    private int contextPos;
    private boolean namespaceDeclUris;

    public NamespaceSupport() {
        this.reset();
    }

    public void reset() {
        this.contexts = new Context[32];
        this.namespaceDeclUris = false;
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
            max *= 2;
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
        this.contexts[this.contextPos].clear();
        --this.contextPos;
        if (this.contextPos < 0) {
            throw new EmptyStackException();
        }
        this.currentContext = this.contexts[this.contextPos];
    }

    public boolean declarePrefix(String prefix, String uri) {
        if (prefix.equals("xml") || prefix.equals("xmlns")) {
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

    public Enumeration getPrefixes() {
        return this.currentContext.getPrefixes();
    }

    public String getPrefix(String uri) {
        return this.currentContext.getPrefix(uri);
    }

    public Enumeration getPrefixes(String uri) {
        Vector<String> prefixes = new Vector<String>();
        Enumeration allPrefixes = this.getPrefixes();
        while (allPrefixes.hasMoreElements()) {
            String prefix = (String)allPrefixes.nextElement();
            if (!uri.equals(this.getURI(prefix))) continue;
            prefixes.addElement(prefix);
        }
        return prefixes.elements();
    }

    public Enumeration getDeclaredPrefixes() {
        return this.currentContext.getDeclaredPrefixes();
    }

    public void setNamespaceDeclUris(boolean value) {
        if (this.contextPos != 0) {
            throw new IllegalStateException();
        }
        if (value == this.namespaceDeclUris) {
            return;
        }
        this.namespaceDeclUris = value;
        if (value) {
            this.currentContext.declarePrefix("xmlns", NSDECL);
        } else {
            this.contexts[this.contextPos] = this.currentContext = new Context();
            this.currentContext.declarePrefix("xml", XMLNS);
        }
    }

    public boolean isNamespaceDeclUris() {
        return this.namespaceDeclUris;
    }

    final class Context {
        Hashtable prefixTable;
        Hashtable uriTable;
        Hashtable elementNameTable;
        Hashtable attributeNameTable;
        String defaultNS = "";
        private Vector declarations = null;
        private boolean declSeen = false;
        private Context parent = null;

        Context() {
            this.copyTables();
        }

        void setParent(Context parent) {
            this.parent = parent;
            this.declarations = null;
            this.prefixTable = parent.prefixTable;
            this.uriTable = parent.uriTable;
            this.elementNameTable = parent.elementNameTable;
            this.attributeNameTable = parent.attributeNameTable;
            this.defaultNS = parent.defaultNS;
            this.declSeen = false;
        }

        void clear() {
            this.parent = null;
            this.prefixTable = null;
            this.uriTable = null;
            this.elementNameTable = null;
            this.attributeNameTable = null;
            this.defaultNS = "";
        }

        void declarePrefix(String prefix, String uri) {
            if (!this.declSeen) {
                this.copyTables();
            }
            if (this.declarations == null) {
                this.declarations = new Vector();
            }
            prefix = prefix.intern();
            uri = uri.intern();
            if ("".equals(prefix)) {
                this.defaultNS = uri;
            } else {
                this.prefixTable.put(prefix, uri);
                this.uriTable.put(uri, prefix);
            }
            this.declarations.addElement(prefix);
        }

        String[] processName(String qName, boolean isAttribute) {
            Hashtable table = isAttribute ? this.attributeNameTable : this.elementNameTable;
            String[] name = (String[])table.get(qName);
            if (name != null) {
                return name;
            }
            name = new String[3];
            name[2] = qName.intern();
            int index = qName.indexOf(58);
            if (index == -1) {
                name[0] = isAttribute ? (qName == "xmlns" && NamespaceSupport.this.namespaceDeclUris ? NamespaceSupport.NSDECL : "") : this.defaultNS;
                name[1] = name[2];
            } else {
                String prefix = qName.substring(0, index);
                String local = qName.substring(index + 1);
                String uri = "".equals(prefix) ? this.defaultNS : (String)this.prefixTable.get(prefix);
                if (uri == null || !isAttribute && "xmlns".equals(prefix)) {
                    return null;
                }
                name[0] = uri;
                name[1] = local.intern();
            }
            table.put(name[2], name);
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
            if (this.uriTable != null) {
                String uriPrefix = (String)this.uriTable.get(uri);
                if (uriPrefix == null) {
                    return null;
                }
                String verifyNamespace = (String)this.prefixTable.get(uriPrefix);
                if (uri.equals(verifyNamespace)) {
                    return uriPrefix;
                }
            }
            return null;
        }

        Enumeration getDeclaredPrefixes() {
            if (this.declarations == null) {
                return EMPTY_ENUMERATION;
            }
            return this.declarations.elements();
        }

        Enumeration getPrefixes() {
            if (this.prefixTable == null) {
                return EMPTY_ENUMERATION;
            }
            return this.prefixTable.keys();
        }

        private void copyTables() {
            this.prefixTable = this.prefixTable != null ? (Hashtable)this.prefixTable.clone() : new Hashtable();
            this.uriTable = this.uriTable != null ? (Hashtable)this.uriTable.clone() : new Hashtable();
            this.elementNameTable = new Hashtable();
            this.attributeNameTable = new Hashtable();
            this.declSeen = true;
        }
    }
}

