/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.compatibility.impl;

import com.microsoft.schemas.compatibility.AlternateContentDocument;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class AlternateContentDocumentImpl
extends XmlComplexContentImpl
implements AlternateContentDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/markup-compatibility/2006", "AlternateContent")};

    public AlternateContentDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AlternateContentDocument.AlternateContent getAlternateContent() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            AlternateContentDocument.AlternateContent target = null;
            target = (AlternateContentDocument.AlternateContent)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setAlternateContent(AlternateContentDocument.AlternateContent alternateContent) {
        this.generatedSetterHelperImpl(alternateContent, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AlternateContentDocument.AlternateContent addNewAlternateContent() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            AlternateContentDocument.AlternateContent target = null;
            target = (AlternateContentDocument.AlternateContent)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    public static class AlternateContentImpl
    extends XmlComplexContentImpl
    implements AlternateContentDocument.AlternateContent {
        private static final long serialVersionUID = 1L;
        private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/markup-compatibility/2006", "Choice"), new QName("http://schemas.openxmlformats.org/markup-compatibility/2006", "Fallback"), new QName("http://schemas.openxmlformats.org/markup-compatibility/2006", "Ignorable"), new QName("http://schemas.openxmlformats.org/markup-compatibility/2006", "MustUnderstand"), new QName("http://schemas.openxmlformats.org/markup-compatibility/2006", "ProcessContent")};

        public AlternateContentImpl(SchemaType sType) {
            super(sType);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public List<AlternateContentDocument.AlternateContent.Choice> getChoiceList() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return new JavaListXmlObject<AlternateContentDocument.AlternateContent.Choice>(this::getChoiceArray, this::setChoiceArray, this::insertNewChoice, this::removeChoice, this::sizeOfChoiceArray);
            }
        }

        @Override
        public AlternateContentDocument.AlternateContent.Choice[] getChoiceArray() {
            return (AlternateContentDocument.AlternateContent.Choice[])this.getXmlObjectArray(PROPERTY_QNAME[0], new AlternateContentDocument.AlternateContent.Choice[0]);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public AlternateContentDocument.AlternateContent.Choice getChoiceArray(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                AlternateContentDocument.AlternateContent.Choice target = null;
                target = (AlternateContentDocument.AlternateContent.Choice)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int sizeOfChoiceArray() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().count_elements(PROPERTY_QNAME[0]);
            }
        }

        @Override
        public void setChoiceArray(AlternateContentDocument.AlternateContent.Choice[] choiceArray) {
            this.check_orphaned();
            this.arraySetterHelper(choiceArray, PROPERTY_QNAME[0]);
        }

        @Override
        public void setChoiceArray(int i, AlternateContentDocument.AlternateContent.Choice choice) {
            this.generatedSetterHelperImpl(choice, PROPERTY_QNAME[0], i, (short)2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public AlternateContentDocument.AlternateContent.Choice insertNewChoice(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                AlternateContentDocument.AlternateContent.Choice target = null;
                target = (AlternateContentDocument.AlternateContent.Choice)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public AlternateContentDocument.AlternateContent.Choice addNewChoice() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                AlternateContentDocument.AlternateContent.Choice target = null;
                target = (AlternateContentDocument.AlternateContent.Choice)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removeChoice(int i) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_element(PROPERTY_QNAME[0], i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public AlternateContentDocument.AlternateContent.Fallback getFallback() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                AlternateContentDocument.AlternateContent.Fallback target = null;
                target = (AlternateContentDocument.AlternateContent.Fallback)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
                return target == null ? null : target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isSetFallback() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().count_elements(PROPERTY_QNAME[1]) != 0;
            }
        }

        @Override
        public void setFallback(AlternateContentDocument.AlternateContent.Fallback fallback) {
            this.generatedSetterHelperImpl(fallback, PROPERTY_QNAME[1], 0, (short)1);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public AlternateContentDocument.AlternateContent.Fallback addNewFallback() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                AlternateContentDocument.AlternateContent.Fallback target = null;
                target = (AlternateContentDocument.AlternateContent.Fallback)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void unsetFallback() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_element(PROPERTY_QNAME[1], 0);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public String getIgnorable() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
                return target == null ? null : target.getStringValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public XmlString xgetIgnorable() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlString target = null;
                target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isSetIgnorable() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(PROPERTY_QNAME[2]) != null;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setIgnorable(String ignorable) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
                if (target == null) {
                    target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[2]));
                }
                target.setStringValue(ignorable);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void xsetIgnorable(XmlString ignorable) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlString target = null;
                target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
                if (target == null) {
                    target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[2]));
                }
                target.set(ignorable);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void unsetIgnorable() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_attribute(PROPERTY_QNAME[2]);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public String getMustUnderstand() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[3]));
                return target == null ? null : target.getStringValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public XmlString xgetMustUnderstand() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlString target = null;
                target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[3]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isSetMustUnderstand() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(PROPERTY_QNAME[3]) != null;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setMustUnderstand(String mustUnderstand) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[3]));
                if (target == null) {
                    target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[3]));
                }
                target.setStringValue(mustUnderstand);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void xsetMustUnderstand(XmlString mustUnderstand) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlString target = null;
                target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[3]));
                if (target == null) {
                    target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[3]));
                }
                target.set(mustUnderstand);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void unsetMustUnderstand() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_attribute(PROPERTY_QNAME[3]);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public String getProcessContent() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[4]));
                return target == null ? null : target.getStringValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public XmlString xgetProcessContent() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlString target = null;
                target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[4]));
                return target;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isSetProcessContent() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(PROPERTY_QNAME[4]) != null;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void setProcessContent(String processContent) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[4]));
                if (target == null) {
                    target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[4]));
                }
                target.setStringValue(processContent);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void xsetProcessContent(XmlString processContent) {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                XmlString target = null;
                target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[4]));
                if (target == null) {
                    target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[4]));
                }
                target.set(processContent);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void unsetProcessContent() {
            Object object = this.monitor();
            synchronized (object) {
                this.check_orphaned();
                this.get_store().remove_attribute(PROPERTY_QNAME[4]);
            }
        }

        public static class FallbackImpl
        extends XmlComplexContentImpl
        implements AlternateContentDocument.AlternateContent.Fallback {
            private static final long serialVersionUID = 1L;
            private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/markup-compatibility/2006", "Ignorable"), new QName("http://schemas.openxmlformats.org/markup-compatibility/2006", "MustUnderstand"), new QName("http://schemas.openxmlformats.org/markup-compatibility/2006", "ProcessContent")};

            public FallbackImpl(SchemaType sType) {
                super(sType);
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public String getIgnorable() {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    SimpleValue target = null;
                    target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
                    return target == null ? null : target.getStringValue();
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public XmlString xgetIgnorable() {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    XmlString target = null;
                    target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
                    return target;
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public boolean isSetIgnorable() {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    return this.get_store().find_attribute_user(PROPERTY_QNAME[0]) != null;
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void setIgnorable(String ignorable) {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    SimpleValue target = null;
                    target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
                    if (target == null) {
                        target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[0]));
                    }
                    target.setStringValue(ignorable);
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void xsetIgnorable(XmlString ignorable) {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    XmlString target = null;
                    target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
                    if (target == null) {
                        target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[0]));
                    }
                    target.set(ignorable);
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void unsetIgnorable() {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    this.get_store().remove_attribute(PROPERTY_QNAME[0]);
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public String getMustUnderstand() {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    SimpleValue target = null;
                    target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
                    return target == null ? null : target.getStringValue();
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public XmlString xgetMustUnderstand() {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    XmlString target = null;
                    target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
                    return target;
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public boolean isSetMustUnderstand() {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    return this.get_store().find_attribute_user(PROPERTY_QNAME[1]) != null;
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void setMustUnderstand(String mustUnderstand) {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    SimpleValue target = null;
                    target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
                    if (target == null) {
                        target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[1]));
                    }
                    target.setStringValue(mustUnderstand);
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void xsetMustUnderstand(XmlString mustUnderstand) {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    XmlString target = null;
                    target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
                    if (target == null) {
                        target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[1]));
                    }
                    target.set(mustUnderstand);
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void unsetMustUnderstand() {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    this.get_store().remove_attribute(PROPERTY_QNAME[1]);
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public String getProcessContent() {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    SimpleValue target = null;
                    target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
                    return target == null ? null : target.getStringValue();
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public XmlString xgetProcessContent() {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    XmlString target = null;
                    target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
                    return target;
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public boolean isSetProcessContent() {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    return this.get_store().find_attribute_user(PROPERTY_QNAME[2]) != null;
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void setProcessContent(String processContent) {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    SimpleValue target = null;
                    target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
                    if (target == null) {
                        target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[2]));
                    }
                    target.setStringValue(processContent);
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void xsetProcessContent(XmlString processContent) {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    XmlString target = null;
                    target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
                    if (target == null) {
                        target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[2]));
                    }
                    target.set(processContent);
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void unsetProcessContent() {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    this.get_store().remove_attribute(PROPERTY_QNAME[2]);
                }
            }
        }

        public static class ChoiceImpl
        extends XmlComplexContentImpl
        implements AlternateContentDocument.AlternateContent.Choice {
            private static final long serialVersionUID = 1L;
            private static final QName[] PROPERTY_QNAME = new QName[]{new QName("", "Requires"), new QName("http://schemas.openxmlformats.org/markup-compatibility/2006", "Ignorable"), new QName("http://schemas.openxmlformats.org/markup-compatibility/2006", "MustUnderstand"), new QName("http://schemas.openxmlformats.org/markup-compatibility/2006", "ProcessContent")};

            public ChoiceImpl(SchemaType sType) {
                super(sType);
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public String getRequires() {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    SimpleValue target = null;
                    target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
                    return target == null ? null : target.getStringValue();
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public XmlString xgetRequires() {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    XmlString target = null;
                    target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
                    return target;
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void setRequires(String requires) {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    SimpleValue target = null;
                    target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
                    if (target == null) {
                        target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[0]));
                    }
                    target.setStringValue(requires);
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void xsetRequires(XmlString requires) {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    XmlString target = null;
                    target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
                    if (target == null) {
                        target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[0]));
                    }
                    target.set(requires);
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public String getIgnorable() {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    SimpleValue target = null;
                    target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
                    return target == null ? null : target.getStringValue();
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public XmlString xgetIgnorable() {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    XmlString target = null;
                    target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
                    return target;
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public boolean isSetIgnorable() {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    return this.get_store().find_attribute_user(PROPERTY_QNAME[1]) != null;
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void setIgnorable(String ignorable) {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    SimpleValue target = null;
                    target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
                    if (target == null) {
                        target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[1]));
                    }
                    target.setStringValue(ignorable);
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void xsetIgnorable(XmlString ignorable) {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    XmlString target = null;
                    target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
                    if (target == null) {
                        target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[1]));
                    }
                    target.set(ignorable);
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void unsetIgnorable() {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    this.get_store().remove_attribute(PROPERTY_QNAME[1]);
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public String getMustUnderstand() {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    SimpleValue target = null;
                    target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
                    return target == null ? null : target.getStringValue();
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public XmlString xgetMustUnderstand() {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    XmlString target = null;
                    target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
                    return target;
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public boolean isSetMustUnderstand() {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    return this.get_store().find_attribute_user(PROPERTY_QNAME[2]) != null;
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void setMustUnderstand(String mustUnderstand) {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    SimpleValue target = null;
                    target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
                    if (target == null) {
                        target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[2]));
                    }
                    target.setStringValue(mustUnderstand);
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void xsetMustUnderstand(XmlString mustUnderstand) {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    XmlString target = null;
                    target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
                    if (target == null) {
                        target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[2]));
                    }
                    target.set(mustUnderstand);
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void unsetMustUnderstand() {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    this.get_store().remove_attribute(PROPERTY_QNAME[2]);
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public String getProcessContent() {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    SimpleValue target = null;
                    target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[3]));
                    return target == null ? null : target.getStringValue();
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public XmlString xgetProcessContent() {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    XmlString target = null;
                    target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[3]));
                    return target;
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public boolean isSetProcessContent() {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    return this.get_store().find_attribute_user(PROPERTY_QNAME[3]) != null;
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void setProcessContent(String processContent) {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    SimpleValue target = null;
                    target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[3]));
                    if (target == null) {
                        target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[3]));
                    }
                    target.setStringValue(processContent);
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void xsetProcessContent(XmlString processContent) {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    XmlString target = null;
                    target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[3]));
                    if (target == null) {
                        target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[3]));
                    }
                    target.set(processContent);
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void unsetProcessContent() {
                Object object = this.monitor();
                synchronized (object) {
                    this.check_orphaned();
                    this.get_store().remove_attribute(PROPERTY_QNAME[3]);
                }
            }
        }
    }
}

