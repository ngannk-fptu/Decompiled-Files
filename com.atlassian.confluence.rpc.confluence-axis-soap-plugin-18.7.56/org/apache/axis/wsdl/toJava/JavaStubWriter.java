/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.Binding
 *  javax.wsdl.BindingOperation
 *  javax.wsdl.Fault
 *  javax.wsdl.Message
 *  javax.wsdl.Operation
 *  javax.wsdl.OperationType
 *  javax.wsdl.Part
 *  javax.wsdl.PortType
 *  javax.wsdl.extensions.UnknownExtensibilityElement
 *  javax.wsdl.extensions.soap.SOAPBinding
 *  javax.wsdl.extensions.soap.SOAPOperation
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.wsdl.toJava;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Fault;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.OperationType;
import javax.wsdl.Part;
import javax.wsdl.PortType;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.xml.namespace.QName;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.StringUtils;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.DefinedType;
import org.apache.axis.wsdl.symbolTable.FaultInfo;
import org.apache.axis.wsdl.symbolTable.Parameter;
import org.apache.axis.wsdl.symbolTable.Parameters;
import org.apache.axis.wsdl.symbolTable.SchemaUtils;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.TypeEntry;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.JavaBindingWriter;
import org.apache.axis.wsdl.toJava.JavaClassWriter;
import org.apache.axis.wsdl.toJava.Utils;
import org.apache.commons.logging.Log;

