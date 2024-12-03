/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.seraph.controller;

import com.atlassian.seraph.config.SecurityConfig;
import com.atlassian.seraph.controller.SecurityController;
import java.util.Map;

public class NullSecurityController
implements SecurityController {
    @Override
    public boolean isSecurityEnabled() {
        return true;
    }

    @Override
    public void init(Map<String, String> params, SecurityConfig config) {
    }
}

