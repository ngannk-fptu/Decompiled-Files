/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.factory;

import org.apache.abdera.model.Base;
import org.apache.abdera.model.Element;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ExtensionFactory {
    public boolean handlesNamespace(String var1);

    public String[] getNamespaces();

    public <T extends Element> T getElementWrapper(Element var1);

    public <T extends Base> String getMimeType(T var1);
}

