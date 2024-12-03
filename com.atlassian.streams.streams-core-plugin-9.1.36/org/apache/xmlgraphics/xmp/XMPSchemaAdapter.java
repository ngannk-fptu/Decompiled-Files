/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.xmp;

import java.util.Date;
import java.util.TimeZone;
import org.apache.xmlgraphics.util.DateFormatUtil;
import org.apache.xmlgraphics.util.QName;
import org.apache.xmlgraphics.xmp.Metadata;
import org.apache.xmlgraphics.xmp.PropertyAccess;
import org.apache.xmlgraphics.xmp.XMPArray;
import org.apache.xmlgraphics.xmp.XMPArrayType;
import org.apache.xmlgraphics.xmp.XMPProperty;
import org.apache.xmlgraphics.xmp.XMPSchema;

public class XMPSchemaAdapter {
    protected Metadata meta;
    private XMPSchema schema;
    private boolean compact = true;

    public XMPSchemaAdapter(Metadata meta, XMPSchema schema) {
        if (meta == null) {
            throw new NullPointerException("Parameter meta must not be null");
        }
        if (schema == null) {
            throw new NullPointerException("Parameter schema must not be null");
        }
        this.meta = meta;
        this.schema = schema;
    }

    public XMPSchema getSchema() {
        return this.schema;
    }

    protected QName getQName(String propName) {
        return new QName(this.getSchema().getNamespace(), this.getSchema().getPreferredPrefix(), propName);
    }

    private void addStringToArray(String propName, String value, XMPArrayType arrayType) {
        if (value == null || value.length() == 0) {
            throw new IllegalArgumentException("'" + propName + "' value must not be empty");
        }
        this.addObjectToArray(propName, value, arrayType);
    }

    protected void addObjectToArray(String propName, Object value, XMPArrayType arrayType) {
        if (value == null) {
            throw new IllegalArgumentException("'" + propName + "' value must not be null");
        }
        QName name = this.getQName(propName);
        XMPProperty prop = this.meta.getProperty(name);
        if (prop == null) {
            prop = new XMPProperty(name, value);
            this.meta.setProperty(prop);
            if (!this.compact) {
                prop.convertSimpleValueToArray(arrayType);
            }
        } else {
            prop.convertSimpleValueToArray(arrayType);
            prop.getArrayValue().add(value);
        }
    }

    protected boolean removeStringFromArray(String propName, String value) {
        if (value == null) {
            return false;
        }
        QName name = this.getQName(propName);
        XMPProperty prop = this.meta.getProperty(name);
        if (prop != null) {
            if (prop.isArray()) {
                XMPArray arr = prop.getArrayValue();
                boolean removed = arr.remove(value);
                if (arr.isEmpty()) {
                    this.meta.removeProperty(name);
                }
                return removed;
            }
            Object currentValue = prop.getValue();
            if (value.equals(currentValue)) {
                this.meta.removeProperty(name);
                return true;
            }
        }
        return false;
    }

    protected void addStringToSeq(String propName, String value) {
        this.addStringToArray(propName, value, XMPArrayType.SEQ);
    }

    protected void addStringToBag(String propName, String value) {
        this.addStringToArray(propName, value, XMPArrayType.BAG);
    }

    public static String formatISO8601Date(Date dt) {
        return XMPSchemaAdapter.formatISO8601Date(dt, TimeZone.getDefault());
    }

    public static String formatISO8601Date(Date dt, TimeZone tz) {
        return DateFormatUtil.formatISO8601(dt, tz);
    }

    protected void addDateToSeq(String propName, Date value) {
        String dt = XMPSchemaAdapter.formatISO8601Date(value);
        this.addStringToSeq(propName, dt);
    }

    protected void setDateValue(String propName, Date value) {
        String dt = XMPSchemaAdapter.formatISO8601Date(value);
        this.setValue(propName, dt);
    }

    protected Date getDateValue(String propName) {
        String dt = this.getValue(propName);
        if (dt == null) {
            return null;
        }
        return DateFormatUtil.parseISO8601Date(dt);
    }

    protected void setLangAlt(String propName, String lang, String value) {
        QName name;
        XMPProperty prop;
        if (lang == null) {
            lang = "x-default";
        }
        if ((prop = this.meta.getProperty(name = this.getQName(propName))) == null) {
            if (value != null && value.length() > 0) {
                prop = new XMPProperty(name, value);
                prop.setXMLLang(lang);
                this.meta.setProperty(prop);
            }
        } else {
            prop.convertSimpleValueToArray(XMPArrayType.ALT);
            XMPArray array = prop.getArrayValue();
            array.removeLangValue(lang);
            if (value != null && value.length() > 0) {
                array.add(value, lang);
            } else if (array.isEmpty()) {
                this.meta.removeProperty(name);
            }
        }
    }

