/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.radeox.api.engine.RenderEngine
 *  org.radeox.api.engine.context.InitialRenderContext
 *  org.radeox.api.engine.context.RenderContext
 *  org.radeox.engine.context.BaseRenderContext
 *  org.radeox.macro.parameter.BaseMacroParameter
 *  org.radeox.macro.parameter.MacroParameter
 *  org.radeox.util.StringBufferWriter
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.renderer.macro;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.macro.Macro;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.radeox.api.engine.RenderEngine;
import org.radeox.api.engine.context.InitialRenderContext;
import org.radeox.engine.context.BaseRenderContext;
import org.radeox.macro.parameter.BaseMacroParameter;
import org.radeox.macro.parameter.MacroParameter;
import org.radeox.util.StringBufferWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RadeoxCompatibilityMacro
extends BaseMacro {
    public static final String RENDER_CONTEXT = "RENDER_CONTEXT";
    public static final String ATTACHMENTS_PATH = "ATTACHMENTS_PATH";
    public static final String EXTRACTED_EXTERNAL_REFERENCES = "EXTRACTED_EXTERNAL_REFERENCES";
    private static final Logger log = LoggerFactory.getLogger(RadeoxCompatibilityMacro.class);
    private static final Pattern INLINE_PATTERN = Pattern.compile("^\\s*<(span|code|a |font).*", 32);
    private Macro radeoxMacro;
    private String lastContent;
    private Boolean inline;

    public RadeoxCompatibilityMacro(Macro radeoxMacro) {
        this.radeoxMacro = radeoxMacro;
        try {
            radeoxMacro.setInitialContext(new BogusRadeoxContext());
        }
        catch (Exception e) {
            log.warn("Error wrapping radeox macro: {" + radeoxMacro.getName() + "} - " + e.getMessage());
        }
    }

    @Override
    public RenderMode getBodyRenderMode() {
        return RenderMode.COMPATIBILITY_MODE;
    }

    public Macro getRadeoxMacro() {
        return this.radeoxMacro;
    }

    @Override
    public boolean hasBody() {
        return true;
    }

    @Override
    public boolean isInline() {
        if (this.inline != null) {
            return this.inline;
        }
        if (this.lastContent == null || this.lastContent.trim().length() == 0) {
            return false;
        }
        this.inline = INLINE_PATTERN.matcher(this.lastContent).matches() ? Boolean.TRUE : Boolean.FALSE;
        this.lastContent = null;
        return this.inline;
    }

    @Override
    public String execute(Map parameters, String content, RenderContext context) throws MacroException {
        BaseRenderContext renderContext = new BaseRenderContext();
        BaseMacroParameter mParams = new BaseMacroParameter((org.radeox.api.engine.context.RenderContext)renderContext);
        renderContext.setParameters(new HashMap());
        mParams.getContext().getParameters().put(RENDER_CONTEXT, context);
        String attachmentsPath = context.getAttachmentsPath();
        if (attachmentsPath != null) {
            mParams.getContext().getParameters().put(ATTACHMENTS_PATH, attachmentsPath);
        }
        mParams.setContent(content);
        mParams.setContentStart(0);
        mParams.setContentEnd(content.length());
        mParams.setStart(0);
        mParams.setEnd(content.length());
        mParams.setParams((String)parameters.get(": = | RAW | = :"));
        StringBuffer output = new StringBuffer();
        try {
            this.radeoxMacro.execute((Writer)new StringBufferWriter(output), (MacroParameter)mParams);
            if (this.inline == null) {
                this.lastContent = output.toString();
            }
            return output.toString();
        }
        catch (IOException e) {
            throw new MacroException(e.getMessage(), e);
        }
    }

    private static class BogusRadeoxContext
    implements InitialRenderContext {
        private BogusRadeoxContext() {
        }

        public RenderEngine getRenderEngine() {
            throw new UnsupportedOperationException("Radeox compatibility layer does not have a render engine");
        }

        public void setRenderEngine(RenderEngine renderEngine) {
        }

        public Object get(String s) {
            throw new UnsupportedOperationException("Radeox compatibility layer does not have context properties");
        }

        public void set(String s, Object o) {
            throw new UnsupportedOperationException("Radeox compatibility layer does not have context properties");
        }

        public Map getParameters() {
            throw new UnsupportedOperationException("Radeox compatibility layer does not have context properties");
        }

        public void setParameters(Map map) {
            throw new UnsupportedOperationException("Radeox compatibility layer does not have context properties");
        }

        public void setCacheable(boolean b) {
        }

        public void commitCache() {
            throw new UnsupportedOperationException("Radeox compatibility layer does not have a cache");
        }

        public boolean isCacheable() {
            return false;
        }
    }
}

