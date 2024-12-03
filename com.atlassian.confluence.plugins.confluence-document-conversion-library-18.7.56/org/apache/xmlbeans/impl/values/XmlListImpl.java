/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.values;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlSimpleList;
import org.apache.xmlbeans.impl.common.PrefixResolver;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.common.ValidationContext;
import org.apache.xmlbeans.impl.common.XMLChar;
import org.apache.xmlbeans.impl.values.NamespaceContext;
import org.apache.xmlbeans.impl.values.NamespaceManager;
import org.apache.xmlbeans.impl.values.XmlObjectBase;
import org.apache.xmlbeans.impl.values.XmlValueOutOfRangeException;

public class XmlListImpl
extends XmlObjectBase
implements XmlAnySimpleType {
    private final SchemaType _schemaType;
    private XmlSimpleList<? extends XmlAnySimpleType> _value;
    private XmlSimpleList<?> _jvalue;
    private static final String[] EMPTY_STRINGARRAY = new String[0];

    public XmlListImpl(SchemaType type, boolean complex) {
        this._schemaType = type;
        this.initComplexType(complex, false);
    }

    @Override
    public SchemaType schemaType() {
        return this._schemaType;
    }

    private static String compute_list_text(List<? extends XmlAnySimpleType> xList) {
        return xList.isEmpty() ? "" : xList.stream().map(XmlListImpl::object2String).collect(Collectors.joining(" "));
    }

    private static String object2String(Object o) {
        String s = o instanceof SimpleValue ? ((SimpleValue)o).getStringValue() : o.toString();
        return s == null ? "" : s;
    }

    @Override
    protected String compute_text(NamespaceManager nsm) {
        return XmlListImpl.compute_list_text(this._value);
    }

    @Override
    protected boolean is_defaultable_ws(String v) {
        try {
            XmlSimpleList<? extends XmlAnySimpleType> savedValue = this._value;
            this.set_text(v);
            this._value = savedValue;
            return false;
        }
        catch (XmlValueOutOfRangeException e) {
            return true;
        }
    }

    @Override
    protected void set_text(String s) {
        if (this._validateOnSet() && !this._schemaType.matchPatternFacet(s)) {
            throw new XmlValueOutOfRangeException("cvc-datatype-valid.1.1", new Object[]{"list", s, QNameHelper.readable(this._schemaType)});
        }
        SchemaType itemType = this._schemaType.getListItemType();
        XmlSimpleList<? extends XmlAnySimpleType> newval = XmlListImpl.lex(s, itemType, _voorVc, this.has_store() ? this.get_store() : null);
        if (this._validateOnSet()) {
            XmlListImpl.validateValue(newval, this._schemaType, _voorVc);
        }
        this._value = newval;
        this._jvalue = null;
    }

    public static String[] split_list(String s) {
        if (s.length() == 0) {
            return EMPTY_STRINGARRAY;
        }
        ArrayList<String> result = new ArrayList<String>();
        int i = 0;
        while (true) {
            if (i < s.length() && XMLChar.isSpace(s.charAt(i))) {
                ++i;
                continue;
            }
            if (i >= s.length()) {
                return result.toArray(EMPTY_STRINGARRAY);
            }
            int start = i;
            while (i < s.length() && !XMLChar.isSpace(s.charAt(i))) {
                ++i;
            }
            result.add(s.substring(start, i));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static XmlSimpleList<? extends XmlAnySimpleType> lex(String s, SchemaType itemType, ValidationContext ctx, PrefixResolver resolver) {
        String[] parts = XmlListImpl.split_list(s);
        Function<String, XmlAnySimpleType> fun = str -> {
            try {
                return itemType.newValue(str);
            }
            catch (XmlValueOutOfRangeException e) {
                Object[] obj = new Object[]{"item '" + str + "' is not a valid value of " + QNameHelper.readable(itemType)};
                ctx.invalid("list", obj);
                return null;
            }
        };
        boolean pushed = false;
        if (resolver != null) {
            NamespaceContext.push(new NamespaceContext(resolver));
            pushed = true;
        }
        try {
            List list = Stream.of(parts).map(fun).collect(Collectors.toList());
            XmlSimpleList xmlSimpleList = new XmlSimpleList(list);
            return xmlSimpleList;
        }
        finally {
            if (pushed) {
                NamespaceContext.pop();
            }
        }
    }

    @Override
    protected void set_nil() {
        this._value = null;
    }

    public XmlSimpleList<? extends XmlAnySimpleType> xgetListValue() {
        this.check_dated();
        return this._value;
    }

    @Override
    public List<?> getListValue() {
        this.check_dated();
        if (this._value == null) {
            return null;
        }
        if (this._jvalue != null) {
            return this._jvalue;
        }
        ArrayList<Object> javaResult = new ArrayList<Object>();
        for (XmlAnySimpleType xmlAnySimpleType : this._value) {
            javaResult.add(XmlListImpl.java_value(xmlAnySimpleType));
        }
        this._jvalue = new XmlSimpleList(javaResult);
        return this._jvalue;
    }

    private static boolean permits_inner_space(XmlObject obj) {
        switch (((SimpleValue)obj).instanceType().getPrimitiveType().getBuiltinTypeCode()) {
            case 1: 
            case 2: 
            case 6: 
            case 12: {
                return true;
            }
        }
        return false;
    }

    private static boolean contains_white_space(String s) {
        return s.indexOf(32) >= 0 || s.indexOf(9) >= 0 || s.indexOf(10) >= 0 || s.indexOf(13) >= 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void set_list(List<?> list) {
        XmlSimpleList xList;
        SchemaType itemType = this._schemaType.getListItemType();
        boolean pushed = false;
        if (this.has_store()) {
            NamespaceContext.push(new NamespaceContext(this.get_store()));
            pushed = true;
        }
        Function<Object, XmlAnySimpleType> fun = entry -> {
            String stringrep;
            if (entry instanceof XmlObject && XmlListImpl.permits_inner_space((XmlObject)entry) && XmlListImpl.contains_white_space(stringrep = entry.toString())) {
                throw new XmlValueOutOfRangeException();
            }
            return itemType.newValue(entry);
        };
        try {
            xList = new XmlSimpleList(list.stream().map(fun).collect(Collectors.toList()));
        }
        finally {
            if (pushed) {
                NamespaceContext.pop();
            }
        }
        if (this._validateOnSet()) {
            XmlListImpl.validateValue(xList, this._schemaType, _voorVc);
        }
        this._value = xList;
        this._jvalue = null;
    }

    public static void validateValue(XmlSimpleList<? extends XmlAnySimpleType> items, SchemaType sType, ValidationContext context) {
        int i;
        XmlAnySimpleType o;
        block6: {
            XmlAnySimpleType[] enumvals = sType.getEnumerationValues();
            if (enumvals != null) {
                for (XmlAnySimpleType enumval : enumvals) {
                    if (!XmlListImpl.equal_xmlLists(items, ((XmlObjectBase)((Object)enumval)).xgetListValue())) {
                        continue;
                    }
                    break block6;
                }
                context.invalid("cvc-enumeration-valid", new Object[]{"list", items, QNameHelper.readable(sType)});
            }
        }
        if ((o = sType.getFacet(0)) != null && (i = ((SimpleValue)((Object)o)).getIntValue()) != items.size()) {
            context.invalid("cvc-length-valid.2", new Object[]{items, items.size(), i, QNameHelper.readable(sType)});
        }
        if ((o = sType.getFacet(1)) != null && (i = ((SimpleValue)((Object)o)).getIntValue()) > items.size()) {
            context.invalid("cvc-minLength-valid.2", new Object[]{items, items.size(), i, QNameHelper.readable(sType)});
        }
        if ((o = sType.getFacet(2)) != null && (i = ((SimpleValue)((Object)o)).getIntValue()) < items.size()) {
            context.invalid("cvc-maxLength-valid.2", new Object[]{items, items.size(), i, QNameHelper.readable(sType)});
        }
    }

    @Override
    protected boolean equal_to(XmlObject obj) {
        return XmlListImpl.equal_xmlLists(this._value, ((XmlObjectBase)obj).xgetListValue());
    }

    private static boolean equal_xmlLists(List<?> a, List<?> b) {
        if (a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); ++i) {
            if (a.get(i).equals(b.get(i))) continue;
            return false;
        }
        return true;
    }

    @Override
    protected int value_hash_code() {
        if (this._value == null) {
            return 0;
        }
        int hash = this._value.size();
        int incr = this._value.size() / 9;
        if (incr < 1) {
            incr = 1;
        }
        for (int i = 0; i < this._value.size(); i += incr) {
            hash *= 19;
            hash += this._value.get(i).hashCode();
        }
        return hash;
    }

    @Override
    protected void validate_simpleval(String lexical, ValidationContext ctx) {
        XmlListImpl.validateValue((XmlSimpleList<? extends XmlAnySimpleType>)this.xgetListValue(), this.schemaType(), ctx);
    }
}

