/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.resolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.osgi.resource.Requirement;

public class ResolutionException
extends Exception {
    private static final long serialVersionUID = 1L;
    private final transient Collection<Requirement> unresolvedRequirements;

    public ResolutionException(String message, Throwable cause, Collection<Requirement> unresolvedRequirements) {
        super(message, cause);
        this.unresolvedRequirements = unresolvedRequirements == null || unresolvedRequirements.isEmpty() ? null : Collections.unmodifiableCollection(new ArrayList<Requirement>(unresolvedRequirements));
    }

    public ResolutionException(String message) {
        super(message);
        this.unresolvedRequirements = null;
    }

    public ResolutionException(Throwable cause) {
        super(cause);
        this.unresolvedRequirements = null;
    }

    private static Collection<Requirement> emptyCollection() {
        return Collections.EMPTY_LIST;
    }

    public Collection<Requirement> getUnresolvedRequirements() {
        return this.unresolvedRequirements != null ? this.unresolvedRequirements : ResolutionException.emptyCollection();
    }
}

