/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.keyvalue.core.KeyValueOperations
 *  org.springframework.data.keyvalue.repository.query.KeyValuePartTreeQuery
 *  org.springframework.data.keyvalue.repository.query.KeyValuePartTreeQuery$QueryCreatorFactory
 *  org.springframework.data.mapping.context.MappingContext
 *  org.springframework.data.repository.query.ParameterAccessor
 *  org.springframework.data.repository.query.QueryMethod
 *  org.springframework.data.repository.query.QueryMethodEvaluationContextProvider
 *  org.springframework.data.repository.query.parser.AbstractQueryCreator
 *  org.springframework.data.repository.query.parser.PartTree
 */
package org.springframework.vault.repository.query;

import org.springframework.data.keyvalue.core.KeyValueOperations;
import org.springframework.data.keyvalue.repository.query.KeyValuePartTreeQuery;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.repository.query.ParameterAccessor;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.repository.query.parser.AbstractQueryCreator;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.vault.repository.mapping.VaultPersistentEntity;
import org.springframework.vault.repository.mapping.VaultPersistentProperty;
import org.springframework.vault.repository.query.VaultQueryCreator;

public class VaultPartTreeQuery
extends KeyValuePartTreeQuery {
    public VaultPartTreeQuery(QueryMethod queryMethod, QueryMethodEvaluationContextProvider evaluationContextProvider, KeyValueOperations keyValueOperations, Class<? extends AbstractQueryCreator<?, ?>> queryCreator) {
        super(queryMethod, evaluationContextProvider, keyValueOperations, (KeyValuePartTreeQuery.QueryCreatorFactory)new VaultQueryCreatorFactory(keyValueOperations.getMappingContext()));
    }

    static class VaultQueryCreatorFactory
    implements KeyValuePartTreeQuery.QueryCreatorFactory<VaultQueryCreator> {
        private final MappingContext<VaultPersistentEntity<?>, VaultPersistentProperty> mappingContext;

        public VaultQueryCreatorFactory(MappingContext<VaultPersistentEntity<?>, VaultPersistentProperty> mappingContext) {
            this.mappingContext = mappingContext;
        }

        public VaultQueryCreator queryCreatorFor(PartTree partTree, ParameterAccessor accessor) {
            return new VaultQueryCreator(partTree, accessor, this.mappingContext);
        }
    }
}

