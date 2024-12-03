/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.macro.xhtml.MacroMigration
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  com.google.common.base.Strings
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.masterdetail.cqlmigrator;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.extra.masterdetail.cqlmigrator.CompositeQueryExpression;
import com.atlassian.confluence.extra.masterdetail.cqlmigrator.EmptyQueryExpression;
import com.atlassian.confluence.extra.masterdetail.cqlmigrator.ParameterSanitiser;
import com.atlassian.confluence.extra.masterdetail.cqlmigrator.QueryExpression;
import com.atlassian.confluence.extra.masterdetail.cqlmigrator.SimpleQueryExpression;
import com.atlassian.confluence.macro.xhtml.MacroMigration;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.google.common.base.Strings;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DetailsSummaryMacroCqlSchemaMigrator
implements MacroMigration {
    private static final Logger log = LoggerFactory.getLogger(DetailsSummaryMacroCqlSchemaMigrator.class);

    public MacroDefinition migrate(MacroDefinition macro, ConversionContext context) {
        if (Strings.isNullOrEmpty((String)macro.getParameter("cql"))) {
            String cql = this.getCql(macro);
            macro.setParameter("cql", cql);
            macro.setTypedParameter("cql", (Object)cql);
            log.debug("CQL parameter set to '{}'", (Object)cql);
        }
        macro.setSchemaVersion(2);
        return macro;
    }

    private String getCql(MacroDefinition macro) {
        CompositeQueryExpression.Builder builder = CompositeQueryExpression.builder(CompositeQueryExpression.BooleanOperator.AND);
        builder.add(this.getLabelExpression(macro));
        builder.add(this.getSpaceExpression(macro));
        return builder.build().toQueryString();
    }

    private QueryExpression getSpaceExpression(MacroDefinition macro) {
        String spaces = ParameterSanitiser.getParameter("spaces", macro);
        List<String> spaceKeys = ParameterSanitiser.getSpaceKeysFromDelimitedString(spaces);
        if (spaceKeys.isEmpty()) {
            spaceKeys.add("currentSpace()");
        } else if (spaceKeys.contains("@all")) {
            return EmptyQueryExpression.EMPTY;
        }
        return SimpleQueryExpression.of("space", spaceKeys);
    }

    private QueryExpression getLabelExpression(MacroDefinition macro) {
        String label = ParameterSanitiser.getParameter("label", macro);
        return SimpleQueryExpression.of("label", label);
    }
}

