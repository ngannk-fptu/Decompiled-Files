/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.Binding
 *  javax.wsdl.BindingInput
 *  javax.wsdl.BindingOperation
 *  javax.wsdl.Operation
 *  javax.wsdl.Part
 *  javax.wsdl.Port
 *  javax.wsdl.PortType
 *  javax.wsdl.Service
 *  javax.wsdl.extensions.mime.MIMEMultipartRelated
 *  javax.wsdl.extensions.mime.MIMEPart
 *  javax.wsdl.extensions.soap.SOAPAddress
 *  javax.wsdl.extensions.soap.SOAPBody
 *  javax.wsdl.extensions.soap.SOAPOperation
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.client;

import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.wsdl.Binding;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.extensions.mime.MIMEMultipartRelated;
import javax.wsdl.extensions.mime.MIMEPart;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.ParameterMode;
import javax.xml.soap.SOAPException;
import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.AxisProperties;
import org.apache.axis.Handler;
import org.apache.axis.InternalException;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.SOAPPart;
import org.apache.axis.attachments.Attachments;
import org.apache.axis.client.Service;
import org.apache.axis.client.Transport;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.description.FaultDesc;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.encoding.DeserializerFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.SerializerFactory;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.encoding.ser.BaseDeserializerFactory;
import org.apache.axis.encoding.ser.BaseSerializerFactory;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCHeaderParam;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPFault;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.LockableHashtable;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.FaultInfo;
import org.apache.axis.wsdl.symbolTable.Parameter;
import org.apache.axis.wsdl.symbolTable.Parameters;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.toJava.Utils;
import org.apache.commons.logging.Log;

