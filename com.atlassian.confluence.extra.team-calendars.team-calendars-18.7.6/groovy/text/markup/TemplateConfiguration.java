/*
 * Decompiled with CFR 0.152.
 */
package groovy.text.markup;

import groovy.text.markup.BaseTemplate;
import java.util.Locale;

public class TemplateConfiguration {
    private String declarationEncoding;
    private boolean expandEmptyElements;
    private boolean useDoubleQuotes;
    private String newLineString = System.getProperty("line.separator");
    private boolean autoEscape = false;
    private boolean autoIndent = false;
    private String autoIndentString = "    ";
    private boolean autoNewLine = false;
    private Class<? extends BaseTemplate> baseTemplateClass = BaseTemplate.class;
    private Locale locale = Locale.getDefault();
    private boolean cacheTemplates = true;

    public TemplateConfiguration() {
    }

    public TemplateConfiguration(TemplateConfiguration that) {
        this.declarationEncoding = that.declarationEncoding;
        this.expandEmptyElements = that.expandEmptyElements;
        this.useDoubleQuotes = that.useDoubleQuotes;
        this.newLineString = that.newLineString;
        this.autoEscape = that.autoEscape;
        this.autoIndent = that.autoIndent;
        this.autoIndentString = that.autoIndentString;
        this.autoNewLine = that.autoNewLine;
        this.baseTemplateClass = that.baseTemplateClass;
        this.locale = that.locale;
    }

    public String getDeclarationEncoding() {
        return this.declarationEncoding;
    }

    public void setDeclarationEncoding(String declarationEncoding) {
        this.declarationEncoding = declarationEncoding;
    }

    public boolean isExpandEmptyElements() {
        return this.expandEmptyElements;
    }

    public void setExpandEmptyElements(boolean expandEmptyElements) {
        this.expandEmptyElements = expandEmptyElements;
    }

    public boolean isUseDoubleQuotes() {
        return this.useDoubleQuotes;
    }

    public void setUseDoubleQuotes(boolean useDoubleQuotes) {
        this.useDoubleQuotes = useDoubleQuotes;
    }

    public String getNewLineString() {
        return this.newLineString;
    }

    public void setNewLineString(String newLineString) {
        this.newLineString = newLineString;
    }

    public boolean isAutoEscape() {
        return this.autoEscape;
    }

    public void setAutoEscape(boolean autoEscape) {
        this.autoEscape = autoEscape;
    }

    public boolean isAutoIndent() {
        return this.autoIndent;
    }

    public void setAutoIndent(boolean autoIndent) {
        this.autoIndent = autoIndent;
    }

    public String getAutoIndentString() {
        return this.autoIndentString;
    }

    public void setAutoIndentString(String autoIndentString) {
        this.autoIndentString = autoIndentString;
    }

    public boolean isAutoNewLine() {
        return this.autoNewLine;
    }

    public void setAutoNewLine(boolean autoNewLine) {
        this.autoNewLine = autoNewLine;
    }

    public Class<? extends BaseTemplate> getBaseTemplateClass() {
        return this.baseTemplateClass;
    }

    public void setBaseTemplateClass(Class<? extends BaseTemplate> baseTemplateClass) {
        this.baseTemplateClass = baseTemplateClass;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public boolean isCacheTemplates() {
        return this.cacheTemplates;
    }

    public void setCacheTemplates(boolean cacheTemplates) {
        this.cacheTemplates = cacheTemplates;
    }
}

