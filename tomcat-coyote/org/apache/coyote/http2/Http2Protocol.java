/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.buf.StringUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.coyote.http2;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import javax.management.ObjectName;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.Adapter;
import org.apache.coyote.CompressionConfig;
import org.apache.coyote.ContinueResponseTiming;
import org.apache.coyote.Processor;
import org.apache.coyote.Request;
import org.apache.coyote.RequestGroupInfo;
import org.apache.coyote.Response;
import org.apache.coyote.UpgradeProtocol;
import org.apache.coyote.UpgradeToken;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler;
import org.apache.coyote.http11.upgrade.UpgradeProcessorInternal;
import org.apache.coyote.http2.Http2AsyncUpgradeHandler;
import org.apache.coyote.http2.Http2UpgradeHandler;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;

public class Http2Protocol
implements UpgradeProtocol {
    private static final Log log = LogFactory.getLog(Http2Protocol.class);
    private static final StringManager sm = StringManager.getManager(Http2Protocol.class);
    static final long DEFAULT_READ_TIMEOUT = 5000L;
    static final long DEFAULT_WRITE_TIMEOUT = 5000L;
    static final long DEFAULT_KEEP_ALIVE_TIMEOUT = 20000L;
    static final long DEFAULT_STREAM_READ_TIMEOUT = 20000L;
    static final long DEFAULT_STREAM_WRITE_TIMEOUT = 20000L;
    static final long DEFAULT_MAX_CONCURRENT_STREAMS = 100L;
    static final int DEFAULT_MAX_CONCURRENT_STREAM_EXECUTION = 20;
    static final int DEFAULT_OVERHEAD_COUNT_FACTOR = 10;
    static final int DEFAULT_OVERHEAD_RESET_FACTOR = 50;
    static final int DEFAULT_OVERHEAD_REDUCTION_FACTOR = -20;
    static final int DEFAULT_OVERHEAD_CONTINUATION_THRESHOLD = 1024;
    static final int DEFAULT_OVERHEAD_DATA_THRESHOLD = 1024;
    static final int DEFAULT_OVERHEAD_WINDOW_UPDATE_THRESHOLD = 1024;
    private static final String HTTP_UPGRADE_NAME = "h2c";
    private static final String ALPN_NAME = "h2";
    private static final byte[] ALPN_IDENTIFIER = "h2".getBytes(StandardCharsets.UTF_8);
    private long readTimeout = 5000L;
    private long writeTimeout = 5000L;
    private long keepAliveTimeout = 20000L;
    private long streamReadTimeout = 20000L;
    private long streamWriteTimeout = 20000L;
    private long maxConcurrentStreams = 100L;
    private int maxConcurrentStreamExecution = 20;
    private int initialWindowSize = 65535;
    private Set<String> allowedTrailerHeaders = ConcurrentHashMap.newKeySet();
    private int maxHeaderCount = 100;
    private int maxHeaderSize = 8192;
    private int maxTrailerCount = 100;
    private int maxTrailerSize = 8192;
    private int overheadCountFactor = 10;
    private int overheadResetFactor = 50;
    private int overheadContinuationThreshold = 1024;
    private int overheadDataThreshold = 1024;
    private int overheadWindowUpdateThreshold = 1024;
    private boolean initiatePingDisabled = false;
    private boolean useSendfile = true;
    private final CompressionConfig compressionConfig = new CompressionConfig();
    private AbstractHttp11Protocol<?> http11Protocol = null;
    private RequestGroupInfo global = new RequestGroupInfo();

    @Override
    public String getHttpUpgradeName(boolean isSSLEnabled) {
        if (isSSLEnabled) {
            return null;
        }
        return HTTP_UPGRADE_NAME;
    }

    @Override
    public byte[] getAlpnIdentifier() {
        return ALPN_IDENTIFIER;
    }

    @Override
    public String getAlpnName() {
        return ALPN_NAME;
    }

    @Override
    public Processor getProcessor(SocketWrapperBase<?> socketWrapper, Adapter adapter) {
        String upgradeProtocol = this.getUpgradeProtocolName();
        UpgradeProcessorInternal processor = new UpgradeProcessorInternal(socketWrapper, new UpgradeToken(this.getInternalUpgradeHandler(socketWrapper, adapter, null), null, null, upgradeProtocol), null);
        return processor;
    }

    @Override
    public InternalHttpUpgradeHandler getInternalUpgradeHandler(SocketWrapperBase<?> socketWrapper, Adapter adapter, Request coyoteRequest) {
        return socketWrapper.hasAsyncIO() ? new Http2AsyncUpgradeHandler(this, adapter, coyoteRequest) : new Http2UpgradeHandler(this, adapter, coyoteRequest);
    }

    @Override
    public boolean accept(Request request) {
        Enumeration<String> settings = request.getMimeHeaders().values("HTTP2-Settings");
        int count = 0;
        while (settings.hasMoreElements()) {
            ++count;
            settings.nextElement();
        }
        if (count != 1) {
            return false;
        }
        Enumeration<String> connection = request.getMimeHeaders().values("Connection");
        boolean found = false;
        while (connection.hasMoreElements() && !found) {
            found = connection.nextElement().contains("HTTP2-Settings");
        }
        return found;
    }

    public long getReadTimeout() {
        return this.readTimeout;
    }

    public void setReadTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
    }

    public long getWriteTimeout() {
        return this.writeTimeout;
    }

    public void setWriteTimeout(long writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public long getKeepAliveTimeout() {
        return this.keepAliveTimeout;
    }

    public void setKeepAliveTimeout(long keepAliveTimeout) {
        this.keepAliveTimeout = keepAliveTimeout;
    }

    public long getStreamReadTimeout() {
        return this.streamReadTimeout;
    }

    public void setStreamReadTimeout(long streamReadTimeout) {
        this.streamReadTimeout = streamReadTimeout;
    }

    public long getStreamWriteTimeout() {
        return this.streamWriteTimeout;
    }

    public void setStreamWriteTimeout(long streamWriteTimeout) {
        this.streamWriteTimeout = streamWriteTimeout;
    }

    public long getMaxConcurrentStreams() {
        return this.maxConcurrentStreams;
    }

    public void setMaxConcurrentStreams(long maxConcurrentStreams) {
        this.maxConcurrentStreams = maxConcurrentStreams;
    }

    public int getMaxConcurrentStreamExecution() {
        return this.maxConcurrentStreamExecution;
    }

    public void setMaxConcurrentStreamExecution(int maxConcurrentStreamExecution) {
        this.maxConcurrentStreamExecution = maxConcurrentStreamExecution;
    }

    public int getInitialWindowSize() {
        return this.initialWindowSize;
    }

    public void setInitialWindowSize(int initialWindowSize) {
        this.initialWindowSize = initialWindowSize;
    }

    public boolean getUseSendfile() {
        return this.useSendfile;
    }

    public void setUseSendfile(boolean useSendfile) {
        this.useSendfile = useSendfile;
    }

    public void setAllowedTrailerHeaders(String commaSeparatedHeaders) {
        HashSet<String> toRemove = new HashSet<String>();
        toRemove.addAll(this.allowedTrailerHeaders);
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

    public String getAllowedTrailerHeaders() {
        ArrayList<String> copy = new ArrayList<String>(this.allowedTrailerHeaders.size());
        copy.addAll(this.allowedTrailerHeaders);
        return StringUtils.join(copy);
    }

    boolean isTrailerHeaderAllowed(String headerName) {
        return this.allowedTrailerHeaders.contains(headerName);
    }

    public void setMaxHeaderCount(int maxHeaderCount) {
        this.maxHeaderCount = maxHeaderCount;
    }

    public int getMaxHeaderCount() {
        return this.maxHeaderCount;
    }

    public void setMaxHeaderSize(int maxHeaderSize) {
        this.maxHeaderSize = maxHeaderSize;
    }

    public int getMaxHeaderSize() {
        return this.maxHeaderSize;
    }

    public void setMaxTrailerCount(int maxTrailerCount) {
        this.maxTrailerCount = maxTrailerCount;
    }

    public int getMaxTrailerCount() {
        return this.maxTrailerCount;
    }

    public void setMaxTrailerSize(int maxTrailerSize) {
        this.maxTrailerSize = maxTrailerSize;
    }

    public int getMaxTrailerSize() {
        return this.maxTrailerSize;
    }

    public int getOverheadCountFactor() {
        return this.overheadCountFactor;
    }

    public void setOverheadCountFactor(int overheadCountFactor) {
        this.overheadCountFactor = overheadCountFactor;
    }

    public int getOverheadResetFactor() {
        return this.overheadResetFactor;
    }

    public void setOverheadResetFactor(int overheadResetFactor) {
        this.overheadResetFactor = overheadResetFactor < 0 ? 0 : overheadResetFactor;
    }

    public int getOverheadContinuationThreshold() {
        return this.overheadContinuationThreshold;
    }

    public void setOverheadContinuationThreshold(int overheadContinuationThreshold) {
        this.overheadContinuationThreshold = overheadContinuationThreshold;
    }

    public int getOverheadDataThreshold() {
        return this.overheadDataThreshold;
    }

    public void setOverheadDataThreshold(int overheadDataThreshold) {
        this.overheadDataThreshold = overheadDataThreshold;
    }

    public int getOverheadWindowUpdateThreshold() {
        return this.overheadWindowUpdateThreshold;
    }

    public void setOverheadWindowUpdateThreshold(int overheadWindowUpdateThreshold) {
        this.overheadWindowUpdateThreshold = overheadWindowUpdateThreshold;
    }

    public void setInitiatePingDisabled(boolean initiatePingDisabled) {
        this.initiatePingDisabled = initiatePingDisabled;
    }

    public boolean getInitiatePingDisabled() {
        return this.initiatePingDisabled;
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

    public ContinueResponseTiming getContinueResponseTimingInternal() {
        return this.http11Protocol.getContinueResponseTimingInternal();
    }

    public AbstractProtocol<?> getHttp11Protocol() {
        return this.http11Protocol;
    }

    @Override
    public void setHttp11Protocol(AbstractHttp11Protocol<?> http11Protocol) {
        this.http11Protocol = http11Protocol;
        try {
            ObjectName oname = this.http11Protocol.getONameForUpgrade(this.getUpgradeProtocolName());
            if (oname != null) {
                Registry.getRegistry(null, null).registerComponent((Object)this.global, oname, null);
            }
        }
        catch (Exception e) {
            log.warn((Object)sm.getString("http2Protocol.jmxRegistration.fail"), (Throwable)e);
        }
    }

    public String getUpgradeProtocolName() {
        if (this.http11Protocol.isSSLEnabled()) {
            return ALPN_NAME;
        }
        return HTTP_UPGRADE_NAME;
    }

    public RequestGroupInfo getGlobal() {
        return this.global;
    }
}

