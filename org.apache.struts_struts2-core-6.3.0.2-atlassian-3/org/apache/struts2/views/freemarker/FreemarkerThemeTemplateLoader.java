/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  freemarker.cache.TemplateLoader
 */
package org.apache.struts2.views.freemarker;

import com.opensymphony.xwork2.inject.Inject;
import freemarker.cache.TemplateLoader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import org.apache.struts2.components.template.Template;
import org.apache.struts2.components.template.TemplateEngine;

public class FreemarkerThemeTemplateLoader
implements TemplateLoader {
    private TemplateLoader parentTemplateLoader;
    private String themeExpansionToken;
    private TemplateEngine templateEngine;

    public void init(TemplateLoader parent) {
        this.parentTemplateLoader = parent;
    }

    public Object findTemplateSource(String name) throws IOException {
        int tokenIndex;
        int n = tokenIndex = name == null ? -1 : name.indexOf(this.themeExpansionToken);
        if (tokenIndex < 0) {
            return this.parentTemplateLoader.findTemplateSource(name);
        }
        int themeEndIndex = name.lastIndexOf(47);
        if (themeEndIndex < 0) {
            return this.parentTemplateLoader.findTemplateSource(name);
        }
        Template template = new Template(name.substring(0, tokenIndex - 1), name.substring(tokenIndex + this.themeExpansionToken.length(), themeEndIndex), name.substring(themeEndIndex + 1));
        List<Template> possibleTemplates = template.getPossibleTemplates(this.templateEngine);
        for (Template possibleTemplate : possibleTemplates) {
            Object templateSource = this.parentTemplateLoader.findTemplateSource(possibleTemplate.toString().substring(1));
            if (templateSource == null) continue;
            return templateSource;
        }
        String parentTheme = (String)this.templateEngine.getThemeProps(template).get("parent");
        if (parentTheme == null) {
            return null;
        }
        String parentName = "/" + template.getDir() + "/" + this.themeExpansionToken + parentTheme + "/" + template.getName();
        return this.findTemplateSource(parentName);
    }

    public long getLastModified(Object templateSource) {
        return this.parentTemplateLoader.getLastModified(templateSource);
    }

    public Reader getReader(Object templateSource, String encoding) throws IOException {
        return this.parentTemplateLoader.getReader(templateSource, encoding);
    }

    public void closeTemplateSource(Object templateSource) throws IOException {
        this.parentTemplateLoader.closeTemplateSource(templateSource);
    }

    @Inject(value="struts.ui.theme.expansion.token")
    public void setUIThemeExpansionToken(String token) {
        this.themeExpansionToken = token;
    }

    @Inject(value="ftl")
    public void setTemplateEngine(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public TemplateLoader getParentTemplateLoader() {
        return this.parentTemplateLoader;
    }
}

