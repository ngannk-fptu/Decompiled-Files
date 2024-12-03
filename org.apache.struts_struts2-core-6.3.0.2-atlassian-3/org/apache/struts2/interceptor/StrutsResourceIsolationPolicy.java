/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.logging.log4j.util.Strings
 */
package org.apache.struts2.interceptor;

import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.util.Strings;
import org.apache.struts2.interceptor.ResourceIsolationPolicy;

public final class StrutsResourceIsolationPolicy
implements ResourceIsolationPolicy {
    @Override
    public boolean isRequestAllowed(HttpServletRequest request) {
        String site = request.getHeader("Sec-Fetch-Site");
        if (Strings.isEmpty((CharSequence)site)) {
            return true;
        }
        if ("same-origin".equalsIgnoreCase(site) || "same-site".equalsIgnoreCase(site) || "none".equalsIgnoreCase(site)) {
            return true;
        }
        return this.isAllowedTopLevelNavigation(request);
    }

    private boolean isAllowedTopLevelNavigation(HttpServletRequest request) {
        String mode = request.getHeader("Sec-Fetch-Mode");
        String dest = request.getHeader("Sec-Fetch-Dest");
        boolean isSimpleTopLevelNavigation = "navigate".equalsIgnoreCase(mode) || "GET".equalsIgnoreCase(request.getMethod());
        boolean isNotObjectOrEmbedRequest = !"embed".equalsIgnoreCase(dest) && !"object".equalsIgnoreCase(dest);
        return isSimpleTopLevelNavigation && isNotObjectOrEmbedRequest;
    }
}

