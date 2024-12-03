/*
 * Decompiled with CFR 0.152.
 */
package groovy.ui;

import groovy.lang.GroovyShell;
import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public abstract class ConsoleSupport {
    Style promptStyle;
    Style commandStyle;
    Style outputStyle;
    private GroovyShell shell;
    int counter;

    protected void addStylesToDocument(JTextPane outputArea) {
        StyledDocument doc = outputArea.getStyledDocument();
        Style def = StyleContext.getDefaultStyleContext().getStyle("default");
        Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "Monospaced");
        this.promptStyle = doc.addStyle("prompt", regular);
        StyleConstants.setForeground(this.promptStyle, Color.BLUE);
        this.commandStyle = doc.addStyle("command", regular);
        StyleConstants.setForeground(this.commandStyle, Color.MAGENTA);
        this.outputStyle = doc.addStyle("output", regular);
        StyleConstants.setBold(this.outputStyle, true);
    }

    public Style getCommandStyle() {
        return this.commandStyle;
    }

    public Style getOutputStyle() {
        return this.outputStyle;
    }

    public Style getPromptStyle() {
        return this.promptStyle;
    }

    public GroovyShell getShell() {
        if (this.shell == null) {
            this.shell = new GroovyShell();
        }
        return this.shell;
    }

    protected Object evaluate(String text) {
        String name = "Script" + this.counter++;
        try {
            return this.getShell().evaluate(text, name);
        }
        catch (Exception e) {
            this.handleException(text, e);
            return null;
        }
    }

    protected abstract void handleException(String var1, Exception var2);
}

