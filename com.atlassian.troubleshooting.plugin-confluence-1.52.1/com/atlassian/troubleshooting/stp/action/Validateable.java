/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp.action;

import com.atlassian.troubleshooting.stp.ValidationLog;
import com.atlassian.troubleshooting.stp.servlet.SafeHttpServletRequest;
import java.util.Map;

public interface Validateable {
    public void validate(Map<String, Object> var1, SafeHttpServletRequest var2, ValidationLog var3);
}

