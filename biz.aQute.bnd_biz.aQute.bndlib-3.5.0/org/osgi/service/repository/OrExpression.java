/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.osgi.service.repository;

import java.util.List;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.repository.RequirementExpression;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@ProviderType
public interface OrExpression
extends RequirementExpression {
    public List<RequirementExpression> getRequirementExpressions();
}

