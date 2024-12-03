/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.DispatcherType
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.tomcat.util.buf.HexUtils
 *  org.apache.tomcat.util.http.ConcurrentDateFormat
 *  org.apache.tomcat.util.http.FastHttpDateFormat
 *  org.apache.tomcat.util.http.RequestUtil
 *  org.apache.tomcat.util.security.ConcurrentMessageDigest
 */
package org.apache.catalina.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.catalina.WebResource;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.util.DOMWriter;
import org.apache.catalina.util.XMLWriter;
import org.apache.tomcat.util.buf.HexUtils;
import org.apache.tomcat.util.http.ConcurrentDateFormat;
import org.apache.tomcat.util.http.FastHttpDateFormat;
import org.apache.tomcat.util.http.RequestUtil;
import org.apache.tomcat.util.security.ConcurrentMessageDigest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class WebdavServlet
extends DefaultServlet {
    private static final long serialVersionUID = 1L;
    private static final String METHOD_PROPFIND = "PROPFIND";
    private static final String METHOD_PROPPATCH = "PROPPATCH";
    private static final String METHOD_MKCOL = "MKCOL";
    private static final String METHOD_COPY = "COPY";
    private static final String METHOD_MOVE = "MOVE";
    private static final String METHOD_LOCK = "LOCK";
    private static final String METHOD_UNLOCK = "UNLOCK";
    private static final int FIND_BY_PROPERTY = 0;
    private static final int FIND_ALL_PROP = 1;
    private static final int FIND_PROPERTY_NAMES = 2;
    private static final int LOCK_CREATION = 0;
    private static final int LOCK_REFRESH = 1;
    private static final int DEFAULT_TIMEOUT = 3600;
    private static final int MAX_TIMEOUT = 604800;
    protected static final String DEFAULT_NAMESPACE = "DAV:";
    protected static final ConcurrentDateFormat creationDateFormat = new ConcurrentDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US, TimeZone.getTimeZone("GMT"));
    private final Map<String, LockInfo> resourceLocks = new ConcurrentHashMap<String, LockInfo>();
    private final Map<String, List<String>> lockNullResources = new ConcurrentHashMap<String, List<String>>();
    private final List<LockInfo> collectionLocks = Collections.synchronizedList(new ArrayList());
    private String secret = "catalina";
    private int maxDepth = 3;
    private boolean allowSpecialPaths = false;

    @Override
    public void init() throws ServletException {
        super.init();
        if (this.getServletConfig().getInitParameter("secret") != null) {
            this.secret = this.getServletConfig().getInitParameter("secret");
        }
        if (this.getServletConfig().getInitParameter("maxDepth") != null) {
            this.maxDepth = Integer.parseInt(this.getServletConfig().getInitParameter("maxDepth"));
        }
        if (this.getServletConfig().getInitParameter("allowSpecialPaths") != null) {
            this.allowSpecialPaths = Boolean.parseBoolean(this.getServletConfig().getInitParameter("allowSpecialPaths"));
        }
    }

    protected DocumentBuilder getDocumentBuilder() throws ServletException {
        DocumentBuilder documentBuilder = null;
        DocumentBuilderFactory documentBuilderFactory = null;
        try {
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            documentBuilderFactory.setExpandEntityReferences(false);
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            documentBuilder.setEntityResolver(new WebdavResolver(this.getServletContext()));
        }
        catch (ParserConfigurationException e) {
            throw new ServletException(sm.getString("webdavservlet.jaxpfailed"));
        }
        return documentBuilder;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = this.getRelativePath(req);
        if (req.getDispatcherType() == DispatcherType.ERROR) {
            this.doGet(req, resp);
            return;
        }
        if (this.isSpecialPath(path)) {
            resp.sendError(404);
            return;
        }
        String method = req.getMethod();
        if (this.debug > 0) {
            this.log("[" + method + "] " + path);
        }
        if (method.equals(METHOD_PROPFIND)) {
            this.doPropfind(req, resp);
        } else if (method.equals(METHOD_PROPPATCH)) {
            this.doProppatch(req, resp);
        } else if (method.equals(METHOD_MKCOL)) {
            this.doMkcol(req, resp);
        } else if (method.equals(METHOD_COPY)) {
            this.doCopy(req, resp);
        } else if (method.equals(METHOD_MOVE)) {
            this.doMove(req, resp);
        } else if (method.equals(METHOD_LOCK)) {
            this.doLock(req, resp);
        } else if (method.equals(METHOD_UNLOCK)) {
            this.doUnlock(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    private boolean isSpecialPath(String path) {
        return !this.allowSpecialPaths && (path.toUpperCase(Locale.ENGLISH).startsWith("/WEB-INF") || path.toUpperCase(Locale.ENGLISH).startsWith("/META-INF"));
    }

    @Override
    protected boolean checkIfHeaders(HttpServletRequest request, HttpServletResponse response, WebResource resource) throws IOException {
        return super.checkIfHeaders(request, response, resource);
    }

    @Override
    protected String getRelativePath(HttpServletRequest request) {
        return this.getRelativePath(request, false);
    }

    @Override
    protected String getRelativePath(HttpServletRequest request, boolean allowEmptyPath) {
        String pathInfo = request.getAttribute("javax.servlet.include.request_uri") != null ? (String)request.getAttribute("javax.servlet.include.path_info") : request.getPathInfo();
        StringBuilder result = new StringBuilder();
        if (pathInfo != null) {
            result.append(pathInfo);
        }
        if (result.length() == 0) {
            result.append('/');
        }
        return result.toString();
    }

    @Override
    protected String getPathPrefix(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        if (request.getServletPath() != null) {
            contextPath = contextPath + request.getServletPath();
        }
        return contextPath;
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("DAV", "1,2");
        resp.addHeader("Allow", this.determineMethodsAllowed(req));
        resp.addHeader("MS-Author-Via", "DAV");
    }

    protected void doPropfind(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String parentPath;
        List<String> currentLockNullResources;
        int slash;
        WebResource resource;
        if (!this.listings) {
            this.sendNotAllowed(req, resp);
            return;
        }
        String path = this.getRelativePath(req);
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        ArrayList<String> properties = null;
        int depth = this.maxDepth;
        int type = 1;
        String depthStr = req.getHeader("Depth");
        if (depthStr == null) {
            depth = this.maxDepth;
        } else if (depthStr.equals("0")) {
            depth = 0;
        } else if (depthStr.equals("1")) {
            depth = 1;
        } else if (depthStr.equals("infinity")) {
            depth = this.maxDepth;
        }
        Node propNode = null;
        if (req.getContentLengthLong() > 0L) {
            DocumentBuilder documentBuilder = this.getDocumentBuilder();
            try {
                Document document = documentBuilder.parse(new InputSource((InputStream)req.getInputStream()));
                Element rootElement = document.getDocumentElement();
                NodeList childList = rootElement.getChildNodes();
                block10: for (int i = 0; i < childList.getLength(); ++i) {
                    Node currentNode = childList.item(i);
                    switch (currentNode.getNodeType()) {
                        case 3: {
                            continue block10;
                        }
                        case 1: {
                            if (currentNode.getNodeName().endsWith("prop")) {
                                type = 0;
                                propNode = currentNode;
                            }
                            if (currentNode.getNodeName().endsWith("propname")) {
                                type = 2;
                            }
                            if (!currentNode.getNodeName().endsWith("allprop")) continue block10;
                            type = 1;
                        }
                    }
                }
            }
            catch (IOException | SAXException e) {
                resp.sendError(400);
                return;
            }
        }
        if (type == 0) {
            properties = new ArrayList<String>();
            NodeList childList = propNode.getChildNodes();
            block11: for (int i = 0; i < childList.getLength(); ++i) {
                Node currentNode = childList.item(i);
                switch (currentNode.getNodeType()) {
                    case 3: {
                        continue block11;
                    }
                    case 1: {
                        String nodeName = currentNode.getNodeName();
                        String propertyName = null;
                        propertyName = nodeName.indexOf(58) != -1 ? nodeName.substring(nodeName.indexOf(58) + 1) : nodeName;
                        properties.add(propertyName);
                    }
                }
            }
        }
        if (!(resource = this.resources.getResource(path)).exists() && (slash = path.lastIndexOf(47)) != -1 && (currentLockNullResources = this.lockNullResources.get(parentPath = path.substring(0, slash))) != null) {
            for (String lockNullPath : currentLockNullResources) {
                if (!lockNullPath.equals(path)) continue;
                resp.setStatus(207);
                resp.setContentType("text/xml; charset=UTF-8");
                XMLWriter generatedXML = new XMLWriter(resp.getWriter());
                generatedXML.writeXMLHeader();
                generatedXML.writeElement("D", DEFAULT_NAMESPACE, "multistatus", 0);
                this.parseLockNullProperties(req, generatedXML, lockNullPath, type, properties);
                generatedXML.writeElement("D", "multistatus", 1);
                generatedXML.sendData();
                return;
            }
        }
        if (!resource.exists()) {
            resp.sendError(404);
            return;
        }
        resp.setStatus(207);
        resp.setContentType("text/xml; charset=UTF-8");
        XMLWriter generatedXML = new XMLWriter(resp.getWriter());
        generatedXML.writeXMLHeader();
        generatedXML.writeElement("D", DEFAULT_NAMESPACE, "multistatus", 0);
        if (depth == 0) {
            this.parseProperties(req, generatedXML, path, type, properties);
        } else {
            ArrayDeque<String> stack = new ArrayDeque<String>();
            stack.addFirst(path);
            ArrayDeque<String> stackBelow = new ArrayDeque<String>();
            while (!stack.isEmpty() && depth >= 0) {
                String currentPath = (String)stack.remove();
                this.parseProperties(req, generatedXML, currentPath, type, properties);
                resource = this.resources.getResource(currentPath);
                if (resource.isDirectory() && depth > 0) {
                    List<String> currentLockNullResources2;
                    String[] entries;
                    for (String entry : entries = this.resources.list(currentPath)) {
                        String newPath = currentPath;
                        if (!newPath.endsWith("/")) {
                            newPath = newPath + "/";
                        }
                        newPath = newPath + entry;
                        stackBelow.addFirst(newPath);
                    }
                    String lockPath = currentPath;
                    if (lockPath.endsWith("/")) {
                        lockPath = lockPath.substring(0, lockPath.length() - 1);
                    }
                    if ((currentLockNullResources2 = this.lockNullResources.get(lockPath)) != null) {
                        for (String lockNullPath : currentLockNullResources2) {
                            this.parseLockNullProperties(req, generatedXML, lockNullPath, type, properties);
                        }
                    }
                }
                if (stack.isEmpty()) {
                    --depth;
                    stack = stackBelow;
                    stackBelow = new ArrayDeque();
                }
                generatedXML.sendData();
            }
        }
        generatedXML.writeElement("D", "multistatus", 1);
        generatedXML.sendData();
    }

    protected void doProppatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (this.readOnly) {
            resp.sendError(403);
            return;
        }
        if (this.isLocked(req)) {
            resp.sendError(423);
            return;
        }
        resp.sendError(501);
    }

    protected void doMkcol(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = this.getRelativePath(req);
        WebResource resource = this.resources.getResource(path);
        if (resource.exists()) {
            this.sendNotAllowed(req, resp);
            return;
        }
        if (this.readOnly) {
            resp.sendError(403);
            return;
        }
        if (this.isLocked(req)) {
            resp.sendError(423);
            return;
        }
        if (req.getContentLengthLong() > 0L) {
            DocumentBuilder documentBuilder = this.getDocumentBuilder();
            try {
                documentBuilder.parse(new InputSource((InputStream)req.getInputStream()));
                resp.sendError(501);
                return;
            }
            catch (SAXException saxe) {
                resp.sendError(415);
                return;
            }
        }
        if (this.resources.mkdir(path)) {
            resp.setStatus(201);
            this.lockNullResources.remove(path);
        } else {
            resp.sendError(409);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (this.readOnly) {
            this.sendNotAllowed(req, resp);
            return;
        }
        if (this.isLocked(req)) {
            resp.sendError(423);
            return;
        }
        this.deleteResource(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (this.isLocked(req)) {
            resp.sendError(423);
            return;
        }
        String path = this.getRelativePath(req);
        WebResource resource = this.resources.getResource(path);
        if (resource.isDirectory()) {
            this.sendNotAllowed(req, resp);
            return;
        }
        super.doPut(req, resp);
        this.lockNullResources.remove(path);
    }

    protected void doCopy(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (this.readOnly) {
            resp.sendError(403);
            return;
        }
        this.copyResource(req, resp);
    }

    protected void doMove(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (this.readOnly) {
            resp.sendError(403);
            return;
        }
        if (this.isLocked(req)) {
            resp.sendError(423);
            return;
        }
        String path = this.getRelativePath(req);
        if (this.copyResource(req, resp)) {
            this.deleteResource(path, req, resp, false);
        }
    }

    protected void doLock(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path;
        if (this.readOnly) {
            resp.sendError(403);
            return;
        }
        if (this.isLocked(req)) {
            resp.sendError(423);
            return;
        }
        LockInfo lock = new LockInfo(this.maxDepth);
        String depthStr = req.getHeader("Depth");
        lock.depth = depthStr == null ? this.maxDepth : (depthStr.equals("0") ? 0 : this.maxDepth);
        int lockDuration = 3600;
        String lockDurationStr = req.getHeader("Timeout");
        if (lockDurationStr != null) {
            int commaPos = lockDurationStr.indexOf(44);
            if (commaPos != -1) {
                lockDurationStr = lockDurationStr.substring(0, commaPos);
            }
            if (lockDurationStr.startsWith("Second-")) {
                lockDuration = Integer.parseInt(lockDurationStr.substring(7));
            } else if (lockDurationStr.equalsIgnoreCase("infinity")) {
                lockDuration = 604800;
            } else {
                try {
                    lockDuration = Integer.parseInt(lockDurationStr);
                }
                catch (NumberFormatException e) {
                    lockDuration = 604800;
                }
            }
            if (lockDuration == 0) {
                lockDuration = 3600;
            }
            if (lockDuration > 604800) {
                lockDuration = 604800;
            }
        }
        lock.expiresAt = System.currentTimeMillis() + (long)(lockDuration * 1000);
        boolean lockRequestType = false;
        Node lockInfoNode = null;
        DocumentBuilder documentBuilder = this.getDocumentBuilder();
        try {
            Document document = documentBuilder.parse(new InputSource((InputStream)req.getInputStream()));
            Element rootElement = document.getDocumentElement();
            lockInfoNode = rootElement;
        }
        catch (IOException | SAXException e) {
            lockRequestType = true;
        }
        if (lockInfoNode != null) {
            int i;
            NodeList childList = lockInfoNode.getChildNodes();
            StringWriter strWriter = null;
            DOMWriter domWriter = null;
            Node lockScopeNode = null;
            Node lockTypeNode = null;
            Node lockOwnerNode = null;
            block20: for (i = 0; i < childList.getLength(); ++i) {
                Node node = childList.item(i);
                switch (node.getNodeType()) {
                    case 3: {
                        continue block20;
                    }
                    case 1: {
                        String nodeName = node.getNodeName();
                        if (nodeName.endsWith("lockscope")) {
                            lockScopeNode = node;
                        }
                        if (nodeName.endsWith("locktype")) {
                            lockTypeNode = node;
                        }
                        if (!nodeName.endsWith("owner")) continue block20;
                        lockOwnerNode = node;
                    }
                }
            }
            if (lockScopeNode != null) {
                childList = lockScopeNode.getChildNodes();
                block21: for (i = 0; i < childList.getLength(); ++i) {
                    Node node = childList.item(i);
                    switch (node.getNodeType()) {
                        case 3: {
                            continue block21;
                        }
                        case 1: {
                            String tempScope = node.getNodeName();
                            lock.scope = tempScope.indexOf(58) != -1 ? tempScope.substring(tempScope.indexOf(58) + 1) : tempScope;
                        }
                    }
                }
                if (lock.scope == null) {
                    resp.setStatus(400);
                }
            } else {
                resp.setStatus(400);
            }
            if (lockTypeNode != null) {
                childList = lockTypeNode.getChildNodes();
                block22: for (i = 0; i < childList.getLength(); ++i) {
                    Node node = childList.item(i);
                    switch (node.getNodeType()) {
                        case 3: {
                            continue block22;
                        }
                        case 1: {
                            String tempType = node.getNodeName();
                            lock.type = tempType.indexOf(58) != -1 ? tempType.substring(tempType.indexOf(58) + 1) : tempType;
                        }
                    }
                }
                if (lock.type == null) {
                    resp.setStatus(400);
                }
            } else {
                resp.setStatus(400);
            }
            if (lockOwnerNode != null) {
                childList = lockOwnerNode.getChildNodes();
                block23: for (i = 0; i < childList.getLength(); ++i) {
                    Node node = childList.item(i);
                    switch (node.getNodeType()) {
                        case 3: {
                            lock.owner = lock.owner + node.getNodeValue();
                            continue block23;
                        }
                        case 1: {
                            strWriter = new StringWriter();
                            domWriter = new DOMWriter(strWriter);
                            domWriter.print(node);
                            lock.owner = lock.owner + strWriter.toString();
                        }
                    }
                }
                if (lock.owner == null) {
                    resp.setStatus(400);
                }
            } else {
                lock.owner = "";
            }
        }
        lock.path = path = this.getRelativePath(req);
        WebResource resource = this.resources.getResource(path);
        if (!lockRequestType) {
            String lockTokenStr = req.getServletPath() + "-" + lock.type + "-" + lock.scope + "-" + req.getUserPrincipal() + "-" + lock.depth + "-" + lock.owner + "-" + lock.tokens + "-" + lock.expiresAt + "-" + System.currentTimeMillis() + "-" + this.secret;
            String lockToken = HexUtils.toHexString((byte[])ConcurrentMessageDigest.digestMD5((byte[][])new byte[][]{lockTokenStr.getBytes(StandardCharsets.ISO_8859_1)}));
            if (resource.isDirectory() && lock.depth == this.maxDepth) {
                ArrayList<String> lockPaths = new ArrayList<String>();
                Iterator<LockInfo> collectionLocksIterator = this.collectionLocks.iterator();
                while (collectionLocksIterator.hasNext()) {
                    LockInfo currentLock = collectionLocksIterator.next();
                    if (currentLock.hasExpired()) {
                        collectionLocksIterator.remove();
                        continue;
                    }
                    if (!currentLock.path.startsWith(lock.path) || !currentLock.isExclusive() && !lock.isExclusive()) continue;
                    lockPaths.add(currentLock.path);
                }
                for (LockInfo lockInfo : this.resourceLocks.values()) {
                    if (lockInfo.hasExpired()) {
                        this.resourceLocks.remove(lockInfo.path);
                        continue;
                    }
                    if (!lockInfo.path.startsWith(lock.path) || !lockInfo.isExclusive() && !lock.isExclusive()) continue;
                    lockPaths.add(lockInfo.path);
                }
                if (!lockPaths.isEmpty()) {
                    resp.setStatus(409);
                    XMLWriter generatedXML = new XMLWriter();
                    generatedXML.writeXMLHeader();
                    generatedXML.writeElement("D", DEFAULT_NAMESPACE, "multistatus", 0);
                    for (String lockPath : lockPaths) {
                        generatedXML.writeElement("D", "response", 0);
                        generatedXML.writeElement("D", "href", 0);
                        generatedXML.writeText(lockPath);
                        generatedXML.writeElement("D", "href", 1);
                        generatedXML.writeElement("D", "status", 0);
                        generatedXML.writeText("HTTP/1.1 423 ");
                        generatedXML.writeElement("D", "status", 1);
                        generatedXML.writeElement("D", "response", 1);
                    }
                    generatedXML.writeElement("D", "multistatus", 1);
                    PrintWriter printWriter = resp.getWriter();
                    ((Writer)printWriter).write(generatedXML.toString());
                    ((Writer)printWriter).close();
                    return;
                }
                boolean addLock = true;
                for (LockInfo currentLock : this.collectionLocks) {
                    if (!currentLock.path.equals(lock.path)) continue;
                    if (currentLock.isExclusive()) {
                        resp.sendError(423);
                        return;
                    }
                    if (lock.isExclusive()) {
                        resp.sendError(423);
                        return;
                    }
                    currentLock.tokens.add(lockToken);
                    lock = currentLock;
                    addLock = false;
                }
                if (addLock) {
                    lock.tokens.add(lockToken);
                    this.collectionLocks.add(lock);
                }
            } else {
                LockInfo presentLock = this.resourceLocks.get(lock.path);
                if (presentLock != null) {
                    if (presentLock.isExclusive() || lock.isExclusive()) {
                        resp.sendError(412);
                        return;
                    }
                    presentLock.tokens.add(lockToken);
                    lock = presentLock;
                } else {
                    lock.tokens.add(lockToken);
                    this.resourceLocks.put(lock.path, lock);
                    if (!resource.exists()) {
                        int slash = lock.path.lastIndexOf(47);
                        String parentPath = lock.path.substring(0, slash);
                        this.lockNullResources.computeIfAbsent(parentPath, k -> new ArrayList()).add(lock.path);
                    }
                    resp.addHeader("Lock-Token", "<opaquelocktoken:" + lockToken + ">");
                }
            }
        }
        if (lockRequestType) {
            LockInfo toRenew;
            String ifHeader = req.getHeader("If");
            if (ifHeader == null) {
                ifHeader = "";
            }
            if ((toRenew = this.resourceLocks.get(path)) != null) {
                for (String token : toRenew.tokens) {
                    if (!ifHeader.contains(token)) continue;
                    toRenew.expiresAt = lock.expiresAt;
                    lock = toRenew;
                }
            }
            for (LockInfo collecionLock : this.collectionLocks) {
                if (!path.equals(collecionLock.path)) continue;
                for (String string : collecionLock.tokens) {
                    if (!ifHeader.contains(string)) continue;
                    collecionLock.expiresAt = lock.expiresAt;
                    lock = collecionLock;
                }
            }
        }
        XMLWriter generatedXML = new XMLWriter();
        generatedXML.writeXMLHeader();
        generatedXML.writeElement("D", DEFAULT_NAMESPACE, "prop", 0);
        generatedXML.writeElement("D", "lockdiscovery", 0);
        lock.toXML(generatedXML);
        generatedXML.writeElement("D", "lockdiscovery", 1);
        generatedXML.writeElement("D", "prop", 1);
        resp.setStatus(200);
        resp.setContentType("text/xml; charset=UTF-8");
        PrintWriter writer = resp.getWriter();
        ((Writer)writer).write(generatedXML.toString());
        ((Writer)writer).close();
    }

    protected void doUnlock(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LockInfo lock;
        if (this.readOnly) {
            resp.sendError(403);
            return;
        }
        if (this.isLocked(req)) {
            resp.sendError(423);
            return;
        }
        String path = this.getRelativePath(req);
        String lockTokenHeader = req.getHeader("Lock-Token");
        if (lockTokenHeader == null) {
            lockTokenHeader = "";
        }
        if ((lock = this.resourceLocks.get(path)) != null) {
            Iterator<String> tokenList = lock.tokens.iterator();
            while (tokenList.hasNext()) {
                String token = tokenList.next();
                if (!lockTokenHeader.contains(token)) continue;
                tokenList.remove();
            }
            if (lock.tokens.isEmpty()) {
                this.resourceLocks.remove(path);
                this.lockNullResources.remove(path);
            }
        }
        Iterator<LockInfo> collectionLocksList = this.collectionLocks.iterator();
        while (collectionLocksList.hasNext()) {
            lock = collectionLocksList.next();
            if (!path.equals(lock.path)) continue;
            Iterator<String> tokenList = lock.tokens.iterator();
            while (tokenList.hasNext()) {
                String token = tokenList.next();
                if (!lockTokenHeader.contains(token)) continue;
                tokenList.remove();
                break;
            }
            if (!lock.tokens.isEmpty()) continue;
            collectionLocksList.remove();
            this.lockNullResources.remove(path);
        }
        resp.setStatus(204);
    }

    private boolean isLocked(HttpServletRequest req) {
        String lockTokenHeader;
        String path = this.getRelativePath(req);
        String ifHeader = req.getHeader("If");
        if (ifHeader == null) {
            ifHeader = "";
        }
        if ((lockTokenHeader = req.getHeader("Lock-Token")) == null) {
            lockTokenHeader = "";
        }
        return this.isLocked(path, ifHeader + lockTokenHeader);
    }

    private boolean isLocked(String path, String ifHeader) {
        LockInfo lock = this.resourceLocks.get(path);
        if (lock != null && lock.hasExpired()) {
            this.resourceLocks.remove(path);
        } else if (lock != null) {
            boolean tokenMatch = false;
            for (String token : lock.tokens) {
                if (!ifHeader.contains(token)) continue;
                tokenMatch = true;
                break;
            }
            if (!tokenMatch) {
                return true;
            }
        }
        Iterator<LockInfo> collectionLockList = this.collectionLocks.iterator();
        while (collectionLockList.hasNext()) {
            lock = collectionLockList.next();
            if (lock.hasExpired()) {
                collectionLockList.remove();
                continue;
            }
            if (!path.startsWith(lock.path)) continue;
            boolean tokenMatch = false;
            for (String token : lock.tokens) {
                if (!ifHeader.contains(token)) continue;
                tokenMatch = true;
                break;
            }
            if (tokenMatch) continue;
            return true;
        }
        return false;
    }

    private boolean copyResource(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HashMap<String, Integer> errorList;
        boolean result;
        String reqContextPath;
        URI destinationUri;
        String path = this.getRelativePath(req);
        WebResource source = this.resources.getResource(path);
        if (!source.exists()) {
            resp.sendError(404);
            return false;
        }
        String destinationHeader = req.getHeader("Destination");
        if (destinationHeader == null || destinationHeader.isEmpty()) {
            resp.sendError(400);
            return false;
        }
        try {
            destinationUri = new URI(destinationHeader);
        }
        catch (URISyntaxException e) {
            resp.sendError(400);
            return false;
        }
        String destinationPath = destinationUri.getPath();
        if (!destinationPath.equals(RequestUtil.normalize((String)destinationPath))) {
            resp.sendError(400);
            return false;
        }
        if (destinationUri.isAbsolute()) {
            if (!req.getScheme().equals(destinationUri.getScheme()) || !req.getServerName().equals(destinationUri.getHost())) {
                resp.sendError(403);
                return false;
            }
            if (!(req.getServerPort() == destinationUri.getPort() || destinationUri.getPort() == -1 && ("http".equals(req.getScheme()) && req.getServerPort() == 80 || "https".equals(req.getScheme()) && req.getServerPort() == 443))) {
                resp.sendError(403);
                return false;
            }
        }
        if (!destinationPath.startsWith((reqContextPath = req.getContextPath()) + "/")) {
            resp.sendError(403);
            return false;
        }
        destinationPath = destinationPath.substring(reqContextPath.length() + req.getServletPath().length());
        if (this.debug > 0) {
            this.log("Dest path :" + destinationPath);
        }
        if (this.isSpecialPath(destinationPath)) {
            resp.sendError(403);
            return false;
        }
        if (destinationPath.equals(path)) {
            resp.sendError(403);
            return false;
        }
        if (destinationPath.startsWith(path) && destinationPath.charAt(path.length()) == '/' || path.startsWith(destinationPath) && path.charAt(destinationPath.length()) == '/') {
            resp.sendError(403);
            return false;
        }
        boolean overwrite = true;
        String overwriteHeader = req.getHeader("Overwrite");
        if (overwriteHeader != null) {
            overwrite = overwriteHeader.equalsIgnoreCase("T");
        }
        WebResource destination = this.resources.getResource(destinationPath);
        if (overwrite) {
            if (destination.exists()) {
                if (!this.deleteResource(destinationPath, req, resp, true)) {
                    return false;
                }
            } else {
                resp.setStatus(201);
            }
        } else if (destination.exists()) {
            resp.sendError(412);
            return false;
        }
        if (!(result = this.copyResource(errorList = new HashMap<String, Integer>(), path, destinationPath)) || !errorList.isEmpty()) {
            if (errorList.size() == 1) {
                resp.sendError(((Integer)errorList.values().iterator().next()).intValue());
            } else {
                this.sendReport(req, resp, errorList);
            }
            return false;
        }
        if (destination.exists()) {
            resp.setStatus(204);
        } else {
            resp.setStatus(201);
        }
        this.lockNullResources.remove(destinationPath);
        return true;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private boolean copyResource(Map<String, Integer> errorList, String source, String dest) {
        String parent;
        WebResource parentResource;
        int lastSlash;
        WebResource sourceResource;
        if (this.debug > 1) {
            this.log("Copy: " + source + " To: " + dest);
        }
        if ((sourceResource = this.resources.getResource(source)).isDirectory()) {
            String[] entries;
            WebResource destResource;
            if (!this.resources.mkdir(dest) && !(destResource = this.resources.getResource(dest)).isDirectory()) {
                errorList.put(dest, 409);
                return false;
            }
            String[] stringArray = entries = this.resources.list(source);
            int n = stringArray.length;
            int n2 = 0;
            while (n2 < n) {
                String entry = stringArray[n2];
                String childDest = dest;
                if (!childDest.equals("/")) {
                    childDest = childDest + "/";
                }
                childDest = childDest + entry;
                String childSrc = source;
                if (!childSrc.equals("/")) {
                    childSrc = childSrc + "/";
                }
                childSrc = childSrc + entry;
                this.copyResource(errorList, childSrc, childDest);
                ++n2;
            }
            return true;
        }
        if (!sourceResource.isFile()) {
            errorList.put(source, 500);
            return false;
        }
        WebResource destResource = this.resources.getResource(dest);
        if (!(destResource.exists() || destResource.getWebappPath().endsWith("/") || (lastSlash = destResource.getWebappPath().lastIndexOf(47)) <= 0 || (parentResource = this.resources.getResource(parent = destResource.getWebappPath().substring(0, lastSlash))).isDirectory())) {
            errorList.put(source, 409);
            return false;
        }
        if (!destResource.exists() && dest.endsWith("/") && dest.length() > 1) {
            dest = dest.substring(0, dest.length() - 1);
        }
        try (InputStream is = sourceResource.getInputStream();){
            if (this.resources.write(dest, is, false)) return true;
            errorList.put(source, 500);
            boolean bl = false;
            return bl;
        }
        catch (IOException e) {
            this.log(sm.getString("webdavservlet.inputstreamclosefail", new Object[]{source}), e);
            return true;
        }
    }

    private boolean deleteResource(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = this.getRelativePath(req);
        return this.deleteResource(path, req, resp, true);
    }

    private boolean deleteResource(String path, HttpServletRequest req, HttpServletResponse resp, boolean setStatus) throws IOException {
        String lockTokenHeader;
        String ifHeader = req.getHeader("If");
        if (ifHeader == null) {
            ifHeader = "";
        }
        if ((lockTokenHeader = req.getHeader("Lock-Token")) == null) {
            lockTokenHeader = "";
        }
        if (this.isLocked(path, ifHeader + lockTokenHeader)) {
            resp.sendError(423);
            return false;
        }
        WebResource resource = this.resources.getResource(path);
        if (!resource.exists()) {
            resp.sendError(404);
            return false;
        }
        if (!resource.isDirectory()) {
            if (!resource.delete()) {
                resp.sendError(500);
                return false;
            }
        } else {
            HashMap<String, Integer> errorList = new HashMap<String, Integer>();
            this.deleteCollection(req, path, errorList);
            if (!resource.delete()) {
                errorList.put(path, 500);
            }
            if (!errorList.isEmpty()) {
                this.sendReport(req, resp, errorList);
                return false;
            }
        }
        if (setStatus) {
            resp.setStatus(204);
        }
        return true;
    }

    private void deleteCollection(HttpServletRequest req, String path, Map<String, Integer> errorList) {
        String[] entries;
        String lockTokenHeader;
        if (this.debug > 1) {
            this.log("Delete:" + path);
        }
        if (this.isSpecialPath(path)) {
            errorList.put(path, 403);
            return;
        }
        String ifHeader = req.getHeader("If");
        if (ifHeader == null) {
            ifHeader = "";
        }
        if ((lockTokenHeader = req.getHeader("Lock-Token")) == null) {
            lockTokenHeader = "";
        }
        for (String entry : entries = this.resources.list(path)) {
            String childName = path;
            if (!childName.equals("/")) {
                childName = childName + "/";
            }
            if (this.isLocked(childName = childName + entry, ifHeader + lockTokenHeader)) {
                errorList.put(childName, 423);
                continue;
            }
            WebResource childResource = this.resources.getResource(childName);
            if (childResource.isDirectory()) {
                this.deleteCollection(req, childName, errorList);
            }
            if (childResource.delete() || childResource.isDirectory()) continue;
            errorList.put(childName, 500);
        }
    }

    private void sendReport(HttpServletRequest req, HttpServletResponse resp, Map<String, Integer> errorList) throws IOException {
        resp.setStatus(207);
        XMLWriter generatedXML = new XMLWriter();
        generatedXML.writeXMLHeader();
        generatedXML.writeElement("D", DEFAULT_NAMESPACE, "multistatus", 0);
        for (Map.Entry<String, Integer> errorEntry : errorList.entrySet()) {
            String errorPath = errorEntry.getKey();
            int errorCode = errorEntry.getValue();
            generatedXML.writeElement("D", "response", 0);
            generatedXML.writeElement("D", "href", 0);
            generatedXML.writeText(this.getServletContext().getContextPath() + errorPath);
            generatedXML.writeElement("D", "href", 1);
            generatedXML.writeElement("D", "status", 0);
            generatedXML.writeText("HTTP/1.1 " + errorCode + " ");
            generatedXML.writeElement("D", "status", 1);
            generatedXML.writeElement("D", "response", 1);
        }
        generatedXML.writeElement("D", "multistatus", 1);
        PrintWriter writer = resp.getWriter();
        ((Writer)writer).write(generatedXML.toString());
        ((Writer)writer).close();
    }

    private void parseProperties(HttpServletRequest req, XMLWriter generatedXML, String path, int type, List<String> properties) {
        if (this.isSpecialPath(path)) {
            return;
        }
        WebResource resource = this.resources.getResource(path);
        if (!resource.exists()) {
            return;
        }
        String href = req.getContextPath() + req.getServletPath();
        href = href.endsWith("/") && path.startsWith("/") ? href + path.substring(1) : href + path;
        if (resource.isDirectory() && !href.endsWith("/")) {
            href = href + "/";
        }
        String rewrittenUrl = this.rewriteUrl(href);
        this.generatePropFindResponse(generatedXML, rewrittenUrl, path, type, properties, resource.isFile(), false, resource.getCreation(), resource.getLastModified(), resource.getContentLength(), this.getServletContext().getMimeType(resource.getName()), this.generateETag(resource));
    }

    private void parseLockNullProperties(HttpServletRequest req, XMLWriter generatedXML, String path, int type, List<String> properties) {
        if (this.isSpecialPath(path)) {
            return;
        }
        LockInfo lock = this.resourceLocks.get(path);
        if (lock == null) {
            return;
        }
        String absoluteUri = req.getRequestURI();
        String relativePath = this.getRelativePath(req);
        String toAppend = path.substring(relativePath.length());
        if (!toAppend.startsWith("/")) {
            toAppend = "/" + toAppend;
        }
        String rewrittenUrl = this.rewriteUrl(RequestUtil.normalize((String)(absoluteUri + toAppend)));
        this.generatePropFindResponse(generatedXML, rewrittenUrl, path, type, properties, true, true, lock.creationDate.getTime(), lock.creationDate.getTime(), 0L, "", "");
    }

    private void generatePropFindResponse(XMLWriter generatedXML, String rewrittenUrl, String path, int propFindType, List<String> properties, boolean isFile, boolean isLockNull, long created, long lastModified, long contentLength, String contentType, String eTag) {
        generatedXML.writeElement("D", "response", 0);
        String status = "HTTP/1.1 200 ";
        generatedXML.writeElement("D", "href", 0);
        generatedXML.writeText(rewrittenUrl);
        generatedXML.writeElement("D", "href", 1);
        String resourceName = path;
        int lastSlash = path.lastIndexOf(47);
        if (lastSlash != -1) {
            resourceName = resourceName.substring(lastSlash + 1);
        }
        switch (propFindType) {
            case 1: {
                generatedXML.writeElement("D", "propstat", 0);
                generatedXML.writeElement("D", "prop", 0);
                generatedXML.writeProperty("D", "creationdate", this.getISOCreationDate(created));
                generatedXML.writeElement("D", "displayname", 0);
                generatedXML.writeData(resourceName);
                generatedXML.writeElement("D", "displayname", 1);
                if (isFile) {
                    generatedXML.writeProperty("D", "getlastmodified", FastHttpDateFormat.formatDate((long)lastModified));
                    generatedXML.writeProperty("D", "getcontentlength", Long.toString(contentLength));
                    if (contentType != null) {
                        generatedXML.writeProperty("D", "getcontenttype", contentType);
                    }
                    generatedXML.writeProperty("D", "getetag", eTag);
                    if (isLockNull) {
                        generatedXML.writeElement("D", "resourcetype", 0);
                        generatedXML.writeElement("D", "lock-null", 2);
                        generatedXML.writeElement("D", "resourcetype", 1);
                    } else {
                        generatedXML.writeElement("D", "resourcetype", 2);
                    }
                } else {
                    generatedXML.writeProperty("D", "getlastmodified", FastHttpDateFormat.formatDate((long)lastModified));
                    generatedXML.writeElement("D", "resourcetype", 0);
                    generatedXML.writeElement("D", "collection", 2);
                    generatedXML.writeElement("D", "resourcetype", 1);
                }
                generatedXML.writeProperty("D", "source", "");
                String supportedLocks = "<D:lockentry><D:lockscope><D:exclusive/></D:lockscope><D:locktype><D:write/></D:locktype></D:lockentry><D:lockentry><D:lockscope><D:shared/></D:lockscope><D:locktype><D:write/></D:locktype></D:lockentry>";
                generatedXML.writeElement("D", "supportedlock", 0);
                generatedXML.writeText(supportedLocks);
                generatedXML.writeElement("D", "supportedlock", 1);
                this.generateLockDiscovery(path, generatedXML);
                generatedXML.writeElement("D", "prop", 1);
                generatedXML.writeElement("D", "status", 0);
                generatedXML.writeText(status);
                generatedXML.writeElement("D", "status", 1);
                generatedXML.writeElement("D", "propstat", 1);
                break;
            }
            case 2: {
                generatedXML.writeElement("D", "propstat", 0);
                generatedXML.writeElement("D", "prop", 0);
                generatedXML.writeElement("D", "creationdate", 2);
                generatedXML.writeElement("D", "displayname", 2);
                if (isFile) {
                    generatedXML.writeElement("D", "getcontentlanguage", 2);
                    generatedXML.writeElement("D", "getcontentlength", 2);
                    generatedXML.writeElement("D", "getcontenttype", 2);
                    generatedXML.writeElement("D", "getetag", 2);
                    generatedXML.writeElement("D", "getlastmodified", 2);
                }
                generatedXML.writeElement("D", "resourcetype", 2);
                generatedXML.writeElement("D", "source", 2);
                generatedXML.writeElement("D", "lockdiscovery", 2);
                generatedXML.writeElement("D", "prop", 1);
                generatedXML.writeElement("D", "status", 0);
                generatedXML.writeText(status);
                generatedXML.writeElement("D", "status", 1);
                generatedXML.writeElement("D", "propstat", 1);
                break;
            }
            case 0: {
                ArrayList<String> propertiesNotFound = new ArrayList<String>();
                generatedXML.writeElement("D", "propstat", 0);
                generatedXML.writeElement("D", "prop", 0);
                for (String property : properties) {
                    if (property.equals("creationdate")) {
                        generatedXML.writeProperty("D", "creationdate", this.getISOCreationDate(created));
                        continue;
                    }
                    if (property.equals("displayname")) {
                        generatedXML.writeElement("D", "displayname", 0);
                        generatedXML.writeData(resourceName);
                        generatedXML.writeElement("D", "displayname", 1);
                        continue;
                    }
                    if (property.equals("getcontentlanguage")) {
                        if (isFile) {
                            generatedXML.writeElement("D", "getcontentlanguage", 2);
                            continue;
                        }
                        propertiesNotFound.add(property);
                        continue;
                    }
                    if (property.equals("getcontentlength")) {
                        if (isFile) {
                            generatedXML.writeProperty("D", "getcontentlength", Long.toString(contentLength));
                            continue;
                        }
                        propertiesNotFound.add(property);
                        continue;
                    }
                    if (property.equals("getcontenttype")) {
                        if (isFile) {
                            generatedXML.writeProperty("D", "getcontenttype", contentType);
                            continue;
                        }
                        propertiesNotFound.add(property);
                        continue;
                    }
                    if (property.equals("getetag")) {
                        if (isFile) {
                            generatedXML.writeProperty("D", "getetag", eTag);
                            continue;
                        }
                        propertiesNotFound.add(property);
                        continue;
                    }
                    if (property.equals("getlastmodified")) {
                        if (isFile) {
                            generatedXML.writeProperty("D", "getlastmodified", FastHttpDateFormat.formatDate((long)lastModified));
                            continue;
                        }
                        propertiesNotFound.add(property);
                        continue;
                    }
                    if (property.equals("resourcetype")) {
                        if (isFile) {
                            if (isLockNull) {
                                generatedXML.writeElement("D", "resourcetype", 0);
                                generatedXML.writeElement("D", "lock-null", 2);
                                generatedXML.writeElement("D", "resourcetype", 1);
                                continue;
                            }
                            generatedXML.writeElement("D", "resourcetype", 2);
                            continue;
                        }
                        generatedXML.writeElement("D", "resourcetype", 0);
                        generatedXML.writeElement("D", "collection", 2);
                        generatedXML.writeElement("D", "resourcetype", 1);
                        continue;
                    }
                    if (property.equals("source")) {
                        generatedXML.writeProperty("D", "source", "");
                        continue;
                    }
                    if (property.equals("supportedlock")) {
                        String supportedLocks = "<D:lockentry><D:lockscope><D:exclusive/></D:lockscope><D:locktype><D:write/></D:locktype></D:lockentry><D:lockentry><D:lockscope><D:shared/></D:lockscope><D:locktype><D:write/></D:locktype></D:lockentry>";
                        generatedXML.writeElement("D", "supportedlock", 0);
                        generatedXML.writeText(supportedLocks);
                        generatedXML.writeElement("D", "supportedlock", 1);
                        continue;
                    }
                    if (property.equals("lockdiscovery")) {
                        if (this.generateLockDiscovery(path, generatedXML)) continue;
                        propertiesNotFound.add(property);
                        continue;
                    }
                    propertiesNotFound.add(property);
                }
                generatedXML.writeElement("D", "prop", 1);
                generatedXML.writeElement("D", "status", 0);
                generatedXML.writeText(status);
                generatedXML.writeElement("D", "status", 1);
                generatedXML.writeElement("D", "propstat", 1);
                if (propertiesNotFound.isEmpty()) break;
                status = "HTTP/1.1 404 ";
                generatedXML.writeElement("D", "propstat", 0);
                generatedXML.writeElement("D", "prop", 0);
                for (String propertyNotFound : propertiesNotFound) {
                    generatedXML.writeElement("D", propertyNotFound, 2);
                }
                generatedXML.writeElement("D", "prop", 1);
                generatedXML.writeElement("D", "status", 0);
                generatedXML.writeText(status);
                generatedXML.writeElement("D", "status", 1);
                generatedXML.writeElement("D", "propstat", 1);
            }
        }
        generatedXML.writeElement("D", "response", 1);
    }

    private boolean generateLockDiscovery(String path, XMLWriter generatedXML) {
        LockInfo resourceLock = this.resourceLocks.get(path);
        boolean wroteStart = false;
        if (resourceLock != null) {
            wroteStart = true;
            generatedXML.writeElement("D", "lockdiscovery", 0);
            resourceLock.toXML(generatedXML);
        }
        for (LockInfo currentLock : this.collectionLocks) {
            if (!path.startsWith(currentLock.path)) continue;
            if (!wroteStart) {
                wroteStart = true;
                generatedXML.writeElement("D", "lockdiscovery", 0);
            }
            currentLock.toXML(generatedXML);
        }
        if (!wroteStart) {
            return false;
        }
        generatedXML.writeElement("D", "lockdiscovery", 1);
        return true;
    }

    private String getISOCreationDate(long creationDate) {
        return creationDateFormat.format(new Date(creationDate));
    }

    @Override
    protected String determineMethodsAllowed(HttpServletRequest req) {
        WebResource resource = this.resources.getResource(this.getRelativePath(req));
        StringBuilder methodsAllowed = new StringBuilder("OPTIONS, GET, POST, HEAD");
        if (!this.readOnly) {
            methodsAllowed.append(", DELETE");
            if (!resource.isDirectory()) {
                methodsAllowed.append(", PUT");
            }
        }
        if (req instanceof RequestFacade && ((RequestFacade)req).getAllowTrace()) {
            methodsAllowed.append(", TRACE");
        }
        methodsAllowed.append(", LOCK, UNLOCK, PROPPATCH, COPY, MOVE");
        if (this.listings) {
            methodsAllowed.append(", PROPFIND");
        }
        if (!resource.exists()) {
            methodsAllowed.append(", MKCOL");
        }
        return methodsAllowed.toString();
    }

    private static class WebdavResolver
    implements EntityResolver {
        private ServletContext context;

        WebdavResolver(ServletContext theContext) {
            this.context = theContext;
        }

        @Override
        public InputSource resolveEntity(String publicId, String systemId) {
            this.context.log(DefaultServlet.sm.getString("webdavservlet.externalEntityIgnored", new Object[]{publicId, systemId}));
            return new InputSource(new StringReader("Ignored external entity"));
        }
    }

    private static class LockInfo
    implements Serializable {
        private static final long serialVersionUID = 1L;
        private final int maxDepth;
        String path = "/";
        String type = "write";
        String scope = "exclusive";
        int depth = 0;
        String owner = "";
        List<String> tokens = Collections.synchronizedList(new ArrayList());
        long expiresAt = 0L;
        Date creationDate = new Date();

        LockInfo(int maxDepth) {
            this.maxDepth = maxDepth;
        }

        public String toString() {
            StringBuilder result = new StringBuilder("Type:");
            result.append(this.type);
            result.append("\nScope:");
            result.append(this.scope);
            result.append("\nDepth:");
            result.append(this.depth);
            result.append("\nOwner:");
            result.append(this.owner);
            result.append("\nExpiration:");
            result.append(FastHttpDateFormat.formatDate((long)this.expiresAt));
            for (String token : this.tokens) {
                result.append("\nToken:");
                result.append(token);
            }
            result.append("\n");
            return result.toString();
        }

        public boolean hasExpired() {
            return System.currentTimeMillis() > this.expiresAt;
        }

        public boolean isExclusive() {
            return this.scope.equals("exclusive");
        }

        public void toXML(XMLWriter generatedXML) {
            generatedXML.writeElement("D", "activelock", 0);
            generatedXML.writeElement("D", "locktype", 0);
            generatedXML.writeElement("D", this.type, 2);
            generatedXML.writeElement("D", "locktype", 1);
            generatedXML.writeElement("D", "lockscope", 0);
            generatedXML.writeElement("D", this.scope, 2);
            generatedXML.writeElement("D", "lockscope", 1);
            generatedXML.writeElement("D", "depth", 0);
            if (this.depth == this.maxDepth) {
                generatedXML.writeText("Infinity");
            } else {
                generatedXML.writeText("0");
            }
            generatedXML.writeElement("D", "depth", 1);
            generatedXML.writeElement("D", "owner", 0);
            generatedXML.writeText(this.owner);
            generatedXML.writeElement("D", "owner", 1);
            generatedXML.writeElement("D", "timeout", 0);
            long timeout = (this.expiresAt - System.currentTimeMillis()) / 1000L;
            generatedXML.writeText("Second-" + timeout);
            generatedXML.writeElement("D", "timeout", 1);
            generatedXML.writeElement("D", "locktoken", 0);
            for (String token : this.tokens) {
                generatedXML.writeElement("D", "href", 0);
                generatedXML.writeText("opaquelocktoken:" + token);
                generatedXML.writeElement("D", "href", 1);
            }
            generatedXML.writeElement("D", "locktoken", 1);
            generatedXML.writeElement("D", "activelock", 1);
        }
    }
}

