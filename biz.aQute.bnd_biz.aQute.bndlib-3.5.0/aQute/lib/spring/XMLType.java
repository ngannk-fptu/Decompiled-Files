/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.spring;

import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Resource;
import aQute.lib.io.IO;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XMLType {
    Transformer transformer;
    Pattern paths;
    String root;
    static Pattern QN = Pattern.compile("[_A-Za-z$][_A-Za-z0-9$]*(\\.[_A-Za-z$][_A-Za-z0-9$]*)*");

    public XMLType(URL source, String root, String paths) throws Exception {
        this.transformer = this.getTransformer(source);
        this.paths = Pattern.compile(paths);
        this.root = root;
    }

    public Set<String> analyze(InputStream in) throws Exception {
        HashSet<String> refers = new HashSet<String>();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        StreamResult r = new StreamResult(bout);
        StreamSource s = new StreamSource(in);
        this.transformer.transform(s, r);
        try (BufferedReader br = IO.reader(bout.toString("UTF-8"));){
            String line = br.readLine();
            while (line != null) {
                if ((line = line.trim()).length() > 0) {
                    String[] parts = line.split("\\s*,\\s*");
                    for (int i = 0; i < parts.length; ++i) {
                        String pack = XMLType.toPackage(parts[i]);
                        if (pack == null) continue;
                        refers.add(pack);
                    }
                }
                line = br.readLine();
            }
        }
        return refers;
    }

    static String toPackage(String fqn) {
        int n = fqn.lastIndexOf(46);
        if (n < 0 || n + 1 >= fqn.length()) {
            return null;
        }
        char c = fqn.charAt(n + 1);
        if (Character.isJavaIdentifierStart(c) && Character.isUpperCase(c)) {
            String other = fqn.substring(0, n);
            return XMLType.toPackage(other);
        }
        return fqn;
    }

    public boolean analyzeJar(Analyzer analyzer) throws Exception {
        Jar jar = analyzer.getJar();
        Map<String, Resource> dir = jar.getDirectories().get(this.root);
        if (dir == null || dir.isEmpty()) {
            Resource resource = jar.getResource(this.root);
            if (resource != null) {
                this.process(analyzer, this.root, resource);
            }
            return false;
        }
        for (Map.Entry<String, Resource> entry : dir.entrySet()) {
            String path = entry.getKey();
            Resource resource = entry.getValue();
            if (!this.paths.matcher(path).matches()) continue;
            this.process(analyzer, path, resource);
        }
        return false;
    }

    private void process(Analyzer analyzer, String path, Resource resource) {
        try {
            Set<String> set;
            try (InputStream in = resource.openInputStream();){
                set = this.analyze(in);
            }
            Iterator<String> r = set.iterator();
            while (r.hasNext()) {
                Descriptors.PackageRef pack = analyzer.getPackageRef(r.next());
                if (!QN.matcher(pack.getFQN()).matches()) {
                    analyzer.warning("Package does not seem a package in spring resource (%s): %s", path, pack);
                }
                if (analyzer.getReferred().containsKey(pack)) continue;
                analyzer.getReferred().put(pack);
            }
        }
        catch (Exception e) {
            analyzer.error("Unexpected exception in processing spring resources(%s): %s", path, e);
        }
    }

    protected Transformer getTransformer(URL url) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        StreamSource source = new StreamSource(url.openStream());
        return tf.newTransformer(source);
    }
}