public class JavaStubWriter
extends JavaClassWriter {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$wsdl$toJava$JavaStubWriter == null ? (class$org$apache$axis$wsdl$toJava$JavaStubWriter = JavaStubWriter.class$("org.apache.axis.wsdl.toJava.JavaStubWriter")) : class$org$apache$axis$wsdl$toJava$JavaStubWriter).getName());
    private BindingEntry bEntry;
    private Binding binding;
    private SymbolTable symbolTable;
    private static final int MAXIMUM_BINDINGS_PER_METHOD = 100;
    static String[] modeStrings = new String[]{"", "org.apache.axis.description.ParameterDesc.IN", "org.apache.axis.description.ParameterDesc.OUT", "org.apache.axis.description.ParameterDesc.INOUT"};
    static Map styles = new HashMap();
    static Map uses = new HashMap();
    static int OPERDESC_PER_BLOCK;
    static /* synthetic */ Class class$org$apache$axis$wsdl$toJava$JavaStubWriter;

    public JavaStubWriter(Emitter emitter, BindingEntry bEntry, SymbolTable symbolTable) {
        super(emitter, bEntry.getName() + "Stub", "stub");
        this.bEntry = bEntry;
        this.binding = bEntry.getBinding();
        this.symbolTable = symbolTable;
    }

    protected String getExtendsText() {
        return "extends org.apache.axis.client.Stub ";
    }

    protected String getImplementsText() {
        return "implements " + this.bEntry.getDynamicVar(JavaBindingWriter.INTERFACE_NAME) + " ";
    }

    protected void writeFileBody(PrintWriter pw) throws IOException {
        int i;
        PortType portType = this.binding.getPortType();
        HashSet types = this.getTypesInPortType(portType);
        boolean hasMIME = Utils.hasMIME(this.bEntry);
        if (types.size() > 0 || hasMIME) {
            pw.println("    private java.util.Vector cachedSerClasses = new java.util.Vector();");
            pw.println("    private java.util.Vector cachedSerQNames = new java.util.Vector();");
            pw.println("    private java.util.Vector cachedSerFactories = new java.util.Vector();");
            pw.println("    private java.util.Vector cachedDeserFactories = new java.util.Vector();");
        }
        pw.println();
        pw.println("    static org.apache.axis.description.OperationDesc [] _operations;");
        pw.println();
        this.writeOperationMap(pw);
        pw.println();
        pw.println("    public " + this.className + "() throws org.apache.axis.AxisFault {");
        pw.println("         this(null);");
        pw.println("    }");
        pw.println();
        pw.println("    public " + this.className + "(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {");
        pw.println("         this(service);");
        pw.println("         super.cachedEndpoint = endpointURL;");
        pw.println("    }");
        pw.println();
        pw.println("    public " + this.className + "(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {");
        pw.println("        if (service == null) {");
        pw.println("            super.service = new org.apache.axis.client.Service();");
        pw.println("        } else {");
        pw.println("            super.service = service;");
        pw.println("        }");
        pw.println("        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion(\"" + this.emitter.getTypeMappingVersion() + "\");");
        ArrayList<TypeEntry> deferredBindings = new ArrayList<TypeEntry>();
        int typeMappingCount = 0;
        if (types.size() > 0) {
            Iterator it = types.iterator();
            while (it.hasNext()) {
                TypeEntry type = (TypeEntry)it.next();
                if (!Utils.shouldEmit(type)) continue;
                if (typeMappingCount == 0) {
                    this.writeSerializationDecls(pw, hasMIME, this.binding.getQName().getNamespaceURI());
                }
                deferredBindings.add(type);
                ++typeMappingCount;
            }
        }
        Collections.sort(deferredBindings, new Comparator(){

            public int compare(Object a, Object b) {
                TypeEntry type1 = (TypeEntry)a;
                TypeEntry type2 = (TypeEntry)b;
                return type1.getQName().toString().compareToIgnoreCase(type2.getQName().toString());
            }
        });
        if (typeMappingCount == 0 && hasMIME) {
            this.writeSerializationDecls(pw, hasMIME, this.binding.getQName().getNamespaceURI());
            ++typeMappingCount;
        }
        boolean needsMultipleBindingMethods = false;
        if (deferredBindings.size() < 100) {
            Iterator it = deferredBindings.iterator();
            while (it.hasNext()) {
                this.writeSerializationInit(pw, (TypeEntry)it.next());
            }
        } else {
            needsMultipleBindingMethods = true;
            int methodCount = this.calculateBindingMethodCount(deferredBindings);
            for (i = 0; i < methodCount; ++i) {
                pw.println("        addBindings" + i + "();");
            }
        }
        pw.println("    }");
        pw.println();
        if (needsMultipleBindingMethods) {
            this.writeBindingMethods(pw, deferredBindings);
            pw.println();
        }
        pw.println("    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {");
        pw.println("        try {");
        pw.println("            org.apache.axis.client.Call _call = super._createCall();");
        pw.println("            if (super.maintainSessionSet) {");
        pw.println("                _call.setMaintainSession(super.maintainSession);");
        pw.println("            }");
        pw.println("            if (super.cachedUsername != null) {");
        pw.println("                _call.setUsername(super.cachedUsername);");
        pw.println("            }");
        pw.println("            if (super.cachedPassword != null) {");
        pw.println("                _call.setPassword(super.cachedPassword);");
        pw.println("            }");
        pw.println("            if (super.cachedEndpoint != null) {");
        pw.println("                _call.setTargetEndpointAddress(super.cachedEndpoint);");
        pw.println("            }");
        pw.println("            if (super.cachedTimeout != null) {");
        pw.println("                _call.setTimeout(super.cachedTimeout);");
        pw.println("            }");
        pw.println("            if (super.cachedPortName != null) {");
        pw.println("                _call.setPortName(super.cachedPortName);");
        pw.println("            }");
        pw.println("            java.util.Enumeration keys = super.cachedProperties.keys();");
        pw.println("            while (keys.hasMoreElements()) {");
        pw.println("                java.lang.String key = (java.lang.String) keys.nextElement();");
        pw.println("                _call.setProperty(key, super.cachedProperties.get(key));");
        pw.println("            }");
        if (typeMappingCount > 0) {
            pw.println("            // " + Messages.getMessage("typeMap00"));
            pw.println("            // " + Messages.getMessage("typeMap01"));
            pw.println("            // " + Messages.getMessage("typeMap02"));
            pw.println("            // " + Messages.getMessage("typeMap03"));
            pw.println("            // " + Messages.getMessage("typeMap04"));
            pw.println("            synchronized (this) {");
            pw.println("                if (firstCall()) {");
            pw.println("                    // " + Messages.getMessage("mustSetStyle"));
            if (this.bEntry.hasLiteral()) {
                pw.println("                    _call.setEncodingStyle(null);");
            } else {
                Iterator iterator = this.bEntry.getBinding().getExtensibilityElements().iterator();
                while (iterator.hasNext()) {
                    UnknownExtensibilityElement unkElement;
                    QName name;
                    Object obj = iterator.next();
                    if (obj instanceof SOAPBinding) {
                        pw.println("                    _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);");
                        pw.println("                    _call.setEncodingStyle(org.apache.axis.Constants.URI_SOAP11_ENC);");
                        continue;
                    }
                    if (!(obj instanceof UnknownExtensibilityElement) || !(name = (unkElement = (UnknownExtensibilityElement)obj).getElementType()).getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/soap12/") || !name.getLocalPart().equals("binding")) continue;
                    pw.println("                    _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);");
                    pw.println("                    _call.setEncodingStyle(org.apache.axis.Constants.URI_SOAP12_ENC);");
                }
            }
            pw.println("                    for (int i = 0; i < cachedSerFactories.size(); ++i) {");
            pw.println("                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);");
            pw.println("                        javax.xml.namespace.QName qName =");
            pw.println("                                (javax.xml.namespace.QName) cachedSerQNames.get(i);");
            pw.println("                        java.lang.Object x = cachedSerFactories.get(i);");
            pw.println("                        if (x instanceof Class) {");
            pw.println("                            java.lang.Class sf = (java.lang.Class)");
            pw.println("                                 cachedSerFactories.get(i);");
            pw.println("                            java.lang.Class df = (java.lang.Class)");
            pw.println("                                 cachedDeserFactories.get(i);");
            pw.println("                            _call.registerTypeMapping(cls, qName, sf, df, false);");
            pw.println("                        }");
            pw.println("                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {");
            pw.println("                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)");
            pw.println("                                 cachedSerFactories.get(i);");
            pw.println("                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)");
            pw.println("                                 cachedDeserFactories.get(i);");
            pw.println("                            _call.registerTypeMapping(cls, qName, sf, df, false);");
            pw.println("                        }");
            pw.println("                    }");
            pw.println("                }");
            pw.println("            }");
        }
        pw.println("            return _call;");
        pw.println("        }");
        pw.println("        catch (java.lang.Throwable _t) {");
        pw.println("            throw new org.apache.axis.AxisFault(\"" + Messages.getMessage("badCall01") + "\", _t);");
        pw.println("        }");
        pw.println("    }");
        pw.println();
        List operations = this.binding.getBindingOperations();
        for (i = 0; i < operations.size(); ++i) {
            Operation ptOperation;
            OperationType type;
            BindingOperation operation = (BindingOperation)operations.get(i);
            Parameters parameters = this.bEntry.getParameters(operation.getOperation());
            String soapAction = "";
            String opStyle = null;
            Iterator operationExtensibilityIterator = operation.getExtensibilityElements().iterator();
            while (operationExtensibilityIterator.hasNext()) {
                UnknownExtensibilityElement unkElement;
                QName name;
                Object obj = operationExtensibilityIterator.next();
                if (obj instanceof SOAPOperation) {
                    soapAction = ((SOAPOperation)obj).getSoapActionURI();
                    opStyle = ((SOAPOperation)obj).getStyle();
                    break;
                }
                if (!(obj instanceof UnknownExtensibilityElement) || !(name = (unkElement = (UnknownExtensibilityElement)obj).getElementType()).getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/soap12/") || !name.getLocalPart().equals("operation")) continue;
                if (unkElement.getElement().getAttribute("soapAction") != null) {
                    soapAction = unkElement.getElement().getAttribute("soapAction");
                }
                opStyle = unkElement.getElement().getAttribute("style");
            }
            if (OperationType.NOTIFICATION.equals(type = (ptOperation = operation.getOperation()).getStyle()) || OperationType.SOLICIT_RESPONSE.equals(type)) {
                pw.println(parameters.signature);
                pw.println();
                continue;
            }
            this.writeOperation(pw, operation, parameters, soapAction, opStyle, type == OperationType.ONE_WAY, i);
        }
    }

    private int calculateBindingMethodCount(List deferredBindings) {
        int methodCount = deferredBindings.size() / 100;
        if (deferredBindings.size() % 100 != 0) {
            ++methodCount;
        }
        return methodCount;
    }

    protected void writeBindingMethods(PrintWriter pw, List deferredBindings) {
        int methodCount = this.calculateBindingMethodCount(deferredBindings);
        for (int i = 0; i < methodCount; ++i) {
            int absolute;
            pw.println("    private void addBindings" + i + "() {");
            this.writeSerializationDecls(pw, false, null);
            for (int j = 0; j < 100 && (absolute = i * 100 + j) != deferredBindings.size(); ++j) {
                this.writeSerializationInit(pw, (TypeEntry)deferredBindings.get(absolute));
            }
            pw.println("    }");
        }
    }

    protected void writeOperationMap(PrintWriter pw) {
        List operations = this.binding.getBindingOperations();
        pw.println("    static {");
        pw.println("        _operations = new org.apache.axis.description.OperationDesc[" + operations.size() + "];");
        int k = 0;
        for (int j = 0; j < operations.size(); ++j) {
            if (j % OPERDESC_PER_BLOCK != 0) continue;
            pw.println("        _initOperationDesc" + ++k + "();");
        }
        k = 0;
        for (int i = 0; i < operations.size(); ++i) {
            Operation ptOperation;
            OperationType type;
            if (i % OPERDESC_PER_BLOCK == 0) {
                pw.println("    }\n");
                pw.println("    private static void _initOperationDesc" + ++k + "(){");
                pw.println("        org.apache.axis.description.OperationDesc oper;");
                pw.println("        org.apache.axis.description.ParameterDesc param;");
            }
            BindingOperation operation = (BindingOperation)operations.get(i);
            Parameters parameters = this.bEntry.getParameters(operation.getOperation());
            String opStyle = null;
            Iterator operationExtensibilityIterator = operation.getExtensibilityElements().iterator();
            while (operationExtensibilityIterator.hasNext()) {
                UnknownExtensibilityElement unkElement;
                QName name;
                Object obj = operationExtensibilityIterator.next();
                if (obj instanceof SOAPOperation) {
                    opStyle = ((SOAPOperation)obj).getStyle();
                    break;
                }
                if (!(obj instanceof UnknownExtensibilityElement) || !(name = (unkElement = (UnknownExtensibilityElement)obj).getElementType()).getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/soap12/") || !name.getLocalPart().equals("operation")) continue;
                opStyle = unkElement.getElement().getAttribute("style");
            }
            if (OperationType.NOTIFICATION.equals(type = (ptOperation = operation.getOperation()).getStyle()) || OperationType.SOLICIT_RESPONSE.equals(type)) {
                pw.println(parameters.signature);
                pw.println();
            }
            String operName = operation.getName();
            String indent = "        ";
            pw.println(indent + "oper = new org.apache.axis.description.OperationDesc();");
            pw.println(indent + "oper.setName(\"" + operName + "\");");
            for (int j = 0; j < parameters.list.size(); ++j) {
                Parameter p = (Parameter)parameters.list.get(j);
                QName paramType = Utils.getXSIType(p);
                String javaType = Utils.getParameterTypeName(p);
                javaType = javaType != null ? javaType + ".class, " : "null, ";
                String paramNameText = Utils.getNewQNameWithLastLocalPart(p.getQName());
                String paramTypeText = Utils.getNewQName(paramType);
                boolean isInHeader = p.isInHeader();
                boolean isOutHeader = p.isOutHeader();
                pw.println("        param = new org.apache.axis.description.ParameterDesc(" + paramNameText + ", " + modeStrings[p.getMode()] + ", " + paramTypeText + ", " + javaType + isInHeader + ", " + isOutHeader + ");");
                QName itemQName = Utils.getItemQName(p.getType());
                if (itemQName != null) {
                    pw.println("        param.setItemQName(" + Utils.getNewQName(itemQName) + ");");
                }
                pw.println("        oper.addParameter(param);");
            }
            Parameter returnParam = parameters.returnParam;
            if (returnParam != null) {
                QName itemQName;
                QName returnType = Utils.getXSIType(returnParam);
                String javaType = Utils.getParameterTypeName(returnParam);
                javaType = javaType == null ? "" : javaType + ".class";
                pw.println("        oper.setReturnType(" + Utils.getNewQName(returnType) + ");");
                pw.println("        oper.setReturnClass(" + javaType + ");");
                QName returnQName = returnParam.getQName();
                if (returnQName != null) {
                    pw.println("        oper.setReturnQName(" + Utils.getNewQNameWithLastLocalPart(returnQName) + ");");
                }
                if (returnParam.isOutHeader()) {
                    pw.println("        oper.setReturnHeader(true);");
                }
                if ((itemQName = Utils.getItemQName(returnParam.getType())) != null) {
                    pw.println("        param = oper.getReturnParamDesc();");
                    pw.println("        param.setItemQName(" + Utils.getNewQName(itemQName) + ");");
                }
            } else {
                pw.println("        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);");
            }
            boolean hasMIME = Utils.hasMIME(this.bEntry, operation);
            Style style = Style.getStyle(opStyle, this.bEntry.getBindingStyle());
            Use use = this.bEntry.getInputBodyType(operation.getOperation());
            if (style == Style.DOCUMENT && this.symbolTable.isWrapped()) {
                style = Style.WRAPPED;
            }
            if (!hasMIME) {
                pw.println("        oper.setStyle(" + styles.get(style) + ");");
                pw.println("        oper.setUse(" + uses.get(use) + ");");
            }
            this.writeFaultInfo(pw, operation);
            pw.println(indent + "_operations[" + i + "] = oper;");
            pw.println("");
        }
        pw.println("    }");
    }

    private HashSet getTypesInPortType(PortType portType) {
        HashSet<TypeEntry> types = new HashSet<TypeEntry>();
        HashSet firstPassTypes = new HashSet();
        List operations = portType.getOperations();
        for (int i = 0; i < operations.size(); ++i) {
            Operation op = (Operation)operations.get(i);
            firstPassTypes.addAll(this.getTypesInOperation(op));
        }
        Iterator i = firstPassTypes.iterator();
        while (i.hasNext()) {
            TypeEntry type = (TypeEntry)i.next();
            if (types.contains(type)) continue;
            types.add(type);
            types.addAll(type.getNestedTypes(this.symbolTable, true));
        }
        if (this.emitter.isAllWanted()) {
            HashMap rawSymbolTable = this.symbolTable.getHashMap();
            Iterator j = rawSymbolTable.values().iterator();
            while (j.hasNext()) {
                Vector typeVector = (Vector)j.next();
                Iterator k = typeVector.iterator();
                while (k.hasNext()) {
                    TypeEntry type;
                    Object symbol = k.next();
                    if (!(symbol instanceof DefinedType) || types.contains(type = (TypeEntry)symbol)) continue;
                    types.add(type);
                }
            }
        }
        return types;
    }

    private HashSet getTypesInOperation(Operation operation) {
        Map faults;
        HashSet types = new HashSet();
        Vector<TypeEntry> v = new Vector<TypeEntry>();
        Parameters params = this.bEntry.getParameters(operation);
        for (int i = 0; i < params.list.size(); ++i) {
            Parameter p = (Parameter)params.list.get(i);
            v.add(p.getType());
        }
        if (params.returnParam != null) {
            v.add(params.returnParam.getType());
        }
        if ((faults = operation.getFaults()) != null) {
            Iterator i = faults.values().iterator();
            while (i.hasNext()) {
                Fault f = (Fault)i.next();
                this.partTypes(v, f.getMessage().getOrderedParts(null));
            }
        }
        for (int i = 0; i < v.size(); ++i) {
            types.add(v.get(i));
        }
        return types;
    }

    private void partTypes(Vector v, Collection parts) {
        Iterator i = parts.iterator();
        while (i.hasNext()) {
            Part part = (Part)i.next();
            QName qType = part.getTypeName();
            if (qType != null) {
                v.add(this.symbolTable.getType(qType));
                continue;
            }
            qType = part.getElementName();
            if (qType == null) continue;
            v.add(this.symbolTable.getElement(qType));
        }
    }

    protected void writeFaultInfo(PrintWriter pw, BindingOperation bindOp) {
        HashMap faultMap = this.bEntry.getFaults();
        ArrayList faults = (ArrayList)faultMap.get(bindOp);
        if (faults == null) {
            return;
        }
        Iterator faultIt = faults.iterator();
        while (faultIt.hasNext()) {
            FaultInfo info = (FaultInfo)faultIt.next();
            QName qname = info.getQName();
            Message message = info.getMessage();
            if (qname == null) continue;
            String className = Utils.getFullExceptionName(message, this.symbolTable);
            pw.println("        oper.addFault(new org.apache.axis.description.FaultDesc(");
            pw.println("                      " + Utils.getNewQName(qname) + ",");
            pw.println("                      \"" + className + "\",");
            pw.println("                      " + Utils.getNewQName(info.getXMLType()) + ", ");
            pw.println("                      " + Utils.isFaultComplex(message, this.symbolTable));
            pw.println("                     ));");
        }
    }

    protected void writeSerializationDecls(PrintWriter pw, boolean hasMIME, String namespace) {
        pw.println("            java.lang.Class cls;");
        pw.println("            javax.xml.namespace.QName qName;");
        pw.println("            javax.xml.namespace.QName qName2;");
        pw.println("            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;");
        pw.println("            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;");
        pw.println("            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;");
        pw.println("            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;");
        pw.println("            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;");
        pw.println("            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;");
        pw.println("            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;");
        pw.println("            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;");
        pw.println("            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;");
        pw.println("            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;");
        if (hasMIME) {
            pw.println("            java.lang.Class mimesf = org.apache.axis.encoding.ser.JAFDataHandlerSerializerFactory.class;");
            pw.println("            java.lang.Class mimedf = org.apache.axis.encoding.ser.JAFDataHandlerDeserializerFactory.class;");
            pw.println();
            QName qname = new QName(namespace, "DataHandler");
            pw.println("            qName = new javax.xml.namespace.QName(\"" + qname.getNamespaceURI() + "\", \"" + qname.getLocalPart() + "\");");
            pw.println("            cachedSerQNames.add(qName);");
            pw.println("            cls = javax.activation.DataHandler.class;");
            pw.println("            cachedSerClasses.add(cls);");
            pw.println("            cachedSerFactories.add(mimesf);");
            pw.println("            cachedDeserFactories.add(mimedf);");
            pw.println();
        }
    }

    protected void writeSerializationInit(PrintWriter pw, TypeEntry type) {
        QName qname = type.getQName();
        pw.println("            qName = new javax.xml.namespace.QName(\"" + qname.getNamespaceURI() + "\", \"" + qname.getLocalPart() + "\");");
        pw.println("            cachedSerQNames.add(qName);");
        pw.println("            cls = " + type.getName() + ".class;");
        pw.println("            cachedSerClasses.add(cls);");
        if (type.getName().endsWith("[]")) {
            if (SchemaUtils.isListWithItemType(type.getNode())) {
                pw.println("            cachedSerFactories.add(simplelistsf);");
                pw.println("            cachedDeserFactories.add(simplelistdf);");
            } else if (type.getComponentType() != null) {
                QName ct = type.getComponentType();
                QName name = type.getItemQName();
                pw.println("            qName = new javax.xml.namespace.QName(\"" + ct.getNamespaceURI() + "\", \"" + ct.getLocalPart() + "\");");
                if (name != null) {
                    pw.println("            qName2 = new javax.xml.namespace.QName(\"" + name.getNamespaceURI() + "\", \"" + name.getLocalPart() + "\");");
                } else {
                    pw.println("            qName2 = null;");
                }
                pw.println("            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));");
                pw.println("            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());");
            } else {
                pw.println("            cachedSerFactories.add(arraysf);");
                pw.println("            cachedDeserFactories.add(arraydf);");
            }
        } else if (type.getNode() != null && Utils.getEnumerationBaseAndValues(type.getNode(), this.symbolTable) != null) {
            pw.println("            cachedSerFactories.add(enumsf);");
            pw.println("            cachedDeserFactories.add(enumdf);");
        } else if (type.isSimpleType()) {
            pw.println("            cachedSerFactories.add(simplesf);");
            pw.println("            cachedDeserFactories.add(simpledf);");
        } else if (type.getBaseType() != null) {
            pw.println("            cachedSerFactories.add(null);");
            pw.println("            cachedDeserFactories.add(simpledf);");
        } else {
            pw.println("            cachedSerFactories.add(beansf);");
            pw.println("            cachedDeserFactories.add(beandf);");
        }
        pw.println();
    }

    protected void writeOperation(PrintWriter pw, BindingOperation operation, Parameters parms, String soapAction, String opStyle, boolean oneway, int opIndex) {
        Style style;
        this.writeComment(pw, operation.getDocumentationElement(), true);
        pw.println(parms.signature + " {");
        pw.println("        if (super.cachedEndpoint == null) {");
        pw.println("            throw new org.apache.axis.NoEndPointException();");
        pw.println("        }");
        pw.println("        org.apache.axis.client.Call _call = createCall();");
        pw.println("        _call.setOperation(_operations[" + opIndex + "]);");
        if (soapAction != null) {
            pw.println("        _call.setUseSOAPAction(true);");
            pw.println("        _call.setSOAPActionURI(\"" + soapAction + "\");");
        }
        boolean hasMIME = Utils.hasMIME(this.bEntry, operation);
        Use use = this.bEntry.getInputBodyType(operation.getOperation());
        if (use == Use.LITERAL) {
            pw.println("        _call.setEncodingStyle(null);");
            pw.println("        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);");
        }
        if (hasMIME || use == Use.LITERAL) {
            pw.println("        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);");
        }
        if ((style = Style.getStyle(opStyle, this.bEntry.getBindingStyle())) == Style.DOCUMENT && this.symbolTable.isWrapped()) {
            style = Style.WRAPPED;
        }
        Iterator iterator = this.bEntry.getBinding().getExtensibilityElements().iterator();
        while (iterator.hasNext()) {
            UnknownExtensibilityElement unkElement;
            QName name;
            Object obj = iterator.next();
            if (obj instanceof SOAPBinding) {
                pw.println("        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);");
                continue;
            }
            if (!(obj instanceof UnknownExtensibilityElement) || !(name = (unkElement = (UnknownExtensibilityElement)obj).getElementType()).getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/soap12/") || !name.getLocalPart().equals("binding")) continue;
            pw.println("        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);");
        }
        if (style == Style.WRAPPED) {
            Map partsMap = operation.getOperation().getInput().getMessage().getParts();
            Iterator i = partsMap.values().iterator();
            if (i.hasNext()) {
                Part p = (Part)partsMap.values().iterator().next();
                QName q = p.getElementName();
                pw.println("        _call.setOperationName(" + Utils.getNewQName(q) + ");");
            } else {
                log.warn((Object)Messages.getMessage("missingPartsForMessage00", operation.getOperation().getInput().getMessage().getQName().toString()));
            }
        } else {
            QName elementQName = Utils.getOperationQName(operation, this.bEntry, this.symbolTable);
            if (elementQName != null) {
                pw.println("        _call.setOperationName(" + Utils.getNewQName(elementQName) + ");");
            }
        }
        pw.println();
        pw.println("        setRequestHeaders(_call);");
        pw.println("        setAttachments(_call);");
        if (this.bEntry.isOperationDIME(operation.getOperation().getName())) {
            pw.println("        _call.setProperty(_call.ATTACHMENT_ENCAPSULATION_FORMAT, _call.ATTACHMENT_ENCAPSULATION_FORMAT_DIME);");
        }
        if (oneway) {
            pw.print("        _call.invokeOneWay(");
        } else {
            pw.print(" try {");
            pw.print("        java.lang.Object _resp = _call.invoke(");
        }
        pw.print("new java.lang.Object[] {");
        this.writeParameters(pw, parms);
        pw.println("});");
        pw.println();
        if (!oneway) {
            this.writeResponseHandling(pw, parms);
        }
        pw.println("    }");
        pw.println();
    }

    protected void writeParameters(PrintWriter pw, Parameters parms) {
        boolean needComma = false;
        for (int i = 0; i < parms.list.size(); ++i) {
            Parameter p = (Parameter)parms.list.get(i);
            if (p.getMode() == 2) continue;
            if (needComma) {
                pw.print(", ");
            } else {
                needComma = true;
            }
            String javifiedName = Utils.xmlNameToJava(p.getName());
            if (p.getMode() != 1) {
                javifiedName = javifiedName + ".value";
            }
            if (p.getMIMEInfo() == null && !p.isOmittable()) {
                javifiedName = Utils.wrapPrimitiveType(p.getType(), javifiedName);
            }
            pw.print(javifiedName);
        }
    }

    protected void writeResponseHandling(PrintWriter pw, Parameters parms) {
        pw.println("        if (_resp instanceof java.rmi.RemoteException) {");
        pw.println("            throw (java.rmi.RemoteException)_resp;");
        pw.println("        }");
        int allOuts = parms.outputs + parms.inouts;
        if (allOuts > 0) {
            String qnameName;
            String javifiedName;
            Parameter p;
            int i;
            pw.println("        else {");
            pw.println("            extractAttachments(_call);");
            if (allOuts == 1) {
                if (parms.returnParam != null) {
                    this.writeOutputAssign(pw, "return ", parms.returnParam, "_resp");
                } else {
                    i = 0;
                    p = (Parameter)parms.list.get(i);
                    while (p.getMode() == 1) {
                        p = (Parameter)parms.list.get(++i);
                    }
                    javifiedName = Utils.xmlNameToJava(p.getName());
                    qnameName = Utils.getNewQNameWithLastLocalPart(p.getQName());
                    pw.println("            java.util.Map _output;");
                    pw.println("            _output = _call.getOutputParams();");
                    this.writeOutputAssign(pw, javifiedName + ".value = ", p, "_output.get(" + qnameName + ")");
                }
            } else {
                pw.println("            java.util.Map _output;");
                pw.println("            _output = _call.getOutputParams();");
                for (i = 0; i < parms.list.size(); ++i) {
                    p = (Parameter)parms.list.get(i);
                    javifiedName = Utils.xmlNameToJava(p.getName());
                    qnameName = Utils.getNewQNameWithLastLocalPart(p.getQName());
                    if (p.getMode() == 1) continue;
                    this.writeOutputAssign(pw, javifiedName + ".value = ", p, "_output.get(" + qnameName + ")");
                }
                if (parms.returnParam != null) {
                    this.writeOutputAssign(pw, "return ", parms.returnParam, "_resp");
                }
            }
            pw.println("        }");
        } else {
            pw.println("        extractAttachments(_call);");
        }
        Map faults = parms.faults;
        ArrayList<String> exceptionsThrowsList = new ArrayList<String>();
        int index = parms.signature.indexOf("throws");
        if (index != -1) {
            String[] thrExcep = StringUtils.split(parms.signature.substring(index + 6), ',');
            for (int i = 0; i < thrExcep.length; ++i) {
                exceptionsThrowsList.add(thrExcep[i].trim());
            }
        }
        pw.println("  } catch (org.apache.axis.AxisFault axisFaultException) {");
        if (faults != null && faults.size() > 0) {
            pw.println("    if (axisFaultException.detail != null) {");
            Iterator faultIt = exceptionsThrowsList.iterator();
            while (faultIt.hasNext()) {
                String exceptionFullName = (String)faultIt.next();
                pw.println("        if (axisFaultException.detail instanceof " + exceptionFullName + ") {");
                pw.println("              throw (" + exceptionFullName + ") axisFaultException.detail;");
                pw.println("         }");
            }
            pw.println("   }");
        }
        pw.println("  throw axisFaultException;");
        pw.println("}");
    }

    protected void writeOutputAssign(PrintWriter pw, String target, Parameter param, String source) {
        TypeEntry type = param.getType();
        if (type != null && type.getName() != null) {
            String typeName = type.getName();
            if (param.isOmittable() && param.getType().getDimensions().equals("") || param.getType().getUnderlTypeNillable()) {
                typeName = Utils.getWrapperType(type);
            }
            pw.println("            try {");
            pw.println("                " + target + Utils.getResponseString(param, source));
            pw.println("            } catch (java.lang.Exception _exception) {");
            pw.println("                " + target + Utils.getResponseString(param, "org.apache.axis.utils.JavaUtils.convert(" + source + ", " + typeName + ".class)"));
            pw.println("            }");
        } else {
            pw.println("              " + target + Utils.getResponseString(param, source));
        }
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
        styles.put(Style.DOCUMENT, "org.apache.axis.constants.Style.DOCUMENT");
        styles.put(Style.RPC, "org.apache.axis.constants.Style.RPC");
        styles.put(Style.MESSAGE, "org.apache.axis.constants.Style.MESSAGE");
        styles.put(Style.WRAPPED, "org.apache.axis.constants.Style.WRAPPED");
        uses.put(Use.ENCODED, "org.apache.axis.constants.Use.ENCODED");
        uses.put(Use.LITERAL, "org.apache.axis.constants.Use.LITERAL");
        OPERDESC_PER_BLOCK = 10;
    }
}

