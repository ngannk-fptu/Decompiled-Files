/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.http;

import aQute.bnd.http.HttpClient;
import aQute.bnd.osgi.Processor;
import aQute.bnd.service.url.TaggedData;
import aQute.lib.converter.TypeReference;
import aQute.lib.promise.PromiseExecutor;
import aQute.service.reporter.Reporter;
import java.io.File;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.osgi.util.promise.Promise;

public class HttpRequest<T> {
    String verb = "GET";
    Object upload;
    Type download;
    Map<String, String> headers = new HashMap<String, String>();
    long timeout = -1L;
    HttpClient client;
    String ifNoneMatch;
    long ifModifiedSince;
    long ifUnmodifiedSince;
    URL url;
    int redirects = 10;
    String ifMatch;
    boolean cached;
    long maxStale;
    Reporter reporter;
    File useCacheFile;
    boolean updateTag;

    HttpRequest(HttpClient client) {
        this.client = client;
    }

    public <X> HttpRequest<X> get(Class<X> type) {
        this.download = type;
        return this;
    }

    public <X> HttpRequest<X> get(TypeReference<X> type) {
        this.download = type.getType();
        return this;
    }

    public HttpRequest<Object> get(Type type) {
        this.download = type;
        return this;
    }

    public HttpRequest<T> verb(String verb) {
        this.verb = verb;
        return this;
    }

    public HttpRequest<T> put() {
        this.verb = "PUT";
        return this;
    }

    public HttpRequest<T> head() {
        this.verb = "HEAD";
        return this;
    }

    public HttpRequest<T> get() {
        this.verb = "GET";
        return this;
    }

    public HttpRequest<T> post() {
        this.verb = "POST";
        return this;
    }

    public HttpRequest<T> option() {
        this.verb = "OPTION";
        return this;
    }

    public HttpRequest<T> delete() {
        this.verb = "DELETE";
        return this;
    }

    public HttpRequest<T> upload(Object upload) {
        this.upload = upload;
        return this;
    }

    public HttpRequest<T> headers(Map<String, String> map) {
        this.headers.putAll(map);
        return this;
    }

    public HttpRequest<T> headers(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public HttpRequest<T> timeout(long timeoutInMs) {
        this.timeout = timeoutInMs;
        return this;
    }

    public HttpRequest<T> ifNoneMatch(String etag) {
        this.ifNoneMatch = etag;
        return this;
    }

    public HttpRequest<T> ifModifiedSince(long epochTime) {
        this.ifModifiedSince = epochTime;
        return this;
    }

    public HttpRequest<T> maxRedirects(int n) {
        this.redirects = n;
        return this;
    }

    public T go(URL url) throws Exception {
        this.url = url;
        return (T)this.client.send(this);
    }

    public T go(URI url) throws Exception {
        this.url = url.toURL();
        return (T)this.client.send(this);
    }

    public HttpRequest<T> age(int n, TimeUnit tu) {
        this.headers.put("Age", "" + tu.toSeconds(n));
        return this;
    }

    public Promise<T> async(URL url) throws InterruptedException {
        this.url = url;
        PromiseExecutor executor = new PromiseExecutor(Processor.getExecutor());
        return executor.submit(new Callable<T>(){

            @Override
            public T call() throws Exception {
                return HttpRequest.this.client.send(HttpRequest.this);
            }
        });
    }

    public Promise<T> async(URI uri) throws MalformedURLException, InterruptedException {
        return this.async(uri.toURL());
    }

    public String toString() {
        return "HttpRequest [verb=" + this.verb + ", upload=" + this.upload + ", download=" + this.download + ", headers=" + this.headers + ", timeout=" + this.timeout + ", client=" + this.client + ", url=" + this.url + "]";
    }

    public HttpRequest<T> ifUnmodifiedSince(long ifNotModifiedSince) {
        this.ifUnmodifiedSince = ifNotModifiedSince;
        return this;
    }

    public HttpRequest<T> ifMatch(String etag) {
        this.ifMatch = etag;
        return this;
    }

    public HttpRequest<TaggedData> asTag() {
        return this.get(TaggedData.class);
    }

    public HttpRequest<String> asString() {
        return this.get(String.class);
    }

    public boolean isCache() {
        return "GET".equalsIgnoreCase(this.verb) && this.cached || this.download == File.class;
    }

    public HttpRequest<File> useCache(long maxStale) {
        this.maxStale = maxStale;
        this.cached = true;
        this.download = File.class;
        return this;
    }

    public HttpRequest<File> useCache() {
        return this.useCache(-1L);
    }

    public HttpRequest<File> useCache(File file) {
        this.useCacheFile = file;
        return this.useCache(-1L);
    }

    public HttpRequest<File> useCache(File file, long maxStale) {
        this.useCacheFile = file;
        return this.useCache(maxStale);
    }

    public HttpRequest<T> report(Reporter reporter) {
        this.reporter = reporter;
        return this;
    }

    public HttpRequest<T> timeout(long timeout, TimeUnit unit) {
        this.timeout = timeout;
        return this;
    }

    public boolean isTagResult() {
        return this.download == null || this.download == TaggedData.class;
    }

    public HttpRequest<T> updateTag() {
        this.updateTag = true;
        return this;
    }
}

