/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import javax.jcr.AccessDeniedException;
import javax.jcr.Binary;
import javax.jcr.InvalidItemStateException;
import javax.jcr.InvalidLifecycleTransitionException;
import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.MergeException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.Lock;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.version.ActivityViolationException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionHistory;

public interface Node
extends Item {
    public static final String JCR_CONTENT = "{http://www.jcp.org/jcr/1.0}content";
    public static final String JCR_PROPERTY_DEFINITION = "{http://www.jcp.org/jcr/1.0}propertyDefinition";
    public static final String JCR_CHILD_NODE_DEFINITION = "{http://www.jcp.org/jcr/1.0}childNodeDefinition";
    public static final String JCR_ROOT_VERSION = "{http://www.jcp.org/jcr/1.0}rootVersion";
    public static final String JCR_VERSION_LABELS = "{http://www.jcp.org/jcr/1.0}versionLabels";
    public static final String JCR_FROZEN_NODE = "{http://www.jcp.org/jcr/1.0}frozenNode";

    public Node addNode(String var1) throws ItemExistsException, PathNotFoundException, VersionException, ConstraintViolationException, LockException, RepositoryException;

    public Node addNode(String var1, String var2) throws ItemExistsException, PathNotFoundException, NoSuchNodeTypeException, LockException, VersionException, ConstraintViolationException, RepositoryException;

    public void orderBefore(String var1, String var2) throws UnsupportedRepositoryOperationException, VersionException, ConstraintViolationException, ItemNotFoundException, LockException, RepositoryException;

    public Property setProperty(String var1, Value var2) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException;

    public Property setProperty(String var1, Value var2, int var3) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException;

    public Property setProperty(String var1, Value[] var2) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException;

    public Property setProperty(String var1, Value[] var2, int var3) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException;

    public Property setProperty(String var1, String[] var2) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException;

    public Property setProperty(String var1, String[] var2, int var3) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException;

    public Property setProperty(String var1, String var2) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException;

    public Property setProperty(String var1, String var2, int var3) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException;

    public Property setProperty(String var1, InputStream var2) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException;

    public Property setProperty(String var1, Binary var2) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException;

    public Property setProperty(String var1, boolean var2) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException;

    public Property setProperty(String var1, double var2) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException;

    public Property setProperty(String var1, BigDecimal var2) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException;

    public Property setProperty(String var1, long var2) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException;

    public Property setProperty(String var1, Calendar var2) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException;

    public Property setProperty(String var1, Node var2) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException;

    public Node getNode(String var1) throws PathNotFoundException, RepositoryException;

    public NodeIterator getNodes() throws RepositoryException;

    public NodeIterator getNodes(String var1) throws RepositoryException;

    public NodeIterator getNodes(String[] var1) throws RepositoryException;

    public Property getProperty(String var1) throws PathNotFoundException, RepositoryException;

    public PropertyIterator getProperties() throws RepositoryException;

    public PropertyIterator getProperties(String var1) throws RepositoryException;

    public PropertyIterator getProperties(String[] var1) throws RepositoryException;

    public Item getPrimaryItem() throws ItemNotFoundException, RepositoryException;

    public String getUUID() throws UnsupportedRepositoryOperationException, RepositoryException;

    public String getIdentifier() throws RepositoryException;

    public int getIndex() throws RepositoryException;

    public PropertyIterator getReferences() throws RepositoryException;

    public PropertyIterator getReferences(String var1) throws RepositoryException;

    public PropertyIterator getWeakReferences() throws RepositoryException;

    public PropertyIterator getWeakReferences(String var1) throws RepositoryException;

    public boolean hasNode(String var1) throws RepositoryException;

    public boolean hasProperty(String var1) throws RepositoryException;

    public boolean hasNodes() throws RepositoryException;

    public boolean hasProperties() throws RepositoryException;

    public NodeType getPrimaryNodeType() throws RepositoryException;

    public NodeType[] getMixinNodeTypes() throws RepositoryException;

    public boolean isNodeType(String var1) throws RepositoryException;

    public void setPrimaryType(String var1) throws NoSuchNodeTypeException, VersionException, ConstraintViolationException, LockException, RepositoryException;

    public void addMixin(String var1) throws NoSuchNodeTypeException, VersionException, ConstraintViolationException, LockException, RepositoryException;

    public void removeMixin(String var1) throws NoSuchNodeTypeException, VersionException, ConstraintViolationException, LockException, RepositoryException;

    public boolean canAddMixin(String var1) throws NoSuchNodeTypeException, RepositoryException;

    public NodeDefinition getDefinition() throws RepositoryException;

    public Version checkin() throws VersionException, UnsupportedRepositoryOperationException, InvalidItemStateException, LockException, RepositoryException;

    public void checkout() throws UnsupportedRepositoryOperationException, LockException, ActivityViolationException, RepositoryException;

    public void doneMerge(Version var1) throws VersionException, InvalidItemStateException, UnsupportedRepositoryOperationException, RepositoryException;

    public void cancelMerge(Version var1) throws VersionException, InvalidItemStateException, UnsupportedRepositoryOperationException, RepositoryException;

    public void update(String var1) throws NoSuchWorkspaceException, AccessDeniedException, LockException, InvalidItemStateException, RepositoryException;

    public NodeIterator merge(String var1, boolean var2) throws NoSuchWorkspaceException, AccessDeniedException, MergeException, LockException, InvalidItemStateException, RepositoryException;

    public String getCorrespondingNodePath(String var1) throws ItemNotFoundException, NoSuchWorkspaceException, AccessDeniedException, RepositoryException;

    public NodeIterator getSharedSet() throws RepositoryException;

    public void removeSharedSet() throws VersionException, LockException, ConstraintViolationException, RepositoryException;

    public void removeShare() throws VersionException, LockException, ConstraintViolationException, RepositoryException;

    public boolean isCheckedOut() throws RepositoryException;

    public void restore(String var1, boolean var2) throws VersionException, ItemExistsException, UnsupportedRepositoryOperationException, LockException, InvalidItemStateException, RepositoryException;

    public void restore(Version var1, boolean var2) throws VersionException, ItemExistsException, InvalidItemStateException, UnsupportedRepositoryOperationException, LockException, RepositoryException;

    public void restore(Version var1, String var2, boolean var3) throws PathNotFoundException, ItemExistsException, VersionException, ConstraintViolationException, UnsupportedRepositoryOperationException, LockException, InvalidItemStateException, RepositoryException;

    public void restoreByLabel(String var1, boolean var2) throws VersionException, ItemExistsException, UnsupportedRepositoryOperationException, LockException, InvalidItemStateException, RepositoryException;

    public VersionHistory getVersionHistory() throws UnsupportedRepositoryOperationException, RepositoryException;

    public Version getBaseVersion() throws UnsupportedRepositoryOperationException, RepositoryException;

    public Lock lock(boolean var1, boolean var2) throws UnsupportedRepositoryOperationException, LockException, AccessDeniedException, InvalidItemStateException, RepositoryException;

    public Lock getLock() throws UnsupportedRepositoryOperationException, LockException, AccessDeniedException, RepositoryException;

    public void unlock() throws UnsupportedRepositoryOperationException, LockException, AccessDeniedException, InvalidItemStateException, RepositoryException;

    public boolean holdsLock() throws RepositoryException;

    public boolean isLocked() throws RepositoryException;

    public void followLifecycleTransition(String var1) throws UnsupportedRepositoryOperationException, InvalidLifecycleTransitionException, RepositoryException;

    public String[] getAllowedLifecycleTransistions() throws UnsupportedRepositoryOperationException, RepositoryException;
}

