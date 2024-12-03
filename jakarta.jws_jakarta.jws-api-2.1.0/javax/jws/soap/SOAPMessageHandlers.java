/*
 * Decompiled with CFR 0.152.
 */
package javax.jws.soap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.jws.soap.SOAPMessageHandler;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
@Deprecated
public @interface SOAPMessageHandlers {
    public SOAPMessageHandler[] value();
}

