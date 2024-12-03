/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.values;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.GDate;
import org.apache.xmlbeans.GDuration;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.SchemaTypeImpl;
import org.apache.xmlbeans.impl.schema.SchemaTypeVisitorImpl;
import org.apache.xmlbeans.impl.values.NamespaceManager;
import org.apache.xmlbeans.impl.values.TypeStore;
import org.apache.xmlbeans.impl.values.TypeStoreUser;
import org.apache.xmlbeans.impl.values.TypeStoreVisitor;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

public class XmlComplexContentImpl
extends XmlObjectBase {
    private final SchemaTypeImpl _schemaType;

    public XmlComplexContentImpl(SchemaType type) {
        this._schemaType = (SchemaTypeImpl)type;
        this.initComplexType(true, true);
    }

    @Override
    public SchemaType schemaType() {
        return this._schemaType;
    }

    @Override
    public String compute_text(NamespaceManager nsm) {
        return null;
    }

    @Override
    protected final void set_String(String v) {
        assert (this._schemaType.getContentType() != 2);
        if (this._schemaType.getContentType() != 4 && !this._schemaType.isNoType()) {
            throw new IllegalArgumentException("Type does not allow for textual content: " + this._schemaType);
        }
        super.set_String(v);
    }

    @Override
    public void set_text(String str) {
        assert (this._schemaType.getContentType() == 4 || this._schemaType.isNoType());
    }

    @Override
    protected void update_from_complex_content() {
    }

    @Override
    public void set_nil() {
    }

    @Override
    public boolean equal_to(XmlObject complexObject) {
        return this._schemaType.equals(complexObject.schemaType());
    }

    @Override
    protected int value_hash_code() {
        throw new IllegalStateException("Complex types cannot be used as hash keys");
    }

    @Override
    public TypeStoreVisitor new_visitor() {
        return new SchemaTypeVisitorImpl(this._schemaType.getContentModel());
    }

    @Override
    public boolean is_child_element_order_sensitive() {
        return this.schemaType().isOrderSensitive();
    }

    @Override
    public int get_elementflags(QName eltName) {
        SchemaProperty prop = this.schemaType().getElementProperty(eltName);
        if (prop == null) {
            return 0;
        }
        if (prop.hasDefault() == 1 || prop.hasFixed() == 1 || prop.hasNillable() == 1) {
            return -1;
        }
        return (prop.hasDefault() == 0 ? 0 : 2) | (prop.hasFixed() == 0 ? 0 : 4) | (prop.hasNillable() == 0 ? 0 : 1);
    }

    @Override
    public String get_default_attribute_text(QName attrName) {
        return super.get_default_attribute_text(attrName);
    }

    @Override
    public String get_default_element_text(QName eltName) {
        SchemaProperty prop = this.schemaType().getElementProperty(eltName);
        if (prop == null) {
            return "";
        }
        return prop.getDefaultText();
    }

    protected void unionArraySetterHelper(Object[] sources, QName elemName) {
        this.commonSetterHelper2(elemName, null, sources, XmlObjectBase::setObjectValue);
    }

    protected SimpleValue[] arraySetterHelper(int sourcesLength, QName elemName) {
        SimpleValue[] sources = new SimpleValue[sourcesLength];
        this.commonSetterHelper(elemName, null, sources, (XmlObjectBase u, Integer i) -> {
            sources[i.intValue()] = u;
        });
        return sources;
    }

    protected SimpleValue[] arraySetterHelper(int sourcesLength, QName elemName, QNameSet set) {
        SimpleValue[] sources = new SimpleValue[sourcesLength];
        this.commonSetterHelper(elemName, set, sources, (XmlObjectBase u, Integer i) -> {
            sources[i.intValue()] = u;
        });
        return sources;
    }

    protected void arraySetterHelper(boolean[] sources, QName elemName) {
        this.commonSetterHelper(elemName, null, sources == null ? 0 : sources.length, (XmlObjectBase u, Integer i) -> u.setBooleanValue(sources[i]));
    }

    protected void arraySetterHelper(float[] sources, QName elemName) {
        this.commonSetterHelper(elemName, null, sources == null ? 0 : sources.length, (XmlObjectBase u, Integer i) -> u.setFloatValue(sources[i]));
    }

    protected void arraySetterHelper(double[] sources, QName elemName) {
        this.commonSetterHelper(elemName, null, sources == null ? 0 : sources.length, (XmlObjectBase u, Integer i) -> u.setDoubleValue(sources[i]));
    }

    protected void arraySetterHelper(byte[] sources, QName elemName) {
        this.commonSetterHelper(elemName, null, sources == null ? 0 : sources.length, (XmlObjectBase u, Integer i) -> u.setByteValue(sources[i]));
    }

    protected void arraySetterHelper(short[] sources, QName elemName) {
        this.commonSetterHelper(elemName, null, sources == null ? 0 : sources.length, (XmlObjectBase u, Integer i) -> u.setShortValue(sources[i]));
    }

    protected void arraySetterHelper(int[] sources, QName elemName) {
        this.commonSetterHelper(elemName, null, sources == null ? 0 : sources.length, (XmlObjectBase u, Integer i) -> u.setIntValue(sources[i]));
    }

    protected void arraySetterHelper(long[] sources, QName elemName) {
        this.commonSetterHelper(elemName, null, sources == null ? 0 : sources.length, (XmlObjectBase u, Integer i) -> u.setLongValue(sources[i]));
    }

    protected void arraySetterHelper(BigDecimal[] sources, QName elemName) {
        this.commonSetterHelper2(elemName, null, sources, XmlObjectBase::setBigDecimalValue);
    }

    protected void arraySetterHelper(BigInteger[] sources, QName elemName) {
        this.commonSetterHelper2(elemName, null, sources, XmlObjectBase::setBigIntegerValue);
    }

    protected void arraySetterHelper(String[] sources, QName elemName) {
        this.commonSetterHelper2(elemName, null, sources, XmlObjectBase::setStringValue);
    }

    protected void arraySetterHelper(byte[][] sources, QName elemName) {
        this.commonSetterHelper2(elemName, null, (T[])sources, (BiConsumer)XmlObjectBase::setByteArrayValue);
    }

    protected void arraySetterHelper(GDate[] sources, QName elemName) {
        this.commonSetterHelper2(elemName, null, sources, XmlObjectBase::setGDateValue);
    }

    protected void arraySetterHelper(GDuration[] sources, QName elemName) {
        this.commonSetterHelper2(elemName, null, sources, XmlObjectBase::setGDurationValue);
    }

    protected void arraySetterHelper(Calendar[] sources, QName elemName) {
        this.commonSetterHelper2(elemName, null, sources, XmlObjectBase::setCalendarValue);
    }

    protected void arraySetterHelper(Date[] sources, QName elemName) {
        this.commonSetterHelper2(elemName, null, sources, XmlObjectBase::setDateValue);
    }

    protected void arraySetterHelper(QName[] sources, QName elemName) {
        this.commonSetterHelper2(elemName, null, sources, XmlObjectBase::setQNameValue);
    }

    protected void arraySetterHelper(StringEnumAbstractBase[] sources, QName elemName) {
        this.commonSetterHelper2(elemName, null, sources, XmlObjectBase::setEnumValue);
    }

    protected void arraySetterHelper(List<?>[] sources, QName elemName) {
        this.commonSetterHelper2(elemName, null, sources, XmlObjectBase::setListValue);
    }

    protected void unionArraySetterHelper(Object[] sources, QName elemName, QNameSet set) {
        this.commonSetterHelper2(elemName, set, sources, XmlObjectBase::setObjectValue);
    }

    protected void arraySetterHelper(boolean[] sources, QName elemName, QNameSet set) {
        this.commonSetterHelper(elemName, set, sources == null ? 0 : sources.length, (XmlObjectBase u, Integer i) -> u.setBooleanValue(sources[i]));
    }

    protected void arraySetterHelper(float[] sources, QName elemName, QNameSet set) {
        this.commonSetterHelper(elemName, set, sources == null ? 0 : sources.length, (XmlObjectBase u, Integer i) -> u.setFloatValue(sources[i]));
    }

    protected void arraySetterHelper(double[] sources, QName elemName, QNameSet set) {
        this.commonSetterHelper(elemName, set, sources == null ? 0 : sources.length, (XmlObjectBase u, Integer i) -> u.setDoubleValue(sources[i]));
    }

    protected void arraySetterHelper(byte[] sources, QName elemName, QNameSet set) {
        this.commonSetterHelper(elemName, set, sources == null ? 0 : sources.length, (XmlObjectBase u, Integer i) -> u.setByteValue(sources[i]));
    }

    protected void arraySetterHelper(short[] sources, QName elemName, QNameSet set) {
        this.commonSetterHelper(elemName, set, sources == null ? 0 : sources.length, (XmlObjectBase u, Integer i) -> u.setShortValue(sources[i]));
    }

    protected void arraySetterHelper(int[] sources, QName elemName, QNameSet set) {
        this.commonSetterHelper(elemName, set, sources == null ? 0 : sources.length, (XmlObjectBase u, Integer i) -> u.setIntValue(sources[i]));
    }

    protected void arraySetterHelper(long[] sources, QName elemName, QNameSet set) {
        this.commonSetterHelper(elemName, set, sources == null ? 0 : sources.length, (XmlObjectBase u, Integer i) -> u.setLongValue(sources[i]));
    }

    protected void arraySetterHelper(BigDecimal[] sources, QName elemName, QNameSet set) {
        this.commonSetterHelper2(elemName, set, sources, XmlObjectBase::setBigDecimalValue);
    }

    protected void arraySetterHelper(BigInteger[] sources, QName elemName, QNameSet set) {
        this.commonSetterHelper2(elemName, set, sources, XmlObjectBase::setBigIntegerValue);
    }

    protected void arraySetterHelper(String[] sources, QName elemName, QNameSet set) {
        this.commonSetterHelper2(elemName, set, sources, XmlObjectBase::setStringValue);
    }

    protected void arraySetterHelper(byte[][] sources, QName elemName, QNameSet set) {
        this.commonSetterHelper2(elemName, set, (T[])sources, (BiConsumer)XmlObjectBase::setByteArrayValue);
    }

    protected void arraySetterHelper(GDate[] sources, QName elemName, QNameSet set) {
        this.commonSetterHelper2(elemName, set, sources, XmlObjectBase::setGDateValue);
    }

    protected void arraySetterHelper(GDuration[] sources, QName elemName, QNameSet set) {
        this.commonSetterHelper2(elemName, set, sources, XmlObjectBase::setGDurationValue);
    }

    protected void arraySetterHelper(Calendar[] sources, QName elemName, QNameSet set) {
        this.commonSetterHelper2(elemName, set, sources, XmlObjectBase::setCalendarValue);
    }

    protected void arraySetterHelper(Date[] sources, QName elemName, QNameSet set) {
        this.commonSetterHelper2(elemName, set, sources, XmlObjectBase::setDateValue);
    }

    protected void arraySetterHelper(QName[] sources, QName elemName, QNameSet set) {
        this.commonSetterHelper2(elemName, set, sources, XmlObjectBase::setQNameValue);
    }

    protected void arraySetterHelper(StringEnumAbstractBase[] sources, QName elemName, QNameSet set) {
        this.commonSetterHelper2(elemName, set, sources, XmlObjectBase::setEnumValue);
    }

    protected void arraySetterHelper(List<?>[] sources, QName elemName, QNameSet set) {
        this.commonSetterHelper2(elemName, set, sources, XmlObjectBase::setListValue);
    }

    protected void arraySetterHelper(XmlObject[] sources, QName elemName) {
        this.arraySetterHelper(sources, elemName, null);
    }

    protected void arraySetterHelper(XmlObject[] sources, QName elemName, QNameSet set) {
        TypeStoreUser user;
        int i;
        TypeStore store = this.get_store();
        if (sources == null || sources.length == 0) {
            int m;
            int n = m = set == null ? store.count_elements(elemName) : store.count_elements(set);
            while (m > 0) {
                if (set == null) {
                    store.remove_element(elemName, 0);
                } else {
                    store.remove_element(set, 0);
                }
                --m;
            }
            return;
        }
        int m = set == null ? store.count_elements(elemName) : store.count_elements(set);
        int startSrc = 0;
        int startDest = 0;
        for (i = 0; i < sources.length; ++i) {
            if (sources[i].isImmutable()) continue;
            try (XmlCursor c = sources[i].newCursor();){
                if (!c.toParent() || c.getObject() != this) continue;
                break;
            }
        }
        if (i < sources.length) {
            TypeStoreUser current;
            TypeStoreUser typeStoreUser = current = set == null ? store.find_element_user(elemName, 0) : store.find_element_user(set, 0);
            if (current == sources[i]) {
                int j;
                for (j = 0; j < i; ++j) {
                    user = set == null ? store.insert_element_user(elemName, j) : store.insert_element_user(set, elemName, j);
                    ((XmlObjectBase)user).set(sources[j]);
                }
                ++i;
                ++j;
                while (i < sources.length) {
                    XmlCursor c;
                    XmlCursor xmlCursor = c = sources[i].isImmutable() ? null : sources[i].newCursor();
                    if (c != null && c.toParent() && c.getObject() == this) {
                        c.close();
                        TypeStoreUser typeStoreUser2 = current = set == null ? store.find_element_user(elemName, j) : store.find_element_user(set, j);
                        if (current != sources[i]) {
                            break;
                        }
                    } else {
                        if (c != null) {
                            c.close();
                        }
                        TypeStoreUser user2 = set == null ? store.insert_element_user(elemName, j) : store.insert_element_user(set, elemName, j);
                        ((XmlObjectBase)user2).set(sources[i]);
                    }
                    ++i;
                    ++j;
                }
                startDest = j;
                startSrc = i;
                m = store.count_elements(elemName);
            }
        }
        for (int j = i; j < sources.length; ++j) {
            TypeStoreUser user3 = store.add_element_user(elemName);
            ((XmlObjectBase)user3).set(sources[j]);
        }
        int n = i;
        while (m > n - startSrc + startDest) {
            if (set == null) {
                store.remove_element(elemName, m - 1);
            } else {
                store.remove_element(set, m - 1);
            }
            --m;
        }
        i = startSrc;
        int j = startDest;
        while (i < n) {
            user = j >= m ? store.add_element_user(elemName) : (set == null ? store.find_element_user(elemName, j) : store.find_element_user(set, j));
            ((XmlObjectBase)user).set(sources[i]);
            ++i;
            ++j;
        }
    }

    private <T> void commonSetterHelper(QName elemName, QNameSet set, T[] sources, BiConsumer<XmlObjectBase, Integer> fun) {
        this.commonSetterHelper(elemName, set, sources == null ? 0 : sources.length, fun);
    }

    private void commonSetterHelper(QName elemName, QNameSet set, int n, BiConsumer<XmlObjectBase, Integer> fun) {
        int m;
        TypeStore store = this.get_store();
        int n2 = m = set == null ? store.count_elements(elemName) : store.count_elements(set);
        while (m > n) {
            if (set == null) {
                store.remove_element(elemName, m - 1);
            } else {
                store.remove_element(set, m - 1);
            }
            --m;
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user = i >= m ? store.add_element_user(elemName) : (set == null ? store.find_element_user(elemName, i) : store.find_element_user(set, i));
            fun.accept((XmlObjectBase)user, i);
        }
    }

    private <T> void commonSetterHelper2(QName elemName, QNameSet set, T[] sources, BiConsumer<XmlObjectBase, T> c) {
        int m;
        int n = sources == null ? 0 : sources.length;
        TypeStore store = this.get_store();
        int n2 = m = set == null ? store.count_elements(elemName) : store.count_elements(set);
        while (m > n) {
            if (set == null) {
                store.remove_element(elemName, m - 1);
            } else {
                store.remove_element(set, m - 1);
            }
            --m;
        }
        for (int i = 0; i < n; ++i) {
            TypeStoreUser user = i >= m ? store.add_element_user(elemName) : (set == null ? store.find_element_user(elemName, i) : store.find_element_user(set, i));
            c.accept((XmlObjectBase)user, (XmlObjectBase)sources[i]);
        }
    }
}

