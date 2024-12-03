/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package net.fortuna.ical4j.transform.rfc5545;

import java.net.URI;
import java.net.URISyntaxException;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.transform.rfc5545.Rfc5545PropertyRule;
import org.apache.commons.lang3.StringUtils;

public class AttendeePropertyRule
implements Rfc5545PropertyRule<Attendee> {
    private static final String MAILTO = "mailto";
    private static final String APOSTROPHE = "'";
    private static final int MIN_LENGTH = 3;

    @Override
    public void applyTo(Attendee element) {
        String part;
        if (element == null) {
            return;
        }
        URI calAddress = element.getCalAddress();
        if (calAddress == null) {
            return;
        }
        String scheme = calAddress.getScheme();
        if (scheme != null && StringUtils.startsWithIgnoreCase((CharSequence)scheme, (CharSequence)MAILTO) && (part = calAddress.getSchemeSpecificPart()) != null && part.length() >= 3 && StringUtils.startsWith((CharSequence)part, (CharSequence)APOSTROPHE) && StringUtils.endsWith((CharSequence)part, (CharSequence)APOSTROPHE)) {
            String newPart = part.substring(1, part.length() - 1);
            AttendeePropertyRule.safelySetNewValue(element, newPart);
        }
    }

    private static void safelySetNewValue(Attendee element, String newPart) {
        try {
            element.setValue("mailto:" + newPart);
        }
        catch (URISyntaxException uRISyntaxException) {
            // empty catch block
        }
    }

    @Override
    public Class<Attendee> getSupportedType() {
        return Attendee.class;
    }
}

