/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri.evt;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Comment;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.ri.evt.BaseEventImpl;

public class CommentEventImpl
extends BaseEventImpl
implements Comment {
    final String mContent;

    public CommentEventImpl(Location location, String string) {
        super(location);
        this.mContent = string;
    }

    public String getText() {
        return this.mContent;
    }

    public int getEventType() {
        return 5;
    }

    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write("<!--");
            writer.write(this.mContent);
            writer.write("-->");
        }
        catch (IOException iOException) {
            this.throwFromIOE(iOException);
        }
    }

    public void writeUsing(XMLStreamWriter2 xMLStreamWriter2) throws XMLStreamException {
        xMLStreamWriter2.writeComment(this.mContent);
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (!(object instanceof Comment)) {
            return false;
        }
        Comment comment = (Comment)object;
        return this.mContent.equals(comment.getText());
    }

    public int hashCode() {
        return this.mContent.hashCode();
    }
}

