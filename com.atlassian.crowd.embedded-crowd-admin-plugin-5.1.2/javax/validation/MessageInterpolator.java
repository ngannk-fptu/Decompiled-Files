/*
 * Decompiled with CFR 0.152.
 */
package javax.validation;

import java.util.Locale;
import javax.validation.metadata.ConstraintDescriptor;

public interface MessageInterpolator {
    public String interpolate(String var1, Context var2);

    public String interpolate(String var1, Context var2, Locale var3);

    public static interface Context {
        public ConstraintDescriptor<?> getConstraintDescriptor();

        public Object getValidatedValue();

        public <T> T unwrap(Class<T> var1);
    }
}

