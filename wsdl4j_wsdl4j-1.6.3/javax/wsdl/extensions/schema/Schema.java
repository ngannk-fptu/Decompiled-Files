/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl.extensions.schema;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.schema.SchemaImport;
import javax.wsdl.extensions.schema.SchemaReference;
import org.w3c.dom.Element;

public interface Schema
extends ExtensibilityElement,
Serializable {
    public Map getImports();

    public SchemaImport createImport();

    public void addImport(SchemaImport var1);

    public List getIncludes();

    public SchemaReference createInclude();

    public void addInclude(SchemaReference var1);

    public List getRedefines();

    public SchemaReference createRedefine();

    public void addRedefine(SchemaReference var1);

    public void setElement(Element var1);

    public Element getElement();

    public void setDocumentBaseURI(String var1);

    public String getDocumentBaseURI();
}

