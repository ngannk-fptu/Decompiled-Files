/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.RequestDispatcher
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.jsp.PageContext
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.result;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.result.StrutsResultSupport;
import org.apache.struts2.url.QueryStringParser;

public class ServletDispatcherResult
extends StrutsResultSupport {
    private static final long serialVersionUID = -1970659272360685627L;
    private static final Logger LOG = LogManager.getLogger(ServletDispatcherResult.class);
    private QueryStringParser queryStringParser;

    public ServletDispatcherResult() {
    }

    public ServletDispatcherResult(String location) {
        super(location);
    }

    @Inject
    public void setQueryStringParser(QueryStringParser queryStringParser) {
        this.queryStringParser = queryStringParser;
    }

    @Override
    public void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
        LOG.debug("Forwarding to location: {}", (Object)finalLocation);
        PageContext pageContext = ServletActionContext.getPageContext();
        if (pageContext != null) {
            pageContext.include(finalLocation);
        } else {
            HttpServletRequest request = ServletActionContext.getRequest();
            HttpServletResponse response = ServletActionContext.getResponse();
            RequestDispatcher dispatcher = request.getRequestDispatcher(finalLocation);
            if (StringUtils.isNotEmpty((CharSequence)finalLocation) && finalLocation.indexOf(63) > 0) {
                String queryString = finalLocation.substring(finalLocation.indexOf(63) + 1);
                HttpParameters parameters = this.getParameters(invocation);
                QueryStringParser.Result queryParams = this.queryStringParser.parse(queryString);
                if (!queryParams.isEmpty()) {
                    parameters = HttpParameters.create(queryParams.getQueryParams()).withParent(parameters).build();
                    invocation.getInvocationContext().withParameters(parameters);
                    invocation.getInvocationContext().getContextMap().put("parameters", parameters);
                }
            }
            if (dispatcher == null) {
                LOG.warn("Location {} not found!", (Object)finalLocation);
                response.sendError(404, "result '" + finalLocation + "' not found");
                return;
            }
            Boolean insideActionTag = (Boolean)ObjectUtils.defaultIfNull((Object)request.getAttribute("struts.actiontag.invocation"), (Object)Boolean.FALSE);
            if (!insideActionTag.booleanValue() && !response.isCommitted() && request.getAttribute("javax.servlet.include.servlet_path") == null) {
                request.setAttribute("struts.view_uri", (Object)finalLocation);
                request.setAttribute("struts.request_uri", (Object)request.getRequestURI());
                dispatcher.forward((ServletRequest)request, (ServletResponse)response);
            } else {
                dispatcher.include((ServletRequest)request, (ServletResponse)response);
            }
        }
    }

    protected HttpParameters getParameters(ActionInvocation invocation) {
        return invocation.getInvocationContext().getParameters();
    }
}

