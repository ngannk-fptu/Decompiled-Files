/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.bind;

import java.util.Collection;
import org.apache.jackrabbit.webdav.bind.BindConstants;
import org.apache.jackrabbit.webdav.bind.ParentElement;
import org.apache.jackrabbit.webdav.property.AbstractDavProperty;

public class ParentSet
extends AbstractDavProperty<Collection<ParentElement>> {
    private final Collection<ParentElement> parents;

    public ParentSet(Collection<ParentElement> parents) {
        super(BindConstants.PARENTSET, true);
        this.parents = parents;
    }

    @Override
    public Collection<ParentElement> getValue() {
        return this.parents;
    }
}

