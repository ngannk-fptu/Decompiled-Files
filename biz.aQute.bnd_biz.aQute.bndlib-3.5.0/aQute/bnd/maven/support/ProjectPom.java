/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.maven.support;

import aQute.bnd.maven.support.CachedPom;
import aQute.bnd.maven.support.Maven;
import aQute.bnd.maven.support.Pom;
import aQute.lib.io.IO;
import aQute.lib.utf8properties.UTF8Properties;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ProjectPom
extends Pom {
    final List<URI> repositories = new ArrayList<URI>();
    final Properties properties = new UTF8Properties();
    String packaging;
    String url;
    static final Pattern MACRO = Pattern.compile("(\\$\\{\\s*([^}\\s]+)\\s*\\})");

    ProjectPom(Maven maven, File pomFile) throws Exception {
        super(maven, pomFile, pomFile.toURI());
    }

    @Override
    protected void parse(Document doc, XPath xp) throws Exception {
        this.packaging = xp.evaluate("project/packaging", doc);
        this.url = xp.evaluate("project/url", doc);
        Node parent = (Node)xp.evaluate("project/parent", doc, XPathConstants.NODE);
        if (parent != null && parent.hasChildNodes()) {
            Pom parentPom;
            File parentFile = IO.getFile(this.getPomFile().getParentFile(), "../pom.xml");
            String parentGroupId = xp.evaluate("groupId", parent).trim();
            String parentArtifactId = xp.evaluate("artifactId", parent).trim();
            String parentVersion = xp.evaluate("version", parent).trim();
            String parentPath = xp.evaluate("relativePath", parent).trim();
            if (parentPath != null && parentPath.length() != 0) {
                parentFile = IO.getFile(this.getPomFile().getParentFile(), parentPath);
            }
            if (parentFile.isFile()) {
                parentPom = new ProjectPom(this.maven, parentFile);
                parentPom.parse();
                this.dependencies.addAll(parentPom.dependencies);
                Enumeration<?> e = parentPom.properties.propertyNames();
                while (e.hasMoreElements()) {
                    String key = (String)e.nextElement();
                    if (this.properties.contains(key)) continue;
                    this.properties.put(key, parentPom.properties.get(key));
                }
                this.repositories.addAll(parentPom.repositories);
                this.setNames(parentPom);
            } else {
                parentPom = this.maven.getPom(parentGroupId, parentArtifactId, parentVersion, new URI[0]);
                this.dependencies.addAll(((CachedPom)parentPom).dependencies);
                this.setNames(parentPom);
            }
        }
        NodeList propNodes = (NodeList)xp.evaluate("project/properties/*", doc, XPathConstants.NODESET);
        for (int i = 0; i < propNodes.getLength(); ++i) {
            Node node = propNodes.item(i);
            String key = node.getNodeName();
            String value = node.getTextContent();
            if (key == null || key.length() == 0) {
                throw new IllegalArgumentException("Pom has an empty or null key");
            }
            if (value == null || value.length() == 0) {
                throw new IllegalArgumentException("Pom has an empty or null value for property " + key);
            }
            this.properties.setProperty(key, value.trim());
        }
        NodeList repos = (NodeList)xp.evaluate("project/repositories/repository/url", doc, XPathConstants.NODESET);
        for (int i = 0; i < repos.getLength(); ++i) {
            Node node = repos.item(i);
            String URIString = node.getTextContent().trim();
            URI uri = new URI(URIString);
            if (uri.getScheme() == null) {
                uri = IO.getFile(this.pomFile.getParentFile(), URIString).toURI();
            }
            this.repositories.add(uri);
        }
        super.parse(doc, xp);
    }

    private void setNames(Pom pom) throws Exception {
        if (this.artifactId == null || this.artifactId.length() == 0) {
            this.artifactId = pom.getArtifactId();
        }
        if (this.groupId == null || this.groupId.length() == 0) {
            this.groupId = pom.getGroupId();
        }
        if (this.version == null || this.version.length() == 0) {
            this.version = pom.getVersion();
        }
        this.description = this.description == null ? pom.getDescription() : pom.getDescription() + "\n" + this.description;
    }

    public Set<Pom> getDependencies(Pom.Scope action) throws Exception {
        return this.getDependencies(action, this.repositories.toArray(new URI[0]));
    }

    @Override
    protected String replace(String in) {
        System.err.println("Replce: " + in);
        if (in == null) {
            System.err.println("null??");
            in = "<<???>>";
        }
        Matcher matcher = MACRO.matcher(in);
        int last = 0;
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            int n = matcher.start();
            sb.append(in, last, n);
            String replacement = this.get(matcher.group(2));
            if (replacement == null) {
                sb.append(matcher.group(1));
            } else {
                sb.append(replacement);
            }
            last = matcher.end();
        }
        if (last == 0) {
            return in;
        }
        sb.append(in, last, in.length());
        return sb.toString();
    }

    private String get(String key) {
        if (key.equals("pom.artifactId")) {
            return this.artifactId;
        }
        if (key.equals("pom.groupId")) {
            return this.groupId;
        }
        if (key.equals("pom.version")) {
            return this.version;
        }
        if (key.equals("pom.name")) {
            return this.name;
        }
        String prop = this.properties.getProperty(key);
        if (prop != null) {
            return prop;
        }
        return System.getProperty(key);
    }

    public Properties getProperties() {
        return this.properties;
    }

    public String getPackaging() {
        return this.packaging;
    }

    public String getUrl() {
        return this.url;
    }

    public String getProperty(String key) {
        String s = this.properties.getProperty(key);
        return this.replace(s);
    }

    @Override
    public File getArtifact() throws Exception {
        return null;
    }

    static class Rover {
        final Rover previous;
        final Pom.Dependency dependency;

        public Rover(Rover rover, Pom.Dependency d) {
            this.previous = rover;
            this.dependency = d;
        }

        public boolean excludes(String name) {
            return this.dependency.exclusions.contains(name) && this.previous != null && this.previous.excludes(name);
        }
    }
}

