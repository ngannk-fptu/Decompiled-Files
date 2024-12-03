/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.Binding
 *  javax.wsdl.BindingFault
 *  javax.wsdl.BindingInput
 *  javax.wsdl.BindingOperation
 *  javax.wsdl.BindingOutput
 *  javax.wsdl.Definition
 *  javax.wsdl.Fault
 *  javax.wsdl.Import
 *  javax.wsdl.Input
 *  javax.wsdl.Message
 *  javax.wsdl.Operation
 *  javax.wsdl.Output
 *  javax.wsdl.Part
 *  javax.wsdl.Port
 *  javax.wsdl.PortType
 *  javax.wsdl.Service
 *  javax.wsdl.WSDLException
 *  javax.wsdl.extensions.ExtensibilityElement
 *  javax.wsdl.extensions.UnknownExtensibilityElement
 *  javax.wsdl.extensions.http.HTTPBinding
 *  javax.wsdl.extensions.mime.MIMEContent
 *  javax.wsdl.extensions.mime.MIMEMultipartRelated
 *  javax.wsdl.extensions.mime.MIMEPart
 *  javax.wsdl.extensions.soap.SOAPBinding
 *  javax.wsdl.extensions.soap.SOAPBody
 *  javax.wsdl.extensions.soap.SOAPFault
 *  javax.wsdl.extensions.soap.SOAPHeader
 *  javax.wsdl.extensions.soap.SOAPHeaderFault
 *  javax.wsdl.factory.WSDLFactory
 *  javax.wsdl.xml.WSDLReader
 */
