/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.GenerationType
 *  javax.persistence.SequenceGenerator
 *  javax.persistence.TableGenerator
 */
package org.hibernate.boot.internal;

import java.util.ArrayList;
import java.util.UUID;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;
import javax.persistence.TableGenerator;
import org.hibernate.boot.model.IdGeneratorStrategyInterpreter;
import org.hibernate.boot.model.IdentifierGeneratorDefinition;
import org.hibernate.cfg.BinderHelper;
import org.hibernate.id.IncrementGenerator;
import org.hibernate.id.MultipleHiLoPerTableGenerator;
import org.hibernate.id.UUIDGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;

public class IdGeneratorInterpreterImpl
implements IdGeneratorStrategyInterpreter {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(IdGeneratorInterpreterImpl.class);
    private IdGeneratorStrategyInterpreter fallbackInterpreter = FallbackInterpreter.INSTANCE;
    private ArrayList<IdGeneratorStrategyInterpreter> delegates;

    @Override
    public String determineGeneratorName(GenerationType generationType, IdGeneratorStrategyInterpreter.GeneratorNameDeterminationContext context) {
        if (this.delegates != null) {
            for (IdGeneratorStrategyInterpreter delegate : this.delegates) {
                String result = delegate.determineGeneratorName(generationType, context);
                if (result == null) continue;
                return result;
            }
        }
        return this.fallbackInterpreter.determineGeneratorName(generationType, context);
    }

    @Override
    public void interpretTableGenerator(TableGenerator tableGeneratorAnnotation, IdentifierGeneratorDefinition.Builder definitionBuilder) {
        this.fallbackInterpreter.interpretTableGenerator(tableGeneratorAnnotation, definitionBuilder);
        if (this.delegates != null) {
            for (IdGeneratorStrategyInterpreter delegate : this.delegates) {
                delegate.interpretTableGenerator(tableGeneratorAnnotation, definitionBuilder);
            }
        }
    }

    @Override
    public void interpretSequenceGenerator(SequenceGenerator sequenceGeneratorAnnotation, IdentifierGeneratorDefinition.Builder definitionBuilder) {
        this.fallbackInterpreter.interpretSequenceGenerator(sequenceGeneratorAnnotation, definitionBuilder);
        if (this.delegates != null) {
            for (IdGeneratorStrategyInterpreter delegate : this.delegates) {
                delegate.interpretSequenceGenerator(sequenceGeneratorAnnotation, definitionBuilder);
            }
        }
    }

    public void enableLegacyFallback() {
        this.fallbackInterpreter = LegacyFallbackInterpreter.INSTANCE;
    }

    public void disableLegacyFallback() {
        this.fallbackInterpreter = FallbackInterpreter.INSTANCE;
    }

    public void addInterpreterDelegate(IdGeneratorStrategyInterpreter delegate) {
        if (this.delegates == null) {
            this.delegates = new ArrayList();
        }
        this.delegates.add(delegate);
    }

    private static class FallbackInterpreter
    implements IdGeneratorStrategyInterpreter {
        public static final FallbackInterpreter INSTANCE = new FallbackInterpreter();

        private FallbackInterpreter() {
        }

        @Override
        public String determineGeneratorName(GenerationType generationType, IdGeneratorStrategyInterpreter.GeneratorNameDeterminationContext context) {
            switch (generationType) {
                case IDENTITY: {
                    return "identity";
                }
                case SEQUENCE: {
                    return SequenceStyleGenerator.class.getName();
                }
                case TABLE: {
                    return org.hibernate.id.enhanced.TableGenerator.class.getName();
                }
            }
            if ("increment".equalsIgnoreCase(context.getGeneratedValueGeneratorName())) {
                return IncrementGenerator.class.getName();
            }
            Class javaType = context.getIdType();
            if (UUID.class.isAssignableFrom(javaType)) {
                return UUIDGenerator.class.getName();
            }
            return SequenceStyleGenerator.class.getName();
        }

        @Override
        public void interpretTableGenerator(TableGenerator tableGeneratorAnnotation, IdentifierGeneratorDefinition.Builder definitionBuilder) {
            definitionBuilder.setName(tableGeneratorAnnotation.name());
            definitionBuilder.setStrategy(org.hibernate.id.enhanced.TableGenerator.class.getName());
            definitionBuilder.addParam("prefer_entity_table_as_segment_value", "true");
            if (!BinderHelper.isEmptyAnnotationValue(tableGeneratorAnnotation.catalog())) {
                definitionBuilder.addParam("catalog", tableGeneratorAnnotation.catalog());
            }
            if (!BinderHelper.isEmptyAnnotationValue(tableGeneratorAnnotation.schema())) {
                definitionBuilder.addParam("schema", tableGeneratorAnnotation.schema());
            }
            if (!BinderHelper.isEmptyAnnotationValue(tableGeneratorAnnotation.table())) {
                definitionBuilder.addParam("table_name", tableGeneratorAnnotation.table());
            }
            if (!BinderHelper.isEmptyAnnotationValue(tableGeneratorAnnotation.pkColumnName())) {
                definitionBuilder.addParam("segment_column_name", tableGeneratorAnnotation.pkColumnName());
            }
            if (!BinderHelper.isEmptyAnnotationValue(tableGeneratorAnnotation.pkColumnValue())) {
                definitionBuilder.addParam("segment_value", tableGeneratorAnnotation.pkColumnValue());
            }
            if (!BinderHelper.isEmptyAnnotationValue(tableGeneratorAnnotation.valueColumnName())) {
                definitionBuilder.addParam("value_column_name", tableGeneratorAnnotation.valueColumnName());
            }
            definitionBuilder.addParam("increment_size", String.valueOf(tableGeneratorAnnotation.allocationSize()));
            definitionBuilder.addParam("initial_value", String.valueOf(tableGeneratorAnnotation.initialValue() + 1));
            if (tableGeneratorAnnotation.uniqueConstraints() != null && tableGeneratorAnnotation.uniqueConstraints().length > 0) {
                log.ignoringTableGeneratorConstraints(tableGeneratorAnnotation.name());
            }
        }

        @Override
        public void interpretSequenceGenerator(SequenceGenerator sequenceGeneratorAnnotation, IdentifierGeneratorDefinition.Builder definitionBuilder) {
            definitionBuilder.setName(sequenceGeneratorAnnotation.name());
            definitionBuilder.setStrategy(SequenceStyleGenerator.class.getName());
            if (!BinderHelper.isEmptyAnnotationValue(sequenceGeneratorAnnotation.catalog())) {
                definitionBuilder.addParam("catalog", sequenceGeneratorAnnotation.catalog());
            }
            if (!BinderHelper.isEmptyAnnotationValue(sequenceGeneratorAnnotation.schema())) {
                definitionBuilder.addParam("schema", sequenceGeneratorAnnotation.schema());
            }
            if (!BinderHelper.isEmptyAnnotationValue(sequenceGeneratorAnnotation.sequenceName())) {
                definitionBuilder.addParam("sequence_name", sequenceGeneratorAnnotation.sequenceName());
            }
            definitionBuilder.addParam("increment_size", String.valueOf(sequenceGeneratorAnnotation.allocationSize()));
            definitionBuilder.addParam("initial_value", String.valueOf(sequenceGeneratorAnnotation.initialValue()));
        }
    }

    private static class LegacyFallbackInterpreter
    implements IdGeneratorStrategyInterpreter {
        public static final LegacyFallbackInterpreter INSTANCE = new LegacyFallbackInterpreter();

        private LegacyFallbackInterpreter() {
        }

        @Override
        public String determineGeneratorName(GenerationType generationType, IdGeneratorStrategyInterpreter.GeneratorNameDeterminationContext context) {
            switch (generationType) {
                case IDENTITY: {
                    return "identity";
                }
                case SEQUENCE: {
                    return "seqhilo";
                }
                case TABLE: {
                    return MultipleHiLoPerTableGenerator.class.getName();
                }
            }
            if ("increment".equalsIgnoreCase(context.getGeneratedValueGeneratorName())) {
                return IncrementGenerator.class.getName();
            }
            Class javaType = context.getIdType();
            if (UUID.class.isAssignableFrom(javaType)) {
                return UUIDGenerator.class.getName();
            }
            return "native";
        }

        @Override
        public void interpretTableGenerator(TableGenerator tableGeneratorAnnotation, IdentifierGeneratorDefinition.Builder definitionBuilder) {
            definitionBuilder.setName(tableGeneratorAnnotation.name());
            definitionBuilder.setStrategy(MultipleHiLoPerTableGenerator.class.getName());
            if (!BinderHelper.isEmptyAnnotationValue(tableGeneratorAnnotation.table())) {
                definitionBuilder.addParam("table", tableGeneratorAnnotation.table());
            }
            if (!BinderHelper.isEmptyAnnotationValue(tableGeneratorAnnotation.catalog())) {
                definitionBuilder.addParam("catalog", tableGeneratorAnnotation.catalog());
            }
            if (!BinderHelper.isEmptyAnnotationValue(tableGeneratorAnnotation.schema())) {
                definitionBuilder.addParam("schema", tableGeneratorAnnotation.schema());
            }
            if (!BinderHelper.isEmptyAnnotationValue(tableGeneratorAnnotation.pkColumnName())) {
                definitionBuilder.addParam("primary_key_column", tableGeneratorAnnotation.pkColumnName());
            }
            if (!BinderHelper.isEmptyAnnotationValue(tableGeneratorAnnotation.valueColumnName())) {
                definitionBuilder.addParam("value_column", tableGeneratorAnnotation.valueColumnName());
            }
            if (!BinderHelper.isEmptyAnnotationValue(tableGeneratorAnnotation.pkColumnValue())) {
                definitionBuilder.addParam("primary_key_value", tableGeneratorAnnotation.pkColumnValue());
            }
            definitionBuilder.addParam("max_lo", String.valueOf(tableGeneratorAnnotation.allocationSize() - 1));
            if (tableGeneratorAnnotation.uniqueConstraints() != null && tableGeneratorAnnotation.uniqueConstraints().length > 0) {
                log.ignoringTableGeneratorConstraints(tableGeneratorAnnotation.name());
            }
        }

        @Override
        public void interpretSequenceGenerator(SequenceGenerator sequenceGeneratorAnnotation, IdentifierGeneratorDefinition.Builder definitionBuilder) {
            definitionBuilder.setName(sequenceGeneratorAnnotation.name());
            definitionBuilder.setStrategy("seqhilo");
            if (!BinderHelper.isEmptyAnnotationValue(sequenceGeneratorAnnotation.sequenceName())) {
                definitionBuilder.addParam("sequence", sequenceGeneratorAnnotation.sequenceName());
            }
            if (sequenceGeneratorAnnotation.initialValue() != 1) {
                log.unsupportedInitialValue("hibernate.id.new_generator_mappings");
            }
            definitionBuilder.addParam("max_lo", String.valueOf(sequenceGeneratorAnnotation.allocationSize() - 1));
        }
    }
}

