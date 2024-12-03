/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.security;

import java.security.Principal;
import javax.jcr.RepositoryException;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlException;
import javax.jcr.security.AccessControlPolicy;
import javax.jcr.security.Privilege;

public interface AccessControlList
extends AccessControlPolicy {
    public AccessControlEntry[] getAccessControlEntries() throws RepositoryException;

    public boolean addAccessControlEntry(Principal var1, Privilege[] var2) throws AccessControlException, RepositoryException;

    public void removeAccessControlEntry(AccessControlEntry var1) throws AccessControlException, RepositoryException;
}

