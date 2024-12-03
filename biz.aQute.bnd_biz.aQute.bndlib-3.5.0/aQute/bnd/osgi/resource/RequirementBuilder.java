/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi.resource;

import aQute.bnd.osgi.resource.CapReqBuilder;
import aQute.bnd.osgi.resource.FilterBuilder;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;

public class RequirementBuilder
extends CapReqBuilder {
    public RequirementBuilder(Resource resource, String namespace) {
        super(resource, namespace);
    }

    public RequirementBuilder(String namespace) {
        super(namespace);
    }

    public Requirement build() {
        return super.buildRequirement();
    }

    public Requirement synthetic() {
        return super.buildSyntheticRequirement();
    }

    public RequirementBuilder addFilter(String filter) {
        this.addDirective("filter", filter);
        return this;
    }

    public RequirementBuilder addFilter(FilterBuilder filter) {
        this.addDirective("filter", filter.toString());
        return this;
    }
}

