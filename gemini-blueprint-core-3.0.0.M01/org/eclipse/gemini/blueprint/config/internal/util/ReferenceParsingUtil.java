/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.util.StringUtils
 */
package org.eclipse.gemini.blueprint.config.internal.util;

import org.eclipse.gemini.blueprint.service.importer.support.Availability;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public abstract class ReferenceParsingUtil {
    private static final String ONE = "1";
    private static final String M = "m";

    public static void checkAvailabilityAndCardinalityDuplication(Element element, String availabilityName, String cardinalityName, ParserContext context) {
        boolean cardStatus;
        boolean availStatus;
        String avail = element.getAttribute(availabilityName);
        String cardinality = element.getAttribute(cardinalityName);
        if (StringUtils.hasText((String)avail) && StringUtils.hasText((String)cardinality) && (availStatus = avail.startsWith(ONE)) != (cardStatus = cardinality.startsWith(M))) {
            context.getReaderContext().error("Both '" + availabilityName + "' and '" + cardinalityName + "' attributes have been specified but with contradictory values.", (Object)element);
        }
    }

    public static Availability determineAvailabilityFromCardinality(String value) {
        return value.startsWith(ONE) ? Availability.MANDATORY : Availability.OPTIONAL;
    }

    public static Availability determineAvailability(String value) {
        return value.startsWith(M) ? Availability.MANDATORY : Availability.OPTIONAL;
    }
}

