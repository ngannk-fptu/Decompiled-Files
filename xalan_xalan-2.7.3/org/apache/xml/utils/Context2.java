/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.utils;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

final class Context2 {
    private static final Enumeration EMPTY_ENUMERATION = new Vector().elements();
    Hashtable prefixTable;
    Hashtable uriTable;
    Hashtable elementNameTable;
    Hashtable attributeNameTable;
    String defaultNS = null;
    private Vector declarations = null;
    private boolean tablesDirty = false;
    private Context2 parent = null;
    private Context2 child = null;

    Context2(Context2 parent) {
        if (parent == null) {
            this.prefixTable = new Hashtable();
            this.uriTable = new Hashtable();
            this.elementNameTable = null;
            this.attributeNameTable = null;
        } else {
            this.setParent(parent);
        }
    }

    Context2 getChild() {
        return this.child;
    }

    Context2 getParent() {
        return this.parent;
    }

    void setParent(Context2 parent) {
        this.parent = parent;
        parent.child = this;
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
            this.declarations = new Vector();
        }
        prefix = prefix.intern();
        uri = uri.intern();
        if ("".equals(prefix)) {
            this.defaultNS = "".equals(uri) ? null : uri;
        } else {
            this.prefixTable.put(prefix, uri);
            this.uriTable.put(uri, prefix);
        }
        this.declarations.addElement(prefix);
    }

    String[] processName(String qName, boolean isAttribute) {
        Hashtable table;
        if (isAttribute) {
            if (this.elementNameTable == null) {
                this.elementNameTable = new Hashtable();
            }
            table = this.elementNameTable;
        } else {
            if (this.attributeNameTable == null) {
                this.attributeNameTable = new Hashtable();
            }
            table = this.attributeNameTable;
        }
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
        this.prefixTable = (Hashtable)this.prefixTable.clone();
        this.uriTable = (Hashtable)this.uriTable.clone();
        if (this.elementNameTable != null) {
            this.elementNameTable = new Hashtable();
        }
        if (this.attributeNameTable != null) {
            this.attributeNameTable = new Hashtable();
        }
        this.tablesDirty = true;
    }
}

