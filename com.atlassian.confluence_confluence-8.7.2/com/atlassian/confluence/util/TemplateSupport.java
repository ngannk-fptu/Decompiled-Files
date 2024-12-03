/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.context.OutputMimeTypeAwareVelocityContext
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.velocity.Template
 *  org.apache.velocity.context.Context
 *  org.apache.velocity.exception.ParseErrorException
 *  org.apache.velocity.exception.ResourceNotFoundException
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.themes.Theme;
import com.atlassian.confluence.themes.ThemeManager;
import com.atlassian.confluence.themes.ThemedDecorator;
import com.atlassian.confluence.themes.VelocityDecorator;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.confluence.velocity.context.OutputMimeTypeAwareVelocityContext;
import java.io.Writer;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

public final class TemplateSupport {
    private ThemeManager themeManager;
    private OutputMimeTypeAwareVelocityContext context;
    private String templateExtension;
    private boolean exportChildren;

    public TemplateSupport(ThemeManager themeManager, String template_extension) {
        this.templateExtension = template_extension;
        this.themeManager = themeManager;
        this.resetContext();
    }

    private void resetContext() {
        this.context = new OutputMimeTypeAwareVelocityContext();
    }

    public void setOutputMimeType(String mimeType) {
        this.context.setOutputMimeType(mimeType);
    }

    public void putInContext(String key, Object model_obj) {
        this.context.put(key, model_obj);
    }

    public Object getFromContext(String key) {
        return this.context.get(key);
    }

    public void processTemplate(Object obj, Writer output) throws Exception {
        Object template_path = TemplateSupport.classToTemplatePath(ConfluenceEntityObject.getRealClass(obj));
        if (this.exportChildren) {
            template_path = (String)template_path + "-hierarchy";
        }
        template_path = (String)template_path + "." + this.templateExtension;
        Template template = this.getTemplate(obj, (String)template_path);
        VelocityUtils.renderTemplateWithoutSwallowingErrors(template.getName(), (Context)this.context, output);
        output.flush();
        output.close();
    }

    public static String classToTemplatePath(Class klass) {
        return klass.getName().replace('.', '/');
    }

    private Template getTemplate(Object obj, String templatePath) throws Exception {
        String spaceKey = this.getSpaceKey(obj);
        Template template = null;
        if (StringUtils.isNotEmpty((CharSequence)spaceKey) && (template = this.getTemplateFromTheme(this.themeManager.getSpaceTheme(spaceKey), templatePath)) == null) {
            template = this.getSpaceSpecificTemplate(spaceKey, templatePath);
        }
        if (template == null) {
            template = this.getTemplateFromTheme(this.themeManager.getGlobalTheme(), templatePath);
        }
        if (template == null) {
            template = VelocityUtils.getTemplate(templatePath);
        }
        return template;
    }

    private Template getTemplateFromTheme(Theme theme, String templatePath) throws Exception {
        ThemedDecorator decorator;
        if (theme != null && (decorator = theme.getDecorator(templatePath)) != null && decorator instanceof VelocityDecorator) {
            return VelocityUtils.getTemplate(((VelocityDecorator)decorator).getVelocityTemplatePath());
        }
        return null;
    }

    private Template getSpaceSpecificTemplate(String spaceKey, String template_path) throws ParseErrorException, Exception {
        if (spaceKey != null) {
            String spaceDecoratedPath = "@" + spaceKey + "/" + template_path;
            try {
                return VelocityUtils.getTemplate(spaceDecoratedPath);
            }
            catch (ResourceNotFoundException resourceNotFoundException) {
                // empty catch block
            }
        }
        return null;
    }

    private String getSpaceKey(Object obj) {
        String space = null;
        if (obj instanceof SpaceContentEntityObject) {
            space = ((SpaceContentEntityObject)obj).getSpace().getKey();
        }
        if (obj instanceof Space) {
            space = ((Space)obj).getKey();
        }
        return space;
    }

    public void setExportChildren(boolean exportChildren) {
        this.exportChildren = exportChildren;
    }
}

