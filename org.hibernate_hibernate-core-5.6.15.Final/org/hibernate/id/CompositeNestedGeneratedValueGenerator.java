/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.model.relational.ExportableProducer;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.IdentifierGeneratorAggregator;

public class CompositeNestedGeneratedValueGenerator
implements IdentifierGenerator,
Serializable,
IdentifierGeneratorAggregator {
    private final GenerationContextLocator generationContextLocator;
    private List<GenerationPlan> generationPlans = new ArrayList<GenerationPlan>();

    public CompositeNestedGeneratedValueGenerator(GenerationContextLocator generationContextLocator) {
        this.generationContextLocator = generationContextLocator;
    }

    public void addGeneratedValuePlan(GenerationPlan plan) {
        this.generationPlans.add(plan);
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        Serializable context = this.generationContextLocator.locateGenerationContext(session, object);
        Iterator<GenerationPlan> iterator = this.generationPlans.iterator();
        while (iterator.hasNext()) {
            GenerationPlan generationPlan;
            GenerationPlan plan = generationPlan = iterator.next();
            plan.execute(session, object, context);
        }
        return context;
    }

    @Override
    public void registerExportables(Database database) {
        for (GenerationPlan plan : this.generationPlans) {
            plan.registerExportables(database);
        }
    }

    @Override
    public void initialize(SqlStringGenerationContext context) {
        for (GenerationPlan plan : this.generationPlans) {
            plan.initialize(context);
        }
    }

    public static interface GenerationPlan
    extends ExportableProducer {
        default public void initialize(SqlStringGenerationContext context) {
        }

        public void execute(SharedSessionContractImplementor var1, Object var2, Object var3);
    }

    public static interface GenerationContextLocator {
        public Serializable locateGenerationContext(SharedSessionContractImplementor var1, Object var2);
    }
}

