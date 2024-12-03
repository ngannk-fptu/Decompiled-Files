/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import net.fortuna.ical4j.vcard.Group;
import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.Property;
import org.apache.commons.codec.DecoderException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface PropertyFactory<T extends Property> {
    public T createProperty(List<Parameter> var1, String var2) throws URISyntaxException, ParseException, DecoderException;

    public T createProperty(Group var1, List<Parameter> var2, String var3) throws URISyntaxException, ParseException, DecoderException;
}

