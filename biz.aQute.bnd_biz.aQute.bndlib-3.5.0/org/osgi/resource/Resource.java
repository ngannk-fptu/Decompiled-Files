/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package org.osgi.resource;

import java.util.List;
import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@ConsumerType
public interface Resource {
    public List<Capability> getCapabilities(String var1);

    public List<Requirement> getRequirements(String var1);

    public boolean equals(Object var1);

    public int hashCode();
}

