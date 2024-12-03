/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.apache.abdera.parser.stax;

import java.io.IOException;
import java.io.InputStream;
import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.TextValue;
import org.apache.abdera.parser.stax.FOMException;
import org.apache.abdera.parser.stax.FOMFactory;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.llom.OMTextImpl;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FOMTextValue
extends OMTextImpl
implements TextValue {
    public FOMTextValue(OMContainer parent, Object dataHandler, boolean optimize, OMFactory factory, boolean fromBuilder) {
        super(parent, dataHandler, optimize, factory, fromBuilder);
    }

    public FOMTextValue(Object dataHandler, OMFactory factory) {
        super(dataHandler, factory);
    }

    public FOMTextValue(OMContainer parent, char[] charArray, int nodeType, OMFactory factory) {
        super(parent, charArray, nodeType, factory);
    }

    public FOMTextValue(OMContainer parent, QName text, int nodeType, OMFactory factory) {
        super(parent, text, nodeType, factory);
    }

    public FOMTextValue(OMContainer parent, QName text, OMFactory factory) {
        super(parent, text, factory);
    }

    public FOMTextValue(OMContainer parent, String text, int nodeType, OMFactory factory, boolean fromBuilder) {
        super(parent, text, nodeType, factory, fromBuilder);
    }

    public FOMTextValue(OMContainer parent, String text, OMFactory factory) {
        super(parent, text, factory);
    }

    public FOMTextValue(OMContainer parent, String s, String mimeType, boolean optimize, OMFactory factory) {
        super(parent, s, mimeType, optimize, factory);
    }

    public FOMTextValue(String text, int nodeType, OMFactory factory) {
        super(text, nodeType, factory);
    }

    public FOMTextValue(String text, OMFactory factory) {
        super(text, factory);
    }

    public FOMTextValue(String s, String mimeType, boolean optimize, OMFactory factory) {
        super(s, mimeType, optimize, factory);
    }

    @Override
    public DataHandler getDataHandler() {
        return (DataHandler)super.getDataHandler();
    }

    @Override
    public InputStream getInputStream() {
        try {
            return this.getDataHandler().getInputStream();
        }
        catch (IOException ex) {
            throw new FOMException(ex);
        }
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

    public Factory getFactory() {
        return (Factory)((Object)this.factory);
    }

    public String toString() {
        return this.getText();
    }
}

