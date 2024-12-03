/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.BindingInput
 *  javax.wsdl.BindingOperation
 *  javax.wsdl.Input
 *  javax.wsdl.Message
 *  javax.wsdl.Operation
 *  javax.wsdl.Part
 *  javax.wsdl.extensions.ExtensibilityElement
 *  javax.wsdl.extensions.UnknownExtensibilityElement
 *  javax.wsdl.extensions.mime.MIMEMultipartRelated
 *  javax.wsdl.extensions.mime.MIMEPart
 *  javax.wsdl.extensions.soap.SOAPBody
 *  javax.wsdl.extensions.soap.SOAPOperation
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.wsdl.toJava;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.extensions.mime.MIMEMultipartRelated;
import javax.wsdl.extensions.mime.MIMEPart;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.xml.namespace.QName;
import javax.xml.rpc.holders.BooleanHolder;
import org.apache.axis.Constants;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.symbolTable.BaseType;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.CollectionTE;
import org.apache.axis.wsdl.symbolTable.CollectionType;
import org.apache.axis.wsdl.symbolTable.DefinedElement;
import org.apache.axis.wsdl.symbolTable.Element;
import org.apache.axis.wsdl.symbolTable.MessageEntry;
import org.apache.axis.wsdl.symbolTable.MimeInfo;
import org.apache.axis.wsdl.symbolTable.Parameter;
import org.apache.axis.wsdl.symbolTable.Parameters;
import org.apache.axis.wsdl.symbolTable.SchemaUtils;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.Type;
import org.apache.axis.wsdl.symbolTable.TypeEntry;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.JavaEnumTypeWriter;
import org.apache.axis.wsdl.toJava.JavaGeneratorFactory;
import org.apache.axis.wsdl.toJava.Namespaces;
import org.apache.commons.logging.Log;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Utils
extends org.apache.axis.wsdl.symbolTable.Utils {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$wsdl$toJava$Utils == null ? (class$org$apache$axis$wsdl$toJava$Utils = Utils.class$("org.apache.axis.wsdl.toJava.Utils")) : class$org$apache$axis$wsdl$toJava$Utils).getName());
    private static HashMap TYPES = new HashMap(7);
    private static HashMap constructorMap;
    private static HashMap constructorThrowMap;
    static /* synthetic */ Class class$org$apache$axis$wsdl$toJava$Utils;

    public static String holder(TypeEntry type, Emitter emitter) {
        Parameter arg = new Parameter();
        arg.setType(type);
        return Utils.holder(arg, emitter);
    }

    public static String holder(Parameter p, Emitter emitter) {
        String mimeDimensions;
        String mimeType = p.getMIMEInfo() == null ? null : p.getMIMEInfo().getType();
        String string = mimeDimensions = mimeType == null ? "" : p.getMIMEInfo().getDimensions();
        if (mimeType != null) {
            if (mimeType.equals("image/gif") || mimeType.equals("image/jpeg")) {
                return "org.apache.axis.holders.ImageHolder" + mimeDimensions;
            }
            if (mimeType.equals("text/plain")) {
                return "javax.xml.rpc.holders.StringHolder" + mimeDimensions;
            }
            if (mimeType.startsWith("multipart/")) {
                return "org.apache.axis.holders.MimeMultipartHolder" + mimeDimensions;
            }
            if (mimeType.startsWith("application/octetstream") || mimeType.startsWith("application/octet-stream")) {
                return "org.apache.axis.holders.OctetStreamHolder" + mimeDimensions;
            }
            if (mimeType.equals("text/xml") || mimeType.equals("application/xml")) {
                return "org.apache.axis.holders.SourceHolder" + mimeDimensions;
            }
            return "org.apache.axis.holders.DataHandlerHolder" + mimeDimensions;
        }
        TypeEntry type = p.getType();
        String typeValue = type.getName();
        if (p.isOmittable() && (type instanceof BaseType || type instanceof DefinedElement && type.getRefType() instanceof BaseType)) {
            String wrapperTypeValue = (String)TYPES.get(typeValue);
            String string2 = typeValue = wrapperTypeValue == null ? typeValue : wrapperTypeValue;
        }
        if (typeValue.equals("byte[]") && type.isBaseType()) {
            return "javax.xml.rpc.holders.ByteArrayHolder";
        }
        if (typeValue.endsWith("[]")) {
            String name = emitter.getJavaName(type.getQName());
            String packagePrefix = "";
            if (type instanceof CollectionType && type.getRefType() instanceof BaseType) {
                String uri = type.getRefType().getQName().getNamespaceURI();
                packagePrefix = emitter.getNamespaces().getCreate(uri, false);
                packagePrefix = packagePrefix == null ? "" : packagePrefix + '.';
            }
            name = JavaUtils.replace(name, "java.lang.", "");
            name = JavaUtils.replace(name, "[]", "Array");
            name = Utils.addPackageName(name, "holders");
            return packagePrefix + name + "Holder";
        }
        if (typeValue.equals("String")) {
            return "javax.xml.rpc.holders.StringHolder";
        }
        if (typeValue.equals("java.lang.String")) {
            return "javax.xml.rpc.holders.StringHolder";
        }
        if (typeValue.equals("Object")) {
            return "javax.xml.rpc.holders.ObjectHolder";
        }
        if (typeValue.equals("java.lang.Object")) {
            return "javax.xml.rpc.holders.ObjectHolder";
        }
        if (typeValue.equals("int") || typeValue.equals("long") || typeValue.equals("short") || typeValue.equals("float") || typeValue.equals("double") || typeValue.equals("boolean") || typeValue.equals("byte")) {
            return "javax.xml.rpc.holders." + Utils.capitalizeFirstChar(typeValue) + "Holder";
        }
        if (typeValue.startsWith("java.lang.")) {
            return "javax.xml.rpc.holders" + typeValue.substring(typeValue.lastIndexOf(".")) + "WrapperHolder";
        }
        if (typeValue.indexOf(".") < 0) {
            return "javax.xml.rpc.holders" + typeValue + "WrapperHolder";
        }
        if (typeValue.equals("java.math.BigDecimal")) {
            return "javax.xml.rpc.holders.BigDecimalHolder";
        }
        if (typeValue.equals("java.math.BigInteger")) {
            return "javax.xml.rpc.holders.BigIntegerHolder";
        }
        if (typeValue.equals("java.util.Date")) {
            return "org.apache.axis.holders.DateHolder";
        }
        if (typeValue.equals("java.util.Calendar")) {
            return "javax.xml.rpc.holders.CalendarHolder";
        }
        if (typeValue.equals("javax.xml.namespace.QName")) {
            return "javax.xml.rpc.holders.QNameHolder";
        }
        if (typeValue.equals("javax.activation.DataHandler")) {
            return "org.apache.axis.holders.DataHandlerHolder";
        }
        if (typeValue.startsWith("org.apache.axis.types.")) {
            int i = typeValue.lastIndexOf(46);
            String t = typeValue.substring(i + 1);
            return "org.apache.axis.holders." + t + "Holder";
        }
        return Utils.addPackageName(typeValue, "holders") + "Holder";
    }

    public static String addPackageName(String className, String newPkg) {
        int index = className.lastIndexOf(".");
        if (index >= 0) {
            return className.substring(0, index) + "." + newPkg + className.substring(index);
        }
        return newPkg + "." + className;
    }

    public static String getFullExceptionName(Message faultMessage, SymbolTable symbolTable) {
        MessageEntry me = symbolTable.getMessageEntry(faultMessage.getQName());
        return (String)me.getDynamicVar(JavaGeneratorFactory.EXCEPTION_CLASS_NAME);
    }

    public static QName getFaultDataType(Message faultMessage, SymbolTable symbolTable) {
        MessageEntry me = symbolTable.getMessageEntry(faultMessage.getQName());
        return (QName)me.getDynamicVar(JavaGeneratorFactory.EXCEPTION_DATA_TYPE);
    }

    public static boolean isFaultComplex(Message faultMessage, SymbolTable symbolTable) {
        MessageEntry me = symbolTable.getMessageEntry(faultMessage.getQName());
        Boolean ret = (Boolean)me.getDynamicVar(JavaGeneratorFactory.COMPLEX_TYPE_FAULT);
        if (ret != null) {
            return ret;
        }
        return false;
    }

    public static Vector getEnumerationBaseAndValues(Node node, SymbolTable symbolTable) {
        int j;
        NodeList children;
        if (node == null) {
            return null;
        }
        QName nodeKind = Utils.getNodeQName(node);
        if (nodeKind != null && nodeKind.getLocalPart().equals("element") && Constants.isSchemaXSD(nodeKind.getNamespaceURI())) {
            children = node.getChildNodes();
            Node simpleNode = null;
            for (j = 0; j < children.getLength() && simpleNode == null; ++j) {
                QName simpleKind = Utils.getNodeQName(children.item(j));
                if (simpleKind == null || !simpleKind.getLocalPart().equals("simpleType") || !Constants.isSchemaXSD(simpleKind.getNamespaceURI())) continue;
                node = simpleNode = children.item(j);
            }
        }
        if ((nodeKind = Utils.getNodeQName(node)) != null && nodeKind.getLocalPart().equals("simpleType") && Constants.isSchemaXSD(nodeKind.getNamespaceURI())) {
            String javaName;
            QName baseType;
            children = node.getChildNodes();
            Node restrictionNode = null;
            for (j = 0; j < children.getLength() && restrictionNode == null; ++j) {
                QName restrictionKind = Utils.getNodeQName(children.item(j));
                if (restrictionKind == null || !restrictionKind.getLocalPart().equals("restriction") || !Constants.isSchemaXSD(restrictionKind.getNamespaceURI())) continue;
                restrictionNode = children.item(j);
            }
            Type baseEType = null;
            if (restrictionNode != null && (baseEType = symbolTable.getType(baseType = Utils.getTypeQName(restrictionNode, new BooleanHolder(), false))) != null && ((javaName = baseEType.getName()).equals("boolean") || !SchemaUtils.isSimpleSchemaType(baseEType.getQName()))) {
                baseEType = null;
            }
            if (baseEType != null && restrictionNode != null) {
                Vector<Object> v = new Vector<Object>();
                NodeList enums = restrictionNode.getChildNodes();
                for (int i = 0; i < enums.getLength(); ++i) {
                    Node enumNode;
                    String value;
                    QName enumKind = Utils.getNodeQName(enums.item(i));
                    if (enumKind == null || !enumKind.getLocalPart().equals("enumeration") || !Constants.isSchemaXSD(enumKind.getNamespaceURI()) || (value = Utils.getAttribute(enumNode = enums.item(i), "value")) == null) continue;
                    v.add(value);
                }
                if (v.isEmpty()) {
                    return null;
                }
                v.add(0, baseEType);
                return v;
            }
        }
        return null;
    }

    public static String capitalizeFirstChar(String name) {
        if (name == null || name.equals("")) {
            return name;
        }
        char start = name.charAt(0);
        if (Character.isLowerCase(start)) {
            start = Character.toUpperCase(start);
            return start + name.substring(1);
        }
        return name;
    }

    public static String addUnderscore(String name) {
        if (name == null || name.equals("")) {
            return name;
        }
        return "_" + name;
    }

    public static String xmlNameToJava(String name) {
        return JavaUtils.xmlNameToJava(name);
    }

    public static String xmlNameToJavaClass(String name) {
        return Utils.capitalizeFirstChar(Utils.xmlNameToJava(name));
    }

    public static String makePackageName(String namespace) {
        String hostname = null;
        String path = "";
        try {
            URL u = new URL(namespace);
            hostname = u.getHost();
            path = u.getPath();
        }
        catch (MalformedURLException e) {
            if (namespace.indexOf(":") > -1) {
                hostname = namespace.substring(namespace.indexOf(":") + 1);
                if (hostname.indexOf("/") > -1) {
                    hostname = hostname.substring(0, hostname.indexOf("/"));
                }
            }
            hostname = namespace;
        }
        if (hostname == null) {
            return null;
        }
        hostname = hostname.replace('-', '_');
        if ((path = path.replace('-', '_')).length() > 0 && path.charAt(path.length() - 1) == '/') {
            path = path.substring(0, path.length() - 1);
        }
        StringTokenizer st = new StringTokenizer(hostname, ".:");
        String[] words = new String[st.countTokens()];
        for (int i = 0; i < words.length; ++i) {
            words[i] = st.nextToken();
        }
        StringBuffer sb = new StringBuffer(namespace.length());
        for (int i = words.length - 1; i >= 0; --i) {
            Utils.addWordToPackageBuffer(sb, words[i], i == words.length - 1);
        }
        StringTokenizer st2 = new StringTokenizer(path, "/");
        while (st2.hasMoreTokens()) {
            Utils.addWordToPackageBuffer(sb, st2.nextToken(), false);
        }
        return sb.toString();
    }

    private static void addWordToPackageBuffer(StringBuffer sb, String word, boolean firstWord) {
        if (JavaUtils.isJavaKeyword(word)) {
            word = JavaUtils.makeNonJavaKeyword(word);
        }
        if (!firstWord) {
            sb.append('.');
        }
        if (Character.isDigit(word.charAt(0))) {
            sb.append('_');
        }
        if (word.indexOf(46) != -1) {
            char[] buf = word.toCharArray();
            for (int i = 0; i < word.length(); ++i) {
                if (buf[i] != '.') continue;
                buf[i] = 95;
            }
            word = new String(buf);
        }
        sb.append(word);
    }

    public static String getJavaLocalName(String fullName) {
        return fullName.substring(fullName.lastIndexOf(46) + 1);
    }

    public static String getJavaPackageName(String fullName) {
        if (fullName.lastIndexOf(46) > 0) {
            return fullName.substring(0, fullName.lastIndexOf(46));
        }
        return "";
    }

    public static boolean fileExists(String name, String namespace, Namespaces namespaces) throws IOException {
        String packageName = namespaces.getAsDir(namespace);
        String fullName = packageName + name;
        return new File(fullName).exists();
    }

    public static String wrapPrimitiveType(TypeEntry type, String var) {
        String objType;
        String string = objType = type == null ? null : (String)TYPES.get(type.getName());
        if (objType != null) {
            return "new " + objType + "(" + var + ")";
        }
        if (type != null && type.getName().equals("byte[]") && type.getQName().getLocalPart().equals("hexBinary")) {
            return "new org.apache.axis.types.HexBinary(" + var + ")";
        }
        return var;
    }

    public static String getResponseString(Parameter param, String var) {
        String mimeDimensions;
        if (param.getType() == null) {
            return ";";
        }
        String typeName = param.getType().getName();
        MimeInfo mimeInfo = param.getMIMEInfo();
        String mimeType = mimeInfo == null ? null : mimeInfo.getType();
        String string = mimeDimensions = mimeInfo == null ? "" : mimeInfo.getDimensions();
        if (mimeType != null) {
            if (mimeType.equals("image/gif") || mimeType.equals("image/jpeg")) {
                return "(java.awt.Image" + mimeDimensions + ") " + var + ";";
            }
            if (mimeType.equals("text/plain")) {
                return "(java.lang.String" + mimeDimensions + ") " + var + ";";
            }
            if (mimeType.equals("text/xml") || mimeType.equals("application/xml")) {
                return "(javax.xml.transform.Source" + mimeDimensions + ") " + var + ";";
            }
            if (mimeType.startsWith("multipart/")) {
                return "(javax.mail.internet.MimeMultipart" + mimeDimensions + ") " + var + ";";
            }
            if (mimeType.startsWith("application/octetstream") || mimeType.startsWith("application/octet-stream")) {
                return "(org.apache.axis.attachments.OctetStream" + mimeDimensions + ") " + var + ";";
            }
            return "(javax.activation.DataHandler" + mimeDimensions + ") " + var + ";";
        }
        String objType = (String)TYPES.get(typeName);
        if (objType != null) {
            if (param.isOmittable() && param.getType().getDimensions().equals("") || param.getType().getUnderlTypeNillable()) {
                typeName = Utils.getWrapperType(param.getType());
            } else {
                return "((" + objType + ") " + var + ")." + typeName + "Value();";
            }
        }
        return "(" + typeName + ") " + var + ";";
    }

    public static boolean isPrimitiveType(TypeEntry type) {
        return TYPES.get(type.getName()) != null;
    }

    public static String getWrapperType(String type) {
        String ret = (String)TYPES.get(type);
        return ret == null ? type : ret;
    }

    public static String getWrapperType(TypeEntry type) {
        String dims = type.getDimensions();
        if (!dims.equals("")) {
            TypeEntry te = type.getRefType();
            if (te != null && !te.getDimensions().equals("")) {
                return Utils.getWrapperType(te) + dims;
            }
            if (te instanceof BaseType || te instanceof DefinedElement && te.getRefType() instanceof BaseType) {
                return Utils.getWrapperType(te) + dims;
            }
        }
        return Utils.getWrapperType(type.getName());
    }

    public static QName getOperationQName(BindingOperation bindingOper, BindingEntry bEntry, SymbolTable symbolTable) {
        Map parts;
        Input input;
        Operation operation = bindingOper.getOperation();
        String operationName = operation.getName();
        if (bEntry.getBindingStyle() == Style.DOCUMENT && symbolTable.isWrapped() && (input = operation.getInput()) != null && (parts = input.getMessage().getParts()) != null && !parts.isEmpty()) {
            Iterator i = parts.values().iterator();
            Part p = (Part)i.next();
            return p.getElementName();
        }
        String ns = null;
        BindingInput bindInput = bindingOper.getBindingInput();
        if (bindInput != null) {
            Iterator it = bindInput.getExtensibilityElements().iterator();
            while (it.hasNext()) {
                UnknownExtensibilityElement unkElement;
                QName name;
                ExtensibilityElement elem = (ExtensibilityElement)it.next();
                if (elem instanceof SOAPBody) {
                    SOAPBody body = (SOAPBody)elem;
                    ns = body.getNamespaceURI();
                    if (bEntry.getInputBodyType(operation) != Use.ENCODED || ns != null && ns.length() != 0) break;
                    log.warn((Object)Messages.getMessage("badNamespaceForOperation00", bEntry.getName(), operation.getName()));
                    break;
                }
                if (elem instanceof MIMEMultipartRelated) {
                    Object part = null;
                    MIMEMultipartRelated mpr = (MIMEMultipartRelated)elem;
                    List l = mpr.getMIMEParts();
                    block1: for (int j = 0; l != null && j < l.size() && part == null; ++j) {
                        MIMEPart mp = (MIMEPart)l.get(j);
                        List ll = mp.getExtensibilityElements();
                        for (int k = 0; ll != null && k < ll.size() && part == null; ++k) {
                            part = ll.get(k);
                            if (part instanceof SOAPBody) {
                                SOAPBody body = part;
                                ns = body.getNamespaceURI();
                                if (bEntry.getInputBodyType(operation) != Use.ENCODED || ns != null && ns.length() != 0) continue block1;
                                log.warn((Object)Messages.getMessage("badNamespaceForOperation00", bEntry.getName(), operation.getName()));
                                continue block1;
                            }
                            part = null;
                        }
                    }
                    continue;
                }
                if (!(elem instanceof UnknownExtensibilityElement) || !(name = (unkElement = (UnknownExtensibilityElement)elem).getElementType()).getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/soap12/") || !name.getLocalPart().equals("body")) continue;
                ns = unkElement.getElement().getAttribute("namespace");
            }
        }
        if (ns == null) {
            ns = "";
        }
        return new QName(ns, operationName);
    }

    public static String getOperationSOAPAction(BindingOperation bindingOper) {
        List elems = bindingOper.getExtensibilityElements();
        Iterator it = elems.iterator();
        boolean found = false;
        String action = null;
        while (!found && it.hasNext()) {
            UnknownExtensibilityElement unkElement;
            QName name;
            ExtensibilityElement elem = (ExtensibilityElement)it.next();
            if (elem instanceof SOAPOperation) {
                SOAPOperation soapOp = (SOAPOperation)elem;
                action = soapOp.getSoapActionURI();
                found = true;
                continue;
            }
            if (!(elem instanceof UnknownExtensibilityElement) || !(name = (unkElement = (UnknownExtensibilityElement)elem).getElementType()).getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/soap12/") || !name.getLocalPart().equals("operation")) continue;
            action = unkElement.getElement().getAttribute("soapAction");
            found = true;
        }
        return action;
    }

    public static String getNewQName(QName qname) {
        return "new javax.xml.namespace.QName(\"" + qname.getNamespaceURI() + "\", \"" + qname.getLocalPart() + "\")";
    }

    public static String getNewQNameWithLastLocalPart(QName qname) {
        return "new javax.xml.namespace.QName(\"" + qname.getNamespaceURI() + "\", \"" + Utils.getLastLocalPart(qname.getLocalPart()) + "\")";
    }

    public static String getParameterTypeName(Parameter parm) {
        String ret;
        if (parm.getMIMEInfo() == null) {
            ret = parm.getType().getName();
            if (parm.isOmittable() && parm.getType().getDimensions().equals("") || parm.getType().getUnderlTypeNillable()) {
                ret = Utils.getWrapperType(parm.getType());
            }
        } else {
            String mime = parm.getMIMEInfo().getType();
            ret = JavaUtils.mimeToJava(mime);
            ret = ret == null ? parm.getType().getName() : ret + parm.getMIMEInfo().getDimensions();
        }
        return ret;
    }

    public static QName getXSIType(Parameter param) {
        if (param.getMIMEInfo() != null) {
            return Utils.getMIMETypeQName(param.getMIMEInfo().getType());
        }
        return Utils.getXSIType(param.getType());
    }

    public static QName getXSIType(TypeEntry te) {
        QName xmlType = null;
        if (te != null && te instanceof Element && te.getRefType() != null) {
            te = te.getRefType();
        }
        if (te != null && te instanceof CollectionTE && te.getRefType() != null) {
            te = te.getRefType();
        }
        if (te != null) {
            xmlType = te.getQName();
        }
        return xmlType;
    }

    public static QName getMIMETypeQName(String mimeName) {
        if ("text/plain".equals(mimeName)) {
            return Constants.MIME_PLAINTEXT;
        }
        if ("image/gif".equals(mimeName) || "image/jpeg".equals(mimeName)) {
            return Constants.MIME_IMAGE;
        }
        if ("text/xml".equals(mimeName) || "applications/xml".equals(mimeName)) {
            return Constants.MIME_SOURCE;
        }
        if ("application/octet-stream".equals(mimeName) || "application/octetstream".equals(mimeName)) {
            return Constants.MIME_OCTETSTREAM;
        }
        if (mimeName != null && mimeName.startsWith("multipart/")) {
            return Constants.MIME_MULTIPART;
        }
        return Constants.MIME_DATA_HANDLER;
    }

    public static boolean hasMIME(BindingEntry bEntry) {
        List operations = bEntry.getBinding().getBindingOperations();
        for (int i = 0; i < operations.size(); ++i) {
            BindingOperation operation = (BindingOperation)operations.get(i);
            if (!Utils.hasMIME(bEntry, operation)) continue;
            return true;
        }
        return false;
    }

    public static boolean hasMIME(BindingEntry bEntry, BindingOperation operation) {
        Parameters parameters = bEntry.getParameters(operation.getOperation());
        if (parameters != null) {
            for (int idx = 0; idx < parameters.list.size(); ++idx) {
                Parameter p = (Parameter)parameters.list.get(idx);
                if (p.getMIMEInfo() == null) continue;
                return true;
            }
        }
        return false;
    }

    public static String getConstructorForParam(Parameter param, SymbolTable symbolTable, BooleanHolder bThrow) {
        Vector v2;
        String paramType = param.getType().getName();
        if (param.isOmittable()) {
            paramType = Utils.getWrapperType(paramType);
        }
        String mimeType = param.getMIMEInfo() == null ? null : param.getMIMEInfo().getType();
        String mimeDimensions = param.getMIMEInfo() == null ? "" : param.getMIMEInfo().getDimensions();
        String out = null;
        if (mimeType != null) {
            if (mimeType.equals("image/gif") || mimeType.equals("image/jpeg")) {
                return "null";
            }
            if (mimeType.equals("text/xml") || mimeType.equals("application/xml")) {
                if (mimeDimensions.length() <= 0) {
                    return "new javax.xml.transform.stream.StreamSource()";
                }
                return "new javax.xml.transform.stream.StreamSource[0]";
            }
            if (mimeType.equals("application/octet-stream") || mimeType.equals("application/octetstream")) {
                if (mimeDimensions.length() <= 0) {
                    return "new org.apache.axis.attachments.OctetStream()";
                }
                return "new org.apache.axis.attachments.OctetStream[0]";
            }
            return "new " + Utils.getParameterTypeName(param) + "()";
        }
        out = (String)constructorMap.get(paramType);
        if (out != null) {
            return out;
        }
        out = (String)constructorThrowMap.get(paramType);
        if (out != null) {
            bThrow.value = true;
            return out;
        }
        if (paramType.endsWith("[]")) {
            return "new " + JavaUtils.replace(paramType, "[]", "[0]");
        }
        Vector v = Utils.getEnumerationBaseAndValues(param.getType().getNode(), symbolTable);
        if (v != null) {
            String enumeration = (String)JavaEnumTypeWriter.getEnumValueIds(v).get(0);
            return paramType + "." + enumeration;
        }
        if (param.getType().getRefType() != null && (v2 = Utils.getEnumerationBaseAndValues(param.getType().getRefType().getNode(), symbolTable)) != null) {
            String enumeration = (String)JavaEnumTypeWriter.getEnumValueIds(v2).get(0);
            return paramType + "." + enumeration;
        }
        return "new " + paramType + "()";
    }

    public static boolean shouldEmit(TypeEntry type) {
        return (type.getBaseType() == null || type.getRefType() != null) && !(type instanceof CollectionTE) && !(type instanceof Element) && type.isReferenced() && !type.isOnlyLiteralReferenced() && (type.getNode() == null || !Utils.isXsNode(type.getNode(), "group") && !Utils.isXsNode(type.getNode(), "attributeGroup"));
    }

    public static boolean isXsNode(Node node, String nameName) {
        return node.getLocalName().equals(nameName) && Constants.isSchemaXSD(node.getNamespaceURI());
    }

    public static QName getItemQName(TypeEntry te) {
        if (te instanceof DefinedElement) {
            te = te.getRefType();
        }
        return te.getItemQName();
    }

    public static QName getItemType(TypeEntry te) {
        if (te instanceof DefinedElement) {
            te = te.getRefType();
        }
        return te.getComponentType();
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        TYPES.put("int", "java.lang.Integer");
        TYPES.put("float", "java.lang.Float");
        TYPES.put("boolean", "java.lang.Boolean");
        TYPES.put("double", "java.lang.Double");
        TYPES.put("byte", "java.lang.Byte");
        TYPES.put("short", "java.lang.Short");
        TYPES.put("long", "java.lang.Long");
        constructorMap = new HashMap(50);
        constructorThrowMap = new HashMap(50);
        constructorMap.put("int", "0");
        constructorMap.put("float", "0");
        constructorMap.put("boolean", "true");
        constructorMap.put("double", "0");
        constructorMap.put("byte", "(byte)0");
        constructorMap.put("short", "(short)0");
        constructorMap.put("long", "0");
        constructorMap.put("java.lang.Boolean", "new java.lang.Boolean(false)");
        constructorMap.put("java.lang.Byte", "new java.lang.Byte((byte)0)");
        constructorMap.put("java.lang.Double", "new java.lang.Double(0)");
        constructorMap.put("java.lang.Float", "new java.lang.Float(0)");
        constructorMap.put("java.lang.Integer", "new java.lang.Integer(0)");
        constructorMap.put("java.lang.Long", "new java.lang.Long(0)");
        constructorMap.put("java.lang.Short", "new java.lang.Short((short)0)");
        constructorMap.put("java.math.BigDecimal", "new java.math.BigDecimal(0)");
        constructorMap.put("java.math.BigInteger", "new java.math.BigInteger(\"0\")");
        constructorMap.put("java.lang.Object", "new java.lang.String()");
        constructorMap.put("byte[]", "new byte[0]");
        constructorMap.put("java.util.Calendar", "java.util.Calendar.getInstance()");
        constructorMap.put("javax.xml.namespace.QName", "new javax.xml.namespace.QName(\"http://double-double\", \"toil-and-trouble\")");
        constructorMap.put("org.apache.axis.types.NonNegativeInteger", "new org.apache.axis.types.NonNegativeInteger(\"0\")");
        constructorMap.put("org.apache.axis.types.PositiveInteger", "new org.apache.axis.types.PositiveInteger(\"1\")");
        constructorMap.put("org.apache.axis.types.NonPositiveInteger", "new org.apache.axis.types.NonPositiveInteger(\"0\")");
        constructorMap.put("org.apache.axis.types.NegativeInteger", "new org.apache.axis.types.NegativeInteger(\"-1\")");
        constructorThrowMap.put("org.apache.axis.types.Time", "new org.apache.axis.types.Time(\"15:45:45.275Z\")");
        constructorThrowMap.put("org.apache.axis.types.UnsignedLong", "new org.apache.axis.types.UnsignedLong(0)");
        constructorThrowMap.put("org.apache.axis.types.UnsignedInt", "new org.apache.axis.types.UnsignedInt(0)");
        constructorThrowMap.put("org.apache.axis.types.UnsignedShort", "new org.apache.axis.types.UnsignedShort(0)");
        constructorThrowMap.put("org.apache.axis.types.UnsignedByte", "new org.apache.axis.types.UnsignedByte(0)");
        constructorThrowMap.put("org.apache.axis.types.URI", "new org.apache.axis.types.URI(\"urn:testing\")");
        constructorThrowMap.put("org.apache.axis.types.Year", "new org.apache.axis.types.Year(2000)");
        constructorThrowMap.put("org.apache.axis.types.Month", "new org.apache.axis.types.Month(1)");
        constructorThrowMap.put("org.apache.axis.types.Day", "new org.apache.axis.types.Day(1)");
        constructorThrowMap.put("org.apache.axis.types.YearMonth", "new org.apache.axis.types.YearMonth(2000,1)");
        constructorThrowMap.put("org.apache.axis.types.MonthDay", "new org.apache.axis.types.MonthDay(1, 1)");
    }
}

