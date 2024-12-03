/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl.symbolTable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;

public abstract class SymTabEntry {
    protected QName qname;
    protected String name;
    private boolean isReferenced = false;
    private HashMap dynamicVars = new HashMap();

    protected SymTabEntry(QName qname) {
        this.qname = qname;
    }

    public final QName getQName() {
        return this.qname;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public final boolean isReferenced() {
        return this.isReferenced;
    }

    public final void setIsReferenced(boolean isReferenced) {
        this.isReferenced = isReferenced;
    }

    public Object getDynamicVar(Object key) {
        return this.dynamicVars.get(key);
    }

    public void setDynamicVar(Object key, Object value) {
        this.dynamicVars.put(key, value);
    }

    public String toString() {
        return this.toString("");
    }

    protected String toString(String indent) {
        String string = indent + "QName:         " + this.qname + '\n' + indent + "name:          " + this.name + '\n' + indent + "isReferenced?  " + this.isReferenced + '\n';
        String prefix = indent + "dynamicVars:   ";
        Iterator entries = this.dynamicVars.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = entries.next();
            Object key = entry.getKey();
            string = string + prefix + key + " = " + entry.getValue() + '\n';
            prefix = indent + "               ";
        }
        return string;
    }
}

