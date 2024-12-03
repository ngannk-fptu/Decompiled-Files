/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.web.reactive.function.client.WebClient
 *  org.springframework.web.reactive.function.client.WebClient$RequestBodySpec
 *  reactor.core.publisher.Mono
 *  reactor.core.scheduler.Schedulers
 */
package org.springframework.vault.authentication;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.vault.VaultException;
import org.springframework.vault.authentication.AuthenticationSteps;
import org.springframework.vault.authentication.LoginTokenUtil;
import org.springframework.vault.authentication.ResourceCredentialSupplier;
import org.springframework.vault.authentication.VaultLoginException;
import org.springframework.vault.authentication.VaultTokenSupplier;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultToken;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class AuthenticationStepsOperator
implements VaultTokenSupplier {
    private static final Log logger = LogFactory.getLog(AuthenticationStepsOperator.class);
    private final AuthenticationSteps chain;
    private final WebClient webClient;
    private final DataBufferFactory factory = new DefaultDataBufferFactory();

    public AuthenticationStepsOperator(AuthenticationSteps steps, WebClient webClient) {
        Assert.notNull((Object)steps, "AuthenticationSteps must not be null");
        Assert.notNull((Object)webClient, "WebClient must not be null");
        this.chain = steps;
        this.webClient = webClient;
    }

    @Override
    public Mono<VaultToken> getVaultToken() throws VaultException {
        Mono<Object> state = this.createMono(this.chain.steps);
        return state.map(stateObject -> {
            if (stateObject instanceof VaultToken) {
                return (VaultToken)stateObject;
            }
            if (stateObject instanceof VaultResponse) {
                VaultResponse response = (VaultResponse)stateObject;
                Assert.state(response.getAuth() != null, "Auth field must not be null");
                return LoginTokenUtil.from(response.getAuth());
            }
            throw new IllegalStateException(String.format("Cannot retrieve VaultToken from authentication chain. Got instead %s", stateObject));
        }).onErrorMap(t -> new VaultLoginException("Cannot retrieve VaultToken from authentication chain", (Throwable)t));
    }

    private Mono<Object> createMono(Iterable<AuthenticationSteps.Node<?>> steps) {
        Mono state = Mono.just((Object)((Object)Undefinded.UNDEFINDED));
        for (AuthenticationSteps.Node<?> o : steps) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Executing %s with current state %s", o, state));
            }
            if (o instanceof AuthenticationSteps.HttpRequestNode) {
                state = state.flatMap(stateObject -> this.doHttpRequest((AuthenticationSteps.HttpRequestNode)o, stateObject));
            }
            if (o instanceof AuthenticationSteps.MapStep) {
                state = state.map(stateObject -> AuthenticationStepsOperator.doMapStep((AuthenticationSteps.MapStep)o, stateObject));
            }
            if (o instanceof AuthenticationSteps.ZipStep) {
                state = state.zipWith(this.doZipStep((AuthenticationSteps.ZipStep)o)).map(it -> AuthenticationSteps.Pair.of(it.getT1(), it.getT2()));
            }
            if (o instanceof AuthenticationSteps.OnNextStep) {
                state = state.doOnNext(stateObject -> AuthenticationStepsOperator.doOnNext((AuthenticationSteps.OnNextStep)o, stateObject));
            }
            if (o instanceof AuthenticationSteps.ScalarValueStep) {
                state = state.map(stateObject -> AuthenticationStepsOperator.doScalarValueStep((AuthenticationSteps.ScalarValueStep)o));
            }
            if (o instanceof AuthenticationSteps.SupplierStep) {
                state = state.flatMap(stateObject -> this.doSupplierStepLater((AuthenticationSteps.SupplierStep)o));
            }
            if (!logger.isDebugEnabled()) continue;
            logger.debug(String.format("Executed %s with current state %s", o, state));
        }
        return state;
    }

    private Mono<Object> doHttpRequest(AuthenticationSteps.HttpRequestNode<Object> step, Object state) {
        AuthenticationSteps.HttpRequest<Object> definition = step.getDefinition();
        HttpEntity<?> entity = AuthenticationStepsOperator.getEntity(definition.getEntity(), state);
        WebClient.RequestBodySpec spec = definition.getUri() == null ? (WebClient.RequestBodySpec)this.webClient.method(definition.getMethod()).uri(definition.getUriTemplate(), (Object[])definition.getUrlVariables()) : (WebClient.RequestBodySpec)this.webClient.method(definition.getMethod()).uri(definition.getUri());
        for (Map.Entry<String, List<String>> header : entity.getHeaders().entrySet()) {
            spec = (WebClient.RequestBodySpec)spec.header(header.getKey(), new String[]{header.getValue().get(0)});
        }
        if (entity.getBody() != null && !entity.getBody().equals((Object)Undefinded.UNDEFINDED)) {
            return spec.bodyValue(entity.getBody()).retrieve().bodyToMono(definition.getResponseType());
        }
        return spec.retrieve().bodyToMono(definition.getResponseType());
    }

    private static HttpEntity<?> getEntity(@Nullable HttpEntity<?> entity, @Nullable Object state) {
        if (entity == null) {
            return state == null ? HttpEntity.EMPTY : new HttpEntity<Object>(state);
        }
        if (entity.getBody() == null && state != null) {
            return new HttpEntity<Object>(state, entity.getHeaders());
        }
        return entity;
    }

    private static Object doMapStep(AuthenticationSteps.MapStep<Object, Object> o, Object state) {
        return o.apply(state);
    }

    private Mono<Object> doZipStep(AuthenticationSteps.ZipStep<Object, Object> o) {
        return this.createMono(o.getRight());
    }

    private static void doOnNext(AuthenticationSteps.OnNextStep<Object> o, Object state) {
        o.apply(state);
    }

    private static Object doScalarValueStep(AuthenticationSteps.ScalarValueStep<Object> scalarValueStep) {
        return scalarValueStep.get();
    }

    private Mono<Object> doSupplierStepLater(AuthenticationSteps.SupplierStep<Object> supplierStep) {
        Supplier<Object> supplier = supplierStep.getSupplier();
        if (!(supplier instanceof ResourceCredentialSupplier)) {
            return Mono.fromSupplier(supplierStep.getSupplier()).subscribeOn(Schedulers.boundedElastic());
        }
        ResourceCredentialSupplier resourceSupplier = (ResourceCredentialSupplier)supplier;
        return DataBufferUtils.join(DataBufferUtils.read(resourceSupplier.getResource(), this.factory, 4096)).map(dataBuffer -> {
            String result = dataBuffer.toString(ResourceCredentialSupplier.CHARSET);
            DataBufferUtils.release(dataBuffer);
            return result;
        }).onErrorMap(IOException.class, e -> new VaultException(String.format("Credential retrieval from %s failed", resourceSupplier.getResource()), (Throwable)e));
    }

    static enum Undefinded {
        UNDEFINDED;

    }
}

