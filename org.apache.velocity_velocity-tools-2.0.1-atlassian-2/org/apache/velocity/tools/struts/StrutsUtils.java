/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 *  org.apache.struts.action.ActionForm
 *  org.apache.struts.action.ActionMessage
 *  org.apache.struts.action.ActionMessages
 *  org.apache.struts.config.ActionConfig
 *  org.apache.struts.config.ForwardConfig
 *  org.apache.struts.config.ModuleConfig
 *  org.apache.struts.taglib.TagUtils
 *  org.apache.struts.util.MessageResources
 *  org.apache.struts.util.ModuleUtils
 *  org.apache.struts.util.RequestUtils
 */
package org.apache.velocity.tools.struts;

import java.util.Iterator;
import java.util.Locale;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.config.ActionConfig;
import org.apache.struts.config.ForwardConfig;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.ModuleUtils;
import org.apache.struts.util.RequestUtils;

public class StrutsUtils {
    public static final StrutsUtils INSTANCE = new StrutsUtils();

    private StrutsUtils() {
    }

    public StrutsUtils getInstance() {
        return INSTANCE;
    }

    public static MessageResources getMessageResources(HttpServletRequest request, ServletContext app) {
        return StrutsUtils.getMessageResources(request, app, null);
    }

    public static MessageResources getMessageResources(HttpServletRequest request, ServletContext app, String bundle) {
        MessageResources resources;
        ModuleConfig moduleConfig = ModuleUtils.getInstance().getModuleConfig(request, app);
        if (bundle == null) {
            bundle = "org.apache.struts.action.MESSAGE";
        }
        if ((resources = (MessageResources)request.getAttribute(bundle + moduleConfig.getPrefix())) == null) {
            resources = (MessageResources)app.getAttribute(bundle + moduleConfig.getPrefix());
        }
        return resources;
    }

    public static ModuleConfig selectModule(String urlPath, ServletContext app) {
        String prefix = ModuleUtils.getInstance().getModuleName(urlPath, app);
        ModuleConfig config = (ModuleConfig)app.getAttribute("org.apache.struts.action.MODULE" + prefix);
        return config;
    }

    public static Locale getLocale(HttpServletRequest request, HttpSession session) {
        Locale locale = null;
        if (session != null) {
            locale = (Locale)session.getAttribute("org.apache.struts.action.LOCALE");
        }
        if (locale == null) {
            locale = request.getLocale();
        }
        return locale;
    }

    public static String getToken(HttpSession session) {
        if (session == null) {
            return null;
        }
        return (String)session.getAttribute("org.apache.struts.action.TOKEN");
    }

    public static ActionMessages getErrors(HttpServletRequest request) {
        HttpSession session;
        ActionMessages errors = (ActionMessages)request.getAttribute("org.apache.struts.action.ERROR");
        if ((errors == null || errors.isEmpty()) && (session = request.getSession(false)) != null) {
            errors = (ActionMessages)session.getAttribute("org.apache.struts.action.ERROR");
        }
        return errors;
    }

    public static ActionMessages getMessages(HttpServletRequest request) {
        HttpSession session;
        ActionMessages messages = (ActionMessages)request.getAttribute("org.apache.struts.action.ACTION_MESSAGE");
        if ((messages == null || messages.isEmpty()) && (session = request.getSession(false)) != null) {
            messages = (ActionMessages)session.getAttribute("org.apache.struts.action.ACTION_MESSAGE");
        }
        return messages;
    }

    public static ActionForm getActionForm(HttpServletRequest request, HttpSession session) {
        ActionConfig mapping = (ActionConfig)request.getAttribute("org.apache.struts.action.mapping.instance");
        if (mapping == null) {
            return null;
        }
        String attribute = mapping.getAttribute();
        if (attribute == null) {
            return null;
        }
        if ("request".equals(mapping.getScope())) {
            return (ActionForm)request.getAttribute(attribute);
        }
        if (session != null) {
            return (ActionForm)session.getAttribute(attribute);
        }
        return null;
    }

    public static String getActionFormName(HttpServletRequest request, HttpSession session) {
        ActionConfig mapping = (ActionConfig)request.getAttribute("org.apache.struts.action.mapping.instance");
        if (mapping == null) {
            return null;
        }
        return mapping.getAttribute();
    }

