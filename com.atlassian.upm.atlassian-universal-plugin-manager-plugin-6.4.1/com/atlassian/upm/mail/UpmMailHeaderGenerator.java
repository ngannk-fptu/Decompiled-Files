/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.mail;

import com.atlassian.upm.mail.EmailType;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpmMailHeaderGenerator {
    private static final Logger logger = LoggerFactory.getLogger(UpmMailHeaderGenerator.class);
    public static final String MESSAGE_ID_HEADER = "Message-ID";
    public static final String IN_REPLY_TO_HEADER = "In-Reply-To";
    public static final String REFERENCES_HEADER = "References";

    public static Map<String, String> generateMailHeader(EmailType emailType, String pluginKey) {
        String baseId = "<" + pluginKey + "_" + UpmMailHeaderGenerator.group(emailType).getName();
        String suffix = "@" + UpmMailHeaderGenerator.getHostName() + ">";
        String messageId = baseId + "-" + System.nanoTime() + suffix;
        String replyTo = baseId + suffix;
        HashMap<String, String> result = new HashMap<String, String>();
        result.put(MESSAGE_ID_HEADER, messageId);
        result.put(IN_REPLY_TO_HEADER, replyTo);
        result.put(REFERENCES_HEADER, replyTo);
        return Collections.unmodifiableMap(result);
    }

    private static UpmEmailGroup group(EmailType emailType) {
        switch (emailType) {
            case ADDON_REQUESTED: 
            case ADDON_REQUEST_UPDATED: 
            case ADDON_REQUEST_FULFILLED: 
            case ADDON_REQUEST_DISMISSED: {
                return UpmEmailGroup.ADDON_REQUEST_GROUP;
            }
            case ADDON_UPDATE_FREE_TO_PAID: {
                return UpmEmailGroup.ADDON_UPDATE_GROUP;
            }
        }
        throw new IllegalArgumentException("Unable to find group for " + (Object)((Object)emailType));
    }

    private static String getHostName() {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            if (localhost != null && StringUtils.isNotBlank((CharSequence)localhost.getHostName())) {
                return localhost.getHostName();
            }
        }
        catch (UnknownHostException e) {
            logger.debug("unable to get the host name", (Throwable)e);
        }
        return "localhost";
    }

    public static enum UpmEmailGroup {
        ADDON_REQUEST_GROUP("REQUEST"),
        ADDON_LICENSE_GROUP("LICENSE"),
        ADDON_UPDATE_GROUP("UPDATE");

        private final String name;

        private UpmEmailGroup(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}

