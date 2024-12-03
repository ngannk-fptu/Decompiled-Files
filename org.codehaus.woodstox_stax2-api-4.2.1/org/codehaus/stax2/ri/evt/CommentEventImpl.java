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

    public CommentEventImpl(Location loc, String content) {
        super(loc);
        this.mContent = content;
    }

    @Override
    public String getText() {
        return this.mContent;
    }

    @Override
    public int getEventType() {
        return 5;
    }

    @Override
    public void writeAsEncodedUnicode(Writer w) throws XMLStreamException {
        try {
            w.write("<!--");
            w.write(this.mContent);
            w.write("-->");
        }
        catch (IOException ie) {
            this.throwFromIOE(ie);
        }
    }

    @Override
    public void writeUsing(XMLStreamWriter2 w) throws XMLStreamException {
        w.writeComment(this.mContent);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof Comment)) {
            return false;
        }
        Comment other = (Comment)o;
        return this.mContent.equals(other.getText());
    }

    @Override
    public int hashCode() {
        return this.mContent.hashCode();
    }
}

