/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.Cookie
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.buf.CharChunk
 *  org.apache.tomcat.util.buf.MessageBytes
 *  org.apache.tomcat.util.buf.UDecoder
 *  org.apache.tomcat.util.buf.UriUtil
 *  org.apache.tomcat.util.file.ConfigFileLoader
 *  org.apache.tomcat.util.file.ConfigurationSource$Resource
 *  org.apache.tomcat.util.http.RequestUtil
 */
package org.apache.catalina.valves.rewrite;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Pipeline;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.URLEncoder;
import org.apache.catalina.valves.ValveBase;
import org.apache.catalina.valves.rewrite.InternalRewriteMap;
import org.apache.catalina.valves.rewrite.QuotedStringTokenizer;
import org.apache.catalina.valves.rewrite.RandomizedTextRewriteMap;
import org.apache.catalina.valves.rewrite.ResolverImpl;
import org.apache.catalina.valves.rewrite.RewriteCond;
import org.apache.catalina.valves.rewrite.RewriteMap;
import org.apache.catalina.valves.rewrite.RewriteRule;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.CharChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.buf.UDecoder;
import org.apache.tomcat.util.buf.UriUtil;
import org.apache.tomcat.util.file.ConfigFileLoader;
import org.apache.tomcat.util.file.ConfigurationSource;
import org.apache.tomcat.util.http.RequestUtil;

