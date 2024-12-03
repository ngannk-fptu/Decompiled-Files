/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.TupleElement
 */
package org.hibernate.query.criteria.internal;

import javax.persistence.TupleElement;
import org.hibernate.query.criteria.internal.ValueHandlerFactory;

public interface TupleElementImplementor<X>
extends TupleElement<X> {
    public ValueHandlerFactory.ValueHandler<X> getValueHandler();
}

