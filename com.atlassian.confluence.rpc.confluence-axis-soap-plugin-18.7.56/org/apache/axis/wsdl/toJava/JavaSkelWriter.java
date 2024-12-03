/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.Binding
 *  javax.wsdl.BindingInput
 *  javax.wsdl.BindingOperation
 *  javax.wsdl.BindingOutput
 *  javax.wsdl.Operation
 *  javax.wsdl.OperationType
 *  javax.wsdl.extensions.UnknownExtensibilityElement
 *  javax.wsdl.extensions.soap.SOAPBody
 *  javax.wsdl.extensions.soap.SOAPOperation
 */
package org.apache.axis.wsdl.toJava;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.wsdl.Binding;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Operation;
import javax.wsdl.OperationType;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.xml.namespace.QName;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.FaultInfo;
import org.apache.axis.wsdl.symbolTable.Parameter;
import org.apache.axis.wsdl.symbolTable.Parameters;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.JavaBindingWriter;
import org.apache.axis.wsdl.toJava.JavaClassWriter;
import org.apache.axis.wsdl.toJava.Utils;

public class JavaSkelWriter
extends JavaClassWriter {
    private BindingEntry bEntry;
    private Binding binding;
    private SymbolTable symbolTable;

    protected JavaSkelWriter(Emitter emitter, BindingEntry bEntry, SymbolTable symbolTable) {
        super(emitter, bEntry.getName() + "Skeleton", "skeleton");
        this.bEntry = bEntry;
        this.binding = bEntry.getBinding();
        this.symbolTable = symbolTable;
    }

    protected String getImplementsText() {
        return "implements " + this.bEntry.getDynamicVar(JavaBindingWriter.INTERFACE_NAME) + ", org.apache.axis.wsdl.Skeleton ";
    }

    protected void writeFileBody(PrintWriter pw) throws IOException {
        Operation operation;
        String portTypeName = (String)this.bEntry.getDynamicVar(JavaBindingWriter.INTERFACE_NAME);
        String implType = portTypeName + " impl";
        pw.println("    private " + implType + ";");
        pw.println("    private static java.util.Map _myOperations = new java.util.Hashtable();");
        pw.println("    private static java.util.Collection _myOperationsList = new java.util.ArrayList();");
        pw.println();
        pw.println("    /**");
        pw.println("    * Returns List of OperationDesc objects with this name");
        pw.println("    */");
        pw.println("    public static java.util.List getOperationDescByName(java.lang.String methodName) {");
        pw.println("        return (java.util.List)_myOperations.get(methodName);");
        pw.println("    }");
        pw.println();
        pw.println("    /**");
        pw.println("    * Returns Collection of OperationDescs");
        pw.println("    */");
        pw.println("    public static java.util.Collection getOperationDescs() {");
        pw.println("        return _myOperationsList;");
        pw.println("    }");
        pw.println();
        pw.println("    static {");
        pw.println("        org.apache.axis.description.OperationDesc _oper;");
        pw.println("        org.apache.axis.description.FaultDesc _fault;");
        pw.println("        org.apache.axis.description.ParameterDesc [] _params;");
        List operations = this.binding.getBindingOperations();
        for (int i = 0; i < operations.size(); ++i) {
            ArrayList faults;
            BindingOperation bindingOper = (BindingOperation)operations.get(i);
            operation = bindingOper.getOperation();
            OperationType type = operation.getStyle();
            if (OperationType.NOTIFICATION.equals(type) || OperationType.SOLICIT_RESPONSE.equals(type)) continue;
            Parameters parameters = this.bEntry.getParameters(bindingOper.getOperation());
            if (parameters != null) {
                String action;
                QName elementQName;
                String opName = bindingOper.getOperation().getName();
                String javaOpName = Utils.xmlNameToJava(opName);
                pw.println("        _params = new org.apache.axis.description.ParameterDesc [] {");
                for (int j = 0; j < parameters.list.size(); ++j) {
                    String modeStr;
                    Parameter p = (Parameter)parameters.list.get(j);
                    switch (p.getMode()) {
                        case 1: {
                            modeStr = "org.apache.axis.description.ParameterDesc.IN";
                            break;
                        }
                        case 2: {
                            modeStr = "org.apache.axis.description.ParameterDesc.OUT";
                            break;
                        }
                        case 3: {
                            modeStr = "org.apache.axis.description.ParameterDesc.INOUT";
                            break;
                        }
                        default: {
                            throw new IOException(Messages.getMessage("badParmMode00", new Byte(p.getMode()).toString()));
                        }
                    }
                    QName paramName = p.getQName();
                    QName paramType = Utils.getXSIType(p);
                    String inHeader = p.isInHeader() ? "true" : "false";
                    String outHeader = p.isOutHeader() ? "true" : "false";
                    pw.println("            new org.apache.axis.description.ParameterDesc(" + Utils.getNewQNameWithLastLocalPart(paramName) + ", " + modeStr + ", " + Utils.getNewQName(paramType) + ", " + Utils.getParameterTypeName(p) + ".class" + ", " + inHeader + ", " + outHeader + "), ");
                }
                pw.println("        };");
                QName retName = null;
                QName retType = null;
                if (parameters.returnParam != null) {
                    retName = parameters.returnParam.getQName();
                    retType = Utils.getXSIType(parameters.returnParam);
                }
                String returnStr = retName != null ? Utils.getNewQNameWithLastLocalPart(retName) : "null";
                pw.println("        _oper = new org.apache.axis.description.OperationDesc(\"" + javaOpName + "\", _params, " + returnStr + ");");
                if (retType != null) {
                    pw.println("        _oper.setReturnType(" + Utils.getNewQName(retType) + ");");
                    if (parameters.returnParam != null && parameters.returnParam.isOutHeader()) {
                        pw.println("        _oper.setReturnHeader(true);");
                    }
                }
                if ((elementQName = Utils.getOperationQName(bindingOper, this.bEntry, this.symbolTable)) != null) {
                    pw.println("        _oper.setElementQName(" + Utils.getNewQName(elementQName) + ");");
                }
                if ((action = Utils.getOperationSOAPAction(bindingOper)) != null) {
                    pw.println("        _oper.setSoapAction(\"" + action + "\");");
                }
                pw.println("        _myOperationsList.add(_oper);");
                pw.println("        if (_myOperations.get(\"" + javaOpName + "\") == null) {");
                pw.println("            _myOperations.put(\"" + javaOpName + "\", new java.util.ArrayList());");
                pw.println("        }");
                pw.println("        ((java.util.List)_myOperations.get(\"" + javaOpName + "\")).add(_oper);");
            }
            if (this.bEntry.getFaults() == null || (faults = (ArrayList)this.bEntry.getFaults().get(bindingOper)) == null) continue;
            if (parameters == null) {
                String opName = bindingOper.getOperation().getName();
                String javaOpName = Utils.xmlNameToJava(opName);
                pw.println("        _oper = new org.apache.axis.description.OperationDesc();");
                pw.println("        _oper.setName(\"" + javaOpName + "\");");
            }
            Iterator it = faults.iterator();
            while (it.hasNext()) {
                FaultInfo faultInfo = (FaultInfo)it.next();
                QName faultQName = faultInfo.getQName();
                QName faultXMLType = faultInfo.getXMLType();
                String faultName = faultInfo.getName();
                String className = Utils.getFullExceptionName(faultInfo.getMessage(), this.symbolTable);
                pw.println("        _fault = new org.apache.axis.description.FaultDesc();");
                if (faultName != null) {
                    pw.println("        _fault.setName(\"" + faultName + "\");");
                }
                if (faultQName != null) {
                    pw.println("        _fault.setQName(" + Utils.getNewQName(faultQName) + ");");
                }
                if (className != null) {
                    pw.println("        _fault.setClassName(\"" + className + "\");");
                }
                if (faultXMLType != null) {
                    pw.println("        _fault.setXmlType(" + Utils.getNewQName(faultXMLType) + ");");
                }
                pw.println("        _oper.addFault(_fault);");
            }
        }
        pw.println("    }");
        pw.println();
        pw.println("    public " + this.className + "() {");
        String implementationClassName = this.emitter.getImplementationClassName();
        if (implementationClassName == null) {
            implementationClassName = this.bEntry.getName() + "Impl";
        }
        pw.println("        this.impl = new " + implementationClassName + "();");
        pw.println("    }");
        pw.println();
        pw.println("    public " + this.className + "(" + implType + ") {");
        pw.println("        this.impl = impl;");
        pw.println("    }");
        for (int i = 0; i < operations.size(); ++i) {
            Operation ptOperation;
            OperationType type;
            operation = (BindingOperation)operations.get(i);
            Parameters parameters = this.bEntry.getParameters(operation.getOperation());
            String soapAction = "";
            Iterator operationExtensibilityIterator = operation.getExtensibilityElements().iterator();
            while (operationExtensibilityIterator.hasNext()) {
                UnknownExtensibilityElement unkElement;
                QName name;
                Object obj = operationExtensibilityIterator.next();
                if (obj instanceof SOAPOperation) {
                    soapAction = ((SOAPOperation)obj).getSoapActionURI();
                    break;
                }
                if (!(obj instanceof UnknownExtensibilityElement) || !(name = (unkElement = (UnknownExtensibilityElement)obj).getElementType()).getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/soap12/") || !name.getLocalPart().equals("operation") || unkElement.getElement().getAttribute("soapAction") == null) continue;
                soapAction = unkElement.getElement().getAttribute("soapAction");
            }
            String namespace = "";
            Iterator bindingMsgIterator = null;
            BindingInput input = operation.getBindingInput();
            if (input != null) {
                bindingMsgIterator = input.getExtensibilityElements().iterator();
            } else {
                BindingOutput output = operation.getBindingOutput();
                if (output != null) {
                    bindingMsgIterator = output.getExtensibilityElements().iterator();
                }
            }
            if (bindingMsgIterator != null) {
                while (bindingMsgIterator.hasNext()) {
                    UnknownExtensibilityElement unkElement;
                    QName name;
                    Object obj = bindingMsgIterator.next();
                    if (obj instanceof SOAPBody) {
                        namespace = ((SOAPBody)obj).getNamespaceURI();
                        if (namespace == null) {
                            namespace = this.symbolTable.getDefinition().getTargetNamespace();
                        }
                        if (namespace != null) break;
                        namespace = "";
                        break;
                    }
                    if (!(obj instanceof UnknownExtensibilityElement) || !(name = (unkElement = (UnknownExtensibilityElement)obj).getElementType()).getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/soap12/") || !name.getLocalPart().equals("body")) continue;
                    namespace = unkElement.getElement().getAttribute("namespace");
                    if (namespace == null) {
                        namespace = this.symbolTable.getDefinition().getTargetNamespace();
                    }
                    if (namespace != null) break;
                    namespace = "";
                    break;
                }
            }
            if (OperationType.NOTIFICATION.equals(type = (ptOperation = operation.getOperation()).getStyle()) || OperationType.SOLICIT_RESPONSE.equals(type)) {
                pw.println(parameters.signature);
                pw.println();
                continue;
            }
            this.writeOperation(pw, (BindingOperation)operation, parameters, soapAction, namespace);
        }
    }

    protected void writeOperation(PrintWriter pw, BindingOperation operation, Parameters parms, String soapAction, String namespace) {
        this.writeComment(pw, operation.getDocumentationElement(), true);
        pw.println(parms.signature);
        pw.println("    {");
        if (parms.returnParam == null) {
            pw.print("        ");
        } else {
            pw.print("        " + Utils.getParameterTypeName(parms.returnParam) + " ret = ");
        }
        String call = "impl." + Utils.xmlNameToJava(operation.getName()) + "(";
        boolean needComma = false;
        for (int i = 0; i < parms.list.size(); ++i) {
            if (needComma) {
                call = call + ", ";
            } else {
                needComma = true;
            }
            Parameter p = (Parameter)parms.list.get(i);
            call = call + Utils.xmlNameToJava(p.getName());
        }
        call = call + ")";
        pw.println(call + ";");
        if (parms.returnParam != null) {
            pw.println("        return ret;");
        }
        pw.println("    }");
        pw.println();
    }
}

