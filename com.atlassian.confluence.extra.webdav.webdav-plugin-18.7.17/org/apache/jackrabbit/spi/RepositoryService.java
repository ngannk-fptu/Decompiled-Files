/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import javax.jcr.AccessDeniedException;
import javax.jcr.Credentials;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.LoginException;
import javax.jcr.MergeException;
import javax.jcr.NamespaceException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.PathNotFoundException;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.InvalidNodeTypeDefinitionException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeTypeExistsException;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.version.VersionException;
import org.apache.jackrabbit.spi.Batch;
import org.apache.jackrabbit.spi.ChildInfo;
import org.apache.jackrabbit.spi.EventBundle;
import org.apache.jackrabbit.spi.EventFilter;
import org.apache.jackrabbit.spi.IdFactory;
import org.apache.jackrabbit.spi.ItemId;
import org.apache.jackrabbit.spi.ItemInfo;
import org.apache.jackrabbit.spi.ItemInfoCache;
import org.apache.jackrabbit.spi.LockInfo;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NameFactory;
import org.apache.jackrabbit.spi.NodeId;
import org.apache.jackrabbit.spi.NodeInfo;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.PathFactory;
import org.apache.jackrabbit.spi.PrivilegeDefinition;
import org.apache.jackrabbit.spi.PropertyId;
import org.apache.jackrabbit.spi.PropertyInfo;
import org.apache.jackrabbit.spi.QNodeDefinition;
import org.apache.jackrabbit.spi.QNodeTypeDefinition;
import org.apache.jackrabbit.spi.QPropertyDefinition;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.QValueFactory;
import org.apache.jackrabbit.spi.QueryInfo;
import org.apache.jackrabbit.spi.SessionInfo;
import org.apache.jackrabbit.spi.Subscription;
import org.apache.jackrabbit.spi.Tree;

public interface RepositoryService {
    public IdFactory getIdFactory() throws RepositoryException;

    public NameFactory getNameFactory() throws RepositoryException;

    public PathFactory getPathFactory() throws RepositoryException;

    public QValueFactory getQValueFactory() throws RepositoryException;

    public ItemInfoCache getItemInfoCache(SessionInfo var1) throws RepositoryException;

    public Map<String, QValue[]> getRepositoryDescriptors() throws RepositoryException;

    public SessionInfo obtain(Credentials var1, String var2) throws LoginException, NoSuchWorkspaceException, RepositoryException;

    public SessionInfo obtain(SessionInfo var1, String var2) throws LoginException, NoSuchWorkspaceException, RepositoryException;

    public SessionInfo impersonate(SessionInfo var1, Credentials var2) throws LoginException, RepositoryException;

    public void dispose(SessionInfo var1) throws RepositoryException;

    public String[] getWorkspaceNames(SessionInfo var1) throws RepositoryException;

    public boolean isGranted(SessionInfo var1, ItemId var2, String[] var3) throws RepositoryException;

    public PrivilegeDefinition[] getPrivilegeDefinitions(SessionInfo var1) throws RepositoryException;

    public Name[] getPrivilegeNames(SessionInfo var1, NodeId var2) throws RepositoryException;

    public PrivilegeDefinition[] getSupportedPrivileges(SessionInfo var1, NodeId var2) throws RepositoryException;

    public QNodeDefinition getNodeDefinition(SessionInfo var1, NodeId var2) throws RepositoryException;

    public QPropertyDefinition getPropertyDefinition(SessionInfo var1, PropertyId var2) throws RepositoryException;

    public NodeInfo getNodeInfo(SessionInfo var1, NodeId var2) throws ItemNotFoundException, RepositoryException;

    public Iterator<? extends ItemInfo> getItemInfos(SessionInfo var1, ItemId var2) throws ItemNotFoundException, RepositoryException;

    public Iterator<ChildInfo> getChildInfos(SessionInfo var1, NodeId var2) throws ItemNotFoundException, RepositoryException;

    public Iterator<PropertyId> getReferences(SessionInfo var1, NodeId var2, Name var3, boolean var4) throws ItemNotFoundException, RepositoryException;

    public PropertyInfo getPropertyInfo(SessionInfo var1, PropertyId var2) throws ItemNotFoundException, RepositoryException;

