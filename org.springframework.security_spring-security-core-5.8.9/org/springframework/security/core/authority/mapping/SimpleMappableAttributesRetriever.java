/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.core.authority.mapping;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.springframework.security.core.authority.mapping.MappableAttributesRetriever;

public class SimpleMappableAttributesRetriever
implements MappableAttributesRetriever {
    private Set<String> mappableAttributes = null;

    @Override
    public Set<String> getMappableAttributes() {
        return this.mappableAttributes;
    }

    public void setMappableAttributes(Set<String> aMappableRoles) {
        this.mappableAttributes = new HashSet<String>();
        this.mappableAttributes.addAll(aMappableRoles);
        this.mappableAttributes = Collections.unmodifiableSet(this.mappableAttributes);
    }
}

