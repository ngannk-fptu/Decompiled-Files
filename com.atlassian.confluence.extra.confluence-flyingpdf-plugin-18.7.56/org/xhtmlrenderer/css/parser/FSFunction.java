/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.parser;

import java.util.Iterator;
import java.util.List;

public class FSFunction {
    private String _name;
    private List _parameters;

    public FSFunction(String name, List parameters) {
        this._name = name;
        this._parameters = parameters;
    }

    public String getName() {
        return this._name;
    }

    public List getParameters() {
        return this._parameters;
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(this._name);
        result.append('(');
        Iterator i = this._parameters.iterator();
        while (i.hasNext()) {
            result.append(i.next());
            result.append(',');
        }
        result.append(')');
        return result.toString();
    }
}

