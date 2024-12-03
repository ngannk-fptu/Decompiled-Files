/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons;

import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.jcr.AccessDeniedException;
import javax.jcr.Credentials;
import javax.jcr.GuestCredentials;
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
import javax.jcr.SimpleCredentials;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.InvalidNodeTypeDefinitionException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeTypeExistsException;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.version.VersionException;
import org.apache.jackrabbit.commons.cnd.CompactNodeTypeDefReader;
import org.apache.jackrabbit.commons.cnd.ParseException;
import org.apache.jackrabbit.spi.Batch;
import org.apache.jackrabbit.spi.EventBundle;
import org.apache.jackrabbit.spi.EventFilter;
import org.apache.jackrabbit.spi.IdFactory;
import org.apache.jackrabbit.spi.ItemId;
import org.apache.jackrabbit.spi.LockInfo;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NameFactory;
import org.apache.jackrabbit.spi.NodeId;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.PathFactory;
import org.apache.jackrabbit.spi.PropertyId;
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
import org.apache.jackrabbit.spi.commons.SessionInfoImpl;
import org.apache.jackrabbit.spi.commons.identifier.IdFactoryImpl;
import org.apache.jackrabbit.spi.commons.name.NameFactoryImpl;
import org.apache.jackrabbit.spi.commons.name.PathFactoryImpl;
import org.apache.jackrabbit.spi.commons.namespace.NamespaceMapping;
import org.apache.jackrabbit.spi.commons.nodetype.NodeTypeStorage;
import org.apache.jackrabbit.spi.commons.nodetype.NodeTypeStorageImpl;
import org.apache.jackrabbit.spi.commons.nodetype.QDefinitionBuilderFactory;
import org.apache.jackrabbit.spi.commons.value.QValueFactoryImpl;

