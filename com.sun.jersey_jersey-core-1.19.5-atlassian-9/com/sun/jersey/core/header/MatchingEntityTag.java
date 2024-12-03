/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.EntityTag
 */
package com.sun.jersey.core.header;

import com.sun.jersey.core.header.reader.HttpHeaderReader;
import com.sun.jersey.impl.ImplMessages;
import java.text.ParseException;
import java.util.Collections;
import java.util.Set;
import javax.ws.rs.core.EntityTag;

public class MatchingEntityTag
extends EntityTag {
    public static final Set<MatchingEntityTag> ANY_MATCH = Collections.emptySet();

    public MatchingEntityTag(String value) {
        super(value, false);
    }

    public MatchingEntityTag(String value, boolean weak) {
        super(value, weak);
    }

    public static MatchingEntityTag valueOf(HttpHeaderReader reader) throws ParseException {
        String v;
        String originalHeader = reader.getRemainder();
        HttpHeaderReader.Event e = reader.next(false);
        if (e == HttpHeaderReader.Event.QuotedString) {
            return new MatchingEntityTag(reader.getEventValue());
        }
        if (e == HttpHeaderReader.Event.Token && (v = reader.getEventValue()).equals("W")) {
            reader.nextSeparator('/');
            return new MatchingEntityTag(reader.nextQuotedString(), true);
        }
        throw new ParseException(ImplMessages.ERROR_PARSING_ENTITY_TAG(originalHeader), reader.getIndex());
    }
}