    public static String getActionMappingName(String action) {
        String value = action;
        int question = action.indexOf(63);
        if (question >= 0) {
            value = value.substring(0, question);
        }
        int slash = value.lastIndexOf(47);
        int period = value.lastIndexOf(46);
        if (period >= 0 && period > slash) {
            value = value.substring(0, period);
        }
        return value.startsWith("/") ? value : "/" + value;
    }

    public static String getActionMappingURL(ServletContext application, HttpServletRequest request, String action) {
        String servletMapping;
        StringBuilder value = new StringBuilder(request.getContextPath());
        ModuleConfig config = (ModuleConfig)request.getAttribute("org.apache.struts.action.MODULE");
        if (config != null) {
            value.append(config.getPrefix());
        }
        if ((servletMapping = (String)application.getAttribute("org.apache.struts.action.SERVLET_MAPPING")) != null) {
            String queryString = null;
            int question = action.indexOf(63);
            if (question >= 0) {
                queryString = action.substring(question);
            }
            String actionMapping = TagUtils.getInstance().getActionMappingName(action);
            if (servletMapping.startsWith("*.")) {
                value.append(actionMapping);
                value.append(servletMapping.substring(1));
            } else if (servletMapping.endsWith("/*")) {
                value.append(servletMapping.substring(0, servletMapping.length() - 2));
                value.append(actionMapping);
            }
            if (queryString != null) {
                value.append(queryString);
            }
        } else {
            if (!action.startsWith("/")) {
                value.append("/");
            }
            value.append(action);
        }
        return value.toString();
    }

    public static String getForwardURL(HttpServletRequest request, ServletContext app, String forward) {
        ModuleConfig moduleConfig = ModuleUtils.getInstance().getModuleConfig(request, app);
        ActionConfig actionConfig = (ActionConfig)request.getAttribute("org.apache.struts.action.mapping.instance");
        ForwardConfig fc = null;
        if (actionConfig != null) {
            fc = actionConfig.findForwardConfig(forward);
        }
        if (fc == null && (fc = moduleConfig.findForwardConfig(forward)) == null) {
            return null;
        }
        StringBuilder url = new StringBuilder();
        if (fc.getPath().startsWith("/")) {
            url.append(request.getContextPath());
            url.append(RequestUtils.forwardURL((HttpServletRequest)request, (ForwardConfig)fc, (ModuleConfig)moduleConfig));
        } else {
            url.append(fc.getPath());
        }
        return url.toString();
    }

    public static String errorMarkup(String property, HttpServletRequest request, HttpSession session, ServletContext application) {
        return StrutsUtils.errorMarkup(property, null, request, session, application);
    }

    public static String errorMarkup(String property, String bundle, HttpServletRequest request, HttpSession session, ServletContext application) {
        ActionMessages errors = StrutsUtils.getErrors(request);
        if (errors == null) {
            return "";
        }
        Iterator reports = null;
        reports = property == null ? errors.get() : errors.get(property);
        if (!reports.hasNext()) {
            return "";
        }
        StringBuilder results = new StringBuilder();
        String header = null;
        String footer = null;
        String prefix = null;
        String suffix = null;
        Locale locale = StrutsUtils.getLocale(request, session);
        MessageResources resources = StrutsUtils.getMessageResources(request, application, bundle);
        if (resources != null) {
            header = resources.getMessage(locale, "errors.header");
            footer = resources.getMessage(locale, "errors.footer");
            prefix = resources.getMessage(locale, "errors.prefix");
            suffix = resources.getMessage(locale, "errors.suffix");
        }
        if (header == null) {
            header = "errors.header";
        }
        if (footer == null) {
            footer = "errors.footer";
        }
        if (prefix == null) {
            prefix = "";
        }
        if (suffix == null) {
            suffix = "";
        }
        results.append(header);
        results.append("\r\n");
        while (reports.hasNext()) {
            String message = null;
            ActionMessage report = (ActionMessage)reports.next();
            if (resources != null && report.isResource()) {
                message = resources.getMessage(locale, report.getKey(), report.getValues());
            }
            results.append(prefix);
            if (message != null) {
                results.append(message);
            } else {
                results.append(report.getKey());
            }
            results.append(suffix);
            results.append("\r\n");
        }
        results.append(footer);
        results.append("\r\n");
        return results.toString();
    }
}

