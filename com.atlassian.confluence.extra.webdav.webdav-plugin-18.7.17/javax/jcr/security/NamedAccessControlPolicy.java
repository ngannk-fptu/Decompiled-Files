/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.security;

import javax.jcr.RepositoryException;
import javax.jcr.security.AccessControlPolicy;

public interface NamedAccessControlPolicy
extends AccessControlPolicy {
    public String getName() throws RepositoryException;
}