public class RewriteValve
extends ValveBase {
    protected RewriteRule[] rules = null;
    protected ThreadLocal<Boolean> invoked = new ThreadLocal();
    protected String resourcePath = "rewrite.config";
    protected boolean context = false;
    protected boolean enabled = true;
    protected Map<String, RewriteMap> maps = new ConcurrentHashMap<String, RewriteMap>();
    protected ArrayList<String> mapsConfiguration = new ArrayList();

    public RewriteValve() {
        super(true);
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();
        this.containerLog = LogFactory.getLog((String)(this.getContainer().getLogName() + ".rewrite"));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected synchronized void startInternal() throws LifecycleException {
        InputStream is;
        block29: {
            super.startInternal();
            is = null;
            if (this.getContainer() instanceof Context) {
                this.context = true;
                is = ((Context)this.getContainer()).getServletContext().getResourceAsStream("/WEB-INF/" + this.resourcePath);
                if (this.containerLog.isDebugEnabled()) {
                    if (is == null) {
                        this.containerLog.debug((Object)("No configuration resource found: /WEB-INF/" + this.resourcePath));
                    } else {
                        this.containerLog.debug((Object)("Read configuration from: /WEB-INF/" + this.resourcePath));
                    }
                }
            } else {
                String resourceName = Container.getConfigPath(this.getContainer(), this.resourcePath);
                try {
                    ConfigurationSource.Resource resource = ConfigFileLoader.getSource().getResource(resourceName);
                    is = resource.getInputStream();
                }
                catch (IOException e) {
                    if (!this.containerLog.isDebugEnabled()) break block29;
                    this.containerLog.debug((Object)("No configuration resource found: " + resourceName), (Throwable)e);
                }
            }
        }
        if (is == null) {
            return;
        }
        try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isr);){
            this.parse(reader);
        }
        catch (IOException ioe) {
            this.containerLog.error((Object)sm.getString("rewriteValve.closeError"), (Throwable)ioe);
        }
        finally {
            try {
                is.close();
            }
            catch (IOException e) {
                this.containerLog.error((Object)sm.getString("rewriteValve.closeError"), (Throwable)e);
            }
        }
    }

    public void setConfiguration(String configuration) throws Exception {
        if (this.containerLog == null) {
            this.containerLog = LogFactory.getLog((String)(this.getContainer().getLogName() + ".rewrite"));
        }
        this.maps.clear();
        this.parse(new BufferedReader(new StringReader(configuration)));
    }

    public String getConfiguration() {
        StringBuilder buffer = new StringBuilder();
        for (String mapConfiguration : this.mapsConfiguration) {
            buffer.append(mapConfiguration).append("\r\n");
        }
        if (this.mapsConfiguration.size() > 0) {
            buffer.append("\r\n");
        }
        for (RewriteRule rule : this.rules) {
            for (int j = 0; j < rule.getConditions().length; ++j) {
                buffer.append(rule.getConditions()[j].toString()).append("\r\n");
            }
            buffer.append(rule.toString()).append("\r\n").append("\r\n");
        }
        return buffer.toString();
    }

    protected void parse(BufferedReader reader) throws LifecycleException {
        ArrayList<RewriteRule> rules = new ArrayList<RewriteRule>();
        ArrayList<RewriteCond> conditions = new ArrayList<RewriteCond>();
        while (true) {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    Object result = RewriteValve.parse(line);
                    if (result instanceof RewriteRule) {
                        RewriteRule rule = (RewriteRule)result;
                        if (this.containerLog.isDebugEnabled()) {
                            this.containerLog.debug((Object)("Add rule with pattern " + rule.getPatternString() + " and substitution " + rule.getSubstitutionString()));
                        }
                        for (int i = conditions.size() - 1; i > 0; --i) {
                            if (!((RewriteCond)conditions.get(i - 1)).isOrnext()) continue;
                            ((RewriteCond)conditions.get(i)).setOrnext(true);
                        }
                        for (RewriteCond condition : conditions) {
                            if (this.containerLog.isDebugEnabled()) {
                                RewriteCond cond = condition;
                                this.containerLog.debug((Object)("Add condition " + cond.getCondPattern() + " test " + cond.getTestString() + " to rule with pattern " + rule.getPatternString() + " and substitution " + rule.getSubstitutionString() + (cond.isOrnext() ? " [OR]" : "") + (cond.isNocase() ? " [NC]" : "")));
                            }
                            rule.addCondition(condition);
                        }
                        conditions.clear();
                        rules.add(rule);
                        continue;
                    }
                    if (result instanceof RewriteCond) {
                        conditions.add((RewriteCond)result);
                        continue;
                    }
                    if (!(result instanceof Object[])) continue;
                    String mapName = (String)((Object[])result)[0];
                    RewriteMap map = (RewriteMap)((Object[])result)[1];
                    this.maps.put(mapName, map);
                    this.mapsConfiguration.add(line);
                    if (!(map instanceof Lifecycle)) continue;
                    ((Lifecycle)((Object)map)).start();
                }
            }
            catch (IOException e) {
                this.containerLog.error((Object)sm.getString("rewriteValve.readError"), (Throwable)e);
                continue;
            }
            break;
        }
        for (RewriteRule rule : this.rules = rules.toArray(new RewriteRule[0])) {
            rule.parse(this.maps);
        }
    }

    @Override
    protected synchronized void stopInternal() throws LifecycleException {
        super.stopInternal();
        for (RewriteMap map : this.maps.values()) {
            if (!(map instanceof Lifecycle)) continue;
            ((Lifecycle)((Object)map)).stop();
        }
        this.maps.clear();
        this.rules = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        block52: {
            if (!this.getEnabled() || this.rules == null || this.rules.length == 0) {
                this.getNext().invoke(request, response);
                return;
            }
            if (Boolean.TRUE.equals(this.invoked.get())) {
                try {
                    this.getNext().invoke(request, response);
                }
                finally {
                    this.invoked.set(null);
                }
                return;
            }
            try {
                ResolverImpl resolver = new ResolverImpl(request);
                this.invoked.set(Boolean.TRUE);
                Charset uriCharset = request.getConnector().getURICharset();
                String originalQueryStringEncoded = request.getQueryString();
                MessageBytes urlMB = this.context ? request.getRequestPathMB() : request.getDecodedRequestURIMB();
                urlMB.toChars();
                Object urlDecoded = urlMB.getCharChunk();
                CharSequence host = request.getServerName();
                boolean rewritten = false;
                boolean done = false;
                boolean qsa = false;
                boolean qsd = false;
                block9: for (int i = 0; i < this.rules.length; ++i) {
                    RewriteRule rule;
                    Object test = (rule = this.rules[i]).isHost() ? host : urlDecoded;
                    CharSequence newtest = rule.evaluate((CharSequence)test, resolver);
                    if (newtest != null && !test.equals(newtest.toString())) {
                        if (this.containerLog.isDebugEnabled()) {
                            this.containerLog.debug((Object)("Rewrote " + test + " as " + newtest + " with rule pattern " + rule.getPatternString()));
                        }
                        if (rule.isHost()) {
                            host = newtest;
                        } else {
                            urlDecoded = newtest;
                        }
                        rewritten = true;
                    }
                    if (!qsa && newtest != null && rule.isQsappend()) {
                        qsa = true;
                    }
                    if (!qsd && newtest != null && rule.isQsdiscard()) {
                        qsd = true;
                    }
                    if (rule.isForbidden() && newtest != null) {
                        response.sendError(403);
                        done = true;
                        break;
                    }
                    if (rule.isGone() && newtest != null) {
                        response.sendError(410);
                        done = true;
                        break;
                    }
                    if (rule.isRedirect() && newtest != null) {
                        String rewrittenQueryStringDecoded;
                        String urlStringDecoded = urlDecoded.toString();
                        int index = urlStringDecoded.indexOf(63);
                        if (index == -1) {
                            rewrittenQueryStringDecoded = null;
                        } else {
                            rewrittenQueryStringDecoded = urlStringDecoded.substring(index + 1);
                            urlStringDecoded = urlStringDecoded.substring(0, index);
                        }
                        StringBuilder urlStringEncoded = new StringBuilder(URLEncoder.DEFAULT.encode(urlStringDecoded, uriCharset));
                        if (!qsd && originalQueryStringEncoded != null && originalQueryStringEncoded.length() > 0) {
                            if (rewrittenQueryStringDecoded == null) {
                                urlStringEncoded.append('?');
                                urlStringEncoded.append(originalQueryStringEncoded);
                            } else if (qsa) {
                                urlStringEncoded.append('?');
                                urlStringEncoded.append(URLEncoder.QUERY.encode(rewrittenQueryStringDecoded, uriCharset));
                                urlStringEncoded.append('&');
                                urlStringEncoded.append(originalQueryStringEncoded);
                            } else if (index == urlStringEncoded.length() - 1) {
                                urlStringEncoded.deleteCharAt(index);
                            } else {
                                urlStringEncoded.append('?');
                                urlStringEncoded.append(URLEncoder.QUERY.encode(rewrittenQueryStringDecoded, uriCharset));
                            }
                        } else if (rewrittenQueryStringDecoded != null) {
                            urlStringEncoded.append('?');
                            urlStringEncoded.append(URLEncoder.QUERY.encode(rewrittenQueryStringDecoded, uriCharset));
                        }
                        if (this.context && urlStringEncoded.charAt(0) == '/' && !UriUtil.hasScheme((CharSequence)urlStringEncoded)) {
                            urlStringEncoded.insert(0, request.getContext().getEncodedPath());
                        }
                        if (rule.isNoescape()) {
                            response.sendRedirect(UDecoder.URLDecode((String)urlStringEncoded.toString(), (Charset)uriCharset));
                        } else {
                            response.sendRedirect(urlStringEncoded.toString());
                        }
                        response.setStatus(rule.getRedirectCode());
                        done = true;
                        break;
                    }
                    if (rule.isCookie() && newtest != null) {
                        Cookie cookie = new Cookie(rule.getCookieName(), rule.getCookieResult());
                        cookie.setDomain(rule.getCookieDomain());
                        cookie.setMaxAge(rule.getCookieLifetime());
                        cookie.setPath(rule.getCookiePath());
                        cookie.setSecure(rule.isCookieSecure());
                        cookie.setHttpOnly(rule.isCookieHttpOnly());
                        response.addCookie(cookie);
                    }
                    if (rule.isEnv() && newtest != null) {
                        for (int j = 0; j < rule.getEnvSize(); ++j) {
                            request.setAttribute(rule.getEnvName(j), rule.getEnvResult(j));
                        }
                    }
                    if (rule.isType() && newtest != null) {
                        response.setContentType(rule.getTypeValue());
                    }
                    if (rule.isChain() && newtest == null) {
                        for (int j = i; j < this.rules.length; ++j) {
                            if (this.rules[j].isChain()) continue;
                            i = j;
                            continue block9;
                        }
                        continue;
                    }
                    if (rule.isLast() && newtest != null) break;
                    if (rule.isNext() && newtest != null) {
                        i = 0;
                        continue;
                    }
                    if (newtest == null) continue;
                    i += rule.getSkip();
                }
                if (rewritten) {
                    if (done) break block52;
                    String urlStringDecoded = urlDecoded.toString();
                    String queryStringDecoded = null;
                    int queryIndex = urlStringDecoded.indexOf(63);
                    if (queryIndex != -1) {
                        queryStringDecoded = urlStringDecoded.substring(queryIndex + 1);
                        urlStringDecoded = urlStringDecoded.substring(0, queryIndex);
                    }
                    String contextPath = null;
                    if (this.context) {
                        contextPath = request.getContextPath();
                    }
                    request.getCoyoteRequest().requestURI().setChars(MessageBytes.EMPTY_CHAR_ARRAY, 0, 0);
                    CharChunk chunk = request.getCoyoteRequest().requestURI().getCharChunk();
                    if (this.context) {
                        chunk.append(contextPath);
                    }
                    chunk.append(URLEncoder.DEFAULT.encode(urlStringDecoded, uriCharset));
                    urlStringDecoded = RequestUtil.normalize((String)urlStringDecoded);
                    request.getCoyoteRequest().decodedURI().setChars(MessageBytes.EMPTY_CHAR_ARRAY, 0, 0);
                    chunk = request.getCoyoteRequest().decodedURI().getCharChunk();
                    if (this.context) {
                        chunk.append(request.getServletContext().getContextPath());
                    }
                    chunk.append(urlStringDecoded);
                    if (queryStringDecoded != null) {
                        request.getCoyoteRequest().queryString().setChars(MessageBytes.EMPTY_CHAR_ARRAY, 0, 0);
                        chunk = request.getCoyoteRequest().queryString().getCharChunk();
                        chunk.append(URLEncoder.QUERY.encode(queryStringDecoded, uriCharset));
                        if (qsa && originalQueryStringEncoded != null && originalQueryStringEncoded.length() > 0) {
                            chunk.append('&');
                            chunk.append(originalQueryStringEncoded);
                        }
                    }
                    if (!host.equals(request.getServerName())) {
                        request.getCoyoteRequest().serverName().setChars(MessageBytes.EMPTY_CHAR_ARRAY, 0, 0);
                        chunk = request.getCoyoteRequest().serverName().getCharChunk();
                        chunk.append(host.toString());
                    }
                    request.getMappingData().recycle();
                    Connector connector = request.getConnector();
                    try {
                        if (!connector.getProtocolHandler().getAdapter().prepare(request.getCoyoteRequest(), response.getCoyoteResponse())) {
                            return;
                        }
                    }
                    catch (Exception rewrittenQueryStringDecoded) {
                        // empty catch block
                    }
                    Pipeline pipeline = connector.getService().getContainer().getPipeline();
                    request.setAsyncSupported(pipeline.isAsyncSupported());
                    pipeline.getFirst().invoke(request, response);
                    break block52;
                }
                this.getNext().invoke(request, response);
            }
            finally {
                this.invoked.set(null);
            }
        }
    }

    public static Object parse(String line) {
        QuotedStringTokenizer tokenizer = new QuotedStringTokenizer(line);
        if (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.equals("RewriteCond")) {
                RewriteCond condition = new RewriteCond();
                if (tokenizer.countTokens() < 2) {
                    throw new IllegalArgumentException(sm.getString("rewriteValve.invalidLine", new Object[]{line}));
                }
                condition.setTestString(tokenizer.nextToken());
                condition.setCondPattern(tokenizer.nextToken());
                if (tokenizer.hasMoreTokens()) {
                    String flags = tokenizer.nextToken();
                    condition.setFlagsString(flags);
                    if (flags.startsWith("[") && flags.endsWith("]")) {
                        flags = flags.substring(1, flags.length() - 1);
                    }
                    StringTokenizer flagsTokenizer = new StringTokenizer(flags, ",");
                    while (flagsTokenizer.hasMoreElements()) {
                        RewriteValve.parseCondFlag(line, condition, flagsTokenizer.nextToken());
                    }
                }
                return condition;
            }
            if (token.equals("RewriteRule")) {
                RewriteRule rule = new RewriteRule();
                if (tokenizer.countTokens() < 2) {
                    throw new IllegalArgumentException(sm.getString("rewriteValve.invalidLine", new Object[]{line}));
                }
                rule.setPatternString(tokenizer.nextToken());
                rule.setSubstitutionString(tokenizer.nextToken());
                if (tokenizer.hasMoreTokens()) {
                    String flags = tokenizer.nextToken();
                    rule.setFlagsString(flags);
                    if (flags.startsWith("[") && flags.endsWith("]")) {
                        flags = flags.substring(1, flags.length() - 1);
                    }
                    StringTokenizer flagsTokenizer = new StringTokenizer(flags, ",");
                    while (flagsTokenizer.hasMoreElements()) {
                        RewriteValve.parseRuleFlag(line, rule, flagsTokenizer.nextToken());
                    }
                }
                return rule;
            }
            if (token.equals("RewriteMap")) {
                if (tokenizer.countTokens() < 2) {
                    throw new IllegalArgumentException(sm.getString("rewriteValve.invalidLine", new Object[]{line}));
                }
                String name = tokenizer.nextToken();
                String rewriteMapClassName = tokenizer.nextToken();
                RewriteMap map = null;
                if (rewriteMapClassName.startsWith("int:")) {
                    map = InternalRewriteMap.toMap(rewriteMapClassName.substring("int:".length()));
                } else if (rewriteMapClassName.startsWith("txt:")) {
                    map = new RandomizedTextRewriteMap(rewriteMapClassName.substring("txt:".length()), false);
                } else if (rewriteMapClassName.startsWith("rnd:")) {
                    map = new RandomizedTextRewriteMap(rewriteMapClassName.substring("rnd:".length()), true);
                } else if (rewriteMapClassName.startsWith("prg:")) {
                    rewriteMapClassName = rewriteMapClassName.substring("prg:".length());
                } else if (rewriteMapClassName.startsWith("dbm:") || rewriteMapClassName.startsWith("dbd:") || rewriteMapClassName.startsWith("fastdbd:")) {
                    // empty if block
                }
                if (map == null) {
                    try {
                        map = (RewriteMap)Class.forName(rewriteMapClassName).getConstructor(new Class[0]).newInstance(new Object[0]);
                    }
                    catch (Exception e) {
                        throw new IllegalArgumentException(sm.getString("rewriteValve.invalidMapClassName", new Object[]{line}));
                    }
                }
                if (tokenizer.hasMoreTokens()) {
                    if (tokenizer.countTokens() == 1) {
                        map.setParameters(tokenizer.nextToken());
                    } else {
                        ArrayList<String> params = new ArrayList<String>();
                        while (tokenizer.hasMoreTokens()) {
                            params.add(tokenizer.nextToken());
                        }
                        map.setParameters(params.toArray(new String[0]));
                    }
                }
                return new Object[]{name, map};
            }
            if (!token.startsWith("#")) {
                throw new IllegalArgumentException(sm.getString("rewriteValve.invalidLine", new Object[]{line}));
            }
        }
        return null;
    }

    protected static void parseCondFlag(String line, RewriteCond condition, String flag) {
        if (flag.equals("NC") || flag.equals("nocase")) {
            condition.setNocase(true);
        } else if (flag.equals("OR") || flag.equals("ornext")) {
            condition.setOrnext(true);
        } else {
            throw new IllegalArgumentException(sm.getString("rewriteValve.invalidFlags", new Object[]{line, flag}));
        }
    }

    protected static void parseRuleFlag(String line, RewriteRule rule, String flag) {
        if (flag.equals("B")) {
            rule.setEscapeBackReferences(true);
        } else if (flag.equals("chain") || flag.equals("C")) {
            rule.setChain(true);
        } else if (flag.startsWith("cookie=") || flag.startsWith("CO=")) {
            rule.setCookie(true);
            if (flag.startsWith("cookie")) {
                flag = flag.substring("cookie=".length());
            } else if (flag.startsWith("CO=")) {
                flag = flag.substring("CO=".length());
            }
            StringTokenizer tokenizer = new StringTokenizer(flag, ":");
            if (tokenizer.countTokens() < 2) {
                throw new IllegalArgumentException(sm.getString("rewriteValve.invalidFlags", new Object[]{line, flag}));
            }
            rule.setCookieName(tokenizer.nextToken());
            rule.setCookieValue(tokenizer.nextToken());
            if (tokenizer.hasMoreTokens()) {
                rule.setCookieDomain(tokenizer.nextToken());
            }
            if (tokenizer.hasMoreTokens()) {
                try {
                    rule.setCookieLifetime(Integer.parseInt(tokenizer.nextToken()));
                }
                catch (NumberFormatException e) {
                    throw new IllegalArgumentException(sm.getString("rewriteValve.invalidFlags", new Object[]{line, flag}), e);
                }
            }
            if (tokenizer.hasMoreTokens()) {
                rule.setCookiePath(tokenizer.nextToken());
            }
            if (tokenizer.hasMoreTokens()) {
                rule.setCookieSecure(Boolean.parseBoolean(tokenizer.nextToken()));
            }
            if (tokenizer.hasMoreTokens()) {
                rule.setCookieHttpOnly(Boolean.parseBoolean(tokenizer.nextToken()));
            }
        } else if (flag.startsWith("env=") || flag.startsWith("E=")) {
            rule.setEnv(true);
            if (flag.startsWith("env=")) {
                flag = flag.substring("env=".length());
            } else if (flag.startsWith("E=")) {
                flag = flag.substring("E=".length());
            }
            int pos = flag.indexOf(58);
            if (pos == -1 || pos + 1 == flag.length()) {
                throw new IllegalArgumentException(sm.getString("rewriteValve.invalidFlags", new Object[]{line, flag}));
            }
            rule.addEnvName(flag.substring(0, pos));
            rule.addEnvValue(flag.substring(pos + 1));
        } else if (flag.startsWith("forbidden") || flag.startsWith("F")) {
            rule.setForbidden(true);
        } else if (flag.startsWith("gone") || flag.startsWith("G")) {
            rule.setGone(true);
        } else if (flag.startsWith("host") || flag.startsWith("H")) {
            rule.setHost(true);
        } else if (flag.startsWith("last") || flag.startsWith("L")) {
            rule.setLast(true);
        } else if (flag.startsWith("nocase") || flag.startsWith("NC")) {
            rule.setNocase(true);
        } else if (flag.startsWith("noescape") || flag.startsWith("NE")) {
            rule.setNoescape(true);
        } else if (flag.startsWith("next") || flag.startsWith("N")) {
            rule.setNext(true);
        } else if (flag.startsWith("qsappend") || flag.startsWith("QSA")) {
            rule.setQsappend(true);
        } else if (flag.startsWith("qsdiscard") || flag.startsWith("QSD")) {
            rule.setQsdiscard(true);
        } else if (flag.startsWith("redirect") || flag.startsWith("R")) {
            rule.setRedirect(true);
            int redirectCode = 302;
            if (flag.startsWith("redirect=") || flag.startsWith("R=")) {
                if (flag.startsWith("redirect=")) {
                    flag = flag.substring("redirect=".length());
                } else if (flag.startsWith("R=")) {
                    flag = flag.substring("R=".length());
                }
                switch (flag) {
                    case "temp": {
                        redirectCode = 302;
                        break;
                    }
                    case "permanent": {
                        redirectCode = 301;
                        break;
                    }
                    case "seeother": {
                        redirectCode = 303;
                        break;
                    }
                    default: {
                        redirectCode = Integer.parseInt(flag);
                    }
                }
            }
            rule.setRedirectCode(redirectCode);
        } else if (flag.startsWith("skip") || flag.startsWith("S")) {
            if (flag.startsWith("skip=")) {
                flag = flag.substring("skip=".length());
            } else if (flag.startsWith("S=")) {
                flag = flag.substring("S=".length());
            }
            rule.setSkip(Integer.parseInt(flag));
        } else if (flag.startsWith("type") || flag.startsWith("T")) {
            if (flag.startsWith("type=")) {
                flag = flag.substring("type=".length());
            } else if (flag.startsWith("T=")) {
                flag = flag.substring("T=".length());
            }
            rule.setType(true);
            rule.setTypeValue(flag);
        } else {
            throw new IllegalArgumentException(sm.getString("rewriteValve.invalidFlags", new Object[]{line, flag}));
        }
    }
}

