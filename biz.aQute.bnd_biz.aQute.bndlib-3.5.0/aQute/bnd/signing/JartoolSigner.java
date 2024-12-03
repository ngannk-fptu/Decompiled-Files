/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package aQute.bnd.signing;

import aQute.bnd.annotation.plugin.BndPlugin;
import aQute.bnd.osgi.Builder;
import aQute.bnd.osgi.Constants;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Resource;
import aQute.bnd.service.Plugin;
import aQute.bnd.service.SignerPlugin;
import aQute.lib.io.IO;
import aQute.libg.command.Command;
import aQute.service.reporter.Reporter;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@BndPlugin(name="signer", parameters=Config.class)
public class JartoolSigner
implements Plugin,
SignerPlugin {
    private static final Logger logger = LoggerFactory.getLogger(JartoolSigner.class);
    String keystore;
    String storetype;
    String path = "jarsigner";
    String storepass;
    String keypass;
    String sigFile;
    String digestalg;
    String tsa;
    String tsacert;
    String tsapolicyid;

    @Override
    public void setProperties(Map<String, String> map) {
        if (map.containsKey("keystore")) {
            this.keystore = map.get("keystore");
        }
        if (map.containsKey("storetype")) {
            this.storetype = map.get("storetype");
        }
        if (map.containsKey("storepass")) {
            this.storepass = map.get("storepass");
        }
        if (map.containsKey("keypass")) {
            this.keypass = map.get("keypass");
        }
        if (map.containsKey("path")) {
            this.path = map.get("path");
        }
        if (map.containsKey("sigFile")) {
            this.sigFile = map.get("sigFile");
        }
        if (map.containsKey("digestalg")) {
            this.digestalg = map.get("digestalg");
        }
        if (map.containsKey("tsa")) {
            this.tsa = map.get("tsa");
        }
        if (map.containsKey("tsacert")) {
            this.tsacert = map.get("tsacert");
        }
        if (map.containsKey("tsapolicyid")) {
            this.tsapolicyid = map.get("tsapolicyid");
        }
    }

    @Override
    public void setReporter(Reporter processor) {
    }

    @Override
    public void sign(Builder builder, String alias) throws Exception {
        File f = builder.getFile(this.keystore);
        if (!f.isFile()) {
            builder.error("Invalid keystore %s", f.getAbsolutePath());
            return;
        }
        Jar jar = builder.getJar();
        File tmp = File.createTempFile("signdjar", ".jar");
        tmp.deleteOnExit();
        jar.write(tmp);
        Command command = new Command();
        command.add(this.path);
        if (this.keystore != null) {
            command.add("-keystore");
            command.add(f.getAbsolutePath());
        }
        if (this.storetype != null) {
            command.add("-storetype");
            command.add(this.storetype);
        }
        if (this.keypass != null) {
            command.add("-keypass");
            command.add(this.keypass);
        }
        if (this.storepass != null) {
            command.add("-storepass");
            command.add(this.storepass);
        }
        if (this.sigFile != null) {
            command.add("-sigFile");
            command.add(this.sigFile);
        }
        if (this.digestalg != null) {
            command.add("-digestalg");
            command.add(this.digestalg);
        }
        if (this.tsa != null) {
            command.add("-tsa");
            command.add(this.tsa);
        }
        if (this.tsacert != null) {
            command.add("-tsacert");
            command.add(this.tsacert);
        }
        if (this.tsapolicyid != null) {
            command.add("-tsapolicyid");
            command.add(this.tsapolicyid);
        }
        command.add(tmp.getAbsolutePath());
        command.add(alias);
        logger.debug("Jarsigner command: {}", (Object)command);
        command.setTimeout(20L, TimeUnit.SECONDS);
        StringBuilder out = new StringBuilder();
        StringBuilder err = new StringBuilder();
        int exitValue = command.execute(out, err);
        if (exitValue != 0) {
            builder.error("Signing Jar out: %s%nerr: %s", out, err);
        } else {
            logger.debug("Signing Jar out: {}\nerr: {}", (Object)out, (Object)err);
        }
        Jar signed = new Jar(tmp);
        builder.addClose(signed);
        Map<String, Resource> dir = signed.getDirectories().get("META-INF");
        for (Map.Entry<String, Resource> entry : dir.entrySet()) {
            String path = entry.getKey();
            if (!path.matches(".*\\.(DSA|RSA|SF|MF)$")) continue;
            jar.putResource(path, entry.getValue());
        }
        jar.setDoNotTouchManifest();
    }

    StringBuilder collect(final InputStream in) throws Exception {
        final StringBuilder sb = new StringBuilder();
        Thread tin = new Thread(){

            @Override
            public void run() {
                try (BufferedReader rdr = IO.reader(in, Constants.DEFAULT_CHARSET);){
                    String line = rdr.readLine();
                    while (line != null) {
                        sb.append(line);
                        line = rdr.readLine();
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        };
        tin.start();
        return sb;
    }

    static @interface Config {
        public String keystore();

        public String storetype() default "JKS";

        public String path() default "jarsigner";

        public String storepass() default "";

        public String keypass() default "";

        public String sigFile() default "";

        public String digestalg() default "";

        public String tsa() default "";

        public String tsacert() default "";

        public String tsapolicyid() default "";
    }
}

