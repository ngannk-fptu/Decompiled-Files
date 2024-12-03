/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl;

import com.ibm.wsdl.AbstractWSDLElement;
import com.ibm.wsdl.BindingFaultImpl;
import com.ibm.wsdl.BindingImpl;
import com.ibm.wsdl.BindingInputImpl;
import com.ibm.wsdl.BindingOperationImpl;
import com.ibm.wsdl.BindingOutputImpl;
import com.ibm.wsdl.Constants;
import com.ibm.wsdl.FaultImpl;
import com.ibm.wsdl.ImportImpl;
import com.ibm.wsdl.InputImpl;
import com.ibm.wsdl.MessageImpl;
import com.ibm.wsdl.OperationImpl;
import com.ibm.wsdl.OutputImpl;
import com.ibm.wsdl.PartImpl;
import com.ibm.wsdl.PortImpl;
import com.ibm.wsdl.PortTypeImpl;
import com.ibm.wsdl.ServiceImpl;
import com.ibm.wsdl.TypesImpl;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import javax.wsdl.Types;
import javax.wsdl.WSDLElement;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.xml.namespace.QName;

public class DefinitionImpl
extends AbstractWSDLElement
implements Definition {
    protected String documentBaseURI = null;
    protected QName name = null;
    protected String targetNamespace = null;
    protected Map namespaces = new HashMap();
    protected Map imports = new HashMap();
    protected Types types = null;
    protected Map messages = new HashMap();
    protected Map bindings = new HashMap();
    protected Map portTypes = new HashMap();
    protected Map services = new HashMap();
    protected List nativeAttributeNames = Arrays.asList(Constants.DEFINITION_ATTR_NAMES);
    protected ExtensionRegistry extReg = null;
    public static final long serialVersionUID = 1L;

    public void setDocumentBaseURI(String documentBaseURI) {
        this.documentBaseURI = documentBaseURI;
    }

    public String getDocumentBaseURI() {
        return this.documentBaseURI;
    }

    public void setQName(QName name) {
        this.name = name;
    }

    public QName getQName() {
        return this.name;
    }

    public void setTargetNamespace(String targetNamespace) {
        this.targetNamespace = targetNamespace;
    }

    public String getTargetNamespace() {
        return this.targetNamespace;
    }

    public void addNamespace(String prefix, String namespaceURI) {
        if (prefix == null) {
            prefix = "";
        }
        if (namespaceURI != null) {
            this.namespaces.put(prefix, namespaceURI);
        } else {
            this.namespaces.remove(prefix);
        }
    }

    public String getNamespace(String prefix) {
        if (prefix == null) {
            prefix = "";
        }
        return (String)this.namespaces.get(prefix);
    }

    public String removeNamespace(String prefix) {
        if (prefix == null) {
            prefix = "";
        }
        return (String)this.namespaces.remove(prefix);
    }

    public String getPrefix(String namespaceURI) {
        if (namespaceURI == null) {
            return null;
        }
        for (Map.Entry entry : this.namespaces.entrySet()) {
            String prefix = (String)entry.getKey();
            String assocNamespaceURI = (String)entry.getValue();
            if (!namespaceURI.equals(assocNamespaceURI)) continue;
            return prefix;
        }
        return null;
    }

    public Map getNamespaces() {
        return this.namespaces;
    }

    public void setTypes(Types types) {
        this.types = types;
    }

    public Types getTypes() {
        return this.types;
    }

    public void addImport(Import importDef) {
        String namespaceURI = importDef.getNamespaceURI();
        Vector<Import> importList = (Vector<Import>)this.imports.get(namespaceURI);
        if (importList == null) {
            importList = new Vector<Import>();
            this.imports.put(namespaceURI, importList);
        }
        importList.add(importDef);
    }

    public Import removeImport(Import importDef) {
        String namespaceURI = importDef.getNamespaceURI();
        List importList = (List)this.imports.get(namespaceURI);
        Import removed = null;
        if (importList != null && importList.remove(importDef)) {
            removed = importDef;
        }
        return removed;
    }

    public List getImports(String namespaceURI) {
        return (List)this.imports.get(namespaceURI);
    }

    public Map getImports() {
        return this.imports;
    }

    public void addMessage(Message message) {
        this.messages.put(message.getQName(), message);
    }

    public Message getMessage(QName name) {
        Message message = (Message)this.messages.get(name);
        if (message == null && name != null) {
            message = (Message)this.getFromImports("message", name);
        }
        return message;
    }

    public Message removeMessage(QName name) {
        return (Message)this.messages.remove(name);
    }

    public Map getMessages() {
        return this.messages;
    }

    public void addBinding(Binding binding) {
        this.bindings.put(binding.getQName(), binding);
    }

    public Binding getBinding(QName name) {
        Binding binding = (Binding)this.bindings.get(name);
        if (binding == null && name != null) {
            binding = (Binding)this.getFromImports("binding", name);
        }
        return binding;
    }

    public Binding removeBinding(QName name) {
        return (Binding)this.bindings.remove(name);
    }

    public Map getBindings() {
        return this.bindings;
    }

    public void addPortType(PortType portType) {
        this.portTypes.put(portType.getQName(), portType);
    }

    public PortType getPortType(QName name) {
        PortType portType = (PortType)this.portTypes.get(name);
        if (portType == null && name != null) {
            portType = (PortType)this.getFromImports("portType", name);
        }
        return portType;
    }

    public PortType removePortType(QName name) {
        return (PortType)this.portTypes.remove(name);
    }

    public Map getPortTypes() {
        return this.portTypes;
    }

    public void addService(Service service) {
        this.services.put(service.getQName(), service);
    }

    public Service getService(QName name) {
        Service service = (Service)this.services.get(name);
        if (service == null && name != null) {
            service = (Service)this.getFromImports("service", name);
        }
        return service;
    }

    public Service removeService(QName name) {
        return (Service)this.services.remove(name);
    }

    public Map getServices() {
        return this.services;
    }

    public Binding createBinding() {
        return new BindingImpl();
    }

    public BindingFault createBindingFault() {
        return new BindingFaultImpl();
    }

    public BindingInput createBindingInput() {
        return new BindingInputImpl();
    }

    public BindingOperation createBindingOperation() {
        return new BindingOperationImpl();
    }

    public BindingOutput createBindingOutput() {
        return new BindingOutputImpl();
    }

    public Fault createFault() {
        return new FaultImpl();
    }

    public Import createImport() {
        return new ImportImpl();
    }

    public Input createInput() {
        return new InputImpl();
    }

    public Message createMessage() {
        return new MessageImpl();
    }

    public Operation createOperation() {
        return new OperationImpl();
    }

    public Output createOutput() {
        return new OutputImpl();
    }

    public Part createPart() {
        return new PartImpl();
    }

    public Port createPort() {
        return new PortImpl();
    }

    public PortType createPortType() {
        return new PortTypeImpl();
    }

    public Service createService() {
        return new ServiceImpl();
    }

    public Types createTypes() {
        return new TypesImpl();
    }

    public void setExtensionRegistry(ExtensionRegistry extReg) {
        this.extReg = extReg;
    }

    public ExtensionRegistry getExtensionRegistry() {
        return this.extReg;
    }

    private Object getFromImports(String typeOfDefinition, QName name) {
        WSDLElement ret = null;
        List importList = this.getImports(name.getNamespaceURI());
        if (importList != null) {
            for (Import importDef : importList) {
                Definition importedDef;
                if (importDef == null || (importedDef = importDef.getDefinition()) == null) continue;
                if (typeOfDefinition == "service") {
                    ret = importedDef.getService(name);
                } else if (typeOfDefinition == "message") {
                    ret = importedDef.getMessage(name);
                } else if (typeOfDefinition == "binding") {
                    ret = importedDef.getBinding(name);
                } else if (typeOfDefinition == "portType") {
                    ret = importedDef.getPortType(name);
                }
                if (ret == null) continue;
                return ret;
            }
        }
        return ret;
    }

    public String toString() {
        String superString;
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("Definition: name=" + this.name + " targetNamespace=" + this.targetNamespace);
        if (this.imports != null) {
            Iterator importIterator = this.imports.values().iterator();
            while (importIterator.hasNext()) {
                strBuf.append("\n" + importIterator.next());
            }
        }
        if (this.types != null) {
            strBuf.append("\n" + this.types);
        }
        if (this.messages != null) {
            Iterator msgsIterator = this.messages.values().iterator();
            while (msgsIterator.hasNext()) {
                strBuf.append("\n" + msgsIterator.next());
            }
        }
        if (this.portTypes != null) {
            Iterator portTypeIterator = this.portTypes.values().iterator();
            while (portTypeIterator.hasNext()) {
                strBuf.append("\n" + portTypeIterator.next());
            }
        }
        if (this.bindings != null) {
            Iterator bindingIterator = this.bindings.values().iterator();
            while (bindingIterator.hasNext()) {
                strBuf.append("\n" + bindingIterator.next());
            }
        }
        if (this.services != null) {
            Iterator serviceIterator = this.services.values().iterator();
            while (serviceIterator.hasNext()) {
                strBuf.append("\n" + serviceIterator.next());
            }
        }
        if (!(superString = super.toString()).equals("")) {
            strBuf.append("\n");
            strBuf.append(superString);
        }
        return strBuf.toString();
    }

    public List getNativeAttributeNames() {
        return this.nativeAttributeNames;
    }

    public Map getAllBindings() {
        HashMap allBindings = new HashMap(this.getBindings());
        Map importMap = this.getImports();
        for (Vector importDefs : importMap.values()) {
            for (Import importDef : importDefs) {
                Definition importedDef = importDef.getDefinition();
                if (importedDef == null) continue;
                allBindings.putAll(importedDef.getAllBindings());
            }
        }
        return allBindings;
    }

    public Map getAllPortTypes() {
        HashMap allPortTypes = new HashMap(this.getPortTypes());
        Map importMap = this.getImports();
        for (Vector importDefs : importMap.values()) {
            for (Import importDef : importDefs) {
                Definition importedDef = importDef.getDefinition();
                if (importedDef == null) continue;
                allPortTypes.putAll(importedDef.getAllPortTypes());
            }
        }
        return allPortTypes;
    }

    public Map getAllServices() {
        HashMap allServices = new HashMap(this.getServices());
        Map importMap = this.getImports();
        for (Vector importDefs : importMap.values()) {
            for (Import importDef : importDefs) {
                Definition importedDef = importDef.getDefinition();
                if (importedDef == null) continue;
                allServices.putAll(importedDef.getAllServices());
            }
        }
        return allServices;
    }
}

