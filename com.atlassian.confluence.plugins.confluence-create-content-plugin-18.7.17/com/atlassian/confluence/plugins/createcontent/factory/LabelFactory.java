/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Label
 *  com.atlassian.confluence.labels.Label
 */
package com.atlassian.confluence.plugins.createcontent.factory;

import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.labels.Label;

public class LabelFactory {
    public static com.atlassian.confluence.api.model.content.Label buildFrom(Label hibernateObject, Expansions expansions) {
        return new com.atlassian.confluence.api.model.content.Label(hibernateObject.getNamespace().getPrefix(), hibernateObject.getName(), String.valueOf(hibernateObject.getId()));
    }
}

