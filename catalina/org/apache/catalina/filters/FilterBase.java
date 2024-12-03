/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Filter
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  org.apache.juli.logging.Log
 *  org.apache.tomcat.util.IntrospectionUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.filters;

import java.util.Enumeration;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.res.StringManager;

public abstract class FilterBase
implements Filter {
    protected static final StringManager sm = StringManager.getManager(FilterBase.class);

    protected abstract Log getLogger();

    public void init(FilterConfig filterConfig) throws ServletException {
        Enumeration paramNames = filterConfig.getInitParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = (String)paramNames.nextElement();
            if (IntrospectionUtils.setProperty((Object)this, (String)paramName, (String)filterConfig.getInitParameter(paramName))) continue;
            String msg = sm.getString("filterbase.noSuchProperty", new Object[]{paramName, this.getClass().getName()});
            if (this.isConfigProblemFatal()) {
                throw new ServletException(msg);
            }
            this.getLogger().warn((Object)msg);
        }
    }

    protected boolean isConfigProblemFatal() {
        return false;
    }
}

