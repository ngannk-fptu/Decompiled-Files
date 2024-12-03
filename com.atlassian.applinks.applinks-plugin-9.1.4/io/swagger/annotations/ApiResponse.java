/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.annotations;

import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;
import io.swagger.annotations.ResponseHeader;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface ApiResponse {
    public int code();

    public String message();

    public Class<?> response() default Void.class;

    public String reference() default "";

    public ResponseHeader[] responseHeaders() default {@ResponseHeader(name="", response=Void.class)};

    public String responseContainer() default "";

    public Example examples() default @Example(value={@ExampleProperty(value="", mediaType="")});
}

