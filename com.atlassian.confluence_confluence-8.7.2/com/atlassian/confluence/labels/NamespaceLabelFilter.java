/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.filter.Filter
 */
package com.atlassian.confluence.labels;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.labels.persistence.dao.LabelSearchResult;
import com.atlassian.core.util.filter.Filter;

public class NamespaceLabelFilter
implements Filter {
    private String currentUser;
    private Namespace namespace;

    public NamespaceLabelFilter(Namespace namespace) {
        this.namespace = namespace;
    }

    public NamespaceLabelFilter(Namespace namespace, String user) {
        this.namespace = namespace;
        this.currentUser = user;
    }

    public boolean isIncluded(Object object) {
        if (object instanceof Label) {
            Label label = (Label)object;
            return this.isLabelInNamespace(label);
        }
        if (object instanceof LabelSearchResult) {
            Label label = ((LabelSearchResult)object).getLabel();
            return this.isLabelInNamespace(label);
        }
        return false;
    }

    private boolean isLabelInNamespace(Label label) {
        return label.getNamespace().equals(this.namespace) && label.isVisibleTo(this.currentUser);
    }
}

