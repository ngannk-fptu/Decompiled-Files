/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ibm.wsdl.extensions.soap.SOAPAddressImpl
 *  com.ibm.wsdl.extensions.soap.SOAPBindingImpl
 *  com.ibm.wsdl.extensions.soap.SOAPBodyImpl
 *  com.ibm.wsdl.extensions.soap.SOAPFaultImpl
 *  com.ibm.wsdl.extensions.soap.SOAPHeaderImpl
 *  com.ibm.wsdl.extensions.soap.SOAPOperationImpl
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
 *  javax.wsdl.OperationType
 *  javax.wsdl.Output
 *  javax.wsdl.Part
 *  javax.wsdl.Port
 *  javax.wsdl.PortType
 *  javax.wsdl.Service
 *  javax.wsdl.WSDLException
 *  javax.wsdl.extensions.ExtensibilityElement
 *  javax.wsdl.extensions.soap.SOAPFault
 *  javax.wsdl.extensions.soap.SOAPHeader
 *  javax.wsdl.factory.WSDLFactory
 *  javax.wsdl.xml.WSDLReader
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.wsdl.fromJava;

import com.ibm.wsdl.extensions.soap.SOAPAddressImpl;
import com.ibm.wsdl.extensions.soap.SOAPBindingImpl;
import com.ibm.wsdl.extensions.soap.SOAPBodyImpl;
import com.ibm.wsdl.extensions.soap.SOAPFaultImpl;
import com.ibm.wsdl.extensions.soap.SOAPHeaderImpl;
import com.ibm.wsdl.extensions.soap.SOAPOperationImpl;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
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
import javax.wsdl.OperationType;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPFault;
import javax.wsdl.extensions.soap.SOAPHeader;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.InternalException;
import org.apache.axis.Version;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.description.FaultDesc;
import org.apache.axis.description.JavaServiceDesc;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.encoding.TypeMappingRegistryImpl;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.apache.axis.wsdl.fromJava.Namespaces;
import org.apache.axis.wsdl.fromJava.Types;
import org.apache.commons.logging.Log;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class Emitter {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$wsdl$fromJava$Emitter == null ? (class$org$apache$axis$wsdl$fromJava$Emitter = Emitter.class$("org.apache.axis.wsdl.fromJava.Emitter")) : class$org$apache$axis$wsdl$fromJava$Emitter).getName());
    public static final int MODE_ALL = 0;
    public static final int MODE_INTERFACE = 1;
    public static final int MODE_IMPLEMENTATION = 2;
    private Class cls;
    private Class[] extraClasses;
    private Class implCls;
    private Vector allowedMethods = null;
    private Vector disallowedMethods = null;
    private ArrayList stopClasses = new ArrayList();
    private boolean useInheritedMethods = false;
    private String intfNS;
    private String implNS;
    private String inputSchema;
    private String inputWSDL;
    private String locationUrl;
    private String importUrl;
    private String servicePortName;
    private String serviceElementName;
    private String targetService = null;
    private String description;
    private Style style = Style.RPC;
    private Use use = null;
    private TypeMapping tm = null;
    private TypeMappingRegistry tmr = new TypeMappingRegistryImpl();
    private Namespaces namespaces;
    private Map exceptionMsg = null;
    private Map usedElementNames;
    private ArrayList encodingList;
    protected Types types;
    private String clsName;
    private String portTypeName;
    private String bindingName;
    private ServiceDesc serviceDesc;
    private JavaServiceDesc serviceDesc2;
    private String soapAction = "DEFAULT";
    private boolean emitAllTypes = false;
    private String versionMessage = null;
    private HashMap qName2ClassMap;
    public static final int MODE_RPC = 0;
    public static final int MODE_DOCUMENT = 1;
    public static final int MODE_DOC_WRAPPED = 2;
    protected static TypeMapping standardTypes = (TypeMapping)new TypeMappingRegistryImpl().getTypeMapping(null);
    Document docHolder;
    static /* synthetic */ Class class$org$apache$axis$wsdl$fromJava$Emitter;
    static /* synthetic */ Class class$java$lang$Object;

    public Emitter() {
        this.createDocumentFragment();
        this.namespaces = new Namespaces();
        this.exceptionMsg = new HashMap();
        this.usedElementNames = new HashMap();
        this.qName2ClassMap = new HashMap();
    }

    public void emit(String filename1, String filename2) throws IOException, WSDLException, SAXException, ParserConfigurationException {
        Definition intf = this.getIntfWSDL();
        Definition impl = this.getImplWSDL();
        if (filename1 == null) {
            filename1 = this.getServicePortName() + "_interface.wsdl";
        }
        if (filename2 == null) {
            filename2 = this.getServicePortName() + "_implementation.wsdl";
        }
        for (int i = 0; this.extraClasses != null && i < this.extraClasses.length; ++i) {
            this.types.writeTypeForPart(this.extraClasses[i], null);
        }
        Document doc = WSDLFactory.newInstance().newWSDLWriter().getDocument(intf);
        this.types.insertTypesFragment(doc);
        this.prettyDocumentToFile(doc, filename1);
        doc = WSDLFactory.newInstance().newWSDLWriter().getDocument(impl);
        this.prettyDocumentToFile(doc, filename2);
    }

    public void emit(String filename) throws IOException, WSDLException, SAXException, ParserConfigurationException {
        this.emit(filename, 0);
    }

    public Document emit(int mode) throws IOException, WSDLException, SAXException, ParserConfigurationException {
        Document doc;
        switch (mode) {
            default: {
                int i;
                Definition def = this.getWSDL();
                for (i = 0; this.extraClasses != null && i < this.extraClasses.length; ++i) {
                    this.types.writeTypeForPart(this.extraClasses[i], null);
                }
                doc = WSDLFactory.newInstance().newWSDLWriter().getDocument(def);
                this.types.insertTypesFragment(doc);
                break;
            }
            case 1: {
                int i;
                Definition def = this.getIntfWSDL();
                for (i = 0; this.extraClasses != null && i < this.extraClasses.length; ++i) {
                    this.types.writeTypeForPart(this.extraClasses[i], null);
                }
                doc = WSDLFactory.newInstance().newWSDLWriter().getDocument(def);
                this.types.insertTypesFragment(doc);
                break;
            }
            case 2: {
                Definition def = this.getImplWSDL();
                doc = WSDLFactory.newInstance().newWSDLWriter().getDocument(def);
            }
        }
        if (this.versionMessage == null) {
            this.versionMessage = Messages.getMessage("wsdlCreated00", XMLUtils.xmlEncodeString(Version.getVersion()));
        }
        if (this.versionMessage != null && this.versionMessage.length() > 0) {
            Comment wsdlVersion = doc.createComment(this.versionMessage);
            doc.getDocumentElement().insertBefore(wsdlVersion, doc.getDocumentElement().getFirstChild());
        }
        return doc;
    }

    public String emitToString(int mode) throws IOException, WSDLException, SAXException, ParserConfigurationException {
        Document doc = this.emit(mode);
        StringWriter sw = new StringWriter();
        XMLUtils.PrettyDocumentToWriter(doc, sw);
        return sw.toString();
    }

    public void emit(String filename, int mode) throws IOException, WSDLException, SAXException, ParserConfigurationException {
        Document doc = this.emit(mode);
        if (filename == null) {
            filename = this.getServicePortName();
            switch (mode) {
                case 0: {
                    filename = filename + ".wsdl";
                    break;
                }
                case 1: {
                    filename = filename + "_interface.wsdl";
                    break;
                }
                case 2: {
                    filename = filename + "_implementation.wsdl";
                }
            }
        }
        this.prettyDocumentToFile(doc, filename);
    }

    public Definition getWSDL() throws IOException, WSDLException, SAXException, ParserConfigurationException {
        this.init(0);
        Definition def = this.createDefinition();
        this.writeDefinitions(def, this.intfNS);
        this.types = this.createTypes(def);
        Binding binding = this.writeBinding(def, true);
        this.writePortType(def, binding);
        this.writeService(def, binding);
        return def;
    }

    public Definition getIntfWSDL() throws IOException, WSDLException, SAXException, ParserConfigurationException {
        this.init(1);
        Definition def = this.createDefinition();
        this.writeDefinitions(def, this.intfNS);
        this.types = this.createTypes(def);
        Binding binding = this.writeBinding(def, true);
        this.writePortType(def, binding);
        return def;
    }

    public Definition getImplWSDL() throws IOException, WSDLException, SAXException, ParserConfigurationException {
        this.init(2);
        Definition def = this.createDefinition();
        this.writeDefinitions(def, this.implNS);
        this.writeImport(def, this.intfNS, this.importUrl);
        Binding binding = this.writeBinding(def, false);
        this.writeService(def, binding);
        return def;
    }

    protected void init(int mode) {
        if (this.use == null) {
            this.use = this.style == Style.RPC ? Use.ENCODED : Use.LITERAL;
        }
        if (this.tm == null) {
            String encodingStyle = "";
            if (this.use == Use.ENCODED) {
                encodingStyle = "http://schemas.xmlsoap.org/soap/encoding/";
            }
            this.tm = (TypeMapping)this.tmr.getTypeMapping(encodingStyle);
        }
        if (this.serviceDesc == null) {
            JavaServiceDesc javaServiceDesc = new JavaServiceDesc();
            this.serviceDesc = javaServiceDesc;
            javaServiceDesc.setImplClass(this.cls);
            this.serviceDesc.setTypeMapping(this.tm);
            javaServiceDesc.setStopClasses(this.stopClasses);
            this.serviceDesc.setAllowedMethods(this.allowedMethods);
            javaServiceDesc.setDisallowedMethods(this.disallowedMethods);
            this.serviceDesc.setStyle(this.style);
            this.serviceDesc.setUse(this.use);
            if (this.implCls != null && this.implCls != this.cls && this.serviceDesc2 == null) {
                this.serviceDesc2 = new JavaServiceDesc();
                this.serviceDesc2.setImplClass(this.implCls);
                this.serviceDesc2.setTypeMapping(this.tm);
                this.serviceDesc2.setStopClasses(this.stopClasses);
                this.serviceDesc2.setAllowedMethods(this.allowedMethods);
                this.serviceDesc2.setDisallowedMethods(this.disallowedMethods);
                this.serviceDesc2.setStyle(this.style);
            }
        }
        if (this.encodingList == null) {
            String clsName;
            int idx;
            if (this.cls != null) {
                this.clsName = this.cls.getName();
                this.clsName = this.clsName.substring(this.clsName.lastIndexOf(46) + 1);
            } else {
                this.clsName = this.getServiceDesc().getName();
            }
            if (this.getPortTypeName() == null) {
                this.setPortTypeName(this.clsName);
            }
            if (this.getServiceElementName() == null) {
                this.setServiceElementName(this.getPortTypeName() + "Service");
            }
            if (this.getServicePortName() == null) {
                String name = this.getLocationUrl();
                if (name != null && (name = name.lastIndexOf(47) > 0 ? name.substring(name.lastIndexOf(47) + 1) : (name.lastIndexOf(92) > 0 ? name.substring(name.lastIndexOf(92) + 1) : null)) != null && name.endsWith(".jws")) {
                    name = name.substring(0, name.length() - ".jws".length());
                }
                if (name == null || name.equals("")) {
                    name = this.clsName;
                }
                this.setServicePortName(name);
            }
            if (this.getBindingName() == null) {
                this.setBindingName(this.getServicePortName() + "SoapBinding");
            }
            this.encodingList = new ArrayList();
            this.encodingList.add(Constants.URI_DEFAULT_SOAP_ENC);
            if (this.intfNS == null) {
                Package pkg = this.cls.getPackage();
                this.intfNS = this.namespaces.getCreate(pkg == null ? null : pkg.getName());
            }
            if (this.implNS == null) {
                this.implNS = mode == 0 ? this.intfNS : this.intfNS + "-impl";
            }
            this.serviceDesc.setDefaultNamespace(this.intfNS);
            if (this.serviceDesc2 != null) {
                this.serviceDesc2.setDefaultNamespace(this.implNS);
            }
            if (this.cls != null && (idx = (clsName = this.cls.getName()).lastIndexOf(".")) > 0) {
                String pkgName = clsName.substring(0, idx);
                this.namespaces.put(pkgName, this.intfNS, "intf");
            }
            this.namespaces.putPrefix(this.implNS, "impl");
        }
    }

    protected Definition createDefinition() throws WSDLException, SAXException, IOException, ParserConfigurationException {
        Definition def;
        if (this.inputWSDL == null) {
            def = WSDLFactory.newInstance().newDefinition();
        } else {
            WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
            Document doc = XMLUtils.newDocument(this.inputWSDL);
            def = reader.readWSDL(null, doc);
            def.setTypes(null);
        }
        return def;
    }

    protected Types createTypes(Definition def) throws IOException, WSDLException, SAXException, ParserConfigurationException {
        this.types = new Types(def, this.tm, (TypeMapping)this.tmr.getDefaultTypeMapping(), this.namespaces, this.intfNS, this.stopClasses, this.serviceDesc, this);
        if (this.inputWSDL != null) {
            this.types.loadInputTypes(this.inputWSDL);
        }
        if (this.inputSchema != null) {
            StringTokenizer tokenizer = new StringTokenizer(this.inputSchema, ", ");
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                this.types.loadInputSchema(token);
            }
        }
        if (this.emitAllTypes && this.tm != null) {
            Class[] mappedTypes = this.tm.getAllClasses();
            for (int i = 0; i < mappedTypes.length; ++i) {
                Class mappedType = mappedTypes[i];
                QName name = this.tm.getTypeQName(mappedType);
                if (name.getLocalPart().indexOf(">") != -1 || standardTypes.getSerializer(mappedType) != null) continue;
                this.types.writeTypeForPart(mappedType, name);
            }
            this.types.mappedTypes = null;
        }
        return this.types;
    }

    protected Element createDocumentationElement(String documentation) {
        Element element = this.docHolder.createElementNS("http://schemas.xmlsoap.org/wsdl/", "documentation");
        element.setPrefix("wsdl");
        Text textNode = this.docHolder.createTextNode(documentation);
        element.appendChild(textNode);
        return element;
    }

    protected void writeDefinitions(Definition def, String tns) {
        def.setTargetNamespace(tns);
        def.addNamespace("intf", this.intfNS);
        def.addNamespace("impl", this.implNS);
        def.addNamespace("wsdlsoap", "http://schemas.xmlsoap.org/wsdl/soap/");
        this.namespaces.putPrefix("http://schemas.xmlsoap.org/wsdl/soap/", "wsdlsoap");
        def.addNamespace("wsdl", "http://schemas.xmlsoap.org/wsdl/");
        this.namespaces.putPrefix("http://schemas.xmlsoap.org/wsdl/", "wsdl");
        if (this.use == Use.ENCODED) {
            def.addNamespace("soapenc", Constants.URI_DEFAULT_SOAP_ENC);
            this.namespaces.putPrefix(Constants.URI_DEFAULT_SOAP_ENC, "soapenc");
        }
        def.addNamespace("xsd", "http://www.w3.org/2001/XMLSchema");
        this.namespaces.putPrefix("http://www.w3.org/2001/XMLSchema", "xsd");
        def.addNamespace("apachesoap", "http://xml.apache.org/xml-soap");
        this.namespaces.putPrefix("http://xml.apache.org/xml-soap", "apachesoap");
    }

    protected void writeImport(Definition def, String tns, String loc) {
        Import imp = def.createImport();
        imp.setNamespaceURI(tns);
        if (loc != null && !loc.equals("")) {
            imp.setLocationURI(loc);
        }
        def.addImport(imp);
    }

    protected Binding writeBinding(Definition def, boolean add) {
        QName bindingQName = new QName(this.intfNS, this.getBindingName());
        Binding binding = def.getBinding(bindingQName);
        if (binding != null) {
            return binding;
        }
        binding = def.createBinding();
        binding.setUndefined(false);
        binding.setQName(bindingQName);
        SOAPBindingImpl soapBinding = new SOAPBindingImpl();
        String styleStr = this.style == Style.RPC ? "rpc" : "document";
        soapBinding.setStyle(styleStr);
        soapBinding.setTransportURI("http://schemas.xmlsoap.org/soap/http");
        binding.addExtensibilityElement((ExtensibilityElement)soapBinding);
        if (add) {
            def.addBinding(binding);
        }
        return binding;
    }

    private void createDocumentFragment() {
        try {
            this.docHolder = XMLUtils.newDocument();
        }
        catch (ParserConfigurationException e) {
            throw new InternalException(e);
        }
    }

    protected void writeService(Definition def, Binding binding) {
        QName serviceElementQName = new QName(this.implNS, this.getServiceElementName());
        Service service = def.getService(serviceElementQName);
        if (service == null) {
            service = def.createService();
            service.setQName(serviceElementQName);
            def.addService(service);
        }
        if (this.description != null) {
            service.setDocumentationElement(this.createDocumentationElement(this.description));
        } else if (this.serviceDesc.getDocumentation() != null) {
            service.setDocumentationElement(this.createDocumentationElement(this.serviceDesc.getDocumentation()));
        }
        Port port = def.createPort();
        port.setBinding(binding);
        port.setName(this.getServicePortName());
        SOAPAddressImpl addr = new SOAPAddressImpl();
        addr.setLocationURI(this.locationUrl);
        port.addExtensibilityElement((ExtensibilityElement)addr);
        service.addPort(port);
    }

    protected void writePortType(Definition def, Binding binding) throws WSDLException, AxisFault {
        QName portTypeQName = new QName(this.intfNS, this.getPortTypeName());
        PortType portType = def.getPortType(portTypeQName);
        boolean newPortType = false;
        if (portType == null) {
            portType = def.createPortType();
            portType.setUndefined(false);
            portType.setQName(portTypeQName);
            newPortType = true;
        } else if (binding.getBindingOperations().size() > 0) {
            return;
        }
        ArrayList operations = this.serviceDesc.getOperations();
        Iterator i = operations.iterator();
        while (i.hasNext()) {
            OperationDesc thisOper = (OperationDesc)i.next();
            BindingOperation bindingOper = this.writeOperation(def, binding, thisOper);
            Operation oper = bindingOper.getOperation();
            OperationDesc messageOper = thisOper;
            if (messageOper.getDocumentation() != null) {
                oper.setDocumentationElement(this.createDocumentationElement(messageOper.getDocumentation()));
            }
            if (this.serviceDesc2 != null) {
                OperationDesc[] operArray = this.serviceDesc2.getOperationsByName(thisOper.getName());
                boolean found = false;
                if (operArray != null) {
                    for (int j = 0; j < operArray.length && !found; ++j) {
                        OperationDesc tryOper = operArray[j];
                        if (tryOper.getParameters().size() != thisOper.getParameters().size()) continue;
                        boolean parmsMatch = true;
                        for (int k = 0; k < thisOper.getParameters().size() && parmsMatch; ++k) {
                            if (tryOper.getParameter(k).getMode() == thisOper.getParameter(k).getMode() && tryOper.getParameter(k).getJavaType().equals(thisOper.getParameter(k).getJavaType())) continue;
                            parmsMatch = false;
                        }
                        if (!parmsMatch) continue;
                        messageOper = tryOper;
                        found = true;
                    }
                }
            }
            this.writeMessages(def, oper, messageOper, bindingOper);
            if (!newPortType) continue;
            portType.addOperation(oper);
        }
        if (newPortType) {
            def.addPortType(portType);
        }
        binding.setPortType(portType);
    }

    protected void writeMessages(Definition def, Operation oper, OperationDesc desc, BindingOperation bindingOper) throws WSDLException, AxisFault {
        Input input = def.createInput();
        Message msg = this.writeRequestMessage(def, desc, bindingOper);
        input.setMessage(msg);
        String name = msg.getQName().getLocalPart();
        input.setName(name);
        bindingOper.getBindingInput().setName(name);
        oper.setInput(input);
        def.addMessage(msg);
        if (OperationType.REQUEST_RESPONSE.equals(desc.getMep())) {
            msg = this.writeResponseMessage(def, desc, bindingOper);
            Output output = def.createOutput();
            output.setMessage(msg);
            name = msg.getQName().getLocalPart();
            output.setName(name);
            bindingOper.getBindingOutput().setName(name);
            oper.setOutput(output);
            def.addMessage(msg);
        }
        ArrayList exceptions = desc.getFaults();
        for (int i = 0; exceptions != null && i < exceptions.size(); ++i) {
            FaultDesc faultDesc = (FaultDesc)exceptions.get(i);
            msg = this.writeFaultMessage(def, faultDesc);
            Fault fault = def.createFault();
            fault.setMessage(msg);
            fault.setName(faultDesc.getName());
            oper.addFault(fault);
            BindingFault bFault = def.createBindingFault();
            bFault.setName(faultDesc.getName());
            SOAPFault soapFault = this.writeSOAPFault(faultDesc);
            bFault.addExtensibilityElement((ExtensibilityElement)soapFault);
            bindingOper.addBindingFault(bFault);
            if (def.getMessage(msg.getQName()) != null) continue;
            def.addMessage(msg);
        }
        ArrayList parameters = desc.getParameters();
        Vector<String> names = new Vector<String>();
        for (int i = 0; i < parameters.size(); ++i) {
            ParameterDesc param = (ParameterDesc)parameters.get(i);
            names.add(param.getName());
        }
        if (names.size() > 0) {
            if (this.style == Style.WRAPPED) {
                names.clear();
            } else {
                oper.setParameterOrdering(names);
            }
        }
    }

    protected BindingOperation writeOperation(Definition def, Binding binding, OperationDesc desc) {
        Operation oper = def.createOperation();
        QName elementQName = desc.getElementQName();
        if (elementQName != null && elementQName.getLocalPart() != null) {
            oper.setName(elementQName.getLocalPart());
        } else {
            oper.setName(desc.getName());
        }
        oper.setUndefined(false);
        return this.writeBindingOperation(def, binding, oper, desc);
    }

    protected BindingOperation writeBindingOperation(Definition def, Binding binding, Operation oper, OperationDesc desc) {
        String soapAction;
        BindingOperation bindingOper = def.createBindingOperation();
        BindingInput bindingInput = def.createBindingInput();
        BindingOutput bindingOutput = null;
        if (OperationType.REQUEST_RESPONSE.equals(desc.getMep())) {
            bindingOutput = def.createBindingOutput();
        }
        bindingOper.setName(oper.getName());
        bindingOper.setOperation(oper);
        SOAPOperationImpl soapOper = new SOAPOperationImpl();
        if (this.getSoapAction().equalsIgnoreCase("OPERATION")) {
            soapAction = oper.getName();
        } else if (this.getSoapAction().equalsIgnoreCase("NONE")) {
            soapAction = "";
        } else {
            soapAction = desc.getSoapAction();
            if (soapAction == null) {
                soapAction = "";
            }
        }
        soapOper.setSoapActionURI(soapAction);
        bindingOper.addExtensibilityElement((ExtensibilityElement)soapOper);
        ExtensibilityElement inputBody = this.writeSOAPBody(desc.getElementQName());
        bindingInput.addExtensibilityElement(inputBody);
        if (bindingOutput != null) {
            ExtensibilityElement outputBody = this.writeSOAPBody(desc.getReturnQName());
            bindingOutput.addExtensibilityElement(outputBody);
            bindingOper.setBindingOutput(bindingOutput);
        }
        bindingOper.setBindingInput(bindingInput);
        binding.addBindingOperation(bindingOper);
        return bindingOper;
    }

    protected SOAPHeader writeSOAPHeader(ParameterDesc p, QName messageQName, String partName) {
        SOAPHeaderImpl soapHeader = new SOAPHeaderImpl();
        if (this.use == Use.ENCODED) {
            soapHeader.setUse("encoded");
            soapHeader.setEncodingStyles((List)this.encodingList);
        } else {
            soapHeader.setUse("literal");
        }
        if (this.targetService == null) {
            soapHeader.setNamespaceURI(this.intfNS);
        } else {
            soapHeader.setNamespaceURI(this.targetService);
        }
        QName headerQName = p.getQName();
        if (headerQName != null && !headerQName.getNamespaceURI().equals("")) {
            soapHeader.setNamespaceURI(headerQName.getNamespaceURI());
        }
        soapHeader.setMessage(messageQName);
        soapHeader.setPart(partName);
        return soapHeader;
    }

    protected ExtensibilityElement writeSOAPBody(QName operQName) {
        SOAPBodyImpl soapBody = new SOAPBodyImpl();
        if (this.use == Use.ENCODED) {
            soapBody.setUse("encoded");
            soapBody.setEncodingStyles((List)this.encodingList);
        } else {
            soapBody.setUse("literal");
        }
        if (this.style == Style.RPC) {
            if (this.targetService == null) {
                soapBody.setNamespaceURI(this.intfNS);
            } else {
                soapBody.setNamespaceURI(this.targetService);
            }
            if (operQName != null && !operQName.getNamespaceURI().equals("")) {
                soapBody.setNamespaceURI(operQName.getNamespaceURI());
            }
        }
        return soapBody;
    }

    protected SOAPFault writeSOAPFault(FaultDesc faultDesc) {
        SOAPFaultImpl soapFault = new SOAPFaultImpl();
        soapFault.setName(faultDesc.getName());
        if (this.use != Use.ENCODED) {
            soapFault.setUse("literal");
        } else {
            soapFault.setUse("encoded");
            soapFault.setEncodingStyles((List)this.encodingList);
            QName faultQName = faultDesc.getQName();
            if (faultQName != null && !faultQName.getNamespaceURI().equals("")) {
                soapFault.setNamespaceURI(faultQName.getNamespaceURI());
            } else if (this.targetService == null) {
                soapFault.setNamespaceURI(this.intfNS);
            } else {
                soapFault.setNamespaceURI(this.targetService);
            }
        }
        return soapFault;
    }

    protected Message writeRequestMessage(Definition def, OperationDesc oper, BindingOperation bindop) throws WSDLException, AxisFault {
        String partName;
        ArrayList<String> bodyParts = new ArrayList<String>();
        ArrayList parameters = oper.getAllInParams();
        Message msg = def.createMessage();
        QName qName = this.createMessageName(def, this.getRequestQName(oper).getLocalPart() + "Request");
        msg.setQName(qName);
        msg.setUndefined(false);
        boolean headers = this.writeHeaderParts(def, parameters, bindop, msg, true);
        if (oper.getStyle() == Style.MESSAGE) {
            QName qname = oper.getElementQName();
            this.types.writeElementDecl(qname, class$java$lang$Object == null ? (class$java$lang$Object = Emitter.class$("java.lang.Object")) : class$java$lang$Object, Constants.XSD_ANYTYPE, false, null);
            Part part = def.createPart();
            part.setName("part");
            part.setElementName(qname);
            msg.addPart(part);
            bodyParts.add(part.getName());
        } else if (oper.getStyle() == Style.WRAPPED) {
            partName = this.writeWrapperPart(def, msg, oper, true);
            bodyParts.add(partName);
        } else {
            if (oper.getStyle() == Style.DOCUMENT && parameters.size() > 1) {
                System.out.println(Messages.getMessage("warnDocLitInteropMultipleInputParts"));
            }
            for (int i = 0; i < parameters.size(); ++i) {
                ParameterDesc parameter = (ParameterDesc)parameters.get(i);
                if (parameter.isInHeader() || parameter.isOutHeader()) continue;
                partName = this.writePartToMessage(def, msg, true, parameter);
                bodyParts.add(partName);
            }
        }
        if (headers) {
            List extensibilityElements = bindop.getBindingInput().getExtensibilityElements();
            for (int i = 0; i < extensibilityElements.size(); ++i) {
                Object ele = extensibilityElements.get(i);
                if (!(ele instanceof SOAPBodyImpl)) continue;
                SOAPBodyImpl soapBody = (SOAPBodyImpl)ele;
                soapBody.setParts(bodyParts);
            }
        }
        return msg;
    }

    private boolean writeHeaderParts(Definition def, ArrayList parameters, BindingOperation bindop, Message msg, boolean request) throws WSDLException, AxisFault {
        boolean wroteHeaderParts = false;
        for (int i = 0; i < parameters.size(); ++i) {
            SOAPHeader hdr;
            String partName;
            ParameterDesc parameter = (ParameterDesc)parameters.get(i);
            if (request && parameter.isInHeader()) {
                partName = this.writePartToMessage(def, msg, request, parameter);
                hdr = this.writeSOAPHeader(parameter, msg.getQName(), partName);
                bindop.getBindingInput().addExtensibilityElement((ExtensibilityElement)hdr);
                wroteHeaderParts = true;
                continue;
            }
            if (request || !parameter.isOutHeader()) continue;
            partName = this.writePartToMessage(def, msg, request, parameter);
            hdr = this.writeSOAPHeader(parameter, msg.getQName(), partName);
            bindop.getBindingOutput().addExtensibilityElement((ExtensibilityElement)hdr);
            wroteHeaderParts = true;
        }
        return wroteHeaderParts;
    }

    protected QName getRequestQName(OperationDesc oper) {
        this.qualifyOperation(oper);
        QName qname = oper.getElementQName();
        if (qname == null) {
            qname = new QName(oper.getName());
        }
        return qname;
    }

    private void qualifyOperation(OperationDesc oper) {
        if (this.style == Style.WRAPPED && this.use == Use.LITERAL) {
            QName qname = oper.getElementQName();
            if (qname == null) {
                qname = new QName(this.intfNS, oper.getName());
            } else if (qname.getNamespaceURI().equals("")) {
                qname = new QName(this.intfNS, qname.getLocalPart());
            }
            oper.setElementQName(qname);
        }
    }

    protected QName getResponseQName(OperationDesc oper) {
        this.qualifyOperation(oper);
        QName qname = oper.getElementQName();
        if (qname == null) {
            return new QName(oper.getName() + "Response");
        }
        return new QName(qname.getNamespaceURI(), qname.getLocalPart() + "Response");
    }

    public String writeWrapperPart(Definition def, Message msg, OperationDesc oper, boolean request) throws AxisFault {
        QName qname;
        QName qName = qname = request ? this.getRequestQName(oper) : this.getResponseQName(oper);
        boolean hasParams = request ? oper.getNumInParams() > 0 : (oper.getReturnClass() != Void.TYPE ? true : oper.getNumOutParams() > 0);
        Element sequence = this.types.writeWrapperElement(qname, request, hasParams);
        if (sequence != null) {
            ArrayList parameters;
            ArrayList arrayList = parameters = request ? oper.getAllInParams() : oper.getAllOutParams();
            if (!request) {
                String retName = oper.getReturnQName() == null ? oper.getName() + "Return" : oper.getReturnQName().getLocalPart();
                this.types.writeWrappedParameter(sequence, retName, oper.getReturnType(), oper.getReturnClass());
            }
            for (int i = 0; i < parameters.size(); ++i) {
                ParameterDesc parameter = (ParameterDesc)parameters.get(i);
                if (parameter.isInHeader() || parameter.isOutHeader()) continue;
                this.types.writeWrappedParameter(sequence, parameter.getName(), parameter.getTypeQName(), parameter.getJavaType());
            }
        }
        Part part = def.createPart();
        part.setName("parameters");
        part.setElementName(qname);
        msg.addPart(part);
        return part.getName();
    }

    protected Message writeResponseMessage(Definition def, OperationDesc desc, BindingOperation bindop) throws WSDLException, AxisFault {
        String partName;
        ArrayList<String> bodyParts = new ArrayList<String>();
        ArrayList parameters = desc.getAllOutParams();
        Message msg = def.createMessage();
        QName qName = this.createMessageName(def, this.getResponseQName(desc).getLocalPart());
        msg.setQName(qName);
        msg.setUndefined(false);
        boolean headers = this.writeHeaderParts(def, parameters, bindop, msg, false);
        if (desc.getStyle() == Style.WRAPPED) {
            partName = this.writeWrapperPart(def, msg, desc, false);
            bodyParts.add(partName);
        } else {
            ParameterDesc retParam = new ParameterDesc();
            if (desc.getReturnQName() == null) {
                String ns = "";
                if (desc.getStyle() != Style.RPC && ((ns = this.getServiceDesc().getDefaultNamespace()) == null || "".equals(ns))) {
                    ns = "http://ws.apache.org/axis/defaultNS";
                }
                retParam.setQName(new QName(ns, desc.getName() + "Return"));
            } else {
                retParam.setQName(desc.getReturnQName());
            }
            retParam.setTypeQName(desc.getReturnType());
            retParam.setMode((byte)2);
            retParam.setIsReturn(true);
            retParam.setJavaType(desc.getReturnClass());
            String returnPartName = this.writePartToMessage(def, msg, false, retParam);
            bodyParts.add(returnPartName);
            for (int i = 0; i < parameters.size(); ++i) {
                ParameterDesc parameter = (ParameterDesc)parameters.get(i);
                if (parameter.isInHeader() || parameter.isOutHeader()) continue;
                partName = this.writePartToMessage(def, msg, false, parameter);
                bodyParts.add(partName);
            }
        }
        if (headers) {
            List extensibilityElements = bindop.getBindingOutput().getExtensibilityElements();
            for (int i = 0; i < extensibilityElements.size(); ++i) {
                Object ele = extensibilityElements.get(i);
                if (!(ele instanceof SOAPBodyImpl)) continue;
                SOAPBodyImpl soapBody = (SOAPBodyImpl)ele;
                soapBody.setParts(bodyParts);
            }
        }
        return msg;
    }

    protected Message writeFaultMessage(Definition def, FaultDesc exception) throws WSDLException, AxisFault {
        String pkgAndClsName = exception.getClassName();
        String clsName = pkgAndClsName.substring(pkgAndClsName.lastIndexOf(46) + 1, pkgAndClsName.length());
        exception.setName(clsName);
        Message msg = (Message)this.exceptionMsg.get(pkgAndClsName);
        if (msg == null) {
            msg = def.createMessage();
            QName qName = this.createMessageName(def, clsName);
            msg.setQName(qName);
            msg.setUndefined(false);
            ArrayList parameters = exception.getParameters();
            if (parameters != null) {
                for (int i = 0; i < parameters.size(); ++i) {
                    ParameterDesc parameter = (ParameterDesc)parameters.get(i);
                    this.writePartToMessage(def, msg, true, parameter);
                }
            }
            this.exceptionMsg.put(pkgAndClsName, msg);
        }
        return msg;
    }

    public String writePartToMessage(Definition def, Message msg, boolean request, ParameterDesc param) throws WSDLException, AxisFault {
        if (param == null || param.getJavaType() == Void.TYPE) {
            return null;
        }
        if (request && param.getMode() == 2) {
            return null;
        }
        if (!request && param.getMode() == 1) {
            return null;
        }
        Part part = def.createPart();
        if (param.getDocumentation() != null) {
            part.setDocumentationElement(this.createDocumentationElement(param.getDocumentation()));
        }
        Class javaType = param.getJavaType();
        if (param.getMode() != 1 && !param.getIsReturn()) {
            javaType = JavaUtils.getHolderValueType(javaType);
        }
        if (this.use == Use.ENCODED || this.style == Style.RPC) {
            QName typeQName = param.getTypeQName();
            if (javaType != null) {
                typeQName = this.types.writeTypeAndSubTypeForPart(javaType, typeQName);
            }
            if (typeQName != null) {
                part.setName(param.getName());
                part.setTypeName(typeQName);
                msg.addPart(part);
            }
        } else if (this.use == Use.LITERAL) {
            ArrayList<String> names;
            QName qname = param.getQName();
            if (param.getTypeQName() == null) {
                log.warn((Object)Messages.getMessage("registerTypeMappingFor01", param.getJavaType().getName()));
                QName qName = this.types.writeTypeForPart(param.getJavaType(), null);
                if (qName != null) {
                    param.setTypeQName(qName);
                } else {
                    param.setTypeQName(Constants.XSD_ANYTYPE);
                }
            }
            if (param.getTypeQName().getNamespaceURI().equals("")) {
                param.setTypeQName(new QName(this.intfNS, param.getTypeQName().getLocalPart()));
            }
            if (param.getQName().getNamespaceURI().equals("")) {
                qname = new QName(this.intfNS, param.getQName().getLocalPart());
                param.setQName(qname);
            }
            if ((names = (ArrayList<String>)this.usedElementNames.get(qname.getNamespaceURI())) == null) {
                names = new ArrayList<String>(1);
                this.usedElementNames.put(qname.getNamespaceURI(), names);
            } else if (names.contains(qname.getLocalPart())) {
                qname = new QName(qname.getNamespaceURI(), JavaUtils.getUniqueValue(names, qname.getLocalPart()));
            }
            names.add(qname.getLocalPart());
            this.types.writeElementDecl(qname, param.getJavaType(), param.getTypeQName(), false, param.getItemQName());
            part.setName(param.getName());
            part.setElementName(qname);
            msg.addPart(part);
        }
        return param.getName();
    }

    protected QName createMessageName(Definition def, String methodName) {
        QName qName = new QName(this.intfNS, methodName);
        int messageNumber = 1;
        while (def.getMessage(qName) != null) {
            StringBuffer namebuf = new StringBuffer(methodName);
            namebuf.append(messageNumber);
            qName = new QName(this.intfNS, namebuf.toString());
            ++messageNumber;
        }
        return qName;
    }

    protected void prettyDocumentToFile(Document doc, String filename) throws IOException {
        FileOutputStream fos = new FileOutputStream(new File(filename));
        XMLUtils.PrettyDocumentToStream(doc, fos);
        fos.close();
    }

    public Class getCls() {
        return this.cls;
    }

    public void setCls(Class cls) {
        this.cls = cls;
    }

    public void setClsSmart(Class cls, String location) {
        if (cls == null || location == null) {
            return;
        }
        if (location.lastIndexOf(47) > 0) {
            location = location.substring(location.lastIndexOf(47) + 1);
        } else if (location.lastIndexOf(92) > 0) {
            location = location.substring(location.lastIndexOf(92) + 1);
        }
        Constructor<?>[] constructors = cls.getDeclaredConstructors();
        Class<?> intf = null;
        for (int i = 0; i < constructors.length && intf == null; ++i) {
            Class<?>[] parms = constructors[i].getParameterTypes();
            if (parms.length != 1 || !parms[0].isInterface() || parms[0].getName() == null || !Types.getLocalNameFromFullName(parms[0].getName()).equals(location)) continue;
            intf = parms[0];
        }
        if (intf != null) {
            this.setCls(intf);
            if (this.implCls == null) {
                this.setImplCls(cls);
            }
        } else {
            this.setCls(cls);
        }
    }

    public void setCls(String className) throws ClassNotFoundException {
        this.cls = ClassUtils.forName(className);
    }

    public Class getImplCls() {
        return this.implCls;
    }

    public void setImplCls(Class implCls) {
        this.implCls = implCls;
    }

    public void setImplCls(String className) {
        try {
            this.implCls = ClassUtils.forName(className);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getIntfNamespace() {
        return this.intfNS;
    }

    public void setIntfNamespace(String ns) {
        this.intfNS = ns;
    }

    public String getImplNamespace() {
        return this.implNS;
    }

    public void setImplNamespace(String ns) {
        this.implNS = ns;
    }

    public Vector getAllowedMethods() {
        return this.allowedMethods;
    }

    public void setAllowedMethods(String text) {
        if (text != null) {
            StringTokenizer tokenizer = new StringTokenizer(text, " ,+");
            if (this.allowedMethods == null) {
                this.allowedMethods = new Vector();
            }
            while (tokenizer.hasMoreTokens()) {
                this.allowedMethods.add(tokenizer.nextToken());
            }
        }
    }

    public void setAllowedMethods(Vector allowedMethods) {
        if (this.allowedMethods == null) {
            this.allowedMethods = new Vector();
        }
        this.allowedMethods.addAll(allowedMethods);
    }

    public boolean getUseInheritedMethods() {
        return this.useInheritedMethods;
    }

    public void setUseInheritedMethods(boolean useInheritedMethods) {
        this.useInheritedMethods = useInheritedMethods;
    }

    public void setDisallowedMethods(Vector disallowedMethods) {
        if (this.disallowedMethods == null) {
            this.disallowedMethods = new Vector();
        }
        this.disallowedMethods.addAll(disallowedMethods);
    }

    public void setDisallowedMethods(String text) {
        if (text != null) {
            StringTokenizer tokenizer = new StringTokenizer(text, " ,+");
            if (this.disallowedMethods == null) {
                this.disallowedMethods = new Vector();
            }
            this.disallowedMethods = new Vector();
            while (tokenizer.hasMoreTokens()) {
                this.disallowedMethods.add(tokenizer.nextToken());
            }
        }
    }

    public Vector getDisallowedMethods() {
        return this.disallowedMethods;
    }

    public void setStopClasses(ArrayList stopClasses) {
        if (this.stopClasses == null) {
            this.stopClasses = new ArrayList();
        }
        this.stopClasses.addAll(stopClasses);
    }

    public void setStopClasses(String text) {
        if (text != null) {
            StringTokenizer tokenizer = new StringTokenizer(text, " ,+");
            if (this.stopClasses == null) {
                this.stopClasses = new ArrayList();
            }
            while (tokenizer.hasMoreTokens()) {
                this.stopClasses.add(tokenizer.nextToken());
            }
        }
    }

    public ArrayList getStopClasses() {
        return this.stopClasses;
    }

    public Map getNamespaceMap() {
        return this.namespaces;
    }

    public void setNamespaceMap(Map map) {
        if (map != null) {
            this.namespaces.putAll(map);
        }
    }

    public String getInputWSDL() {
        return this.inputWSDL;
    }

    public void setInputWSDL(String inputWSDL) {
        this.inputWSDL = inputWSDL;
    }

    public String getInputSchema() {
        return this.inputSchema;
    }

    public void setInputSchema(String inputSchema) {
        this.inputSchema = inputSchema;
    }

    public String getLocationUrl() {
        return this.locationUrl;
    }

    public void setLocationUrl(String locationUrl) {
        this.locationUrl = locationUrl;
    }

    public String getImportUrl() {
        return this.importUrl;
    }

    public void setImportUrl(String importUrl) {
        this.importUrl = importUrl;
    }

    public String getServicePortName() {
        return this.servicePortName;
    }

    public void setServicePortName(String servicePortName) {
        this.servicePortName = servicePortName;
    }

    public String getServiceElementName() {
        return this.serviceElementName;
    }

    public void setServiceElementName(String serviceElementName) {
        this.serviceElementName = serviceElementName;
    }

    public String getPortTypeName() {
        return this.portTypeName;
    }

    public void setPortTypeName(String portTypeName) {
        this.portTypeName = portTypeName;
    }

    public String getBindingName() {
        return this.bindingName;
    }

    public void setBindingName(String bindingName) {
        this.bindingName = bindingName;
    }

    public String getTargetService() {
        return this.targetService;
    }

    public void setTargetService(String targetService) {
        this.targetService = targetService;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSoapAction() {
        return this.soapAction;
    }

    public void setSoapAction(String value) {
        this.soapAction = value;
    }

    public TypeMapping getTypeMapping() {
        return this.tm;
    }

    public void setTypeMapping(TypeMapping tm) {
        this.tm = tm;
    }

    public TypeMapping getDefaultTypeMapping() {
        return (TypeMapping)this.tmr.getDefaultTypeMapping();
    }

    public void setDefaultTypeMapping(TypeMapping tm) {
        this.tmr.registerDefault(tm);
    }

    public void setTypeMappingRegistry(TypeMappingRegistry tmr) {
        this.tmr = tmr;
    }

    public Style getStyle() {
        return this.style;
    }

    public void setStyle(String value) {
        this.setStyle(Style.getStyle(value));
    }

    public void setStyle(Style value) {
        this.style = value;
        if (this.style.equals(Style.WRAPPED)) {
            this.setUse(Use.LITERAL);
        }
    }

    public Use getUse() {
        return this.use;
    }

    public void setUse(String value) {
        this.use = Use.getUse(value);
    }

    public void setUse(Use value) {
        this.use = value;
    }

    public void setMode(int mode) {
        if (mode == 0) {
            this.setStyle(Style.RPC);
            this.setUse(Use.ENCODED);
        } else if (mode == 1) {
            this.setStyle(Style.DOCUMENT);
            this.setUse(Use.LITERAL);
        } else if (mode == 2) {
            this.setStyle(Style.WRAPPED);
            this.setUse(Use.LITERAL);
        }
    }

    public int getMode() {
        if (this.style == Style.RPC) {
            return 0;
        }
        if (this.style == Style.DOCUMENT) {
            return 1;
        }
        if (this.style == Style.WRAPPED) {
            return 2;
        }
        return -1;
    }

    public ServiceDesc getServiceDesc() {
        return this.serviceDesc;
    }

    public void setServiceDesc(ServiceDesc serviceDesc) {
        this.serviceDesc = serviceDesc;
    }

    public Class[] getExtraClasses() {
        return this.extraClasses;
    }

    public void setExtraClasses(Class[] extraClasses) {
        this.extraClasses = extraClasses;
    }

    public void setExtraClasses(String text) throws ClassNotFoundException {
        Class c;
        Class[] ec;
        ArrayList<Class> clsList = new ArrayList<Class>();
        if (text != null) {
            StringTokenizer tokenizer = new StringTokenizer(text, " ,");
            while (tokenizer.hasMoreTokens()) {
                String clsName = tokenizer.nextToken();
                Class cls = ClassUtils.forName(clsName);
                clsList.add(cls);
            }
        }
        if (this.extraClasses != null) {
            ec = new Class[clsList.size() + this.extraClasses.length];
            for (int i = 0; i < this.extraClasses.length; ++i) {
                ec[i] = c = this.extraClasses[i];
            }
        } else {
            ec = new Class[clsList.size()];
        }
        for (int i = 0; i < clsList.size(); ++i) {
            ec[i] = c = (Class)clsList.get(i);
        }
        this.extraClasses = ec;
    }

    public void setEmitAllTypes(boolean emitAllTypes) {
        this.emitAllTypes = emitAllTypes;
    }

    public String getVersionMessage() {
        return this.versionMessage;
    }

    public void setVersionMessage(String versionMessage) {
        this.versionMessage = versionMessage;
    }

    public HashMap getQName2ClassMap() {
        return this.qName2ClassMap;
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

