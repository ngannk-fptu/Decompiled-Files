/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 */
package org.springframework.web.servlet.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.support.WebContentGenerator;
import org.springframework.web.util.WebUtils;

public abstract class AbstractController
extends WebContentGenerator
implements Controller {
    private boolean synchronizeOnSession = false;

    public AbstractController() {
        this(true);
    }

    public AbstractController(boolean restrictDefaultSupportedMethods) {
        super(restrictDefaultSupportedMethods);
    }

    public final void setSynchronizeOnSession(boolean synchronizeOnSession) {
        this.synchronizeOnSession = synchronizeOnSession;
    }

    public final boolean isSynchronizeOnSession() {
        return this.synchronizeOnSession;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Nullable
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session;
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            response.setHeader("Allow", this.getAllowHeader());
            return null;
        }
        this.checkRequest(request);
        this.prepareResponse(response);
        if (this.synchronizeOnSession && (session = request.getSession(false)) != null) {
            Object mutex;
            Object object = mutex = WebUtils.getSessionMutex(session);
            synchronized (object) {
                return this.handleRequestInternal(request, response);
            }
        }
        return this.handleRequestInternal(request, response);
    }

    @Nullable
    protected abstract ModelAndView handleRequestInternal(HttpServletRequest var1, HttpServletResponse var2) throws Exception;
}

