/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.ldap.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.DirContext;
import javax.naming.ldap.LdapName;
import org.springframework.ldap.InvalidNameException;
import org.springframework.ldap.core.IterableNamingEnumeration;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

public final class NameAwareAttribute
implements Attribute,
Iterable<Object> {
    private final String id;
    private final boolean orderMatters;
    private final Set<Object> values = new LinkedHashSet<Object>();
    private Map<Name, String> valuesAsNames = new HashMap<Name, String>();

    public NameAwareAttribute(String id, Object value) {
        this(id);
        this.values.add(value);
    }

    public NameAwareAttribute(Attribute attribute) {
        this(attribute.getID(), attribute.isOrdered());
        try {
            NamingEnumeration<?> incomingValues = attribute.getAll();
            while (incomingValues.hasMore()) {
                this.add(incomingValues.next());
            }
        }
        catch (NamingException e) {
            throw LdapUtils.convertLdapException(e);
        }
        if (attribute instanceof NameAwareAttribute) {
            NameAwareAttribute nameAwareAttribute = (NameAwareAttribute)attribute;
            this.populateValuesAsNames(nameAwareAttribute, this);
        }
    }

    public NameAwareAttribute(String id) {
        this(id, false);
    }

    public NameAwareAttribute(String id, boolean orderMatters) {
        this.id = id;
        this.orderMatters = orderMatters;
    }

    @Override
    public NamingEnumeration<?> getAll() {
        return new IterableNamingEnumeration<Object>(this.values);
    }

    @Override
    public Object get() {
        if (this.values.isEmpty()) {
            return null;
        }
        return this.values.iterator().next();
    }

    @Override
    public int size() {
        return this.values.size();
    }

    @Override
    public String getID() {
        return this.id;
    }

    @Override
    public boolean contains(Object attrVal) {
        return this.values.contains(attrVal);
    }

    @Override
    public boolean add(Object attrVal) {
        if (attrVal instanceof Name) {
            this.initValuesAsNames();
            LdapName name = LdapUtils.newLdapName((Name)attrVal);
            String currentValue = this.valuesAsNames.get(name);
            String nameAsString = ((Object)name).toString();
            if (currentValue == null) {
                this.valuesAsNames.put(name, ((Object)name).toString());
                this.values.add(nameAsString);
                return true;
            }
            if (!currentValue.equals(nameAsString)) {
                this.values.remove(currentValue);
                this.values.add(nameAsString);
            }
            return false;
        }
        return this.values.add(attrVal);
    }

    public void initValuesAsNames() {
        if (this.hasValuesAsNames()) {
            return;
        }
        HashMap<Name, String> newValuesAsNames = new HashMap<Name, String>();
        for (Object value : this.values) {
            if (value instanceof String) {
                String s = (String)value;
                try {
                    newValuesAsNames.put(LdapUtils.newLdapName(s), s);
                    continue;
                }
                catch (InvalidNameException e) {
                    throw new IllegalArgumentException("This instance has values that are not valid distinguished names; cannot handle Name values", (Throwable)((Object)e));
                }
            }
            if (value instanceof LdapName) {
                newValuesAsNames.put((LdapName)value, value.toString());
                continue;
            }
            throw new IllegalArgumentException("This instance has non-string attribute values; cannot handle Name values");
        }
        this.valuesAsNames = newValuesAsNames;
    }

    public boolean hasValuesAsNames() {
        return !this.valuesAsNames.isEmpty();
    }

    @Override
    public boolean remove(Object attrval) {
        if (attrval instanceof Name) {
            this.initValuesAsNames();
            LdapName name = LdapUtils.newLdapName((Name)attrval);
            String removedValue = this.valuesAsNames.remove(name);
            if (removedValue != null) {
                this.values.remove(removedValue);
                return true;
            }
            return false;
        }
        return this.values.remove(attrval);
    }

    @Override
    public void clear() {
        this.values.clear();
    }

    @Override
    public DirContext getAttributeSyntaxDefinition() throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override
    public DirContext getAttributeDefinition() throws NamingException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isOrdered() {
        return this.orderMatters;
    }

    @Override
    public Object get(int ix) throws NamingException {
        Iterator<Object> iterator = this.values.iterator();
        try {
            Object value = iterator.next();
            for (int i = 0; i < ix; ++i) {
                value = iterator.next();
            }
            return value;
        }
        catch (NoSuchElementException e) {
            throw new IndexOutOfBoundsException("No value at index i");
        }
    }

    @Override
    public Object remove(int ix) {
        Iterator<Object> iterator = this.values.iterator();
        try {
            Object value = iterator.next();
            for (int i = 0; i < ix; ++i) {
                value = iterator.next();
            }
            iterator.remove();
            if (value instanceof String) {
                try {
                    this.valuesAsNames.remove(new LdapName((String)value));
                }
                catch (javax.naming.InvalidNameException invalidNameException) {
                    // empty catch block
                }
            }
            return value;
        }
        catch (NoSuchElementException e) {
            throw new IndexOutOfBoundsException("No value at index i");
        }
    }

    @Override
    public void add(int ix, Object attrVal) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object set(int ix, Object attrVal) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object clone() {
        return new NameAwareAttribute(this);
    }

    private void populateValuesAsNames(NameAwareAttribute from, NameAwareAttribute to) {
        Set<Map.Entry<Name, String>> entries = from.valuesAsNames.entrySet();
        for (Map.Entry<Name, String> entry : entries) {
            to.valuesAsNames.put(LdapUtils.newLdapName(entry.getKey()), entry.getValue());
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        NameAwareAttribute that = (NameAwareAttribute)o;
        if (this.id != null ? !this.id.equals(that.id) : that.id != null) {
            return false;
        }
        if (this.values.size() != that.values.size()) {
            return false;
        }
        if (this.orderMatters != that.orderMatters || this.size() != that.size()) {
            return false;
        }
        if (this.hasValuesAsNames() != that.hasValuesAsNames()) {
            return false;
        }
        Set<Object> myValues = this.values;
        Set<Object> theirValues = that.values;
        if (this.hasValuesAsNames()) {
            myValues = this.valuesAsNames.keySet();
            theirValues = that.valuesAsNames.keySet();
        }
        if (this.orderMatters) {
            Iterator<Object> thisIterator = myValues.iterator();
            Iterator<Object> thatIterator = theirValues.iterator();
            while (thisIterator.hasNext()) {
                if (ObjectUtils.nullSafeEquals((Object)thisIterator.next(), (Object)thatIterator.next())) continue;
                return false;
            }
            return true;
        }
        for (Object value : myValues) {
            if (CollectionUtils.contains(theirValues.iterator(), (Object)value)) continue;
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = this.id != null ? this.id.hashCode() : 0;
        int valuesHash = 7;
        Set<Object> myValues = this.values;
        if (this.hasValuesAsNames()) {
            myValues = this.valuesAsNames.keySet();
        }
        for (Object value : myValues) {
            result += ObjectUtils.nullSafeHashCode((Object)value);
        }
        result = 31 * result + valuesHash;
        return result;
    }

    public String toString() {
        return String.format("NameAwareAttribute; id: %s; hasValuesAsNames: %s; orderMatters: %s; values: %s", this.id, this.hasValuesAsNames(), this.orderMatters, this.values);
    }

    @Override
    public Iterator<Object> iterator() {
        return this.values.iterator();
    }
}

