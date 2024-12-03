/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model;

import java.io.Serializable;
import java.net.URISyntaxException;
import net.fortuna.ical4j.model.Parameter;

public interface ParameterFactory<T extends Parameter>
extends Serializable {
    public T createParameter(String var1) throws URISyntaxException;

    public boolean supports(String var1);
}

