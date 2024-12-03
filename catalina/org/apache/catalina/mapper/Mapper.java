/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.MappingMatch
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.buf.Ascii
 *  org.apache.tomcat.util.buf.CharChunk
 *  org.apache.tomcat.util.buf.MessageBytes
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.servlet.http.MappingMatch;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.Wrapper;
import org.apache.catalina.mapper.MappingData;
import org.apache.catalina.mapper.WrapperMappingInfo;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.Ascii;
import org.apache.tomcat.util.buf.CharChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.res.StringManager;

public final class Mapper {
    private static final Log log = LogFactory.getLog(Mapper.class);
    private static final StringManager sm = StringManager.getManager(Mapper.class);
    volatile MappedHost[] hosts = new MappedHost[0];
    private volatile String defaultHostName = null;
    private volatile MappedHost defaultHost = null;
    private final Map<Context, ContextVersion> contextObjectToContextVersionMap = new ConcurrentHashMap<Context, ContextVersion>();

    public synchronized void setDefaultHostName(String defaultHostName) {
        this.defaultHostName = Mapper.renameWildcardHost(defaultHostName);
        this.defaultHost = this.defaultHostName == null ? null : (MappedHost)Mapper.exactFind((MapElement[])this.hosts, (String)this.defaultHostName);
    }

