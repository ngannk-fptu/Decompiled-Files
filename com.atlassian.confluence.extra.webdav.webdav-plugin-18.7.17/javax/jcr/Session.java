/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessControlException;
import javax.jcr.AccessDeniedException;
import javax.jcr.Credentials;
import javax.jcr.InvalidItemStateException;
import javax.jcr.InvalidSerializedDataException;
import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.LoginException;
import javax.jcr.NamespaceException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.ValueFactory;
import javax.jcr.Workspace;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.retention.RetentionManager;
import javax.jcr.security.AccessControlManager;
import javax.jcr.version.VersionException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public interface Session {
    public static final String ACTION_READ = "read";
    public static final String ACTION_ADD_NODE = "add_node";
    public static final String ACTION_SET_PROPERTY = "set_property";
    public static final String ACTION_REMOVE = "remove";

    public Repository getRepository();

    public String getUserID();

    public String[] getAttributeNames();

    public Object getAttribute(String var1);

    public Workspace getWorkspace();

    public Node getRootNode() throws RepositoryException;

    public Session impersonate(Credentials var1) throws LoginException, RepositoryException;

    public Node getNodeByUUID(String var1) throws ItemNotFoundException, RepositoryException;

    public Node getNodeByIdentifier(String var1) throws ItemNotFoundException, RepositoryException;

    public Item getItem(String var1) throws PathNotFoundException, RepositoryException;

    public Node getNode(String var1) throws PathNotFoundException, RepositoryException;

    public Property getProperty(String var1) throws PathNotFoundException, RepositoryException;

    public boolean itemExists(String var1) throws RepositoryException;

    public boolean nodeExists(String var1) throws RepositoryException;

    public boolean propertyExists(String var1) throws RepositoryException;

    public void move(String var1, String var2) throws ItemExistsException, PathNotFoundException, VersionException, ConstraintViolationException, LockException, RepositoryException;

    public void removeItem(String var1) throws VersionException, LockException, ConstraintViolationException, AccessDeniedException, RepositoryException;

    public void save() throws AccessDeniedException, ItemExistsException, ReferentialIntegrityException, ConstraintViolationException, InvalidItemStateException, VersionException, LockException, NoSuchNodeTypeException, RepositoryException;

    public void refresh(boolean var1) throws RepositoryException;

    public boolean hasPendingChanges() throws RepositoryException;

    public ValueFactory getValueFactory() throws UnsupportedRepositoryOperationException, RepositoryException;

    public boolean hasPermission(String var1, String var2) throws RepositoryException;

    public void checkPermission(String var1, String var2) throws AccessControlException, RepositoryException;

    public boolean hasCapability(String var1, Object var2, Object[] var3) throws RepositoryException;

    public ContentHandler getImportContentHandler(String var1, int var2) throws PathNotFoundException, ConstraintViolationException, VersionException, LockException, RepositoryException;

    public void importXML(String var1, InputStream var2, int var3) throws IOException, PathNotFoundException, ItemExistsException, ConstraintViolationException, VersionException, InvalidSerializedDataException, LockException, RepositoryException;

    public void exportSystemView(String var1, ContentHandler var2, boolean var3, boolean var4) throws PathNotFoundException, SAXException, RepositoryException;

    public void exportSystemView(String var1, OutputStream var2, boolean var3, boolean var4) throws IOException, PathNotFoundException, RepositoryException;

    public void exportDocumentView(String var1, ContentHandler var2, boolean var3, boolean var4) throws PathNotFoundException, SAXException, RepositoryException;

    public void exportDocumentView(String var1, OutputStream var2, boolean var3, boolean var4) throws IOException, PathNotFoundException, RepositoryException;

    public void setNamespacePrefix(String var1, String var2) throws NamespaceException, RepositoryException;

    public String[] getNamespacePrefixes() throws RepositoryException;

    public String getNamespaceURI(String var1) throws NamespaceException, RepositoryException;

    public String getNamespacePrefix(String var1) throws NamespaceException, RepositoryException;

    public void logout();

    public boolean isLive();

    public void addLockToken(String var1);

    public String[] getLockTokens();

    public void removeLockToken(String var1);

    public AccessControlManager getAccessControlManager() throws UnsupportedRepositoryOperationException, RepositoryException;

    public RetentionManager getRetentionManager() throws UnsupportedRepositoryOperationException, RepositoryException;
}

