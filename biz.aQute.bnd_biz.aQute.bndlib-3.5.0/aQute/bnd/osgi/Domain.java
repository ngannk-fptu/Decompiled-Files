/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi;

import aQute.bnd.header.Attrs;
import aQute.bnd.header.OSGiHeader;
import aQute.bnd.header.Parameters;
import aQute.bnd.maven.PomParser;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.Resource;
import aQute.bnd.version.Version;
import aQute.lib.converter.Converter;
import aQute.lib.io.IO;
import aQute.lib.utf8properties.UTF8Properties;
import aQute.service.reporter.Reporter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public abstract class Domain
implements Iterable<String> {
    final Properties translation = new UTF8Properties();

    public abstract String get(String var1);

    public String get(String key, String deflt) {
        String result = this.get(key);
        if (result != null) {
            return result;
        }
        return deflt;
    }

    public String translate(String key) {
        return this.translate(key, null);
    }

    public String translate(String key, String deflt) {
        String value = this.get(key);
        if (value == null) {
            return deflt;
        }
        if (value.indexOf(37) >= 0) {
            value = value.trim().substring(1);
            return this.translation.getProperty(value, value);
        }
        return null;
    }

    public abstract void set(String var1, String var2);

    @Override
    public abstract Iterator<String> iterator();

    public static Domain domain(Manifest manifest) {
        Attributes attrs = manifest.getMainAttributes();
        return Domain.domain(attrs);
    }

    public static Domain domain(final Attributes attrs) {
        return new Domain(){

            @Override
            public String get(String key) {
                return attrs.getValue(key);
            }

            @Override
            public void set(String key, String value) {
                attrs.putValue(key, value);
            }

            @Override
            public Iterator<String> iterator() {
                final Iterator<Object> it = attrs.keySet().iterator();
                return new Iterator<String>(){

                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public String next() {
                        return it.next().toString();
                    }

                    @Override
                    public void remove() {
                        it.remove();
                    }
                };
            }
        };
    }

    public static Domain domain(final Processor processor) {
        return new Domain(){

            @Override
            public String get(String key) {
                return processor.getProperty(key);
            }

            @Override
            public String get(String key, String deflt) {
                return processor.getProperty(key, deflt);
            }

            @Override
            public void set(String key, String value) {
                processor.setProperty(key, value);
            }

            @Override
            public Parameters getParameters(String key) {
                return this.getParameters(key, processor);
            }

            @Override
            public Parameters getParameters(String key, String deflt) {
                return this.getParameters(key, deflt, processor);
            }

            @Override
            public Iterator<String> iterator() {
                final Iterator<String> it = processor.getPropertyKeys(true).iterator();
                return new Iterator<String>(){
                    String current;

                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public String next() {
                        this.current = (String)it.next();
                        return this.current;
                    }

                    @Override
                    public void remove() {
                        processor.getProperties().remove(this.current);
                    }
                };
            }
        };
    }

    public static Domain domain(final Map<String, String> map) {
        return new Domain(){

            @Override
            public String get(String key) {
                return (String)map.get(key);
            }

            @Override
            public void set(String key, String value) {
                map.put(key, value);
            }

            @Override
            public Iterator<String> iterator() {
                return map.keySet().iterator();
            }
        };
    }

    public Parameters getParameters(String key, Reporter reporter) {
        return new Parameters(this.get(key), reporter);
    }

    public Parameters getParameters(String key) {
        return new Parameters(this.get(key));
    }

    public Parameters getParameters(String key, String deflt) {
        return new Parameters(this.get(key, deflt));
    }

    public Parameters getParameters(String key, String deflt, Reporter reporter) {
        return new Parameters(this.get(key, deflt), reporter);
    }

    public Parameters getRequireBundle() {
        return this.getParameters("Require-Bundle");
    }

    public Parameters getImportPackage() {
        return this.getParameters("Import-Package");
    }

    public Parameters getExportPackage() {
        return this.getParameters("Export-Package");
    }

    public Parameters getBundleClassPath() {
        return this.getParameters("Bundle-ClassPath");
    }

    public Parameters getPrivatePackage() {
        Parameters p = this.getParameters("Private-Package");
        p.putAll(this.getParameters("-privatepackage"));
        return p;
    }

    public Parameters getIncludeResource() {
        Parameters ic = this.getParameters("Include-Resource");
        ic.putAll(this.getParameters("-includeresource"));
        ic.putAll(this.getParameters("-wab"));
        return ic;
    }

    public Parameters getDynamicImportPackage() {
        return this.getParameters("DynamicImport-Package");
    }

    public Parameters getExportContents() {
        return this.getParameters("-exportcontents");
    }

    public String getBundleActivator() {
        return this.get("Bundle-Activator");
    }

    public void setPrivatePackage(String s) {
        if (s != null) {
            this.set("-privatepackage", s);
        }
    }

    public void setIncludeResource(String s) {
        if (s != null) {
            this.set("Include-Resource", s);
        }
    }

    public void setBundleActivator(String s) {
        if (s != null) {
            this.set("Bundle-Activator", s);
        }
    }

    public void setExportPackage(String s) {
        if (s != null) {
            this.set("Export-Package", s);
        }
    }

    public void setImportPackage(String s) {
        if (s != null) {
            this.set("Import-Package", s);
        }
    }

    public void setBundleClasspath(String s) {
        if (s != null) {
            this.set("Bundle-ClassPath", s);
        }
    }

    public Parameters getBundleClasspath() {
        return this.getParameters("Bundle-ClassPath");
    }

    public void setBundleRequiredExecutionEnvironment(String s) {
        if (s != null) {
            this.set("Bundle-RequiredExecutionEnvironment", s);
        }
    }

    public Parameters getBundleRequiredExecutionEnvironment() {
        return this.getParameters("Bundle-RequiredExecutionEnvironment");
    }

    public void setSources(boolean b) {
        if (b) {
            this.set("-sources", "true");
        } else {
            this.set("-sources", "false");
        }
    }

    public boolean isSources() {
        return Processor.isTrue(this.get("-sources"));
    }

    public Map.Entry<String, Attrs> getBundleSymbolicName() {
        Parameters p = this.getParameters("Bundle-SymbolicName");
        if (p.isEmpty()) {
            return null;
        }
        return p.entrySet().iterator().next();
    }

    public Map.Entry<String, Attrs> getFragmentHost() {
        Parameters p = this.getParameters("Fragment-Host");
        if (p.isEmpty()) {
            return null;
        }
        return p.entrySet().iterator().next();
    }

    public void setBundleSymbolicName(String s) {
        this.set("Bundle-SymbolicName", s);
    }

    public String getBundleVersion() {
        return this.get("Bundle-Version");
    }

    public void setBundleVersion(String version) {
        Version v = new Version(version);
        this.set("Bundle-Version", v.toString());
    }

    public void setBundleVersion(Version version) {
        this.set("Bundle-Version", version.toString());
    }

    public void setFailOk(boolean b) {
        this.set("-failok", b + "");
    }

    public void setRunfw(String runfw) {
        this.set("-runfw", runfw);
    }

    public void setRunRequires(String runRq) {
        this.set("-runrequires", runRq);
    }

    public void setAugment(String augments) {
        this.set("-augment", augments);
    }

    public boolean isFailOk() {
        return Processor.isTrue(this.get("-failok"));
    }

    public String getIcon(int requestedSize) throws Exception {
        String spec = this.get("Bundle-Icon");
        if (spec == null) {
            return null;
        }
        Parameters p = OSGiHeader.parseHeader(spec);
        int dist = Integer.MAX_VALUE;
        String selected = null;
        for (Map.Entry<String, Attrs> e : p.entrySet()) {
            int size;
            String s;
            String url = e.getKey();
            if (selected == null) {
                selected = url;
            }
            if (e.getValue() == null || (s = e.getValue().get("size")) == null || (size = Converter.cnv(Integer.class, (Object)s).intValue()) == 0 || Math.abs(requestedSize - size) >= dist) continue;
            dist = Math.abs(requestedSize - size);
            selected = url;
        }
        return selected;
    }

    public void setConditionalPackage(String string) {
        this.set("Conditional-Package", string);
    }

    public void setRunblacklist(String blacklist) {
        this.set("-runblacklist", blacklist);
    }

    public String getRunblacklist() {
        return this.get("-runblacklist");
    }

    public void setRunee(String string) {
        this.set("-runee", string);
    }

    public String getRunee() {
        return this.get("-runee");
    }

    public void setTranslation(Jar jar) throws Exception {
        Resource propsResource;
        Manifest m = jar.getManifest();
        if (m == null) {
            return;
        }
        String path = m.getMainAttributes().getValue("Bundle-Localization");
        if (path == null) {
            path = "OSGI-INF/l10n/bundle";
        }
        if ((propsResource = jar.getResource(path = path + ".properties")) != null) {
            try (InputStream in = propsResource.openInputStream();){
                this.translation.load(in);
            }
        }
    }

    public Parameters getRequireCapability() {
        return this.getParameters("Require-Capability");
    }

    public Parameters getProvideCapability() {
        return this.getParameters("Provide-Capability");
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static Domain domain(File file) throws IOException {
        if (file.getName().endsWith(".mf")) {
            try (InputStream in = IO.stream(file);){
                Manifest m = new Manifest(in);
                Domain domain3 = Domain.domain(m);
                return domain3;
            }
        }
        if (file.getName().endsWith(".properties") || file.getName().endsWith(".bnd")) {
            Processor p = new Processor();
            p.setProperties(file);
            return Domain.domain(p);
        }
        if (file.getName().endsWith(".pom")) {
            try {
                PomParser p = new PomParser();
                p.setProperties(p.getProperties(file));
                return Domain.domain(p);
            }
            catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        try (JarInputStream jin = new JarInputStream(IO.stream(file));){
            Manifest m = jin.getManifest();
            if (m != null) {
                Domain domain4 = Domain.domain(m);
                return domain4;
            }
        }
        try {
            throwable = null;
            try (ZipFile zf = new ZipFile(file);){
                ZipEntry entry = zf.getEntry("META-INF/MANIFEST.MF");
                if (entry == null) {
                    Domain domain2 = null;
                    return domain2;
                }
                Manifest m = new Manifest(zf.getInputStream(entry));
                Domain domain = Domain.domain(m);
                return domain;
            }
            catch (Throwable throwable2) {
                throwable = throwable2;
                throw throwable2;
            }
        }
        catch (ZipException e) {
            return null;
        }
    }

    public String getBundleName() {
        return this.get("Bundle-Name");
    }

    public String getBundleDescription() {
        return this.get("Bundle-Description");
    }

    public String getBundleCopyright() {
        return this.get("Bundle-Copyright");
    }

    public String getBundleDocURL() {
        return this.get("Bundle-Copyright");
    }

    public String getBundleVendor() {
        return this.get("Bundle-Vendor");
    }

    public String getBundleContactAddress() {
        return this.get("Bundle-ContactAddress");
    }

    public String getBundleCategory() {
        return this.get("Bundle-Category");
    }

    public String getBundleNative() {
        return this.get("Bundle-NativeCode");
    }

    public void copyFrom(Domain domain) {
        for (String key : domain) {
            this.set(key, domain.get(key));
        }
    }
}

