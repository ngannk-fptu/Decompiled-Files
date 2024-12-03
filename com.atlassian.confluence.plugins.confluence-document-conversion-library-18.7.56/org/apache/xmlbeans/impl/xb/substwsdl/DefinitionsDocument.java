/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.substwsdl;

import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.xb.substwsdl.TImport;
import org.apache.xmlbeans.metadata.system.sXMLTOOLS.TypeSystemHolder;

public interface DefinitionsDocument
extends XmlObject {
    public static final DocumentFactory<DefinitionsDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "definitionsc7f1doctype");
    public static final SchemaType type = Factory.getType();

    public Definitions getDefinitions();

    public void setDefinitions(Definitions var1);

    public Definitions addNewDefinitions();

    public static interface Definitions
    extends XmlObject {
        public static final ElementFactory<Definitions> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "definitions05ddelemtype");
        public static final SchemaType type = Factory.getType();

        public List<TImport> getImportList();

        public TImport[] getImportArray();

        public TImport getImportArray(int var1);

        public int sizeOfImportArray();

        public void setImportArray(TImport[] var1);

        public void setImportArray(int var1, TImport var2);

        public TImport insertNewImport(int var1);

        public TImport addNewImport();

        public void removeImport(int var1);

        public List<XmlObject> getTypesList();

        public XmlObject[] getTypesArray();

        public XmlObject getTypesArray(int var1);

        public int sizeOfTypesArray();

        public void setTypesArray(XmlObject[] var1);

        public void setTypesArray(int var1, XmlObject var2);

        public XmlObject insertNewTypes(int var1);

        public XmlObject addNewTypes();

        public void removeTypes(int var1);

        public List<XmlObject> getMessageList();

        public XmlObject[] getMessageArray();

        public XmlObject getMessageArray(int var1);

        public int sizeOfMessageArray();

        public void setMessageArray(XmlObject[] var1);

        public void setMessageArray(int var1, XmlObject var2);

        public XmlObject insertNewMessage(int var1);

        public XmlObject addNewMessage();

        public void removeMessage(int var1);

        public List<XmlObject> getBindingList();

        public XmlObject[] getBindingArray();

        public XmlObject getBindingArray(int var1);

        public int sizeOfBindingArray();

        public void setBindingArray(XmlObject[] var1);

        public void setBindingArray(int var1, XmlObject var2);

        public XmlObject insertNewBinding(int var1);

        public XmlObject addNewBinding();

        public void removeBinding(int var1);

        public List<XmlObject> getPortTypeList();

        public XmlObject[] getPortTypeArray();

        public XmlObject getPortTypeArray(int var1);

        public int sizeOfPortTypeArray();

        public void setPortTypeArray(XmlObject[] var1);

        public void setPortTypeArray(int var1, XmlObject var2);

        public XmlObject insertNewPortType(int var1);

        public XmlObject addNewPortType();

        public void removePortType(int var1);

        public List<XmlObject> getServiceList();

        public XmlObject[] getServiceArray();

        public XmlObject getServiceArray(int var1);

        public int sizeOfServiceArray();

        public void setServiceArray(XmlObject[] var1);

        public void setServiceArray(int var1, XmlObject var2);

        public XmlObject insertNewService(int var1);

        public XmlObject addNewService();

        public void removeService(int var1);
    }
}

