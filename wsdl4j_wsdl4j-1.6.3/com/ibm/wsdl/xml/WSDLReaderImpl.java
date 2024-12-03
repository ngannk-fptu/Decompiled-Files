/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.xml;

import com.ibm.wsdl.Constants;
import com.ibm.wsdl.extensions.schema.SchemaConstants;
import com.ibm.wsdl.util.StringUtils;
import com.ibm.wsdl.util.xml.DOMUtils;
import com.ibm.wsdl.util.xml.QNameUtils;
import com.ibm.wsdl.util.xml.XPathUtils;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
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
import javax.wsdl.extensions.ExtensionDeserializer;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.schema.SchemaReference;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLLocator;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.InputSource;

public class WSDLReaderImpl
implements WSDLReader {
    private static final List STYLE_ONE_WAY = Arrays.asList("input");
    private static final List STYLE_REQUEST_RESPONSE = Arrays.asList("input", "output");
    private static final List STYLE_SOLICIT_RESPONSE = Arrays.asList("output", "input");
    private static final List STYLE_NOTIFICATION = Arrays.asList("output");
    protected boolean verbose = true;
    protected boolean importDocuments = true;
    protected boolean parseSchema = true;
    protected ExtensionRegistry extReg = null;
    protected String factoryImplName = null;
    protected WSDLLocator loc = null;
    protected WSDLFactory factory = null;
    protected Map allSchemas = new Hashtable();

    public void setFeature(String name, boolean value) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("Feature name must not be null.");
        }
        if (name.equals("javax.wsdl.verbose")) {
            this.verbose = value;
        } else if (name.equals("javax.wsdl.importDocuments")) {
            this.importDocuments = value;
        } else if (name.equals("com.ibm.wsdl.parseXMLSchemas")) {
            this.parseSchema = value;
        } else {
            throw new IllegalArgumentException("Feature name '" + name + "' not recognized.");
        }
    }

    public boolean getFeature(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("Feature name must not be null.");
        }
        if (name.equals("javax.wsdl.verbose")) {
            return this.verbose;
        }
        if (name.equals("javax.wsdl.importDocuments")) {
            return this.importDocuments;
        }
        throw new IllegalArgumentException("Feature name '" + name + "' not recognized.");
    }

    public void setExtensionRegistry(ExtensionRegistry extReg) {
        this.extReg = extReg;
    }

    public ExtensionRegistry getExtensionRegistry() {
        return this.extReg;
    }

    protected WSDLFactory getWSDLFactory() throws WSDLException {
        if (this.factory == null) {
            this.factory = this.factoryImplName != null ? WSDLFactory.newInstance(this.factoryImplName) : WSDLFactory.newInstance();
        }
        return this.factory;
    }

    public void setFactoryImplName(String factoryImplName) throws UnsupportedOperationException {
        if (this.factoryImplName == null && factoryImplName != null || this.factoryImplName != null && !this.factoryImplName.equals(factoryImplName)) {
            this.factory = null;
            this.factoryImplName = factoryImplName;
        }
    }

    public String getFactoryImplName() {
        return this.factoryImplName;
    }

    protected Definition parseDefinitions(String documentBaseURI, Element defEl, Map importedDefs) throws WSDLException {
        WSDLReaderImpl.checkElementName(defEl, Constants.Q_ELEM_DEFINITIONS);
        WSDLFactory factory = this.getWSDLFactory();
        Definition def = factory.newDefinition();
        if (this.extReg != null) {
            def.setExtensionRegistry(this.extReg);
        }
        String name = DOMUtils.getAttribute(defEl, "name");
        String targetNamespace = DOMUtils.getAttribute(defEl, "targetNamespace");
        NamedNodeMap attrs = defEl.getAttributes();
        if (importedDefs == null) {
            importedDefs = new Hashtable<String, Definition>();
        }
        if (documentBaseURI != null) {
            def.setDocumentBaseURI(documentBaseURI);
            importedDefs.put(documentBaseURI, def);
        }
        if (name != null) {
            def.setQName(new QName(targetNamespace, name));
        }
        if (targetNamespace != null) {
            def.setTargetNamespace(targetNamespace);
        }
        int size = attrs.getLength();
        for (int i = 0; i < size; ++i) {
            Attr attr = (Attr)attrs.item(i);
            String namespaceURI = attr.getNamespaceURI();
            String localPart = attr.getLocalName();
            String value = attr.getValue();
            if (namespaceURI == null || !namespaceURI.equals("http://www.w3.org/2000/xmlns/")) continue;
            if (localPart != null && !localPart.equals("xmlns")) {
                def.addNamespace(localPart, value);
                continue;
            }
            def.addNamespace(null, value);
        }
        Element tempEl = DOMUtils.getFirstChildElement(defEl);
        while (tempEl != null) {
            if (QNameUtils.matches(Constants.Q_ELEM_IMPORT, tempEl)) {
                def.addImport(this.parseImport(tempEl, def, importedDefs));
            } else if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl)) {
                def.setDocumentationElement(tempEl);
            } else if (QNameUtils.matches(Constants.Q_ELEM_TYPES, tempEl)) {
                def.setTypes(this.parseTypes(tempEl, def));
            } else if (QNameUtils.matches(Constants.Q_ELEM_MESSAGE, tempEl)) {
                def.addMessage(this.parseMessage(tempEl, def));
            } else if (QNameUtils.matches(Constants.Q_ELEM_PORT_TYPE, tempEl)) {
                def.addPortType(this.parsePortType(tempEl, def));
            } else if (QNameUtils.matches(Constants.Q_ELEM_BINDING, tempEl)) {
                def.addBinding(this.parseBinding(tempEl, def));
            } else if (QNameUtils.matches(Constants.Q_ELEM_SERVICE, tempEl)) {
                def.addService(this.parseService(tempEl, def));
            } else {
                def.addExtensibilityElement(this.parseExtensibilityElement(Definition.class, tempEl, def));
            }
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        this.parseExtensibilityAttributes(defEl, Definition.class, def, def);
        return def;
    }

    protected Import parseImport(Element importEl, Definition def, Map importedDefs) throws WSDLException {
        Import importDef;
        block25: {
            importDef = def.createImport();
            try {
                String namespaceURI = DOMUtils.getAttribute(importEl, "namespace");
                String locationURI = DOMUtils.getAttribute(importEl, "location");
                String contextURI = null;
                if (namespaceURI != null) {
                    importDef.setNamespaceURI(namespaceURI);
                }
                if (locationURI == null) break block25;
                importDef.setLocationURI(locationURI);
                if (!this.importDocuments) break block25;
                try {
                    contextURI = def.getDocumentBaseURI();
                    Definition importedDef = null;
                    InputStream inputStream = null;
                    InputSource inputSource = null;
                    URL url = null;
                    if (this.loc != null) {
                        inputSource = this.loc.getImportInputSource(contextURI, locationURI);
                        String liu = this.loc.getLatestImportURI();
                        importedDef = (Definition)importedDefs.get(liu);
                        inputSource.setSystemId(liu);
                    } else {
                        URL contextURL = contextURI != null ? StringUtils.getURL(null, contextURI) : null;
                        url = StringUtils.getURL(contextURL, locationURI);
                        importedDef = (Definition)importedDefs.get(url.toString());
                        if (importedDef == null && (inputStream = StringUtils.getContentAsInputStream(url)) != null) {
                            inputSource = new InputSource(inputStream);
                            inputSource.setSystemId(url.toString());
                        }
                    }
                    if (importedDef == null) {
                        Element documentElement;
                        if (inputSource == null) {
                            throw new WSDLException("OTHER_ERROR", "Unable to locate imported document at '" + locationURI + "'" + (contextURI == null ? "." : ", relative to '" + contextURI + "'."));
                        }
                        Document doc = WSDLReaderImpl.getDocument(inputSource, inputSource.getSystemId());
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        if (QNameUtils.matches(Constants.Q_ELEM_DEFINITIONS, documentElement = doc.getDocumentElement())) {
                            if (this.verbose) {
                                System.out.println("Retrieving document at '" + locationURI + "'" + (contextURI == null ? "." : ", relative to '" + contextURI + "'."));
                            }
                            String urlString = this.loc != null ? this.loc.getLatestImportURI() : (url != null ? url.toString() : locationURI);
                            importedDef = this.readWSDL(urlString, documentElement, importedDefs);
                        } else {
                            QName docElementQName = QNameUtils.newQName(documentElement);
                            if (SchemaConstants.XSD_QNAME_LIST.contains(docElementQName)) {
                                if (this.verbose) {
                                    System.out.println("Retrieving schema wsdl:imported from '" + locationURI + "'" + (contextURI == null ? "." : ", relative to '" + contextURI + "'."));
                                }
                                WSDLFactory factory = this.getWSDLFactory();
                                importedDef = factory.newDefinition();
                                if (this.extReg != null) {
                                    importedDef.setExtensionRegistry(this.extReg);
                                }
                                String urlString = this.loc != null ? this.loc.getLatestImportURI() : (url != null ? url.toString() : locationURI);
                                importedDef.setDocumentBaseURI(urlString);
                                Types types = importedDef.createTypes();
                                types.addExtensibilityElement(this.parseSchema(Types.class, documentElement, importedDef));
                                importedDef.setTypes(types);
                            }
                        }
                    }
                    if (importedDef != null) {
                        importDef.setDefinition(importedDef);
                    }
                }
                catch (WSDLException e) {
                    throw e;
                }
                catch (RuntimeException e) {
                    throw e;
                }
                catch (Exception e) {
                    throw new WSDLException("OTHER_ERROR", "Unable to resolve imported document at '" + locationURI + (contextURI == null ? "'." : "', relative to '" + contextURI + "'"), e);
                }
            }
            catch (WSDLException e) {
                if (e.getLocation() == null) {
                    e.setLocation(XPathUtils.getXPathExprFromNode(importEl));
                } else {
                    String loc = XPathUtils.getXPathExprFromNode(importEl) + e.getLocation();
                    e.setLocation(loc);
                }
                throw e;
            }
        }
        NamedNodeMap attrs = importEl.getAttributes();
        WSDLReaderImpl.registerNSDeclarations(attrs, def);
        Element tempEl = DOMUtils.getFirstChildElement(importEl);
        while (tempEl != null) {
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl)) {
                importDef.setDocumentationElement(tempEl);
            } else {
                importDef.addExtensibilityElement(this.parseExtensibilityElement(Import.class, tempEl, def));
            }
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        this.parseExtensibilityAttributes(importEl, Import.class, importDef, def);
        return importDef;
    }

    protected Types parseTypes(Element typesEl, Definition def) throws WSDLException {
        NamedNodeMap attrs = typesEl.getAttributes();
        WSDLReaderImpl.registerNSDeclarations(attrs, def);
        Types types = def.createTypes();
        Element tempEl = DOMUtils.getFirstChildElement(typesEl);
        while (tempEl != null) {
            QName tempElType = QNameUtils.newQName(tempEl);
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl)) {
                types.setDocumentationElement(tempEl);
            } else if (SchemaConstants.XSD_QNAME_LIST.contains(tempElType)) {
                if (this.parseSchema) {
                    types.addExtensibilityElement(this.parseSchema(Types.class, tempEl, def));
                } else {
                    types.addExtensibilityElement(this.parseExtensibilityElementAsDefaultExtensiblityElement(Types.class, tempEl, def));
                }
            } else {
                types.addExtensibilityElement(this.parseExtensibilityElement(Types.class, tempEl, def));
            }
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        this.parseExtensibilityAttributes(typesEl, Types.class, types, def);
        return types;
    }

    protected ExtensibilityElement parseSchema(Class parentType, Element el, Definition def) throws WSDLException {
        Object elementType = null;
        ExtensionRegistry extReg = null;
        try {
            extReg = def.getExtensionRegistry();
            if (extReg == null) {
                throw new WSDLException("CONFIGURATION_ERROR", "No ExtensionRegistry set for this Definition, so unable to deserialize a '" + elementType + "' element in the " + "context of a '" + parentType.getName() + "'.");
            }
            return this.parseSchema(parentType, el, def, extReg);
        }
        catch (WSDLException e) {
            if (e.getLocation() == null) {
                e.setLocation(XPathUtils.getXPathExprFromNode(el));
            }
            throw e;
        }
    }

    protected ExtensibilityElement parseSchema(Class parentType, Element el, Definition def, ExtensionRegistry extReg) throws WSDLException {
        Schema schema = null;
        SchemaReference schemaRef = null;
        try {
            QName elementType = QNameUtils.newQName(el);
            ExtensionDeserializer exDS = extReg.queryDeserializer(parentType, elementType);
            ExtensibilityElement ee = exDS.unmarshall(parentType, elementType, el, def, extReg);
            if (!(ee instanceof Schema)) {
                return ee;
            }
            schema = (Schema)ee;
            if (schema.getDocumentBaseURI() != null) {
                this.allSchemas.put(schema.getDocumentBaseURI(), schema);
            }
            ArrayList allSchemaRefs = new ArrayList();
            Collection ic = schema.getImports().values();
            Iterator importsIterator = ic.iterator();
            while (importsIterator.hasNext()) {
                allSchemaRefs.addAll((Collection)importsIterator.next());
            }
            allSchemaRefs.addAll(schema.getIncludes());
            allSchemaRefs.addAll(schema.getRedefines());
            ListIterator schemaRefIterator = allSchemaRefs.listIterator();
            while (schemaRefIterator.hasNext()) {
                try {
                    schemaRef = (SchemaReference)schemaRefIterator.next();
                    if (schemaRef.getSchemaLocationURI() == null) continue;
                    if (this.verbose) {
                        System.out.println("Retrieving schema at '" + schemaRef.getSchemaLocationURI() + (schema.getDocumentBaseURI() == null ? "'." : "', relative to '" + schema.getDocumentBaseURI() + "'."));
                    }
                    InputStream inputStream = null;
                    InputSource inputSource = null;
                    Schema referencedSchema = null;
                    String location = null;
                    if (this.loc != null) {
                        inputSource = this.loc.getImportInputSource(schema.getDocumentBaseURI(), schemaRef.getSchemaLocationURI());
                        if (inputSource == null) {
                            throw new WSDLException("OTHER_ERROR", "Unable to locate with a locator the schema referenced at '" + schemaRef.getSchemaLocationURI() + "' relative to document base '" + schema.getDocumentBaseURI() + "'");
                        }
                        location = this.loc.getLatestImportURI();
                        referencedSchema = (Schema)this.allSchemas.get(location);
                    } else {
                        String contextURI = schema.getDocumentBaseURI();
                        URL contextURL = contextURI != null ? StringUtils.getURL(null, contextURI) : null;
                        URL url = StringUtils.getURL(contextURL, schemaRef.getSchemaLocationURI());
                        location = url.toExternalForm();
                        referencedSchema = (Schema)this.allSchemas.get(location);
                        if (referencedSchema == null) {
                            inputStream = StringUtils.getContentAsInputStream(url);
                            if (inputStream != null) {
                                inputSource = new InputSource(inputStream);
                            }
                            if (inputSource == null) {
                                throw new WSDLException("OTHER_ERROR", "Unable to locate with a url the document referenced at '" + schemaRef.getSchemaLocationURI() + "'" + (contextURI == null ? "." : ", relative to '" + contextURI + "'."));
                            }
                        }
                    }
                    if (referencedSchema == null) {
                        Element documentElement;
                        QName docElementQName;
                        inputSource.setSystemId(location);
                        Document doc = WSDLReaderImpl.getDocument(inputSource, location);
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        if (SchemaConstants.XSD_QNAME_LIST.contains(docElementQName = QNameUtils.newQName(documentElement = doc.getDocumentElement()))) {
                            WSDLFactory factory = this.getWSDLFactory();
                            Definition dummyDef = factory.newDefinition();
                            dummyDef.setDocumentBaseURI(location);
                            referencedSchema = (Schema)this.parseSchema(parentType, documentElement, dummyDef, extReg);
                        }
                    }
                    schemaRef.setReferencedSchema(referencedSchema);
                }
                catch (WSDLException e) {
                    throw e;
                }
                catch (RuntimeException e) {
                    throw e;
                }
                catch (Exception e) {
                    throw new WSDLException("OTHER_ERROR", "An error occurred trying to resolve schema referenced at '" + schemaRef.getSchemaLocationURI() + "'" + (schema.getDocumentBaseURI() == null ? "." : ", relative to '" + schema.getDocumentBaseURI() + "'."), e);
                }
            }
            return schema;
        }
        catch (WSDLException e) {
            if (e.getLocation() == null) {
                e.setLocation(XPathUtils.getXPathExprFromNode(el));
            } else {
                String loc = XPathUtils.getXPathExprFromNode(el) + e.getLocation();
                e.setLocation(loc);
            }
            throw e;
        }
    }

    protected Binding parseBinding(Element bindingEl, Definition def) throws WSDLException {
        Binding binding = null;
        List remainingAttrs = DOMUtils.getAttributes(bindingEl);
        String name = DOMUtils.getAttribute(bindingEl, "name", remainingAttrs);
        QName portTypeName = WSDLReaderImpl.getQualifiedAttributeValue(bindingEl, "type", "binding", def, remainingAttrs);
        PortType portType = null;
        if (name != null) {
            QName bindingName = new QName(def.getTargetNamespace(), name);
            binding = def.getBinding(bindingName);
            if (binding == null) {
                binding = def.createBinding();
                binding.setQName(bindingName);
            }
        } else {
            binding = def.createBinding();
        }
        binding.setUndefined(false);
        if (portTypeName != null) {
            portType = def.getPortType(portTypeName);
            if (portType == null) {
                portType = def.createPortType();
                portType.setQName(portTypeName);
                def.addPortType(portType);
            }
            binding.setPortType(portType);
        }
        NamedNodeMap attrs = bindingEl.getAttributes();
        WSDLReaderImpl.registerNSDeclarations(attrs, def);
        Element tempEl = DOMUtils.getFirstChildElement(bindingEl);
        while (tempEl != null) {
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl)) {
                binding.setDocumentationElement(tempEl);
            } else if (QNameUtils.matches(Constants.Q_ELEM_OPERATION, tempEl)) {
                binding.addBindingOperation(this.parseBindingOperation(tempEl, portType, def));
            } else {
                binding.addExtensibilityElement(this.parseExtensibilityElement(Binding.class, tempEl, def));
            }
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        this.parseExtensibilityAttributes(bindingEl, Binding.class, binding, def);
        return binding;
    }

    protected BindingOperation parseBindingOperation(Element bindingOperationEl, PortType portType, Definition def) throws WSDLException {
        BindingOperation bindingOperation = def.createBindingOperation();
        List remainingAttrs = DOMUtils.getAttributes(bindingOperationEl);
        String name = DOMUtils.getAttribute(bindingOperationEl, "name", remainingAttrs);
        if (name != null) {
            bindingOperation.setName(name);
        }
        NamedNodeMap attrs = bindingOperationEl.getAttributes();
        WSDLReaderImpl.registerNSDeclarations(attrs, def);
        Element tempEl = DOMUtils.getFirstChildElement(bindingOperationEl);
        while (tempEl != null) {
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl)) {
                bindingOperation.setDocumentationElement(tempEl);
            } else if (QNameUtils.matches(Constants.Q_ELEM_INPUT, tempEl)) {
                bindingOperation.setBindingInput(this.parseBindingInput(tempEl, def));
            } else if (QNameUtils.matches(Constants.Q_ELEM_OUTPUT, tempEl)) {
                bindingOperation.setBindingOutput(this.parseBindingOutput(tempEl, def));
            } else if (QNameUtils.matches(Constants.Q_ELEM_FAULT, tempEl)) {
                bindingOperation.addBindingFault(this.parseBindingFault(tempEl, def));
            } else {
                bindingOperation.addExtensibilityElement(this.parseExtensibilityElement(BindingOperation.class, tempEl, def));
            }
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        if (portType != null) {
            String inputName;
            BindingInput bindingInput = bindingOperation.getBindingInput();
            BindingOutput bindingOutput = bindingOperation.getBindingOutput();
            String string = bindingInput != null ? (bindingInput.getName() != null ? bindingInput.getName() : ":none") : (inputName = null);
            String outputName = bindingOutput != null ? (bindingOutput.getName() != null ? bindingOutput.getName() : ":none") : null;
            Operation op = portType.getOperation(name, inputName, outputName);
            if (op == null) {
                if (":none".equals(inputName) && ":none".equals(outputName)) {
                    op = portType.getOperation(name, null, null);
                } else if (":none".equals(inputName)) {
                    op = portType.getOperation(name, null, outputName);
                } else if (":none".equals(outputName)) {
                    op = portType.getOperation(name, inputName, null);
                }
            }
            if (op == null) {
                Input input = def.createInput();
                Output output = def.createOutput();
                op = def.createOperation();
                op.setName(name);
                input.setName(inputName);
                output.setName(outputName);
                op.setInput(input);
                op.setOutput(output);
                portType.addOperation(op);
            }
            bindingOperation.setOperation(op);
        }
        this.parseExtensibilityAttributes(bindingOperationEl, BindingOperation.class, bindingOperation, def);
        return bindingOperation;
    }

    protected BindingInput parseBindingInput(Element bindingInputEl, Definition def) throws WSDLException {
        BindingInput bindingInput = def.createBindingInput();
        List remainingAttrs = DOMUtils.getAttributes(bindingInputEl);
        String name = DOMUtils.getAttribute(bindingInputEl, "name", remainingAttrs);
        if (name != null) {
            bindingInput.setName(name);
        }
        NamedNodeMap attrs = bindingInputEl.getAttributes();
        WSDLReaderImpl.registerNSDeclarations(attrs, def);
        Element tempEl = DOMUtils.getFirstChildElement(bindingInputEl);
        while (tempEl != null) {
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl)) {
                bindingInput.setDocumentationElement(tempEl);
            } else {
                bindingInput.addExtensibilityElement(this.parseExtensibilityElement(BindingInput.class, tempEl, def));
            }
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        this.parseExtensibilityAttributes(bindingInputEl, BindingInput.class, bindingInput, def);
        return bindingInput;
    }

    protected BindingOutput parseBindingOutput(Element bindingOutputEl, Definition def) throws WSDLException {
        BindingOutput bindingOutput = def.createBindingOutput();
        List remainingAttrs = DOMUtils.getAttributes(bindingOutputEl);
        String name = DOMUtils.getAttribute(bindingOutputEl, "name", remainingAttrs);
        if (name != null) {
            bindingOutput.setName(name);
        }
        NamedNodeMap attrs = bindingOutputEl.getAttributes();
        WSDLReaderImpl.registerNSDeclarations(attrs, def);
        Element tempEl = DOMUtils.getFirstChildElement(bindingOutputEl);
        while (tempEl != null) {
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl)) {
                bindingOutput.setDocumentationElement(tempEl);
            } else {
                bindingOutput.addExtensibilityElement(this.parseExtensibilityElement(BindingOutput.class, tempEl, def));
            }
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        this.parseExtensibilityAttributes(bindingOutputEl, BindingOutput.class, bindingOutput, def);
        return bindingOutput;
    }

    protected BindingFault parseBindingFault(Element bindingFaultEl, Definition def) throws WSDLException {
        BindingFault bindingFault = def.createBindingFault();
        List remainingAttrs = DOMUtils.getAttributes(bindingFaultEl);
        String name = DOMUtils.getAttribute(bindingFaultEl, "name", remainingAttrs);
        if (name != null) {
            bindingFault.setName(name);
        }
        NamedNodeMap attrs = bindingFaultEl.getAttributes();
        WSDLReaderImpl.registerNSDeclarations(attrs, def);
        Element tempEl = DOMUtils.getFirstChildElement(bindingFaultEl);
        while (tempEl != null) {
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl)) {
                bindingFault.setDocumentationElement(tempEl);
            } else {
                bindingFault.addExtensibilityElement(this.parseExtensibilityElement(BindingFault.class, tempEl, def));
            }
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        this.parseExtensibilityAttributes(bindingFaultEl, BindingFault.class, bindingFault, def);
        return bindingFault;
    }

    protected Message parseMessage(Element msgEl, Definition def) throws WSDLException {
        Message msg = null;
        List remainingAttrs = DOMUtils.getAttributes(msgEl);
        String name = DOMUtils.getAttribute(msgEl, "name", remainingAttrs);
        if (name != null) {
            QName messageName = new QName(def.getTargetNamespace(), name);
            msg = def.getMessage(messageName);
            if (msg == null) {
                msg = def.createMessage();
                msg.setQName(messageName);
            }
        } else {
            msg = def.createMessage();
        }
        msg.setUndefined(false);
        NamedNodeMap attrs = msgEl.getAttributes();
        WSDLReaderImpl.registerNSDeclarations(attrs, def);
        Element tempEl = DOMUtils.getFirstChildElement(msgEl);
        while (tempEl != null) {
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl)) {
                msg.setDocumentationElement(tempEl);
            } else if (QNameUtils.matches(Constants.Q_ELEM_PART, tempEl)) {
                msg.addPart(this.parsePart(tempEl, def));
            } else {
                msg.addExtensibilityElement(this.parseExtensibilityElement(Message.class, tempEl, def));
            }
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        this.parseExtensibilityAttributes(msgEl, Message.class, msg, def);
        return msg;
    }

    protected Part parsePart(Element partEl, Definition def) throws WSDLException {
        Part part = def.createPart();
        String name = DOMUtils.getAttribute(partEl, "name");
        QName elementName = WSDLReaderImpl.getQualifiedAttributeValue(partEl, "element", "message", def);
        QName typeName = WSDLReaderImpl.getQualifiedAttributeValue(partEl, "type", "message", def);
        if (name != null) {
            part.setName(name);
        }
        if (elementName != null) {
            part.setElementName(elementName);
        }
        if (typeName != null) {
            part.setTypeName(typeName);
        }
        NamedNodeMap attrs = partEl.getAttributes();
        WSDLReaderImpl.registerNSDeclarations(attrs, def);
        Element tempEl = DOMUtils.getFirstChildElement(partEl);
        while (tempEl != null) {
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl)) {
                part.setDocumentationElement(tempEl);
            } else {
                part.addExtensibilityElement(this.parseExtensibilityElement(Part.class, tempEl, def));
            }
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        this.parseExtensibilityAttributes(partEl, Part.class, part, def);
        return part;
    }

    protected void parseExtensibilityAttributes(Element el, Class parentType, AttributeExtensible attrExt, Definition def) throws WSDLException {
        if (attrExt == null) {
            return;
        }
        List nativeAttributeNames = attrExt.getNativeAttributeNames();
        NamedNodeMap nodeMap = el.getAttributes();
        int length = nodeMap.getLength();
        for (int i = 0; i < length; ++i) {
            Attr attribute = (Attr)nodeMap.item(i);
            String localName = attribute.getLocalName();
            String namespaceURI = attribute.getNamespaceURI();
            String prefix = attribute.getPrefix();
            QName qname = new QName(namespaceURI, localName);
            if (namespaceURI != null && !namespaceURI.equals("http://schemas.xmlsoap.org/wsdl/")) {
                if (namespaceURI.equals("http://www.w3.org/2000/xmlns/")) continue;
                DOMUtils.registerUniquePrefix(prefix, namespaceURI, def);
                String strValue = attribute.getValue();
                int attrType = -1;
                ExtensionRegistry extReg = def.getExtensionRegistry();
                if (extReg != null) {
                    attrType = extReg.queryExtensionAttributeType(parentType, qname);
                }
                Object val = this.parseExtensibilityAttribute(el, attrType, strValue, def);
                attrExt.setExtensionAttribute(qname, val);
                continue;
            }
            if (nativeAttributeNames.contains(localName)) continue;
            WSDLException wsdlExc = new WSDLException("INVALID_WSDL", "Encountered illegal extension attribute '" + qname + "'. Extension " + "attributes must be in " + "a namespace other than " + "WSDL's.");
            wsdlExc.setLocation(XPathUtils.getXPathExprFromNode(el));
            throw wsdlExc;
        }
    }

    protected Object parseExtensibilityAttribute(Element el, int attrType, String attrValue, Definition def) throws WSDLException {
        if (attrType == 1) {
            return DOMUtils.getQName(attrValue, el, def);
        }
        if (attrType == 2) {
            return StringUtils.parseNMTokens(attrValue);
        }
        if (attrType == 3) {
            List oldList = StringUtils.parseNMTokens(attrValue);
            int size = oldList.size();
            Vector<QName> newList = new Vector<QName>(size);
            for (int i = 0; i < size; ++i) {
                String str = (String)oldList.get(i);
                QName qValue = DOMUtils.getQName(str, el, def);
                newList.add(qValue);
            }
            return newList;
        }
        if (attrType == 0) {
            return attrValue;
        }
        QName qValue = null;
        try {
            qValue = DOMUtils.getQName(attrValue, el, def);
        }
        catch (WSDLException e) {
            qValue = new QName(attrValue);
        }
        return qValue;
    }

    protected PortType parsePortType(Element portTypeEl, Definition def) throws WSDLException {
        PortType portType = null;
        String name = DOMUtils.getAttribute(portTypeEl, "name");
        if (name != null) {
            QName portTypeName = new QName(def.getTargetNamespace(), name);
            portType = def.getPortType(portTypeName);
            if (portType == null) {
                portType = def.createPortType();
                portType.setQName(portTypeName);
            }
        } else {
            portType = def.createPortType();
        }
        portType.setUndefined(false);
        NamedNodeMap attrs = portTypeEl.getAttributes();
        WSDLReaderImpl.registerNSDeclarations(attrs, def);
        Element tempEl = DOMUtils.getFirstChildElement(portTypeEl);
        while (tempEl != null) {
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl)) {
                portType.setDocumentationElement(tempEl);
            } else if (QNameUtils.matches(Constants.Q_ELEM_OPERATION, tempEl)) {
                Operation op = this.parseOperation(tempEl, portType, def);
                if (op != null) {
                    portType.addOperation(op);
                }
            } else {
                portType.addExtensibilityElement(this.parseExtensibilityElement(PortType.class, tempEl, def));
            }
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        this.parseExtensibilityAttributes(portTypeEl, PortType.class, portType, def);
        return portType;
    }

    protected Operation parseOperation(Element opEl, PortType portType, Definition def) throws WSDLException {
        Operation op = null;
        List remainingAttrs = DOMUtils.getAttributes(opEl);
        String name = DOMUtils.getAttribute(opEl, "name", remainingAttrs);
        String parameterOrderStr = DOMUtils.getAttribute(opEl, "parameterOrder", remainingAttrs);
        NamedNodeMap attrs = opEl.getAttributes();
        WSDLReaderImpl.registerNSDeclarations(attrs, def);
        Element tempEl = DOMUtils.getFirstChildElement(opEl);
        Vector<String> messageOrder = new Vector<String>();
        Element docEl = null;
        Input input = null;
        Output output = null;
        Vector<Fault> faults = new Vector<Fault>();
        Vector<ExtensibilityElement> extElements = new Vector<ExtensibilityElement>();
        boolean retrieved = true;
        while (tempEl != null) {
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl)) {
                docEl = tempEl;
            } else if (QNameUtils.matches(Constants.Q_ELEM_INPUT, tempEl)) {
                input = this.parseInput(tempEl, def);
                messageOrder.add("input");
            } else if (QNameUtils.matches(Constants.Q_ELEM_OUTPUT, tempEl)) {
                output = this.parseOutput(tempEl, def);
                messageOrder.add("output");
            } else if (QNameUtils.matches(Constants.Q_ELEM_FAULT, tempEl)) {
                faults.add(this.parseFault(tempEl, def));
            } else {
                extElements.add(this.parseExtensibilityElement(Operation.class, tempEl, def));
            }
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        if (name != null) {
            Output tempOut;
            Input tempIn;
            String inputName;
            String string = input != null ? (input.getName() != null ? input.getName() : ":none") : (inputName = null);
            String outputName = output != null ? (output.getName() != null ? output.getName() : ":none") : null;
            op = portType.getOperation(name, inputName, outputName);
            if (op != null && !op.isUndefined()) {
                op = null;
            }
            if (op != null && inputName == null && (tempIn = op.getInput()) != null && tempIn.getName() != null) {
                op = null;
            }
            if (op != null && outputName == null && (tempOut = op.getOutput()) != null && tempOut.getName() != null) {
                op = null;
            }
            if (op == null) {
                op = def.createOperation();
                op.setName(name);
                retrieved = false;
            }
        } else {
            op = def.createOperation();
            retrieved = false;
        }
        op.setUndefined(false);
        if (parameterOrderStr != null) {
            op.setParameterOrdering(StringUtils.parseNMTokens(parameterOrderStr));
        }
        if (docEl != null) {
            op.setDocumentationElement(docEl);
        }
        if (input != null) {
            op.setInput(input);
        }
        if (output != null) {
            op.setOutput(output);
        }
        if (faults.size() > 0) {
            Iterator faultIterator = faults.iterator();
            while (faultIterator.hasNext()) {
                op.addFault((Fault)faultIterator.next());
            }
        }
        if (extElements.size() > 0) {
            Iterator eeIterator = extElements.iterator();
            while (eeIterator.hasNext()) {
                op.addExtensibilityElement((ExtensibilityElement)eeIterator.next());
            }
        }
        OperationType style = null;
        if (((Object)messageOrder).equals(STYLE_ONE_WAY)) {
            style = OperationType.ONE_WAY;
        } else if (((Object)messageOrder).equals(STYLE_REQUEST_RESPONSE)) {
            style = OperationType.REQUEST_RESPONSE;
        } else if (((Object)messageOrder).equals(STYLE_SOLICIT_RESPONSE)) {
            style = OperationType.SOLICIT_RESPONSE;
        } else if (((Object)messageOrder).equals(STYLE_NOTIFICATION)) {
            style = OperationType.NOTIFICATION;
        }
        if (style != null) {
            op.setStyle(style);
        }
        this.parseExtensibilityAttributes(opEl, Operation.class, op, def);
        if (retrieved) {
            op = null;
        }
        return op;
    }

    protected Service parseService(Element serviceEl, Definition def) throws WSDLException {
        Service service = def.createService();
        List remainingAttrs = DOMUtils.getAttributes(serviceEl);
        String name = DOMUtils.getAttribute(serviceEl, "name", remainingAttrs);
        if (name != null) {
            service.setQName(new QName(def.getTargetNamespace(), name));
        }
        NamedNodeMap attrs = serviceEl.getAttributes();
        WSDLReaderImpl.registerNSDeclarations(attrs, def);
        Element tempEl = DOMUtils.getFirstChildElement(serviceEl);
        while (tempEl != null) {
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl)) {
                service.setDocumentationElement(tempEl);
            } else if (QNameUtils.matches(Constants.Q_ELEM_PORT, tempEl)) {
                service.addPort(this.parsePort(tempEl, def));
            } else {
                service.addExtensibilityElement(this.parseExtensibilityElement(Service.class, tempEl, def));
            }
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        this.parseExtensibilityAttributes(serviceEl, Service.class, service, def);
        return service;
    }

    protected Port parsePort(Element portEl, Definition def) throws WSDLException {
        Port port = def.createPort();
        List remainingAttrs = DOMUtils.getAttributes(portEl);
        String name = DOMUtils.getAttribute(portEl, "name", remainingAttrs);
        QName bindingStr = WSDLReaderImpl.getQualifiedAttributeValue(portEl, "binding", "port", def, remainingAttrs);
        if (name != null) {
            port.setName(name);
        }
        if (bindingStr != null) {
            Binding binding = def.getBinding(bindingStr);
            if (binding == null) {
                binding = def.createBinding();
                binding.setQName(bindingStr);
                def.addBinding(binding);
            }
            port.setBinding(binding);
        }
        NamedNodeMap attrs = portEl.getAttributes();
        WSDLReaderImpl.registerNSDeclarations(attrs, def);
        Element tempEl = DOMUtils.getFirstChildElement(portEl);
        while (tempEl != null) {
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl)) {
                port.setDocumentationElement(tempEl);
            } else {
                port.addExtensibilityElement(this.parseExtensibilityElement(Port.class, tempEl, def));
            }
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        this.parseExtensibilityAttributes(portEl, Port.class, port, def);
        return port;
    }

    protected ExtensibilityElement parseExtensibilityElement(Class parentType, Element el, Definition def) throws WSDLException {
        QName elementType = QNameUtils.newQName(el);
        String namespaceURI = el.getNamespaceURI();
        try {
            if (namespaceURI == null || namespaceURI.equals("http://schemas.xmlsoap.org/wsdl/")) {
                throw new WSDLException("INVALID_WSDL", "Encountered illegal extension element '" + elementType + "' in the context of a '" + parentType.getName() + "'. Extension elements must be in " + "a namespace other than WSDL's.");
            }
            ExtensionRegistry extReg = def.getExtensionRegistry();
            if (extReg == null) {
                throw new WSDLException("CONFIGURATION_ERROR", "No ExtensionRegistry set for this Definition, so unable to deserialize a '" + elementType + "' element in the " + "context of a '" + parentType.getName() + "'.");
            }
            ExtensionDeserializer extDS = extReg.queryDeserializer(parentType, elementType);
            NamedNodeMap attrs = el.getAttributes();
            WSDLReaderImpl.registerNSDeclarations(attrs, def);
            return extDS.unmarshall(parentType, elementType, el, def, extReg);
        }
        catch (WSDLException e) {
            if (e.getLocation() == null) {
                e.setLocation(XPathUtils.getXPathExprFromNode(el));
            }
            throw e;
        }
    }

    protected ExtensibilityElement parseExtensibilityElementAsDefaultExtensiblityElement(Class parentType, Element el, Definition def) throws WSDLException {
        QName elementType = QNameUtils.newQName(el);
        String namespaceURI = el.getNamespaceURI();
        try {
            if (namespaceURI == null || namespaceURI.equals("http://schemas.xmlsoap.org/wsdl/")) {
                throw new WSDLException("INVALID_WSDL", "Encountered illegal extension element '" + elementType + "' in the context of a '" + parentType.getName() + "'. Extension elements must be in " + "a namespace other than WSDL's.");
            }
            ExtensionRegistry extReg = def.getExtensionRegistry();
            if (extReg == null) {
                throw new WSDLException("CONFIGURATION_ERROR", "No ExtensionRegistry set for this Definition, so unable to deserialize a '" + elementType + "' element in the " + "context of a '" + parentType.getName() + "'.");
            }
            ExtensionDeserializer extDS = extReg.getDefaultDeserializer();
            NamedNodeMap attrs = el.getAttributes();
            WSDLReaderImpl.registerNSDeclarations(attrs, def);
            return extDS.unmarshall(parentType, elementType, el, def, extReg);
        }
        catch (WSDLException e) {
            if (e.getLocation() == null) {
                e.setLocation(XPathUtils.getXPathExprFromNode(el));
            }
            throw e;
        }
    }

    protected Input parseInput(Element inputEl, Definition def) throws WSDLException {
        Input input = def.createInput();
        String name = DOMUtils.getAttribute(inputEl, "name");
        QName messageName = WSDLReaderImpl.getQualifiedAttributeValue(inputEl, "message", "input", def);
        if (name != null) {
            input.setName(name);
        }
        if (messageName != null) {
            Message message = def.getMessage(messageName);
            if (message == null) {
                message = def.createMessage();
                message.setQName(messageName);
                def.addMessage(message);
            }
            input.setMessage(message);
        }
        NamedNodeMap attrs = inputEl.getAttributes();
        WSDLReaderImpl.registerNSDeclarations(attrs, def);
        Element tempEl = DOMUtils.getFirstChildElement(inputEl);
        while (tempEl != null) {
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl)) {
                input.setDocumentationElement(tempEl);
            } else {
                input.addExtensibilityElement(this.parseExtensibilityElement(Input.class, tempEl, def));
            }
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        this.parseExtensibilityAttributes(inputEl, Input.class, input, def);
        return input;
    }

    protected Output parseOutput(Element outputEl, Definition def) throws WSDLException {
        Output output = def.createOutput();
        String name = DOMUtils.getAttribute(outputEl, "name");
        QName messageName = WSDLReaderImpl.getQualifiedAttributeValue(outputEl, "message", "output", def);
        if (name != null) {
            output.setName(name);
        }
        if (messageName != null) {
            Message message = def.getMessage(messageName);
            if (message == null) {
                message = def.createMessage();
                message.setQName(messageName);
                def.addMessage(message);
            }
            output.setMessage(message);
        }
        NamedNodeMap attrs = outputEl.getAttributes();
        WSDLReaderImpl.registerNSDeclarations(attrs, def);
        Element tempEl = DOMUtils.getFirstChildElement(outputEl);
        while (tempEl != null) {
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl)) {
                output.setDocumentationElement(tempEl);
            } else {
                output.addExtensibilityElement(this.parseExtensibilityElement(Output.class, tempEl, def));
            }
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        this.parseExtensibilityAttributes(outputEl, Output.class, output, def);
        return output;
    }

    protected Fault parseFault(Element faultEl, Definition def) throws WSDLException {
        Fault fault = def.createFault();
        String name = DOMUtils.getAttribute(faultEl, "name");
        QName messageName = WSDLReaderImpl.getQualifiedAttributeValue(faultEl, "message", "fault", def);
        if (name != null) {
            fault.setName(name);
        }
        if (messageName != null) {
            Message message = def.getMessage(messageName);
            if (message == null) {
                message = def.createMessage();
                message.setQName(messageName);
                def.addMessage(message);
            }
            fault.setMessage(message);
        }
        NamedNodeMap attrs = faultEl.getAttributes();
        WSDLReaderImpl.registerNSDeclarations(attrs, def);
        Element tempEl = DOMUtils.getFirstChildElement(faultEl);
        while (tempEl != null) {
            if (QNameUtils.matches(Constants.Q_ELEM_DOCUMENTATION, tempEl)) {
                fault.setDocumentationElement(tempEl);
            } else {
                fault.addExtensibilityElement(this.parseExtensibilityElement(Fault.class, tempEl, def));
            }
            tempEl = DOMUtils.getNextSiblingElement(tempEl);
        }
        this.parseExtensibilityAttributes(faultEl, Fault.class, fault, def);
        return fault;
    }

    private static QName getQualifiedAttributeValue(Element el, String attrName, String elDesc, Definition def) throws WSDLException {
        try {
            return DOMUtils.getQualifiedAttributeValue(el, attrName, elDesc, false, def);
        }
        catch (WSDLException e) {
            if (e.getFaultCode().equals("NO_PREFIX_SPECIFIED")) {
                String attrValue = DOMUtils.getAttribute(el, attrName);
                return new QName(attrValue);
            }
            throw e;
        }
    }

    private static QName getQualifiedAttributeValue(Element el, String attrName, String elDesc, Definition def, List remainingAttrs) throws WSDLException {
        try {
            return DOMUtils.getQualifiedAttributeValue(el, attrName, elDesc, false, def, remainingAttrs);
        }
        catch (WSDLException e) {
            if (e.getFaultCode().equals("NO_PREFIX_SPECIFIED")) {
                String attrValue = DOMUtils.getAttribute(el, attrName, remainingAttrs);
                return new QName(attrValue);
            }
            throw e;
        }
    }

    private static void checkElementName(Element el, QName qname) throws WSDLException {
        if (!QNameUtils.matches(qname, el)) {
            WSDLException wsdlExc = new WSDLException("INVALID_WSDL", "Expected element '" + qname + "'.");
            wsdlExc.setLocation(XPathUtils.getXPathExprFromNode(el));
            throw wsdlExc;
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

    private static void registerNSDeclarations(NamedNodeMap attrs, Definition def) {
        int size = attrs.getLength();
        for (int i = 0; i < size; ++i) {
            Attr attr = (Attr)attrs.item(i);
            String namespaceURI = attr.getNamespaceURI();
            String localPart = attr.getLocalName();
            String value = attr.getValue();
            if (namespaceURI == null || !namespaceURI.equals("http://www.w3.org/2000/xmlns/")) continue;
            if (localPart != null && !localPart.equals("xmlns")) {
                DOMUtils.registerUniquePrefix(localPart, value, def);
                continue;
            }
            DOMUtils.registerUniquePrefix(null, value, def);
        }
    }

    public Definition readWSDL(String wsdlURI) throws WSDLException {
        return this.readWSDL(null, wsdlURI);
    }

    public Definition readWSDL(String contextURI, String wsdlURI) throws WSDLException {
        try {
            if (this.verbose) {
                System.out.println("Retrieving document at '" + wsdlURI + "'" + (contextURI == null ? "." : ", relative to '" + contextURI + "'."));
            }
            URL contextURL = contextURI != null ? StringUtils.getURL(null, contextURI) : null;
            URL url = StringUtils.getURL(contextURL, wsdlURI);
            InputStream inputStream = StringUtils.getContentAsInputStream(url);
            InputSource inputSource = new InputSource(inputStream);
            inputSource.setSystemId(url.toString());
            Document doc = WSDLReaderImpl.getDocument(inputSource, url.toString());
            inputStream.close();
            Definition def = this.readWSDL(url.toString(), doc);
            return def;
        }
        catch (WSDLException e) {
            throw e;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new WSDLException("OTHER_ERROR", "Unable to resolve imported document at '" + wsdlURI + (contextURI == null ? "'." : "', relative to '" + contextURI + "'."), e);
        }
    }

    public Definition readWSDL(String documentBaseURI, Element definitionsElement) throws WSDLException {
        return this.readWSDL(documentBaseURI, definitionsElement, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Definition readWSDL(WSDLLocator locator, Element definitionsElement) throws WSDLException {
        try {
            this.loc = locator;
            Definition definition = this.readWSDL(locator.getBaseURI(), definitionsElement, null);
            return definition;
        }
        finally {
            locator.close();
            this.loc = null;
        }
    }

    protected Definition readWSDL(String documentBaseURI, Element definitionsElement, Map importedDefs) throws WSDLException {
        return this.parseDefinitions(documentBaseURI, definitionsElement, importedDefs);
    }

    public Definition readWSDL(String documentBaseURI, Document wsdlDocument) throws WSDLException {
        return this.readWSDL(documentBaseURI, wsdlDocument.getDocumentElement());
    }

    public Definition readWSDL(String documentBaseURI, InputSource inputSource) throws WSDLException {
        String location = inputSource.getSystemId() != null ? inputSource.getSystemId() : "- WSDL Document -";
        return this.readWSDL(documentBaseURI, WSDLReaderImpl.getDocument(inputSource, location));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Definition readWSDL(WSDLLocator locator) throws WSDLException {
        InputSource is = locator.getBaseInputSource();
        String base = locator.getBaseURI();
        if (is == null) {
            throw new WSDLException("OTHER_ERROR", "Unable to locate document at '" + base + "'.");
        }
        is.setSystemId(base);
        this.loc = locator;
        if (this.verbose) {
            System.out.println("Retrieving document at '" + base + "'.");
        }
        try {
            Definition definition = this.readWSDL(base, is);
            return definition;
        }
        finally {
            this.loc.close();
            this.loc = null;
        }
    }
}

