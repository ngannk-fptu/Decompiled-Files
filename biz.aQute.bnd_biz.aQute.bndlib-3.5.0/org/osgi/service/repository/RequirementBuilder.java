/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.osgi.service.repository;

import java.util.Map;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.service.repository.IdentityExpression;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@ProviderType
public interface RequirementBuilder {
    public RequirementBuilder addAttribute(String var1, Object var2);

    public RequirementBuilder addDirective(String var1, String var2);

    public RequirementBuilder setAttributes(Map<String, Object> var1);

    public RequirementBuilder setDirectives(Map<String, String> var1);

    public RequirementBuilder setResource(Resource var1);

    public Requirement build();

    public IdentityExpression buildExpression();
}

