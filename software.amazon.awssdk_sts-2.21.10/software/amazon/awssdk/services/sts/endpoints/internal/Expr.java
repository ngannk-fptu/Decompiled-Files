/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.protocols.jsoncore.JsonNode
 */
package software.amazon.awssdk.services.sts.endpoints.internal;

import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.services.sts.endpoints.internal.Eval;
import software.amazon.awssdk.services.sts.endpoints.internal.ExprVisitor;
import software.amazon.awssdk.services.sts.endpoints.internal.FnNode;
import software.amazon.awssdk.services.sts.endpoints.internal.GetAttr;
import software.amazon.awssdk.services.sts.endpoints.internal.Identifier;
import software.amazon.awssdk.services.sts.endpoints.internal.Literal;
import software.amazon.awssdk.services.sts.endpoints.internal.Ref;
import software.amazon.awssdk.services.sts.endpoints.internal.RuleError;
import software.amazon.awssdk.services.sts.endpoints.internal.SourceException;

@SdkInternalApi
public abstract class Expr
implements Eval {
    public abstract <R> R accept(ExprVisitor<R> var1);

    public GetAttr getAttr(String path) {
        return GetAttr.builder().target(this).path(path).build();
    }

    public GetAttr getAttr(Identifier path) {
        return GetAttr.builder().target(this).path(path.asString()).build();
    }

    public static Expr fromNode(JsonNode node) {
        if (node.isObject()) {
            JsonNode fn;
            Map objNode = node.asObject();
            JsonNode ref = (JsonNode)objNode.get("ref");
            if ((ref != null ? 1 : 0) + ((fn = (JsonNode)objNode.get("fn")) != null ? 1 : 0) != 1) {
                throw SourceException.builder().message("expected exactly one of `ref` or `fn` to be set").build();
            }
            if (ref != null) {
                return Expr.ref(Identifier.of(ref.asString()));
            }
            return RuleError.ctx("while parsing fn", () -> FnNode.fromNode(node).validate());
        }
        if (node.isString()) {
            return Literal.fromStr(node.asString());
        }
        return Literal.fromNode(node);
    }

    public static Expr parseShortform(String shortForm) {
        return RuleError.ctx("while parsing `" + shortForm + "` within a template", () -> {
            if (shortForm.contains("#")) {
                String[] parts = shortForm.split("#", 2);
                String base = parts[0];
                String pattern = parts[1];
                return GetAttr.builder().target(Expr.ref(Identifier.of(base))).path(pattern).build();
            }
            return Expr.ref(Identifier.of(shortForm));
        });
    }

    public String template() {
        throw new RuntimeException(String.format("cannot convert %s to a string template", this));
    }

    public static Ref ref(Identifier name) {
        return new Ref(name);
    }

    public static Expr of(boolean value) {
        return Literal.fromBool(value);
    }

    public static Expr of(int value) {
        return Literal.fromInteger(value);
    }

    public static Expr of(String value) {
        return Literal.fromStr(value);
    }
}

