/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.ParameterExpression
 */
package org.hibernate.query.criteria.internal.compile;

import java.util.List;
import java.util.Map;
import javax.persistence.criteria.ParameterExpression;
import org.hibernate.query.criteria.internal.compile.ExplicitParameterInfo;
import org.hibernate.query.criteria.internal.compile.ImplicitParameterBinding;

public interface InterpretedParameterMetadata {
    public Map<ParameterExpression<?>, ExplicitParameterInfo<?>> explicitParameterInfoMap();

    public List<ImplicitParameterBinding> implicitParameterBindings();
}

