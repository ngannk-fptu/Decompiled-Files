/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi;

import javax.jcr.RepositoryException;
import javax.jcr.nodetype.ConstraintViolationException;
import org.apache.jackrabbit.spi.QValue;

public interface QValueConstraint {
    public static final QValueConstraint[] EMPTY_ARRAY = new QValueConstraint[0];

    public void check(QValue var1) throws ConstraintViolationException, RepositoryException;

    public String getString();
}

