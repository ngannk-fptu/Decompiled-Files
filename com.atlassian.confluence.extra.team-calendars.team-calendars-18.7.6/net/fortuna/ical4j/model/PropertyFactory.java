/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.text.ParseException;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;

public interface PropertyFactory<T extends Property>
extends Serializable {
    public T createProperty();

    public T createProperty(ParameterList var1, String var2) throws IOException, URISyntaxException, ParseException;

    public boolean supports(String var1);
}

