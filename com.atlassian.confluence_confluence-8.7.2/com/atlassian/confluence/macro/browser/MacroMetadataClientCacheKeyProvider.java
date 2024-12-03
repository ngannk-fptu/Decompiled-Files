/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.json.marshal.wrapped.JsonableString
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 */
package com.atlassian.confluence.macro.browser;

import com.atlassian.confluence.macro.browser.MacroMetadataClientCacheKeyManager;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.json.marshal.wrapped.JsonableString;
import com.atlassian.webresource.api.data.WebResourceDataProvider;

public class MacroMetadataClientCacheKeyProvider
implements WebResourceDataProvider {
    private final MacroMetadataClientCacheKeyManager macroMetadataClientCacheKeyManager;

    public MacroMetadataClientCacheKeyProvider(MacroMetadataClientCacheKeyManager macroMetadataClientCacheKeyManager) {
        this.macroMetadataClientCacheKeyManager = macroMetadataClientCacheKeyManager;
    }

    public Jsonable get() {
        return new JsonableString(this.macroMetadataClientCacheKeyManager.getKey());
    }
}

