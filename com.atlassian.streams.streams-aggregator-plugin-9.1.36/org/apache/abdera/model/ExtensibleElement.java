/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.model;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.abdera.model.Element;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ExtensibleElement
extends Element {
    public List<Element> getExtensions();

    public List<Element> getExtensions(String var1);

    public <T extends Element> List<T> getExtensions(QName var1);

    public <T extends Element> T getExtension(QName var1);

    public <T extends ExtensibleElement> T addExtension(Element var1);

    public <T extends ExtensibleElement> T addExtension(Element var1, Element var2);

    public <T extends Element> T addExtension(QName var1);

    public <T extends Element> T addExtension(QName var1, QName var2);

    public <T extends Element> T addExtension(String var1, String var2, String var3);

    public Element addSimpleExtension(QName var1, String var2);

    public Element addSimpleExtension(String var1, String var2, String var3, String var4);

    public String getSimpleExtension(QName var1);

    public String getSimpleExtension(String var1, String var2, String var3);

    public <T extends Element> T getExtension(Class<T> var1);
}

