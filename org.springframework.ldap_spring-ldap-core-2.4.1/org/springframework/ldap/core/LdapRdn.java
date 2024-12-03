/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.ldap.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.ldap.BadLdapGrammarException;
import org.springframework.ldap.core.DefaultDnParserFactory;
import org.springframework.ldap.core.DnParser;
import org.springframework.ldap.core.LdapRdnComponent;
import org.springframework.ldap.core.ParseException;
import org.springframework.ldap.core.TokenMgrError;
import org.springframework.util.ObjectUtils;

public class LdapRdn
implements Serializable,
Comparable {
    private static final long serialVersionUID = 5681397547245228750L;
    private static final int DEFAULT_BUFFER_SIZE = 100;
    private Map<String, LdapRdnComponent> components = new LinkedHashMap<String, LdapRdnComponent>();

    public LdapRdn() {
    }

    public LdapRdn(String string) {
        LdapRdn rdn;
        DnParser parser = DefaultDnParserFactory.createDnParser(string);
        try {
            rdn = parser.rdn();
        }
        catch (ParseException e) {
            throw new BadLdapGrammarException("Failed to parse Rdn", e);
        }
        catch (TokenMgrError e) {
            throw new BadLdapGrammarException("Failed to parse Rdn", e);
        }
        this.components = rdn.components;
    }

    public LdapRdn(String key, String value) {
        this.components.put(key, new LdapRdnComponent(key, value));
    }

    public void addComponent(LdapRdnComponent rdnComponent) {
        this.components.put(rdnComponent.getKey(), rdnComponent);
    }

    public List getComponents() {
        return new ArrayList<LdapRdnComponent>(this.components.values());
    }

    public LdapRdnComponent getComponent() {
        if (this.components.size() == 0) {
            throw new IndexOutOfBoundsException("No components");
        }
        return this.components.values().iterator().next();
    }

    public LdapRdnComponent getComponent(int idx) {
        if (idx >= this.components.size()) {
            throw new IndexOutOfBoundsException();
        }
        return new ArrayList<LdapRdnComponent>(this.components.values()).get(idx);
    }

    public String getLdapEncoded() {
        if (this.components.size() == 0) {
            throw new IndexOutOfBoundsException("No components in Rdn.");
        }
        StringBuffer sb = new StringBuffer(100);
        Iterator<LdapRdnComponent> iter = this.components.values().iterator();
        while (iter.hasNext()) {
            LdapRdnComponent component = iter.next();
            sb.append(component.encodeLdap());
            if (!iter.hasNext()) continue;
            sb.append("+");
        }
        return sb.toString();
    }

    public String encodeUrl() {
        StringBuffer sb = new StringBuffer(100);
        Iterator<LdapRdnComponent> iter = this.components.values().iterator();
        while (iter.hasNext()) {
            LdapRdnComponent component = iter.next();
            sb.append(component.encodeUrl());
            if (!iter.hasNext()) continue;
            sb.append("+");
        }
        return sb.toString();
    }

    public int compareTo(Object obj) {
        LdapRdn that = (LdapRdn)obj;
        if (this.components.size() != that.components.size()) {
            return this.components.size() - that.components.size();
        }
        Set<Map.Entry<String, LdapRdnComponent>> theseEntries = this.components.entrySet();
        for (Map.Entry<String, LdapRdnComponent> oneEntry : theseEntries) {
            LdapRdnComponent thatEntry = that.components.get(oneEntry.getKey());
            if (thatEntry == null) {
                return -1;
            }
            int compared = oneEntry.getValue().compareTo(thatEntry);
            if (compared == 0) continue;
            return compared;
        }
        return 0;
    }

    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        LdapRdn that = (LdapRdn)obj;
        if (this.components.size() != that.components.size()) {
            return false;
        }
        Set<Map.Entry<String, LdapRdnComponent>> theseEntries = this.components.entrySet();
        for (Map.Entry<String, LdapRdnComponent> oneEntry : theseEntries) {
            if (oneEntry.getValue().equals(that.components.get(oneEntry.getKey()))) continue;
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.getClass().hashCode() ^ new HashSet(this.getComponents()).hashCode();
    }

    public String toString() {
        return this.getLdapEncoded();
    }

    public String getValue() {
        return this.getComponent().getValue();
    }

    public String getKey() {
        return this.getComponent().getKey();
    }

    public String getValue(String key) {
        for (LdapRdnComponent component : this.components.values()) {
            if (!ObjectUtils.nullSafeEquals((Object)component.getKey(), (Object)key)) continue;
            return component.getValue();
        }
        throw new IllegalArgumentException("No RdnComponent with the key " + key);
    }

    public LdapRdn immutableLdapRdn() {
        LinkedHashMap<String, LdapRdnComponent> mapWithImmutableRdns = new LinkedHashMap<String, LdapRdnComponent>(this.components.size());
        for (LdapRdnComponent rdnComponent : this.components.values()) {
            mapWithImmutableRdns.put(rdnComponent.getKey(), rdnComponent.immutableLdapRdnComponent());
        }
        Map unmodifiableMapOfImmutableRdns = Collections.unmodifiableMap(mapWithImmutableRdns);
        LdapRdn immutableRdn = new LdapRdn();
        immutableRdn.components = unmodifiableMapOfImmutableRdns;
        return immutableRdn;
    }
}

