/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.mapping.model;

import org.springframework.data.mapping.model.CamelCaseSplittingFieldNamingStrategy;

public class SnakeCaseFieldNamingStrategy
extends CamelCaseSplittingFieldNamingStrategy {
    public SnakeCaseFieldNamingStrategy() {
        super("_");
    }
}

