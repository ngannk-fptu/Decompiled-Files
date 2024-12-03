/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr;

import java.io.IOException;
import java.io.InputStream;
import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.InvalidSerializedDataException;
import javax.jcr.ItemExistsException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.lock.LockException;
import javax.jcr.lock.LockManager;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.observation.ObservationManager;
import javax.jcr.query.QueryManager;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionManager;
import org.xml.sax.ContentHandler;

public interface Workspace {
    public static final String NAME_WORKSPACE_ROOT = "";
    public static final String PATH_WORKSPACE_ROOT = "/";
    public static final String NAME_SYSTEM_NODE = "{http://www.jcp.org/jcr/1.0}system";
    public static final String PATH_SYSTEM_NODE = "/{http://www.jcp.org/jcr/1.0}system";
    public static final String NAME_NODE_TYPES_NODE = "{http://www.jcp.org/jcr/1.0}nodeTypes";
    public static final String PATH_NODE_TYPES_NODE = "/{http://www.jcp.org/jcr/1.0}system/{http://www.jcp.org/jcr/1.0}nodeTypes";
    public static final String NAME_VERSION_STORAGE_NODE = "{http://www.jcp.org/jcr/1.0}versionStorage";
    public static final String PATH_VERSION_STORAGE_NODE = "/{http://www.jcp.org/jcr/1.0}system/{http://www.jcp.org/jcr/1.0}versionStorage";
    public static final String NAME_ACTIVITIES_NODE = "{http://www.jcp.org/jcr/1.0}activities";
    public static final String PATH_ACTIVITIES_NODE = "/{http://www.jcp.org/jcr/1.0}system/{http://www.jcp.org/jcr/1.0}activities";
    public static final String NAME_CONFIGURATIONS_NODE = "{http://www.jcp.org/jcr/1.0}configurations";
    public static final String PATH_CONFIGURATIONS_NODE = "/{http://www.jcp.org/jcr/1.0}system/{http://www.jcp.org/jcr/1.0}configurations";
    public static final String NAME_UNFILED_NODE = "{http://www.jcp.org/jcr/1.0}unfiled";
    public static final String PATH_UNFILED_NODE = "/{http://www.jcp.org/jcr/1.0}system/{http://www.jcp.org/jcr/1.0}unfiled";
    public static final String NAME_JCR_XMLTEXT = "{http://www.jcp.org/jcr/1.0}xmltext";
    public static final String NAME_JCR_XMLCHARACTERS = "{http://www.jcp.org/jcr/1.0}xmlcharacters";
    public static final String RELPATH_JCR_XMLCHARACTERS = "{http://www.jcp.org/jcr/1.0}xmltext/{http://www.jcp.org/jcr/1.0}xmlcharacters";

    public Session getSession();

    public String getName();

    public void copy(String var1, String var2) throws ConstraintViolationException, VersionException, AccessDeniedException, PathNotFoundException, ItemExistsException, LockException, RepositoryException;

    public void copy(String var1, String var2, String var3) throws NoSuchWorkspaceException, ConstraintViolationException, VersionException, AccessDeniedException, PathNotFoundException, ItemExistsException, LockException, RepositoryException;

    public void clone(String var1, String var2, String var3, boolean var4) throws NoSuchWorkspaceException, ConstraintViolationException, VersionException, AccessDeniedException, PathNotFoundException, ItemExistsException, LockException, RepositoryException;

    public void move(String var1, String var2) throws ConstraintViolationException, VersionException, AccessDeniedException, PathNotFoundException, ItemExistsException, LockException, RepositoryException;

    public void restore(Version[] var1, boolean var2) throws ItemExistsException, UnsupportedRepositoryOperationException, VersionException, LockException, InvalidItemStateException, RepositoryException;

    public LockManager getLockManager() throws UnsupportedRepositoryOperationException, RepositoryException;

    public QueryManager getQueryManager() throws RepositoryException;

    public NamespaceRegistry getNamespaceRegistry() throws RepositoryException;

    public NodeTypeManager getNodeTypeManager() throws RepositoryException;

    public ObservationManager getObservationManager() throws UnsupportedRepositoryOperationException, RepositoryException;

    public VersionManager getVersionManager() throws UnsupportedRepositoryOperationException, RepositoryException;

    public String[] getAccessibleWorkspaceNames() throws RepositoryException;

    public ContentHandler getImportContentHandler(String var1, int var2) throws PathNotFoundException, ConstraintViolationException, VersionException, LockException, AccessDeniedException, RepositoryException;

    public void importXML(String var1, InputStream var2, int var3) throws IOException, VersionException, PathNotFoundException, ItemExistsException, ConstraintViolationException, InvalidSerializedDataException, LockException, AccessDeniedException, RepositoryException;

    public void createWorkspace(String var1) throws AccessDeniedException, UnsupportedRepositoryOperationException, RepositoryException;

    public void createWorkspace(String var1, String var2) throws AccessDeniedException, UnsupportedRepositoryOperationException, NoSuchWorkspaceException, RepositoryException;

    public void deleteWorkspace(String var1) throws AccessDeniedException, UnsupportedRepositoryOperationException, NoSuchWorkspaceException, RepositoryException;
}

