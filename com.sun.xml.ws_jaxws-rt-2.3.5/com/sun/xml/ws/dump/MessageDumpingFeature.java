/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceFeature
 *  org.glassfish.gmbal.ManagedAttribute
 *  org.glassfish.gmbal.ManagedData
 */
package com.sun.xml.ws.dump;

import com.sun.xml.ws.api.FeatureConstructor;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import javax.xml.ws.WebServiceFeature;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedData;

@ManagedData
public final class MessageDumpingFeature
extends WebServiceFeature {
    public static final String ID = "com.sun.xml.ws.messagedump.MessageDumpingFeature";
    private static final Level DEFAULT_MSG_LOG_LEVEL = Level.FINE;
    private final Queue<String> messageQueue;
    private final AtomicBoolean messageLoggingStatus;
    private final String messageLoggingRoot;
    private final Level messageLoggingLevel;

    public MessageDumpingFeature() {
        this(null, null, true);
    }

    public MessageDumpingFeature(String msgLogRoot, Level msgLogLevel, boolean storeMessages) {
        this.messageQueue = storeMessages ? new ConcurrentLinkedQueue() : null;
        this.messageLoggingStatus = new AtomicBoolean(true);
        this.messageLoggingRoot = msgLogRoot != null && msgLogRoot.length() > 0 ? msgLogRoot : "com.sun.xml.ws.messagedump";
        this.messageLoggingLevel = msgLogLevel != null ? msgLogLevel : DEFAULT_MSG_LOG_LEVEL;
        this.enabled = true;
    }

    public MessageDumpingFeature(boolean enabled) {
        this();
        this.enabled = enabled;
    }

    @FeatureConstructor(value={"enabled", "messageLoggingRoot", "messageLoggingLevel", "storeMessages"})
    public MessageDumpingFeature(boolean enabled, String msgLogRoot, String msgLogLevel, boolean storeMessages) {
        this(msgLogRoot, Level.parse(msgLogLevel), storeMessages);
        this.enabled = enabled;
    }

    @ManagedAttribute
    public String getID() {
        return ID;
    }

    public String nextMessage() {
        return this.messageQueue != null ? this.messageQueue.poll() : null;
    }

    public void enableMessageLogging() {
        this.messageLoggingStatus.set(true);
    }

    public void disableMessageLogging() {
        this.messageLoggingStatus.set(false);
    }

    @ManagedAttribute
    public boolean getMessageLoggingStatus() {
        return this.messageLoggingStatus.get();
    }

    @ManagedAttribute
    public String getMessageLoggingRoot() {
        return this.messageLoggingRoot;
    }

    @ManagedAttribute
    public Level getMessageLoggingLevel() {
        return this.messageLoggingLevel;
    }

    boolean offerMessage(String message) {
        return this.messageQueue != null ? this.messageQueue.offer(message) : false;
    }
}

