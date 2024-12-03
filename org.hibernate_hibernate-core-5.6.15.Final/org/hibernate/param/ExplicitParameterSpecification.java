/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.param;

import org.hibernate.param.ParameterSpecification;

public interface ExplicitParameterSpecification
extends ParameterSpecification {
    public int getSourceLine();

    public int getSourceColumn();
}

