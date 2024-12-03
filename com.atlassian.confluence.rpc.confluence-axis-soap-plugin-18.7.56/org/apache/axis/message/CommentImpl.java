/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.message;

import org.apache.axis.message.Text;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CommentImpl
extends Text
implements javax.xml.soap.Text,
Comment {
    public CommentImpl(String text) {
        super(text);
    }

    public boolean isComment() {
        return true;
    }

    public org.w3c.dom.Text splitText(int offset) throws DOMException {
        int length = this.textRep.getLength();
        String tailData = this.textRep.substringData(offset, length);
        this.textRep.deleteData(offset, length);
        CommentImpl tailText = new CommentImpl(tailData);
        Node myParent = this.getParentNode();
        if (myParent != null) {
            NodeList brothers = myParent.getChildNodes();
            for (int i = 0; i < brothers.getLength(); ++i) {
                if (!brothers.item(i).equals(this)) continue;
                myParent.insertBefore(tailText, this);
                return tailText;
            }
        }
        return tailText;
    }
}

