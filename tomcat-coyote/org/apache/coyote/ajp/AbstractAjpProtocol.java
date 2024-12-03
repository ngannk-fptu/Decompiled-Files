/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.coyote.ajp;

import java.net.InetAddress;
import java.util.regex.Pattern;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.Processor;
import org.apache.coyote.UpgradeProtocol;
import org.apache.coyote.UpgradeToken;
import org.apache.coyote.ajp.AjpProcessor;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;

public abstract class AbstractAjpProtocol<S>
extends AbstractProtocol<S> {
    protected static final StringManager sm = StringManager.getManager(AbstractAjpProtocol.class);
    private boolean ajpFlush = true;
    private boolean tomcatAuthentication = true;
    private boolean tomcatAuthorization = false;
    private String secret = null;
    private boolean secretRequired = true;
    private Pattern allowedRequestAttributesPattern;
    private int packetSize = 8192;

    public AbstractAjpProtocol(AbstractEndpoint<S, ?> endpoint) {
        super(endpoint);
        this.setConnectionTimeout(-1);
        this.getEndpoint().setUseSendfile(false);
        this.getEndpoint().setAddress(InetAddress.getLoopbackAddress());
    }

    @Override
    protected String getProtocolName() {
        return "Ajp";
    }

    @Override
    protected AbstractEndpoint<S, ?> getEndpoint() {
        return super.getEndpoint();
    }

    @Override
    protected UpgradeProtocol getNegotiatedProtocol(String name) {
        return null;
    }

    @Override
    protected UpgradeProtocol getUpgradeProtocol(String name) {
        return null;
    }

    public boolean getAjpFlush() {
        return this.ajpFlush;
    }

    public void setAjpFlush(boolean ajpFlush) {
        this.ajpFlush = ajpFlush;
    }

    public boolean getTomcatAuthentication() {
        return this.tomcatAuthentication;
    }

    public void setTomcatAuthentication(boolean tomcatAuthentication) {
        this.tomcatAuthentication = tomcatAuthentication;
    }

    public boolean getTomcatAuthorization() {
        return this.tomcatAuthorization;
    }

    public void setTomcatAuthorization(boolean tomcatAuthorization) {
        this.tomcatAuthorization = tomcatAuthorization;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    protected String getSecret() {
        return this.secret;
    }

    @Deprecated
    public void setRequiredSecret(String requiredSecret) {
        this.setSecret(requiredSecret);
    }

    @Deprecated
    protected String getRequiredSecret() {
        return this.getSecret();
    }

    public void setSecretRequired(boolean secretRequired) {
        this.secretRequired = secretRequired;
    }

    public boolean getSecretRequired() {
        return this.secretRequired;
    }

    public void setAllowedRequestAttributesPattern(String allowedRequestAttributesPattern) {
        this.allowedRequestAttributesPattern = Pattern.compile(allowedRequestAttributesPattern);
    }

    public String getAllowedRequestAttributesPattern() {
        return this.allowedRequestAttributesPattern.pattern();
    }

    protected Pattern getAllowedRequestAttributesPatternInternal() {
        return this.allowedRequestAttributesPattern;
    }

    public int getPacketSize() {
        return this.packetSize;
    }

    public void setPacketSize(int packetSize) {
        this.packetSize = Math.max(packetSize, 8192);
    }

    @Override
    public int getDesiredBufferSize() {
        return this.getPacketSize() - 8;
    }

    @Override
    public void addSslHostConfig(SSLHostConfig sslHostConfig) {
        this.getLog().warn((Object)sm.getString("ajpprotocol.noSSL", new Object[]{sslHostConfig.getHostName()}));
    }

    @Override
    public void addSslHostConfig(SSLHostConfig sslHostConfig, boolean replace) {
        this.getLog().warn((Object)sm.getString("ajpprotocol.noSSL", new Object[]{sslHostConfig.getHostName()}));
    }

    @Override
    public SSLHostConfig[] findSslHostConfigs() {
        return new SSLHostConfig[0];
    }

    @Override
    public void addUpgradeProtocol(UpgradeProtocol upgradeProtocol) {
        this.getLog().warn((Object)sm.getString("ajpprotocol.noUpgrade", new Object[]{upgradeProtocol.getClass().getName()}));
    }

    @Override
    public UpgradeProtocol[] findUpgradeProtocols() {
        return new UpgradeProtocol[0];
    }

    @Override
    protected Processor createProcessor() {
        AjpProcessor processor = new AjpProcessor(this, this.getAdapter());
        return processor;
    }

    @Override
    protected Processor createUpgradeProcessor(SocketWrapperBase<?> socket, UpgradeToken upgradeToken) {
        throw new IllegalStateException(sm.getString("ajpprotocol.noUpgradeHandler", new Object[]{upgradeToken.getHttpUpgradeHandler().getClass().getName()}));
    }

    @Override
    public void start() throws Exception {
        String secret;
        if (this.getSecretRequired() && ((secret = this.getSecret()) == null || secret.length() == 0)) {
            throw new IllegalArgumentException(sm.getString("ajpprotocol.noSecret"));
        }
        super.start();
    }
}

