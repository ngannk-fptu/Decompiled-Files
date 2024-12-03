/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.MessageStoreInterceptor;
import org.apache.struts2.result.Redirectable;

public class MessageStorePreResultListener
implements PreResultListener {
    private static final Logger LOG = LogManager.getLogger(MessageStorePreResultListener.class);
    protected MessageStoreInterceptor interceptor;

    public void init(MessageStoreInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void beforeResult(ActionInvocation invocation, String resultCode) {
        boolean isCommitted = this.isCommitted();
        if (isCommitted) {
            LOG.trace("Response was already committed, cannot store messages!");
            return;
        }
        boolean isInvalidated = this.isInvalidated();
        if (isInvalidated) {
            LOG.trace("Session was invalidated or never created, cannot store messages!");
            return;
        }
        Map<String, Object> session = invocation.getInvocationContext().getSession();
        if (session == null) {
            LOG.trace("Could not store action [{}] error/messages into session, because session hasn't been opened yet.", invocation.getAction());
            return;
        }
        String reqOperationMode = this.interceptor.getRequestOperationMode(invocation);
        boolean isRedirect = this.isRedirect(invocation, resultCode);
        if ("STORE".equalsIgnoreCase(reqOperationMode) || "STORE".equalsIgnoreCase(this.interceptor.getOperationModel()) || "AUTOMATIC".equalsIgnoreCase(this.interceptor.getOperationModel()) && isRedirect) {
            Object action = invocation.getAction();
            if (action instanceof ValidationAware) {
                LOG.debug("Storing action [{}] error/messages into session ", action);
                ValidationAware validationAwareAction = (ValidationAware)action;
                session.put("__MessageStoreInterceptor_ActionErrors_SessionKey", validationAwareAction.getActionErrors());
                session.put("__MessageStoreInterceptor_ActionMessages_SessionKey", validationAwareAction.getActionMessages());
                session.put("__MessageStoreInterceptor_FieldErrors_SessionKey", validationAwareAction.getFieldErrors());
            } else {
                LOG.debug("Action [{}] is not ValidationAware, no message / error that are storeable", action);
            }
        }
    }

    protected boolean isCommitted() {
        return ServletActionContext.getResponse().isCommitted();
    }

    protected boolean isInvalidated() {
        return ServletActionContext.getRequest().getSession(false) == null;
    }

    protected boolean isRedirect(ActionInvocation invocation, String resultCode) {
        boolean isRedirect = false;
        try {
            ResultConfig resultConfig = invocation.getProxy().getConfig().getResults().get(resultCode);
            if (resultConfig != null) {
                isRedirect = Redirectable.class.isAssignableFrom(Class.forName(resultConfig.getClassName()));
            }
        }
        catch (Exception e) {
            LOG.warn("Cannot read result!", (Throwable)e);
        }
        return isRedirect;
    }
}

