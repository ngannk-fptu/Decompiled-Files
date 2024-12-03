/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.v2.macro.MacroManager
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.user.User
 *  com.atlassian.xwork.ParameterSafe
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.admin.actions.macros;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.macro.GenericVelocityMacro;
import com.atlassian.confluence.macro.browser.MacroMetadataManager;
import com.atlassian.confluence.macro.browser.beans.MacroCategory;
import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.confluence.renderer.UserMacroConfig;
import com.atlassian.confluence.renderer.UserMacroLibrary;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.util.HTMLPairType;
import com.atlassian.renderer.v2.macro.MacroManager;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.user.User;
import com.atlassian.xwork.ParameterSafe;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

@WebSudoRequired
@SystemAdminOnly
public class UserMacroAction
extends ConfluenceActionSupport {
    private List<HTMLPairType> categories;
    private List<String> bodyTypeKeys;
    protected UserMacroConfig userMacro;
    protected UserMacroLibrary userMacroLibrary;
    protected MacroManager macroManager;
    protected MacroMetadataManager macroMetadataManager;
    protected String macro;
    boolean showInMacroBrowser = false;
    private List<UserMacroConfig> userMacros;
    private String templateHtml = "## Macro title: My Macro\n## Macro has a body: Y or N\n## Body processing: Selected body processing option\n## Output: Selected output option\n##\n## Developed by: My Name\n## Date created: dd/mm/yyyy\n## Installed by: My Name\n\n## This is an example macro\n## @param Name:title=Name|type=string|required=true|desc=Your name\n## @param Colour:title=Favourite Colour|type=enum|enumValues=red,green,blue|default=red|desc=Choose your favourite colour\n\nHello, <font color=\"$paramColour\">$paramName</font>!";

    @ParameterSafe
    public UserMacroConfig getUserMacro() {
        return this.userMacro;
    }

    public List<UserMacroConfig> getUserMacros() {
        if (this.userMacros == null) {
            Map<String, UserMacroConfig> macroMap = this.userMacroLibrary.getMacros();
            ArrayList<UserMacroConfig> macros = new ArrayList<UserMacroConfig>(macroMap.values());
            Collections.sort(macros, (o1, o2) -> o1.getName().compareTo(o2.getName()));
            this.userMacros = Collections.unmodifiableList(macros);
        }
        return this.userMacros;
    }

    public void setUserMacroLibrary(UserMacroLibrary userMacroLibrary) {
        this.userMacroLibrary = userMacroLibrary;
    }

    public void setMacroManager(MacroManager macroManager) {
        this.macroManager = macroManager;
    }

    public void setMacroMetadataManager(MacroMetadataManager macroMetadataManager) {
        this.macroMetadataManager = macroMetadataManager;
    }

    public void setMacro(String macro) {
        this.macro = macro;
    }

    public void setUserMacro(UserMacroConfig userMacro) {
        this.userMacro = userMacro;
    }

    @Override
    public String doDefault() throws Exception {
        if (StringUtils.isNotBlank((CharSequence)this.macro)) {
            this.userMacro = this.userMacroLibrary.getMacro(this.macro);
            this.validateMacroMissingContextVariables(this.userMacro);
        } else {
            this.userMacro = new UserMacroConfig();
            this.userMacro.setHidden(false);
            this.userMacro.setOutputType("html");
            this.userMacro.setHasBody(false);
            if ("UserMacroAction".equals(this.getClass().getSimpleName())) {
                this.validateMacroMissingContextVariables(null);
            }
        }
        if (!this.userMacro.isHasBody()) {
            this.userMacro.setBodyType("none");
        }
        this.showInMacroBrowser = !this.userMacro.isHidden();
        return super.doDefault();
    }

    protected void addUpdateMacro(UserMacroConfig userMacro) {
        userMacro.setHidden(!this.showInMacroBrowser);
        userMacro.setHasBody(!"none".equals(userMacro.getBodyType()));
        this.userMacroLibrary.addUpdateMacro(userMacro);
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }

    public boolean isWikiTemplate(String macroName) {
        if (StringUtils.isBlank((CharSequence)macroName)) {
            return false;
        }
        UserMacroConfig config = this.userMacroLibrary.getMacro(macroName);
        if (config == null) {
            return false;
        }
        return "wiki".equals(config.getOutputType());
    }

    public boolean isMissingMetadata(String macroName) {
        Set<MacroMetadata> allMetadata = this.macroMetadataManager.getAllMacroMetadata();
        for (MacroMetadata metadata : allMetadata) {
            if (!macroName.equals(metadata.getMacroName()) || !"_-user-macro-_".equals(metadata.getPluginKey())) continue;
            return false;
        }
        return true;
    }

    public int countUserMacrosWithWikiTemplates() {
        int count = 0;
        for (String macroName : this.userMacroLibrary.getMacroNames()) {
            UserMacroConfig config = this.userMacroLibrary.getMacro(macroName);
            if (config == null || !"wiki".equals(config.getOutputType())) continue;
            ++count;
        }
        return count;
    }

    public int countUserMacrosWithoutMetadata() {
        int count = 0;
        SortedSet<String> macroNames = this.userMacroLibrary.getMacroNames();
        for (String macroName : macroNames) {
            if (!this.isMissingMetadata(macroName)) continue;
            ++count;
        }
        return count;
    }

    public List<String> getBodyTypes() {
        if (this.bodyTypeKeys == null) {
            this.bodyTypeKeys = new ArrayList<String>(3);
            this.bodyTypeKeys.add("none");
            this.bodyTypeKeys.add("escapehtml");
            this.bodyTypeKeys.add("raw");
            this.bodyTypeKeys.add("rendered");
        }
        return this.bodyTypeKeys;
    }

    public List<String> getVisibleTypes() {
        return Collections.unmodifiableList(Arrays.asList("true", "false"));
    }

    public List<HTMLPairType> getCategories() {
        if (this.categories == null) {
            ArrayList<HTMLPairType> tmpCategories = new ArrayList<HTMLPairType>();
            for (MacroCategory category : MacroCategory.values()) {
                if (category == MacroCategory.HIDDEN) continue;
                tmpCategories.add(new HTMLPairType(category.getName(), this.getText(category.getDisplayName().getKey())));
            }
            Collections.sort(tmpCategories, (o1, o2) -> o1.getValue().toString().compareTo(o2.getValue().toString()));
            this.categories = Collections.unmodifiableList(tmpCategories);
        }
        return this.categories;
    }

    public boolean getShowInMacroBrowser() {
        return this.showInMacroBrowser;
    }

    public void setShowInMacroBrowser(boolean showInMacroBrowser) {
        this.showInMacroBrowser = showInMacroBrowser;
    }

    public String getTemplateHtml() {
        return this.templateHtml;
    }

    protected void validateNewMacroName(String name) {
        if (StringUtils.isEmpty((CharSequence)name)) {
            this.addFieldError("userMacro.name", "user.macro.name.empty", new Object[]{name});
        } else if (!name.matches("^[-\\w]+$")) {
            this.addFieldError("userMacro.name", "user.macro.name.invalid", new Object[]{name});
        } else if (this.userMacroLibrary.hasMacro(name)) {
            this.addFieldError("userMacro.name", "user.macro.already.exists", new Object[]{name});
        } else if (this.macroManager.getEnabledMacro(name) != null) {
            this.addFieldError("userMacro.name", "system.macro.already.exists", new Object[]{name});
        }
    }

    protected void validateMacroForm() {
        String icon;
        if (StringUtils.isEmpty((CharSequence)this.userMacro.getTemplate())) {
            this.addFieldError("userMacro.template", "user.macro.template.empty", new Object[0]);
        }
        if (StringUtils.isBlank((CharSequence)this.userMacro.getTitle())) {
            this.addFieldError("userMacro.title", "user.macro.title.empty", new Object[0]);
        } else if (this.userMacro.getTitle().length() > 64) {
            this.addFieldError("userMacro.title", "user.macro.title.length.limit", new Object[0]);
        }
        if (StringUtils.isNotBlank((CharSequence)this.userMacro.getDescription()) && this.userMacro.getDescription().length() > 512) {
            this.addFieldError("userMacro.description", "user.macro.description.length.limit", new Object[0]);
        }
        if (StringUtils.isNotEmpty((CharSequence)(icon = this.userMacro.getIconLocation())) && !icon.startsWith("http") && !icon.startsWith("/")) {
            this.addFieldError("userMacro.iconLocation", "user.macro.icon.location.invalid", new Object[0]);
        }
    }

    protected void validateMacroMissingContextVariables(@Nullable UserMacroConfig macroConfig) {
        List<String> configuredContextKeys = GenericVelocityMacro.REQUIRED_VELOCITY_CONTEXT_KEYS;
        Set<String> defaultContextKeys = MacroUtils.defaultVelocityContext().keySet();
        Pattern pattern = Pattern.compile("\\$([a-zA-Z0-9]+)");
        this.getToBeValidatedUserMacros(macroConfig).forEach(userMacroConfig -> {
            HashSet<String> macroContextKeys = new HashSet<String>();
            Matcher matcher = pattern.matcher(userMacroConfig.getTemplate());
            while (matcher.find()) {
                macroContextKeys.add(matcher.group(1));
            }
            macroContextKeys.retainAll(defaultContextKeys);
            macroContextKeys.removeAll(configuredContextKeys);
            if (!macroContextKeys.isEmpty()) {
                this.getMessageHolder().addActionWarning("usermacro.miss.context.keys", userMacroConfig.getName(), String.join((CharSequence)", ", macroContextKeys));
            }
        });
    }

    protected List<UserMacroConfig> getToBeValidatedUserMacros(@Nullable UserMacroConfig macroConfig) {
        if (macroConfig == null) {
            return new ArrayList<UserMacroConfig>(this.userMacroLibrary.getMacros().values());
        }
        return Collections.singletonList(macroConfig);
    }
}

