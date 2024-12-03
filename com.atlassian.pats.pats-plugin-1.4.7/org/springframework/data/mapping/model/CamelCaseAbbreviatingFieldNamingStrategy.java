/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.mapping.model;

import org.springframework.data.mapping.model.CamelCaseSplittingFieldNamingStrategy;

public class CamelCaseAbbreviatingFieldNamingStrategy
extends CamelCaseSplittingFieldNamingStrategy {
    public CamelCaseAbbreviatingFieldNamingStrategy() {
        super("");
    }

    @Override
    protected String preparePart(String part) {
        return part.substring(0, 1);
    }
}

