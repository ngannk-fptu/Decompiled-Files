/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Selection
 */
package org.hibernate.query.criteria.internal;

import java.util.List;
import javax.persistence.criteria.Selection;
import org.hibernate.query.criteria.internal.TupleElementImplementor;
import org.hibernate.query.criteria.internal.ValueHandlerFactory;

public interface SelectionImplementor<X>
extends TupleElementImplementor<X>,
Selection<X> {
    public List<ValueHandlerFactory.ValueHandler> getValueHandlers();
}

