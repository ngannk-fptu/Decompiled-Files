/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport.model;

import com.atlassian.confluence.importexport.xmlimport.model.PrimitiveProperty;

@Deprecated
public class PrimitiveId
extends PrimitiveProperty {
    public PrimitiveId(String name, String value) {
        super(name, null, value);
    }

    @Override
    public String toString() {
        return "Id[" + this.getName() + "]=" + this.getValue();
    }
}

