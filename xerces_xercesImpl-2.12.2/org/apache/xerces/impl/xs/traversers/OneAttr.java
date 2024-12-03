/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.traversers;

class OneAttr {
    public String name;
    public int dvIndex;
    public int valueIndex;
    public Object dfltValue;

    public OneAttr(String string, int n, int n2, Object object) {
        this.name = string;
        this.dvIndex = n;
        this.valueIndex = n2;
        this.dfltValue = object;
    }
}

