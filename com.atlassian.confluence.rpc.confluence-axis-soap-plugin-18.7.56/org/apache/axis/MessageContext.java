/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.SOAPMessage;
import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.AxisProperties;
import org.apache.axis.ConfigurationException;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.attachments.Attachments;
import org.apache.axis.client.AxisClient;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.schema.SchemaVersion;
import org.apache.axis.session.Session;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.LockableHashtable;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class MessageContext
implements SOAPMessageContext {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$MessageContext == null ? (class$org$apache$axis$MessageContext = MessageContext.class$("org.apache.axis.MessageContext")) : class$org$apache$axis$MessageContext).getName());
    private Message requestMessage;
    private Message responseMessage;
    private String targetService;
    private String transportName;
    private ClassLoader classLoader;
    private AxisEngine axisEngine;
    private Session session;
    private boolean maintainSession = false;
    private boolean havePassedPivot = false;
    private int timeout = 600000;
    private boolean highFidelity = true;
    private LockableHashtable bag = new LockableHashtable();
    private String username = null;
    private String password = null;
    private String encodingStyle = Use.ENCODED.getEncoding();
    private boolean useSOAPAction = false;
    private String SOAPActionURI = null;
    private String[] roles;
    private SOAPConstants soapConstants = Constants.DEFAULT_SOAP_VERSION;
    private SchemaVersion schemaVersion = SchemaVersion.SCHEMA_2001;
    private OperationDesc currentOperation = null;
    protected static String systemTempDir = null;
    private TypeMappingRegistry mappingRegistry = null;
    private SOAPService serviceHandler;
    public static final String ENGINE_HANDLER = "engine.handler";
    public static final String TRANS_URL = "transport.url";
    public static final String QUIT_REQUESTED = "quit.requested";
    public static final String AUTHUSER = "authenticatedUser";
    public static final String CALL = "call_object";
    public static final String IS_MSG = "isMsg";
    public static final String ATTACHMENTS_DIR = "attachments.directory";
    public static final String ACCEPTMISSINGPARAMS = "acceptMissingParams";
    public static final String WSDLGEN_INTFNAMESPACE = "axis.wsdlgen.intfnamespace";
    public static final String WSDLGEN_SERV_LOC_URL = "axis.wsdlgen.serv.loc.url";
    public static final String HTTP_TRANSPORT_VERSION = "axis.transport.version";
    public static final String SECURITY_PROVIDER = "securityProvider";
    static /* synthetic */ Class class$org$apache$axis$MessageContext;
    static /* synthetic */ Class array$Lorg$apache$axis$description$OperationDesc;

    public OperationDesc getOperation() {
        return this.currentOperation;
    }

    public void setOperation(OperationDesc operation) {
        this.currentOperation = operation;
    }

    public OperationDesc[] getPossibleOperationsByQName(QName qname) throws AxisFault {
        ServiceDesc desc;
        if (this.currentOperation != null) {
            return new OperationDesc[]{this.currentOperation};
        }
        OperationDesc[] possibleOperations = null;
        if (this.serviceHandler == null) {
            try {
                if (log.isDebugEnabled()) {
                    log.debug((Object)Messages.getMessage("dispatching00", qname.getNamespaceURI()));
                }
                this.setService(this.axisEngine.getConfig().getServiceByNamespaceURI(qname.getNamespaceURI()));
            }
            catch (ConfigurationException e) {
                // empty catch block
            }
        }
        if (this.serviceHandler != null && (desc = this.serviceHandler.getInitializedServiceDesc(this)) != null) {
            if (desc.getStyle() != Style.DOCUMENT) {
                possibleOperations = desc.getOperationsByQName(qname);
            } else {
                ArrayList allOperations = desc.getOperations();
                ArrayList<OperationDesc> foundOperations = new ArrayList<OperationDesc>();
                for (int i = 0; i < allOperations.size(); ++i) {
                    OperationDesc tryOp = (OperationDesc)allOperations.get(i);
                    if (tryOp.getParamByQName(qname) == null) continue;
                    foundOperations.add(tryOp);
                }
                if (foundOperations.size() > 0) {
                    possibleOperations = (OperationDesc[])JavaUtils.convert(foundOperations, array$Lorg$apache$axis$description$OperationDesc == null ? (array$Lorg$apache$axis$description$OperationDesc = MessageContext.class$("[Lorg.apache.axis.description.OperationDesc;")) : array$Lorg$apache$axis$description$OperationDesc);
                }
            }
        }
        return possibleOperations;
    }

    public OperationDesc getOperationByQName(QName qname) throws AxisFault {
        OperationDesc[] possibleOperations;
        if (this.currentOperation == null && (possibleOperations = this.getPossibleOperationsByQName(qname)) != null && possibleOperations.length > 0) {
            this.currentOperation = possibleOperations[0];
        }
        return this.currentOperation;
    }

    public static MessageContext getCurrentContext() {
        return AxisEngine.getCurrentMessageContext();
    }

    public MessageContext(AxisEngine engine) {
        this.axisEngine = engine;
        if (null != engine) {
            String singleSOAPVersion;
            String defaultSOAPVersion;
            Hashtable opts = engine.getOptions();
            String attachmentsdir = null;
            if (null != opts) {
                attachmentsdir = (String)opts.get("attachments.Directory");
            }
            if (null == attachmentsdir) {
                attachmentsdir = systemTempDir;
            }
            if (attachmentsdir != null) {
                this.setProperty(ATTACHMENTS_DIR, attachmentsdir);
            }
            if ((defaultSOAPVersion = (String)engine.getOption("defaultSOAPVersion")) != null && "1.2".equals(defaultSOAPVersion)) {
                this.setSOAPConstants(SOAPConstants.SOAP12_CONSTANTS);
            }
            if ((singleSOAPVersion = (String)engine.getOption("singleSOAPVersion")) != null) {
                if ("1.2".equals(singleSOAPVersion)) {
                    this.setProperty("SingleSOAPVersion", SOAPConstants.SOAP12_CONSTANTS);
                } else if ("1.1".equals(singleSOAPVersion)) {
                    this.setProperty("SingleSOAPVersion", SOAPConstants.SOAP11_CONSTANTS);
                }
            }
        }
    }

    protected void finalize() {
        this.dispose();
    }

    public void setTypeMappingRegistry(TypeMappingRegistry reg) {
        this.mappingRegistry = reg;
    }

    public TypeMappingRegistry getTypeMappingRegistry() {
        if (this.mappingRegistry == null) {
            return this.axisEngine.getTypeMappingRegistry();
        }
        return this.mappingRegistry;
    }

    public TypeMapping getTypeMapping() {
        return (TypeMapping)this.getTypeMappingRegistry().getTypeMapping(this.encodingStyle);
    }

    public String getTransportName() {
        return this.transportName;
    }

    public void setTransportName(String transportName) {
        this.transportName = transportName;
    }

    public SOAPConstants getSOAPConstants() {
        return this.soapConstants;
    }

    public void setSOAPConstants(SOAPConstants soapConstants) {
        if (this.soapConstants.getEncodingURI().equals(this.encodingStyle)) {
            this.encodingStyle = soapConstants.getEncodingURI();
        }
        this.soapConstants = soapConstants;
    }

    public SchemaVersion getSchemaVersion() {
        return this.schemaVersion;
    }

    public void setSchemaVersion(SchemaVersion schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    public Session getSession() {
        return this.session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public boolean isEncoded() {
        return this.getOperationUse() == Use.ENCODED;
    }

    public void setMaintainSession(boolean yesno) {
        this.maintainSession = yesno;
    }

    public boolean getMaintainSession() {
        return this.maintainSession;
    }

    public Message getRequestMessage() {
        return this.requestMessage;
    }

    public void setRequestMessage(Message reqMsg) {
        this.requestMessage = reqMsg;
        if (this.requestMessage != null) {
            this.requestMessage.setMessageContext(this);
        }
    }

    public Message getResponseMessage() {
        return this.responseMessage;
    }

    public void setResponseMessage(Message respMsg) {
        this.responseMessage = respMsg;
        if (this.responseMessage != null) {
            this.responseMessage.setMessageContext(this);
            Message reqMsg = this.getRequestMessage();
            if (null != reqMsg) {
                Attachments reqAttch = reqMsg.getAttachmentsImpl();
                Attachments respAttch = respMsg.getAttachmentsImpl();
                if (null != reqAttch && null != respAttch && respAttch.getSendType() == 1) {
                    respAttch.setSendType(reqAttch.getSendType());
                }
            }
        }
    }

    public Message getCurrentMessage() {
        return this.havePassedPivot ? this.responseMessage : this.requestMessage;
    }

    public SOAPMessage getMessage() {
        return this.getCurrentMessage();
    }

    public void setCurrentMessage(Message curMsg) {
        curMsg.setMessageContext(this);
        if (this.havePassedPivot) {
            this.responseMessage = curMsg;
        } else {
            this.requestMessage = curMsg;
        }
    }

    public void setMessage(SOAPMessage message) {
        this.setCurrentMessage((Message)message);
    }

    public boolean getPastPivot() {
        return this.havePassedPivot;
    }

    public void setPastPivot(boolean pastPivot) {
        this.havePassedPivot = pastPivot;
    }

    public void setTimeout(int value) {
        this.timeout = value;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public ClassLoader getClassLoader() {
        if (this.classLoader == null) {
            this.classLoader = Thread.currentThread().getContextClassLoader();
        }
        return this.classLoader;
    }

    public void setClassLoader(ClassLoader cl) {
        this.classLoader = cl;
    }

    public String getTargetService() {
        return this.targetService;
    }

    public AxisEngine getAxisEngine() {
        return this.axisEngine;
    }

    public void setTargetService(String tServ) throws AxisFault {
        block4: {
            log.debug((Object)("MessageContext: setTargetService(" + tServ + ")"));
            if (tServ == null) {
                this.setService(null);
            } else {
                try {
                    this.setService(this.getAxisEngine().getService(tServ));
                }
                catch (AxisFault fault) {
                    if (this.isClient()) break block4;
                    throw fault;
                }
            }
        }
        this.targetService = tServ;
    }

    public SOAPService getService() {
        return this.serviceHandler;
    }

    public void setService(SOAPService sh) throws AxisFault {
        log.debug((Object)("MessageContext: setServiceHandler(" + sh + ")"));
        this.serviceHandler = sh;
        if (sh != null) {
            if (!sh.isRunning()) {
                throw new AxisFault(Messages.getMessage("disabled00"));
            }
            this.targetService = sh.getName();
            SOAPService service = sh;
            TypeMappingRegistry tmr = service.getTypeMappingRegistry();
            this.setTypeMappingRegistry(tmr);
            this.setEncodingStyle(service.getUse().getEncoding());
            this.bag.setParent(sh.getOptions());
            this.highFidelity = service.needsHighFidelityRecording();
            service.getInitializedServiceDesc(this);
        }
    }

    public boolean isClient() {
        return this.axisEngine instanceof AxisClient;
    }

    public String getStrProp(String propName) {
        return (String)this.getProperty(propName);
    }

    public boolean isPropertyTrue(String propName) {
        return this.isPropertyTrue(propName, false);
    }

    public boolean isPropertyTrue(String propName, boolean defaultVal) {
        return JavaUtils.isTrue(this.getProperty(propName), defaultVal);
    }

    public void setProperty(String name, Object value) {
        if (name == null || value == null) {
            return;
        }
        if (name.equals("javax.xml.rpc.security.auth.username")) {
            if (!(value instanceof String)) {
                throw new IllegalArgumentException(Messages.getMessage("badProp00", new String[]{name, "java.lang.String", value.getClass().getName()}));
            }
            this.setUsername((String)value);
        } else if (name.equals("javax.xml.rpc.security.auth.password")) {
            if (!(value instanceof String)) {
                throw new IllegalArgumentException(Messages.getMessage("badProp00", new String[]{name, "java.lang.String", value.getClass().getName()}));
            }
            this.setPassword((String)value);
        } else if (name.equals("javax.xml.rpc.session.maintain")) {
            if (!(value instanceof Boolean)) {
                throw new IllegalArgumentException(Messages.getMessage("badProp00", new String[]{name, "java.lang.Boolean", value.getClass().getName()}));
            }
            this.setMaintainSession((Boolean)value);
        } else if (name.equals("javax.xml.rpc.soap.http.soapaction.use")) {
            if (!(value instanceof Boolean)) {
                throw new IllegalArgumentException(Messages.getMessage("badProp00", new String[]{name, "java.lang.Boolean", value.getClass().getName()}));
            }
            this.setUseSOAPAction((Boolean)value);
        } else if (name.equals("javax.xml.rpc.soap.http.soapaction.uri")) {
            if (!(value instanceof String)) {
                throw new IllegalArgumentException(Messages.getMessage("badProp00", new String[]{name, "java.lang.String", value.getClass().getName()}));
            }
            this.setSOAPActionURI((String)value);
        } else if (name.equals("javax.xml.rpc.encodingstyle.namespace.uri")) {
            if (!(value instanceof String)) {
                throw new IllegalArgumentException(Messages.getMessage("badProp00", new String[]{name, "java.lang.String", value.getClass().getName()}));
            }
            this.setEncodingStyle((String)value);
        } else {
            this.bag.put(name, value);
        }
    }

    public boolean containsProperty(String name) {
        Object propertyValue = this.getProperty(name);
        return propertyValue != null;
    }

    public Iterator getPropertyNames() {
        return this.bag.keySet().iterator();
    }

    public Iterator getAllPropertyNames() {
        return this.bag.getAllKeys().iterator();
    }

    public Object getProperty(String name) {
        if (name != null) {
            if (name.equals("javax.xml.rpc.security.auth.username")) {
                return this.getUsername();
            }
            if (name.equals("javax.xml.rpc.security.auth.password")) {
                return this.getPassword();
            }
            if (name.equals("javax.xml.rpc.session.maintain")) {
                return this.getMaintainSession() ? Boolean.TRUE : Boolean.FALSE;
            }
            if (name.equals("javax.xml.rpc.soap.operation.style")) {
                return this.getOperationStyle() == null ? null : this.getOperationStyle().getName();
            }
            if (name.equals("javax.xml.rpc.soap.http.soapaction.use")) {
                return this.useSOAPAction() ? Boolean.TRUE : Boolean.FALSE;
            }
            if (name.equals("javax.xml.rpc.soap.http.soapaction.uri")) {
                return this.getSOAPActionURI();
            }
            if (name.equals("javax.xml.rpc.encodingstyle.namespace.uri")) {
                return this.getEncodingStyle();
            }
            if (this.bag == null) {
                return null;
            }
            return this.bag.get(name);
        }
        return null;
    }

    public void setPropertyParent(Hashtable parent) {
        this.bag.setParent(parent);
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

    public Style getOperationStyle() {
        if (this.currentOperation != null) {
            return this.currentOperation.getStyle();
        }
        if (this.serviceHandler != null) {
            return this.serviceHandler.getStyle();
        }
        return Style.RPC;
    }

    public Use getOperationUse() {
        if (this.currentOperation != null) {
            return this.currentOperation.getUse();
        }
        if (this.serviceHandler != null) {
            return this.serviceHandler.getUse();
        }
        return Use.ENCODED;
    }

    public void setUseSOAPAction(boolean useSOAPAction) {
        this.useSOAPAction = useSOAPAction;
    }

    public boolean useSOAPAction() {
        return this.useSOAPAction;
    }

    public void setSOAPActionURI(String SOAPActionURI) throws IllegalArgumentException {
        this.SOAPActionURI = SOAPActionURI;
    }

    public String getSOAPActionURI() {
        return this.SOAPActionURI;
    }

    public void setEncodingStyle(String namespaceURI) {
        if (namespaceURI == null) {
            namespaceURI = "";
        } else if (Constants.isSOAP_ENC(namespaceURI)) {
            namespaceURI = this.soapConstants.getEncodingURI();
        }
        this.encodingStyle = namespaceURI;
    }

    public String getEncodingStyle() {
        return this.encodingStyle;
    }

    public void removeProperty(String propName) {
        if (this.bag != null) {
            this.bag.remove(propName);
        }
    }

    public void reset() {
        if (this.bag != null) {
            this.bag.clear();
        }
        this.serviceHandler = null;
        this.havePassedPivot = false;
        this.currentOperation = null;
    }

    public boolean isHighFidelity() {
        return this.highFidelity;
    }

    public void setHighFidelity(boolean highFidelity) {
        this.highFidelity = highFidelity;
    }

    public String[] getRoles() {
        return this.roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public synchronized void dispose() {
        log.debug((Object)"disposing of message context");
        if (this.requestMessage != null) {
            this.requestMessage.dispose();
            this.requestMessage = null;
        }
        if (this.responseMessage != null) {
            this.responseMessage.dispose();
            this.responseMessage = null;
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
        try {
            systemTempDir = AxisProperties.getProperty("axis.attachments.Directory");
        }
        catch (Throwable t) {
            systemTempDir = null;
        }
        if (systemTempDir == null) {
            try {
                File tf = File.createTempFile("Axis", ".tmp");
                File dir = tf.getParentFile();
                if (tf.exists()) {
                    tf.delete();
                }
                if (dir != null) {
                    systemTempDir = dir.getCanonicalPath();
                }
            }
            catch (Throwable t) {
                log.debug((Object)"Unable to find a temp dir with write access");
                systemTempDir = null;
            }
        }
    }
}

