/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ClassUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.license.exception.handler;

import com.atlassian.confluence.license.exception.handler.LicenseExceptionHandler;
import com.atlassian.confluence.util.i18n.I18NBean;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompositeLicenseExceptionHandler
implements LicenseExceptionHandler<Exception> {
    public static final Logger log = LoggerFactory.getLogger(CompositeLicenseExceptionHandler.class);
    private I18NBean i18NBean;
    private Map<Class<? extends Exception>, LicenseExceptionHandler<? extends Exception>> handlers;

    public CompositeLicenseExceptionHandler(I18NBean i18NBean, Map<Class<? extends Exception>, LicenseExceptionHandler<? extends Exception>> handlers) {
        this.i18NBean = i18NBean;
        this.handlers = new TreeMap<Class<? extends Exception>, LicenseExceptionHandler<? extends Exception>>((o1, o2) -> {
            if (ClassUtils.isAssignable((Class)o1, (Class)o2, (boolean)true)) {
                return -1;
            }
            if (ClassUtils.isAssignable((Class)o2, (Class)o1, (boolean)true)) {
                return 1;
            }
            return o1.getName().compareTo(o2.getName());
        });
        this.handlers.putAll(handlers);
    }

    @Override
    public String handle(Exception exception) {
        for (Map.Entry<Class<? extends Exception>, LicenseExceptionHandler<? extends Exception>> entry : this.handlers.entrySet()) {
            String message;
            if (!ClassUtils.isAssignable(exception.getClass(), entry.getKey(), (boolean)true) || (message = entry.getValue().handle(exception)) == null) continue;
            if (log.isDebugEnabled()) {
                log.debug("Handled exception, see cause.", (Throwable)exception);
            }
            return message;
        }
        if (log.isWarnEnabled()) {
            log.warn("Unhandled exception, see cause.", (Throwable)exception);
        }
        return this.i18NBean.getText("error.license.not.valid");
    }
}

