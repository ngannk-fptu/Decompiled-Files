/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.felix.framework.BundleRevisionImpl;
import org.apache.felix.framework.DTOFactory;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.FrameworkStartLevelImpl;
import org.apache.felix.framework.cache.BundleArchive;
import org.apache.felix.framework.util.SecurityManagerEx;
import org.apache.felix.framework.util.ShrinkableCollection;
import org.apache.felix.framework.util.StringMap;
import org.apache.felix.framework.util.Util;
import org.osgi.dto.DTO;
import org.osgi.framework.AdaptPermission;
import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServicePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.framework.hooks.bundle.CollisionHook;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleRevisions;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;

class BundleImpl
implements Bundle,
BundleRevisions {
    private final Felix __m_felix;
    private final BundleArchive m_archive;
    private final List<BundleRevisionImpl> m_revisions = new ArrayList<BundleRevisionImpl>(0);
    private volatile BundleRevisionImpl m_currentRevision = null;
    private volatile int m_state;
    private boolean m_useDeclaredActivationPolicy;
    private BundleActivator m_activator = null;
    private volatile BundleContext m_context = null;
    private final Map m_cachedHeaders = new HashMap();
    private Map m_uninstalledHeaders = null;
    private long m_cachedHeadersTimestamp;
    private final Bundle m_installingBundle;
    private boolean m_stale = false;
    private int m_lockCount = 0;
    private Thread m_lockThread = null;
    private static final SecurityManagerEx m_smEx = new SecurityManagerEx();
    private static final ClassLoader m_classloader = Felix.class.getClassLoader();

    BundleImpl() {
        this.__m_felix = null;
        this.m_archive = null;
        this.m_state = 2;
        this.m_useDeclaredActivationPolicy = false;
        this.m_stale = false;
        this.m_activator = null;
        this.m_context = null;
        this.m_installingBundle = null;
    }

    BundleImpl(Felix felix, Bundle installingBundle, BundleArchive archive) throws Exception {
        this.__m_felix = felix;
        this.m_archive = archive;
        this.m_state = 2;
        this.m_useDeclaredActivationPolicy = false;
        this.m_stale = false;
        this.m_activator = null;
        this.m_context = null;
        this.m_installingBundle = installingBundle;
        BundleRevisionImpl revision = this.createRevision(false);
        this.addRevision(revision);
    }

    Felix getFramework() {
        return this.__m_felix;
    }

    BundleArchive getArchive() {
        return this.m_archive;
    }

    synchronized void close() {
        this.closeRevisions();
        try {
            this.m_archive.close();
        }
        catch (Exception ex) {
            this.getFramework().getLogger().log(this, 1, "Unable to close archive revisions.", (Throwable)ex);
        }
    }

    synchronized void closeAndDelete() throws Exception {
        if (!this.m_stale) {
            this.m_stale = true;
            this.closeRevisions();
            this.m_archive.closeAndDelete();
        }
    }

    private void closeRevisions() {
        for (BundleRevisionImpl br : this.m_revisions) {
            this.getFramework().getResolver().removeRevision(br);
            br.close();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    synchronized void refresh() throws Exception {
        if (this.isExtension() && this.getFramework().getState() != 16) {
            this.getFramework().getLogger().log(this, 2, "Framework restart on extension bundle refresh not implemented.");
        } else {
            BundleRevisionImpl current = this.adapt(BundleRevisionImpl.class);
            if (this.isRemovalPending()) {
                this.closeRevisions();
                this.m_archive.purge();
                current.resetContent(this.m_archive.getCurrentRevision().getContent());
            } else {
                this.getFramework().getResolver().removeRevision(current);
                current.resolve(null);
                current.disposeContentPath();
            }
            this.m_revisions.clear();
            this.addRevision(current);
            this.m_state = 2;
            this.m_stale = false;
            Map map = this.m_cachedHeaders;
            synchronized (map) {
                this.m_cachedHeaders.clear();
                this.m_cachedHeadersTimestamp = 0L;
            }
        }
    }

    synchronized boolean isDeclaredActivationPolicyUsed() {
        return this.m_useDeclaredActivationPolicy;
    }

    synchronized void setDeclaredActivationPolicyUsed(boolean b) {
        this.m_useDeclaredActivationPolicy = b;
    }

    synchronized BundleActivator getActivator() {
        return this.m_activator;
    }

    synchronized void setActivator(BundleActivator activator) {
        this.m_activator = activator;
    }

    @Override
    public BundleContext getBundleContext() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new AdminPermission(this, "context"));
        }
        return this.m_context;
    }

    void setBundleContext(BundleContext context) {
        this.m_context = context;
    }

    @Override
    public long getBundleId() {
        try {
            return this.m_archive.getId();
        }
        catch (Exception ex) {
            this.getFramework().getLogger().log(this, 1, "Error getting the identifier from bundle archive.", (Throwable)ex);
            return -1L;
        }
    }

    @Override
    public URL getEntry(String name) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            try {
                sm.checkPermission(new AdminPermission(this, "resource"));
            }
            catch (Exception e) {
                return null;
            }
        }
        return this.getFramework().getBundleEntry(this, name);
    }

    public Enumeration getEntryPaths(String path) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            try {
                sm.checkPermission(new AdminPermission(this, "resource"));
            }
            catch (Exception e) {
                return null;
            }
        }
        return this.getFramework().getBundleEntryPaths(this, path);
    }

    public Enumeration findEntries(String path, String filePattern, boolean recurse) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            try {
                sm.checkPermission(new AdminPermission(this, "resource"));
            }
            catch (Exception e) {
                return null;
            }
        }
        return this.getFramework().findBundleEntries(this, path, filePattern, recurse);
    }

    public Dictionary getHeaders() {
        return this.getHeaders(Locale.getDefault().toString());
    }

    public Dictionary getHeaders(String locale) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new AdminPermission(this, "metadata"));
        }
        if (locale == null) {
            locale = Locale.getDefault().toString();
        }
        return this.getFramework().getBundleHeaders(this, locale);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Map getCurrentLocalizedHeader(String locale) {
        Map<String, Object> result = null;
        if (locale.length() == 0) {
            result = new StringMap(this.adapt(BundleRevisionImpl.class).getHeaders());
        }
        if (result == null) {
            Map map = this.m_cachedHeaders;
            synchronized (map) {
                if (this.m_uninstalledHeaders != null) {
                    result = this.m_uninstalledHeaders;
                } else if (this.getLastModified() > this.m_cachedHeadersTimestamp) {
                    this.m_cachedHeaders.clear();
                } else if (this.m_cachedHeaders.containsKey(locale)) {
                    result = (Map)this.m_cachedHeaders.get(locale);
                }
            }
        }
        if (result == null) {
            StringMap headers;
            result = headers = new StringMap(this.adapt(BundleRevisionImpl.class).getHeaders());
            boolean localize = false;
            Iterator it = headers.values().iterator();
            while (!localize && it.hasNext()) {
                if (!((String)it.next()).startsWith("%")) continue;
                localize = true;
            }
            if (!localize) {
                this.updateHeaderCache(locale, headers);
            } else {
                String basename = (String)headers.get("Bundle-Localization");
                if (basename == null) {
                    basename = "OSGI-INF/l10n/bundle";
                }
                List<BundleRevision> revisionList = BundleImpl.createLocalizationRevisionList(this.adapt(BundleRevisionImpl.class));
                List<String> resourceList = BundleImpl.createLocalizationResourceList(basename, locale);
                boolean found = false;
                Properties mergedProperties = new Properties();
                for (BundleRevision bundleRevision : revisionList) {
                    for (String res : resourceList) {
                        URL temp = ((BundleRevisionImpl)bundleRevision).getEntry(res + ".properties");
                        if (temp == null) continue;
                        found = true;
                        try {
                            mergedProperties.load(temp.openConnection().getInputStream());
                        }
                        catch (IOException iOException) {}
                    }
                }
                if (!found && !locale.equals(Locale.getDefault().toString())) {
                    result = this.getCurrentLocalizedHeader(Locale.getDefault().toString());
                } else {
                    for (Map.Entry entry : headers.entrySet()) {
                        String value = (String)entry.getValue();
                        if (!value.startsWith("%")) continue;
                        String key = value.substring(value.indexOf("%") + 1);
                        String newvalue = mergedProperties.getProperty(key);
                        if (newvalue == null) {
                            newvalue = key;
                        }
                        entry.setValue(newvalue);
                    }
                    this.updateHeaderCache(locale, headers);
                }
            }
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void updateHeaderCache(String locale, Map localizedHeaders) {
        Map map = this.m_cachedHeaders;
        synchronized (map) {
            if (this.m_uninstalledHeaders == null) {
                this.m_cachedHeaders.put(locale, localizedHeaders);
                this.m_cachedHeadersTimestamp = System.currentTimeMillis();
            }
        }
    }

    private static List<BundleRevision> createLocalizationRevisionList(BundleRevision br) {
        List<BundleRevision> fragments;
        List<BundleWire> hostWires;
        if (Util.isFragment(br) && br.getWiring() != null && (hostWires = br.getWiring().getRequiredWires(null)) != null && hostWires.size() > 0) {
            br = hostWires.get(0).getProviderWiring().getRevision();
            for (int hostIdx = 1; hostIdx < hostWires.size(); ++hostIdx) {
                if (br.getVersion().compareTo(hostWires.get(hostIdx).getProviderWiring().getRevision().getVersion()) >= 0) continue;
                br = hostWires.get(hostIdx).getProviderWiring().getRevision();
            }
        }
        ArrayList<BundleRevision> result = new ArrayList<BundleRevision>();
        result.add(br);
        BundleWiring wiring = br.getWiring();
        if (wiring != null && (fragments = Util.getFragments(wiring)) != null) {
            result.addAll(fragments);
        }
        return result;
    }

    private static List<String> createLocalizationResourceList(String basename, String locale) {
        ArrayList<String> result = new ArrayList<String>(4);
        StringBuilder tempLocale = new StringBuilder(basename);
        result.add(tempLocale.toString());
        if (locale.length() > 0) {
            StringTokenizer tokens = new StringTokenizer(locale, "_");
            while (tokens.hasMoreTokens()) {
                tempLocale.append("_").append(tokens.nextToken());
                result.add(tempLocale.toString());
            }
        }
        return result;
    }

    @Override
    public long getLastModified() {
        try {
            return this.m_archive.getLastModified();
        }
        catch (Exception ex) {
            this.getFramework().getLogger().log(this, 1, "Error reading last modification time from bundle archive.", (Throwable)ex);
            return 0L;
        }
    }

    void setLastModified(long l) {
        try {
            this.m_archive.setLastModified(l);
        }
        catch (Exception ex) {
            this.getFramework().getLogger().log(this, 1, "Error writing last modification time to bundle archive.", (Throwable)ex);
        }
    }

    @Override
    public String getLocation() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new AdminPermission(this, "metadata"));
        }
        return this._getLocation();
    }

    String _getLocation() {
        try {
            return this.m_archive.getLocation();
        }
        catch (Exception ex) {
            this.getFramework().getLogger().log(this, 1, "Error getting location from bundle archive.", (Throwable)ex);
            return null;
        }
    }

    @Override
    public URL getResource(String name) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            try {
                sm.checkPermission(new AdminPermission(this, "resource"));
            }
            catch (Exception e) {
                return null;
            }
        }
        return this.getFramework().getBundleResource(this, name);
    }

    public Enumeration getResources(String name) throws IOException {
        Enumeration e;
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            try {
                sm.checkPermission(new AdminPermission(this, "resource"));
            }
            catch (Exception e2) {
                return null;
            }
        }
        return (e = this.getFramework().getBundleResources(this, name)) == null || !e.hasMoreElements() ? null : e;
    }

    public ServiceReference[] getRegisteredServices() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            ServiceReference[] refs = this.getFramework().getBundleRegisteredServices(this);
            if (refs == null) {
                return refs;
            }
            ArrayList<ServiceReference> result = new ArrayList<ServiceReference>();
            for (int i = 0; i < refs.length; ++i) {
                try {
                    sm.checkPermission(new ServicePermission(refs[i], "get"));
                    result.add(refs[i]);
                    continue;
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            if (result.isEmpty()) {
                return null;
            }
            return result.toArray(new ServiceReference[result.size()]);
        }
        return this.getFramework().getBundleRegisteredServices(this);
    }

    public ServiceReference[] getServicesInUse() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            ServiceReference[] refs = this.getFramework().getBundleServicesInUse(this);
            if (refs == null) {
                return refs;
            }
            ArrayList<ServiceReference> result = new ArrayList<ServiceReference>();
            for (int i = 0; i < refs.length; ++i) {
                try {
                    sm.checkPermission(new ServicePermission(refs[i], "get"));
                    result.add(refs[i]);
                    continue;
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            if (result.isEmpty()) {
                return null;
            }
            return result.toArray(new ServiceReference[result.size()]);
        }
        return this.getFramework().getBundleServicesInUse(this);
    }

    @Override
    public int getState() {
        return this.m_state;
    }

    void __setState(int i) {
        this.m_state = i;
    }

    int getPersistentState() {
        try {
            return this.m_archive.getPersistentState();
        }
        catch (Exception ex) {
            this.getFramework().getLogger().log(this, 1, "Error reading persistent state from bundle archive.", (Throwable)ex);
            return 2;
        }
    }

    void setPersistentStateInactive() {
        try {
            this.m_archive.setPersistentState(2);
        }
        catch (Exception ex) {
            this.getFramework().getLogger().log(this, 1, "Error writing persistent state to bundle archive.", (Throwable)ex);
        }
    }

    void setPersistentStateActive() {
        try {
            this.m_archive.setPersistentState(32);
        }
        catch (Exception ex) {
            this.getFramework().getLogger().log(this, 1, "Error writing persistent state to bundle archive.", (Throwable)ex);
        }
    }

    void setPersistentStateStarting() {
        try {
            this.m_archive.setPersistentState(8);
        }
        catch (Exception ex) {
            this.getFramework().getLogger().log(this, 1, "Error writing persistent state to bundle archive.", (Throwable)ex);
        }
    }

    void setPersistentStateUninstalled() {
        try {
            this.m_archive.setPersistentState(1);
        }
        catch (Exception ex) {
            this.getFramework().getLogger().log(this, 1, "Error writing persistent state to bundle archive.", (Throwable)ex);
        }
    }

    int getStartLevel(int defaultLevel) {
        try {
            int level = this.m_archive.getStartLevel();
            if (level == -1) {
                level = defaultLevel;
            }
            return level;
        }
        catch (Exception ex) {
            this.getFramework().getLogger().log(this, 1, "Error reading start level from bundle archive.", (Throwable)ex);
            return defaultLevel;
        }
    }

    void setStartLevel(int i) {
        try {
            this.m_archive.setStartLevel(i);
        }
        catch (Exception ex) {
            this.getFramework().getLogger().log(this, 1, "Error writing start level to bundle archive.", (Throwable)ex);
        }
    }

    synchronized boolean isStale() {
        return this.m_stale;
    }

    synchronized boolean isExtension() {
        for (BundleRevisionImpl revision : this.m_revisions) {
            if (!revision.isExtension()) continue;
            return true;
        }
        return false;
    }

    @Override
    public String getSymbolicName() {
        return this.adapt(BundleRevisionImpl.class).getSymbolicName();
    }

    @Override
    public Version getVersion() {
        return this.adapt(BundleRevisionImpl.class).getVersion();
    }

    @Override
    public boolean hasPermission(Object obj) {
        return this.getFramework().bundleHasPermission(this, obj);
    }

    public Map getSignerCertificates(int signersType) {
        return (Map)this.getFramework().getSignerMatcher(this, signersType);
    }

    public Class loadClass(String name) throws ClassNotFoundException {
        if (this.isExtension()) {
            throw new ClassNotFoundException("Extension bundles cannot load classes.");
        }
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            try {
                sm.checkPermission(new AdminPermission(this, "class"));
            }
            catch (Exception ex) {
                throw new ClassNotFoundException("No permission.", ex);
            }
        }
        return this.getFramework().loadBundleClass(this, name);
    }

    @Override
    public void start() throws BundleException {
        this.start(0);
    }

    @Override
    public void start(int options) throws BundleException {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new AdminPermission(this, "execute"));
        }
        this.getFramework().startBundle(this, options);
    }

    @Override
    public void update() throws BundleException {
        this.update(null);
    }

    @Override
    public void update(InputStream is) throws BundleException {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new AdminPermission(this, "lifecycle"));
        }
        this.getFramework().updateBundle(this, is);
    }

    @Override
    public void stop() throws BundleException {
        this.stop(0);
    }

    @Override
    public void stop(int options) throws BundleException {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new AdminPermission(this, "execute"));
        }
        this.getFramework().stopBundle(this, (options & 1) == 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void uninstall() throws BundleException {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new AdminPermission(this, "lifecycle"));
        }
        Map headers = this.getCurrentLocalizedHeader(Locale.getDefault().toString());
        this.getFramework().uninstallBundle(this);
        Map map = this.m_cachedHeaders;
        synchronized (map) {
            if (this.m_uninstalledHeaders == null) {
                this.m_uninstalledHeaders = headers;
                this.m_cachedHeaders.clear();
            }
        }
    }

    <A> void checkAdapt(Class<A> type) {
        Class[] classes;
        SecurityManager sm = System.getSecurityManager();
        if (!(sm == null || this.getFramework().getSecurityProvider() == null || (classes = m_smEx.getClassContext()).length >= 3 && Felix.m_secureAction.getClassLoader(classes[3]) == m_classloader && classes[3].getName().startsWith("org.apache.felix.framework."))) {
            sm.checkPermission(new AdaptPermission(type.getName(), this, "adapt"));
        }
    }

    @Override
    public <A> A adapt(Class<A> type) {
        this.checkAdapt(type);
        if (type == BundleContext.class) {
            return (A)this.m_context;
        }
        if (type == BundleStartLevel.class) {
            return (A)this.getFramework().adapt(FrameworkStartLevelImpl.class).createBundleStartLevel(this);
        }
        if (type == BundleRevision.class) {
            if (this.m_state == 1) {
                return null;
            }
            return (A)this.m_currentRevision;
        }
        if (type == BundleRevisionImpl.class) {
            return (A)this.m_currentRevision;
        }
        if (type == BundleRevisions.class) {
            return (A)this;
        }
        if (type == BundleWiring.class) {
            if (this.m_state == 1) {
                return null;
            }
            BundleRevisionImpl revision = this.m_currentRevision;
            return (A)(revision != null ? revision.getWiring() : null);
        }
        if (type == AccessControlContext.class) {
            if (this.m_state == 1) {
                return null;
            }
            ProtectionDomain pd = this.getProtectionDomain();
            if (pd == null) {
                return null;
            }
            return (A)new AccessControlContext(new ProtectionDomain[]{pd});
        }
        if (DTO.class.isAssignableFrom(type) || DTO[].class.isAssignableFrom(type)) {
            return DTOFactory.createDTO(this, type);
        }
        return null;
    }

    @Override
    public File getDataFile(String filename) {
        return this.getFramework().getDataFile(this, filename);
    }

    @Override
    public int compareTo(Bundle t) {
        long thatBundleId;
        long thisBundleId = this.getBundleId();
        return thisBundleId < (thatBundleId = t.getBundleId()) ? -1 : (thisBundleId == thatBundleId ? 0 : 1);
    }

    public String toString() {
        String sym = this.getSymbolicName();
        if (sym != null) {
            return sym + " [" + this.getBundleId() + "]";
        }
        return "[" + this.getBundleId() + "]";
    }

    synchronized boolean isRemovalPending() {
        return this.m_state == 1 || this.m_revisions.size() > 1 || this.m_stale;
    }

    @Override
    public Bundle getBundle() {
        return this;
    }

    @Override
    public synchronized List<BundleRevision> getRevisions() {
        return new ArrayList<BundleRevision>(this.m_revisions);
    }

    synchronized boolean hasRevision(BundleRevision revision) {
        return this.m_revisions.contains(revision);
    }

    synchronized void revise(String location, InputStream is) throws Exception {
        this.m_archive.revise(location, is);
        try {
            BundleRevisionImpl revision = this.createRevision(true);
            this.addRevision(revision);
        }
        catch (Exception ex) {
            this.m_archive.rollbackRevise();
            throw ex;
        }
    }

    synchronized boolean rollbackRevise() throws Exception {
        BundleRevision br = this.m_revisions.remove(0);
        this.m_currentRevision = !this.m_revisions.isEmpty() ? this.m_revisions.get(0) : null;
        this.getFramework().getResolver().removeRevision(br);
        return this.m_archive.rollbackRevise();
    }

    synchronized void addRevision(BundleRevisionImpl revision) throws Exception {
        BundleRevisionImpl previous = this.m_currentRevision;
        this.m_revisions.add(0, revision);
        this.m_currentRevision = revision;
        try {
            this.getFramework().setBundleProtectionDomain(revision);
        }
        catch (Exception ex) {
            this.m_revisions.remove(0);
            this.m_currentRevision = previous;
            throw ex;
        }
        this.getFramework().getResolver().addRevision(revision);
    }

    private BundleRevisionImpl createRevision(boolean isUpdate) throws Exception {
        Map<String, Object> headerMap = Util.getMultiReleaseAwareManifestHeaders(this.getFramework()._getProperty("java.specification.version"), this.m_archive.getCurrentRevision());
        BundleRevisionImpl revision = new BundleRevisionImpl(this, Long.toString(this.getBundleId()) + "." + this.m_archive.getCurrentRevisionNumber().toString(), headerMap, this.m_archive.getCurrentRevision().getContent());
        String allowMultiple = (String)this.getFramework().getConfig().get("org.osgi.framework.bsnversion");
        String string = allowMultiple = allowMultiple == null ? "managed" : allowMultiple;
        if (revision.getManifestVersion().equals("2") && !allowMultiple.equals("multiple")) {
            Set<ServiceReference<CollisionHook>> hooks;
            Version bundleVersion = revision.getVersion();
            bundleVersion = bundleVersion == null ? Version.emptyVersion : bundleVersion;
            String symName = revision.getSymbolicName();
            ArrayList<Bundle> collisionCanditates = new ArrayList<Bundle>();
            Bundle[] bundles = this.getFramework().getBundles();
            for (int i = 0; bundles != null && i < bundles.length; ++i) {
                long id = ((BundleImpl)bundles[i]).getBundleId();
                if (id == this.getBundleId() || !symName.equals(bundles[i].getSymbolicName()) || !bundleVersion.equals(bundles[i].getVersion())) continue;
                collisionCanditates.add(bundles[i]);
            }
            if (!collisionCanditates.isEmpty() && allowMultiple.equals("managed") && !(hooks = this.getFramework().getHookRegistry().getHooks(CollisionHook.class)).isEmpty()) {
                ShrinkableCollection<Bundle> shrinkableCollisionCandidates = new ShrinkableCollection<Bundle>(collisionCanditates);
                for (ServiceReference<CollisionHook> hook : hooks) {
                    BundleImpl target;
                    int operationType;
                    CollisionHook ch = this.getFramework().getService(this.getFramework(), hook, false);
                    if (ch == null) continue;
                    if (isUpdate) {
                        operationType = 2;
                        target = this;
                    } else {
                        operationType = 1;
                        target = this.m_installingBundle == null ? this : this.m_installingBundle;
                    }
                    Felix.m_secureAction.invokeBundleCollisionHook(ch, operationType, target, shrinkableCollisionCandidates);
                }
            }
            if (!collisionCanditates.isEmpty() && this.m_installingBundle != null) {
                throw new BundleException("Bundle symbolic name and version are not unique: " + symName + ':' + bundleVersion, 9);
            }
        }
        return revision;
    }

    synchronized ProtectionDomain getProtectionDomain() {
        ProtectionDomain pd = null;
        for (int i = 0; i < this.m_revisions.size() && pd == null; ++i) {
            pd = this.m_revisions.get(i).getProtectionDomain();
        }
        return pd;
    }

    synchronized boolean isLockable() {
        return this.m_lockCount == 0 || this.m_lockThread == Thread.currentThread();
    }

    synchronized Thread getLockingThread() {
        return this.m_lockThread;
    }

    synchronized void lock() {
        if (this.m_lockCount > 0 && this.m_lockThread != Thread.currentThread()) {
            throw new IllegalStateException("Bundle is locked by another thread.");
        }
        ++this.m_lockCount;
        this.m_lockThread = Thread.currentThread();
    }

    synchronized void unlock() {
        if (this.m_lockCount == 0) {
            throw new IllegalStateException("Bundle is not locked.");
        }
        if (this.m_lockCount > 0 && this.m_lockThread != Thread.currentThread()) {
            throw new IllegalStateException("Bundle is locked by another thread.");
        }
        --this.m_lockCount;
        if (this.m_lockCount == 0) {
            this.m_lockThread = null;
        }
    }

    BundleContext _getBundleContext() {
        return this.m_context;
    }
}

