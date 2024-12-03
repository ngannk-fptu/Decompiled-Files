/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpUpgradeHandler
 *  org.apache.tomcat.util.buf.StringUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.servlet.http.HttpUpgradeHandler;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.CompressionConfig;
import org.apache.coyote.ContinueResponseTiming;
import org.apache.coyote.Processor;
import org.apache.coyote.Request;
import org.apache.coyote.Response;
import org.apache.coyote.UpgradeProtocol;
import org.apache.coyote.UpgradeToken;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler;
import org.apache.coyote.http11.upgrade.UpgradeGroupInfo;
import org.apache.coyote.http11.upgrade.UpgradeProcessorExternal;
import org.apache.coyote.http11.upgrade.UpgradeProcessorInternal;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.modeler.Util;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;

public abstract class AbstractHttp11Protocol<S>
extends AbstractProtocol<S> {
    protected static final StringManager sm = StringManager.getManager(AbstractHttp11Protocol.class);
    private final CompressionConfig compressionConfig = new CompressionConfig();
    private ContinueResponseTiming continueResponseTiming = ContinueResponseTiming.IMMEDIATELY;
    private boolean useKeepAliveResponseHeader = true;
    private String relaxedPathChars = null;
    private String relaxedQueryChars = null;
    private boolean allowHostHeaderMismatch = false;
    private boolean rejectIllegalHeader = true;
    private int maxSavePostSize = 4096;
    private int maxHttpHeaderSize = 8192;
    private int maxHttpRequestHeaderSize = -1;
    private int maxHttpResponseHeaderSize = -1;
    private int connectionUploadTimeout = 300000;
    private boolean disableUploadTimeout = true;
    private Pattern restrictedUserAgents = null;
    private String server;
    private boolean serverRemoveAppProvidedValues = false;
    private int maxTrailerSize = 8192;
    private int maxExtensionSize = 8192;
    private int maxSwallowSize = 0x200000;
    private boolean secure;
    private Set<String> allowedTrailerHeaders = ConcurrentHashMap.newKeySet();
    private final List<UpgradeProtocol> upgradeProtocols = new ArrayList<UpgradeProtocol>();
    private final Map<String, UpgradeProtocol> httpUpgradeProtocols = new HashMap<String, UpgradeProtocol>();
    private final Map<String, UpgradeProtocol> negotiatedProtocols = new HashMap<String, UpgradeProtocol>();
    private final Map<String, UpgradeGroupInfo> upgradeProtocolGroupInfos = new ConcurrentHashMap<String, UpgradeGroupInfo>();
    private SSLHostConfig defaultSSLHostConfig = null;

    public AbstractHttp11Protocol(AbstractEndpoint<S, ?> endpoint) {
        super(endpoint);
        this.setConnectionTimeout(60000);
    }

    @Override
    public void init() throws Exception {
        for (UpgradeProtocol upgradeProtocol : this.upgradeProtocols) {
            this.configureUpgradeProtocol(upgradeProtocol);
        }
        super.init();
        for (UpgradeProtocol upgradeProtocol : this.upgradeProtocols) {
            upgradeProtocol.setHttp11Protocol(this);
        }
    }

    @Override
    public void destroy() throws Exception {
        ObjectName rgOname = this.getGlobalRequestProcessorMBeanName();
        if (rgOname != null) {
            Registry registry = Registry.getRegistry(null, null);
            ObjectName query = new ObjectName(rgOname.getCanonicalName() + ",Upgrade=*");
            Set<ObjectInstance> upgrades = registry.getMBeanServer().queryMBeans(query, null);
            for (ObjectInstance upgrade : upgrades) {
                registry.unregisterComponent(upgrade.getObjectName());
            }
        }
        super.destroy();
    }

    @Override
    protected String getProtocolName() {
        return "Http";
    }

    @Override
    protected AbstractEndpoint<S, ?> getEndpoint() {
        return super.getEndpoint();
    }

    public String getContinueResponseTiming() {
        return this.continueResponseTiming.toString();
    }

    public void setContinueResponseTiming(String continueResponseTiming) {
        this.continueResponseTiming = ContinueResponseTiming.fromString(continueResponseTiming);
    }

    public ContinueResponseTiming getContinueResponseTimingInternal() {
        return this.continueResponseTiming;
    }

    public boolean getUseKeepAliveResponseHeader() {
        return this.useKeepAliveResponseHeader;
    }

    public void setUseKeepAliveResponseHeader(boolean useKeepAliveResponseHeader) {
        this.useKeepAliveResponseHeader = useKeepAliveResponseHeader;
    }

    public String getRelaxedPathChars() {
        return this.relaxedPathChars;
    }

    public void setRelaxedPathChars(String relaxedPathChars) {
        this.relaxedPathChars = relaxedPathChars;
    }

    public String getRelaxedQueryChars() {
        return this.relaxedQueryChars;
    }

    public void setRelaxedQueryChars(String relaxedQueryChars) {
        this.relaxedQueryChars = relaxedQueryChars;
    }

    @Deprecated
    public boolean getAllowHostHeaderMismatch() {
        return this.allowHostHeaderMismatch;
    }

    @Deprecated
    public void setAllowHostHeaderMismatch(boolean allowHostHeaderMismatch) {
        this.allowHostHeaderMismatch = allowHostHeaderMismatch;
    }

    @Deprecated
    public boolean getRejectIllegalHeader() {
        return this.rejectIllegalHeader;
    }

    @Deprecated
    public void setRejectIllegalHeader(boolean rejectIllegalHeader) {
        this.rejectIllegalHeader = rejectIllegalHeader;
    }

    @Deprecated
    public boolean getRejectIllegalHeaderName() {
        return this.rejectIllegalHeader;
    }

    @Deprecated
    public void setRejectIllegalHeaderName(boolean rejectIllegalHeaderName) {
        this.rejectIllegalHeader = rejectIllegalHeaderName;
    }

    public int getMaxSavePostSize() {
        return this.maxSavePostSize;
    }

    public void setMaxSavePostSize(int maxSavePostSize) {
        this.maxSavePostSize = maxSavePostSize;
    }

    public int getMaxHttpHeaderSize() {
        return this.maxHttpHeaderSize;
    }

    public void setMaxHttpHeaderSize(int valueI) {
        this.maxHttpHeaderSize = valueI;
    }

    public int getMaxHttpRequestHeaderSize() {
        return this.maxHttpRequestHeaderSize == -1 ? this.getMaxHttpHeaderSize() : this.maxHttpRequestHeaderSize;
    }

    public void setMaxHttpRequestHeaderSize(int valueI) {
        this.maxHttpRequestHeaderSize = valueI;
    }

    public int getMaxHttpResponseHeaderSize() {
        return this.maxHttpResponseHeaderSize == -1 ? this.getMaxHttpHeaderSize() : this.maxHttpResponseHeaderSize;
    }

    public void setMaxHttpResponseHeaderSize(int valueI) {
        this.maxHttpResponseHeaderSize = valueI;
    }

    public int getConnectionUploadTimeout() {
        return this.connectionUploadTimeout;
    }

    public void setConnectionUploadTimeout(int timeout) {
        this.connectionUploadTimeout = timeout;
    }

    public boolean getDisableUploadTimeout() {
        return this.disableUploadTimeout;
    }

    public void setDisableUploadTimeout(boolean isDisabled) {
        this.disableUploadTimeout = isDisabled;
    }

    public void setCompression(String compression) {
        this.compressionConfig.setCompression(compression);
    }

    public String getCompression() {
        return this.compressionConfig.getCompression();
    }

    protected int getCompressionLevel() {
        return this.compressionConfig.getCompressionLevel();
    }

    public String getNoCompressionUserAgents() {
        return this.compressionConfig.getNoCompressionUserAgents();
    }

    protected Pattern getNoCompressionUserAgentsPattern() {
        return this.compressionConfig.getNoCompressionUserAgentsPattern();
    }

    public void setNoCompressionUserAgents(String noCompressionUserAgents) {
        this.compressionConfig.setNoCompressionUserAgents(noCompressionUserAgents);
    }

    public String getCompressibleMimeType() {
        return this.compressionConfig.getCompressibleMimeType();
    }

    public void setCompressibleMimeType(String valueS) {
        this.compressionConfig.setCompressibleMimeType(valueS);
    }

    public String[] getCompressibleMimeTypes() {
        return this.compressionConfig.getCompressibleMimeTypes();
    }

    public int getCompressionMinSize() {
        return this.compressionConfig.getCompressionMinSize();
    }

    public void setCompressionMinSize(int compressionMinSize) {
        this.compressionConfig.setCompressionMinSize(compressionMinSize);
    }

    @Deprecated
    public boolean getNoCompressionStrongETag() {
        return this.compressionConfig.getNoCompressionStrongETag();
    }

    @Deprecated
    public void setNoCompressionStrongETag(boolean noCompressionStrongETag) {
        this.compressionConfig.setNoCompressionStrongETag(noCompressionStrongETag);
    }

    public boolean useCompression(Request request, Response response) {
        return this.compressionConfig.useCompression(request, response);
    }

    public String getRestrictedUserAgents() {
        if (this.restrictedUserAgents == null) {
            return null;
        }
        return this.restrictedUserAgents.toString();
    }

    protected Pattern getRestrictedUserAgentsPattern() {
        return this.restrictedUserAgents;
    }

    public void setRestrictedUserAgents(String restrictedUserAgents) {
        this.restrictedUserAgents = restrictedUserAgents == null || restrictedUserAgents.length() == 0 ? null : Pattern.compile(restrictedUserAgents);
    }

    public String getServer() {
        return this.server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public boolean getServerRemoveAppProvidedValues() {
        return this.serverRemoveAppProvidedValues;
    }

    public void setServerRemoveAppProvidedValues(boolean serverRemoveAppProvidedValues) {
        this.serverRemoveAppProvidedValues = serverRemoveAppProvidedValues;
    }

    public int getMaxTrailerSize() {
        return this.maxTrailerSize;
    }

    public void setMaxTrailerSize(int maxTrailerSize) {
        this.maxTrailerSize = maxTrailerSize;
    }

    public int getMaxExtensionSize() {
        return this.maxExtensionSize;
    }

    public void setMaxExtensionSize(int maxExtensionSize) {
        this.maxExtensionSize = maxExtensionSize;
    }

    public int getMaxSwallowSize() {
        return this.maxSwallowSize;
    }

    public void setMaxSwallowSize(int maxSwallowSize) {
        this.maxSwallowSize = maxSwallowSize;
    }

    public boolean getSecure() {
        return this.secure;
    }

    public void setSecure(boolean b) {
        this.secure = b;
    }

    public void setAllowedTrailerHeaders(String commaSeparatedHeaders) {
        HashSet<String> toRemove = new HashSet<String>(this.allowedTrailerHeaders);
        if (commaSeparatedHeaders != null) {
            String[] headers;
            for (String header : headers = commaSeparatedHeaders.split(",")) {
                String trimmedHeader = header.trim().toLowerCase(Locale.ENGLISH);
                if (toRemove.contains(trimmedHeader)) {
                    toRemove.remove(trimmedHeader);
                    continue;
                }
                this.allowedTrailerHeaders.add(trimmedHeader);
            }
            this.allowedTrailerHeaders.removeAll(toRemove);
        }
    }

    protected Set<String> getAllowedTrailerHeadersInternal() {
        return this.allowedTrailerHeaders;
    }

    public String getAllowedTrailerHeaders() {
        ArrayList<String> copy = new ArrayList<String>(this.allowedTrailerHeaders);
        return StringUtils.join(copy);
    }

    public void addAllowedTrailerHeader(String header) {
        if (header != null) {
            this.allowedTrailerHeaders.add(header.trim().toLowerCase(Locale.ENGLISH));
        }
    }

    public void removeAllowedTrailerHeader(String header) {
        if (header != null) {
            this.allowedTrailerHeaders.remove(header.trim().toLowerCase(Locale.ENGLISH));
        }
    }

    @Override
    public void addUpgradeProtocol(UpgradeProtocol upgradeProtocol) {
        this.upgradeProtocols.add(upgradeProtocol);
    }

    @Override
    public UpgradeProtocol[] findUpgradeProtocols() {
        return this.upgradeProtocols.toArray(new UpgradeProtocol[0]);
    }

    private void configureUpgradeProtocol(UpgradeProtocol upgradeProtocol) {
        String alpnName;
        String httpUpgradeName = upgradeProtocol.getHttpUpgradeName(this.getEndpoint().isSSLEnabled());
        boolean httpUpgradeConfigured = false;
        if (httpUpgradeName != null && httpUpgradeName.length() > 0) {
            this.httpUpgradeProtocols.put(httpUpgradeName, upgradeProtocol);
            httpUpgradeConfigured = true;
            this.getLog().info((Object)sm.getString("abstractHttp11Protocol.httpUpgradeConfigured", new Object[]{this.getName(), httpUpgradeName}));
        }
        if ((alpnName = upgradeProtocol.getAlpnName()) != null && alpnName.length() > 0) {
            if (this.getEndpoint().isAlpnSupported()) {
                this.negotiatedProtocols.put(alpnName, upgradeProtocol);
                this.getEndpoint().addNegotiatedProtocol(alpnName);
                this.getLog().info((Object)sm.getString("abstractHttp11Protocol.alpnConfigured", new Object[]{this.getName(), alpnName}));
            } else if (!httpUpgradeConfigured) {
                this.getLog().error((Object)sm.getString("abstractHttp11Protocol.alpnWithNoAlpn", new Object[]{upgradeProtocol.getClass().getName(), alpnName, this.getName()}));
            }
        }
    }

    @Override
    public UpgradeProtocol getNegotiatedProtocol(String negotiatedName) {
        return this.negotiatedProtocols.get(negotiatedName);
    }

    @Override
    public UpgradeProtocol getUpgradeProtocol(String upgradedName) {
        return this.httpUpgradeProtocols.get(upgradedName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public UpgradeGroupInfo getUpgradeGroupInfo(String upgradeProtocol) {
        if (upgradeProtocol == null) {
            return null;
        }
        UpgradeGroupInfo result = this.upgradeProtocolGroupInfos.get(upgradeProtocol);
        if (result == null) {
            Map<String, UpgradeGroupInfo> map = this.upgradeProtocolGroupInfos;
            synchronized (map) {
                result = this.upgradeProtocolGroupInfos.get(upgradeProtocol);
                if (result == null) {
                    result = new UpgradeGroupInfo();
                    this.upgradeProtocolGroupInfos.put(upgradeProtocol, result);
                    ObjectName oname = this.getONameForUpgrade(upgradeProtocol);
                    if (oname != null) {
                        try {
                            Registry.getRegistry(null, null).registerComponent((Object)result, oname, null);
                        }
                        catch (Exception e) {
                            this.getLog().warn((Object)sm.getString("abstractHttp11Protocol.upgradeJmxRegistrationFail"), (Throwable)e);
                            result = null;
                        }
                    }
                }
            }
        }
        return result;
    }

    public ObjectName getONameForUpgrade(String upgradeProtocol) {
        ObjectName oname = null;
        ObjectName parentRgOname = this.getGlobalRequestProcessorMBeanName();
        if (parentRgOname != null) {
            StringBuilder name = new StringBuilder(parentRgOname.getCanonicalName());
            name.append(",Upgrade=");
            if (Util.objectNameValueNeedsQuote(upgradeProtocol)) {
                name.append(ObjectName.quote(upgradeProtocol));
            } else {
                name.append(upgradeProtocol);
            }
            try {
                oname = new ObjectName(name.toString());
            }
            catch (Exception e) {
                this.getLog().warn((Object)sm.getString("abstractHttp11Protocol.upgradeJmxNameFail"), (Throwable)e);
            }
        }
        return oname;
    }

    public boolean isSSLEnabled() {
        return this.getEndpoint().isSSLEnabled();
    }

    public void setSSLEnabled(boolean SSLEnabled) {
        this.getEndpoint().setSSLEnabled(SSLEnabled);
    }

    public boolean getUseSendfile() {
        return this.getEndpoint().getUseSendfile();
    }

    public void setUseSendfile(boolean useSendfile) {
        this.getEndpoint().setUseSendfile(useSendfile);
    }

    public int getMaxKeepAliveRequests() {
        return this.getEndpoint().getMaxKeepAliveRequests();
    }

    public void setMaxKeepAliveRequests(int mkar) {
        this.getEndpoint().setMaxKeepAliveRequests(mkar);
    }

    public String getDefaultSSLHostConfigName() {
        return this.getEndpoint().getDefaultSSLHostConfigName();
    }

    public void setDefaultSSLHostConfigName(String defaultSSLHostConfigName) {
        this.getEndpoint().setDefaultSSLHostConfigName(defaultSSLHostConfigName);
        if (this.defaultSSLHostConfig != null) {
            this.defaultSSLHostConfig.setHostName(defaultSSLHostConfigName);
        }
    }

    @Override
    public void addSslHostConfig(SSLHostConfig sslHostConfig) {
        this.getEndpoint().addSslHostConfig(sslHostConfig);
    }

    @Override
    public void addSslHostConfig(SSLHostConfig sslHostConfig, boolean replace) {
        this.getEndpoint().addSslHostConfig(sslHostConfig, replace);
    }

    @Override
    public SSLHostConfig[] findSslHostConfigs() {
        return this.getEndpoint().findSslHostConfigs();
    }

    public void reloadSslHostConfigs() {
        this.getEndpoint().reloadSslHostConfigs();
    }

    public void reloadSslHostConfig(String hostName) {
        this.getEndpoint().reloadSslHostConfig(hostName);
    }

    private void registerDefaultSSLHostConfig() {
        if (this.defaultSSLHostConfig == null) {
            for (SSLHostConfig sslHostConfig : this.findSslHostConfigs()) {
                if (!this.getDefaultSSLHostConfigName().equals(sslHostConfig.getHostName())) continue;
                this.defaultSSLHostConfig = sslHostConfig;
                break;
            }
            if (this.defaultSSLHostConfig == null) {
                this.defaultSSLHostConfig = new SSLHostConfig();
                this.defaultSSLHostConfig.setHostName(this.getDefaultSSLHostConfigName());
                this.getEndpoint().addSslHostConfig(this.defaultSSLHostConfig);
            }
        }
    }

    public String getSslEnabledProtocols() {
        this.registerDefaultSSLHostConfig();
        return StringUtils.join((String[])this.defaultSSLHostConfig.getEnabledProtocols());
    }

    public void setSslEnabledProtocols(String enabledProtocols) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setProtocols(enabledProtocols);
    }

    public String getSSLProtocol() {
        this.registerDefaultSSLHostConfig();
        return StringUtils.join((String[])this.defaultSSLHostConfig.getEnabledProtocols());
    }

    public void setSSLProtocol(String sslProtocol) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setProtocols(sslProtocol);
    }

    public String getKeystoreFile() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateKeystoreFile();
    }

    public void setKeystoreFile(String keystoreFile) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateKeystoreFile(keystoreFile);
    }

    public String getSSLCertificateChainFile() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateChainFile();
    }

    public void setSSLCertificateChainFile(String certificateChainFile) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateChainFile(certificateChainFile);
    }

    public String getSSLCertificateFile() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateFile();
    }

    public void setSSLCertificateFile(String certificateFile) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateFile(certificateFile);
    }

    public String getSSLCertificateKeyFile() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateKeyFile();
    }

    public void setSSLCertificateKeyFile(String certificateKeyFile) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateKeyFile(certificateKeyFile);
    }

    public String getAlgorithm() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getKeyManagerAlgorithm();
    }

    public void setAlgorithm(String keyManagerAlgorithm) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setKeyManagerAlgorithm(keyManagerAlgorithm);
    }

    public String getClientAuth() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateVerificationAsString();
    }

    public void setClientAuth(String certificateVerification) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateVerification(certificateVerification);
    }

    public String getSSLVerifyClient() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateVerificationAsString();
    }

    public void setSSLVerifyClient(String certificateVerification) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateVerification(certificateVerification);
    }

    public int getTrustMaxCertLength() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateVerificationDepth();
    }

    public void setTrustMaxCertLength(int certificateVerificationDepth) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateVerificationDepth(certificateVerificationDepth);
    }

    public int getSSLVerifyDepth() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateVerificationDepth();
    }

    public void setSSLVerifyDepth(int certificateVerificationDepth) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateVerificationDepth(certificateVerificationDepth);
    }

    public boolean getUseServerCipherSuitesOrder() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getHonorCipherOrder();
    }

    public void setUseServerCipherSuitesOrder(boolean honorCipherOrder) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setHonorCipherOrder(honorCipherOrder);
    }

    public boolean getSSLHonorCipherOrder() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getHonorCipherOrder();
    }

    public void setSSLHonorCipherOrder(boolean honorCipherOrder) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setHonorCipherOrder(honorCipherOrder);
    }

    public String getCiphers() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCiphers();
    }

    public void setCiphers(String ciphers) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCiphers(ciphers);
    }

    public String getSSLCipherSuite() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCiphers();
    }

    public void setSSLCipherSuite(String ciphers) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCiphers(ciphers);
    }

    public String getKeystorePass() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateKeystorePassword();
    }

    public void setKeystorePass(String certificateKeystorePassword) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateKeystorePassword(certificateKeystorePassword);
    }

    public String getKeystorePassFile() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateKeystorePasswordFile();
    }

    public void setKeystorePassFile(String certificateKeystorePasswordFile) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateKeystorePasswordFile(certificateKeystorePasswordFile);
    }

    public String getKeyPass() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateKeyPassword();
    }

    public void setKeyPass(String certificateKeyPassword) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateKeyPassword(certificateKeyPassword);
    }

    public String getKeyPassFile() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateKeyPasswordFile();
    }

    public void setKeyPassFile(String certificateKeyPasswordFile) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateKeyPasswordFile(certificateKeyPasswordFile);
    }

    public String getSSLPassword() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateKeyPassword();
    }

    public void setSSLPassword(String certificateKeyPassword) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateKeyPassword(certificateKeyPassword);
    }

    public String getSSLPasswordFile() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateKeyPasswordFile();
    }

    public void setSSLPasswordFile(String certificateKeyPasswordFile) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateKeyPasswordFile(certificateKeyPasswordFile);
    }

    public String getCrlFile() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateRevocationListFile();
    }

    public void setCrlFile(String certificateRevocationListFile) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateRevocationListFile(certificateRevocationListFile);
    }

    public String getSSLCARevocationFile() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateRevocationListFile();
    }

    public void setSSLCARevocationFile(String certificateRevocationListFile) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateRevocationListFile(certificateRevocationListFile);
    }

    public String getSSLCARevocationPath() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateRevocationListPath();
    }

    public void setSSLCARevocationPath(String certificateRevocationListPath) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateRevocationListPath(certificateRevocationListPath);
    }

    public String getKeystoreType() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateKeystoreType();
    }

    public void setKeystoreType(String certificateKeystoreType) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateKeystoreType(certificateKeystoreType);
    }

    public String getKeystoreProvider() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateKeystoreProvider();
    }

    public void setKeystoreProvider(String certificateKeystoreProvider) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateKeystoreProvider(certificateKeystoreProvider);
    }

    public String getKeyAlias() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateKeyAlias();
    }

    public void setKeyAlias(String certificateKeyAlias) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateKeyAlias(certificateKeyAlias);
    }

    public String getTruststoreAlgorithm() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getTruststoreAlgorithm();
    }

    public void setTruststoreAlgorithm(String truststoreAlgorithm) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setTruststoreAlgorithm(truststoreAlgorithm);
    }

    public String getTruststoreFile() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getTruststoreFile();
    }

    public void setTruststoreFile(String truststoreFile) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setTruststoreFile(truststoreFile);
    }

    public String getTruststorePass() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getTruststorePassword();
    }

    public void setTruststorePass(String truststorePassword) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setTruststorePassword(truststorePassword);
    }

    public String getTruststoreType() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getTruststoreType();
    }

    public void setTruststoreType(String truststoreType) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setTruststoreType(truststoreType);
    }

    public String getTruststoreProvider() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getTruststoreProvider();
    }

    public void setTruststoreProvider(String truststoreProvider) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setTruststoreProvider(truststoreProvider);
    }

    public String getSslProtocol() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getSslProtocol();
    }

    public void setSslProtocol(String sslProtocol) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setSslProtocol(sslProtocol);
    }

    public int getSessionCacheSize() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getSessionCacheSize();
    }

    public void setSessionCacheSize(int sessionCacheSize) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setSessionCacheSize(sessionCacheSize);
    }

    public int getSessionTimeout() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getSessionTimeout();
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setSessionTimeout(sessionTimeout);
    }

    public String getSSLCACertificatePath() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCaCertificatePath();
    }

    public void setSSLCACertificatePath(String caCertificatePath) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCaCertificatePath(caCertificatePath);
    }

    public String getSSLCACertificateFile() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCaCertificateFile();
    }

    public void setSSLCACertificateFile(String caCertificateFile) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCaCertificateFile(caCertificateFile);
    }

    public boolean getSSLDisableCompression() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getDisableCompression();
    }

    public void setSSLDisableCompression(boolean disableCompression) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setDisableCompression(disableCompression);
    }

    public boolean getSSLDisableSessionTickets() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getDisableSessionTickets();
    }

    public void setSSLDisableSessionTickets(boolean disableSessionTickets) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setDisableSessionTickets(disableSessionTickets);
    }

    public String getTrustManagerClassName() {
        this.registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getTrustManagerClassName();
    }

    public void setTrustManagerClassName(String trustManagerClassName) {
        this.registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setTrustManagerClassName(trustManagerClassName);
    }

    @Override
    protected Processor createProcessor() {
        Http11Processor processor = new Http11Processor(this, this.adapter);
        return processor;
    }

    @Override
    protected Processor createUpgradeProcessor(SocketWrapperBase<?> socket, UpgradeToken upgradeToken) {
        HttpUpgradeHandler httpUpgradeHandler = upgradeToken.getHttpUpgradeHandler();
        if (httpUpgradeHandler instanceof InternalHttpUpgradeHandler) {
            return new UpgradeProcessorInternal(socket, upgradeToken, this.getUpgradeGroupInfo(upgradeToken.getProtocol()));
        }
        return new UpgradeProcessorExternal(socket, upgradeToken, this.getUpgradeGroupInfo(upgradeToken.getProtocol()));
    }
}

