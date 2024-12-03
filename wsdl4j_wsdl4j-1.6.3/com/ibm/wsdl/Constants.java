/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl;

import javax.xml.namespace.QName;

public class Constants {
    public static final String NS_URI_WSDL = "http://schemas.xmlsoap.org/wsdl/";
    public static final String NS_URI_XMLNS = "http://www.w3.org/2000/xmlns/";
    public static final String ELEM_DEFINITIONS = "definitions";
    public static final String ELEM_IMPORT = "import";
    public static final String ELEM_TYPES = "types";
    public static final String ELEM_MESSAGE = "message";
    public static final String ELEM_PORT_TYPE = "portType";
    public static final String ELEM_BINDING = "binding";
    public static final String ELEM_SERVICE = "service";
    public static final String ELEM_PART = "part";
    public static final String ELEM_OPERATION = "operation";
    public static final String ELEM_INPUT = "input";
    public static final String ELEM_OUTPUT = "output";
    public static final String ELEM_FAULT = "fault";
    public static final String ELEM_PORT = "port";
    public static final String ELEM_DOCUMENTATION = "documentation";
    public static final QName Q_ELEM_DEFINITIONS = new QName("http://schemas.xmlsoap.org/wsdl/", "definitions");
    public static final QName Q_ELEM_IMPORT = new QName("http://schemas.xmlsoap.org/wsdl/", "import");
    public static final QName Q_ELEM_TYPES = new QName("http://schemas.xmlsoap.org/wsdl/", "types");
    public static final QName Q_ELEM_MESSAGE = new QName("http://schemas.xmlsoap.org/wsdl/", "message");
    public static final QName Q_ELEM_PORT_TYPE = new QName("http://schemas.xmlsoap.org/wsdl/", "portType");
    public static final QName Q_ELEM_BINDING = new QName("http://schemas.xmlsoap.org/wsdl/", "binding");
    public static final QName Q_ELEM_SERVICE = new QName("http://schemas.xmlsoap.org/wsdl/", "service");
    public static final QName Q_ELEM_PART = new QName("http://schemas.xmlsoap.org/wsdl/", "part");
    public static final QName Q_ELEM_OPERATION = new QName("http://schemas.xmlsoap.org/wsdl/", "operation");
    public static final QName Q_ELEM_INPUT = new QName("http://schemas.xmlsoap.org/wsdl/", "input");
    public static final QName Q_ELEM_OUTPUT = new QName("http://schemas.xmlsoap.org/wsdl/", "output");
    public static final QName Q_ELEM_FAULT = new QName("http://schemas.xmlsoap.org/wsdl/", "fault");
    public static final QName Q_ELEM_PORT = new QName("http://schemas.xmlsoap.org/wsdl/", "port");
    public static final QName Q_ELEM_DOCUMENTATION = new QName("http://schemas.xmlsoap.org/wsdl/", "documentation");
    public static final String ATTR_NAME = "name";
    public static final String ATTR_TARGET_NAMESPACE = "targetNamespace";
    public static final String ATTR_ELEMENT = "element";
    public static final String ATTR_TYPE = "type";
    public static final String ATTR_MESSAGE = "message";
    public static final String ATTR_PARAMETER_ORDER = "parameterOrder";
    public static final String ATTR_BINDING = "binding";
    public static final String ATTR_XMLNS = "xmlns";
    public static final String ATTR_NAMESPACE = "namespace";
    public static final String ATTR_LOCATION = "location";
    public static final String ATTR_REQUIRED = "required";
    public static final String[] DEFINITION_ATTR_NAMES = new String[]{"name", "targetNamespace"};
    public static final String[] PART_ATTR_NAMES = new String[]{"name", "type", "element"};
    public static final String[] BINDING_ATTR_NAMES = new String[]{"name", "type"};
    public static final String[] BINDING_FAULT_ATTR_NAMES = new String[]{"name"};
    public static final String[] BINDING_INPUT_ATTR_NAMES = new String[]{"name"};
    public static final String[] BINDING_OPERATION_ATTR_NAMES = new String[]{"name"};
    public static final String[] BINDING_OUTPUT_ATTR_NAMES = new String[]{"name"};
    public static final String[] FAULT_ATTR_NAMES = new String[]{"name", "message"};
    public static final String[] IMPORT_ATTR_NAMES = new String[]{"namespace", "location"};
    public static final String[] INPUT_ATTR_NAMES = new String[]{"name", "message"};
    public static final String[] MESSAGE_ATTR_NAMES = new String[]{"name"};
    public static final String[] OPERATION_ATTR_NAMES = new String[]{"name", "parameterOrder"};
    public static final String[] OUTPUT_ATTR_NAMES = new String[]{"name", "message"};
    public static final String[] PORT_ATTR_NAMES = new String[]{"name", "binding"};
    public static final String[] PORT_TYPE_ATTR_NAMES = new String[]{"name"};
    public static final String[] SERVICE_ATTR_NAMES = new String[]{"name"};
    public static final String[] TYPES_ATTR_NAMES = new String[0];
    public static final QName Q_ATTR_REQUIRED = new QName("http://schemas.xmlsoap.org/wsdl/", "required");
    public static final String XML_DECL_DEFAULT = "UTF-8";
    public static final String XML_DECL_START = "<?xml version=\"1.0\" encoding=\"";
    public static final String XML_DECL_END = "\"?>";
    public static final String FEATURE_VERBOSE = "javax.wsdl.verbose";
    public static final String FEATURE_IMPORT_DOCUMENTS = "javax.wsdl.importDocuments";
    public static final String FEATURE_PARSE_SCHEMA = "com.ibm.wsdl.parseXMLSchemas";
    public static final String NONE = ":none";
}

