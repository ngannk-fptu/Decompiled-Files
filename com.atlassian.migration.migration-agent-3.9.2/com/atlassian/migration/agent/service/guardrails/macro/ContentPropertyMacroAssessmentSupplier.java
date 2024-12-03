/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.confluence.setup.BootstrapManager
 *  javax.persistence.Tuple
 *  org.apache.commons.lang3.tuple.Pair
 *  org.jetbrains.annotations.NotNull
 */
package com.atlassian.migration.agent.service.guardrails.macro;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.migration.agent.service.guardrails.macro.AbstractDatabaseMacroAssessmentSupplier;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Tuple;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

class ContentPropertyMacroAssessmentSupplier
extends AbstractDatabaseMacroAssessmentSupplier {
    public ContentPropertyMacroAssessmentSupplier(EntityManagerTemplate tmpl, BootstrapManager bootstrapManager) {
        super(tmpl, bootstrapManager);
    }

    @Override
    String getQuery() {
        String emptyString = this.getEmptyString();
        return "select cp.content.id as id, cp.stringval as macros from ContentProperty cp join Content c on c.id = cp.content.id where c.previousVersion is null and cp.name = 'macroNames' and cp.stringval is not null and cp.stringval != " + emptyString;
    }

    @NotNull
    private String getEmptyString() {
        ApplicationConfiguration applicationConfig = this.bootstrapManager.getApplicationConfig();
        String dialect = (String)applicationConfig.getProperty((Object)"hibernate.dialect");
        return dialect.toLowerCase().contains("oracle") ? "' '" : "''";
    }

    @Override
    Set<Pair<String, Long>> map(Tuple tuple) {
        Long contentId = (Long)tuple.get(0, Long.class);
        String stringProps = (String)tuple.get(1, String.class);
        return ContentPropertyMacroAssessmentSupplier.createContentMacroPairs(stringProps, contentId);
    }

    @NotNull
    private static Set<Pair<String, Long>> createContentMacroPairs(String stringProps, Long contentId) {
        return Arrays.stream(stringProps.split(",")).map((? super T macro) -> Pair.of((Object)macro, (Object)contentId)).collect(Collectors.toSet());
    }
}

