/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.web.api.model;

import com.atlassian.plugin.web.api.WebFragment;
import java.util.Map;
import javax.annotation.Nonnull;

public abstract class AbstractWebFragment
implements WebFragment {
    private final String completeKey;
    private final String label;
    private final String title;
    private final String styleClass;
    private final String id;
    private final Map<String, String> params;
    private final int weight;

    protected AbstractWebFragment(String completeKey, String label, String title, String styleClass, String id, Map<String, String> params, int weight) {
        this.completeKey = completeKey;
        this.label = label;
        this.title = title;
        this.styleClass = styleClass;
        this.id = id;
        this.params = params;
        this.weight = weight;
    }

    @Override
    public String getCompleteKey() {
        return this.completeKey;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getStyleClass() {
        return this.styleClass;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    @Nonnull
    public Map<String, String> getParams() {
        return this.params;
    }

    @Override
    public int getWeight() {
        return this.weight;
    }

    public String toString() {
        return this.getClass().getName() + "{" + this.toStringOfFields() + "}";
    }

    protected String toStringOfFields() {
        return "completeKey=" + this.completeKey + ", label=" + this.label + ", title=" + this.title + ", styleClass=" + this.styleClass + ", id=" + this.id + ", params=" + this.params + ", weight=" + this.weight;
    }
}

