/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.model;

import org.apache.abdera.model.ExtensibleElement;

public interface Div
extends ExtensibleElement {
    public String[] getXhtmlClass();

    public String getId();

    public String getTitle();

    public Div setId(String var1);

    public Div setTitle(String var1);

    public Div setXhtmlClass(String[] var1);

    public String getValue();

    public void setValue(String var1);
}

