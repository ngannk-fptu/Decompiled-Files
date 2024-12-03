/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 */
package org.apache.tika.fork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.NotSerializableException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import org.apache.commons.io.IOUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.fork.ClassLoaderProxy;
import org.apache.tika.fork.ClassLoaderResource;
import org.apache.tika.fork.ContentHandlerProxy;
import org.apache.tika.fork.ContentHandlerResource;
import org.apache.tika.fork.ForkObjectInputStream;
import org.apache.tika.fork.ForkProxy;
import org.apache.tika.fork.ForkResource;
import org.apache.tika.fork.ForkServer;
import org.apache.tika.fork.InputStreamProxy;
import org.apache.tika.fork.InputStreamResource;
import org.apache.tika.fork.MemoryURLConnection;
import org.apache.tika.fork.MemoryURLStreamHandler;
import org.apache.tika.fork.MemoryURLStreamHandlerFactory;
import org.apache.tika.fork.MemoryURLStreamRecord;
import org.apache.tika.fork.ParserFactoryFactory;
import org.apache.tika.fork.RecursiveMetadataContentHandlerProxy;
import org.apache.tika.fork.RecursiveMetadataContentHandlerResource;
import org.apache.tika.fork.TimeoutLimits;
import org.apache.tika.sax.AbstractRecursiveParserWrapperHandler;
import org.apache.tika.sax.RecursiveParserWrapperHandler;
import org.apache.tika.utils.ProcessUtils;
import org.xml.sax.ContentHandler;

class ForkClient {
    private static final AtomicInteger CLIENT_COUNTER = new AtomicInteger(0);
    private final List<ForkResource> resources = new ArrayList<ForkResource>();
    private final ClassLoader loader;
    private final File jar;
    private final Process process;
    private final DataOutputStream output;
    private final DataInputStream input;
    private final int id = CLIENT_COUNTER.incrementAndGet();
    private volatile int filesProcessed = 0;

    public ForkClient(Path tikaDir, ParserFactoryFactory parserFactoryFactory, List<String> java, TimeoutLimits timeoutLimits) throws IOException, TikaException {
        this(tikaDir, parserFactoryFactory, null, java, timeoutLimits);
    }

    public ForkClient(Path tikaDir, ParserFactoryFactory parserFactoryFactory, ClassLoader classLoader, List<String> java, TimeoutLimits timeoutLimits) throws IOException, TikaException {
        this.jar = null;
        this.loader = null;
        boolean ok = false;
        ProcessBuilder builder = new ProcessBuilder(new String[0]);
        ArrayList<String> command = new ArrayList<String>(java);
        command.add("-cp");
        String dirString = tikaDir.toAbsolutePath().toString();
        dirString = !dirString.endsWith("/") ? dirString + "/*" : dirString + "/";
        dirString = ProcessUtils.escapeCommandLine(dirString);
        command.add(dirString);
        command.add("org.apache.tika.fork.ForkServer");
        command.add(Long.toString(timeoutLimits.getPulseMS()));
        command.add(Long.toString(timeoutLimits.getParseTimeoutMS()));
        command.add(Long.toString(timeoutLimits.getWaitTimeoutMS()));
        builder.command(command);
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);
        try {
            this.process = builder.start();
            this.output = new DataOutputStream(this.process.getOutputStream());
            this.input = new DataInputStream(this.process.getInputStream());
            this.waitForStartBeacon();
            if (classLoader != null) {
                this.output.writeByte(8);
            } else {
                this.output.writeByte(6);
            }
            this.output.flush();
            this.sendObject(parserFactoryFactory, this.resources);
            if (classLoader != null) {
                this.sendObject(classLoader, this.resources);
            }
            this.waitForStartBeacon();
            ok = true;
        }
        catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
        finally {
            if (!ok) {
                this.close();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ForkClient(ClassLoader loader, Object object, List<String> java, TimeoutLimits timeoutLimits) throws IOException, TikaException {
        boolean ok = false;
        try {
            this.loader = loader;
            this.jar = ForkClient.createBootstrapJar();
            ProcessBuilder builder = new ProcessBuilder(new String[0]);
            ArrayList<String> command = new ArrayList<String>(java);
            command.add("-jar");
            command.add(this.jar.getPath());
            command.add(Long.toString(timeoutLimits.getPulseMS()));
            command.add(Long.toString(timeoutLimits.getParseTimeoutMS()));
            command.add(Long.toString(timeoutLimits.getWaitTimeoutMS()));
            builder.command(command);
            builder.redirectError(ProcessBuilder.Redirect.INHERIT);
            this.process = builder.start();
            this.output = new DataOutputStream(this.process.getOutputStream());
            this.input = new DataInputStream(this.process.getInputStream());
            this.waitForStartBeacon();
            this.output.writeByte(7);
            this.output.flush();
            this.sendObject(loader, this.resources);
            this.sendObject(object, this.resources);
            this.waitForStartBeacon();
            ok = true;
        }
        finally {
            if (!ok) {
                this.close();
            }
        }
    }

    private static File createBootstrapJar() throws IOException {
        File file = Files.createTempFile("apache-tika-fork-", ".jar", new FileAttribute[0]).toFile();
        boolean ok = false;
        try {
            ForkClient.fillBootstrapJar(file);
            ok = true;
        }
        finally {
            if (!ok) {
                file.delete();
            }
        }
        return file;
    }

    private static void fillBootstrapJar(File file) throws IOException {
        try (JarOutputStream jar = new JarOutputStream(new FileOutputStream(file));){
            String manifest = "Main-Class: " + ForkServer.class.getName() + "\n";
            jar.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));
            jar.write(manifest.getBytes(StandardCharsets.UTF_8));
            Class[] bootstrap = new Class[]{ForkServer.class, ForkObjectInputStream.class, ForkProxy.class, ClassLoaderProxy.class, MemoryURLConnection.class, MemoryURLStreamHandler.class, MemoryURLStreamHandlerFactory.class, MemoryURLStreamRecord.class, TikaException.class};
            ClassLoader loader = ForkServer.class.getClassLoader();
            for (Class klass : bootstrap) {
                String path = klass.getName().replace('.', '/') + ".class";
                try (InputStream input = loader.getResourceAsStream(path);){
                    jar.putNextEntry(new JarEntry(path));
                    IOUtils.copy((InputStream)input, (OutputStream)jar);
                }
            }
        }
    }

