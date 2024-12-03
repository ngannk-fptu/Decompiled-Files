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

public class CoepInterceptor
extends AbstractInterceptor
implements PreResultListener {
    private static final Logger LOG = LogManager.getLogger(CoepInterceptor.class);
    private static final String REQUIRE_COEP_HEADER = "require-corp";
    private static final String COEP_ENFORCING_HEADER = "Cross-Origin-Embedder-Policy";
    private static final String COEP_REPORT_HEADER = "Cross-Origin-Embedder-Policy-Report-Only";
    private final Set<String> exemptedPaths = new HashSet<String>();
    private String header = "Cross-Origin-Embedder-Policy";

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        invocation.addPreResultListener(this);
        return invocation.invoke();
    }

    @Override
    public void beforeResult(ActionInvocation invocation, String resultCode) {
        HttpServletRequest req = invocation.getInvocationContext().getServletRequest();
        String path = req.getContextPath();
        if (this.exemptedPaths.contains(path)) {
            LOG.debug("Skipping COEP header for exempted path: {}", (Object)path);
        } else {
            LOG.trace("Applying COEP header: {} with value: {}", (Object)this.header, (Object)REQUIRE_COEP_HEADER);
            HttpServletResponse response = invocation.getInvocationContext().getServletResponse();
            response.setHeader(this.header, REQUIRE_COEP_HEADER);
        }
    }

    public void setExemptedPaths(String paths) {
        this.exemptedPaths.addAll(TextParseUtil.commaDelimitedStringToSet(paths));
    }

    public void setEnforcingMode(String mode) {
        boolean enforcingMode = Boolean.parseBoolean(mode);
        this.header = enforcingMode ? COEP_ENFORCING_HEADER : COEP_REPORT_HEADER;
    }
}

