/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.protocols.jsoncore.JsonNode
 */
package software.amazon.awssdk.services.secretsmanager.endpoints.internal;

import java.util.Map;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Eval;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Expr;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Fn;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.FnNode;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Identifier;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.IntoSelf;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Scope;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Value;

@SdkInternalApi
public final class Condition
implements Eval,
IntoSelf<Condition> {
    public static final String ASSIGN = "assign";
    private final Expr fn;
    private final Identifier result;

    private Condition(Builder builder) {
        this.fn = builder.fn;
        this.result = builder.result;
    }

    public Expr getFn() {
        return this.fn;
    }

    public Optional<Identifier> getResult() {
        return Optional.ofNullable(this.result);
    }

    public static Condition fromNode(JsonNode node) {
        Map objNode = node.asObject();
        Builder b = Condition.builder();
        Fn fn = FnNode.fromNode(node).validate();
        b.fn(fn);
        JsonNode assignNode = (JsonNode)objNode.get(ASSIGN);
        if (assignNode != null) {
            b.result(assignNode.asString());
        }
        return b.build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        this.getResult().ifPresent(res -> sb.append(res).append(" = "));
        sb.append(this.fn);
        return sb.toString();
    }

    @Override
    public Value eval(Scope<Value> scope) {
        Value value = this.fn.eval(scope);
        if (!value.isNone()) {
            this.getResult().ifPresent(res -> scope.insert((Identifier)res, value));
        }
        return value;
    }

    public Expr expr() {
        if (this.getResult().isPresent()) {
            return Expr.ref(this.getResult().get());
        }
        throw new RuntimeException("Cannot generate expr from a condition without a result");
    }

    public static class Builder {
        private Fn fn;
        private Identifier result;

        public Builder fn(Fn fn) {
            this.fn = fn;
            return this;
        }

        public Builder result(String result) {
            this.result = Identifier.of(result);
            return this;
        }

        public Condition build() {
            return new Condition(this);
        }
    }
}

