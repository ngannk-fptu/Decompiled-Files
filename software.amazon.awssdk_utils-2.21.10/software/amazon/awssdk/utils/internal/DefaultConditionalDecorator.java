/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.utils.internal;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.utils.ConditionalDecorator;

@SdkInternalApi
public final class DefaultConditionalDecorator<T>
implements ConditionalDecorator<T> {
    private final Predicate<T> predicate;
    private final UnaryOperator<T> transform;

    DefaultConditionalDecorator(Builder<T> builder) {
        this.predicate = ((Builder)builder).predicate;
        this.transform = ((Builder)builder).transform;
    }

    public static <T> Builder<T> builder() {
        return new Builder();
    }

    @Override
    public Predicate<T> predicate() {
        return this.predicate;
    }

    @Override
    public UnaryOperator<T> transform() {
        return this.transform;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultConditionalDecorator)) {
            return false;
        }
        DefaultConditionalDecorator that = (DefaultConditionalDecorator)o;
        if (!Objects.equals(this.predicate, that.predicate)) {
            return false;
        }
        return Objects.equals(this.transform, that.transform);
    }

    public int hashCode() {
        int result = this.predicate != null ? this.predicate.hashCode() : 0;
        result = 31 * result + (this.transform != null ? this.transform.hashCode() : 0);
        return result;
    }

    public static final class Builder<T> {
        private Predicate<T> predicate;
        private UnaryOperator<T> transform;

        public Builder<T> predicate(Predicate<T> predicate) {
            this.predicate = predicate;
            return this;
        }

        public Builder<T> transform(UnaryOperator<T> transform) {
            this.transform = transform;
            return this;
        }

        public ConditionalDecorator<T> build() {
            return new DefaultConditionalDecorator(this);
        }
    }
}

