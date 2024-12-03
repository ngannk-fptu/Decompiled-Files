/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.text.StringEscapeUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionEventListener;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.LocalizedTextProvider;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.UnknownHandlerManager;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.inject.Inject;
import java.io.Serializable;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultActionProxy
implements ActionProxy,
Serializable {
    private static final long serialVersionUID = 3293074152487468527L;
    private static final Logger LOG = LogManager.getLogger(DefaultActionProxy.class);
    protected Configuration configuration;
    protected ActionConfig config;
    protected ActionInvocation invocation;
    protected UnknownHandlerManager unknownHandlerManager;
    protected LocalizedTextProvider localizedTextProvider;
    protected String actionName;
    protected String namespace;
    protected String method;
    protected boolean executeResult;
    protected boolean cleanupContext;
    protected ObjectFactory objectFactory;
    protected ActionEventListener actionEventListener;
    private boolean methodSpecified = true;

    protected DefaultActionProxy(ActionInvocation inv, String namespace, String actionName, String methodName, boolean executeResult, boolean cleanupContext) {
        this.invocation = inv;
        this.cleanupContext = cleanupContext;
        LOG.debug("Creating an DefaultActionProxy for namespace [{}] and action name [{}]", (Object)namespace, (Object)actionName);
        this.actionName = StringEscapeUtils.escapeHtml4((String)actionName);
        this.namespace = namespace;
        this.executeResult = executeResult;
        this.method = StringEscapeUtils.escapeEcmaScript((String)StringEscapeUtils.escapeHtml4((String)methodName));
    }

    @Inject
    public void setObjectFactory(ObjectFactory factory) {
        this.objectFactory = factory;
    }

    @Inject
    public void setConfiguration(Configuration config) {
        this.configuration = config;
    }

    @Inject
    public void setUnknownHandler(UnknownHandlerManager unknownHandlerManager) {
        this.unknownHandlerManager = unknownHandlerManager;
    }

    @Inject(required=false)
    public void setActionEventListener(ActionEventListener listener) {
        this.actionEventListener = listener;
    }

    @Inject
    public void setLocalizedTextProvider(LocalizedTextProvider localizedTextProvider) {
        this.localizedTextProvider = localizedTextProvider;
    }

    @Override
    public Object getAction() {
        return this.invocation.getAction();
    }

    @Override
    public String getActionName() {
        return this.actionName;
    }

    @Override
    public ActionConfig getConfig() {
        return this.config;
    }

    @Override
    public void setExecuteResult(boolean executeResult) {
        this.executeResult = executeResult;
    }

    @Override
    public boolean getExecuteResult() {
        return this.executeResult;
    }

    @Override
    public ActionInvocation getInvocation() {
        return this.invocation;
    }

    @Override
    public String getNamespace() {
        return this.namespace;
    }

    @Override
    public String execute() throws Exception {
        ActionContext nestedContext = ActionContext.getContext();
        ActionContext.bind(this.invocation.getInvocationContext());
        try {
            String string = this.invocation.invoke();
            return string;
        }
        finally {
            if (this.cleanupContext) {
                ActionContext.bind(nestedContext);
            }
        }
    }

    @Override
    public String getMethod() {
        return this.method;
    }

    private void resolveMethod() {
        if (StringUtils.isEmpty((CharSequence)this.method)) {
            this.method = this.config.getMethodName();
            if (StringUtils.isEmpty((CharSequence)this.method)) {
                this.method = "execute";
            }
            this.methodSpecified = false;
        }
    }

    protected void prepare() {
        this.config = this.configuration.getRuntimeConfiguration().getActionConfig(this.namespace, this.actionName);
        if (this.config == null && this.unknownHandlerManager.hasUnknownHandlers()) {
            this.config = this.unknownHandlerManager.handleUnknownAction(this.namespace, this.actionName);
        }
        if (this.config == null) {
            throw new ConfigurationException(this.getErrorMessage());
        }
        this.resolveMethod();
        if (!this.config.isAllowedMethod(this.method)) {
            throw new ConfigurationException(this.prepareNotAllowedErrorMessage());
        }
        this.invocation.init(this);
    }

    protected String prepareNotAllowedErrorMessage() {
        return this.localizedTextProvider.findDefaultText("struts.exception.method-not-allowed", Locale.getDefault(), new String[]{this.method, this.actionName});
    }

    protected String getErrorMessage() {
        if (this.namespace != null && this.namespace.trim().length() > 0) {
            return this.localizedTextProvider.findDefaultText("xwork.exception.missing-package-action", Locale.getDefault(), new String[]{this.namespace, this.actionName});
        }
        return this.localizedTextProvider.findDefaultText("xwork.exception.missing-action", Locale.getDefault(), new String[]{this.actionName});
    }

    @Override
    public boolean isMethodSpecified() {
        return this.methodSpecified;
    }
}

