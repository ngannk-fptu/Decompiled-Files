/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources.selectors;

import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;

public class Not
implements ResourceSelector {
    private ResourceSelector sel;

    public Not() {
    }

    public Not(ResourceSelector s) {
        this.add(s);
    }

    public void add(ResourceSelector s) {
        if (this.sel != null) {
            throw new IllegalStateException("The Not ResourceSelector accepts a single nested ResourceSelector");
        }
        this.sel = s;
    }

    @Override
    public boolean isSelected(Resource r) {
        return !this.sel.isSelected(r);
    }
}

