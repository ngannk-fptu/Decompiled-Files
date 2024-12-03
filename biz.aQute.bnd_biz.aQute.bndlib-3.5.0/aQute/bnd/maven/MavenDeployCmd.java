/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package aQute.bnd.maven;

import aQute.bnd.build.Project;
import aQute.bnd.header.Attrs;
import aQute.bnd.header.Parameters;
import aQute.bnd.maven.PomResource;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.JarResource;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.Resource;
import aQute.lib.io.IO;
import aQute.libg.command.Command;
import aQute.libg.slf4j.GradleLogging;
import aQute.service.reporter.Reporter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;
import java.util.jar.Manifest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MavenDeployCmd
extends Processor {
    private static final Logger logger = LoggerFactory.getLogger(MavenDeployCmd.class);
    String repository = "nexus";
    String url = "http://oss.sonatype.org/service/local/staging/deploy/maven2";
    String homedir;
    String keyname;
    String passphrase;
    Reporter reporter;

    void run(String[] args, int i) throws Exception {
        if (i >= args.length) {
            System.err.printf("Usage:%n", new Object[0]);
            System.err.println("  deploy [-url repo] [-passphrase passphrase] [-homedir homedir] [-keyname keyname] bundle ...");
            System.err.println("  settings");
            return;
        }
        ++i;
        while (i < args.length && args[i].startsWith("-")) {
            String option = args[i];
            if (option.equals("-url")) {
                this.repository = args[++i];
                continue;
            }
            if (option.equals("-passphrase")) {
                this.passphrase = args[++i];
                continue;
            }
            if (option.equals("-url")) {
                this.homedir = args[++i];
                continue;
            }
            if (option.equals("-keyname")) {
                this.keyname = args[++i];
                continue;
            }
            this.error("Invalid command ", new Object[0]);
        }
    }

    public void setProperties(Map<String, String> map) {
        this.repository = map.get("repository");
        this.url = map.get("url");
        this.passphrase = map.get("passphrase");
        this.homedir = map.get("homedir");
        this.keyname = map.get("keyname");
        if (this.url == null) {
            throw new IllegalArgumentException("MavenDeploy plugin must get a repository URL");
        }
        if (this.repository == null) {
            throw new IllegalArgumentException("MavenDeploy plugin must get a repository name");
        }
    }

    public void setReporter(Reporter processor) {
        this.reporter = processor;
    }

    public boolean deploy(Project project, Jar original) throws Exception {
        Parameters deploy = project.parseHeader(project.getProperty("-deploy"));
        Attrs maven = deploy.get(this.repository);
        if (maven == null) {
            return false;
        }
        logger.info(GradleLogging.LIFECYCLE, "deploying {} to Maven repo: {}", (Object)original, (Object)this.repository);
        File target = project.getTarget();
        File tmp = Processor.getFile(target, this.repository);
        if (!tmp.exists() && !tmp.mkdirs()) {
            throw new IOException("Could not create directory " + tmp);
        }
        Manifest manifest = original.getManifest();
        if (manifest == null) {
            project.error("Jar has no manifest: %s", original);
        } else {
            logger.info(GradleLogging.LIFECYCLE, "Writing pom.xml");
            PomResource pom = new PomResource(manifest);
            pom.setProperties(maven);
            File pomFile = this.write(tmp, pom, "pom.xml");
            try (Jar main = new Jar("main");
                 Jar src = new Jar("src");){
                this.split(original, main, src);
                Parameters exports = project.parseHeader(manifest.getMainAttributes().getValue("Export-Package"));
                File jdoc = new File(tmp, "jdoc");
                IO.mkdirs(jdoc);
                logger.info(GradleLogging.LIFECYCLE, "Generating Javadoc for: {}", exports.keySet());
                Jar javadoc = this.javadoc(jdoc, project, exports.keySet());
                logger.info(GradleLogging.LIFECYCLE, "Writing javadoc jar");
                File javadocFile = this.write(tmp, new JarResource(javadoc), "javadoc.jar");
                logger.info(GradleLogging.LIFECYCLE, "Writing main file");
                File mainFile = this.write(tmp, new JarResource(main), "main.jar");
                logger.info(GradleLogging.LIFECYCLE, "Writing sources file");
                File srcFile = this.write(tmp, new JarResource(main), "src.jar");
                logger.info(GradleLogging.LIFECYCLE, "Deploying main file");
                this.maven_gpg_sign_and_deploy(project, mainFile, null, pomFile);
                logger.info(GradleLogging.LIFECYCLE, "Deploying main sources file");
                this.maven_gpg_sign_and_deploy(project, srcFile, "sources", null);
                logger.info(GradleLogging.LIFECYCLE, "Deploying main javadoc file");
                this.maven_gpg_sign_and_deploy(project, javadocFile, "javadoc", null);
            }
        }
        return true;
    }

    private void split(Jar original, Jar main, Jar src) {
        for (Map.Entry<String, Resource> e : original.getResources().entrySet()) {
            String path = e.getKey();
            if (path.startsWith("OSGI-OPT/src/")) {
                src.putResource(path.substring("OSGI-OPT/src/".length()), e.getValue());
                continue;
            }
            main.putResource(path, e.getValue());
        }
    }

    private void maven_gpg_sign_and_deploy(Project b, File file, String classifier, File pomFile) throws Exception {
        Command command = new Command();
        command.setTrace();
        command.add(b.getProperty("mvn", "mvn"));
        command.add("gpg:sign-and-deploy-file", "-DreleaseInfo=true", "-DpomFile=pom.xml");
        command.add("-Dfile=" + file.getAbsolutePath());
        command.add("-DrepositoryId=" + this.repository);
        command.add("-Durl=" + this.url);
        this.optional(command, "passphrase", this.passphrase);
        this.optional(command, "keyname", this.keyname);
        this.optional(command, "homedir", this.homedir);
        this.optional(command, "classifier", classifier);
        this.optional(command, "pomFile", pomFile == null ? null : pomFile.getAbsolutePath());
        StringBuilder stdout = new StringBuilder();
        StringBuilder stderr = new StringBuilder();
        int result = command.execute(stdout, stderr);
        if (result != 0) {
            b.error("Maven deploy to %s failed to sign and transfer %s because %s", this.repository, file, "" + stdout + stderr);
        }
    }

    private void optional(Command command, String key, String value) {
        if (value == null) {
            return;
        }
        command.add("-D=" + value);
    }

    private Jar javadoc(File tmp, Project b, Set<String> exports) throws Exception {
        Command command = new Command();
        command.add(b.getProperty("javadoc", "javadoc"));
        command.add("-d");
        command.add(tmp.getAbsolutePath());
        command.add("-sourcepath");
        command.add(Processor.join(b.getSourcePath(), File.pathSeparator));
        for (String packageName : exports) {
            command.add(packageName);
        }
        StringBuilder out = new StringBuilder();
        StringBuilder err = new StringBuilder();
        Command c = new Command();
        c.setTrace();
        int result = c.execute(out, err);
        if (result == 0) {
            Jar jar = new Jar(tmp);
            b.addClose(jar);
            return jar;
        }
        b.error("Error during execution of javadoc command: %s / %s", out, err);
        return null;
    }

    private File write(File base, Resource r, String fileName) throws Exception {
        File f = Processor.getFile(base, fileName);
        try (OutputStream out = IO.outputStream(f);){
            r.write(out);
        }
        return f;
    }
}

