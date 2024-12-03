/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.http.trust.TrustedConnectionStatus
 *  org.apache.commons.io.IOUtils
 *  org.jdom.Document
 *  org.jdom.Element
 *  org.jdom.JDOMException
 *  org.jdom.input.SAXBuilder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.jira.request;

import com.atlassian.confluence.extra.jira.Channel;
import com.atlassian.confluence.extra.jira.api.services.JiraResponseHandler;
import com.atlassian.confluence.extra.jira.request.SAXBuilderFactory;
import com.atlassian.confluence.util.http.trust.TrustedConnectionStatus;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import org.apache.commons.io.IOUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraChannelResponseHandler
implements JiraResponseHandler,
Serializable {
    private static final Logger log = LoggerFactory.getLogger(JiraChannelResponseHandler.class);
    private final String url;
    private Channel responseChannel;

    public JiraChannelResponseHandler(String url) {
        this.url = url;
    }

    public Channel getResponseChannel() {
        return this.responseChannel;
    }

    @Override
    public void handleJiraResponse(InputStream in, TrustedConnectionStatus trustedConnectionStatus) throws IOException {
        this.responseChannel = new Channel(this.url, IOUtils.toByteArray((InputStream)in), trustedConnectionStatus);
    }

    public static Element getChannelElement(InputStream responseStream) throws IOException {
        try {
            SAXBuilder saxBuilder = SAXBuilderFactory.createSAXBuilder();
            Document document = saxBuilder.build(responseStream);
            Element root = document.getRootElement();
            if (root != null) {
                Element element = root.getChild("channel");
                return element;
            }
            Element element = null;
            return element;
        }
        catch (JDOMException e) {
            log.error("Error while trying to assemble the issues returned in XML format: " + e.getMessage());
            throw new IOException(e.getMessage());
        }
        finally {
            IOUtils.closeQuietly((InputStream)responseStream);
        }
    }
}

