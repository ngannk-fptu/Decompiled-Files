/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.spring;

import aQute.bnd.annotation.plugin.BndPlugin;
import aQute.bnd.header.Attrs;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Resource;
import aQute.bnd.service.AnalyzerPlugin;
import aQute.lib.io.IO;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

@BndPlugin(name="spring")
public class SpringComponent
implements AnalyzerPlugin {
    static Transformer transformer;
    static Pattern SPRING_SOURCE;
    static Pattern QN;

    public static Set<CharSequence> analyze(InputStream in) throws Exception {
        if (transformer == null) {
            TransformerFactory tf = TransformerFactory.newInstance();
            StreamSource source = new StreamSource(SpringComponent.class.getResourceAsStream("extract.xsl"));
            transformer = tf.newTransformer(source);
        }
        HashSet<CharSequence> refers = new HashSet<CharSequence>();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        StreamResult r = new StreamResult(bout);
        StreamSource s = new StreamSource(in);
        transformer.transform(s, r);
        try (BufferedReader br = IO.reader(bout.toString("UTF-8"));){
            String line = br.readLine();
            while (line != null) {
                if ((line = line.trim()).length() > 0) {
                    String[] parts = line.split("\\s*,\\s*");
                    for (int i = 0; i < parts.length; ++i) {
                        int n = parts[i].lastIndexOf(46);
                        if (n <= 0) continue;
                        refers.add(parts[i].subSequence(0, n));
                    }
                }
                line = br.readLine();
            }
        }
        return refers;
    }

    @Override
    public boolean analyzeJar(Analyzer analyzer) throws Exception {
        Jar jar = analyzer.getJar();
        Map<String, Resource> dir = jar.getDirectories().get("META-INF/spring");
        if (dir == null || dir.isEmpty()) {
            return false;
        }
        for (Map.Entry<String, Resource> entry : dir.entrySet()) {
            String path = entry.getKey();
            Resource resource = entry.getValue();
            if (!SPRING_SOURCE.matcher(path).matches()) continue;
            try {
                Set<CharSequence> set;
                try (InputStream in = resource.openInputStream();){
                    set = SpringComponent.analyze(in);
                }
                Iterator<CharSequence> r = set.iterator();
                while (r.hasNext()) {
                    Descriptors.PackageRef pack = analyzer.getPackageRef((String)r.next());
                    if (!QN.matcher(pack.getFQN()).matches()) {
                        analyzer.warning("Package does not seem a package in spring resource (%s): %s", path, pack);
                    }
                    if (analyzer.getReferred().containsKey(pack)) continue;
                    analyzer.getReferred().put(pack, new Attrs());
                }
            }
            catch (Exception e) {
                analyzer.error("Unexpected exception in processing spring resources(%s): %s", path, e);
            }
        }
        return false;
    }

    static {
        SPRING_SOURCE = Pattern.compile("META-INF/spring/.*\\.xml");
        QN = Pattern.compile("[_A-Za-z$][_A-Za-z0-9$]*(\\.[_A-Za-z$][_A-Za-z0-9$]*)*");
    }
}

