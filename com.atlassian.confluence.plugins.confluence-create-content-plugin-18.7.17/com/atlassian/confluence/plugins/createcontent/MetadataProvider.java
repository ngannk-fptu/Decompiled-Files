/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.xwork.FlashScope
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.ContextProvider
 */
package com.atlassian.confluence.plugins.createcontent;

import com.atlassian.confluence.plugins.createcontent.ContentBlueprintManager;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.xwork.FlashScope;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import java.util.Map;
import java.util.UUID;

public class MetadataProvider
implements ContextProvider {
    private ContentBlueprintManager contentBlueprintManager;

    public MetadataProvider(ContentBlueprintManager contentBlueprintManager) {
        this.contentBlueprintManager = contentBlueprintManager;
    }

    public void init(Map<String, String> stringStringMap) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> stringObjectMap) {
        UUID id = (UUID)FlashScope.get((String)"firstBlueprintForUser");
        String indexKey = ((ContentBlueprint)this.contentBlueprintManager.getById(id)).getIndexKey();
        stringObjectMap.put("indexKey", indexKey);
        return stringObjectMap;
    }
}

