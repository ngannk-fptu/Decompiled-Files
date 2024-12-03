/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.embedded.api.OperationType
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.Validate
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.model.directory;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.embedded.api.OperationType;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.model.InternalEntity;
import com.atlassian.crowd.model.InternalEntityTemplate;
import com.atlassian.crowd.util.InternalEntityUtils;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Deprecated
public class DirectoryImpl
extends InternalEntity
implements Directory {
    public static final String ATTRIBUTE_KEY_USER_ENCRYPTION_METHOD = "user_encryption_method";
    public static final String ATTRIBUTE_KEY_USE_NESTED_GROUPS = "useNestedGroups";
    public static final String ATTRIBUTE_KEY_LOCAL_USER_STATUS = "localUserStatusEnabled";
    public static final String ATTRIBUTE_KEY_AUTO_ADD_GROUPS = "autoAddGroups";
    public static final String ATTRIBUTE_KEY_USE_PRIMARY_GROUP = "ldap.activedirectory.use_primary_groups";
    public static final String ATTRIBUTE_KEY_USER_ATTRIBUTES_SYNC_ENABLED = "userAttributesSyncEnabled";
    public static final String ATTRIBUTE_KEY_GROUP_ATTRIBUTES_SYNC_ENABLED = "groupAttributesSyncEnabled";
    public static final String LAST_CONFIGURATION_CHANGE = "configuration.change.timestamp";
    public static final char AUTO_ADD_GROUPS_SEPARATOR = '|';
    public static final Set<String> PASSWORD_ATTRIBUTES = ImmutableSet.of((Object)"ldap.password", (Object)"application.password", (Object)"AZURE_AD_WEBAPP_CLIENT_SECRET", (Object)"crowd.server.http.proxy.password");
    public static final String SANITISED_PASSWORD = "********";
    private String lowerName;
    private String description;
    private DirectoryType type;
    private String implementationClass;
    private String lowerImplementationClass;
    private Set<OperationType> allowedOperations = new HashSet<OperationType>();
    private Map<String, String> attributes = new HashMap<String, String>();

    public DirectoryImpl() {
    }

    public DirectoryImpl(InternalEntityTemplate template) {
        super(template);
    }

    public DirectoryImpl(String name, DirectoryType type, String implementationClass) {
        this.setName(name);
        this.setType(type);
        this.setImplementationClass(implementationClass);
        this.setActive(true);
    }

    public DirectoryImpl(Directory directory) {
        super(new InternalEntityTemplate(directory.getId(), directory.getName(), directory.isActive(), directory.getCreatedDate(), directory.getUpdatedDate()));
        this.setName(directory.getName());
        this.setType(directory.getType());
        this.setImplementationClass(directory.getImplementationClass());
        this.setActive(directory.isActive());
        this.setDescription(directory.getDescription());
        this.updateAllowedOperationsFrom(directory.getAllowedOperations());
        this.updateAttributesFrom(directory.getAttributes());
    }

    public void updateDetailsFrom(Directory directory) {
        this.setName(directory.getName());
        this.setType(directory.getType());
        this.setImplementationClass(directory.getImplementationClass());
        this.setActive(directory.isActive());
        this.setDescription(directory.getDescription());
        this.updateAllowedOperationsFrom(directory.getAllowedOperations());
        this.updateAttributesFrom(directory.getAttributes());
    }

    public void updateAttributesFrom(Map<String, String> attributes) {
        this.attributes.entrySet().retainAll(attributes.entrySet());
        this.attributes.putAll(Maps.filterValues(attributes, value -> !Strings.isNullOrEmpty((String)value)));
    }

    public String getEncryptionType() {
        String encryptionType = "com.atlassian.crowd.directory.InternalDirectory".equals(this.getImplementationClass()) ? this.getValue(ATTRIBUTE_KEY_USER_ENCRYPTION_METHOD) : this.getValue("ldap.user.encryption");
        return encryptionType;
    }

    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public Set<OperationType> getAllowedOperations() {
        return this.allowedOperations;
    }

    public void addAllowedOperation(OperationType operationType) {
        this.getAllowedOperations().add(operationType);
    }

    public void setAllowedOperations(Set<OperationType> allowedOperations) {
        this.allowedOperations = allowedOperations;
    }

    public void updateAllowedOperationsFrom(Set<OperationType> allowedOperations) {
        this.allowedOperations.retainAll(allowedOperations);
        this.allowedOperations.addAll(allowedOperations);
    }

    private void setLowerImplementationClass(String lowerImplementationClass) {
        this.lowerImplementationClass = lowerImplementationClass;
    }

    public String getLowerImplementationClass() {
        return this.lowerImplementationClass;
    }

    public String getLowerName() {
        return this.lowerName;
    }

    private void setLowerName(String lowerName) {
        this.lowerName = lowerName;
    }

    public String getDescription() {
        return this.description;
    }

    public DirectoryType getType() {
        return this.type;
    }

    public String getImplementationClass() {
        return this.implementationClass;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(DirectoryType type) {
        Validate.notNull((Object)type);
        this.type = type;
    }

    public void setImplementationClass(String implementationClass) {
        Validate.notNull((Object)implementationClass);
        this.implementationClass = implementationClass;
        this.lowerImplementationClass = implementationClass.toLowerCase(Locale.ENGLISH);
    }

    @Override
    public void setName(String name) {
        Validate.notNull((Object)name);
        InternalEntityUtils.validateLength(name);
        this.name = name;
        this.lowerName = IdentifierUtils.toLowerCase((String)name);
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<String> getValues(String name) {
        String value = this.getValue(name);
        if (value != null) {
            return Collections.singleton(value);
        }
        return null;
    }

    public String getValue(String name) {
        return this.attributes.get(name);
    }

    public Set<String> getKeys() {
        return this.attributes.keySet();
    }

    public boolean isEmpty() {
        return this.attributes.isEmpty();
    }

    public void setAttribute(String name, String value) {
        this.attributes.put(name, value);
    }

    public void removeAttribute(String name) {
        this.attributes.remove(name);
    }

    public void validate() {
        Validate.notEmpty((CharSequence)this.name, (String)"name cannot be null", (Object[])new Object[0]);
        Validate.isTrue((boolean)IdentifierUtils.toLowerCase((String)this.name).equals(this.lowerName), (String)"lowerName must be the lower-case representation of name", (Object[])new Object[0]);
        Validate.notNull((Object)this.type, (String)"type cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)this.implementationClass, (String)"implementationClass cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)this.createdDate, (String)"createdDate cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)this.updatedDate, (String)"updatedDate cannot be null", (Object[])new Object[0]);
        Validate.notNull(this.allowedOperations, (String)"allowedOperations cannot be null", (Object[])new Object[0]);
        Validate.notNull(this.attributes, (String)"attributes cannot be null", (Object[])new Object[0]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DirectoryImpl)) {
            return false;
        }
        DirectoryImpl directory = (DirectoryImpl)o;
        if (this.getImplementationClass() != null ? !this.getImplementationClass().equals(directory.getImplementationClass()) : directory.getImplementationClass() != null) {
            return false;
        }
        if (this.getLowerName() != null ? !this.getLowerName().equals(directory.getLowerName()) : directory.getLowerName() != null) {
            return false;
        }
        return this.getType() == directory.getType();
    }

    @Override
    public int hashCode() {
        int result = this.getLowerName() != null ? this.getLowerName().hashCode() : 0;
        result = 31 * result + (this.getType() != null ? this.getType().hashCode() : 0);
        result = 31 * result + (this.getImplementationClass() != null ? this.getImplementationClass().hashCode() : 0);
        return result;
    }

    public final String toString() {
        Map<String, String> attrs = this.getAttributes();
        attrs = new HashMap<String, String>(attrs);
        for (String a : PASSWORD_ATTRIBUTES) {
            if (!attrs.containsKey(a)) continue;
            attrs.put(a, SANITISED_PASSWORD);
        }
        return new ToStringBuilder((Object)this).append("lowerName", (Object)this.getLowerName()).append("description", (Object)this.getDescription()).append("type", (Object)this.getType()).append("implementationClass", (Object)this.getImplementationClass()).append("allowedOperations", this.getAllowedOperations()).append("attributes", attrs).toString();
    }
}

