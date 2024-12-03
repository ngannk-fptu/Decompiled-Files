/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl.extensions;

import java.util.List;
import javax.wsdl.extensions.ExtensibilityElement;

public interface ElementExtensible {
    public void addExtensibilityElement(ExtensibilityElement var1);

    public ExtensibilityElement removeExtensibilityElement(ExtensibilityElement var1);

    public List getExtensibilityElements();
}

