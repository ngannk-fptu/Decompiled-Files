/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.Pair
 */
package software.amazon.awssdk.services.s3.endpoints.internal;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.s3.endpoints.internal.Condition;
import software.amazon.awssdk.services.s3.endpoints.internal.Expr;
import software.amazon.awssdk.services.s3.endpoints.internal.ExprVisitor;
import software.amazon.awssdk.services.s3.endpoints.internal.FnNode;
import software.amazon.awssdk.services.s3.endpoints.internal.FnVisitor;
import software.amazon.awssdk.services.s3.endpoints.internal.Into;
import software.amazon.awssdk.services.s3.endpoints.internal.RuleError;
import software.amazon.awssdk.services.s3.endpoints.internal.SourceException;
import software.amazon.awssdk.utils.Pair;

@SdkInternalApi
public abstract class Fn
extends Expr
implements Into<Condition> {
    protected FnNode fnNode;

    public Fn(FnNode fnNode) {
        this.fnNode = fnNode;
    }

    public Condition condition() {
        return new Condition.Builder().fn(this).build();
    }

    public Condition condition(String result) {
        return new Condition.Builder().fn(this).result(result).build();
    }

    public abstract <T> T acceptFnVisitor(FnVisitor<T> var1);

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visitFn(this);
    }

    public String getName() {
        return this.fnNode.getId();
    }

    public List<Expr> getArgv() {
        return this.fnNode.getArgv();
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
        Fn fn = (Fn)o;
        return this.fnNode != null ? this.fnNode.equals(fn.fnNode) : fn.fnNode == null;
    }

    public int hashCode() {
        return this.fnNode != null ? this.fnNode.hashCode() : 0;
    }

    public String toString() {
        return String.format("%s(%s)", this.fnNode.getId(), this.fnNode.getArgv().stream().map(Object::toString).collect(Collectors.joining(", ")));
    }

    protected Expr expectOneArg() {
        List<Expr> argv = this.fnNode.getArgv();
        if (argv.size() == 1) {
            return argv.get(0);
        }
        throw RuleError.builder().cause((Throwable)((Object)SourceException.builder().message("expected 1 argument but found " + argv.size()).build())).build();
    }

    protected Pair<Expr, Expr> expectTwoArgs() {
        List<Expr> argv = this.fnNode.getArgv();
        if (argv.size() == 2) {
            return Pair.of((Object)argv.get(0), (Object)argv.get(1));
        }
        throw RuleError.builder().cause((Throwable)((Object)SourceException.builder().message("expected 2 arguments but found " + argv.size()).build())).build();
    }

    protected List<Expr> expectVariableArgs(int expectedNumberArgs) {
        List<Expr> argv = this.fnNode.getArgv();
        if (argv.size() == expectedNumberArgs) {
            return argv;
        }
        throw RuleError.builder().cause((Throwable)((Object)SourceException.builder().message(String.format("expected %d arguments but found %d", expectedNumberArgs, argv.size())).build())).build();
    }

    @Override
    public Condition into() {
        return this.condition();
    }
}

