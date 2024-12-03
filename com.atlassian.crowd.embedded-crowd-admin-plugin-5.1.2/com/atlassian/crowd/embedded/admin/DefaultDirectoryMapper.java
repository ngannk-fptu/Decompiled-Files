/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.DelegatedAuthenticationDirectory
 *  com.atlassian.crowd.directory.InternalDirectory
 *  com.atlassian.crowd.directory.RemoteCrowdDirectory
 *  com.atlassian.crowd.directory.ldap.connectionpool.SpringLdapPoolConfigService
 *  com.atlassian.crowd.directory.ldap.connectionpool.data.LdapPoolConfig
 *  com.atlassian.crowd.directory.ldap.connectionpool.data.LdapPoolConfig$Builder
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.embedded.api.OperationType
 *  com.atlassian.crowd.embedded.api.PermissionOption
 *  com.atlassian.crowd.embedded.impl.ConnectionPoolPropertyUtil
 *  com.atlassian.crowd.embedded.impl.ImmutableDirectory
 *  com.atlassian.crowd.embedded.impl.ImmutableDirectory$Builder
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.springframework.beans.BeanUtils
 */
package com.atlassian.crowd.embedded.admin;

import com.atlassian.crowd.directory.DelegatedAuthenticationDirectory;
import com.atlassian.crowd.directory.InternalDirectory;
import com.atlassian.crowd.directory.RemoteCrowdDirectory;
import com.atlassian.crowd.directory.ldap.connectionpool.SpringLdapPoolConfigService;
import com.atlassian.crowd.directory.ldap.connectionpool.data.LdapPoolConfig;
import com.atlassian.crowd.embedded.admin.DirectoryMapper;
import com.atlassian.crowd.embedded.admin.crowd.CrowdDirectoryConfiguration;
import com.atlassian.crowd.embedded.admin.crowd.CrowdPermissionOption;
import com.atlassian.crowd.embedded.admin.delegatingldap.DelegatingLdapDirectoryConfiguration;
import com.atlassian.crowd.embedded.admin.directory.CrowdDirectoryAttributes;
import com.atlassian.crowd.embedded.admin.directory.LdapDelegatingDirectoryAttributes;
import com.atlassian.crowd.embedded.admin.directory.LdapDirectoryAttributes;
import com.atlassian.crowd.embedded.admin.internal.InternalDirectoryConfiguration;
import com.atlassian.crowd.embedded.admin.jirajdbc.JiraJdbcDirectoryConfiguration;
import com.atlassian.crowd.embedded.admin.ldap.LdapDirectoryConfiguration;
import com.atlassian.crowd.embedded.admin.ldap.SharedLdapDirectoryConfiguration;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.embedded.api.OperationType;
import com.atlassian.crowd.embedded.api.PermissionOption;
import com.atlassian.crowd.embedded.impl.ConnectionPoolPropertyUtil;
import com.atlassian.crowd.embedded.impl.ImmutableDirectory;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;

