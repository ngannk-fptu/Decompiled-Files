/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.GenerationType
 *  javax.persistence.SequenceGenerator
 *  javax.persistence.TableGenerator
 */
package org.hibernate.boot.model;

import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;
import javax.persistence.TableGenerator;
import org.hibernate.boot.model.IdentifierGeneratorDefinition;

public interface IdGeneratorStrategyInterpreter {
    public String determineGeneratorName(GenerationType var1, GeneratorNameDeterminationContext var2);

    public void interpretTableGenerator(TableGenerator var1, IdentifierGeneratorDefinition.Builder var2);

    public void interpretSequenceGenerator(SequenceGenerator var1, IdentifierGeneratorDefinition.Builder var2);

    public static interface GeneratorNameDeterminationContext {
        public Class getIdType();

        public String getGeneratedValueGeneratorName();
    }
}

