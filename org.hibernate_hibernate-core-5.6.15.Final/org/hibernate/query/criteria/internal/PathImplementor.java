/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Path
 *  javax.persistence.metamodel.Attribute
 */
package org.hibernate.query.criteria.internal;

import javax.persistence.criteria.Path;
import javax.persistence.metamodel.Attribute;
import org.hibernate.query.criteria.internal.ExpressionImplementor;
import org.hibernate.query.criteria.internal.PathSource;
import org.hibernate.query.criteria.internal.Renderable;

public interface PathImplementor<X>
extends ExpressionImplementor<X>,
Path<X>,
PathSource<X>,
Renderable {
    public Attribute<?, ?> getAttribute();

    public <T extends X> PathImplementor<T> treatAs(Class<T> var1);
}

