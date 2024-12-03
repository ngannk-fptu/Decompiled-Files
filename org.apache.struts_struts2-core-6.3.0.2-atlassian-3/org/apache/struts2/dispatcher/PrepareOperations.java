/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.dispatcher;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.RequestUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsException;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

public class PrepareOperations {
    private static final Logger LOG = LogManager.getLogger(PrepareOperations.class);
    private static final ThreadLocal<Boolean> devModeOverride = new InheritableThreadLocal<Boolean>();
    private final Dispatcher dispatcher;
    private static final String STRUTS_ACTION_MAPPING_KEY = "struts.actionMapping";
    private static final String NO_ACTION_MAPPING = "noActionMapping";
    private static final String PREPARE_COUNTER = "__prepare_recursion_counter";
    private static final String WRAP_COUNTER = "__wrap_recursion_counter";

    public PrepareOperations(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void trackRecursion(HttpServletRequest request) {
        PrepareOperations.incrementRecursionCounter(request, PREPARE_COUNTER);
    }

    public void cleanupRequest(HttpServletRequest request) {
        PrepareOperations.decrementRecursionCounter(request, PREPARE_COUNTER, () -> {
            try {
                this.dispatcher.cleanUpRequest(request);
            }
            finally {
                ActionContext.clear();
                Dispatcher.clearInstance();
                devModeOverride.remove();
            }
        });
    }

    public ActionContext createActionContext(HttpServletRequest request, HttpServletResponse response) {
        ActionContext ctx;
        ActionContext oldContext = ActionContext.getContext();
        if (oldContext != null) {
            ctx = ActionContext.of(new HashMap<String, Object>(oldContext.getContextMap())).bind();
        } else {
            ctx = ServletActionContext.getActionContext(request);
            if (ctx == null) {
                ValueStack stack = this.dispatcher.getValueStackFactory().createValueStack();
                stack.getContext().putAll(this.dispatcher.createContextMap(request, response, null));
                ctx = ActionContext.of(stack.getContext()).bind();
            }
        }
        return ctx;
    }

    public void assignDispatcherToThread() {
        Dispatcher.setInstance(this.dispatcher);
    }

    public void setEncodingAndLocale(HttpServletRequest request, HttpServletResponse response) {
        this.dispatcher.prepare(request, response);
    }

    public HttpServletRequest wrapRequest(HttpServletRequest request) throws ServletException {
        PrepareOperations.incrementRecursionCounter(request, WRAP_COUNTER);
        try {
            request = this.dispatcher.wrapRequest(request);
            ServletActionContext.setRequest(request);
        }
        catch (IOException e) {
            throw new ServletException("Could not wrap servlet request with MultipartRequestWrapper!", (Throwable)e);
        }
        return request;
    }

    public void cleanupWrappedRequest(HttpServletRequest request) {
        PrepareOperations.decrementRecursionCounter(request, WRAP_COUNTER, () -> this.dispatcher.cleanUpRequest(request));
    }

    public ActionMapping findActionMapping(HttpServletRequest request, HttpServletResponse response) {
        return this.findActionMapping(request, response, false);
    }

    public ActionMapping findActionMapping(HttpServletRequest request, HttpServletResponse response, boolean forceLookup) {
        ActionMapping mapping;
        block6: {
            mapping = null;
            Object mappingAttr = request.getAttribute(STRUTS_ACTION_MAPPING_KEY);
            if (mappingAttr == null || forceLookup) {
                try {
                    mapping = this.dispatcher.getActionMapper().getMapping(request, this.dispatcher.getConfigurationManager());
                    if (mapping != null) {
                        request.setAttribute(STRUTS_ACTION_MAPPING_KEY, (Object)mapping);
                        break block6;
                    }
                    request.setAttribute(STRUTS_ACTION_MAPPING_KEY, (Object)NO_ACTION_MAPPING);
                }
                catch (Exception ex) {
                    if (this.dispatcher.isHandleException() || this.dispatcher.isDevMode()) {
                        this.dispatcher.sendError(request, response, 500, ex);
                    }
                    break block6;
                }
            }
            if (!NO_ACTION_MAPPING.equals(mappingAttr)) {
                mapping = (ActionMapping)mappingAttr;
            }
        }
        return mapping;
    }

    public void cleanupDispatcher() {
        if (this.dispatcher == null) {
            throw new StrutsException("Something is seriously wrong, Dispatcher is not initialized (null) ");
        }
        try {
            this.dispatcher.cleanup();
        }
        finally {
            ActionContext.clear();
        }
    }

    public boolean isUrlExcluded(HttpServletRequest request, List<Pattern> excludedPatterns) {
        if (excludedPatterns == null) {
            return false;
        }
        String uri = RequestUtils.getUri(request);
        for (Pattern pattern : excludedPatterns) {
            if (!pattern.matcher(uri).matches()) continue;
            return true;
        }
        return false;
    }

    public static void overrideDevMode(boolean devMode) {
        devModeOverride.set(devMode);
    }

    public static Boolean getDevModeOverride() {
        return devModeOverride.get();
    }

    public static void clearDevModeOverride() {
        devModeOverride.remove();
    }

    public static void incrementRecursionCounter(HttpServletRequest request, String attributeName) {
        Integer setCounter = (Integer)request.getAttribute(attributeName);
        if (setCounter == null) {
            setCounter = 0;
        }
        setCounter = setCounter + 1;
        request.setAttribute(attributeName, (Object)setCounter);
    }

    public static void decrementRecursionCounter(HttpServletRequest request, String attributeName, Runnable runnable) {
        Integer setCounter = (Integer)request.getAttribute(attributeName);
        if (setCounter != null) {
            setCounter = setCounter - 1;
            request.setAttribute(attributeName, (Object)setCounter);
        }
        if ((setCounter == null || setCounter == 0) && runnable != null) {
            runnable.run();
        }
    }
}

