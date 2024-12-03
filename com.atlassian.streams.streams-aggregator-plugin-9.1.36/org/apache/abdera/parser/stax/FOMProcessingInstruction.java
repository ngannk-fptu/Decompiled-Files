/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax;

import java.io.CharArrayWriter;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ProcessingInstruction;
import org.apache.abdera.parser.stax.FOMFactory;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.llom.OMProcessingInstructionImpl;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FOMProcessingInstruction
extends OMProcessingInstructionImpl
implements ProcessingInstruction {
    public FOMProcessingInstruction(OMContainer parent, String target, String value, OMFactory factory, boolean fromBuilder) {
        super(parent, target, value, factory, fromBuilder);
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

    @Override
    public String getText() {
        return this.getValue();
    }

    @Override
    public <T extends ProcessingInstruction> T setText(String text) {
        this.setValue(text);
        return (T)this;
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

    @Override
    public void setTarget(String target) {
        super.setTarget(target);
    }
}

