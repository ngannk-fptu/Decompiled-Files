/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.Definition
 *  javax.wsdl.WSDLException
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.wsdl.fromJava;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.axis.AxisFault;
import org.apache.axis.AxisProperties;
import org.apache.axis.Constants;
import org.apache.axis.InternalException;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.constants.Style;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.SerializerFactory;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.axis.encoding.ser.EnumSerializerFactory;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.StringUtils;
import org.apache.axis.utils.XMLUtils;
import org.apache.axis.wsdl.fromJava.Emitter;
import org.apache.axis.wsdl.fromJava.Namespaces;
import org.apache.axis.wsdl.symbolTable.BaseTypeMapping;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.TypeEntry;
import org.apache.commons.logging.Log;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Types {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$wsdl$fromJava$Types == null ? (class$org$apache$axis$wsdl$fromJava$Types = Types.class$("org.apache.axis.wsdl.fromJava.Types")) : class$org$apache$axis$wsdl$fromJava$Types).getName());
    Definition def;
    Namespaces namespaces = null;
    TypeMapping tm;
    TypeMapping defaultTM;
    String targetNamespace;
    Element wsdlTypesElem = null;
    HashMap schemaTypes = null;
    HashMap schemaElementNames = null;
    HashMap schemaUniqueElementNames = null;
    HashMap wrapperMap = new HashMap();
    List stopClasses = null;
    List beanCompatErrs = new ArrayList();
    private ServiceDesc serviceDesc = null;
    private Set writtenElementQNames = new HashSet();
    Class[] mappedTypes = null;
    Emitter emitter = null;
    Document docHolder;
    static /* synthetic */ Class class$org$apache$axis$wsdl$fromJava$Types;
    static /* synthetic */ Class class$java$util$Collection;
    static /* synthetic */ Class class$java$lang$Object;
    static /* synthetic */ Class class$javax$xml$rpc$holders$Holder;
    static /* synthetic */ Class array$B;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$org$apache$axis$encoding$SimpleType;
    static /* synthetic */ Class class$java$lang$Throwable;
    static /* synthetic */ Class class$java$lang$Exception;
    static /* synthetic */ Class class$java$rmi$RemoteException;
    static /* synthetic */ Class class$org$apache$axis$AxisFault;

    public static boolean isArray(Class clazz) {
        return clazz.isArray() || (class$java$util$Collection == null ? (class$java$util$Collection = Types.class$("java.util.Collection")) : class$java$util$Collection).isAssignableFrom(clazz);
    }

    private static Class getComponentType(Class clazz) {
        if (clazz.isArray()) {
            return clazz.getComponentType();
        }
        if ((class$java$util$Collection == null ? (class$java$util$Collection = Types.class$("java.util.Collection")) : class$java$util$Collection).isAssignableFrom(clazz)) {
            return class$java$lang$Object == null ? (class$java$lang$Object = Types.class$("java.lang.Object")) : class$java$lang$Object;
        }
        return null;
    }

    public Types(Definition def, TypeMapping tm, TypeMapping defaultTM, Namespaces namespaces, String targetNamespace, List stopClasses, ServiceDesc serviceDesc) {
        this.def = def;
        this.serviceDesc = serviceDesc;
        this.createDocumentFragment();
        this.tm = tm;
        this.defaultTM = defaultTM;
        this.mappedTypes = tm.getAllClasses();
        this.namespaces = namespaces;
        this.targetNamespace = targetNamespace;
        this.stopClasses = stopClasses;
        this.schemaElementNames = new HashMap();
        this.schemaUniqueElementNames = new HashMap();
        this.schemaTypes = new HashMap();
    }

    public Types(Definition def, TypeMapping tm, TypeMapping defaultTM, Namespaces namespaces, String targetNamespace, List stopClasses, ServiceDesc serviceDesc, Emitter emitter) {
        this(def, tm, defaultTM, namespaces, targetNamespace, stopClasses, serviceDesc);
        this.emitter = emitter;
    }

    public Namespaces getNamespaces() {
        return this.namespaces;
    }

    public void loadInputSchema(String inputSchema) throws IOException, WSDLException, SAXException, ParserConfigurationException {
        Document doc = XMLUtils.newDocument(inputSchema);
        Element root = doc.getDocumentElement();
        if (root.getLocalName().equals("schema") && Constants.isSchemaXSD(root.getNamespaceURI())) {
            Node schema = this.docHolder.importNode(root, true);
            if (null == this.wsdlTypesElem) {
                this.writeWsdlTypesElement();
            }
            this.wsdlTypesElem.appendChild(schema);
            BaseTypeMapping btm = new BaseTypeMapping(){

                public String getBaseName(QName qNameIn) {
                    QName qName = new QName(qNameIn.getNamespaceURI(), qNameIn.getLocalPart());
                    Class cls = Types.this.defaultTM.getClassForQName(qName);
                    if (cls == null) {
                        return null;
                    }
                    return JavaUtils.getTextClassName(cls.getName());
                }
            };
            SymbolTable symbolTable = new SymbolTable(btm, true, false, false);
            symbolTable.populateTypes(new URL(inputSchema), doc);
            this.processSymTabEntries(symbolTable);
        }
    }

    private void processSymTabEntries(SymbolTable symbolTable) {
        String prefix;
        TypeEntry te;
        QName name;
        Map.Entry me;
        Iterator iterator = symbolTable.getElementIndex().entrySet().iterator();
        while (iterator.hasNext()) {
            me = iterator.next();
            name = (QName)me.getKey();
            te = (TypeEntry)me.getValue();
            prefix = XMLUtils.getPrefix(name.getNamespaceURI(), te.getNode());
            if (null != prefix && !"".equals(prefix)) {
                this.namespaces.putPrefix(name.getNamespaceURI(), prefix);
                this.def.addNamespace(prefix, name.getNamespaceURI());
            }
            this.addToElementsList(name);
        }
        iterator = symbolTable.getTypeIndex().entrySet().iterator();
        while (iterator.hasNext()) {
            me = iterator.next();
            name = (QName)me.getKey();
            te = (TypeEntry)me.getValue();
            prefix = XMLUtils.getPrefix(name.getNamespaceURI(), te.getNode());
            if (null != prefix && !"".equals(prefix)) {
                this.namespaces.putPrefix(name.getNamespaceURI(), prefix);
                this.def.addNamespace(prefix, name.getNamespaceURI());
            }
            this.addToTypesList(name);
        }
    }

    public void loadInputTypes(String inputWSDL) throws IOException, WSDLException, SAXException, ParserConfigurationException {
        Document doc = XMLUtils.newDocument(inputWSDL);
        NodeList elements = doc.getChildNodes();
        if (elements.getLength() > 0 && elements.item(0).getLocalName().equals("definitions")) {
            elements = elements.item(0).getChildNodes();
            for (int i = 0; i < elements.getLength() && this.wsdlTypesElem == null; ++i) {
                Node node = elements.item(i);
                if (node.getLocalName() == null || !node.getLocalName().equals("types")) continue;
                this.wsdlTypesElem = (Element)node;
            }
        }
        if (this.wsdlTypesElem == null) {
            return;
        }
        this.wsdlTypesElem = (Element)this.docHolder.importNode(this.wsdlTypesElem, true);
        this.docHolder.appendChild(this.wsdlTypesElem);
        BaseTypeMapping btm = new BaseTypeMapping(){

            public String getBaseName(QName qNameIn) {
                QName qName = new QName(qNameIn.getNamespaceURI(), qNameIn.getLocalPart());
                Class cls = Types.this.tm.getClassForQName(qName);
                if (cls == null) {
                    return null;
                }
                return JavaUtils.getTextClassName(cls.getName());
            }
        };
        SymbolTable symbolTable = new SymbolTable(btm, true, false, false);
        symbolTable.populate(null, doc);
        this.processSymTabEntries(symbolTable);
    }

    public QName writeTypeForPart(Class type, QName qname) throws AxisFault {
        if (type.getName().equals("void")) {
            return null;
        }
        if ((class$javax$xml$rpc$holders$Holder == null ? (class$javax$xml$rpc$holders$Holder = Types.class$("javax.xml.rpc.holders.Holder")) : class$javax$xml$rpc$holders$Holder).isAssignableFrom(type)) {
            type = JavaUtils.getHolderValueType(type);
        }
        if ((qname == null || Constants.isSOAP_ENC(qname.getNamespaceURI()) && "Array".equals(qname.getLocalPart())) && (qname = this.getTypeQName(type)) == null) {
            throw new AxisFault("Class:" + type.getName());
        }
        if (!this.makeTypeElement(type, qname, null)) {
            qname = Constants.XSD_ANYTYPE;
        }
        return qname;
    }

    public QName writeTypeAndSubTypeForPart(Class type, QName qname) throws AxisFault {
        QName qNameRet = this.writeTypeForPart(type, qname);
        if (this.mappedTypes != null) {
            for (int i = 0; i < this.mappedTypes.length; ++i) {
                Class tempMappedType = this.mappedTypes[i];
                if (tempMappedType == null || type == (class$java$lang$Object == null ? Types.class$("java.lang.Object") : class$java$lang$Object) || tempMappedType == type || !type.isAssignableFrom(tempMappedType)) continue;
                QName name = this.tm.getTypeQName(tempMappedType);
                if (!this.isAnonymousType(name)) {
                    this.writeTypeForPart(tempMappedType, name);
                }
                this.mappedTypes[i] = null;
            }
        }
        return qNameRet;
    }

    public QName writeElementForPart(Class type, QName qname) throws AxisFault {
        if (type.getName().equals("void")) {
            return null;
        }
        if ((class$javax$xml$rpc$holders$Holder == null ? (class$javax$xml$rpc$holders$Holder = Types.class$("javax.xml.rpc.holders.Holder")) : class$javax$xml$rpc$holders$Holder).isAssignableFrom(type)) {
            type = JavaUtils.getHolderValueType(type);
        }
        if ((qname == null || Constants.isSOAP_ENC(qname.getNamespaceURI()) && "Array".equals(qname.getLocalPart())) && (qname = this.getTypeQName(type)) == null) {
            throw new AxisFault("Class:" + type.getName());
        }
        String nsURI = qname.getNamespaceURI();
        if (Constants.isSchemaXSD(nsURI) || Constants.isSOAP_ENC(nsURI) && !"Array".equals(qname.getLocalPart())) {
            return null;
        }
        if (this.wsdlTypesElem == null) {
            this.writeWsdlTypesElement();
        }
        if (this.writeTypeAsElement(type, qname) == null) {
            qname = null;
        }
        return qname;
    }

    public Element writeWrapperElement(QName qname, boolean request, boolean hasParams) throws AxisFault {
        if (this.wsdlTypesElem == null) {
            this.writeWsdlTypesElement();
        }
        this.writeTypeNamespace(qname.getNamespaceURI());
        Element wrapperElement = this.docHolder.createElement("element");
        this.writeSchemaElementDecl(qname, wrapperElement);
        wrapperElement.setAttribute("name", qname.getLocalPart());
        Element complexType = this.docHolder.createElement("complexType");
        wrapperElement.appendChild(complexType);
        if (hasParams) {
            Element sequence = this.docHolder.createElement("sequence");
            complexType.appendChild(sequence);
            return sequence;
        }
        return null;
    }

    public void writeWrappedParameter(Element sequence, String name, QName type, Class javaType) throws AxisFault {
        Element childElem;
        if (javaType == Void.TYPE) {
            return;
        }
        type = javaType.isArray() && !javaType.equals(array$B == null ? (array$B = Types.class$("[B")) : array$B) ? this.writeTypeForPart(javaType.getComponentType(), null) : this.writeTypeForPart(javaType, type);
        if (type == null) {
            return;
        }
        if (this.isAnonymousType(type)) {
            childElem = this.createElementWithAnonymousType(name, javaType, false, this.docHolder);
        } else {
            childElem = this.docHolder.createElement("element");
            childElem.setAttribute("name", name);
            String prefix = this.namespaces.getCreatePrefix(type.getNamespaceURI());
            String prefixedName = prefix + ":" + type.getLocalPart();
            childElem.setAttribute("type", prefixedName);
            if (javaType.isArray() && !javaType.equals(array$B == null ? (array$B = Types.class$("[B")) : array$B)) {
                childElem.setAttribute("maxOccurs", "unbounded");
            }
        }
        sequence.appendChild(childElem);
    }

    private boolean isAnonymousType(QName type) {
        return type.getLocalPart().indexOf(">") != -1;
    }

    private QName writeTypeAsElement(Class type, QName qName) throws AxisFault {
        if (qName == null || Constants.equals(Constants.SOAP_ARRAY, qName)) {
            qName = this.getTypeQName(type);
        }
        this.writeTypeNamespace(type, qName);
        String elementType = this.writeType(type, qName);
        if (elementType != null) {
            return qName;
        }
        return null;
    }

    private QName writeTypeNamespace(Class type, QName qName) {
        if (qName == null) {
            qName = this.getTypeQName(type);
        }
        this.writeTypeNamespace(qName.getNamespaceURI());
        return qName;
    }

    private void writeTypeNamespace(String namespaceURI) {
        String pref;
        if (namespaceURI != null && !namespaceURI.equals("") && (pref = this.def.getPrefix(namespaceURI)) == null) {
            this.def.addNamespace(this.namespaces.getCreatePrefix(namespaceURI), namespaceURI);
        }
    }

    public QName getTypeQName(Class javaType) {
        QName qName = null;
        qName = this.tm.getTypeQName(javaType);
        if (Types.isArray(javaType) && Constants.equals(Constants.SOAP_ARRAY, qName)) {
            QName cqName;
            Class componentType = Types.getComponentType(javaType);
            String arrayTypePrefix = "ArrayOf";
            boolean isWSICompliant = JavaUtils.isTrue(AxisProperties.getProperty("axis.ws-i.bp11.compatibility"));
            if (isWSICompliant) {
                arrayTypePrefix = "MyArrayOf";
            }
            if (this.targetNamespace.equals((cqName = this.getTypeQName(componentType)).getNamespaceURI())) {
                qName = new QName(this.targetNamespace, arrayTypePrefix + cqName.getLocalPart());
            } else {
                String pre = this.namespaces.getCreatePrefix(cqName.getNamespaceURI());
                qName = new QName(this.targetNamespace, arrayTypePrefix + "_" + pre + "_" + cqName.getLocalPart());
            }
            return qName;
        }
        if (qName == null) {
            String pkg = Types.getPackageNameFromFullName(javaType.getName());
            String lcl = Types.getLocalNameFromFullName(javaType.getName());
            String ns = this.namespaces.getCreate(pkg);
            this.namespaces.getCreatePrefix(ns);
            String localPart = lcl.replace('$', '_');
            qName = new QName(ns, localPart);
        }
        return qName;
    }

    public String getQNameString(QName qname) {
        String prefix = this.namespaces.getCreatePrefix(qname.getNamespaceURI());
        return prefix + ":" + qname.getLocalPart();
    }

    public static String getPackageNameFromFullName(String full) {
        if (full.lastIndexOf(46) < 0) {
            return "";
        }
        return full.substring(0, full.lastIndexOf(46));
    }

    public static String getLocalNameFromFullName(String full) {
        String end = "";
        if (full.startsWith("[L")) {
            end = "[]";
            full = full.substring(3, full.length() - 1);
        }
        if (full.lastIndexOf(46) < 0) {
            return full + end;
        }
        return full.substring(full.lastIndexOf(46) + 1) + end;
    }

    public void writeSchemaTypeDecl(QName qname, Element element) throws AxisFault {
        this.writeSchemaElement(qname.getNamespaceURI(), element);
    }

    public void writeSchemaElementDecl(QName qname, Element element) throws AxisFault {
        if (this.writtenElementQNames.contains(qname)) {
            throw new AxisFault("Server.generalException", Messages.getMessage("duplicateSchemaElement", qname.toString()), null, null);
        }
        this.writeSchemaElement(qname.getNamespaceURI(), element);
        this.writtenElementQNames.add(qname);
    }

    public void writeSchemaElement(QName qName, Element element) throws AxisFault {
        this.writeSchemaElement(qName.getNamespaceURI(), element);
    }

    public void writeSchemaElement(String namespaceURI, Element element) throws AxisFault {
        if (this.wsdlTypesElem == null) {
            try {
                this.writeWsdlTypesElement();
            }
            catch (Exception e) {
                log.error((Object)e);
                return;
            }
        }
        if (namespaceURI == null || namespaceURI.equals("")) {
            throw new AxisFault("Server.generalException", Messages.getMessage("noNamespace00", namespaceURI), null, null);
        }
        Node schemaElem = null;
        NodeList nl = this.wsdlTypesElem.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            NamedNodeMap attrs = nl.item(i).getAttributes();
            if (attrs == null) continue;
            for (int n = 0; n < attrs.getLength(); ++n) {
                Attr a = (Attr)attrs.item(n);
                if (!a.getName().equals("targetNamespace") || !a.getValue().equals(namespaceURI)) continue;
                schemaElem = (Element)nl.item(i);
            }
        }
        if (schemaElem == null) {
            schemaElem = this.docHolder.createElement("schema");
            this.wsdlTypesElem.appendChild(schemaElem);
            schemaElem.setAttribute("xmlns", "http://www.w3.org/2001/XMLSchema");
            schemaElem.setAttribute("targetNamespace", namespaceURI);
            if (this.serviceDesc.getStyle() == Style.RPC) {
                Element importElem = this.docHolder.createElement("import");
                schemaElem.appendChild(importElem);
                importElem.setAttribute("namespace", Constants.URI_DEFAULT_SOAP_ENC);
            }
            SOAPService service = null;
            if (MessageContext.getCurrentContext() != null) {
                service = MessageContext.getCurrentContext().getService();
            }
            if (service != null && this.isPresent((String)service.getOption("schemaQualified"), namespaceURI)) {
                schemaElem.setAttribute("elementFormDefault", "qualified");
            } else if (!(service != null && this.isPresent((String)service.getOption("schemaUnqualified"), namespaceURI) || this.serviceDesc.getStyle() != Style.DOCUMENT && this.serviceDesc.getStyle() != Style.WRAPPED)) {
                schemaElem.setAttribute("elementFormDefault", "qualified");
            }
            this.writeTypeNamespace(namespaceURI);
        }
        schemaElem.appendChild(element);
    }

    private boolean isPresent(String list, String namespace) {
        if (list == null || list.length() == 0) {
            return false;
        }
        String[] array = StringUtils.split(list, ',');
        for (int i = 0; i < array.length; ++i) {
            if (!array[i].equals(namespace)) continue;
            return true;
        }
        return false;
    }

    private void writeWsdlTypesElement() {
        if (this.wsdlTypesElem == null) {
            this.wsdlTypesElem = this.docHolder.createElementNS("http://schemas.xmlsoap.org/wsdl/", "types");
            this.wsdlTypesElem.setPrefix("wsdl");
        }
    }

    public String writeType(Class type) throws AxisFault {
        return this.writeType(type, null);
    }

    public String writeType(Class type, QName qName) throws AxisFault {
        if (qName == null || Constants.equals(Constants.SOAP_ARRAY, qName)) {
            qName = this.getTypeQName(type);
        }
        if (!this.makeTypeElement(type, qName, null)) {
            return null;
        }
        return this.getQNameString(qName);
    }

    public Element createArrayElement(String componentTypeName) {
        MessageContext mc = MessageContext.getCurrentContext();
        SOAPConstants constants = mc == null || mc.getSOAPConstants() == null ? SOAPConstants.SOAP11_CONSTANTS : mc.getSOAPConstants();
        String prefix = this.namespaces.getCreatePrefix(constants.getEncodingURI());
        Element complexType = this.docHolder.createElement("complexType");
        Element complexContent = this.docHolder.createElement("complexContent");
        complexType.appendChild(complexContent);
        Element restriction = this.docHolder.createElement("restriction");
        complexContent.appendChild(restriction);
        restriction.setAttribute("base", prefix + ":Array");
        Element attribute = this.docHolder.createElement("attribute");
        restriction.appendChild(attribute);
        attribute.setAttribute("ref", prefix + ":arrayType");
        prefix = this.namespaces.getCreatePrefix("http://schemas.xmlsoap.org/wsdl/");
        attribute.setAttribute(prefix + ":arrayType", componentTypeName);
        return complexType;
    }

    public Element createLiteralArrayElement(String componentType, QName itemName) {
        String itemLocalName = "item";
        if (itemName != null) {
            itemLocalName = itemName.getLocalPart();
        }
        Element complexType = this.docHolder.createElement("complexType");
        Element sequence = this.docHolder.createElement("sequence");
        complexType.appendChild(sequence);
        Element elem = this.docHolder.createElement("element");
        elem.setAttribute("name", itemLocalName);
        elem.setAttribute("type", componentType);
        elem.setAttribute("minOccurs", "0");
        elem.setAttribute("maxOccurs", "unbounded");
        sequence.appendChild(elem);
        return complexType;
    }

    public static boolean isEnumClass(Class cls) {
        block5: {
            Method m = cls.getMethod("getValue", null);
            Method m2 = cls.getMethod("toString", null);
            if (m == null || m2 == null) break block5;
            Method m3 = cls.getDeclaredMethod("fromString", class$java$lang$String == null ? (class$java$lang$String = Types.class$("java.lang.String")) : class$java$lang$String);
            Method m4 = cls.getDeclaredMethod("fromValue", m.getReturnType());
            if (m3 == null || !Modifier.isStatic(m3.getModifiers()) || !Modifier.isPublic(m3.getModifiers()) || m4 == null || !Modifier.isStatic(m4.getModifiers()) || !Modifier.isPublic(m4.getModifiers())) break block5;
            try {
                return cls.getMethod("setValue", m.getReturnType()) == null;
            }
            catch (NoSuchMethodException e) {
                try {
                    return true;
                }
                catch (NoSuchMethodException noSuchMethodException) {
                    // empty catch block
                }
            }
        }
        return false;
    }

    public Element writeEnumType(QName qName, Class cls) throws NoSuchMethodException, IllegalAccessException, AxisFault {
        if (!Types.isEnumClass(cls)) {
            return null;
        }
        Method m = cls.getMethod("getValue", null);
        Class<?> base = m.getReturnType();
        Element simpleType = this.docHolder.createElement("simpleType");
        simpleType.setAttribute("name", qName.getLocalPart());
        Element restriction = this.docHolder.createElement("restriction");
        simpleType.appendChild(restriction);
        String baseType = this.writeType(base, null);
        restriction.setAttribute("base", baseType);
        Field[] fields = cls.getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            Field field = fields[i];
            int mod = field.getModifiers();
            if (!Modifier.isPublic(mod) || !Modifier.isStatic(mod) || !Modifier.isFinal(mod) || field.getType() != base) continue;
            Element enumeration = this.docHolder.createElement("enumeration");
            enumeration.setAttribute("value", field.get(null).toString());
            restriction.appendChild(enumeration);
        }
        return simpleType;
    }

    public void writeElementDecl(QName qname, Class javaType, QName typeQName, boolean nillable, QName itemQName) throws AxisFault {
        if (this.writtenElementQNames.contains(qname)) {
            return;
        }
        String name = qname.getLocalPart();
        Element element = this.docHolder.createElement("element");
        element.setAttribute("name", name);
        if (nillable) {
            element.setAttribute("nillable", "true");
        }
        if (javaType.isArray()) {
            String componentType = this.writeType(javaType.getComponentType());
            Element complexType = this.createLiteralArrayElement(componentType, itemQName);
            element.appendChild(complexType);
        } else {
            this.makeTypeElement(javaType, typeQName, element);
        }
        this.writeSchemaElementDecl(qname, element);
    }

    public Element createElement(String elementName, String elementType, boolean nullable, boolean omittable, Document docHolder) {
        Element element = docHolder.createElement("element");
        element.setAttribute("name", elementName);
        if (nullable) {
            element.setAttribute("nillable", "true");
        }
        if (omittable) {
            element.setAttribute("minOccurs", "0");
            element.setAttribute("maxOccurs", "1");
        }
        if (elementType != null) {
            element.setAttribute("type", elementType);
        }
        return element;
    }

    public Element createAttributeElement(String elementName, Class javaType, QName xmlType, boolean nullable, Document docHolder) throws AxisFault {
        Element element = docHolder.createElement("attribute");
        element.setAttribute("name", elementName);
        if (nullable) {
            element.setAttribute("nillable", "true");
        }
        this.makeTypeElement(javaType, xmlType, element);
        return element;
    }

    boolean isSimpleType(Class type) {
        QName qname = this.tm.getTypeQName(type);
        if (qname == null) {
            return false;
        }
        String nsURI = qname.getNamespaceURI();
        return Constants.isSchemaXSD(nsURI) || Constants.isSOAP_ENC(nsURI);
    }

    public boolean isAcceptableAsAttribute(Class type) {
        return this.isSimpleType(type) || Types.isEnumClass(type) || this.implementsSimpleType(type);
    }

    boolean implementsSimpleType(Class type) {
        Class<?>[] impls = type.getInterfaces();
        for (int i = 0; i < impls.length; ++i) {
            if (impls[i] != (class$org$apache$axis$encoding$SimpleType == null ? Types.class$("org.apache.axis.encoding.SimpleType") : class$org$apache$axis$encoding$SimpleType)) continue;
            return true;
        }
        return false;
    }

    private boolean addToTypesList(QName qName) {
        boolean added = false;
        String namespaceURI = qName.getNamespaceURI();
        ArrayList<String> types = (ArrayList<String>)this.schemaTypes.get(namespaceURI);
        if (Constants.isSchemaXSD(namespaceURI) || Constants.isSOAP_ENC(namespaceURI) && !"Array".equals(qName.getLocalPart())) {
            this.writeTypeNamespace(namespaceURI);
            return false;
        }
        if (types == null) {
            types = new ArrayList<String>();
            types.add(qName.getLocalPart());
            this.writeTypeNamespace(namespaceURI);
            this.schemaTypes.put(namespaceURI, types);
            added = true;
        } else if (!types.contains(qName.getLocalPart())) {
            types.add(qName.getLocalPart());
            added = true;
        }
        if (added) {
            String prefix = this.namespaces.getCreatePrefix(namespaceURI);
            return !prefix.equals("soapenv") && !prefix.equals("soapenc") && !prefix.equals("xsd") && !prefix.equals("wsdl") && !prefix.equals("wsdlsoap");
        }
        return false;
    }

    private boolean addToElementsList(QName qName) {
        if (qName == null) {
            return false;
        }
        boolean added = false;
        ArrayList<String> elements = (ArrayList<String>)this.schemaElementNames.get(qName.getNamespaceURI());
        if (elements == null) {
            elements = new ArrayList<String>();
            elements.add(qName.getLocalPart());
            this.schemaElementNames.put(qName.getNamespaceURI(), elements);
            added = true;
        } else if (!elements.contains(qName.getLocalPart())) {
            elements.add(qName.getLocalPart());
            added = true;
        }
        return added;
    }

    public static boolean isNullable(Class type) {
        return !type.isPrimitive();
    }

    private void createDocumentFragment() {
        try {
            this.docHolder = XMLUtils.newDocument();
        }
        catch (ParserConfigurationException e) {
            throw new InternalException(e);
        }
    }

    public void updateNamespaces() {
        Namespaces namespaces = this.getNamespaces();
        Iterator nspIterator = namespaces.getNamespaces();
        while (nspIterator.hasNext()) {
            String nsp = (String)nspIterator.next();
            String pref = this.def.getPrefix(nsp);
            if (pref != null) continue;
            this.def.addNamespace(namespaces.getCreatePrefix(nsp), nsp);
        }
    }

    public void insertTypesFragment(Document doc) {
        this.updateNamespaces();
        if (this.wsdlTypesElem == null) {
            return;
        }
        Element schemaElem = null;
        String tns = null;
        NodeList nl = this.wsdlTypesElem.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            NamedNodeMap attrs = nl.item(i).getAttributes();
            if (attrs == null) continue;
            for (int n = 0; n < attrs.getLength(); ++n) {
                Attr a = (Attr)attrs.item(n);
                if (!a.getName().equals("targetNamespace")) continue;
                tns = a.getValue();
                schemaElem = (Element)nl.item(i);
                break;
            }
            if (tns != null && !"".equals(tns.trim())) {
                Iterator it = this.schemaTypes.keySet().iterator();
                while (it.hasNext()) {
                    String otherTns = (String)it.next();
                    if (tns.equals(otherTns)) continue;
                    Element importElem = this.docHolder.createElement("import");
                    importElem.setAttribute("namespace", otherTns);
                    schemaElem.insertBefore(importElem, schemaElem.getFirstChild());
                }
            }
            schemaElem = null;
            tns = null;
        }
        Node node = doc.importNode(this.wsdlTypesElem, true);
        doc.getDocumentElement().insertBefore(node, doc.getDocumentElement().getFirstChild());
    }

    public List getStopClasses() {
        return this.stopClasses;
    }

    public Element createElement(String elementName) {
        return this.docHolder.createElement(elementName);
    }

    protected boolean isBeanCompatible(Class javaType, boolean issueErrors) {
        Class superClass;
        if (javaType.isArray() || javaType.isPrimitive()) {
            if (issueErrors && !this.beanCompatErrs.contains(javaType)) {
                log.warn((Object)Messages.getMessage("beanCompatType00", javaType.getName()));
                this.beanCompatErrs.add(javaType);
            }
            return false;
        }
        if (javaType.getName().startsWith("java.") || javaType.getName().startsWith("javax.")) {
            if (issueErrors && !this.beanCompatErrs.contains(javaType)) {
                log.warn((Object)Messages.getMessage("beanCompatPkg00", javaType.getName()));
                this.beanCompatErrs.add(javaType);
            }
            return false;
        }
        if (JavaUtils.isEnumClass(javaType)) {
            return true;
        }
        if (!(class$java$lang$Throwable == null ? (class$java$lang$Throwable = Types.class$("java.lang.Throwable")) : class$java$lang$Throwable).isAssignableFrom(javaType)) {
            try {
                javaType.getConstructor(new Class[0]);
            }
            catch (NoSuchMethodException e) {
                if (issueErrors && !this.beanCompatErrs.contains(javaType)) {
                    log.warn((Object)Messages.getMessage("beanCompatConstructor00", javaType.getName()));
                    this.beanCompatErrs.add(javaType);
                }
                return false;
            }
        }
        if (!((superClass = javaType.getSuperclass()) == null || superClass == (class$java$lang$Object == null ? (class$java$lang$Object = Types.class$("java.lang.Object")) : class$java$lang$Object) || superClass == (class$java$lang$Exception == null ? (class$java$lang$Exception = Types.class$("java.lang.Exception")) : class$java$lang$Exception) || superClass == (class$java$lang$Throwable == null ? (class$java$lang$Throwable = Types.class$("java.lang.Throwable")) : class$java$lang$Throwable) || superClass == (class$java$rmi$RemoteException == null ? (class$java$rmi$RemoteException = Types.class$("java.rmi.RemoteException")) : class$java$rmi$RemoteException) || superClass == (class$org$apache$axis$AxisFault == null ? (class$org$apache$axis$AxisFault = Types.class$("org.apache.axis.AxisFault")) : class$org$apache$axis$AxisFault) || this.stopClasses != null && this.stopClasses.contains(superClass.getName()) || this.isBeanCompatible(superClass, false))) {
            if (issueErrors && !this.beanCompatErrs.contains(javaType)) {
                log.warn((Object)Messages.getMessage("beanCompatExtends00", javaType.getName(), superClass.getName(), javaType.getName()));
                this.beanCompatErrs.add(javaType);
            }
            return false;
        }
        return true;
    }

    public Element createElementWithAnonymousType(String elementName, Class fieldType, boolean omittable, Document ownerDocument) throws AxisFault {
        Element element = this.docHolder.createElement("element");
        element.setAttribute("name", elementName);
        if (Types.isNullable(fieldType)) {
            element.setAttribute("nillable", "true");
        }
        if (omittable) {
            element.setAttribute("minOccurs", "0");
            element.setAttribute("maxOccurs", "1");
        }
        this.makeTypeElement(fieldType, null, element);
        return element;
    }

    private boolean makeTypeElement(Class type, QName qName, Element containingElement) throws AxisFault {
        Element typeEl;
        Serializer ser;
        boolean anonymous;
        if (qName == null || Constants.equals(Constants.SOAP_ARRAY, qName)) {
            qName = this.getTypeQName(type);
        }
        if ((anonymous = this.isAnonymousType(qName)) && containingElement == null) {
            throw new AxisFault(Messages.getMessage("noContainerForAnonymousType", qName.toString()));
        }
        if (!this.addToTypesList(qName) && !anonymous) {
            if (containingElement != null) {
                containingElement.setAttribute("type", this.getQNameString(qName));
            }
            return true;
        }
        SerializerFactory factory = (SerializerFactory)this.tm.getSerializer(type, qName);
        if (factory == null) {
            if (Types.isEnumClass(type)) {
                factory = new EnumSerializerFactory(type, qName);
            } else if (this.isBeanCompatible(type, true)) {
                factory = new BeanSerializerFactory(type, qName);
            } else {
                return false;
            }
        }
        if ((ser = (Serializer)factory.getSerializerAs("Axis SAX Mechanism")) == null) {
            throw new AxisFault(Messages.getMessage("NoSerializer00", type.getName()));
        }
        try {
            typeEl = ser.writeSchema(type, this);
        }
        catch (Exception e) {
            throw AxisFault.makeFault(e);
        }
        if (anonymous) {
            if (typeEl == null) {
                containingElement.setAttribute("type", this.getQNameString(this.getTypeQName(type)));
            } else {
                containingElement.appendChild(typeEl);
            }
        } else {
            if (typeEl != null) {
                typeEl.setAttribute("name", qName.getLocalPart());
                this.writeSchemaTypeDecl(qName, typeEl);
            }
            if (containingElement != null) {
                containingElement.setAttribute("type", this.getQNameString(qName));
            }
        }
        if (this.emitter != null) {
            this.emitter.getQName2ClassMap().put(qName, type);
        }
        return true;
    }

    public ServiceDesc getServiceDesc() {
        return this.serviceDesc;
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

