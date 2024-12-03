/*
 * Decompiled with CFR 0.152.
 */
package org.twdata.pkgscanner;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.twdata.pkgscanner.ExportPackage;
import org.twdata.pkgscanner.InternalScanner;
import org.twdata.pkgscanner.pattern.CompiledPattern;
import org.twdata.pkgscanner.pattern.PatternFactory;
import org.twdata.pkgscanner.pattern.SimpleWildcardPatternFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PackageScanner {
    private Patterns packagePatterns = new Patterns(new String[]{"com.*", "net.*", "org.*"}, new String[0]);
    private Patterns jarPatterns = new Patterns(new String[]{"*"}, new String[0]);
    private ClassLoader classLoader;
    private VersionMapping[] versionMappings = new VersionMapping[0];
    private PatternFactory patternFactory = new SimpleWildcardPatternFactory();
    private boolean debug = false;

    public static void main(String[] args) {
        Collection<ExportPackage> exports = new PackageScanner().select(PackageScanner.jars(PackageScanner.include("*.jar", "bar-*.jar"), PackageScanner.exclude("*dira*.jar")), PackageScanner.packages(PackageScanner.include("org.*", "com.*", "javax.*", "org.twdata.pkgscanner.*"), PackageScanner.exclude("com.intellij.*"))).withMappings(PackageScanner.mapPackage("org.twdata.pkgscanner.foo").toVersion("2.0.4")).scan();
        StringBuilder sb = new StringBuilder();
        sb.append("Export-Package: \n");
        Iterator<ExportPackage> i = exports.iterator();
        while (i.hasNext()) {
            ExportPackage pkg = i.next();
            sb.append("\t");
            sb.append(pkg.getPackageName());
            if (pkg.getVersion() != null) {
                sb.append(";version=").append(pkg.getVersion());
            }
            if (!i.hasNext()) continue;
            sb.append(", \n");
        }
        System.out.println(sb.toString());
    }

    public PackageScanner select(Patterns jars, Patterns packages) {
        this.jarPatterns = jars;
        this.packagePatterns = packages;
        return this;
    }

    public Collection<ExportPackage> scan() {
        this.initPatterns();
        List<String> roots = this.packagePatterns.getRoots();
        InternalScanner scanner = new InternalScanner(this.getClassLoader(), this.versionMappings, this.debug);
        Collection<ExportPackage> exports = scanner.findInPackages(new PatternTest(), roots.toArray(new String[roots.size()]));
        return exports;
    }

    public Collection<ExportPackage> scan(URL ... urls) {
        this.initPatterns();
        InternalScanner scanner = new InternalScanner(this.getClassLoader(), this.versionMappings, this.debug);
        Collection<ExportPackage> exports = scanner.findInUrls(new PatternTest(), urls);
        return exports;
    }

    private void initPatterns() {
        this.jarPatterns.setPatternFactory(this.patternFactory);
        this.packagePatterns.setPatternFactory(this.patternFactory);
        for (VersionMapping mapping : this.versionMappings) {
            mapping.setPatternFactory(this.patternFactory);
        }
    }

    public PackageScanner useClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }

    public PackageScanner withMappings(VersionMapping ... mappings) {
        this.versionMappings = mappings;
        return this;
    }

    public PackageScanner withMappings(Map<String, String> mappings) {
        ArrayList<VersionMapping> versions = new ArrayList<VersionMapping>();
        if (mappings != null) {
            for (Map.Entry<String, String> entry : mappings.entrySet()) {
                versions.add(new VersionMapping(entry.getKey()).toVersion(entry.getValue()));
            }
        }
        return this.withMappings(versions.toArray(new VersionMapping[versions.size()]));
    }

    public PackageScanner enableDebug() {
        this.debug = true;
        return this;
    }

    public PackageScanner usePatternFactory(PatternFactory factory) {
        this.patternFactory = factory;
        return this;
    }

    public static VersionMapping mapPackage(String name) {
        return new VersionMapping(name);
    }

    public static String[] include(String ... includes) {
        return includes;
    }

    public static String[] exclude(String ... includes) {
        return includes;
    }

    public static Patterns jars(String[] includes, String[] excludes) {
        return new Patterns(includes, excludes);
    }

    public static Patterns jars(String[] includes) {
        return new Patterns(includes, new String[0]);
    }

    public static Patterns packages(String[] includes, String[] excludes) {
        return new Patterns(includes, excludes);
    }

    public static Patterns packages(String[] includes) {
        return new Patterns(includes, new String[0]);
    }

    ClassLoader getClassLoader() {
        return this.classLoader == null ? Thread.currentThread().getContextClassLoader() : this.classLoader;
    }

    private class PatternTest
    implements InternalScanner.Test {
        private PatternTest() {
        }

        public boolean matchesPackage(String pkg) {
            return PackageScanner.this.packagePatterns.match(pkg);
        }

        public boolean matchesJar(String name) {
            return PackageScanner.this.jarPatterns.match(name);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Patterns {
        private String[] origIncludes;
        private String[] origExcludes;
        private List<CompiledPattern> includes;
        private List<CompiledPattern> excludes;
        private PatternFactory factory;

        public Patterns(String[] includes, String[] excludes) {
            this.origIncludes = includes;
            this.origExcludes = excludes;
        }

        void setPatternFactory(PatternFactory factory) {
            this.factory = factory;
        }

        boolean match(String val) {
            if (this.includes == null) {
                this.compilePatterns();
            }
            for (CompiledPattern ptn : this.includes) {
                if (!ptn.matches(val)) continue;
                for (CompiledPattern exptn : this.excludes) {
                    if (!exptn.matches(val)) continue;
                    return false;
                }
                return true;
            }
            return false;
        }

        List<String> getRoots() {
            ArrayList<String> roots = new ArrayList<String>();
            for (String inc : this.origIncludes) {
                int dotPos;
                String root = inc;
                int starPos = root.indexOf("*");
                if (starPos > -1 && (dotPos = root.lastIndexOf(".", starPos)) > -1) {
                    root = root.substring(0, dotPos);
                }
                roots.add(root);
            }
            return roots;
        }

        private void compilePatterns() {
            this.includes = new ArrayList<CompiledPattern>();
            for (String ptn : this.origIncludes) {
                this.includes.add(this.factory.compile(ptn));
            }
            this.excludes = new ArrayList<CompiledPattern>();
            for (String ptn : this.origExcludes) {
                this.excludes.add(this.factory.compile(ptn));
            }
        }
    }

    public static class VersionMapping {
        private CompiledPattern compiledPattern;
        private String packagePattern;
        private String toVersion;
        private PatternFactory factory;

        public VersionMapping(String packagePattern) {
            this(packagePattern, null);
        }

        public VersionMapping(String packagePattern, String version) {
            this.packagePattern = packagePattern;
            this.toVersion = version;
        }

        void setPatternFactory(PatternFactory factory) {
            this.factory = factory;
        }

        public VersionMapping toVersion(String toVersion) {
            this.toVersion = toVersion;
            return this;
        }

        String getVersion() {
            return this.toVersion;
        }

        String getPackagePattern() {
            return this.packagePattern;
        }

        boolean matches(String pkg) {
            if (this.compiledPattern == null) {
                this.compiledPattern = this.factory.compile(this.packagePattern);
            }
            return this.compiledPattern.matches(pkg);
        }
    }
}