package org.apache.axis.wsdl.symbolTable;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.wsdl.Binding;
import javax.wsdl.BindingFault;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Import;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.extensions.http.HTTPBinding;
import javax.wsdl.extensions.mime.MIMEContent;
import javax.wsdl.extensions.mime.MIMEMultipartRelated;
import javax.wsdl.extensions.mime.MIMEPart;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPFault;
import javax.wsdl.extensions.soap.SOAPHeader;
import javax.wsdl.extensions.soap.SOAPHeaderFault;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.holders.BooleanHolder;
import javax.xml.rpc.holders.IntHolder;
import javax.xml.rpc.holders.QNameHolder;
import org.apache.axis.Constants;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.URLHashSet;
import org.apache.axis.utils.XMLUtils;
import org.apache.axis.wsdl.symbolTable.BaseType;
import org.apache.axis.wsdl.symbolTable.BaseTypeMapping;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.CollectionElement;
import org.apache.axis.wsdl.symbolTable.CollectionType;
import org.apache.axis.wsdl.symbolTable.DefinedElement;
import org.apache.axis.wsdl.symbolTable.DefinedType;
import org.apache.axis.wsdl.symbolTable.Element;
import org.apache.axis.wsdl.symbolTable.ElementDecl;
import org.apache.axis.wsdl.symbolTable.FaultInfo;
import org.apache.axis.wsdl.symbolTable.MessageEntry;
import org.apache.axis.wsdl.symbolTable.MimeInfo;
import org.apache.axis.wsdl.symbolTable.Parameter;
import org.apache.axis.wsdl.symbolTable.Parameters;
import org.apache.axis.wsdl.symbolTable.PortEntry;
import org.apache.axis.wsdl.symbolTable.PortTypeEntry;
import org.apache.axis.wsdl.symbolTable.SchemaUtils;
import org.apache.axis.wsdl.symbolTable.ServiceEntry;
import org.apache.axis.wsdl.symbolTable.SymTabEntry;
import org.apache.axis.wsdl.symbolTable.Type;
import org.apache.axis.wsdl.symbolTable.TypeEntry;
import org.apache.axis.wsdl.symbolTable.Undefined;
import org.apache.axis.wsdl.symbolTable.UndefinedElement;
import org.apache.axis.wsdl.symbolTable.UndefinedType;
import org.apache.axis.wsdl.symbolTable.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SymbolTable {
    protected HashMap derivedTypes = new HashMap();
    private boolean addImports;
    private HashMap symbolTable = new HashMap();
    private final Map elementTypeEntries = new HashMap();
    private final Map elementIndex = Collections.unmodifiableMap(this.elementTypeEntries);
    private final Map typeTypeEntries = new HashMap();
    private final Map typeIndex = Collections.unmodifiableMap(this.typeTypeEntries);
    protected final Map node2ExtensionBase = new HashMap();
    private boolean verbose;
    protected boolean quiet;
    private BaseTypeMapping btm = null;
    private boolean nowrap;
    private boolean wrapped = false;
    public static final String ANON_TOKEN = ">";
    private Definition def = null;
    private String wsdlURI = null;
    private boolean wrapArrays;
    Set arrayTypeQNames = new HashSet();
    private final Map elementFormDefaults = new HashMap();
    private URLHashSet importedFiles = new URLHashSet();
    private static final int ABOVE_SCHEMA_LEVEL = -1;
    private static final int SCHEMA_LEVEL = 0;
    static /* synthetic */ Class class$org$apache$axis$wsdl$symbolTable$MessageEntry;
    static /* synthetic */ Class class$org$apache$axis$wsdl$symbolTable$PortTypeEntry;
    static /* synthetic */ Class class$org$apache$axis$wsdl$symbolTable$BindingEntry;
    static /* synthetic */ Class class$org$apache$axis$wsdl$symbolTable$ServiceEntry;
    static /* synthetic */ Class class$org$apache$axis$wsdl$symbolTable$UndefinedType;
    static /* synthetic */ Class class$org$apache$axis$wsdl$symbolTable$UndefinedElement;

    public SymbolTable(BaseTypeMapping btm, boolean addImports, boolean verbose, boolean nowrap) {
        this.btm = btm;
        this.addImports = addImports;
        this.verbose = verbose;
        this.nowrap = nowrap;
    }

    public boolean isQuiet() {
        return this.quiet;
    }

    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    public HashMap getHashMap() {
        return this.symbolTable;
    }

    public Vector getSymbols(QName qname) {
        return (Vector)this.symbolTable.get(qname);
    }

    public SymTabEntry get(QName qname, Class cls) {
        Vector v = (Vector)this.symbolTable.get(qname);
        if (v == null) {
            return null;
        }
        for (int i = 0; i < v.size(); ++i) {
            SymTabEntry entry = (SymTabEntry)v.elementAt(i);
            if (!cls.isInstance(entry)) continue;
            return entry;
        }
        return null;
    }

    public TypeEntry getTypeEntry(QName qname, boolean wantElementType) {
        if (wantElementType) {
            return this.getElement(qname);
        }
        return this.getType(qname);
    }

    public Type getType(QName qname) {
        return (Type)this.typeTypeEntries.get(qname);
    }

    public Element getElement(QName qname) {
        return (Element)this.elementTypeEntries.get(qname);
    }

    public MessageEntry getMessageEntry(QName qname) {
        return (MessageEntry)this.get(qname, class$org$apache$axis$wsdl$symbolTable$MessageEntry == null ? (class$org$apache$axis$wsdl$symbolTable$MessageEntry = SymbolTable.class$("org.apache.axis.wsdl.symbolTable.MessageEntry")) : class$org$apache$axis$wsdl$symbolTable$MessageEntry);
    }

    public PortTypeEntry getPortTypeEntry(QName qname) {
        return (PortTypeEntry)this.get(qname, class$org$apache$axis$wsdl$symbolTable$PortTypeEntry == null ? (class$org$apache$axis$wsdl$symbolTable$PortTypeEntry = SymbolTable.class$("org.apache.axis.wsdl.symbolTable.PortTypeEntry")) : class$org$apache$axis$wsdl$symbolTable$PortTypeEntry);
    }

    public BindingEntry getBindingEntry(QName qname) {
        return (BindingEntry)this.get(qname, class$org$apache$axis$wsdl$symbolTable$BindingEntry == null ? (class$org$apache$axis$wsdl$symbolTable$BindingEntry = SymbolTable.class$("org.apache.axis.wsdl.symbolTable.BindingEntry")) : class$org$apache$axis$wsdl$symbolTable$BindingEntry);
    }

    public ServiceEntry getServiceEntry(QName qname) {
        return (ServiceEntry)this.get(qname, class$org$apache$axis$wsdl$symbolTable$ServiceEntry == null ? (class$org$apache$axis$wsdl$symbolTable$ServiceEntry = SymbolTable.class$("org.apache.axis.wsdl.symbolTable.ServiceEntry")) : class$org$apache$axis$wsdl$symbolTable$ServiceEntry);
    }

    public Vector getTypes() {
        Vector v = new Vector();
        v.addAll(this.elementTypeEntries.values());
        v.addAll(this.typeTypeEntries.values());
        return v;
    }

    public Map getElementIndex() {
        return this.elementIndex;
    }

    public Map getTypeIndex() {
        return this.typeIndex;
    }

    public int getTypeEntryCount() {
        return this.elementTypeEntries.size() + this.typeTypeEntries.size();
    }

    public Definition getDefinition() {
        return this.def;
    }

    public String getWSDLURI() {
        return this.wsdlURI;
    }

    public boolean isWrapped() {
        return this.wrapped;
    }

    public void setWrapped(boolean wrapped) {
        this.wrapped = wrapped;
    }

    public void dump(PrintStream out) {
        out.println();
        out.println(Messages.getMessage("symbolTable00"));
        out.println("-----------------------");
        Iterator it = this.symbolTable.values().iterator();
        while (it.hasNext()) {
            Vector v = (Vector)it.next();
            for (int i = 0; i < v.size(); ++i) {
                out.println(v.elementAt(i).getClass().getName());
                out.println(v.elementAt(i));
            }
        }
        out.println("-----------------------");
    }

    public void populate(String uri) throws IOException, WSDLException, SAXException, ParserConfigurationException {
        this.populate(uri, null, null);
    }

    public void populate(String uri, String username, String password) throws IOException, WSDLException, SAXException, ParserConfigurationException {
        if (this.verbose) {
            System.out.println(Messages.getMessage("parsing00", uri));
        }
        Document doc = XMLUtils.newDocument(uri, username, password);
        this.wsdlURI = uri;
        try {
            File f = new File(uri);
            if (f.exists()) {
                uri = f.toURL().toString();
            }
        }
        catch (Exception e) {
            // empty catch block
        }
        this.populate(uri, doc);
    }

    public void populate(String context, Document doc) throws IOException, SAXException, WSDLException, ParserConfigurationException {
        WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
        reader.setFeature("javax.wsdl.verbose", this.verbose);
        this.def = reader.readWSDL(context, doc);
        this.add(context, this.def, doc);
    }

    protected void add(String context, Definition def, Document doc) throws IOException, SAXException, WSDLException, ParserConfigurationException {
        URL contextURL = context == null ? null : SymbolTable.getURL(null, context);
        this.populate(contextURL, def, doc, null);
        this.processTypes();
        this.checkForUndefined();
        this.populateParameters();
        this.setReferences(def, doc);
    }

    private void checkForUndefined(Definition def, String filename) throws IOException {
        if (def != null) {
            Iterator ib = def.getBindings().values().iterator();
            while (ib.hasNext()) {
                Binding binding = (Binding)ib.next();
                if (!binding.isUndefined()) continue;
                if (filename == null) {
                    throw new IOException(Messages.getMessage("emitFailtUndefinedBinding01", binding.getQName().getLocalPart()));
                }
                throw new IOException(Messages.getMessage("emitFailtUndefinedBinding02", binding.getQName().getLocalPart(), filename));
            }
            Iterator ip = def.getPortTypes().values().iterator();
            while (ip.hasNext()) {
                PortType portType = (PortType)ip.next();
                if (!portType.isUndefined()) continue;
                if (filename == null) {
                    throw new IOException(Messages.getMessage("emitFailtUndefinedPort01", portType.getQName().getLocalPart()));
                }
                throw new IOException(Messages.getMessage("emitFailtUndefinedPort02", portType.getQName().getLocalPart(), filename));
            }
        }
    }

    private void checkForUndefined() throws IOException {
        Iterator it = this.symbolTable.values().iterator();
        while (it.hasNext()) {
            Vector v = (Vector)it.next();
            for (int i = 0; i < v.size(); ++i) {
                SymTabEntry entry = (SymTabEntry)v.get(i);
                if (entry instanceof UndefinedType) {
                    QName qn = entry.getQName();
                    if (qn.getLocalPart().equals("dateTime") && !qn.getNamespaceURI().equals("http://www.w3.org/2001/XMLSchema") || qn.getLocalPart().equals("timeInstant") && qn.getNamespaceURI().equals("http://www.w3.org/2001/XMLSchema")) {
                        throw new IOException(Messages.getMessage("wrongNamespace00", qn.getLocalPart(), qn.getNamespaceURI()));
                    }
                    if (SchemaUtils.isSimpleSchemaType(qn)) {
                        throw new IOException(Messages.getMessage("unsupportedSchemaType00", qn.getLocalPart()));
                    }
                    throw new IOException(Messages.getMessage("undefined00", qn.toString()));
                }
                if (!(entry instanceof UndefinedElement)) continue;
                throw new IOException(Messages.getMessage("undefinedElem00", entry.getQName().toString()));
            }
        }
    }

    private void populate(URL context, Definition def, Document doc, String filename) throws IOException, ParserConfigurationException, SAXException, WSDLException {
        if (doc != null) {
            this.populateTypes(context, doc);
            if (this.addImports) {
                this.lookForImports(context, doc);
            }
        }
        if (def != null) {
            this.checkForUndefined(def, filename);
            if (this.addImports) {
                Map imports = def.getImports();
                Object[] importKeys = imports.keySet().toArray();
                for (int i = 0; i < importKeys.length; ++i) {
                    Vector v = (Vector)imports.get(importKeys[i]);
                    for (int j = 0; j < v.size(); ++j) {
                        Import imp = (Import)v.get(j);
                        if (this.importedFiles.contains(imp.getLocationURI())) continue;
                        this.importedFiles.add(imp.getLocationURI());
                        URL url = SymbolTable.getURL(context, imp.getLocationURI());
                        this.populate(url, imp.getDefinition(), XMLUtils.newDocument(url.toString()), url.toString());
                    }
                }
            }
            this.populateMessages(def);
            this.populatePortTypes(def);
            this.populateBindings(def);
            this.populateServices(def);
        }
    }

    private static URL getURL(URL contextURL, String spec) throws IOException {
        String path = spec.replace('\\', '/');
        URL url = null;
        try {
            url = new URL(contextURL, path);
            if (contextURL != null && url.getProtocol().equals("file") && contextURL.getProtocol().equals("file")) {
                url = SymbolTable.getFileURL(contextURL, path);
            }
        }
        catch (MalformedURLException me) {
            url = SymbolTable.getFileURL(contextURL, path);
        }
        return url;
    }

    private static URL getFileURL(URL contextURL, String path) throws IOException {
        if (contextURL != null) {
            String contextFileName = contextURL.getFile();
            URL parent = null;
            File parentFile = new File(contextFileName).getParentFile();
            if (parentFile != null) {
                parent = parentFile.toURL();
            }
            if (parent != null) {
                return new URL(parent, path);
            }
        }
        return new URL("file", "", path);
    }

    private void lookForImports(URL context, Node node) throws IOException, ParserConfigurationException, SAXException, WSDLException {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node child = children.item(i);
            if ("import".equals(child.getLocalName())) {
                URL url;
                NamedNodeMap attributes = child.getAttributes();
                Node namespace = attributes.getNamedItem("namespace");
                if (namespace != null && this.isKnownNamespace(namespace.getNodeValue())) continue;
                Node importFile = attributes.getNamedItem("schemaLocation");
                if (importFile != null && !this.importedFiles.contains(url = SymbolTable.getURL(context, importFile.getNodeValue()))) {
                    this.importedFiles.add(url);
                    String filename = url.toString();
                    this.populate(url, null, XMLUtils.newDocument(filename), filename);
                }
            }
            this.lookForImports(context, child);
        }
    }

    public boolean isKnownNamespace(String namespace) {
        if (Constants.isSOAP_ENC(namespace)) {
            return true;
        }
        if (Constants.isSchemaXSD(namespace)) {
            return true;
        }
        if (Constants.isSchemaXSI(namespace)) {
            return true;
        }
        return namespace.equals("http://www.w3.org/XML/1998/namespace");
    }

    public void populateTypes(URL context, Document doc) throws IOException, SAXException, WSDLException, ParserConfigurationException {
        this.addTypes(context, doc, -1);
    }

    private void addTypes(URL context, Node node, int level) throws IOException, ParserConfigurationException, WSDLException, SAXException {
        if (node == null) {
            return;
        }
        String localPart = node.getLocalName();
        if (localPart != null) {
            String includeName;
            Node re;
            boolean isXSD = Constants.isSchemaXSD(node.getNamespaceURI());
            if (isXSD && localPart.equals("complexType") || localPart.equals("simpleType")) {
                QName[] memberTypes;
                Node union;
                Node list;
                re = SchemaUtils.getRestrictionOrExtensionNode(node);
                if (re != null && Utils.getAttribute(re, "base") != null) {
                    this.createTypeFromRef(re);
                }
                if ((list = SchemaUtils.getListNode(node)) != null && Utils.getAttribute(list, "itemType") != null) {
                    this.createTypeFromRef(list);
                }
                if ((union = SchemaUtils.getUnionNode(node)) != null && (memberTypes = Utils.getMemberTypeQNames(union)) != null) {
                    for (int i = 0; i < memberTypes.length; ++i) {
                        if (!SchemaUtils.isSimpleSchemaType(memberTypes[i]) || this.getType(memberTypes[i]) != null) continue;
                        this.symbolTablePut(new BaseType(memberTypes[i]));
                    }
                }
                this.createTypeFromDef(node, false, false);
            } else if (isXSD && localPart.equals("element")) {
                this.createTypeFromRef(node);
                re = SchemaUtils.getRestrictionOrExtensionNode(node);
                if (re != null && Utils.getAttribute(re, "base") != null) {
                    this.createTypeFromRef(re);
                }
                this.createTypeFromDef(node, true, level > 0);
            } else if (isXSD && localPart.equals("attributeGroup")) {
                this.createTypeFromRef(node);
                this.createTypeFromDef(node, false, level > 0);
            } else if (isXSD && localPart.equals("group")) {
                this.createTypeFromRef(node);
                this.createTypeFromDef(node, false, level > 0);
            } else if (isXSD && localPart.equals("attribute")) {
                BooleanHolder forElement = new BooleanHolder();
                QName refQName = Utils.getTypeQName(node, forElement, false);
                if (refQName != null && !forElement.value) {
                    this.createTypeFromRef(node);
                    if (refQName != null) {
                        TypeEntry refType = this.getTypeEntry(refQName, false);
                        if (refType != null && refType instanceof Undefined) {
                            refType.setSimpleType(true);
                        } else if (refType == null || !(refType instanceof BaseType) && !refType.isSimpleType()) {
                            throw new IOException(Messages.getMessage("AttrNotSimpleType01", refQName.toString()));
                        }
                    }
                }
                this.createTypeFromDef(node, true, level > 0);
            } else if (isXSD && localPart.equals("any")) {
                if (this.getType(Constants.XSD_ANY) == null) {
                    BaseType type = new BaseType(Constants.XSD_ANY);
                    this.symbolTablePut(type);
                }
            } else if (localPart.equals("part") && Constants.isWSDL(node.getNamespaceURI())) {
                this.createTypeFromRef(node);
            } else if (isXSD && localPart.equals("include") && (includeName = Utils.getAttribute(node, "schemaLocation")) != null) {
                org.w3c.dom.Element parentSchemaEl;
                URL url = SymbolTable.getURL(context, includeName);
                Document includeDoc = XMLUtils.newDocument(url.toString());
                org.w3c.dom.Element schemaEl = includeDoc.getDocumentElement();
                if (!schemaEl.hasAttribute("targetNamespace") && (parentSchemaEl = (org.w3c.dom.Element)node.getParentNode()).hasAttribute("targetNamespace")) {
                    String tns = parentSchemaEl.getAttribute("targetNamespace");
                    schemaEl.setAttribute("targetNamespace", tns);
                    schemaEl.setAttribute("xmlns", tns);
                }
                this.populate(url, null, includeDoc, url.toString());
            }
        }
        if (level == -1) {
            if (localPart != null && localPart.equals("schema")) {
                level = 0;
                String targetNamespace = ((org.w3c.dom.Element)node).getAttribute("targetNamespace");
                String elementFormDefault = ((org.w3c.dom.Element)node).getAttribute("elementFormDefault");
                if (targetNamespace != null && targetNamespace.length() > 0) {
                    String string = elementFormDefault = elementFormDefault == null || elementFormDefault.length() == 0 ? "unqualified" : elementFormDefault;
                    if (this.elementFormDefaults.get(targetNamespace) == null) {
                        this.elementFormDefaults.put(targetNamespace, elementFormDefault);
                    }
                }
            }
        } else {
            ++level;
        }
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            this.addTypes(context, children.item(i), level);
        }
    }

    private void createTypeFromDef(Node node, boolean isElement, boolean belowSchemaLevel) throws IOException {
        QName qName = Utils.getNodeNameQName(node);
        if (qName != null) {
            if (!isElement && this.btm.getBaseName(qName) != null) {
                return;
            }
            BooleanHolder forElement = new BooleanHolder();
            QName refQName = Utils.getTypeQName(node, forElement, false);
            if (refQName != null) {
                if (qName.getLocalPart().length() == 0) {
                    String name = Utils.getAttribute(node, "name");
                    if (name == null) {
                        name = "unknown";
                    }
                    throw new IOException(Messages.getMessage("emptyref00", name));
                }
                TypeEntry refType = this.getTypeEntry(refQName, forElement.value);
                if (!belowSchemaLevel) {
                    if (refType == null) {
                        throw new IOException(Messages.getMessage("absentRef00", refQName.toString(), qName.toString()));
                    }
                    this.symbolTablePut(new DefinedElement(qName, refType, node, ""));
                }
            } else {
                IntHolder numDims = new IntHolder();
                BooleanHolder underlTypeNillable = new BooleanHolder();
                QNameHolder itemQName = this.wrapArrays ? null : new QNameHolder();
                numDims.value = 0;
                QName arrayEQName = SchemaUtils.getArrayComponentQName(node, numDims, underlTypeNillable, itemQName, this);
                if (arrayEQName != null) {
                    refQName = arrayEQName;
                    TypeEntry refType = this.getTypeEntry(refQName, false);
                    if (refType == null) {
                        String baseName = this.btm.getBaseName(refQName);
                        refType = baseName != null ? new BaseType(refQName) : new UndefinedType(refQName);
                        this.symbolTablePut(refType);
                    }
                    String dims = "";
                    while (numDims.value > 0) {
                        dims = dims + "[]";
                        --numDims.value;
                    }
                    TypeEntry defType = null;
                    if (isElement) {
                        if (!belowSchemaLevel) {
                            defType = new DefinedElement(qName, refType, node, dims);
                            defType.setComponentType(arrayEQName);
                            if (itemQName != null) {
                                defType.setItemQName(itemQName.value);
                            }
                        }
                    } else {
                        defType = new DefinedType(qName, refType, node, dims);
                        defType.setComponentType(arrayEQName);
                        defType.setUnderlTypeNillable(underlTypeNillable.value);
                        if (itemQName != null) {
                            defType.setItemQName(itemQName.value);
                        }
                    }
                    if (defType != null) {
                        this.symbolTablePut(defType);
                    }
                } else {
                    String baseName = this.btm.getBaseName(qName);
                    if (baseName != null) {
                        this.symbolTablePut(new BaseType(qName));
                    } else {
                        TypeEntry te = null;
                        TypeEntry parentType = null;
                        if (!isElement) {
                            te = new DefinedType(qName, node);
                            if (qName.getLocalPart().indexOf(ANON_TOKEN) >= 0) {
                                Node parent = node.getParentNode();
                                QName parentQName = Utils.getNodeNameQName(parent);
                                parentType = this.getElement(parentQName);
                            }
                        } else if (!belowSchemaLevel) {
                            te = new DefinedElement(qName, node);
                        }
                        if (te != null) {
                            if (SchemaUtils.isSimpleTypeOrSimpleContent(node)) {
                                te.setSimpleType(true);
                            }
                            te = (TypeEntry)this.symbolTablePut(te);
                            if (parentType != null) {
                                parentType.setRefType(te);
                            }
                        }
                    }
                }
            }
        }
    }

    private void createTypeFromRef(Node node) throws IOException {
        BooleanHolder forElement = new BooleanHolder();
        QName qName = Utils.getTypeQName(node, forElement, false);
        if (qName == null || Constants.isSchemaXSD(qName.getNamespaceURI()) && qName.getLocalPart().equals("simpleRestrictionModel")) {
            return;
        }
        if (qName.getLocalPart().length() == 0) {
            String name = Utils.getAttribute(node, "name");
            if (name == null) {
                name = "unknown";
            }
            throw new IOException(Messages.getMessage("emptyref00", name));
        }
        TypeEntry type = this.getTypeEntry(qName, forElement.value);
        if (type == null) {
            if (qName.getLocalPart().indexOf("[") > 0) {
                QName containedQName = Utils.getTypeQName(node, forElement, true);
                TypeEntry containedTE = this.getTypeEntry(containedQName, forElement.value);
                if (!forElement.value) {
                    if (containedTE == null) {
                        String baseName = this.btm.getBaseName(containedQName);
                        containedTE = baseName != null ? new BaseType(containedQName) : new UndefinedType(containedQName);
                        this.symbolTablePut(containedTE);
                    }
                    this.symbolTablePut(new CollectionType(qName, containedTE, node, "[]"));
                } else {
                    if (containedTE == null) {
                        containedTE = new UndefinedElement(containedQName);
                        this.symbolTablePut(containedTE);
                    }
                    this.symbolTablePut(new CollectionElement(qName, containedTE, node, "[]"));
                }
            } else {
                String baseName = this.btm.getBaseName(qName);
                if (baseName != null) {
                    this.symbolTablePut(new BaseType(qName));
                } else if (qName.equals(Constants.SOAP_COMMON_ATTRS11)) {
                    this.symbolTablePut(new BaseType(qName));
                    if (this.getTypeEntry(Constants.XSD_ID, false) == null) {
                        this.symbolTablePut(new BaseType(Constants.XSD_ID));
                    }
                    if (this.getTypeEntry(Constants.XSD_ANYURI, false) == null) {
                        this.symbolTablePut(new BaseType(Constants.XSD_ANYURI));
                    }
                } else if (qName.equals(Constants.SOAP_COMMON_ATTRS12)) {
                    this.symbolTablePut(new BaseType(qName));
                    if (this.getTypeEntry(Constants.XSD_ID, false) == null) {
                        this.symbolTablePut(new BaseType(Constants.XSD_ID));
                    }
                } else if (qName.equals(Constants.SOAP_ARRAY_ATTRS11)) {
                    this.symbolTablePut(new BaseType(qName));
                    if (this.getTypeEntry(Constants.XSD_STRING, false) == null) {
                        this.symbolTablePut(new BaseType(Constants.XSD_STRING));
                    }
                } else if (qName.equals(Constants.SOAP_ARRAY_ATTRS12)) {
                    this.symbolTablePut(new BaseType(qName));
                    if (this.getTypeEntry(Constants.XSD_STRING, false) == null) {
                        this.symbolTablePut(new BaseType(Constants.XSD_STRING));
                    }
                    if (this.getTypeEntry(Constants.XSD_QNAME, false) == null) {
                        this.symbolTablePut(new BaseType(Constants.XSD_QNAME));
                    }
                } else if (!forElement.value) {
                    this.symbolTablePut(new UndefinedType(qName));
                } else {
                    this.symbolTablePut(new UndefinedElement(qName));
                }
            }
        }
    }

    private void populateMessages(Definition def) throws IOException {
        Iterator i = def.getMessages().values().iterator();
        while (i.hasNext()) {
            Message message = (Message)i.next();
            MessageEntry mEntry = new MessageEntry(message);
            this.symbolTablePut(mEntry);
        }
    }

    protected void ensureOperationMessageValid(Message message) throws IOException {
        if (message == null) {
            throw new IOException("<input>,<output>, or <fault> in <operation ..> without attribute 'message' found. Attribute 'message' is required.");
        }
        if (message.isUndefined()) {
            throw new IOException("<input ..>, <output ..> or <fault ..> in <portType> with undefined message found. message name is '" + message.getQName().toString() + "'");
        }
    }

    protected void ensureOperationValid(Operation operation) throws IOException {
        Map faults;
        Output output;
        Message message;
        if (operation == null) {
            throw new IllegalArgumentException("parameter 'operation' must not be null");
        }
        Input input = operation.getInput();
        if (input != null) {
            message = input.getMessage();
            if (message == null) {
                throw new IOException("No 'message' attribute in <input> for operation '" + operation.getName() + "'");
            }
            this.ensureOperationMessageValid(message);
        }
        if ((output = operation.getOutput()) != null) {
            message = output.getMessage();
            if (message == null) {
                throw new IOException("No 'message' attribute in <output> for operation '" + operation.getName() + "'");
            }
            this.ensureOperationMessageValid(output.getMessage());
        }
        if ((faults = operation.getFaults()) != null) {
            Iterator it = faults.values().iterator();
            while (it.hasNext()) {
                Fault fault = (Fault)it.next();
                message = fault.getMessage();
                if (message == null) {
                    throw new IOException("No 'message' attribute in <fault> named '" + fault.getName() + "' for operation '" + operation.getName() + "'");
                }
                this.ensureOperationMessageValid(message);
            }
        }
    }

    protected void ensureOperationsOfPortTypeValid(PortType portType) throws IOException {
        if (portType == null) {
            throw new IllegalArgumentException("parameter 'portType' must not be null");
        }
        List operations = portType.getOperations();
        if (operations == null || operations.size() == 0) {
            return;
        }
        Iterator it = operations.iterator();
        while (it.hasNext()) {
            Operation operation = (Operation)it.next();
            this.ensureOperationValid(operation);
        }
    }

    private void populatePortTypes(Definition def) throws IOException {
        Iterator i = def.getPortTypes().values().iterator();
        while (i.hasNext()) {
            PortType portType = (PortType)i.next();
            if (portType.isUndefined()) continue;
            this.ensureOperationsOfPortTypeValid(portType);
            PortTypeEntry ptEntry = new PortTypeEntry(portType);
            this.symbolTablePut(ptEntry);
        }
    }

    private void populateParameters() throws IOException {
        Iterator it = this.symbolTable.values().iterator();
        while (it.hasNext()) {
            Vector v = (Vector)it.next();
            for (int i = 0; i < v.size(); ++i) {
                BindingEntry bEntry;
                if (!(v.get(i) instanceof BindingEntry) || (bEntry = (BindingEntry)v.get(i)).getBindingType() != 0) continue;
                Binding binding = bEntry.getBinding();
                Set bindOperations = bEntry.getOperations();
                PortType portType = binding.getPortType();
                HashMap<Operation, Parameters> parameters = new HashMap<Operation, Parameters>();
                Iterator operations = portType.getOperations().iterator();
                while (operations.hasNext()) {
                    Operation operation = (Operation)operations.next();
                    if (!bindOperations.contains(operation)) {
                        throw new IOException(Messages.getMessage("emitFailNoMatchingBindOperation01", operation.getName(), portType.getQName().getLocalPart()));
                    }
                    String namespace = portType.getQName().getNamespaceURI();
                    Parameters parms = this.getOperationParameters(operation, namespace, bEntry);
                    parameters.put(operation, parms);
                }
                bEntry.setParameters(parameters);
            }
        }
    }

    public Parameters getOperationParameters(Operation operation, String namespace, BindingEntry bindingEntry) throws IOException {
        Parameters parameters = new Parameters();
        Vector inputs = new Vector();
        Vector outputs = new Vector();
        List parameterOrder = operation.getParameterOrdering();
        if (parameterOrder != null && parameterOrder.isEmpty()) {
            parameterOrder = null;
        }
        Input input = operation.getInput();
        Output output = operation.getOutput();
        parameters.mep = operation.getStyle();
        if (parameterOrder != null && !this.wrapped && input != null) {
            Message inputMsg = input.getMessage();
            Map allInputs = inputMsg.getParts();
            List orderedInputs = inputMsg.getOrderedParts(parameterOrder);
            if (allInputs.size() != orderedInputs.size()) {
                throw new IOException(Messages.getMessage("emitFail00", operation.getName()));
            }
        }
        boolean literalInput = false;
        boolean literalOutput = false;
        if (bindingEntry != null) {
            literalInput = bindingEntry.getInputBodyType(operation) == Use.LITERAL;
            boolean bl = literalOutput = bindingEntry.getOutputBodyType(operation) == Use.LITERAL;
        }
        if (input != null && input.getMessage() != null) {
            this.getParametersFromParts(inputs, input.getMessage().getOrderedParts(null), literalInput, operation.getName(), bindingEntry);
        }
        if (output != null && output.getMessage() != null) {
            this.getParametersFromParts(outputs, output.getMessage().getOrderedParts(null), literalOutput, operation.getName(), bindingEntry);
        }
        if (parameterOrder != null && !this.wrapped) {
            for (int i = 0; i < parameterOrder.size(); ++i) {
                String name = (String)parameterOrder.get(i);
                int index = this.getPartIndex(name, inputs);
                int outdex = this.getPartIndex(name, outputs);
                if (index >= 0) {
                    this.addInishParm(inputs, outputs, index, outdex, parameters, true);
                    continue;
                }
                if (outdex >= 0) {
                    this.addOutParm(outputs, outdex, parameters, true);
                    continue;
                }
                System.err.println(Messages.getMessage("noPart00", name));
            }
        }
        if (this.wrapped && inputs.size() == 1 && outputs.size() == 1 && Utils.getLastLocalPart(((Parameter)inputs.get(0)).getName()).equals(Utils.getLastLocalPart(((Parameter)outputs.get(0)).getName()))) {
            this.addInishParm(inputs, null, 0, -1, parameters, false);
        } else {
            for (int i = 0; i < inputs.size(); ++i) {
                Parameter p = (Parameter)inputs.get(i);
                int outdex = this.getPartIndex(p.getName(), outputs);
                this.addInishParm(inputs, outputs, i, outdex, parameters, false);
            }
        }
        if (outputs.size() == 1) {
            parameters.returnParam = (Parameter)outputs.get(0);
            parameters.returnParam.setMode((byte)2);
            if (parameters.returnParam.getType() instanceof DefinedElement) {
                parameters.returnParam.setQName(parameters.returnParam.getType().getQName());
            }
            ++parameters.outputs;
        } else {
            for (int i = 0; i < outputs.size(); ++i) {
                this.addOutParm(outputs, i, parameters, false);
            }
        }
        parameters.faults = operation.getFaults();
        Vector<String> used = new Vector<String>(parameters.list.size());
        Iterator i = parameters.list.iterator();
        while (i.hasNext()) {
            Parameter parameter = (Parameter)i.next();
            int count = 2;
            while (used.contains(parameter.getName())) {
                parameter.setName(parameter.getName() + Integer.toString(count++));
            }
            used.add(parameter.getName());
        }
        return parameters;
    }

    private int getPartIndex(String name, Vector v) {
        name = Utils.getLastLocalPart(name);
        for (int i = 0; i < v.size(); ++i) {
            String paramName = ((Parameter)v.get(i)).getName();
            if (!name.equals(paramName = Utils.getLastLocalPart(paramName))) continue;
            return i;
        }
        return -1;
    }

    private void addInishParm(Vector inputs, Vector outputs, int index, int outdex, Parameters parameters, boolean trimInput) {
        Parameter p = (Parameter)inputs.get(index);
        if (p.getType() instanceof DefinedElement) {
            DefinedElement de = (DefinedElement)p.getType();
            p.setQName(de.getQName());
        }
        if (p.getType() instanceof CollectionElement) {
            p.setQName(p.getType().getRefType().getQName());
        }
        if (trimInput) {
            inputs.remove(index);
        }
        if (outdex >= 0) {
            TypeEntry outParamEntry;
            Parameter outParam = (Parameter)outputs.get(outdex);
            TypeEntry paramEntry = p.getType();
            if (paramEntry.equals(outParamEntry = outParam.getType())) {
                outputs.remove(outdex);
                p.setMode((byte)3);
                ++parameters.inouts;
            } else {
                ++parameters.inputs;
            }
        } else {
            ++parameters.inputs;
        }
        parameters.list.add(p);
    }

    private void addOutParm(Vector outputs, int outdex, Parameters parameters, boolean trim) {
        Parameter p = (Parameter)outputs.get(outdex);
        if (p.getType() instanceof DefinedElement) {
            DefinedElement de = (DefinedElement)p.getType();
            p.setQName(de.getQName());
        }
        if (p.getType() instanceof CollectionElement) {
            p.setQName(p.getType().getRefType().getQName());
        }
        if (trim) {
            outputs.remove(outdex);
        }
        p.setMode((byte)2);
        ++parameters.outputs;
        parameters.list.add(p);
    }

    public void getParametersFromParts(Vector v, Collection parts, boolean literal, String opName, BindingEntry bindingEntry) throws IOException {
        int numberOfElements = 0;
        boolean possiblyWrapped = false;
        Iterator i = parts.iterator();
        while (i.hasNext()) {
            Part part = (Part)i.next();
            if (part.getElementName() == null) continue;
            ++numberOfElements;
            if (!part.getElementName().getLocalPart().equals(opName)) continue;
            possiblyWrapped = true;
        }
        if (!this.nowrap && literal && numberOfElements == 1 && possiblyWrapped) {
            this.wrapped = true;
        }
        i = parts.iterator();
        while (i.hasNext()) {
            BooleanHolder forElement;
            Parameter param = new Parameter();
            Part part = (Part)i.next();
            QName elementName = part.getElementName();
            QName typeName = part.getTypeName();
            String partName = part.getName();
            if (!literal || !this.wrapped || elementName == null) {
                param.setName(partName);
                if (typeName != null) {
                    param.setType(this.getType(typeName));
                } else if (elementName != null) {
                    param.setType(this.getElement(elementName));
                } else {
                    throw new IOException(Messages.getMessage("noTypeOrElement00", new String[]{partName, opName}));
                }
                this.fillParamInfo(param, bindingEntry, opName, partName);
                v.add(param);
                continue;
            }
            Node node = null;
            TypeEntry typeEntry = null;
            if (typeName != null && (bindingEntry == null || bindingEntry.getMIMETypes().size() == 0)) {
                String bindingName = bindingEntry == null ? "unknown" : bindingEntry.getBinding().getQName().toString();
                throw new IOException(Messages.getMessage("literalTypePart00", new String[]{partName, opName, bindingName}));
            }
            typeEntry = this.getTypeEntry(elementName, true);
            node = typeEntry.getNode();
            QName type = Utils.getTypeQName(node, forElement = new BooleanHolder(), false);
            if (type != null && !forElement.value) {
                typeEntry = this.getTypeEntry(type, false);
                node = typeEntry.getNode();
            }
            Vector vTypes = null;
            if (node == null) {
                this.wrapped = false;
            } else {
                if (typeEntry.getContainedAttributes() != null) {
                    this.wrapped = false;
                }
                if (!SchemaUtils.isWrappedType(node)) {
                    typeEntry.setOnlyLiteralReference(false);
                    this.wrapped = false;
                }
                vTypes = typeEntry.getContainedElements();
            }
            if (vTypes != null && this.wrapped) {
                for (int j = 0; j < vTypes.size(); ++j) {
                    ElementDecl elem = (ElementDecl)vTypes.elementAt(j);
                    Parameter p = new Parameter();
                    p.setQName(elem.getQName());
                    String paramName = p.getName();
                    int gt = paramName.lastIndexOf(ANON_TOKEN);
                    if (gt != 1) {
                        paramName = paramName.substring(gt + 1);
                    }
                    p.setName(paramName);
                    p.setType(elem.getType());
                    p.setOmittable(elem.getMinOccursIs0());
                    this.fillParamInfo(p, bindingEntry, opName, partName);
                    v.add(p);
                }
                continue;
            }
            param.setName(partName);
            if (typeName != null) {
                param.setType(this.getType(typeName));
            } else if (elementName != null) {
                param.setType(this.getElement(elementName));
            }
            this.fillParamInfo(param, bindingEntry, opName, partName);
            v.add(param);
        }
    }

    private void fillParamInfo(Parameter param, BindingEntry bindingEntry, String opName, String partName) {
        if (bindingEntry == null) {
            return;
        }
        this.setMIMEInfo(param, bindingEntry.getMIMEInfo(opName, partName));
        boolean isHeader = false;
        if (bindingEntry.isInHeaderPart(opName, partName)) {
            isHeader = true;
            param.setInHeader(true);
        }
        if (bindingEntry.isOutHeaderPart(opName, partName)) {
            isHeader = true;
            param.setOutHeader(true);
        }
        if (isHeader && bindingEntry.getBinding() != null) {
            List list = bindingEntry.getBinding().getBindingOperations();
            for (int i = 0; list != null && i < list.size(); ++i) {
                QName qName;
                BindingOperation operation = (BindingOperation)list.get(i);
                if (!operation.getName().equals(opName)) continue;
                if (param.isInHeader()) {
                    qName = this.getBindedParameterName(operation.getBindingInput().getExtensibilityElements(), param);
                    if (qName == null) continue;
                    param.setQName(qName);
                    continue;
                }
                if (!param.isOutHeader() || (qName = this.getBindedParameterName(operation.getBindingOutput().getExtensibilityElements(), param)) == null) continue;
                param.setQName(qName);
            }
        }
    }

    private QName getBindedParameterName(List elements, Parameter p) {
        QName paramName = null;
        String defaultNamespace = null;
        String parameterPartName = p.getName();
        Iterator k = elements.iterator();
        while (k.hasNext()) {
            SOAPHeader headerElement;
            String part;
            ExtensibilityElement element = (ExtensibilityElement)k.next();
            if (element instanceof SOAPBody) {
                SOAPBody bodyElement = (SOAPBody)element;
                List parts = bodyElement.getParts();
                if (parts == null || parts.size() == 0) {
                    defaultNamespace = bodyElement.getNamespaceURI();
                    continue;
                }
                boolean found = false;
                Iterator l = parts.iterator();
                while (l.hasNext()) {
                    Object o = l.next();
                    if (!(o instanceof String) || !parameterPartName.equals((String)o)) continue;
                    paramName = new QName(bodyElement.getNamespaceURI(), parameterPartName);
                    found = true;
                    break;
                }
                if (!found) continue;
                break;
            }
            if (!(element instanceof SOAPHeader) || !parameterPartName.equals(part = (headerElement = (SOAPHeader)element).getPart())) continue;
            paramName = new QName(headerElement.getNamespaceURI(), parameterPartName);
            break;
        }
        if (paramName == null && !p.isInHeader() && !p.isOutHeader()) {
            paramName = defaultNamespace != null ? new QName(defaultNamespace, parameterPartName) : p.getQName();
        }
        return paramName;
    }

    private void setMIMEInfo(Parameter p, MimeInfo mimeInfo) {
        QName mimeQName;
        if (mimeInfo == null && p.getType() != null && (mimeQName = p.getType().getQName()).getNamespaceURI().equals("http://xml.apache.org/xml-soap")) {
            if (Constants.MIME_IMAGE.equals(mimeQName)) {
                mimeInfo = new MimeInfo("image/jpeg", "");
            } else if (Constants.MIME_PLAINTEXT.equals(mimeQName)) {
                mimeInfo = new MimeInfo("text/plain", "");
            } else if (Constants.MIME_MULTIPART.equals(mimeQName)) {
                mimeInfo = new MimeInfo("multipart/related", "");
            } else if (Constants.MIME_SOURCE.equals(mimeQName)) {
                mimeInfo = new MimeInfo("text/xml", "");
            } else if (Constants.MIME_OCTETSTREAM.equals(mimeQName)) {
                mimeInfo = new MimeInfo("application/octet-stream", "");
            }
        }
        p.setMIMEInfo(mimeInfo);
    }

    private void populateBindings(Definition def) throws IOException {
        Iterator i = def.getBindings().values().iterator();
        while (i.hasNext()) {
            Binding binding = (Binding)i.next();
            BindingEntry bEntry = new BindingEntry(binding);
            this.symbolTablePut(bEntry);
            Iterator extensibilityElementsIterator = binding.getExtensibilityElements().iterator();
            while (extensibilityElementsIterator.hasNext()) {
                UnknownExtensibilityElement unkElement;
                QName name;
                Object obj = extensibilityElementsIterator.next();
                if (obj instanceof SOAPBinding) {
                    bEntry.setBindingType(0);
                    SOAPBinding sb = (SOAPBinding)obj;
                    String style = sb.getStyle();
                    if (!"rpc".equalsIgnoreCase(style)) continue;
                    bEntry.setBindingStyle(Style.RPC);
                    continue;
                }
                if (obj instanceof HTTPBinding) {
                    HTTPBinding hb = (HTTPBinding)obj;
                    if (hb.getVerb().equalsIgnoreCase("post")) {
                        bEntry.setBindingType(2);
                        continue;
                    }
                    bEntry.setBindingType(1);
                    continue;
                }
                if (!(obj instanceof UnknownExtensibilityElement) || !(name = (unkElement = (UnknownExtensibilityElement)obj).getElementType()).getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/soap12/") || !name.getLocalPart().equals("binding")) continue;
                bEntry.setBindingType(0);
                String style = unkElement.getElement().getAttribute("style");
                if (!"rpc".equalsIgnoreCase(style)) continue;
                bEntry.setBindingStyle(Style.RPC);
            }
            HashMap<Operation, BindingEntry.OperationAttr> attributes = new HashMap<Operation, BindingEntry.OperationAttr>();
            List bindList = binding.getBindingOperations();
            HashMap faultMap = new HashMap();
            Iterator opIterator = bindList.iterator();
            while (opIterator.hasNext()) {
                String outputName;
                BindingOperation bindOp = (BindingOperation)opIterator.next();
                Operation operation = bindOp.getOperation();
                BindingInput bindingInput = bindOp.getBindingInput();
                BindingOutput bindingOutput = bindOp.getBindingOutput();
                String opName = bindOp.getName();
                String inputName = bindingInput == null ? null : bindingInput.getName();
                String string = outputName = bindingOutput == null ? null : bindingOutput.getName();
                if (binding.getPortType().getOperation(opName, inputName, outputName) == null) {
                    throw new IOException(Messages.getMessage("unmatchedOp", new String[]{opName, inputName, outputName}));
                }
                ArrayList faults = new ArrayList();
                if (bindingInput != null && bindingInput.getExtensibilityElements() != null) {
                    Iterator inIter = bindingInput.getExtensibilityElements().iterator();
                    this.fillInBindingInfo(bEntry, operation, inIter, faults, true);
                }
                if (bindingOutput != null && bindingOutput.getExtensibilityElements() != null) {
                    Iterator outIter = bindingOutput.getExtensibilityElements().iterator();
                    this.fillInBindingInfo(bEntry, operation, outIter, faults, false);
                }
                this.faultsFromSOAPFault(binding, bindOp, operation, faults);
                faultMap.put(bindOp, faults);
                Use inputBodyType = bEntry.getInputBodyType(operation);
                Use outputBodyType = bEntry.getOutputBodyType(operation);
                attributes.put(bindOp.getOperation(), new BindingEntry.OperationAttr(inputBodyType, outputBodyType, faultMap));
                if (inputBodyType == Use.LITERAL || outputBodyType == Use.LITERAL) {
                    bEntry.setHasLiteral(true);
                }
                bEntry.setFaultBodyTypeMap(operation, faultMap);
            }
            bEntry.setFaults(faultMap);
        }
    }

    private void fillInBindingInfo(BindingEntry bEntry, Operation operation, Iterator it, ArrayList faults, boolean input) throws IOException {
        while (it.hasNext()) {
            Object obj = it.next();
            if (obj instanceof SOAPBody) {
                this.setBodyType(((SOAPBody)obj).getUse(), bEntry, operation, input);
                continue;
            }
            if (obj instanceof SOAPHeader) {
                SOAPHeader header = (SOAPHeader)obj;
                this.setBodyType(header.getUse(), bEntry, operation, input);
                bEntry.setHeaderPart(operation.getName(), header.getPart(), input ? 1 : 2);
                Iterator headerFaults = header.getSOAPHeaderFaults().iterator();
                while (headerFaults.hasNext()) {
                    SOAPHeaderFault headerFault = (SOAPHeaderFault)headerFaults.next();
                    faults.add(new FaultInfo(headerFault, this));
                }
                continue;
            }
            if (obj instanceof MIMEMultipartRelated) {
                bEntry.setBodyType(operation, this.addMIMETypes(bEntry, (MIMEMultipartRelated)obj, operation), input);
                continue;
            }
            if (!(obj instanceof UnknownExtensibilityElement)) continue;
            UnknownExtensibilityElement unkElement = (UnknownExtensibilityElement)obj;
            QName name = unkElement.getElementType();
            if (name.getNamespaceURI().equals("http://schemas.xmlsoap.org/ws/2002/04/dime/wsdl/") && name.getLocalPart().equals("message")) {
                this.fillInDIMEInformation(unkElement, input, operation, bEntry);
            }
            if (name.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/soap12/") && name.getLocalPart().equals("body")) {
                this.setBodyType(unkElement.getElement().getAttribute("use"), bEntry, operation, input);
            }
            if (!name.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/soap12/") || !name.getLocalPart().equals("header")) continue;
            this.setBodyType(unkElement.getElement().getAttribute("use"), bEntry, operation, input);
            bEntry.setHeaderPart(operation.getName(), unkElement.getElement().getAttribute("part"), input ? 1 : 2);
            NodeList headerFaults = unkElement.getElement().getChildNodes();
            for (int i = 0; i < headerFaults.getLength(); ++i) {
                String faultMessage = unkElement.getElement().getAttribute("message");
                String faultPart = unkElement.getElement().getAttribute("part");
                String faultUse = unkElement.getElement().getAttribute("use");
                String faultNamespaceURI = unkElement.getElement().getAttribute("namespace");
                QName faultMessageQName = null;
                int sep = faultMessage.indexOf(58);
                faultMessageQName = sep == -1 ? new QName(faultMessage) : new QName(faultMessage.substring(0, sep), faultMessage.substring(sep + 1));
                faults.add(new FaultInfo(faultMessageQName, faultPart, faultUse, faultNamespaceURI, this));
            }
        }
    }

    private void fillInDIMEInformation(UnknownExtensibilityElement unkElement, boolean input, Operation operation, BindingEntry bEntry) {
        String layout = unkElement.getElement().getAttribute("layout");
        if (layout.equals("http://schemas.xmlsoap.org/ws/2002/04/dime/closed-layout") || layout.equals("http://schemas.xmlsoap.org/ws/2002/04/dime/open-layout")) {
            // empty if block
        }
        Map parts = null;
        parts = input ? operation.getInput().getMessage().getParts() : operation.getOutput().getMessage().getParts();
        if (parts != null) {
            Iterator iterator = parts.values().iterator();
            while (iterator.hasNext()) {
                org.w3c.dom.Element e;
                Part part = (Part)iterator.next();
                if (part == null) continue;
                String dims = "";
                org.w3c.dom.Element element = null;
                if (part.getTypeName() != null) {
                    TypeEntry partType = this.getType(part.getTypeName());
                    if (partType.getDimensions().length() > 0) {
                        dims = partType.getDimensions();
                        partType = partType.getRefType();
                    }
                    element = (org.w3c.dom.Element)partType.getNode();
                } else if (part.getElementName() != null) {
                    TypeEntry partElement = this.getElement(part.getElementName()).getRefType();
                    element = (org.w3c.dom.Element)partElement.getNode();
                    QName name = SymbolTable.getInnerCollectionComponentQName(element);
                    if (name != null) {
                        dims = dims + "[]";
                        partElement = this.getType(name);
                        element = (org.w3c.dom.Element)partElement.getNode();
                    } else {
                        name = SymbolTable.getInnerTypeQName(element);
                        if (name != null) {
                            partElement = this.getType(name);
                            element = (org.w3c.dom.Element)partElement.getNode();
                        }
                    }
                }
                if (element == null || (e = (org.w3c.dom.Element)XMLUtils.findNode(element, new QName("http://schemas.xmlsoap.org/ws/2002/04/content-type/", "mediaType"))) == null) continue;
                String value = e.getAttribute("value");
                bEntry.setOperationDIME(operation.getName());
                bEntry.setMIMEInfo(operation.getName(), part.getName(), value, dims);
            }
        }
    }

    private void faultsFromSOAPFault(Binding binding, BindingOperation bindOp, Operation operation, ArrayList faults) throws IOException {
        Iterator faultMapIter = bindOp.getBindingFaults().values().iterator();
        while (faultMapIter.hasNext()) {
            BindingFault bFault = (BindingFault)faultMapIter.next();
            String faultName = bFault.getName();
            if (faultName == null || faultName.length() == 0) {
                throw new IOException(Messages.getMessage("unNamedFault00", bindOp.getName(), binding.getQName().toString()));
            }
            boolean foundSOAPFault = false;
            String soapFaultUse = "";
            String soapFaultNamespace = "";
            Iterator faultIter = bFault.getExtensibilityElements().iterator();
            while (faultIter.hasNext()) {
                UnknownExtensibilityElement unkElement;
                QName name;
                Object obj = faultIter.next();
                if (obj instanceof SOAPFault) {
                    foundSOAPFault = true;
                    soapFaultUse = ((SOAPFault)obj).getUse();
                    soapFaultNamespace = ((SOAPFault)obj).getNamespaceURI();
                    break;
                }
                if (!(obj instanceof UnknownExtensibilityElement) || !(name = (unkElement = (UnknownExtensibilityElement)obj).getElementType()).getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/soap12/") || !name.getLocalPart().equals("fault")) continue;
                if (unkElement.getElement().getAttribute("use") != null) {
                    soapFaultUse = unkElement.getElement().getAttribute("use");
                }
                if (unkElement.getElement().getAttribute("namespace") == null) continue;
                soapFaultNamespace = unkElement.getElement().getAttribute("namespace");
            }
            if (!foundSOAPFault) {
                throw new IOException(Messages.getMessage("missingSoapFault00", faultName, bindOp.getName(), binding.getQName().toString()));
            }
            Fault opFault = operation.getFault(bFault.getName());
            if (opFault == null) {
                throw new IOException(Messages.getMessage("noPortTypeFault", new String[]{bFault.getName(), bindOp.getName(), binding.getQName().toString()}));
            }
            faults.add(new FaultInfo(opFault, Use.getUse(soapFaultUse), soapFaultNamespace, this));
        }
    }

    private void setBodyType(String use, BindingEntry bEntry, Operation operation, boolean input) {
        if (use == null) {
            use = "literal";
        }
        if (use.equalsIgnoreCase("literal")) {
            bEntry.setBodyType(operation, Use.LITERAL, input);
        }
    }

    private Use addMIMETypes(BindingEntry bEntry, MIMEMultipartRelated mpr, Operation op) throws IOException {
        Use bodyType = Use.ENCODED;
        List parts = mpr.getMIMEParts();
        Iterator i = parts.iterator();
        while (i.hasNext()) {
            MIMEPart part = (MIMEPart)i.next();
            List elems = part.getExtensibilityElements();
            Iterator j = elems.iterator();
            while (j.hasNext()) {
                UnknownExtensibilityElement unkElement;
                QName name;
                Object obj = j.next();
                if (obj instanceof MIMEContent) {
                    String type;
                    Node node;
                    MIMEContent content = (MIMEContent)obj;
                    TypeEntry typeEntry = this.findPart(op, content.getPart());
                    if (typeEntry == null) {
                        throw new RuntimeException(Messages.getMessage("cannotFindPartForOperation00", content.getPart(), op.getName(), content.getType()));
                    }
                    String dims = typeEntry.getDimensions();
                    if (dims.length() <= 0 && typeEntry.getRefType() != null && SymbolTable.getInnerCollectionComponentQName(node = typeEntry.getRefType().getNode()) != null) {
                        dims = dims + "[]";
                    }
                    if ((type = content.getType()) == null || type.length() == 0) {
                        type = "text/plain";
                    }
                    bEntry.setMIMEInfo(op.getName(), content.getPart(), type, dims);
                    continue;
                }
                if (obj instanceof SOAPBody) {
                    String use = ((SOAPBody)obj).getUse();
                    if (use == null) {
                        throw new IOException(Messages.getMessage("noUse", op.getName()));
                    }
                    if (!use.equalsIgnoreCase("literal")) continue;
                    bodyType = Use.LITERAL;
                    continue;
                }
                if (!(obj instanceof UnknownExtensibilityElement) || !(name = (unkElement = (UnknownExtensibilityElement)obj).getElementType()).getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/soap12/") || !name.getLocalPart().equals("body")) continue;
                String use = unkElement.getElement().getAttribute("use");
                if (use == null) {
                    throw new IOException(Messages.getMessage("noUse", op.getName()));
                }
                if (!use.equalsIgnoreCase("literal")) continue;
                bodyType = Use.LITERAL;
            }
        }
        return bodyType;
    }

    private TypeEntry findPart(Operation operation, String partName) {
        Map parts = operation.getInput().getMessage().getParts();
        Iterator iterator = parts.values().iterator();
        TypeEntry part = this.findPart(iterator, partName);
        if (part == null) {
            parts = operation.getOutput().getMessage().getParts();
            iterator = parts.values().iterator();
            part = this.findPart(iterator, partName);
        }
        return part;
    }

    private TypeEntry findPart(Iterator iterator, String partName) {
        while (iterator.hasNext()) {
            String typeName;
            Part part = (Part)iterator.next();
            if (part == null || !partName.equals(typeName = part.getName())) continue;
            if (part.getTypeName() != null) {
                return this.getType(part.getTypeName());
            }
            if (part.getElementName() == null) continue;
            return this.getElement(part.getElementName());
        }
        return null;
    }

    private void populateServices(Definition def) throws IOException {
        Iterator i = def.getServices().values().iterator();
        while (i.hasNext()) {
            Service service = (Service)i.next();
            if (service.getQName() == null || service.getQName().getLocalPart() == null || service.getQName().getLocalPart().equals("")) {
                throw new IOException(Messages.getMessage("BadServiceName00"));
            }
            ServiceEntry sEntry = new ServiceEntry(service);
            this.symbolTablePut(sEntry);
            this.populatePorts(service.getPorts());
        }
    }

    private void populatePorts(Map ports) throws IOException {
        if (ports == null) {
            return;
        }
        Iterator it = ports.values().iterator();
        while (it.hasNext()) {
            Port port = (Port)it.next();
            String portName = port.getName();
            Binding portBinding = port.getBinding();
            if (portName == null) {
                throw new IOException(Messages.getMessage("missingPortNameException"));
            }
            if (portBinding == null) {
                throw new IOException(Messages.getMessage("missingBindingException"));
            }
            if (this.existsPortWithName(new QName(portName))) {
                throw new IOException(Messages.getMessage("twoPortsWithSameName", portName));
            }
            PortEntry portEntry = new PortEntry(port);
            this.symbolTablePut(portEntry);
        }
    }

    private void setReferences(Definition def, Document doc) {
        Map stuff = def.getServices();
        if (stuff.isEmpty()) {
            stuff = def.getBindings();
            if (stuff.isEmpty()) {
                stuff = def.getPortTypes();
                if (stuff.isEmpty()) {
                    stuff = def.getMessages();
                    if (stuff.isEmpty()) {
                        Iterator i = this.elementTypeEntries.values().iterator();
                        while (i.hasNext()) {
                            this.setTypeReferences((TypeEntry)i.next(), doc, false);
                        }
                        i = this.typeTypeEntries.values().iterator();
                        while (i.hasNext()) {
                            this.setTypeReferences((TypeEntry)i.next(), doc, false);
                        }
                    } else {
                        Iterator i = stuff.values().iterator();
                        while (i.hasNext()) {
                            Message message = (Message)i.next();
                            MessageEntry mEntry = this.getMessageEntry(message.getQName());
                            this.setMessageReferences(mEntry, def, doc, false);
                        }
                    }
                } else {
                    Iterator i = stuff.values().iterator();
                    while (i.hasNext()) {
                        PortType portType = (PortType)i.next();
                        PortTypeEntry ptEntry = this.getPortTypeEntry(portType.getQName());
                        this.setPortTypeReferences(ptEntry, null, def, doc);
                    }
                }
            } else {
                Iterator i = stuff.values().iterator();
                while (i.hasNext()) {
                    Binding binding = (Binding)i.next();
                    BindingEntry bEntry = this.getBindingEntry(binding.getQName());
                    this.setBindingReferences(bEntry, def, doc);
                }
            }
        } else {
            Iterator i = stuff.values().iterator();
            while (i.hasNext()) {
                Service service = (Service)i.next();
                ServiceEntry sEntry = this.getServiceEntry(service.getQName());
                this.setServiceReferences(sEntry, def, doc);
            }
        }
    }

    private void setTypeReferences(TypeEntry entry, Document doc, boolean literal) {
        if (entry.isReferenced() && !literal || entry.isOnlyLiteralReferenced() && literal) {
            return;
        }
        if (this.wrapped) {
            if (!entry.isReferenced() && literal) {
                entry.setOnlyLiteralReference(true);
            } else if (entry.isOnlyLiteralReferenced() && !literal) {
                entry.setOnlyLiteralReference(false);
            }
        }
        Node node = entry.getNode();
        if (this.addImports || node == null || node.getOwnerDocument() == doc) {
            entry.setIsReferenced(true);
            if (entry instanceof DefinedElement) {
                Type anonType;
                QName anonQName;
                TypeEntry referent;
                BooleanHolder forElement = new BooleanHolder();
                QName referentName = Utils.getTypeQName(node, forElement, false);
                if (referentName != null && (referent = this.getTypeEntry(referentName, forElement.value)) != null) {
                    this.setTypeReferences(referent, doc, literal);
                }
                if ((anonQName = SchemaUtils.getElementAnonQName(entry.getNode())) != null && (anonType = this.getType(anonQName)) != null) {
                    this.setTypeReferences(anonType, doc, literal);
                    return;
                }
            }
        }
        HashSet nestedTypes = entry.getNestedTypes(this, true);
        Iterator it = nestedTypes.iterator();
        while (it.hasNext()) {
            TypeEntry nestedType = (TypeEntry)it.next();
            TypeEntry refType = entry.getRefType();
            if (nestedType == null) continue;
            if (refType != null && !refType.equals(nestedType) && nestedType.isOnlyLiteralReferenced()) {
                nestedType.setOnlyLiteralReference(false);
            }
            if (nestedType.isReferenced() || nestedType == entry) continue;
            this.setTypeReferences(nestedType, doc, false);
        }
    }

    private void setMessageReferences(MessageEntry entry, Definition def, Document doc, boolean literal) {
        Message message = entry.getMessage();
        if (this.addImports) {
            entry.setIsReferenced(true);
        } else {
            Map messages = def.getMessages();
            if (messages.containsValue(message)) {
                entry.setIsReferenced(true);
            }
        }
        Iterator parts = message.getParts().values().iterator();
        while (parts.hasNext()) {
            Part part = (Part)parts.next();
            TypeEntry type = this.getType(part.getTypeName());
            if (type != null) {
                this.setTypeReferences(type, doc, literal);
            }
            if ((type = this.getElement(part.getElementName())) == null) continue;
            this.setTypeReferences(type, doc, literal);
            TypeEntry refType = type.getRefType();
            if (refType == null) continue;
            this.setTypeReferences(refType, doc, literal);
        }
    }

    private void setPortTypeReferences(PortTypeEntry entry, BindingEntry bEntry, Definition def, Document doc) {
        PortType portType = entry.getPortType();
        if (this.addImports) {
            entry.setIsReferenced(true);
        } else {
            Map portTypes = def.getPortTypes();
            if (portTypes.containsValue(portType)) {
                entry.setIsReferenced(true);
            }
        }
        Iterator operations = portType.getOperations().iterator();
        while (operations.hasNext()) {
            MessageEntry mEntry;
            Message message;
            Operation operation = (Operation)operations.next();
            Input input = operation.getInput();
            Output output = operation.getOutput();
            boolean literalInput = false;
            boolean literalOutput = false;
            if (bEntry != null) {
                literalInput = bEntry.getInputBodyType(operation) == Use.LITERAL;
                boolean bl = literalOutput = bEntry.getOutputBodyType(operation) == Use.LITERAL;
            }
            if (input != null && (message = input.getMessage()) != null && (mEntry = this.getMessageEntry(message.getQName())) != null) {
                this.setMessageReferences(mEntry, def, doc, literalInput);
            }
            if (output != null && (message = output.getMessage()) != null && (mEntry = this.getMessageEntry(message.getQName())) != null) {
                this.setMessageReferences(mEntry, def, doc, literalOutput);
            }
            Iterator faults = operation.getFaults().values().iterator();
            while (faults.hasNext()) {
                MessageEntry mEntry2;
                Message message2 = ((Fault)faults.next()).getMessage();
                if (message2 == null || (mEntry2 = this.getMessageEntry(message2.getQName())) == null) continue;
                this.setMessageReferences(mEntry2, def, doc, false);
            }
        }
    }

    private void setBindingReferences(BindingEntry entry, Definition def, Document doc) {
        if (entry.getBindingType() == 0) {
            Binding binding = entry.getBinding();
            if (this.addImports) {
                entry.setIsReferenced(true);
            } else {
                Map bindings = def.getBindings();
                if (bindings.containsValue(binding)) {
                    entry.setIsReferenced(true);
                }
            }
            PortType portType = binding.getPortType();
            PortTypeEntry ptEntry = this.getPortTypeEntry(portType.getQName());
            if (ptEntry != null) {
                this.setPortTypeReferences(ptEntry, entry, def, doc);
            }
        }
    }

    private void setServiceReferences(ServiceEntry entry, Definition def, Document doc) {
        Service service = entry.getService();
        if (this.addImports) {
            entry.setIsReferenced(true);
        } else {
            Map services = def.getServices();
            if (services.containsValue(service)) {
                entry.setIsReferenced(true);
            }
        }
        Iterator ports = service.getPorts().values().iterator();
        while (ports.hasNext()) {
            BindingEntry bEntry;
            Port port = (Port)ports.next();
            Binding binding = port.getBinding();
            if (binding == null || (bEntry = this.getBindingEntry(binding.getQName())) == null) continue;
            this.setBindingReferences(bEntry, def, doc);
        }
    }

    private SymTabEntry symbolTablePut(SymTabEntry entry) throws IOException {
        QName name = entry.getQName();
        SymTabEntry e = this.get(name, entry.getClass());
        if (e == null) {
            e = entry;
            if (entry instanceof Type && this.get(name, class$org$apache$axis$wsdl$symbolTable$UndefinedType == null ? (class$org$apache$axis$wsdl$symbolTable$UndefinedType = SymbolTable.class$("org.apache.axis.wsdl.symbolTable.UndefinedType")) : class$org$apache$axis$wsdl$symbolTable$UndefinedType) != null) {
                if (((TypeEntry)this.get(name, class$org$apache$axis$wsdl$symbolTable$UndefinedType == null ? (class$org$apache$axis$wsdl$symbolTable$UndefinedType = SymbolTable.class$("org.apache.axis.wsdl.symbolTable.UndefinedType")) : class$org$apache$axis$wsdl$symbolTable$UndefinedType)).isSimpleType() && !((TypeEntry)entry).isSimpleType()) {
                    throw new IOException(Messages.getMessage("AttrNotSimpleType01", name.toString()));
                }
                Vector v = (Vector)this.symbolTable.get(name);
                for (int i = 0; i < v.size(); ++i) {
                    Object oldEntry = v.elementAt(i);
                    if (!(oldEntry instanceof UndefinedType)) continue;
                    v.setElementAt(entry, i);
                    this.typeTypeEntries.put(name, entry);
                    ((UndefinedType)oldEntry).update((Type)entry);
                }
            } else if (entry instanceof Element && this.get(name, class$org$apache$axis$wsdl$symbolTable$UndefinedElement == null ? (class$org$apache$axis$wsdl$symbolTable$UndefinedElement = SymbolTable.class$("org.apache.axis.wsdl.symbolTable.UndefinedElement")) : class$org$apache$axis$wsdl$symbolTable$UndefinedElement) != null) {
                Vector v = (Vector)this.symbolTable.get(name);
                for (int i = 0; i < v.size(); ++i) {
                    Object oldEntry = v.elementAt(i);
                    if (!(oldEntry instanceof UndefinedElement)) continue;
                    v.setElementAt(entry, i);
                    this.elementTypeEntries.put(name, entry);
                    ((Undefined)oldEntry).update((Element)entry);
                }
            } else {
                Vector<SymTabEntry> v = (Vector<SymTabEntry>)this.symbolTable.get(name);
                if (v == null) {
                    v = new Vector<SymTabEntry>();
                    this.symbolTable.put(name, v);
                }
                v.add(entry);
                if (entry instanceof Element) {
                    this.elementTypeEntries.put(name, entry);
                } else if (entry instanceof Type) {
                    this.typeTypeEntries.put(name, entry);
                }
            }
        } else if (!this.quiet) {
            System.out.println(Messages.getMessage("alreadyExists00", "" + name));
        }
        return e;
    }

    protected boolean existsPortWithName(QName name) {
        Vector v = (Vector)this.symbolTable.get(name);
        if (v == null) {
            return false;
        }
        Iterator it = v.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (!(o instanceof PortEntry)) continue;
            return true;
        }
        return false;
    }

    private static QName getInnerCollectionComponentQName(Node node) {
        if (node == null) {
            return null;
        }
        QName name = SchemaUtils.getCollectionComponentQName(node, new QNameHolder());
        if (name != null) {
            return name;
        }
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            name = SymbolTable.getInnerCollectionComponentQName(children.item(i));
            if (name == null) continue;
            return name;
        }
        return null;
    }

    private static QName getInnerTypeQName(Node node) {
        if (node == null) {
            return null;
        }
        BooleanHolder forElement = new BooleanHolder();
        QName name = Utils.getTypeQName(node, forElement, true);
        if (name != null) {
            return name;
        }
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            name = SymbolTable.getInnerTypeQName(children.item(i));
            if (name == null) continue;
            return name;
        }
        return null;
    }

    protected void processTypes() {
        Iterator i = this.typeTypeEntries.values().iterator();
        while (i.hasNext()) {
            Vector elements;
            Type type = (Type)i.next();
            Node node = type.getNode();
            Vector attributes = SchemaUtils.getContainedAttributeTypes(node, this);
            if (attributes != null) {
                type.setContainedAttributes(attributes);
            }
            if ((elements = SchemaUtils.getContainedElementDeclarations(node, this)) == null) continue;
            type.setContainedElements(elements);
        }
    }

    public List getMessageEntries() {
        ArrayList<SymTabEntry> messageEntries = new ArrayList<SymTabEntry>();
        Iterator iter = this.symbolTable.values().iterator();
        while (iter.hasNext()) {
            Vector v = (Vector)iter.next();
            for (int i = 0; i < v.size(); ++i) {
                SymTabEntry entry = (SymTabEntry)v.elementAt(i);
                if (!(entry instanceof MessageEntry)) continue;
                messageEntries.add(entry);
            }
        }
        return messageEntries;
    }

    public void setWrapArrays(boolean wrapArrays) {
        this.wrapArrays = wrapArrays;
    }

    public Map getElementFormDefaults() {
        return this.elementFormDefaults;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