    public Batch createBatch(SessionInfo var1, ItemId var2) throws RepositoryException;

    public void submit(Batch var1) throws PathNotFoundException, ItemNotFoundException, NoSuchNodeTypeException, ValueFormatException, VersionException, LockException, ConstraintViolationException, AccessDeniedException, UnsupportedRepositoryOperationException, RepositoryException;

    public Tree createTree(SessionInfo var1, Batch var2, Name var3, Name var4, String var5) throws RepositoryException;

    public void importXml(SessionInfo var1, NodeId var2, InputStream var3, int var4) throws ItemExistsException, PathNotFoundException, VersionException, ConstraintViolationException, LockException, AccessDeniedException, UnsupportedRepositoryOperationException, RepositoryException;

    public void move(SessionInfo var1, NodeId var2, NodeId var3, Name var4) throws ItemExistsException, PathNotFoundException, VersionException, ConstraintViolationException, LockException, AccessDeniedException, UnsupportedRepositoryOperationException, RepositoryException;

    public void copy(SessionInfo var1, String var2, NodeId var3, NodeId var4, Name var5) throws NoSuchWorkspaceException, ConstraintViolationException, VersionException, AccessDeniedException, PathNotFoundException, ItemExistsException, LockException, UnsupportedRepositoryOperationException, RepositoryException;

    public void update(SessionInfo var1, NodeId var2, String var3) throws NoSuchWorkspaceException, AccessDeniedException, LockException, InvalidItemStateException, RepositoryException;

    public void clone(SessionInfo var1, String var2, NodeId var3, NodeId var4, Name var5, boolean var6) throws NoSuchWorkspaceException, ConstraintViolationException, VersionException, AccessDeniedException, PathNotFoundException, ItemExistsException, LockException, UnsupportedRepositoryOperationException, RepositoryException;

    public LockInfo getLockInfo(SessionInfo var1, NodeId var2) throws AccessDeniedException, RepositoryException;

    public LockInfo lock(SessionInfo var1, NodeId var2, boolean var3, boolean var4) throws UnsupportedRepositoryOperationException, LockException, AccessDeniedException, RepositoryException;

    public LockInfo lock(SessionInfo var1, NodeId var2, boolean var3, boolean var4, long var5, String var7) throws UnsupportedRepositoryOperationException, LockException, AccessDeniedException, RepositoryException;

    public void refreshLock(SessionInfo var1, NodeId var2) throws UnsupportedRepositoryOperationException, LockException, AccessDeniedException, RepositoryException;

    public void unlock(SessionInfo var1, NodeId var2) throws UnsupportedRepositoryOperationException, LockException, AccessDeniedException, RepositoryException;

    public NodeId checkin(SessionInfo var1, NodeId var2) throws VersionException, UnsupportedRepositoryOperationException, InvalidItemStateException, LockException, RepositoryException;

    public void checkout(SessionInfo var1, NodeId var2) throws UnsupportedRepositoryOperationException, LockException, RepositoryException;

    public void checkout(SessionInfo var1, NodeId var2, NodeId var3) throws UnsupportedRepositoryOperationException, LockException, RepositoryException;

    public NodeId checkpoint(SessionInfo var1, NodeId var2) throws UnsupportedRepositoryOperationException, RepositoryException;

    public NodeId checkpoint(SessionInfo var1, NodeId var2, NodeId var3) throws UnsupportedRepositoryOperationException, RepositoryException;

    public void removeVersion(SessionInfo var1, NodeId var2, NodeId var3) throws ReferentialIntegrityException, AccessDeniedException, UnsupportedRepositoryOperationException, VersionException, RepositoryException;

    public void restore(SessionInfo var1, NodeId var2, NodeId var3, boolean var4) throws VersionException, PathNotFoundException, ItemExistsException, UnsupportedRepositoryOperationException, LockException, InvalidItemStateException, RepositoryException;

    public void restore(SessionInfo var1, NodeId[] var2, boolean var3) throws ItemExistsException, UnsupportedRepositoryOperationException, VersionException, LockException, InvalidItemStateException, RepositoryException;

    public Iterator<NodeId> merge(SessionInfo var1, NodeId var2, String var3, boolean var4) throws NoSuchWorkspaceException, AccessDeniedException, MergeException, LockException, InvalidItemStateException, RepositoryException;

