/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Label
 */
package com.atlassian.confluence.api.impl.service.content.factory;

import com.atlassian.confluence.api.impl.service.content.factory.ModelFactory;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.labels.Label;

public class LabelFactory
extends ModelFactory<Label, com.atlassian.confluence.api.model.content.Label> {
    @Override
    public com.atlassian.confluence.api.model.content.Label buildFrom(Label hibernateObject, Expansions expansions) {
        return new com.atlassian.confluence.api.model.content.Label(hibernateObject.getNamespace().getPrefix(), hibernateObject.getName(), String.valueOf(hibernateObject.getId()));
    }
}

