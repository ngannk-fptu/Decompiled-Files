/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.RequestDispatcher
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.servlet.view;

import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.util.WebUtils;

public class InternalResourceView
extends AbstractUrlBasedView {
    private boolean alwaysInclude = false;
    private boolean preventDispatchLoop = false;

    public InternalResourceView() {
    }

    public InternalResourceView(String url) {
        super(url);
    }

    public InternalResourceView(String url, boolean alwaysInclude) {
        super(url);
        this.alwaysInclude = alwaysInclude;
    }

    public void setAlwaysInclude(boolean alwaysInclude) {
        this.alwaysInclude = alwaysInclude;
    }

    public void setPreventDispatchLoop(boolean preventDispatchLoop) {
        this.preventDispatchLoop = preventDispatchLoop;
    }

    @Override
    protected boolean isContextRequired() {
        return false;
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.exposeModelAsRequestAttributes(model, request);
        this.exposeHelpers(request);
        String dispatcherPath = this.prepareForRendering(request, response);
        RequestDispatcher rd = this.getRequestDispatcher(request, dispatcherPath);
        if (rd == null) {
            throw new ServletException("Could not get RequestDispatcher for [" + this.getUrl() + "]: Check that the corresponding file exists within your web application archive!");
        }
        if (this.useInclude(request, response)) {
            response.setContentType(this.getContentType());
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Including [" + this.getUrl() + "]"));
            }
            rd.include((ServletRequest)request, (ServletResponse)response);
        } else {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Forwarding to [" + this.getUrl() + "]"));
            }
            rd.forward((ServletRequest)request, (ServletResponse)response);
        }
    }

    protected void exposeHelpers(HttpServletRequest request) throws Exception {
    }

    protected String prepareForRendering(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = this.getUrl();
        Assert.state(path != null, "'url' not set");
        if (this.preventDispatchLoop) {
            String uri = request.getRequestURI();
            if (path.startsWith("/") ? uri.equals(path) : uri.equals(StringUtils.applyRelativePath(uri, path))) {
                throw new ServletException("Circular view path [" + path + "]: would dispatch back to the current handler URL [" + uri + "] again. Check your ViewResolver setup! (Hint: This may be the result of an unspecified view, due to default view name generation.)");
            }
        }
        return path;
    }

    @Nullable
    protected RequestDispatcher getRequestDispatcher(HttpServletRequest request, String path) {
        return request.getRequestDispatcher(path);
    }

    protected boolean useInclude(HttpServletRequest request, HttpServletResponse response) {
        return this.alwaysInclude || WebUtils.isIncludeRequest((ServletRequest)request) || response.isCommitted();
    }
}

