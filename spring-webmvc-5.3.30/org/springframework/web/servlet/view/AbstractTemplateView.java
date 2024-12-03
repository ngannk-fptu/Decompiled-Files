/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 */
package org.springframework.web.servlet.view;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

public abstract class AbstractTemplateView
extends AbstractUrlBasedView {
    public static final String SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE = "springMacroRequestContext";
    private boolean exposeRequestAttributes = false;
    private boolean allowRequestOverride = false;
    private boolean exposeSessionAttributes = false;
    private boolean allowSessionOverride = false;
    private boolean exposeSpringMacroHelpers = true;

    public void setExposeRequestAttributes(boolean exposeRequestAttributes) {
        this.exposeRequestAttributes = exposeRequestAttributes;
    }

    public void setAllowRequestOverride(boolean allowRequestOverride) {
        this.allowRequestOverride = allowRequestOverride;
    }

    public void setExposeSessionAttributes(boolean exposeSessionAttributes) {
        this.exposeSessionAttributes = exposeSessionAttributes;
    }

    public void setAllowSessionOverride(boolean allowSessionOverride) {
        this.allowSessionOverride = allowSessionOverride;
    }

    public void setExposeSpringMacroHelpers(boolean exposeSpringMacroHelpers) {
        this.exposeSpringMacroHelpers = exposeSpringMacroHelpers;
    }

    @Override
    protected final void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session;
        if (this.exposeRequestAttributes) {
            LinkedHashMap<String, Object> exposed = null;
            Enumeration en = request.getAttributeNames();
            while (en.hasMoreElements()) {
                String attribute = (String)en.nextElement();
                if (model.containsKey(attribute) && !this.allowRequestOverride) {
                    throw new ServletException("Cannot expose request attribute '" + attribute + "' because of an existing model object of the same name");
                }
                Object attributeValue = request.getAttribute(attribute);
                if (this.logger.isDebugEnabled()) {
                    exposed = exposed != null ? exposed : new LinkedHashMap<String, Object>();
                    exposed.put(attribute, attributeValue);
                }
                model.put(attribute, attributeValue);
            }
            if (this.logger.isTraceEnabled() && exposed != null) {
                this.logger.trace((Object)("Exposed request attributes to model: " + exposed));
            }
        }
        if (this.exposeSessionAttributes && (session = request.getSession(false)) != null) {
            LinkedHashMap<String, Object> exposed = null;
            Enumeration en = session.getAttributeNames();
            while (en.hasMoreElements()) {
                String attribute = (String)en.nextElement();
                if (model.containsKey(attribute) && !this.allowSessionOverride) {
                    throw new ServletException("Cannot expose session attribute '" + attribute + "' because of an existing model object of the same name");
                }
                Object attributeValue = session.getAttribute(attribute);
                if (this.logger.isDebugEnabled()) {
                    exposed = exposed != null ? exposed : new LinkedHashMap<String, Object>();
                    exposed.put(attribute, attributeValue);
                }
                model.put(attribute, attributeValue);
            }
            if (this.logger.isTraceEnabled() && exposed != null) {
                this.logger.trace((Object)("Exposed session attributes to model: " + exposed));
            }
        }
        if (this.exposeSpringMacroHelpers) {
            if (model.containsKey(SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE)) {
                throw new ServletException("Cannot expose bind macro helper 'springMacroRequestContext' because of an existing model object of the same name");
            }
            model.put(SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE, new RequestContext(request, response, this.getServletContext(), model));
        }
        this.applyContentType(response);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Rendering [" + this.getUrl() + "]"));
        }
        this.renderMergedTemplateModel(model, request, response);
    }

    protected void applyContentType(HttpServletResponse response) {
        if (response.getContentType() == null) {
            response.setContentType(this.getContentType());
        }
    }

    protected abstract void renderMergedTemplateModel(Map<String, Object> var1, HttpServletRequest var2, HttpServletResponse var3) throws Exception;
}

