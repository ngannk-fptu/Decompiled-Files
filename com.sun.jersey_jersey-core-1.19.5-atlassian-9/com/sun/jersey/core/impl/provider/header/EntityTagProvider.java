/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.EntityTag
 */
package com.sun.jersey.core.impl.provider.header;

import com.sun.jersey.core.header.reader.HttpHeaderReader;
import com.sun.jersey.core.impl.provider.header.WriterUtil;
import com.sun.jersey.spi.HeaderDelegateProvider;
import java.text.ParseException;
import javax.ws.rs.core.EntityTag;

public class EntityTagProvider
implements HeaderDelegateProvider<EntityTag> {
    @Override
    public boolean supports(Class<?> type) {
        return type == EntityTag.class;
    }

    public String toString(EntityTag header) {
        StringBuilder b = new StringBuilder();
        if (header.isWeak()) {
            b.append("W/");
        }
        WriterUtil.appendQuoted(b, header.getValue());
        return b.toString();
    }

    public EntityTag fromString(String header) {
        if (header == null) {
            throw new IllegalArgumentException("Entity tag is null");
        }
        try {
            HttpHeaderReader reader = HttpHeaderReader.newInstance(header);
            HttpHeaderReader.Event e = reader.next(false);
            if (e == HttpHeaderReader.Event.QuotedString) {
                return new EntityTag(reader.getEventValue());
            }
            if (e == HttpHeaderReader.Event.Token && reader.getEventValue().equals("W")) {
                reader.nextSeparator('/');
                return new EntityTag(reader.nextQuotedString(), true);
            }
        }
        catch (ParseException ex) {
            throw new IllegalArgumentException("Error parsing entity tag '" + header + "'", ex);
        }
        throw new IllegalArgumentException("Error parsing entity tag '" + header + "'");
    }
}

