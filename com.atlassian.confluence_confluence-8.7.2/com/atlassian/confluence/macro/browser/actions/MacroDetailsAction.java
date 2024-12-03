/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.macro.browser.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.macro.browser.MacroMetadataManager;
import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public final class MacroDetailsAction
extends ConfluenceActionSupport
implements Beanable {
    protected Map<String, Object> bean = new HashMap<String, Object>();
    private MacroMetadataManager macroMetadataManager;
    private String key;
    private String alternateId;
    private List<String> keys;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        if (StringUtils.isNotBlank((CharSequence)this.key)) {
            this.bean.put("details", this.getDetails(this.key));
        }
        if (this.keys != null && !this.keys.isEmpty()) {
            HashMap map = Maps.newHashMap();
            for (String key : this.keys) {
                MacroMetadata details = this.getDetails(key);
                if (details == null) continue;
                map.put(key, details);
            }
            this.bean.put("detailsMap", map);
        }
        return super.execute();
    }

    private MacroMetadata getDetails(String key) {
        return this.macroMetadataManager.getMacroMetadataByNameAndId(key, this.alternateId);
    }

    @Override
    public Map<String, Object> getBean() {
        return this.bean;
    }

    @Override
    public boolean isPermitted() {
        return true;
    }

    public void setMacroMetadataManager(MacroMetadataManager macroMetadataManager) {
        this.macroMetadataManager = macroMetadataManager;
    }

    public void setId(String id) {
        this.key = id;
    }

    public void setIds(List<String> ids) {
        this.keys = ids;
    }

    public void setAlternateId(String alternateId) {
        this.alternateId = alternateId;
    }
}

