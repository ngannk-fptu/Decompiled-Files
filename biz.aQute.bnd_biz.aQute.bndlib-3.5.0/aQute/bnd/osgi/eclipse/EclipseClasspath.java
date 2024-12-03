/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi.eclipse;

import aQute.service.reporter.Reporter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class EclipseClasspath {
    static DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder db;
    File project;
    File workspace;
    Set<File> sources = new LinkedHashSet<File>();
    Set<File> allSources = new LinkedHashSet<File>();
    Set<File> classpath = new LinkedHashSet<File>();
    List<File> dependents = new ArrayList<File>();
    File output;
    boolean recurse = true;
    Set<File> exports = new LinkedHashSet<File>();
    Map<String, String> properties = new HashMap<String, String>();
    Reporter reporter;
    int options;
    Set<File> bootclasspath = new LinkedHashSet<File>();
    public static final int DO_VARIABLES = 1;
    static Pattern PATH = Pattern.compile("([A-Z_]+)/(.*)");

    public EclipseClasspath(Reporter reporter, File workspace, File project, int options) throws Exception {
        this.project = project.getCanonicalFile();
        this.workspace = workspace.getCanonicalFile();
        this.reporter = reporter;
        this.db = documentBuilderFactory.newDocumentBuilder();
        this.parse(this.project, true);
        this.db = null;
    }

    public EclipseClasspath(Reporter reporter, File workspace, File project) throws Exception {
        this(reporter, workspace, project, 0);
    }

    void parse(File project, boolean top) throws ParserConfigurationException, SAXException, IOException {
        File file = new File(project, ".classpath");
        if (!file.exists()) {
            throw new FileNotFoundException(".classpath file not found: " + file.getAbsolutePath());
        }
        Document doc = this.db.parse(file);
        NodeList nodelist = doc.getDocumentElement().getElementsByTagName("classpathentry");
        if (nodelist == null) {
            throw new IllegalArgumentException("Can not find classpathentry in classpath file");
        }
        for (int i = 0; i < nodelist.getLength(); ++i) {
            Node node = nodelist.item(i);
            NamedNodeMap attrs = node.getAttributes();
            String kind = this.get(attrs, "kind");
            if ("src".equals(kind)) {
                String path = this.get(attrs, "path");
                if (path.startsWith("/")) {
                    File subProject = this.getFile(this.workspace, project, path);
                    if (this.recurse) {
                        this.parse(subProject, false);
                    }
                    this.dependents.add(subProject.getCanonicalFile());
                    continue;
                }
                File src = this.getFile(this.workspace, project, path);
                this.allSources.add(src);
                if (!top) continue;
                this.sources.add(src);
                continue;
            }
            if ("lib".equals(kind)) {
                String path = this.get(attrs, "path");
                boolean exported = "true".equalsIgnoreCase(this.get(attrs, "exported"));
                if (!top && !exported) continue;
                File jar = this.getFile(this.workspace, project, path);
                if (jar.getName().startsWith("ee.")) {
                    this.bootclasspath.add(jar);
                } else {
                    this.classpath.add(jar);
                }
                if (!exported) continue;
                this.exports.add(jar);
                continue;
            }
            if ("output".equals(kind)) {
                String path = this.get(attrs, "path");
                path = path.replace('/', File.separatorChar);
                this.output = this.getFile(this.workspace, project, path);
                this.classpath.add(this.output);
                this.exports.add(this.output);
                continue;
            }
            if ("var".equals(kind)) {
                boolean exported = "true".equalsIgnoreCase(this.get(attrs, "exported"));
                File lib = this.replaceVar(this.get(attrs, "path"));
                File slib = this.replaceVar(this.get(attrs, "sourcepath"));
                if (lib != null) {
                    this.classpath.add(lib);
                    if (exported) {
                        this.exports.add(lib);
                    }
                }
                if (slib == null) continue;
                this.sources.add(slib);
                continue;
            }
            if (!"con".equals(kind)) continue;
        }
    }

    private File getFile(File abs, File relative, String opath) {
        String path = opath.replace('/', File.separatorChar);
        File result = new File(path);
        if (result.isAbsolute() && result.isFile()) {
            return result;
        }
        if (path.startsWith(File.separator)) {
            result = abs;
            path = path.substring(1);
        } else {
            result = relative;
        }
        StringTokenizer st = new StringTokenizer(path, File.separator);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            result = new File(result, token);
        }
        if (!result.exists()) {
            System.err.println("File not found: project=" + this.project + " workspace=" + this.workspace + " path=" + opath + " file=" + result);
        }
        return result;
    }

    private File replaceVar(String path) {
        if ((this.options & 1) == 0) {
            return null;
        }
        Matcher m = PATH.matcher(path);
        if (m.matches()) {
            String var = m.group(1);
            String remainder = m.group(2);
            String base = this.properties.get(var);
            if (base != null) {
                File b = new File(base);
                File f = new File(b, remainder.replace('/', File.separatorChar));
                return f;
            }
            this.reporter.error("Can't find replacement variable for: %s", path);
        } else {
            this.reporter.error("Cant split variable path: %s", path);
        }
        return null;
    }

    private String get(NamedNodeMap map, String name) {
        Node node = map.getNamedItem(name);
        if (node == null) {
            return null;
        }
        return node.getNodeValue();
    }

    public Set<File> getClasspath() {
        return this.classpath;
    }

    public Set<File> getSourcepath() {
        return this.sources;
    }

    public File getOutput() {
        return this.output;
    }

    public List<File> getDependents() {
        return this.dependents;
    }

    public void setRecurse(boolean recurse) {
        this.recurse = recurse;
    }

    public Set<File> getExports() {
        return this.exports;
    }

    public void setProperties(Map<String, String> map) {
        this.properties = map;
    }

    public Set<File> getBootclasspath() {
        return this.bootclasspath;
    }

    public Set<File> getAllSources() {
        return this.allSources;
    }
}

