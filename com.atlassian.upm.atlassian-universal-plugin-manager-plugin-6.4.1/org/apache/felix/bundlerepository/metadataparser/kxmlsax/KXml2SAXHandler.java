/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.bundlerepository.metadataparser.kxmlsax;

import java.util.Properties;

public interface KXml2SAXHandler {
    public void characters(char[] var1, int var2, int var3) throws Exception;

    public void startElement(String var1, String var2, String var3, Properties var4) throws Exception;

    public void endElement(String var1, String var2, String var3) throws Exception;

    public void processingInstruction(String var1, String var2) throws Exception;

    public void setLineNumber(int var1);

    public void setColumnNumber(int var1);
}

