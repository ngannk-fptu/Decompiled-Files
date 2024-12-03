/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources.selectors;

import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;
import org.apache.tools.ant.types.resources.selectors.ResourceSelectorContainer;

public class Or
extends ResourceSelectorContainer
implements ResourceSelector {
    public Or() {
    }

    public Or(ResourceSelector ... r) {
        super(r);
    }

    @Override
    public boolean isSelected(Resource r) {
        return this.getResourceSelectors().stream().anyMatch(s -> s.isSelected(r));
    }
}

