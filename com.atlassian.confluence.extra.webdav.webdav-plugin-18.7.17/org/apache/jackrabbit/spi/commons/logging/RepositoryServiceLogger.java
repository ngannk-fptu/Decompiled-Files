/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.logging;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import javax.jcr.Credentials;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.nodetype.InvalidNodeTypeDefinitionException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeTypeExistsException;
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
import org.apache.jackrabbit.spi.RepositoryService;
import org.apache.jackrabbit.spi.SessionInfo;
import org.apache.jackrabbit.spi.Subscription;
import org.apache.jackrabbit.spi.Tree;
import org.apache.jackrabbit.spi.commons.logging.AbstractLogger;
import org.apache.jackrabbit.spi.commons.logging.BatchLogger;
import org.apache.jackrabbit.spi.commons.logging.LogWriter;
import org.apache.jackrabbit.spi.commons.logging.SessionInfoLogger;

public class RepositoryServiceLogger
extends AbstractLogger
implements RepositoryService {
    private final RepositoryService service;

    public RepositoryServiceLogger(RepositoryService service, LogWriter writer) {
        super(writer);
        this.service = service;
    }

    public RepositoryService getRepositoryService() {
        return this.service;
    }

    @Override
    public NameFactory getNameFactory() throws RepositoryException {
        return (NameFactory)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.getNameFactory();
            }
        }, "getNameFactory()", new Object[0]);
    }

    @Override
    public PathFactory getPathFactory() throws RepositoryException {
        return (PathFactory)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.getPathFactory();
            }
        }, "getPathFactory()", new Object[0]);
    }

    @Override
    public IdFactory getIdFactory() throws RepositoryException {
        return (IdFactory)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.getIdFactory();
            }
        }, "getIdFactory()", new Object[0]);
    }

    @Override
    public QValueFactory getQValueFactory() throws RepositoryException {
        return (QValueFactory)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.getQValueFactory();
            }
        }, "getQValueFactory()", new Object[0]);
    }

    @Override
    public Map<String, QValue[]> getRepositoryDescriptors() throws RepositoryException {
        return (Map)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.getRepositoryDescriptors();
            }
        }, "getRepositoryDescriptors()", new Object[0]);
    }

    @Override
    public ItemInfoCache getItemInfoCache(final SessionInfo sessionInfo) throws RepositoryException {
        return (ItemInfoCache)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.getItemInfoCache(sessionInfo);
            }
        }, "getItemInfoCache(SessionInfo)", new Object[]{sessionInfo});
    }

    @Override
    public SessionInfo obtain(final Credentials credentials, final String workspaceName) throws RepositoryException {
        return (SessionInfo)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.obtain(credentials, workspaceName);
            }
        }, "obtain(Credentials, String)", new Object[]{credentials, workspaceName});
    }

    @Override
    public SessionInfo obtain(final SessionInfo sessionInfo, final String workspaceName) throws RepositoryException {
        return (SessionInfo)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.obtain(RepositoryServiceLogger.unwrap(sessionInfo), workspaceName);
            }
        }, "obtain(SessionInfo, String)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), workspaceName});
    }

    @Override
    public SessionInfo impersonate(final SessionInfo sessionInfo, final Credentials credentials) throws RepositoryException {
        return (SessionInfo)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.impersonate(RepositoryServiceLogger.unwrap(sessionInfo), credentials);
            }
        }, "impersonate(SessionInfo, Credentials)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), credentials});
    }

    @Override
    public void dispose(final SessionInfo sessionInfo) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                RepositoryServiceLogger.this.service.dispose(RepositoryServiceLogger.unwrap(sessionInfo));
                return null;
            }
        }, "dispose(SessionInfo)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo)});
    }

    @Override
    public String[] getWorkspaceNames(final SessionInfo sessionInfo) throws RepositoryException {
        return (String[])this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.getWorkspaceNames(RepositoryServiceLogger.unwrap(sessionInfo));
            }
        }, "getWorkspaceNames(SessionInfo)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo)});
    }

    @Override
    public boolean isGranted(final SessionInfo sessionInfo, final ItemId itemId, final String[] actions) throws RepositoryException {
        return (Boolean)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.isGranted(RepositoryServiceLogger.unwrap(sessionInfo), itemId, actions);
            }
        }, "isGranted(SessionInfo, ItemId, String[])", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), itemId, actions});
    }

    @Override
    public PrivilegeDefinition[] getPrivilegeDefinitions(final SessionInfo sessionInfo) throws RepositoryException {
        return (PrivilegeDefinition[])this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.getPrivilegeDefinitions(RepositoryServiceLogger.unwrap(sessionInfo));
            }
        }, "getSupportedPrivileges(SessionInfo)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo)});
    }

    @Override
    public PrivilegeDefinition[] getSupportedPrivileges(final SessionInfo sessionInfo, final NodeId nodeId) throws RepositoryException {
        return (PrivilegeDefinition[])this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.getSupportedPrivileges(RepositoryServiceLogger.unwrap(sessionInfo), nodeId);
            }
        }, "getSupportedPrivileges(SessionInfo, NodeId)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), nodeId});
    }

    @Override
    public Name[] getPrivilegeNames(final SessionInfo sessionInfo, final NodeId nodeId) throws RepositoryException {
        return (Name[])this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.getPrivilegeNames(RepositoryServiceLogger.unwrap(sessionInfo), nodeId);
            }
        }, "getPrivileges(SessionInfo, NodeId)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), nodeId});
    }

    @Override
    public QNodeDefinition getNodeDefinition(final SessionInfo sessionInfo, final NodeId nodeId) throws RepositoryException {
        return (QNodeDefinition)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.getNodeDefinition(RepositoryServiceLogger.unwrap(sessionInfo), nodeId);
            }
        }, "getNodeDefinition(SessionInfo, NodeId)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), nodeId});
    }

    @Override
    public QPropertyDefinition getPropertyDefinition(final SessionInfo sessionInfo, final PropertyId propertyId) throws RepositoryException {
        return (QPropertyDefinition)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.getPropertyDefinition(RepositoryServiceLogger.unwrap(sessionInfo), propertyId);
            }
        }, "getPropertyDefinition(SessionInfo, PropertyId)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), propertyId});
    }

    @Override
    public NodeInfo getNodeInfo(final SessionInfo sessionInfo, final NodeId nodeId) throws RepositoryException {
        return (NodeInfo)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.getNodeInfo(RepositoryServiceLogger.unwrap(sessionInfo), nodeId);
            }
        }, "getNodeInfo(SessionInfo, NodeId)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), nodeId});
    }

    @Override
    public Iterator<? extends ItemInfo> getItemInfos(final SessionInfo sessionInfo, final ItemId itemId) throws RepositoryException {
        return (Iterator)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.getItemInfos(RepositoryServiceLogger.unwrap(sessionInfo), itemId);
            }
        }, "getItemInfos(SessionInfo, NodeId)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), itemId});
    }

    @Override
    public Iterator<ChildInfo> getChildInfos(final SessionInfo sessionInfo, final NodeId parentId) throws RepositoryException {
        return (Iterator)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.getChildInfos(RepositoryServiceLogger.unwrap(sessionInfo), parentId);
            }
        }, "getChildInfos(SessionInfo, NodeId)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), parentId});
    }

    @Override
    public Iterator<PropertyId> getReferences(final SessionInfo sessionInfo, final NodeId nodeId, final Name propertyName, final boolean weakReferences) throws RepositoryException {
        return (Iterator)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.getReferences(RepositoryServiceLogger.unwrap(sessionInfo), nodeId, propertyName, weakReferences);
            }
        }, "getReferences(SessionInfo, NodeId, Name, boolean)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), nodeId, propertyName, weakReferences});
    }

    @Override
    public PropertyInfo getPropertyInfo(final SessionInfo sessionInfo, final PropertyId propertyId) throws RepositoryException {
        return (PropertyInfo)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.getPropertyInfo(RepositoryServiceLogger.unwrap(sessionInfo), propertyId);
            }
        }, "getPropertyInfo(SessionInfo,PropertyId)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), propertyId});
    }

    @Override
    public Batch createBatch(final SessionInfo sessionInfo, final ItemId itemId) throws RepositoryException {
        return (Batch)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.createBatch(RepositoryServiceLogger.unwrap(sessionInfo), itemId);
            }
        }, "createBatch(SessionInfo, ItemId)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), itemId});
    }

    @Override
    public void submit(final Batch batch) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                RepositoryServiceLogger.this.service.submit(RepositoryServiceLogger.unwrap(batch));
                return null;
            }
        }, "submit(Batch)", new Object[]{RepositoryServiceLogger.unwrap(batch)});
    }

    @Override
    public Tree createTree(final SessionInfo sessionInfo, final Batch batch, final Name nodeName, final Name primaryTypeName, final String uniqueId) throws RepositoryException {
        return (Tree)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.createTree(sessionInfo, batch, nodeName, primaryTypeName, uniqueId);
            }
        }, "createTree(SessionInfo, Batch, Name, Name, String)", new Object[]{sessionInfo, batch, nodeName, primaryTypeName, uniqueId});
    }

    @Override
    public void importXml(final SessionInfo sessionInfo, final NodeId parentId, final InputStream xmlStream, final int uuidBehaviour) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                RepositoryServiceLogger.this.service.importXml(RepositoryServiceLogger.unwrap(sessionInfo), parentId, xmlStream, uuidBehaviour);
                return null;
            }
        }, "importXml(SessionInfo, NodeId, InputStream, int)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), parentId, xmlStream, uuidBehaviour});
    }

    @Override
    public void move(final SessionInfo sessionInfo, final NodeId srcNodeId, final NodeId destParentNodeId, final Name destName) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                RepositoryServiceLogger.this.service.move(RepositoryServiceLogger.unwrap(sessionInfo), srcNodeId, destParentNodeId, destName);
                return null;
            }
        }, "move(SessionInfo, NodeId, NodeId, Name)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), srcNodeId, destParentNodeId, destName});
    }

    @Override
    public void copy(final SessionInfo sessionInfo, final String srcWorkspaceName, final NodeId srcNodeId, final NodeId destParentNodeId, final Name destName) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                RepositoryServiceLogger.this.service.copy(RepositoryServiceLogger.unwrap(sessionInfo), srcWorkspaceName, srcNodeId, destParentNodeId, destName);
                return null;
            }
        }, "copy(SessionInfo, String, NodeId, NodeId, Name)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), srcWorkspaceName, srcNodeId, destParentNodeId, destName});
    }

    @Override
    public void update(final SessionInfo sessionInfo, final NodeId nodeId, final String srcWorkspaceName) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                RepositoryServiceLogger.this.service.update(RepositoryServiceLogger.unwrap(sessionInfo), nodeId, srcWorkspaceName);
                return null;
            }
        }, "update(SessionInfo, NodeId, String)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), nodeId, srcWorkspaceName});
    }

    @Override
    public void clone(final SessionInfo sessionInfo, final String srcWorkspaceName, final NodeId srcNodeId, final NodeId destParentNodeId, final Name destName, final boolean removeExisting) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                RepositoryServiceLogger.this.service.clone(RepositoryServiceLogger.unwrap(sessionInfo), srcWorkspaceName, srcNodeId, destParentNodeId, destName, removeExisting);
                return null;
            }
        }, "clone(SessionInfo, String, NodeId, NodeId, Name, boolean)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), srcWorkspaceName, srcNodeId, destParentNodeId, destName, removeExisting});
    }

    @Override
    public LockInfo getLockInfo(final SessionInfo sessionInfo, final NodeId nodeId) throws RepositoryException {
        return (LockInfo)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.getLockInfo(RepositoryServiceLogger.unwrap(sessionInfo), nodeId);
            }
        }, "getLockInfo(SessionInfo, NodeId)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), nodeId});
    }

    @Override
    public LockInfo lock(final SessionInfo sessionInfo, final NodeId nodeId, final boolean deep, final boolean sessionScoped) throws RepositoryException {
        return (LockInfo)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.lock(RepositoryServiceLogger.unwrap(sessionInfo), nodeId, deep, sessionScoped);
            }
        }, "lock(SessionInfo, NodeId, boolean, boolean)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), nodeId, deep, sessionScoped});
    }

    @Override
    public LockInfo lock(final SessionInfo sessionInfo, final NodeId nodeId, final boolean deep, final boolean sessionScoped, final long timeoutHint, final String ownerHint) throws RepositoryException {
        return (LockInfo)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.lock(RepositoryServiceLogger.unwrap(sessionInfo), nodeId, deep, sessionScoped, timeoutHint, ownerHint);
            }
        }, "lock(SessionInfo, NodeId, boolean, boolean, long, String)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), nodeId, deep, sessionScoped, timeoutHint, ownerHint});
    }

    @Override
    public void refreshLock(final SessionInfo sessionInfo, final NodeId nodeId) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                RepositoryServiceLogger.this.service.refreshLock(RepositoryServiceLogger.unwrap(sessionInfo), nodeId);
                return null;
            }
        }, "refreshLock(SessionInfo, NodeId)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), nodeId});
    }

    @Override
    public void unlock(final SessionInfo sessionInfo, final NodeId nodeId) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                RepositoryServiceLogger.this.service.unlock(RepositoryServiceLogger.unwrap(sessionInfo), nodeId);
                return null;
            }
        }, "unlock(SessionInfo, NodeId)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), nodeId});
    }

    @Override
    public NodeId checkin(final SessionInfo sessionInfo, final NodeId nodeId) throws RepositoryException {
        return (NodeId)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.checkin(RepositoryServiceLogger.unwrap(sessionInfo), nodeId);
            }
        }, "checkin(SessionInfo, NodeId)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), nodeId});
    }

    @Override
    public void checkout(final SessionInfo sessionInfo, final NodeId nodeId) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                RepositoryServiceLogger.this.service.checkout(RepositoryServiceLogger.unwrap(sessionInfo), nodeId);
                return null;
            }
        }, "checkout(SessionInfo, NodeId)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), nodeId});
    }

    @Override
    public void checkout(final SessionInfo sessionInfo, final NodeId nodeId, final NodeId activityId) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                RepositoryServiceLogger.this.service.checkout(RepositoryServiceLogger.unwrap(sessionInfo), nodeId, activityId);
                return null;
            }
        }, "checkout(SessionInfo, NodeId, NodeId)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), nodeId, activityId});
    }

    @Override
    public NodeId checkpoint(final SessionInfo sessionInfo, final NodeId nodeId) throws UnsupportedRepositoryOperationException, RepositoryException {
        return (NodeId)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.checkpoint(RepositoryServiceLogger.unwrap(sessionInfo), nodeId);
            }
        }, "checkpoint(SessionInfo, NodeId)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), nodeId});
    }

    @Override
    public NodeId checkpoint(final SessionInfo sessionInfo, final NodeId nodeId, final NodeId activityId) throws UnsupportedRepositoryOperationException, RepositoryException {
        return (NodeId)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.checkpoint(RepositoryServiceLogger.unwrap(sessionInfo), nodeId, activityId);
            }
        }, "checkpoint(SessionInfo, NodeId, NodeId)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), nodeId, activityId});
    }

    @Override
    public void removeVersion(final SessionInfo sessionInfo, final NodeId versionHistoryId, final NodeId versionId) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                RepositoryServiceLogger.this.service.removeVersion(RepositoryServiceLogger.unwrap(sessionInfo), versionHistoryId, versionId);
                return null;
            }
        }, "removeVersion(SessionInfo, NodeId, NodeId)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), versionHistoryId, versionId});
    }

    @Override
    public void restore(final SessionInfo sessionInfo, final NodeId nodeId, final NodeId versionId, final boolean removeExisting) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                RepositoryServiceLogger.this.service.restore(RepositoryServiceLogger.unwrap(sessionInfo), nodeId, versionId, removeExisting);
                return null;
            }
        }, "restore(SessionInfo, NodeId, NodeId, boolean)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), nodeId, versionId, removeExisting});
    }

    @Override
    public void restore(final SessionInfo sessionInfo, final NodeId[] nodeIds, final boolean removeExisting) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                RepositoryServiceLogger.this.service.restore(RepositoryServiceLogger.unwrap(sessionInfo), nodeIds, removeExisting);
                return null;
            }
        }, "restore(SessionInfo, NodeId[], boolean)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), nodeIds, removeExisting});
    }

    @Override
    public Iterator<NodeId> merge(final SessionInfo sessionInfo, final NodeId nodeId, final String srcWorkspaceName, final boolean bestEffort) throws RepositoryException {
        return (Iterator)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.merge(RepositoryServiceLogger.unwrap(sessionInfo), nodeId, srcWorkspaceName, bestEffort);
            }
        }, "merge(SessionInfo, NodeId, String, boolean)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), nodeId, srcWorkspaceName, bestEffort});
    }

    @Override
    public Iterator<NodeId> merge(final SessionInfo sessionInfo, final NodeId nodeId, final String srcWorkspaceName, final boolean bestEffort, final boolean isShallow) throws RepositoryException {
        return (Iterator)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.merge(RepositoryServiceLogger.unwrap(sessionInfo), nodeId, srcWorkspaceName, bestEffort, isShallow);
            }
        }, "merge(SessionInfo, NodeId, String, boolean, boolean)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), nodeId, srcWorkspaceName, bestEffort});
    }

    @Override
    public void resolveMergeConflict(final SessionInfo sessionInfo, final NodeId nodeId, final NodeId[] mergeFailedIds, final NodeId[] predecessorIds) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                RepositoryServiceLogger.this.service.resolveMergeConflict(RepositoryServiceLogger.unwrap(sessionInfo), nodeId, mergeFailedIds, predecessorIds);
                return null;
            }
        }, "resolveMergeConflict(SessionInfo, NodeId, NodeId[], NodeId[])", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), nodeId, mergeFailedIds, predecessorIds});
    }

    @Override
    public void addVersionLabel(final SessionInfo sessionInfo, final NodeId versionHistoryId, final NodeId versionId, final Name label, final boolean moveLabel) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                RepositoryServiceLogger.this.service.addVersionLabel(RepositoryServiceLogger.unwrap(sessionInfo), versionHistoryId, versionId, label, moveLabel);
                return null;
            }
        }, "addVersionLabel(SessionInfo, NodeId, NodeId, Name, boolean)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), versionHistoryId, versionId, label, moveLabel});
    }

    @Override
    public void removeVersionLabel(final SessionInfo sessionInfo, final NodeId versionHistoryId, final NodeId versionId, final Name label) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                RepositoryServiceLogger.this.service.removeVersionLabel(RepositoryServiceLogger.unwrap(sessionInfo), versionHistoryId, versionId, label);
                return null;
            }
        }, "removeVersionLabel(SessionInfo, NodeId, NodeId, Name)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), versionHistoryId, versionId, label});
    }

    @Override
    public NodeId createActivity(final SessionInfo sessionInfo, final String title) throws UnsupportedRepositoryOperationException, RepositoryException {
        return (NodeId)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.createActivity(RepositoryServiceLogger.unwrap(sessionInfo), title);
            }
        }, "createActivity(SessionInfo, String)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), title});
    }

    @Override
    public void removeActivity(final SessionInfo sessionInfo, final NodeId activityId) throws UnsupportedRepositoryOperationException, RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                RepositoryServiceLogger.this.service.removeActivity(RepositoryServiceLogger.unwrap(sessionInfo), activityId);
                return null;
            }
        }, "removeActivity(SessionInfo, NodeId)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), activityId});
    }

    @Override
    public Iterator<NodeId> mergeActivity(final SessionInfo sessionInfo, final NodeId activityId) throws UnsupportedRepositoryOperationException, RepositoryException {
        return (Iterator)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.mergeActivity(RepositoryServiceLogger.unwrap(sessionInfo), activityId);
            }
        }, "mergeActivity(SessionInfo, NodeId)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), activityId});
    }

    @Override
    public NodeId createConfiguration(final SessionInfo sessionInfo, final NodeId nodeId) throws UnsupportedRepositoryOperationException, RepositoryException {
        return (NodeId)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.createConfiguration(RepositoryServiceLogger.unwrap(sessionInfo), nodeId);
            }
        }, "createConfiguration(SessionInfo, NodeId, NodeId)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), nodeId});
    }

    @Override
    public String[] getSupportedQueryLanguages(final SessionInfo sessionInfo) throws RepositoryException {
        return (String[])this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.getSupportedQueryLanguages(RepositoryServiceLogger.unwrap(sessionInfo));
            }
        }, "getSupportedQueryLanguages(SessionInfo)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo)});
    }

    @Override
    public String[] checkQueryStatement(final SessionInfo sessionInfo, final String statement, final String language, final Map<String, String> namespaces) throws RepositoryException {
        return (String[])this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.checkQueryStatement(RepositoryServiceLogger.unwrap(sessionInfo), statement, language, namespaces);
            }
        }, "checkQueryStatement(SessionInfo, String, String, Map)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), statement, language, namespaces});
    }

    @Override
    public QueryInfo executeQuery(final SessionInfo sessionInfo, final String statement, final String language, final Map<String, String> namespaces, final long limit, final long offset, final Map<String, QValue> values) throws RepositoryException {
        return (QueryInfo)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.executeQuery(RepositoryServiceLogger.unwrap(sessionInfo), statement, language, namespaces, limit, offset, values);
            }
        }, "executeQuery(SessionInfo, String, String, Map, long, long, Map)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), statement, language, namespaces, limit, offset, values});
    }

    @Override
    public EventFilter createEventFilter(final SessionInfo sessionInfo, final int eventTypes, final Path absPath, final boolean isDeep, final String[] uuid, final Name[] qnodeTypeName, final boolean noLocal) throws RepositoryException {
        return (EventFilter)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.createEventFilter(RepositoryServiceLogger.unwrap(sessionInfo), eventTypes, absPath, isDeep, uuid, qnodeTypeName, noLocal);
            }
        }, "createEventFilter(SessionInfo, int, Path, boolean, String[], Name[], boolean)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), eventTypes, absPath, isDeep, uuid, qnodeTypeName, noLocal});
    }

    @Override
    public Subscription createSubscription(final SessionInfo sessionInfo, final EventFilter[] filters) throws RepositoryException {
        return (Subscription)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.createSubscription(RepositoryServiceLogger.unwrap(sessionInfo), filters);
            }
        }, "createSubscription(SessionInfo, EventFilter[])", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), filters});
    }

    @Override
    public EventBundle[] getEvents(final Subscription subscription, final long timeout) throws RepositoryException, InterruptedException {
        String methodName = "getEvents(Subscription, long)";
        final Object[] args = new Object[]{subscription, timeout};
        final InterruptedException[] ex = new InterruptedException[1];
        EventBundle[] result = (EventBundle[])this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                try {
                    return RepositoryServiceLogger.this.service.getEvents(subscription, timeout);
                }
                catch (InterruptedException e) {
                    RepositoryServiceLogger.this.writer.error("getEvents(Subscription, long)", args, e);
                    ex[0] = e;
                    return null;
                }
            }
        }, "getEvents(Subscription, long)", args);
        if (ex[0] != null) {
            throw ex[0];
        }
        return result;
    }

    @Override
    public EventBundle getEvents(final SessionInfo sessionInfo, final EventFilter filter, final long after) throws RepositoryException, UnsupportedRepositoryOperationException {
        return (EventBundle)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.getEvents(sessionInfo, filter, after);
            }
        }, "getEvents(SessionInfo, EventFilter, long)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), filter, after});
    }

    @Override
    public void updateEventFilters(final Subscription subscription, final EventFilter[] eventFilters) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                RepositoryServiceLogger.this.service.updateEventFilters(subscription, eventFilters);
                return null;
            }
        }, "updateEventFilters(Subscription, EventFilter[])", new Object[]{subscription, eventFilters});
    }

    @Override
    public void dispose(final Subscription subscription) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                RepositoryServiceLogger.this.service.dispose(subscription);
                return null;
            }
        }, "dispose(Subscription)", new Object[0]);
    }

    @Override
    public Map<String, String> getRegisteredNamespaces(final SessionInfo sessionInfo) throws RepositoryException {
        return (Map)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.getRegisteredNamespaces(RepositoryServiceLogger.unwrap(sessionInfo));
            }
        }, "getRegisteredNamespaces(SessionInfo)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo)});
    }

    @Override
    public String getNamespaceURI(final SessionInfo sessionInfo, final String prefix) throws RepositoryException {
        return (String)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.getNamespaceURI(RepositoryServiceLogger.unwrap(sessionInfo), prefix);
            }
        }, "getNamespaceURI(SessionInfo, String)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), prefix});
    }

    @Override
    public String getNamespacePrefix(final SessionInfo sessionInfo, final String uri) throws RepositoryException {
        return (String)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.getNamespacePrefix(RepositoryServiceLogger.unwrap(sessionInfo), uri);
            }
        }, "getNamespacePrefix(SessionInfo, String)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), uri});
    }

    @Override
    public void registerNamespace(final SessionInfo sessionInfo, final String prefix, final String uri) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                RepositoryServiceLogger.this.service.registerNamespace(RepositoryServiceLogger.unwrap(sessionInfo), prefix, uri);
                return null;
            }
        }, "registerNamespace(SessionInfo, String, String)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), prefix, uri});
    }

    @Override
    public void unregisterNamespace(final SessionInfo sessionInfo, final String uri) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                RepositoryServiceLogger.this.service.unregisterNamespace(RepositoryServiceLogger.unwrap(sessionInfo), uri);
                return null;
            }
        }, "unregisterNamespace(SessionInfo, String)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), uri});
    }

    @Override
    public Iterator<QNodeTypeDefinition> getQNodeTypeDefinitions(final SessionInfo sessionInfo) throws RepositoryException {
        return (Iterator)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.getQNodeTypeDefinitions(RepositoryServiceLogger.unwrap(sessionInfo));
            }
        }, "getQNodeTypeDefinitions(SessionInfo)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo)});
    }

    @Override
    public Iterator<QNodeTypeDefinition> getQNodeTypeDefinitions(final SessionInfo sessionInfo, final Name[] nodetypeNames) throws RepositoryException {
        return (Iterator)this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return RepositoryServiceLogger.this.service.getQNodeTypeDefinitions(RepositoryServiceLogger.unwrap(sessionInfo), nodetypeNames);
            }
        }, "getQNodeTypeDefinitions(SessionInfo, Name[])", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), nodetypeNames});
    }

    @Override
    public void registerNodeTypes(final SessionInfo sessionInfo, final QNodeTypeDefinition[] nodeTypeDefinitions, final boolean allowUpdate) throws InvalidNodeTypeDefinitionException, NodeTypeExistsException, UnsupportedRepositoryOperationException, RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                RepositoryServiceLogger.this.service.registerNodeTypes(RepositoryServiceLogger.unwrap(sessionInfo), nodeTypeDefinitions, allowUpdate);
                return null;
            }
        }, "registerNodeTypes(SessionInfo, QNodeTypeDefinition[], boolean)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), nodeTypeDefinitions, allowUpdate});
    }

    @Override
    public void unregisterNodeTypes(final SessionInfo sessionInfo, final Name[] nodeTypeNames) throws UnsupportedRepositoryOperationException, NoSuchNodeTypeException, RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                RepositoryServiceLogger.this.service.unregisterNodeTypes(RepositoryServiceLogger.unwrap(sessionInfo), nodeTypeNames);
                return null;
            }
        }, "unregisterNodeTypes(SessionInfo, Name[])", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), nodeTypeNames});
    }

    @Override
    public void createWorkspace(final SessionInfo sessionInfo, final String name, final String srcWorkspaceName) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                RepositoryServiceLogger.this.service.createWorkspace(RepositoryServiceLogger.unwrap(sessionInfo), name, srcWorkspaceName);
                return null;
            }
        }, "createWorkspace(SessionInfo, String, String)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), name, srcWorkspaceName});
    }

    @Override
    public void deleteWorkspace(final SessionInfo sessionInfo, final String name) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                RepositoryServiceLogger.this.service.deleteWorkspace(RepositoryServiceLogger.unwrap(sessionInfo), name);
                return null;
            }
        }, "deleteWorkspace(SessionInfo, String, String)", new Object[]{RepositoryServiceLogger.unwrap(sessionInfo), name});
    }

    private static SessionInfo unwrap(SessionInfo sessionInfo) {
        if (sessionInfo instanceof SessionInfoLogger) {
            return ((SessionInfoLogger)sessionInfo).getSessionInfo();
        }
        return sessionInfo;
    }

    private static Batch unwrap(Batch batch) {
        if (batch instanceof BatchLogger) {
            return ((BatchLogger)batch).getBatch();
        }
        return batch;
    }
}