public class Call
implements javax.xml.rpc.Call {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$client$Call == null ? (class$org$apache$axis$client$Call = Call.class$("org.apache.axis.client.Call")) : class$org$apache$axis$client$Call).getName());
    private static Log tlog = LogFactory.getLog("org.apache.axis.TIME");
    protected static Log entLog = LogFactory.getLog("org.apache.axis.enterprise");
    private boolean parmAndRetReq = true;
    private Service service = null;
    private QName portName = null;
    private QName portTypeName = null;
    private QName operationName = null;
    private MessageContext msgContext = null;
    private LockableHashtable myProperties = new LockableHashtable();
    private String username = null;
    private String password = null;
    private boolean maintainSession = false;
    private boolean useSOAPAction = false;
    private String SOAPActionURI = null;
    private Integer timeout = null;
    private boolean useStreaming = false;
    private OperationDesc operation = null;
    private boolean operationSetManually = false;
    private boolean invokeOneWay = false;
    private boolean isMsg = false;
    private Transport transport = null;
    private String transportName = null;
    private HashMap outParams = null;
    private ArrayList outParamsList = null;
    private Vector myHeaders = null;
    public static final String SEND_TYPE_ATTR = "sendXsiTypes";
    public static final String TRANSPORT_NAME = "transport_name";
    public static final String CHARACTER_SET_ENCODING = "javax.xml.soap.character-set-encoding";
    public static final String TRANSPORT_PROPERTY = "java.protocol.handler.pkgs";
    public static final String WSDL_SERVICE = "wsdl.service";
    public static final String WSDL_PORT_NAME = "wsdl.portName";
    public static final String JAXRPC_SERVICE = "wsdl.service";
    public static final String JAXRPC_PORTTYPE_NAME = "wsdl.portName";
    public static final String FAULT_ON_NO_RESPONSE = "call.FaultOnNoResponse";
    public static final String CHECK_MUST_UNDERSTAND = "call.CheckMustUnderstand";
    public static final String ATTACHMENT_ENCAPSULATION_FORMAT = "attachment_encapsulation_format";
    public static final String ATTACHMENT_ENCAPSULATION_FORMAT_MIME = "axis.attachment.style.mime";
    public static final String ATTACHMENT_ENCAPSULATION_FORMAT_DIME = "axis.attachment.style.dime";
    public static final String CONNECTION_TIMEOUT_PROPERTY = "axis.connection.timeout";
    public static final String STREAMING_PROPERTY = "axis.streaming";
    protected static final String ONE_WAY = "axis.one.way";
    private static Hashtable transports = new Hashtable();
    static ParameterMode[] modes = new ParameterMode[]{null, ParameterMode.IN, ParameterMode.OUT, ParameterMode.INOUT};
    private boolean encodingStyleExplicitlySet = false;
    private boolean useExplicitlySet = false;
    private SOAPService myService = null;
    protected Vector attachmentParts = new Vector();
    private boolean isNeverInvoked = true;
    private static ArrayList propertyNames;
    private static ArrayList transportPackages;
    static /* synthetic */ Class class$org$apache$axis$client$Call;
    static /* synthetic */ Class class$org$apache$axis$client$Transport;
    static /* synthetic */ Class class$org$apache$axis$transport$java$JavaTransport;
    static /* synthetic */ Class class$org$apache$axis$transport$local$LocalTransport;
    static /* synthetic */ Class class$org$apache$axis$transport$http$HTTPTransport;
    static /* synthetic */ Class class$javax$xml$soap$SOAPMessage;

    public Call(Service service) {
        this.service = service;
        AxisEngine engine = service.getEngine();
        this.msgContext = new MessageContext(engine);
        this.myProperties.setParent(engine.getOptions());
        this.maintainSession = service.getMaintainSession();
    }

    public Call(String url) throws MalformedURLException {
        this(new Service());
        this.setTargetEndpointAddress(new URL(url));
    }

    public Call(URL url) {
        this(new Service());
        this.setTargetEndpointAddress(url);
    }

    public void setProperty(String name, Object value) {
        if (name == null || value == null) {
            throw new JAXRPCException(Messages.getMessage(name == null ? "badProp03" : "badProp04"));
        }
        if (name.equals("javax.xml.rpc.security.auth.username")) {
            this.verifyStringProperty(name, value);
            this.setUsername((String)value);
        } else if (name.equals("javax.xml.rpc.security.auth.password")) {
            this.verifyStringProperty(name, value);
            this.setPassword((String)value);
        } else if (name.equals("javax.xml.rpc.session.maintain")) {
            this.verifyBooleanProperty(name, value);
            this.setMaintainSession((Boolean)value);
        } else if (name.equals("javax.xml.rpc.soap.operation.style")) {
            this.verifyStringProperty(name, value);
            this.setOperationStyle((String)value);
            if (this.getOperationStyle() == Style.DOCUMENT || this.getOperationStyle() == Style.WRAPPED) {
                this.setOperationUse("literal");
            } else if (this.getOperationStyle() == Style.RPC) {
                this.setOperationUse("encoded");
            }
        } else if (name.equals("javax.xml.rpc.soap.http.soapaction.use")) {
            this.verifyBooleanProperty(name, value);
            this.setUseSOAPAction((Boolean)value);
        } else if (name.equals("javax.xml.rpc.soap.http.soapaction.uri")) {
            this.verifyStringProperty(name, value);
            this.setSOAPActionURI((String)value);
        } else if (name.equals("javax.xml.rpc.encodingstyle.namespace.uri")) {
            this.verifyStringProperty(name, value);
            this.setEncodingStyle((String)value);
        } else if (name.equals("javax.xml.rpc.service.endpoint.address")) {
            this.verifyStringProperty(name, value);
            this.setTargetEndpointAddress((String)value);
        } else if (name.equals(TRANSPORT_NAME)) {
            this.verifyStringProperty(name, value);
            this.transportName = (String)value;
            if (this.transport != null) {
                this.transport.setTransportName((String)value);
            }
        } else if (name.equals(ATTACHMENT_ENCAPSULATION_FORMAT)) {
            this.verifyStringProperty(name, value);
            if (!value.equals(ATTACHMENT_ENCAPSULATION_FORMAT_MIME) && !value.equals(ATTACHMENT_ENCAPSULATION_FORMAT_DIME)) {
                throw new JAXRPCException(Messages.getMessage("badattachmenttypeerr", new String[]{(String)value, "axis.attachment.style.mime axis.attachment.style.dime"}));
            }
        } else if (name.equals(CONNECTION_TIMEOUT_PROPERTY)) {
            this.verifyIntegerProperty(name, value);
            this.setTimeout((Integer)value);
        } else if (name.equals(STREAMING_PROPERTY)) {
            this.verifyBooleanProperty(name, value);
            this.setStreaming((Boolean)value);
        } else if (name.equals(CHARACTER_SET_ENCODING)) {
            this.verifyStringProperty(name, value);
        } else if (name.startsWith("java.") || name.startsWith("javax.")) {
            throw new JAXRPCException(Messages.getMessage("badProp05", name));
        }
        this.myProperties.put(name, value);
    }

    private void verifyStringProperty(String name, Object value) {
        if (!(value instanceof String)) {
            throw new JAXRPCException(Messages.getMessage("badProp00", new String[]{name, "java.lang.String", value.getClass().getName()}));
        }
    }

    private void verifyBooleanProperty(String name, Object value) {
        if (!(value instanceof Boolean)) {
            throw new JAXRPCException(Messages.getMessage("badProp00", new String[]{name, "java.lang.Boolean", value.getClass().getName()}));
        }
    }

    private void verifyIntegerProperty(String name, Object value) {
        if (!(value instanceof Integer)) {
            throw new JAXRPCException(Messages.getMessage("badProp00", new String[]{name, "java.lang.Integer", value.getClass().getName()}));
        }
    }

    public Object getProperty(String name) {
        if (name == null || !this.isPropertySupported(name)) {
            throw new JAXRPCException(name == null ? Messages.getMessage("badProp03") : Messages.getMessage("badProp05", name));
        }
        return this.myProperties.get(name);
    }

    public void removeProperty(String name) {
        if (name == null || !this.isPropertySupported(name)) {
            throw new JAXRPCException(name == null ? Messages.getMessage("badProp03") : Messages.getMessage("badProp05", name));
        }
        this.myProperties.remove(name);
    }

    public Iterator getPropertyNames() {
        return propertyNames.iterator();
    }

    public boolean isPropertySupported(String name) {
        return propertyNames.contains(name) || !name.startsWith("java.") && !name.startsWith("javax.");
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    public void setMaintainSession(boolean yesno) {
        this.maintainSession = yesno;
    }

    public boolean getMaintainSession() {
        return this.maintainSession;
    }

    public void setOperationStyle(String operationStyle) {
        Style style = Style.getStyle(operationStyle, Style.DEFAULT);
        this.setOperationStyle(style);
    }

    public void setOperationStyle(Style operationStyle) {
        if (this.operation == null) {
            this.operation = new OperationDesc();
        }
        this.operation.setStyle(operationStyle);
        if (!this.useExplicitlySet && operationStyle != Style.RPC) {
            this.operation.setUse(Use.LITERAL);
        }
        if (!this.encodingStyleExplicitlySet) {
            String encStyle = "";
            if (operationStyle == Style.RPC) {
                encStyle = this.msgContext.getSOAPConstants().getEncodingURI();
            }
            this.msgContext.setEncodingStyle(encStyle);
        }
    }

    public Style getOperationStyle() {
        if (this.operation != null) {
            return this.operation.getStyle();
        }
        return Style.DEFAULT;
    }

    public void setOperationUse(String operationUse) {
        Use use = Use.getUse(operationUse, Use.DEFAULT);
        this.setOperationUse(use);
    }

    public void setOperationUse(Use operationUse) {
        this.useExplicitlySet = true;
        if (this.operation == null) {
            this.operation = new OperationDesc();
        }
        this.operation.setUse(operationUse);
        if (!this.encodingStyleExplicitlySet) {
            String encStyle = "";
            if (operationUse == Use.ENCODED) {
                encStyle = this.msgContext.getSOAPConstants().getEncodingURI();
            }
            this.msgContext.setEncodingStyle(encStyle);
        }
    }

    public Use getOperationUse() {
        if (this.operation != null) {
            return this.operation.getUse();
        }
        return Use.DEFAULT;
    }

    public void setUseSOAPAction(boolean useSOAPAction) {
        this.useSOAPAction = useSOAPAction;
    }

    public boolean useSOAPAction() {
        return this.useSOAPAction;
    }

    public void setSOAPActionURI(String SOAPActionURI) {
        this.useSOAPAction = true;
        this.SOAPActionURI = SOAPActionURI;
    }

    public String getSOAPActionURI() {
        return this.SOAPActionURI;
    }

    public void setEncodingStyle(String namespaceURI) {
        this.encodingStyleExplicitlySet = true;
        this.msgContext.setEncodingStyle(namespaceURI);
    }

    public String getEncodingStyle() {
        return this.msgContext.getEncodingStyle();
    }

    public void setTargetEndpointAddress(String address) {
        URL urlAddress;
        try {
            urlAddress = new URL(address);
        }
        catch (MalformedURLException mue) {
            throw new JAXRPCException(mue);
        }
        this.setTargetEndpointAddress(urlAddress);
    }

    public void setTargetEndpointAddress(URL address) {
        try {
            URL tmpURL;
            String oldProto;
            String oldAddr;
            if (address == null) {
                this.setTransport(null);
                return;
            }
            String protocol = address.getProtocol();
            if (this.transport != null && (oldAddr = this.transport.getUrl()) != null && !oldAddr.equals("") && protocol.equals(oldProto = (tmpURL = new URL(oldAddr)).getProtocol())) {
                this.transport.setUrl(address.toString());
                return;
            }
            Transport transport = this.service.getTransportForURL(address);
            if (transport != null) {
                this.setTransport(transport);
            } else {
                transport = this.getTransportForProtocol(protocol);
                if (transport == null) {
                    throw new AxisFault("Call.setTargetEndpointAddress", Messages.getMessage("noTransport01", protocol), null, null);
                }
                transport.setUrl(address.toString());
                this.setTransport(transport);
                this.service.registerTransportForURL(address, transport);
            }
        }
        catch (Exception exp) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)exp);
        }
    }

    public String getTargetEndpointAddress() {
        try {
            if (this.transport == null) {
                return null;
            }
            return this.transport.getUrl();
        }
        catch (Exception exp) {
            return null;
        }
    }

    public Integer getTimeout() {
        return this.timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public boolean getStreaming() {
        return this.useStreaming;
    }

    public void setStreaming(boolean useStreaming) {
        this.useStreaming = useStreaming;
    }

    public boolean isParameterAndReturnSpecRequired(QName operationName) {
        return this.parmAndRetReq;
    }

    public void addParameter(QName paramName, QName xmlType, ParameterMode parameterMode) {
        Class javaType = null;
        TypeMapping tm = this.getTypeMapping();
        if (tm != null) {
            javaType = tm.getClassForQName(xmlType);
        }
        this.addParameter(paramName, xmlType, javaType, parameterMode);
    }

    public void addParameter(QName paramName, QName xmlType, Class javaType, ParameterMode parameterMode) {
        if (this.operationSetManually) {
            throw new RuntimeException(Messages.getMessage("operationAlreadySet"));
        }
        if (this.operation == null) {
            this.operation = new OperationDesc();
        }
        ParameterDesc param = new ParameterDesc();
        byte mode = 1;
        if (parameterMode == ParameterMode.INOUT) {
            mode = 3;
            param.setIsReturn(true);
        } else if (parameterMode == ParameterMode.OUT) {
            mode = 2;
            param.setIsReturn(true);
        }
        param.setMode(mode);
        param.setQName(new QName(paramName.getNamespaceURI(), Utils.getLastLocalPart(paramName.getLocalPart())));
        param.setTypeQName(xmlType);
        param.setJavaType(javaType);
        this.operation.addParameter(param);
        this.parmAndRetReq = true;
    }

    public void addParameter(String paramName, QName xmlType, ParameterMode parameterMode) {
        Class javaType = null;
        TypeMapping tm = this.getTypeMapping();
        if (tm != null) {
            javaType = tm.getClassForQName(xmlType);
        }
        this.addParameter(new QName("", paramName), xmlType, javaType, parameterMode);
    }

    public void addParameter(String paramName, QName xmlType, Class javaType, ParameterMode parameterMode) {
        this.addParameter(new QName("", paramName), xmlType, javaType, parameterMode);
    }

    public void addParameterAsHeader(QName paramName, QName xmlType, ParameterMode parameterMode, ParameterMode headerMode) {
        Class javaType = null;
        TypeMapping tm = this.getTypeMapping();
        if (tm != null) {
            javaType = tm.getClassForQName(xmlType);
        }
        this.addParameterAsHeader(paramName, xmlType, javaType, parameterMode, headerMode);
    }

    public void addParameterAsHeader(QName paramName, QName xmlType, Class javaType, ParameterMode parameterMode, ParameterMode headerMode) {
        if (this.operationSetManually) {
            throw new RuntimeException(Messages.getMessage("operationAlreadySet"));
        }
        if (this.operation == null) {
            this.operation = new OperationDesc();
        }
        ParameterDesc param = new ParameterDesc();
        param.setQName(new QName(paramName.getNamespaceURI(), Utils.getLastLocalPart(paramName.getLocalPart())));
        param.setTypeQName(xmlType);
        param.setJavaType(javaType);
        if (parameterMode == ParameterMode.IN) {
            param.setMode((byte)1);
        } else if (parameterMode == ParameterMode.INOUT) {
            param.setMode((byte)3);
        } else if (parameterMode == ParameterMode.OUT) {
            param.setMode((byte)2);
        }
        if (headerMode == ParameterMode.IN) {
            param.setInHeader(true);
        } else if (headerMode == ParameterMode.INOUT) {
            param.setInHeader(true);
            param.setOutHeader(true);
        } else if (headerMode == ParameterMode.OUT) {
            param.setOutHeader(true);
        }
        this.operation.addParameter(param);
        this.parmAndRetReq = true;
    }

    public QName getParameterTypeByName(String paramName) {
        QName paramQName = new QName("", paramName);
        return this.getParameterTypeByQName(paramQName);
    }

    public QName getParameterTypeByQName(QName paramQName) {
        ParameterDesc param = this.operation.getParamByQName(paramQName);
        if (param != null) {
            return param.getTypeQName();
        }
        return null;
    }

    public void setReturnType(QName type) {
        if (this.operationSetManually) {
            throw new RuntimeException(Messages.getMessage("operationAlreadySet"));
        }
        if (this.operation == null) {
            this.operation = new OperationDesc();
        }
        this.operation.setReturnType(type);
        TypeMapping tm = this.getTypeMapping();
        this.operation.setReturnClass(tm.getClassForQName(type));
        this.parmAndRetReq = true;
    }

    public void setReturnType(QName xmlType, Class javaType) {
        this.setReturnType(xmlType);
        this.operation.setReturnClass(javaType);
    }

    public void setReturnTypeAsHeader(QName xmlType) {
        this.setReturnType(xmlType);
        this.operation.setReturnHeader(true);
    }

    public void setReturnTypeAsHeader(QName xmlType, Class javaType) {
        this.setReturnType(xmlType, javaType);
        this.operation.setReturnHeader(true);
    }

    public QName getReturnType() {
        if (this.operation != null) {
            return this.operation.getReturnType();
        }
        return null;
    }

    public void setReturnQName(QName qname) {
        if (this.operationSetManually) {
            throw new RuntimeException(Messages.getMessage("operationAlreadySet"));
        }
        if (this.operation == null) {
            this.operation = new OperationDesc();
        }
        this.operation.setReturnQName(qname);
    }

    public void setReturnClass(Class cls) {
        if (this.operationSetManually) {
            throw new RuntimeException(Messages.getMessage("operationAlreadySet"));
        }
        if (this.operation == null) {
            this.operation = new OperationDesc();
        }
        this.operation.setReturnClass(cls);
        TypeMapping tm = this.getTypeMapping();
        this.operation.setReturnType(tm.getTypeQName(cls));
        this.parmAndRetReq = true;
    }

    public void removeAllParameters() {
        this.operation = new OperationDesc();
        this.operationSetManually = false;
        this.parmAndRetReq = true;
    }

    public QName getOperationName() {
        return this.operationName;
    }

    public void setOperationName(QName opName) {
        this.operationName = opName;
    }

    public void setOperationName(String opName) {
        this.operationName = new QName(opName);
    }

    public void setOperation(String opName) {
        Style style;
        BindingInput bIn;
        if (this.service == null) {
            throw new JAXRPCException(Messages.getMessage("noService04"));
        }
        this.setOperationName(opName);
        this.setEncodingStyle(null);
        this.setReturnType(null);
        this.removeAllParameters();
        javax.wsdl.Service wsdlService = this.service.getWSDLService();
        if (wsdlService == null) {
            return;
        }
        Port port = wsdlService.getPort(this.portName.getLocalPart());
        if (port == null) {
            throw new JAXRPCException(Messages.getMessage("noPort00", "" + this.portName));
        }
        Binding binding = port.getBinding();
        PortType portType = binding.getPortType();
        if (portType == null) {
            throw new JAXRPCException(Messages.getMessage("noPortType00", "" + this.portName));
        }
        this.setPortTypeName(portType.getQName());
        List operations = portType.getOperations();
        if (operations == null) {
            throw new JAXRPCException(Messages.getMessage("noOperation01", opName));
        }
        Operation op = null;
        for (int i = 0; i < operations.size() && !opName.equals((op = (Operation)operations.get(i)).getName()); ++i) {
            op = null;
        }
        if (op == null) {
            throw new JAXRPCException(Messages.getMessage("noOperation01", opName));
        }
        List list = port.getExtensibilityElements();
        String opStyle = null;
        BindingOperation bop = binding.getBindingOperation(opName, null, null);
        if (bop == null) {
            throw new JAXRPCException(Messages.getMessage("noOperation02", opName));
        }
        list = bop.getExtensibilityElements();
        for (int i = 0; list != null && i < list.size(); ++i) {
            Object obj = list.get(i);
            if (!(obj instanceof SOAPOperation)) continue;
            SOAPOperation sop = (SOAPOperation)obj;
            opStyle = ((SOAPOperation)obj).getStyle();
            String action = sop.getSoapActionURI();
            if (action != null) {
                this.setUseSOAPAction(true);
                this.setSOAPActionURI(action);
                break;
            }
            this.setUseSOAPAction(false);
            this.setSOAPActionURI(null);
            break;
        }
        if ((bIn = bop.getBindingInput()) != null) {
            list = bIn.getExtensibilityElements();
            for (int i = 0; list != null && i < list.size(); ++i) {
                String ns;
                Object obj = list.get(i);
                if (obj instanceof MIMEMultipartRelated) {
                    MIMEMultipartRelated mpr = (MIMEMultipartRelated)obj;
                    Object part = null;
                    List l = mpr.getMIMEParts();
                    for (int j = 0; l != null && j < l.size() && part == null; ++j) {
                        MIMEPart mp = (MIMEPart)l.get(j);
                        List ll = mp.getExtensibilityElements();
                        for (int k = 0; ll != null && k < ll.size() && part == null; ++k) {
                            part = ll.get(k);
                            if (part instanceof SOAPBody) continue;
                            part = null;
                        }
                    }
                    if (null != part) {
                        obj = part;
                    }
                }
                if (!(obj instanceof SOAPBody)) continue;
                SOAPBody sBody = (SOAPBody)obj;
                list = sBody.getEncodingStyles();
                if (list != null && list.size() > 0) {
                    this.setEncodingStyle((String)list.get(0));
                }
                if ((ns = sBody.getNamespaceURI()) == null || ns.equals("")) break;
                this.setOperationName(new QName(ns, opName));
                break;
            }
        }
        Service service = this.getService();
        SymbolTable symbolTable = service.getWSDLParser().getSymbolTable();
        BindingEntry bEntry = symbolTable.getBindingEntry(binding.getQName());
        Parameters parameters = bEntry.getParameters(bop.getOperation());
        for (int j = 0; j < parameters.list.size(); ++j) {
            Parameter p = (Parameter)parameters.list.get(j);
            QName paramType = Utils.getXSIType(p);
            ParameterMode mode = modes[p.getMode()];
            if (p.isInHeader() || p.isOutHeader()) {
                this.addParameterAsHeader(p.getQName(), paramType, mode, mode);
                continue;
            }
            this.addParameter(p.getQName(), paramType, mode);
        }
        HashMap faultMap = bEntry.getFaults();
        ArrayList faults = (ArrayList)faultMap.get(bop);
        if (faults == null) {
            return;
        }
        Iterator faultIt = faults.iterator();
        while (faultIt.hasNext()) {
            FaultInfo info = (FaultInfo)faultIt.next();
            QName qname = info.getQName();
            info.getMessage();
            if (qname == null) continue;
            QName xmlType = info.getXMLType();
            Class clazz = this.getTypeMapping().getClassForQName(xmlType);
            if (clazz != null) {
                this.addFault(qname, clazz, xmlType, true);
                continue;
            }
            log.debug((Object)Messages.getMessage("clientNoTypemapping", xmlType.toString()));
        }
        if (parameters.returnParam != null) {
            QName returnType = Utils.getXSIType(parameters.returnParam);
            QName returnQName = parameters.returnParam.getQName();
            String javaType = null;
            javaType = parameters.returnParam.getMIMEInfo() != null ? "javax.activation.DataHandler" : parameters.returnParam.getType().getName();
            javaType = javaType == null ? "" : javaType + ".class";
            this.setReturnType(returnType);
            try {
                Class clazz = ClassUtils.forName(javaType);
                this.setReturnClass(clazz);
            }
            catch (ClassNotFoundException swallowedException) {
                log.debug((Object)Messages.getMessage("clientNoReturnClass", javaType));
            }
            this.setReturnQName(returnQName);
        } else {
            this.setReturnType(XMLType.AXIS_VOID);
        }
        boolean hasMIME = Utils.hasMIME(bEntry, bop);
        Use use = bEntry.getInputBodyType(bop.getOperation());
        this.setOperationUse(use);
        if (use == Use.LITERAL) {
            this.setEncodingStyle(null);
            this.setProperty(SEND_TYPE_ATTR, Boolean.FALSE);
        }
        if (hasMIME || use == Use.LITERAL) {
            this.setProperty("sendMultiRefs", Boolean.FALSE);
        }
        if ((style = Style.getStyle(opStyle, bEntry.getBindingStyle())) == Style.DOCUMENT && symbolTable.isWrapped()) {
            style = Style.WRAPPED;
        }
        this.setOperationStyle(style);
        if (style == Style.WRAPPED) {
            Map partsMap = bop.getOperation().getInput().getMessage().getParts();
            Part p = (Part)partsMap.values().iterator().next();
            QName q = p.getElementName();
            this.setOperationName(q);
        } else {
            QName elementQName = Utils.getOperationQName(bop, bEntry, symbolTable);
            if (elementQName != null) {
                this.setOperationName(elementQName);
            }
        }
        this.parmAndRetReq = false;
    }

    public void setOperation(QName portName, String opName) {
        this.setOperation(portName, new QName(opName));
    }

    public void setOperation(QName portName, QName opName) {
        if (this.service == null) {
            throw new JAXRPCException(Messages.getMessage("noService04"));
        }
        this.setPortName(portName);
        this.setOperationName(opName);
        this.setReturnType(null);
        this.removeAllParameters();
        javax.wsdl.Service wsdlService = this.service.getWSDLService();
        if (wsdlService == null) {
            return;
        }
        this.setTargetEndpointAddress((URL)null);
        Port port = wsdlService.getPort(portName.getLocalPart());
        if (port == null) {
            throw new JAXRPCException(Messages.getMessage("noPort00", "" + portName));
        }
        List list = port.getExtensibilityElements();
        for (int i = 0; list != null && i < list.size(); ++i) {
            Object obj = list.get(i);
            if (!(obj instanceof SOAPAddress)) continue;
            try {
                SOAPAddress addr = (SOAPAddress)obj;
                URL url = new URL(addr.getLocationURI());
                this.setTargetEndpointAddress(url);
                continue;
            }
            catch (Exception exp) {
                throw new JAXRPCException(Messages.getMessage("cantSetURI00", "" + exp));
            }
        }
        this.setOperation(opName.getLocalPart());
    }

    public QName getPortName() {
        return this.portName;
    }

    public void setPortName(QName portName) {
        this.portName = portName;
    }

    public QName getPortTypeName() {
        return this.portTypeName == null ? new QName("") : this.portTypeName;
    }

    public void setPortTypeName(QName portType) {
        this.portTypeName = portType;
    }

    public void setSOAPVersion(SOAPConstants soapConstants) {
        this.msgContext.setSOAPConstants(soapConstants);
    }

    public Object invoke(QName operationName, Object[] params) throws RemoteException {
        QName origOpName = this.operationName;
        this.operationName = operationName;
        try {
            return this.invoke(params);
        }
        catch (AxisFault af) {
            this.operationName = origOpName;
            if (af.detail != null && af.detail instanceof RemoteException) {
                throw (RemoteException)af.detail;
            }
            throw af;
        }
        catch (RemoteException re) {
            this.operationName = origOpName;
            throw re;
        }
        catch (RuntimeException re) {
            this.operationName = origOpName;
            throw re;
        }
        catch (Error e) {
            this.operationName = origOpName;
            throw e;
        }
    }

    public Object invoke(Object[] params) throws RemoteException {
        int i;
        long t0 = 0L;
        long t1 = 0L;
        if (tlog.isDebugEnabled()) {
            t0 = System.currentTimeMillis();
        }
        SOAPEnvelope env = null;
        for (i = 0; params != null && i < params.length && params[i] instanceof SOAPBodyElement; ++i) {
        }
        if (params != null && params.length > 0 && i == params.length) {
            this.isMsg = true;
            env = new SOAPEnvelope(this.msgContext.getSOAPConstants(), this.msgContext.getSchemaVersion());
            for (i = 0; i < params.length; ++i) {
                env.addBodyElement((SOAPBodyElement)params[i]);
            }
            Message msg = new Message(env);
            this.setRequestMessage(msg);
            this.invoke();
            msg = this.msgContext.getResponseMessage();
            if (msg == null) {
                if (this.msgContext.isPropertyTrue(FAULT_ON_NO_RESPONSE, false)) {
                    throw new AxisFault(Messages.getMessage("nullResponse00"));
                }
                return null;
            }
            env = msg.getSOAPEnvelope();
            return env.getBodyElements();
        }
        if (this.operationName == null) {
            throw new AxisFault(Messages.getMessage("noOperation00"));
        }
        try {
            Object res = this.invoke(this.operationName.getNamespaceURI(), this.operationName.getLocalPart(), params);
            if (tlog.isDebugEnabled()) {
                t1 = System.currentTimeMillis();
                tlog.debug((Object)("axis.Call.invoke: " + (t1 - t0) + " " + this.operationName));
            }
            return res;
        }
        catch (AxisFault af) {
            if (af.detail != null && af.detail instanceof RemoteException) {
                throw (RemoteException)af.detail;
            }
            throw af;
        }
        catch (Exception exp) {
            entLog.debug((Object)Messages.getMessage("toAxisFault00"), (Throwable)exp);
            throw AxisFault.makeFault(exp);
        }
    }

    public void invokeOneWay(Object[] params) {
        try {
            this.invokeOneWay = true;
            this.invoke(params);
        }
        catch (Exception exp) {
            throw new JAXRPCException(exp.toString());
        }
        finally {
            this.invokeOneWay = false;
        }
    }

    public SOAPEnvelope invoke(Message msg) throws AxisFault {
        try {
            this.setRequestMessage(msg);
            this.invoke();
            msg = this.msgContext.getResponseMessage();
            if (msg == null) {
                if (this.msgContext.isPropertyTrue(FAULT_ON_NO_RESPONSE, false)) {
                    throw new AxisFault(Messages.getMessage("nullResponse00"));
                }
                return null;
            }
            SOAPEnvelope res = null;
            res = msg.getSOAPEnvelope();
            return res;
        }
        catch (Exception exp) {
            if (exp instanceof AxisFault) {
                throw (AxisFault)exp;
            }
            entLog.debug((Object)Messages.getMessage("toAxisFault00"), (Throwable)exp);
            throw new AxisFault(Messages.getMessage("errorInvoking00", "\n" + exp));
        }
    }

    public SOAPEnvelope invoke(SOAPEnvelope env) throws AxisFault {
        try {
            Message msg = new Message(env);
            if (this.getProperty(CHARACTER_SET_ENCODING) != null) {
                msg.setProperty(CHARACTER_SET_ENCODING, this.getProperty(CHARACTER_SET_ENCODING));
            } else if (this.msgContext.getProperty(CHARACTER_SET_ENCODING) != null) {
                msg.setProperty(CHARACTER_SET_ENCODING, this.msgContext.getProperty(CHARACTER_SET_ENCODING));
            }
            this.setRequestMessage(msg);
            this.invoke();
            msg = this.msgContext.getResponseMessage();
            if (msg == null) {
                if (this.msgContext.isPropertyTrue(FAULT_ON_NO_RESPONSE, false)) {
                    throw new AxisFault(Messages.getMessage("nullResponse00"));
                }
                return null;
            }
            return msg.getSOAPEnvelope();
        }
        catch (Exception exp) {
            if (exp instanceof AxisFault) {
                throw (AxisFault)exp;
            }
            entLog.debug((Object)Messages.getMessage("toAxisFault00"), (Throwable)exp);
            throw AxisFault.makeFault(exp);
        }
    }

    public static void setTransportForProtocol(String protocol, Class transportClass) {
        if (!(class$org$apache$axis$client$Transport == null ? (class$org$apache$axis$client$Transport = Call.class$("org.apache.axis.client.Transport")) : class$org$apache$axis$client$Transport).isAssignableFrom(transportClass)) {
            throw new InternalException(transportClass.toString());
        }
        transports.put(protocol, transportClass);
    }

    public static synchronized void initialize() {
        Call.addTransportPackage("org.apache.axis.transport");
        Call.setTransportForProtocol("java", class$org$apache$axis$transport$java$JavaTransport == null ? (class$org$apache$axis$transport$java$JavaTransport = Call.class$("org.apache.axis.transport.java.JavaTransport")) : class$org$apache$axis$transport$java$JavaTransport);
        Call.setTransportForProtocol("local", class$org$apache$axis$transport$local$LocalTransport == null ? (class$org$apache$axis$transport$local$LocalTransport = Call.class$("org.apache.axis.transport.local.LocalTransport")) : class$org$apache$axis$transport$local$LocalTransport);
        Call.setTransportForProtocol("http", class$org$apache$axis$transport$http$HTTPTransport == null ? (class$org$apache$axis$transport$http$HTTPTransport = Call.class$("org.apache.axis.transport.http.HTTPTransport")) : class$org$apache$axis$transport$http$HTTPTransport);
        Call.setTransportForProtocol("https", class$org$apache$axis$transport$http$HTTPTransport == null ? (class$org$apache$axis$transport$http$HTTPTransport = Call.class$("org.apache.axis.transport.http.HTTPTransport")) : class$org$apache$axis$transport$http$HTTPTransport);
    }

    public static synchronized void addTransportPackage(String packageName) {
        CharSequence currentPackages;
        if (transportPackages == null) {
            transportPackages = new ArrayList();
            currentPackages = AxisProperties.getProperty(TRANSPORT_PROPERTY);
            if (currentPackages != null) {
                StringTokenizer tok = new StringTokenizer((String)currentPackages, "|");
                while (tok.hasMoreTokens()) {
                    transportPackages.add(tok.nextToken());
                }
            }
        }
        if (transportPackages.contains(packageName)) {
            return;
        }
        transportPackages.add(packageName);
        currentPackages = new StringBuffer();
        Iterator i = transportPackages.iterator();
        while (i.hasNext()) {
            String thisPackage = (String)i.next();
            ((StringBuffer)currentPackages).append(thisPackage);
            ((StringBuffer)currentPackages).append('|');
        }
        System.setProperty(TRANSPORT_PROPERTY, ((StringBuffer)currentPackages).toString());
    }

    private Object[] getParamList(Object[] params) {
        int numParams = 0;
        if (log.isDebugEnabled()) {
            log.debug((Object)("operation=" + this.operation));
            if (this.operation != null) {
                log.debug((Object)("operation.getNumParams()=" + this.operation.getNumParams()));
            }
        }
        if (this.operation == null || this.operation.getNumParams() == 0) {
            return params;
        }
        numParams = this.operation.getNumInParams();
        if (params == null || numParams != params.length) {
            throw new JAXRPCException(Messages.getMessage("parmMismatch00", params == null ? "no params" : "" + params.length, "" + numParams));
        }
        log.debug((Object)("getParamList number of params: " + params.length));
        Vector<RPCParam> result = new Vector<RPCParam>();
        int j = 0;
        ArrayList parameters = this.operation.getParameters();
        for (int i = 0; i < parameters.size(); ++i) {
            Object p;
            ParameterDesc param = (ParameterDesc)parameters.get(i);
            if (param.getMode() == 2) continue;
            QName paramQName = param.getQName();
            RPCParam rpcParam = null;
            rpcParam = (p = params[j++]) instanceof RPCParam ? (RPCParam)p : new RPCParam(paramQName.getNamespaceURI(), paramQName.getLocalPart(), p);
            rpcParam.setParamDesc(param);
            if (param.isInHeader()) {
                this.addHeader(new RPCHeaderParam(rpcParam));
                continue;
            }
            result.add(rpcParam);
        }
        return result.toArray();
    }

    public void setTransport(Transport trans) {
        this.transport = trans;
        if (log.isDebugEnabled()) {
            log.debug((Object)Messages.getMessage("transport00", "" + this.transport));
        }
    }

    public Transport getTransportForProtocol(String protocol) {
        Class transportClass = (Class)transports.get(protocol);
        Transport ret = null;
        if (transportClass != null) {
            try {
                ret = (Transport)transportClass.newInstance();
            }
            catch (InstantiationException e) {
            }
            catch (IllegalAccessException e) {
                // empty catch block
            }
        }
        return ret;
    }

    public void setRequestMessage(Message msg) {
        Attachments attachments;
        String attachformat = (String)this.getProperty(ATTACHMENT_ENCAPSULATION_FORMAT);
        if (null != attachformat && null != (attachments = msg.getAttachmentsImpl())) {
            if (ATTACHMENT_ENCAPSULATION_FORMAT_MIME.equals(attachformat)) {
                attachments.setSendType(2);
            } else if (ATTACHMENT_ENCAPSULATION_FORMAT_DIME.equals(attachformat)) {
                attachments.setSendType(3);
            }
        }
        if (null != this.attachmentParts && !this.attachmentParts.isEmpty()) {
            try {
                attachments = msg.getAttachmentsImpl();
                if (null == attachments) {
                    throw new RuntimeException(Messages.getMessage("noAttachments"));
                }
                attachments.setAttachmentParts(this.attachmentParts);
            }
            catch (AxisFault ex) {
                log.info((Object)Messages.getMessage("axisFault00"), (Throwable)ex);
                throw new RuntimeException(ex.getMessage());
            }
        }
        this.msgContext.setRequestMessage(msg);
        this.attachmentParts.clear();
    }

    public Message getResponseMessage() {
        return this.msgContext.getResponseMessage();
    }

    public MessageContext getMessageContext() {
        return this.msgContext;
    }

    public void addHeader(SOAPHeaderElement header) {
        if (this.myHeaders == null) {
            this.myHeaders = new Vector();
        }
        this.myHeaders.add(header);
    }

    public void clearHeaders() {
        this.myHeaders = null;
    }

    public TypeMapping getTypeMapping() {
        TypeMappingRegistry tmr = this.msgContext.getTypeMappingRegistry();
        return tmr.getOrMakeTypeMapping(this.getEncodingStyle());
    }

    public void registerTypeMapping(Class javaType, QName xmlType, SerializerFactory sf, DeserializerFactory df) {
        this.registerTypeMapping(javaType, xmlType, sf, df, true);
    }

    public void registerTypeMapping(Class javaType, QName xmlType, SerializerFactory sf, DeserializerFactory df, boolean force) {
        TypeMapping tm = this.getTypeMapping();
        if (!force && tm.isRegistered(javaType, xmlType)) {
            return;
        }
        tm.register(javaType, xmlType, sf, df);
    }

    public void registerTypeMapping(Class javaType, QName xmlType, Class sfClass, Class dfClass) {
        this.registerTypeMapping(javaType, xmlType, sfClass, dfClass, true);
    }

    public void registerTypeMapping(Class javaType, QName xmlType, Class sfClass, Class dfClass, boolean force) {
        SerializerFactory sf = BaseSerializerFactory.createFactory(sfClass, javaType, xmlType);
        DeserializerFactory df = BaseDeserializerFactory.createFactory(dfClass, javaType, xmlType);
        if (sf != null || df != null) {
            this.registerTypeMapping(javaType, xmlType, sf, df, force);
        }
    }

    public Object invoke(String namespace, String method, Object[] args) throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: Call::invoke(ns, meth, args)");
        }
        if (this.getReturnType() != null && args != null && args.length != 0 && this.operation.getNumParams() == 0) {
            throw new AxisFault(Messages.getMessage("mustSpecifyParms"));
        }
        RPCElement body = new RPCElement(namespace, method, this.getParamList(args));
        Object ret = this.invoke(body);
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: Call::invoke(ns, meth, args)");
        }
        return ret;
    }

    public Object invoke(String method, Object[] args) throws AxisFault {
        return this.invoke("", method, args);
    }

    public Object invoke(RPCElement body) throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: Call::invoke(RPCElement)");
        }
        if (!this.invokeOneWay && this.operation != null && this.operation.getNumParams() > 0 && this.getReturnType() == null) {
            log.error((Object)Messages.getMessage("mustSpecifyReturnType"));
        }
        SOAPEnvelope reqEnv = new SOAPEnvelope(this.msgContext.getSOAPConstants(), this.msgContext.getSchemaVersion());
        SOAPEnvelope resEnv = null;
        Message reqMsg = new Message(reqEnv);
        Message resMsg = null;
        Vector resArgs = null;
        Object result = null;
        this.outParams = new HashMap();
        this.outParamsList = new ArrayList();
        try {
            body.setEncodingStyle(this.getEncodingStyle());
            this.setRequestMessage(reqMsg);
            reqEnv.addBodyElement(body);
            reqEnv.setMessageType("request");
            this.invoke();
        }
        catch (Exception e) {
            entLog.debug((Object)Messages.getMessage("toAxisFault00"), (Throwable)e);
            throw AxisFault.makeFault(e);
        }
        resMsg = this.msgContext.getResponseMessage();
        if (resMsg == null) {
            if (this.msgContext.isPropertyTrue(FAULT_ON_NO_RESPONSE, false)) {
                throw new AxisFault(Messages.getMessage("nullResponse00"));
            }
            return null;
        }
        resEnv = resMsg.getSOAPEnvelope();
        SOAPBodyElement bodyEl = resEnv.getFirstBody();
        if (bodyEl == null) {
            return null;
        }
        if (bodyEl instanceof RPCElement) {
            try {
                resArgs = ((RPCElement)bodyEl).getParams();
            }
            catch (Exception e) {
                log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
                throw AxisFault.makeFault(e);
            }
            if (resArgs != null && resArgs.size() > 0) {
                int outParamStart = 0;
                boolean findReturnParam = false;
                QName returnParamQName = null;
                if (this.operation != null) {
                    returnParamQName = this.operation.getReturnQName();
                }
                if (!XMLType.AXIS_VOID.equals(this.getReturnType())) {
                    if (returnParamQName == null) {
                        RPCParam param = (RPCParam)resArgs.get(0);
                        result = param.getObjectValue();
                        outParamStart = 1;
                    } else {
                        findReturnParam = true;
                    }
                }
                for (int i = outParamStart; i < resArgs.size(); ++i) {
                    RPCParam param = (RPCParam)resArgs.get(i);
                    Class javaType = this.getJavaTypeForQName(param.getQName());
                    Object value = param.getObjectValue();
                    if (javaType != null && value != null && !javaType.isAssignableFrom(value.getClass())) {
                        value = JavaUtils.convert(value, javaType);
                    }
                    if (findReturnParam && returnParamQName.equals(param.getQName())) {
                        result = value;
                        findReturnParam = false;
                        continue;
                    }
                    this.outParams.put(param.getQName(), value);
                    this.outParamsList.add(value);
                }
                if (findReturnParam) {
                    Iterator it = this.outParams.keySet().iterator();
                    while (it.hasNext() && findReturnParam) {
                        QName qname = (QName)it.next();
                        ParameterDesc paramDesc = this.operation.getOutputParamByQName(qname);
                        if (paramDesc != null) continue;
                        findReturnParam = false;
                        result = this.outParams.remove(qname);
                    }
                }
                if (findReturnParam) {
                    String returnParamName = returnParamQName.toString();
                    throw new AxisFault(Messages.getMessage("noReturnParam", returnParamName));
                }
            }
        } else {
            try {
                result = bodyEl.getValueAsType(this.getReturnType());
            }
            catch (Exception e) {
                result = bodyEl;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: Call::invoke(RPCElement)");
        }
        if (this.operation != null && this.operation.getReturnClass() != null) {
            result = JavaUtils.convert(result, this.operation.getReturnClass());
        }
        return result;
    }

    private Class getJavaTypeForQName(QName name) {
        if (this.operation == null) {
            return null;
        }
        ParameterDesc param = this.operation.getOutputParamByQName(name);
        return param == null ? null : param.getJavaType();
    }

    public void setOption(String name, Object value) {
        this.service.getEngine().setOption(name, value);
    }

    public void invoke() throws AxisFault {
        Message requestMessage;
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: Call::invoke()");
        }
        this.isNeverInvoked = false;
        Message reqMsg = null;
        SOAPEnvelope reqEnv = null;
        this.msgContext.reset();
        this.msgContext.setResponseMessage(null);
        this.msgContext.setProperty("call_object", this);
        this.msgContext.setProperty("wsdl.service", this.service);
        this.msgContext.setProperty("wsdl.portName", this.getPortName());
        if (this.isMsg) {
            this.msgContext.setProperty("isMsg", "true");
        }
        if (this.username != null) {
            this.msgContext.setUsername(this.username);
        }
        if (this.password != null) {
            this.msgContext.setPassword(this.password);
        }
        this.msgContext.setMaintainSession(this.maintainSession);
        if (this.operation != null) {
            this.msgContext.setOperation(this.operation);
            this.operation.setStyle(this.getOperationStyle());
            this.operation.setUse(this.getOperationUse());
        }
        if (this.useSOAPAction) {
            this.msgContext.setUseSOAPAction(true);
        }
        if (this.SOAPActionURI != null) {
            this.msgContext.setSOAPActionURI(this.SOAPActionURI);
        } else {
            this.msgContext.setSOAPActionURI(null);
        }
        if (this.timeout != null) {
            this.msgContext.setTimeout(this.timeout);
        }
        this.msgContext.setHighFidelity(!this.useStreaming);
        if (this.myService != null) {
            this.msgContext.setService(this.myService);
        } else if (this.portName != null) {
            this.msgContext.setTargetService(this.portName.getLocalPart());
        } else {
            SOAPBodyElement body;
            reqMsg = this.msgContext.getRequestMessage();
            boolean isStream = ((SOAPPart)reqMsg.getSOAPPart()).isBodyStream();
            if (reqMsg != null && !isStream && (body = (reqEnv = reqMsg.getSOAPEnvelope()).getFirstBody()) != null) {
                if (body.getNamespaceURI() == null) {
                    throw new AxisFault("Call.invoke", Messages.getMessage("cantInvoke00", body.getName()), null, null);
                }
                this.msgContext.setTargetService(body.getNamespaceURI());
            }
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)Messages.getMessage("targetService", this.msgContext.getTargetService()));
        }
        if ((requestMessage = this.msgContext.getRequestMessage()) != null) {
            try {
                this.msgContext.setProperty(CHARACTER_SET_ENCODING, requestMessage.getProperty(CHARACTER_SET_ENCODING));
            }
            catch (SOAPException e) {
                // empty catch block
            }
            if (this.myHeaders != null) {
                reqEnv = requestMessage.getSOAPEnvelope();
                for (int i = 0; this.myHeaders != null && i < this.myHeaders.size(); ++i) {
                    reqEnv.addHeader((SOAPHeaderElement)this.myHeaders.get(i));
                }
            }
        }
        if (this.transport != null) {
            this.transport.setupMessageContext(this.msgContext, this, this.service.getEngine());
        } else {
            this.msgContext.setTransportName(this.transportName);
        }
        SOAPService svc = this.msgContext.getService();
        if (svc != null) {
            svc.setPropertyParent(this.myProperties);
        } else {
            this.msgContext.setPropertyParent(this.myProperties);
        }
        if (log.isDebugEnabled()) {
            StringWriter writer = new StringWriter();
            try {
                SerializationContext ctx = new SerializationContext(writer, this.msgContext);
                requestMessage.getSOAPEnvelope().output(ctx);
                writer.close();
            }
            catch (Exception e) {
                throw AxisFault.makeFault(e);
            }
            finally {
                log.debug((Object)writer.getBuffer().toString());
            }
        }
        if (!this.invokeOneWay) {
            this.invokeEngine(this.msgContext);
        } else {
            this.invokeEngineOneWay(this.msgContext);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: Call::invoke()");
        }
    }

    private void invokeEngine(MessageContext msgContext) throws AxisFault {
        Message resMsg;
        this.service.getEngine().invoke(msgContext);
        if (this.transport != null) {
            this.transport.processReturnedMessageContext(msgContext);
        }
        if ((resMsg = msgContext.getResponseMessage()) == null) {
            if (msgContext.isPropertyTrue(FAULT_ON_NO_RESPONSE, false)) {
                throw new AxisFault(Messages.getMessage("nullResponse00"));
            }
            return;
        }
        resMsg.setMessageType("response");
        SOAPEnvelope resEnv = resMsg.getSOAPEnvelope();
        SOAPBodyElement respBody = resEnv.getFirstBody();
        if (respBody instanceof SOAPFault && (this.operation == null || this.operation.getReturnClass() == null || this.operation.getReturnClass() != (class$javax$xml$soap$SOAPMessage == null ? (class$javax$xml$soap$SOAPMessage = Call.class$("javax.xml.soap.SOAPMessage")) : class$javax$xml$soap$SOAPMessage))) {
            throw ((SOAPFault)respBody).getFault();
        }
    }

    private void invokeEngineOneWay(final MessageContext msgContext) {
        Runnable runnable = new Runnable(){

            public void run() {
                msgContext.setProperty(Call.ONE_WAY, Boolean.TRUE);
                try {
                    Call.this.service.getEngine().invoke(msgContext);
                }
                catch (AxisFault af) {
                    log.debug((Object)Messages.getMessage("exceptionPrinting"), (Throwable)af);
                }
                msgContext.removeProperty(Call.ONE_WAY);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public Map getOutputParams() {
        if (this.isNeverInvoked) {
            throw new JAXRPCException(Messages.getMessage("outputParamsUnavailable"));
        }
        return this.outParams;
    }

    public List getOutputValues() {
        if (this.isNeverInvoked) {
            throw new JAXRPCException(Messages.getMessage("outputParamsUnavailable"));
        }
        return this.outParamsList;
    }

    public Service getService() {
        return this.service;
    }

    public void setSOAPService(SOAPService service) {
        this.myService = service;
        if (service != null) {
            service.setEngine(this.service.getAxisClient());
            service.setPropertyParent(this.myProperties);
        }
    }

    public void setClientHandlers(Handler reqHandler, Handler respHandler) {
        this.setSOAPService(new SOAPService(reqHandler, null, respHandler));
    }

    public void addAttachmentPart(Object attachment) {
        this.attachmentParts.add(attachment);
    }

    public void addFault(QName qname, Class cls, QName xmlType, boolean isComplex) {
        if (this.operationSetManually) {
            throw new RuntimeException(Messages.getMessage("operationAlreadySet"));
        }
        if (this.operation == null) {
            this.operation = new OperationDesc();
        }
        FaultDesc fault = new FaultDesc();
        fault.setQName(qname);
        fault.setClassName(cls.getName());
        fault.setXmlType(xmlType);
        fault.setComplex(isComplex);
        this.operation.addFault(fault);
    }

    public void setOperation(OperationDesc operation) {
        this.operation = operation;
        this.operationSetManually = true;
    }

    public OperationDesc getOperation() {
        return this.operation;
    }

    public void clearOperation() {
        this.operation = null;
        this.operationSetManually = false;
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
        Call.initialize();
        propertyNames = new ArrayList();
        propertyNames.add("javax.xml.rpc.security.auth.username");
        propertyNames.add("javax.xml.rpc.security.auth.password");
        propertyNames.add("javax.xml.rpc.session.maintain");
        propertyNames.add("javax.xml.rpc.soap.operation.style");
        propertyNames.add("javax.xml.rpc.soap.http.soapaction.use");
        propertyNames.add("javax.xml.rpc.soap.http.soapaction.uri");
        propertyNames.add("javax.xml.rpc.encodingstyle.namespace.uri");
        propertyNames.add("javax.xml.rpc.service.endpoint.address");
        propertyNames.add(TRANSPORT_NAME);
        propertyNames.add(ATTACHMENT_ENCAPSULATION_FORMAT);
        propertyNames.add(CONNECTION_TIMEOUT_PROPERTY);
        propertyNames.add(CHARACTER_SET_ENCODING);
        transportPackages = null;
    }
}

