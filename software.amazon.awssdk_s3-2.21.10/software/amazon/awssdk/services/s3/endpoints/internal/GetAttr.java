/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.s3.endpoints.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.s3.endpoints.internal.Expr;
import software.amazon.awssdk.services.s3.endpoints.internal.Fn;
import software.amazon.awssdk.services.s3.endpoints.internal.FnNode;
import software.amazon.awssdk.services.s3.endpoints.internal.FnVisitor;
import software.amazon.awssdk.services.s3.endpoints.internal.Identifier;
import software.amazon.awssdk.services.s3.endpoints.internal.InnerParseError;
import software.amazon.awssdk.services.s3.endpoints.internal.Literal;
import software.amazon.awssdk.services.s3.endpoints.internal.Ref;
import software.amazon.awssdk.services.s3.endpoints.internal.Scope;
import software.amazon.awssdk.services.s3.endpoints.internal.SourceException;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

@SdkInternalApi
public class GetAttr
extends Fn {
    public static final String ID = "getAttr";

    public GetAttr(FnNode node) {
        super(node);
    }

    @Override
    public Value eval(Scope<Value> scope) {
        List<Part> path;
        Value root = this.target().eval(scope);
        try {
            path = this.path();
        }
        catch (InnerParseError e) {
            throw new RuntimeException(e);
        }
        for (Part part : path) {
            root = part.eval(root);
        }
        return root;
    }

    private static GetAttr fromBuilder(Builder builder) {
        return new GetAttr(FnNode.builder().fn(ID).argv(Arrays.asList(builder.target, Literal.fromStr(String.join((CharSequence)".", builder.path)))).build());
    }

    public static Builder builder() {
        return new Builder();
    }

    public Expr target() {
        return (Expr)this.expectTwoArgs().left();
    }

    public List<Part> path() throws InnerParseError {
        Expr right = (Expr)this.expectTwoArgs().right();
        if (right instanceof Literal) {
            Literal path = (Literal)right;
            return GetAttr.parse(path.expectLiteralString());
        }
        throw SourceException.builder().message("second argument must be a string literal").build();
    }

    private static List<Part> parse(String path) throws InnerParseError {
        String[] components = path.split("\\.");
        ArrayList<Part> result = new ArrayList<Part>();
        for (String component : components) {
            if (component.contains("[")) {
                int slicePartIndex = component.indexOf("[");
                String slicePart = component.substring(slicePartIndex);
                if (!slicePart.endsWith("]")) {
                    throw new InnerParseError("Invalid path component: %s. Must end with `]`");
                }
                try {
                    String number = slicePart.substring(1, slicePart.length() - 1);
                    int slice = Integer.parseInt(number);
                    if (slice < 0) {
                        throw new InnerParseError("Invalid path component: slice index must be >= 0");
                    }
                    result.add(Part.Key.of(component.substring(0, slicePartIndex)));
                    result.add(new Part.Index(slice));
                    continue;
                }
                catch (NumberFormatException ex) {
                    throw new InnerParseError(String.format("%s could not be parsed as a number", slicePart));
                }
            }
            result.add(Part.Key.of(component));
        }
        if (result.isEmpty()) {
            throw new InnerParseError("Invalid argument to GetAttr: path may not be empty");
        }
        return result;
    }

    @Override
    public <T> T acceptFnVisitor(FnVisitor<T> visitor) {
        return visitor.visitGetAttr(this);
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append(this.target());
        try {
            for (Part part : this.path()) {
                out.append(".");
                out.append(part);
            }
        }
        catch (InnerParseError e) {
            throw new RuntimeException(e);
        }
        return out.toString();
    }

    @Override
    public String template() {
        List<Part> partList;
        String target = ((Ref)this.target()).getName().asString();
        StringBuilder pathPart = new StringBuilder();
        try {
            partList = this.path();
        }
        catch (InnerParseError e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < partList.size(); ++i) {
            if (i != 0 && partList.get(i) instanceof Part.Key) {
                pathPart.append(".");
            }
            pathPart.append(partList.get(i).toString());
        }
        return "{" + target + "#" + pathPart + "}";
    }

    public static class Builder {
        Expr target;
        String path;

        public Builder target(Expr target) {
            this.target = target;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public GetAttr build() {
            return GetAttr.fromBuilder(this);
        }
    }

    public static interface Part {
        public Value eval(Value var1);

        public static final class Index
        implements Part {
            private final int index;

            public Index(int index) {
                this.index = index;
            }

            @Override
            public Value eval(Value container) {
                return container.expectArray().get(this.index);
            }

            public String toString() {
                return String.format("[%s]", this.index);
            }

            public int index() {
                return this.index;
            }

            public boolean equals(Object obj) {
                if (obj == this) {
                    return true;
                }
                if (obj == null || obj.getClass() != this.getClass()) {
                    return false;
                }
                Index that = (Index)obj;
                return this.index == that.index;
            }

            public int hashCode() {
                return this.index;
            }
        }

        public static final class Key
        implements Part {
            private final Identifier key;

            public Key(Identifier key) {
                this.key = key;
            }

            public String toString() {
                return this.key.asString();
            }

            public static Key of(String key) {
                return new Key(Identifier.of(key));
            }

            @Override
            public Value eval(Value container) {
                return container.expectRecord().get(this.key);
            }

            public Identifier key() {
                return this.key;
            }

            public boolean equals(Object obj) {
                if (obj == this) {
                    return true;
                }
                if (obj == null || obj.getClass() != this.getClass()) {
                    return false;
                }
                Key that = (Key)obj;
                return Objects.equals(this.key, that.key);
            }

            public int hashCode() {
                return this.key != null ? this.key.hashCode() : 0;
            }
        }
    }
}

