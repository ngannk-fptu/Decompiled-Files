/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.ldap.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import javax.naming.CompositeName;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.BadLdapGrammarException;
import org.springframework.ldap.UncategorizedLdapException;
import org.springframework.ldap.core.DefaultDnParserFactory;
import org.springframework.ldap.core.DnParser;
import org.springframework.ldap.core.LdapRdn;
import org.springframework.ldap.core.ParseException;
import org.springframework.ldap.core.TokenMgrError;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.ldap.support.ListComparator;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class DistinguishedName
implements Name {
    public static final String SPACED_DN_FORMAT_PROPERTY = "org.springframework.ldap.core.spacedDnFormat";
    public static final String KEY_CASE_FOLD_PROPERTY = "org.springframework.ldap.core.keyCaseFold";
    public static final String KEY_CASE_FOLD_LOWER = "lower";
    public static final String KEY_CASE_FOLD_UPPER = "upper";
    public static final String KEY_CASE_FOLD_NONE = "none";
    private static final String MANGLED_DOUBLE_QUOTES = "\\\\\"";
    private static final String PROPER_DOUBLE_QUOTES = "\\\"";
    private static final Logger LOG = LoggerFactory.getLogger(DistinguishedName.class);
    private static final boolean COMPACT = true;
    private static final boolean NON_COMPACT = false;
    private static final long serialVersionUID = 3514344371999042586L;
    public static final DistinguishedName EMPTY_PATH = new DistinguishedName(Collections.EMPTY_LIST);
    private static final int DEFAULT_BUFFER_SIZE = 256;
    private List names;

    public DistinguishedName() {
        this.names = new LinkedList();
    }

    public DistinguishedName(String path) {
        if (!StringUtils.hasText((String)path)) {
            this.names = new LinkedList();
        } else {
            this.parse(path);
        }
    }

    public DistinguishedName(List list) {
        this.names = list;
    }

    public DistinguishedName(Name name) {
        Assert.notNull((Object)name, (String)"name cannot be null");
        if (name instanceof CompositeName) {
            this.parse(LdapUtils.convertCompositeNameToString((CompositeName)name));
            return;
        }
        this.names = new LinkedList();
        for (int i = 0; i < name.size(); ++i) {
            this.names.add(new LdapRdn(name.get(i)));
        }
    }

    protected final void parse(String path) {
        DistinguishedName dn;
        DnParser parser = DefaultDnParserFactory.createDnParser(this.unmangleCompositeName(path));
        try {
            dn = parser.dn();
        }
        catch (ParseException e) {
            throw new BadLdapGrammarException("Failed to parse DN", e);
        }
        catch (TokenMgrError e) {
            throw new BadLdapGrammarException("Failed to parse DN", e);
        }
        this.names = dn.names;
    }

    private String unmangleCompositeName(String path) {
        String tempPath = path.startsWith("\"") && path.endsWith("\"") ? path.substring(1, path.length() - 1) : path;
        tempPath = StringUtils.replace((String)tempPath, (String)MANGLED_DOUBLE_QUOTES, (String)PROPER_DOUBLE_QUOTES);
        return tempPath;
    }

    public LdapRdn getLdapRdn(int index) {
        return (LdapRdn)this.names.get(index);
    }

    public LdapRdn getLdapRdn(String key) {
        for (LdapRdn rdn : this.names) {
            if (!ObjectUtils.nullSafeEquals((Object)rdn.getKey(), (Object)key)) continue;
            return rdn;
        }
        throw new IllegalArgumentException("No Rdn with the requested key: '" + key + "'");
    }

    public String getValue(String key) {
        return this.getLdapRdn(key).getValue();
    }

    public List getNames() {
        return this.names;
    }

    public String toString() {
        String spacedFormatting = System.getProperty(SPACED_DN_FORMAT_PROPERTY);
        if (!StringUtils.hasText((String)spacedFormatting)) {
            return this.format(true);
        }
        return this.format(false);
    }

    public String toCompactString() {
        return this.format(true);
    }

    public String encode() {
        return this.format(false);
    }

    private String format(boolean compact) {
        if (this.names.size() == 0) {
            return "";
        }
        StringBuffer buffer = new StringBuffer(256);
        ListIterator i = this.names.listIterator(this.names.size());
        while (i.hasPrevious()) {
            LdapRdn rdn = (LdapRdn)i.previous();
            buffer.append(rdn.getLdapEncoded());
            if (!i.hasPrevious()) continue;
            if (compact) {
                buffer.append(",");
                continue;
            }
            buffer.append(", ");
        }
        return buffer.toString();
    }

    public String toUrl() {
        StringBuffer buffer = new StringBuffer(256);
        for (int i = this.names.size() - 1; i >= 0; --i) {
            LdapRdn n = (LdapRdn)this.names.get(i);
            buffer.append(n.encodeUrl());
            if (i <= 0) continue;
            buffer.append(",");
        }
        return buffer.toString();
    }

    public boolean contains(DistinguishedName path) {
        List shortlist = path.getNames();
        if (this.getNames().size() < shortlist.size()) {
            return false;
        }
        if (shortlist.size() == 0) {
            return false;
        }
        Iterator longiter = this.getNames().iterator();
        Iterator shortiter = shortlist.iterator();
        LdapRdn longname = (LdapRdn)longiter.next();
        LdapRdn shortname = (LdapRdn)shortiter.next();
        while (!longname.equals(shortname) && longiter.hasNext()) {
            longname = (LdapRdn)longiter.next();
        }
        if (!shortiter.hasNext() && longname.equals(shortname)) {
            return true;
        }
        if (!longiter.hasNext()) {
            return false;
        }
        while (longname.equals(shortname) && longiter.hasNext() && shortiter.hasNext()) {
            longname = (LdapRdn)longiter.next();
            shortname = (LdapRdn)shortiter.next();
        }
        return !shortiter.hasNext() && longname.equals(shortname);
    }

    public DistinguishedName append(DistinguishedName path) {
        this.getNames().addAll(path.getNames());
        return this;
    }

    public DistinguishedName append(String key, String value) {
        this.add(key, value);
        return this;
    }

    public void prepend(DistinguishedName path) {
        ListIterator i = path.getNames().listIterator(path.getNames().size());
        while (i.hasPrevious()) {
            this.names.add(0, i.previous());
        }
    }

    public LdapRdn removeFirst() {
        return (LdapRdn)this.names.remove(0);
    }

    public void removeFirst(Name path) {
        if (path != null && this.startsWith(path)) {
            for (int i = 0; i < path.size(); ++i) {
                this.removeFirst();
            }
        }
    }

    @Override
    public Object clone() {
        try {
            DistinguishedName result = (DistinguishedName)super.clone();
            result.names = new LinkedList(this.names);
            return result;
        }
        catch (CloneNotSupportedException e) {
            LOG.error("CloneNotSupported thrown from superclass - this should not happen");
            throw new UncategorizedLdapException("Fatal error in clone", e);
        }
    }

    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        DistinguishedName name = (DistinguishedName)obj;
        return this.getNames().equals(name.getNames());
    }

    public int hashCode() {
        return this.getClass().hashCode() ^ this.getNames().hashCode();
    }

    @Override
    public int compareTo(Object obj) {
        DistinguishedName that = (DistinguishedName)obj;
        ListComparator comparator = new ListComparator();
        return comparator.compare(this.names, that.names);
    }

    @Override
    public int size() {
        return this.names.size();
    }

    @Override
    public boolean isEmpty() {
        return this.names.size() == 0;
    }

    public Enumeration getAll() {
        LinkedList<String> strings = new LinkedList<String>();
        for (LdapRdn rdn : this.names) {
            strings.add(rdn.getLdapEncoded());
        }
        return Collections.enumeration(strings);
    }

    @Override
    public String get(int index) {
        LdapRdn rdn = (LdapRdn)this.names.get(index);
        return rdn.getLdapEncoded();
    }

    @Override
    public Name getPrefix(int index) {
        LinkedList newNames = new LinkedList();
        for (int i = 0; i < index; ++i) {
            newNames.add(this.names.get(i));
        }
        return new DistinguishedName(newNames);
    }

    @Override
    public Name getSuffix(int index) {
        if (index > this.names.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        LinkedList newNames = new LinkedList();
        for (int i = index; i < this.names.size(); ++i) {
            newNames.add(this.names.get(i));
        }
        return new DistinguishedName(newNames);
    }

    @Override
    public boolean startsWith(Name name) {
        if (name.size() == 0) {
            return false;
        }
        DistinguishedName start = null;
        if (!(name instanceof DistinguishedName)) {
            return false;
        }
        start = (DistinguishedName)name;
        if (start.size() > this.size()) {
            return false;
        }
        Iterator longiter = this.names.iterator();
        Iterator shortiter = start.getNames().iterator();
        while (shortiter.hasNext()) {
            Object shortname;
            Object longname = longiter.next();
            if (longname.equals(shortname = shortiter.next())) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean endsWith(Name name) {
        DistinguishedName path = null;
        if (!(name instanceof DistinguishedName)) {
            return false;
        }
        path = (DistinguishedName)name;
        List shortlist = path.getNames();
        if (this.getNames().size() < shortlist.size()) {
            return false;
        }
        if (shortlist.size() == 0) {
            return false;
        }
        ListIterator longiter = this.getNames().listIterator(this.getNames().size());
        ListIterator shortiter = shortlist.listIterator(shortlist.size());
        while (shortiter.hasPrevious()) {
            LdapRdn shortname;
            LdapRdn longname = (LdapRdn)longiter.previous();
            if (longname.equals(shortname = (LdapRdn)shortiter.previous())) continue;
            return false;
        }
        return true;
    }

    @Override
    public Name addAll(Name name) throws InvalidNameException {
        return this.addAll(this.names.size(), name);
    }

    @Override
    public Name addAll(int arg0, Name name) throws InvalidNameException {
        DistinguishedName distinguishedName = null;
        try {
            distinguishedName = (DistinguishedName)name;
        }
        catch (ClassCastException e) {
            throw new InvalidNameException("Invalid name type");
        }
        this.names.addAll(arg0, distinguishedName.getNames());
        return this;
    }

    @Override
    public Name add(String string) throws InvalidNameException {
        return this.add(this.names.size(), string);
    }

    @Override
    public Name add(int index, String string) throws InvalidNameException {
        try {
            this.names.add(index, new LdapRdn(string));
        }
        catch (BadLdapGrammarException e) {
            throw new InvalidNameException("Failed to parse rdn '" + string + "'");
        }
        return this;
    }

    @Override
    public Object remove(int arg0) throws InvalidNameException {
        LdapRdn rdn = (LdapRdn)this.names.remove(arg0);
        return rdn.getLdapEncoded();
    }

    public LdapRdn removeLast() {
        return (LdapRdn)this.names.remove(this.names.size() - 1);
    }

    public void add(String key, String value) {
        this.names.add(new LdapRdn(key, value));
    }

    public void add(LdapRdn rdn) {
        this.names.add(rdn);
    }

    public void add(int idx, LdapRdn rdn) {
        this.names.add(idx, rdn);
    }

    public DistinguishedName immutableDistinguishedName() {
        ArrayList<LdapRdn> listWithImmutableRdns = new ArrayList<LdapRdn>(this.names.size());
        for (LdapRdn rdn : this.names) {
            listWithImmutableRdns.add(rdn.immutableLdapRdn());
        }
        return new DistinguishedName(Collections.unmodifiableList(listWithImmutableRdns));
    }

    public static final DistinguishedName immutableDistinguishedName(String dnString) {
        return new DistinguishedName(dnString).immutableDistinguishedName();
    }
}

