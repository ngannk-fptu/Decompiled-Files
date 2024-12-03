/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xmlconfig;

import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.xb.xmlconfig.Extensionconfig;
import org.apache.xmlbeans.impl.xb.xmlconfig.Nsconfig;
import org.apache.xmlbeans.impl.xb.xmlconfig.Qnameconfig;
import org.apache.xmlbeans.impl.xb.xmlconfig.Usertypeconfig;
import org.apache.xmlbeans.metadata.system.sXMLCONFIG.TypeSystemHolder;

public interface ConfigDocument
extends XmlObject {
    public static final DocumentFactory<ConfigDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "config4185doctype");
    public static final SchemaType type = Factory.getType();

    public Config getConfig();

    public void setConfig(Config var1);

    public Config addNewConfig();

    public static interface Config
    extends XmlObject {
        public static final ElementFactory<Config> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "configf467elemtype");
        public static final SchemaType type = Factory.getType();

        public List<Nsconfig> getNamespaceList();

        public Nsconfig[] getNamespaceArray();

        public Nsconfig getNamespaceArray(int var1);

        public int sizeOfNamespaceArray();

        public void setNamespaceArray(Nsconfig[] var1);

        public void setNamespaceArray(int var1, Nsconfig var2);

        public Nsconfig insertNewNamespace(int var1);

        public Nsconfig addNewNamespace();

        public void removeNamespace(int var1);

        public List<Qnameconfig> getQnameList();

        public Qnameconfig[] getQnameArray();

        public Qnameconfig getQnameArray(int var1);

        public int sizeOfQnameArray();

        public void setQnameArray(Qnameconfig[] var1);

        public void setQnameArray(int var1, Qnameconfig var2);

        public Qnameconfig insertNewQname(int var1);

        public Qnameconfig addNewQname();

        public void removeQname(int var1);

        public List<Extensionconfig> getExtensionList();

        public Extensionconfig[] getExtensionArray();

        public Extensionconfig getExtensionArray(int var1);

        public int sizeOfExtensionArray();

        public void setExtensionArray(Extensionconfig[] var1);

        public void setExtensionArray(int var1, Extensionconfig var2);

        public Extensionconfig insertNewExtension(int var1);

        public Extensionconfig addNewExtension();

        public void removeExtension(int var1);

        public List<Usertypeconfig> getUsertypeList();

        public Usertypeconfig[] getUsertypeArray();

        public Usertypeconfig getUsertypeArray(int var1);

        public int sizeOfUsertypeArray();

        public void setUsertypeArray(Usertypeconfig[] var1);

        public void setUsertypeArray(int var1, Usertypeconfig var2);

        public Usertypeconfig insertNewUsertype(int var1);

        public Usertypeconfig addNewUsertype();

        public void removeUsertype(int var1);
    }
}

