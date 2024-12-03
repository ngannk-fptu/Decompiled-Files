/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.authentication;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.vault.VaultException;
import org.springframework.vault.authentication.AppIdAuthentication;
import org.springframework.vault.authentication.AuthenticationSteps;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.LoginTokenUtil;
import org.springframework.vault.authentication.VaultLoginException;
import org.springframework.vault.client.VaultResponses;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultToken;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestOperations;

public class AuthenticationStepsExecutor
implements ClientAuthentication {
    private static final Log logger = LogFactory.getLog(AppIdAuthentication.class);
    private final AuthenticationSteps chain;
    private final RestOperations restOperations;

    public AuthenticationStepsExecutor(AuthenticationSteps steps, RestOperations restOperations) {
        Assert.notNull((Object)steps, "AuthenticationSteps must not be null");
        Assert.notNull((Object)restOperations, "RestOperations must not be null");
        this.chain = steps;
        this.restOperations = restOperations;
    }

    @Override
    public VaultToken login() throws VaultException {
        List<AuthenticationSteps.Node<?>> steps = this.chain.steps;
        Object state = this.evaluate(steps);
        if (state instanceof VaultToken) {
            return (VaultToken)state;
        }
        if (state instanceof VaultResponse) {
            VaultResponse response = (VaultResponse)state;
            Assert.state(response.getAuth() != null, "Auth field must not be null");
            return LoginTokenUtil.from(response.getAuth());
        }
        throw new IllegalStateException(String.format("Cannot retrieve VaultToken from authentication chain. Got instead %s", state));
    }

    private Object evaluate(Iterable<AuthenticationSteps.Node<?>> steps) {
        Object state = null;
        for (AuthenticationSteps.Node<?> o : steps) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Executing %s with current state %s", o, state));
            }
            try {
                if (o instanceof AuthenticationSteps.HttpRequestNode) {
                    state = this.doHttpRequest((AuthenticationSteps.HttpRequestNode)o, state);
                }
                if (o instanceof AuthenticationSteps.MapStep) {
                    state = AuthenticationStepsExecutor.doMapStep((AuthenticationSteps.MapStep)o, state);
                }
                if (o instanceof AuthenticationSteps.ZipStep) {
                    state = this.doZipStep((AuthenticationSteps.ZipStep)o, state);
                }
                if (o instanceof AuthenticationSteps.OnNextStep) {
                    state = AuthenticationStepsExecutor.doOnNext((AuthenticationSteps.OnNextStep)o, state);
                }
                if (o instanceof AuthenticationSteps.ScalarValueStep) {
                    state = AuthenticationStepsExecutor.doScalarValueStep((AuthenticationSteps.ScalarValueStep)o);
                }
                if (o instanceof AuthenticationSteps.SupplierStep) {
                    state = AuthenticationStepsExecutor.doSupplierStep((AuthenticationSteps.SupplierStep)o);
                }
                if (!logger.isDebugEnabled()) continue;
                logger.debug(String.format("Executed %s with current state %s", o, state));
            }
            catch (HttpStatusCodeException e) {
                throw new VaultLoginException(String.format("HTTP request %s in state %s failed with Status %s and body %s", o, state, e.getRawStatusCode(), VaultResponses.getError(e.getResponseBodyAsString())), e);
            }
            catch (RuntimeException e) {
                throw new VaultLoginException(String.format("Authentication execution failed in %s", o), e);
            }
        }
        return state;
    }

    @Nullable
    private Object doHttpRequest(AuthenticationSteps.HttpRequestNode<Object> step, @Nullable Object state) {
        AuthenticationSteps.HttpRequest<Object> definition = step.getDefinition();
        if (definition.getUri() == null) {
            ResponseEntity<Object> exchange2 = this.restOperations.exchange(definition.getUriTemplate(), definition.getMethod(), AuthenticationStepsExecutor.getEntity(definition.getEntity(), state), definition.getResponseType(), (Object[])definition.getUrlVariables());
            return exchange2.getBody();
        }
        ResponseEntity<Object> exchange3 = this.restOperations.exchange(definition.getUri(), definition.getMethod(), AuthenticationStepsExecutor.getEntity(definition.getEntity(), state), definition.getResponseType());
        return exchange3.getBody();
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

    private Object doZipStep(AuthenticationSteps.ZipStep<Object, Object> o, Object state) {
        Object result = this.evaluate(o.getRight());
        return AuthenticationSteps.Pair.of(state, result);
    }

    private static Object doOnNext(AuthenticationSteps.OnNextStep<Object> o, Object state) {
        return o.apply(state);
    }

    private static Object doScalarValueStep(AuthenticationSteps.ScalarValueStep<Object> scalarValueStep) {
        return scalarValueStep.get();
    }

    private static Object doSupplierStep(AuthenticationSteps.SupplierStep<Object> supplierStep) {
        return supplierStep.get();
    }
}

