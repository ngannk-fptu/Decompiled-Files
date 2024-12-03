/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl.extensions;

import java.io.PrintWriter;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.xml.namespace.QName;

public interface ExtensionSerializer {
    public void marshall(Class var1, QName var2, ExtensibilityElement var3, PrintWriter var4, Definition var5, ExtensionRegistry var6) throws WSDLException;
}

