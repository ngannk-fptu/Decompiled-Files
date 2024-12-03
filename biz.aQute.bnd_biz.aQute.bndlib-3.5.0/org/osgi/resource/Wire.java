/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package org.osgi.resource;

import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;

@ConsumerType
public interface Wire {
    public Capability getCapability();

    public Requirement getRequirement();

    public Resource getProvider();

    public Resource getRequirer();

    public boolean equals(Object var1);

    public int hashCode();
}