    private void waitForStartBeacon() throws IOException {
        int type;
        do {
            if ((byte)(type = this.input.read()) == 4) {
                return;
            }
            if ((byte)type != 5) continue;
            throw new IOException("Server had a catastrophic initialization failure");
        } while (type != -1);
        throw new IOException("EOF while waiting for start beacon");
    }

    public synchronized boolean ping() {
        try {
            this.output.writeByte(2);
            this.output.flush();
            int type = this.input.read();
            return type == 2;
        }
        catch (IOException e) {
            return false;
        }
    }

    public synchronized Throwable call(String method, Object ... args) throws IOException, TikaException {
        ++this.filesProcessed;
        ArrayList<ForkResource> r = new ArrayList<ForkResource>(this.resources);
        this.output.writeByte(1);
        this.output.writeUTF(method);
        for (Object arg : args) {
            this.sendObject(arg, r);
        }
        return this.waitForResponse(r);
    }

    public int getFilesProcessed() {
        return this.filesProcessed;
    }

    private void sendObject(Object object, List<ForkResource> resources) throws IOException, TikaException {
        int n = resources.size();
        if (object instanceof InputStream) {
            resources.add(new InputStreamResource((InputStream)object));
            object = new InputStreamProxy(n);
        } else if (object instanceof RecursiveParserWrapperHandler) {
            resources.add(new RecursiveMetadataContentHandlerResource((RecursiveParserWrapperHandler)object));
            object = new RecursiveMetadataContentHandlerProxy(n, ((RecursiveParserWrapperHandler)object).getContentHandlerFactory());
        } else if (object instanceof ContentHandler && !(object instanceof AbstractRecursiveParserWrapperHandler)) {
            resources.add(new ContentHandlerResource((ContentHandler)object));
            object = new ContentHandlerProxy(n);
        } else if (object instanceof ClassLoader) {
            resources.add(new ClassLoaderResource((ClassLoader)object));
            object = new ClassLoaderProxy(n);
        }
        try {
            ForkObjectInputStream.sendObject(object, this.output);
        }
        catch (NotSerializableException nse) {
            throw new TikaException("Unable to serialize " + object.getClass().getSimpleName() + " to pass to the Forked Parser", nse);
        }
        this.waitForResponse(resources);
    }

    public synchronized void close() {
        try {
            if (this.output != null) {
                this.output.close();
            }
            if (this.input != null) {
                this.input.close();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        if (this.process != null) {
            this.process.destroyForcibly();
            try {
                this.process.waitFor();
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
        }
        if (this.jar != null) {
            this.jar.delete();
        }
    }

    private Throwable waitForResponse(List<ForkResource> resources) throws IOException {
        this.output.flush();
        while (!Thread.currentThread().isInterrupted()) {
            int type = this.input.read();
            if (type == -1) {
                throw new IOException("Lost connection to a forked server process");
            }
            if (type == 3) {
                ForkResource resource = resources.get(this.input.readUnsignedByte());
                resource.process(this.input, this.output);
                continue;
            }
            if ((byte)type == -1) {
                try {
                    return (Throwable)ForkObjectInputStream.readObject(this.input, this.loader);
                }
                catch (ClassNotFoundException e) {
                    throw new IOException("Unable to deserialize an exception", e);
                }
            }
            return null;
        }
        throw new IOException(new InterruptedException());
    }

    public int getId() {
        return this.id;
    }
}

