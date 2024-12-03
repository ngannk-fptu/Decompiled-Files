/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Version
 */
package aQute.bnd.osgi.resource;

import aQute.bnd.build.model.clauses.VersionedClause;
import aQute.bnd.header.Attrs;
import aQute.bnd.osgi.Macro;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.resource.CapReqBuilder;
import aQute.bnd.osgi.resource.ResolutionDirective;
import aQute.bnd.osgi.resource.ResourceBuilder;
import aQute.bnd.osgi.resource.ResourceImpl;
import aQute.lib.converter.Converter;
import aQute.lib.filter.Filter;
import aQute.lib.strings.Strings;
import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.osgi.framework.Version;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;

public class ResourceUtils {
    public static final Comparator<Resource> IDENTITY_VERSION_COMPARATOR = new Comparator<Resource>(){

        @Override
        public int compare(Resource o1, Resource o2) {
            String v2;
            if (o1 == o2) {
                return 0;
            }
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }
            if (o1.equals(o2)) {
                return 0;
            }
            String v1 = ResourceUtils.getIdentityVersion(o1);
            if (v1 == (v2 = ResourceUtils.getIdentityVersion(o2))) {
                return 0;
            }
            if (v1 == null) {
                return -1;
            }
            if (v2 == null) {
                return 1;
            }
            return new aQute.bnd.version.Version(v1).compareTo(new aQute.bnd.version.Version(v2));
        }
    };
    private static final Comparator<? super Resource> RESOURCE_COMPARATOR = new Comparator<Resource>(){

        @Override
        public int compare(Resource o1, Resource o2) {
            if (o1 == o2) {
                return 0;
            }
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }
            if (o1.equals(o2)) {
                return 0;
            }
            if (o1 instanceof ResourceImpl && o2 instanceof ResourceImpl) {
                return ((ResourceImpl)o1).compareTo(o2);
            }
            return o1.toString().compareTo(o2.toString());
        }
    };
    public static final Resource DUMMY_RESOURCE = new ResourceBuilder().build();
    public static final String WORKSPACE_NAMESPACE = "bnd.workspace.project";
    static Converter cnv = new Converter();

    public static ContentCapability getContentCapability(Resource resource) {
        List<ContentCapability> caps = ResourceUtils.getContentCapabilities(resource);
        if (caps.isEmpty()) {
            return null;
        }
        return caps.get(0);
    }

    public static List<ContentCapability> getContentCapabilities(Resource resource) {
        ArrayList<ContentCapability> result = new ArrayList<ContentCapability>();
        for (Capability c : resource.getCapabilities("osgi.content")) {
            result.add(ResourceUtils.as(c, ContentCapability.class));
        }
        return result;
    }

    public static IdentityCapability getIdentityCapability(Resource resource) {
        List<Capability> caps = resource.getCapabilities("osgi.identity");
        if (caps.isEmpty()) {
            return null;
        }
        return ResourceUtils.as(caps.get(0), IdentityCapability.class);
    }

    public static String getIdentityVersion(Resource resource) {
        IdentityCapability cap = ResourceUtils.getIdentityCapability(resource);
        if (cap == null) {
            return null;
        }
        Object v = cap.getAttributes().get("version");
        if (v == null) {
            return null;
        }
        return v.toString();
    }

    public static BundleCap getBundleCapability(Resource resource) {
        List<Capability> caps = resource.getCapabilities("osgi.wiring.bundle");
        if (caps.isEmpty()) {
            return null;
        }
        return ResourceUtils.as(caps.get(0), BundleCap.class);
    }

    public static aQute.bnd.version.Version toVersion(Object v) {
        if (v instanceof aQute.bnd.version.Version) {
            return (aQute.bnd.version.Version)v;
        }
        if (v instanceof Version) {
            Version o = (Version)v;
            String q = o.getQualifier();
            return q.isEmpty() ? new aQute.bnd.version.Version(o.getMajor(), o.getMinor(), o.getMicro()) : new aQute.bnd.version.Version(o.getMajor(), o.getMinor(), o.getMicro(), q);
        }
        if (v instanceof String) {
            if (!aQute.bnd.version.Version.isVersion((String)v)) {
                return null;
            }
            return new aQute.bnd.version.Version((String)v);
        }
        return null;
    }

    public static final aQute.bnd.version.Version getVersion(Capability cap) {
        Object v = cap.getAttributes().get("version");
        if (v == null) {
            return null;
        }
        if (v instanceof aQute.bnd.version.Version) {
            return (aQute.bnd.version.Version)v;
        }
        if (v instanceof Version) {
            return new aQute.bnd.version.Version(v.toString());
        }
        if (v instanceof String) {
            return aQute.bnd.version.Version.parseVersion((String)v);
        }
        return null;
    }

    public static URI getURI(Capability contentCapability) {
        Object uriObj = contentCapability.getAttributes().get("url");
        if (uriObj == null) {
            return null;
        }
        if (uriObj instanceof URI) {
            return (URI)uriObj;
        }
        try {
            if (uriObj instanceof URL) {
                return ((URL)uriObj).toURI();
            }
            if (uriObj instanceof String) {
                try {
                    URL url = new URL((String)uriObj);
                    return url.toURI();
                }
                catch (MalformedURLException mfue) {
                    File f = new File((String)uriObj);
                    if (f.isFile()) {
                        return f.toURI();
                    }
                    return new URI((String)uriObj);
                }
            }
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException("Resource content capability has illegal URL attribute", e);
        }
        return null;
    }

    public static String getVersionAttributeForNamespace(String ns) {
        Object name = "osgi.identity".equals(ns) ? "version" : ("osgi.wiring.bundle".equals(ns) ? "bundle-version" : ("osgi.wiring.host".equals(ns) ? "bundle-version" : ("osgi.wiring.package".equals(ns) ? "version" : ("osgi.service".equals(ns) ? null : ("osgi.ee".equals(ns) ? "version" : ("osgi.extender".equals(ns) ? "version" : ("osgi.contract".equals(ns) ? "version" : null)))))));
        return name;
    }

    public static <T extends Capability> T as(final Capability cap, Class<T> type) {
        return (T)((Capability)Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, new InvocationHandler(){

            @Override
            public Object invoke(Object target, Method method, Object[] args) throws Throwable {
                if (Capability.class == method.getDeclaringClass()) {
                    return method.invoke((Object)cap, args);
                }
                return ResourceUtils.get(method, cap.getAttributes(), cap.getDirectives(), args);
            }
        }));
    }

    public static <T extends Requirement> T as(final Requirement req, Class<T> type) {
        return (T)((Requirement)Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, new InvocationHandler(){

            @Override
            public Object invoke(Object target, Method method, Object[] args) throws Throwable {
                if (Requirement.class == method.getDeclaringClass()) {
                    return method.invoke((Object)req, args);
                }
                return ResourceUtils.get(method, req.getAttributes(), req.getDirectives(), args);
            }
        }));
    }

    static <T> T get(Method method, Map<String, Object> attrs, Map<String, String> directives, Object[] args) throws Exception {
        String name = method.getName().replace('_', '.');
        Object value = name.startsWith("$") ? directives.get(name.substring(1)) : attrs.get(name);
        if (value == null && args != null && args.length == 1) {
            value = args[0];
        }
        return (T)cnv.convert(method.getGenericReturnType(), value);
    }

    public static Set<Resource> getResources(Collection<? extends Capability> providers) {
        if (providers == null || providers.isEmpty()) {
            return Collections.emptySet();
        }
        TreeSet<Resource> resources = new TreeSet<Resource>(RESOURCE_COMPARATOR);
        for (Capability capability : providers) {
            resources.add(capability.getResource());
        }
        return resources;
    }

    public static Requirement createWildcardRequirement() {
        return CapReqBuilder.createSimpleRequirement("osgi.identity", "*", null).buildSyntheticRequirement();
    }

    public static boolean isEffective(Requirement r, Capability c) {
        String capabilityEffective = c.getDirectives().get("effective");
        if (capabilityEffective == null) {
            return true;
        }
        if (capabilityEffective.equals("resolve")) {
            return true;
        }
        String requirementEffective = r.getDirectives().get("effective");
        if (requirementEffective == null) {
            return false;
        }
        return capabilityEffective.equals(requirementEffective);
    }

    public static boolean matches(Requirement r, Resource resource) {
        for (Capability c : resource.getCapabilities(r.getNamespace())) {
            if (!ResourceUtils.matches(r, c)) continue;
            return true;
        }
        return false;
    }

    public static boolean matches(Requirement r, Capability c) {
        if (!r.getNamespace().equals(c.getNamespace())) {
            return false;
        }
        if (!ResourceUtils.isEffective(r, c)) {
            return false;
        }
        String filter = r.getDirectives().get("filter");
        if (filter == null) {
            return true;
        }
        try {
            Filter f = new Filter(filter);
            return f.matchMap(c.getAttributes());
        }
        catch (Exception e) {
            return false;
        }
    }

    public static String getEffective(Map<String, String> directives) {
        String effective = directives.get("effective");
        if (effective == null) {
            return "resolve";
        }
        return effective;
    }

    public static ResolutionDirective getResolution(Requirement r) {
        String resolution = r.getDirectives().get("resolution");
        if (resolution == null || resolution.equals("mandatory")) {
            return ResolutionDirective.mandatory;
        }
        if (resolution.equals("optional")) {
            return ResolutionDirective.optional;
        }
        return null;
    }

    public static String toRequireCapability(Requirement req) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(req.getNamespace());
        CapReqBuilder r = new CapReqBuilder(req.getNamespace());
        r.addAttributes(req.getAttributes());
        r.addDirectives(req.getDirectives());
        Attrs attrs = r.toAttrs();
        sb.append(";").append(attrs);
        return sb.toString();
    }

    public static String toProvideCapability(Capability cap) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(cap.getNamespace());
        CapReqBuilder r = new CapReqBuilder(cap.getNamespace());
        r.addAttributes(cap.getAttributes());
        r.addDirectives(cap.getDirectives());
        Attrs attrs = r.toAttrs();
        sb.append(";").append(attrs);
        return sb.toString();
    }

    public static Map<URI, String> getLocations(Resource resource) {
        HashMap<URI, String> locations = new HashMap<URI, String>();
        for (ContentCapability c : ResourceUtils.getContentCapabilities(resource)) {
            URI uri = c.url();
            String sha = c.osgi_content();
            if (uri == null) continue;
            locations.put(uri, sha);
        }
        return locations;
    }

    public static List<Capability> findProviders(Requirement requirement, Collection<? extends Capability> capabilities) {
        ArrayList<Capability> result = new ArrayList<Capability>();
        for (Capability capability : capabilities) {
            if (!ResourceUtils.matches(requirement, capability)) continue;
            result.add(capability);
        }
        return result;
    }

    public static boolean isFragment(Resource resource) {
        IdentityCapability identity = ResourceUtils.getIdentityCapability(resource);
        if (identity == null) {
            return false;
        }
        return "osgi.fragment".equals(identity.getAttributes().get("type"));
    }

    public static String stripDirective(String name) {
        if (Strings.charAt(name, -1) == ':') {
            return Strings.substring(name, 0, -1);
        }
        return name;
    }

    public static String getIdentity(Capability identityCapability) throws IllegalArgumentException {
        String id = (String)identityCapability.getAttributes().get("osgi.identity");
        if (id == null) {
            throw new IllegalArgumentException("Resource identity capability has missing identity attribute");
        }
        return id;
    }

    public static VersionedClause toVersionClause(Resource resource, String mask) {
        String versionString;
        IdentityCapability idCap = ResourceUtils.getIdentityCapability(resource);
        String identity = ResourceUtils.getIdentity(idCap);
        if (resource.getCapabilities(WORKSPACE_NAMESPACE).isEmpty()) {
            Macro macro = new Macro(new Processor(), new Object[0]);
            aQute.bnd.version.Version version = ResourceUtils.getVersion(idCap);
            versionString = macro._range(new String[]{"range", mask, version.toString()});
        } else {
            versionString = "snapshot";
        }
        Attrs attribs = new Attrs();
        attribs.put("version", versionString);
        return new VersionedClause(identity, attribs);
    }

    static <T> T requireNonNull(T obj) {
        if (obj != null) {
            return obj;
        }
        throw new NullPointerException();
    }

    static {
        cnv.hook((Type)((Object)aQute.bnd.version.Version.class), new Converter.Hook(){

            @Override
            public Object convert(Type dest, Object o) throws Exception {
                if (o instanceof Version) {
                    return new aQute.bnd.version.Version(o.toString());
                }
                return null;
            }
        });
    }

    public static interface BundleCap
    extends Capability {
        public String osgi_wiring_bundle();

        public boolean singleton();

        public aQute.bnd.version.Version bundle_version();
    }

    public static interface ContentCapability
    extends Capability {
        public String osgi_content();

        public URI url();

        public long size();

        public String mime();
    }

    public static interface IdentityCapability
    extends Capability {
        public String osgi_identity();

        public boolean singleton();

        public aQute.bnd.version.Version version();

        public Type type();

        public URI uri();

        public String copyright();

        public String description(String var1);

        public String documentation();

        public String license();

        public static enum Type {
            bundle("osgi.bundle"),
            fragment("osgi.fragment"),
            unknown("unknown");

            private String s;

            private Type(String s) {
                this.s = s;
            }

            public String toString() {
                return this.s;
            }
        }
    }
}

