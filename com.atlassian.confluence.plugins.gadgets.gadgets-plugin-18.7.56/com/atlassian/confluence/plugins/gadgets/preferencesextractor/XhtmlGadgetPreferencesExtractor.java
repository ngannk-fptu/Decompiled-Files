/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gadgets.preferencesextractor;

import com.atlassian.confluence.plugins.gadgets.BodyToMacroConverterImpl;
import com.atlassian.confluence.plugins.gadgets.preferencesextractor.GadgetPreferencesExtractor;
import java.util.Map;

public class XhtmlGadgetPreferencesExtractor
implements GadgetPreferencesExtractor {
    @Override
    public Map<String, String> getGadgetPreferences(Map<String, String> params, String ignoredBody) {
        String userPreferences = params.get("preferences");
        if (userPreferences != null) {
            params.remove(userPreferences);
        } else {
            userPreferences = "";
        }
        return new BodyToMacroConverterImpl().convertToMap(userPreferences);
    }
}

