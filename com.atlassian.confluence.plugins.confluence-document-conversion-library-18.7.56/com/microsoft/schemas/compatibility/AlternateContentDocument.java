/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.compatibility;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.ElementFactory;

public interface AlternateContentDocument
extends XmlObject {
    public static final DocumentFactory<AlternateContentDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "alternatecontentdd64doctype");
    public static final SchemaType type = Factory.getType();

    public AlternateContent getAlternateContent();

    public void setAlternateContent(AlternateContent var1);

    public AlternateContent addNewAlternateContent();

    public static interface AlternateContent
    extends XmlObject {
        public static final ElementFactory<AlternateContent> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "alternatecontenta8a9elemtype");
        public static final SchemaType type = Factory.getType();

        public List<Choice> getChoiceList();

        public Choice[] getChoiceArray();

        public Choice getChoiceArray(int var1);

        public int sizeOfChoiceArray();

        public void setChoiceArray(Choice[] var1);

        public void setChoiceArray(int var1, Choice var2);

        public Choice insertNewChoice(int var1);

        public Choice addNewChoice();

        public void removeChoice(int var1);

        public Fallback getFallback();

        public boolean isSetFallback();

        public void setFallback(Fallback var1);

        public Fallback addNewFallback();

        public void unsetFallback();

        public String getIgnorable();

        public XmlString xgetIgnorable();

        public boolean isSetIgnorable();

        public void setIgnorable(String var1);

        public void xsetIgnorable(XmlString var1);

        public void unsetIgnorable();

        public String getMustUnderstand();

        public XmlString xgetMustUnderstand();

        public boolean isSetMustUnderstand();

        public void setMustUnderstand(String var1);

        public void xsetMustUnderstand(XmlString var1);

        public void unsetMustUnderstand();

        public String getProcessContent();

        public XmlString xgetProcessContent();

        public boolean isSetProcessContent();

        public void setProcessContent(String var1);

        public void xsetProcessContent(XmlString var1);

        public void unsetProcessContent();

        public static interface Fallback
        extends XmlObject {
            public static final ElementFactory<Fallback> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "fallback4cc7elemtype");
            public static final SchemaType type = Factory.getType();

            public String getIgnorable();

            public XmlString xgetIgnorable();

            public boolean isSetIgnorable();

            public void setIgnorable(String var1);

            public void xsetIgnorable(XmlString var1);

            public void unsetIgnorable();

            public String getMustUnderstand();

            public XmlString xgetMustUnderstand();

            public boolean isSetMustUnderstand();

            public void setMustUnderstand(String var1);

            public void xsetMustUnderstand(XmlString var1);

            public void unsetMustUnderstand();

            public String getProcessContent();

            public XmlString xgetProcessContent();

            public boolean isSetProcessContent();

            public void setProcessContent(String var1);

            public void xsetProcessContent(XmlString var1);

            public void unsetProcessContent();
        }

        public static interface Choice
        extends XmlObject {
            public static final ElementFactory<Choice> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "choice69c6elemtype");
            public static final SchemaType type = Factory.getType();

            public String getRequires();

            public XmlString xgetRequires();

            public void setRequires(String var1);

            public void xsetRequires(XmlString var1);

            public String getIgnorable();

            public XmlString xgetIgnorable();

            public boolean isSetIgnorable();

            public void setIgnorable(String var1);

            public void xsetIgnorable(XmlString var1);

            public void unsetIgnorable();

            public String getMustUnderstand();

            public XmlString xgetMustUnderstand();

            public boolean isSetMustUnderstand();

            public void setMustUnderstand(String var1);

            public void xsetMustUnderstand(XmlString var1);

            public void unsetMustUnderstand();

            public String getProcessContent();

            public XmlString xgetProcessContent();

            public boolean isSetProcessContent();

            public void setProcessContent(String var1);

            public void xsetProcessContent(XmlString var1);

            public void unsetProcessContent();
        }
    }
}

