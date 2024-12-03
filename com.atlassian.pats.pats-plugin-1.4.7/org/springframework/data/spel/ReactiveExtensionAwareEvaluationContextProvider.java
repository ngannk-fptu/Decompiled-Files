/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.core.ResolvableType
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.spel.support.StandardEvaluationContext
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.data.spel;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.ResolvableType;
import org.springframework.data.spel.EvaluationContextExtensionInformation;
import org.springframework.data.spel.ExpressionDependencies;
import org.springframework.data.spel.ExtensionAwareEvaluationContextProvider;
import org.springframework.data.spel.ReactiveEvaluationContextProvider;
import org.springframework.data.spel.spi.EvaluationContextExtension;
import org.springframework.data.spel.spi.ExtensionIdAware;
import org.springframework.data.spel.spi.ReactiveEvaluationContextExtension;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ReactiveExtensionAwareEvaluationContextProvider
implements ReactiveEvaluationContextProvider {
    private static final ResolvableType GENERIC_EXTENSION_TYPE = ResolvableType.forClass(EvaluationContextExtension.class);
    private final ExtensionAwareEvaluationContextProvider evaluationContextProvider;

    public ReactiveExtensionAwareEvaluationContextProvider() {
        this.evaluationContextProvider = new ExtensionAwareEvaluationContextProvider();
    }

    public ReactiveExtensionAwareEvaluationContextProvider(ListableBeanFactory beanFactory) {
        this.evaluationContextProvider = new ExtensionAwareEvaluationContextProvider(beanFactory);
    }

    public ReactiveExtensionAwareEvaluationContextProvider(Collection<? extends ExtensionIdAware> extensions) {
        this.evaluationContextProvider = new ExtensionAwareEvaluationContextProvider(extensions);
    }

    @Override
    public EvaluationContext getEvaluationContext(Object rootObject) {
        return this.evaluationContextProvider.getEvaluationContext(rootObject);
    }

    @Override
    public EvaluationContext getEvaluationContext(Object rootObject, ExpressionDependencies dependencies) {
        return this.evaluationContextProvider.getEvaluationContext(rootObject, dependencies);
    }

    public Mono<StandardEvaluationContext> getEvaluationContextLater(Object rootObject) {
        return this.getExtensions(it -> true).map(it -> this.evaluationContextProvider.doGetEvaluationContext(rootObject, (Collection<? extends EvaluationContextExtension>)it));
    }

    public Mono<StandardEvaluationContext> getEvaluationContextLater(Object rootObject, ExpressionDependencies dependencies) {
        return this.getExtensions(it -> dependencies.stream().anyMatch(it::provides)).map(it -> this.evaluationContextProvider.doGetEvaluationContext(rootObject, (Collection<? extends EvaluationContextExtension>)it));
    }

    private Mono<List<EvaluationContextExtension>> getExtensions(Predicate<EvaluationContextExtensionInformation> extensionFilter) {
        Collection<? extends ExtensionIdAware> extensions = this.evaluationContextProvider.getExtensions();
        return Flux.fromIterable(extensions).concatMap(it -> {
            if (it instanceof EvaluationContextExtension) {
                EvaluationContextExtension extension = (EvaluationContextExtension)it;
                EvaluationContextExtensionInformation information = this.evaluationContextProvider.getOrCreateInformation(extension);
                if (extensionFilter.test(information)) {
                    return Mono.just((Object)extension);
                }
                return Mono.empty();
            }
            if (it instanceof ReactiveEvaluationContextExtension) {
                ReactiveEvaluationContextExtension extension = (ReactiveEvaluationContextExtension)it;
                ResolvableType actualType = ReactiveExtensionAwareEvaluationContextProvider.getExtensionType(it);
                if (actualType.equals((Object)ResolvableType.NONE) || actualType.isAssignableFrom(GENERIC_EXTENSION_TYPE)) {
                    return extension.getExtension();
                }
                EvaluationContextExtensionInformation information = this.evaluationContextProvider.getOrCreateInformation(actualType.getRawClass());
                if (extensionFilter.test(information)) {
                    return extension.getExtension();
                }
                return Mono.empty();
            }
            return Mono.error((Throwable)new IllegalStateException("Unsupported extension type: " + it));
        }).collectList();
    }

    private static ResolvableType getExtensionType(ExtensionIdAware extensionCandidate) {
        return ResolvableType.forMethodReturnType((Method)ReflectionUtils.findRequiredMethod(extensionCandidate.getClass(), "getExtension", new Class[0])).getGeneric(new int[]{0});
    }
}

