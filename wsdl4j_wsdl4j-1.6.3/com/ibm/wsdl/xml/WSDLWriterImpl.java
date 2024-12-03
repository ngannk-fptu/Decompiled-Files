/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.xml;

import com.ibm.wsdl.util.StringUtils;
import com.ibm.wsdl.util.xml.DOM2Writer;
import com.ibm.wsdl.util.xml.DOMUtils;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import javax.wsdl.OperationType;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.AttributeExtensible;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.ExtensionSerializer;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class WSDLWriterImpl
implements WSDLWriter {
    public void setFeature(String name, boolean value) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("Feature name must not be null.");
        }
        throw new IllegalArgumentException("Feature name '" + name + "' not recognized.");
    }

    public boolean getFeature(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("Feature name must not be null.");
        }
        throw new IllegalArgumentException("Feature name '" + name + "' not recognized.");
    }

    protected void printDefinition(Definition def, PrintWriter pw) throws WSDLException {
        if (def == null) {
            return;
        }
        if (def.getPrefix("http://schemas.xmlsoap.org/wsdl/") == null) {
            String prefix = "wsdl";
            int subscript = 0;
            while (def.getNamespace(prefix) != null) {
                prefix = "wsdl" + subscript++;
            }
            def.addNamespace(prefix, "http://schemas.xmlsoap.org/wsdl/");
        }
        String tagName = DOMUtils.getQualifiedValue("http://schemas.xmlsoap.org/wsdl/", "definitions", def);
        pw.print('<' + tagName);
        QName name = def.getQName();
        String targetNamespace = def.getTargetNamespace();
        Map namespaces = def.getNamespaces();
        if (name != null) {
            DOMUtils.printAttribute("name", name.getLocalPart(), pw);
        }
        DOMUtils.printAttribute("targetNamespace", targetNamespace, pw);
        this.printExtensibilityAttributes(Definition.class, def, def, pw);
        this.printNamespaceDeclarations(namespaces, pw);
        pw.println('>');
        this.printDocumentation(def.getDocumentationElement(), def, pw);
        this.printImports(def.getImports(), def, pw);
        this.printTypes(def.getTypes(), def, pw);
        this.printMessages(def.getMessages(), def, pw);
        this.printPortTypes(def.getPortTypes(), def, pw);
        this.printBindings(def.getBindings(), def, pw);
        this.printServices(def.getServices(), def, pw);
        List extElements = def.getExtensibilityElements();
        this.printExtensibilityElements(Definition.class, extElements, def, pw);
        pw.println("</" + tagName + '>');
        pw.flush();
    }

    protected void printServices(Map services, Definition def, PrintWriter pw) throws WSDLException {
        if (services != null) {
            String tagName = DOMUtils.getQualifiedValue("http://schemas.xmlsoap.org/wsdl/", "service", def);
            for (Service service : services.values()) {
                pw.print("  <" + tagName);
                QName name = service.getQName();
                if (name != null) {
                    DOMUtils.printAttribute("name", name.getLocalPart(), pw);
                }
                this.printExtensibilityAttributes(Service.class, service, def, pw);
                pw.println('>');
                this.printDocumentation(service.getDocumentationElement(), def, pw);
                this.printPorts(service.getPorts(), def, pw);
                List extElements = service.getExtensibilityElements();
                this.printExtensibilityElements(Service.class, extElements, def, pw);
                pw.println("  </" + tagName + '>');
            }
        }
    }

    protected void printPorts(Map ports, Definition def, PrintWriter pw) throws WSDLException {
        if (ports != null) {
            String tagName = DOMUtils.getQualifiedValue("http://schemas.xmlsoap.org/wsdl/", "port", def);
            for (Port port : ports.values()) {
                pw.print("    <" + tagName);
                DOMUtils.printAttribute("name", port.getName(), pw);
                Binding binding = port.getBinding();
                if (binding != null) {
                    DOMUtils.printQualifiedAttribute("binding", binding.getQName(), def, pw);
                }
                this.printExtensibilityAttributes(Port.class, port, def, pw);
                pw.println('>');
                this.printDocumentation(port.getDocumentationElement(), def, pw);
                List extElements = port.getExtensibilityElements();
                this.printExtensibilityElements(Port.class, extElements, def, pw);
                pw.println("    </" + tagName + '>');
            }
        }
    }

    protected void printBindings(Map bindings, Definition def, PrintWriter pw) throws WSDLException {
        if (bindings != null) {
            String tagName = DOMUtils.getQualifiedValue("http://schemas.xmlsoap.org/wsdl/", "binding", def);
            for (Binding binding : bindings.values()) {
                PortType portType;
                if (binding.isUndefined()) continue;
                pw.print("  <" + tagName);
                QName name = binding.getQName();
                if (name != null) {
                    DOMUtils.printAttribute("name", name.getLocalPart(), pw);
                }
                if ((portType = binding.getPortType()) != null) {
                    DOMUtils.printQualifiedAttribute("type", portType.getQName(), def, pw);
                }
                pw.println('>');
                this.printDocumentation(binding.getDocumentationElement(), def, pw);
                List extElements = binding.getExtensibilityElements();
                this.printExtensibilityElements(Binding.class, extElements, def, pw);
                this.printBindingOperations(binding.getBindingOperations(), def, pw);
                pw.println("  </" + tagName + '>');
            }
        }
    }

    protected void printBindingOperations(List bindingOperations, Definition def, PrintWriter pw) throws WSDLException {
        if (bindingOperations != null) {
            String tagName = DOMUtils.getQualifiedValue("http://schemas.xmlsoap.org/wsdl/", "operation", def);
            for (BindingOperation bindingOperation : bindingOperations) {
                pw.print("    <" + tagName);
                DOMUtils.printAttribute("name", bindingOperation.getName(), pw);
                this.printExtensibilityAttributes(BindingOperation.class, bindingOperation, def, pw);
                pw.println('>');
                this.printDocumentation(bindingOperation.getDocumentationElement(), def, pw);
                List extElements = bindingOperation.getExtensibilityElements();
                this.printExtensibilityElements(BindingOperation.class, extElements, def, pw);
                this.printBindingInput(bindingOperation.getBindingInput(), def, pw);
                this.printBindingOutput(bindingOperation.getBindingOutput(), def, pw);
                this.printBindingFaults(bindingOperation.getBindingFaults(), def, pw);
                pw.println("    </" + tagName + '>');
            }
        }
    }

    protected void printBindingInput(BindingInput bindingInput, Definition def, PrintWriter pw) throws WSDLException {
        if (bindingInput != null) {
            String tagName = DOMUtils.getQualifiedValue("http://schemas.xmlsoap.org/wsdl/", "input", def);
            pw.print("      <" + tagName);
            DOMUtils.printAttribute("name", bindingInput.getName(), pw);
            this.printExtensibilityAttributes(BindingInput.class, bindingInput, def, pw);
            pw.println('>');
            this.printDocumentation(bindingInput.getDocumentationElement(), def, pw);
            List extElements = bindingInput.getExtensibilityElements();
            this.printExtensibilityElements(BindingInput.class, extElements, def, pw);
            pw.println("      </" + tagName + '>');
        }
    }

    protected void printBindingOutput(BindingOutput bindingOutput, Definition def, PrintWriter pw) throws WSDLException {
        if (bindingOutput != null) {
            String tagName = DOMUtils.getQualifiedValue("http://schemas.xmlsoap.org/wsdl/", "output", def);
            pw.print("      <" + tagName);
            DOMUtils.printAttribute("name", bindingOutput.getName(), pw);
            pw.println('>');
            this.printDocumentation(bindingOutput.getDocumentationElement(), def, pw);
            List extElements = bindingOutput.getExtensibilityElements();
            this.printExtensibilityElements(BindingOutput.class, extElements, def, pw);
            pw.println("      </" + tagName + '>');
        }
    }

    protected void printBindingFaults(Map bindingFaults, Definition def, PrintWriter pw) throws WSDLException {
        if (bindingFaults != null) {
            String tagName = DOMUtils.getQualifiedValue("http://schemas.xmlsoap.org/wsdl/", "fault", def);
            for (BindingFault bindingFault : bindingFaults.values()) {
                pw.print("      <" + tagName);
                DOMUtils.printAttribute("name", bindingFault.getName(), pw);
                this.printExtensibilityAttributes(BindingFault.class, bindingFault, def, pw);
                pw.println('>');
                this.printDocumentation(bindingFault.getDocumentationElement(), def, pw);
                List extElements = bindingFault.getExtensibilityElements();
                this.printExtensibilityElements(BindingFault.class, extElements, def, pw);
                pw.println("      </" + tagName + '>');
            }
        }
    }

    protected void printPortTypes(Map portTypes, Definition def, PrintWriter pw) throws WSDLException {
        if (portTypes != null) {
            String tagName = DOMUtils.getQualifiedValue("http://schemas.xmlsoap.org/wsdl/", "portType", def);
            for (PortType portType : portTypes.values()) {
                if (portType.isUndefined()) continue;
                pw.print("  <" + tagName);
                QName name = portType.getQName();
                if (name != null) {
                    DOMUtils.printAttribute("name", name.getLocalPart(), pw);
                }
                this.printExtensibilityAttributes(PortType.class, portType, def, pw);
                pw.println('>');
                this.printDocumentation(portType.getDocumentationElement(), def, pw);
                this.printOperations(portType.getOperations(), def, pw);
                List extElements = portType.getExtensibilityElements();
                this.printExtensibilityElements(PortType.class, extElements, def, pw);
                pw.println("  </" + tagName + '>');
            }
        }
    }

    protected void printOperations(List operations, Definition def, PrintWriter pw) throws WSDLException {
        if (operations != null) {
            String tagName = DOMUtils.getQualifiedValue("http://schemas.xmlsoap.org/wsdl/", "operation", def);
            for (Operation operation : operations) {
                if (operation.isUndefined()) continue;
                pw.print("    <" + tagName);
                DOMUtils.printAttribute("name", operation.getName(), pw);
                DOMUtils.printAttribute("parameterOrder", StringUtils.getNMTokens(operation.getParameterOrdering()), pw);
                this.printExtensibilityAttributes(Operation.class, operation, def, pw);
                pw.println('>');
                this.printDocumentation(operation.getDocumentationElement(), def, pw);
                OperationType operationType = operation.getStyle();
                if (operationType == OperationType.ONE_WAY) {
                    this.printInput(operation.getInput(), def, pw);
                } else if (operationType == OperationType.SOLICIT_RESPONSE) {
                    this.printOutput(operation.getOutput(), def, pw);
                    this.printInput(operation.getInput(), def, pw);
                } else if (operationType == OperationType.NOTIFICATION) {
                    this.printOutput(operation.getOutput(), def, pw);
                } else {
                    this.printInput(operation.getInput(), def, pw);
                    this.printOutput(operation.getOutput(), def, pw);
                }
                this.printFaults(operation.getFaults(), def, pw);
                List extElements = operation.getExtensibilityElements();
                this.printExtensibilityElements(Operation.class, extElements, def, pw);
                pw.println("    </" + tagName + '>');
            }
        }
    }

    protected void printInput(Input input, Definition def, PrintWriter pw) throws WSDLException {
        if (input != null) {
            String tagName = DOMUtils.getQualifiedValue("http://schemas.xmlsoap.org/wsdl/", "input", def);
            pw.print("      <" + tagName);
            DOMUtils.printAttribute("name", input.getName(), pw);
            Message message = input.getMessage();
            if (message != null) {
                DOMUtils.printQualifiedAttribute("message", message.getQName(), def, pw);
            }
            this.printExtensibilityAttributes(Input.class, input, def, pw);
            pw.println('>');
            this.printDocumentation(input.getDocumentationElement(), def, pw);
            List extElements = input.getExtensibilityElements();
            this.printExtensibilityElements(Input.class, extElements, def, pw);
            pw.println("    </" + tagName + '>');
        }
    }

    protected void printOutput(Output output, Definition def, PrintWriter pw) throws WSDLException {
        if (output != null) {
            String tagName = DOMUtils.getQualifiedValue("http://schemas.xmlsoap.org/wsdl/", "output", def);
            pw.print("      <" + tagName);
            DOMUtils.printAttribute("name", output.getName(), pw);
            Message message = output.getMessage();
            if (message != null) {
                DOMUtils.printQualifiedAttribute("message", message.getQName(), def, pw);
            }
            this.printExtensibilityAttributes(Output.class, output, def, pw);
            pw.println('>');
            this.printDocumentation(output.getDocumentationElement(), def, pw);
            List extElements = output.getExtensibilityElements();
            this.printExtensibilityElements(Output.class, extElements, def, pw);
            pw.println("    </" + tagName + '>');
        }
    }

    protected void printFaults(Map faults, Definition def, PrintWriter pw) throws WSDLException {
        if (faults != null) {
            String tagName = DOMUtils.getQualifiedValue("http://schemas.xmlsoap.org/wsdl/", "fault", def);
            for (Fault fault : faults.values()) {
                pw.print("      <" + tagName);
                DOMUtils.printAttribute("name", fault.getName(), pw);
                Message message = fault.getMessage();
                if (message != null) {
                    DOMUtils.printQualifiedAttribute("message", message.getQName(), def, pw);
                }
                this.printExtensibilityAttributes(Fault.class, fault, def, pw);
                pw.println('>');
                this.printDocumentation(fault.getDocumentationElement(), def, pw);
                List extElements = fault.getExtensibilityElements();
                this.printExtensibilityElements(Fault.class, extElements, def, pw);
                pw.println("    </" + tagName + '>');
            }
        }
    }

    protected void printMessages(Map messages, Definition def, PrintWriter pw) throws WSDLException {
        if (messages != null) {
            String tagName = DOMUtils.getQualifiedValue("http://schemas.xmlsoap.org/wsdl/", "message", def);
            for (Message message : messages.values()) {
                if (message.isUndefined()) continue;
                pw.print("  <" + tagName);
                QName name = message.getQName();
                if (name != null) {
                    DOMUtils.printAttribute("name", name.getLocalPart(), pw);
                }
                this.printExtensibilityAttributes(Message.class, message, def, pw);
                pw.println('>');
                this.printDocumentation(message.getDocumentationElement(), def, pw);
                this.printParts(message.getOrderedParts(null), def, pw);
                List extElements = message.getExtensibilityElements();
                this.printExtensibilityElements(Message.class, extElements, def, pw);
                pw.println("  </" + tagName + '>');
            }
        }
    }

    protected void printParts(List parts, Definition def, PrintWriter pw) throws WSDLException {
        if (parts != null) {
            String tagName = DOMUtils.getQualifiedValue("http://schemas.xmlsoap.org/wsdl/", "part", def);
            for (Part part : parts) {
                pw.print("    <" + tagName);
                DOMUtils.printAttribute("name", part.getName(), pw);
                DOMUtils.printQualifiedAttribute("element", part.getElementName(), def, pw);
                DOMUtils.printQualifiedAttribute("type", part.getTypeName(), def, pw);
                this.printExtensibilityAttributes(Part.class, part, def, pw);
                pw.println('>');
                this.printDocumentation(part.getDocumentationElement(), def, pw);
                List extElements = part.getExtensibilityElements();
                this.printExtensibilityElements(Part.class, extElements, def, pw);
                pw.println("    </" + tagName + '>');
            }
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected void printExtensibilityAttributes(Class parentType, AttributeExtensible attrExt, Definition def, PrintWriter pw) throws WSDLException {
        Map extensionAttributes = attrExt.getExtensionAttributes();
        for (QName attrName : extensionAttributes.keySet()) {
            Object attrValue = extensionAttributes.get(attrName);
            String attrStrValue = null;
            QName attrQNameValue = null;
            if (attrValue instanceof String) {
                attrStrValue = (String)attrValue;
            } else if (attrValue instanceof QName) {
                attrQNameValue = (QName)attrValue;
            } else {
                if (!(attrValue instanceof List)) throw new WSDLException("CONFIGURATION_ERROR", "Unknown type of extension attribute '" + attrName + "': " + attrValue.getClass().getName());
                List attrValueList = (List)attrValue;
                int size = attrValueList.size();
                if (size > 0) {
                    Object tempAttrVal = attrValueList.get(0);
                    if (tempAttrVal instanceof String) {
                        attrStrValue = StringUtils.getNMTokens(attrValueList);
                    } else {
                        if (!(tempAttrVal instanceof QName)) throw new WSDLException("CONFIGURATION_ERROR", "Unknown type of extension attribute '" + attrName + "': " + tempAttrVal.getClass().getName());
                        StringBuffer strBuf = new StringBuffer();
                        for (int i = 0; i < size; ++i) {
                            QName tempQName = (QName)attrValueList.get(i);
                            strBuf.append((i > 0 ? " " : "") + DOMUtils.getQualifiedValue(tempQName.getNamespaceURI(), tempQName.getLocalPart(), def));
                        }
                        attrStrValue = strBuf.toString();
                    }
                } else {
                    attrStrValue = "";
                }
            }
            if (attrQNameValue != null) {
                DOMUtils.printQualifiedAttribute(attrName, attrQNameValue, def, pw);
                continue;
            }
            DOMUtils.printQualifiedAttribute(attrName, attrStrValue, def, pw);
        }
    }

    protected void printDocumentation(Element docElement, Definition def, PrintWriter pw) throws WSDLException {
        if (docElement != null) {
            DOM2Writer.serializeAsXML(docElement, def.getNamespaces(), pw);
            pw.println();
        }
    }

    protected void printTypes(Types types, Definition def, PrintWriter pw) throws WSDLException {
        if (types != null) {
            String tagName = DOMUtils.getQualifiedValue("http://schemas.xmlsoap.org/wsdl/", "types", def);
            pw.print("  <" + tagName);
            this.printExtensibilityAttributes(Types.class, types, def, pw);
            pw.println('>');
            this.printDocumentation(types.getDocumentationElement(), def, pw);
            List extElements = types.getExtensibilityElements();
            this.printExtensibilityElements(Types.class, extElements, def, pw);
            pw.println("  </" + tagName + '>');
        }
    }

    protected void printImports(Map imports, Definition def, PrintWriter pw) throws WSDLException {
        if (imports != null) {
            String tagName = DOMUtils.getQualifiedValue("http://schemas.xmlsoap.org/wsdl/", "import", def);
            for (List importList : imports.values()) {
                for (Import importDef : importList) {
                    pw.print("  <" + tagName);
                    DOMUtils.printAttribute("namespace", importDef.getNamespaceURI(), pw);
                    DOMUtils.printAttribute("location", importDef.getLocationURI(), pw);
                    this.printExtensibilityAttributes(Import.class, importDef, def, pw);
                    pw.println('>');
                    this.printDocumentation(importDef.getDocumentationElement(), def, pw);
                    List extElements = importDef.getExtensibilityElements();
                    this.printExtensibilityElements(Import.class, extElements, def, pw);
                    pw.println("    </" + tagName + '>');
                }
            }
        }
    }

    protected void printNamespaceDeclarations(Map namespaces, PrintWriter pw) throws WSDLException {
        if (namespaces != null) {
            Set keys = namespaces.keySet();
            for (String prefix : keys) {
                if (prefix == null) {
                    prefix = "";
                }
                DOMUtils.printAttribute("xmlns" + (!prefix.equals("") ? ":" + prefix : ""), (String)namespaces.get(prefix), pw);
            }
        }
    }

    protected void printExtensibilityElements(Class parentType, List extensibilityElements, Definition def, PrintWriter pw) throws WSDLException {
        if (extensibilityElements != null) {
            for (ExtensibilityElement ext : extensibilityElements) {
                QName elementType = ext.getElementType();
                ExtensionRegistry extReg = def.getExtensionRegistry();
                if (extReg == null) {
                    throw new WSDLException("CONFIGURATION_ERROR", "No ExtensionRegistry set for this Definition, so unable to serialize a '" + elementType + "' element in the context of a '" + parentType.getName() + "'.");
                }
                ExtensionSerializer extSer = ext instanceof UnknownExtensibilityElement ? extReg.getDefaultSerializer() : extReg.querySerializer(parentType, elementType);
                extSer.marshall(parentType, elementType, ext, pw, def, extReg);
            }
        }
    }

    private static Document getDocument(InputSource inputSource, String desc) throws WSDLException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(inputSource);
            return doc;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new WSDLException("PARSER_ERROR", "Problem parsing '" + desc + "'.", e);
        }
    }

    public Document getDocument(Definition wsdlDef) throws WSDLException {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        this.writeWSDL(wsdlDef, pw);
        StringReader sr = new StringReader(sw.toString());
        InputSource is = new InputSource(sr);
        return WSDLWriterImpl.getDocument(is, "- WSDL Document -");
    }

    public void writeWSDL(Definition wsdlDef, Writer sink) throws WSDLException {
        PrintWriter pw = new PrintWriter(sink);
        String javaEncoding = sink instanceof OutputStreamWriter ? ((OutputStreamWriter)sink).getEncoding() : null;
        String xmlEncoding = DOM2Writer.java2XMLEncoding(javaEncoding);
        if (xmlEncoding == null) {
            throw new WSDLException("CONFIGURATION_ERROR", "Unsupported Java encoding for writing wsdl file: '" + javaEncoding + "'.");
        }
        pw.println("<?xml version=\"1.0\" encoding=\"" + xmlEncoding + "\"?>");
        this.printDefinition(wsdlDef, pw);
    }

    public void writeWSDL(Definition wsdlDef, OutputStream sink) throws WSDLException {
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(sink, "UTF8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            writer = new OutputStreamWriter(sink);
        }
        this.writeWSDL(wsdlDef, writer);
    }

    public static void main(String[] argv) throws WSDLException {
        if (argv.length == 1) {
            WSDLFactory wsdlFactory = WSDLFactory.newInstance();
            WSDLReader wsdlReader = wsdlFactory.newWSDLReader();
            WSDLWriter wsdlWriter = wsdlFactory.newWSDLWriter();
            wsdlWriter.writeWSDL(wsdlReader.readWSDL(null, argv[0]), System.out);
        } else {
            System.err.println("Usage:");
            System.err.println();
            System.err.println("  java " + WSDLWriterImpl.class.getName() + " filename|URL");
            System.err.println();
            System.err.println("This test driver simply reads a WSDL document into a model (using a WSDLReader), and then serializes it back to standard out. In effect, it performs a round-trip test on the specified WSDL document.");
        }
    }
}