    protected void setValue(String propName, String value) {
        QName name = this.getQName(propName);
        XMPProperty prop = this.meta.getProperty(name);
        if (value != null && value.length() > 0) {
            if (prop != null) {
                prop.setValue(value);
            } else {
                prop = new XMPProperty(name, value);
                this.meta.setProperty(prop);
            }
        } else if (prop != null) {
            this.meta.removeProperty(name);
        }
    }

    protected String getValue(String propName) {
        QName name = this.getQName(propName);
        XMPProperty prop = this.meta.getProperty(name);
        if (prop == null) {
            return null;
        }
        return prop.getValue().toString();
    }

    protected String removeLangAlt(String lang, String propName) {
        QName name = this.getQName(propName);
        XMPProperty prop = this.meta.getProperty(name);
        if (prop != null && lang != null) {
            XMPArray array = prop.getArrayValue();
            if (array != null) {
                String removed = array.removeLangValue(lang);
                if (array.isEmpty()) {
                    this.meta.removeProperty(name);
                }
                return removed;
            }
            String removed = prop.getValue().toString();
            if (lang.equals(prop.getXMLLang())) {
                this.meta.removeProperty(name);
            }
            return removed;
        }
        return null;
    }

    protected String getLangAlt(String lang, String propName) {
        XMPProperty prop = this.meta.getProperty(this.getQName(propName));
        if (prop == null) {
            return null;
        }
        XMPArray array = prop.getArrayValue();
        if (array != null) {
            return array.getLangValue(lang);
        }
        return prop.getValue().toString();
    }

    protected PropertyAccess findQualifiedStructure(String propName, QName qualifier, String qualifierValue) {
        XMPProperty prop = this.meta.getProperty(this.getQName(propName));
        if (prop != null) {
            PropertyAccess pa;
            XMPProperty q;
            XMPArray array = prop.getArrayValue();
            if (array != null) {
                int c = array.getSize();
                for (int i = 0; i < c; ++i) {
                    PropertyAccess pa2;
                    XMPProperty q2;
                    Object value = array.getValue(i);
                    if (!(value instanceof PropertyAccess) || (q2 = (pa2 = (PropertyAccess)value).getProperty(qualifier)) == null || !q2.getValue().equals(qualifierValue)) continue;
                    return pa2;
                }
            } else if (prop.getStructureValue() != null && (q = (pa = prop.getStructureValue()).getProperty(qualifier)) != null && q.getValue().equals(qualifierValue)) {
                return pa;
            }
        }
        return null;
    }

    protected Object findQualifiedValue(String propName, QName qualifier, String qualifierValue) {
        XMPProperty rdfValue;
        PropertyAccess pa = this.findQualifiedStructure(propName, qualifier, qualifierValue);
        if (pa != null && (rdfValue = pa.getValueProperty()) != null) {
            return rdfValue.getValue();
        }
        return null;
    }

    protected Object[] getObjectArray(String propName) {
        XMPProperty prop = this.meta.getProperty(this.getQName(propName));
        if (prop == null) {
            return null;
        }
        XMPArray array = prop.getArrayValue();
        if (array != null) {
            return array.toObjectArray();
        }
        return new Object[]{prop.getValue()};
    }

    protected String[] getStringArray(String propName) {
        Object[] arr = this.getObjectArray(propName);
        if (arr == null) {
            return null;
        }
        String[] res = new String[arr.length];
        int c = res.length;
        for (int i = 0; i < c; ++i) {
            Object o = arr[i];
            if (o instanceof PropertyAccess) {
                XMPProperty prop = ((PropertyAccess)o).getValueProperty();
                res[i] = prop.getValue().toString();
                continue;
            }
            res[i] = o.toString();
        }
        return res;
    }

    protected Date[] getDateArray(String propName) {
        Object[] arr = this.getObjectArray(propName);
        if (arr == null) {
            return null;
        }
        Date[] res = new Date[arr.length];
        int c = res.length;
        for (int i = 0; i < c; ++i) {
            Object obj = arr[i];
            res[i] = obj instanceof Date ? (Date)((Date)obj).clone() : DateFormatUtil.parseISO8601Date(obj.toString());
        }
        return res;
    }

    public void setCompact(boolean c) {
        this.compact = c;
    }
}

