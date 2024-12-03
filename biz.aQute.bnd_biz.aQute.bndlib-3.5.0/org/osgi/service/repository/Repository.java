/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.osgi.service.repository;

import java.util.Collection;
import java.util.Map;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.service.repository.ExpressionCombiner;
import org.osgi.service.repository.RequirementBuilder;
import org.osgi.service.repository.RequirementExpression;
import org.osgi.util.promise.Promise;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@ProviderType
public interface Repository {
    public static final String URL = "repository.url";

    public Map<Requirement, Collection<Capability>> findProviders(Collection<? extends Requirement> var1);

    public Promise<Collection<Resource>> findProviders(RequirementExpression var1);

    public ExpressionCombiner getExpressionCombiner();

    public RequirementBuilder newRequirementBuilder(String var1);
}

