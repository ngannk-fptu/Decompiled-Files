/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.Nullable
 *  javax.xml.ws.WebServiceFeature
 *  org.glassfish.gmbal.ManagedAttribute
 *  org.glassfish.gmbal.ManagedData
 *  org.jvnet.mimepull.MIMEConfig
 */
package com.sun.xml.ws.developer;

import com.sun.istack.Nullable;
import com.sun.xml.ws.api.FeatureConstructor;
import javax.xml.ws.WebServiceFeature;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedData;
import org.jvnet.mimepull.MIMEConfig;

@ManagedData
public final class StreamingAttachmentFeature
extends WebServiceFeature {
    public static final String ID = "http://jax-ws.dev.java.net/features/mime";
    private MIMEConfig config;
    private String dir;
    private boolean parseEagerly;
    private long memoryThreshold;

    public StreamingAttachmentFeature() {
    }

    @FeatureConstructor(value={"dir", "parseEagerly", "memoryThreshold"})
    public StreamingAttachmentFeature(@Nullable String dir, boolean parseEagerly, long memoryThreshold) {
        this.enabled = true;
        this.dir = dir;
        this.parseEagerly = parseEagerly;
        this.memoryThreshold = memoryThreshold;
    }

    @ManagedAttribute
    public String getID() {
        return ID;
    }

    @ManagedAttribute
    public MIMEConfig getConfig() {
        if (this.config == null) {
            this.config = new MIMEConfig();
            this.config.setDir(this.dir);
            this.config.setParseEagerly(this.parseEagerly);
            this.config.setMemoryThreshold(this.memoryThreshold);
            this.config.validate();
        }
        return this.config;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public void setParseEagerly(boolean parseEagerly) {
        this.parseEagerly = parseEagerly;
    }

    public void setMemoryThreshold(long memoryThreshold) {
        this.memoryThreshold = memoryThreshold;
    }
}

