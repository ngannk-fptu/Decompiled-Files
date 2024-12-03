/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.resolver.reason;

import java.util.Collection;
import org.osgi.resource.Requirement;
import org.osgi.service.resolver.ResolutionException;

public class ReasonException
extends ResolutionException {
    private static final long serialVersionUID = -5276675175114379539L;
    private final Reason reason;

    public ReasonException(Reason reason, String message, Throwable cause, Collection<Requirement> unresolvedRequirements) {
        super(message, cause, unresolvedRequirements);
        this.reason = reason;
    }

    public Reason getReason() {
        return this.reason;
    }

    public static enum Reason {
        DynamicImport,
        FragmentNotSelected,
        MissingRequirement,
        UseConstraint;

    }
}

