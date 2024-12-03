/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableList
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory.ldap.util;

import com.atlassian.crowd.directory.ApacheDS;
import com.atlassian.crowd.directory.ApacheDS15;
import com.atlassian.crowd.directory.AppleOpenDirectory;
import com.atlassian.crowd.directory.FedoraDS;
import com.atlassian.crowd.directory.GenericLDAP;
import com.atlassian.crowd.directory.LDAPDirectory;
import com.atlassian.crowd.directory.MicrosoftActiveDirectory;
import com.atlassian.crowd.directory.NovelleDirectory;
import com.atlassian.crowd.directory.OpenDS;
import com.atlassian.crowd.directory.OpenLDAP;
import com.atlassian.crowd.directory.OpenLDAPRfc2307;
import com.atlassian.crowd.directory.Rfc2307;
import com.atlassian.crowd.directory.SunONE;
import com.atlassian.crowd.directory.ldap.LdapTypeConfig;
import com.atlassian.crowd.directory.ldap.util.LDAPPropertiesHelper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LDAPPropertiesHelperImpl
implements LDAPPropertiesHelper {
    private static final Logger logger = LoggerFactory.getLogger(LDAPPropertiesHelperImpl.class);
    private Map<String, String> implementations;
    private Map<String, Properties> configurationDetails;
    private List<LdapTypeConfig> ldapTypeConfigs = new ArrayList<LdapTypeConfig>();
    public static final Collection<? extends Class<? extends LDAPDirectory>> DIRECTORIES_WITH_CONFIGURABLE_USER_ENCRYPTION = ImmutableList.of(OpenLDAP.class, OpenLDAPRfc2307.class, GenericLDAP.class, Rfc2307.class, ApacheDS.class, ApacheDS15.class);

    public LDAPPropertiesHelperImpl() {
        this.init();
    }

    private void init() {
        this.implementations = new LinkedHashMap<String, String>();
        this.implementations.put(MicrosoftActiveDirectory.getStaticDirectoryType(), MicrosoftActiveDirectory.class.getName());
        this.implementations.put(ApacheDS.getStaticDirectoryType(), ApacheDS.class.getName());
        this.implementations.put(ApacheDS15.getStaticDirectoryType(), ApacheDS15.class.getName());
        this.implementations.put(AppleOpenDirectory.getStaticDirectoryType(), AppleOpenDirectory.class.getName());
        this.implementations.put(FedoraDS.getStaticDirectoryType(), FedoraDS.class.getName());
        this.implementations.put(GenericLDAP.getStaticDirectoryType(), GenericLDAP.class.getName());
        this.implementations.put(NovelleDirectory.getStaticDirectoryType(), NovelleDirectory.class.getName());
        this.implementations.put(OpenDS.getStaticDirectoryType(), OpenDS.class.getName());
        this.implementations.put(OpenLDAP.getStaticDirectoryType(), OpenLDAP.class.getName());
        this.implementations.put(OpenLDAPRfc2307.getStaticDirectoryType(), OpenLDAPRfc2307.class.getName());
        this.implementations.put(Rfc2307.getStaticDirectoryType(), Rfc2307.class.getName());
        this.implementations.put(SunONE.getStaticDirectoryType(), SunONE.class.getName());
        logger.debug("Added the following LDAP implementations: " + this.implementations.toString());
        this.configurationDetails = new HashMap<String, Properties>();
        this.configurationDetails.put(GenericLDAP.class.getName(), this.loadDirectoryProperties(GenericLDAP.class));
        this.configurationDetails.put(OpenLDAP.class.getName(), this.loadDirectoryProperties(OpenLDAP.class));
        this.configurationDetails.put(MicrosoftActiveDirectory.class.getName(), this.loadDirectoryProperties(MicrosoftActiveDirectory.class));
        this.configurationDetails.put(SunONE.class.getName(), this.loadDirectoryProperties(SunONE.class));
        this.configurationDetails.put(ApacheDS.class.getName(), this.loadDirectoryProperties(ApacheDS.class));
        this.configurationDetails.put(ApacheDS15.class.getName(), this.loadDirectoryProperties(ApacheDS15.class));
        this.configurationDetails.put(NovelleDirectory.class.getName(), this.loadDirectoryProperties(NovelleDirectory.class));
        this.configurationDetails.put(Rfc2307.class.getName(), this.loadDirectoryProperties(Rfc2307.class));
        this.configurationDetails.put(AppleOpenDirectory.class.getName(), this.loadDirectoryProperties(AppleOpenDirectory.class));
        this.configurationDetails.put(OpenDS.class.getName(), this.loadDirectoryProperties(OpenDS.class));
        this.configurationDetails.put(FedoraDS.class.getName(), this.loadDirectoryProperties(FedoraDS.class));
        this.configurationDetails.put(OpenLDAPRfc2307.class.getName(), this.loadDirectoryProperties(OpenLDAPRfc2307.class));
        logger.debug("Added the following LDAP configuration details: " + this.configurationDetails.toString());
        this.initHiddenFields();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static boolean isUserEncryptionConfigurable(String implementationClass) {
        if (Strings.isNullOrEmpty((String)implementationClass)) return false;
        if (!DIRECTORIES_WITH_CONFIGURABLE_USER_ENCRYPTION.stream().map(Class::getCanonicalName).anyMatch(implementationClass::equals)) return false;
        return true;
    }

    private void initHiddenFields() {
        HashMap<String, List<String>> classesHidingField = new HashMap<String, List<String>>();
        ArrayList<String> classesWithHiddenEncryption = new ArrayList<String>(this.implementations.values());
        for (Class<? extends LDAPDirectory> clazz : DIRECTORIES_WITH_CONFIGURABLE_USER_ENCRYPTION) {
            classesWithHiddenEncryption.remove(clazz.getName());
        }
        classesHidingField.put("ldap.user.encryption", classesWithHiddenEncryption);
        classesHidingField.put("ldap.nestedgroups.disabled", Arrays.asList(OpenLDAPRfc2307.class.getName(), Rfc2307.class.getName(), AppleOpenDirectory.class.getName(), FedoraDS.class.getName()));
        ArrayList<String> classesExcludingActiveDirectory = new ArrayList<String>(this.implementations.values());
        classesExcludingActiveDirectory.remove(MicrosoftActiveDirectory.class.getName());
        classesHidingField.put("localUserStatusEnabled", classesExcludingActiveDirectory);
        classesHidingField.put("ldap.filter.expiredUsers", classesExcludingActiveDirectory);
        classesHidingField.put("ldap.usermembership.use.for.groups", classesExcludingActiveDirectory);
        classesHidingField.put("crowd.sync.incremental.enabled", classesExcludingActiveDirectory);
        Set set = classesHidingField.keySet();
        for (Map.Entry<String, String> entry : this.implementations.entrySet()) {
            String className = entry.getValue();
            String displayName = entry.getKey();
            LdapTypeConfig config = new LdapTypeConfig(className, displayName, this.configurationDetails.get(className));
            for (String field : set) {
                if (!((List)classesHidingField.get(field)).contains(className)) continue;
                config.setHiddenField(field);
            }
            this.ldapTypeConfigs.add(config);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Properties loadDirectoryProperties(Class<?> clazz) {
        Properties props = new Properties();
        String fqcn = clazz.getName();
        String key = fqcn.substring(fqcn.lastIndexOf(46) + 1) + ".properties";
        InputStream stream = clazz.getResourceAsStream("/com/atlassian/crowd/integration/directory/" + key.toLowerCase(Locale.ENGLISH));
        if (stream == null) {
            logger.warn("Unable to load properties with key: " + key);
        } else {
            try {
                props.load(stream);
                if (logger.isDebugEnabled()) {
                    logger.debug("The following properties for key: " + key + "were loaded: " + props);
                }
            }
            catch (IOException e) {
                logger.error("Failed to load property with key: " + key, (Throwable)e);
            }
            finally {
                try {
                    stream.close();
                }
                catch (IOException e) {
                    logger.warn(e.getMessage(), (Throwable)e);
                }
            }
        }
        return props;
    }

    @Override
    public Map<String, String> getImplementations() {
        return this.implementations;
    }

    @Override
    public Map<String, Properties> getConfigurationDetails() {
        return this.configurationDetails;
    }

    @Override
    public List<LdapTypeConfig> getLdapTypeConfigs() {
        return this.ldapTypeConfigs;
    }
}

