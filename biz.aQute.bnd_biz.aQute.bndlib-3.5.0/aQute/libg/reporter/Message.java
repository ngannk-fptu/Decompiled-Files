/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.reporter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value=RetentionPolicy.RUNTIME)
public @interface Message {
    public String value();
}

