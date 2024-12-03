/*
 * Decompiled with CFR 0.152.
 */
package org.jdom.output;

import java.util.Stack;
import org.jdom.Namespace;

class NamespaceStack {
    private static final String CVS_ID = "@(#) $RCSfile: NamespaceStack.java,v $ $Revision: 1.14 $ $Date: 2007/11/10 05:29:01 $ $Name:  $";
    private Stack prefixes = new Stack();
    private Stack uris = new Stack();

    NamespaceStack() {
    }

    public void push(Namespace ns) {
        this.prefixes.push(ns.getPrefix());
        this.uris.push(ns.getURI());
    }

    public String pop() {
        String prefix = (String)this.prefixes.pop();
        this.uris.pop();
        return prefix;
    }

    public int size() {
        return this.prefixes.size();
    }

    public String getURI(String prefix) {
        int index = this.prefixes.lastIndexOf(prefix);
        if (index == -1) {
            return null;
        }
        String uri = (String)this.uris.elementAt(index);
        return uri;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        String sep = System.getProperty("line.separator");
        buf.append("Stack: " + this.prefixes.size() + sep);
        for (int i = 0; i < this.prefixes.size(); ++i) {
            buf.append(this.prefixes.elementAt(i) + "&" + this.uris.elementAt(i) + sep);
        }
        return buf.toString();
    }
}