public abstract class AbstractRepositoryService
implements RepositoryService {
    protected final Map<String, QValue[]> descriptors = new HashMap<String, QValue[]>();
    protected final NamespaceMapping namespaces = new NamespaceMapping();
    protected final NodeTypeStorage nodeTypeDefs = new NodeTypeStorageImpl();
    protected QNodeDefinition rootNodeDefinition;

    @Override
    public IdFactory getIdFactory() throws RepositoryException {
        return IdFactoryImpl.getInstance();
    }

    @Override
    public NameFactory getNameFactory() throws RepositoryException {
        return NameFactoryImpl.getInstance();
    }

    @Override
    public PathFactory getPathFactory() throws RepositoryException {
        return PathFactoryImpl.getInstance();
    }

    @Override
    public QValueFactory getQValueFactory() throws RepositoryException {
        return QValueFactoryImpl.getInstance();
    }

    protected AbstractRepositoryService() throws RepositoryException {
        QValueFactory qvf = QValueFactoryImpl.getInstance();
        QValue[] vFalse = new QValue[]{qvf.create(false)};
        this.descriptors.put("write.supported", vFalse);
        this.descriptors.put("identifier.stability", new QValue[]{qvf.create("identifier.stability.save.duration", 1)});
        this.descriptors.put("option.xml.import.supported", vFalse);
        this.descriptors.put("option.xml.export.supported", vFalse);
        this.descriptors.put("option.unfiled.content.supported", vFalse);
        this.descriptors.put("option.versioning.supported", vFalse);
        this.descriptors.put("option.simple.versioning.supported", vFalse);
        this.descriptors.put("option.access.control.supported", vFalse);
        this.descriptors.put("option.locking.supported", vFalse);
        this.descriptors.put("option.observation.supported", vFalse);
        this.descriptors.put("option.journaled.observation.supported", vFalse);
        this.descriptors.put("option.retention.supported", vFalse);
        this.descriptors.put("option.lifecycle.supported", vFalse);
        this.descriptors.put("option.transactions.supported", vFalse);
        this.descriptors.put("option.workspace.management.supported", vFalse);
        this.descriptors.put("option.update.primary.node.type.supported", vFalse);
        this.descriptors.put("option.update.mixin.node.types.supported", vFalse);
        this.descriptors.put("option.shareable.nodes.supported", vFalse);
        this.descriptors.put("option.node.type.management.supported", vFalse);
        this.descriptors.put("query.languages", new QValue[0]);
        this.descriptors.put("query.stored.queries.supported", vFalse);
        this.descriptors.put("query.full.text.search.supported", vFalse);
        this.descriptors.put("query.joins", new QValue[]{qvf.create("query.joins.none", 1)});
        this.descriptors.putAll(this.descriptors);
    }

    public AbstractRepositoryService(Map<String, QValue[]> descriptors, Map<String, String> namespaces, QNodeTypeDefinition[] nodeTypeDefs) throws RepositoryException {
        this();
        this.descriptors.putAll(descriptors);
        for (Map.Entry<String, String> entry : namespaces.entrySet()) {
            this.namespaces.setMapping(entry.getKey(), entry.getValue());
        }
        this.nodeTypeDefs.registerNodeTypes(nodeTypeDefs, true);
    }

    public AbstractRepositoryService(Map<String, QValue[]> descriptors, Map<String, String> namespaces, Reader cnd) throws RepositoryException {
        this();
        this.descriptors.putAll(descriptors);
        for (Map.Entry<String, String> entry : namespaces.entrySet()) {
            this.namespaces.setMapping(entry.getKey(), entry.getValue());
        }
        try {
            CompactNodeTypeDefReader<QNodeTypeDefinition, NamespaceMapping> reader = new CompactNodeTypeDefReader<QNodeTypeDefinition, NamespaceMapping>(cnd, "", this.namespaces, new QDefinitionBuilderFactory());
            List<QNodeTypeDefinition> ntds = reader.getNodeTypeDefinitions();
            this.nodeTypeDefs.registerNodeTypes(ntds.toArray(new QNodeTypeDefinition[ntds.size()]), true);
        }
        catch (ParseException e) {
            throw new RepositoryException("Error reading node type definitions", e);
        }
    }

    protected abstract QNodeDefinition createRootNodeDefinition(SessionInfo var1) throws RepositoryException;

    protected void checkCredentials(Credentials credentials, String workspaceName) throws LoginException {
    }

    protected void checkWorkspace(String workspaceName) throws NoSuchWorkspaceException {
    }

    protected SessionInfo createSessionInfo(Credentials credentials, String workspaceName) throws RepositoryException {
        String userId = null;
        if (credentials instanceof SimpleCredentials) {
            userId = ((SimpleCredentials)credentials).getUserID();
        } else if (credentials instanceof GuestCredentials) {
            userId = "anonymous";
        }
        SessionInfoImpl s = new SessionInfoImpl();
        s.setUserID(userId);
        s.setWorkspacename(workspaceName);
        return s;
    }

    protected SessionInfo createSessionInfo(SessionInfo sessionInfo, String workspaceName) throws RepositoryException {
        String userId = sessionInfo.getUserID();
        SessionInfoImpl s = new SessionInfoImpl();
        s.setUserID(userId);
        s.setWorkspacename(workspaceName);
        return s;
    }

    protected void checkSessionInfo(SessionInfo sessionInfo) throws RepositoryException {
        if (sessionInfo instanceof SessionInfoImpl) {
            return;
        }
        throw new RepositoryException("SessionInfo not of type " + SessionInfoImpl.class.getName());
    }

    @Override
    public Map<String, QValue[]> getRepositoryDescriptors() throws RepositoryException {
        return this.descriptors;
    }

    @Override
    public SessionInfo obtain(Credentials credentials, String workspaceName) throws LoginException, NoSuchWorkspaceException, RepositoryException {
        this.checkCredentials(credentials, workspaceName);
        this.checkWorkspace(workspaceName);
        return this.createSessionInfo(credentials, workspaceName);
    }

    @Override
    public SessionInfo obtain(SessionInfo sessionInfo, String workspaceName) throws LoginException, NoSuchWorkspaceException, RepositoryException {
        return this.createSessionInfo(sessionInfo, workspaceName);
    }

    @Override
    public SessionInfo impersonate(SessionInfo sessionInfo, Credentials credentials) throws LoginException, RepositoryException {
        return this.obtain(credentials, sessionInfo.getWorkspaceName());
    }

    @Override
    public void dispose(SessionInfo sessionInfo) throws RepositoryException {
    }

    @Override
    public Iterator<QNodeTypeDefinition> getQNodeTypeDefinitions(SessionInfo sessionInfo) throws RepositoryException {
        this.checkSessionInfo(sessionInfo);
        return this.nodeTypeDefs.getAllDefinitions();
    }

    @Override
    public Iterator<QNodeTypeDefinition> getQNodeTypeDefinitions(SessionInfo sessionInfo, Name[] nodetypeNames) throws RepositoryException {
        this.checkSessionInfo(sessionInfo);
        return this.nodeTypeDefs.getDefinitions(nodetypeNames);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public QNodeDefinition getNodeDefinition(SessionInfo sessionInfo, NodeId nodeId) throws RepositoryException {
        this.checkSessionInfo(sessionInfo);
        if (nodeId.getUniqueID() == null && nodeId.getPath().denotesRoot()) {
            AbstractRepositoryService abstractRepositoryService = this;
            synchronized (abstractRepositoryService) {
                if (this.rootNodeDefinition == null) {
                    this.rootNodeDefinition = this.createRootNodeDefinition(sessionInfo);
                }
                return this.rootNodeDefinition;
            }
        }
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public QPropertyDefinition getPropertyDefinition(SessionInfo sessionInfo, PropertyId propertyId) throws RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public void registerNodeTypes(SessionInfo sessionInfo, QNodeTypeDefinition[] nodeTypeDefinitions, boolean allowUpdate) throws InvalidNodeTypeDefinitionException, NodeTypeExistsException, UnsupportedRepositoryOperationException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public void unregisterNodeTypes(SessionInfo sessionInfo, Name[] nodeTypeNames) throws UnsupportedRepositoryOperationException, NoSuchNodeTypeException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public Map<String, String> getRegisteredNamespaces(SessionInfo sessionInfo) throws RepositoryException {
        this.checkSessionInfo(sessionInfo);
        return this.namespaces.getPrefixToURIMapping();
    }

    @Override
    public String getNamespaceURI(SessionInfo sessionInfo, String prefix) throws NamespaceException, RepositoryException {
        this.checkSessionInfo(sessionInfo);
        return this.namespaces.getURI(prefix);
    }

    @Override
    public String getNamespacePrefix(SessionInfo sessionInfo, String uri) throws NamespaceException, RepositoryException {
        this.checkSessionInfo(sessionInfo);
        return this.namespaces.getPrefix(uri);
    }

    @Override
    public Batch createBatch(SessionInfo sessionInfo, ItemId itemId) throws RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public void submit(Batch batch) throws PathNotFoundException, ItemNotFoundException, NoSuchNodeTypeException, ValueFormatException, VersionException, LockException, ConstraintViolationException, AccessDeniedException, UnsupportedRepositoryOperationException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public Tree createTree(SessionInfo sessionInfo, Batch batch, Name nodeName, Name primaryTypeName, String uniqueId) throws RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public void importXml(SessionInfo sessionInfo, NodeId parentId, InputStream xmlStream, int uuidBehaviour) throws ItemExistsException, PathNotFoundException, VersionException, ConstraintViolationException, LockException, AccessDeniedException, UnsupportedRepositoryOperationException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public void move(SessionInfo sessionInfo, NodeId srcNodeId, NodeId destParentNodeId, Name destName) throws ItemExistsException, PathNotFoundException, VersionException, ConstraintViolationException, LockException, AccessDeniedException, UnsupportedRepositoryOperationException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public void copy(SessionInfo sessionInfo, String srcWorkspaceName, NodeId srcNodeId, NodeId destParentNodeId, Name destName) throws NoSuchWorkspaceException, ConstraintViolationException, VersionException, AccessDeniedException, PathNotFoundException, ItemExistsException, LockException, UnsupportedRepositoryOperationException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public void update(SessionInfo sessionInfo, NodeId nodeId, String srcWorkspaceName) throws NoSuchWorkspaceException, AccessDeniedException, LockException, InvalidItemStateException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public void clone(SessionInfo sessionInfo, String srcWorkspaceName, NodeId srcNodeId, NodeId destParentNodeId, Name destName, boolean removeExisting) throws NoSuchWorkspaceException, ConstraintViolationException, VersionException, AccessDeniedException, PathNotFoundException, ItemExistsException, LockException, UnsupportedRepositoryOperationException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public LockInfo lock(SessionInfo sessionInfo, NodeId nodeId, boolean deep, boolean sessionScoped) throws UnsupportedRepositoryOperationException, LockException, AccessDeniedException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public LockInfo lock(SessionInfo sessionInfo, NodeId nodeId, boolean deep, boolean sessionScoped, long timeoutHint, String ownerHint) throws UnsupportedRepositoryOperationException, LockException, AccessDeniedException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public LockInfo getLockInfo(SessionInfo sessionInfo, NodeId nodeId) throws AccessDeniedException, RepositoryException {
        return null;
    }

    @Override
    public void refreshLock(SessionInfo sessionInfo, NodeId nodeId) throws UnsupportedRepositoryOperationException, LockException, AccessDeniedException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public void unlock(SessionInfo sessionInfo, NodeId nodeId) throws UnsupportedRepositoryOperationException, LockException, AccessDeniedException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public NodeId checkin(SessionInfo sessionInfo, NodeId nodeId) throws VersionException, UnsupportedRepositoryOperationException, InvalidItemStateException, LockException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public void checkout(SessionInfo sessionInfo, NodeId nodeId) throws UnsupportedRepositoryOperationException, LockException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public void checkout(SessionInfo sessionInfo, NodeId nodeId, NodeId activityId) throws UnsupportedRepositoryOperationException, LockException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public NodeId checkpoint(SessionInfo sessionInfo, NodeId nodeId) throws UnsupportedRepositoryOperationException, LockException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public NodeId checkpoint(SessionInfo sessionInfo, NodeId nodeId, NodeId activityId) throws UnsupportedRepositoryOperationException, LockException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public void removeVersion(SessionInfo sessionInfo, NodeId versionHistoryId, NodeId versionId) throws ReferentialIntegrityException, AccessDeniedException, UnsupportedRepositoryOperationException, VersionException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public void restore(SessionInfo sessionInfo, NodeId nodeId, NodeId versionId, boolean removeExisting) throws VersionException, PathNotFoundException, ItemExistsException, UnsupportedRepositoryOperationException, LockException, InvalidItemStateException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public void restore(SessionInfo sessionInfo, NodeId[] versionIds, boolean removeExisting) throws ItemExistsException, UnsupportedRepositoryOperationException, VersionException, LockException, InvalidItemStateException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public Iterator<NodeId> merge(SessionInfo sessionInfo, NodeId nodeId, String srcWorkspaceName, boolean bestEffort) throws NoSuchWorkspaceException, AccessDeniedException, MergeException, LockException, InvalidItemStateException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public Iterator<NodeId> merge(SessionInfo sessionInfo, NodeId nodeId, String srcWorkspaceName, boolean bestEffort, boolean isShallow) throws NoSuchWorkspaceException, AccessDeniedException, MergeException, LockException, InvalidItemStateException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public void resolveMergeConflict(SessionInfo sessionInfo, NodeId nodeId, NodeId[] mergeFailedIds, NodeId[] predecessorIds) throws VersionException, InvalidItemStateException, UnsupportedRepositoryOperationException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public void addVersionLabel(SessionInfo sessionInfo, NodeId versionHistoryId, NodeId versionId, Name label, boolean moveLabel) throws VersionException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public void removeVersionLabel(SessionInfo sessionInfo, NodeId versionHistoryId, NodeId versionId, Name label) throws VersionException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public NodeId createActivity(SessionInfo sessionInfo, String title) throws UnsupportedRepositoryOperationException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public void removeActivity(SessionInfo sessionInfo, NodeId activityId) throws UnsupportedRepositoryOperationException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public Iterator<NodeId> mergeActivity(SessionInfo sessionInfo, NodeId activityId) throws UnsupportedRepositoryOperationException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public NodeId createConfiguration(SessionInfo sessionInfo, NodeId nodeId) throws UnsupportedRepositoryOperationException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public EventFilter createEventFilter(SessionInfo sessionInfo, int eventTypes, Path absPath, boolean isDeep, String[] uuid, Name[] nodeTypeName, boolean noLocal) throws UnsupportedRepositoryOperationException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public Subscription createSubscription(SessionInfo sessionInfo, EventFilter[] filters) throws UnsupportedRepositoryOperationException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public void updateEventFilters(Subscription subscription, EventFilter[] filters) throws RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public EventBundle[] getEvents(Subscription subscription, long timeout) throws RepositoryException, InterruptedException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public EventBundle getEvents(SessionInfo sessionInfo, EventFilter filter, long after) throws RepositoryException, UnsupportedRepositoryOperationException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public void dispose(Subscription subscription) throws RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public void registerNamespace(SessionInfo sessionInfo, String prefix, String uri) throws NamespaceException, UnsupportedRepositoryOperationException, AccessDeniedException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public void unregisterNamespace(SessionInfo sessionInfo, String uri) throws NamespaceException, UnsupportedRepositoryOperationException, AccessDeniedException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public void createWorkspace(SessionInfo sessionInfo, String name, String srcWorkspaceName) throws AccessDeniedException, UnsupportedRepositoryOperationException, NoSuchWorkspaceException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public void deleteWorkspace(SessionInfo sessionInfo, String name) throws AccessDeniedException, UnsupportedRepositoryOperationException, NoSuchWorkspaceException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public String[] getSupportedQueryLanguages(SessionInfo sessionInfo) throws RepositoryException {
        this.checkSessionInfo(sessionInfo);
        return new String[0];
    }

    @Override
    public String[] checkQueryStatement(SessionInfo sessionInfo, String statement, String language, Map<String, String> namespaces) throws InvalidQueryException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }

    @Override
    public QueryInfo executeQuery(SessionInfo sessionInfo, String statement, String language, Map<String, String> namespaces, long limit, long offset, Map<String, QValue> values) throws RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }
}

