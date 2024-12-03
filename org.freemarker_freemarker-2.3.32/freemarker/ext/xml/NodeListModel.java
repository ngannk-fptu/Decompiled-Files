/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.xml;

import freemarker.ext.xml.Namespaces;
import freemarker.ext.xml.Navigator;
import freemarker.ext.xml.NodeOperator;
import freemarker.log.Logger;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNodeModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.utility.ClassUtil;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

@Deprecated
public class NodeListModel
implements TemplateHashModel,
TemplateMethodModel,
TemplateScalarModel,
TemplateSequenceModel,
TemplateNodeModel {
    private static final Logger LOG = Logger.getLogger("freemarker.xml");
    private static final Class DOM_NODE_CLASS = NodeListModel.getClass("org.w3c.dom.Node");
    private static final Class DOM4J_NODE_CLASS = NodeListModel.getClass("org.dom4j.Node");
    private static final Navigator DOM_NAVIGATOR = NodeListModel.getNavigator("Dom");
    private static final Navigator DOM4J_NAVIGATOR = NodeListModel.getNavigator("Dom4j");
    private static final Navigator JDOM_NAVIGATOR = NodeListModel.getNavigator("Jdom");
    private static volatile boolean useJaxenNamespaces = true;
    private final Navigator navigator;
    private final List nodes;
    private Namespaces namespaces;

    public NodeListModel(Object nodes) {
        Object node = nodes;
        if (nodes instanceof Collection) {
            this.nodes = new ArrayList((Collection)nodes);
            node = this.nodes.isEmpty() ? null : this.nodes.get(0);
        } else if (nodes != null) {
            this.nodes = Collections.singletonList(nodes);
        } else {
            throw new IllegalArgumentException("nodes == null");
        }
        this.navigator = DOM_NODE_CLASS != null && DOM_NODE_CLASS.isInstance(node) ? DOM_NAVIGATOR : (DOM4J_NODE_CLASS != null && DOM4J_NODE_CLASS.isInstance(node) ? DOM4J_NAVIGATOR : JDOM_NAVIGATOR);
        this.namespaces = this.createNamespaces();
    }

    private Namespaces createNamespaces() {
        if (useJaxenNamespaces) {
            try {
                return (Namespaces)Class.forName("freemarker.ext.xml._JaxenNamespaces").newInstance();
            }
            catch (Throwable t) {
                useJaxenNamespaces = false;
            }
        }
        return new Namespaces();
    }

    private NodeListModel(Navigator navigator, List nodes, Namespaces namespaces) {
        this.navigator = navigator;
        this.nodes = nodes;
        this.namespaces = namespaces;
    }

    private NodeListModel deriveModel(List derivedNodes) {
        this.namespaces.markShared();
        return new NodeListModel(this.navigator, derivedNodes, this.namespaces);
    }

    @Override
    public int size() {
        return this.nodes.size();
    }

    @Override
    public Object exec(List arguments) throws TemplateModelException {
        if (arguments.size() != 1) {
            throw new TemplateModelException("Expecting exactly one argument - an XPath expression");
        }
        return this.deriveModel(this.navigator.applyXPath(this.nodes, (String)arguments.get(0), this.namespaces));
    }

    @Override
    public String getAsString() throws TemplateModelException {
        StringWriter sw = new StringWriter(this.size() * 128);
        for (Object o : this.nodes) {
            if (o instanceof String) {
                sw.write((String)o);
                continue;
            }
            this.navigator.getAsString(o, sw);
        }
        return sw.toString();
    }

    @Override
    public TemplateModel get(int index) {
        return this.deriveModel(Collections.singletonList(this.nodes.get(index)));
    }

    @Override
    public TemplateModel get(String key) throws TemplateModelException {
        NodeOperator op = this.navigator.getOperator(key);
        String localName = null;
        String namespaceUri = "";
        if (op == null && key.length() > 0 && key.charAt(0) == '_') {
            if (key.equals("_unique")) {
                return this.deriveModel(NodeListModel.removeDuplicates(this.nodes));
            }
            if (key.equals("_filterType") || key.equals("_ftype")) {
                return new FilterByType();
            }
            if (key.equals("_registerNamespace")) {
                if (this.namespaces.isShared()) {
                    this.namespaces = (Namespaces)this.namespaces.clone();
                }
                return this.namespaces;
            }
        }
        if (op == null) {
            int colon = key.indexOf(58);
            if (colon == -1) {
                localName = key;
            } else {
                localName = key.substring(colon + 1);
                String prefix = key.substring(0, colon);
                namespaceUri = this.namespaces.translateNamespacePrefixToUri(prefix);
                if (namespaceUri == null) {
                    throw new TemplateModelException("Namespace prefix " + prefix + " is not registered.");
                }
            }
            if (localName.charAt(0) == '@') {
                op = this.navigator.getAttributeOperator();
                localName = localName.substring(1);
            } else {
                op = this.navigator.getChildrenOperator();
            }
        }
        ArrayList result = new ArrayList();
        Iterator iter = this.nodes.iterator();
        while (iter.hasNext()) {
            try {
                op.process(iter.next(), localName, namespaceUri, result);
            }
            catch (RuntimeException e) {
                throw new TemplateModelException(e);
            }
        }
        return this.deriveModel(result);
    }

    @Override
    public boolean isEmpty() {
        return this.nodes.isEmpty();
    }

    public void registerNamespace(String prefix, String uri) {
        if (this.namespaces.isShared()) {
            this.namespaces = (Namespaces)this.namespaces.clone();
        }
        this.namespaces.registerNamespace(prefix, uri);
    }

    private static final List removeDuplicates(List list) {
        int s = list.size();
        ArrayList ulist = new ArrayList(s);
        HashSet set = new HashSet(s * 4 / 3, 0.75f);
        for (Object o : list) {
            if (!set.add(o)) continue;
            ulist.add(o);
        }
        return ulist;
    }

    private static Class getClass(String className) {
        try {
            return ClassUtil.forName(className);
        }
        catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Couldn't load class " + className, e);
            }
            return null;
        }
    }

    private static Navigator getNavigator(String navType) {
        try {
            return (Navigator)ClassUtil.forName("freemarker.ext.xml._" + navType + "Navigator").newInstance();
        }
        catch (Throwable t) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Could not load navigator for " + navType, t);
            }
            return null;
        }
    }

    @Override
    public TemplateSequenceModel getChildNodes() throws TemplateModelException {
        return (TemplateSequenceModel)this.get("_content");
    }

    @Override
    public String getNodeName() throws TemplateModelException {
        return this.getUniqueText((NodeListModel)this.get("_name"), "name");
    }

    @Override
    public String getNodeNamespace() throws TemplateModelException {
        return this.getUniqueText((NodeListModel)this.get("_nsuri"), "namespace");
    }

    @Override
    public String getNodeType() throws TemplateModelException {
        return this.getUniqueText((NodeListModel)this.get("_type"), "type");
    }

    @Override
    public TemplateNodeModel getParentNode() throws TemplateModelException {
        return (TemplateNodeModel)this.get("_parent");
    }

    private String getUniqueText(NodeListModel model, String property) throws TemplateModelException {
        String s1 = null;
        HashSet<String> s = null;
        for (String s2 : model.nodes) {
            if (s2 == null) continue;
            if (s1 == null) {
                s1 = s2;
                continue;
            }
            if (s1.equals(s2)) continue;
            if (s == null) {
                s = new HashSet<String>();
                s.add(s1);
            }
            s.add(s2);
        }
        if (s == null) {
            return s1;
        }
        throw new TemplateModelException("Value for node " + property + " is ambiguos: " + s);
    }

    private class FilterByType
    implements TemplateMethodModel {
        private FilterByType() {
        }

        @Override
        public Object exec(List arguments) {
            ArrayList filteredNodes = new ArrayList();
            for (Object node : arguments) {
                if (!arguments.contains(NodeListModel.this.navigator.getType(node))) continue;
                filteredNodes.add(node);
            }
            return NodeListModel.this.deriveModel(filteredNodes);
        }
    }
}

