/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.TextParseUtil;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CoopInterceptor
extends AbstractInterceptor
implements PreResultListener {
    private static final Logger LOG = LogManager.getLogger(CoopInterceptor.class);
    private static final String SAME_ORIGIN = "same-origin";
    private static final String SAME_ORIGIN_ALLOW_POPUPS = "same-origin-allow-popups";
    private static final String UNSAFE_NONE = "unsafe-none";
    private static final String COOP_HEADER = "Cross-Origin-Opener-Policy";
    private final Set<String> exemptedPaths = new HashSet<String>();
    private String mode = "same-origin";

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        invocation.addPreResultListener(this);
        return invocation.invoke();
    }

    @Override
    public void beforeResult(ActionInvocation invocation, String resultCode) {
        HttpServletRequest request = invocation.getInvocationContext().getServletRequest();
        String path = request.getContextPath();
        if (this.isExempted(path)) {
            LOG.debug("Skipping COOP header for exempted path {}", (Object)path);
        } else {
            LOG.trace("Applying COOP header: {} with value: {}", (Object)COOP_HEADER, (Object)this.mode);
            HttpServletResponse response = invocation.getInvocationContext().getServletResponse();
            response.setHeader(COOP_HEADER, this.mode);
        }
    }

    public boolean isExempted(String path) {
        return this.exemptedPaths.contains(path);
    }

    public void setExemptedPaths(String paths) {
        this.exemptedPaths.addAll(TextParseUtil.commaDelimitedStringToSet(paths));
    }

    public void setMode(String mode) {
        if (!(mode.equals(SAME_ORIGIN) || mode.equals(SAME_ORIGIN_ALLOW_POPUPS) || mode.equals(UNSAFE_NONE))) {
            throw new IllegalArgumentException(String.format("Mode '%s' not recognized!", mode));
        }
        this.mode = mode;
    }
}

