/*
 * Decompiled with CFR 0.152.
 */
package freemarker.cache;

import freemarker.cache.TemplateConfigurationFactory;
import freemarker.cache.TemplateConfigurationFactoryException;
import freemarker.core.TemplateConfiguration;
import freemarker.template.Configuration;
import freemarker.template.utility.StringUtil;
import java.io.IOException;

public class FirstMatchTemplateConfigurationFactory
extends TemplateConfigurationFactory {
    private final TemplateConfigurationFactory[] templateConfigurationFactories;
    private boolean allowNoMatch;
    private String noMatchErrorDetails;

    public FirstMatchTemplateConfigurationFactory(TemplateConfigurationFactory ... templateConfigurationFactories) {
        this.templateConfigurationFactories = templateConfigurationFactories;
    }

    @Override
    public TemplateConfiguration get(String sourceName, Object templateSource) throws IOException, TemplateConfigurationFactoryException {
        for (TemplateConfigurationFactory tcf : this.templateConfigurationFactories) {
            TemplateConfiguration tc = tcf.get(sourceName, templateSource);
            if (tc == null) continue;
            return tc;
        }
        if (!this.allowNoMatch) {
            throw new TemplateConfigurationFactoryException(FirstMatchTemplateConfigurationFactory.class.getSimpleName() + " has found no matching choice for source name " + StringUtil.jQuote(sourceName) + ". " + (this.noMatchErrorDetails != null ? "Error details: " + this.noMatchErrorDetails : "(Set the noMatchErrorDetails property of the factory bean to give a more specific error message. Set allowNoMatch to true if this shouldn't be an error.)"));
        }
        return null;
    }

    public boolean getAllowNoMatch() {
        return this.allowNoMatch;
    }

    public void setAllowNoMatch(boolean allowNoMatch) {
        this.allowNoMatch = allowNoMatch;
    }

    public String getNoMatchErrorDetails() {
        return this.noMatchErrorDetails;
    }

    public void setNoMatchErrorDetails(String noMatchErrorDetails) {
        this.noMatchErrorDetails = noMatchErrorDetails;
    }

    public FirstMatchTemplateConfigurationFactory allowNoMatch(boolean allow) {
        this.setAllowNoMatch(allow);
        return this;
    }

    public FirstMatchTemplateConfigurationFactory noMatchErrorDetails(String message) {
        this.setNoMatchErrorDetails(message);
        return this;
    }

    @Override
    protected void setConfigurationOfChildren(Configuration cfg) {
        for (TemplateConfigurationFactory templateConfigurationFactory : this.templateConfigurationFactories) {
            templateConfigurationFactory.setConfiguration(cfg);
        }
    }
}

