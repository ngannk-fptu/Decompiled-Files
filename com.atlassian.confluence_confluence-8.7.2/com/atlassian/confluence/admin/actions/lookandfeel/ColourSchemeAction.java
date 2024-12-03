/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.confluence.util.misc.CssColourValidator
 *  com.atlassian.xwork.ParameterSafe
 *  com.opensymphony.xwork2.util.Element
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.admin.actions.lookandfeel;

import com.atlassian.config.ConfigurationException;
import com.atlassian.confluence.admin.actions.lookandfeel.LookAndFeelAction;
import com.atlassian.confluence.themes.ColourScheme;
import com.atlassian.confluence.util.LayoutHelper;
import com.atlassian.confluence.util.misc.CssColourValidator;
import com.atlassian.xwork.ParameterSafe;
import com.opensymphony.xwork2.util.Element;
import java.util.HashMap;
import java.util.Map;
import org.apache.struts2.ServletActionContext;

public class ColourSchemeAction
extends LookAndFeelAction {
    private boolean resetDefaults;
    @Element(value=String.class)
    private Map<String, String> colourMap = new HashMap<String, String>();

    public String execute() throws ConfigurationException {
        if (this.resetDefaults) {
            return this.doReset();
        }
        this.getColourSchemeType();
        this.validateAndPopulateColourScheme();
        if (!this.getFieldErrors().isEmpty() || !this.getActionErrors().isEmpty()) {
            return "error";
        }
        this.convertMapToScheme();
        if (this.getSpace() != null) {
            this.colourSchemeManager.saveSpaceColourScheme(this.getSpace(), this.getEditableColourScheme());
        } else {
            this.colourSchemeManager.saveGlobalColourScheme(this.getEditableColourScheme());
        }
        LayoutHelper.flushThemeComponents(this.getSpaceKey());
        return "success";
    }

    public String changeColorScheme() {
        this.setColourSchemeSetting();
        LayoutHelper.flushThemeComponents(this.getSpaceKey());
        return "success";
    }

    @Override
    public String doDefault() throws Exception {
        this.convertSchemeToMap();
        return super.doDefault();
    }

    public String getColour(String key) {
        return this.getEditableColourScheme().get(key);
    }

    @ParameterSafe
    public Map<String, String> getColourMap() {
        return this.colourMap;
    }

    public String schemeKeyToParamKey(String key) {
        return key.replace(".", "_");
    }

    private String doReset() {
        this.colourSchemeManager.resetColourScheme(this.getSpace());
        LayoutHelper.flushThemeComponents(this.getSpaceKey());
        return "reset";
    }

    public void setResetDefaults(String resetDefaults) {
        this.resetDefaults = !"".equals(resetDefaults);
    }

    private void validateAndPopulateColourScheme() {
        for (String colourKey : ColourScheme.ORDERED_KEYS) {
            String colourVal = this.colourMap.get(this.schemeKeyToParamKey(colourKey));
            if (CssColourValidator.check((String)colourVal)) continue;
            this.addColourError(colourVal);
        }
    }

    private void addColourError(String value) {
        this.addActionError("colour.notvalid", value);
    }

    private void convertSchemeToMap() {
        for (String colourKey : ColourScheme.ORDERED_KEYS) {
            this.colourMap.put(this.schemeKeyToParamKey(colourKey), this.getEditableColourScheme().get(colourKey));
        }
    }

    private void convertMapToScheme() {
        for (String colourKey : ColourScheme.ORDERED_KEYS) {
            this.getEditableColourScheme().set(colourKey, this.colourMap.get(this.schemeKeyToParamKey(colourKey)));
        }
    }

    private void setColourSchemeSetting() {
        Map parameterMap = ServletActionContext.getRequest().getParameterMap();
        for (int i = 0; i < this.layoutHelper.getColourSchemeTypes().size(); ++i) {
            String key = (String)this.layoutHelper.getColourSchemeTypes().get(i);
            if (!parameterMap.containsKey(key)) continue;
            this.colourSchemeManager.setColourSchemeSetting(this.getSpace(), key);
        }
    }
}

