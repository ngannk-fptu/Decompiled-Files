/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.AtlassianCoreException
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.core.FormAware;
import com.atlassian.confluence.user.actions.AbstractUserProfileAction;
import com.atlassian.core.AtlassianCoreException;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

public class EditorSettingsAction
extends AbstractUserProfileAction
implements FormAware {
    private boolean editMode = false;
    private boolean autocompleteDisabled = false;
    private boolean autoformatDisabled = false;

    @PermittedMethods(value={HttpMethod.GET})
    public String doView() throws Exception {
        return "success";
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String doInput() throws Exception {
        this.editMode = true;
        return "input";
    }

    public String execute() throws Exception {
        this.updateUser();
        return "success";
    }

    private void updateUser() throws AtlassianCoreException {
        this.getUserPreferences().setBoolean("confluence.prefs.editor.disable.autocomplete", this.autocompleteDisabled);
        this.getUserPreferences().setBoolean("confluence.prefs.editor.disable.autoformat", this.autoformatDisabled);
    }

    public boolean isAutocompleteDisabled() {
        return this.getUserPreferences().getBoolean("confluence.prefs.editor.disable.autocomplete");
    }

    public void setAutocompleteDisabled(boolean autocompleteDisabled) {
        this.autocompleteDisabled = autocompleteDisabled;
    }

    public boolean isAutoformatDisabled() {
        return this.getUserPreferences().getBoolean("confluence.prefs.editor.disable.autoformat");
    }

    public void setAutoformatDisabled(boolean autoformatDisabled) {
        this.autoformatDisabled = autoformatDisabled;
    }

    @Override
    public boolean isPermitted() {
        return this.getUsername() != null && super.isPermitted();
    }

    @Override
    public boolean isEditMode() {
        return this.editMode;
    }
}

