/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.image.ImageDimensions
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.macro.DefaultImagePlaceholder
 *  com.atlassian.confluence.macro.EditorImagePlaceholder
 *  com.atlassian.confluence.macro.ImagePlaceholder
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.user.actions.ProfilePictureInfo
 *  com.atlassian.user.User
 *  org.springframework.web.util.HtmlUtils
 */
package com.atlassian.confluence.plugins.profile;

import com.atlassian.confluence.content.render.image.ImageDimensions;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.DefaultImagePlaceholder;
import com.atlassian.confluence.macro.EditorImagePlaceholder;
import com.atlassian.confluence.macro.ImagePlaceholder;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.actions.ProfilePictureInfo;
import com.atlassian.user.User;
import java.util.Map;
import org.springframework.web.util.HtmlUtils;

public class ProfilePictureMacro
implements Macro,
EditorImagePlaceholder {
    private static final String PARAM_USER = "User";
    private UserAccessor userAccessor;
    private SettingsManager settingsManager;

    public ImagePlaceholder getImagePlaceholder(Map<String, String> params, ConversionContext ctx) {
        String url = null;
        if (params.containsKey(PARAM_USER)) {
            ConfluenceUser user = this.userAccessor.getUserByName(params.get(PARAM_USER));
            ProfilePictureInfo picture = this.userAccessor.getUserProfilePicture((User)user);
            url = picture == null ? null : picture.getUriReference();
        }
        return new DefaultImagePlaceholder(url, false, new ImageDimensions(48, 48));
    }

    public String execute(Map<String, String> params, String body, ConversionContext ctx) throws MacroExecutionException {
        if (!params.containsKey(PARAM_USER)) {
            throw new MacroExecutionException("No user parameter specified");
        }
        ConfluenceUser user = this.userAccessor.getUserByName(params.get(PARAM_USER));
        ProfilePictureInfo picture = this.userAccessor.getUserProfilePicture((User)user);
        String url = picture == null ? null : picture.getUriReference();
        String username = HtmlUtils.htmlEscape((String)params.get(PARAM_USER));
        return String.format("<a class=\"userLogoLink\" data-username=\"%s\" href=\"%s\" title=\"\"><img class=\"userLogo logo\" src=\"%s\" alt=\"User icon: %s\" title=\"\"></a>", username, this.settingsManager.getGlobalSettings().getBaseUrl() + "/display/~" + username, url, username);
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.INLINE;
    }

    public void setUserAccessor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }
}

