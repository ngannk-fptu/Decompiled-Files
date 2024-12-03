/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.sal.api.net.ResponseException
 *  com.google.common.base.Supplier
 *  org.apache.commons.lang3.SystemUtils
 *  org.jdom.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.jira;

import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.confluence.extra.jira.Channel;
import com.atlassian.confluence.extra.jira.JiraIssuesManagerInternal;
import com.atlassian.confluence.extra.jira.request.JiraChannelResponseHandler;
import com.atlassian.confluence.plugins.jira.beans.JiraIssueBean;
import com.atlassian.sal.api.net.ResponseException;
import com.google.common.base.Supplier;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.SystemUtils;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xerial.snappy.Snappy;

public interface JiraIssuesManager
extends JiraIssuesManagerInternal {
    public Map<String, String> getColumnMap(String var1);

    public void setColumnMap(String var1, Map<String, String> var2);

    public Channel retrieveXMLAsChannel(String var1, Set<String> var2, ReadOnlyApplicationLink var3, boolean var4, boolean var5) throws IOException, CredentialsRequiredException, ResponseException;

    default public Channel retrieveXMLAsChannel(String url, Set<String> columns, ReadOnlyApplicationLink appLink, boolean forceAnonymous, boolean checkCacheBeforeLookup, boolean updateCacheAfterLookup) throws IOException, CredentialsRequiredException, ResponseException {
        return this.retrieveXMLAsChannel(url, columns, appLink, forceAnonymous, checkCacheBeforeLookup);
    }

    public Channel retrieveXMLAsChannelByAnonymous(String var1, Set<String> var2, ReadOnlyApplicationLink var3, boolean var4, boolean var5) throws IOException, CredentialsRequiredException, ResponseException;

    default public Channel retrieveXMLAsChannelByAnonymous(String url, Set<String> columns, ReadOnlyApplicationLink applink, boolean forceAnonymous, boolean checkCacheBeforeLookup, boolean updateCacheAfterLookup) throws IOException, CredentialsRequiredException, ResponseException {
        return this.retrieveXMLAsChannelByAnonymous(url, columns, applink, forceAnonymous, checkCacheBeforeLookup);
    }

    public String retrieveXMLAsString(String var1, Set<String> var2, ReadOnlyApplicationLink var3, boolean var4, boolean var5) throws IOException, CredentialsRequiredException, ResponseException;

    default public String retrieveXMLAsString(String url, Set<String> columns, ReadOnlyApplicationLink applink, boolean forceAnonymous, boolean checkCacheBeforeLookup, boolean updateCacheAfterLookup) throws IOException, CredentialsRequiredException, ResponseException {
        return this.retrieveXMLAsString(url, columns, applink, forceAnonymous, checkCacheBeforeLookup);
    }

    public String retrieveJQLFromFilter(String var1, ReadOnlyApplicationLink var2) throws ResponseException;

    public String executeJqlQuery(String var1, ReadOnlyApplicationLink var2) throws CredentialsRequiredException, ResponseException;

    public List<JiraIssueBean> createIssues(List<JiraIssueBean> var1, ReadOnlyApplicationLink var2) throws CredentialsRequiredException, ResponseException;

    default public void initializeCache() {
    }

    public static class ByteStreamBasedSupplier
    implements Supplier<Element>,
    Serializable {
        private static final Logger LOGGER = LoggerFactory.getLogger(ByteStreamBasedSupplier.class);
        final byte[] compressedBytes;

        public ByteStreamBasedSupplier(byte[] bytes) {
            this.compressedBytes = ByteStreamBasedSupplier.compress(bytes);
        }

        static byte[] compress(byte[] bytes) {
            if (SystemUtils.IS_OS_SOLARIS || SystemUtils.IS_OS_SUN_OS) {
                LOGGER.debug("Sun Solaris or Sun OS will be ignored Snappy-java compression");
                return bytes;
            }
            try {
                return Snappy.compress(bytes);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        static byte[] uncompress(byte[] bytes) {
            if (SystemUtils.IS_OS_SOLARIS || SystemUtils.IS_OS_SUN_OS) {
                LOGGER.debug("Sun Solaris or Sun OS will be ignored Snappy-java decompression");
                return bytes;
            }
            try {
                return Snappy.uncompress(bytes);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public Element get() {
            try {
                return JiraChannelResponseHandler.getChannelElement(new ByteArrayInputStream(ByteStreamBasedSupplier.uncompress(this.compressedBytes)));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class DomBasedSupplier
    implements Supplier<Element>,
    Serializable {
        private final Element channelElement;

        public DomBasedSupplier(Element channelElement) {
            this.channelElement = channelElement;
        }

        public Element get() {
            return this.channelElement;
        }
    }
}

