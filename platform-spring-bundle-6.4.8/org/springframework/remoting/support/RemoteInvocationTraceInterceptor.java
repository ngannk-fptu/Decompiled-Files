/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.remoting.support;

import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
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
    @Nullable
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("Incoming " + this.exporterNameClause + "remote call: " + ClassUtils.getQualifiedMethodName(method)));
        }
        try {
            Object retVal = invocation.proceed();
            if (logger.isDebugEnabled()) {
                logger.debug((Object)("Finished processing of " + this.exporterNameClause + "remote call: " + ClassUtils.getQualifiedMethodName(method)));
            }
            return retVal;
        }
        catch (Throwable ex) {
            if (ex instanceof RuntimeException || ex instanceof Error) {
                if (logger.isWarnEnabled()) {
                    logger.warn((Object)("Processing of " + this.exporterNameClause + "remote call resulted in fatal exception: " + ClassUtils.getQualifiedMethodName(method)), ex);
                }
            } else if (logger.isInfoEnabled()) {
                logger.info((Object)("Processing of " + this.exporterNameClause + "remote call resulted in exception: " + ClassUtils.getQualifiedMethodName(method)), ex);
            }
            throw ex;
        }
    }
}

