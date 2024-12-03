/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.labels.Label
 */
package com.atlassian.confluence.plugins.rest.entities;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.plugins.rest.entities.LabelEntity;

public class LabelEntityBuilder {
    public LabelEntity build(Label label) {
        LabelEntity labelEntity = new LabelEntity();
        labelEntity.setName(label.getName());
        labelEntity.setNamespace(label.getNamespace().toString());
        labelEntity.setOwner(label.getOwner());
        return labelEntity;
    }
}

