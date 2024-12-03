/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.Lists
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.manager.validation;

import com.atlassian.crowd.manager.proxy.TrustedProxyManager;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XForwardedForUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(XForwardedForUtil.class);
    private static final String X_FORWARDED_FOR = "X-Forwarded-For";

    private XForwardedForUtil() {
    }

    public static InetAddress getTrustedAddress(TrustedProxyManager trustedProxyManager, HttpServletRequest request) {
        String trustedAddress = XForwardedForUtil.getTrustedAddress(trustedProxyManager, request.getRemoteAddr(), request.getHeader(X_FORWARDED_FOR));
        try {
            return InetAddress.getByName(trustedAddress);
        }
        catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getTrustedAddress(TrustedProxyManager trustedProxyManager, String requestAddress, String xForwardedFor) {
        ArrayList chain = Lists.newArrayList((Object[])StringUtils.split((String)Strings.nullToEmpty((String)xForwardedFor), (String)", "));
        chain.add(requestAddress);
        List proxies = chain.subList(1, chain.size());
        boolean areAllProxiesTrusted = proxies.stream().allMatch(trustedProxyManager::isTrusted);
        if (!areAllProxiesTrusted && LOGGER.isDebugEnabled()) {
            String untrustedProxies = proxies.stream().filter(p -> !trustedProxyManager.isTrusted((String)p)).collect(Collectors.joining(","));
            LOGGER.debug("Proxies [{}] are untrusted (for requestAddress={} and xForwardedFor={})", new Object[]{untrustedProxies, requestAddress, xForwardedFor});
        }
        return areAllProxiesTrusted ? (String)chain.get(0) : requestAddress;
    }
}

