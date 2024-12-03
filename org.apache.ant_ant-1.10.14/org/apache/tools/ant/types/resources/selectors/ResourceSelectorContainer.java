/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources.selectors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;

public class ResourceSelectorContainer
extends DataType {
    private final List<ResourceSelector> resourceSelectors = new ArrayList<ResourceSelector>();

    public ResourceSelectorContainer() {
    }

    public ResourceSelectorContainer(ResourceSelector ... resourceSelectors) {
        for (ResourceSelector rsel : resourceSelectors) {
            this.add(rsel);
        }
    }

    public void add(ResourceSelector s) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        if (s == null) {
            return;
        }
        this.resourceSelectors.add(s);
        this.setChecked(false);
    }

    public boolean hasSelectors() {
        if (this.isReference()) {
            return this.getRef().hasSelectors();
        }
        this.dieOnCircularReference();
        return !this.resourceSelectors.isEmpty();
    }

    public int selectorCount() {
        if (this.isReference()) {
            return this.getRef().selectorCount();
        }
        this.dieOnCircularReference();
        return this.resourceSelectors.size();
    }

    public Iterator<ResourceSelector> getSelectors() {
        if (this.isReference()) {
            return this.getRef().getSelectors();
        }
        return this.getResourceSelectors().iterator();
    }

    public List<ResourceSelector> getResourceSelectors() {
        if (this.isReference()) {
            return this.getRef().getResourceSelectors();
        }
        this.dieOnCircularReference();
        return Collections.unmodifiableList(this.resourceSelectors);
    }

    @Override
    protected void dieOnCircularReference(Stack<Object> stk, Project p) throws BuildException {
        if (this.isChecked()) {
            return;
        }
        if (this.isReference()) {
            super.dieOnCircularReference(stk, p);
        } else {
            for (ResourceSelector resourceSelector : this.resourceSelectors) {
                if (!(resourceSelector instanceof DataType)) continue;
                ResourceSelectorContainer.pushAndInvokeCircularReferenceCheck((DataType)((Object)resourceSelector), stk, p);
            }
            this.setChecked(true);
        }
    }

    private ResourceSelectorContainer getRef() {
        return this.getCheckedRef(ResourceSelectorContainer.class);
    }
}

