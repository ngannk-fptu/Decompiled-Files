/*
 * Decompiled with CFR 0.152.
 */
package javax.websocket;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.Decoder;
import javax.websocket.Encoder;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
public @interface ClientEndpoint {
    public String[] subprotocols() default {};

    public Class<? extends Decoder>[] decoders() default {};

    public Class<? extends Encoder>[] encoders() default {};

    public Class<? extends ClientEndpointConfig.Configurator> configurator() default ClientEndpointConfig.Configurator.class;
}

