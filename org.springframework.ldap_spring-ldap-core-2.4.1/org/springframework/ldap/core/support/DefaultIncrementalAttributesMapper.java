/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.springframework.ldap.core.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.IncrementalAttributesMapper;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.core.support.RangeOption;
import org.springframework.ldap.support.LdapUtils;

public class DefaultIncrementalAttributesMapper
implements IncrementalAttributesMapper<DefaultIncrementalAttributesMapper> {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultIncrementalAttributesMapper.class);
    private Map<String, IncrementalAttributeState> stateMap = new LinkedHashMap<String, IncrementalAttributeState>();
    private Set<String> rangedAttributesInNextIteration = new LinkedHashSet<String>();
    private static final IncrementalAttributeState NOT_FOUND_ATTRIBUTE_STATE = new IncrementalAttributeState(){

        @Override
        public String getRequestedAttributeName() {
            throw new UnsupportedOperationException("This method should never be called");
        }

        @Override
        public boolean hasMore() {
            return false;
        }

        @Override
        public void calculateNextRange(RangeOption responseRange) {
        }

        @Override
        public String getAttributeNameForQuery() {
            throw new UnsupportedOperationException("This method should never be called");
        }

        @Override
        public void processValues(Attributes attributes, String attributeName) throws NamingException {
        }

        @Override
        public List<Object> getValues() {
            return null;
        }
    };

    public DefaultIncrementalAttributesMapper(String attributeName) {
        this(-1, attributeName);
    }

    public DefaultIncrementalAttributesMapper(String[] attributeNames) {
        this(-1, attributeNames);
    }

    public DefaultIncrementalAttributesMapper(int pageSize, String attributeName) {
        this(pageSize, new String[]{attributeName});
    }

    public DefaultIncrementalAttributesMapper(int pageSize, String[] attributeNames) {
        for (String attributeName : attributeNames) {
            this.stateMap.put(attributeName, new DefaultIncrementalAttributeState(attributeName, pageSize));
            this.rangedAttributesInNextIteration.add(attributeName);
        }
    }

    @Override
    public final DefaultIncrementalAttributesMapper mapFromAttributes(Attributes attributes) throws NamingException {
        if (!this.hasMore()) {
            throw new IllegalStateException("No more attributes!");
        }
        this.rangedAttributesInNextIteration = new HashSet<String>();
        NamingEnumeration<String> attributeNameEnum = attributes.getIDs();
        while (attributeNameEnum.hasMore()) {
            String attributeName = attributeNameEnum.next();
            String[] attributeNameSplit = attributeName.split(";");
            IncrementalAttributeState state = this.getState(attributeNameSplit[0]);
            if (attributeNameSplit.length == 1) {
                state.processValues(attributes, attributeName);
                continue;
            }
            for (String option : attributeNameSplit) {
                RangeOption responseRange = RangeOption.parse(option);
                if (responseRange == null) continue;
                state.processValues(attributes, attributeName);
                state.calculateNextRange(responseRange);
                if (!state.hasMore()) continue;
                this.rangedAttributesInNextIteration.add(state.getRequestedAttributeName());
            }
        }
        return this;
    }

    private IncrementalAttributeState getState(String attributeName) {
        IncrementalAttributeState mappedState = this.stateMap.get(attributeName);
        if (mappedState == null) {
            LOG.warn("Attribute '" + attributeName + "' is not handled by this instance");
            mappedState = NOT_FOUND_ATTRIBUTE_STATE;
        }
        return mappedState;
    }

    @Override
    public final List<Object> getValues(String attributeName) {
        return this.getState(attributeName).getValues();
    }

    @Override
    public final Attributes getCollectedAttributes() {
        BasicAttributes attributes = new BasicAttributes();
        Set<String> attributeNames = this.stateMap.keySet();
        for (String attributeName : attributeNames) {
            BasicAttribute oneAttribute = new BasicAttribute(attributeName);
            List<Object> values = this.getValues(attributeName);
            if (values != null) {
                for (Object oneValue : values) {
                    oneAttribute.add(oneValue);
                }
            }
            attributes.put(oneAttribute);
        }
        return attributes;
    }

    @Override
    public final boolean hasMore() {
        return this.rangedAttributesInNextIteration.size() > 0;
    }

    @Override
    public final String[] getAttributesForLookup() {
        String[] result = new String[this.rangedAttributesInNextIteration.size()];
        int index = 0;
        for (String next : this.rangedAttributesInNextIteration) {
            IncrementalAttributeState state = this.stateMap.get(next);
            result[index++] = state.getAttributeNameForQuery();
        }
        return result;
    }

    public static Attributes lookupAttributes(LdapOperations ldapOperations, String dn, String attribute) {
        return DefaultIncrementalAttributesMapper.lookupAttributes(ldapOperations, (Name)LdapUtils.newLdapName(dn), attribute);
    }

    public static Attributes lookupAttributes(LdapOperations ldapOperations, String dn, String[] attributes) {
        return DefaultIncrementalAttributesMapper.lookupAttributes(ldapOperations, (Name)LdapUtils.newLdapName(dn), attributes);
    }

    public static Attributes lookupAttributes(LdapOperations ldapOperations, Name dn, String attribute) {
        return DefaultIncrementalAttributesMapper.lookupAttributes(ldapOperations, dn, new String[]{attribute});
    }

    public static Attributes lookupAttributes(LdapOperations ldapOperations, Name dn, String[] attributes) {
        return DefaultIncrementalAttributesMapper.loopForAllAttributeValues(ldapOperations, dn, attributes).getCollectedAttributes();
    }

    public static List<Object> lookupAttributeValues(LdapOperations ldapOperations, String dn, String attribute) {
        return DefaultIncrementalAttributesMapper.lookupAttributeValues(ldapOperations, LdapUtils.newLdapName(dn), attribute);
    }

    public static List<Object> lookupAttributeValues(LdapOperations ldapOperations, Name dn, String attribute) {
        List<Object> values = DefaultIncrementalAttributesMapper.loopForAllAttributeValues(ldapOperations, dn, new String[]{attribute}).getValues(attribute);
        if (values == null) {
            values = Collections.emptyList();
        }
        return values;
    }

    private static DefaultIncrementalAttributesMapper loopForAllAttributeValues(LdapOperations ldapOperations, Name dn, String[] attributes) {
        DefaultIncrementalAttributesMapper mapper = new DefaultIncrementalAttributesMapper(attributes);
        while (mapper.hasMore()) {
            ldapOperations.lookup(dn, mapper.getAttributesForLookup(), mapper);
        }
        return mapper;
    }

    private static interface IncrementalAttributeState {
        public boolean hasMore();

        public void calculateNextRange(RangeOption var1);

        public String getAttributeNameForQuery();

        public String getRequestedAttributeName();

        public void processValues(Attributes var1, String var2) throws NamingException;

        public List<Object> getValues();
    }

    private static final class DefaultIncrementalAttributeState
    implements IncrementalAttributeState {
        private final String actualAttributeName;
        private List<Object> values = null;
        private final int pageSize;
        boolean more = true;
        private RangeOption requestRange;

        private DefaultIncrementalAttributeState(String actualAttributeName, int pageSize) {
            this.actualAttributeName = actualAttributeName;
            this.pageSize = pageSize;
            this.requestRange = new RangeOption(0, pageSize);
        }

        @Override
        public boolean hasMore() {
            return this.more;
        }

        @Override
        public String getRequestedAttributeName() {
            return this.actualAttributeName;
        }

        @Override
        public void calculateNextRange(RangeOption responseRange) {
            boolean bl = this.more = this.requestRange.compareTo(responseRange) > 0;
            if (this.more) {
                this.requestRange = responseRange.nextRange(this.pageSize);
            }
        }

        @Override
        public String getAttributeNameForQuery() {
            StringBuilder attributeBuilder = new StringBuilder(this.actualAttributeName);
            if (!this.requestRange.isFullRange()) {
                attributeBuilder.append(';');
                this.requestRange.appendTo(attributeBuilder);
            }
            return attributeBuilder.toString();
        }

        @Override
        public void processValues(Attributes attributes, String attributeName) throws NamingException {
            Attribute attribute = attributes.get(attributeName);
            NamingEnumeration<?> valueEnum = attribute.getAll();
            this.initValuesIfApplicable();
            while (valueEnum.hasMore()) {
                this.values.add(valueEnum.next());
            }
        }

        private void initValuesIfApplicable() {
            if (this.values == null) {
                this.values = new LinkedList<Object>();
            }
        }

        @Override
        public List<Object> getValues() {
            if (this.values != null) {
                return new ArrayList<Object>(this.values);
            }
            return null;
        }
    }
}

