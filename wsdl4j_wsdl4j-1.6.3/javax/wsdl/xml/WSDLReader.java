/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl.xml;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.xml.WSDLLocator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public interface WSDLReader {
    public void setFeature(String var1, boolean var2) throws IllegalArgumentException;

    public boolean getFeature(String var1) throws IllegalArgumentException;

    public void setExtensionRegistry(ExtensionRegistry var1);

    public ExtensionRegistry getExtensionRegistry();

    public void setFactoryImplName(String var1) throws UnsupportedOperationException;

    public String getFactoryImplName();

    public Definition readWSDL(String var1) throws WSDLException;

    public Definition readWSDL(String var1, String var2) throws WSDLException;

    public Definition readWSDL(String var1, Element var2) throws WSDLException;

    public Definition readWSDL(WSDLLocator var1, Element var2) throws WSDLException;

    public Definition readWSDL(String var1, Document var2) throws WSDLException;

    public Definition readWSDL(String var1, InputSource var2) throws WSDLException;

    public Definition readWSDL(WSDLLocator var1) throws WSDLException;
}

