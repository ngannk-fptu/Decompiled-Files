/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.macro;

import com.atlassian.renderer.v2.V2SubRenderer;
import com.atlassian.renderer.v2.macro.Macro;
import com.atlassian.renderer.v2.macro.MacroManager;
import com.atlassian.renderer.v2.macro.basic.BasicAnchorMacro;
import com.atlassian.renderer.v2.macro.basic.ColorMacro;
import com.atlassian.renderer.v2.macro.basic.InlineHtmlMacro;
import com.atlassian.renderer.v2.macro.basic.LoremIpsumMacro;
import com.atlassian.renderer.v2.macro.basic.NoformatMacro;
import com.atlassian.renderer.v2.macro.basic.PanelMacro;
import com.atlassian.renderer.v2.macro.basic.QuoteMacro;
import com.atlassian.renderer.v2.macro.code.CodeMacro;
import com.atlassian.renderer.v2.macro.code.formatter.AbstractFormatter;
import com.atlassian.renderer.v2.macro.code.formatter.ActionScriptFormatter;
import com.atlassian.renderer.v2.macro.code.formatter.JavaFormatter;
import com.atlassian.renderer.v2.macro.code.formatter.JavaScriptFormatter;
import com.atlassian.renderer.v2.macro.code.formatter.NoneFormatter;
import com.atlassian.renderer.v2.macro.code.formatter.SqlFormatter;
import com.atlassian.renderer.v2.macro.code.formatter.XmlFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DefaultMacroManager
implements MacroManager {
    private HashMap macros = new HashMap();

    public DefaultMacroManager(V2SubRenderer subRenderer) {
        this.macros.put("anchor", new BasicAnchorMacro());
        this.macros.put("code", new CodeMacro(subRenderer, this.getCodeFormatters()));
        this.macros.put("quote", new QuoteMacro());
        this.macros.put("noformat", new NoformatMacro(subRenderer));
        this.macros.put("panel", new PanelMacro(subRenderer));
        this.macros.put("color", new ColorMacro());
        this.macros.put("loremipsum", new LoremIpsumMacro());
        this.macros.put("html", new InlineHtmlMacro());
    }

    public void registerMacro(String name, Macro macro) {
        this.macros.put(name, macro);
    }

    private List getCodeFormatters() {
        ArrayList<AbstractFormatter> codeFormatters = new ArrayList<AbstractFormatter>();
        codeFormatters.add(new SqlFormatter());
        codeFormatters.add(new JavaFormatter());
        codeFormatters.add(new JavaScriptFormatter());
        codeFormatters.add(new ActionScriptFormatter());
        codeFormatters.add(new XmlFormatter());
        codeFormatters.add(new NoneFormatter());
        return codeFormatters;
    }

    @Override
    public Macro getEnabledMacro(String name) {
        return (Macro)this.macros.get(name);
    }

    public void unregisterMacro(String name) {
        this.macros.remove(name);
    }
}

