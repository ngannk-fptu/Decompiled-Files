/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.query.qom;

import javax.jcr.query.Query;
import javax.jcr.query.qom.Column;
import javax.jcr.query.qom.Constraint;
import javax.jcr.query.qom.Ordering;
import javax.jcr.query.qom.Source;

public interface QueryObjectModel
extends Query {
    public Source getSource();

    public Constraint getConstraint();

    public Ordering[] getOrderings();

    public Column[] getColumns();
}

