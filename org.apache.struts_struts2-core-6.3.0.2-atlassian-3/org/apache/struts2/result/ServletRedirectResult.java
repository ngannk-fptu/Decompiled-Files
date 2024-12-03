/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.result;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.reflection.ReflectionException;
import com.opensymphony.xwork2.util.reflection.ReflectionExceptionHandler;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.result.Redirectable;
import org.apache.struts2.result.StrutsResultSupport;
import org.apache.struts2.url.QueryStringBuilder;

public class ServletRedirectResult
extends StrutsResultSupport
implements ReflectionExceptionHandler,
Redirectable {
    private static final long serialVersionUID = 6316947346435301270L;
    private static final Logger LOG = LogManager.getLogger(ServletRedirectResult.class);
    protected boolean prependServletContext = true;
    protected ActionMapper actionMapper;
    protected int statusCode = 302;
    protected boolean suppressEmptyParameters = false;
    protected Map<String, Object> requestParameters = new LinkedHashMap<String, Object>();
    protected String anchor;
    private QueryStringBuilder queryStringBuilder;

    public ServletRedirectResult() {
    }

    public ServletRedirectResult(String location) {
        this(location, null);
    }

    public ServletRedirectResult(String location, String anchor) {
        super(location);
        this.anchor = anchor;
    }

    @Inject
    public void setActionMapper(ActionMapper mapper) {
        this.actionMapper = mapper;
    }

    @Inject
    public void setQueryStringBuilder(QueryStringBuilder queryStringBuilder) {
        this.queryStringBuilder = queryStringBuilder;
    }

    public void setStatusCode(int code) {
        this.statusCode = code;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    public void setPrependServletContext(boolean prependServletContext) {
        this.prependServletContext = prependServletContext;
    }

    @Override
    public void execute(ActionInvocation invocation) throws Exception {
        if (invocation == null) {
            throw new IllegalArgumentException("Invocation cannot be null!");
        }
        if (this.anchor != null) {
            this.anchor = this.conditionalParse(this.anchor, invocation);
        }
        super.execute(invocation);
    }

    @Override
    protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
        ResultConfig resultConfig;
        ActionContext ctx = invocation.getInvocationContext();
        HttpServletRequest request = ctx.getServletRequest();
        HttpServletResponse response = ctx.getServletResponse();
        if (this.isPathUrl(finalLocation)) {
            if (!finalLocation.startsWith("/")) {
                ActionMapping mapping = this.actionMapper.getMapping(request, Dispatcher.getInstance().getConfigurationManager());
                String namespace = null;
                if (mapping != null) {
                    namespace = mapping.getNamespace();
                }
                finalLocation = namespace != null && namespace.length() > 0 && !"/".equals(namespace) ? namespace + "/" + finalLocation : "/" + finalLocation;
            }
            if (this.prependServletContext && request.getContextPath() != null && request.getContextPath().length() > 0) {
                finalLocation = request.getContextPath() + finalLocation;
            }
        }
        if ((resultConfig = invocation.getProxy().getConfig().getResults().get(invocation.getResultCode())) != null) {
            Map<String, String> resultConfigParams = resultConfig.getParams();
            List<String> prohibitedResultParams = this.getProhibitedResultParams();
            for (Map.Entry<String, String> e : resultConfigParams.entrySet()) {
                if (prohibitedResultParams.contains(e.getKey())) continue;
                Collection<String> values = this.conditionalParseCollection(e.getValue(), invocation, this.suppressEmptyParameters);
                if (this.suppressEmptyParameters && values.isEmpty()) continue;
                this.requestParameters.put(e.getKey(), values);
            }
        }
        StringBuilder tmpLocation = new StringBuilder(finalLocation);
        this.queryStringBuilder.build(this.requestParameters, tmpLocation, "&");
        if (this.anchor != null) {
            tmpLocation.append('#').append(this.anchor);
        }
        finalLocation = response.encodeRedirectURL(tmpLocation.toString());
        LOG.debug("Redirecting to finalLocation: {}", (Object)finalLocation);
        this.sendRedirect(response, finalLocation);
    }

    protected List<String> getProhibitedResultParams() {
        return Arrays.asList("location", "namespace", "method", "encode", "parse", "location", "prependServletContext", "suppressEmptyParameters", "anchor", "statusCode");
    }

    protected void sendRedirect(HttpServletResponse response, String finalLocation) throws IOException {
        block7: {
            try {
                if (302 == this.statusCode) {
                    response.sendRedirect(finalLocation);
                    break block7;
                }
                response.setStatus(this.statusCode);
                response.setHeader("Location", finalLocation);
                try {
                    response.getWriter().write(finalLocation);
                }
                finally {
                    response.getWriter().close();
                }
            }
            catch (IOException ioe) {
                LOG.warn("Unable to redirect to: {}, code: {}; {}", (Object)finalLocation, (Object)this.statusCode, (Object)ioe);
                throw ioe;
            }
            catch (IllegalStateException ise) {
                LOG.warn("Unable to redirect to: {}, code: {}; isCommited: {}; {}", (Object)finalLocation, (Object)this.statusCode, (Object)response.isCommitted(), (Object)ise);
                throw ise;
            }
        }
    }

    protected boolean isPathUrl(String url) {
        try {
            URI uri;
            String rawUrl = url;
            if (url.contains("?")) {
                rawUrl = url.substring(0, url.indexOf(63));
            }
            if ((uri = URI.create(rawUrl.replaceAll(" ", "%20"))).isAbsolute()) {
                URL validUrl = uri.toURL();
                LOG.debug("[{}] is full url, not a path", (Object)url);
                return validUrl.getProtocol() == null;
            }
            LOG.debug("[{}] isn't absolute URI, assuming it's a path", (Object)url);
            return true;
        }
        catch (IllegalArgumentException | MalformedURLException e) {
            LOG.debug("[{}] isn't a valid URL, assuming it's a path", (Object)url, (Object)e);
            return true;
        }
    }

    public void setSuppressEmptyParameters(boolean suppressEmptyParameters) {
        this.suppressEmptyParameters = suppressEmptyParameters;
    }

    public ServletRedirectResult addParameter(String key, Object value) {
        this.requestParameters.put(key, String.valueOf(value));
        return this;
    }

    @Override
    public void handle(ReflectionException ex) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(ex.getMessage(), (Throwable)ex);
        }
    }
}

