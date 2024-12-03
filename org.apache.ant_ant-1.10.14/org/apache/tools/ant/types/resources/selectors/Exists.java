/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources.selectors;

import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;

public class Exists
implements ResourceSelector {
    @Override
    public boolean isSelected(Resource r) {
        return r.isExists();
    }
}

