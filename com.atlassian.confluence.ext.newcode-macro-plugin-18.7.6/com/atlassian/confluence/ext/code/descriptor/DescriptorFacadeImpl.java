/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.ext.code.descriptor;

import com.atlassian.confluence.ext.code.descriptor.BrushDefinition;
import com.atlassian.confluence.ext.code.descriptor.ConfluenceStrategy;
import com.atlassian.confluence.ext.code.descriptor.DescriptorFacade;
import com.atlassian.confluence.ext.code.descriptor.ThemeDefinition;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class DescriptorFacadeImpl
implements DescriptorFacade {
    private static final Logger LOG = LoggerFactory.getLogger(DescriptorFacadeImpl.class);
    private final ConfluenceStrategy strategy;

    @Autowired
    public DescriptorFacadeImpl(ConfluenceStrategy loadingStrategy) {
        this.strategy = loadingStrategy;
    }

    @Override
    public BrushDefinition[] listBuiltinBrushes() {
        LOG.debug("Retrieving declared brushes");
        BrushDefinition[] result = this.strategy.listBuiltinBrushes();
        if (LOG.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            for (BrushDefinition brush : result) {
                builder.append(brush.getLocation()).append(',');
            }
            builder.append("]");
            LOG.debug("Declared brushes retrieved: {}", (Object)builder);
        }
        return result;
    }

    @Override
    public ThemeDefinition[] listBuiltinThemes() {
        LOG.debug("Retrieving declared themes");
        ThemeDefinition[] result = this.strategy.listBuiltinThemes();
        if (LOG.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            for (ThemeDefinition tmp : result) {
                builder.append(tmp.getLocation()).append(',');
            }
            builder.append("]");
            LOG.debug("Declared brushes themes: {}", (Object)builder);
        }
        return result;
    }

    @Override
    public List<String> listLocalization() {
        LOG.debug("Retrieving declared localization");
        List<String> result = this.strategy.listLocalization();
        LOG.debug("Declared localizations: {}", result);
        return result;
    }
}

