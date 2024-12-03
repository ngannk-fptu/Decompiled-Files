/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build;

import aQute.bnd.build.Project;
import aQute.bnd.version.Version;
import aQute.lib.io.IO;
import java.io.File;
import java.io.IOException;
import java.util.Formatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PackageInfo {
    private static final String PACKAGE_INFO_JAVA = "package-info.java";
    private static final String PACKAGEINFO = "packageinfo";
    private static final Pattern MODERN_P = Pattern.compile("@\\s*[a-zA-Z0-9_$.]*\\s*Version\\((?:\\s*value\\s*=\\s*)?\"([0-9]{1,9}(:?\\.[0-9]{1,9}(:?\\.[0-9]{1,9}(:?\\.[0-9A-Za-z_-]+)?)?)?)\"\\)");
    private static final Pattern CLASSIC_P = Pattern.compile("\\s*version\\s*(?:\\s|:|=)\\s*([0-9]{1,9}(:?\\.[0-9]{1,9}(:?\\.[0-9]{1,9}(:?\\.[0-9A-Za-z_-]+)?)?)?)");
    private static final Pattern MODERN_PACKAGE_P = Pattern.compile("package[^;]*;");
    private final Project project;

    PackageInfo(Project project) {
        this.project = project;
    }

    public Version getPackageInfo(String packageName) throws Exception {
        File target = this.getFile(packageName);
        if (target != null && target.isFile()) {
            Version v = this.getVersion(target, this.getPattern(target));
            if (v == null && this.isModern(target)) {
                target = new File(target.getParentFile(), PACKAGEINFO);
                v = this.getVersion(target, this.getPattern(target));
            }
            if (v != null) {
                return v;
            }
        }
        return Version.emptyVersion;
    }

    public boolean setPackageInfo(String packageName, Version version) throws Exception {
        File target = this.getFile(packageName);
        if (target == null || !target.getParentFile().isDirectory()) {
            return false;
        }
        if (target.isFile()) {
            String versionAnnotation;
            if (this.replace(target, version, this.getPattern(target))) {
                return true;
            }
            if (this.isModern(target) && (versionAnnotation = this.getVersionAnnotation()) != null) {
                String content = IO.collect(target);
                Matcher m = MODERN_PACKAGE_P.matcher(content);
                if (m.find()) {
                    content = m.replaceFirst("@Version(\"" + version + "\")\n$0\nimport " + versionAnnotation + ";");
                    IO.store((Object)content, target);
                    return true;
                }
                return false;
            }
            target = new File(target.getParentFile(), PACKAGEINFO);
        }
        String content = this.getContent(this.isModern(target), packageName, version);
        IO.store((Object)content, target);
        return true;
    }

    private String getVersionAnnotation() {
        String versionAnnotation = this.project.getProperty("-packageinfotype");
        if (versionAnnotation == null) {
            return null;
        }
        if ("osgi".equals(versionAnnotation)) {
            return "org.osgi.annotation.versioning.Version";
        }
        if ("bnd".equals(versionAnnotation)) {
            return "aQute.bnd.annotation.Version";
        }
        if (PACKAGEINFO.equals(versionAnnotation)) {
            return null;
        }
        return versionAnnotation;
    }

    private String getContent(boolean modern, String packageName, Version version) {
        try (Formatter f = new Formatter();){
            if (modern) {
                f.format("@Version(\"%s\")\n", version);
                f.format("package %s;\n", packageName);
                f.format("import %s;\n", this.getVersionAnnotation());
            } else {
                f.format("version %s\n", version);
            }
            String string = f.toString();
            return string;
        }
    }

    private Pattern getPattern(File target) {
        if (this.isModern(target)) {
            return MODERN_P;
        }
        return CLASSIC_P;
    }

    private boolean isModern(File target) {
        return target.getName().endsWith(".java");
    }

    private boolean replace(File target, Version newVersion, Pattern pattern) throws IOException {
        String content = IO.collect(target);
        Matcher m = pattern.matcher(content);
        if (!m.find()) {
            return false;
        }
        Version oldVersion = new Version(m.group(1));
        if (newVersion.compareTo(oldVersion) == 0) {
            return true;
        }
        return this.replace(newVersion, content, m, target);
    }

    private boolean replace(Version newVersion, String content, Matcher m, File target) throws IOException {
        StringBuilder output = new StringBuilder();
        output.append(content, 0, m.start(1));
        output.append(newVersion);
        output.append(content, m.end(1), m.regionEnd());
        IO.store((Object)output, target);
        return true;
    }

    private File getFile(String packageName) throws Exception {
        String relativePackagePath = packageName.replace('.', '/');
        File first = null;
        for (File srcDir : this.project.getSourcePath()) {
            File target;
            File packageDir;
            if (!srcDir.isDirectory() || !(packageDir = IO.getFile(srcDir, relativePackagePath)).isDirectory()) continue;
            if (first == null) {
                first = packageDir;
            }
            if ((target = new File(packageDir, PACKAGE_INFO_JAVA)).isFile()) {
                return target;
            }
            target = new File(packageDir, PACKAGEINFO);
            if (!target.isFile()) continue;
            return target;
        }
        if (first == null) {
            return null;
        }
        String versionAnnotation = this.getVersionAnnotation();
        if (versionAnnotation == null) {
            return new File(first, PACKAGEINFO);
        }
        return new File(first, PACKAGE_INFO_JAVA);
    }

    private Version getVersion(File source, Pattern pattern) throws IOException {
        if (!source.isFile()) {
            return null;
        }
        String content = IO.collect(source);
        Matcher m = pattern.matcher(content);
        if (!m.find()) {
            return null;
        }
        return new Version(m.group(1));
    }
}

