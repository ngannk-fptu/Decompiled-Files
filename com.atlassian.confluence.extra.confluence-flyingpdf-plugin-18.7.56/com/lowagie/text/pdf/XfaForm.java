/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.xml.XmlDomWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XfaForm {
    private Xml2SomTemplate templateSom;
    private Node templateNode;
    private Xml2SomDatasets datasetsSom;
    private Node datasetsNode;
    private AcroFieldsSearch acroFieldsSom;
    private PdfReader reader;
    private boolean xfaPresent;
    private Document domDocument;
    private boolean changed;
    public static final String XFA_DATA_SCHEMA = "http://www.xfa.org/schema/xfa-data/1.0/";

    public XfaForm() {
    }

    public static PdfObject getXfaObject(PdfReader reader) {
        PdfDictionary af = (PdfDictionary)PdfReader.getPdfObjectRelease(reader.getCatalog().get(PdfName.ACROFORM));
        if (af == null) {
            return null;
        }
        return PdfReader.getPdfObjectRelease(af.get(PdfName.XFA));
    }

    public XfaForm(PdfReader reader) throws IOException, ParserConfigurationException, SAXException {
        this.reader = reader;
        PdfObject xfa = XfaForm.getXfaObject(reader);
        if (xfa == null) {
            this.xfaPresent = false;
            return;
        }
        this.xfaPresent = true;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        if (xfa.isArray()) {
            PdfArray ar = (PdfArray)xfa;
            for (int k = 1; k < ar.size(); k += 2) {
                PdfObject ob = ar.getDirectObject(k);
                if (!(ob instanceof PRStream)) continue;
                byte[] b = PdfReader.getStreamBytes((PRStream)ob);
                bout.write(b);
            }
        } else if (xfa instanceof PRStream) {
            byte[] b = PdfReader.getStreamBytes((PRStream)xfa);
            bout.write(b);
        }
        bout.close();
        DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
        fact.setNamespaceAware(true);
        DocumentBuilder db = fact.newDocumentBuilder();
        db.setEntityResolver((publicId, systemId) -> new InputSource(new StringReader("")));
        this.domDocument = db.parse(new ByteArrayInputStream(bout.toByteArray()));
        this.extractNodes();
    }

    private void extractNodes() {
        Node n = this.domDocument.getFirstChild();
        while (n.getChildNodes().getLength() == 0) {
            n = n.getNextSibling();
        }
        for (n = n.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() != 1) continue;
            String s = n.getLocalName();
            if (s.equals("template")) {
                this.templateNode = n;
                this.templateSom = new Xml2SomTemplate(n);
                continue;
            }
            if (!s.equals("datasets")) continue;
            this.datasetsNode = n;
            this.datasetsSom = new Xml2SomDatasets(n.getFirstChild());
        }
    }

    public static void setXfa(XfaForm form, PdfReader reader, PdfWriter writer) throws IOException {
        PdfDictionary af = (PdfDictionary)PdfReader.getPdfObjectRelease(reader.getCatalog().get(PdfName.ACROFORM));
        if (af == null) {
            return;
        }
        PdfObject xfa = XfaForm.getXfaObject(reader);
        if (xfa.isArray()) {
            PdfArray ar = (PdfArray)xfa;
            int t = -1;
            int d = -1;
            for (int k = 0; k < ar.size(); k += 2) {
                PdfString s = ar.getAsString(k);
                if ("template".equals(s.toString())) {
                    t = k + 1;
                }
                if (!"datasets".equals(s.toString())) continue;
                d = k + 1;
            }
            if (t > -1 && d > -1) {
                reader.killXref(ar.getAsIndirectObject(t));
                reader.killXref(ar.getAsIndirectObject(d));
                PdfStream tStream = new PdfStream(XfaForm.serializeDoc(form.templateNode));
                tStream.flateCompress(writer.getCompressionLevel());
                ar.set(t, writer.addToBody(tStream).getIndirectReference());
                PdfStream dStream = new PdfStream(XfaForm.serializeDoc(form.datasetsNode));
                dStream.flateCompress(writer.getCompressionLevel());
                ar.set(d, writer.addToBody(dStream).getIndirectReference());
                af.put(PdfName.XFA, new PdfArray(ar));
                return;
            }
        }
        reader.killXref(af.get(PdfName.XFA));
        PdfStream str = new PdfStream(XfaForm.serializeDoc(form.domDocument));
        str.flateCompress(writer.getCompressionLevel());
        PdfIndirectReference ref = writer.addToBody(str).getIndirectReference();
        af.put(PdfName.XFA, ref);
    }

    public void setXfa(PdfWriter writer) throws IOException {
        XfaForm.setXfa(this, this.reader, writer);
    }

    public static byte[] serializeDoc(Node n) throws IOException {
        XmlDomWriter xw = new XmlDomWriter();
        ByteArrayOutputStream fout = new ByteArrayOutputStream();
        xw.setOutput(fout, null);
        xw.setCanonical(false);
        xw.write(n);
        fout.close();
        return fout.toByteArray();
    }

    public boolean isXfaPresent() {
        return this.xfaPresent;
    }

    public Document getDomDocument() {
        return this.domDocument;
    }

    public String findFieldName(String name, AcroFields af) {
        Map<String, AcroFields.Item> items = af.getAllFields();
        if (items.containsKey(name)) {
            return name;
        }
        if (this.acroFieldsSom == null) {
            this.acroFieldsSom = items.isEmpty() && this.xfaPresent ? new AcroFieldsSearch(this.datasetsSom.getNodesByName().keySet()) : new AcroFieldsSearch(items.keySet());
        }
        if (this.acroFieldsSom.getLongByShortNames().containsKey(name)) {
            return this.acroFieldsSom.getLongByShortNames().get(name);
        }
        return this.acroFieldsSom.inverseSearch(Xml2Som.splitParts(name));
    }

    public String findDatasetsName(String name) {
        if (this.datasetsSom.getNodesByName().containsKey(name)) {
            return name;
        }
        return this.datasetsSom.inverseSearch(Xml2Som.splitParts(name));
    }

    public Node findDatasetsNode(@Nullable String name) {
        if (name == null) {
            return null;
        }
        if ((name = this.findDatasetsName(name)) == null) {
            return null;
        }
        return this.datasetsSom.getNodesByName().get(name);
    }

    public static String getNodeText(@Nullable Node n) {
        if (n == null) {
            return "";
        }
        return XfaForm.getNodeText(n, "");
    }

    private static String getNodeText(Node n, String name) {
        for (Node n2 = n.getFirstChild(); n2 != null; n2 = n2.getNextSibling()) {
            if (n2.getNodeType() == 1) {
                name = XfaForm.getNodeText(n2, name);
                continue;
            }
            if (n2.getNodeType() != 3) continue;
            name = name + n2.getNodeValue();
        }
        return name;
    }

    public void setNodeText(@Nullable Node n, String text) {
        Node nc;
        if (n == null) {
            return;
        }
        while ((nc = n.getFirstChild()) != null) {
            n.removeChild(nc);
        }
        if (n.getAttributes().getNamedItemNS(XFA_DATA_SCHEMA, "dataNode") != null) {
            n.getAttributes().removeNamedItemNS(XFA_DATA_SCHEMA, "dataNode");
        }
        n.appendChild(this.domDocument.createTextNode(text));
        this.changed = true;
    }

    public void setXfaPresent(boolean xfaPresent) {
        this.xfaPresent = xfaPresent;
    }

    public void setDomDocument(Document domDocument) {
        this.domDocument = domDocument;
        this.extractNodes();
    }

    public PdfReader getReader() {
        return this.reader;
    }

    public void setReader(PdfReader reader) {
        this.reader = reader;
    }

    public boolean isChanged() {
        return this.changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public Xml2SomTemplate getTemplateSom() {
        return this.templateSom;
    }

    public void setTemplateSom(Xml2SomTemplate templateSom) {
        this.templateSom = templateSom;
    }

    public Xml2SomDatasets getDatasetsSom() {
        return this.datasetsSom;
    }

    public void setDatasetsSom(Xml2SomDatasets datasetsSom) {
        this.datasetsSom = datasetsSom;
    }

    public AcroFieldsSearch getAcroFieldsSom() {
        return this.acroFieldsSom;
    }

    public void setAcroFieldsSom(AcroFieldsSearch acroFieldsSom) {
        this.acroFieldsSom = acroFieldsSom;
    }

    public Node getDatasetsNode() {
        return this.datasetsNode;
    }

    public void fillXfaForm(File file) throws ParserConfigurationException, SAXException, IOException {
        this.fillXfaForm(new FileInputStream(file));
    }

    public void fillXfaForm(InputStream is) throws ParserConfigurationException, SAXException, IOException {
        this.fillXfaForm(new InputSource(is));
    }

    public void fillXfaForm(InputSource is) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        db.setEntityResolver((publicId, systemId) -> new InputSource(new StringReader("")));
        Document newdoc = db.parse(is);
        this.fillXfaForm(newdoc.getDocumentElement());
    }

    public void fillXfaForm(Node node) {
        Node data = this.datasetsNode.getFirstChild();
        NodeList list = data.getChildNodes();
        if (list.getLength() == 0) {
            data.appendChild(this.domDocument.importNode(node, true));
        } else {
            data.replaceChild(this.domDocument.importNode(node, true), data.getFirstChild());
        }
        this.extractNodes();
        this.setChanged(true);
    }

    public static class Xml2SomTemplate
    extends Xml2Som {
        private boolean dynamicForm;
        private int templateLevel;

        public Xml2SomTemplate(Node n) {
            this.order = new ArrayList();
            this.name2Node = new HashMap();
            this.stack = new Stack2();
            this.anform = 0;
            this.templateLevel = 0;
            this.inverseSearch = new HashMap();
            this.processTemplate(n, null);
        }

        public String getFieldType(String s) {
            Node ui;
            Node n = (Node)this.name2Node.get(s);
            if (n == null) {
                return null;
            }
            if (n.getLocalName().equals("exclGroup")) {
                return "exclGroup";
            }
            for (ui = n.getFirstChild(); !(ui == null || ui.getNodeType() == 1 && ui.getLocalName().equals("ui")); ui = ui.getNextSibling()) {
            }
            if (ui == null) {
                return null;
            }
            for (Node type = ui.getFirstChild(); type != null; type = type.getNextSibling()) {
                if (type.getNodeType() != 1 || type.getLocalName().equals("extras") && type.getLocalName().equals("picture")) continue;
                return type.getLocalName();
            }
            return null;
        }

        private void processTemplate(Node n, @Nullable Map<String, Integer> ff) {
            if (ff == null) {
                ff = new HashMap<String, Integer>();
            }
            HashMap<String, Integer> ss = new HashMap<String, Integer>();
            for (Node n2 = n.getFirstChild(); n2 != null; n2 = n2.getNextSibling()) {
                if (n2.getNodeType() != 1) continue;
                String s = n2.getLocalName();
                if (s.equals("subform")) {
                    Integer i;
                    Node name = n2.getAttributes().getNamedItem("name");
                    String nn = "#subform";
                    boolean annon = true;
                    if (name != null) {
                        nn = Xml2SomTemplate.escapeSom(name.getNodeValue());
                        annon = false;
                    }
                    if (annon) {
                        i = this.anform;
                        ++this.anform;
                    } else {
                        i = (Integer)ss.get(nn);
                        i = i == null ? Integer.valueOf(0) : Integer.valueOf(i + 1);
                        ss.put(nn, i);
                    }
                    this.stack.push(nn + "[" + i.toString() + "]");
                    ++this.templateLevel;
                    if (annon) {
                        this.processTemplate(n2, ff);
                    } else {
                        this.processTemplate(n2, null);
                    }
                    --this.templateLevel;
                    this.stack.pop();
                    continue;
                }
                if (s.equals("field") || s.equals("exclGroup")) {
                    Node name = n2.getAttributes().getNamedItem("name");
                    if (name == null) continue;
                    String nn = Xml2SomTemplate.escapeSom(name.getNodeValue());
                    Integer i = ff.get(nn);
                    i = i == null ? Integer.valueOf(0) : Integer.valueOf(i + 1);
                    ff.put(nn, i);
                    this.stack.push(nn + "[" + i.toString() + "]");
                    String unstack = this.printStack();
                    this.order.add(unstack);
                    this.inverseSearchAdd(unstack);
                    this.name2Node.put(unstack, n2);
                    this.stack.pop();
                    continue;
                }
                if (this.dynamicForm || this.templateLevel <= 0 || !s.equals("occur")) continue;
                int initial = 1;
                int min = 1;
                int max = 1;
                Node a = n2.getAttributes().getNamedItem("initial");
                if (a != null) {
                    try {
                        initial = Integer.parseInt(a.getNodeValue().trim());
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                if ((a = n2.getAttributes().getNamedItem("min")) != null) {
                    try {
                        min = Integer.parseInt(a.getNodeValue().trim());
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                if ((a = n2.getAttributes().getNamedItem("max")) != null) {
                    try {
                        max = Integer.parseInt(a.getNodeValue().trim());
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                if (initial == min && min == max) continue;
                this.dynamicForm = true;
            }
        }

        public boolean isDynamicForm() {
            return this.dynamicForm;
        }

        public void setDynamicForm(boolean dynamicForm) {
            this.dynamicForm = dynamicForm;
        }
    }

    public static class AcroFieldsSearch
    extends Xml2Som {
        private Map<String, String> acroShort2LongName;

        public AcroFieldsSearch(Collection<String> items) {
            this.inverseSearch = new HashMap();
            this.acroShort2LongName = new HashMap<String, String>();
            for (String itemName : items) {
                String itemShort = AcroFieldsSearch.getShortName(itemName);
                this.acroShort2LongName.put(itemShort, itemName);
                AcroFieldsSearch.addSomNameToSearchNodeChain(this.inverseSearch, AcroFieldsSearch.splitParts(itemShort), itemName);
            }
        }

        @Deprecated
        public HashMap getAcroShort2LongName() {
            return (HashMap)this.acroShort2LongName;
        }

        @Deprecated
        public void setAcroShort2LongName(HashMap acroShort2LongName) {
            this.acroShort2LongName = acroShort2LongName;
        }

        public Map<String, String> getLongByShortNames() {
            return this.acroShort2LongName;
        }

        public void setLongByShortNames(Map<String, String> acroShort2LongName) {
            this.acroShort2LongName = acroShort2LongName;
        }
    }

    public static class Xml2SomDatasets
    extends Xml2Som {
        public Xml2SomDatasets(Node n) {
            this.order = new ArrayList();
            this.name2Node = new HashMap();
            this.stack = new Stack2();
            this.anform = 0;
            this.inverseSearch = new HashMap();
            this.processDatasetsInternal(n);
        }

        public Node insertNode(Node n, String shortName) {
            Stack2 stack = Xml2SomDatasets.splitParts(shortName);
            Document doc = n.getOwnerDocument();
            Node n2 = null;
            n = n.getFirstChild();
            for (Object o : stack) {
                String s;
                String part = (String)o;
                int idx = part.lastIndexOf(91);
                String name = part.substring(0, idx);
                idx = Integer.parseInt(part.substring(idx + 1, part.length() - 1));
                int found = -1;
                for (n2 = n.getFirstChild(); !(n2 == null || n2.getNodeType() == 1 && (s = Xml2SomDatasets.escapeSom(n2.getLocalName())).equals(name) && ++found == idx); n2 = n2.getNextSibling()) {
                }
                while (found < idx) {
                    n2 = doc.createElementNS(null, name);
                    n2 = n.appendChild(n2);
                    Attr attr = doc.createAttributeNS(XfaForm.XFA_DATA_SCHEMA, "dataNode");
                    attr.setNodeValue("dataGroup");
                    n2.getAttributes().setNamedItemNS(attr);
                    ++found;
                }
                n = n2;
            }
            Xml2SomDatasets.addSomNameToSearchNodeChain(this.inverseSearch, stack, shortName);
            this.name2Node.put(shortName, n2);
            this.order.add(shortName);
            return n2;
        }

        private static boolean hasChildren(Node n) {
            Node dataNodeN = n.getAttributes().getNamedItemNS(XfaForm.XFA_DATA_SCHEMA, "dataNode");
            if (dataNodeN != null) {
                String dataNode = dataNodeN.getNodeValue();
                if ("dataGroup".equals(dataNode)) {
                    return true;
                }
                if ("dataValue".equals(dataNode)) {
                    return false;
                }
            }
            if (!n.hasChildNodes()) {
                return false;
            }
            for (Node n2 = n.getFirstChild(); n2 != null; n2 = n2.getNextSibling()) {
                if (n2.getNodeType() != 1) continue;
                return true;
            }
            return false;
        }

        private void processDatasetsInternal(Node n) {
            HashMap<String, Integer> ss = new HashMap<String, Integer>();
            for (Node n2 = n.getFirstChild(); n2 != null; n2 = n2.getNextSibling()) {
                if (n2.getNodeType() != 1) continue;
                String s = Xml2SomDatasets.escapeSom(n2.getLocalName());
                Integer i = (Integer)ss.get(s);
                i = i == null ? Integer.valueOf(0) : Integer.valueOf(i + 1);
                ss.put(s, i);
                if (Xml2SomDatasets.hasChildren(n2)) {
                    this.stack.push(s + "[" + i.toString() + "]");
                    this.processDatasetsInternal(n2);
                    this.stack.pop();
                    continue;
                }
                this.stack.push(s + "[" + i.toString() + "]");
                String unstack = this.printStack();
                this.order.add(unstack);
                this.inverseSearchAdd(unstack);
                this.name2Node.put(unstack, n2);
                this.stack.pop();
            }
        }
    }

    public static class Xml2Som {
        protected List<String> order;
        protected Map<String, Node> name2Node;
        protected Map<String, InverseStore> inverseSearch;
        protected Stack2 stack;
        protected int anform;

        public static String escapeSom(String s) {
            if (s == null) {
                return "";
            }
            int idx = s.indexOf(46);
            if (idx < 0) {
                return s;
            }
            StringBuilder sb = new StringBuilder();
            int last = 0;
            while (idx >= 0) {
                sb.append(s, last, idx);
                sb.append('\\');
                last = idx;
                idx = s.indexOf(46, idx + 1);
            }
            sb.append(s.substring(last));
            return sb.toString();
        }

        public static String unescapeSom(String s) {
            int idx = s.indexOf(92);
            if (idx < 0) {
                return s;
            }
            StringBuilder sb = new StringBuilder();
            int last = 0;
            while (idx >= 0) {
                sb.append(s, last, idx);
                last = idx + 1;
                idx = s.indexOf(92, idx + 1);
            }
            sb.append(s.substring(last));
            return sb.toString();
        }

        protected String printStack() {
            if (this.stack.empty()) {
                return "";
            }
            StringBuilder s = new StringBuilder();
            for (String o : this.stack) {
                s.append('.').append(o);
            }
            return s.substring(1);
        }

        public static String getShortName(String s) {
            int idx = s.indexOf(".#subform[");
            if (idx < 0) {
                return s;
            }
            int last = 0;
            StringBuilder sb = new StringBuilder();
            while (idx >= 0) {
                sb.append(s, last, idx);
                idx = s.indexOf("]", idx + 10);
                if (idx < 0) {
                    return sb.toString();
                }
                last = idx + 1;
                idx = s.indexOf(".#subform[", last);
            }
            sb.append(s.substring(last));
            return sb.toString();
        }

        public void inverseSearchAdd(String unstack) {
            Xml2Som.addSomNameToSearchNodeChain(this.inverseSearch, this.stack, unstack);
        }

        @Deprecated
        public static void inverseSearchAdd(HashMap inverseSearch, Stack2 stack, String unstack) {
            Xml2Som.addSomNameToSearchNodeChain(inverseSearch, stack, unstack);
        }

        public static void addSomNameToSearchNodeChain(Map<String, InverseStore> inverseSearch, Stack2 stack, String unstack) {
            String last = stack.peek();
            InverseStore store = inverseSearch.get(last);
            if (store == null) {
                store = new InverseStore();
                inverseSearch.put(last, store);
            }
            for (int k = stack.size() - 2; k >= 0; --k) {
                InverseStore store2;
                last = (String)stack.get(k);
                int idx = store.part.indexOf(last);
                if (idx < 0) {
                    store.part.add(last);
                    store2 = new InverseStore();
                    store.follow.add(store2);
                } else {
                    store2 = (InverseStore)store.follow.get(idx);
                }
                store = store2;
            }
            store.part.add("");
            store.follow.add(unstack);
        }

        @Deprecated
        public String inverseSearchGlobal(ArrayList parts) {
            return this.inverseSearch(parts);
        }

        public String inverseSearch(List<String> parts) {
            if (parts.isEmpty()) {
                return null;
            }
            InverseStore store = this.inverseSearch.get(parts.get(parts.size() - 1));
            if (store == null) {
                return null;
            }
            for (int k = parts.size() - 2; k >= 0; --k) {
                String part = parts.get(k);
                int idx = store.part.indexOf(part);
                if (idx < 0) {
                    if (store.isSimilar(part)) {
                        return null;
                    }
                    return store.getDefaultName();
                }
                store = (InverseStore)store.follow.get(idx);
            }
            return store.getDefaultName();
        }

        public static Stack2 splitParts(String name) {
            String part;
            while (name.startsWith(".")) {
                name = name.substring(1);
            }
            Stack2 parts = new Stack2();
            int last = 0;
            int pos = 0;
            while (true) {
                pos = last;
                while ((pos = name.indexOf(46, pos)) >= 0 && name.charAt(pos - 1) == '\\') {
                    ++pos;
                }
                if (pos < 0) break;
                part = name.substring(last, pos);
                if (!part.endsWith("]")) {
                    part = part + "[0]";
                }
                parts.add(part);
                last = pos + 1;
            }
            part = name.substring(last);
            if (!part.endsWith("]")) {
                part = part + "[0]";
            }
            parts.add(part);
            return parts;
        }

        @Deprecated
        public ArrayList getOrder() {
            return (ArrayList)this.order;
        }

        public List<String> getNamesOrder() {
            return this.order;
        }

        @Deprecated
        public void setOrder(ArrayList order) {
            this.order = order;
        }

        public void setNamesOrder(List<String> order) {
            this.order = order;
        }

        @Deprecated
        public HashMap getName2Node() {
            return (HashMap)this.name2Node;
        }

        public Map<String, Node> getNodesByName() {
            return this.name2Node;
        }

        @Deprecated
        public void setName2Node(HashMap name2Node) {
            this.name2Node = name2Node;
        }

        public void setNodesByName(Map<String, Node> name2Node) {
            this.name2Node = name2Node;
        }

        @Deprecated
        public HashMap getInverseSearch() {
            return (HashMap)this.inverseSearch;
        }

        public Map<String, InverseStore> getInverseSearchData() {
            return this.inverseSearch;
        }

        @Deprecated
        public void setInverseSearch(Map<String, InverseStore> inverseSearch) {
            this.inverseSearch = inverseSearch;
        }

        public void setInverseSearchData(Map<String, InverseStore> inverseSearch) {
            this.inverseSearch = inverseSearch;
        }
    }

    public static class Stack2
    extends ArrayList<String> {
        private static final long serialVersionUID = -7451476576174095212L;

        public String peek() {
            if (this.size() == 0) {
                throw new EmptyStackException();
            }
            return (String)this.get(this.size() - 1);
        }

        public String pop() {
            if (this.size() == 0) {
                throw new EmptyStackException();
            }
            String ret = (String)this.get(this.size() - 1);
            this.remove(this.size() - 1);
            return ret;
        }

        public String push(String item) {
            this.add(item);
            return item;
        }

        public boolean empty() {
            return this.size() == 0;
        }
    }

    public static class InverseStore {
        protected List<String> part = new ArrayList<String>();
        protected List<Object> follow = new ArrayList<Object>();

        public String getDefaultName() {
            InverseStore store = this;
            Object obj;
            while (!((obj = store.follow.get(0)) instanceof String)) {
                store = (InverseStore)obj;
            }
            return (String)obj;
        }

        public boolean isSimilar(String name) {
            int idx = name.indexOf(91);
            name = name.substring(0, idx + 1);
            for (String o : this.part) {
                if (!o.startsWith(name)) continue;
                return true;
            }
            return false;
        }
    }
}

