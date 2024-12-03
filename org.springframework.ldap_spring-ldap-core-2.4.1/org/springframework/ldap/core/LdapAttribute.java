/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.naming.directory.BasicAttribute;

public class LdapAttribute
extends BasicAttribute {
    private static final long serialVersionUID = -5263905906016179429L;
    protected Set<String> options = new HashSet<String>();

    public LdapAttribute(String id) {
        super(id);
    }

    public LdapAttribute(String id, Object value) {
        super(id, value);
    }

    public LdapAttribute(String id, Object value, Collection<String> options) {
        super(id, value);
        this.options.addAll(options);
    }

    public LdapAttribute(String id, boolean ordered) {
        super(id, ordered);
    }

    public LdapAttribute(String id, Collection<String> options, boolean ordered) {
        super(id, ordered);
        this.options.addAll(options);
    }

    public LdapAttribute(String id, Object value, boolean ordered) {
        super(id, value, ordered);
    }

    public LdapAttribute(String id, Object value, Collection<String> options, boolean ordered) {
        super(id, value, ordered);
        this.options.addAll(options);
    }

    public Set<String> getOptions() {
        return this.options;
    }

    public void setOptions(Set<String> options) {
        this.options = options;
    }

    public boolean addOption(String option) {
        return this.options.add(option);
    }

    public boolean addAllOptions(Collection<String> options) {
        return this.options.addAll(options);
    }

    public void clearOptions() {
        this.options.clear();
    }

    public boolean contains(String option) {
        return this.options.contains(option);
    }

    public boolean containsAll(Collection<String> options) {
        return this.options.containsAll(options);
    }

    public boolean hasOptions() {
        return !this.options.isEmpty();
    }

    public boolean removeOption(String option) {
        return this.options.remove(option);
    }

    public boolean removeAllOptions(Collection<String> options) {
        return this.options.removeAll(options);
    }

    public boolean retainAllOptions(Collection<String> options) {
        return this.options.retainAll(options);
    }
}

