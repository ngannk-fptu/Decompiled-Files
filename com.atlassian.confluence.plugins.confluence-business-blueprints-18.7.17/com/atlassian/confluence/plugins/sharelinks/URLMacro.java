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
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.plugins.sharelinks;

import com.atlassian.confluence.content.render.image.ImageDimensions;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.DefaultImagePlaceholder;
import com.atlassian.confluence.macro.EditorImagePlaceholder;
import com.atlassian.confluence.macro.ImagePlaceholder;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugins.BusinessBlueprintsContextProviderHelper;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;

public class URLMacro
implements Macro,
EditorImagePlaceholder {
    private final SettingsManager settingsManager;
    private final BusinessBlueprintsContextProviderHelper helper;
    private static final String IMAGE_PATH = "/download/resources/com.atlassian.confluence.plugins.confluence-business-blueprints:sharelinks-urlmacro-editor-resources/sharelinks-urlmacro-placeholder.png";

    public URLMacro(SettingsManager settingsManager, BusinessBlueprintsContextProviderHelper helper) {
        this.settingsManager = settingsManager;
        this.helper = helper;
    }

    public String execute(Map<String, String> parameters, String bodyText, ConversionContext conversionContext) throws MacroExecutionException {
        String soyBookmarkletLinkTemplateName = "Confluence.Blueprints.SharelinksUrlMacro.bookmarkletLink.soy";
        String bookmarkletActionURL = this.settingsManager.getGlobalSettings().getBaseUrl() + "/plugins/sharelinksbookmarklet/bookmarklet.action";
        HashMap soyLinkMetaDataContext = Maps.newHashMap();
        soyLinkMetaDataContext.put("bookmarkletActionURL", bookmarkletActionURL);
        return this.helper.renderFromSoy("com.atlassian.confluence.plugins.confluence-business-blueprints:sharelinks-urlmacro-resources", soyBookmarkletLinkTemplateName, soyLinkMetaDataContext);
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }

    public ImagePlaceholder getImagePlaceholder(Map<String, String> params, ConversionContext ctx) {
        return new DefaultImagePlaceholder(IMAGE_PATH, false, new ImageDimensions(175, 30));
    }
}

