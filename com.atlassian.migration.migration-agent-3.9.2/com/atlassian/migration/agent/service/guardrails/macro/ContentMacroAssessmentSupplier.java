/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.BootstrapManager
 *  javax.persistence.Tuple
 *  org.apache.commons.lang3.tuple.Pair
 *  org.jsoup.Jsoup
 *  org.jsoup.nodes.Document
 *  org.jsoup.select.Elements
 */
package com.atlassian.migration.agent.service.guardrails.macro;

import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.migration.agent.service.guardrails.macro.AbstractDatabaseMacroAssessmentSupplier;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Tuple;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

class ContentMacroAssessmentSupplier
extends AbstractDatabaseMacroAssessmentSupplier {
    public ContentMacroAssessmentSupplier(EntityManagerTemplate tmpl, BootstrapManager bootstrapManager) {
        super(tmpl, bootstrapManager);
    }

    @Override
    String getQuery() {
        return "select bc.body, bc.contentId from BodyContent bc join Content c on c.id = bc.contentId where c.previousVersion is null and bc.body like '%ac:structured-macro%'";
    }

    @Override
    Set<Pair<String, Long>> map(Tuple tuple) {
        Long contentId = (Long)tuple.get(1, Long.class);
        Document html = Jsoup.parse((String)((String)tuple.get(0, String.class)));
        Elements macroTags = html.select("ac|structured-macro");
        return macroTags.stream().map((? super T tag) -> Pair.of((Object)tag.attr("ac:name"), (Object)contentId)).collect(Collectors.toSet());
    }
}

