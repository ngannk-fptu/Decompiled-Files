/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.TokenType
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.extra.livesearch;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class LiveSearchMacro
extends BaseMacro
implements Macro {
    public static final String SPACE_NAME = "space name";
    public static final String MEDIUM_SIZE = "medium";
    public static final String CONF_ALL = "conf_all";
    public static final String SELF = "@self";
    private final SpaceManager spaceManager;
    private final VelocityHelperService velocityHelperService;

    public LiveSearchMacro(SpaceManager spaceManager, VelocityHelperService velocityHelperService) {
        this.spaceManager = spaceManager;
        this.velocityHelperService = velocityHelperService;
    }

    public TokenType getTokenType(Map parameters, String body, RenderContext context) {
        return TokenType.INLINE;
    }

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    public String execute(Map macroParams, String s, RenderContext renderContext) {
        return this.execute((Map<String, String>)macroParams, s, (ConversionContext)new DefaultConversionContext(renderContext));
    }

    public String execute(Map<String, String> parameters, String body, ConversionContext conversionContext) {
        String size;
        String spaceKey = parameters.get("spaceKey");
        String additional = parameters.get("additional");
        if (StringUtils.isBlank((CharSequence)additional)) {
            additional = SPACE_NAME;
        }
        if (StringUtils.isBlank((CharSequence)(size = parameters.get("size")))) {
            size = MEDIUM_SIZE;
        }
        String labels = parameters.get("labels");
        String contentType = parameters.get("type");
        String placeholder = parameters.get("placeholder");
        Map<String, Object> contextMap = this.getMacroVelocityContext();
        if (null == spaceKey) {
            contextMap.put("where", CONF_ALL);
        } else if (spaceKey.equals(SELF)) {
            contextMap.put("where", conversionContext.getSpaceKey());
        } else {
            Space space = this.spaceManager.getSpace(spaceKey);
            if (space != null) {
                contextMap.put("where", space.getKey());
            }
        }
        contextMap.put("additional", additional);
        contextMap.put("labels", labels);
        contextMap.put("contentType", contentType);
        contextMap.put("size", size);
        contextMap.put("placeholder", placeholder);
        return this.renderWithVelocityTemplate(contextMap);
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.INLINE;
    }

    private String renderWithVelocityTemplate(Map<String, Object> contextMap) {
        return this.velocityHelperService.getRenderedTemplate("templates/extra/livesearch/livesearchmacro.vm", contextMap);
    }

    private Map<String, Object> getMacroVelocityContext() {
        return this.velocityHelperService.createDefaultVelocityContext();
    }
}

