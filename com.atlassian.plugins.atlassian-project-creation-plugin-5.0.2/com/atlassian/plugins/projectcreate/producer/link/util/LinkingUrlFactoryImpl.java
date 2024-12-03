/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.plugins.projectcreate.linking.spi.LocalRoot
 *  com.atlassian.plugins.projectcreate.linking.spi.RemoteRoot
 *  org.apache.commons.codec.digest.DigestUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.plugins.projectcreate.producer.link.util;

import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.plugins.projectcreate.linking.spi.LocalRoot;
import com.atlassian.plugins.projectcreate.linking.spi.RemoteRoot;
import com.atlassian.plugins.projectcreate.producer.link.util.InternalHostApplicationAccessor;
import com.atlassian.plugins.projectcreate.producer.link.util.LinkingUrlFactory;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LinkingUrlFactoryImpl
implements LinkingUrlFactory {
    private final InternalHostApplication internalHostApplication;

    @Autowired
    public LinkingUrlFactoryImpl(InternalHostApplicationAccessor internalHostApplicationAccessor) {
        this.internalHostApplication = internalHostApplicationAccessor.get();
    }

    @Override
    public String getLinkUrl(LocalRoot localRoot) {
        return this.getLinkUrl(this.internalHostApplication.getBaseUrl().toString(), localRoot);
    }

    @Override
    public String getLinkDetailsUrl(LocalRoot localRoot, RemoteRoot remoteRoot) {
        return this.getLinkUrl(localRoot) + "/" + this.getInstanceIdHash(remoteRoot.getRemoteUrl()) + "/" + remoteRoot.getRootType() + "/" + remoteRoot.getRootKey();
    }

    @Override
    public String getRootUrl(LocalRoot localRoot) {
        return this.getRootUrl(this.internalHostApplication.getBaseUrl().toString(), localRoot.getRootType(), localRoot.getRootKey());
    }

    private String getRootUrl(String displayUrl, String rootType, String key) {
        return StringUtils.stripEnd((String)displayUrl, (String)"/") + "/rest/capabilities/aggregate-root/" + rootType + "/" + key;
    }

    private String getLinkUrl(String displayUrl, LocalRoot localRoot) {
        return StringUtils.stripEnd((String)displayUrl, (String)"/") + "/rest/capabilities/aggregate-root-link/" + localRoot.getRootType() + "/" + localRoot.getRootKey();
    }

    public String getCapabilityUrl(String displayUrl) {
        return StringUtils.stripEnd((String)displayUrl, (String)"/") + "/rest/capabilities";
    }

    @Override
    public String getInstanceIdHash(URI displayUri) {
        return DigestUtils.shaHex((String)this.getCapabilityUrl(displayUri.toString()));
    }

    @Override
    public LocalRoot getLocalRootForUrl(String url) {
        String[] urlComponents = StringUtils.stripEnd((String)url, (String)"/").split("/");
        return new LocalRoot(urlComponents[urlComponents.length - 2], urlComponents[urlComponents.length - 1]);
    }

    @Override
    public RemoteRoot getRemoteRootForUrl(String url) throws URISyntaxException {
        String[] urlComponents = StringUtils.stripEnd((String)url, (String)"/").split("/");
        String baseUrl = url.substring(0, url.lastIndexOf("/rest/capabilities/aggregate-root"));
        return new RemoteRoot(new URI(baseUrl), urlComponents[urlComponents.length - 2], urlComponents[urlComponents.length - 1]);
    }

    @Override
    public String getRootUrlForRemote(RemoteRoot remoteRoot) {
        return StringUtils.stripEnd((String)remoteRoot.getRemoteUrl().toString(), (String)"/") + "/rest/capabilities/aggregate-root/" + remoteRoot.getRootType() + "/" + remoteRoot.getRootKey();
    }
}

