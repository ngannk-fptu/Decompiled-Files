/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 */
package com.atlassian.confluence.util.i18n;

import com.atlassian.confluence.util.i18n.I18nModeManager;
import com.atlassian.confluence.util.i18n.LightningTranslationMode;
import com.atlassian.confluence.util.i18n.NormalTranslationMode;
import com.atlassian.confluence.util.i18n.TranslationMode;
import com.atlassian.confluence.web.context.HttpContext;
import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class DefaultI18nModeManager
implements I18nModeManager {
    private HttpContext httpContext;
    private final NormalTranslationMode NORMAL = new NormalTranslationMode();
    private final LightningTranslationMode LIGHTNING = new LightningTranslationMode();
    private final List<TranslationMode> MODES = ImmutableList.of((Object)this.NORMAL, (Object)this.LIGHTNING);

    public void setHttpContext(HttpContext httpContext) {
        this.httpContext = httpContext;
    }

    @Override
    public TranslationMode getTranslationMode() {
        HttpSession session = this.httpContext.getSession(false);
        TranslationMode mode = this.NORMAL;
        if (session != null && session.getAttribute("confluence.i18n.mode") != null) {
            mode = (TranslationMode)session.getAttribute("confluence.i18n.mode");
        }
        return mode;
    }

    @Override
    public void setTranslationMode(HttpServletRequest httpServletRequest, TranslationMode mode) {
        HttpSession session = httpServletRequest.getSession();
        session.setAttribute("confluence.i18n.mode", (Object)mode);
    }

    @Override
    public TranslationMode getModeForString(String param) {
        for (TranslationMode mode : this.MODES) {
            if (!mode.getParams().contains(param)) continue;
            return mode;
        }
        return null;
    }
}

