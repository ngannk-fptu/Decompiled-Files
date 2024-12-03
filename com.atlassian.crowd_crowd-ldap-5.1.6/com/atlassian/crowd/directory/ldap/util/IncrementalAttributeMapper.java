/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.ldap.core.AttributesMapper
 */
package com.atlassian.crowd.directory.ldap.util;

import com.atlassian.crowd.directory.ldap.util.AttributeValueProcessor;
import com.atlassian.crowd.directory.ldap.util.RangeOption;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import org.springframework.ldap.core.AttributesMapper;

public class IncrementalAttributeMapper
implements AttributesMapper {
    private final String attributeName;
    private boolean more = true;
    private RangeOption requestRange;
    private final AttributeValueProcessor valueProcessor;

    public IncrementalAttributeMapper(String attributeName, AttributeValueProcessor valueProcessor) {
        this(attributeName, valueProcessor, new RangeOption(0, -1));
    }

    public IncrementalAttributeMapper(String attributeName, AttributeValueProcessor valueProcessor, RangeOption requestRange) {
        this.attributeName = attributeName;
        this.valueProcessor = valueProcessor;
        this.requestRange = requestRange;
    }

    public Object mapFromAttributes(Attributes attributes) throws NamingException {
        if (!this.more) {
            throw new IllegalStateException("No more attributes!");
        }
        this.more = false;
        NamingEnumeration<String> attributeNameEnum = attributes.getIDs();
        while (attributeNameEnum.hasMore()) {
            String attributeName = attributeNameEnum.next();
            if (attributeName.equals(this.attributeName)) {
                this.processValues(attributes, this.attributeName);
                continue;
            }
            if (!attributeName.startsWith(this.attributeName + ";")) continue;
            for (String option : attributeName.split(";")) {
                RangeOption responseRange = RangeOption.parse(option);
                if (responseRange == null) continue;
                boolean bl = this.more = this.requestRange.compareTo(responseRange) > 0;
                if (this.more) {
                    this.requestRange = responseRange.nextRange(-1);
                }
                this.processValues(attributes, attributeName);
            }
        }
        return this;
    }

    private void processValues(Attributes attributes, String attributeName) throws NamingException {
        Attribute attribute = attributes.get(attributeName);
        NamingEnumeration<?> valueEnum = attribute.getAll();
        while (valueEnum.hasMore()) {
            this.valueProcessor.process(valueEnum.next());
        }
    }

    public boolean hasMore() {
        return this.more;
    }

    public String[] getAttributesArray() {
        StringBuilder attributeBuilder = new StringBuilder(this.attributeName);
        if (!this.requestRange.isFullRange()) {
            attributeBuilder.append(';');
            this.requestRange.toString(attributeBuilder);
        }
        return new String[]{attributeBuilder.toString()};
    }
}

