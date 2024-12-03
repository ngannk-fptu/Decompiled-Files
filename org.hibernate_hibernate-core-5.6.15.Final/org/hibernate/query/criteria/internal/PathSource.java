/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Path
 */
package org.hibernate.query.criteria.internal;

import javax.persistence.criteria.Path;
import org.hibernate.query.criteria.internal.compile.RenderingContext;

public interface PathSource<X>
extends Path<X> {
    public void prepareAlias(RenderingContext var1);

    public String getPathIdentifier();
}

