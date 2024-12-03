/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.highlight.model;

import com.atlassian.confluence.plugins.highlight.model.XMLModification;
import org.codehaus.jackson.annotate.JsonProperty;

public class CellModification
extends XMLModification {
    private final int row;

    public CellModification(@JsonProperty(value="rowIndex") int row, @JsonProperty(value="xmlInsertion") String xml) {
        super(xml);
        this.row = row;
    }

    public int getRow() {
        return this.row;
    }
}

