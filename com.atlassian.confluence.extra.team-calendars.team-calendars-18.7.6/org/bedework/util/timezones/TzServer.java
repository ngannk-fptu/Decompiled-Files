/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonInclude$Include
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  org.apache.http.Header
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpResponse
 *  org.apache.http.NameValuePair
 *  org.apache.http.client.methods.CloseableHttpResponse
 *  org.apache.http.client.methods.HttpGet
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.client.utils.URIBuilder
 *  org.apache.http.impl.client.CloseableHttpClient
 *  org.apache.http.message.BasicHeader
 *  org.apache.http.message.BasicNameValuePair
 *  org.apache.http.util.EntityUtils
 */
package org.bedework.util.timezones;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.bedework.util.http.HttpUtil;
import org.bedework.util.misc.Logged;
import org.bedework.util.timezones.Timezones;
import org.bedework.util.timezones.TimezonesException;
import org.bedework.util.timezones.model.CapabilitiesType;
import org.bedework.util.timezones.model.TimezoneListType;

public class TzServer
extends Logged
implements AutoCloseable {
    private static String tzserverUri;
    final boolean oldVersion = false;
    private CapabilitiesType capabilities;
    private final ObjectMapper om = new ObjectMapper();
    private CloseableHttpClient client;
    private int status;

    public TzServer(String uri) throws TimezonesException {
        this.om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        tzserverUri = this.discover(uri);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public Timezones.TaggedTimeZone getTz(String id, String etag) throws TimezonesException {
        if (id == null) {
            return null;
        }
        try (CloseableHttpResponse hresp = this.doCall("zones/" + id.replace("/", "%2F"), etag, null);){
            if (this.status == 204) {
                Timezones.TaggedTimeZone taggedTimeZone = new Timezones.TaggedTimeZone(etag);
                return taggedTimeZone;
            }
            if (this.status != 200) {
                Timezones.TaggedTimeZone taggedTimeZone = null;
                return taggedTimeZone;
            }
            String respEtag = HttpUtil.getFirstHeaderValue((HttpResponse)hresp, "Etag");
            if (respEtag == null) {
                Timezones.TaggedTimeZone taggedTimeZone = new Timezones.TaggedTimeZone("--No etag--", EntityUtils.toString((HttpEntity)hresp.getEntity()));
                return taggedTimeZone;
            }
            Timezones.TaggedTimeZone taggedTimeZone = new Timezones.TaggedTimeZone(respEtag, EntityUtils.toString((HttpEntity)hresp.getEntity()));
            return taggedTimeZone;
        }
        catch (TimezonesException cfe) {
            throw cfe;
        }
        catch (Throwable t) {
            throw new TimezonesException(t);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public TimezoneListType getList(String changedSince) throws TimezonesException {
        ArrayList<BasicNameValuePair> pars = new ArrayList<BasicNameValuePair>();
        if (changedSince != null) {
            pars.add(new BasicNameValuePair("changedsince", changedSince));
        }
        NameValuePair[] parsArray = pars.toArray(new NameValuePair[0]);
        try (CloseableHttpResponse hresp = this.doCall("zones", null, parsArray);){
            if (this.status != 200) {
                TimezoneListType timezoneListType2 = null;
                return timezoneListType2;
            }
            InputStream is = hresp.getEntity().getContent();
            TimezoneListType timezoneListType = (TimezoneListType)this.om.readValue(is, TimezoneListType.class);
            return timezoneListType;
        }
        catch (Throwable t) {
            this.error("getList error: " + t.getMessage());
            t.printStackTrace();
            return null;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public Properties getAliases() throws TimezonesException {
        try (CloseableHttpResponse hresp = this.doCall("aliases", null, null);){
            if (this.status != 200) {
                Properties properties2 = null;
                return properties2;
            }
            Properties a = new Properties();
            InputStream is = hresp.getEntity().getContent();
            a.load(is);
            Properties properties = a;
            return properties;
        }
        catch (Throwable t) {
            this.error("getAliases error: " + t.getMessage());
            t.printStackTrace();
            return null;
        }
    }

    public CapabilitiesType getCapabilities() {
        return this.capabilities;
    }

    @Override
    public void close() {
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private String discover(String url) throws TimezonesException {
        String realUrl;
        try {
            new URL(url);
            realUrl = url;
        }
        catch (Throwable t) {
            realUrl = "https://" + url + "/.well-known/timezone";
        }
        int redirects = 0;
        while (true) {
            block27: {
                if (redirects >= 10) {
                    if (!this.debug) throw new TimezonesException("Too many redirects on " + realUrl);
                    this.error("Too many redirects: Got response " + this.status + ", from " + realUrl);
                    throw new TimezonesException("Too many redirects on " + realUrl);
                }
                try (CloseableHttpResponse hresp = this.doCall(realUrl, "capabilities", (String)null, (NameValuePair[])null);){
                    String newLoc;
                    if ((this.status == 301 || this.status == 302 || this.status == 307) && (newLoc = HttpUtil.getFirstHeaderValue((HttpResponse)hresp, "location")) != null) {
                        int qpos;
                        if (this.debug) {
                            this.debug("Got redirected to " + newLoc + " from " + url);
                        }
                        realUrl = (qpos = newLoc.indexOf("?")) < 0 ? newLoc : newLoc.substring(0, qpos);
                        break block27;
                    }
                    if (this.status != 200) {
                        this.error("================================================");
                        this.error("================================================");
                        this.error("================================================");
                        this.error("Got response " + this.status + ", from " + realUrl);
                        this.error("================================================");
                        this.error("================================================");
                        this.error("================================================");
                        throw new TimezonesException(TimezonesException.noPrimary, "Got response " + this.status + ", from " + realUrl);
                    }
                    try {
                        this.capabilities = (CapabilitiesType)this.om.readValue(hresp.getEntity().getContent(), CapabilitiesType.class);
                    }
                    catch (Throwable t) {
                        this.error(t);
                    }
                    String string = realUrl;
                    return string;
                }
                catch (TimezonesException tze) {
                    throw tze;
                }
                catch (Throwable t) {
                    if (!this.debug) throw new TimezonesException(t);
                    this.error(t);
                    throw new TimezonesException(t);
                }
            }
            ++redirects;
        }
    }

    private CloseableHttpResponse doCall(String action, String etag, NameValuePair ... params) throws TimezonesException {
        if (tzserverUri == null) {
            throw new TimezonesException("No timezones server URI defined");
        }
        return this.doCall(tzserverUri, action, etag, params);
    }

    private CloseableHttpResponse doCall(String serverUrl, String action, String etag, NameValuePair ... params) throws TimezonesException {
        try {
            String url = serverUrl;
            if (!url.endsWith("/")) {
                url = url + "/";
            }
            url = url + action;
            URI tzUri = new URI(url);
            URIBuilder urib = new URIBuilder().setScheme(tzUri.getScheme()).setHost(tzUri.getHost()).setPort(tzUri.getPort()).setPath(tzUri.getPath());
            if (params != null) {
                urib.setParameters(params);
            }
            URI uri = urib.build();
            HttpGet httpGet = new HttpGet(uri);
            if (etag != null) {
                httpGet.addHeader((Header)new BasicHeader("If-None-Match", etag));
                httpGet.addHeader((Header)new BasicHeader("Accept", "application/json"));
            }
            CloseableHttpResponse resp = this.getClient().execute((HttpUriRequest)httpGet);
            this.status = HttpUtil.getStatus((HttpResponse)resp);
            return resp;
        }
        catch (Throwable t) {
            throw new TimezonesException(t);
        }
    }

    private CloseableHttpClient getClient() {
        if (this.client != null) {
            return this.client;
        }
        this.client = HttpUtil.getClient(true);
        return this.client;
    }
}

