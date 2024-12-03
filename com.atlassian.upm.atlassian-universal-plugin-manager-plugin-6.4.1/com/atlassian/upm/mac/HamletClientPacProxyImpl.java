/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.UriBuilder
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.http.Header
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpResponse
 *  org.apache.http.NameValuePair
 *  org.apache.http.client.HttpClient
 *  org.apache.http.client.config.RequestConfig
 *  org.apache.http.client.methods.HttpGet
 *  org.apache.http.client.methods.HttpPost
 *  org.apache.http.client.methods.HttpRequestBase
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.entity.ContentType
 *  org.apache.http.entity.StringEntity
 *  org.apache.http.util.EntityUtils
 *  org.codehaus.jackson.JsonFactory
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.DeserializationConfig$Feature
 *  org.codehaus.jackson.map.MappingJsonFactory
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.joda.time.DateTime
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.upm.mac;

import com.atlassian.marketplace.client.http.HttpConfiguration;
import com.atlassian.marketplace.client.impl.CommonsHttpTransport;
import com.atlassian.upm.SysPersisted;
import com.atlassian.upm.UpmFugueConverters;
import com.atlassian.upm.UpmSettings;
import com.atlassian.upm.UpmSys;
import com.atlassian.upm.api.license.HostLicenseInformation;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.pac.ClientContext;
import com.atlassian.upm.core.pac.ClientContextFactory;
import com.atlassian.upm.core.pac.MarketplaceClientConfiguration;
import com.atlassian.upm.core.pac.MarketplaceClientManager;
import com.atlassian.upm.jwt.JwtTokenFactory;
import com.atlassian.upm.jwt.UpmJwtToken;
import com.atlassian.upm.license.internal.HostApplicationLicense;
import com.atlassian.upm.license.internal.HostLicenseProvider;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import com.atlassian.upm.mac.HamletClient;
import com.atlassian.upm.mac.HamletException;
import com.atlassian.upm.mac.HamletLicenseCollection;
import com.atlassian.upm.mac.HamletLicenseInfo;
import com.atlassian.upm.mac.LicensesSummary;
import com.atlassian.upm.rest.UpmUriBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.core.UriBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

