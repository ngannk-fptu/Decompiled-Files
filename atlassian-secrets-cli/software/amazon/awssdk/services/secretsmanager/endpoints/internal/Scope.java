/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.endpoints.internal;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Expr;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.FatScope;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Identifier;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.InnerParseError;

@SdkInternalApi
public class Scope<T> {
    private final Deque<FatScope<T>> scope = new ArrayDeque<FatScope<T>>();

    public Scope() {
        this.scope.push(new FatScope());
    }

    public void push() {
        this.scope.push(new FatScope());
    }

    public void pop() {
        this.scope.pop();
    }

    public void insert(String name, T value) {
        this.insert(Identifier.of(name), value);
    }

    public void insert(Identifier name, T value) {
        this.scope.getFirst().types().put(name, value);
    }

    public void insertFact(Expr name, T value) {
        this.scope.getFirst().facts().put(name, value);
    }

    public <U> U inScope(Supplier<U> func) {
        this.push();
        try {
            U u = func.get();
            return u;
        }
        finally {
            this.pop();
        }
    }

    public String toString() {
        HashMap<Identifier, T> toPrint = new HashMap<Identifier, T>();
        for (FatScope<T> layer : this.scope) {
            toPrint.putAll(layer.types());
        }
        return toPrint.toString();
    }

    public Optional<T> eval(Expr expr) {
        for (FatScope<T> layer : this.scope) {
            if (!layer.facts().containsKey(expr)) continue;
            return Optional.of(layer.facts().get(expr));
        }
        return Optional.empty();
    }

    public T expectValue(Identifier name) {
        for (FatScope<T> layer : this.scope) {
            if (!layer.types().containsKey(name)) continue;
            return layer.types().get(name);
        }
        throw new InnerParseError(String.format("No field named %s", name));
    }

    public Optional<T> getValue(Identifier name) {
        for (FatScope<T> layer : this.scope) {
            if (!layer.types().containsKey(name)) continue;
            return Optional.of(layer.types().get(name));
        }
        return Optional.empty();
    }
}

