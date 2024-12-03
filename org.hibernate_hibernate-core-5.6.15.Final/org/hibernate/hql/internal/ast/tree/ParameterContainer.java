/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.ast.tree;

import org.hibernate.param.ParameterSpecification;

@Deprecated
public interface ParameterContainer {
    public void setText(String var1);

    public void addEmbeddedParameter(ParameterSpecification var1);

    public boolean hasEmbeddedParameters();

    public ParameterSpecification[] getEmbeddedParameters();
}

