/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.struts2.components.template;

import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.components.template.Template;
import org.apache.struts2.components.template.TemplateEngine;

public class TemplateEngineManager {
    public static final String DEFAULT_TEMPLATE_TYPE = "ftl";
    Map<String, EngineFactory> templateEngines = new HashMap<String, EngineFactory>();
    Container container;
    String defaultTemplateType;

    @Inject(value="struts.ui.templateSuffix")
    public void setDefaultTemplateType(String type) {
        this.defaultTemplateType = type;
    }

    @Inject
    public void setContainer(Container container) {
        this.container = container;
        HashMap<String, LazyEngineFactory> map = new HashMap<String, LazyEngineFactory>();
        Set<String> prefixes = container.getInstanceNames(TemplateEngine.class);
        for (String prefix : prefixes) {
            map.put(prefix, new LazyEngineFactory(prefix));
        }
        this.templateEngines = Collections.unmodifiableMap(map);
    }

    public void registerTemplateEngine(String templateExtension, final TemplateEngine templateEngine) {
        this.templateEngines.put(templateExtension, new EngineFactory(){

            @Override
            public TemplateEngine create() {
                return templateEngine;
            }
        });
    }

    public TemplateEngine getTemplateEngine(Template template, String templateTypeOverride) {
        String templateType = DEFAULT_TEMPLATE_TYPE;
        String templateName = template.toString();
        if (StringUtils.contains((CharSequence)templateName, (CharSequence)".")) {
            templateType = StringUtils.substring((String)templateName, (int)(StringUtils.indexOf((CharSequence)templateName, (CharSequence)".") + 1));
        } else if (StringUtils.isNotBlank((CharSequence)templateTypeOverride)) {
            templateType = templateTypeOverride;
        } else {
            String type = this.defaultTemplateType;
            if (type != null) {
                templateType = type;
            }
        }
        return this.templateEngines.get(templateType).create();
    }

    class LazyEngineFactory
    implements EngineFactory {
        private String name;

        public LazyEngineFactory(String name) {
            this.name = name;
        }

        @Override
        public TemplateEngine create() {
            TemplateEngine engine = TemplateEngineManager.this.container.getInstance(TemplateEngine.class, this.name);
            if (engine == null) {
                throw new ConfigurationException("Unable to locate template engine: " + this.name);
            }
            return engine;
        }
    }

    static interface EngineFactory {
        public TemplateEngine create();
    }
}

