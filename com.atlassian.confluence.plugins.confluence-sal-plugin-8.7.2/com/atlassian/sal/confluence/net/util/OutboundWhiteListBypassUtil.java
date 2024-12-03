/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.sal.confluence.net.util;

import com.google.common.collect.ImmutableSet;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class OutboundWhiteListBypassUtil {
    private static final Set<String> LOCAL_HOSTS_REGEX = ImmutableSet.of((Object)"^0.0.0.0$", (Object)"^127\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$", (Object)"^0:0:0:0:0:0:[a-z0-9]{1,4}:[a-z0-9]{1,4}");
    public static final String ASTC_DATA_URL_PROPERTY_NAME = "atst.data.url";
    public static final String SYNCHRONY_HOST_PROPERTY_NAME = "synchrony.host";
    public static final String SYNCHRONY_LOCAL_PROPERTY_NAME = "synchrony.local.service.url";
    public static final String SYNCHRONY_PROPERTY_NAME = "synchrony.service.url";
    public static final String DEFAULT_ASTC_DATA_URL_PREFIX = "https://atst-data.atl-paas.net/healthcheck";
    public static final String LOCAL_SYNCHRONY_HEARTBEAT_PATH = "synchrony/heartbeat";
    public static final String SYNCHRONY_HEARTBEAT_PATH = "/heartbeat";
    public static final String SYNCHRONY_PROXY_HEALTHCHECK_PATH = "synchrony-proxy/healthcheck";
    public static final String WHISPHER_DEFAULT_ENDPOINT = "https://server-notifications.atlassian.com/api/delivery";
    public static final String WHISPHER_ENDPOINT_PROPERTY = "atlassian.whisper.delivery.endpoint";
    private static final String APPLICATION_LINKS_PATH_REG_EX = "^(http?|https)://(([^:/]+)(?::(\\d{1,5}))?)(/[^/]+){0,5}/rest/(applinks|applinks-oauth)/(\\d+.\\d|latest)/(.*)";

    public static boolean byPassOutboundWhitelist(String url, String synchronyBandanaUrl) {
        return OutboundWhiteListBypassUtil.isAstcHealthCheck(url) || OutboundWhiteListBypassUtil.isApplinksUrl(url) || OutboundWhiteListBypassUtil.isSynchronyProxyHealthcheckUrl(url) || OutboundWhiteListBypassUtil.isSynchronyHeartbeatUrl(url, synchronyBandanaUrl) || OutboundWhiteListBypassUtil.isWhispherEndpoint(url);
    }

    private static boolean isWhispherEndpoint(String url) {
        return StringUtils.contains((CharSequence)url, (CharSequence)System.getProperty(WHISPHER_ENDPOINT_PROPERTY, WHISPHER_DEFAULT_ENDPOINT));
    }

    private static boolean isApplinksUrl(String url) {
        return url.matches(APPLICATION_LINKS_PATH_REG_EX);
    }

    private static boolean isAstcHealthCheck(String url) {
        String astcUrlPrefix = System.getProperty(ASTC_DATA_URL_PROPERTY_NAME);
        if (StringUtils.isEmpty((CharSequence)astcUrlPrefix)) {
            astcUrlPrefix = DEFAULT_ASTC_DATA_URL_PREFIX;
        }
        return url.startsWith(astcUrlPrefix);
    }

    private static boolean isSynchronyHost(String url) {
        URI remoteUrl = URI.create(url);
        String host = remoteUrl.getHost();
        String synchronyHost = System.getProperty(SYNCHRONY_HOST_PROPERTY_NAME);
        if (null != synchronyHost && synchronyHost.equals(host)) {
            return true;
        }
        return OutboundWhiteListBypassUtil.isLocalHost(url);
    }

    private static boolean isLocalHost(String url) {
        try {
            URI remoteUrl = URI.create(url);
            String host = remoteUrl.getHost();
            InetAddress inetAddress = InetAddress.getByName(host);
            String hostAddress = inetAddress.getHostAddress();
            for (String blockedHostRegex : LOCAL_HOSTS_REGEX) {
                if (!hostAddress.matches(blockedHostRegex)) continue;
                return true;
            }
        }
        catch (UnknownHostException e) {
            return false;
        }
        return false;
    }

    private static boolean isSynchronyProxyHealthcheckUrl(String url) {
        return url.contains(SYNCHRONY_PROXY_HEALTHCHECK_PATH) && OutboundWhiteListBypassUtil.isSynchronyHost(url);
    }

    private static boolean isLocalSynchronyUrl(String url) {
        return url.contains(LOCAL_SYNCHRONY_HEARTBEAT_PATH) && OutboundWhiteListBypassUtil.isLocalHost(url);
    }

    private static boolean isSynchronyHeartbeatUrl(String url, String synchronyBandanaUrl) {
        return OutboundWhiteListBypassUtil.isLocalSynchronyUrl(url) || url.contains(SYNCHRONY_HEARTBEAT_PATH) && OutboundWhiteListBypassUtil.isExternalSynchrony(url, synchronyBandanaUrl);
    }

    private static boolean isExternalSynchrony(String url, String synchronyBandanaUrl) {
        String synchronyServiceUrl = System.getProperty(SYNCHRONY_LOCAL_PROPERTY_NAME);
        String synchronyExternalServiceUrl = System.getProperty(SYNCHRONY_PROPERTY_NAME);
        return StringUtils.isNotBlank((CharSequence)synchronyServiceUrl) && StringUtils.contains((CharSequence)url, (CharSequence)synchronyServiceUrl) || StringUtils.isNotBlank((CharSequence)synchronyExternalServiceUrl) && StringUtils.contains((CharSequence)url, (CharSequence)synchronyExternalServiceUrl) || StringUtils.isNotBlank((CharSequence)synchronyBandanaUrl) && StringUtils.contains((CharSequence)url, (CharSequence)synchronyBandanaUrl);
    }
}

