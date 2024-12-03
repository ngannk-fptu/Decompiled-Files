/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gadgets.preferencesextractor;

import com.atlassian.confluence.plugins.gadgets.BodyToMacroConverterImpl;
import com.atlassian.confluence.plugins.gadgets.preferencesextractor.GadgetPreferencesExtractor;
import java.util.Map;

public class WikiMarkupGadgetPreferencesExtractor
implements GadgetPreferencesExtractor {
    @Override
    public Map<String, String> getGadgetPreferences(Map<String, String> params, String body) {
        return new BodyToMacroConverterImpl().convertToMap(body);
    }
}

