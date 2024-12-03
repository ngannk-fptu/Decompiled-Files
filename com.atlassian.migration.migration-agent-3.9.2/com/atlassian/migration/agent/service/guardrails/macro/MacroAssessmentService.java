/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.BootstrapManager
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.guardrails.macro;

import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.migration.agent.service.guardrails.macro.ContentMacroAssessmentSupplier;
import com.atlassian.migration.agent.service.guardrails.macro.ContentPropertyMacroAssessmentSupplier;
import com.atlassian.migration.agent.service.guardrails.macro.MacroAssessment;
import com.atlassian.migration.agent.service.guardrails.macro.MacroAssessmentResult;
import com.atlassian.migration.agent.service.guardrails.macro.MacroAssessmentSupplier;
import com.atlassian.migration.agent.service.guardrails.macro.StaticMacroInformation;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.lang.invoke.LambdaMetafactory;
import java.util.Arrays;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MacroAssessmentService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(MacroAssessmentService.class);
    private final EntityManagerTemplate tmpl;
    private final BootstrapManager bootstrapManager;

    public MacroAssessmentResult assess(String contentSupplier) {
        long start = System.currentTimeMillis();
        MacroAssessmentSupplier querySupplier = ContentSupplierType.getMacroAssessmentSupplier(contentSupplier, this.tmpl, this.bootstrapManager);
        Set<MacroAssessment> macroAssessments = querySupplier.fetch();
        MacroAssessmentResult macroAssessmentResult = new MacroAssessmentResult(macroAssessments, new StaticMacroInformation());
        long finish = System.currentTimeMillis();
        log.info("Assessment completed in {}ms", (Object)(finish - start));
        return macroAssessmentResult;
    }

    @Generated
    public MacroAssessmentService(EntityManagerTemplate tmpl, BootstrapManager bootstrapManager) {
        this.tmpl = tmpl;
        this.bootstrapManager = bootstrapManager;
    }

    private static enum ContentSupplierType {
        CONTENT(ContentMacroAssessmentSupplier::new),
        PROPERTY(ContentPropertyMacroAssessmentSupplier::new),
        DEFAULT(ContentPropertyMacroAssessmentSupplier::new);

        private final BiFunction<EntityManagerTemplate, BootstrapManager, MacroAssessmentSupplier> macroAssessmentSupplier;

        private ContentSupplierType(BiFunction<EntityManagerTemplate, BootstrapManager, MacroAssessmentSupplier> macroAssessmentSupplier) {
            this.macroAssessmentSupplier = macroAssessmentSupplier;
        }

        static MacroAssessmentSupplier getMacroAssessmentSupplier(String contentSupplier, EntityManagerTemplate tmpl, BootstrapManager bootstrapManager) {
            return Arrays.stream(ContentSupplierType.values()).filter((Predicate<ContentSupplierType>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Z, lambda$getMacroAssessmentSupplier$0(java.lang.String com.atlassian.migration.agent.service.guardrails.macro.MacroAssessmentService$ContentSupplierType ), (Lcom/atlassian/migration/agent/service/guardrails/macro/MacroAssessmentService$ContentSupplierType;)Z)((String)contentSupplier)).findFirst().orElse((ContentSupplierType)ContentSupplierType.DEFAULT).macroAssessmentSupplier.apply(tmpl, bootstrapManager);
        }

        private static /* synthetic */ boolean lambda$getMacroAssessmentSupplier$0(String contentSupplier, ContentSupplierType type) {
            return type.name().equalsIgnoreCase(contentSupplier);
        }
    }
}

