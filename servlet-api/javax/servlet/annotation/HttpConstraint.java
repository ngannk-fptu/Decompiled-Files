/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.servlet.annotation.ServletSecurity;

@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface HttpConstraint {
    public ServletSecurity.EmptyRoleSemantic value() default ServletSecurity.EmptyRoleSemantic.PERMIT;

    public ServletSecurity.TransportGuarantee transportGuarantee() default ServletSecurity.TransportGuarantee.NONE;

    public String[] rolesAllowed() default {};
}

