/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.version;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.MergeException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionHistory;

public interface VersionManager {
    public Version checkin(String var1) throws VersionException, UnsupportedRepositoryOperationException, InvalidItemStateException, LockException, RepositoryException;

    public void checkout(String var1) throws UnsupportedRepositoryOperationException, LockException, RepositoryException;

    public Version checkpoint(String var1) throws VersionException, UnsupportedRepositoryOperationException, InvalidItemStateException, LockException, RepositoryException;

    public boolean isCheckedOut(String var1) throws RepositoryException;

    public VersionHistory getVersionHistory(String var1) throws UnsupportedRepositoryOperationException, RepositoryException;

    public Version getBaseVersion(String var1) throws UnsupportedRepositoryOperationException, RepositoryException;

    public void restore(Version[] var1, boolean var2) throws ItemExistsException, UnsupportedRepositoryOperationException, VersionException, LockException, InvalidItemStateException, RepositoryException;

    public void restore(String var1, String var2, boolean var3) throws VersionException, ItemExistsException, UnsupportedRepositoryOperationException, LockException, InvalidItemStateException, RepositoryException;

    public void restore(Version var1, boolean var2) throws VersionException, ItemExistsException, InvalidItemStateException, UnsupportedRepositoryOperationException, LockException, RepositoryException;

    public void restore(String var1, Version var2, boolean var3) throws PathNotFoundException, ItemExistsException, VersionException, ConstraintViolationException, UnsupportedRepositoryOperationException, LockException, InvalidItemStateException, RepositoryException;

    public void restoreByLabel(String var1, String var2, boolean var3) throws VersionException, ItemExistsException, UnsupportedRepositoryOperationException, LockException, InvalidItemStateException, RepositoryException;

    public NodeIterator merge(String var1, String var2, boolean var3) throws NoSuchWorkspaceException, AccessDeniedException, MergeException, LockException, InvalidItemStateException, RepositoryException;

    public NodeIterator merge(String var1, String var2, boolean var3, boolean var4) throws NoSuchWorkspaceException, AccessDeniedException, MergeException, LockException, InvalidItemStateException, RepositoryException;

    public void doneMerge(String var1, Version var2) throws VersionException, InvalidItemStateException, UnsupportedRepositoryOperationException, RepositoryException;

    public void cancelMerge(String var1, Version var2) throws VersionException, InvalidItemStateException, UnsupportedRepositoryOperationException, RepositoryException;

    public Node createConfiguration(String var1) throws UnsupportedRepositoryOperationException, RepositoryException;

    public Node setActivity(Node var1) throws UnsupportedRepositoryOperationException, RepositoryException;

    public Node getActivity() throws UnsupportedRepositoryOperationException, RepositoryException;

    public Node createActivity(String var1) throws UnsupportedRepositoryOperationException, RepositoryException;

    public void removeActivity(Node var1) throws UnsupportedRepositoryOperationException, VersionException, RepositoryException;

    public NodeIterator merge(Node var1) throws VersionException, AccessDeniedException, MergeException, LockException, InvalidItemStateException, RepositoryException;
}