    public Iterator<NodeId> merge(SessionInfo var1, NodeId var2, String var3, boolean var4, boolean var5) throws NoSuchWorkspaceException, AccessDeniedException, MergeException, LockException, InvalidItemStateException, RepositoryException;

    public void resolveMergeConflict(SessionInfo var1, NodeId var2, NodeId[] var3, NodeId[] var4) throws VersionException, InvalidItemStateException, UnsupportedRepositoryOperationException, RepositoryException;

    public void addVersionLabel(SessionInfo var1, NodeId var2, NodeId var3, Name var4, boolean var5) throws VersionException, RepositoryException;

    public void removeVersionLabel(SessionInfo var1, NodeId var2, NodeId var3, Name var4) throws VersionException, RepositoryException;

    public NodeId createActivity(SessionInfo var1, String var2) throws UnsupportedRepositoryOperationException, RepositoryException;

    public void removeActivity(SessionInfo var1, NodeId var2) throws UnsupportedRepositoryOperationException, RepositoryException;

    public Iterator<NodeId> mergeActivity(SessionInfo var1, NodeId var2) throws UnsupportedRepositoryOperationException, RepositoryException;

    public NodeId createConfiguration(SessionInfo var1, NodeId var2) throws UnsupportedRepositoryOperationException, RepositoryException;

    public String[] getSupportedQueryLanguages(SessionInfo var1) throws RepositoryException;

    public String[] checkQueryStatement(SessionInfo var1, String var2, String var3, Map<String, String> var4) throws InvalidQueryException, RepositoryException;

    public QueryInfo executeQuery(SessionInfo var1, String var2, String var3, Map<String, String> var4, long var5, long var7, Map<String, QValue> var9) throws RepositoryException;

    public EventFilter createEventFilter(SessionInfo var1, int var2, Path var3, boolean var4, String[] var5, Name[] var6, boolean var7) throws UnsupportedRepositoryOperationException, RepositoryException;

    public Subscription createSubscription(SessionInfo var1, EventFilter[] var2) throws UnsupportedRepositoryOperationException, RepositoryException;

    public void updateEventFilters(Subscription var1, EventFilter[] var2) throws RepositoryException;

    public EventBundle[] getEvents(Subscription var1, long var2) throws RepositoryException, InterruptedException;

    public EventBundle getEvents(SessionInfo var1, EventFilter var2, long var3) throws RepositoryException, UnsupportedRepositoryOperationException;

    public void dispose(Subscription var1) throws RepositoryException;

    public Map<String, String> getRegisteredNamespaces(SessionInfo var1) throws RepositoryException;

    public String getNamespaceURI(SessionInfo var1, String var2) throws NamespaceException, RepositoryException;

    public String getNamespacePrefix(SessionInfo var1, String var2) throws NamespaceException, RepositoryException;

    public void registerNamespace(SessionInfo var1, String var2, String var3) throws NamespaceException, UnsupportedRepositoryOperationException, AccessDeniedException, RepositoryException;

    public void unregisterNamespace(SessionInfo var1, String var2) throws NamespaceException, UnsupportedRepositoryOperationException, AccessDeniedException, RepositoryException;

    public Iterator<QNodeTypeDefinition> getQNodeTypeDefinitions(SessionInfo var1) throws RepositoryException;

    public Iterator<QNodeTypeDefinition> getQNodeTypeDefinitions(SessionInfo var1, Name[] var2) throws RepositoryException;

    public void registerNodeTypes(SessionInfo var1, QNodeTypeDefinition[] var2, boolean var3) throws InvalidNodeTypeDefinitionException, NodeTypeExistsException, UnsupportedRepositoryOperationException, RepositoryException;

    public void unregisterNodeTypes(SessionInfo var1, Name[] var2) throws UnsupportedRepositoryOperationException, NoSuchNodeTypeException, RepositoryException;

    public void createWorkspace(SessionInfo var1, String var2, String var3) throws AccessDeniedException, UnsupportedRepositoryOperationException, NoSuchWorkspaceException, RepositoryException;

    public void deleteWorkspace(SessionInfo var1, String var2) throws AccessDeniedException, UnsupportedRepositoryOperationException, NoSuchWorkspaceException, RepositoryException;
}

