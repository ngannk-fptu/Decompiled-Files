/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.maven;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MavenDependencyGraph {
    static final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    static final XPathFactory xpathFactory = XPathFactory.newInstance();
    final List<Artifact> dependencies = new ArrayList<Artifact>();
    final List<URL> repositories = new ArrayList<URL>();
    final XPath xpath = xpathFactory.newXPath();
    final Map<URI, Artifact> cache = new HashMap<URI, Artifact>();
    Artifact root;

    public void addRepository(URL repository) {
        this.repositories.add(repository);
    }

    public Artifact getArtifact(String groupId, String artifactId, String version) {
        for (URL repository : this.repositories) {
            String path = this.getPath(repository.toString(), groupId, artifactId, version);
            try {
                URI url = new URL(path + ".pom").toURI();
                if (this.cache.containsKey(url)) {
                    return this.cache.get(url);
                }
                return new Artifact(url.toURL());
            }
            catch (Exception e) {
                System.err.println("Failed to get " + artifactId + " from " + repository);
            }
        }
        return null;
    }

    private String getPath(String path, String groupId, String artifactId, String version) {
        StringBuilder sb = new StringBuilder();
        sb.append(path);
        if (!path.endsWith("/")) {
            sb.append("/");
        }
        sb.append(groupId.replace('.', '/'));
        sb.append('/');
        sb.append(artifactId);
        sb.append('/');
        sb.append(version);
        sb.append('/');
        sb.append(artifactId);
        sb.append('-');
        sb.append(version);
        return null;
    }

    public void addArtifact(Artifact artifact) throws Exception {
        if (this.root == null) {
            this.root = new Artifact(null);
        }
        this.root.add(artifact);
    }

    public class Artifact {
        String groupId;
        String artifactId;
        String version;
        Scope scope = Scope.COMPILE;
        boolean optional;
        String type;
        URL url;
        List<Artifact> dependencies = new ArrayList<Artifact>();

        public Artifact(URL url) throws Exception {
            if (url != null) {
                this.url = url;
                DocumentBuilder db = docFactory.newDocumentBuilder();
                Document doc = db.parse(url.toString());
                Node node = (Node)MavenDependencyGraph.this.xpath.evaluate("/project", doc, XPathConstants.NODE);
                this.groupId = MavenDependencyGraph.this.xpath.evaluate("groupId", node);
                this.artifactId = MavenDependencyGraph.this.xpath.evaluate("artifactId", node);
                this.version = MavenDependencyGraph.this.xpath.evaluate("version", node);
                this.type = MavenDependencyGraph.this.xpath.evaluate("type", node);
                this.optional = (Boolean)MavenDependencyGraph.this.xpath.evaluate("optinal", node, XPathConstants.BOOLEAN);
                String scope = MavenDependencyGraph.this.xpath.evaluate("scope", node);
                if (scope != null && scope.length() > 0) {
                    this.scope = Scope.valueOf(scope.toUpperCase());
                }
                NodeList evaluate = (NodeList)MavenDependencyGraph.this.xpath.evaluate("//dependencies/dependency", doc, XPathConstants.NODESET);
                for (int i = 0; i < evaluate.getLength(); ++i) {
                    Node childNode = evaluate.item(i);
                    Artifact artifact = MavenDependencyGraph.this.getArtifact(MavenDependencyGraph.this.xpath.evaluate("groupId", childNode), MavenDependencyGraph.this.xpath.evaluate("artifactId", childNode), MavenDependencyGraph.this.xpath.evaluate("version", childNode));
                    this.add(artifact);
                }
            }
        }

        public void add(Artifact artifact) {
            this.dependencies.add(artifact);
        }

        public String toString() {
            return this.groupId + "." + this.artifactId + "-" + this.version + "[" + (Object)((Object)this.scope) + "," + this.optional + "]";
        }

        public String getPath() throws URISyntaxException {
            return this.groupId.replace('.', '/') + "/" + this.artifactId + "/" + this.version + "/" + this.artifactId + "-" + this.version;
        }
    }

    static enum Scope {
        COMPILE,
        RUNTIME,
        TEST,
        PROVIDED,
        SYSTEM,
        IMPORT;

    }
}

