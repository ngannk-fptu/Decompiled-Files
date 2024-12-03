/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.signing;

import aQute.bnd.osgi.EmbeddedResource;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.Resource;
import aQute.lib.base64.Base64;
import aQute.lib.io.IO;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Map;
import java.util.jar.Manifest;
import java.util.regex.Pattern;

public class Signer
extends Processor {
    static final int BUFFER_SIZE = 4096;
    static Pattern METAINFDIR = Pattern.compile("META-INF/[^/]*");
    String[] digestNames = new String[]{"MD5"};
    File keystoreFile = new File("keystore");
    String password;
    String alias;

    public void signJar(Jar jar) {
        if (this.digestNames == null || this.digestNames.length == 0) {
            this.error("Need at least one digest algorithm name, none are specified", new Object[0]);
        }
        if (this.keystoreFile == null || !this.keystoreFile.getAbsoluteFile().exists()) {
            this.error("No such keystore file: %s", this.keystoreFile);
            return;
        }
        if (this.alias == null) {
            this.error("Private key alias not set for signing", new Object[0]);
            return;
        }
        MessageDigest[] digestAlgorithms = new MessageDigest[this.digestNames.length];
        this.getAlgorithms(this.digestNames, digestAlgorithms);
        try {
            Manifest manifest = jar.getManifest();
            manifest.getMainAttributes().putValue("Signed-By", "Bnd");
            ByteArrayOutputStream o = new ByteArrayOutputStream();
            manifest.write(o);
            this.doManifest(jar, this.digestNames, digestAlgorithms, o);
            o.flush();
            byte[] newManifestBytes = o.toByteArray();
            jar.putResource("META-INF/MANIFEST.MF", new EmbeddedResource(newManifestBytes, 0L));
            byte[] signatureFileBytes = this.doSignatureFile(this.digestNames, digestAlgorithms, newManifestBytes);
            jar.putResource("META-INF/BND.SF", new EmbeddedResource(signatureFileBytes, 0L));
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            KeyStore.PrivateKeyEntry privateKeyEntry = null;
            try (InputStream keystoreInputStream = IO.stream(this.keystoreFile);){
                char[] pw = this.password == null ? new char[]{} : this.password.toCharArray();
                keystore.load(keystoreInputStream, pw);
                keystoreInputStream.close();
                privateKeyEntry = (KeyStore.PrivateKeyEntry)keystore.getEntry(this.alias, new KeyStore.PasswordProtection(pw));
            }
            catch (Exception e) {
                this.exception(e, "Not able to load the private key from the given keystore(%s) with alias %s", this.keystoreFile.getAbsolutePath(), this.alias);
                return;
            }
            PrivateKey privateKey = privateKeyEntry.getPrivateKey();
            Signature signature = Signature.getInstance("MD5withRSA");
            signature.initSign(privateKey);
            signature.update(signatureFileBytes);
            signature.sign();
            ByteArrayOutputStream tmpStream = new ByteArrayOutputStream();
            jar.putResource("META-INF/BND.RSA", new EmbeddedResource(tmpStream.toByteArray(), 0L));
        }
        catch (Exception e) {
            this.exception(e, "During signing: %s", e);
        }
    }

    /*
     * Exception decompiling
     */
    private byte[] doSignatureFile(String[] digestNames, MessageDigest[] algorithms, byte[] manbytes) throws IOException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void doManifest(Jar jar, String[] digestNames, MessageDigest[] algorithms, OutputStream out) throws Exception {
        PrintWriter w = IO.writer(out, StandardCharsets.UTF_8);
        try {
            for (Map.Entry<String, Resource> entry : jar.getResources().entrySet()) {
                String name = entry.getKey();
                if (METAINFDIR.matcher(name).matches()) continue;
                ((Writer)w).write("\r\n");
                ((Writer)w).write("Name: ");
                ((Writer)w).write(name);
                ((Writer)w).write("\r\n");
                this.digest(algorithms, entry.getValue());
                for (int a = 0; a < algorithms.length; ++a) {
                    if (algorithms[a] == null) continue;
                    byte[] digest = algorithms[a].digest();
                    String header = digestNames[a] + "-Digest: " + new Base64(digest) + "\r\n";
                    ((Writer)w).write(header);
                }
            }
        }
        finally {
            ((Writer)w).flush();
        }
    }

    private void digest(MessageDigest[] algorithms, Resource r) throws Exception {
        try (InputStream in = r.openInputStream();){
            byte[] data = new byte[4096];
            int size = in.read(data);
            while (size > 0) {
                for (int a = 0; a < algorithms.length; ++a) {
                    if (algorithms[a] == null) continue;
                    algorithms[a].update(data, 0, size);
                }
                size = in.read(data);
            }
        }
    }

    private void getAlgorithms(String[] digestNames, MessageDigest[] algorithms) {
        for (int i = 0; i < algorithms.length; ++i) {
            String name = digestNames[i];
            try {
                algorithms[i] = MessageDigest.getInstance(name);
                continue;
            }
            catch (NoSuchAlgorithmException e) {
                this.exception(e, "Specified digest algorithm %s, but not such algorithm was found", digestNames[i]);
            }
        }
    }

    public void setPassword(String string) {
        this.password = string;
    }

    public void setKeystore(File keystore) {
        this.keystoreFile = keystore;
    }

    public void setAlias(String string) {
        this.alias = string;
    }
}

