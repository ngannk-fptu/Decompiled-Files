/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.property;

public interface Child {
    public Child getNextChild();

    public Child getPreviousChild();

    public void setNextChild(Child var1);

    public void setPreviousChild(Child var1);
}

