/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jaxen.Context
 *  org.jaxen.JaxenException
 *  org.jaxen.NamespaceContext
 *  org.jaxen.jdom.JDOMXPath
 *  org.jdom.Attribute
 *  org.jdom.CDATA
 *  org.jdom.Comment
 *  org.jdom.DocType
 *  org.jdom.Document
 *  org.jdom.Element
 *  org.jdom.EntityRef
 *  org.jdom.Namespace
 *  org.jdom.ProcessingInstruction
 *  org.jdom.Text
 *  org.jdom.input.SAXBuilder
 *  org.jdom.output.XMLOutputter
 */
package freemarker.ext.jdom;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.Template;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template._ObjectWrappers;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.jaxen.Context;
import org.jaxen.JaxenException;
import org.jaxen.NamespaceContext;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Attribute;
import org.jdom.CDATA;
import org.jdom.Comment;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.Namespace;
import org.jdom.ProcessingInstruction;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

@Deprecated
public class NodeListModel
implements TemplateHashModel,
TemplateMethodModel,
TemplateCollectionModel,
TemplateSequenceModel,
TemplateScalarModel {
    private static final AttributeXMLOutputter OUTPUT = new AttributeXMLOutputter();
    private static final NodeListModel EMPTY = new NodeListModel(null, false);
    private static final Map XPATH_CACHE = new WeakHashMap();
    private static final NamedNodeOperator NAMED_CHILDREN_OP = new NamedChildrenOp();
    private static final NamedNodeOperator NAMED_ATTRIBUTE_OP = new NamedAttributeOp();
    private static final NodeOperator ALL_ATTRIBUTES_OP = new AllAttributesOp();
    private static final NodeOperator ALL_CHILDREN_OP = new AllChildrenOp();
    private static final Map OPERATIONS = NodeListModel.createOperations();
    private static final Map SPECIAL_OPERATIONS = NodeListModel.createSpecialOperations();
    private static final int SPECIAL_OPERATION_COPY = 0;
    private static final int SPECIAL_OPERATION_UNIQUE = 1;
    private static final int SPECIAL_OPERATION_FILTER_NAME = 2;
    private static final int SPECIAL_OPERATION_FILTER_TYPE = 3;
    private static final int SPECIAL_OPERATION_QUERY_TYPE = 4;
    private static final int SPECIAL_OPERATION_REGISTER_NAMESPACE = 5;
    private static final int SPECIAL_OPERATION_PLAINTEXT = 6;
    private final List nodes;
    private final Map namespaces;

    public NodeListModel(Document document) {
        this.nodes = document == null ? Collections.EMPTY_LIST : Collections.singletonList(document);
        this.namespaces = new HashMap();
    }

    public NodeListModel(Element element) {
        this.nodes = element == null ? Collections.EMPTY_LIST : Collections.singletonList(element);
        this.namespaces = new HashMap();
    }

    private NodeListModel(Object object, Map namespaces) {
        this.nodes = object == null ? Collections.EMPTY_LIST : Collections.singletonList(object);
        this.namespaces = namespaces;
    }

    public NodeListModel(List nodes) {
        this(nodes, true);
    }

    public NodeListModel(List nodes, boolean copy) {
        this.nodes = copy && nodes != null ? new ArrayList(nodes) : (nodes == null ? Collections.EMPTY_LIST : nodes);
        this.namespaces = new HashMap();
    }

    private NodeListModel(List nodes, Map namespaces) {
        this.nodes = nodes == null ? Collections.EMPTY_LIST : nodes;
        this.namespaces = namespaces;
    }

    private static final NodeListModel createNodeListModel(List list, Map namespaces) {
        if (list == null || list.isEmpty()) {
            if (namespaces.isEmpty()) {
                return EMPTY;
            }
            return new NodeListModel(Collections.EMPTY_LIST, namespaces);
        }
        if (list.size() == 1) {
            return new NodeListModel(list.get(0), namespaces);
        }
        return new NodeListModel(list, namespaces);
    }

    @Override
    public boolean isEmpty() {
        return this.nodes.isEmpty();
    }

    @Override
    public String getAsString() throws TemplateModelException {
        if (this.isEmpty()) {
            return "";
        }
        StringWriter sw = new StringWriter(this.nodes.size() * 128);
        try {
            for (Object node : this.nodes) {
                if (node instanceof Element) {
                    OUTPUT.output((Element)node, sw);
                    continue;
                }
                if (node instanceof Attribute) {
                    OUTPUT.output((Attribute)node, sw);
                    continue;
                }
                if (node instanceof String) {
                    sw.write(OUTPUT.escapeElementEntities(node.toString()));
                    continue;
                }
                if (node instanceof Text) {
                    OUTPUT.output((Text)node, sw);
                    continue;
                }
                if (node instanceof Document) {
                    OUTPUT.output((Document)node, sw);
                    continue;
                }
                if (node instanceof ProcessingInstruction) {
                    OUTPUT.output((ProcessingInstruction)node, sw);
                    continue;
                }
                if (node instanceof Comment) {
                    OUTPUT.output((Comment)node, sw);
                    continue;
                }
                if (node instanceof CDATA) {
                    OUTPUT.output((CDATA)node, sw);
                    continue;
                }
                if (node instanceof DocType) {
                    OUTPUT.output((DocType)node, sw);
                    continue;
                }
                if (node instanceof EntityRef) {
                    OUTPUT.output((EntityRef)node, sw);
                    continue;
                }
                throw new TemplateModelException(node.getClass().getName() + " is not a core JDOM class");
            }
        }
        catch (IOException e) {
            throw new TemplateModelException(e.getMessage());
        }
        return sw.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TemplateModel get(String key) throws TemplateModelException {
        if (this.isEmpty()) {
            return EMPTY;
        }
        if (key == null || key.length() == 0) {
            throw new TemplateModelException("Invalid key [" + key + "]");
        }
        NodeOperator op = null;
        NamedNodeOperator nop = null;
        String name = null;
        switch (key.charAt(0)) {
            case '@': {
                if (key.length() != 2 || key.charAt(1) != '*') {
                    nop = NAMED_ATTRIBUTE_OP;
                    name = key.substring(1);
                    break;
                }
                op = ALL_ATTRIBUTES_OP;
                break;
            }
            case '*': {
                if (key.length() == 1) {
                    op = ALL_CHILDREN_OP;
                    break;
                }
                throw new TemplateModelException("Invalid key [" + key + "]");
            }
            case '_': 
            case 'x': {
                Integer specop;
                op = (NodeOperator)OPERATIONS.get(key);
                if (op != null || (specop = (Integer)SPECIAL_OPERATIONS.get(key)) == null) break;
                switch (specop) {
                    case 0: {
                        Map map = this.namespaces;
                        synchronized (map) {
                            return new NodeListModel(this.nodes, (Map)((HashMap)this.namespaces).clone());
                        }
                    }
                    case 1: {
                        return new NodeListModel(NodeListModel.removeDuplicates(this.nodes), this.namespaces);
                    }
                    case 2: {
                        return new NameFilter();
                    }
                    case 3: {
                        return new TypeFilter();
                    }
                    case 4: {
                        return this.getType();
                    }
                    case 5: {
                        return new RegisterNamespace();
                    }
                    case 6: {
                        return this.getPlainText();
                    }
                }
            }
        }
        if (op == null && nop == null) {
            nop = NAMED_CHILDREN_OP;
            name = key;
        }
        List list = null;
        if (op != null) {
            list = NodeListModel.evaluateElementOperation(op, this.nodes);
        } else {
            String localName = name;
            Namespace namespace = Namespace.NO_NAMESPACE;
            int colon = name.indexOf(58);
            if (colon != -1) {
                localName = name.substring(colon + 1);
                String nsPrefix = name.substring(0, colon);
                Map map = this.namespaces;
                synchronized (map) {
                    namespace = (Namespace)this.namespaces.get(nsPrefix);
                }
                if (namespace == null) {
                    if (nsPrefix.equals("xml")) {
                        namespace = Namespace.XML_NAMESPACE;
                    } else {
                        throw new TemplateModelException("Unregistered namespace prefix '" + nsPrefix + "'");
                    }
                }
            }
            list = NodeListModel.evaluateNamedElementOperation(nop, localName, namespace, this.nodes);
        }
        return NodeListModel.createNodeListModel(list, this.namespaces);
    }

    private TemplateModel getType() {
        if (this.nodes.size() == 0) {
            return new SimpleScalar("");
        }
        Object firstNode = this.nodes.get(0);
        char code = firstNode instanceof Element ? (char)'e' : (firstNode instanceof Text || firstNode instanceof String ? (char)'x' : (firstNode instanceof Attribute ? (char)'a' : (firstNode instanceof EntityRef ? (char)'n' : (firstNode instanceof Document ? (char)'d' : (firstNode instanceof DocType ? (char)'t' : (firstNode instanceof Comment ? (char)'c' : (firstNode instanceof ProcessingInstruction ? (char)'p' : '?')))))));
        return new SimpleScalar(new String(new char[]{code}));
    }

    private SimpleScalar getPlainText() throws TemplateModelException {
        List list = NodeListModel.evaluateElementOperation((TextOp)OPERATIONS.get("_text"), this.nodes);
        StringBuilder buf = new StringBuilder();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            buf.append(it.next());
        }
        return new SimpleScalar(buf.toString());
    }

    @Override
    public TemplateModelIterator iterator() {
        return new TemplateModelIterator(){
            private final Iterator it;
            {
                this.it = NodeListModel.this.nodes.iterator();
            }

            @Override
            public TemplateModel next() {
                return this.it.hasNext() ? new NodeListModel(this.it.next(), NodeListModel.this.namespaces) : null;
            }

            @Override
            public boolean hasNext() {
                return this.it.hasNext();
            }
        };
    }

    @Override
    public TemplateModel get(int i) throws TemplateModelException {
        try {
            return new NodeListModel(this.nodes.get(i), this.namespaces);
        }
        catch (IndexOutOfBoundsException e) {
            throw new TemplateModelException("Index out of bounds: " + e.getMessage());
        }
    }

    @Override
    public int size() {
        return this.nodes.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object exec(List arguments) throws TemplateModelException {
        if (arguments == null || arguments.size() != 1) {
            throw new TemplateModelException("Exactly one argument required for execute() on NodeTemplate");
        }
        String xpathString = (String)arguments.get(0);
        JDOMXPathEx xpath = null;
        try {
            Map map = XPATH_CACHE;
            synchronized (map) {
                xpath = (JDOMXPathEx)((Object)XPATH_CACHE.get(xpathString));
                if (xpath == null) {
                    xpath = new JDOMXPathEx(xpathString);
                    XPATH_CACHE.put(xpathString, xpath);
                }
            }
            return NodeListModel.createNodeListModel(xpath.selectNodes(this.nodes, this.namespaces), this.namespaces);
        }
        catch (Exception e) {
            throw new TemplateModelException("Could not evaulate XPath expression " + xpathString, e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void registerNamespace(String prefix, String uri) {
        Map map = this.namespaces;
        synchronized (map) {
            this.namespaces.put(prefix, Namespace.getNamespace((String)prefix, (String)uri));
        }
    }

    private static final Element getParent(Object node) {
        if (node instanceof Element) {
            return ((Element)node).getParent();
        }
        if (node instanceof Attribute) {
            return ((Attribute)node).getParent();
        }
        if (node instanceof Text) {
            return ((Text)node).getParent();
        }
        if (node instanceof ProcessingInstruction) {
            return ((ProcessingInstruction)node).getParent();
        }
        if (node instanceof Comment) {
            return ((Comment)node).getParent();
        }
        if (node instanceof EntityRef) {
            return ((EntityRef)node).getParent();
        }
        return null;
    }

    private static final List evaluateElementOperation(NodeOperator op, List nodes) throws TemplateModelException {
        int s = nodes.size();
        List[] lists = new List[s];
        int l = 0;
        int i = 0;
        Iterator it = nodes.iterator();
        while (it.hasNext()) {
            List list = op.operate(it.next());
            if (list == null) continue;
            lists[i++] = list;
            l += list.size();
        }
        ArrayList retval = new ArrayList(l);
        for (int i2 = 0; i2 < s; ++i2) {
            if (lists[i2] == null) continue;
            retval.addAll(lists[i2]);
        }
        return retval;
    }

    private static final List evaluateNamedElementOperation(NamedNodeOperator op, String localName, Namespace namespace, List nodes) throws TemplateModelException {
        int s = nodes.size();
        List[] lists = new List[s];
        int l = 0;
        int i = 0;
        Iterator it = nodes.iterator();
        while (it.hasNext()) {
            List list = op.operate(it.next(), localName, namespace);
            lists[i++] = list;
            l += list.size();
        }
        ArrayList retval = new ArrayList(l);
        for (int i2 = 0; i2 < s; ++i2) {
            retval.addAll(lists[i2]);
        }
        return retval;
    }

    private static final List removeDuplicates(List list) {
        int s = list.size();
        ArrayList ulist = new ArrayList(s);
        HashSet set = new HashSet(s * 4 / 3, 0.75f);
        for (Object o : list) {
            if (!set.add(o)) continue;
            ulist.add(o);
        }
        ulist.trimToSize();
        return ulist;
    }

    private static final Map createOperations() {
        HashMap<String, NodeOperator> map = new HashMap<String, NodeOperator>();
        map.put("_ancestor", new AncestorOp());
        map.put("_ancestorOrSelf", new AncestorOrSelfOp());
        map.put("_attributes", ALL_ATTRIBUTES_OP);
        map.put("_children", ALL_CHILDREN_OP);
        map.put("_cname", new CanonicalNameOp());
        map.put("_content", new ContentOp());
        map.put("_descendant", new DescendantOp());
        map.put("_descendantOrSelf", new DescendantOrSelfOp());
        map.put("_document", new DocumentOp());
        map.put("_doctype", new DocTypeOp());
        map.put("_name", new NameOp());
        map.put("_nsprefix", new NamespacePrefixOp());
        map.put("_nsuri", new NamespaceUriOp());
        map.put("_parent", new ParentOp());
        map.put("_qname", new QNameOp());
        map.put("_text", new TextOp());
        return map;
    }

    private static final Map createSpecialOperations() {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        Integer copy = 0;
        Integer unique = 1;
        Integer fname = 2;
        Integer ftype = 3;
        Integer type = 4;
        Integer regns = 5;
        Integer plaintext = 6;
        map.put("_copy", copy);
        map.put("_unique", unique);
        map.put("_fname", fname);
        map.put("_ftype", ftype);
        map.put("_type", type);
        map.put("_registerNamespace", regns);
        map.put("_plaintext", plaintext);
        map.put("x_copy", copy);
        map.put("x_unique", unique);
        map.put("x_fname", fname);
        map.put("x_ftype", ftype);
        map.put("x_type", type);
        return map;
    }

    @Deprecated
    public static void main(String[] args) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(System.in);
        SimpleHash model = new SimpleHash(_ObjectWrappers.SAFE_OBJECT_WRAPPER);
        model.put("document", new NodeListModel(document));
        FileReader fr = new FileReader(args[0]);
        Template template = new Template(args[0], fr);
        OutputStreamWriter w = new OutputStreamWriter(System.out);
        template.process(model, w);
        ((Writer)w).flush();
        ((Writer)w).close();
    }

    private static final class JDOMXPathEx
    extends JDOMXPath {
        JDOMXPathEx(String path) throws JaxenException {
            super(path);
        }

        public List selectNodes(Object object, Map namespaces) throws JaxenException {
            Context context = this.getContext(object);
            context.getContextSupport().setNamespaceContext((NamespaceContext)new NamespaceContextImpl(namespaces));
            return this.selectNodesForContext(context);
        }

        private static final class NamespaceContextImpl
        implements NamespaceContext {
            private final Map namespaces;

            NamespaceContextImpl(Map namespaces) {
                this.namespaces = namespaces;
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public String translateNamespacePrefixToUri(String prefix) {
                if (prefix.length() == 0) {
                    return prefix;
                }
                Map map = this.namespaces;
                synchronized (map) {
                    Namespace ns = (Namespace)this.namespaces.get(prefix);
                    return ns == null ? null : ns.getURI();
                }
            }
        }
    }

    private static final class AttributeXMLOutputter
    extends XMLOutputter {
        private AttributeXMLOutputter() {
        }

        public void output(Attribute attribute, Writer out) throws IOException {
            out.write(" ");
            out.write(attribute.getQualifiedName());
            out.write("=");
            out.write("\"");
            out.write(this.escapeAttributeEntities(attribute.getValue()));
            out.write("\"");
        }
    }

    private final class TypeFilter
    implements TemplateMethodModel {
        private TypeFilter() {
        }

        public boolean isEmpty() {
            return false;
        }

        @Override
        public Object exec(List arguments) throws TemplateModelException {
            if (arguments == null || arguments.size() == 0) {
                throw new TemplateModelException("_type expects exactly one argument");
            }
            String arg = (String)arguments.get(0);
            boolean invert = arg.indexOf(33) != -1;
            boolean a = invert != (arg.indexOf(97) == -1);
            boolean c = invert != (arg.indexOf(99) == -1);
            boolean d = invert != (arg.indexOf(100) == -1);
            boolean e = invert != (arg.indexOf(101) == -1);
            boolean n = invert != (arg.indexOf(110) == -1);
            boolean p = invert != (arg.indexOf(112) == -1);
            boolean t = invert != (arg.indexOf(116) == -1);
            boolean x = invert != (arg.indexOf(120) == -1);
            LinkedList list = new LinkedList(NodeListModel.this.nodes);
            Iterator it = list.iterator();
            while (it.hasNext()) {
                Object node = it.next();
                if (!(node instanceof Element && e || node instanceof Attribute && a || node instanceof String && x || node instanceof Text && x || node instanceof ProcessingInstruction && p || node instanceof Comment && c || node instanceof EntityRef && n || node instanceof Document && d) && (!(node instanceof DocType) || !t)) continue;
                it.remove();
            }
            return NodeListModel.createNodeListModel(list, NodeListModel.this.namespaces);
        }
    }

    private final class NameFilter
    implements TemplateMethodModel {
        private NameFilter() {
        }

        public boolean isEmpty() {
            return false;
        }

        @Override
        public Object exec(List arguments) {
            HashSet names = new HashSet(arguments);
            LinkedList list = new LinkedList(NodeListModel.this.nodes);
            Iterator it = list.iterator();
            while (it.hasNext()) {
                Object node = it.next();
                String name = null;
                if (node instanceof Element) {
                    name = ((Element)node).getName();
                } else if (node instanceof Attribute) {
                    name = ((Attribute)node).getName();
                } else if (node instanceof ProcessingInstruction) {
                    name = ((ProcessingInstruction)node).getTarget();
                } else if (node instanceof EntityRef) {
                    name = ((EntityRef)node).getName();
                } else if (node instanceof DocType) {
                    name = ((DocType)node).getPublicID();
                }
                if (name != null && names.contains(name)) continue;
                it.remove();
            }
            return NodeListModel.createNodeListModel(list, NodeListModel.this.namespaces);
        }
    }

    private final class RegisterNamespace
    implements TemplateMethodModel {
        private RegisterNamespace() {
        }

        public boolean isEmpty() {
            return false;
        }

        @Override
        public Object exec(List arguments) throws TemplateModelException {
            if (arguments.size() != 2) {
                throw new TemplateModelException("_registerNamespace(prefix, uri) requires two arguments");
            }
            NodeListModel.this.registerNamespace((String)arguments.get(0), (String)arguments.get(1));
            return TemplateScalarModel.EMPTY_STRING;
        }
    }

    private static final class TextOp
    implements NodeOperator {
        private TextOp() {
        }

        @Override
        public List operate(Object node) {
            if (node instanceof Element) {
                return Collections.singletonList(((Element)node).getTextTrim());
            }
            if (node instanceof Attribute) {
                return Collections.singletonList(((Attribute)node).getValue());
            }
            if (node instanceof CDATA) {
                return Collections.singletonList(((CDATA)node).getText());
            }
            if (node instanceof Comment) {
                return Collections.singletonList(((Comment)node).getText());
            }
            if (node instanceof ProcessingInstruction) {
                return Collections.singletonList(((ProcessingInstruction)node).getData());
            }
            return null;
        }
    }

    private static final class ContentOp
    implements NodeOperator {
        private ContentOp() {
        }

        @Override
        public List operate(Object node) {
            if (node instanceof Element) {
                return ((Element)node).getContent();
            }
            if (node instanceof Document) {
                return ((Document)node).getContent();
            }
            return null;
        }
    }

    private static final class DocTypeOp
    implements NodeOperator {
        private DocTypeOp() {
        }

        @Override
        public List operate(Object node) {
            if (node instanceof Document) {
                DocType doctype = ((Document)node).getDocType();
                return doctype == null ? Collections.EMPTY_LIST : Collections.singletonList(doctype);
            }
            return null;
        }
    }

    private static final class DocumentOp
    implements NodeOperator {
        private DocumentOp() {
        }

        @Override
        public List operate(Object node) {
            Document doc = null;
            if (node instanceof Element) {
                doc = ((Element)node).getDocument();
            } else if (node instanceof Attribute) {
                Element parent = ((Attribute)node).getParent();
                doc = parent == null ? null : parent.getDocument();
            } else if (node instanceof Text) {
                Element parent = ((Text)node).getParent();
                doc = parent == null ? null : parent.getDocument();
            } else if (node instanceof Document) {
                doc = (Document)node;
            } else if (node instanceof ProcessingInstruction) {
                doc = ((ProcessingInstruction)node).getDocument();
            } else if (node instanceof EntityRef) {
                doc = ((EntityRef)node).getDocument();
            } else if (node instanceof Comment) {
                doc = ((Comment)node).getDocument();
            } else {
                return null;
            }
            return doc == null ? Collections.EMPTY_LIST : Collections.singletonList(doc);
        }
    }

    private static final class DescendantOrSelfOp
    extends DescendantOp {
        private DescendantOrSelfOp() {
        }

        @Override
        public List operate(Object node) {
            LinkedList list = (LinkedList)super.operate(node);
            list.addFirst(node);
            return list;
        }
    }

    private static class DescendantOp
    implements NodeOperator {
        private DescendantOp() {
        }

        @Override
        public List operate(Object node) {
            LinkedList<Element> list = new LinkedList<Element>();
            if (node instanceof Element) {
                this.addChildren((Element)node, list);
            } else if (node instanceof Document) {
                Element root = ((Document)node).getRootElement();
                list.add(root);
                this.addChildren(root, list);
            } else {
                return null;
            }
            return list;
        }

        private void addChildren(Element element, List list) {
            List children = element.getChildren();
            for (Element child : children) {
                list.add(child);
                this.addChildren(child, list);
            }
        }
    }

    private static final class AncestorOrSelfOp
    implements NodeOperator {
        private AncestorOrSelfOp() {
        }

        @Override
        public List operate(Object node) {
            Element parent = NodeListModel.getParent(node);
            if (parent == null) {
                return Collections.singletonList(node);
            }
            LinkedList<Object> list = new LinkedList<Object>();
            list.addFirst(node);
            do {
                list.addFirst(parent);
            } while ((parent = parent.getParent()) != null);
            return list;
        }
    }

    private static final class AncestorOp
    implements NodeOperator {
        private AncestorOp() {
        }

        @Override
        public List operate(Object node) {
            Element parent = NodeListModel.getParent(node);
            if (parent == null) {
                return Collections.EMPTY_LIST;
            }
            LinkedList<Element> list = new LinkedList<Element>();
            do {
                list.addFirst(parent);
            } while ((parent = parent.getParent()) != null);
            return list;
        }
    }

    private static final class ParentOp
    implements NodeOperator {
        private ParentOp() {
        }

        @Override
        public List operate(Object node) {
            Element parent = NodeListModel.getParent(node);
            return parent == null ? Collections.EMPTY_LIST : Collections.singletonList(parent);
        }
    }

    private static final class CanonicalNameOp
    implements NodeOperator {
        private CanonicalNameOp() {
        }

        @Override
        public List operate(Object node) {
            if (node instanceof Element) {
                Element element = (Element)node;
                return Collections.singletonList(element.getNamespace().getURI() + element.getName());
            }
            if (node instanceof Attribute) {
                Attribute attribute = (Attribute)node;
                return Collections.singletonList(attribute.getNamespace().getURI() + attribute.getName());
            }
            return null;
        }
    }

    private static final class NamespacePrefixOp
    implements NodeOperator {
        private NamespacePrefixOp() {
        }

        @Override
        public List operate(Object node) {
            if (node instanceof Element) {
                return Collections.singletonList(((Element)node).getNamespace().getPrefix());
            }
            if (node instanceof Attribute) {
                return Collections.singletonList(((Attribute)node).getNamespace().getPrefix());
            }
            return null;
        }
    }

    private static final class NamespaceUriOp
    implements NodeOperator {
        private NamespaceUriOp() {
        }

        @Override
        public List operate(Object node) {
            if (node instanceof Element) {
                return Collections.singletonList(((Element)node).getNamespace().getURI());
            }
            if (node instanceof Attribute) {
                return Collections.singletonList(((Attribute)node).getNamespace().getURI());
            }
            return null;
        }
    }

    private static final class QNameOp
    implements NodeOperator {
        private QNameOp() {
        }

        @Override
        public List operate(Object node) {
            if (node instanceof Element) {
                return Collections.singletonList(((Element)node).getQualifiedName());
            }
            if (node instanceof Attribute) {
                return Collections.singletonList(((Attribute)node).getQualifiedName());
            }
            return null;
        }
    }

    private static final class NameOp
    implements NodeOperator {
        private NameOp() {
        }

        @Override
        public List operate(Object node) {
            if (node instanceof Element) {
                return Collections.singletonList(((Element)node).getName());
            }
            if (node instanceof Attribute) {
                return Collections.singletonList(((Attribute)node).getName());
            }
            if (node instanceof EntityRef) {
                return Collections.singletonList(((EntityRef)node).getName());
            }
            if (node instanceof ProcessingInstruction) {
                return Collections.singletonList(((ProcessingInstruction)node).getTarget());
            }
            if (node instanceof DocType) {
                return Collections.singletonList(((DocType)node).getPublicID());
            }
            return null;
        }
    }

    private static final class NamedAttributeOp
    implements NamedNodeOperator {
        private NamedAttributeOp() {
        }

        @Override
        public List operate(Object node, String localName, Namespace namespace) {
            Attribute attr = null;
            if (node instanceof Element) {
                Element element = (Element)node;
                attr = element.getAttribute(localName, namespace);
            } else if (node instanceof ProcessingInstruction) {
                ProcessingInstruction pi = (ProcessingInstruction)node;
                attr = "target".equals(localName) ? new Attribute("target", pi.getTarget()) : ("data".equals(localName) ? new Attribute("data", pi.getData()) : new Attribute(localName, pi.getValue(localName)));
            } else if (node instanceof DocType) {
                DocType doctype = (DocType)node;
                if ("publicId".equals(localName)) {
                    attr = new Attribute("publicId", doctype.getPublicID());
                } else if ("systemId".equals(localName)) {
                    attr = new Attribute("systemId", doctype.getSystemID());
                } else if ("elementName".equals(localName)) {
                    attr = new Attribute("elementName", doctype.getElementName());
                }
            } else {
                return null;
            }
            return attr == null ? Collections.EMPTY_LIST : Collections.singletonList(attr);
        }
    }

    private static final class AllAttributesOp
    implements NodeOperator {
        private AllAttributesOp() {
        }

        @Override
        public List operate(Object node) {
            if (!(node instanceof Element)) {
                return null;
            }
            return ((Element)node).getAttributes();
        }
    }

    private static final class NamedChildrenOp
    implements NamedNodeOperator {
        private NamedChildrenOp() {
        }

        @Override
        public List operate(Object node, String localName, Namespace namespace) {
            if (node instanceof Element) {
                return ((Element)node).getChildren(localName, namespace);
            }
            if (node instanceof Document) {
                Element root = ((Document)node).getRootElement();
                if (root != null && root.getName().equals(localName) && root.getNamespaceURI().equals(namespace.getURI())) {
                    return Collections.singletonList(root);
                }
                return Collections.EMPTY_LIST;
            }
            return null;
        }
    }

    private static final class AllChildrenOp
    implements NodeOperator {
        private AllChildrenOp() {
        }

        @Override
        public List operate(Object node) {
            if (node instanceof Element) {
                return ((Element)node).getChildren();
            }
            if (node instanceof Document) {
                Element root = ((Document)node).getRootElement();
                return root == null ? Collections.EMPTY_LIST : Collections.singletonList(root);
            }
            return null;
        }
    }

    private static interface NamedNodeOperator {
        public List operate(Object var1, String var2, Namespace var3) throws TemplateModelException;
    }

    private static interface NodeOperator {
        public List operate(Object var1) throws TemplateModelException;
    }
}

