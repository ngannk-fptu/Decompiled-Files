/*
 * Decompiled with CFR 0.152.
 */
package freemarker.cache;

import freemarker.cache.TemplateConfigurationFactory;
import freemarker.cache.TemplateConfigurationFactoryException;
import freemarker.core.TemplateConfiguration;
import freemarker.template.Configuration;
import java.io.IOException;

public class MergingTemplateConfigurationFactory
extends TemplateConfigurationFactory {
    private final TemplateConfigurationFactory[] templateConfigurationFactories;

    public MergingTemplateConfigurationFactory(TemplateConfigurationFactory ... templateConfigurationFactories) {
        this.templateConfigurationFactories = templateConfigurationFactories;
    }

    @Override
    public TemplateConfiguration get(String sourceName, Object templateSource) throws IOException, TemplateConfigurationFactoryException {
        TemplateConfiguration mergedTC = null;
        TemplateConfiguration resultTC = null;
        for (TemplateConfigurationFactory tcf : this.templateConfigurationFactories) {
            TemplateConfiguration tc = tcf.get(sourceName, templateSource);
            if (tc == null) continue;
            if (resultTC == null) {
                resultTC = tc;
                continue;
            }
            if (mergedTC == null) {
                Configuration cfg = this.getConfiguration();
                if (cfg == null) {
                    throw new IllegalStateException("The TemplateConfigurationFactory wasn't associated to a Configuration yet.");
                }
                mergedTC = new TemplateConfiguration();
                mergedTC.setParentConfiguration(cfg);
                mergedTC.merge(resultTC);
                resultTC = mergedTC;
            }
            mergedTC.merge(tc);
        }
        return resultTC;
    }

    @Override
    protected void setConfigurationOfChildren(Configuration cfg) {
        for (TemplateConfigurationFactory templateConfigurationFactory : this.templateConfigurationFactories) {
            templateConfigurationFactory.setConfiguration(cfg);
        }
    }
}

