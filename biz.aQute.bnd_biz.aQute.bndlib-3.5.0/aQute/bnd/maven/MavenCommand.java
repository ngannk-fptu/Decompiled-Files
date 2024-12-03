/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package aQute.bnd.maven;

import aQute.bnd.header.Attrs;
import aQute.bnd.header.OSGiHeader;
import aQute.bnd.header.Parameters;
import aQute.bnd.maven.PomFromManifest;
import aQute.bnd.maven.support.CachedPom;
import aQute.bnd.maven.support.Maven;
import aQute.bnd.maven.support.Pom;
import aQute.bnd.osgi.Builder;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.Resource;
import aQute.lib.collections.LineCollection;
import aQute.lib.io.IO;
import aQute.lib.settings.Settings;
import aQute.lib.utf8properties.UTF8Properties;
import aQute.libg.command.Command;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MavenCommand
extends Processor {
    private static final Logger logger = LoggerFactory.getLogger(MavenCommand.class);
    final Settings settings = new Settings();
    File temp;
    static Executor executor = Executors.newCachedThreadPool();
    static Pattern GROUP_ARTIFACT_VERSION = Pattern.compile("([^+]+)\\+([^+]+)\\+([^+]+)");

    public MavenCommand() {
    }

    public MavenCommand(Processor p) {
        super(p);
    }

    public void run(String[] args, int i) throws Exception {
        this.temp = new File("maven-bundle");
        if (i >= args.length) {
            this.help();
            return;
        }
        while (i < args.length && args[i].startsWith("-")) {
            String option = args[i];
            logger.debug("option {}", (Object)option);
            if (option.equals("-temp")) {
                this.temp = this.getFile(args[++i]);
            } else {
                this.help();
                this.error("Invalid option %s", option);
            }
            ++i;
        }
        String cmd = args[i++];
        logger.debug("temp dir {}", (Object)this.temp);
        IO.delete(this.temp);
        IO.mkdirs(this.temp);
        if (!this.temp.isDirectory()) {
            throw new IOException("Cannot create temp directory");
        }
        if (cmd.equals("settings")) {
            this.settings();
        } else if (cmd.equals("help")) {
            this.help();
        } else if (cmd.equals("bundle")) {
            this.bundle(args, i);
        } else if (cmd.equals("view")) {
            this.view(args, i);
        } else {
            this.error("No such command %s, type help", cmd);
        }
    }

    private void help() {
        System.err.printf("Usage:%n", new Object[0]);
        System.err.printf("  maven %n  [-temp <dir>]            use as temp directory%n  settings                 show maven settings%n  bundle                   turn a bundle into a maven bundle%n    [-properties <file>]   provide properties, properties starting with javadoc are options for javadoc, like javadoc-tag=...%n    [-javadoc <file|url>]  where to find the javadoc (zip/dir), otherwise generated%n    [-source <file|url>]   where to find the source (zip/dir), otherwise from OSGI-OPT/src%n    [-scm <url>]           required scm in pom, otherwise from Bundle-SCM%n    [-url <url>]           required project url in pom%n    [-bsn bsn]             overrides bsn%n    [-version <version>]   overrides version%n    [-developer <email>]   developer email%n    [-nodelete]            do not delete temp files%n    [-passphrase <gpgp passphrase>] signer password%n        <file|url>%n", new Object[0]);
    }

    private void settings() throws FileNotFoundException, Exception {
        File userHome = new File(System.getProperty("user.home"));
        File m2 = new File(userHome, ".m2");
        if (!m2.isDirectory()) {
            this.error("There is no m2 directory at %s", userHome);
            return;
        }
        File settings = new File(m2, "settings.xml");
        if (!settings.isFile()) {
            this.error("There is no settings file at '%s'", settings.getAbsolutePath());
            return;
        }
        try (LineCollection lc = new LineCollection(IO.reader(settings));){
            while (lc.hasNext()) {
                System.err.println(lc.next());
            }
        }
    }

    private void bundle(String[] args, int i) throws Exception {
        Jar javadocJar;
        Jar sourceJar;
        ArrayList<String> developers = new ArrayList<String>();
        UTF8Properties properties = new UTF8Properties();
        String scm = null;
        String passphrase = null;
        String javadoc = null;
        String source = null;
        String output = "bundle.jar";
        String url = null;
        String artifact = null;
        String group = null;
        String version = null;
        boolean nodelete = false;
        while (i < args.length && args[i].startsWith("-")) {
            String option = args[i++];
            logger.debug("bundle option {}", (Object)option);
            if (option.equals("-scm")) {
                scm = args[i++];
                continue;
            }
            if (option.equals("-group")) {
                group = args[i++];
                continue;
            }
            if (option.equals("-artifact")) {
                artifact = args[i++];
                continue;
            }
            if (option.equals("-version")) {
                version = args[i++];
                continue;
            }
            if (option.equals("-developer")) {
                developers.add(args[i++]);
                continue;
            }
            if (option.equals("-passphrase")) {
                passphrase = args[i++];
                continue;
            }
            if (option.equals("-url")) {
                url = args[i++];
                continue;
            }
            if (option.equals("-javadoc")) {
                javadoc = args[i++];
                continue;
            }
            if (option.equals("-source")) {
                source = args[i++];
                continue;
            }
            if (option.equals("-output")) {
                output = args[i++];
                continue;
            }
            if (option.equals("-nodelete")) {
                nodelete = true;
                continue;
            }
            if (!option.startsWith("-properties")) continue;
            try {
                InputStream in = IO.stream(Paths.get(args[i++], new String[0]));
                Throwable throwable = null;
                try {
                    ((Properties)properties).load(in);
                }
                catch (Throwable x2) {
                    throwable = x2;
                    throw x2;
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
            catch (Exception e) {}
        }
        if (developers.isEmpty()) {
            String email = this.settings.remove("email");
            if (email == null) {
                this.error("No developer email set, you can set global default email with: bnd global email Peter.Kriens@aQute.biz", new Object[0]);
            } else {
                developers.add(email);
            }
        }
        if (i == args.length) {
            this.error("too few arguments, no bundle specified", new Object[0]);
            return;
        }
        if (i != args.length - 1) {
            this.error("too many arguments, only one bundle allowed", new Object[0]);
            return;
        }
        String input = args[i++];
        Jar binaryJar = this.getJarFromFileOrURL(input);
        logger.debug("got {}", (Object)binaryJar);
        if (binaryJar == null) {
            this.error("JAR does not exist: %s", input);
            return;
        }
        File original = MavenCommand.getFile(this.temp, "original");
        IO.mkdirs(original);
        binaryJar.expand(original);
        binaryJar.calcChecksums(null);
        Manifest manifest = binaryJar.getManifest();
        logger.debug("got manifest");
        PomFromManifest pom = new PomFromManifest(manifest);
        if (scm != null) {
            pom.setSCM(scm);
        }
        if (url != null) {
            pom.setURL(url);
        }
        if (artifact != null) {
            pom.setArtifact(artifact);
        }
        if (artifact != null) {
            pom.setGroup(group);
        }
        if (version != null) {
            pom.setVersion(version);
        }
        logger.debug("{}", (Object)url);
        for (String d : developers) {
            pom.addDeveloper(d);
        }
        Set<String> exports = OSGiHeader.parseHeader(manifest.getMainAttributes().getValue("Export-Package")).keySet();
        if (source == null) {
            logger.debug("Splitting source code");
            sourceJar = new Jar("source");
            for (Map.Entry<String, Resource> entry : binaryJar.getResources().entrySet()) {
                if (!entry.getKey().startsWith("OSGI-OPT/src")) continue;
                sourceJar.putResource(entry.getKey().substring("OSGI-OPT/src/".length()), entry.getValue());
            }
            this.copyInfo(binaryJar, sourceJar, "source");
        } else {
            sourceJar = this.getJarFromFileOrURL(source);
        }
        sourceJar.calcChecksums(null);
        if (javadoc == null) {
            logger.debug("creating javadoc because -javadoc not used");
            javadocJar = this.javadoc(MavenCommand.getFile(original, "OSGI-OPT/src"), exports, manifest, properties);
            if (javadocJar == null) {
                this.error("Cannot find source code in OSGI-OPT/src to generate Javadoc", new Object[0]);
                return;
            }
            this.copyInfo(binaryJar, javadocJar, "javadoc");
        } else {
            logger.debug("Loading javadoc externally {}", (Object)javadoc);
            javadocJar = this.getJarFromFileOrURL(javadoc);
        }
        javadocJar.calcChecksums(null);
        this.addClose(binaryJar);
        this.addClose(sourceJar);
        this.addClose(javadocJar);
        logger.debug("creating bundle dir");
        File bundle = new File(this.temp, "bundle");
        IO.mkdirs(bundle);
        String prefix = pom.getArtifactId() + "-" + pom.getVersion();
        File binaryFile = new File(bundle, prefix + ".jar");
        File sourceFile = new File(bundle, prefix + "-sources.jar");
        File javadocFile = new File(bundle, prefix + "-javadoc.jar");
        File pomFile = new File(bundle, "pom.xml").getAbsoluteFile();
        logger.debug("creating output files {}, {}, {}, and {}", new Object[]{binaryFile, sourceFile, javadocFile, pomFile});
        IO.copy(pom.openInputStream(), pomFile);
        logger.debug("copied pom");
        logger.debug("writing binary {}", (Object)binaryFile);
        binaryJar.write(binaryFile);
        logger.debug("writing source {}", (Object)sourceFile);
        sourceJar.write(sourceFile);
        logger.debug("writing javadoc {}", (Object)javadocFile);
        javadocJar.write(javadocFile);
        this.sign(binaryFile, passphrase);
        this.sign(sourceFile, passphrase);
        this.sign(javadocFile, passphrase);
        this.sign(pomFile, passphrase);
        logger.debug("create bundle");
        Jar bundleJar = new Jar(bundle);
        this.addClose(bundleJar);
        File outputFile = this.getFile(output);
        bundleJar.write(outputFile);
        logger.debug("created bundle {}", (Object)outputFile);
        binaryJar.close();
        sourceJar.close();
        javadocJar.close();
        bundleJar.close();
        if (!nodelete) {
            IO.delete(this.temp);
        }
    }

    private void copyInfo(Jar source, Jar dest, String type) throws Exception {
        source.ensureManifest();
        dest.ensureManifest();
        this.copyInfoResource(source, dest, "LICENSE");
        this.copyInfoResource(source, dest, "LICENSE.html");
        this.copyInfoResource(source, dest, "about.html");
        Manifest sm = source.getManifest();
        Manifest dm = dest.getManifest();
        this.copyInfoHeader(sm, dm, "Bundle-Description", "");
        this.copyInfoHeader(sm, dm, "Bundle-Vendor", "");
        this.copyInfoHeader(sm, dm, "Bundle-Copyright", "");
        this.copyInfoHeader(sm, dm, "Bundle-DocURL", "");
        this.copyInfoHeader(sm, dm, "Bundle-License", "");
        this.copyInfoHeader(sm, dm, "Bundle-Name", " " + type);
        this.copyInfoHeader(sm, dm, "Bundle-SymbolicName", "." + type);
        this.copyInfoHeader(sm, dm, "Bundle-Version", "");
    }

    private void copyInfoHeader(Manifest sm, Manifest dm, String key, String value) {
        String v = sm.getMainAttributes().getValue(key);
        if (v == null) {
            logger.debug("no source for {}", (Object)key);
            return;
        }
        if (dm.getMainAttributes().getValue(key) != null) {
            logger.debug("already have {}", (Object)key);
            return;
        }
        dm.getMainAttributes().putValue(key, v + value);
    }

    private void copyInfoResource(Jar source, Jar dest, String type) {
        if (source.getResources().containsKey(type) && !dest.getResources().containsKey(type)) {
            dest.putResource(type, source.getResource(type));
        }
    }

    protected Jar getJarFromFileOrURL(String spec) throws IOException, MalformedURLException {
        Jar jar;
        File jarFile = this.getFile(spec);
        if (jarFile.exists()) {
            jar = new Jar(jarFile);
        } else {
            URL url = new URL(spec);
            try (InputStream in = url.openStream();){
                jar = new Jar(url.getFile(), in);
            }
        }
        this.addClose(jar);
        return jar;
    }

    private void sign(File file, String passphrase) throws Exception {
        logger.debug("signing {}", (Object)file);
        File asc = new File(file.getParentFile(), file.getName() + ".asc");
        IO.delete(asc);
        Command command = new Command();
        command.setTrace();
        command.add(this.getProperty("gpg", "gpg"));
        if (passphrase != null) {
            command.add("--passphrase", passphrase);
        }
        command.add("-ab", "--sign");
        command.add(file.getAbsolutePath());
        System.err.println(command);
        StringBuilder stdout = new StringBuilder();
        StringBuilder stderr = new StringBuilder();
        int result = command.execute(stdout, stderr);
        if (result != 0) {
            this.error("gpg signing %s failed because %s", file, "" + stdout + stderr);
        }
    }

    private Jar javadoc(File source, Set<String> exports, Manifest m, Properties p) throws Exception {
        File tmp = new File(this.temp, "javadoc");
        IO.mkdirs(tmp);
        Command command = new Command();
        command.add(this.getProperty("javadoc", "javadoc"));
        command.add("-quiet");
        command.add("-protected");
        command.add("-d");
        command.add(tmp.getAbsolutePath());
        command.add("-charset");
        command.add("UTF-8");
        command.add("-sourcepath");
        command.add(source.getAbsolutePath());
        Attributes attr = m.getMainAttributes();
        UTF8Properties pp = new UTF8Properties(p);
        this.set(pp, "-doctitle", this.description(attr));
        this.set(pp, "-header", this.description(attr));
        this.set(pp, "-windowtitle", this.name(attr));
        this.set(pp, "-bottom", this.copyright(attr));
        this.set(pp, "-footer", this.license(attr));
        command.add("-tag");
        command.add("Immutable:t:Immutable");
        command.add("-tag");
        command.add("ThreadSafe:t:ThreadSafe");
        command.add("-tag");
        command.add("NotThreadSafe:t:NotThreadSafe");
        command.add("-tag");
        command.add("GuardedBy:mf:Guarded By:");
        command.add("-tag");
        command.add("security:m:Required Permissions");
        command.add("-tag");
        command.add("noimplement:t:Consumers of this API must not implement this interface");
        Enumeration<?> e = pp.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String)e.nextElement();
            String value = pp.getProperty(key);
            if (!key.startsWith("javadoc")) continue;
            key = key.substring("javadoc".length());
            MavenCommand.removeDuplicateMarker(key);
            command.add(key);
            command.add(value);
        }
        for (String packageName : exports) {
            command.add(packageName);
        }
        StringBuilder out = new StringBuilder();
        StringBuilder err = new StringBuilder();
        System.err.println(command);
        int result = command.execute(out, err);
        if (result != 0) {
            this.warning("Error during execution of javadoc command: %s\n******************\n%s", out, err);
        }
        Jar jar = new Jar(tmp);
        this.addClose(jar);
        return jar;
    }

    private String license(Attributes attr) {
        Parameters map = Processor.parseHeader(attr.getValue("Bundle-License"), null);
        if (map.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String sep = "Licensed under ";
        for (Map.Entry<String, Attrs> entry : map.entrySet()) {
            sb.append(sep);
            String key = entry.getKey();
            String link = entry.getValue().get("link");
            String description = entry.getValue().get("description");
            if (description == null) {
                description = key;
            }
            if (link != null) {
                sb.append("<a href='");
                sb.append(link);
                sb.append("'>");
            }
            sb.append(description);
            if (link != null) {
                sb.append("</a>");
            }
            sep = ",<br/>";
        }
        return sb.toString();
    }

    private String copyright(Attributes attr) {
        return attr.getValue("Bundle-Copyright");
    }

    private String name(Attributes attr) {
        String name = attr.getValue("Bundle-Name");
        if (name == null) {
            name = attr.getValue("Bundle-SymbolicName");
        }
        return name;
    }

    private String description(Attributes attr) {
        String descr = attr.getValue("Bundle-Description");
        if (descr == null) {
            descr = attr.getValue("Bundle-Name");
        }
        if (descr == null) {
            descr = attr.getValue("Bundle-SymbolicName");
        }
        return descr;
    }

    private void set(Properties pp, String option, String defaultValue) {
        String key = "javadoc" + option;
        String existingValue = pp.getProperty(key);
        if (existingValue != null) {
            return;
        }
        pp.setProperty(key, defaultValue);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    void view(String[] args, int i) throws Exception {
        Maven maven = new Maven(executor);
        ArrayList<URI> urls = new ArrayList<URI>();
        Path output = null;
        while (i < args.length && args[i].startsWith("-")) {
            if ("-r".equals(args[i])) {
                URI uri = new URI(args[++i]);
                urls.add(uri);
                System.err.println("URI for repo " + uri);
            } else {
                if (!"-o".equals(args[i])) throw new IllegalArgumentException("Unknown option: " + args[i]);
                output = Paths.get(args[++i], new String[0]);
            }
            ++i;
        }
        URI[] urls2 = urls.toArray(new URI[0]);
        PrintWriter pw = IO.writer(output == null ? System.err : IO.outputStream(output));
        try {
            while (i < args.length) {
                String ref = args[i++];
                pw.println("Ref " + ref);
                Matcher matcher = GROUP_ARTIFACT_VERSION.matcher(ref);
                if (matcher.matches()) {
                    Builder a;
                    block20: {
                        String group = matcher.group(1);
                        String artifact = matcher.group(2);
                        String version = matcher.group(3);
                        CachedPom pom = maven.getPom(group, artifact, version, urls2);
                        a = new Builder();
                        Throwable throwable = null;
                        try {
                            a.setProperty("-privatepackage", "*");
                            Set<Pom> dependencies = pom.getDependencies(Pom.Scope.compile, urls2);
                            for (Pom dep : dependencies) {
                                System.err.printf("%20s %-20s %10s%n", dep.getGroupId(), dep.getArtifactId(), dep.getVersion());
                                a.addClasspath(dep.getArtifact());
                            }
                            pw.println(a.getClasspath());
                            a.build();
                            TreeSet<Descriptors.PackageRef> sorted = new TreeSet<Descriptors.PackageRef>(a.getImports().keySet());
                            for (Descriptors.PackageRef p : sorted) {
                                pw.printf("%-40s\n", p);
                            }
                            a.close();
                            if (a == null) continue;
                            if (throwable == null) break block20;
                        }
                        catch (Throwable throwable2) {
                            try {
                                throwable = throwable2;
                                throw throwable2;
                            }
                            catch (Throwable throwable3) {
                                if (a == null) throw throwable3;
                                if (throwable == null) {
                                    a.close();
                                    throw throwable3;
                                }
                                try {
                                    a.close();
                                    throw throwable3;
                                }
                                catch (Throwable x2) {
                                    throwable.addSuppressed(x2);
                                    throw throwable3;
                                }
                            }
                        }
                        try {
                            a.close();
                            continue;
                        }
                        catch (Throwable x2) {
                            throwable.addSuppressed(x2);
                            continue;
                        }
                    }
                    a.close();
                    continue;
                }
                System.err.println("Wrong, must look like group+artifact+version, is " + ref);
            }
            return;
        }
        finally {
            pw.flush();
            if (output != null) {
                IO.close(pw);
            }
        }
    }
}