public final class DefaultDirectoryMapper
implements DirectoryMapper {
    private final SpringLdapPoolConfigService ldapPoolConfigService;

    DefaultDirectoryMapper(SpringLdapPoolConfigService ldapPoolConfigService) {
        this.ldapPoolConfigService = ldapPoolConfigService;
    }

    @Override
    public Directory buildCrowdDirectory(CrowdDirectoryConfiguration configuration) {
        ImmutableDirectory.Builder builder = this.createBuilder();
        builder.setAllowedOperations(configuration.getCrowdPermissionOption().getAllowedOperations());
        builder.setActive(configuration.isActive());
        builder.setId(Long.valueOf(configuration.getDirectoryId()));
        builder.setImplementationClass(RemoteCrowdDirectory.class.getName());
        builder.setName(configuration.getName());
        builder.setType(DirectoryType.CROWD);
        CrowdDirectoryAttributes attributes = new CrowdDirectoryAttributes();
        BeanUtils.copyProperties((Object)configuration, (Object)attributes);
        attributes.setCrowdServerSynchroniseIntervalInSeconds(Long.toString(configuration.getCrowdServerSynchroniseIntervalInMin() * 60L));
        builder.setAttributes(attributes.toAttributesMap());
        return builder.toDirectory();
    }

    @Override
    public Directory buildLdapDirectory(LdapDirectoryConfiguration configuration) {
        ImmutableDirectory.Builder builder = this.createBuilder();
        builder.setActive(configuration.isActive());
        builder.setAllowedOperations(configuration.getLdapPermissionOption().getAllowedOperations());
        builder.setEncryptionType(configuration.getLdapUserEncryption());
        builder.setId(Long.valueOf(configuration.getDirectoryId()));
        builder.setImplementationClass(configuration.getType());
        builder.setName(configuration.getName());
        builder.setType(DirectoryType.CONNECTOR);
        LdapDirectoryAttributes attributes = new LdapDirectoryAttributes();
        BeanUtils.copyProperties((Object)configuration, (Object)attributes);
        BeanUtils.copyProperties((Object)configuration, (Object)attributes.getLdapCpAttributes());
        attributes.setIncrementalSyncEnabled(configuration.isCrowdSyncIncrementalEnabled());
        if (configuration.getLdapPermissionOption() == PermissionOption.READ_ONLY_LOCAL_GROUPS) {
            attributes.setLdapAutoAddGroups(DefaultDirectoryMapper.commaWhitespaceSeparatedGroupsToPipeSeparatedGroups(configuration.getLdapAutoAddGroups()));
        } else {
            attributes.setLdapAutoAddGroups("");
        }
        builder.setAttributes(this.toAttributesMap(attributes));
        return builder.toDirectory();
    }

    @Override
    public <T extends SharedLdapDirectoryConfiguration> void setDefaultSpringLdapProperties(T configuration) {
        LdapPoolConfig defaults = this.getDefaultLdapPoolConfig();
        configuration.setMaxTotal(String.valueOf(defaults.getMaxTotal()));
        configuration.setMaxTotalPerKey(String.valueOf(defaults.getMaxTotalPerKey()));
        configuration.setMaxIdlePerKey(String.valueOf(defaults.getMaxIdlePerKey()));
        configuration.setMinIdlePerKey(String.valueOf(defaults.getMinIdlePerKey()));
        configuration.setBlockWhenExhausted(defaults.isBlockWhenExhausted());
        configuration.setMaxWaitSeconds(ConnectionPoolPropertyUtil.millisToSeconds((String)String.valueOf(defaults.getMaxWaitMillis())));
        configuration.setTestOnCreate(defaults.isTestOnCreate());
        configuration.setTestOnBorrow(defaults.isTestOnBorrow());
        configuration.setTestOnReturn(defaults.isTestOnReturn());
        configuration.setTestWhileIdle(defaults.isTestWhileIdle());
        configuration.setTimeBetweenEvictionRunsSeconds(ConnectionPoolPropertyUtil.millisToSeconds((String)String.valueOf(defaults.getTimeBetweenEvictionRunsMillis())));
        configuration.setMinEvictableIdleTimeSeconds(ConnectionPoolPropertyUtil.millisToSeconds((String)String.valueOf(defaults.getMinEvictableIdleTimeMillis())));
    }

    private LdapPoolConfig getDefaultLdapPoolConfig() {
        LdapPoolConfig.Builder ldapPoolConfigBuilder = LdapPoolConfig.builder();
        this.ldapPoolConfigService.enrichByDefaultValues(ldapPoolConfigBuilder);
        return ldapPoolConfigBuilder.build();
    }

    @Override
    public Directory buildDelegatingLdapDirectory(DelegatingLdapDirectoryConfiguration configuration) {
        ImmutableDirectory.Builder builder = this.createBuilder();
        builder.setActive(configuration.isActive());
        builder.setId(Long.valueOf(configuration.getDirectoryId()));
        builder.setImplementationClass(DelegatedAuthenticationDirectory.class.getName());
        builder.setName(configuration.getName());
        builder.setType(DirectoryType.DELEGATING);
        LdapDelegatingDirectoryAttributes attributes = new LdapDelegatingDirectoryAttributes();
        BeanUtils.copyProperties((Object)configuration, (Object)attributes);
        BeanUtils.copyProperties((Object)configuration, (Object)attributes.getLdapCpAttributes());
        attributes.setDelegatedToClass(configuration.getType());
        EnumSet<OperationType> allowedOperations = EnumSet.allOf(OperationType.class);
        if (configuration.isCreateUserOnAuth()) {
            attributes.setLdapAutoAddGroups(DefaultDirectoryMapper.commaWhitespaceSeparatedGroupsToPipeSeparatedGroups(configuration.getLdapAutoAddGroups()));
        } else {
            attributes.setUpdateUserOnAuth(false);
            attributes.setLdapAutoAddGroups("");
        }
        builder.setAllowedOperations(allowedOperations);
        builder.setAttributes(this.toAttributesMap(attributes));
        return builder.toDirectory();
    }

    @Override
    public Directory buildInternalDirectory(InternalDirectoryConfiguration configuration) {
        ImmutableDirectory.Builder builder = this.createBuilder();
        builder.setAllowedOperations(EnumSet.allOf(OperationType.class));
        builder.setActive(configuration.isActive());
        builder.setId(Long.valueOf(configuration.getDirectoryId()));
        builder.setImplementationClass(InternalDirectory.class.getName());
        builder.setName(configuration.getName());
        builder.setType(DirectoryType.INTERNAL);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("user_encryption_method", "atlassian-security");
        map.put("useNestedGroups", Boolean.toString(configuration.isNestedGroupsEnabled()));
        builder.setAttributes(map);
        return builder.toDirectory();
    }

    @Override
    public Directory buildJiraJdbcDirectory(JiraJdbcDirectoryConfiguration configuration) {
        ImmutableDirectory.Builder builder = this.createBuilder();
        builder.setAllowedOperations(EnumSet.of(OperationType.UPDATE_USER));
        builder.setActive(configuration.isActive());
        builder.setId(Long.valueOf(configuration.getDirectoryId()));
        builder.setImplementationClass("com.atlassian.confluence.user.crowd.jira.JiraJdbcRemoteDirectory");
        builder.setName(configuration.getName());
        builder.setType(DirectoryType.CUSTOM);
        builder.setAttributes(Collections.singletonMap("jirajdbc.datasource.url", configuration.getDatasourceJndiName()));
        return builder.toDirectory();
    }

    private ImmutableDirectory.Builder createBuilder() {
        ImmutableDirectory.Builder builder = ImmutableDirectory.newBuilder();
        Date now = new Date();
        builder.setCreatedDate(now);
        builder.setUpdatedDate(now);
        return builder;
    }

    @Override
    public CrowdDirectoryConfiguration toCrowdConfiguration(Directory directory) {
        CrowdDirectoryConfiguration configuration = new CrowdDirectoryConfiguration();
        configuration.setCrowdPermissionOption(CrowdPermissionOption.fromAllowedOperations(directory.getAllowedOperations()));
        configuration.setDirectoryId(directory.getId() != null ? directory.getId() : 0L);
        configuration.setActive(directory.isActive());
        configuration.setName(directory.getName());
        CrowdDirectoryAttributes attributes = CrowdDirectoryAttributes.fromAttributesMap(directory.getAttributes());
        BeanUtils.copyProperties((Object)attributes, (Object)configuration);
        configuration.setCrowdServerSynchroniseIntervalInMin(NumberUtils.toLong((String)attributes.getCrowdServerSynchroniseIntervalInSeconds()) / 60L);
        return configuration;
    }

    @Override
    public LdapDirectoryConfiguration toLdapConfiguration(Directory directory) {
        this.validateDirectoryExists(directory);
        LdapDirectoryConfiguration configuration = new LdapDirectoryConfiguration();
        configuration.setLdapPermissionOption(PermissionOption.fromAllowedOperations((Set)directory.getAllowedOperations()));
        configuration.setActive(directory.isActive());
        configuration.setLdapUserEncryption(directory.getEncryptionType());
        configuration.setDirectoryId(directory.getId());
        configuration.setType(directory.getImplementationClass());
        configuration.setName(directory.getName());
        LdapDirectoryAttributes attributes = this.fromConnectorAttributesMap(directory);
        BeanUtils.copyProperties((Object)attributes, (Object)configuration);
        BeanUtils.copyProperties((Object)attributes.getLdapCpAttributes(), (Object)configuration);
        configuration.setCrowdSyncIncrementalEnabled(attributes.isIncrementalSyncEnabled());
        configuration.setLdapAutoAddGroups(DefaultDirectoryMapper.pipeSeparatedGroupsToCommaSeparatedGroups(attributes.getLdapAutoAddGroups()));
        return configuration;
    }

    @Override
    public DelegatingLdapDirectoryConfiguration toDelegatingLdapConfiguration(Directory directory) {
        this.validateDirectoryExists(directory);
        DelegatingLdapDirectoryConfiguration configuration = new DelegatingLdapDirectoryConfiguration();
        configuration.setDirectoryId(directory.getId());
        configuration.setActive(directory.isActive());
        configuration.setName(directory.getName());
        LdapDelegatingDirectoryAttributes attributes = this.fromDelegatedDirectoryAttributesMap(directory);
        BeanUtils.copyProperties((Object)attributes, (Object)configuration);
        BeanUtils.copyProperties((Object)attributes.getLdapCpAttributes(), (Object)configuration);
        configuration.setType(attributes.getDelegatedToClass());
        String ldapAutoAddGroups = attributes.getLdapAutoAddGroups();
        if (ldapAutoAddGroups != null) {
            configuration.setLdapAutoAddGroups(DefaultDirectoryMapper.pipeSeparatedGroupsToCommaSeparatedGroups(ldapAutoAddGroups));
        } else {
            configuration.setLdapAutoAddGroups("");
        }
        return configuration;
    }

    private void validateDirectoryExists(Directory directory) {
        Validate.notNull((Object)directory.getId(), (String)"Can only convert an existing Directory", (Object[])new Object[0]);
    }

    @Override
    public JiraJdbcDirectoryConfiguration toJiraJdbcConfiguration(Directory directory) {
        JiraJdbcDirectoryConfiguration configuration = new JiraJdbcDirectoryConfiguration();
        configuration.setDirectoryId(directory.getId() != null ? directory.getId() : 0L);
        configuration.setActive(directory.isActive());
        configuration.setName(directory.getName());
        configuration.setDatasourceJndiName((String)directory.getAttributes().get("jirajdbc.datasource.url"));
        return configuration;
    }

    @Override
    public InternalDirectoryConfiguration toInternalConfiguration(Directory directory) {
        InternalDirectoryConfiguration configuration = new InternalDirectoryConfiguration();
        configuration.setDirectoryId(directory.getId() != null ? directory.getId() : 0L);
        configuration.setActive(directory.isActive());
        configuration.setName(directory.getName());
        Map attributes = directory.getAttributes();
        String useNestedGroups = (String)attributes.get("useNestedGroups");
        if (useNestedGroups != null) {
            configuration.setNestedGroupsEnabled(Boolean.parseBoolean(useNestedGroups));
        } else {
            configuration.setNestedGroupsEnabled(false);
        }
        return configuration;
    }

    public static String pipeSeparatedGroupsToCommaSeparatedGroups(String pipeSeparated) {
        return StringUtils.replaceChars((String)pipeSeparated, (char)'|', (char)',');
    }

    public static String commaWhitespaceSeparatedGroupsToPipeSeparatedGroups(String commaSeparated) {
        if (commaSeparated == null) {
            return "";
        }
        String[] untrimmedGroups = StringUtils.split((String)commaSeparated, (char)',');
        LinkedHashSet<String> uniqueGroups = new LinkedHashSet<String>(untrimmedGroups.length);
        for (String untrimmedGroup : untrimmedGroups) {
            uniqueGroups.add(untrimmedGroup.trim());
        }
        return StringUtils.join(uniqueGroups, (char)'|');
    }

    private LdapDirectoryAttributes fromConnectorAttributesMap(Directory directory) {
        return LdapDirectoryAttributes.fromAttributesMap(directory.getAttributes()).apply(this.ldapPoolConfigService);
    }

    private LdapDelegatingDirectoryAttributes fromDelegatedDirectoryAttributesMap(Directory directory) {
        return LdapDelegatingDirectoryAttributes.fromAttributesMap(directory.getAttributes()).apply(this.ldapPoolConfigService);
    }

    private Map<String, String> toAttributesMap(LdapDelegatingDirectoryAttributes attributes) {
        return attributes.toAttributesMap().apply(this.ldapPoolConfigService);
    }

    private Map<String, String> toAttributesMap(LdapDirectoryAttributes attributes) {
        return attributes.toAttributesMap().apply(this.ldapPoolConfigService);
    }
}

