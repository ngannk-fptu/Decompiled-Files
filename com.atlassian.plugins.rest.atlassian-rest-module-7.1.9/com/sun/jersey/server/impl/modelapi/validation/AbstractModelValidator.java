/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.modelapi.validation;

import com.sun.jersey.api.model.AbstractModelComponent;
import com.sun.jersey.api.model.AbstractModelVisitor;
import com.sun.jersey.api.model.ResourceModelIssue;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractModelValidator
implements AbstractModelVisitor {
    final List<ResourceModelIssue> issueList = new LinkedList<ResourceModelIssue>();

    public List<ResourceModelIssue> getIssueList() {
        return this.issueList;
    }

    public boolean fatalIssuesFound() {
        for (ResourceModelIssue issue : this.getIssueList()) {
            if (!issue.isFatal()) continue;
            return true;
        }
        return false;
    }

    public void cleanIssueList() {
        this.issueList.clear();
    }

    public void validate(AbstractModelComponent component) {
        component.accept(this);
        List<AbstractModelComponent> componentList = component.getComponents();
        if (null != componentList) {
            for (AbstractModelComponent subcomponent : componentList) {
                this.validate(subcomponent);
            }
        }
    }
}

