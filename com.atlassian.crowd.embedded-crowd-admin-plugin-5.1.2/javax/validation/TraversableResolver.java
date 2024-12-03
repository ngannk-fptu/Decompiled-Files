/*
 * Decompiled with CFR 0.152.
 */
package javax.validation;

import java.lang.annotation.ElementType;
import javax.validation.Path;

public interface TraversableResolver {
    public boolean isReachable(Object var1, Path.Node var2, Class<?> var3, Path var4, ElementType var5);

    public boolean isCascadable(Object var1, Path.Node var2, Class<?> var3, Path var4, ElementType var5);
}

