/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.web.api.model;

import com.atlassian.plugin.web.api.WebSection;
import com.atlassian.plugin.web.api.model.AbstractWebFragment;
import java.util.Map;
import javax.annotation.Nonnull;

public class WebSectionImpl
extends AbstractWebFragment
implements WebSection {
    private final String location;

    WebSectionImpl(String completeKey, String label, String title, String styleClass, String id, Map<String, String> params, int weight, String location) {
        super(completeKey, label, title, styleClass, id, params, weight);
        this.location = location;
    }

    @Override
    @Nonnull
    public String getLocation() {
        return this.location;
    }

    @Override
    protected String toStringOfFields() {
        return super.toStringOfFields() + ", location=" + this.location;
    }
}

