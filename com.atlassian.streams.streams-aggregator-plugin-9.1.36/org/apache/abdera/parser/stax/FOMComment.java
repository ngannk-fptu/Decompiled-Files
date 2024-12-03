/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax;

import java.io.CharArrayWriter;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Comment;
import org.apache.abdera.model.Element;
import org.apache.abdera.parser.stax.FOMFactory;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.llom.OMCommentImpl;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FOMComment
extends OMCommentImpl
implements Comment {
    public FOMComment(OMContainer parent, String contentText, OMFactory factory, boolean fromBuilder) {
        super(parent, contentText, factory, fromBuilder);
    }

    @Override
    public String getText() {
        return super.getValue();
    }

    @Override
    public Comment setText(String text) {
        super.setValue(text);
        return this;
    }

    @Override
    public <T extends Base> T getParentElement() {
        Base parent = (Base)((Object)super.getParent());
        return (T)(parent instanceof Element ? this.getWrapped((Element)parent) : parent);
    }

    protected Element getWrapped(Element internal) {
        if (internal == null) {
            return null;
        }
        FOMFactory factory = (FOMFactory)this.getFactory();
        return factory.getElementWrapper(internal);
    }

    @Override
    public Factory getFactory() {
        return (Factory)((Object)this.factory);
    }

    public String toString() {
        CharArrayWriter w = new CharArrayWriter();
        try {
            super.serialize(w);
        }
        catch (Exception exception) {
            // empty catch block
        }
        return w.toString();
    }
}

