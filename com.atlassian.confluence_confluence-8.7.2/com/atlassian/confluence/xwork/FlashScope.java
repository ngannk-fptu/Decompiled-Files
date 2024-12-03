/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.google.common.collect.Maps
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.xwork;

import com.atlassian.core.filters.ServletContextThreadLocal;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;

public class FlashScope {
    private static final String FLASH_SCOPE_KEY = "flash-scope-key:";
    private static final String FLASH_IN_SCOPE_KEY = "flash-in-scope-key";
    private static final String FLASH_OUT_SCOPE_KEY = "flash-out-scope-key";
    static final String FLASH_ID_PARAM = "flashId";
    private static final Random generator = new Random();

    public static void put(String key, Object value) {
        HttpServletRequest request = FlashScope.getHttpServletRequest();
        if (request == null) {
            return;
        }
        Map attrs = (Map)request.getAttribute(FLASH_OUT_SCOPE_KEY);
        if (attrs == null) {
            attrs = Maps.newHashMap();
            request.setAttribute(FLASH_OUT_SCOPE_KEY, (Object)attrs);
        }
        attrs.put(key, value);
    }

    public static Object get(String key) {
        HttpServletRequest request = FlashScope.getHttpServletRequest();
        if (request == null) {
            return null;
        }
        Map attrs = (Map)request.getAttribute(FLASH_IN_SCOPE_KEY);
        return attrs != null ? attrs.get(key) : null;
    }

    public static boolean has(String key) {
        HttpServletRequest request = FlashScope.getHttpServletRequest();
        if (request == null) {
            return false;
        }
        Map attrs = (Map)request.getAttribute(FLASH_IN_SCOPE_KEY);
        return attrs != null && attrs.containsKey(key);
    }

    public static String getFlashScopeUrl(String url, String flashId) {
        if (StringUtils.isBlank((CharSequence)flashId)) {
            return url;
        }
        String joiner = url.contains("?") ? "&" : "?";
        return url + joiner + "flashId=" + flashId;
    }

    public static String persist() {
        HttpServletRequest request = FlashScope.getHttpServletRequest();
        if (request == null) {
            return null;
        }
        HttpSession sessionOut = request.getSession();
        if (sessionOut == null) {
            return null;
        }
        Map map = (Map)request.getAttribute(FLASH_OUT_SCOPE_KEY);
        if (map == null) {
            return null;
        }
        String flashId = String.valueOf(generator.nextInt());
        sessionOut.setAttribute(FlashScope.getSessionKey(flashId), (Object)map);
        return flashId;
    }

    static void retrieve() {
        HttpServletRequest request = FlashScope.getHttpServletRequest();
        if (request == null) {
            return;
        }
        String flashId = request.getParameter(FLASH_ID_PARAM);
        if (StringUtils.isEmpty((CharSequence)flashId)) {
            return;
        }
        HttpSession sessionIn = request.getSession();
        if (sessionIn == null) {
            return;
        }
        String sessionKey = FlashScope.getSessionKey(flashId);
        Map map = (Map)sessionIn.getAttribute(sessionKey);
        if (map == null) {
            return;
        }
        request.setAttribute(FLASH_IN_SCOPE_KEY, (Object)map);
        sessionIn.removeAttribute(sessionKey);
    }

    private static HttpServletRequest getHttpServletRequest() {
        return ServletContextThreadLocal.getRequest();
    }

    private static String getSessionKey(String flashId) {
        return FLASH_SCOPE_KEY + flashId;
    }
}

