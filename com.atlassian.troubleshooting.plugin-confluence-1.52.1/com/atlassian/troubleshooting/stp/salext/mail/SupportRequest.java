/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.stp.salext.mail;

import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.salext.mail.SupportRequestAttachment;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SupportRequest
implements Serializable {
    public static final int DEFAULT_PRIORITY = 3;
    private static final Logger LOG = LoggerFactory.getLogger(SupportRequest.class);
    private final Map<String, String> headers = new HashMap<String, String>();
    private final List<SupportRequestAttachment> attachments = new ArrayList<SupportRequestAttachment>();
    private final String description;
    private final String subject;
    private final String toAddress;
    private final String fromAddress;
    private final String body;
    private final int priority;

    public SupportRequest(String description, String subject, String toAddress, String fromAddress, String body, int priority) {
        this.description = description;
        this.subject = subject;
        this.toAddress = toAddress;
        this.fromAddress = fromAddress;
        this.body = body;
        this.priority = priority;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String saveForMail(SupportApplicationInfo info) {
        String version;
        String serverID;
        Properties prop = new Properties();
        prop.setProperty("description", this.description);
        prop.setProperty("contactEmail", this.fromAddress);
        prop.setProperty("subject", this.subject);
        prop.setProperty("timeZone", info.getTimeZoneRelativeToGMT());
        prop.setProperty("priority", String.valueOf(this.priority));
        String sen = info.getApplicationSEN();
        if (sen != null) {
            prop.setProperty("sen", sen);
        }
        if ((serverID = info.getApplicationServerID()) != null) {
            prop.setProperty("serverID", serverID);
        }
        if ((version = info.getApplicationVersion()) != null) {
            prop.setProperty("version", version);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            prop.store(baos, null);
        }
        catch (IOException e) {
            LOG.error("Error storing mail properties", (Throwable)e);
        }
        finally {
            IOUtils.closeQuietly((OutputStream)baos);
        }
        return baos.toString();
    }

    public Iterable<Map.Entry<String, String>> getHeaders() {
        return this.headers.entrySet();
    }

    public Iterable<SupportRequestAttachment> getAttachments() {
        return this.attachments;
    }

    public void addHeader(String name, String value) {
        this.headers.put(name, value);
    }

    public void addAttachment(SupportRequestAttachment attachment) {
        this.attachments.add(attachment);
    }

    public String getDescription() {
        return this.description;
    }

    public String getFromAddress() {
        return this.fromAddress;
    }

    public String getToAddress() {
        return this.toAddress;
    }

    public String getSubject() {
        return this.subject;
    }

    public int getPriority() {
        return this.priority;
    }

    public String getBody() {
        return this.body;
    }
}