public class HamletClientPacProxyImpl
implements HamletClient,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(HamletClientPacProxyImpl.class);
    private static final Pattern ETAG_REGEX = Pattern.compile("\"([0-9]*),([^\"]*)\"");
    private final HttpClient httpClient;
    private final ClientContextFactory clientContextFactory;
    private final MarketplaceClientManager marketplaceClientFactory;
    private final ObjectMapper objectMapper;
    private final SysPersisted sysPersisted;
    private final JwtTokenFactory jwtTokenFactory;
    private final HostLicenseProvider hostLicenseProvider;
    private final UpmUriBuilder uriBuilder;
    private final PluginLicenseRepository pluginLicenseRepository;
    private final HostLicenseInformation hostLicenseInformation;

    public HamletClientPacProxyImpl(ClientContextFactory clientContextFactory, MarketplaceClientManager marketplaceClientFactory, SysPersisted sysPersisted, JwtTokenFactory jwtTokenFactory, HostLicenseProvider hostLicenseProvider, UpmUriBuilder uriBuilder, PluginLicenseRepository pluginLicenseRepository, HostLicenseInformation hostLicenseInformation) {
        this.clientContextFactory = Objects.requireNonNull(clientContextFactory, "clientContextFactory");
        this.marketplaceClientFactory = Objects.requireNonNull(marketplaceClientFactory, "marketplaceClientFactory");
        this.sysPersisted = Objects.requireNonNull(sysPersisted, "sysPersisted");
        this.jwtTokenFactory = Objects.requireNonNull(jwtTokenFactory, "jwtTokenFactory");
        this.hostLicenseProvider = Objects.requireNonNull(hostLicenseProvider, "hostLicenseProvider");
        this.uriBuilder = Objects.requireNonNull(uriBuilder, "uriBuilder");
        this.pluginLicenseRepository = Objects.requireNonNull(pluginLicenseRepository, "pluginLicenseRepository");
        this.hostLicenseInformation = Objects.requireNonNull(hostLicenseInformation, "hostLicenseInformation");
        HttpConfiguration config = MarketplaceClientConfiguration.httpConfigurationFromSystemProperties().build();
        this.httpClient = CommonsHttpTransport.createHttpClient(config, UpmFugueConverters.fugueNone(URI.class));
        this.objectMapper = new ObjectMapper((JsonFactory)new MappingJsonFactory());
        this.objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public Option<HamletLicenseCollection> getPurchasedLicensesWithCredentials(String username, String password) throws HamletException {
        try {
            HttpPost post = new HttpPost(this.makeLicensesUri());
            String json = this.objectMapper.writeValueAsString((Object)new CredentialsRepresentation(username, password));
            post.setEntity((HttpEntity)new StringEntity(json, ContentType.APPLICATION_JSON));
            return this.getPurchasedLicenses((HttpRequestBase)post);
        }
        catch (HamletException e) {
            throw e;
        }
        catch (Exception e) {
            throw new HamletException(e);
        }
    }

    @Override
    public Option<HamletLicenseCollection> getPurchasedLicensesWithJwtToken() throws HamletException {
        return this.getPurchasedLicenses((HttpRequestBase)new HttpGet(this.makeLicensesJwtSignedUri(this.generateJwtToken())));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Option<HamletLicenseCollection> getPurchasedLicenses(HttpRequestBase method) throws HamletException {
        Option<HamletLicenseCollection> option;
        if (this.sysPersisted.is(UpmSettings.PAC_DISABLED)) {
            return Option.none();
        }
        HttpResponse resp = this.doRequest(method);
        try {
            String s = IOUtils.toString((InputStream)resp.getEntity().getContent(), (Charset)StandardCharsets.UTF_8);
            LicenseCollectionRepresentation rep = (LicenseCollectionRepresentation)this.objectMapper.readValue(s, LicenseCollectionRepresentation.class);
            Option<LicensesSummary> summary = this.parseSummary(resp);
            option = Option.some(new HamletLicenseCollection(rep.getLicense(), rep.getAddons(), summary));
        }
        catch (Throwable throwable) {
            try {
                EntityUtils.consumeQuietly((HttpEntity)resp.getEntity());
                throw throwable;
            }
            catch (HamletException e) {
                throw e;
            }
            catch (Exception e) {
                throw new HamletException(e);
            }
        }
        EntityUtils.consumeQuietly((HttpEntity)resp.getEntity());
        return option;
    }

    private Option<URI> getRedirectLocationFromRequest(HttpRequestBase request) throws HamletException {
        HttpResponse response;
        try {
            request.setConfig(RequestConfig.custom().setRedirectsEnabled(false).build());
            response = this.httpClient.execute((HttpUriRequest)request);
        }
        catch (Exception e) {
            throw new HamletException(e);
        }
        int status = response.getStatusLine().getStatusCode();
        try {
            if (status == 303) {
                Option<Header> header = Option.option(response.getFirstHeader("Location"));
                Option<URI> option = header.map(NameValuePair::getValue).map(URI::create);
                return option;
            }
            try {
                if (status == 401) {
                    throw new HamletException.InvalidCredentialsException();
                }
                log.error(IOUtils.toString((InputStream)response.getEntity().getContent()));
                throw new HamletException.HttpException(status);
            }
            catch (IOException e) {
                throw new HamletException(e);
            }
        }
        finally {
            EntityUtils.consumeQuietly((HttpEntity)response.getEntity());
        }
    }

    @Override
    public Option<HamletLicenseInfo> getPurchasedLicense(String pluginKey) throws HamletException {
        for (HamletLicenseCollection purchasedLicenses : this.getPurchasedLicensesWithJwtToken()) {
            for (HamletLicenseInfo addonLicense : purchasedLicenses.getAddonLicenses()) {
                if (!addonLicense.getKey().equals(pluginKey)) continue;
                return Option.some(addonLicense);
            }
        }
        return Option.none();
    }

    @Override
    public Option<URI> crossgradeAppLicense(String pluginKey) throws HamletException {
        if (this.sysPersisted.is(UpmSettings.PAC_DISABLED)) {
            return Option.none();
        }
        Option method = this.hostLicenseInformation.hostSen().flatMap(hostSen -> this.pluginLicenseRepository.getPluginLicense(pluginKey).flatMap(PluginLicense::getSupportEntitlementNumber).flatMap(appSen -> this.asJson(new CrossgradeAppRepresentation(this.uriBuilder.getDataCenterHamsKey(pluginKey), (String)appSen, (String)hostSen, this.uriBuilder.buildLicenseReceiptUri(pluginKey), "license")).map(json -> {
            HttpPost request = new HttpPost(this.uriBuilder.buildHamletCrossgradeLicenseUri());
            request.setEntity((HttpEntity)new StringEntity(json, ContentType.APPLICATION_JSON));
            return request;
        })));
        Iterator iterator = method.iterator();
        if (iterator.hasNext()) {
            HttpPost request = (HttpPost)iterator.next();
            return this.getRedirectLocationFromRequest((HttpRequestBase)request);
        }
        return Option.none();
    }

    private <T> Option<String> asJson(T rep) {
        try {
            return Option.some(this.objectMapper.writeValueAsString(rep));
        }
        catch (Exception e) {
            return Option.none();
        }
    }

    private UpmJwtToken generateJwtToken() {
        for (HostApplicationLicense hostLicense : this.hostLicenseProvider.getHostApplicationLicenses()) {
            if (!hostLicense.getSen().isDefined()) continue;
            String sharedSecret = this.generateSharedSecret(hostLicense);
            Map<String, String> claims = this.generateClaims(hostLicense);
            return claims.isEmpty() ? UpmJwtToken.invalidJwtToken() : this.jwtTokenFactory.generateToken(sharedSecret, claims, Option.none());
        }
        return UpmJwtToken.invalidJwtToken();
    }

    private String generateSharedSecret(HostApplicationLicense hostLicense) {
        return hostLicense.getRawLicense().replaceAll("[\n\r]", "");
    }

    private Map<String, String> generateClaims(HostApplicationLicense hostLicense) {
        HashMap<String, String> result = new HashMap<String, String>();
        for (String sen : hostLicense.getSen()) {
            result.put("sen", sen);
            result.put("purchaseDate", Long.toString(hostLicense.getPurchaseDate().getMillis()));
        }
        return Collections.unmodifiableMap(result);
    }

    public void destroy() {
        this.httpClient.getConnectionManager().shutdown();
    }

    private URI makeLicensesUri() {
        return UriBuilder.fromUri((String)UpmSys.getMpacBaseUrl()).path("rest/1.0/mac/licenses").build(new Object[0]);
    }

    private URI makeLicensesJwtSignedUri(UpmJwtToken jwtToken) {
        return UriBuilder.fromUri((URI)this.makeLicensesUri()).path("signed").path(jwtToken.getToken()).build(new Object[0]);
    }

    private HttpResponse doRequest(HttpRequestBase method) throws HamletException {
        HttpResponse response;
        ClientContext ctx = this.clientContextFactory.getClientContext(true);
        if (ctx.getSen() == null) {
            throw new HamletException.SenRequiredException();
        }
        method.addHeader("X-Pac-Client-Info", ctx.toString());
        method.addHeader("User-Agent", this.marketplaceClientFactory.getUserAgent());
        try {
            response = this.httpClient.execute((HttpUriRequest)method);
        }
        catch (Exception e) {
            throw new HamletException(e);
        }
        int status = response.getStatusLine().getStatusCode();
        if (status == 200) {
            return response;
        }
        EntityUtils.consumeQuietly((HttpEntity)response.getEntity());
        if (status == 401) {
            throw new HamletException.InvalidCredentialsException();
        }
        throw new HamletException.HttpException(status);
    }

    private Option<LicensesSummary> parseSummary(HttpResponse resp) throws HamletException {
        Header etag = resp.getFirstHeader("ETag");
        if (etag == null || StringUtils.isBlank((CharSequence)etag.getValue())) {
            return Option.none();
        }
        String value = etag.getValue();
        log.debug("License summary ETag: {}", (Object)value);
        Matcher m = ETAG_REGEX.matcher(value);
        if (m.matches()) {
            try {
                int count = Integer.parseInt(m.group(1));
                DateTime lastModified = HamletLicenseInfo.DATE_FORMAT.parseDateTime(m.group(2));
                return Option.some(new LicensesSummary(count, lastModified));
            }
            catch (IllegalArgumentException iax) {
                throw new HamletException("invalid ETag header");
            }
        }
        throw new HamletException("invalid ETag header");
    }

    static class CrossgradeAppRepresentation {
        @JsonProperty
        private final String productKey;
        @JsonProperty
        private final String crossgradingSEN;
        @JsonProperty
        private final String targetParentSEN;
        @JsonProperty
        private final URI callback;
        @JsonProperty
        private final String licenseFieldName;

        @JsonCreator
        CrossgradeAppRepresentation(@JsonProperty(value="productKey") String productKey, @JsonProperty(value="crossgradingSEN") String crossgradingSEN, @JsonProperty(value="targetParentSEN") String targetParentSEN, @JsonProperty(value="callback") URI callback, @JsonProperty(value="licenseFieldName") String licenseFieldName) {
            this.productKey = productKey;
            this.crossgradingSEN = crossgradingSEN;
            this.targetParentSEN = targetParentSEN;
            this.callback = callback;
            this.licenseFieldName = licenseFieldName;
        }
    }

    static class LicenseCollectionRepresentation {
        @JsonProperty
        private final HamletLicenseInfo license;
        @JsonProperty
        private final List<HamletLicenseInfo> addons;

        @JsonCreator
        public LicenseCollectionRepresentation(@JsonProperty(value="license") HamletLicenseInfo license, @JsonProperty(value="addons") Collection<HamletLicenseInfo> addons) {
            this.license = license;
            this.addons = Collections.unmodifiableList(new ArrayList<HamletLicenseInfo>(addons));
        }

        public HamletLicenseInfo getLicense() {
            return this.license;
        }

        @JsonIgnore
        public List<HamletLicenseInfo> getAddons() {
            return this.addons;
        }
    }

    static class CredentialsRepresentation {
        @JsonProperty
        private final String username;
        @JsonProperty
        private final String password;

        @JsonCreator
        public CredentialsRepresentation(@JsonProperty(value="username") String username, @JsonProperty(value="password") String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return this.username;
        }

        public String getPassword() {
            return this.password;
        }
    }
}

