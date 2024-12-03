/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.coyote.AbstractProtocol
 *  org.apache.coyote.Adapter
 *  org.apache.coyote.ProtocolHandler
 *  org.apache.coyote.UpgradeProtocol
 *  org.apache.coyote.ajp.AjpAprProtocol
 *  org.apache.coyote.ajp.AjpNioProtocol
 *  org.apache.coyote.http11.AbstractHttp11JsseProtocol
 *  org.apache.coyote.http11.Http11AprProtocol
 *  org.apache.coyote.http11.Http11NioProtocol
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.IntrospectionUtils
 *  org.apache.tomcat.util.buf.B2CConverter
 *  org.apache.tomcat.util.buf.CharsetUtil
 *  org.apache.tomcat.util.buf.EncodedSolidusHandling
 *  org.apache.tomcat.util.buf.UDecoder
 *  org.apache.tomcat.util.net.SSLHostConfig
 *  org.apache.tomcat.util.net.openssl.OpenSSLImplementation
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.connector;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import javax.management.ObjectName;
import org.apache.catalina.Executor;
import org.apache.catalina.Globals;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Service;
import org.apache.catalina.connector.CoyoteAdapter;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.AprStatus;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.Adapter;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.UpgradeProtocol;
import org.apache.coyote.ajp.AjpAprProtocol;
import org.apache.coyote.ajp.AjpNioProtocol;
import org.apache.coyote.http11.AbstractHttp11JsseProtocol;
import org.apache.coyote.http11.Http11AprProtocol;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.CharsetUtil;
import org.apache.tomcat.util.buf.EncodedSolidusHandling;
import org.apache.tomcat.util.buf.UDecoder;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.openssl.OpenSSLImplementation;
import org.apache.tomcat.util.res.StringManager;

