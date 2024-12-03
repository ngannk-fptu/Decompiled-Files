/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.security.core.authority.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.Attributes2GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.MappableAttributesRetriever;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class MapBasedAttributes2GrantedAuthoritiesMapper
implements Attributes2GrantedAuthoritiesMapper,
MappableAttributesRetriever,
InitializingBean {
    private Map<String, Collection<GrantedAuthority>> attributes2grantedAuthoritiesMap = null;
    private String stringSeparator = ",";
    private Set<String> mappableAttributes = null;

    public void afterPropertiesSet() {
        Assert.notNull(this.attributes2grantedAuthoritiesMap, (String)"attributes2grantedAuthoritiesMap must be set");
    }

    public List<GrantedAuthority> getGrantedAuthorities(Collection<String> attributes) {
        ArrayList<GrantedAuthority> result = new ArrayList<GrantedAuthority>();
        for (String attribute : attributes) {
            Collection<GrantedAuthority> granted = this.attributes2grantedAuthoritiesMap.get(attribute);
            if (granted == null) continue;
            result.addAll(granted);
        }
        result.trimToSize();
        return result;
    }

    public Map<String, Collection<GrantedAuthority>> getAttributes2grantedAuthoritiesMap() {
        return this.attributes2grantedAuthoritiesMap;
    }

    public void setAttributes2grantedAuthoritiesMap(Map<?, ?> attributes2grantedAuthoritiesMap) {
        Assert.notEmpty(attributes2grantedAuthoritiesMap, (String)"A non-empty attributes2grantedAuthoritiesMap must be supplied");
        this.attributes2grantedAuthoritiesMap = this.preProcessMap(attributes2grantedAuthoritiesMap);
        this.mappableAttributes = Collections.unmodifiableSet(this.attributes2grantedAuthoritiesMap.keySet());
    }

    private Map<String, Collection<GrantedAuthority>> preProcessMap(Map<?, ?> orgMap) {
        HashMap<String, Collection<GrantedAuthority>> result = new HashMap<String, Collection<GrantedAuthority>>(orgMap.size());
        for (Map.Entry<?, ?> entry : orgMap.entrySet()) {
            Assert.isInstanceOf(String.class, entry.getKey(), (String)"attributes2grantedAuthoritiesMap contains non-String objects as keys");
            result.put((String)entry.getKey(), this.getGrantedAuthorityCollection(entry.getValue()));
        }
        return result;
    }

    private Collection<GrantedAuthority> getGrantedAuthorityCollection(Object value) {
        ArrayList<GrantedAuthority> result = new ArrayList<GrantedAuthority>();
        this.addGrantedAuthorityCollection(result, value);
        return result;
    }

    private void addGrantedAuthorityCollection(Collection<GrantedAuthority> result, Object value) {
        if (value == null) {
            return;
        }
        if (value instanceof Collection) {
            this.addGrantedAuthorityCollection(result, (Collection)value);
        } else if (value instanceof Object[]) {
            this.addGrantedAuthorityCollection(result, (Object[])value);
        } else if (value instanceof String) {
            this.addGrantedAuthorityCollection(result, (String)value);
        } else if (value instanceof GrantedAuthority) {
            result.add((GrantedAuthority)value);
        } else {
            throw new IllegalArgumentException("Invalid object type: " + value.getClass().getName());
        }
    }

    private void addGrantedAuthorityCollection(Collection<GrantedAuthority> result, Collection<?> value) {
        for (Object elt : value) {
            this.addGrantedAuthorityCollection(result, elt);
        }
    }

    private void addGrantedAuthorityCollection(Collection<GrantedAuthority> result, Object[] value) {
        for (Object aValue : value) {
            this.addGrantedAuthorityCollection(result, aValue);
        }
    }

    private void addGrantedAuthorityCollection(Collection<GrantedAuthority> result, String value) {
        StringTokenizer tokenizer = new StringTokenizer(value, this.stringSeparator, false);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (!StringUtils.hasText((String)token)) continue;
            result.add(new SimpleGrantedAuthority(token));
        }
    }

    @Override
    public Set<String> getMappableAttributes() {
        return this.mappableAttributes;
    }

    public String getStringSeparator() {
        return this.stringSeparator;
    }

    public void setStringSeparator(String stringSeparator) {
        this.stringSeparator = stringSeparator;
    }
}

