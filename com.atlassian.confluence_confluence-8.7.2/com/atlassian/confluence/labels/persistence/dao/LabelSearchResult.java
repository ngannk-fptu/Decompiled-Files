/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.labels.persistence.dao;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.dto.CountableLabel;
import java.io.Serializable;

public class LabelSearchResult
implements CountableLabel,
Serializable {
    private Label label;
    private int count;

    public LabelSearchResult(Label label, int count) {
        this.label = label;
        this.count = count;
    }

    @Override
    public int getCount() {
        return this.count;
    }

    public Label getLabel() {
        return this.label;
    }
}

