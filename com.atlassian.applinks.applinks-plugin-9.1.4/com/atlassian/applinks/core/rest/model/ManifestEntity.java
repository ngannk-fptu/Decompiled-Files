/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.spi.Manifest
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.google.common.collect.Sets
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 *  org.osgi.framework.Version
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.core.rest.model;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.core.AppLinkPluginUtil;
import com.atlassian.applinks.core.rest.model.adapter.ApplicationIdAdapter;
import com.atlassian.applinks.core.rest.model.adapter.RequiredURIAdapter;
import com.atlassian.applinks.core.rest.model.adapter.TypeIdAdapter;
import com.atlassian.applinks.core.rest.model.adapter.VersionAdapter;
import com.atlassian.applinks.core.rest.util.EntityUtil;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.application.IconUriResolver;
import com.atlassian.applinks.spi.Manifest;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.sal.api.ApplicationProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Sets;
import java.net.URI;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name="manifest")
public class ManifestEntity {
    private static final Logger LOG = LoggerFactory.getLogger(ManifestEntity.class);
    @XmlJavaTypeAdapter(value=ApplicationIdAdapter.class)
    private ApplicationId id;
    private String name;
    @XmlJavaTypeAdapter(value=TypeIdAdapter.class)
    private TypeId typeId;
    private String version;
    private long buildNumber;
    @XmlJavaTypeAdapter(value=VersionAdapter.class)
    private Version applinksVersion;
    private Set<String> inboundAuthenticationTypes = Sets.newHashSet((Object[])new String[]{"placeholder.to.ensure.backwards.compatibility"});
    private Set<String> outboundAuthenticationTypes = Sets.newHashSet((Object[])new String[]{"placeholder.to.ensure.backwards.compatibility"});
    @JsonSerialize
    private Boolean publicSignup;
    @XmlJavaTypeAdapter(value=RequiredURIAdapter.class)
    private URI url;
    @XmlJavaTypeAdapter(value=RequiredURIAdapter.class)
    private URI iconUrl;
    @XmlJavaTypeAdapter(value=RequiredURIAdapter.class)
    private URI iconUri;

    private ManifestEntity() {
    }

    public ManifestEntity(InternalHostApplication internalHostApp, ApplicationProperties applicationProperties, AppLinkPluginUtil pluginUtil) {
        this.name = internalHostApp.getName();
        this.typeId = TypeId.getTypeId((ApplicationType)internalHostApp.getType());
        this.url = internalHostApp.getBaseUrl();
        this.iconUrl = internalHostApp.getType().getIconUrl();
        this.iconUri = IconUriResolver.resolveIconUri(internalHostApp.getType());
        this.inboundAuthenticationTypes.addAll(EntityUtil.getClassNames(internalHostApp.getSupportedInboundAuthenticationTypes()));
        this.outboundAuthenticationTypes.addAll(EntityUtil.getClassNames(internalHostApp.getSupportedInboundAuthenticationTypes()));
        this.id = internalHostApp.getId();
        this.applinksVersion = pluginUtil.getVersion();
        this.version = applicationProperties.getVersion();
        this.publicSignup = internalHostApp.hasPublicSignup();
        try {
            this.buildNumber = Long.parseLong(applicationProperties.getBuildNumber());
        }
        catch (NumberFormatException nfe) {
            this.buildNumber = 0L;
            LOG.warn("Cannot parse the application's build number {0}, using 0 instead.", (Object)applicationProperties.getBuildNumber());
        }
    }

    public ManifestEntity(Manifest manifest) {
        this.name = manifest.getName();
        this.typeId = manifest.getTypeId();
        this.url = manifest.getUrl();
        this.iconUrl = manifest.getIconUrl();
        this.iconUri = manifest.getIconUri();
        this.inboundAuthenticationTypes = EntityUtil.getClassNames(manifest.getInboundAuthenticationTypes());
        this.outboundAuthenticationTypes = EntityUtil.getClassNames(manifest.getOutboundAuthenticationTypes());
        this.id = manifest.getId();
        this.applinksVersion = manifest.getAppLinksVersion();
        this.version = manifest.getVersion();
        Long manifestBuildNumber = manifest.getBuildNumber();
        if (manifestBuildNumber != null) {
            this.buildNumber = manifestBuildNumber;
        } else {
            this.buildNumber = 0L;
            LOG.warn("Null value supplied for build number, using 0 instead.");
        }
        this.publicSignup = manifest.hasPublicSignup();
    }

    public ApplicationId getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public TypeId getTypeId() {
        return this.typeId;
    }

    public long getBuildNumber() {
        return this.buildNumber;
    }

    public String getVersion() {
        return this.version;
    }

    public URI getUrl() {
        return this.url;
    }

    public URI getIconUrl() {
        return this.iconUrl;
    }

    public URI getIconUri() {
        return this.iconUri;
    }

    public Version getApplinksVersion() {
        return this.applinksVersion;
    }

    public Boolean hasPublicSignup() {
        return this.publicSignup;
    }

    public Set<String> getInboundAuthenticationTypes() {
        return this.inboundAuthenticationTypes;
    }

    public Set<String> getOutboundAuthenticationTypes() {
        return this.outboundAuthenticationTypes;
    }
}

