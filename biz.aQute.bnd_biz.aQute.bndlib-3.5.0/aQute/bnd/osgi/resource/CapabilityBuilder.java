/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi.resource;

import aQute.bnd.osgi.resource.CapReqBuilder;
import org.osgi.resource.Capability;
import org.osgi.resource.Resource;

public class CapabilityBuilder
extends CapReqBuilder {
    public CapabilityBuilder(Resource resource, String namespace) {
        super(resource, namespace);
    }

    public CapabilityBuilder(String namespace) {
        super(namespace);
    }

    public Capability build() {
        return super.buildCapability();
    }

    public Capability synthetic() {
        return super.buildSyntheticCapability();
    }
}

