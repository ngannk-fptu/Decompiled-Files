/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro.browser;

import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import java.io.InputStream;

public interface MacroIconManager {
    public String getExternalSmallIconUrl(MacroMetadata var1);

    public InputStream getIconStream(MacroMetadata var1);
}

