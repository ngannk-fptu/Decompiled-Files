/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.server.remoting.davex;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import javax.jcr.Item;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.ValueFormatException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.ItemDefinition;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeManager;
import org.apache.jackrabbit.commons.json.JsonHandler;
import org.apache.jackrabbit.commons.json.JsonParser;
import org.apache.jackrabbit.commons.webdav.JcrValueType;
import org.apache.jackrabbit.server.remoting.davex.DiffException;
import org.apache.jackrabbit.server.remoting.davex.DiffHandler;
import org.apache.jackrabbit.server.remoting.davex.ProtectedRemoveManager;
import org.apache.jackrabbit.server.util.RequestData;
import org.apache.jackrabbit.util.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

class JsonDiffHandler
implements DiffHandler {
    private static final Logger log = LoggerFactory.getLogger(JsonDiffHandler.class);
    private static final String ORDER_POSITION_AFTER = "#after";
    private static final String ORDER_POSITION_BEFORE = "#before";
    private static final String ORDER_POSITION_FIRST = "#first";
    private static final String ORDER_POSITION_LAST = "#last";
    private final Session session;
    private final ValueFactory vf;
    private final String requestItemPath;
    private final RequestData data;
    private final ProtectedRemoveManager protectedRemoveManager;
    private NodeTypeManager ntManager;

    JsonDiffHandler(Session session, String requestItemPath, RequestData data) throws RepositoryException {
        this(session, requestItemPath, data, null);
    }

    JsonDiffHandler(Session session, String requestItemPath, RequestData data, ProtectedRemoveManager protectedRemoveManager) throws RepositoryException {
        this.session = session;
        this.requestItemPath = requestItemPath;
        this.data = data;
        this.vf = session.getValueFactory();
        this.protectedRemoveManager = protectedRemoveManager;
    }

    @Override
    public void addNode(String targetPath, String diffValue) throws DiffException {
        if (diffValue == null || !diffValue.startsWith("{") || !diffValue.endsWith("}")) {
            throw new DiffException("Invalid 'addNode' value '" + diffValue + "'");
        }
        try {
            String itemPath = this.getItemPath(targetPath);
            String parentPath = Text.getRelativeParent(itemPath, 1);
            String nodeName = Text.getName(itemPath);
            this.addNode(parentPath, nodeName, diffValue);
        }
        catch (RepositoryException e) {
            throw new DiffException(e.getMessage(), e);
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void setProperty(String targetPath, String diffValue) throws DiffException {
        try {
            String itemPath = this.getItemPath(targetPath);
            Item item = this.session.getItem(Text.getRelativeParent(itemPath, 1));
            if (!item.isNode()) {
                throw new DiffException("No such node " + itemPath, new ItemNotFoundException(itemPath));
            }
            Node parent = (Node)item;
            String propName = Text.getName(itemPath);
            if ("jcr:mixinTypes".equals(propName)) {
                JsonDiffHandler.setMixins(parent, this.extractValuesFromRequest(targetPath));
                return;
            } else if ("jcr:primaryType".equals(propName)) {
                JsonDiffHandler.setPrimaryType(parent, this.extractValuesFromRequest(targetPath));
                return;
            } else if (diffValue == null || diffValue.length() == 0) {
                Value[] vs = this.extractValuesFromRequest(targetPath);
                if (vs.length == 0) {
                    if (parent.hasProperty(propName)) {
                        parent.getProperty(propName).remove();
                        return;
                    } else {
                        parent.setProperty(propName, (Value)null);
                    }
                    return;
                } else {
                    if (vs.length != 1) throw new DiffException("Unexpected number of values in multipart. Was " + vs.length + " but expected 1.");
                    parent.setProperty(propName, vs[0]);
                }
                return;
            } else if (diffValue.startsWith("[") && diffValue.endsWith("]")) {
                if (diffValue.length() == 2) {
                    Value[] vs = this.extractValuesFromRequest(targetPath);
                    parent.setProperty(propName, vs);
                    return;
                } else {
                    Value[] vs = this.extractValues(diffValue);
                    parent.setProperty(propName, vs);
                }
                return;
            } else {
                Value v = this.extractValue(diffValue);
                parent.setProperty(propName, v);
            }
            return;
        }
        catch (RepositoryException e) {
            throw new DiffException(e.getMessage(), e);
        }
        catch (IOException e) {
            if (!(e instanceof DiffException)) throw new DiffException(e.getMessage(), e);
            throw (DiffException)e;
        }
    }

    @Override
    public void remove(String targetPath, String diffValue) throws DiffException {
        if (diffValue != null && diffValue.trim().length() != 0) {
            throw new DiffException("'remove' may not have a diffValue.");
        }
        try {
            ItemDefinition def;
            String itemPath = this.getItemPath(targetPath);
            Item item = this.session.getItem(itemPath);
            ItemDefinition itemDefinition = def = item.isNode() ? ((Node)item).getDefinition() : ((Property)item).getDefinition();
            if (def.isProtected()) {
                if (this.protectedRemoveManager == null || !this.protectedRemoveManager.remove(this.session, itemPath)) {
                    throw new ConstraintViolationException("Cannot remove protected node: no suitable handler configured.");
                }
            } else {
                item.remove();
            }
        }
        catch (RepositoryException e) {
            throw new DiffException(e.getMessage(), e);
        }
    }

    @Override
    public void move(String targetPath, String diffValue) throws DiffException {
        if (diffValue == null || diffValue.length() == 0) {
            throw new DiffException("Invalid 'move' value '" + diffValue + "'");
        }
        try {
            String srcPath = this.getItemPath(targetPath);
            String orderPosition = JsonDiffHandler.getOrderPosition(diffValue);
            if (orderPosition == null) {
                String destPath = this.getItemPath(diffValue);
                this.session.move(srcPath, destPath);
            } else {
                String srcName = Text.getName(srcPath);
                int pos = diffValue.lastIndexOf(35);
                String destName = pos == 0 ? null : Text.getName(diffValue.substring(0, pos));
                Item item = this.session.getItem(Text.getRelativeParent(srcPath, 1));
                if (!item.isNode()) {
                    throw new ItemNotFoundException(srcPath);
                }
                Node parent = (Node)item;
                if (ORDER_POSITION_FIRST.equals(orderPosition)) {
                    if (destName != null) {
                        throw new DiffException("#first may not have a leading destination.");
                    }
                    destName = Text.getName(parent.getNodes().nextNode().getPath());
                    parent.orderBefore(srcName, destName);
                } else if (ORDER_POSITION_LAST.equals(orderPosition)) {
                    if (destName != null) {
                        throw new DiffException("#last may not have a leading destination.");
                    }
                    parent.orderBefore(srcName, null);
                } else if (ORDER_POSITION_AFTER.equals(orderPosition)) {
                    if (destName == null) {
                        throw new DiffException("#after must have a leading destination.");
                    }
                    NodeIterator it = parent.getNodes();
                    while (it.hasNext()) {
                        Node child = it.nextNode();
                        if (!destName.equals(child.getName())) continue;
                        if (it.hasNext()) {
                            destName = Text.getName(it.nextNode().getName());
                            break;
                        }
                        destName = null;
                        break;
                    }
                    parent.orderBefore(srcName, destName);
                } else {
                    parent.orderBefore(srcName, destName);
                }
            }
        }
        catch (RepositoryException e) {
            throw new DiffException(e.getMessage(), e);
        }
    }

    String getItemPath(String diffPath) throws RepositoryException {
        StringBuffer itemPath;
        if (!diffPath.startsWith("/")) {
            itemPath = new StringBuffer(this.requestItemPath);
            if (!this.requestItemPath.endsWith("/")) {
                itemPath.append('/');
            }
            itemPath.append(diffPath);
        } else {
            itemPath = new StringBuffer(diffPath);
        }
        return JsonDiffHandler.normalize(itemPath.toString());
    }

    private void addNode(String parentPath, String nodeName, String diffValue) throws DiffException, RepositoryException {
        Item item = this.session.getItem(parentPath);
        if (!item.isNode()) {
            throw new ItemNotFoundException(parentPath);
        }
        Node parent = (Node)item;
        try {
            NodeHandler hndlr = new NodeHandler(parent, nodeName);
            new JsonParser(hndlr).parse(diffValue);
        }
        catch (IOException e) {
            if (e instanceof DiffException) {
                throw (DiffException)e;
            }
            throw new DiffException(e.getMessage(), e);
        }
    }

    private NodeTypeManager getNodeTypeManager() throws RepositoryException {
        if (this.ntManager == null) {
            this.ntManager = this.session.getWorkspace().getNodeTypeManager();
        }
        return this.ntManager;
    }

    private static String normalize(String path) {
        if (path.indexOf(46) == -1) {
            return path;
        }
        String[] elems = Text.explode(path, 47, false);
        LinkedList<String> queue = new LinkedList<String>();
        String last = "..";
        for (String segm : elems) {
            if ("..".equals(segm) && !"..".equals(last)) {
                queue.removeLast();
                if (queue.isEmpty()) {
                    last = "..";
                    continue;
                }
                last = (String)queue.getLast();
                continue;
            }
            if (".".equals(segm)) continue;
            last = segm;
            queue.add(last);
        }
        return "/" + Text.implode(queue.toArray(new String[queue.size()]), "/");
    }

    private static ContentHandler createContentHandler(Node parent) throws RepositoryException {
        return parent.getSession().getImportContentHandler(parent.getPath(), 3);
    }

    private static Node importNode(Node parent, String nodeName, String ntName, String uuid) throws RepositoryException {
        String uri = "http://www.jcp.org/jcr/sv/1.0";
        String prefix = "sv:";
        ContentHandler ch = JsonDiffHandler.createContentHandler(parent);
        try {
            ch.startDocument();
            String nN = "node";
            AttributesImpl attrs = new AttributesImpl();
            attrs.addAttribute(uri, "name", prefix + "name", "CDATA", nodeName);
            ch.startElement(uri, nN, prefix + nN, attrs);
            String pN = "property";
            attrs = new AttributesImpl();
            attrs.addAttribute(uri, "name", prefix + "name", "CDATA", "jcr:primaryType");
            attrs.addAttribute(uri, "type", prefix + "type", "CDATA", PropertyType.nameFromValue(7));
            ch.startElement(uri, pN, prefix + pN, attrs);
            ch.startElement(uri, "value", prefix + "value", new AttributesImpl());
            char[] val = ntName.toCharArray();
            ch.characters(val, 0, val.length);
            ch.endElement(uri, "value", prefix + "value");
            ch.endElement(uri, pN, prefix + pN);
            attrs = new AttributesImpl();
            attrs.addAttribute(uri, "name", prefix + "name", "CDATA", "jcr:uuid");
            attrs.addAttribute(uri, "type", prefix + "type", "CDATA", PropertyType.nameFromValue(1));
            ch.startElement(uri, pN, prefix + pN, attrs);
            ch.startElement(uri, "value", prefix + "value", new AttributesImpl());
            val = uuid.toCharArray();
            ch.characters(val, 0, val.length);
            ch.endElement(uri, "value", prefix + "value");
            ch.endElement(uri, pN, prefix + pN);
            ch.endElement(uri, nN, prefix + nN);
            ch.endDocument();
        }
        catch (SAXException e) {
            throw new RepositoryException(e);
        }
        Node n = null;
        NodeIterator it = parent.getNodes(nodeName);
        while (it.hasNext()) {
            n = it.nextNode();
        }
        if (n == null) {
            throw new RepositoryException("Internal error: No child node added.");
        }
        return n;
    }

    private static void setPrimaryType(Node n, Value[] values) throws RepositoryException, DiffException {
        if (values.length == 1) {
            String ntName = values[0].getString();
            if (!ntName.equals(n.getPrimaryNodeType().getName())) {
                n.setPrimaryType(ntName);
            }
        } else {
            throw new DiffException("Invalid diff: jcr:primarytype cannot have multiple values, nor can it's value be removed.");
        }
    }

    private static void setMixins(Node n, Value[] values) throws RepositoryException {
        if (values.length == 0) {
            NodeType[] mixins;
            for (NodeType mixin : mixins = n.getMixinNodeTypes()) {
                String mixinName = mixin.getName();
                n.removeMixin(mixinName);
            }
        } else {
            ArrayList<String> newMixins = new ArrayList<String>(values.length);
            for (Value value : values) {
                newMixins.add(value.getString());
            }
            NodeType[] mixins = n.getMixinNodeTypes();
            for (NodeType mixin : mixins) {
                String mixinName = mixin.getName();
                if (newMixins.remove(mixinName)) continue;
                n.removeMixin(mixinName);
            }
            for (String newMixinName : newMixins) {
                n.addMixin(newMixinName);
            }
        }
    }

    private static String getOrderPosition(String diffValue) {
        String position = null;
        if (diffValue.indexOf(35) > -1 && (diffValue.endsWith(ORDER_POSITION_FIRST) || diffValue.endsWith(ORDER_POSITION_LAST) || diffValue.endsWith(ORDER_POSITION_BEFORE) || diffValue.endsWith(ORDER_POSITION_AFTER))) {
            position = diffValue.substring(diffValue.lastIndexOf(35));
        }
        return position;
    }

    private Value[] extractValuesFromRequest(String paramName) throws RepositoryException, IOException {
        Value[] vs;
        ValueFactory vf = this.session.getValueFactory();
        InputStream[] ins = this.data.getFileParameters(paramName);
        if (ins != null) {
            vs = new Value[ins.length];
            for (int i = 0; i < ins.length; ++i) {
                vs[i] = vf.createValue(ins[i]);
            }
        } else {
            String[] strs = this.data.getParameterValues(paramName);
            if (strs == null) {
                vs = new Value[]{};
            } else {
                ArrayList<Value> valList = new ArrayList<Value>(strs.length);
                for (int i = 0; i < strs.length; ++i) {
                    int type;
                    if (strs[i] == null) continue;
                    String[] types = this.data.getParameterTypes(paramName);
                    int n = type = types == null || types.length <= i ? 0 : JcrValueType.typeFromContentType(types[i]);
                    if (type == 0) {
                        valList.add(vf.createValue(strs[i]));
                        continue;
                    }
                    valList.add(vf.createValue(strs[i], type));
                }
                vs = valList.toArray(new Value[valList.size()]);
            }
        }
        return vs;
    }

    private Value extractValue(String diffValue) throws RepositoryException, DiffException, IOException {
        ValueHandler hndlr = new ValueHandler();
        new JsonParser(hndlr).parse("{\"a\":" + diffValue + "}");
        return hndlr.getValue();
    }

    private Value[] extractValues(String diffValue) throws RepositoryException, DiffException, IOException {
        ValuesHandler hndlr = new ValuesHandler();
        new JsonParser(hndlr).parse("{\"a\":" + diffValue + "}");
        return hndlr.getValues();
    }

    private final class ImportMvProp
    extends ImportProperty {
        private List<Value> values;

        private ImportMvProp(String parentPath, String name) throws IOException {
            super(parentPath, name);
            this.values = new ArrayList<Value>();
        }

        @Override
        void createItem(Node parent) throws RepositoryException {
            Value[] vls = this.values.toArray(new Value[this.values.size()]);
            if ("jcr:mixinTypes".equals(this.name)) {
                JsonDiffHandler.setMixins(parent, vls);
            } else {
                parent.setProperty(this.name, vls);
            }
        }

        @Override
        void startValueElement(ContentHandler contentHandler) throws IOException {
            try {
                if (this.values.size() == 0) {
                    this.values = Arrays.asList(JsonDiffHandler.this.extractValuesFromRequest(this.getPath()));
                }
                for (Value v : this.values) {
                    String str = v.getString();
                    contentHandler.startElement("http://www.jcp.org/jcr/sv/1.0", "value", "sv:value", new AttributesImpl());
                    contentHandler.characters(str.toCharArray(), 0, str.length());
                    contentHandler.endElement("http://www.jcp.org/jcr/sv/1.0", "value", "sv:value");
                }
            }
            catch (SAXException e) {
                throw new DiffException(e.getMessage());
            }
            catch (ValueFormatException e) {
                throw new DiffException(e.getMessage());
            }
            catch (RepositoryException e) {
                throw new DiffException(e.getMessage());
            }
        }
    }

    private final class ImportProp
    extends ImportProperty {
        private final Value value;

        private ImportProp(String parentPath, String name, Value value) throws IOException {
            super(parentPath, name);
            try {
                this.value = value == null ? JsonDiffHandler.this.extractValuesFromRequest(this.getPath())[0] : value;
            }
            catch (RepositoryException e) {
                throw new DiffException(e.getMessage(), e);
            }
        }

        @Override
        void createItem(Node parent) throws RepositoryException {
            parent.setProperty(this.name, this.value);
        }

        @Override
        void startValueElement(ContentHandler contentHandler) throws IOException {
            try {
                String str = this.value.getString();
                contentHandler.startElement("http://www.jcp.org/jcr/sv/1.0", "value", "sv:value", new AttributesImpl());
                contentHandler.characters(str.toCharArray(), 0, str.length());
                contentHandler.endElement("http://www.jcp.org/jcr/sv/1.0", "value", "sv:value");
            }
            catch (SAXException e) {
                throw new DiffException(e.getMessage());
            }
            catch (ValueFormatException e) {
                throw new DiffException(e.getMessage());
            }
            catch (RepositoryException e) {
                throw new DiffException(e.getMessage());
            }
        }
    }

    private abstract class ImportProperty
    extends ImportItem {
        static final String VALUE = "value";
        static final String TYPE = "type";
        static final String LOCAL_NAME = "property";

        private ImportProperty(String parentPath, String name) throws IOException {
            super(parentPath, name);
        }

        @Override
        boolean mandatesImport(Node parent) {
            return false;
        }

        @Override
        void importItem(ContentHandler contentHandler) throws IOException {
            try {
                AttributesImpl propAtts = new AttributesImpl();
                this.setNameAttribute(propAtts);
                this.setTypeAttribute(propAtts);
                contentHandler.startElement("http://www.jcp.org/jcr/sv/1.0", LOCAL_NAME, "sv:property", propAtts);
                this.startValueElement(contentHandler);
                contentHandler.endElement("http://www.jcp.org/jcr/sv/1.0", LOCAL_NAME, "sv:property");
            }
            catch (SAXException e) {
                throw new DiffException(e.getMessage(), e);
            }
        }

        void setTypeAttribute(AttributesImpl attr) {
            String type = null;
            type = this.name.equals("jcr:primaryType") ? PropertyType.nameFromValue(7) : (this.name.equals("jcr:mixinTypes") ? PropertyType.nameFromValue(7) : (this.name.equals("jcr:uuid") ? PropertyType.nameFromValue(1) : PropertyType.nameFromValue(0)));
            attr.addAttribute("http://www.jcp.org/jcr/sv/1.0", TYPE, "sv:type", "CDATA", type);
        }

        abstract void startValueElement(ContentHandler var1) throws IOException;
    }

    private final class ImportNode
    extends ImportItem {
        private static final String LOCAL_NAME = "node";
        private ImportProp ntName;
        private ImportProp uuid;
        private List<ImportNode> childN;
        private List<ImportProperty> childP;

        private ImportNode(String parentPath, String name) throws IOException {
            super(parentPath, name);
            this.childN = new ArrayList<ImportNode>();
            this.childP = new ArrayList<ImportProperty>();
        }

        private String getUUID() {
            if (this.uuid != null && this.uuid.value != null) {
                try {
                    return this.uuid.value.getString();
                }
                catch (RepositoryException e) {
                    log.error(e.getMessage());
                }
            }
            return null;
        }

        private String getPrimaryType() {
            if (this.ntName != null && this.ntName.value != null) {
                try {
                    return this.ntName.value.getString();
                }
                catch (RepositoryException e) {
                    log.error(e.getMessage());
                }
            }
            return null;
        }

        @Override
        boolean mandatesImport(Node parent) {
            String primaryType = this.getPrimaryType();
            if (!primaryType.startsWith("nt")) {
                try {
                    NodeType nt = JsonDiffHandler.this.getNodeTypeManager().getNodeType(primaryType);
                    for (NodeDefinition nodeDefinition : nt.getChildNodeDefinitions()) {
                        if (!nodeDefinition.isProtected()) continue;
                        return true;
                    }
                    for (ItemDefinition itemDefinition : nt.getPropertyDefinitions()) {
                        if (itemDefinition.getName().startsWith("jcr") || !itemDefinition.isProtected()) continue;
                        return true;
                    }
                }
                catch (RepositoryException e) {
                    log.warn(e.getMessage(), (Throwable)e);
                }
            }
            return false;
        }

        void addProp(ImportProp prop) {
            if (prop.name.equals("jcr:primaryType")) {
                this.ntName = prop;
            } else if (prop.name.equals("jcr:uuid")) {
                this.uuid = prop;
            } else {
                this.childP.add(prop);
            }
        }

        void addProp(ImportMvProp prop) {
            this.childP.add(prop);
        }

        void addNode(ImportNode node) {
            this.childN.add(node);
        }

        @Override
        void importItem(ContentHandler contentHandler) throws IOException {
            try {
                AttributesImpl attr = new AttributesImpl();
                this.setNameAttribute(attr);
                contentHandler.startElement("http://www.jcp.org/jcr/sv/1.0", LOCAL_NAME, "sv:node", attr);
                if (this.ntName != null && this.ntName.value != null) {
                    this.ntName.importItem(contentHandler);
                }
                if (this.uuid != null && this.uuid.value != null) {
                    this.uuid.importItem(contentHandler);
                }
                for (ImportProperty prop : this.childP) {
                    prop.importItem(contentHandler);
                }
                for (ImportNode node : this.childN) {
                    node.importItem(contentHandler);
                }
                contentHandler.endElement("http://www.jcp.org/jcr/sv/1.0", LOCAL_NAME, "sv:node");
            }
            catch (SAXException e) {
                throw new DiffException(e.getMessage(), e);
            }
        }

        @Override
        void createItem(Node parent) throws RepositoryException, IOException {
            if (this.mandatesImport(parent)) {
                ContentHandler ch = JsonDiffHandler.createContentHandler(parent);
                try {
                    ch.startDocument();
                    this.importItem(ch);
                    ch.endDocument();
                }
                catch (SAXException e) {
                    throw new DiffException(e.getMessage(), e);
                }
            } else {
                String uuidValue = this.getUUID();
                String primaryType = this.getPrimaryType();
                Node n = uuidValue == null ? (primaryType == null ? parent.addNode(this.name) : parent.addNode(this.name, primaryType)) : JsonDiffHandler.importNode(parent, this.name, primaryType, uuidValue);
                for (ImportItem importItem : this.childP) {
                    importItem.createItem(n);
                }
                for (ImportItem importItem : this.childN) {
                    importItem.createItem(n);
                }
            }
        }
    }

    private abstract class ImportItem {
        static final String TYPE_CDATA = "CDATA";
        final String parentPath;
        final String name;
        final String path;

        private ImportItem(String parentPath, String name) throws IOException {
            if (name == null) {
                throw new DiffException("Invalid DIFF format: NULL key.");
            }
            this.name = name;
            this.parentPath = parentPath;
            this.path = parentPath + "/" + name;
        }

        void setNameAttribute(AttributesImpl attr) {
            attr.addAttribute("http://www.jcp.org/jcr/sv/1.0", "name", "sv:name", TYPE_CDATA, this.name);
        }

        String getPath() {
            return this.path;
        }

        abstract boolean mandatesImport(Node var1);

        abstract void createItem(Node var1) throws RepositoryException, IOException;

        abstract void importItem(ContentHandler var1) throws IOException;
    }

    private final class NodeHandler
    implements JsonHandler {
        private Node parent;
        private String key;
        private Stack<ImportItem> st = new Stack();

        private NodeHandler(Node parent, String nodeName) throws IOException {
            this.parent = parent;
            this.key = nodeName;
        }

        @Override
        public void object() throws IOException {
            ImportNode n;
            if (this.st.isEmpty()) {
                try {
                    n = new ImportNode(this.parent.getPath(), this.key);
                }
                catch (RepositoryException e) {
                    throw new DiffException(e.getMessage(), e);
                }
            } else {
                ImportItem obj = this.st.peek();
                n = new ImportNode(obj.getPath(), this.key);
                if (obj instanceof ImportNode) {
                    ((ImportNode)obj).addNode(n);
                } else {
                    throw new DiffException("Invalid DIFF format: The JSONArray may only contain simple values.");
                }
            }
            this.st.push(n);
        }

        @Override
        public void endObject() throws IOException {
            ImportItem obj = this.st.pop();
            if (!(obj instanceof ImportNode)) {
                throw new DiffException("Invalid DIFF format.");
            }
            if (this.st.isEmpty()) {
                try {
                    if (obj.mandatesImport(this.parent)) {
                        obj.importItem(JsonDiffHandler.createContentHandler(this.parent));
                    } else {
                        obj.createItem(this.parent);
                    }
                }
                catch (IOException e) {
                    log.error(e.getMessage());
                    throw new DiffException(e.getMessage(), e);
                }
                catch (RepositoryException e) {
                    log.error(e.getMessage());
                    throw new DiffException(e.getMessage(), e);
                }
            }
        }

        @Override
        public void array() throws IOException {
            ImportItem obj = this.st.peek();
            ImportMvProp prop = new ImportMvProp(obj.getPath(), this.key);
            if (!(obj instanceof ImportNode)) {
                throw new DiffException("Invalid DIFF format: The JSONArray may only contain simple values.");
            }
            ((ImportNode)obj).addProp(prop);
            this.st.push(prop);
        }

        @Override
        public void endArray() throws IOException {
            ImportItem obj = this.st.pop();
            if (!(obj instanceof ImportMvProp)) {
                throw new DiffException("Invalid DIFF format: The JSONArray may only contain simple values.");
            }
        }

        @Override
        public void key(String key) throws IOException {
            this.key = key;
        }

        @Override
        public void value(String value) throws IOException {
            Value v = value == null ? null : JsonDiffHandler.this.vf.createValue(value);
            this.value(v);
        }

        @Override
        public void value(boolean value) throws IOException {
            this.value(JsonDiffHandler.this.vf.createValue(value));
        }

        @Override
        public void value(long value) throws IOException {
            Value v = JsonDiffHandler.this.vf.createValue(value);
            this.value(v);
        }

        @Override
        public void value(double value) throws IOException {
            this.value(JsonDiffHandler.this.vf.createValue(value));
        }

        private void value(Value v) throws IOException {
            ImportItem obj = this.st.peek();
            if (obj instanceof ImportMvProp) {
                ((ImportMvProp)obj).values.add(v);
            } else {
                ((ImportNode)obj).addProp(new ImportProp(obj.getPath(), this.key, v));
            }
        }
    }

    private final class ValuesHandler
    implements JsonHandler {
        private List<Value> values = new ArrayList<Value>();

        private ValuesHandler() {
        }

        @Override
        public void object() throws IOException {
        }

        @Override
        public void endObject() throws IOException {
        }

        @Override
        public void array() throws IOException {
        }

        @Override
        public void endArray() throws IOException {
        }

        @Override
        public void key(String key) throws IOException {
        }

        @Override
        public void value(String value) throws IOException {
            if (value != null) {
                this.values.add(JsonDiffHandler.this.vf.createValue(value));
            } else {
                log.warn("Null element for a multivalued property -> Ignore.");
            }
        }

        @Override
        public void value(boolean value) throws IOException {
            this.values.add(JsonDiffHandler.this.vf.createValue(value));
        }

        @Override
        public void value(long value) throws IOException {
            this.values.add(JsonDiffHandler.this.vf.createValue(value));
        }

        @Override
        public void value(double value) throws IOException {
            this.values.add(JsonDiffHandler.this.vf.createValue(value));
        }

        private Value[] getValues() {
            return this.values.toArray(new Value[this.values.size()]);
        }
    }

    private final class ValueHandler
    implements JsonHandler {
        private Value v;

        private ValueHandler() {
        }

        @Override
        public void object() throws IOException {
        }

        @Override
        public void endObject() throws IOException {
        }

        @Override
        public void array() throws IOException {
        }

        @Override
        public void endArray() throws IOException {
        }

        @Override
        public void key(String key) throws IOException {
        }

        @Override
        public void value(String value) throws IOException {
            this.v = value == null ? null : JsonDiffHandler.this.vf.createValue(value);
        }

        @Override
        public void value(boolean value) throws IOException {
            this.v = JsonDiffHandler.this.vf.createValue(value);
        }

        @Override
        public void value(long value) throws IOException {
            this.v = JsonDiffHandler.this.vf.createValue(value);
        }

        @Override
        public void value(double value) throws IOException {
            this.v = JsonDiffHandler.this.vf.createValue(value);
        }

        private Value getValue() {
            return this.v;
        }
    }
}

