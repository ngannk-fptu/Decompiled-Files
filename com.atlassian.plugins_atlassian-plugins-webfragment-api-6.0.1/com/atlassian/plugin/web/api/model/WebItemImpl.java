/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.web.api.model;

import com.atlassian.plugin.web.api.WebItem;
import com.atlassian.plugin.web.api.model.AbstractWebFragment;
import java.util.Map;
import javax.annotation.Nonnull;

public class WebItemImpl
extends AbstractWebFragment
implements WebItem {
    private final String section;
    private final String url;
    private final String accessKey;
    private final String entryPoint;

    WebItemImpl(String completeKey, String label, String title, String styleClass, String id, Map<String, String> params, int weight, String section, String url, String accessKey, String entryPoint) {
        super(completeKey, label, title, styleClass, id, params, weight);
        this.section = section;
        this.url = url;
        this.accessKey = accessKey;
        this.entryPoint = entryPoint;
    }

    @Override
    @Nonnull
    public String getSection() {
        return this.section;
    }

    @Override
    @Nonnull
    public String getUrl() {
        return this.url;
    }

    @Override
    public String getAccessKey() {
        return this.accessKey;
    }

    @Override
    protected String toStringOfFields() {
        return super.toStringOfFields() + ", section=" + this.section + ", url=" + this.url + ", accessKey=" + this.accessKey;
    }

    @Override
    @Nonnull
    public String getEntryPoint() {
        return this.entryPoint;
    }
}

