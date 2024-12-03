/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.maven.support;

import aQute.bnd.maven.support.CachedPom;
import aQute.bnd.maven.support.Maven;
import aQute.bnd.maven.support.MavenEntry;
import aQute.lib.io.IO;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class Pom {
    static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    static XPathFactory xpf = XPathFactory.newInstance();
    final Maven maven;
    final URI home;
    String groupId;
    String artifactId;
    String version;
    List<Dependency> dependencies = new ArrayList<Dependency>();
    File pomFile;
    String description = "";
    String name;

    public String getDescription() {
        return this.description;
    }

    public Pom(Maven maven, File pomFile, URI home) throws Exception {
        this.maven = maven;
        this.home = home;
        this.pomFile = pomFile;
    }

    void parse() throws Exception {
        DocumentBuilder db = dbf.newDocumentBuilder();
        System.err.println("Parsing " + this.pomFile.getAbsolutePath());
        Document doc = db.parse(this.pomFile);
        XPath xp = xpf.newXPath();
        this.parse(doc, xp);
    }

    protected void parse(Document doc, XPath xp) throws XPathExpressionException, Exception {
        this.artifactId = this.replace(xp.evaluate("project/artifactId", doc).trim(), this.artifactId);
        this.groupId = this.replace(xp.evaluate("project/groupId", doc).trim(), this.groupId);
        this.version = this.replace(xp.evaluate("project/version", doc).trim(), this.version);
        String nextDescription = xp.evaluate("project/description", doc).trim();
        if (this.description.length() != 0 && nextDescription.length() != 0) {
            this.description = this.description + "\n";
        }
        this.description = this.description + this.replace(nextDescription);
        this.name = this.replace(xp.evaluate("project/name", doc).trim(), this.name);
        NodeList list = (NodeList)xp.evaluate("project/dependencies/dependency", doc, XPathConstants.NODESET);
        for (int i = 0; i < list.getLength(); ++i) {
            Node node = list.item(i);
            Dependency dep = new Dependency();
            String scope = xp.evaluate("scope", node).trim();
            dep.scope = scope.length() == 0 ? Scope.compile : Scope.valueOf(scope);
            dep.type = xp.evaluate("type", node).trim();
            String opt = xp.evaluate("optional", node).trim();
            dep.optional = opt != null && opt.equalsIgnoreCase("true");
            dep.groupId = this.replace(xp.evaluate("groupId", node));
            dep.artifactId = this.replace(xp.evaluate("artifactId", node).trim());
            dep.version = this.replace(xp.evaluate("version", node).trim());
            this.dependencies.add(dep);
            NodeList exclusions = (NodeList)xp.evaluate("exclusions", node, XPathConstants.NODESET);
            for (int e = 0; e < exclusions.getLength(); ++e) {
                Node exc = exclusions.item(e);
                String exclGroupId = xp.evaluate("groupId", exc).trim();
                String exclArtifactId = xp.evaluate("artifactId", exc).trim();
                dep.exclusions.add(exclGroupId + "+" + exclArtifactId);
            }
        }
    }

    private String replace(String key, String dflt) {
        if (key == null || key.length() == 0) {
            return dflt;
        }
        return this.replace(key);
    }

    public String getArtifactId() throws Exception {
        return this.replace(this.artifactId);
    }

    public String getGroupId() throws Exception {
        return this.replace(this.groupId);
    }

    public String getVersion() throws Exception {
        if (this.version == null) {
            return "<not set>";
        }
        return this.replace(this.version);
    }

    public List<Dependency> getDependencies() throws Exception {
        return this.dependencies;
    }

    public Set<Pom> getDependencies(Scope scope, URI ... urls) throws Exception {
        LinkedHashSet<Pom> result = new LinkedHashSet<Pom>();
        ArrayList<Rover> queue = new ArrayList<Rover>();
        for (Dependency d : this.dependencies) {
            queue.add(new Rover(null, d));
        }
        while (!queue.isEmpty()) {
            Rover rover = (Rover)queue.remove(0);
            Dependency dep = rover.dependency;
            String groupId = this.replace(dep.groupId);
            String artifactId = this.replace(dep.artifactId);
            String version = this.replace(dep.version);
            String name = groupId + "+" + artifactId;
            if (rover.excludes(name) || dep.optional || dep.scope != scope || dep.optional) continue;
            try {
                CachedPom sub = this.maven.getPom(groupId, artifactId, version, urls);
                if (sub != null) {
                    if (result.contains(sub)) continue;
                    result.add(sub);
                    for (Dependency subd : sub.dependencies) {
                        queue.add(new Rover(rover, subd));
                    }
                    continue;
                }
                if (rover.previous != null) {
                    System.err.println("Cannot find " + dep + " from " + rover.previous.dependency);
                    continue;
                }
                System.err.println("Cannot find " + dep + " from top");
            }
            catch (Exception e) {
                if (rover.previous != null) {
                    System.err.println("Cannot find " + dep + " from " + rover.previous.dependency);
                    continue;
                }
                System.err.println("Cannot find " + dep + " from top");
            }
        }
        return result;
    }

    protected String replace(String in) {
        System.err.println("replace: " + in);
        if (in == null) {
            return "null";
        }
        if ("${pom.version}".equals(in = in.trim()) || "${version}".equals(in) || "${project.version}".equals(in)) {
            return this.version;
        }
        if ("${basedir}".equals(in)) {
            return this.pomFile.getParentFile().getAbsolutePath();
        }
        if ("${pom.name}".equals(in) || "${project.name}".equals(in)) {
            return this.name;
        }
        if ("${pom.artifactId}".equals(in) || "${project.artifactId}".equals(in)) {
            return this.artifactId;
        }
        if ("${pom.groupId}".equals(in) || "${project.groupId}".equals(in)) {
            return this.groupId;
        }
        return in;
    }

    public String toString() {
        return this.groupId + "+" + this.artifactId + "-" + this.version;
    }

    public File getLibrary(Scope action, URI ... repositories) throws Exception {
        MavenEntry entry = this.maven.getEntry(this);
        File file = new File(entry.dir, (Object)((Object)action) + ".lib");
        if (file.isFile() && file.lastModified() >= this.getPomFile().lastModified()) {
            return file;
        }
        IO.delete(file);
        try (PrintWriter writer = IO.writer(file);){
            this.doEntry(writer, this);
            for (Pom dep : this.getDependencies(action, repositories)) {
                this.doEntry(writer, dep);
            }
        }
        return file;
    }

    private void doEntry(Writer writer, Pom dep) throws IOException, Exception {
        writer.append(dep.getGroupId());
        writer.append("+");
        writer.append(dep.getArtifactId());
        writer.append(";version=\"");
        writer.append(dep.getVersion());
        writer.append("\"\n");
    }

    public File getPomFile() {
        return this.pomFile;
    }

    public String getName() {
        return this.name;
    }

    public abstract File getArtifact() throws Exception;

    static {
        dbf.setNamespaceAware(false);
    }

    static class Rover {
        final Rover previous;
        final Dependency dependency;

        public Rover(Rover rover, Dependency d) {
            this.previous = rover;
            this.dependency = d;
        }

        public boolean excludes(String name) {
            return this.dependency.exclusions.contains(name) && this.previous != null && this.previous.excludes(name);
        }
    }

    public class Dependency {
        Scope scope;
        String type;
        boolean optional;
        String groupId;
        String artifactId;
        String version;
        Set<String> exclusions = new HashSet<String>();

        public Scope getScope() {
            return this.scope;
        }

        public String getType() {
            return this.type;
        }

        public boolean isOptional() {
            return this.optional;
        }

        public String getGroupId() {
            return Pom.this.replace(this.groupId);
        }

        public String getArtifactId() {
            return Pom.this.replace(this.artifactId);
        }

        public String getVersion() {
            return Pom.this.replace(this.version);
        }

        public Set<String> getExclusions() {
            return this.exclusions;
        }

        public Pom getPom() throws Exception {
            return Pom.this.maven.getPom(this.groupId, this.artifactId, this.version, new URI[0]);
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Dependency [");
            if (this.groupId != null) {
                builder.append("groupId=").append(this.groupId).append(", ");
            }
            if (this.artifactId != null) {
                builder.append("artifactId=").append(this.artifactId).append(", ");
            }
            if (this.version != null) {
                builder.append("version=").append(this.version).append(", ");
            }
            if (this.type != null) {
                builder.append("type=").append(this.type).append(", ");
            }
            if (this.scope != null) {
                builder.append("scope=").append((Object)this.scope).append(", ");
            }
            builder.append("optional=").append(this.optional).append(", ");
            if (this.exclusions != null) {
                builder.append("exclusions=").append(this.exclusions);
            }
            builder.append("]");
            return builder.toString();
        }
    }

    public static enum Scope {
        compile,
        runtime,
        system,
        import_,
        provided,
        test;

    }
}

