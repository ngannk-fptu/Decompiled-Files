/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.security;

import javax.jcr.AccessDeniedException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.lock.LockException;
import javax.jcr.security.AccessControlException;
import javax.jcr.security.AccessControlPolicy;
import javax.jcr.security.AccessControlPolicyIterator;
import javax.jcr.security.Privilege;
import javax.jcr.version.VersionException;

public interface AccessControlManager {
    public Privilege[] getSupportedPrivileges(String var1) throws PathNotFoundException, RepositoryException;

    public Privilege privilegeFromName(String var1) throws AccessControlException, RepositoryException;

    public boolean hasPrivileges(String var1, Privilege[] var2) throws PathNotFoundException, RepositoryException;

    public Privilege[] getPrivileges(String var1) throws PathNotFoundException, RepositoryException;

    public AccessControlPolicy[] getPolicies(String var1) throws PathNotFoundException, AccessDeniedException, RepositoryException;

    public AccessControlPolicy[] getEffectivePolicies(String var1) throws PathNotFoundException, AccessDeniedException, RepositoryException;

    public AccessControlPolicyIterator getApplicablePolicies(String var1) throws PathNotFoundException, AccessDeniedException, RepositoryException;

    public void setPolicy(String var1, AccessControlPolicy var2) throws PathNotFoundException, AccessControlException, AccessDeniedException, LockException, VersionException, RepositoryException;

    public void removePolicy(String var1, AccessControlPolicy var2) throws PathNotFoundException, AccessControlException, AccessDeniedException, LockException, VersionException, RepositoryException;
}

