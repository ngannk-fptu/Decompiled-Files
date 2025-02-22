/*
 * Decompiled with CFR 0.152.
 */
package org.apache.coyote;

import javax.management.ObjectName;
import org.apache.coyote.ActionCode;
import org.apache.coyote.Request;
import org.apache.coyote.RequestGroupInfo;

public class RequestInfo {
    private RequestGroupInfo global = null;
    private final Request req;
    private int stage = 0;
    private String workerThreadName;
    private ObjectName rpName;
    private long bytesSent;
    private long bytesReceived;
    private long processingTime;
    private long maxTime;
    private String maxRequestUri;
    private int requestCount;
    private int errorCount;
    private long lastRequestProcessingTime = 0L;

    public RequestInfo(Request req) {
        this.req = req;
    }

    public RequestGroupInfo getGlobalProcessor() {
        return this.global;
    }

    public void setGlobalProcessor(RequestGroupInfo global) {
        if (global != null) {
            this.global = global;
            global.addRequestProcessor(this);
        } else if (this.global != null) {
            this.global.removeRequestProcessor(this);
            this.global = null;
        }
    }

    public String getMethod() {
        return this.req.method().toString();
    }

    public String getCurrentUri() {
        return this.req.requestURI().toString();
    }

    public String getCurrentQueryString() {
        return this.req.queryString().toString();
    }

    public String getProtocol() {
        return this.req.protocol().toString();
    }

    public String getVirtualHost() {
        return this.req.serverName().toString();
    }

    public int getServerPort() {
        return this.req.getServerPort();
    }

    public String getRemoteAddr() {
        this.req.action(ActionCode.REQ_HOST_ADDR_ATTRIBUTE, null);
        return this.req.remoteAddr().toString();
    }

    public String getPeerAddr() {
        this.req.action(ActionCode.REQ_PEER_ADDR_ATTRIBUTE, null);
        return this.req.peerAddr().toString();
    }

    public String getRemoteAddrForwarded() {
        String remoteAddrProxy = (String)this.req.getAttribute("org.apache.tomcat.remoteAddr");
        if (remoteAddrProxy == null) {
            return this.getRemoteAddr();
        }
        return remoteAddrProxy;
    }

    public int getContentLength() {
        return this.req.getContentLength();
    }

    public long getRequestBytesReceived() {
        return this.req.getBytesRead();
    }

    public long getRequestBytesSent() {
        return this.req.getResponse().getContentWritten();
    }

    public long getRequestProcessingTime() {
        long startTime = this.req.getStartTime();
        if (this.getStage() == 7 || startTime < 0L) {
            return 0L;
        }
        return System.currentTimeMillis() - startTime;
    }

    void updateCounters() {
        long time;
        this.bytesReceived += this.req.getBytesRead();
        this.bytesSent += this.req.getResponse().getContentWritten();
        ++this.requestCount;
        if (this.req.getResponse().getStatus() >= 400) {
            ++this.errorCount;
        }
        long t0 = this.req.getStartTime();
        long t1 = System.currentTimeMillis();
        this.lastRequestProcessingTime = time = t1 - t0;
        this.processingTime += time;
        if (this.maxTime < time) {
            this.maxTime = time;
            this.maxRequestUri = this.req.requestURI().toString();
        }
    }

    public int getStage() {
        return this.stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public long getBytesSent() {
        return this.bytesSent;
    }

    public void setBytesSent(long bytesSent) {
        this.bytesSent = bytesSent;
    }

    public long getBytesReceived() {
        return this.bytesReceived;
    }

    public void setBytesReceived(long bytesReceived) {
        this.bytesReceived = bytesReceived;
    }

    public long getProcessingTime() {
        return this.processingTime;
    }

    public void setProcessingTime(long processingTime) {
        this.processingTime = processingTime;
    }

    public long getMaxTime() {
        return this.maxTime;
    }

    public void setMaxTime(long maxTime) {
        this.maxTime = maxTime;
    }

    public String getMaxRequestUri() {
        return this.maxRequestUri;
    }

    public void setMaxRequestUri(String maxRequestUri) {
        this.maxRequestUri = maxRequestUri;
    }

    public int getRequestCount() {
        return this.requestCount;
    }

    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }

    public int getErrorCount() {
        return this.errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public String getWorkerThreadName() {
        return this.workerThreadName;
    }

    public ObjectName getRpName() {
        return this.rpName;
    }

    public long getLastRequestProcessingTime() {
        return this.lastRequestProcessingTime;
    }

    public void setWorkerThreadName(String workerThreadName) {
        this.workerThreadName = workerThreadName;
    }

    public void setRpName(ObjectName rpName) {
        this.rpName = rpName;
    }

    public void setLastRequestProcessingTime(long lastRequestProcessingTime) {
        this.lastRequestProcessingTime = lastRequestProcessingTime;
    }
}

