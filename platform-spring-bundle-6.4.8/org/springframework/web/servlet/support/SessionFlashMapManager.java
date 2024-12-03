/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 */
package org.springframework.web.servlet.support;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.AbstractFlashMapManager;
import org.springframework.web.util.WebUtils;

public class SessionFlashMapManager
extends AbstractFlashMapManager {
    private static final String FLASH_MAPS_SESSION_ATTRIBUTE = SessionFlashMapManager.class.getName() + ".FLASH_MAPS";

    @Override
    @Nullable
    protected List<FlashMap> retrieveFlashMaps(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null ? (List)session.getAttribute(FLASH_MAPS_SESSION_ATTRIBUTE) : null;
    }

    @Override
    protected void updateFlashMaps(List<FlashMap> flashMaps, HttpServletRequest request, HttpServletResponse response) {
        WebUtils.setSessionAttribute(request, FLASH_MAPS_SESSION_ATTRIBUTE, !flashMaps.isEmpty() ? flashMaps : null);
    }

    @Override
    protected Object getFlashMapsMutex(HttpServletRequest request) {
        return WebUtils.getSessionMutex(request.getSession());
    }
}

