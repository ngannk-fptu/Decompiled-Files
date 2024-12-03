/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi.resource;

import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.resource.Wire;

public class WireImpl
implements Wire {
    private final Capability capability;
    private final Requirement requirement;

    public WireImpl(Capability capability, Requirement requirement) {
        if (capability == null || requirement == null) {
            throw new IllegalArgumentException("Both a capabability and a requirement are required. The following were supplied. Cap: " + String.valueOf(capability) + " Req: " + String.valueOf(requirement));
        }
        this.capability = capability;
        this.requirement = requirement;
    }

    @Override
    public Capability getCapability() {
        return this.capability;
    }

    @Override
    public Requirement getRequirement() {
        return this.requirement;
    }

    @Override
    public Resource getProvider() {
        return this.capability.getResource();
    }

    @Override
    public Resource getRequirer() {
        return this.requirement.getResource();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("WireImpl [").append(this.requirement.toString()).append("  -->  ").append(this.capability).append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        return this.capability.hashCode() + this.requirement.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        Wire w;
        if (obj == this) {
            return true;
        }
        if (obj instanceof Wire && this.capability.equals((w = (Wire)obj).getCapability()) && this.requirement.equals(w.getRequirement())) {
            Resource provider = this.getProvider();
            Resource requirer = this.getRequirer();
            return (provider == null ? w.getProvider() == null : provider.equals(w.getProvider())) && (requirer == null ? w.getRequirer() == null : requirer.equals(w.getRequirer()));
        }
        return false;
    }
}

