/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.ldap.core;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.InvalidNameException;
import org.springframework.ldap.NoSuchAttributeException;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.NameAwareAttribute;
import org.springframework.ldap.core.NameAwareAttributes;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class DirContextAdapter
implements DirContextOperations {
    private static final boolean DONT_ADD_IF_DUPLICATE_EXISTS = false;
    private static final String EMPTY_STRING = "";
    private static final boolean ORDER_DOESNT_MATTER = false;
    private static final String NOT_IMPLEMENTED = "Not implemented.";
    private static Logger log = LoggerFactory.getLogger(DirContextAdapter.class);
    private final NameAwareAttributes originalAttrs;
    private LdapName dn;
    private LdapName base = LdapUtils.emptyLdapName();
    private boolean updateMode = false;
    private NameAwareAttributes updatedAttrs;
    private String referralUrl;

    public DirContextAdapter() {
        this(null, null, null);
    }

    public DirContextAdapter(String dnString) {
        this(LdapUtils.newLdapName(dnString));
    }

    public DirContextAdapter(Name dn) {
        this(null, dn);
    }

    public DirContextAdapter(Attributes attrs, Name dn) {
        this(attrs, dn, null);
    }

    public DirContextAdapter(Attributes attrs, Name dn, Name base) {
        this(attrs, dn, base, null);
    }

    public DirContextAdapter(Attributes attrs, Name dn, Name base, String referralUrl) {
        this.originalAttrs = attrs != null ? new NameAwareAttributes(attrs) : new NameAwareAttributes();
        this.dn = dn != null ? LdapUtils.newLdapName(dn) : LdapUtils.emptyLdapName();
        this.base = base != null ? LdapUtils.newLdapName(base) : LdapUtils.emptyLdapName();
        this.referralUrl = referralUrl != null ? referralUrl : EMPTY_STRING;
    }

    protected DirContextAdapter(DirContextAdapter main) {
        this.originalAttrs = (NameAwareAttributes)main.originalAttrs.clone();
        this.dn = main.dn;
        this.updatedAttrs = (NameAwareAttributes)main.updatedAttrs.clone();
        this.updateMode = main.updateMode;
    }

    public void setUpdateMode(boolean mode) {
        this.updateMode = mode;
        if (this.updateMode) {
            this.updatedAttrs = new NameAwareAttributes();
        }
    }

    @Override
    public boolean isUpdateMode() {
        return this.updateMode;
    }

    @Override
    public String[] getNamesOfModifiedAttributes() {
        ArrayList<String> tmpList = new ArrayList<String>();
        NamingEnumeration<NameAwareAttribute> attributesEnumeration = this.isUpdateMode() ? this.updatedAttrs.getAll() : this.originalAttrs.getAll();
        try {
            while (attributesEnumeration.hasMore()) {
                Attribute oneAttribute = attributesEnumeration.next();
                tmpList.add(oneAttribute.getID());
            }
        }
        catch (NamingException e) {
            throw LdapUtils.convertLdapException(e);
        }
        finally {
            this.closeNamingEnumeration(attributesEnumeration);
        }
        return tmpList.toArray(new String[tmpList.size()]);
    }

    private void closeNamingEnumeration(NamingEnumeration<?> enumeration) {
        try {
            if (enumeration != null) {
                enumeration.close();
            }
        }
        catch (NamingException namingException) {
            // empty catch block
        }
    }

    @Override
    public ModificationItem[] getModificationItems() {
        if (!this.updateMode) {
            return new ModificationItem[0];
        }
        LinkedList<ModificationItem> tmpList = new LinkedList<ModificationItem>();
        NamingEnumeration<NameAwareAttribute> attributesEnumeration = null;
        try {
            attributesEnumeration = this.updatedAttrs.getAll();
            while (attributesEnumeration.hasMore()) {
                NameAwareAttribute oneAttr = attributesEnumeration.next();
                this.collectModifications(oneAttr, tmpList);
            }
        }
        catch (NamingException e) {
            throw LdapUtils.convertLdapException(e);
        }
        finally {
            this.closeNamingEnumeration(attributesEnumeration);
        }
        if (log.isDebugEnabled()) {
            log.debug("Number of modifications:" + tmpList.size());
        }
        return tmpList.toArray(new ModificationItem[tmpList.size()]);
    }

    private void collectModifications(NameAwareAttribute changedAttr, List<ModificationItem> modificationList) throws NamingException {
        NameAwareAttribute currentAttribute = this.originalAttrs.get(changedAttr.getID());
        if (currentAttribute != null && changedAttr.hasValuesAsNames()) {
            try {
                currentAttribute.initValuesAsNames();
            }
            catch (IllegalArgumentException e) {
                log.warn("Incompatible attributes; changed attribute has Name values but original cannot be converted to this");
            }
        }
        if (changedAttr.equals(currentAttribute)) {
            return;
        }
        if (currentAttribute != null && currentAttribute.size() == 1 && changedAttr.size() == 1) {
            modificationList.add(new ModificationItem(2, changedAttr));
        } else if (changedAttr.size() == 0 && currentAttribute != null) {
            modificationList.add(new ModificationItem(3, changedAttr));
        } else if ((currentAttribute == null || currentAttribute.size() == 0) && changedAttr.size() > 0) {
            modificationList.add(new ModificationItem(1, changedAttr));
        } else if (changedAttr.size() > 0 && changedAttr.isOrdered()) {
            modificationList.add(new ModificationItem(2, changedAttr));
        } else if (changedAttr.size() > 0) {
            LinkedList<ModificationItem> myModifications = new LinkedList<ModificationItem>();
            this.collectModifications(currentAttribute, changedAttr, myModifications);
            if (myModifications.isEmpty()) {
                myModifications.add(new ModificationItem(2, changedAttr));
            }
            modificationList.addAll(myModifications);
        }
    }

    private void collectModifications(Attribute originalAttr, Attribute changedAttr, List<ModificationItem> modificationList) throws NamingException {
        Attribute originalClone = (Attribute)originalAttr.clone();
        NameAwareAttribute addedValuesAttribute = new NameAwareAttribute(originalAttr.getID());
        NamingEnumeration<?> allValues = changedAttr.getAll();
        while (allValues.hasMoreElements()) {
            Object attributeValue = allValues.nextElement();
            if (originalClone.remove(attributeValue)) continue;
            addedValuesAttribute.add(attributeValue);
        }
        if (originalClone.size() > 0 && originalClone.size() == originalAttr.size()) {
            modificationList.add(new ModificationItem(2, addedValuesAttribute));
        } else {
            if (originalClone.size() > 0) {
                modificationList.add(new ModificationItem(3, originalClone));
            }
            if (addedValuesAttribute.size() > 0) {
                modificationList.add(new ModificationItem(1, addedValuesAttribute));
            }
        }
    }

    private boolean isEmptyAttribute(Attribute a) {
        try {
            return a == null || a.size() == 0 || a.get() == null;
        }
        catch (NamingException e) {
            return true;
        }
    }

    private boolean isChanged(String name, Object[] values, boolean orderMatters) {
        boolean emptyNewValue;
        NameAwareAttribute orig = this.originalAttrs.get(name);
        NameAwareAttribute prev = this.updatedAttrs.get(name);
        boolean bl = emptyNewValue = values == null || values.length == 0;
        if (emptyNewValue) {
            return orig != null;
        }
        if (orig == null) {
            return true;
        }
        if (orig.size() != values.length) {
            return true;
        }
        if (prev != null && prev.size() != values.length) {
            return true;
        }
        if (this.isAttributeUpdated(values, orderMatters, orig)) {
            return true;
        }
        return prev != null && this.isAttributeUpdated(values, orderMatters, prev);
    }

    private boolean isAttributeUpdated(Object[] values, boolean orderMatters, NameAwareAttribute orig) {
        int i = 0;
        for (Object obj : orig) {
            if (!(obj instanceof String)) {
                return true;
            }
            if (orderMatters ? !values[i].equals(obj) : !ObjectUtils.containsElement((Object[])values, (Object)obj)) {
                return true;
            }
            ++i;
        }
        return false;
    }

    protected final boolean exists(Attribute attr) {
        return this.exists(attr.getID());
    }

    protected final boolean exists(String attrId) {
        return this.originalAttrs.get(attrId) != null;
    }

    @Override
    public String getStringAttribute(String name) {
        return (String)this.getObjectAttribute(name);
    }

    @Override
    public Object getObjectAttribute(String name) {
        NameAwareAttribute oneAttr = this.originalAttrs.get(name);
        if (oneAttr == null || oneAttr.size() == 0) {
            return null;
        }
        try {
            return oneAttr.get();
        }
        catch (NamingException e) {
            throw LdapUtils.convertLdapException(e);
        }
    }

    @Override
    public boolean attributeExists(String name) {
        NameAwareAttribute oneAttr = this.originalAttrs.get(name);
        return oneAttr != null;
    }

    @Override
    public void setAttributeValue(String name, Object value) {
        if (!this.updateMode && value != null) {
            this.originalAttrs.put(name, value);
        }
        if (this.updateMode) {
            NameAwareAttribute attribute = new NameAwareAttribute(name);
            if (value != null) {
                attribute.add(value);
            }
            this.updatedAttrs.put(attribute);
        }
    }

    @Override
    public void addAttributeValue(String name, Object value) {
        this.addAttributeValue(name, value, false);
    }

    @Override
    public void addAttributeValue(String name, Object value, boolean addIfDuplicateExists) {
        if (!this.updateMode && value != null) {
            NameAwareAttribute attr = this.originalAttrs.get(name);
            if (attr == null) {
                this.originalAttrs.put(name, value);
            } else {
                attr.add(value);
            }
        } else if (this.updateMode) {
            Attribute attr = this.updatedAttrs.get(name);
            if (attr == null) {
                if (this.originalAttrs.get(name) == null) {
                    this.updatedAttrs.put(name, value);
                } else {
                    attr = (Attribute)this.originalAttrs.get(name).clone();
                    if (addIfDuplicateExists || !attr.contains(value)) {
                        attr.add(value);
                    }
                    this.updatedAttrs.put(attr);
                }
            } else {
                attr.add(value);
            }
        }
    }

    @Override
    public void removeAttributeValue(String name, Object value) {
        if (!this.updateMode && value != null) {
            NameAwareAttribute attr = this.originalAttrs.get(name);
            if (attr != null) {
                attr.remove(value);
                if (attr.size() == 0) {
                    this.originalAttrs.remove(name);
                }
            }
        } else if (this.updateMode) {
            Attribute attr = this.updatedAttrs.get(name);
            if (attr == null) {
                if (this.originalAttrs.get(name) != null) {
                    attr = (Attribute)this.originalAttrs.get(name).clone();
                    attr.remove(value);
                    this.updatedAttrs.put(attr);
                }
            } else {
                attr.remove(value);
            }
        }
    }

    @Override
    public void setAttributeValues(String name, Object[] values) {
        this.setAttributeValues(name, values, false);
    }

    @Override
    public void setAttributeValues(String name, Object[] values, boolean orderMatters) {
        NameAwareAttribute a = new NameAwareAttribute(name, orderMatters);
        for (int i = 0; values != null && i < values.length; ++i) {
            a.add(values[i]);
        }
        if (!this.updateMode && values != null && values.length > 0) {
            this.originalAttrs.put(a);
        }
        if (this.updateMode && this.isChanged(name, values, orderMatters)) {
            this.updatedAttrs.put(a);
        }
    }

    @Override
    public void update() {
        NamingEnumeration<NameAwareAttribute> attributesEnumeration = null;
        try {
            attributesEnumeration = this.updatedAttrs.getAll();
            while (attributesEnumeration.hasMore()) {
                Attribute a = attributesEnumeration.next();
                if (this.isEmptyAttribute(a)) {
                    this.originalAttrs.remove(a.getID());
                    continue;
                }
                this.originalAttrs.put(a);
            }
        }
        catch (NamingException e) {
            throw LdapUtils.convertLdapException(e);
        }
        finally {
            this.closeNamingEnumeration(attributesEnumeration);
        }
        this.updatedAttrs = new NameAwareAttributes();
    }

    @Override
    public String[] getStringAttributes(String name) {
        try {
            List<String> objects = this.collectAttributeValuesAsList(name, String.class);
            return objects.toArray(new String[objects.size()]);
        }
        catch (NoSuchAttributeException e) {
            return null;
        }
    }

    @Override
    public Object[] getObjectAttributes(String name) {
        try {
            List<Object> list = this.collectAttributeValuesAsList(name, Object.class);
            return list.toArray(new Object[list.size()]);
        }
        catch (NoSuchAttributeException e) {
            return null;
        }
    }

    private <T> List<T> collectAttributeValuesAsList(String name, Class<T> clazz) {
        LinkedList list = new LinkedList();
        LdapUtils.collectAttributeValues(this.originalAttrs, name, list, clazz);
        return list;
    }

    @Override
    public SortedSet<String> getAttributeSortedStringSet(String name) {
        try {
            TreeSet<String> attrSet = new TreeSet<String>();
            LdapUtils.collectAttributeValues(this.originalAttrs, name, attrSet, String.class);
            return attrSet;
        }
        catch (NoSuchAttributeException e) {
            return null;
        }
    }

    public void setAttribute(Attribute attribute) {
        if (!this.updateMode) {
            this.originalAttrs.put(attribute);
        } else {
            this.updatedAttrs.put(attribute);
        }
    }

    @Override
    public Attributes getAttributes() {
        return this.originalAttrs;
    }

    @Override
    public Attributes getAttributes(Name name) throws NamingException {
        return this.getAttributes(name.toString());
    }

    @Override
    public Attributes getAttributes(String name) throws NamingException {
        if (StringUtils.hasLength((String)name)) {
            throw new NameNotFoundException();
        }
        return (Attributes)this.originalAttrs.clone();
    }

    @Override
    public Attributes getAttributes(Name name, String[] attrIds) throws NamingException {
        return this.getAttributes(name.toString(), attrIds);
    }

    @Override
    public Attributes getAttributes(String name, String[] attrIds) throws NamingException {
        if (StringUtils.hasLength((String)name)) {
            throw new NameNotFoundException();
        }
        NameAwareAttributes a = new NameAwareAttributes();
        for (String attrId : attrIds) {
            NameAwareAttribute target = this.originalAttrs.get(attrId);
            if (target == null) continue;
            a.put(target);
        }
        return a;
    }

    @Override
    public void modifyAttributes(Name name, int modOp, Attributes attrs) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public void modifyAttributes(String name, int modOp, Attributes attrs) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public void modifyAttributes(Name name, ModificationItem[] mods) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public void modifyAttributes(String name, ModificationItem[] mods) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public void bind(Name name, Object obj, Attributes attrs) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public void bind(String name, Object obj, Attributes attrs) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public void rebind(Name name, Object obj, Attributes attrs) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public void rebind(String name, Object obj, Attributes attrs) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public DirContext createSubcontext(Name name, Attributes attrs) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public DirContext createSubcontext(String name, Attributes attrs) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public DirContext getSchema(Name name) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public DirContext getSchema(String name) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public DirContext getSchemaClassDefinition(Name name) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public DirContext getSchemaClassDefinition(String name) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public NamingEnumeration<SearchResult> search(Name name, Attributes matchingAttributes, String[] attributesToReturn) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public NamingEnumeration<SearchResult> search(String name, Attributes matchingAttributes, String[] attributesToReturn) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public NamingEnumeration<SearchResult> search(Name name, Attributes matchingAttributes) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public NamingEnumeration<SearchResult> search(String name, Attributes matchingAttributes) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public NamingEnumeration<SearchResult> search(Name name, String filter, SearchControls cons) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public NamingEnumeration<SearchResult> search(String name, String filter, SearchControls cons) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public NamingEnumeration<SearchResult> search(Name name, String filterExpr, Object[] filterArgs, SearchControls cons) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public NamingEnumeration<SearchResult> search(String name, String filterExpr, Object[] filterArgs, SearchControls cons) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public Object lookup(Name name) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public Object lookup(String name) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public void bind(Name name, Object obj) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public void bind(String name, Object obj) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public void rebind(Name name, Object obj) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public void rebind(String name, Object obj) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public void unbind(Name name) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public void unbind(String name) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public void rename(Name oldName, Name newName) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public void rename(String oldName, String newName) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public void destroySubcontext(Name name) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public void destroySubcontext(String name) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public Context createSubcontext(Name name) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public Context createSubcontext(String name) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public Object lookupLink(Name name) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public Object lookupLink(String name) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public NameParser getNameParser(Name name) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public NameParser getNameParser(String name) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public Name composeName(Name name, Name prefix) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public String composeName(String name, String prefix) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public Object addToEnvironment(String propName, Object propVal) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public Object removeFromEnvironment(String propName) throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public Hashtable<?, ?> getEnvironment() throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public void close() throws NamingException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public String getNameInNamespace() {
        if (this.base.size() == 0) {
            return this.dn.toString();
        }
        try {
            LdapName result = (LdapName)this.dn.clone();
            result.addAll(0, this.base);
            return result.toString();
        }
        catch (javax.naming.InvalidNameException e) {
            throw new InvalidNameException(e);
        }
    }

    @Override
    public Name getDn() {
        return LdapUtils.newLdapName(this.dn);
    }

    @Override
    public final void setDn(Name dn) {
        if (this.updateMode) {
            throw new IllegalStateException("Not possible to call setDn() on a DirContextAdapter in update mode");
        }
        this.dn = LdapUtils.newLdapName(dn);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DirContextAdapter that = (DirContextAdapter)o;
        if (this.updateMode != that.updateMode) {
            return false;
        }
        if (this.base != null ? !this.base.equals(that.base) : that.base != null) {
            return false;
        }
        if (this.dn != null ? !this.dn.equals(that.dn) : that.dn != null) {
            return false;
        }
        if (this.originalAttrs != null ? !this.originalAttrs.equals(that.originalAttrs) : that.originalAttrs != null) {
            return false;
        }
        if (this.referralUrl != null ? !this.referralUrl.equals(that.referralUrl) : that.referralUrl != null) {
            return false;
        }
        return !(this.updatedAttrs != null ? !this.updatedAttrs.equals(that.updatedAttrs) : that.updatedAttrs != null);
    }

    public int hashCode() {
        int result = this.originalAttrs != null ? this.originalAttrs.hashCode() : 0;
        result = 31 * result + (this.dn != null ? this.dn.hashCode() : 0);
        result = 31 * result + (this.base != null ? this.base.hashCode() : 0);
        result = 31 * result + (this.updateMode ? 1 : 0);
        result = 31 * result + (this.updatedAttrs != null ? this.updatedAttrs.hashCode() : 0);
        result = 31 * result + (this.referralUrl != null ? this.referralUrl.hashCode() : 0);
        return result;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getName());
        builder.append(":");
        if (this.dn != null) {
            builder.append(" dn=").append(this.dn);
        }
        builder.append(" {");
        try {
            NamingEnumeration<NameAwareAttribute> i = this.originalAttrs.getAll();
            while (i.hasMore()) {
                Attribute attribute = i.next();
                if (attribute.size() == 1) {
                    builder.append(attribute.getID());
                    builder.append('=');
                    builder.append(attribute.get());
                } else {
                    int j = 0;
                    for (Object value : (Iterable)((Object)attribute)) {
                        this.appendAttributeValue(builder, attribute.getID(), value, j);
                        ++j;
                    }
                }
                if (!i.hasMore()) continue;
                builder.append(", ");
            }
        }
        catch (NamingException e) {
            log.warn("Error in toString()");
        }
        builder.append('}');
        return builder.toString();
    }

    private void appendAttributeValue(StringBuilder builder, String attributeID, Object value, int index) throws NamingException {
        if (index > 0) {
            builder.append(", ");
        }
        builder.append(attributeID);
        builder.append('[');
        builder.append(index);
        builder.append("]=");
        builder.append(value);
    }

    @Override
    public String getReferralUrl() {
        return this.referralUrl;
    }

    @Override
    public boolean isReferral() {
        return StringUtils.hasLength((String)this.referralUrl);
    }
}

