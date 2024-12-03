/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.xml.namespace.QName;
import javax.xml.rpc.server.ServiceLifecycle;
import org.apache.axis.AxisFault;
import org.apache.axis.AxisProperties;
import org.apache.axis.ConfigurationException;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.InternalException;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.TypeMappingImpl;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.session.Session;
import org.apache.axis.session.SimpleSession;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.cache.ClassCache;
import org.apache.commons.logging.Log;

public abstract class AxisEngine
extends BasicHandler {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$AxisEngine == null ? (class$org$apache$axis$AxisEngine = AxisEngine.class$("org.apache.axis.AxisEngine")) : class$org$apache$axis$AxisEngine).getName());
    public static final String PROP_XML_DECL = "sendXMLDeclaration";
    public static final String PROP_DEBUG_LEVEL = "debugLevel";
    public static final String PROP_DEBUG_FILE = "debugFile";
    public static final String PROP_DOMULTIREFS = "sendMultiRefs";
    public static final String PROP_DISABLE_PRETTY_XML = "disablePrettyXML";
    public static final String PROP_ENABLE_NAMESPACE_PREFIX_OPTIMIZATION = "enableNamespacePrefixOptimization";
    public static final String PROP_PASSWORD = "adminPassword";
    public static final String PROP_SYNC_CONFIG = "syncConfiguration";
    public static final String PROP_SEND_XSI = "sendXsiTypes";
    public static final String PROP_ATTACHMENT_DIR = "attachments.Directory";
    public static final String PROP_ATTACHMENT_IMPLEMENTATION = "attachments.implementation";
    public static final String PROP_ATTACHMENT_CLEANUP = "attachment.DirectoryCleanUp";
    public static final String PROP_DEFAULT_CONFIG_CLASS = "axis.engineConfigClass";
    public static final String PROP_SOAP_VERSION = "defaultSOAPVersion";
    public static final String PROP_SOAP_ALLOWED_VERSION = "singleSOAPVersion";
    public static final String PROP_TWOD_ARRAY_ENCODING = "enable2DArrayEncoding";
    public static final String PROP_XML_ENCODING = "axis.xmlEncoding";
    public static final String PROP_XML_REUSE_SAX_PARSERS = "axis.xml.reuseParsers";
    public static final String PROP_BYTE_BUFFER_BACKING = "axis.byteBuffer.backing";
    public static final String PROP_BYTE_BUFFER_CACHE_INCREMENT = "axis.byteBuffer.cacheIncrement";
    public static final String PROP_BYTE_BUFFER_RESIDENT_MAX_SIZE = "axis.byteBuffer.residentMaxSize";
    public static final String PROP_BYTE_BUFFER_WORK_BUFFER_SIZE = "axis.byteBuffer.workBufferSize";
    public static final String PROP_EMIT_ALL_TYPES = "emitAllTypesInWSDL";
    public static final String PROP_DOTNET_SOAPENC_FIX = "dotNetSoapEncFix";
    public static final String PROP_BP10_COMPLIANCE = "ws-i.bp10Compliance";
    public static final String DEFAULT_ATTACHMENT_IMPL = "org.apache.axis.attachments.AttachmentsImpl";
    public static final String ENV_ATTACHMENT_DIR = "axis.attachments.Directory";
    public static final String ENV_SERVLET_REALPATH = "servlet.realpath";
    public static final String ENV_SERVLET_CONTEXT = "servletContext";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin";
    protected EngineConfiguration config;
    protected boolean _hasSafePassword = false;
    protected boolean shouldSaveConfig = false;
    protected transient ClassCache classCache = new ClassCache();
    private Session session = new SimpleSession();
    private ArrayList actorURIs = new ArrayList();
    private static ThreadLocal currentMessageContext = new ThreadLocal();
    private static final String[] BOOLEAN_OPTIONS = new String[]{"sendMultiRefs", "sendXsiTypes", "sendXMLDeclaration", "disablePrettyXML", "enableNamespacePrefixOptimization"};
    static /* synthetic */ Class class$org$apache$axis$AxisEngine;

    protected static void setCurrentMessageContext(MessageContext mc) {
        currentMessageContext.set(mc);
    }

    public static MessageContext getCurrentMessageContext() {
        return (MessageContext)currentMessageContext.get();
    }

    public AxisEngine(EngineConfiguration config) {
        this.config = config;
        this.init();
    }

    public void init() {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: AxisEngine::init");
        }
        try {
            this.config.configureEngine(this);
        }
        catch (Exception e) {
            throw new InternalException(e);
        }
        this.setOptionDefault(PROP_ATTACHMENT_IMPLEMENTATION, AxisProperties.getProperty("axis.attachments.implementation"));
        this.setOptionDefault(PROP_ATTACHMENT_IMPLEMENTATION, DEFAULT_ATTACHMENT_IMPL);
        Object dotnet = this.getOption(PROP_DOTNET_SOAPENC_FIX);
        if (JavaUtils.isTrue(dotnet)) {
            TypeMappingImpl.dotnet_soapenc_bugfix = true;
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: AxisEngine::init");
        }
    }

    public void cleanup() {
        super.cleanup();
        Enumeration keys = this.session.getKeys();
        if (keys != null) {
            while (keys.hasMoreElements()) {
                String key = (String)keys.nextElement();
                Object obj = this.session.get(key);
                if (obj != null && obj instanceof ServiceLifecycle) {
                    ((ServiceLifecycle)obj).destroy();
                }
                this.session.remove(key);
            }
        }
    }

    public void saveConfiguration() {
        if (!this.shouldSaveConfig) {
            return;
        }
        try {
            this.config.writeEngineConfig(this);
        }
        catch (Exception e) {
            log.error((Object)Messages.getMessage("saveConfigFail00"), (Throwable)e);
        }
    }

    public EngineConfiguration getConfig() {
        return this.config;
    }

    public boolean hasSafePassword() {
        return this._hasSafePassword;
    }

    public void setAdminPassword(String pw) {
        this.setOption(PROP_PASSWORD, pw);
        this._hasSafePassword = true;
        this.saveConfiguration();
    }

    public void setShouldSaveConfig(boolean shouldSaveConfig) {
        this.shouldSaveConfig = shouldSaveConfig;
    }

    public Handler getHandler(String name) throws AxisFault {
        try {
            return this.config.getHandler(new QName(null, name));
        }
        catch (ConfigurationException e) {
            throw new AxisFault(e);
        }
    }

    public SOAPService getService(String name) throws AxisFault {
        try {
            return this.config.getService(new QName(null, name));
        }
        catch (ConfigurationException e) {
            try {
                return this.config.getServiceByNamespaceURI(name);
            }
            catch (ConfigurationException e1) {
                throw new AxisFault(e);
            }
        }
    }

    public Handler getTransport(String name) throws AxisFault {
        try {
            return this.config.getTransport(new QName(null, name));
        }
        catch (ConfigurationException e) {
            throw new AxisFault(e);
        }
    }

    public TypeMappingRegistry getTypeMappingRegistry() {
        TypeMappingRegistry tmr = null;
        try {
            tmr = this.config.getTypeMappingRegistry();
        }
        catch (ConfigurationException e) {
            log.error((Object)Messages.getMessage("axisConfigurationException00"), (Throwable)e);
        }
        return tmr;
    }

    public Handler getGlobalRequest() throws ConfigurationException {
        return this.config.getGlobalRequest();
    }

    public Handler getGlobalResponse() throws ConfigurationException {
        return this.config.getGlobalResponse();
    }

    public ArrayList getActorURIs() {
        return (ArrayList)this.actorURIs.clone();
    }

    public void addActorURI(String uri) {
        this.actorURIs.add(uri);
    }

    public void removeActorURI(String uri) {
        this.actorURIs.remove(uri);
    }

    public abstract AxisEngine getClientEngine();

    public static void normaliseOptions(Handler handler) {
        AxisEngine engine;
        for (int i = 0; i < BOOLEAN_OPTIONS.length; ++i) {
            Object val = handler.getOption(BOOLEAN_OPTIONS[i]);
            if (val != null) {
                if (val instanceof Boolean) continue;
                if (JavaUtils.isFalse(val)) {
                    handler.setOption(BOOLEAN_OPTIONS[i], Boolean.FALSE);
                    continue;
                }
            } else if (!(handler instanceof AxisEngine)) continue;
            handler.setOption(BOOLEAN_OPTIONS[i], Boolean.TRUE);
        }
        if (handler instanceof AxisEngine && !(engine = (AxisEngine)handler).setOptionDefault(PROP_PASSWORD, DEFAULT_ADMIN_PASSWORD)) {
            engine.setAdminPassword((String)engine.getOption(PROP_PASSWORD));
        }
    }

    public void refreshGlobalOptions() throws ConfigurationException {
        Hashtable globalOptions = this.config.getGlobalOptions();
        if (globalOptions != null) {
            this.setOptions(globalOptions);
        }
        AxisEngine.normaliseOptions(this);
        this.actorURIs = new ArrayList(this.config.getRoles());
    }

    public Session getApplicationSession() {
        return this.session;
    }

    public ClassCache getClassCache() {
        return this.classCache;
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

