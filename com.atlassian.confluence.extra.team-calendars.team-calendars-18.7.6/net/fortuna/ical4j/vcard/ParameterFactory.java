/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard;

import net.fortuna.ical4j.vcard.Parameter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ParameterFactory<T extends Parameter> {
    public T createParameter(String var1);
}

