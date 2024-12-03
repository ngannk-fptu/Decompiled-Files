/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.StringTokenizer;
import javax.jcr.Binary;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.EventListenerIterator;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;
import javax.jcr.security.AccessControlPolicyIterator;
import javax.jcr.version.Version;
import javax.jcr.version.VersionIterator;

public class JcrUtils {
    public static final String REPOSITORY_URI = "org.apache.jackrabbit.repository.uri";
    public static final Value[] NO_VALUES = new Value[0];
    private static final List<String> PROPERTY_TYPES_NAMES = new ArrayList<String>();
    private static final Map<String, Integer> PROPERTY_TYPES = new HashMap<String, Integer>();

    private JcrUtils() {
    }

    public static Repository getRepository() throws RepositoryException {
        return JcrUtils.getRepository((Map<String, String>)null);
    }

    public static Repository getRepository(Map<String, String> parameters) throws RepositoryException {
        String newline = System.getProperty("line.separator");
        StringBuilder log = new StringBuilder("Unable to access a repository");
        if (parameters != null) {
            log.append(" with the following settings:");
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                log.append(newline);
                log.append("    ");
                log.append(entry.getKey());
                log.append(": ");
                log.append(entry.getValue());
            }
        } else {
            log.append(" with the default settings.");
        }
        if (parameters != null && parameters.containsKey(REPOSITORY_URI)) {
            String uri = parameters.get(REPOSITORY_URI);
            try {
                URI u = new URI(uri);
                String query = u.getRawQuery();
                if (query != null) {
                    HashMap<String, String> copy = new HashMap<String, String>(parameters);
                    for (String entry : query.split("&")) {
                        int i = entry.indexOf(61);
                        if (i != -1) {
                            copy.put(URLDecoder.decode(entry.substring(0, i), "UTF-8"), URLDecoder.decode(entry.substring(i + 1), "UTF-8"));
                            continue;
                        }
                        copy.put(URLDecoder.decode(entry, "UTF-8"), Boolean.TRUE.toString());
                    }
                    copy.put(REPOSITORY_URI, new URI(u.getScheme(), u.getRawAuthority(), u.getRawPath(), null, u.getRawFragment()).toASCIIString());
                    parameters = copy;
                }
            }
            catch (URISyntaxException e) {
                log.append(newline);
                log.append("Note that the given repository URI was invalid:");
                log.append(newline);
                log.append("        ").append(uri);
                log.append(newline);
                log.append("        ").append(e.getMessage());
            }
            catch (UnsupportedEncodingException e) {
                throw new RepositoryException("UTF-8 is not supported!", e);
            }
        }
        log.append(newline);
        log.append("The following RepositoryFactory classes were consulted:");
        for (RepositoryFactory factory : ServiceLoader.load(RepositoryFactory.class)) {
            log.append(newline);
            log.append("    ");
            log.append(factory.getClass().getName());
            try {
                Repository repository = factory.getRepository(parameters);
                if (repository != null) {
                    return repository;
                }
                log.append(": declined");
            }
            catch (Exception e) {
                log.append(": failed");
                try {
                    StringWriter writer = new StringWriter();
                    Object object = null;
                    try {
                        PrintWriter printWriter = new PrintWriter(writer);
                        Throwable throwable = null;
                        try {
                            e.printStackTrace(printWriter);
                            log.append(newline).append(writer.getBuffer());
                        }
                        catch (Throwable throwable2) {
                            throwable = throwable2;
                            throw throwable2;
                        }
                        finally {
                            if (printWriter == null) continue;
                            if (throwable != null) {
                                try {
                                    printWriter.close();
                                }
                                catch (Throwable throwable3) {
                                    throwable.addSuppressed(throwable3);
                                }
                                continue;
                            }
                            printWriter.close();
                        }
                    }
                    catch (Throwable throwable) {
                        object = throwable;
                        throw throwable;
                    }
                    finally {
                        if (writer == null) continue;
                        if (object != null) {
                            try {
                                writer.close();
                            }
                            catch (Throwable throwable) {
                                ((Throwable)object).addSuppressed(throwable);
                            }
                            continue;
                        }
                        writer.close();
                    }
                }
                catch (IOException e1) {
                    log.append("Could not determine root cause due to ").append(e.getMessage());
                }
            }
        }
        log.append(newline);
        log.append("Perhaps the repository you are trying to access is not available at the moment.");
        throw new RepositoryException(log.toString());
    }

    public static Repository getRepository(String uri) throws RepositoryException {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(REPOSITORY_URI, uri);
        return JcrUtils.getRepository(parameters);
    }

    public static Iterable<Node> getSharedSet(final Node node) throws RepositoryException {
        final NodeIterator iterator = node.getSharedSet();
        return new Iterable<Node>(){
            private boolean first = true;

            @Override
            public synchronized Iterator<Node> iterator() {
                if (this.first) {
                    this.first = false;
                    return iterator;
                }
                try {
                    return node.getSharedSet();
                }
                catch (RepositoryException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static Iterable<Node> getChildNodes(final Node node) throws RepositoryException {
        final NodeIterator iterator = node.getNodes();
        return new Iterable<Node>(){
            private boolean first = true;

            @Override
            public synchronized Iterator<Node> iterator() {
                if (this.first) {
                    this.first = false;
                    return iterator;
                }
                try {
                    return node.getNodes();
                }
                catch (RepositoryException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static Iterable<Node> getChildNodes(final Node node, final String pattern) throws RepositoryException {
        final NodeIterator iterator = node.getNodes(pattern);
        return new Iterable<Node>(){
            private boolean first = true;

            @Override
            public synchronized Iterator<Node> iterator() {
                if (this.first) {
                    this.first = false;
                    return iterator;
                }
                try {
                    return node.getNodes(pattern);
                }
                catch (RepositoryException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static Iterable<Node> getChildNodes(final Node node, final String[] globs) throws RepositoryException {
        final NodeIterator iterator = node.getNodes(globs);
        return new Iterable<Node>(){
            private boolean first = true;

            @Override
            public synchronized Iterator<Node> iterator() {
                if (this.first) {
                    this.first = false;
                    return iterator;
                }
                try {
                    return node.getNodes(globs);
                }
                catch (RepositoryException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static Iterable<Property> getProperties(final Node node) throws RepositoryException {
        final PropertyIterator iterator = node.getProperties();
        return new Iterable<Property>(){
            private boolean first = true;

            @Override
            public synchronized Iterator<Property> iterator() {
                if (this.first) {
                    this.first = false;
                    return iterator;
                }
                try {
                    return node.getProperties();
                }
                catch (RepositoryException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static Iterable<Property> getProperties(final Node node, final String pattern) throws RepositoryException {
        final PropertyIterator iterator = node.getProperties(pattern);
        return new Iterable<Property>(){
            private boolean first = true;

            @Override
            public synchronized Iterator<Property> iterator() {
                if (this.first) {
                    this.first = false;
                    return iterator;
                }
                try {
                    return node.getProperties(pattern);
                }
                catch (RepositoryException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static Iterable<Property> getProperties(final Node node, final String[] globs) throws RepositoryException {
        final PropertyIterator iterator = node.getProperties(globs);
        return new Iterable<Property>(){
            private boolean first = true;

            @Override
            public synchronized Iterator<Property> iterator() {
                if (this.first) {
                    this.first = false;
                    return iterator;
                }
                try {
                    return node.getProperties(globs);
                }
                catch (RepositoryException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static Iterable<Property> getReferences(final Node node) throws RepositoryException {
        final PropertyIterator iterator = node.getReferences();
        return new Iterable<Property>(){
            private boolean first = true;

            @Override
            public synchronized Iterator<Property> iterator() {
                if (this.first) {
                    this.first = false;
                    return iterator;
                }
                try {
                    return node.getReferences();
                }
                catch (RepositoryException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static Iterable<Property> getReferences(final Node node, final String name) throws RepositoryException {
        final PropertyIterator iterator = node.getReferences(name);
        return new Iterable<Property>(){
            private boolean first = true;

            @Override
            public synchronized Iterator<Property> iterator() {
                if (this.first) {
                    this.first = false;
                    return iterator;
                }
                try {
                    return node.getReferences(name);
                }
                catch (RepositoryException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static Iterable<Property> getWeakReferences(final Node node) throws RepositoryException {
        final PropertyIterator iterator = node.getWeakReferences();
        return new Iterable<Property>(){
            private boolean first = true;

            @Override
            public synchronized Iterator<Property> iterator() {
                if (this.first) {
                    this.first = false;
                    return iterator;
                }
                try {
                    return node.getWeakReferences();
                }
                catch (RepositoryException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static Iterable<Property> getWeakReferences(final Node node, final String name) throws RepositoryException {
        final PropertyIterator iterator = node.getWeakReferences(name);
        return new Iterable<Property>(){
            private boolean first = true;

            @Override
            public synchronized Iterator<Property> iterator() {
                if (this.first) {
                    this.first = false;
                    return iterator;
                }
                try {
                    return node.getWeakReferences(name);
                }
                catch (RepositoryException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static Iterable<Node> getNodes(final QueryResult result) throws RepositoryException {
        final NodeIterator iterator = result.getNodes();
        return new Iterable<Node>(){
            private boolean first = true;

            @Override
            public synchronized Iterator<Node> iterator() {
                if (this.first) {
                    this.first = false;
                    return iterator;
                }
                try {
                    return result.getNodes();
                }
                catch (RepositoryException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static Iterable<Row> getRows(final QueryResult result) throws RepositoryException {
        final RowIterator iterator = result.getRows();
        return new Iterable<Row>(){
            private boolean first = true;

            @Override
            public synchronized Iterator<Row> iterator() {
                if (this.first) {
                    this.first = false;
                    return iterator;
                }
                try {
                    return result.getRows();
                }
                catch (RepositoryException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static <I> Iterable<I> in(final Iterator<I> iterator) {
        return new Iterable<I>(){
            private boolean stale = false;

            @Override
            public synchronized Iterator<I> iterator() {
                if (this.stale) {
                    throw new IllegalStateException("Cannot reuse Iterable intended for single use");
                }
                this.stale = true;
                return iterator;
            }
        };
    }

    public static Iterable<AccessControlPolicyIterator> in(AccessControlPolicyIterator iterator) {
        return JcrUtils.in(iterator);
    }

    public static Iterable<Event> in(EventIterator iterator) {
        return JcrUtils.in(iterator);
    }

    public static Iterable<EventListener> in(EventListenerIterator iterator) {
        return JcrUtils.in(iterator);
    }

    public static Iterable<Node> in(NodeIterator iterator) {
        return JcrUtils.in(iterator);
    }

    public static Iterable<NodeType> in(NodeTypeIterator iterator) {
        return JcrUtils.in(iterator);
    }

    public static Iterable<Property> in(PropertyIterator iterator) {
        return JcrUtils.in(iterator);
    }

    public static Iterable<Row> in(RowIterator iterator) {
        return JcrUtils.in(iterator);
    }

    public static Iterable<Version> in(VersionIterator iterator) {
        return JcrUtils.in(iterator);
    }

    public static Node getOrAddNode(Node parent, String name) throws RepositoryException {
        return JcrUtils.getOrAddNode(parent, name, null);
    }

    public static Node getOrAddNode(Node parent, String name, String type) throws RepositoryException {
        if (parent.hasNode(name)) {
            return parent.getNode(name);
        }
        if (type != null) {
            return parent.addNode(name, type);
        }
        return parent.addNode(name);
    }

    public static Node getOrAddFolder(Node parent, String name) throws RepositoryException {
        return JcrUtils.getOrAddNode(parent, name, "{http://www.jcp.org/jcr/nt/1.0}folder");
    }

    public static Node putFile(Node parent, String name, String mime, InputStream data) throws RepositoryException {
        return JcrUtils.putFile(parent, name, mime, data, Calendar.getInstance());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Node putFile(Node parent, String name, String mime, InputStream data, Calendar date) throws RepositoryException {
        Binary binary = parent.getSession().getValueFactory().createBinary(data);
        try {
            Node file = JcrUtils.getOrAddNode(parent, name, "{http://www.jcp.org/jcr/nt/1.0}file");
            Node content = JcrUtils.getOrAddNode(file, "{http://www.jcp.org/jcr/1.0}content", "{http://www.jcp.org/jcr/nt/1.0}resource");
            content.setProperty("{http://www.jcp.org/jcr/1.0}mimeType", mime);
            String[] parameters = mime.split(";");
            for (int i = 1; i < parameters.length; ++i) {
                String parameter;
                int equals = parameters[i].indexOf(61);
                if (equals == -1 || !"charset".equalsIgnoreCase((parameter = parameters[i].substring(0, equals)).trim())) continue;
                content.setProperty("{http://www.jcp.org/jcr/1.0}encoding", parameters[i].substring(equals + 1).trim());
            }
            content.setProperty("{http://www.jcp.org/jcr/1.0}lastModified", date);
            content.setProperty("{http://www.jcp.org/jcr/1.0}data", binary);
            Node node = file;
            return node;
        }
        finally {
            binary.dispose();
        }
    }

    public static InputStream readFile(Node node) throws RepositoryException {
        if (node.hasProperty("{http://www.jcp.org/jcr/1.0}data")) {
            Property data = node.getProperty("{http://www.jcp.org/jcr/1.0}data");
            final Binary binary = data.getBinary();
            return new FilterInputStream(binary.getStream()){

                @Override
                public void close() throws IOException {
                    super.close();
                    binary.dispose();
                }
            };
        }
        if (node.hasNode("{http://www.jcp.org/jcr/1.0}content")) {
            return JcrUtils.readFile(node.getNode("{http://www.jcp.org/jcr/1.0}content"));
        }
        throw new RepositoryException("Unable to read file node: " + node.getPath());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void readFile(Node node, OutputStream output) throws RepositoryException, IOException {
        try (InputStream input = JcrUtils.readFile(node);){
            byte[] buffer = new byte[16384];
            int n = input.read(buffer);
            while (n != -1) {
                output.write(buffer, 0, n);
                n = input.read(buffer);
            }
        }
    }

    public static Calendar getLastModified(Node node) throws RepositoryException {
        if (node.hasProperty("{http://www.jcp.org/jcr/1.0}lastModified")) {
            return node.getProperty("{http://www.jcp.org/jcr/1.0}lastModified").getDate();
        }
        if (node.hasNode("{http://www.jcp.org/jcr/1.0}content")) {
            return JcrUtils.getLastModified(node.getNode("{http://www.jcp.org/jcr/1.0}content"));
        }
        return null;
    }

    public static void setLastModified(Node node, Calendar date) throws RepositoryException {
        if (node.hasNode("{http://www.jcp.org/jcr/1.0}content")) {
            JcrUtils.setLastModified(node.getNode("{http://www.jcp.org/jcr/1.0}content"), date);
        } else {
            node.setProperty("{http://www.jcp.org/jcr/1.0}lastModified", date);
        }
    }

    public static String toString(Item item) {
        StringBuilder builder = new StringBuilder();
        try {
            if (item.isNode()) {
                builder.append(item.getPath());
                builder.append(" [");
                builder.append(((Node)item).getPrimaryNodeType().getName());
                builder.append("]");
            } else {
                builder.append("@");
                builder.append(item.getName());
                builder.append(" = ");
                Property property = (Property)item;
                if (property.isMultiple()) {
                    builder.append("[ ");
                    Value[] values = property.getValues();
                    for (int i = 0; i < values.length && i < 3; ++i) {
                        if (i > 0) {
                            builder.append(", ");
                        }
                        JcrUtils.append(builder, values[i]);
                    }
                    if (values.length >= 3) {
                        builder.append(", ...");
                    }
                    builder.append(" ]");
                } else {
                    JcrUtils.append(builder, property.getValue());
                }
            }
        }
        catch (RepositoryException e) {
            builder.append("!!! ");
            builder.append(e.getMessage());
            builder.append(" !!!");
        }
        return builder.toString();
    }

    private static void append(StringBuilder builder, Value value) throws RepositoryException {
        if (value.getType() == 2) {
            Binary binary = value.getBinary();
            try {
                builder.append("<");
                builder.append(binary.getSize());
                builder.append(" bytes>");
            }
            finally {
                binary.dispose();
            }
        } else {
            String string = value.getString();
            if (string.length() > 40) {
                builder.append(string.substring(0, 37));
                builder.append("...");
            } else {
                builder.append(string);
            }
        }
    }

    public static int getPropertyType(String name) throws IllegalArgumentException {
        Integer type = PROPERTY_TYPES.get(name.toLowerCase());
        if (type != null) {
            return type;
        }
        throw new IllegalArgumentException("Unknown property type: " + name);
    }

    public static String[] getPropertyTypeNames(boolean includeUndefined) {
        if (includeUndefined) {
            return PROPERTY_TYPES_NAMES.toArray(new String[PROPERTY_TYPES_NAMES.size()]);
        }
        String[] typeNames = new String[PROPERTY_TYPES_NAMES.size() - 1];
        int i = 0;
        for (String name : PROPERTY_TYPES_NAMES) {
            if ("undefined".equals(name)) continue;
            typeNames[i++] = name;
        }
        return typeNames;
    }

    public static Node getOrCreateByPath(String absolutePath, String nodeType, Session session) throws RepositoryException {
        return JcrUtils.getOrCreateByPath(absolutePath, false, nodeType, nodeType, session, false);
    }

    public static Node getOrCreateByPath(String absolutePath, String intermediateNodeType, String nodeType, Session session, boolean autoSave) throws RepositoryException {
        return JcrUtils.getOrCreateByPath(absolutePath, false, intermediateNodeType, nodeType, session, autoSave);
    }

    public static Node getOrCreateUniqueByPath(String pathHint, String nodeType, Session session) throws RepositoryException {
        return JcrUtils.getOrCreateByPath(pathHint, true, nodeType, nodeType, session, false);
    }

    public static Node getOrCreateByPath(String absolutePath, boolean createUniqueLeaf, String intermediateNodeType, String nodeType, Session session, boolean autoSave) throws RepositoryException {
        if (absolutePath == null || absolutePath.length() == 0 || "/".equals(absolutePath)) {
            return session.getRootNode();
        }
        if (!absolutePath.startsWith("/")) {
            throw new IllegalArgumentException("not an absolute path: " + absolutePath);
        }
        if (session.nodeExists(absolutePath) && !createUniqueLeaf) {
            return session.getNode(absolutePath);
        }
        String path = absolutePath;
        int currentIndex = path.lastIndexOf(47);
        String existingPath = null;
        while (currentIndex > 0 && existingPath == null) {
            if (session.nodeExists(path = path.substring(0, currentIndex))) {
                existingPath = path;
                continue;
            }
            currentIndex = path.lastIndexOf(47);
        }
        return JcrUtils.getOrCreateByPath(existingPath == null ? session.getRootNode() : session.getNode(existingPath), absolutePath.substring(currentIndex + 1), createUniqueLeaf, intermediateNodeType, nodeType, autoSave);
    }

    public static Node getOrCreateUniqueByPath(Node parent, String nodeNameHint, String nodeType) throws RepositoryException {
        return JcrUtils.getOrCreateByPath(parent, nodeNameHint, true, nodeType, nodeType, false);
    }

    public static Node getOrCreateByPath(Node baseNode, String path, boolean createUniqueLeaf, String intermediateNodeType, String nodeType, boolean autoSave) throws RepositoryException {
        if (!createUniqueLeaf && baseNode.hasNode(path)) {
            return baseNode.getNode(path);
        }
        String fullPath = baseNode.getPath().equals("/") ? "/" + path : baseNode.getPath() + "/" + path;
        int currentIndex = fullPath.lastIndexOf(47);
        String temp = fullPath;
        String existingPath = null;
        while (currentIndex > 0) {
            temp = temp.substring(0, currentIndex);
            if (baseNode.getSession().itemExists(temp)) {
                existingPath = temp;
                break;
            }
            currentIndex = temp.lastIndexOf(47);
        }
        if (existingPath != null) {
            baseNode = baseNode.getSession().getNode(existingPath);
            path = fullPath.substring(existingPath.length() + 1);
        }
        Node node = baseNode;
        int pos = path.lastIndexOf(47);
        if (pos != -1) {
            StringTokenizer st = new StringTokenizer(path.substring(0, pos), "/");
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                if (!node.hasNode(token)) {
                    try {
                        if (intermediateNodeType != null) {
                            node.addNode(token, intermediateNodeType);
                        } else {
                            node.addNode(token);
                        }
                        if (autoSave) {
                            node.getSession().save();
                        }
                    }
                    catch (RepositoryException e) {
                        node.refresh(false);
                    }
                }
                node = node.getNode(token);
            }
            path = path.substring(pos + 1);
        }
        if (!node.hasNode(path)) {
            if (nodeType != null) {
                node.addNode(path, nodeType);
            } else {
                node.addNode(path);
            }
            if (autoSave) {
                node.getSession().save();
            }
        } else if (createUniqueLeaf) {
            String leafNodeName;
            int i = 0;
            do {
                leafNodeName = path + String.valueOf(i);
                ++i;
            } while (node.hasNode(leafNodeName));
            Node leaf = nodeType != null ? node.addNode(leafNodeName, nodeType) : node.addNode(leafNodeName);
            if (autoSave) {
                node.getSession().save();
            }
            return leaf;
        }
        return node.getNode(path);
    }

    public static Node getNodeIfExists(Node baseNode, String relPath) throws RepositoryException {
        try {
            return baseNode.getNode(relPath);
        }
        catch (PathNotFoundException e) {
            return null;
        }
    }

    public static Node getNodeIfExists(String absPath, Session session) throws RepositoryException {
        try {
            return session.getNode(absPath);
        }
        catch (PathNotFoundException e) {
            return null;
        }
    }

    public static String getStringProperty(Node baseNode, String relPath, String defaultValue) throws RepositoryException {
        try {
            return baseNode.getProperty(relPath).getString();
        }
        catch (PathNotFoundException e) {
            return defaultValue;
        }
    }

    public static long getLongProperty(Node baseNode, String relPath, long defaultValue) throws RepositoryException {
        try {
            return baseNode.getProperty(relPath).getLong();
        }
        catch (PathNotFoundException e) {
            return defaultValue;
        }
    }

    public static double getDoubleProperty(Node baseNode, String relPath, double defaultValue) throws RepositoryException {
        try {
            return baseNode.getProperty(relPath).getDouble();
        }
        catch (PathNotFoundException e) {
            return defaultValue;
        }
    }

    public static boolean getBooleanProperty(Node baseNode, String relPath, boolean defaultValue) throws RepositoryException {
        try {
            return baseNode.getProperty(relPath).getBoolean();
        }
        catch (PathNotFoundException e) {
            return defaultValue;
        }
    }

    public static Calendar getDateProperty(Node baseNode, String relPath, Calendar defaultValue) throws RepositoryException {
        try {
            return baseNode.getProperty(relPath).getDate();
        }
        catch (PathNotFoundException e) {
            return defaultValue;
        }
    }

    public static BigDecimal getDecimalProperty(Node baseNode, String relPath, BigDecimal defaultValue) throws RepositoryException {
        try {
            return baseNode.getProperty(relPath).getDecimal();
        }
        catch (PathNotFoundException e) {
            return defaultValue;
        }
    }

    public static Binary getBinaryProperty(Node baseNode, String relPath, Binary defaultValue) throws RepositoryException {
        try {
            return baseNode.getProperty(relPath).getBinary();
        }
        catch (PathNotFoundException e) {
            return defaultValue;
        }
    }

    public static String getStringProperty(Session session, String absPath, String defaultValue) throws RepositoryException {
        try {
            return session.getProperty(absPath).getString();
        }
        catch (PathNotFoundException e) {
            return defaultValue;
        }
    }

    public static long getLongProperty(Session session, String absPath, long defaultValue) throws RepositoryException {
        try {
            return session.getProperty(absPath).getLong();
        }
        catch (PathNotFoundException e) {
            return defaultValue;
        }
    }

    public static double getDoubleProperty(Session session, String absPath, double defaultValue) throws RepositoryException {
        try {
            return session.getProperty(absPath).getDouble();
        }
        catch (PathNotFoundException e) {
            return defaultValue;
        }
    }

    public static boolean getBooleanProperty(Session session, String absPath, boolean defaultValue) throws RepositoryException {
        try {
            return session.getProperty(absPath).getBoolean();
        }
        catch (PathNotFoundException e) {
            return defaultValue;
        }
    }

    public static Calendar getDateProperty(Session session, String absPath, Calendar defaultValue) throws RepositoryException {
        try {
            return session.getProperty(absPath).getDate();
        }
        catch (PathNotFoundException e) {
            return defaultValue;
        }
    }

    public static BigDecimal getDecimalProperty(Session session, String absPath, BigDecimal defaultValue) throws RepositoryException {
        try {
            return session.getProperty(absPath).getDecimal();
        }
        catch (PathNotFoundException e) {
            return defaultValue;
        }
    }

    public static Binary getBinaryProperty(Session session, String absPath, Binary defaultValue) throws RepositoryException {
        try {
            return session.getProperty(absPath).getBinary();
        }
        catch (PathNotFoundException e) {
            return defaultValue;
        }
    }

    static {
        for (int i = 0; i <= 12; ++i) {
            String typeName = PropertyType.nameFromValue(i);
            PROPERTY_TYPES_NAMES.add(typeName);
            PROPERTY_TYPES.put(typeName.toLowerCase(), i);
        }
    }
}

