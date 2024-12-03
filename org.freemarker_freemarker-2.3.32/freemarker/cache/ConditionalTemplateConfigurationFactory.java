/*
 * Decompiled with CFR 0.152.
 */
package freemarker.cache;

import freemarker.cache.TemplateConfigurationFactory;
import freemarker.cache.TemplateConfigurationFactoryException;
import freemarker.cache.TemplateSourceMatcher;
import freemarker.core.TemplateConfiguration;
import freemarker.template.Configuration;
import java.io.IOException;

public class ConditionalTemplateConfigurationFactory
extends TemplateConfigurationFactory {
    private final TemplateSourceMatcher matcher;
    private final TemplateConfiguration templateConfiguration;
    private final TemplateConfigurationFactory templateConfigurationFactory;

    public ConditionalTemplateConfigurationFactory(TemplateSourceMatcher matcher, TemplateConfigurationFactory templateConfigurationFactory) {
        this.matcher = matcher;
        this.templateConfiguration = null;
        this.templateConfigurationFactory = templateConfigurationFactory;
    }

    public ConditionalTemplateConfigurationFactory(TemplateSourceMatcher matcher, TemplateConfiguration templateConfiguration) {
        this.matcher = matcher;
        this.templateConfiguration = templateConfiguration;
        this.templateConfigurationFactory = null;
    }

    @Override
    public TemplateConfiguration get(String sourceName, Object templateSource) throws IOException, TemplateConfigurationFactoryException {
        if (this.matcher.matches(sourceName, templateSource)) {
            if (this.templateConfigurationFactory != null) {
                return this.templateConfigurationFactory.get(sourceName, templateSource);
            }
            return this.templateConfiguration;
        }
        return null;
    }

    @Override
    protected void setConfigurationOfChildren(Configuration cfg) {
        if (this.templateConfiguration != null) {
            this.templateConfiguration.setParentConfiguration(cfg);
        }
        if (this.templateConfigurationFactory != null) {
            this.templateConfigurationFactory.setConfiguration(cfg);
        }
    }
}

