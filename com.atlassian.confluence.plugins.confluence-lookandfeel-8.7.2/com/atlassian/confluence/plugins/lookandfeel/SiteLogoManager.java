/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.lookandfeel;

import com.atlassian.confluence.plugins.lookandfeel.SiteLogo;
import java.io.File;
import java.io.IOException;

public interface SiteLogoManager {
    public void uploadLogo(File var1, String var2) throws IOException;

    public String getSiteLogoUrl();

    public SiteLogo getCurrent();

    public void resetToDefault();

    public boolean useCustomLogo();
}

