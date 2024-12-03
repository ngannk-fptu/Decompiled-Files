/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsException;
import org.apache.struts2.components.Form;
import org.apache.struts2.components.UrlProvider;
import org.apache.struts2.components.UrlRenderer;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.url.QueryStringParser;
import org.apache.struts2.views.util.UrlHelper;

public class ServletUrlRenderer
implements UrlRenderer {
    private static final Logger LOG = LogManager.getLogger(ServletUrlRenderer.class);
    private ActionMapper actionMapper;
    private UrlHelper urlHelper;
    private QueryStringParser queryStringParser;

    @Override
    @Inject
    public void setActionMapper(ActionMapper mapper) {
        this.actionMapper = mapper;
    }

    @Inject
    public void setUrlHelper(UrlHelper urlHelper) {
        this.urlHelper = urlHelper;
    }

    @Inject
    public void setQueryStringParser(QueryStringParser queryStringParser) {
        this.queryStringParser = queryStringParser;
    }

    @Override
    public void renderUrl(Writer writer, UrlProvider urlComponent) {
        String result;
        ValueStack vs;
        String scheme = urlComponent.getHttpServletRequest().getScheme();
        if (urlComponent.getScheme() != null && (scheme = (vs = ActionContext.getContext().getValueStack()).findString(urlComponent.getScheme())) == null) {
            scheme = urlComponent.getScheme();
        }
        ActionInvocation ai = ActionContext.getContext().getActionInvocation();
        if (urlComponent.getValue() == null && urlComponent.getAction() != null) {
            result = urlComponent.determineActionURL(urlComponent.getAction(), urlComponent.getNamespace(), urlComponent.getMethod(), urlComponent.getHttpServletRequest(), urlComponent.getHttpServletResponse(), urlComponent.getParameters(), scheme, urlComponent.isIncludeContext(), urlComponent.isEncode(), urlComponent.isForceAddSchemeHostAndPort(), urlComponent.isEscapeAmp());
        } else if (urlComponent.getValue() == null && urlComponent.getAction() == null && ai != null) {
            String action = ai.getProxy().getActionName();
            String namespace = ai.getProxy().getNamespace();
            String method = urlComponent.getMethod() != null || !ai.getProxy().isMethodSpecified() ? urlComponent.getMethod() : ai.getProxy().getMethod();
            result = urlComponent.determineActionURL(action, namespace, method, urlComponent.getHttpServletRequest(), urlComponent.getHttpServletResponse(), urlComponent.getParameters(), scheme, urlComponent.isIncludeContext(), urlComponent.isEncode(), urlComponent.isForceAddSchemeHostAndPort(), urlComponent.isEscapeAmp());
        } else {
            String _value = urlComponent.getValue();
            if (_value != null && _value.indexOf(63) > 0) {
                _value = _value.substring(0, _value.indexOf(63));
            }
            result = this.urlHelper.buildUrl(_value, urlComponent.getHttpServletRequest(), urlComponent.getHttpServletResponse(), urlComponent.getParameters(), scheme, urlComponent.isIncludeContext(), urlComponent.isEncode(), urlComponent.isForceAddSchemeHostAndPort(), urlComponent.isEscapeAmp());
        }
        String anchor = urlComponent.getAnchor();
        if (StringUtils.isNotEmpty((CharSequence)anchor)) {
            result = result + '#' + urlComponent.findString(anchor);
        }
        if (urlComponent.isPutInContext()) {
            String var = urlComponent.getVar();
            if (StringUtils.isNotEmpty((CharSequence)var)) {
                urlComponent.putInContext(result);
                urlComponent.getHttpServletRequest().setAttribute(var, (Object)result);
            } else {
                try {
                    writer.write(result);
                }
                catch (IOException e) {
                    throw new StrutsException("IOError: " + e.getMessage(), e);
                }
            }
        } else {
            try {
                writer.write(result);
            }
            catch (IOException e) {
                throw new StrutsException("IOError: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void renderFormUrl(Form formComponent) {
        String action;
        String namespace = formComponent.determineNamespace(formComponent.namespace, formComponent.getStack(), formComponent.request);
        ValueStack vs = ActionContext.getContext().getValueStack();
        String scheme = vs.findString("scheme");
        if (formComponent.action != null) {
            action = formComponent.findString(formComponent.action);
        } else {
            ActionInvocation ai = formComponent.getStack().getActionContext().getActionInvocation();
            if (ai != null) {
                action = ai.getProxy().getActionName();
                namespace = ai.getProxy().getNamespace();
            } else {
                String uri = formComponent.request.getRequestURI();
                action = uri.substring(uri.lastIndexOf(47));
            }
        }
        QueryStringParser.Result queryStringResult = this.queryStringParser.empty();
        if (action != null && action.indexOf(63) > 0) {
            String queryString = action.substring(action.indexOf(63) + 1);
            queryStringResult = this.queryStringParser.parse(queryString);
            action = action.substring(0, action.indexOf(63));
        }
        ActionMapping nameMapping = this.actionMapper.getMappingFromActionName(action);
        String actionName = nameMapping.getName();
        String actionMethod = nameMapping.getMethod();
        ActionConfig actionConfig = formComponent.configuration.getRuntimeConfiguration().getActionConfig(namespace, actionName);
        if (actionConfig != null) {
            ActionMapping mapping = new ActionMapping(actionName, namespace, actionMethod, formComponent.parameters);
            String result = this.urlHelper.buildUrl(formComponent.actionMapper.getUriFromActionMapping(mapping), formComponent.request, formComponent.response, queryStringResult.getQueryParams(), scheme, formComponent.includeContext, true, false, false);
            formComponent.addParameter("action", result);
            formComponent.addParameter("actionName", actionName);
            try {
                Class clazz = formComponent.objectFactory.getClassInstance(actionConfig.getClassName());
                formComponent.addParameter("actionClass", clazz);
            }
            catch (ClassNotFoundException clazz) {
                // empty catch block
            }
            formComponent.addParameter("namespace", namespace);
            if (formComponent.name == null) {
                formComponent.addParameter("name", actionName);
            }
            if (formComponent.getId() == null && actionName != null) {
                String escapedId = formComponent.escape(actionName);
                formComponent.addParameter("id", escapedId);
                formComponent.addParameter("escapedId", escapedId);
            }
        } else if (action != null) {
            if (namespace != null && LOG.isWarnEnabled()) {
                LOG.warn("No configuration found for the specified action: '{}' in namespace: '{}'. Form action defaulting to 'action' attribute's literal value.", (Object)actionName, (Object)namespace);
            }
            String result = this.urlHelper.buildUrl(action, formComponent.request, formComponent.response, queryStringResult.getQueryParams(), scheme, formComponent.includeContext, true);
            formComponent.addParameter("action", result);
            int slash = result.lastIndexOf(47);
            if (slash != -1) {
                formComponent.addParameter("namespace", result.substring(0, slash));
            } else {
                formComponent.addParameter("namespace", "");
            }
            String id = formComponent.getId();
            if (id == null) {
                slash = result.lastIndexOf(47);
                int dot = result.indexOf(46, slash);
                id = dot != -1 ? result.substring(slash + 1, dot) : result.substring(slash + 1);
                String escapedId = formComponent.escape(id);
                formComponent.addParameter("id", escapedId);
                formComponent.addParameter("escapedId", escapedId);
            }
        }
        formComponent.evaluateClientSideJsEnablement(actionName, namespace, actionMethod);
    }

    @Override
    public void beforeRenderUrl(UrlProvider urlComponent) {
        if (urlComponent.getValue() != null) {
            urlComponent.setValue(urlComponent.findString(urlComponent.getValue()));
        }
        try {
            String includeParams;
            String string = includeParams = urlComponent.getUrlIncludeParams() != null ? urlComponent.getUrlIncludeParams().toLowerCase() : "get";
            if (urlComponent.getIncludeParams() != null) {
                includeParams = urlComponent.findString(urlComponent.getIncludeParams());
            }
            if ("none".equalsIgnoreCase(includeParams)) {
                this.mergeRequestParameters(urlComponent.getValue(), urlComponent.getParameters(), Collections.emptyMap());
            } else if ("all".equalsIgnoreCase(includeParams)) {
                this.mergeRequestParameters(urlComponent.getValue(), urlComponent.getParameters(), urlComponent.getHttpServletRequest().getParameterMap());
                this.includeGetParameters(urlComponent);
                this.includeExtraParameters(urlComponent);
            } else if ("get".equalsIgnoreCase(includeParams) || includeParams == null && urlComponent.getValue() == null && urlComponent.getAction() == null) {
                this.includeGetParameters(urlComponent);
                this.includeExtraParameters(urlComponent);
            } else if (includeParams != null) {
                LOG.warn("Unknown value for includeParams parameter to URL tag: {}", (Object)includeParams);
            }
        }
        catch (Exception e) {
            LOG.warn("Unable to put request parameters ({}) into parameter map.", (Object)urlComponent.getHttpServletRequest().getQueryString(), (Object)e);
        }
    }

    private void includeExtraParameters(UrlProvider urlComponent) {
        if (urlComponent.getExtraParameterProvider() != null) {
            this.mergeRequestParameters(urlComponent.getValue(), urlComponent.getParameters(), urlComponent.getExtraParameterProvider().getExtraParameters());
        }
    }

    private void includeGetParameters(UrlProvider urlComponent) {
        String query = this.extractQueryString(urlComponent);
        QueryStringParser.Result result = this.queryStringParser.parse(query);
        this.mergeRequestParameters(urlComponent.getValue(), urlComponent.getParameters(), result.getQueryParams());
        if (!result.getQueryFragment().isEmpty()) {
            urlComponent.setAnchor(result.getQueryFragment());
        }
    }

    private String extractQueryString(UrlProvider urlComponent) {
        int idx;
        String query = urlComponent.getHttpServletRequest().getQueryString();
        if (query == null) {
            query = (String)urlComponent.getHttpServletRequest().getAttribute("javax.servlet.forward.query_string");
        }
        if (query != null && (idx = query.lastIndexOf(35)) != -1) {
            query = query.substring(0, idx);
        }
        return query;
    }

    protected void mergeRequestParameters(String value, Map<String, Object> parameters, Map<String, ?> contextParameters) {
        LinkedHashMap<String, Object> mergedParams = new LinkedHashMap(contextParameters);
        if (StringUtils.contains((CharSequence)value, (CharSequence)"?")) {
            String queryString = value.substring(value.indexOf(63) + 1);
            mergedParams = new LinkedHashMap<String, Object>(this.queryStringParser.parse(queryString).getQueryParams());
            for (Map.Entry<String, ?> entry : contextParameters.entrySet()) {
                if (mergedParams.containsKey(entry.getKey())) continue;
                mergedParams.put(entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry entry : mergedParams.entrySet()) {
            if (parameters.containsKey(entry.getKey())) continue;
            parameters.put((String)entry.getKey(), entry.getValue());
        }
    }
}

