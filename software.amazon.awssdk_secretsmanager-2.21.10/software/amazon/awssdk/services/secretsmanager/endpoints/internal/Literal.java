/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.protocols.jsoncore.JsonNode
 */
package software.amazon.awssdk.services.secretsmanager.endpoints.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Expr;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.ExprVisitor;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Identifier;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.RuleError;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Scope;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.SourceException;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Template;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Value;

@SdkInternalApi
public class Literal
extends Expr {
    private final Lit source;

    private Literal(Lit source) {
        this.source = source;
    }

    public <T> T accept(Visitor<T> visitor) {
        return this.source.accept(visitor);
    }

    public String expectLiteralString() {
        if (this.source instanceof Str) {
            Str s = (Str)this.source;
            return s.value.expectLiteral();
        }
        throw RuleError.builder().cause((Throwable)((Object)SourceException.builder().message("Expected a literal string, got " + this.source).build())).build();
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visitLiteral(this);
    }

    @Override
    public Value eval(final Scope<Value> scope) {
        return this.source.accept(new Visitor<Value>(){

            @Override
            public Value visitInt(int value) {
                return Value.fromInteger(value);
            }

            @Override
            public Value visitBool(boolean b) {
                return Value.fromBool(b);
            }

            @Override
            public Value visitStr(Template value) {
                return value.eval(scope);
            }

            @Override
            public Value visitObject(Map<Identifier, Literal> members) {
                HashMap<Identifier, Value> tpe = new HashMap<Identifier, Value>();
                members.forEach((k, v) -> tpe.put((Identifier)k, v.eval(scope)));
                return Value.fromRecord(tpe);
            }

            @Override
            public Value visitTuple(List<Literal> members) {
                ArrayList<Value> tuples = new ArrayList<Value>();
                for (Literal el : ((Tuple)Literal.this.source).members) {
                    tuples.add(el.eval(scope));
                }
                return Value.fromArray(tuples);
            }
        });
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Literal literal = (Literal)o;
        return this.source != null ? this.source.equals(literal.source) : literal.source == null;
    }

    public int hashCode() {
        return this.source != null ? this.source.hashCode() : 0;
    }

    public String toString() {
        return this.source.toString();
    }

    public static Literal fromNode(JsonNode node) {
        Lit lit;
        if (node.isArray()) {
            List<Literal> array = node.asArray().stream().map(Literal::fromNode).collect(Collectors.toList());
            lit = new Tuple(array);
        } else if (node.isBoolean()) {
            lit = new Bool(node.asBoolean());
        } else {
            if (node.isNull()) {
                throw SdkClientException.create((String)"null node not supported");
            }
            if (node.isNumber()) {
                lit = new Int(Integer.parseInt(node.asNumber()));
            } else if (node.isObject()) {
                HashMap<Identifier, Literal> obj = new HashMap<Identifier, Literal>();
                node.asObject().forEach((k, v) -> obj.put(Identifier.of(k), Literal.fromNode(v)));
                lit = new Obj(obj);
            } else if (node.isString()) {
                lit = new Str(new Template(node.asString()));
            } else {
                throw SdkClientException.create((String)("Unable to create literal from " + node));
            }
        }
        return new Literal(lit);
    }

    public static Literal fromTuple(List<Literal> authSchemes) {
        return new Literal(new Tuple(authSchemes));
    }

    public static Literal fromRecord(Map<Identifier, Literal> record) {
        return new Literal(new Obj(record));
    }

    public static Literal fromStr(Template value) {
        return new Literal(new Str(value));
    }

    public static Literal fromStr(String s) {
        return Literal.fromStr(new Template(s));
    }

    public static Literal fromInteger(int value) {
        return new Literal(new Int(value));
    }

    public static Literal fromBool(boolean value) {
        return new Literal(new Bool(value));
    }

    static final class Str
    implements Lit {
        private final Template value;

        Str(Template value) {
            this.value = value;
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitStr(this.value);
        }

        public String toString() {
            return this.value.toString();
        }

        public Template value() {
            return this.value;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Str str = (Str)o;
            return this.value != null ? this.value.equals(str.value) : str.value == null;
        }

        public int hashCode() {
            return this.value != null ? this.value.hashCode() : 0;
        }
    }

    static final class Bool
    implements Lit {
        private final boolean value;

        Bool(boolean value) {
            this.value = value;
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitBool(this.value);
        }

        public String toString() {
            return Boolean.toString(this.value);
        }

        public Boolean value() {
            return this.value;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Bool bool = (Bool)o;
            return this.value == bool.value;
        }

        public int hashCode() {
            return this.value ? 1 : 0;
        }
    }

    static final class Obj
    implements Lit {
        private final Map<Identifier, Literal> members;

        Obj(Map<Identifier, Literal> members) {
            this.members = members;
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitObject(this.members);
        }

        public String toString() {
            return this.members.toString();
        }

        public Map<Identifier, Literal> members() {
            return this.members;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Obj obj = (Obj)o;
            return this.members != null ? this.members.equals(obj.members) : obj.members == null;
        }

        public int hashCode() {
            return this.members != null ? this.members.hashCode() : 0;
        }
    }

    static final class Tuple
    implements Lit {
        private final List<Literal> members;

        Tuple(List<Literal> members) {
            this.members = members;
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitTuple(this.members);
        }

        public String toString() {
            return this.members.stream().map(Literal::toString).collect(Collectors.joining(", ", "[", "]"));
        }

        public List<Literal> members() {
            return this.members;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Tuple tuple = (Tuple)o;
            return this.members != null ? this.members.equals(tuple.members) : tuple.members == null;
        }

        public int hashCode() {
            return this.members != null ? this.members.hashCode() : 0;
        }
    }

    static final class Int
    implements Lit {
        private final Integer value;

        Int(Integer value) {
            this.value = value;
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitInt(this.value);
        }

        public String toString() {
            return Integer.toString(this.value);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Int anInt = (Int)o;
            return this.value != null ? this.value.equals(anInt.value) : anInt.value == null;
        }

        public int hashCode() {
            return this.value != null ? this.value.hashCode() : 0;
        }
    }

    private static interface Lit {
        public <T> T accept(Visitor<T> var1);
    }

    public static interface Visitor<T> {
        public T visitBool(boolean var1);

        public T visitStr(Template var1);

        public T visitObject(Map<Identifier, Literal> var1);

        public T visitTuple(List<Literal> var1);

        public T visitInt(int var1);
    }
}

