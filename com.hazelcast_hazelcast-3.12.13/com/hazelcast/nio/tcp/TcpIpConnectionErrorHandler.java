/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.tcp;

import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.IOService;
import com.hazelcast.nio.tcp.TcpIpEndpointManager;

public class TcpIpConnectionErrorHandler {
    private final ILogger logger;
    private final IOService ioService;
    private final Address endPoint;
    private final long minInterval;
    private final int maxFaults;
    private int faults;
    private long lastFaultTime;

    TcpIpConnectionErrorHandler(TcpIpEndpointManager endpointManager, Address endPoint) {
        this.endPoint = endPoint;
        this.ioService = endpointManager.getNetworkingService().getIoService();
        this.minInterval = this.ioService.getConnectionMonitorInterval();
        this.maxFaults = this.ioService.getConnectionMonitorMaxFaults();
        this.logger = this.ioService.getLoggingService().getLogger(this.getClass());
    }

    public Address getEndPoint() {
        return this.endPoint;
    }

    public synchronized void onError(Throwable t) {
        String errorMessage = "An error occurred on connection to " + this.endPoint + this.getCauseDescription(t);
        this.logger.finest(errorMessage);
        long now = System.currentTimeMillis();
        long last = this.lastFaultTime;
        if (now - last > this.minInterval) {
            if (this.faults++ >= this.maxFaults) {
                String removeEndpointMessage = "Removing connection to endpoint " + this.endPoint + this.getCauseDescription(t);
                this.logger.warning(removeEndpointMessage);
                this.ioService.removeEndpoint(this.endPoint);
            }
            this.lastFaultTime = now;
        }
    }

    public synchronized void reset() {
        String resetMessage = "Resetting connection monitor for endpoint " + this.endPoint;
        this.logger.finest(resetMessage);
        this.faults = 0;
        this.lastFaultTime = 0L;
    }

    private String getCauseDescription(Throwable t) {
        StringBuilder s = new StringBuilder(" Cause => ");
        if (t != null) {
            s.append(t.getClass().getName()).append(" {").append(t.getMessage()).append("}");
        } else {
            s.append("Unknown");
        }
        return s.append(", Error-Count: ").append(this.faults + 1).toString();
    }
}