public class Connector
extends LifecycleMBeanBase {
    private static final Log log = LogFactory.getLog(Connector.class);
    public static final boolean RECYCLE_FACADES = Boolean.parseBoolean(System.getProperty("org.apache.catalina.connector.RECYCLE_FACADES", "false"));
    public static final String INTERNAL_EXECUTOR_NAME = "Internal";
    protected Service service = null;
    protected boolean allowTrace = false;
    protected long asyncTimeout = 30000L;
    protected boolean enableLookups = false;
    protected boolean xpoweredBy = false;
    protected String proxyName = null;
    protected int proxyPort = 0;
    protected boolean discardFacades = RECYCLE_FACADES;
    protected int redirectPort = 443;
    protected String scheme = "http";
    protected boolean secure = false;
    protected static final StringManager sm = StringManager.getManager(Connector.class);
    private int maxCookieCount = 200;
    protected int maxParameterCount = 10000;
    protected int maxPostSize = 0x200000;
    protected int maxSavePostSize = 4096;
    protected String parseBodyMethods = "POST";
    protected HashSet<String> parseBodyMethodsSet;
    protected boolean useIPVHosts = false;
    protected final String protocolHandlerClassName;
    protected final ProtocolHandler protocolHandler;
    protected Adapter adapter = null;
    private Charset uriCharset = StandardCharsets.UTF_8;
    private EncodedSolidusHandling encodedSolidusHandling = UDecoder.ALLOW_ENCODED_SLASH ? EncodedSolidusHandling.DECODE : EncodedSolidusHandling.REJECT;
    protected boolean useBodyEncodingForURI = false;

    public Connector() {
        this("HTTP/1.1");
    }

    public Connector(String protocol) {
        boolean apr = AprStatus.getUseAprConnector() && AprStatus.isInstanceCreated() && AprLifecycleListener.isAprAvailable();
        ProtocolHandler p = null;
        try {
            p = ProtocolHandler.create((String)protocol, (boolean)apr);
        }
        catch (Exception e) {
            log.error((Object)sm.getString("coyoteConnector.protocolHandlerInstantiationFailed"), (Throwable)e);
        }
        if (p != null) {
            this.protocolHandler = p;
            this.protocolHandlerClassName = this.protocolHandler.getClass().getName();
        } else {
            this.protocolHandler = null;
            this.protocolHandlerClassName = protocol;
        }
        this.setThrowOnFailure(Boolean.getBoolean("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE"));
    }

    public Connector(ProtocolHandler protocolHandler) {
        this.protocolHandlerClassName = protocolHandler.getClass().getName();
        this.protocolHandler = protocolHandler;
        this.setThrowOnFailure(Boolean.getBoolean("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE"));
    }

    public Object getProperty(String name) {
        if (this.protocolHandler == null) {
            return null;
        }
        return IntrospectionUtils.getProperty((Object)this.protocolHandler, (String)name);
    }

    public boolean setProperty(String name, String value) {
        if (this.protocolHandler == null) {
            return false;
        }
        return IntrospectionUtils.setProperty((Object)this.protocolHandler, (String)name, (String)value);
    }

    @Deprecated
    public Object getAttribute(String name) {
        return this.getProperty(name);
    }

    @Deprecated
    public void setAttribute(String name, Object value) {
        this.setProperty(name, String.valueOf(value));
    }

    public Service getService() {
        return this.service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public boolean getAllowTrace() {
        return this.allowTrace;
    }

    public void setAllowTrace(boolean allowTrace) {
        this.allowTrace = allowTrace;
        this.setProperty("allowTrace", String.valueOf(allowTrace));
    }

    public long getAsyncTimeout() {
        return this.asyncTimeout;
    }

    public void setAsyncTimeout(long asyncTimeout) {
        this.asyncTimeout = asyncTimeout;
    }

    public boolean getDiscardFacades() {
        return this.discardFacades || Globals.IS_SECURITY_ENABLED;
    }

    public void setDiscardFacades(boolean discardFacades) {
        this.discardFacades = discardFacades;
    }

    public boolean getEnableLookups() {
        return this.enableLookups;
    }

    public void setEnableLookups(boolean enableLookups) {
        this.enableLookups = enableLookups;
        this.setProperty("enableLookups", String.valueOf(enableLookups));
    }

    public int getMaxCookieCount() {
        return this.maxCookieCount;
    }

    public void setMaxCookieCount(int maxCookieCount) {
        this.maxCookieCount = maxCookieCount;
    }

    public int getMaxParameterCount() {
        return this.maxParameterCount;
    }

    public void setMaxParameterCount(int maxParameterCount) {
        this.maxParameterCount = maxParameterCount;
        this.setProperty("maxParameterCount", String.valueOf(maxParameterCount));
    }

    public int getMaxPostSize() {
        return this.maxPostSize;
    }

    public void setMaxPostSize(int maxPostSize) {
        this.maxPostSize = maxPostSize;
        this.setProperty("maxPostSize", String.valueOf(maxPostSize));
    }

    public int getMaxSavePostSize() {
        return this.maxSavePostSize;
    }

    public void setMaxSavePostSize(int maxSavePostSize) {
        this.maxSavePostSize = maxSavePostSize;
        this.setProperty("maxSavePostSize", String.valueOf(maxSavePostSize));
    }

    public String getParseBodyMethods() {
        return this.parseBodyMethods;
    }

    public void setParseBodyMethods(String methods) {
        HashSet<String> methodSet = new HashSet<String>();
        if (null != methods) {
            methodSet.addAll(Arrays.asList(methods.split("\\s*,\\s*")));
        }
        if (methodSet.contains("TRACE")) {
            throw new IllegalArgumentException(sm.getString("coyoteConnector.parseBodyMethodNoTrace"));
        }
        this.parseBodyMethods = methods;
        this.parseBodyMethodsSet = methodSet;
        this.setProperty("parseBodyMethods", methods);
    }

    protected boolean isParseBodyMethod(String method) {
        return this.parseBodyMethodsSet.contains(method);
    }

    public int getPort() {
        if (this.protocolHandler instanceof AbstractProtocol) {
            return ((AbstractProtocol)this.protocolHandler).getPort();
        }
        Object port = this.getProperty("port");
        if (port instanceof Integer) {
            return (Integer)port;
        }
        return -1;
    }

    public void setPort(int port) {
        this.setProperty("port", String.valueOf(port));
    }

    public int getPortOffset() {
        if (this.protocolHandler instanceof AbstractProtocol) {
            return ((AbstractProtocol)this.protocolHandler).getPortOffset();
        }
        Object port = this.getProperty("portOffset");
        if (port instanceof Integer) {
            return (Integer)port;
        }
        return 0;
    }

    public void setPortOffset(int portOffset) {
        this.setProperty("portOffset", String.valueOf(portOffset));
    }

    public int getPortWithOffset() {
        int port = this.getPort();
        if (port > 0) {
            return port + this.getPortOffset();
        }
        return port;
    }

    public int getLocalPort() {
        return (Integer)this.getProperty("localPort");
    }

    public String getProtocol() {
        boolean apr = AprStatus.getUseAprConnector();
        if (!apr && Http11NioProtocol.class.getName().equals(this.protocolHandlerClassName) || apr && Http11AprProtocol.class.getName().equals(this.protocolHandlerClassName)) {
            return "HTTP/1.1";
        }
        if (!apr && AjpNioProtocol.class.getName().equals(this.protocolHandlerClassName) || apr && AjpAprProtocol.class.getName().equals(this.protocolHandlerClassName)) {
            return "AJP/1.3";
        }
        return this.protocolHandlerClassName;
    }

    public String getProtocolHandlerClassName() {
        return this.protocolHandlerClassName;
    }

    public ProtocolHandler getProtocolHandler() {
        return this.protocolHandler;
    }

    public String getProxyName() {
        return this.proxyName;
    }

    public void setProxyName(String proxyName) {
        this.proxyName = proxyName != null && proxyName.length() > 0 ? proxyName : null;
        this.setProperty("proxyName", this.proxyName);
    }

    public int getProxyPort() {
        return this.proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
        this.setProperty("proxyPort", String.valueOf(proxyPort));
    }

    public int getRedirectPort() {
        return this.redirectPort;
    }

    public void setRedirectPort(int redirectPort) {
        this.redirectPort = redirectPort;
        this.setProperty("redirectPort", String.valueOf(redirectPort));
    }

    public int getRedirectPortWithOffset() {
        return this.getRedirectPort() + this.getPortOffset();
    }

    public String getScheme() {
        return this.scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public boolean getSecure() {
        return this.secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
        this.setProperty("secure", Boolean.toString(secure));
    }

    public String getURIEncoding() {
        return this.uriCharset.name();
    }

    public Charset getURICharset() {
        return this.uriCharset;
    }

    public void setURIEncoding(String URIEncoding) {
        try {
            Charset charset = B2CConverter.getCharset((String)URIEncoding);
            if (!CharsetUtil.isAsciiSuperset((Charset)charset)) {
                log.error((Object)sm.getString("coyoteConnector.notAsciiSuperset", new Object[]{URIEncoding}));
            }
            this.uriCharset = charset;
        }
        catch (UnsupportedEncodingException e) {
            log.error((Object)sm.getString("coyoteConnector.invalidEncoding", new Object[]{URIEncoding, this.uriCharset.name()}), (Throwable)e);
        }
    }

    public boolean getUseBodyEncodingForURI() {
        return this.useBodyEncodingForURI;
    }

    public void setUseBodyEncodingForURI(boolean useBodyEncodingForURI) {
        this.useBodyEncodingForURI = useBodyEncodingForURI;
        this.setProperty("useBodyEncodingForURI", String.valueOf(useBodyEncodingForURI));
    }

    public boolean getXpoweredBy() {
        return this.xpoweredBy;
    }

    public void setXpoweredBy(boolean xpoweredBy) {
        this.xpoweredBy = xpoweredBy;
        this.setProperty("xpoweredBy", String.valueOf(xpoweredBy));
    }

    public void setUseIPVHosts(boolean useIPVHosts) {
        this.useIPVHosts = useIPVHosts;
        this.setProperty("useIPVHosts", String.valueOf(useIPVHosts));
    }

    public boolean getUseIPVHosts() {
        return this.useIPVHosts;
    }

    public String getExecutorName() {
        java.util.concurrent.Executor obj = this.protocolHandler.getExecutor();
        if (obj instanceof Executor) {
            return ((Executor)obj).getName();
        }
        return INTERNAL_EXECUTOR_NAME;
    }

    public void addSslHostConfig(SSLHostConfig sslHostConfig) {
        this.protocolHandler.addSslHostConfig(sslHostConfig);
    }

    public SSLHostConfig[] findSslHostConfigs() {
        return this.protocolHandler.findSslHostConfigs();
    }

    public void addUpgradeProtocol(UpgradeProtocol upgradeProtocol) {
        this.protocolHandler.addUpgradeProtocol(upgradeProtocol);
    }

    public UpgradeProtocol[] findUpgradeProtocols() {
        return this.protocolHandler.findUpgradeProtocols();
    }

    public String getEncodedSolidusHandling() {
        return this.encodedSolidusHandling.getValue();
    }

    public void setEncodedSolidusHandling(String encodedSolidusHandling) {
        this.encodedSolidusHandling = EncodedSolidusHandling.fromString((String)encodedSolidusHandling);
    }

    public EncodedSolidusHandling getEncodedSolidusHandlingInternal() {
        return this.encodedSolidusHandling;
    }

    public Request createRequest() {
        return new Request(this);
    }

    public Response createResponse() {
        int size = this.protocolHandler.getDesiredBufferSize();
        if (size > 0) {
            return new Response(size);
        }
        return new Response();
    }

    protected String createObjectNameKeyProperties(String type) {
        String id;
        Object addressObj = this.getProperty("address");
        StringBuilder sb = new StringBuilder("type=");
        sb.append(type);
        String string = id = this.protocolHandler != null ? this.protocolHandler.getId() : null;
        if (id != null) {
            sb.append(",port=0,address=");
            sb.append(ObjectName.quote(id));
        } else {
            sb.append(",port=");
            int port = this.getPortWithOffset();
            if (port > 0) {
                sb.append(port);
            } else {
                sb.append("auto-");
                sb.append(this.getProperty("nameIndex"));
            }
            String address = "";
            if (addressObj instanceof InetAddress) {
                address = ((InetAddress)addressObj).getHostAddress();
            } else if (addressObj != null) {
                address = addressObj.toString();
            }
            if (address.length() > 0) {
                sb.append(",address=");
                sb.append(ObjectName.quote(address));
            }
        }
        return sb.toString();
    }

    public void pause() {
        try {
            if (this.protocolHandler != null) {
                this.protocolHandler.pause();
            }
        }
        catch (Exception e) {
            log.error((Object)sm.getString("coyoteConnector.protocolHandlerPauseFailed"), (Throwable)e);
        }
    }

    public void resume() {
        try {
            if (this.protocolHandler != null) {
                this.protocolHandler.resume();
            }
        }
        catch (Exception e) {
            log.error((Object)sm.getString("coyoteConnector.protocolHandlerResumeFailed"), (Throwable)e);
        }
    }

    @Override
    protected void initInternal() throws LifecycleException {
        AbstractHttp11JsseProtocol jsseProtocolHandler;
        super.initInternal();
        if (this.protocolHandler == null) {
            throw new LifecycleException(sm.getString("coyoteConnector.protocolHandlerInstantiationFailed"));
        }
        this.adapter = new CoyoteAdapter(this);
        this.protocolHandler.setAdapter(this.adapter);
        if (null == this.parseBodyMethodsSet) {
            this.setParseBodyMethods(this.getParseBodyMethods());
        }
        if (this.protocolHandler.isAprRequired() && !AprStatus.isInstanceCreated()) {
            throw new LifecycleException(sm.getString("coyoteConnector.protocolHandlerNoAprListener", new Object[]{this.getProtocolHandlerClassName()}));
        }
        if (this.protocolHandler.isAprRequired() && !AprStatus.isAprAvailable()) {
            throw new LifecycleException(sm.getString("coyoteConnector.protocolHandlerNoAprLibrary", new Object[]{this.getProtocolHandlerClassName()}));
        }
        if (AprStatus.isAprAvailable() && AprStatus.getUseOpenSSL() && this.protocolHandler instanceof AbstractHttp11JsseProtocol && (jsseProtocolHandler = (AbstractHttp11JsseProtocol)this.protocolHandler).isSSLEnabled() && jsseProtocolHandler.getSslImplementationName() == null) {
            jsseProtocolHandler.setSslImplementationName(OpenSSLImplementation.class.getName());
        }
        try {
            this.protocolHandler.init();
        }
        catch (Exception e) {
            throw new LifecycleException(sm.getString("coyoteConnector.protocolHandlerInitializationFailed"), e);
        }
    }

    @Override
    protected void startInternal() throws LifecycleException {
        String id;
        String string = id = this.protocolHandler != null ? this.protocolHandler.getId() : null;
        if (id == null && this.getPortWithOffset() < 0) {
            throw new LifecycleException(sm.getString("coyoteConnector.invalidPort", new Object[]{this.getPortWithOffset()}));
        }
        this.setState(LifecycleState.STARTING);
        if (this.protocolHandler != null && this.service != null) {
            this.protocolHandler.setUtilityExecutor(this.service.getServer().getUtilityExecutor());
        }
        try {
            this.protocolHandler.start();
        }
        catch (Exception e) {
            throw new LifecycleException(sm.getString("coyoteConnector.protocolHandlerStartFailed"), e);
        }
    }

    @Override
    protected void stopInternal() throws LifecycleException {
        this.setState(LifecycleState.STOPPING);
        try {
            if (this.protocolHandler != null) {
                this.protocolHandler.stop();
            }
        }
        catch (Exception e) {
            throw new LifecycleException(sm.getString("coyoteConnector.protocolHandlerStopFailed"), e);
        }
        if (this.protocolHandler != null) {
            this.protocolHandler.setUtilityExecutor(null);
        }
    }

    @Override
    protected void destroyInternal() throws LifecycleException {
        try {
            if (this.protocolHandler != null) {
                this.protocolHandler.destroy();
            }
        }
        catch (Exception e) {
            throw new LifecycleException(sm.getString("coyoteConnector.protocolHandlerDestroyFailed"), e);
        }
        if (this.getService() != null) {
            this.getService().removeConnector(this);
        }
        super.destroyInternal();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Connector[");
        String name = (String)this.getProperty("name");
        if (name == null) {
            String id;
            sb.append(this.getProtocol());
            sb.append('-');
            String string = id = this.protocolHandler != null ? this.protocolHandler.getId() : null;
            if (id != null) {
                sb.append(id);
            } else {
                int port = this.getPortWithOffset();
                if (port > 0) {
                    sb.append(port);
                } else {
                    sb.append("auto-");
                    sb.append(this.getProperty("nameIndex"));
                }
            }
        } else {
            sb.append(name);
        }
        sb.append(']');
        return sb.toString();
    }

    @Override
    protected String getDomainInternal() {
        Service s = this.getService();
        if (s == null) {
            return null;
        }
        return this.service.getDomain();
    }

    @Override
    protected String getObjectNameKeyProperties() {
        return this.createObjectNameKeyProperties("Connector");
    }
}

