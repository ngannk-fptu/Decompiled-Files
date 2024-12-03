/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.mobile.service;

import com.atlassian.confluence.plugins.mobile.dto.LoginInfoDto;
import com.atlassian.confluence.plugins.mobile.dto.ServerInfoDto;

public interface MobileInfoService {
    public LoginInfoDto getLoginInfo();

    public ServerInfoDto getServerInfo();
}

