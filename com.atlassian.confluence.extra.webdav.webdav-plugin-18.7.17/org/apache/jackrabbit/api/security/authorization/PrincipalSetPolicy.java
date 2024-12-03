/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package org.apache.jackrabbit.api.security.authorization;

import java.security.Principal;
import java.util.Set;
import javax.jcr.security.AccessControlException;
import javax.jcr.security.AccessControlPolicy;
import org.jetbrains.annotations.NotNull;

public interface PrincipalSetPolicy
extends AccessControlPolicy {
    @NotNull
    public Set<Principal> getPrincipals();

    public boolean addPrincipals(Principal ... var1) throws AccessControlException;

    public boolean removePrincipals(Principal ... var1) throws AccessControlException;
}

