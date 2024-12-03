/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.utility.privilege;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Method;
import java.security.PrivilegedAction;
import java.util.Arrays;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class GetMethodAction
implements PrivilegedAction<Method> {
    private final String type;
    private final String name;
    private final Class<?>[] parameter;

    public GetMethodAction(String type, String name, Class<?> ... parameter) {
        this.type = type;
        this.name = name;
        this.parameter = parameter;
    }

    @Override
    @MaybeNull
    @SuppressFBWarnings(value={"REC_CATCH_EXCEPTION"}, justification="Exception should not be rethrown but trigger a fallback.")
    public Method run() {
        try {
            return Class.forName(this.type).getMethod(this.name, this.parameter);
        }
        catch (Exception ignored) {
            return null;
        }
    }

    public boolean equals(@MaybeNull Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (this.getClass() != object.getClass()) {
            return false;
        }
        if (!this.type.equals(((GetMethodAction)object).type)) {
            return false;
        }
        if (!this.name.equals(((GetMethodAction)object).name)) {
            return false;
        }
        return Arrays.equals(this.parameter, ((GetMethodAction)object).parameter);
    }

    public int hashCode() {
        return ((this.getClass().hashCode() * 31 + this.type.hashCode()) * 31 + this.name.hashCode()) * 31 + Arrays.hashCode(this.parameter);
    }
}

