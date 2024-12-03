/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.macro.params.MacroParamUtils
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.macros.dashboard.recentupdates;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.macro.params.MacroParamUtils;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.renderer.v2.macro.MacroException;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public final class RecentlyUpdatedMacroParams {
    private static final EnumSet<ContentTypeEnum> DEFAULT_TYPE_FILTER = EnumSet.of(ContentTypeEnum.PAGE, new ContentTypeEnum[]{ContentTypeEnum.BLOG, ContentTypeEnum.COMMENT, ContentTypeEnum.PERSONAL_INFORMATION, ContentTypeEnum.SPACE_DESCRIPTION, ContentTypeEnum.PERSONAL_SPACE_DESCRIPTION, ContentTypeEnum.ATTACHMENT});
    private boolean showProfilePic;
    private Set<String> validLabels;
    private Set<String> nonExistentLabels;
    private boolean hasSpaces;
    private Set<String> spaces;
    private Set<ConfluenceUser> users;
    private EnumSet<ContentTypeEnum> types;
    private static String overrideDefaultFilterTypes = System.getProperty("confluence.dashboard.recentlyupdate.filtertypes");

    public RecentlyUpdatedMacroParams(Map<String, String> macroParams, LabelManager labelManager) throws MacroException {
        this.parseShowProfilePic(macroParams);
        this.parseLabels(macroParams, labelManager);
        this.parseSpaces(macroParams);
        this.parseUsers(macroParams);
        this.parseTypes(macroParams);
    }

    public boolean isShowProfilePic() {
        return this.showProfilePic;
    }

    public Set<String> getValidLabels() {
        return this.validLabels;
    }

    public Set<String> getNonExistentLabels() {
        return this.nonExistentLabels;
    }

    public Set<String> getSpaces() {
        return this.spaces;
    }

    public boolean hasSpaces() {
        return this.hasSpaces;
    }

    public Set<ConfluenceUser> getUsers() {
        return this.users;
    }

    public EnumSet<ContentTypeEnum> getTypes() {
        return this.types;
    }

    private void parseShowProfilePic(Map<String, String> macroParams) {
        this.showProfilePic = "true".equalsIgnoreCase(macroParams.get("showProfilePic"));
    }

    private void parseLabels(Map<String, String> macroParams, LabelManager labelManager) {
        Set<String> labelParams = RecentlyUpdatedMacroParams.commaSeparatedStringToSet(macroParams.get("labels"));
        if (labelParams == null) {
            return;
        }
        this.validLabels = new LinkedHashSet<String>();
        this.nonExistentLabels = new LinkedHashSet<String>();
        for (String labelName : labelParams) {
            Label label = labelManager.getLabel(labelName);
            if (label != null) {
                this.validLabels.add(label.toStringWithNamespace());
                continue;
            }
            this.nonExistentLabels.add(labelName);
        }
    }

    private void parseSpaces(Map<String, String> macroParams) {
        String spacesMacroParam = macroParams.get("spaces");
        boolean bl = this.hasSpaces = spacesMacroParam != null;
        if ("*".equals(spacesMacroParam)) {
            this.spaces = null;
            return;
        }
        this.spaces = RecentlyUpdatedMacroParams.commaSeparatedStringToSet(spacesMacroParam);
    }

    private void parseUsers(Map<String, String> macroParams) {
        Set<String> usernames = RecentlyUpdatedMacroParams.commaSeparatedStringToSet(macroParams.get("users"));
        if (usernames == null) {
            return;
        }
        this.users = Sets.newHashSetWithExpectedSize((int)usernames.size());
        for (String username : usernames) {
            ConfluenceUser user = FindUserHelper.getUserByUsername((String)username);
            if (user == null) continue;
            this.users.add(user);
        }
    }

    private void parseTypes(Map<String, String> macroParams) throws MacroException {
        HashSet<String> typeKeys = RecentlyUpdatedMacroParams.commaSeparatedStringToSet(macroParams.get("types"));
        if (typeKeys == null) {
            HashSet<String> hashSet = typeKeys = StringUtils.isNotBlank((CharSequence)overrideDefaultFilterTypes) ? new HashSet<String>(Arrays.asList(StringUtils.split((String)overrideDefaultFilterTypes, (String)","))) : null;
        }
        if (typeKeys == null) {
            this.types = DEFAULT_TYPE_FILTER;
            return;
        }
        ArrayList<ContentTypeEnum> validTypes = new ArrayList<ContentTypeEnum>(typeKeys.size());
        for (String key : typeKeys) {
            ContentTypeEnum type;
            if (key.equals("news")) {
                key = "blogpost";
            }
            if ((type = ContentTypeEnum.getByRepresentation((String)key)) == null) {
                throw new MacroException("Type '" + key + "' is not a recognized content type");
            }
            validTypes.add(type);
        }
        this.types = EnumSet.copyOf(validTypes);
    }

    private static Set<String> commaSeparatedStringToSet(String str) {
        List strings = MacroParamUtils.parseCommaSeparatedStrings((String)str);
        if (strings.isEmpty()) {
            return null;
        }
        return new LinkedHashSet<String>(strings);
    }

    public static void setOverrideDefaultFilterTypes(String overrideDefaultFilterTypes) {
        RecentlyUpdatedMacroParams.overrideDefaultFilterTypes = overrideDefaultFilterTypes;
    }
}

