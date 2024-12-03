/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.ApacheDS
 *  com.atlassian.crowd.directory.ApacheDS15
 *  com.atlassian.crowd.directory.AppleOpenDirectory
 *  com.atlassian.crowd.directory.DelegatedAuthenticationDirectory
 *  com.atlassian.crowd.directory.FedoraDS
 *  com.atlassian.crowd.directory.GenericLDAP
 *  com.atlassian.crowd.directory.InternalDirectory
 *  com.atlassian.crowd.directory.MicrosoftActiveDirectory
 *  com.atlassian.crowd.directory.NovelleDirectory
 *  com.atlassian.crowd.directory.OpenDS
 *  com.atlassian.crowd.directory.OpenLDAP
 *  com.atlassian.crowd.directory.OpenLDAPRfc2307
 *  com.atlassian.crowd.directory.RemoteCrowdDirectory
 *  com.atlassian.crowd.directory.Rfc2307
 *  com.atlassian.crowd.directory.SunONE
 */
package com.atlassian.confluence.user.crowd;

import com.atlassian.crowd.directory.ApacheDS;
import com.atlassian.crowd.directory.ApacheDS15;
import com.atlassian.crowd.directory.AppleOpenDirectory;
import com.atlassian.crowd.directory.DelegatedAuthenticationDirectory;
import com.atlassian.crowd.directory.FedoraDS;
import com.atlassian.crowd.directory.GenericLDAP;
import com.atlassian.crowd.directory.InternalDirectory;
import com.atlassian.crowd.directory.MicrosoftActiveDirectory;
import com.atlassian.crowd.directory.NovelleDirectory;
import com.atlassian.crowd.directory.OpenDS;
import com.atlassian.crowd.directory.OpenLDAP;
import com.atlassian.crowd.directory.OpenLDAPRfc2307;
import com.atlassian.crowd.directory.RemoteCrowdDirectory;
import com.atlassian.crowd.directory.Rfc2307;
import com.atlassian.crowd.directory.SunONE;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CrowdUserDirectoryImplementation {
    APACHE_DS_10(ApacheDS.class.getName()),
    APACHE_DS_15(ApacheDS15.class.getName()),
    APPLE_OPEN_DIRECTORY(AppleOpenDirectory.class.getName()),
    FEDORA_DS(FedoraDS.class.getName()),
    GENERIC_LDAP(GenericLDAP.class.getName()),
    GENERIC_LDAP_RFC2307(Rfc2307.class.getName()),
    MICROSOFT_ACTIVE_DIRECTORY(MicrosoftActiveDirectory.class.getName()),
    NOVELL_EDIRECTORY(NovelleDirectory.class.getName()),
    OPEN_DS(OpenDS.class.getName()),
    OPEN_LDAP(OpenLDAP.class.getName()),
    OPEN_LDAP_RFC2307(OpenLDAPRfc2307.class.getName()),
    SUN_ONE(SunONE.class.getName()),
    REMOTE_CROWD(RemoteCrowdDirectory.class.getName()),
    DELEGATED_LDAP(DelegatedAuthenticationDirectory.class.getName()),
    INTERNAL(InternalDirectory.class.getName()),
    UNKNOWN("");

    private final String implementationClass;
    private static final Map<String, CrowdUserDirectoryImplementation> implementationClassLookup;

    private CrowdUserDirectoryImplementation(String implementationClass) {
        this.implementationClass = implementationClass;
    }

    public String getImplementationClass() {
        return this.implementationClass;
    }

    public static CrowdUserDirectoryImplementation getByImplementationClass(String implementationClass) {
        return implementationClass != null ? implementationClassLookup.getOrDefault(implementationClass, UNKNOWN) : UNKNOWN;
    }

    static {
        implementationClassLookup = Arrays.stream(CrowdUserDirectoryImplementation.values()).collect(Collectors.toMap(CrowdUserDirectoryImplementation::getImplementationClass, Function.identity()));
    }
}

