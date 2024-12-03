/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.radeox.macro;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.radeox.api.engine.context.InitialRenderContext;
import org.radeox.filter.context.BaseFilterContext;
import org.radeox.filter.context.FilterContext;
import org.radeox.macro.LocalePreserved;
import org.radeox.macro.code.SourceCodeFormatter;
import org.radeox.macro.parameter.MacroParameter;
import org.radeox.util.Service;

public class CodeMacro
extends LocalePreserved {
    private static Log log = LogFactory.getLog((Class)(class$org$radeox$macro$CodeMacro == null ? (class$org$radeox$macro$CodeMacro = CodeMacro.class$("org.radeox.macro.CodeMacro")) : class$org$radeox$macro$CodeMacro));
    private Map formatters;
    private FilterContext nullContext = new BaseFilterContext();
    private String start;
    private String end;
    private String[] paramDescription = new String[]{"?1: syntax highlighter to use, defaults to java"};
    static /* synthetic */ Class class$org$radeox$macro$CodeMacro;
    static /* synthetic */ Class class$org$radeox$macro$code$SourceCodeFormatter;

    public String[] getParamDescription() {
        return this.paramDescription;
    }

    public String getLocaleKey() {
        return "macro.code";
    }

    public void setInitialContext(InitialRenderContext context) {
        super.setInitialContext(context);
        Locale outputLocale = (Locale)context.get("RenderContext.output_locale");
        String outputName = (String)context.get("RenderContext.output_bundle_name");
        ResourceBundle outputMessages = ResourceBundle.getBundle(outputName, outputLocale);
        this.start = outputMessages.getString(this.getLocaleKey() + ".start");
        this.end = outputMessages.getString(this.getLocaleKey() + ".end");
    }

    public CodeMacro() {
        this.formatters = new HashMap();
        Iterator formatterIt = Service.providers(class$org$radeox$macro$code$SourceCodeFormatter == null ? (class$org$radeox$macro$code$SourceCodeFormatter = CodeMacro.class$("org.radeox.macro.code.SourceCodeFormatter")) : class$org$radeox$macro$code$SourceCodeFormatter);
        while (formatterIt.hasNext()) {
            try {
                SourceCodeFormatter formatter = (SourceCodeFormatter)formatterIt.next();
                String name = formatter.getName();
                if (this.formatters.containsKey(name)) {
                    SourceCodeFormatter existing = (SourceCodeFormatter)this.formatters.get(name);
                    if (existing.getPriority() >= formatter.getPriority()) continue;
                    this.formatters.put(name, formatter);
                    log.debug((Object)("Replacing formatter: " + formatter.getClass() + " (" + name + ")"));
                    continue;
                }
                this.formatters.put(name, formatter);
                log.debug((Object)("Loaded formatter: " + formatter.getClass() + " (" + name + ")"));
            }
            catch (Exception e) {
                log.warn((Object)"CodeMacro: unable to load code formatter", (Throwable)e);
            }
        }
        this.addSpecial('[');
        this.addSpecial(']');
        this.addSpecial('{');
        this.addSpecial('}');
        this.addSpecial('*');
        this.addSpecial('-');
        this.addSpecial('\\');
    }

    public void execute(Writer writer, MacroParameter params) throws IllegalArgumentException, IOException {
        SourceCodeFormatter formatter = null;
        if (params.getLength() == 0 || !this.formatters.containsKey(params.get("0"))) {
            formatter = (SourceCodeFormatter)this.formatters.get(this.initialContext.get("RenderContext.default_formatter"));
            if (null == formatter) {
                System.err.println("Formatter not found.");
                formatter = (SourceCodeFormatter)this.formatters.get("java");
            }
        } else {
            formatter = (SourceCodeFormatter)this.formatters.get(params.get("0"));
        }
        String result = formatter.filter(params.getContent(), this.nullContext);
        writer.write(this.start);
        writer.write(this.replace(result.trim()));
        writer.write(this.end);
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

