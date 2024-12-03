/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.james.mime4j.MimeException
 *  org.apache.james.mime4j.stream.EntityState
 *  org.apache.james.mime4j.stream.Field
 *  org.apache.james.mime4j.stream.MimeTokenStream
 */
package org.apache.axiom.attachments;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.axiom.attachments.IncomingAttachmentInputStream;
import org.apache.axiom.attachments.IncomingAttachmentStreams;
import org.apache.axiom.om.OMException;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.stream.EntityState;
import org.apache.james.mime4j.stream.Field;
import org.apache.james.mime4j.stream.MimeTokenStream;

final class MultipartAttachmentStreams
extends IncomingAttachmentStreams {
    private final MimeTokenStream parser;

    public MultipartAttachmentStreams(MimeTokenStream parser) throws OMException {
        this.parser = parser;
    }

    public IncomingAttachmentInputStream getNextStream() throws OMException {
        IncomingAttachmentInputStream stream;
        if (!this.isReadyToGetNextStream()) {
            throw new IllegalStateException("nextStreamNotReady");
        }
        try {
            if (this.parser.getState() == EntityState.T_BODY) {
                if (this.parser.next() != EntityState.T_END_BODYPART) {
                    throw new IllegalStateException();
                }
                this.parser.next();
            }
            if (this.parser.getState() != EntityState.T_START_BODYPART) {
                return null;
            }
            if (this.parser.next() != EntityState.T_START_HEADER) {
                throw new IllegalStateException();
            }
            ArrayList<Field> fields = new ArrayList<Field>();
            while (this.parser.next() == EntityState.T_FIELD) {
                fields.add(this.parser.getField());
            }
            if (this.parser.next() != EntityState.T_BODY) {
                throw new IllegalStateException();
            }
            stream = new IncomingAttachmentInputStream(this.parser.getInputStream(), this);
            for (Field field : fields) {
                String name = field.getName();
                String value = field.getBody();
                if ("content-id".equals(name) || "content-type".equals(name) || "content-location".equals(name)) {
                    value = value.trim();
                }
                stream.addHeader(name, value);
            }
        }
        catch (MimeException ex) {
            throw new OMException(ex);
        }
        catch (IOException ex) {
            throw new OMException(ex);
        }
        this.setReadyToGetNextStream(false);
        return stream;
    }
}

