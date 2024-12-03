/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.config.entities.ExceptionMappingConfig;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.ExceptionHolder;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.HttpParameters;

public class ExceptionMappingInterceptor
extends AbstractInterceptor {
    private static final Logger LOG = LogManager.getLogger(ExceptionMappingInterceptor.class);
    protected Logger categoryLogger;
    protected boolean logEnabled = false;
    protected String logCategory;
    protected String logLevel;

    public boolean isLogEnabled() {
        return this.logEnabled;
    }

    public void setLogEnabled(boolean logEnabled) {
        this.logEnabled = logEnabled;
    }

    public String getLogCategory() {
        return this.logCategory;
    }

    public void setLogCategory(String logCatgory) {
        this.logCategory = logCatgory;
    }

    public String getLogLevel() {
        return this.logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        String result;
        try {
            result = invocation.invoke();
        }
        catch (Exception e) {
            List<ExceptionMappingConfig> exceptionMappings;
            ExceptionMappingConfig mappingConfig;
            if (this.isLogEnabled()) {
                this.handleLogging(e);
            }
            if ((mappingConfig = this.findMappingFromExceptions(exceptionMappings = invocation.getProxy().getConfig().getExceptionMappings(), e)) != null && mappingConfig.getResult() != null) {
                Map<String, String> mappingParams = mappingConfig.getParams();
                HttpParameters parameters = HttpParameters.create(mappingParams).build();
                invocation.getInvocationContext().withParameters(parameters);
                result = mappingConfig.getResult();
                this.publishException(invocation, new ExceptionHolder(e));
            }
            throw e;
        }
        return result;
    }

    protected void handleLogging(Exception e) {
        if (this.logCategory != null) {
            if (this.categoryLogger == null) {
                this.categoryLogger = LogManager.getLogger((String)this.logCategory);
            }
            this.doLog(this.categoryLogger, e);
        } else {
            this.doLog(LOG, e);
        }
    }

    protected void doLog(Logger logger, Exception e) {
        if (this.logLevel == null) {
            logger.debug(e.getMessage(), (Throwable)e);
            return;
        }
        if ("trace".equalsIgnoreCase(this.logLevel)) {
            logger.trace(e.getMessage(), (Throwable)e);
        } else if ("debug".equalsIgnoreCase(this.logLevel)) {
            logger.debug(e.getMessage(), (Throwable)e);
        } else if ("info".equalsIgnoreCase(this.logLevel)) {
            logger.info(e.getMessage(), (Throwable)e);
        } else if ("warn".equalsIgnoreCase(this.logLevel)) {
            logger.warn(e.getMessage(), (Throwable)e);
        } else if ("error".equalsIgnoreCase(this.logLevel)) {
            logger.error(e.getMessage(), (Throwable)e);
        } else if ("fatal".equalsIgnoreCase(this.logLevel)) {
            logger.fatal(e.getMessage(), (Throwable)e);
        } else {
            throw new IllegalArgumentException("LogLevel [" + this.logLevel + "] is not supported");
        }
    }

    protected ExceptionMappingConfig findMappingFromExceptions(List<ExceptionMappingConfig> exceptionMappings, Throwable t) {
        ExceptionMappingConfig config = null;
        if (exceptionMappings != null) {
            int deepest = Integer.MAX_VALUE;
            for (ExceptionMappingConfig exceptionMapping : exceptionMappings) {
                ExceptionMappingConfig exceptionMappingConfig = exceptionMapping;
                int depth = this.getDepth(exceptionMappingConfig.getExceptionClassName(), t);
                if (depth < 0 || depth >= deepest) continue;
                deepest = depth;
                config = exceptionMappingConfig;
            }
        }
        return config;
    }

    public int getDepth(String exceptionMapping, Throwable t) {
        return this.getDepth(exceptionMapping, t.getClass(), 0);
    }

    private int getDepth(String exceptionMapping, Class exceptionClass, int depth) {
        if (exceptionClass.getName().contains(exceptionMapping)) {
            return depth;
        }
        if (exceptionClass.equals(Throwable.class)) {
            return -1;
        }
        return this.getDepth(exceptionMapping, exceptionClass.getSuperclass(), depth + 1);
    }

    protected void publishException(ActionInvocation invocation, ExceptionHolder exceptionHolder) {
        invocation.getStack().push(exceptionHolder);
    }
}

