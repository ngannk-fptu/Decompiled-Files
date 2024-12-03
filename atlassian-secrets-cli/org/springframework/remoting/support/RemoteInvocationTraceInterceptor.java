/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.remoting.support;

import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ClassUtils;

public class RemoteInvocationTraceInterceptor
implements MethodInterceptor {
    protected static final Log logger = LogFactory.getLog(RemoteInvocationTraceInterceptor.class);
    private final String exporterNameClause;

    public RemoteInvocationTraceInterceptor() {
        this.exporterNameClause = "";
    }

    public RemoteInvocationTraceInterceptor(String exporterName) {
        this.exporterNameClause = exporterName + " ";
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        if (logger.isDebugEnabled()) {
            logger.debug("Incoming " + this.exporterNameClause + "remote call: " + ClassUtils.getQualifiedMethodName(method));
        }
        try {
            Object retVal = invocation.proceed();
            if (logger.isDebugEnabled()) {
                logger.debug("Finished processing of " + this.exporterNameClause + "remote call: " + ClassUtils.getQualifiedMethodName(method));
            }
            return retVal;
        }
        catch (Throwable ex) {
            if (ex instanceof RuntimeException || ex instanceof Error) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Processing of " + this.exporterNameClause + "remote call resulted in fatal exception: " + ClassUtils.getQualifiedMethodName(method), ex);
                }
            } else if (logger.isInfoEnabled()) {
                logger.info("Processing of " + this.exporterNameClause + "remote call resulted in exception: " + ClassUtils.getQualifiedMethodName(method), ex);
            }
            throw ex;
        }
    }
}

