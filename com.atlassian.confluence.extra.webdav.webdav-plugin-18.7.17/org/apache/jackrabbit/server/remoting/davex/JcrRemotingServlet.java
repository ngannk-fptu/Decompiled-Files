/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.server.remoting.davex;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import javax.jcr.Item;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.nodetype.NodeType;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.apache.jackrabbit.server.remoting.davex.BatchReadConfig;
import org.apache.jackrabbit.server.remoting.davex.DiffException;
import org.apache.jackrabbit.server.remoting.davex.DiffParser;
import org.apache.jackrabbit.server.remoting.davex.JsonDiffHandler;
import org.apache.jackrabbit.server.remoting.davex.JsonWriter;
import org.apache.jackrabbit.server.remoting.davex.ProtectedRemoveManager;
import org.apache.jackrabbit.server.util.RequestData;
import org.apache.jackrabbit.util.Text;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavLocatorFactory;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavSession;
import org.apache.jackrabbit.webdav.WebdavRequest;
import org.apache.jackrabbit.webdav.WebdavResponse;
import org.apache.jackrabbit.webdav.jcr.DavResourceFactoryImpl;
import org.apache.jackrabbit.webdav.jcr.JCRWebdavServerServlet;
import org.apache.jackrabbit.webdav.jcr.JcrDavException;
import org.apache.jackrabbit.webdav.jcr.JcrDavSession;
import org.apache.jackrabbit.webdav.jcr.transaction.TxLockManagerImpl;
import org.apache.jackrabbit.webdav.observation.SubscriptionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JcrRemotingServlet
extends JCRWebdavServerServlet {
    private static Logger log = LoggerFactory.getLogger(JcrRemotingServlet.class);
    public static final String INIT_PARAM_HOME = "home";
    public static final String INIT_PARAM_TMP_DIRECTORY = "temp-directory";
    public static final String ATTR_TMP_DIRECTORY = "remoting-servlet.tmpdir";
    public static final String INIT_PARAM_BATCHREAD_CONFIG = "batchread-config";
    public static final String INIT_PARAM_PROTECTED_HANDLERS_CONFIG = "protectedhandlers-config";
    private static final String PARAM_DIFF = ":diff";
    private static final String PARAM_COPY = ":copy";
    private static final String PARAM_CLONE = ":clone";
    private static final String PARAM_INCLUDE = ":include";
    private static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
    private BatchReadConfig brConfig;
    private ProtectedRemoveManager protectedRemoveManager;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void init() throws ServletException {
        super.init();
        this.brConfig = new BatchReadConfig();
        String brConfigParam = this.getServletConfig().getInitParameter(INIT_PARAM_BATCHREAD_CONFIG);
        if (brConfigParam == null) {
            log.debug("batchread-config missing -> initialize defaults.");
            this.brConfig.setDepth("nt:file", -1);
            this.brConfig.setDefaultDepth(5);
        } else {
            try {
                InputStream in = this.getServletContext().getResourceAsStream(brConfigParam);
                if (in != null) {
                    this.brConfig.load(in);
                }
            }
            catch (IOException e) {
                log.debug("Unable to build BatchReadConfig from " + brConfigParam + ".");
            }
        }
        String protectedHandlerConfig = this.getServletConfig().getInitParameter(INIT_PARAM_PROTECTED_HANDLERS_CONFIG);
        InputStream in = null;
        try {
            in = this.getServletContext().getResourceAsStream(protectedHandlerConfig);
            if (in != null) {
                this.protectedRemoveManager = new ProtectedRemoveManager();
                this.protectedRemoveManager.load(in);
            } else {
                this.protectedRemoveManager = new ProtectedRemoveManager(protectedHandlerConfig);
            }
        }
        catch (IOException e) {
            log.debug("Unable to create ProtectedRemoveManager from " + protectedHandlerConfig, (Throwable)e);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException e) {}
            }
        }
        File tmp = null;
        ServletConfig config = this.getServletConfig();
        String paramHome = config.getInitParameter(INIT_PARAM_HOME);
        String paramTemp = config.getInitParameter(INIT_PARAM_TMP_DIRECTORY);
        if (paramHome != null || paramTemp != null) {
            if (paramHome == null) {
                log.debug("Missing init-param home. Using default: 'jackrabbit'");
                paramHome = "jackrabbit";
            } else if (paramTemp == null) {
                log.debug("Missing init-param temp-directory. Using default: 'tmp'");
                paramTemp = "tmp";
            }
            tmp = new File(paramHome, paramTemp);
            try {
                tmp = tmp.getCanonicalFile();
                tmp.mkdirs();
                log.debug("  temp-directory = " + tmp.getPath());
            }
            catch (IOException e) {
                log.warn("Invalid temporary directory " + tmp.getPath() + ", using system default instead", (Throwable)e);
                tmp = null;
            }
        }
        this.getServletContext().setAttribute(ATTR_TMP_DIRECTORY, tmp);
        super.setLocatorFactory(new DavLocatorFactoryImpl(this.getResourcePathPrefix()));
    }

    protected String getResourcePathPrefix() {
        return this.getInitParameter("resource-path-prefix");
    }

    @Override
    public DavResourceFactory getResourceFactory() {
        return new ResourceFactoryImpl(this.txMgr, this.subscriptionMgr);
    }

    @Override
    protected void doGet(WebdavRequest webdavRequest, WebdavResponse webdavResponse, DavResource davResource) throws IOException, DavException {
        if (this.canHandle(2, webdavRequest, davResource)) {
            DavResourceLocator locator = davResource.getLocator();
            String path = locator.getRepositoryPath();
            Session session = JcrRemotingServlet.getRepositorySession(webdavRequest);
            try {
                Node node = session.getNode(path);
                int depth = ((WrappingLocator)locator).getDepth();
                webdavResponse.setContentType(CONTENT_TYPE_APPLICATION_JSON);
                webdavResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
                webdavResponse.setStatus(200);
                JsonWriter writer = new JsonWriter(webdavResponse.getWriter());
                String[] includes = webdavRequest.getParameterValues(PARAM_INCLUDE);
                if (includes == null) {
                    if (depth < -1) {
                        NodeType type = node.getPrimaryNodeType();
                        depth = this.brConfig.getDepth(type.getName());
                    }
                    writer.write(node, depth);
                }
                this.writeMultiple(writer, node, includes, depth);
            }
            catch (PathNotFoundException e) {
                throw new JcrDavException(new ItemNotFoundException("No node at " + path), 404);
            }
            catch (RepositoryException e) {
                log.debug(e.getMessage());
                throw new JcrDavException(e);
            }
        } else {
            super.doGet(webdavRequest, webdavResponse, davResource);
        }
    }

    private void writeMultiple(JsonWriter writer, Node node, String[] includes, int depth) throws RepositoryException, IOException {
        ArrayList<Node> nodes = new ArrayList<Node>();
        HashSet<String> alreadyAdded = new HashSet<String>();
        for (String include : includes) {
            try {
                Node n = include.startsWith("/") ? node.getSession().getNode(include) : node.getNode(include);
                String np = n.getPath();
                if (alreadyAdded.contains(np)) continue;
                nodes.add(n);
                alreadyAdded.add(np);
            }
            catch (PathNotFoundException pathNotFoundException) {
                // empty catch block
            }
        }
        writer.write(nodes, depth);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    protected void doPost(WebdavRequest webdavRequest, WebdavResponse webdavResponse, DavResource davResource) throws IOException, DavException {
        if (this.canHandle(4, webdavRequest, davResource)) {
            Session session = JcrRemotingServlet.getRepositorySession(webdavRequest);
            RequestData data = new RequestData(webdavRequest, JcrRemotingServlet.getTempDirectory(this.getServletContext()));
            String loc = null;
            try {
                String[] includes = null;
                String[] pValues = data.getParameterValues(PARAM_CLONE);
                if (pValues != null) {
                    loc = JcrRemotingServlet.clone(session, pValues, davResource.getLocator());
                } else {
                    String targetPath;
                    pValues = data.getParameterValues(PARAM_COPY);
                    if (pValues != null) {
                        loc = JcrRemotingServlet.copy(session, pValues, davResource.getLocator());
                    } else if (data.getParameterValues(PARAM_DIFF) != null) {
                        targetPath = davResource.getLocator().getRepositoryPath();
                        JcrRemotingServlet.processDiff(session, targetPath, data, this.protectedRemoveManager);
                    } else {
                        pValues = data.getParameterValues(PARAM_INCLUDE);
                        if (pValues != null && this.canHandle(2, webdavRequest, davResource)) {
                            includes = pValues;
                        } else {
                            targetPath = davResource.getLocator().getRepositoryPath();
                            loc = JcrRemotingServlet.modifyContent(session, targetPath, data, this.protectedRemoveManager);
                        }
                    }
                }
                if (loc == null) {
                    webdavResponse.setStatus(200);
                    if (includes == null) return;
                    webdavResponse.setContentType(CONTENT_TYPE_APPLICATION_JSON);
                    webdavResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    JsonWriter writer = new JsonWriter(webdavResponse.getWriter());
                    DavResourceLocator locator = davResource.getLocator();
                    String path = locator.getRepositoryPath();
                    Node node = session.getNode(path);
                    int depth = ((WrappingLocator)locator).getDepth();
                    this.writeMultiple(writer, node, includes, depth);
                    return;
                }
                webdavResponse.setHeader("Location", loc);
                webdavResponse.setStatus(201);
                return;
            }
            catch (RepositoryException e) {
                log.warn(e.getMessage(), (Throwable)e);
                throw new JcrDavException(e);
            }
            catch (DiffException e) {
                log.warn(e.getMessage());
                Throwable cause = e.getCause();
                if (!(cause instanceof RepositoryException)) throw new DavException(400, "Invalid diff format.");
                throw new JcrDavException((RepositoryException)cause);
            }
            finally {
                data.dispose();
            }
        } else {
            super.doPost(webdavRequest, webdavResponse, davResource);
        }
    }

    private boolean canHandle(int methodCode, WebdavRequest request, DavResource davResource) {
        DavResourceLocator locator = davResource.getLocator();
        switch (methodCode) {
            case 2: {
                return davResource.exists() && locator instanceof WrappingLocator && ((WrappingLocator)locator).isJsonRequest;
            }
            case 4: {
                String ct = request.getContentType();
                if (ct == null) {
                    return false;
                }
                int semicolon = ct.indexOf(59);
                if (semicolon >= 0) {
                    ct = ct.substring(0, semicolon);
                }
                return "multipart/form-data".equals(ct = ct.trim().toLowerCase(Locale.ENGLISH)) || "application/x-www-form-urlencoded".equals(ct);
            }
        }
        return false;
    }

    private static String clone(Session session, String[] cloneArgs, DavResourceLocator reqLocator) throws RepositoryException {
        Workspace wsp = session.getWorkspace();
        String destPath = null;
        for (String cloneArg : cloneArgs) {
            String[] args = cloneArg.split(",");
            if (args.length != 4) {
                throw new RepositoryException(":clone parameter must have a value consisting of the 4 args needed for a Workspace.clone() call.");
            }
            wsp.clone(args[0], args[1], args[2], Boolean.valueOf(args[3]));
            destPath = args[2];
        }
        return JcrRemotingServlet.buildLocationHref(session, destPath, reqLocator);
    }

    private static String copy(Session session, String[] copyArgs, DavResourceLocator reqLocator) throws RepositoryException {
        Workspace wsp = session.getWorkspace();
        String destPath = null;
        block4: for (String copyArg : copyArgs) {
            String[] args = copyArg.split(",");
            switch (args.length) {
                case 2: {
                    wsp.copy(args[0], args[1]);
                    destPath = args[1];
                    continue block4;
                }
                case 3: {
                    wsp.copy(args[0], args[1], args[2]);
                    destPath = args[2];
                    continue block4;
                }
                default: {
                    throw new RepositoryException(":copy parameter must have a value consisting of 2 jcr paths or workspaceName plus 2 jcr paths separated by ','.");
                }
            }
        }
        return JcrRemotingServlet.buildLocationHref(session, destPath, reqLocator);
    }

    private static String buildLocationHref(Session s, String destPath, DavResourceLocator reqLocator) throws RepositoryException {
        if (destPath != null) {
            NodeIterator it = s.getRootNode().getNodes(destPath.substring(1));
            Node n = null;
            while (it.hasNext()) {
                n = it.nextNode();
            }
            if (n != null) {
                DavResourceLocator loc = reqLocator.getFactory().createResourceLocator(reqLocator.getPrefix(), reqLocator.getWorkspacePath(), n.getPath(), false);
                return loc.getHref(true);
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void processDiff(Session session, String targetPath, RequestData data, ProtectedRemoveManager protectedRemoveManager) throws RepositoryException, DiffException, IOException {
        String[] diffs = data.getParameterValues(PARAM_DIFF);
        JsonDiffHandler handler = new JsonDiffHandler(session, targetPath, data, protectedRemoveManager);
        DiffParser parser = new DiffParser(handler);
        for (String diff : diffs) {
            boolean success = false;
            try {
                parser.parse(diff);
                session.save();
                success = true;
            }
            finally {
                if (!success) {
                    session.refresh(false);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static String modifyContent(Session session, String targetPath, RequestData data, ProtectedRemoveManager protectedRemoveManager) throws RepositoryException, DiffException {
        JsonDiffHandler dh = new JsonDiffHandler(session, targetPath, data, protectedRemoveManager);
        boolean success = false;
        try {
            Iterator<String> pNames = data.getParameterNames();
            while (pNames.hasNext()) {
                String paramName = pNames.next();
                String propPath = dh.getItemPath(paramName);
                String parentPath = Text.getRelativeParent(propPath, 1);
                if (!session.itemExists(parentPath) || !session.getItem(parentPath).isNode()) {
                    JcrRemotingServlet.createNode(session, parentPath, data);
                }
                if ("jcr:primaryType".equals(Text.getName(propPath))) continue;
                dh.setProperty(paramName, null);
            }
            session.save();
            success = true;
        }
        finally {
            if (!success) {
                session.refresh(false);
            }
        }
        return null;
    }

    private static void createNode(Session session, String nodePath, RequestData data) throws RepositoryException {
        String[] smgts;
        Node parent = session.getRootNode();
        for (String nodeName : smgts = Text.explode(nodePath, 47)) {
            String nPath;
            String ntName;
            parent = parent.hasNode(nodeName) ? parent.getNode(nodeName) : ((ntName = data.getParameter((nPath = parent.getPath() + "/" + nodeName) + "/" + "jcr:primaryType")) == null ? parent.addNode(nodeName) : parent.addNode(nodeName, ntName));
        }
    }

    private static Session getRepositorySession(WebdavRequest request) throws DavException {
        DavSession ds = request.getDavSession();
        return JcrDavSession.getRepositorySession(ds);
    }

    private static File getTempDirectory(ServletContext servletCtx) {
        return (File)servletCtx.getAttribute(ATTR_TMP_DIRECTORY);
    }

    private static class ResourceFactoryImpl
    extends DavResourceFactoryImpl {
        public ResourceFactoryImpl(TxLockManagerImpl txMgr, SubscriptionManager subsMgr) {
            super(txMgr, subsMgr);
        }

        @Override
        protected Item getItem(JcrDavSession sessionImpl, DavResourceLocator locator) throws PathNotFoundException, RepositoryException {
            if (locator instanceof WrappingLocator && ((WrappingLocator)locator).isJsonRequest) {
                Session s = sessionImpl.getRepositorySession();
                try {
                    if (s.itemExists(((WrappingLocator)locator).loc.getRepositoryPath())) {
                        ((WrappingLocator)locator).isJsonRequest = false;
                    }
                }
                catch (RepositoryException repositoryException) {
                    // empty catch block
                }
            }
            return super.getItem(sessionImpl, locator);
        }
    }

    private static class WrappingLocator
    implements DavResourceLocator {
        private final DavResourceLocator loc;
        private boolean isJsonRequest = true;
        private int depth = Integer.MIN_VALUE;
        private String repositoryPath;

        private WrappingLocator(DavResourceLocator loc) {
            this.loc = loc;
        }

        private void extract() {
            String rp = this.loc.getRepositoryPath();
            int pos = (rp = rp.substring(0, rp.lastIndexOf(46))).lastIndexOf(46);
            if (pos > -1) {
                String depthStr = rp.substring(pos + 1);
                try {
                    this.depth = Integer.parseInt(depthStr);
                    rp = rp.substring(0, pos);
                }
                catch (NumberFormatException numberFormatException) {
                    // empty catch block
                }
            }
            this.repositoryPath = rp;
        }

        private int getDepth() {
            if (this.isJsonRequest) {
                if (this.repositoryPath == null) {
                    this.extract();
                }
                return this.depth;
            }
            return Integer.MIN_VALUE;
        }

        @Override
        public String getPrefix() {
            return this.loc.getPrefix();
        }

        @Override
        public String getResourcePath() {
            return this.loc.getResourcePath();
        }

        @Override
        public String getWorkspacePath() {
            return this.loc.getWorkspacePath();
        }

        @Override
        public String getWorkspaceName() {
            return this.loc.getWorkspaceName();
        }

        @Override
        public boolean isSameWorkspace(DavResourceLocator davResourceLocator) {
            return this.loc.isSameWorkspace(davResourceLocator);
        }

        @Override
        public boolean isSameWorkspace(String string) {
            return this.loc.isSameWorkspace(string);
        }

        @Override
        public String getHref(boolean b) {
            return this.loc.getHref(b);
        }

        @Override
        public boolean isRootLocation() {
            return this.loc.isRootLocation();
        }

        @Override
        public DavLocatorFactory getFactory() {
            return this.loc.getFactory();
        }

        @Override
        public String getRepositoryPath() {
            if (this.isJsonRequest) {
                if (this.repositoryPath == null) {
                    this.extract();
                }
                return this.repositoryPath;
            }
            return this.loc.getRepositoryPath();
        }
    }

    private static class DavLocatorFactoryImpl
    extends org.apache.jackrabbit.webdav.jcr.DavLocatorFactoryImpl {
        public DavLocatorFactoryImpl(String s) {
            super(s);
        }

        @Override
        public DavResourceLocator createResourceLocator(String prefix, String href) {
            return this.createResourceLocator(prefix, href, false);
        }

        @Override
        public DavResourceLocator createResourceLocator(String prefix, String href, boolean forDestination) {
            DavResourceLocator loc = super.createResourceLocator(prefix, href);
            if (!forDestination && DavLocatorFactoryImpl.endsWithJson(href)) {
                loc = new WrappingLocator(super.createResourceLocator(prefix, href));
            }
            return loc;
        }

        @Override
        public DavResourceLocator createResourceLocator(String prefix, String workspacePath, String path, boolean isResourcePath) {
            DavResourceLocator loc = super.createResourceLocator(prefix, workspacePath, path, isResourcePath);
            if (isResourcePath && DavLocatorFactoryImpl.endsWithJson(path)) {
                loc = new WrappingLocator(loc);
            }
            return loc;
        }

        private static boolean endsWithJson(String s) {
            return s.endsWith(".json");
        }
    }
}

