/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.secretsmanager.endpoints.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Fn;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Literal;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Ref;

@SdkInternalApi
public interface ExprVisitor<R> {
    public R visitLiteral(Literal var1);

    public R visitRef(Ref var1);

    public R visitFn(Fn var1);

    public static abstract class Default<R>
    implements ExprVisitor<R> {
        public abstract R getDefault();

        @Override
        public R visitLiteral(Literal literal) {
            return this.getDefault();
        }

        @Override
        public R visitRef(Ref ref) {
            return this.getDefault();
        }

        @Override
        public R visitFn(Fn fn) {
            return this.getDefault();
        }
    }
}

