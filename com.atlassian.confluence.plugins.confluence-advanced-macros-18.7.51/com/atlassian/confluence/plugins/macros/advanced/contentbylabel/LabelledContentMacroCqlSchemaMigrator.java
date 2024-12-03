/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.macro.MacroExecutionContext
 *  com.atlassian.confluence.macro.query.params.AuthorParameter
 *  com.atlassian.confluence.macro.query.params.ContentTypeParameter
 *  com.atlassian.confluence.macro.query.params.LabelParameter
 *  com.atlassian.confluence.macro.query.params.SpaceKeyParameter
 *  com.atlassian.confluence.macro.xhtml.MacroMigration
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.spring.container.ContainerManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.macros.advanced.contentbylabel;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.MacroExecutionContext;
import com.atlassian.confluence.macro.query.params.AuthorParameter;
import com.atlassian.confluence.macro.query.params.ContentTypeParameter;
import com.atlassian.confluence.macro.query.params.LabelParameter;
import com.atlassian.confluence.macro.query.params.SpaceKeyParameter;
import com.atlassian.confluence.macro.xhtml.MacroMigration;
import com.atlassian.confluence.plugins.macros.advanced.contentbylabel.LegacyParameterConverter;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.spring.container.ContainerManager;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LabelledContentMacroCqlSchemaMigrator
implements MacroMigration {
    private static final Logger log = LoggerFactory.getLogger(LabelledContentMacroCqlSchemaMigrator.class);
    private I18NBeanFactory i18NBeanFactory;

    public MacroDefinition migrate(MacroDefinition macro, ConversionContext context) {
        String cql = this.getCql(macro, context);
        macro.setParameter("cql", cql);
        macro.setTypedParameter("cql", (Object)cql);
        log.debug("CQL parameter set to '{}'", (Object)cql);
        macro.setSchemaVersion(2);
        return macro;
    }

    private String getCql(MacroDefinition macro, ConversionContext context) {
        LabelParameter labelParam = new LabelParameter();
        labelParam.setValidate(false);
        labelParam.addParameterAlias("");
        ContentTypeParameter contentTypeParam = new ContentTypeParameter();
        SpaceKeyParameter spaceKeyParam = new SpaceKeyParameter();
        spaceKeyParam.addParameterAlias("key");
        spaceKeyParam.setDefaultValue("@all");
        AuthorParameter authorParam = new AuthorParameter();
        LegacyParameterConverter converter = new LegacyParameterConverter(this.getI18nBeanFactory().getI18NBean(), spaceKeyParam, labelParam, authorParam, contentTypeParam);
        PageContext pageContext = context.getPageContext();
        Map parameters = macro.getParameters();
        MacroExecutionContext macroContext = new MacroExecutionContext(parameters, null, pageContext);
        try {
            return converter.buildQueryStringFromLegacyParameters(parameters, macroContext);
        }
        catch (MacroException e) {
            log.debug("Exception thrown when migrating parameters to CQL", (Throwable)e);
            throw new IllegalArgumentException("Unable to migrate contentbylabel parameters", e);
        }
    }

    private I18NBeanFactory getI18nBeanFactory() {
        if (this.i18NBeanFactory == null) {
            this.i18NBeanFactory = (I18NBeanFactory)ContainerManager.getComponent((String)"i18NBeanFactory", I18NBeanFactory.class);
        }
        return this.i18NBeanFactory;
    }
}

