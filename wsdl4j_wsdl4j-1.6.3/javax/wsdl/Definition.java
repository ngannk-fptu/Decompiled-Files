/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl;

import java.util.List;
import java.util.Map;
import javax.wsdl.Binding;
import javax.wsdl.BindingFault;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
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

public interface Definition
extends WSDLElement {
    public void setDocumentBaseURI(String var1);

    public String getDocumentBaseURI();

    public void setQName(QName var1);

    public QName getQName();

    public void setTargetNamespace(String var1);

    public String getTargetNamespace();

    public void addNamespace(String var1, String var2);

    public String getNamespace(String var1);

    public String removeNamespace(String var1);

    public String getPrefix(String var1);

    public Map getNamespaces();

    public void setTypes(Types var1);

    public Types getTypes();

    public void addImport(Import var1);

    public Import removeImport(Import var1);

    public List getImports(String var1);

    public Map getImports();

    public void addMessage(Message var1);

    public Message getMessage(QName var1);

    public Message removeMessage(QName var1);

    public Map getMessages();

    public void addBinding(Binding var1);

    public Binding getBinding(QName var1);

    public Binding removeBinding(QName var1);

    public Map getBindings();

    public Map getAllBindings();

    public void addPortType(PortType var1);

    public PortType getPortType(QName var1);

    public PortType removePortType(QName var1);

    public Map getPortTypes();

    public Map getAllPortTypes();

    public void addService(Service var1);

    public Service getService(QName var1);

    public Service removeService(QName var1);

    public Map getServices();

    public Map getAllServices();

    public Binding createBinding();

    public BindingFault createBindingFault();

    public BindingInput createBindingInput();

    public BindingOperation createBindingOperation();

    public BindingOutput createBindingOutput();

    public Fault createFault();

    public Import createImport();

    public Input createInput();

    public Message createMessage();

    public Operation createOperation();

    public Output createOutput();

    public Part createPart();

    public Port createPort();

    public PortType createPortType();

    public Service createService();

    public Types createTypes();

    public ExtensionRegistry getExtensionRegistry();

    public void setExtensionRegistry(ExtensionRegistry var1);
}

