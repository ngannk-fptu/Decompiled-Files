/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.macro.count;

import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsAccumulationKey;
import com.atlassian.confluence.macro.GenericVelocityMacro;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.google.common.base.Preconditions;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MacroMetricsKey
implements MarshallerMetricsAccumulationKey {
    private final String macroType;

    public static @NonNull MacroMetricsKey createFrom(MacroDefinition macroDefinition, @Nullable Macro macro) {
        return new MacroMetricsKey(MacroMetricsKey.buildMacroType(macroDefinition, macro == null ? null : macro.getClass()));
    }

    private MacroMetricsKey(String macroType) {
        this.macroType = (String)Preconditions.checkNotNull((Object)macroType);
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MacroMetricsKey that = (MacroMetricsKey)o;
        return this.macroType.equals(that.macroType);
    }

    public String getMacroType() {
        return this.macroType;
    }

    public int hashCode() {
        return this.macroType.hashCode();
    }

    private static String buildMacroType(MacroDefinition macroDefinition, @Nullable Class<? extends Macro> macroClass) {
        if (macroClass == null) {
            return "unknown";
        }
        if (GenericVelocityMacro.class.isAssignableFrom(macroClass)) {
            return "user-macro";
        }
        String macroName = macroDefinition.getName();
        if (StringUtils.isBlank((CharSequence)macroName)) {
            return "unknown";
        }
        switch (macroName) {
            case "gadget": {
                return MacroMetricsKey.getGadgetMacroType(macroDefinition);
            }
            case "jira": {
                return MacroMetricsKey.getJiraMacroType(macroDefinition);
            }
        }
        return macroName;
    }

    private static String getGadgetMacroType(MacroDefinition macroDefinition) {
        String gadgetUrl = macroDefinition.getParameter("url");
        if (StringUtils.isNotBlank((CharSequence)gadgetUrl)) {
            return Stream.of("rest/gadgets/1.0/g/", "/rest/gadgets/1.0/g/").filter(gadgetUrl::contains).findFirst().map(recognizedUrlPrefix -> {
                int chopIndex = gadgetUrl.indexOf((String)recognizedUrlPrefix) + recognizedUrlPrefix.length();
                String gadgetUrlWithoutPrefix = gadgetUrl.substring(chopIndex);
                return "gadget.url." + StringUtils.split((String)gadgetUrlWithoutPrefix, (char)'/')[0];
            }).orElse("gadget.url.other");
        }
        return "gadget.other";
    }

    private static String getJiraMacroType(MacroDefinition macroDefinition) {
        if (StringUtils.isNotBlank((CharSequence)macroDefinition.getParameter("key"))) {
            return "jira.key";
        }
        if (StringUtils.isNotBlank((CharSequence)macroDefinition.getParameter("url"))) {
            return "jira.url";
        }
        if (StringUtils.isNotBlank((CharSequence)macroDefinition.getParameter("jqlQuery"))) {
            return "jira.jqlQuery";
        }
        return "jira";
    }

    @Override
    public String getAccumulationKeyAsString() {
        return this.getMacroType();
    }
}

