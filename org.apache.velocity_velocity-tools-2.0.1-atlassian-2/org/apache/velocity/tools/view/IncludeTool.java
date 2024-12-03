/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.apache.velocity.app.VelocityEngine
 *  org.apache.velocity.exception.ResourceNotFoundException
 */
package org.apache.velocity.tools.view;

import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.InvalidScope;
import org.apache.velocity.tools.view.ViewToolContext;

@DefaultKey(value="include")
@InvalidScope(value={"application"})
public class IncludeTool {
    protected static final String DEFAULT_LANGUAGE_KEY = "org.apache.velocity.tools.view.i18n.defaultLanguage";
    protected String defaultLanguage;
    protected VelocityEngine engine;

    public void configure(Map params) {
        this.configure((ViewToolContext)params.get("velocityContext"));
    }

    protected void configure(ViewToolContext ctx) {
        this.defaultLanguage = (String)ctx.get(DEFAULT_LANGUAGE_KEY);
        if (this.defaultLanguage == null || this.defaultLanguage.trim().equals("")) {
            ServletContext sc = ctx.getServletContext();
            this.defaultLanguage = (String)sc.getAttribute(DEFAULT_LANGUAGE_KEY);
            if (this.defaultLanguage == null || this.defaultLanguage.trim().equals("")) {
                this.defaultLanguage = Locale.getDefault().getLanguage();
            }
        }
        this.engine = ctx.getVelocityEngine();
    }

    public String find(String name, Locale locale) {
        if (locale == null) {
            return null;
        }
        return this.find(name, locale.getLanguage());
    }

    public String find(String name) {
        return this.find(name, this.defaultLanguage);
    }

    public String find(String name, String language) {
        String localizedName = name + '.' + language;
        if (!this.exists(localizedName)) {
            String defaultLangSuffix = '.' + this.defaultLanguage;
            if (localizedName.endsWith(defaultLangSuffix)) {
                localizedName = name;
            } else {
                localizedName = name + defaultLangSuffix;
                if (!this.exists(localizedName)) {
                    localizedName = name;
                }
            }
        }
        return localizedName;
    }

    public boolean exists(String name) {
        try {
            return this.engine.resourceExists(name);
        }
        catch (ResourceNotFoundException rnfe) {
            return false;
        }
    }

    public boolean exists(String name, String language) {
        String localizedName = name + '.' + language;
        return this.exists(localizedName);
    }
}

