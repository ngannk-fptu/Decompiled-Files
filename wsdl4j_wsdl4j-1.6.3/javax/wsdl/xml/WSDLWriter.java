/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl.xml;

import java.io.OutputStream;
import java.io.Writer;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import org.w3c.dom.Document;

public interface WSDLWriter {
    public void setFeature(String var1, boolean var2) throws IllegalArgumentException;

    public boolean getFeature(String var1) throws IllegalArgumentException;

    public Document getDocument(Definition var1) throws WSDLException;

    public void writeWSDL(Definition var1, Writer var2) throws WSDLException;

    public void writeWSDL(Definition var1, OutputStream var2) throws WSDLException;
}

