/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.repository.cdi;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.support.QueryCreationListener;
import org.springframework.data.repository.core.support.RepositoryProxyPostProcessor;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;

public interface CdiRepositoryConfiguration {
    default public Optional<QueryMethodEvaluationContextProvider> getEvaluationContextProvider() {
        return Optional.empty();
    }

    default public Optional<NamedQueries> getNamedQueries() {
        return Optional.empty();
    }

    default public Optional<QueryLookupStrategy.Key> getQueryLookupStrategy() {
        return Optional.empty();
    }

    default public Optional<Class<?>> getRepositoryBeanClass() {
        return Optional.empty();
    }

    default public String getRepositoryImplementationPostfix() {
        return "Impl";
    }

    default public List<RepositoryProxyPostProcessor> getRepositoryProxyPostProcessors() {
        return Collections.emptyList();
    }

    default public List<QueryCreationListener<?>> getQueryCreationListeners() {
        return Collections.emptyList();
    }
}

