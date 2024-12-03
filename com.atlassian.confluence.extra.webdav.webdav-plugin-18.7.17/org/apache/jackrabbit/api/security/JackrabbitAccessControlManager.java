/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.apache.jackrabbit.api.security;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import javax.jcr.AccessDeniedException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.security.AccessControlException;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.AccessControlPolicy;
import javax.jcr.security.Privilege;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlPolicy;
import org.apache.jackrabbit.api.security.authorization.PrivilegeCollection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface JackrabbitAccessControlManager
extends AccessControlManager {
    @NotNull
    public JackrabbitAccessControlPolicy[] getApplicablePolicies(@NotNull Principal var1) throws AccessDeniedException, AccessControlException, UnsupportedRepositoryOperationException, RepositoryException;

    @NotNull
    public JackrabbitAccessControlPolicy[] getPolicies(@NotNull Principal var1) throws AccessDeniedException, AccessControlException, UnsupportedRepositoryOperationException, RepositoryException;

    @NotNull
    public AccessControlPolicy[] getEffectivePolicies(@NotNull Set<Principal> var1) throws AccessDeniedException, AccessControlException, UnsupportedRepositoryOperationException, RepositoryException;

    @NotNull
    default public Iterator<AccessControlPolicy> getEffectivePolicies(@NotNull Set<Principal> principals, String ... absPaths) throws AccessDeniedException, AccessControlException, UnsupportedRepositoryOperationException, RepositoryException {
        return Arrays.stream(this.getEffectivePolicies(principals)).filter(policy -> {
            if (policy instanceof JackrabbitAccessControlPolicy) {
                String acPath = ((JackrabbitAccessControlPolicy)policy).getPath();
                return Arrays.stream(absPaths).anyMatch(path -> {
                    if (path == null) {
                        return acPath == null;
                    }
                    return acPath != null && (acPath.equals(path) || path.startsWith(acPath + "/"));
                });
            }
            return true;
        }).iterator();
    }

    public boolean hasPrivileges(@Nullable String var1, @NotNull Set<Principal> var2, @NotNull Privilege[] var3) throws PathNotFoundException, AccessDeniedException, RepositoryException;

    @NotNull
    public Privilege[] getPrivileges(@Nullable String var1, @NotNull Set<Principal> var2) throws PathNotFoundException, AccessDeniedException, RepositoryException;

    @NotNull
    default public PrivilegeCollection getPrivilegeCollection(@Nullable String absPath) throws RepositoryException {
        return new PrivilegeCollection.Default(this.getPrivileges(absPath), this);
    }

    @NotNull
    default public PrivilegeCollection getPrivilegeCollection(@Nullable String absPath, @NotNull Set<Principal> principals) throws RepositoryException {
        return new PrivilegeCollection.Default(this.getPrivileges(absPath, principals), this);
    }

    @NotNull
    default public PrivilegeCollection privilegeCollectionFromNames(String ... privilegeNames) throws RepositoryException {
        ArrayList<Privilege> privileges = new ArrayList<Privilege>();
        for (String privilegeName : privilegeNames) {
            privileges.add(this.privilegeFromName(privilegeName));
        }
        return new PrivilegeCollection.Default(privileges.toArray(new Privilege[0]), this);
    }
}

