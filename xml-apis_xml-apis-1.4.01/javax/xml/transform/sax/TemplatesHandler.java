/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.transform.sax;

import javax.xml.transform.Templates;
import org.xml.sax.ContentHandler;

public interface TemplatesHandler
extends ContentHandler {
    public Templates getTemplates();

    public void setSystemId(String var1);

    public String getSystemId();
}

