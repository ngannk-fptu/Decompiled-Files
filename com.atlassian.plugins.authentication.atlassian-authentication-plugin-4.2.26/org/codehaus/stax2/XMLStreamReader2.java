/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2;

import java.io.IOException;
import java.io.Writer;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.AttributeInfo;
import org.codehaus.stax2.DTDInfo;
import org.codehaus.stax2.LocationInfo;
import org.codehaus.stax2.typed.TypedXMLStreamReader;
import org.codehaus.stax2.validation.Validatable;

public interface XMLStreamReader2
extends TypedXMLStreamReader,
Validatable {
    public static final String FEATURE_DTD_OVERRIDE = "org.codehaus.stax2.propDtdOverride";

    public boolean isPropertySupported(String var1);

    public boolean setProperty(String var1, Object var2);

    public Object getFeature(String var1);

    public void setFeature(String var1, Object var2);

    public void skipElement() throws XMLStreamException;

    public DTDInfo getDTDInfo() throws XMLStreamException;

    public AttributeInfo getAttributeInfo() throws XMLStreamException;

    public LocationInfo getLocationInfo();

    public int getText(Writer var1, boolean var2) throws IOException, XMLStreamException;

    public boolean isEmptyElement() throws XMLStreamException;

    public int getDepth();

    public NamespaceContext getNonTransientNamespaceContext();

    public String getPrefixedName();

    public void closeCompletely() throws XMLStreamException;
}

