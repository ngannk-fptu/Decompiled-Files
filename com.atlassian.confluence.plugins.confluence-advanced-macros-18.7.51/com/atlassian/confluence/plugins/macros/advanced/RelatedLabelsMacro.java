/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.labels.LabelParser
 *  com.atlassian.confluence.labels.ParsedLabelName
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.renderer.radeox.macros.MacroUtils
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.velocity.VelocityUtils
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.RenderUtils
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.macros.advanced;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.LabelParser;
import com.atlassian.confluence.labels.ParsedLabelName;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.commons.lang3.StringUtils;

public class RelatedLabelsMacro
extends BaseMacro {
    private static final String TEMPLATE_NAME = "com/atlassian/confluence/plugins/macros/advanced/relatedlabelsmacro.vm";
    private static final String LABELS = "labels";
    private LabelManager labelManager;
    private ConfluenceActionSupport confluenceActionSuppport;

    public void setLabelManager(LabelManager manager) {
        this.labelManager = manager;
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

    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        if (!(renderContext instanceof PageContext)) {
            return RenderUtils.blockError((String)this.getConfluenceActionSuppport().getText("relatedlabels.error.unable-to-render"), (String)this.getConfluenceActionSuppport().getText("relatedlabels.error.can-only-be-used-in-pages-or-blogposts"));
        }
        PageContext pageContext = (PageContext)renderContext;
        Map<String, Object> contextMap = this.getMacroVelocityContext();
        LinkedList<Label> contents = new LinkedList<Label>();
        ArrayList<Label> allLabels = new ArrayList<Label>();
        String labels = (String)parameters.get(LABELS);
        if (StringUtils.isEmpty((CharSequence)labels)) {
            allLabels.addAll(pageContext.getEntity().getLabels());
        } else {
            StringTokenizer tokenizer = new StringTokenizer(labels, ", ");
            while (tokenizer.hasMoreTokens()) {
                Label label;
                String labelName = tokenizer.nextToken().trim();
                ParsedLabelName ref = LabelParser.parse((String)labelName, (User)AuthenticatedUserThreadLocal.get());
                if (ref == null || (label = this.labelManager.getLabel(ref)) == null) continue;
                allLabels.add(label);
            }
        }
        for (Label label : allLabels) {
            List relatedLabels = this.labelManager.getRelatedLabels(label);
            for (Label currentRelLabel : relatedLabels) {
                if (contents.contains(currentRelLabel)) continue;
                contents.add(currentRelLabel);
            }
        }
        contextMap.put("relatedLabels", contents);
        return this.renderRelatedLabels(contextMap);
    }

    public ConfluenceActionSupport getConfluenceActionSuppport() {
        if (null == this.confluenceActionSuppport) {
            this.confluenceActionSuppport = new ConfluenceActionSupport();
            ContainerManager.autowireComponent((Object)this.confluenceActionSuppport);
        }
        return this.confluenceActionSuppport;
    }

    protected Map<String, Object> getMacroVelocityContext() {
        return MacroUtils.defaultVelocityContext();
    }

    protected String renderRelatedLabels(Map<String, Object> contextMap) {
        return VelocityUtils.getRenderedTemplate((String)TEMPLATE_NAME, contextMap);
    }
}

