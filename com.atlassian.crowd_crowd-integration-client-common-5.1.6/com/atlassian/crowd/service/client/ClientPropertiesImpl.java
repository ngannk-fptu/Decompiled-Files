/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.service.client;

import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.model.authentication.ApplicationAuthenticationContext;
import com.atlassian.crowd.service.client.AbstractClientProperties;
import com.atlassian.crowd.service.client.AuthenticationMethod;
import com.atlassian.crowd.service.client.ResourceLocator;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Properties;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientPropertiesImpl
extends AbstractClientProperties {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected ClientPropertiesImpl() {
    }

    @Override
    public void updateProperties(Properties properties) {
        this.applicationName = this.loadAndLogPropertyString(properties, "application.name");
        this.applicationPassword = this.loadPropertyString(properties, "application.password");
        this.applicationAuthenticationURL = this.loadAndLogPropertyString(properties, "application.login.url");
        this.cookieTokenKey = this.loadPropertyString(properties, "cookie.tokenkey");
        this.sessionTokenKey = this.loadAndLogPropertyString(properties, "session.tokenkey");
        this.sessionLastValidation = this.loadAndLogPropertyString(properties, "session.lastvalidation");
        this.sessionValidationInterval = this.loadPropertyLong(properties, "session.validationinterval", true);
        this.httpProxyHost = this.loadPropertyString(properties, "http.proxy.host");
        this.httpProxyPort = this.loadPropertyString(properties, "http.proxy.port");
        this.httpProxyUsername = this.loadPropertyString(properties, "http.proxy.username");
        this.httpProxyPassword = this.loadPropertyString(properties, "http.proxy.password");
        this.httpMaxConnections = this.loadPropertyString(properties, "http.max.connections");
        this.httpTimeout = this.loadPropertyString(properties, "http.timeout");
        this.socketTimeout = this.loadPropertyString(properties, "socket.timeout");
        this.ssoCookieDomainName = this.loadAndLogPropertyString(properties, "cookie.domain");
        this.authenticationMethod = AuthenticationMethod.parse((String)StringUtils.defaultIfBlank((CharSequence)this.loadAndLogPropertyString(properties, "authentication.method"), (CharSequence)AuthenticationMethod.BASIC_AUTH.getKey()));
        PasswordCredential credentials = new PasswordCredential(this.applicationPassword);
        this.applicationAuthenticationContext = new ApplicationAuthenticationContext();
        this.applicationAuthenticationContext.setName(this.applicationName);
        this.applicationAuthenticationContext.setCredential(credentials);
        this.baseURL = this.loadBaseURL(properties);
    }

    private long loadPropertyLong(Properties properties, String propertyName, boolean logProperty) {
        String propertyValueAsString = logProperty ? this.loadAndLogPropertyString(properties, propertyName) : this.loadPropertyString(properties, propertyName);
        long propertyValue = 0L;
        if (propertyValueAsString != null) {
            propertyValue = Long.parseLong(propertyValueAsString);
        }
        return propertyValue;
    }

    @Nullable
    public String loadPropertyString(Properties properties, String propertyName) {
        String propertyValue = StringUtils.stripToNull((String)System.getProperty("crowd.property." + propertyName));
        if (propertyValue == null) {
            propertyValue = StringUtils.stripToNull((String)ClientPropertiesImpl.loadPropertyFromEnv(propertyName));
        }
        if (propertyValue == null && properties != null && properties.containsKey(propertyName)) {
            propertyValue = StringUtils.stripToNull((String)properties.getProperty(propertyName));
        }
        return propertyValue;
    }

    @Nullable
    private static String loadPropertyFromEnv(String propertyName) {
        if (Boolean.getBoolean("atlassian.use.environment.variables")) {
            String envPropertyName = "CROWD_PROPERTY_" + propertyName.toUpperCase(Locale.ENGLISH).replace(".", "_");
            return System.getenv(envPropertyName);
        }
        return null;
    }

    @Nullable
    protected String loadAndLogPropertyString(Properties properties, String propertyName) {
        String propertyValue = this.loadPropertyString(properties, propertyName);
        if (propertyValue != null) {
            this.logger.debug("Loading property: '" + propertyName + "' : '" + propertyValue + "'");
        } else {
            this.logger.debug("Failed to find value for property: " + propertyName);
        }
        return propertyValue;
    }

    @Nullable
    private String loadBaseURL(Properties properties) {
        String baseURL = this.loadPropertyString(properties, "crowd.base.url");
        if (StringUtils.isBlank((CharSequence)baseURL)) {
            baseURL = this.generateBaseURL(properties);
        }
        return StringUtils.removeEnd((String)baseURL, (String)"/");
    }

    @Nullable
    private String generateBaseURL(Properties properties) {
        String propertyUrl = this.loadPropertyString(properties, "crowd.server.url");
        if (propertyUrl == null) {
            return null;
        }
        try {
            URI uri = new URI(propertyUrl);
            URI truncatedUri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), ClientPropertiesImpl.truncatePath(uri.getPath()), uri.getQuery(), uri.getFragment());
            return truncatedUri.toString();
        }
        catch (URISyntaxException e) {
            return propertyUrl;
        }
    }

    private static String truncatePath(String originalPath) {
        if (originalPath == null) {
            return null;
        }
        String noTrailingSlashPath = StringUtils.removeEnd((String)originalPath, (String)"/");
        String noCrowdServicePath = StringUtils.removeEnd((String)noTrailingSlashPath, (String)"/services");
        String noSecurityServerPath = StringUtils.removeEnd((String)noTrailingSlashPath, (String)"/services/SecurityServer");
        if (noCrowdServicePath.length() < noSecurityServerPath.length()) {
            return noCrowdServicePath;
        }
        return noSecurityServerPath;
    }

    public static ClientPropertiesImpl newInstanceFromResourceLocator(ResourceLocator resourceLocator) {
        Properties properties = resourceLocator.getProperties();
        return ClientPropertiesImpl.newInstanceFromProperties(properties);
    }

    public static ClientPropertiesImpl newInstanceFromProperties(Properties properties) {
        ClientPropertiesImpl clientProperties = new ClientPropertiesImpl();
        clientProperties.updateProperties(properties);
        return clientProperties;
    }
}

