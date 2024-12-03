/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.Binding
 *  javax.wsdl.BindingOperation
 *  javax.wsdl.Definition
 *  javax.wsdl.Operation
 *  javax.wsdl.OperationType
 *  javax.wsdl.Port
 *  javax.wsdl.Service
 *  javax.wsdl.extensions.UnknownExtensibilityElement
 *  javax.wsdl.extensions.soap.SOAPBinding
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.wsdl.toJava;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.OperationType;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.xml.namespace.QName;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.constants.Scope;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.FaultInfo;
import org.apache.axis.wsdl.symbolTable.Parameter;
import org.apache.axis.wsdl.symbolTable.Parameters;
import org.apache.axis.wsdl.symbolTable.SchemaUtils;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.TypeEntry;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.JavaWriter;
import org.apache.axis.wsdl.toJava.Utils;
import org.apache.commons.logging.Log;

public class JavaDeployWriter
extends JavaWriter {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$wsdl$toJava$JavaDeployWriter == null ? (class$org$apache$axis$wsdl$toJava$JavaDeployWriter = JavaDeployWriter.class$("org.apache.axis.wsdl.toJava.JavaDeployWriter")) : class$org$apache$axis$wsdl$toJava$JavaDeployWriter).getName());
    protected Definition definition;
    protected SymbolTable symbolTable;
    protected Emitter emitter;
    Use use = Use.DEFAULT;
    private static final Map mepStrings = new HashMap();
    static /* synthetic */ Class class$org$apache$axis$wsdl$toJava$JavaDeployWriter;

    public JavaDeployWriter(Emitter emitter, Definition definition, SymbolTable symbolTable) {
        super(emitter, "deploy");
        this.emitter = emitter;
        this.definition = definition;
        this.symbolTable = symbolTable;
    }

    public void generate() throws IOException {
        if (this.emitter.isServerSide()) {
            super.generate();
        }
    }

    protected String getFileName() {
        String dir = this.emitter.getNamespaces().getAsDir(this.definition.getTargetNamespace());
        return dir + "deploy.wsdd";
    }

    protected void writeFileHeader(PrintWriter pw) throws IOException {
        pw.println(Messages.getMessage("deploy00"));
        pw.println(Messages.getMessage("deploy02"));
        pw.println(Messages.getMessage("deploy03"));
        pw.println(Messages.getMessage("deploy05"));
        pw.println(Messages.getMessage("deploy06"));
        pw.println(Messages.getMessage("deploy07"));
        pw.println(Messages.getMessage("deploy09"));
        pw.println();
        pw.println("<deployment");
        pw.println("    xmlns=\"http://xml.apache.org/axis/wsdd/\"");
        pw.println("    xmlns:java=\"http://xml.apache.org/axis/wsdd/providers/java\">");
    }

    protected void writeFileBody(PrintWriter pw) throws IOException {
        this.writeDeployServices(pw);
        pw.println("</deployment>");
    }

    protected void writeDeployServices(PrintWriter pw) throws IOException {
        Map serviceMap = this.definition.getServices();
        Iterator mapIterator = serviceMap.values().iterator();
        while (mapIterator.hasNext()) {
            Service myService = (Service)mapIterator.next();
            pw.println();
            pw.println("  <!-- " + Messages.getMessage("wsdlService00", myService.getQName().getLocalPart()) + " -->");
            pw.println();
            Iterator portIterator = myService.getPorts().values().iterator();
            while (portIterator.hasNext()) {
                Port myPort = (Port)portIterator.next();
                BindingEntry bEntry = this.symbolTable.getBindingEntry(myPort.getBinding().getQName());
                if (bEntry.getBindingType() != 0) continue;
                this.writeDeployPort(pw, myPort, myService, bEntry);
            }
        }
    }

    protected void writeDeployTypes(PrintWriter pw, Binding binding, boolean hasLiteral, boolean hasMIME, Use use) throws IOException {
        pw.println();
        if (hasMIME) {
            QName bQName = binding.getQName();
            this.writeTypeMapping(pw, bQName.getNamespaceURI(), "DataHandler", "javax.activation.DataHandler", "org.apache.axis.encoding.ser.JAFDataHandlerSerializerFactory", "org.apache.axis.encoding.ser.JAFDataHandlerDeserializerFactory", use.getEncoding());
        }
        Map types = this.symbolTable.getTypeIndex();
        Collection typeCollection = types.values();
        Iterator i = typeCollection.iterator();
        while (i.hasNext()) {
            String deserializerFactory;
            String serializerFactory;
            TypeEntry type = (TypeEntry)i.next();
            boolean process = true;
            if (!Utils.shouldEmit(type)) {
                process = false;
            }
            if (!process) continue;
            String namespaceURI = type.getQName().getNamespaceURI();
            String localPart = type.getQName().getLocalPart();
            String javaType = type.getName();
            String encodingStyle = "";
            QName innerType = null;
            if (!hasLiteral) {
                encodingStyle = use.getEncoding();
            }
            if (javaType.endsWith("[]")) {
                if (SchemaUtils.isListWithItemType(type.getNode())) {
                    serializerFactory = "org.apache.axis.encoding.ser.SimpleListSerializerFactory";
                    deserializerFactory = "org.apache.axis.encoding.ser.SimpleListDeserializerFactory";
                } else {
                    serializerFactory = "org.apache.axis.encoding.ser.ArraySerializerFactory";
                    deserializerFactory = "org.apache.axis.encoding.ser.ArrayDeserializerFactory";
                    innerType = type.getComponentType();
                }
            } else if (type.getNode() != null && Utils.getEnumerationBaseAndValues(type.getNode(), this.symbolTable) != null) {
                serializerFactory = "org.apache.axis.encoding.ser.EnumSerializerFactory";
                deserializerFactory = "org.apache.axis.encoding.ser.EnumDeserializerFactory";
            } else if (type.isSimpleType()) {
                serializerFactory = "org.apache.axis.encoding.ser.SimpleSerializerFactory";
                deserializerFactory = "org.apache.axis.encoding.ser.SimpleDeserializerFactory";
            } else if (type.getBaseType() != null) {
                serializerFactory = "org.apache.axis.encoding.ser.SimpleSerializerFactory";
                deserializerFactory = "org.apache.axis.encoding.ser.SimpleDeserializerFactory";
            } else {
                serializerFactory = "org.apache.axis.encoding.ser.BeanSerializerFactory";
                deserializerFactory = "org.apache.axis.encoding.ser.BeanDeserializerFactory";
            }
            if (innerType == null) {
                this.writeTypeMapping(pw, namespaceURI, localPart, javaType, serializerFactory, deserializerFactory, encodingStyle);
                continue;
            }
            this.writeArrayTypeMapping(pw, namespaceURI, localPart, javaType, encodingStyle, innerType);
        }
    }

    protected void writeArrayTypeMapping(PrintWriter pw, String namespaceURI, String localPart, String javaType, String encodingStyle, QName innerType) throws IOException {
        pw.println("      <arrayMapping");
        pw.println("        xmlns:ns=\"" + namespaceURI + "\"");
        pw.println("        qname=\"ns:" + localPart + '\"');
        pw.println("        type=\"java:" + javaType + '\"');
        pw.println("        innerType=\"" + Utils.genQNameAttributeString(innerType, "cmp-ns") + '\"');
        pw.println("        encodingStyle=\"" + encodingStyle + "\"");
        pw.println("      />");
    }

    protected void writeTypeMapping(PrintWriter pw, String namespaceURI, String localPart, String javaType, String serializerFactory, String deserializerFactory, String encodingStyle) throws IOException {
        pw.println("      <typeMapping");
        pw.println("        xmlns:ns=\"" + namespaceURI + "\"");
        pw.println("        qname=\"ns:" + localPart + '\"');
        pw.println("        type=\"java:" + javaType + '\"');
        pw.println("        serializer=\"" + serializerFactory + "\"");
        pw.println("        deserializer=\"" + deserializerFactory + "\"");
        pw.println("        encodingStyle=\"" + encodingStyle + "\"");
        pw.println("      />");
    }

    protected void writeDeployPort(PrintWriter pw, Port port, Service service, BindingEntry bEntry) throws IOException {
        String serviceName = port.getName();
        boolean hasLiteral = bEntry.hasLiteral();
        boolean hasMIME = Utils.hasMIME(bEntry);
        String prefix = "java";
        String styleStr = "";
        Iterator iterator = bEntry.getBinding().getExtensibilityElements().iterator();
        while (iterator.hasNext()) {
            UnknownExtensibilityElement unkElement;
            QName name;
            Object obj = iterator.next();
            if (obj instanceof SOAPBinding) {
                this.use = Use.ENCODED;
                continue;
            }
            if (!(obj instanceof UnknownExtensibilityElement) || !(name = (unkElement = (UnknownExtensibilityElement)obj).getElementType()).getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/soap12/") || !name.getLocalPart().equals("binding")) continue;
            this.use = Use.ENCODED;
        }
        if (this.symbolTable.isWrapped()) {
            styleStr = " style=\"" + Style.WRAPPED + "\"";
            this.use = Use.LITERAL;
        } else {
            styleStr = " style=\"" + bEntry.getBindingStyle().getName() + "\"";
            if (hasLiteral) {
                this.use = Use.LITERAL;
            }
        }
        String useStr = " use=\"" + this.use + "\"";
        pw.println("  <service name=\"" + serviceName + "\" provider=\"" + prefix + ":RPC" + "\"" + styleStr + useStr + ">");
        pw.println("      <parameter name=\"wsdlTargetNamespace\" value=\"" + service.getQName().getNamespaceURI() + "\"/>");
        pw.println("      <parameter name=\"wsdlServiceElement\" value=\"" + service.getQName().getLocalPart() + "\"/>");
        if (hasMIME) {
            pw.println("      <parameter name=\"sendMultiRefs\" value=\"false\"/>");
        }
        ArrayList qualified = new ArrayList();
        ArrayList unqualified = new ArrayList();
        Map elementFormDefaults = this.symbolTable.getElementFormDefaults();
        Iterator it = elementFormDefaults.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = it.next();
            if (entry.getValue().equals("qualified")) {
                qualified.add(entry.getKey());
                continue;
            }
            unqualified.add(entry.getKey());
        }
        if (qualified.size() > 0) {
            pw.print("      <parameter name=\"schemaQualified\" value=\"");
            for (int i = 0; i < qualified.size(); ++i) {
                pw.print(qualified.get(i));
                if (i == qualified.size() - 1) continue;
                pw.print(',');
            }
            pw.println("\"/>");
        }
        if (unqualified.size() > 0) {
            pw.print("      <parameter name=\"schemaUnqualified\" value=\"");
            for (int i = 0; i < unqualified.size(); ++i) {
                pw.print(unqualified.get(i));
                if (i == unqualified.size() - 1) continue;
                pw.print(',');
            }
            pw.println("\"/>");
        }
        pw.println("      <parameter name=\"wsdlServicePort\" value=\"" + serviceName + "\"/>");
        this.writeDeployBinding(pw, bEntry);
        this.writeDeployTypes(pw, bEntry.getBinding(), hasLiteral, hasMIME, this.use);
        pw.println("  </service>");
    }

    protected void writeDeployBinding(PrintWriter pw, BindingEntry bEntry) throws IOException {
        String customClassName;
        Binding binding = bEntry.getBinding();
        String className = bEntry.getName();
        className = this.emitter.isSkeletonWanted() ? className + "Skeleton" : ((customClassName = this.emitter.getImplementationClassName()) != null ? customClassName : className + "Impl");
        pw.println("      <parameter name=\"className\" value=\"" + className + "\"/>");
        pw.println("      <parameter name=\"wsdlPortType\" value=\"" + binding.getPortType().getQName().getLocalPart() + "\"/>");
        pw.println("      <parameter name=\"typeMappingVersion\" value=\"" + this.emitter.getTypeMappingVersion() + "\"/>");
        HashSet<String> allowedMethods = new HashSet<String>();
        String namespaceURI = binding.getQName().getNamespaceURI();
        if (!this.emitter.isSkeletonWanted()) {
            Iterator operationsIterator = binding.getBindingOperations().iterator();
            while (operationsIterator.hasNext()) {
                BindingOperation bindingOper = (BindingOperation)operationsIterator.next();
                Operation operation = bindingOper.getOperation();
                OperationType type = operation.getStyle();
                if (OperationType.NOTIFICATION.equals(type) || OperationType.SOLICIT_RESPONSE.equals(type)) continue;
                String javaOperName = null;
                ServiceDesc serviceDesc = this.emitter.getServiceDesc();
                if (this.emitter.isDeploy() && serviceDesc != null) {
                    OperationDesc[] operDescs = serviceDesc.getOperationsByQName(new QName(namespaceURI, operation.getName()));
                    if (operDescs.length == 0) {
                        log.warn((Object)("Can't find operation in the Java Class for WSDL binding operation : " + operation.getName()));
                        continue;
                    }
                    OperationDesc operDesc = operDescs[0];
                    if (operDesc.getMethod() == null) {
                        log.warn((Object)("Can't find Java method for operation descriptor : " + operDesc.getName()));
                        continue;
                    }
                    javaOperName = operDesc.getMethod().getName();
                } else {
                    javaOperName = JavaUtils.xmlNameToJava(operation.getName());
                }
                allowedMethods.add(javaOperName);
                Parameters params = this.symbolTable.getOperationParameters(operation, "", bEntry);
                if (params == null) continue;
                QName elementQName = Utils.getOperationQName(bindingOper, bEntry, this.symbolTable);
                QName returnQName = null;
                QName returnType = null;
                if (params.returnParam != null) {
                    returnQName = params.returnParam.getQName();
                    returnType = Utils.getXSIType(params.returnParam);
                }
                HashMap faultMap = bEntry.getFaults();
                ArrayList faults = null;
                if (faultMap != null) {
                    faults = (ArrayList)faultMap.get(bindingOper);
                }
                String SOAPAction = Utils.getOperationSOAPAction(bindingOper);
                this.writeOperation(pw, javaOperName, elementQName, returnQName, returnType, params, binding.getQName(), faults, SOAPAction);
            }
        }
        pw.print("      <parameter name=\"allowedMethods\" value=\"");
        if (allowedMethods.isEmpty()) {
            pw.println("*\"/>");
        } else {
            boolean first = true;
            Iterator i = allowedMethods.iterator();
            while (i.hasNext()) {
                String method = (String)i.next();
                if (first) {
                    pw.print(method);
                    first = false;
                    continue;
                }
                pw.print(" " + method);
            }
            pw.println("\"/>");
        }
        Scope scope = this.emitter.getScope();
        if (scope != null) {
            pw.println("      <parameter name=\"scope\" value=\"" + scope.getName() + "\"/>");
        }
    }

    protected void writeOperation(PrintWriter pw, String javaOperName, QName elementQName, QName returnQName, QName returnType, Parameters params, QName bindingQName, ArrayList faults, String SOAPAction) {
        String mepString;
        Parameter retParam;
        pw.print("      <operation name=\"" + javaOperName + "\"");
        if (elementQName != null) {
            pw.print(" qname=\"" + Utils.genQNameAttributeString(elementQName, "operNS") + "\"");
        }
        if (returnQName != null) {
            pw.print(" returnQName=\"" + Utils.genQNameAttributeStringWithLastLocalPart(returnQName, "retNS") + "\"");
        }
        if (returnType != null) {
            pw.print(" returnType=\"" + Utils.genQNameAttributeString(returnType, "rtns") + "\"");
        }
        if ((retParam = params.returnParam) != null) {
            QName returnItemType;
            TypeEntry type = retParam.getType();
            QName returnItemQName = Utils.getItemQName(type);
            if (returnItemQName != null) {
                pw.print(" returnItemQName=\"");
                pw.print(Utils.genQNameAttributeString(returnItemQName, "tns"));
                pw.print("\"");
            }
            if ((returnItemType = Utils.getItemType(type)) != null && this.use == Use.ENCODED) {
                pw.print(" returnItemType=\"");
                pw.print(Utils.genQNameAttributeString(returnItemType, "tns2"));
                pw.print("\"");
            }
        }
        if (SOAPAction != null) {
            pw.print(" soapAction=\"" + SOAPAction + "\"");
        }
        if (!OperationType.REQUEST_RESPONSE.equals(params.mep) && (mepString = this.getMepString(params.mep)) != null) {
            pw.print(" mep=\"" + mepString + "\"");
        }
        if (params.returnParam != null && params.returnParam.isOutHeader()) {
            pw.print(" returnHeader=\"true\"");
        }
        pw.println(" >");
        Vector paramList = params.list;
        for (int i = 0; i < paramList.size(); ++i) {
            QName itemQName;
            Parameter param = (Parameter)paramList.elementAt(i);
            QName paramQName = param.getQName();
            QName paramType = Utils.getXSIType(param);
            pw.print("        <parameter");
            if (paramQName == null) {
                pw.print(" name=\"" + param.getName() + "\"");
            } else {
                pw.print(" qname=\"" + Utils.genQNameAttributeStringWithLastLocalPart(paramQName, "pns") + "\"");
            }
            pw.print(" type=\"" + Utils.genQNameAttributeString(paramType, "tns") + "\"");
            if (param.getMode() != 1) {
                pw.print(" mode=\"" + this.getModeString(param.getMode()) + "\"");
            }
            if (param.isInHeader()) {
                pw.print(" inHeader=\"true\"");
            }
            if (param.isOutHeader()) {
                pw.print(" outHeader=\"true\"");
            }
            if ((itemQName = Utils.getItemQName(param.getType())) != null) {
                pw.print(" itemQName=\"");
                pw.print(Utils.genQNameAttributeString(itemQName, "itns"));
                pw.print("\"");
            }
            pw.println("/>");
        }
        if (faults != null) {
            Iterator iterator = faults.iterator();
            while (iterator.hasNext()) {
                FaultInfo faultInfo = (FaultInfo)iterator.next();
                QName faultQName = faultInfo.getQName();
                if (faultQName == null) continue;
                String className = Utils.getFullExceptionName(faultInfo.getMessage(), this.symbolTable);
                pw.print("        <fault");
                pw.print(" name=\"" + faultInfo.getName() + "\"");
                pw.print(" qname=\"" + Utils.genQNameAttributeString(faultQName, "fns") + "\"");
                pw.print(" class=\"" + className + "\"");
                pw.print(" type=\"" + Utils.genQNameAttributeString(faultInfo.getXMLType(), "tns") + "\"");
                pw.println("/>");
            }
        }
        pw.println("      </operation>");
    }

    public String getModeString(byte mode) {
        if (mode == 1) {
            return "IN";
        }
        if (mode == 3) {
            return "INOUT";
        }
        return "OUT";
    }

    protected PrintWriter getPrintWriter(String filename) throws IOException {
        File file = new File(filename);
        File parent = new File(file.getParent());
        parent.mkdirs();
        FileOutputStream out = new FileOutputStream(file);
        OutputStreamWriter writer = new OutputStreamWriter((OutputStream)out, "UTF-8");
        return new PrintWriter(writer);
    }

    String getMepString(OperationType mep) {
        return (String)mepStrings.get(mep.toString());
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
        mepStrings.put(OperationType.REQUEST_RESPONSE.toString(), "request-response");
        mepStrings.put(OperationType.ONE_WAY.toString(), "oneway");
    }
}

