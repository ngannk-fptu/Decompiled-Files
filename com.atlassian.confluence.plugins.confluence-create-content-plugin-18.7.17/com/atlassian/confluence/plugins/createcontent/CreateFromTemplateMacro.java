/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.createcontent;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugins.createcontent.api.services.CreateButtonService;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class CreateFromTemplateMacro
implements Macro {
    private static final String SPACE_KEY = "spaceKey";
    private static final String TEMPLATE_ID = "templateId";
    private static final String BLUEPRINT_MODULE_COMPLETE_KEY = "blueprintModuleCompleteKey";
    private static final String NEW_PAGE_TITLE = "title";
    private static final String CONTENT_BLUEPRINT_ID = "contentBlueprintId";
    private static final String OLD_BUTTON_LABEL = "createButtonLabel";
    private static final String BUTTON_LABEL = "buttonLabel";
    private final SpaceManager spaceManager;
    private final CreateButtonService createButtonService;

    public CreateFromTemplateMacro(SpaceManager spaceManager, CreateButtonService createButtonService) {
        this.spaceManager = spaceManager;
        this.createButtonService = createButtonService;
    }

    public String execute(Map<String, String> params, String body, ConversionContext conversionContext) throws MacroExecutionException {
        String templateId;
        Space space = this.getSpace(params, conversionContext);
        String title = params.get(NEW_PAGE_TITLE);
        String buttonLabelKey = params.get(BUTTON_LABEL);
        if (buttonLabelKey == null) {
            buttonLabelKey = params.get(OLD_BUTTON_LABEL);
        }
        if (StringUtils.isNotBlank((CharSequence)(templateId = params.get(TEMPLATE_ID)))) {
            return this.createButtonService.renderTemplateButton(space, Long.parseLong(templateId), buttonLabelKey, title);
        }
        String contentBlueprintId = params.get(CONTENT_BLUEPRINT_ID);
        String blueprintModuleCompleteKey = params.get(BLUEPRINT_MODULE_COMPLETE_KEY);
        return this.createButtonService.renderBlueprintButton(space, contentBlueprintId, blueprintModuleCompleteKey, buttonLabelKey, title);
    }

    private Space getSpace(Map<String, String> params, ConversionContext conversionContext) throws MacroExecutionException {
        Space space;
        String spaceKey = params.get(SPACE_KEY);
        if (StringUtils.isBlank((CharSequence)spaceKey)) {
            spaceKey = conversionContext.getSpaceKey();
        }
        if ((space = this.spaceManager.getSpace(spaceKey)) == null) {
            throw new MacroExecutionException("No space found with space key: " + spaceKey);
        }
        return space;
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.INLINE;
    }
}

