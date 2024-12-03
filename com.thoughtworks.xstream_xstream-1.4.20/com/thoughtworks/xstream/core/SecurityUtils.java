/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.security.InputManipulationException;

public class SecurityUtils {
    public static void checkForCollectionDoSAttack(UnmarshallingContext context, long start) {
        Integer secondsUsed;
        int diff = (int)((System.currentTimeMillis() - start) / 1000L);
        if (diff > 0 && (secondsUsed = (Integer)context.get("XStreamCollectionUpdateSeconds")) != null) {
            Integer limit = (Integer)context.get("XStreamCollectionUpdateLimit");
            if (limit == null) {
                throw new ConversionException("Missing limit for updating collections.");
            }
            int seconds = secondsUsed + diff;
            if (seconds > limit) {
                throw new InputManipulationException("Denial of Service attack assumed. Adding elements to collections or maps exceeds " + limit + " seconds.");
            }
            context.put("XStreamCollectionUpdateSeconds", new Integer(seconds));
        }
    }
}

