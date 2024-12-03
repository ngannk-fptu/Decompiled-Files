/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.model.wsdl;

import com.sun.xml.ws.api.model.wsdl.WSDLExtension;
import com.sun.xml.ws.api.model.wsdl.WSDLObject;
import java.util.List;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public interface WSDLExtensible
extends WSDLObject {
    public Iterable<WSDLExtension> getExtensions();

    public <T extends WSDLExtension> Iterable<T> getExtensions(Class<T> var1);

    public <T extends WSDLExtension> T getExtension(Class<T> var1);

    public void addExtension(WSDLExtension var1);

    public boolean areRequiredExtensionsUnderstood();

    public void addNotUnderstoodExtension(QName var1, Locator var2);

    public List<? extends WSDLExtension> getNotUnderstoodExtensions();
}

