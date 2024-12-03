/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.macro.CustomHtmlEditorPlaceholder$PlaceholderGenerationException
 *  com.atlassian.confluence.macro.EditorImagePlaceholder
 *  com.atlassian.confluence.macro.ImagePlaceholder
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.macro.StreamableMacro
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.google.common.base.Joiner
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.text.StringEscapeUtils
 */
package com.atlassian.confluence.plugins.status;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.macro.CustomHtmlEditorPlaceholder;
import com.atlassian.confluence.macro.EditorImagePlaceholder;
import com.atlassian.confluence.macro.ImagePlaceholder;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.macro.StreamableMacro;
import com.atlassian.confluence.plugins.status.StatusMacroConfiguration;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.google.common.base.Joiner;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

public class StatusMacro
extends BaseMacro
implements Macro,
StreamableMacro,
EditorImagePlaceholder {
    private static final String CSS_CLASS_NAME_STATUS = "status-macro";
    private static final String CSS_CLASS_NAME_LOZENGE = "aui-lozenge";
    private static final String CSS_CLASS_NAME_LOZENGE_SUBTLE = "aui-lozenge-subtle";
    private static final Joiner SPACE_JOINER = Joiner.on((char)' ').skipNulls();
    private final EditorImagePlaceholder delegate;

    public StatusMacro(EditorImagePlaceholder delegate) {
        this.delegate = delegate;
    }

    public Streamable executeToStream(Map<String, String> parameters, Streamable body, ConversionContext context) throws MacroExecutionException {
        return writer -> StatusMacro.render(writer, parameters, context);
    }

    public String execute(Map<String, String> params, String body, ConversionContext ctx) throws MacroExecutionException {
        StringBuilder sb = new StringBuilder();
        try {
            StatusMacro.render(sb, params, ctx);
        }
        catch (IOException e) {
            throw new MacroExecutionException((Throwable)e);
        }
        return sb.toString();
    }

    private static void render(Appendable sb, Map<String, String> params, ConversionContext ctx) throws IOException {
        StatusMacroConfiguration configuration = StatusMacroConfiguration.createFor(params);
        String title = StatusMacro.getTitle(configuration);
        sb.append("<span class=\"");
        sb.append(StatusMacro.buildCssClasses(configuration));
        sb.append("\"");
        if (ctx != null && ConversionContextOutputType.PDF.value().equals(ctx.getOutputType())) {
            sb.append(" style=\"min-width:76px;padding-bottom:4px;\"");
        }
        sb.append(">");
        sb.append(title);
        sb.append("</span>");
    }

    private static String getTitle(StatusMacroConfiguration configuration) {
        String title = configuration.getTitle();
        if (StringUtils.isBlank((CharSequence)title)) {
            return "&nbsp;";
        }
        return StringEscapeUtils.escapeHtml4((String)title);
    }

    public String getCustomPlaceholder(Map<String, String> params, String body, ConversionContext ctx) throws CustomHtmlEditorPlaceholder.PlaceholderGenerationException {
        try {
            return this.execute(params, body, ctx);
        }
        catch (MacroExecutionException e) {
            throw new CustomHtmlEditorPlaceholder.PlaceholderGenerationException((Throwable)e);
        }
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.INLINE;
    }

    public boolean hasBody() {
        return false;
    }

    private static String buildCssClasses(StatusMacroConfiguration configuration) {
        String lozengeCssClassName = configuration.getColour().correspondingLozengeCssClass();
        return SPACE_JOINER.join((Object)CSS_CLASS_NAME_STATUS, (Object)CSS_CLASS_NAME_LOZENGE, new Object[]{lozengeCssClassName, configuration.isSubtle() ? CSS_CLASS_NAME_LOZENGE_SUBTLE : null}).trim();
    }

    public ImagePlaceholder getImagePlaceholder(Map<String, String> parameters, ConversionContext context) {
        return this.delegate.getImagePlaceholder(parameters, context);
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.INLINE;
    }

    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        try {
            return this.execute((Map<String, String>)parameters, body, (ConversionContext)new DefaultConversionContext(renderContext));
        }
        catch (MacroExecutionException e) {
            throw new MacroException((Throwable)e);
        }
    }
}

