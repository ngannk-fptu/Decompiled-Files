/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi;

import aQute.bnd.annotation.headers.BundleCategory;
import aQute.bnd.annotation.headers.BundleContributors;
import aQute.bnd.annotation.headers.BundleCopyright;
import aQute.bnd.annotation.headers.BundleDevelopers;
import aQute.bnd.annotation.headers.BundleDocURL;
import aQute.bnd.annotation.headers.BundleLicense;
import aQute.bnd.annotation.headers.Category;
import aQute.bnd.annotation.headers.ProvideCapability;
import aQute.bnd.annotation.headers.RequireCapability;
import aQute.bnd.header.Attrs;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Annotation;
import aQute.bnd.osgi.ClassDataCollector;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.Macro;
import aQute.bnd.osgi.Processor;
import aQute.bnd.version.Version;
import aQute.lib.collections.MultiMap;
import aQute.lib.strings.Strings;
import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class AnnotationHeaders
extends ClassDataCollector
implements Closeable {
    static final Pattern SIMPLE_PARAM_PATTERN = Pattern.compile("\\$\\{(\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*)\\}");
    final Analyzer analyzer;
    final Set<Descriptors.TypeRef> interesting = new HashSet<Descriptors.TypeRef>();
    final MultiMap<String, String> headers = new MultiMap();
    final Descriptors.TypeRef bundleLicenseRef;
    final Descriptors.TypeRef requireCapabilityRef;
    final Descriptors.TypeRef provideCapabilityRef;
    final Descriptors.TypeRef bundleCategoryRef;
    final Descriptors.TypeRef bundleDocURLRef;
    final Descriptors.TypeRef bundleDeveloperRef;
    final Descriptors.TypeRef bundleContributorRef;
    final Descriptors.TypeRef bundleCopyrightRef;
    Clazz current;
    boolean finalizing;

    AnnotationHeaders(Analyzer analyzer) {
        this.analyzer = analyzer;
        this.bundleLicenseRef = analyzer.getTypeRefFromFQN(BundleLicense.class.getName());
        this.interesting.add(this.bundleLicenseRef);
        this.requireCapabilityRef = analyzer.getTypeRefFromFQN(RequireCapability.class.getName());
        this.interesting.add(this.requireCapabilityRef);
        this.provideCapabilityRef = analyzer.getTypeRefFromFQN(ProvideCapability.class.getName());
        this.interesting.add(this.provideCapabilityRef);
        this.bundleCategoryRef = analyzer.getTypeRefFromFQN(BundleCategory.class.getName());
        this.interesting.add(this.bundleCategoryRef);
        this.bundleDocURLRef = analyzer.getTypeRefFromFQN(BundleDocURL.class.getName());
        this.interesting.add(this.bundleDocURLRef);
        this.bundleDeveloperRef = analyzer.getTypeRefFromFQN(BundleDevelopers.class.getName());
        this.interesting.add(this.bundleDeveloperRef);
        this.bundleContributorRef = analyzer.getTypeRefFromFQN(BundleContributors.class.getName());
        this.interesting.add(this.bundleContributorRef);
        this.bundleCopyrightRef = analyzer.getTypeRefFromFQN(BundleCopyright.class.getName());
        this.interesting.add(this.bundleCopyrightRef);
    }

    @Override
    public boolean classStart(Clazz c) {
        if (!c.isAnnotation() && c.annotations != null) {
            this.current = c;
            return true;
        }
        this.current = null;
        return false;
    }

    @Override
    public void annotation(Annotation annotation) throws Exception {
        Descriptors.TypeRef name = annotation.getName();
        if (name.isJava()) {
            return;
        }
        if (name == this.bundleLicenseRef) {
            this.doLicense(annotation);
        } else if (name == this.requireCapabilityRef) {
            this.doRequireCapability(annotation);
        } else if (name == this.provideCapabilityRef) {
            this.doProvideCapability(annotation);
        } else if (name == this.bundleCategoryRef) {
            this.doBundleCategory(annotation.getAnnotation(BundleCategory.class));
        } else if (name == this.bundleDocURLRef) {
            this.doBundleDocURL(annotation.getAnnotation(BundleDocURL.class));
        } else if (name == this.bundleDeveloperRef) {
            this.doBundleDevelopers(annotation.getAnnotation(BundleDevelopers.class));
        } else if (name == this.bundleContributorRef) {
            this.doBundleContributors(annotation.getAnnotation(BundleContributors.class));
        } else if (name == this.bundleCopyrightRef) {
            this.doBundeCopyright(annotation.getAnnotation(BundleCopyright.class));
        } else {
            this.doAnnotatedAnnotation(annotation, name);
        }
    }

    void doAnnotatedAnnotation(final Annotation annotation, Descriptors.TypeRef name) throws Exception {
        final Clazz c = this.analyzer.findClass(annotation.getName());
        if (c != null && c.annotations != null && this.containsAny(this.interesting, c.annotations)) {
            c.parseClassFileWithCollector(new ClassDataCollector(){

                @Override
                public void annotation(Annotation a) throws Exception {
                    if (AnnotationHeaders.this.interesting.contains(a.getName())) {
                        a.merge(annotation);
                        a.addDefaults(c);
                        AnnotationHeaders.this.annotation(a);
                    }
                }
            });
        }
    }

    @Override
    public void close() throws IOException {
    }

    private void doBundleDevelopers(BundleDevelopers annotation) throws IOException {
        StringBuilder sb = new StringBuilder(annotation.value());
        if (annotation.name() != null) {
            sb.append(";name='");
            this.escape(sb, annotation.name());
            sb.append("'");
        }
        if (annotation.roles() != null) {
            sb.append(";roles='");
            this.escape(sb, annotation.roles());
            sb.append("'");
        }
        if (annotation.organizationUrl() != null) {
            sb.append(";organizationUrl='");
            this.escape(sb, annotation.organizationUrl());
            sb.append("'");
        }
        if (annotation.organization() != null) {
            sb.append(";organization='");
            this.escape(sb, annotation.organization());
            sb.append("'");
        }
        if (annotation.timezone() != 0) {
            sb.append(";timezone=").append(annotation.timezone());
        }
        this.add("Bundle-Developers", sb.toString());
    }

    private void doBundleContributors(BundleContributors annotation) throws IOException {
        StringBuilder sb = new StringBuilder(annotation.value());
        if (annotation.name() != null) {
            sb.append(";name='");
            this.escape(sb, annotation.name());
            sb.append("'");
        }
        if (annotation.roles() != null) {
            sb.append(";roles='");
            this.escape(sb, annotation.roles());
            sb.append("'");
        }
        if (annotation.organizationUrl() != null) {
            sb.append(";organizationUrl='");
            this.escape(sb, annotation.organizationUrl());
            sb.append("'");
        }
        if (annotation.organization() != null) {
            sb.append(";organization='");
            this.escape(sb, annotation.organization());
            sb.append("'");
        }
        if (annotation.timezone() != 0) {
            sb.append(";timezone=").append(annotation.timezone());
        }
        this.add("Bundle-Contributors", sb.toString());
    }

    private void doBundeCopyright(BundleCopyright annotation) throws IOException {
        this.add("Bundle-Copyright", annotation.value());
    }

    private void doBundleDocURL(BundleDocURL annotation) throws IOException {
        this.add("Bundle-DocURL", annotation.value());
    }

    private void doBundleCategory(BundleCategory annotation) throws IOException {
        if (annotation.custom() != null) {
            for (String string : annotation.custom()) {
                this.add("Bundle-Category", string);
            }
        }
        if (annotation.value() != null) {
            for (Category category : annotation.value()) {
                this.add("Bundle-Category", category.toString());
            }
        }
    }

    private void doProvideCapability(Annotation a) throws Exception {
        ProvideCapability annotation = a.getAnnotation(ProvideCapability.class);
        Parameters p = new Parameters();
        Attrs attrs = this.getAttributes(a, "ns");
        this.directivesAndVersion(attrs, "uses", "mandatory", "effective");
        p.put(annotation.ns(), attrs);
        String value = attrs.remove("name");
        if (value != null) {
            attrs.put(annotation.ns(), value);
        }
        value = attrs.remove("value");
        String s = p.toString();
        if (value != null) {
            s = s + ";" + annotation.value();
        }
        this.add("Provide-Capability", s);
    }

    private void doRequireCapability(Annotation a) throws Exception {
        RequireCapability annotation = a.getAnnotation(RequireCapability.class);
        Parameters p = new Parameters();
        Attrs attrs = this.getAttributes(a, "ns");
        this.directivesAndVersion(attrs, "filter", "effective", "resolution");
        this.replaceParameters(attrs);
        if ("".equals(attrs.get("filter:"))) {
            attrs.remove("filter:");
        }
        p.put(annotation.ns(), attrs);
        String s = p.toString();
        String extra = annotation.extra();
        if (extra != null && (extra = extra.trim()).length() > 0) {
            s = s + ";" + extra;
        }
        this.add("Require-Capability", s);
    }

    private void replaceParameters(Attrs attrs) throws IllegalArgumentException {
        for (Map.Entry<String, String> entry : attrs.entrySet()) {
            boolean modified = false;
            StringBuffer sb = new StringBuffer();
            Matcher matcher = SIMPLE_PARAM_PATTERN.matcher(entry.getValue());
            while (matcher.find()) {
                modified = true;
                String key = matcher.group(1);
                String substitution = attrs.get(key);
                if (substitution == null) {
                    matcher.appendReplacement(sb, "");
                    sb.append(matcher.group(0));
                    continue;
                }
                if (SIMPLE_PARAM_PATTERN.matcher(substitution).find()) {
                    throw new IllegalArgumentException("nested substitutions not permitted");
                }
                matcher.appendReplacement(sb, substitution);
            }
            if (!modified) continue;
            matcher.appendTail(sb);
            entry.setValue(sb.toString());
        }
    }

    private void doLicense(Annotation a) throws Exception {
        BundleLicense annotation = a.getAnnotation(BundleLicense.class);
        Parameters p = new Parameters();
        p.put(annotation.name(), this.getAttributes(a, "name"));
        this.add("Bundle-License", p.toString());
    }

    private void directivesAndVersion(Attrs attrs, String ... directives) {
        for (String directive : directives) {
            String s = attrs.remove(directive);
            if (s == null) continue;
            attrs.put(directive + ":", s);
        }
        String remove = attrs.remove("version");
        if (remove != null) {
            attrs.putTyped("version", Version.parseVersion(remove));
        }
    }

    private Attrs getAttributes(Annotation a, String ... ignores) {
        Attrs attrs = new Attrs();
        block0: for (String key : a.keySet()) {
            for (String ignore : ignores) {
                if (key.equals(ignore)) continue block0;
            }
            attrs.putTyped(key, a.get(key));
        }
        return attrs;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void add(String name, String value) throws IOException {
        if (value == null) {
            return;
        }
        Processor next = new Processor(this.analyzer);
        next.setProperty("@class", this.current.getFQN());
        next.setProperty("@class-short", this.current.getClassName().getShortName());
        Descriptors.PackageRef pref = this.current.getClassName().getPackageRef();
        next.setProperty("@package", pref.getFQN());
        Attrs info = this.analyzer.getClasspathExports().get(pref);
        if (info == null) {
            info = this.analyzer.getContained().get(pref);
        }
        if (info != null && info.containsKey("version")) {
            next.setProperty("@version", info.get("version"));
        }
        Macro macro = next.getReplacer();
        boolean prev = macro.setNosystem(true);
        try {
            value = macro.process(value);
            this.headers.add(name, value);
            next.close();
        }
        finally {
            macro.setNosystem(prev);
        }
    }

    public String getHeader(String name) {
        String value = this.analyzer.getProperty(name);
        if (this.headers.containsKey(name)) {
            TreeSet set = new TreeSet((Collection)this.headers.get(name));
            String header = Strings.join(set);
            if (value == null) {
                return header;
            }
            return value + "," + header;
        }
        return value;
    }

    private <T> boolean containsAny(Set<T> a, Set<T> b) {
        for (T aa : a) {
            if (!b.contains(aa)) continue;
            return true;
        }
        return false;
    }

    private void escape(StringBuilder app, String[] s) throws IOException {
        String joined = Strings.join(s);
        this.escape(app, joined);
    }

    private void escape(StringBuilder app, String s) throws IOException {
        Processor.quote(app, s);
    }
}

