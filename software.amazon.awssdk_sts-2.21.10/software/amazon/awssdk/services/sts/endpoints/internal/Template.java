/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.sts.endpoints.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.sts.endpoints.internal.Expr;
import software.amazon.awssdk.services.sts.endpoints.internal.InnerParseError;
import software.amazon.awssdk.services.sts.endpoints.internal.RuleError;
import software.amazon.awssdk.services.sts.endpoints.internal.Scope;
import software.amazon.awssdk.services.sts.endpoints.internal.TemplateVisitor;
import software.amazon.awssdk.services.sts.endpoints.internal.Value;

@SdkInternalApi
public class Template {
    private final List<Part> parts = RuleError.ctx("when parsing template", () -> this.parseTemplate(template));

    Template(String template) {
    }

    public <T> Stream<T> accept(TemplateVisitor<T> visitor) {
        if (this.isStatic()) {
            return Stream.of(visitor.visitStaticTemplate(this.expectLiteral()));
        }
        if (this.parts.size() == 1) {
            return Stream.of(visitor.visitSingleDynamicTemplate(((Dynamic)this.parts.get(0)).expr));
        }
        Stream<T> start = Stream.of(visitor.startMultipartTemplate());
        Stream<Object> components = this.parts.stream().map(part -> part.accept(visitor));
        Stream<T> end = Stream.of(visitor.finishMultipartTemplate());
        return Stream.concat(start, Stream.concat(components, end));
    }

    public List<Part> getParts() {
        return this.parts;
    }

    public boolean isStatic() {
        return this.parts.stream().allMatch(it -> it instanceof Literal);
    }

    public String expectLiteral() {
        assert (this.isStatic());
        return this.parts.stream().map(Object::toString).collect(Collectors.joining());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Template template = (Template)o;
        return this.parts != null ? this.parts.equals(template.parts) : template.parts == null;
    }

    public int hashCode() {
        return this.parts != null ? this.parts.hashCode() : 0;
    }

    public static Template fromString(String s) {
        return new Template(s);
    }

    public String toString() {
        return String.format("\"%s\"", this.parts.stream().map(Object::toString).collect(Collectors.joining()));
    }

    public Value eval(Scope<Value> scope) {
        return Value.fromStr(this.parts.stream().map(part -> part.eval(scope)).collect(Collectors.joining()));
    }

    private List<Part> parseTemplate(String template) {
        ArrayList<Part> out = new ArrayList<Part>();
        Optional<Object> templateStart = Optional.empty();
        int depth = 0;
        int templateEnd = 0;
        for (int i = 0; i < template.length(); ++i) {
            if (template.substring(i).startsWith("{{")) {
                ++i;
                continue;
            }
            if (template.substring(i).startsWith("}}")) {
                ++i;
                continue;
            }
            if (template.charAt(i) == '{') {
                if (depth == 0) {
                    if (templateEnd != i) {
                        out.add(Literal.unescape(template.substring(templateEnd, i)));
                    }
                    templateStart = Optional.of(i + 1);
                }
                ++depth;
            }
            if (template.charAt(i) != '}') continue;
            if (--depth < 0) {
                throw new InnerParseError("unmatched `}` in template");
            }
            if (depth == 0) {
                out.add(Dynamic.parse(template.substring((Integer)templateStart.get(), i)));
                templateStart = Optional.empty();
            }
            templateEnd = i + 1;
        }
        if (depth != 0) {
            throw new InnerParseError("unmatched `{` in template");
        }
        if (templateEnd < template.length()) {
            out.add(Literal.unescape(template.substring(templateEnd)));
        }
        return out;
    }

    public static class Dynamic
    extends Part {
        private final String raw;
        private final Expr expr;

        private Dynamic(String raw, Expr expr) {
            this.raw = raw;
            this.expr = expr;
        }

        public String toString() {
            return String.format("{dyn %s}", this.raw);
        }

        @Override
        String eval(Scope<Value> scope) {
            return RuleError.ctx("while evaluating " + this, () -> this.expr.eval(scope).expectString());
        }

        @Override
        <T> T accept(TemplateVisitor<T> visitor) {
            return visitor.visitDynamicElement(this.expr);
        }

        public Expr getExpr() {
            return this.expr;
        }

        public static Dynamic parse(String value) {
            return new Dynamic(value, Expr.parseShortform(value));
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
            Dynamic dynamic = (Dynamic)o;
            if (this.raw != null ? !this.raw.equals(dynamic.raw) : dynamic.raw != null) {
                return false;
            }
            return this.expr != null ? this.expr.equals(dynamic.expr) : dynamic.expr == null;
        }

        public int hashCode() {
            int result = this.raw != null ? this.raw.hashCode() : 0;
            result = 31 * result + (this.expr != null ? this.expr.hashCode() : 0);
            return result;
        }
    }

    public static class Literal
    extends Part {
        private final String value;

        public Literal(String value) {
            if (value.isEmpty()) {
                throw new RuntimeException("value cannot blank");
            }
            this.value = value;
        }

        public static Literal unescape(String value) {
            return new Literal(value.replace("{{", "{").replace("}}", "}"));
        }

        public String getValue() {
            return this.value;
        }

        public String toString() {
            return this.value;
        }

        @Override
        String eval(Scope<Value> scope) {
            return this.value;
        }

        @Override
        <T> T accept(TemplateVisitor<T> visitor) {
            return visitor.visitStaticElement(this.value);
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
            return this.value != null ? this.value.equals(literal.value) : literal.value == null;
        }

        public int hashCode() {
            return this.value != null ? this.value.hashCode() : 0;
        }
    }

    public static abstract class Part {
        abstract String eval(Scope<Value> var1);

        abstract <T> T accept(TemplateVisitor<T> var1);
    }
}

