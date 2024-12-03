/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.exception.spi;

import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;

public interface ConversionContext {
    public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter();
}

