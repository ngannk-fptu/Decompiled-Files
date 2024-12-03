/*
 * Decompiled with CFR 0.152.
 */
package javax.ws.rs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.ws.rs.HttpMethod;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
@HttpMethod(value="DELETE")
public @interface DELETE {
}