    public synchronized void addHost(String name, String[] aliases, Host host) {
        MappedHost[] newHosts = new MappedHost[this.hosts.length + 1];
        MappedHost newHost = new MappedHost(name = Mapper.renameWildcardHost(name), host);
        if (Mapper.insertMap(this.hosts, newHosts, newHost)) {
            this.hosts = newHosts;
            if (newHost.name.equals(this.defaultHostName)) {
                this.defaultHost = newHost;
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("mapper.addHost.success", new Object[]{name}));
            }
        } else {
            MappedHost duplicate = this.hosts[Mapper.find(this.hosts, name)];
            if (duplicate.object == host) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("mapper.addHost.sameHost", new Object[]{name}));
                }
                newHost = duplicate;
            } else {
                log.error((Object)sm.getString("mapper.duplicateHost", new Object[]{name, duplicate.getRealHostName()}));
                return;
            }
        }
        ArrayList<MappedHost> newAliases = new ArrayList<MappedHost>(aliases.length);
        for (String alias : aliases) {
            MappedHost newAlias = new MappedHost(alias = Mapper.renameWildcardHost(alias), newHost);
            if (!this.addHostAliasImpl(newAlias)) continue;
            newAliases.add(newAlias);
        }
        newHost.addAliases(newAliases);
    }

    public synchronized void removeHost(String name) {
        MappedHost host = (MappedHost)Mapper.exactFind((MapElement[])this.hosts, (String)(name = Mapper.renameWildcardHost(name)));
        if (host == null || host.isAlias()) {
            return;
        }
        MappedHost[] newHosts = (MappedHost[])this.hosts.clone();
        int j = 0;
        for (int i = 0; i < newHosts.length; ++i) {
            if (newHosts[i].getRealHost() == host) continue;
            newHosts[j++] = newHosts[i];
        }
        this.hosts = Arrays.copyOf(newHosts, j);
    }

    public synchronized void addHostAlias(String name, String alias) {
        MappedHost realHost = (MappedHost)Mapper.exactFind((MapElement[])this.hosts, (String)name);
        if (realHost == null) {
            return;
        }
        MappedHost newAlias = new MappedHost(alias = Mapper.renameWildcardHost(alias), realHost);
        if (this.addHostAliasImpl(newAlias)) {
            realHost.addAlias(newAlias);
        }
    }

    private synchronized boolean addHostAliasImpl(MappedHost newAlias) {
        MappedHost[] newHosts = new MappedHost[this.hosts.length + 1];
        if (Mapper.insertMap(this.hosts, newHosts, newAlias)) {
            this.hosts = newHosts;
            if (newAlias.name.equals(this.defaultHostName)) {
                this.defaultHost = newAlias;
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("mapper.addHostAlias.success", new Object[]{newAlias.name, newAlias.getRealHostName()}));
            }
            return true;
        }
        MappedHost duplicate = this.hosts[Mapper.find(this.hosts, newAlias.name)];
        if (duplicate.getRealHost() == newAlias.getRealHost()) {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("mapper.addHostAlias.sameHost", new Object[]{newAlias.name, newAlias.getRealHostName()}));
            }
            return false;
        }
        log.error((Object)sm.getString("mapper.duplicateHostAlias", new Object[]{newAlias.name, newAlias.getRealHostName(), duplicate.getRealHostName()}));
        return false;
    }

    public synchronized void removeHostAlias(String alias) {
        MappedHost hostMapping = (MappedHost)Mapper.exactFind((MapElement[])this.hosts, (String)(alias = Mapper.renameWildcardHost(alias)));
        if (hostMapping == null || !hostMapping.isAlias()) {
            return;
        }
        MappedHost[] newHosts = new MappedHost[this.hosts.length - 1];
        if (Mapper.removeMap(this.hosts, newHosts, alias)) {
            this.hosts = newHosts;
            hostMapping.getRealHost().removeAlias(hostMapping);
        }
    }

    private void updateContextList(MappedHost realHost, ContextList newContextList) {
        realHost.contextList = newContextList;
        for (MappedHost alias : realHost.getAliases()) {
            alias.contextList = newContextList;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addContextVersion(String hostName, Host host, String path, String version, Context context, String[] welcomeResources, WebResourceRoot resources, Collection<WrapperMappingInfo> wrappers) {
        MappedHost mappedHost = (MappedHost)Mapper.exactFind((MapElement[])this.hosts, (String)(hostName = Mapper.renameWildcardHost(hostName)));
        if (mappedHost == null) {
            this.addHost(hostName, new String[0], host);
            mappedHost = (MappedHost)Mapper.exactFind((MapElement[])this.hosts, (String)hostName);
            if (mappedHost == null) {
                log.error((Object)sm.getString("mapper.addContext.noHost", new Object[]{hostName}));
                return;
            }
        }
        if (mappedHost.isAlias()) {
            log.error((Object)sm.getString("mapper.addContext.hostIsAlias", new Object[]{hostName}));
            return;
        }
        int slashCount = Mapper.slashCount(path);
        MappedHost mappedHost2 = mappedHost;
        synchronized (mappedHost2) {
            ContextVersion newContextVersion = new ContextVersion(version, path, slashCount, context, resources, welcomeResources);
            if (wrappers != null) {
                this.addWrappers(newContextVersion, wrappers);
            }
            ContextList contextList = mappedHost.contextList;
            MappedContext mappedContext = (MappedContext)Mapper.exactFind((MapElement[])contextList.contexts, (String)path);
            if (mappedContext == null) {
                mappedContext = new MappedContext(path, newContextVersion);
                ContextList newContextList = contextList.addContext(mappedContext, slashCount);
                if (newContextList != null) {
                    this.updateContextList(mappedHost, newContextList);
                    this.contextObjectToContextVersionMap.put(context, newContextVersion);
                }
            } else {
                ContextVersion[] contextVersions = mappedContext.versions;
                ContextVersion[] newContextVersions = new ContextVersion[contextVersions.length + 1];
                if (Mapper.insertMap(contextVersions, newContextVersions, newContextVersion)) {
                    mappedContext.versions = newContextVersions;
                    this.contextObjectToContextVersionMap.put(context, newContextVersion);
                } else {
                    int pos = Mapper.find(contextVersions, version);
                    if (pos >= 0 && contextVersions[pos].name.equals(version)) {
                        contextVersions[pos] = newContextVersion;
                        this.contextObjectToContextVersionMap.put(context, newContextVersion);
                    }
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeContextVersion(Context ctxt, String hostName, String path, String version) {
        hostName = Mapper.renameWildcardHost(hostName);
        this.contextObjectToContextVersionMap.remove(ctxt);
        MappedHost host = (MappedHost)Mapper.exactFind((MapElement[])this.hosts, (String)hostName);
        if (host == null || host.isAlias()) {
            return;
        }
        MappedHost mappedHost = host;
        synchronized (mappedHost) {
            ContextList contextList = host.contextList;
            MappedContext context = (MappedContext)Mapper.exactFind((MapElement[])contextList.contexts, (String)path);
            if (context == null) {
                return;
            }
            ContextVersion[] contextVersions = context.versions;
            ContextVersion[] newContextVersions = new ContextVersion[contextVersions.length - 1];
            if (Mapper.removeMap(contextVersions, newContextVersions, version)) {
                if (newContextVersions.length == 0) {
                    ContextList newContextList = contextList.removeContext(path);
                    if (newContextList != null) {
                        this.updateContextList(host, newContextList);
                    }
                } else {
                    context.versions = newContextVersions;
                }
            }
        }
    }

    public void pauseContextVersion(Context ctxt, String hostName, String contextPath, String version) {
        ContextVersion contextVersion = this.findContextVersion(hostName = Mapper.renameWildcardHost(hostName), contextPath, version, true);
        if (contextVersion == null || !ctxt.equals(contextVersion.object)) {
            return;
        }
        contextVersion.markPaused();
    }

    private ContextVersion findContextVersion(String hostName, String contextPath, String version, boolean silent) {
        MappedHost host = (MappedHost)Mapper.exactFind((MapElement[])this.hosts, (String)hostName);
        if (host == null || host.isAlias()) {
            if (!silent) {
                log.error((Object)sm.getString("mapper.findContext.noHostOrAlias", new Object[]{hostName}));
            }
            return null;
        }
        MappedContext context = (MappedContext)Mapper.exactFind((MapElement[])host.contextList.contexts, (String)contextPath);
        if (context == null) {
            if (!silent) {
                log.error((Object)sm.getString("mapper.findContext.noContext", new Object[]{contextPath}));
            }
            return null;
        }
        ContextVersion contextVersion = (ContextVersion)Mapper.exactFind((MapElement[])context.versions, (String)version);
        if (contextVersion == null) {
            if (!silent) {
                log.error((Object)sm.getString("mapper.findContext.noContextVersion", new Object[]{contextPath, version}));
            }
            return null;
        }
        return contextVersion;
    }

    public void addWrapper(String hostName, String contextPath, String version, String path, Wrapper wrapper, boolean jspWildCard, boolean resourceOnly) {
        ContextVersion contextVersion = this.findContextVersion(hostName = Mapper.renameWildcardHost(hostName), contextPath, version, false);
        if (contextVersion == null) {
            return;
        }
        this.addWrapper(contextVersion, path, wrapper, jspWildCard, resourceOnly);
    }

    public void addWrappers(String hostName, String contextPath, String version, Collection<WrapperMappingInfo> wrappers) {
        ContextVersion contextVersion = this.findContextVersion(hostName = Mapper.renameWildcardHost(hostName), contextPath, version, false);
        if (contextVersion == null) {
            return;
        }
        this.addWrappers(contextVersion, wrappers);
    }

    private void addWrappers(ContextVersion contextVersion, Collection<WrapperMappingInfo> wrappers) {
        for (WrapperMappingInfo wrapper : wrappers) {
            this.addWrapper(contextVersion, wrapper.getMapping(), wrapper.getWrapper(), wrapper.isJspWildCard(), wrapper.isResourceOnly());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void addWrapper(ContextVersion context, String path, Wrapper wrapper, boolean jspWildCard, boolean resourceOnly) {
        ContextVersion contextVersion = context;
        synchronized (contextVersion) {
            if (path.endsWith("/*")) {
                MappedWrapper[] oldWrappers = context.wildcardWrappers;
                MappedWrapper[] newWrappers = new MappedWrapper[oldWrappers.length + 1];
                String name = path.substring(0, path.length() - 2);
                MappedWrapper newWrapper = new MappedWrapper(name, wrapper, jspWildCard, resourceOnly);
                if (Mapper.insertMap(oldWrappers, newWrappers, newWrapper)) {
                    context.wildcardWrappers = newWrappers;
                    int slashCount = Mapper.slashCount(newWrapper.name);
                    if (slashCount > context.nesting) {
                        context.nesting = slashCount;
                    }
                }
            } else if (path.startsWith("*.")) {
                MappedWrapper[] oldWrappers = context.extensionWrappers;
                MappedWrapper[] newWrappers = new MappedWrapper[oldWrappers.length + 1];
                String name = path.substring(2);
                MappedWrapper newWrapper = new MappedWrapper(name, wrapper, jspWildCard, resourceOnly);
                if (Mapper.insertMap(oldWrappers, newWrappers, newWrapper)) {
                    context.extensionWrappers = newWrappers;
                }
            } else if (path.equals("/")) {
                MappedWrapper newWrapper;
                context.defaultWrapper = newWrapper = new MappedWrapper("", wrapper, jspWildCard, resourceOnly);
            } else {
                MappedWrapper[] oldWrappers = context.exactWrappers;
                MappedWrapper[] newWrappers = new MappedWrapper[oldWrappers.length + 1];
                String name = path.length() == 0 ? "/" : path;
                MappedWrapper newWrapper = new MappedWrapper(name, wrapper, jspWildCard, resourceOnly);
                if (Mapper.insertMap(oldWrappers, newWrappers, newWrapper)) {
                    context.exactWrappers = newWrappers;
                }
            }
        }
    }

    public void removeWrapper(String hostName, String contextPath, String version, String path) {
        ContextVersion contextVersion = this.findContextVersion(hostName = Mapper.renameWildcardHost(hostName), contextPath, version, true);
        if (contextVersion == null || contextVersion.isPaused()) {
            return;
        }
        this.removeWrapper(contextVersion, path);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void removeWrapper(ContextVersion context, String path) {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("mapper.removeWrapper", new Object[]{context.name, path}));
        }
        ContextVersion contextVersion = context;
        synchronized (contextVersion) {
            if (path.endsWith("/*")) {
                String name = path.substring(0, path.length() - 2);
                MappedWrapper[] oldWrappers = context.wildcardWrappers;
                if (oldWrappers.length == 0) {
                    return;
                }
                MappedWrapper[] newWrappers = new MappedWrapper[oldWrappers.length - 1];
                if (Mapper.removeMap(oldWrappers, newWrappers, name)) {
                    context.nesting = 0;
                    for (MappedWrapper newWrapper : newWrappers) {
                        int slashCount = Mapper.slashCount(newWrapper.name);
                        if (slashCount <= context.nesting) continue;
                        context.nesting = slashCount;
                    }
                    context.wildcardWrappers = newWrappers;
                }
            } else if (path.startsWith("*.")) {
                String name = path.substring(2);
                MappedWrapper[] oldWrappers = context.extensionWrappers;
                if (oldWrappers.length == 0) {
                    return;
                }
                MappedWrapper[] newWrappers = new MappedWrapper[oldWrappers.length - 1];
                if (Mapper.removeMap(oldWrappers, newWrappers, name)) {
                    context.extensionWrappers = newWrappers;
                }
            } else if (path.equals("/")) {
                context.defaultWrapper = null;
            } else {
                String name = path.length() == 0 ? "/" : path;
                MappedWrapper[] oldWrappers = context.exactWrappers;
                if (oldWrappers.length == 0) {
                    return;
                }
                MappedWrapper[] newWrappers = new MappedWrapper[oldWrappers.length - 1];
                if (Mapper.removeMap(oldWrappers, newWrappers, name)) {
                    context.exactWrappers = newWrappers;
                }
            }
        }
    }

    public void addWelcomeFile(String hostName, String contextPath, String version, String welcomeFile) {
        ContextVersion contextVersion = this.findContextVersion(hostName = Mapper.renameWildcardHost(hostName), contextPath, version, false);
        if (contextVersion == null) {
            return;
        }
        int len = contextVersion.welcomeResources.length + 1;
        String[] newWelcomeResources = new String[len];
        System.arraycopy(contextVersion.welcomeResources, 0, newWelcomeResources, 0, len - 1);
        newWelcomeResources[len - 1] = welcomeFile;
        contextVersion.welcomeResources = newWelcomeResources;
    }

    public void removeWelcomeFile(String hostName, String contextPath, String version, String welcomeFile) {
        ContextVersion contextVersion = this.findContextVersion(hostName = Mapper.renameWildcardHost(hostName), contextPath, version, false);
        if (contextVersion == null || contextVersion.isPaused()) {
            return;
        }
        int match = -1;
        for (int i = 0; i < contextVersion.welcomeResources.length; ++i) {
            if (!welcomeFile.equals(contextVersion.welcomeResources[i])) continue;
            match = i;
            break;
        }
        if (match > -1) {
            int len = contextVersion.welcomeResources.length - 1;
            String[] newWelcomeResources = new String[len];
            System.arraycopy(contextVersion.welcomeResources, 0, newWelcomeResources, 0, match);
            if (match < len) {
                System.arraycopy(contextVersion.welcomeResources, match + 1, newWelcomeResources, match, len - match);
            }
            contextVersion.welcomeResources = newWelcomeResources;
        }
    }

    public void clearWelcomeFiles(String hostName, String contextPath, String version) {
        ContextVersion contextVersion = this.findContextVersion(hostName = Mapper.renameWildcardHost(hostName), contextPath, version, false);
        if (contextVersion == null) {
            return;
        }
        contextVersion.welcomeResources = new String[0];
    }

    public void map(MessageBytes host, MessageBytes uri, String version, MappingData mappingData) throws IOException {
        if (host.isNull()) {
            String defaultHostName = this.defaultHostName;
            if (defaultHostName == null) {
                return;
            }
            host.setChars(MessageBytes.EMPTY_CHAR_ARRAY, 0, 0);
            host.getCharChunk().append(defaultHostName);
        }
        host.toChars();
        uri.toChars();
        this.internalMap(host.getCharChunk(), uri.getCharChunk(), version, mappingData);
    }

    public void map(Context context, MessageBytes uri, MappingData mappingData) throws IOException {
        ContextVersion contextVersion = this.contextObjectToContextVersionMap.get(context);
        uri.toChars();
        CharChunk uricc = uri.getCharChunk();
        uricc.setLimit(-1);
        this.internalMapWrapper(contextVersion, uricc, mappingData);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void internalMap(CharChunk host, CharChunk uri, String version, MappingData mappingData) throws IOException {
        if (mappingData.host != null) {
            throw new AssertionError();
        }
        MapElement[] hosts = this.hosts;
        MappedHost mappedHost = (MappedHost)Mapper.exactFindIgnoreCase((MapElement[])hosts, (CharChunk)host);
        if (mappedHost == null) {
            int firstDot = host.indexOf('.');
            if (firstDot > -1) {
                int offset = host.getOffset();
                try {
                    host.setOffset(firstDot + offset);
                    mappedHost = (MappedHost)Mapper.exactFindIgnoreCase((MapElement[])hosts, (CharChunk)host);
                }
                finally {
                    host.setOffset(offset);
                }
            }
            if (mappedHost == null && (mappedHost = this.defaultHost) == null) {
                return;
            }
        }
        mappingData.host = (Host)mappedHost.object;
        if (uri.isNull()) {
            return;
        }
        uri.setLimit(-1);
        ContextList contextList = mappedHost.contextList;
        MappedContext[] contexts = contextList.contexts;
        int pos = Mapper.find(contexts, uri);
        if (pos == -1) {
            return;
        }
        int lastSlash = -1;
        int uriEnd = uri.getEnd();
        int length = -1;
        boolean found = false;
        MappedContext context = null;
        while (pos >= 0) {
            context = contexts[pos];
            if (uri.startsWith(context.name)) {
                length = context.name.length();
                if (uri.getLength() == length) {
                    found = true;
                    break;
                }
                if (uri.startsWithIgnoreCase("/", length)) {
                    found = true;
                    break;
                }
            }
            lastSlash = lastSlash == -1 ? Mapper.nthSlash(uri, contextList.nesting + 1) : Mapper.lastSlash(uri);
            uri.setEnd(lastSlash);
            pos = Mapper.find(contexts, uri);
        }
        uri.setEnd(uriEnd);
        if (!found) {
            context = contexts[0].name.equals("") ? contexts[0] : null;
        }
        if (context == null) {
            return;
        }
        mappingData.contextPath.setString(context.name);
        ContextVersion contextVersion = null;
        MapElement[] contextVersions = context.versions;
        int versionCount = contextVersions.length;
        if (versionCount > 1) {
            Context[] contextObjects = new Context[contextVersions.length];
            for (int i = 0; i < contextObjects.length; ++i) {
                contextObjects[i] = (Context)contextVersions[i].object;
            }
            mappingData.contexts = contextObjects;
            if (version != null) {
                contextVersion = (ContextVersion)Mapper.exactFind((MapElement[])contextVersions, (String)version);
            }
        }
        if (contextVersion == null) {
            contextVersion = contextVersions[versionCount - 1];
        }
        mappingData.context = (Context)contextVersion.object;
        mappingData.contextSlashCount = contextVersion.slashCount;
        if (!contextVersion.isPaused()) {
            this.internalMapWrapper(contextVersion, uri, mappingData);
        }
    }

    private void internalMapWrapper(ContextVersion contextVersion, CharChunk path, MappingData mappingData) throws IOException {
        char[] buf;
        boolean checkWelcomeFiles;
        int pathOffset = path.getOffset();
        int pathEnd = path.getEnd();
        boolean noServletPath = false;
        int length = contextVersion.path.length();
        if (length == pathEnd - pathOffset) {
            noServletPath = true;
        }
        int servletPath = pathOffset + length;
        path.setOffset(servletPath);
        MappedWrapper[] exactWrappers = contextVersion.exactWrappers;
        this.internalMapExactWrapper(exactWrappers, path, mappingData);
        boolean checkJspWelcomeFiles = false;
        MappedWrapper[] wildcardWrappers = contextVersion.wildcardWrappers;
        if (mappingData.wrapper == null) {
            this.internalMapWildcardWrapper(wildcardWrappers, contextVersion.nesting, path, mappingData);
            if (mappingData.wrapper != null && mappingData.jspWildCard) {
                char[] buf2 = path.getBuffer();
                if (buf2[pathEnd - 1] == '/') {
                    mappingData.wrapper = null;
                    checkJspWelcomeFiles = true;
                } else {
                    mappingData.wrapperPath.setChars(buf2, path.getStart(), path.getLength());
                    mappingData.pathInfo.recycle();
                }
            }
        }
        if (mappingData.wrapper == null && noServletPath && ((Context)contextVersion.object).getMapperContextRootRedirectEnabled()) {
            path.append('/');
            pathEnd = path.getEnd();
            mappingData.redirectPath.setChars(path.getBuffer(), pathOffset, pathEnd - pathOffset);
            path.setEnd(pathEnd - 1);
            return;
        }
        MappedWrapper[] extensionWrappers = contextVersion.extensionWrappers;
        if (mappingData.wrapper == null && !checkJspWelcomeFiles) {
            this.internalMapExtensionWrapper(extensionWrappers, path, mappingData, true);
        }
        if (mappingData.wrapper == null) {
            checkWelcomeFiles = checkJspWelcomeFiles;
            if (!checkWelcomeFiles) {
                buf = path.getBuffer();
                boolean bl = checkWelcomeFiles = buf[pathEnd - 1] == '/';
            }
            if (checkWelcomeFiles) {
                for (int i = 0; i < contextVersion.welcomeResources.length && mappingData.wrapper == null; ++i) {
                    String pathStr;
                    WebResource file;
                    path.setOffset(pathOffset);
                    path.setEnd(pathEnd);
                    path.append(contextVersion.welcomeResources[i], 0, contextVersion.welcomeResources[i].length());
                    path.setOffset(servletPath);
                    this.internalMapExactWrapper(exactWrappers, path, mappingData);
                    if (mappingData.wrapper == null) {
                        this.internalMapWildcardWrapper(wildcardWrappers, contextVersion.nesting, path, mappingData);
                    }
                    if (mappingData.wrapper != null || contextVersion.resources == null || (file = contextVersion.resources.getResource(pathStr = path.toString())) == null || !file.isFile()) continue;
                    this.internalMapExtensionWrapper(extensionWrappers, path, mappingData, true);
                    if (mappingData.wrapper != null || contextVersion.defaultWrapper == null) continue;
                    mappingData.wrapper = (Wrapper)contextVersion.defaultWrapper.object;
                    mappingData.requestPath.setChars(path.getBuffer(), path.getStart(), path.getLength());
                    mappingData.wrapperPath.setChars(path.getBuffer(), path.getStart(), path.getLength());
                    mappingData.requestPath.setString(pathStr);
                    mappingData.wrapperPath.setString(pathStr);
                }
                path.setOffset(servletPath);
                path.setEnd(pathEnd);
            }
        }
        if (mappingData.wrapper == null) {
            checkWelcomeFiles = checkJspWelcomeFiles;
            if (!checkWelcomeFiles) {
                buf = path.getBuffer();
                boolean bl = checkWelcomeFiles = buf[pathEnd - 1] == '/';
            }
            if (checkWelcomeFiles) {
                for (int i = 0; i < contextVersion.welcomeResources.length && mappingData.wrapper == null; ++i) {
                    path.setOffset(pathOffset);
                    path.setEnd(pathEnd);
                    path.append(contextVersion.welcomeResources[i], 0, contextVersion.welcomeResources[i].length());
                    path.setOffset(servletPath);
                    this.internalMapExtensionWrapper(extensionWrappers, path, mappingData, false);
                }
                path.setOffset(servletPath);
                path.setEnd(pathEnd);
            }
        }
        if (mappingData.wrapper == null && !checkJspWelcomeFiles) {
            if (contextVersion.defaultWrapper != null) {
                mappingData.wrapper = (Wrapper)contextVersion.defaultWrapper.object;
                mappingData.requestPath.setChars(path.getBuffer(), path.getStart(), path.getLength());
                mappingData.wrapperPath.setChars(path.getBuffer(), path.getStart(), path.getLength());
                mappingData.matchType = MappingMatch.DEFAULT;
            }
            char[] buf3 = path.getBuffer();
            if (contextVersion.resources != null && buf3[pathEnd - 1] != '/') {
                String pathStr = path.toString();
                if (((Context)contextVersion.object).getMapperDirectoryRedirectEnabled()) {
                    WebResource file = pathStr.length() == 0 ? contextVersion.resources.getResource("/") : contextVersion.resources.getResource(pathStr);
                    if (file != null && file.isDirectory()) {
                        path.setOffset(pathOffset);
                        path.append('/');
                        mappingData.redirectPath.setChars(path.getBuffer(), path.getStart(), path.getLength());
                    } else {
                        mappingData.requestPath.setString(pathStr);
                        mappingData.wrapperPath.setString(pathStr);
                    }
                } else {
                    mappingData.requestPath.setString(pathStr);
                    mappingData.wrapperPath.setString(pathStr);
                }
            }
        }
        path.setOffset(pathOffset);
        path.setEnd(pathEnd);
    }

    private void internalMapExactWrapper(MappedWrapper[] wrappers, CharChunk path, MappingData mappingData) {
        MappedWrapper wrapper = (MappedWrapper)Mapper.exactFind((MapElement[])wrappers, (CharChunk)path);
        if (wrapper != null) {
            mappingData.requestPath.setString(wrapper.name);
            mappingData.wrapper = (Wrapper)wrapper.object;
            if (path.equals("/")) {
                mappingData.pathInfo.setString("/");
                mappingData.wrapperPath.setString("");
                mappingData.contextPath.setString("");
                mappingData.matchType = MappingMatch.CONTEXT_ROOT;
            } else {
                mappingData.wrapperPath.setString(wrapper.name);
                mappingData.matchType = MappingMatch.EXACT;
            }
        }
    }

    private void internalMapWildcardWrapper(MappedWrapper[] wrappers, int nesting, CharChunk path, MappingData mappingData) {
        int pathEnd = path.getEnd();
        int lastSlash = -1;
        int length = -1;
        int pos = Mapper.find(wrappers, path);
        if (pos != -1) {
            boolean found = false;
            while (pos >= 0) {
                if (path.startsWith(wrappers[pos].name)) {
                    length = wrappers[pos].name.length();
                    if (path.getLength() == length) {
                        found = true;
                        break;
                    }
                    if (path.startsWithIgnoreCase("/", length)) {
                        found = true;
                        break;
                    }
                }
                lastSlash = lastSlash == -1 ? Mapper.nthSlash(path, nesting + 1) : Mapper.lastSlash(path);
                path.setEnd(lastSlash);
                pos = Mapper.find(wrappers, path);
            }
            path.setEnd(pathEnd);
            if (found) {
                mappingData.wrapperPath.setString(wrappers[pos].name);
                if (path.getLength() > length) {
                    mappingData.pathInfo.setChars(path.getBuffer(), path.getOffset() + length, path.getLength() - length);
                }
                mappingData.requestPath.setChars(path.getBuffer(), path.getOffset(), path.getLength());
                mappingData.wrapper = (Wrapper)wrappers[pos].object;
                mappingData.jspWildCard = wrappers[pos].jspWildCard;
                mappingData.matchType = MappingMatch.PATH;
            }
        }
    }

    private void internalMapExtensionWrapper(MappedWrapper[] wrappers, CharChunk path, MappingData mappingData, boolean resourceExpected) {
        char[] buf = path.getBuffer();
        int pathEnd = path.getEnd();
        int servletPath = path.getOffset();
        int slash = -1;
        for (int i = pathEnd - 1; i >= servletPath; --i) {
            if (buf[i] != '/') continue;
            slash = i;
            break;
        }
        if (slash >= 0) {
            int period = -1;
            for (int i = pathEnd - 1; i > slash; --i) {
                if (buf[i] != '.') continue;
                period = i;
                break;
            }
            if (period >= 0) {
                path.setOffset(period + 1);
                path.setEnd(pathEnd);
                MappedWrapper wrapper = (MappedWrapper)Mapper.exactFind((MapElement[])wrappers, (CharChunk)path);
                if (wrapper != null && (resourceExpected || !wrapper.resourceOnly)) {
                    mappingData.wrapperPath.setChars(buf, servletPath, pathEnd - servletPath);
                    mappingData.requestPath.setChars(buf, servletPath, pathEnd - servletPath);
                    mappingData.wrapper = (Wrapper)wrapper.object;
                    mappingData.matchType = MappingMatch.EXTENSION;
                }
                path.setOffset(servletPath);
                path.setEnd(pathEnd);
            }
        }
    }

    private static <T> int find(MapElement<T>[] map, CharChunk name) {
        return Mapper.find(map, name, name.getStart(), name.getEnd());
    }

    private static <T> int find(MapElement<T>[] map, CharChunk name, int start, int end) {
        int a = 0;
        int b = map.length - 1;
        if (b == -1) {
            return -1;
        }
        if (Mapper.compare(name, start, end, map[0].name) < 0) {
            return -1;
        }
        if (b == 0) {
            return 0;
        }
        int i = 0;
        do {
            i = b + a >>> 1;
            int result = Mapper.compare(name, start, end, map[i].name);
            if (result == 1) {
                a = i;
                continue;
            }
            if (result == 0) {
                return i;
            }
            b = i;
        } while (b - a != 1);
        int result2 = Mapper.compare(name, start, end, map[b].name);
        if (result2 < 0) {
            return a;
        }
        return b;
    }

    private static <T> int findIgnoreCase(MapElement<T>[] map, CharChunk name) {
        return Mapper.findIgnoreCase(map, name, name.getStart(), name.getEnd());
    }

    private static <T> int findIgnoreCase(MapElement<T>[] map, CharChunk name, int start, int end) {
        int a = 0;
        int b = map.length - 1;
        if (b == -1) {
            return -1;
        }
        if (Mapper.compareIgnoreCase(name, start, end, map[0].name) < 0) {
            return -1;
        }
        if (b == 0) {
            return 0;
        }
        int i = 0;
        do {
            i = b + a >>> 1;
            int result = Mapper.compareIgnoreCase(name, start, end, map[i].name);
            if (result == 1) {
                a = i;
                continue;
            }
            if (result == 0) {
                return i;
            }
            b = i;
        } while (b - a != 1);
        int result2 = Mapper.compareIgnoreCase(name, start, end, map[b].name);
        if (result2 < 0) {
            return a;
        }
        return b;
    }

    private static <T> int find(MapElement<T>[] map, String name) {
        int a = 0;
        int b = map.length - 1;
        if (b == -1) {
            return -1;
        }
        if (name.compareTo(map[0].name) < 0) {
            return -1;
        }
        if (b == 0) {
            return 0;
        }
        int i = 0;
        do {
            i = b + a >>> 1;
            int result = name.compareTo(map[i].name);
            if (result > 0) {
                a = i;
                continue;
            }
            if (result == 0) {
                return i;
            }
            b = i;
        } while (b - a != 1);
        int result2 = name.compareTo(map[b].name);
        if (result2 < 0) {
            return a;
        }
        return b;
    }

    private static <T, E extends MapElement<T>> E exactFind(E[] map, String name) {
        int pos = Mapper.find(map, name);
        if (pos >= 0) {
            E result = map[pos];
            if (name.equals(((MapElement)result).name)) {
                return result;
            }
        }
        return null;
    }

    private static <T, E extends MapElement<T>> E exactFind(E[] map, CharChunk name) {
        int pos = Mapper.find(map, name);
        if (pos >= 0) {
            E result = map[pos];
            if (name.equals(((MapElement)result).name)) {
                return result;
            }
        }
        return null;
    }

    private static <T, E extends MapElement<T>> E exactFindIgnoreCase(E[] map, CharChunk name) {
        int pos = Mapper.findIgnoreCase(map, name);
        if (pos >= 0) {
            E result = map[pos];
            if (name.equalsIgnoreCase(((MapElement)result).name)) {
                return result;
            }
        }
        return null;
    }

    private static int compare(CharChunk name, int start, int end, String compareTo) {
        int result = 0;
        char[] c = name.getBuffer();
        int compareLen = compareTo.length();
        int len = compareLen;
        if (end - start < len) {
            len = end - start;
        }
        for (int i = 0; i < len && result == 0; ++i) {
            char nameChar = c[i + start];
            char compareToChar = compareTo.charAt(i);
            if (nameChar > compareToChar) {
                result = 1;
                continue;
            }
            if (nameChar >= compareToChar) continue;
            result = -1;
        }
        if (result == 0) {
            if (compareLen > end - start) {
                result = -1;
            } else if (compareLen < end - start) {
                result = 1;
            }
        }
        return result;
    }

    private static int compareIgnoreCase(CharChunk name, int start, int end, String compareTo) {
        int result = 0;
        char[] c = name.getBuffer();
        int compareLen = compareTo.length();
        int len = compareLen;
        if (end - start < len) {
            len = end - start;
        }
        for (int i = 0; i < len && result == 0; ++i) {
            int compareLower;
            int nameLower = Ascii.toLower((int)c[i + start]);
            if (nameLower > (compareLower = Ascii.toLower((int)compareTo.charAt(i)))) {
                result = 1;
                continue;
            }
            if (nameLower >= compareLower) continue;
            result = -1;
        }
        if (result == 0) {
            if (compareLen > end - start) {
                result = -1;
            } else if (compareLen < end - start) {
                result = 1;
            }
        }
        return result;
    }

    private static int lastSlash(CharChunk name) {
        char[] c = name.getBuffer();
        int end = name.getEnd();
        int start = name.getStart();
        int pos = end;
        while (pos > start && c[--pos] != '/') {
        }
        return pos;
    }

    private static int nthSlash(CharChunk name, int n) {
        int start;
        char[] c = name.getBuffer();
        int end = name.getEnd();
        int pos = start = name.getStart();
        int count = 0;
        while (pos < end) {
            if (c[pos++] != '/' || ++count != n) continue;
            --pos;
            break;
        }
        return pos;
    }

    private static int slashCount(String name) {
        int pos = -1;
        int count = 0;
        while ((pos = name.indexOf(47, pos + 1)) != -1) {
            ++count;
        }
        return count;
    }

    private static <T> boolean insertMap(MapElement<T>[] oldMap, MapElement<T>[] newMap, MapElement<T> newElement) {
        int pos = Mapper.find(oldMap, newElement.name);
        if (pos != -1 && newElement.name.equals(oldMap[pos].name)) {
            return false;
        }
        System.arraycopy(oldMap, 0, newMap, 0, pos + 1);
        newMap[pos + 1] = newElement;
        System.arraycopy(oldMap, pos + 1, newMap, pos + 2, oldMap.length - pos - 1);
        return true;
    }

    private static <T> boolean removeMap(MapElement<T>[] oldMap, MapElement<T>[] newMap, String name) {
        int pos = Mapper.find(oldMap, name);
        if (pos != -1 && name.equals(oldMap[pos].name)) {
            System.arraycopy(oldMap, 0, newMap, 0, pos);
            System.arraycopy(oldMap, pos + 1, newMap, pos, oldMap.length - pos - 1);
            return true;
        }
        return false;
    }

    private static String renameWildcardHost(String hostName) {
        if (hostName != null && hostName.startsWith("*.")) {
            return hostName.substring(1);
        }
        return hostName;
    }

    protected static abstract class MapElement<T> {
        public final String name;
        public final T object;

        public MapElement(String name, T object) {
            this.name = name;
            this.object = object;
        }
    }

    protected static final class MappedHost
    extends MapElement<Host> {
        public volatile ContextList contextList;
        private final MappedHost realHost;
        private final List<MappedHost> aliases;

        public MappedHost(String name, Host host) {
            super(name, host);
            this.realHost = this;
            this.contextList = new ContextList();
            this.aliases = new CopyOnWriteArrayList<MappedHost>();
        }

        public MappedHost(String alias, MappedHost realHost) {
            super(alias, (Host)realHost.object);
            this.realHost = realHost;
            this.contextList = realHost.contextList;
            this.aliases = null;
        }

        public boolean isAlias() {
            return this.realHost != this;
        }

        public MappedHost getRealHost() {
            return this.realHost;
        }

        public String getRealHostName() {
            return this.realHost.name;
        }

        public Collection<MappedHost> getAliases() {
            return this.aliases;
        }

        public void addAlias(MappedHost alias) {
            this.aliases.add(alias);
        }

        public void addAliases(Collection<? extends MappedHost> c) {
            this.aliases.addAll(c);
        }

        public void removeAlias(MappedHost alias) {
            this.aliases.remove(alias);
        }
    }

    protected static final class ContextList {
        public final MappedContext[] contexts;
        public final int nesting;

        public ContextList() {
            this(new MappedContext[0], 0);
        }

        private ContextList(MappedContext[] contexts, int nesting) {
            this.contexts = contexts;
            this.nesting = nesting;
        }

        public ContextList addContext(MappedContext mappedContext, int slashCount) {
            MapElement[] newContexts = new MappedContext[this.contexts.length + 1];
            if (Mapper.insertMap(this.contexts, newContexts, mappedContext)) {
                return new ContextList((MappedContext[])newContexts, Math.max(this.nesting, slashCount));
            }
            return null;
        }

        public ContextList removeContext(String path) {
            MapElement[] newContexts = new MappedContext[this.contexts.length - 1];
            if (Mapper.removeMap(this.contexts, newContexts, path)) {
                int newNesting = 0;
                for (MapElement context : newContexts) {
                    newNesting = Math.max(newNesting, Mapper.slashCount(((MappedContext)context).name));
                }
                return new ContextList((MappedContext[])newContexts, newNesting);
            }
            return null;
        }
    }

    protected static final class ContextVersion
    extends MapElement<Context> {
        public final String path;
        public final int slashCount;
        public final WebResourceRoot resources;
        public String[] welcomeResources;
        public MappedWrapper defaultWrapper = null;
        public MappedWrapper[] exactWrappers = new MappedWrapper[0];
        public MappedWrapper[] wildcardWrappers = new MappedWrapper[0];
        public MappedWrapper[] extensionWrappers = new MappedWrapper[0];
        public int nesting = 0;
        private volatile boolean paused;

        public ContextVersion(String version, String path, int slashCount, Context context, WebResourceRoot resources, String[] welcomeResources) {
            super(version, context);
            this.path = path;
            this.slashCount = slashCount;
            this.resources = resources;
            this.welcomeResources = welcomeResources;
        }

        public boolean isPaused() {
            return this.paused;
        }

        public void markPaused() {
            this.paused = true;
        }
    }

    protected static final class MappedContext
    extends MapElement<Void> {
        public volatile ContextVersion[] versions;

        public MappedContext(String name, ContextVersion firstVersion) {
            super(name, null);
            this.versions = new ContextVersion[]{firstVersion};
        }
    }

    protected static class MappedWrapper
    extends MapElement<Wrapper> {
        public final boolean jspWildCard;
        public final boolean resourceOnly;

        public MappedWrapper(String name, Wrapper wrapper, boolean jspWildCard, boolean resourceOnly) {
            super(name, wrapper);
            this.jspWildCard = jspWildCard;
            this.resourceOnly = resourceOnly;
        }
    }
}

