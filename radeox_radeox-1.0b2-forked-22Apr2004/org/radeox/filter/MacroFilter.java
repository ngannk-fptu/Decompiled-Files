/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.radeox.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.radeox.api.engine.IncludeRenderEngine;
import org.radeox.api.engine.RenderEngine;
import org.radeox.api.engine.context.InitialRenderContext;
import org.radeox.filter.context.FilterContext;
import org.radeox.filter.regex.RegexTokenFilter;
import org.radeox.macro.Macro;
import org.radeox.macro.MacroRepository;
import org.radeox.macro.Repository;
import org.radeox.macro.parameter.MacroParameter;
import org.radeox.regex.MatchResult;
import org.radeox.util.StringBufferWriter;

public class MacroFilter
extends RegexTokenFilter {
    private static Log log = LogFactory.getLog((Class)(class$org$radeox$filter$MacroFilter == null ? (class$org$radeox$filter$MacroFilter = MacroFilter.class$("org.radeox.filter.MacroFilter")) : class$org$radeox$filter$MacroFilter));
    private MacroRepository macros;
    static /* synthetic */ Class class$org$radeox$filter$MacroFilter;

    public MacroFilter() {
        super("\\{([^:}]+)(?::([^\\}]*))?\\}(.*?)\\{\\1\\}", false);
        this.addRegex("\\{([^:}]+)(?::([^\\}]*))?\\}", "", true);
    }

    public void setInitialContext(InitialRenderContext context) {
        this.macros = MacroRepository.getInstance();
        this.macros.setInitialContext(context);
    }

    protected Repository getMacroRepository() {
        return this.macros;
    }

    public void handleMatch(StringBuffer buffer, MatchResult result, FilterContext context) {
        String command = result.group(1);
        if (command != null) {
            if (!command.startsWith("$")) {
                MacroParameter mParams = context.getMacroParameter();
                switch (result.groups()) {
                    case 3: {
                        mParams.setContent(result.group(3));
                        mParams.setContentStart(result.beginOffset(3));
                        mParams.setContentEnd(result.endOffset(3));
                    }
                    case 2: {
                        mParams.setParams(result.group(2));
                    }
                }
                mParams.setStart(result.beginOffset(0));
                mParams.setEnd(result.endOffset(0));
                try {
                    Macro macro;
                    if (this.getMacroRepository().containsKey(command)) {
                        macro = (Macro)this.getMacroRepository().get(command);
                        if (null != mParams.getContent()) {
                            mParams.setContent(this.filter(mParams.getContent(), context));
                        }
                    } else {
                        if (command.startsWith("!")) {
                            RenderEngine engine = context.getRenderContext().getRenderEngine();
                            if (engine instanceof IncludeRenderEngine) {
                                String include = ((IncludeRenderEngine)((Object)engine)).include(command.substring(1));
                                if (null != include) {
                                    buffer.append(include);
                                } else {
                                    buffer.append(command.substring(1) + " not found.");
                                }
                            }
                            return;
                        }
                        buffer.append(result.group(0));
                        return;
                    }
                    StringBufferWriter writer = new StringBufferWriter(buffer);
                    macro.execute(writer, mParams);
                }
                catch (IllegalArgumentException e) {
                    buffer.append("<div class=\"error\">" + command + ": " + e.getMessage() + "</div>");
                }
                catch (Throwable e) {
                    log.warn((Object)("MacroFilter: unable to format macro: " + result.group(1)), e);
                    buffer.append("<div class=\"error\">" + command + ": " + e.getMessage() + "</div>");
                    return;
                }
            } else {
                buffer.append("<");
                buffer.append(command.substring(1));
                buffer.append(">");
            }
        } else {
            buffer.append(result.group(0));
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

