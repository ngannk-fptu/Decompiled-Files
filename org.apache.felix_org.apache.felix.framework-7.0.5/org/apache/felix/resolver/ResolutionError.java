/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.resolver;

import java.util.Collection;
import java.util.Collections;
import org.osgi.resource.Requirement;
import org.osgi.service.resolver.ResolutionException;

public abstract class ResolutionError {
    public abstract String getMessage();

    public Collection<Requirement> getUnresolvedRequirements() {
        return Collections.emptyList();
    }

    public abstract ResolutionException toException();

    public String toString() {
        return this.getMessage();
    }
}

