/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.edgeindex.edge;

import com.atlassian.confluence.plugins.edgeindex.model.EdgeType;

public class DefaultEdgeUiSupport
implements EdgeType.EdgeUiSupport {
    private final int weight;
    private final String cssClass;
    private final String i18nKey;

    public DefaultEdgeUiSupport() {
        this(100, "", "");
    }

    public DefaultEdgeUiSupport(int weight, String cssClass, String i18nKey) {
        this.weight = weight;
        this.cssClass = cssClass;
        this.i18nKey = i18nKey;
    }

    @Override
    public int getWeight() {
        return this.weight;
    }

    @Override
    public String getCssClass() {
        return this.cssClass;
    }

    @Override
    public String getI18NKey() {
        return this.i18nKey;
    }
}

