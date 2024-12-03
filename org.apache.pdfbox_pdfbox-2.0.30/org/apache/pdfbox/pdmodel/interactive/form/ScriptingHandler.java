/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.form;

import org.apache.pdfbox.pdmodel.interactive.action.PDActionJavaScript;

public interface ScriptingHandler {
    public String keyboard(PDActionJavaScript var1, String var2);

    public String format(PDActionJavaScript var1, String var2);

    public boolean validate(PDActionJavaScript var1, String var2);

    public String calculate(PDActionJavaScript var1, String var2);
}

