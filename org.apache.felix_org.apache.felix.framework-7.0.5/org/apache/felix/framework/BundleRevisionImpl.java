/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import org.apache.felix.framework.BundleImpl;
import org.apache.felix.framework.BundleWiringImpl;
import org.apache.felix.framework.cache.Content;
import org.apache.felix.framework.util.MultiReleaseContent;
import org.apache.felix.framework.util.SecureAction;
import org.apache.felix.framework.util.Util;
import org.apache.felix.framework.util.manifestparser.ManifestParser;
import org.apache.felix.framework.util.manifestparser.NativeLibrary;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;

public class BundleRevisionImpl
implements BundleRevision,
Resource {
    public static final int EAGER_ACTIVATION = 0;
    public static final int LAZY_ACTIVATION = 1;
    private final String m_id;
    private final Map<String, Object> m_headerMap;
    private final String m_manifestVersion;
    private final boolean m_isExtension;
    private final boolean m_isFragment;
    private final String m_symbolicName;
    private final Version m_version;
    private final List<BundleCapability> m_declaredCaps;
    private final List<BundleRequirement> m_declaredReqs;
    private final List<NativeLibrary> m_declaredNativeLibs;
    private final int m_declaredActivationPolicy;
    private final List<String> m_activationIncludes;
    private final List<String> m_activationExcludes;
    private final BundleImpl m_bundle;
    private volatile Content m_content;
    private volatile List<Content> m_contentPath;
    private volatile ProtectionDomain m_protectionDomain = null;
    private static final SecureAction m_secureAction = new SecureAction();
    private volatile BundleWiringImpl m_wiring = null;

    public BundleRevisionImpl(BundleImpl bundle, String id) {
        this.m_bundle = bundle;
        this.m_id = id;
        this.m_headerMap = null;
        this.m_content = null;
        this.m_manifestVersion = "";
        this.m_symbolicName = null;
        this.m_isExtension = false;
        this.m_isFragment = false;
        this.m_version = null;
        this.m_declaredCaps = Collections.emptyList();
        this.m_declaredReqs = Collections.emptyList();
        this.m_declaredNativeLibs = null;
        this.m_declaredActivationPolicy = 0;
        this.m_activationExcludes = null;
        this.m_activationIncludes = null;
    }

    BundleRevisionImpl(BundleImpl bundle, String id, Map<String, Object> headerMap, Content content) throws BundleException {
        this.m_bundle = bundle;
        this.m_id = id;
        this.m_headerMap = headerMap;
        this.m_content = content;
        ManifestParser mp = new ManifestParser(bundle.getFramework().getLogger(), bundle.getFramework().getConfig(), this, this.m_headerMap);
        this.m_isExtension = mp.isExtension();
        this.m_manifestVersion = mp.getManifestVersion();
        this.m_version = mp.getBundleVersion();
        this.m_declaredCaps = mp.getCapabilities();
        this.m_declaredReqs = mp.getRequirements();
        this.m_declaredNativeLibs = mp.getLibraries();
        this.m_declaredActivationPolicy = mp.getActivationPolicy();
        this.m_activationExcludes = mp.getActivationExcludeDirective() == null ? null : ManifestParser.parseDelimitedString(mp.getActivationExcludeDirective(), ",");
        this.m_activationIncludes = mp.getActivationIncludeDirective() == null ? null : ManifestParser.parseDelimitedString(mp.getActivationIncludeDirective(), ",");
        this.m_symbolicName = mp.getSymbolicName();
        this.m_isFragment = this.m_headerMap.containsKey("Fragment-Host");
    }

    static SecureAction getSecureAction() {
        return m_secureAction;
    }

    int getDeclaredActivationPolicy() {
        return this.m_declaredActivationPolicy;
    }

    boolean isActivationTrigger(String pkgName) {
        if (this.m_activationIncludes == null && this.m_activationExcludes == null) {
            return true;
        }
        boolean included = this.m_activationIncludes == null;
        for (int i = 0; !included && this.m_activationIncludes != null && i < this.m_activationIncludes.size(); ++i) {
            included = this.m_activationIncludes.get(i).equals(pkgName);
        }
        boolean excluded = false;
        for (int i = 0; !excluded && this.m_activationExcludes != null && i < this.m_activationExcludes.size(); ++i) {
            excluded = this.m_activationExcludes.get(i).equals(pkgName);
        }
        return included && !excluded;
    }

    @Override
    public String getSymbolicName() {
        return this.m_symbolicName;
    }

    @Override
    public Version getVersion() {
        return this.m_version;
    }

    @Override
    public List<Capability> getCapabilities(String namespace) {
        return BundleRevisionImpl.asCapabilityList(this.getDeclaredCapabilities(namespace));
    }

    static List<Capability> asCapabilityList(List reqs) {
        return reqs;
    }

    @Override
    public List<BundleCapability> getDeclaredCapabilities(String namespace) {
        List<BundleCapability> result = this.m_declaredCaps;
        if (namespace != null) {
            result = new ArrayList<BundleCapability>();
            for (BundleCapability cap : this.m_declaredCaps) {
                if (!cap.getNamespace().equals(namespace)) continue;
                result.add(cap);
            }
        }
        return result;
    }

    @Override
    public List<Requirement> getRequirements(String namespace) {
        return BundleRevisionImpl.asRequirementList(this.getDeclaredRequirements(namespace));
    }

    static List<Requirement> asRequirementList(List reqs) {
        return reqs;
    }

    @Override
    public List<BundleRequirement> getDeclaredRequirements(String namespace) {
        List<BundleRequirement> result = this.m_declaredReqs;
        if (namespace != null) {
            result = new ArrayList<BundleRequirement>();
            for (BundleRequirement req : this.m_declaredReqs) {
                if (!req.getNamespace().equals(namespace)) continue;
                result.add(req);
            }
        }
        return result;
    }

    @Override
    public int getTypes() {
        return this.getManifestVersion().equals("2") && this.m_isFragment ? 1 : 0;
    }

    @Override
    public BundleWiring getWiring() {
        return this.m_wiring;
    }

    @Override
    public BundleImpl getBundle() {
        return this.m_bundle;
    }

    public Map<String, Object> getHeaders() {
        return this.m_headerMap;
    }

    public boolean isExtension() {
        return this.m_isExtension;
    }

    public String getManifestVersion() {
        return this.m_manifestVersion;
    }

    public List<NativeLibrary> getDeclaredNativeLibraries() {
        return this.m_declaredNativeLibs;
    }

    public String getId() {
        return this.m_id;
    }

    public synchronized void resolve(BundleWiringImpl wiring) {
        if (this.m_wiring != null) {
            this.m_wiring.dispose();
            this.m_wiring = null;
        }
        if (wiring != null) {
            if (!Util.getFragments(wiring).isEmpty()) {
                for (int i = 0; this.m_contentPath != null && i < this.m_contentPath.size(); ++i) {
                    if (this.m_content == this.m_contentPath.get(i)) continue;
                    this.m_contentPath.get(i).close();
                }
                this.m_contentPath = null;
            }
            this.m_wiring = wiring;
        }
    }

    synchronized void disposeContentPath() {
        for (int i = 0; this.m_contentPath != null && i < this.m_contentPath.size(); ++i) {
            this.m_contentPath.get(i).close();
        }
        this.m_contentPath = null;
    }

    public void setProtectionDomain(ProtectionDomain pd) {
        this.m_protectionDomain = pd;
    }

    public ProtectionDomain getProtectionDomain() {
        return this.m_protectionDomain;
    }

    public Content getContent() {
        return this.m_content;
    }

    synchronized void resetContent(Content content) {
        this.m_content = content;
    }

    List<Content> getContentPath() {
        List<Content> contentPath = this.m_contentPath;
        if (contentPath == null) {
            try {
                contentPath = this.initializeContentPath();
            }
            catch (Exception ex) {
                this.m_bundle.getFramework().getLogger().log(this.m_bundle, 1, "Unable to get module class path.", (Throwable)ex);
            }
        }
        return contentPath;
    }

    private synchronized List<Content> initializeContentPath() throws Exception {
        if (this.m_contentPath != null) {
            return this.m_contentPath;
        }
        ArrayList<Content> contentList = new ArrayList<Content>();
        this.calculateContentPath(this, this.getContent(), contentList, true);
        List<BundleRevision> fragments = null;
        List<Content> fragmentContents = null;
        if (this.m_wiring != null) {
            fragments = this.m_wiring.getFragments();
            fragmentContents = this.m_wiring.getFragmentContents();
        }
        if (fragments != null) {
            for (int i = 0; i < fragments.size(); ++i) {
                this.calculateContentPath(fragments.get(i), fragmentContents.get(i), contentList, false);
            }
        }
        this.m_contentPath = contentList;
        return this.m_contentPath;
    }

    private List calculateContentPath(BundleRevision revision, Content content, List<Content> contentList, boolean searchFragments) {
        ArrayList<Content> localContentList = new ArrayList<Content>();
        String classPath = (String)((BundleRevisionImpl)revision).getHeaders().get("Bundle-ClassPath");
        List<String> classPathStrings = ManifestParser.parseDelimitedString(classPath, ",");
        if (classPathStrings == null) {
            classPathStrings = new ArrayList<String>(0);
        }
        for (int i = 0; i < classPathStrings.size(); ++i) {
            classPathStrings.set(i, classPathStrings.get(i).startsWith("/") ? classPathStrings.get(i).substring(1) : classPathStrings.get(i));
            if (classPathStrings.get(i).equals(".")) {
                localContentList.add(MultiReleaseContent.wrap(this.getBundle().getFramework()._getProperty("java.specification.version"), content));
                continue;
            }
            Content embeddedContent = content.getEntryAsContent(classPathStrings.get(i));
            List<Content> fragmentContents = this.m_wiring == null ? null : this.m_wiring.getFragmentContents();
            for (int fragIdx = 0; searchFragments && embeddedContent == null && fragmentContents != null && fragIdx < fragmentContents.size(); ++fragIdx) {
                embeddedContent = fragmentContents.get(fragIdx).getEntryAsContent(classPathStrings.get(i));
            }
            if (embeddedContent != null) {
                localContentList.add(MultiReleaseContent.wrap(this.getBundle().getFramework()._getProperty("java.specification.version"), embeddedContent));
                continue;
            }
            this.m_bundle.getFramework().getLogger().log(this.getBundle(), 3, "Class path entry not found: " + classPathStrings.get(i));
        }
        if (localContentList.isEmpty()) {
            localContentList.add(MultiReleaseContent.wrap(this.getBundle().getFramework()._getProperty("java.specification.version"), content));
        }
        contentList.addAll(localContentList);
        return contentList;
    }

    URL getResourceLocal(String name) {
        URL url = null;
        if (name.equals("/")) {
            url = this.createURL(1, name);
        } else if (name.startsWith("/")) {
            name = name.substring(1);
        }
        List<Content> contentPath = this.getContentPath();
        for (int i = 0; url == null && i < contentPath.size(); ++i) {
            if (!contentPath.get(i).hasEntry(name)) continue;
            if (!name.endsWith("/") && contentPath.get(i).isDirectory(name)) {
                name = name + "/";
            }
            url = this.createURL(i + 1, name);
        }
        return url;
    }

    Enumeration getResourcesLocal(String name) {
        ArrayList<URL> l = new ArrayList<URL>();
        List<Content> contentPath = this.getContentPath();
        if (contentPath == null) {
            return Collections.enumeration(Collections.emptyList());
        }
        if (name.equals("/")) {
            for (int i = 0; i < contentPath.size(); ++i) {
                l.add(this.createURL(i + 1, name));
            }
        } else {
            if (name.startsWith("/")) {
                name = name.substring(1);
            }
            for (int i = 0; i < contentPath.size(); ++i) {
                if (!contentPath.get(i).hasEntry(name)) continue;
                if (!name.endsWith("/") && contentPath.get(i).isDirectory(name)) {
                    name = name + "/";
                }
                l.add(this.createURL(i + 1, name));
            }
        }
        return Collections.enumeration(l);
    }

    public URL getEntry(String name) {
        URL url = null;
        if (name.equals("/")) {
            url = this.createURL(0, "/");
        }
        if (url == null) {
            if (name.startsWith("/")) {
                name = name.substring(1);
            }
            if (this.getContent().hasEntry(name)) {
                if (!name.endsWith("/") && this.getContent().isDirectory(name)) {
                    name = name + "/";
                }
                url = this.createURL(0, name);
            }
        }
        return url;
    }

    public boolean hasInputStream(int index, String urlPath) {
        if (urlPath.startsWith("/")) {
            urlPath = urlPath.substring(1);
        }
        if (index == 0) {
            return this.getContent().hasEntry(urlPath);
        }
        return this.getContentPath().get(index - 1).hasEntry(urlPath);
    }

    public InputStream getInputStream(int index, String urlPath) throws IOException {
        if (urlPath.startsWith("/")) {
            urlPath = urlPath.substring(1);
        }
        if (index == 0) {
            return this.getContent().getEntryAsStream(urlPath);
        }
        return this.getContentPath().get(index - 1).getEntryAsStream(urlPath);
    }

    public long getContentTime(int index, String urlPath) {
        if (urlPath.startsWith("/")) {
            urlPath = urlPath.substring(1);
        }
        Content content = index == 0 ? this.getContent() : this.getContentPath().get(index - 1);
        long result = content.getContentTime(urlPath);
        return result > 0L ? result : this.m_bundle.getLastModified();
    }

    public URL getLocalURL(int index, String urlPath) {
        if (urlPath.startsWith("/")) {
            urlPath = urlPath.substring(1);
        }
        if (index == 0) {
            return this.getContent().getEntryAsURL(urlPath);
        }
        return this.getContentPath().get(index - 1).getEntryAsURL(urlPath);
    }

    private URL createURL(int port, String path) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        try {
            return m_secureAction.createURL(null, "bundle://" + this.m_bundle.getFramework()._getProperty("org.osgi.framework.uuid") + "_" + this.m_id + ":" + port + path, this.getBundle().getFramework().getBundleStreamHandler());
        }
        catch (Exception ex) {
            this.m_bundle.getFramework().getLogger().log(this.m_bundle, 1, "Unable to create resource URL.", (Throwable)ex);
            return null;
        }
    }

    synchronized void close() {
        try {
            this.resolve(null);
        }
        catch (Exception ex) {
            this.m_bundle.getFramework().getLogger().log(1, "Error releasing revision: " + ex.getMessage(), ex);
        }
        this.m_content.close();
        this.m_content = null;
        this.disposeContentPath();
    }

    public String toString() {
        return this.m_bundle.toString() + "(R " + this.m_id + ")";
    }
}

