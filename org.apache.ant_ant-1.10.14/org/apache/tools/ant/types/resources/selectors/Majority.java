/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources.selectors;

import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;
import org.apache.tools.ant.types.resources.selectors.ResourceSelectorContainer;

public class Majority
extends ResourceSelectorContainer
implements ResourceSelector {
    private boolean tie = true;

    public Majority() {
    }

    public Majority(ResourceSelector ... r) {
        super(r);
    }

    public synchronized void setAllowtie(boolean b) {
        this.tie = b;
    }

    @Override
    public synchronized boolean isSelected(Resource r) {
        int passed = 0;
        int failed = 0;
        int count = this.selectorCount();
        boolean even = count % 2 == 0;
        int threshold = count / 2;
        for (ResourceSelector rs : this.getResourceSelectors()) {
            if (rs.isSelected(r)) {
                if (++passed <= threshold && (!even || !this.tie || passed != threshold)) continue;
                return true;
            }
            if (++failed <= threshold && (!even || this.tie || failed != threshold)) continue;
            return false;
        }
        return false;
    }
}

