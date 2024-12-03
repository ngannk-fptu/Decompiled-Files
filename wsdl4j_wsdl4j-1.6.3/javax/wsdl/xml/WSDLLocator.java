/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl.xml;

import org.xml.sax.InputSource;

public interface WSDLLocator {
    public InputSource getBaseInputSource();

    public InputSource getImportInputSource(String var1, String var2);

    public String getBaseURI();

    public String getLatestImportURI();

    public void close();
}

