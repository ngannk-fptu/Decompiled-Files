/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 */
package freemarker.ext.servlet;

import freemarker.ext.servlet.FreemarkerServlet;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public final class HttpSessionHashModel
implements TemplateHashModel,
Serializable {
    private static final long serialVersionUID = 1L;
    private transient HttpSession session;
    private final transient ObjectWrapper wrapper;
    private final transient FreemarkerServlet servlet;
    private final transient HttpServletRequest request;
    private final transient HttpServletResponse response;

    public HttpSessionHashModel(HttpSession session, ObjectWrapper wrapper) {
        this.session = session;
        this.wrapper = wrapper;
        this.servlet = null;
        this.request = null;
        this.response = null;
    }

    public HttpSessionHashModel(FreemarkerServlet servlet, HttpServletRequest request, HttpServletResponse response, ObjectWrapper wrapper) {
        this.wrapper = wrapper;
        this.servlet = servlet;
        this.request = request;
        this.response = response;
    }

    @Override
    public TemplateModel get(String key) throws TemplateModelException {
        this.checkSessionExistence();
        return this.wrapper.wrap(this.session != null ? this.session.getAttribute(key) : null);
    }

    private void checkSessionExistence() throws TemplateModelException {
        if (this.session == null && this.request != null) {
            this.session = this.request.getSession(false);
            if (this.session != null && this.servlet != null) {
                try {
                    this.servlet.initializeSessionAndInstallModel(this.request, this.response, this, this.session);
                }
                catch (RuntimeException e) {
                    throw e;
                }
                catch (Exception e) {
                    throw new TemplateModelException(e);
                }
            }
        }
    }

    boolean isOrphaned(HttpSession currentSession) {
        return this.session != null && this.session != currentSession || this.session == null && this.request == null;
    }

    @Override
    public boolean isEmpty() throws TemplateModelException {
        this.checkSessionExistence();
        return this.session == null || !this.session.getAttributeNames().hasMoreElements();
    }
}

