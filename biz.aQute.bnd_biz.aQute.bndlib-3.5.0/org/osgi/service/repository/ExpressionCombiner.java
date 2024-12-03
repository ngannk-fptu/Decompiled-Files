/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.osgi.service.repository;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.resource.Requirement;
import org.osgi.service.repository.AndExpression;
import org.osgi.service.repository.IdentityExpression;
import org.osgi.service.repository.NotExpression;
import org.osgi.service.repository.OrExpression;
import org.osgi.service.repository.RequirementExpression;

@ProviderType
public interface ExpressionCombiner {
    public AndExpression and(RequirementExpression var1, RequirementExpression var2);

    public AndExpression and(RequirementExpression var1, RequirementExpression var2, RequirementExpression ... var3);

    public IdentityExpression identity(Requirement var1);

    public NotExpression not(RequirementExpression var1);

    public OrExpression or(RequirementExpression var1, RequirementExpression var2);

    public OrExpression or(RequirementExpression var1, RequirementExpression var2, RequirementExpression ... var3);
}

