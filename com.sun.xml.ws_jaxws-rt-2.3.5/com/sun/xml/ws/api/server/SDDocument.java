/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.Nullable
 *  org.glassfish.gmbal.ManagedAttribute
 *  org.glassfish.gmbal.ManagedData
 */
package com.sun.xml.ws.api.server;

import com.sun.istack.Nullable;
import com.sun.xml.ws.api.server.DocumentAddressResolver;
import com.sun.xml.ws.api.server.PortAddressResolver;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedData;

@ManagedData
public interface SDDocument {
    @ManagedAttribute
    public QName getRootName();

    @ManagedAttribute
    public boolean isWSDL();

    @ManagedAttribute
    public boolean isSchema();

    @ManagedAttribute
    public Set<String> getImports();

    @ManagedAttribute
    public URL getURL();

    public void writeTo(@Nullable PortAddressResolver var1, DocumentAddressResolver var2, OutputStream var3) throws IOException;

    public void writeTo(PortAddressResolver var1, DocumentAddressResolver var2, XMLStreamWriter var3) throws XMLStreamException, IOException;

    public static interface WSDL
    extends SDDocument {
        @ManagedAttribute
        public String getTargetNamespace();

        @ManagedAttribute
        public boolean hasPortType();

        @ManagedAttribute
        public boolean hasService();

        @ManagedAttribute
        public Set<QName> getAllServices();
    }

    public static interface Schema
    extends SDDocument {
        @ManagedAttribute
        public String getTargetNamespace();
    }
}

