/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.GeneralUtil
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.service.impl;

import com.atlassian.confluence.plugin.copyspace.service.ConfluenceUtilService;
import com.atlassian.confluence.util.GeneralUtil;
import org.springframework.stereotype.Component;

@Component(value="confluenceUtilServiceImpl")
public class ConfluenceUtilServiceImpl
implements ConfluenceUtilService {
    @Override
    public boolean isLicenseExpired() {
        return GeneralUtil.isLicenseExpired();
    }
}

