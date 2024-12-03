/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.differ;

import aQute.bnd.differ.Element;
import aQute.bnd.differ.JavaElement;
import aQute.bnd.header.Attrs;
import aQute.bnd.header.OSGiHeader;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.About;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.Instructions;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Resource;
import aQute.bnd.service.diff.Delta;
import aQute.bnd.service.diff.Differ;
import aQute.bnd.service.diff.Tree;
import aQute.bnd.service.diff.Type;
import aQute.bnd.version.Version;
import aQute.lib.collections.ExtList;
import aQute.lib.hex.Hex;
import aQute.lib.io.IO;
import aQute.libg.cryptography.Digester;
import aQute.libg.cryptography.SHA1;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.Manifest;
import java.util.regex.Pattern;

public class DiffPluginImpl
implements Differ {
    static final Set<String> MAJOR_HEADERS = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
    static final Set<String> IGNORE_HEADERS = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
    static final Set<String> ORDERED_HEADERS = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
    Instructions localIgnore = null;
    static Pattern META_INF_P;

    public Tree tree(File newer) throws Exception {
        try (Jar jnewer = new Jar(newer);){
            Tree tree = this.tree(jnewer);
            return tree;
        }
    }

    @Override
    public Tree tree(Jar newer) throws Exception {
        try (Analyzer anewer = new Analyzer(newer);){
            Tree tree = this.tree(anewer);
            return tree;
        }
    }

    @Override
    public Tree tree(Analyzer newer) throws Exception {
        return this.bundleElement(newer);
    }

    private Element bundleElement(Analyzer analyzer) throws Exception {
        ArrayList<Element> result = new ArrayList<Element>();
        Manifest manifest = analyzer.getJar().getManifest();
        if (manifest != null) {
            result.add(JavaElement.getAPI(analyzer));
            result.add(this.manifestElement(manifest));
        }
        result.add(this.resourcesElement(analyzer));
        return new Element(Type.BUNDLE, analyzer.getJar().getName(), result, Delta.CHANGED, Delta.CHANGED, null);
    }

    private Element resourcesElement(Analyzer analyzer) throws Exception {
        Jar jar = analyzer.getJar();
        ArrayList<Element> resources = new ArrayList<Element>();
        for (Map.Entry<String, Resource> entry : jar.getResources().entrySet()) {
            String path;
            if (META_INF_P.matcher(entry.getKey()).matches() || this.localIgnore != null && this.localIgnore.matches(entry.getKey()) || (path = entry.getKey()).endsWith("<<EMPTY>>") || analyzer.since(About._3_0) && this.hasSource(analyzer, path)) continue;
            Resource resource = entry.getValue();
            InputStream in = resource.openInputStream();
            Throwable throwable = null;
            try {
                Digester<SHA1> digester = SHA1.getDigester(new OutputStream[0]);
                Throwable throwable2 = null;
                try {
                    IO.copy(in, digester);
                    String value = Hex.toHexString(digester.digest().digest());
                    resources.add(new Element(Type.RESOURCE, entry.getKey(), Arrays.asList(new Element(Type.SHA, value)), Delta.CHANGED, Delta.CHANGED, null));
                }
                catch (Throwable throwable3) {
                    throwable2 = throwable3;
                    throw throwable3;
                }
                finally {
                    if (digester == null) continue;
                    if (throwable2 != null) {
                        try {
                            digester.close();
                        }
                        catch (Throwable x2) {
                            throwable2.addSuppressed(x2);
                        }
                        continue;
                    }
                    digester.close();
                }
            }
            catch (Throwable throwable4) {
                throwable = throwable4;
                throw throwable4;
            }
            finally {
                if (in == null) continue;
                if (throwable != null) {
                    try {
                        in.close();
                    }
                    catch (Throwable x2) {
                        throwable.addSuppressed(x2);
                    }
                    continue;
                }
                in.close();
            }
        }
        return new Element(Type.RESOURCES, "<resources>", resources, Delta.CHANGED, Delta.CHANGED, null);
    }

    private boolean hasSource(Analyzer analyzer, String path) throws Exception {
        if (!path.endsWith(".class")) {
            return false;
        }
        Descriptors.TypeRef type = analyzer.getTypeRefFromPath(path);
        Descriptors.PackageRef packageRef = type.getPackageRef();
        Clazz clazz = analyzer.findClass(type);
        if (clazz == null) {
            return false;
        }
        String sourceFile = clazz.getSourceFile();
        if (sourceFile == null) {
            return false;
        }
        String source = "OSGI-OPT/src/" + packageRef.getBinary() + "/" + sourceFile;
        Resource sourceResource = analyzer.getJar().getResource(source);
        return sourceResource != null;
    }

    private Element manifestElement(Manifest manifest) {
        ArrayList<Element> result = new ArrayList<Element>();
        for (Object key : manifest.getMainAttributes().keySet()) {
            String header = key.toString();
            String value = manifest.getMainAttributes().getValue(header);
            if (IGNORE_HEADERS.contains(header) || this.localIgnore != null && this.localIgnore.matches(header)) continue;
            if (MAJOR_HEADERS.contains(header)) {
                if (header.equalsIgnoreCase("Bundle-Version")) {
                    Version v = new Version(value).getWithoutQualifier();
                    result.add(new Element(Type.HEADER, header + ":" + v.toString(), null, Delta.CHANGED, Delta.CHANGED, null));
                    continue;
                }
                Parameters clauses = OSGiHeader.parseHeader(value);
                ArrayList<Element> clausesDef = new ArrayList<Element>();
                for (Map.Entry<String, Attrs> clause : clauses.entrySet()) {
                    ArrayList<Element> parameterDef = new ArrayList<Element>();
                    for (Map.Entry<String, String> parameter : clause.getValue().entrySet()) {
                        String paramValue = parameter.getValue();
                        if ("Export-Package".equals(header) && "uses:".equals(parameter.getKey())) {
                            ExtList<String> uses = ExtList.from(parameter.getValue());
                            Collections.sort(uses);
                            paramValue = uses.join();
                        }
                        parameterDef.add(new Element(Type.PARAMETER, parameter.getKey() + ":" + paramValue, null, Delta.CHANGED, Delta.CHANGED, null));
                    }
                    clausesDef.add(new Element(Type.CLAUSE, clause.getKey(), parameterDef, Delta.CHANGED, Delta.CHANGED, null));
                }
                result.add(new Element(Type.HEADER, header, clausesDef, Delta.CHANGED, Delta.CHANGED, null));
                continue;
            }
            if (ORDERED_HEADERS.contains(header)) {
                ExtList<String> values = ExtList.from(value);
                Collections.sort(values);
                result.add(new Element(Type.HEADER, header + ":" + values.join(), null, Delta.CHANGED, Delta.CHANGED, null));
                continue;
            }
            result.add(new Element(Type.HEADER, header + ":" + value, null, Delta.CHANGED, Delta.CHANGED, null));
        }
        return new Element(Type.MANIFEST, "<manifest>", result, Delta.CHANGED, Delta.CHANGED, null);
    }

    @Override
    public Tree deserialize(Tree.Data data) throws Exception {
        return new Element(data);
    }

    public void setIgnore(String diffignore) {
        if (diffignore == null) {
            this.localIgnore = null;
            return;
        }
        Parameters p = new Parameters(diffignore);
        this.localIgnore = new Instructions(p);
    }

    static {
        MAJOR_HEADERS.add("Export-Package");
        MAJOR_HEADERS.add("Import-Package");
        MAJOR_HEADERS.add("Require-Bundle");
        MAJOR_HEADERS.add("Fragment-Host");
        MAJOR_HEADERS.add("Bundle-SymbolicName");
        MAJOR_HEADERS.add("Bundle-License");
        MAJOR_HEADERS.add("Bundle-NativeCode");
        MAJOR_HEADERS.add("Bundle-RequiredExecutionEnvironment");
        MAJOR_HEADERS.add("DynamicImport-Package");
        MAJOR_HEADERS.add("Bundle-Version");
        IGNORE_HEADERS.add("Tool");
        IGNORE_HEADERS.add("Bnd-LastModified");
        IGNORE_HEADERS.add("Created-By");
        ORDERED_HEADERS.add("Service-Component");
        ORDERED_HEADERS.add("Test-Cases");
        META_INF_P = Pattern.compile("META-INF/([^/]+\\.(MF|SF|DSA|RSA))|(SIG-.*)");
    }
}

