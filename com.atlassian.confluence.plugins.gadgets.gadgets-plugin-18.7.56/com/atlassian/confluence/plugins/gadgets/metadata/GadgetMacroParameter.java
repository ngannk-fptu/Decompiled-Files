/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.macro.browser.beans.MacroParameter
 *  com.atlassian.confluence.macro.browser.beans.MacroParameterType
 *  com.atlassian.confluence.util.i18n.Message
 *  com.atlassian.gadgets.spec.DataType
 *  com.atlassian.gadgets.spec.UserPrefSpec
 */
package com.atlassian.confluence.plugins.gadgets.metadata;

import com.atlassian.confluence.macro.browser.beans.MacroParameter;
import com.atlassian.confluence.macro.browser.beans.MacroParameterType;
import com.atlassian.confluence.util.i18n.Message;
import com.atlassian.gadgets.spec.DataType;
import com.atlassian.gadgets.spec.UserPrefSpec;

public class GadgetMacroParameter
extends MacroParameter {
    private final String displayName;

    public boolean isShared() {
        return true;
    }

    public GadgetMacroParameter(UserPrefSpec pref) {
        super("", "gadget", pref.getName(), GadgetMacroParameter.convertToMacroParamType(pref), pref.isRequired(), GadgetMacroParameter.isMultiplePref(pref), pref.getDefaultValue(), false);
        for (String s : pref.getEnumValues().keySet()) {
            this.addEnumValue(s);
        }
        this.displayName = pref.getDisplayName();
    }

    private static boolean isMultiplePref(UserPrefSpec pref) {
        return pref.getDataType() == DataType.LIST;
    }

    private static MacroParameterType convertToMacroParamType(UserPrefSpec pref) {
        switch (pref.getDataType()) {
            case BOOL: {
                return MacroParameterType.BOOLEAN;
            }
            case LIST: {
                return MacroParameterType.STRING;
            }
            case ENUM: {
                return MacroParameterType.ENUM;
            }
            case NUMBER: {
                return MacroParameterType.INT;
            }
            case STRING: {
                return MacroParameterType.STRING;
            }
        }
        throw new IllegalArgumentException(pref.getDataType() + " is not allowed as a datatype");
    }

    public Message getDisplayName() {
        return Message.getInstance((String)this.displayName);
    }

    public Message getDescription() {
        return null;
    }
}

