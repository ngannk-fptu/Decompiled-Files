/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.osgi.service.repository;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.resource.Requirement;
import org.osgi.service.repository.RequirementExpression;

@ProviderType
public interface IdentityExpression
extends RequirementExpression {
    public Requirement getRequirement();
}

