/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.core.service.NotAuthorizedException
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.renderer.radeox.macros.MacroUtils
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.actions.AlphabeticalLabelGroupingSupport
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.util.velocity.VelocityUtils
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.user.User
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.macros.advanced;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.core.service.NotAuthorizedException;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.actions.AlphabeticalLabelGroupingSupport;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class ListLabelsMacro
extends BaseMacro
implements Macro {
    private static final String TOKEN_ALL = "@all";
    public static final String PARAM_SPACEKEY = "spaceKey";
    public static final String PARAM_EXCLUDEDLABELS = "excludedLabels";
    private final LabelManager labelManager;
    private final SpaceManager spaceManager;
    private final PermissionManager permissionManager;
    private final I18NBeanFactory i18NBeanFactory;

    public ListLabelsMacro(LabelManager labelManager, SpaceManager spaceManager, PermissionManager permissionManager, I18NBeanFactory i18NBeanFactory) {
        this.labelManager = labelManager;
        this.spaceManager = spaceManager;
        this.permissionManager = permissionManager;
        this.i18NBeanFactory = i18NBeanFactory;
    }

    public boolean isInline() {
        return false;
    }

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }

    public String execute(Map parameters, String body, ConversionContext conversionContext) {
        try {
            return this.execute(parameters, body, (RenderContext)conversionContext.getPageContext());
        }
        catch (MacroException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        Space space;
        String spaceKey = (String)parameters.get(PARAM_SPACEKEY);
        String excludedLabelsParam = (String)parameters.get(PARAM_EXCLUDEDLABELS);
        Object[] excludedLabels = null;
        if (!StringUtils.isBlank((CharSequence)excludedLabelsParam)) {
            excludedLabels = excludedLabelsParam.trim().split("\\s*,\\s*");
            Arrays.sort(excludedLabels);
        }
        if (StringUtils.isBlank((CharSequence)spaceKey)) {
            PageContext pCtx = (PageContext)renderContext;
            spaceKey = pCtx.getSpaceKey();
        }
        if ((space = this.spaceManager.getSpace(spaceKey)) != null && !this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)space)) {
            throw new NotAuthorizedException(this.i18NBeanFactory.getI18NBean().getText("confluence.macros.advanced.listlabels.error.user.not.authorized", (Object[])new String[]{AuthenticatedUserThreadLocal.get() == null ? this.i18NBeanFactory.getI18NBean().getText("anonymous.name") : AuthenticatedUserThreadLocal.getUsername()}));
        }
        AlphabeticalLabelGroupingSupport alphaSupport = this.getAlphaSupport(spaceKey, (String[])excludedLabels);
        Map<String, Object> contextMap = this.getDefaultVelocityContext();
        contextMap.put("alphaSupport", alphaSupport);
        contextMap.put("space", this.spaceManager.getSpace(spaceKey));
        contextMap.put(PARAM_SPACEKEY, spaceKey);
        return this.getRenderedTemplate(contextMap);
    }

    public AlphabeticalLabelGroupingSupport getAlphaSupport(@Nonnull String spaceKey, @Nullable String[] sortedExcludedLabels) {
        ArrayList labels;
        if (TOKEN_ALL.equals(spaceKey)) {
            TreeSet labelsSet = new TreeSet();
            List spaces = this.spaceManager.getAllSpaces();
            for (Space space : spaces) {
                labelsSet.addAll(this.labelManager.getLabelsInSpace(space.getKey()));
            }
            labels = new ArrayList(labelsSet);
        } else {
            labels = this.labelManager.getLabelsInSpace(spaceKey);
        }
        if (!ArrayUtils.isEmpty((Object[])sortedExcludedLabels)) {
            labels = labels.stream().filter(input -> input != null && Arrays.binarySearch(sortedExcludedLabels, input.toString()) < 0).collect(Collectors.toList());
        }
        return new AlphabeticalLabelGroupingSupport((Collection)labels);
    }

    protected String getRenderedTemplate(Map<String, Object> contextMap) {
        return VelocityUtils.getRenderedTemplate((String)"/com/atlassian/confluence/plugins/macros/advanced/listlabelsmacro.vm", contextMap);
    }

    protected Map<String, Object> getDefaultVelocityContext() {
        return MacroUtils.defaultVelocityContext();
    }
}

