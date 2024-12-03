/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.tool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipException;
import javax.tools.JavaFileObject;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.tool.Archive;
import org.eclipse.jdt.internal.compiler.tool.ArchiveFileObject;
import org.eclipse.jdt.internal.compiler.tool.ModuleLocationHandler;
import org.eclipse.jdt.internal.compiler.tool.Util;
import org.eclipse.jdt.internal.compiler.util.JRTUtil;

public class JrtFileSystem
extends Archive {
    private static URI JRT_URI = URI.create("jrt:/");
    static final String BOOT_MODULE = "jrt-fs.jar";
    public HashMap<String, Path> modulePathMap;
    Path modules;
    private FileSystem jrtfs;

    public JrtFileSystem(File file) throws ZipException, IOException {
        this.file = file;
        this.initialize();
    }

    public void initialize() throws IOException {
        block10: {
            this.modulePathMap = new HashMap();
            URL jrtPath = null;
            if (this.file.exists()) {
                jrtPath = Paths.get(this.file.toPath().toString(), "lib", BOOT_MODULE).toUri().toURL();
                Throwable throwable = null;
                Object var3_4 = null;
                try (URLClassLoader loader = new URLClassLoader(new URL[]{jrtPath});){
                    HashMap env = new HashMap();
                    this.jrtfs = FileSystems.newFileSystem(JRT_URI, env, (ClassLoader)loader);
                    this.modules = this.jrtfs.getPath("/modules", new String[0]);
                    break block10;
                }
                catch (Throwable throwable2) {
                    if (throwable == null) {
                        throwable = throwable2;
                    } else if (throwable != throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                    throw throwable;
                }
            }
            return;
        }
        JRTUtil.walkModuleImage(this.file, new JRTUtil.JrtFileVisitor<Path>(){

            @Override
            public FileVisitResult visitPackage(Path dir, Path mod, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path f, Path mod, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitModule(Path path, String name) throws IOException {
                JrtFileSystem.this.modulePathMap.put(name, path);
                return FileVisitResult.CONTINUE;
            }
        }, 4);
    }

    public List<JrtFileObject> list(ModuleLocationHandler.ModuleLocationWrapper location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse, Charset charset) {
        String module = location.modName;
        Path mPath = this.modules.resolve(module);
        Path resolve = mPath.resolve(packageName);
        List files = null;
        try {
            Throwable throwable = null;
            Object var11_12 = null;
            try (Stream<Path> p = Files.list(resolve);){
                files = p.filter(path -> !Files.isDirectory(path, new LinkOption[0])).collect(Collectors.toList());
            }
            catch (Throwable throwable2) {
                if (throwable == null) {
                    throwable = throwable2;
                } else if (throwable != throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
        }
        catch (IOException iOException) {}
        ArrayList<JrtFileObject> result = new ArrayList<JrtFileObject>();
        for (Path p : files) {
            result.add(new JrtFileObject(this.file, p, module, charset));
        }
        return result;
    }

    @Override
    public ArchiveFileObject getArchiveFileObject(String fileName, String module, Charset charset) {
        return new JrtFileObject(this.file, this.modules.resolve(module).resolve(fileName), module, charset);
    }

    @Override
    public boolean contains(String entryName) {
        return false;
    }

    @Override
    public String toString() {
        return "JRT: " + (this.file == null ? "UNKNOWN_ARCHIVE" : this.file.getAbsolutePath());
    }

    class JrtFileObject
    extends ArchiveFileObject {
        String module;
        Path path;

        private JrtFileObject(File file, Path path, String module, Charset charset) {
            super(file, path.toString(), charset);
            this.path = path;
        }

        @Override
        protected void finalize() throws Throwable {
        }

        @Override
        protected ClassFileReader getClassReader() {
            byte[] content;
            ClassFileReader reader;
            block4: {
                reader = null;
                content = JRTUtil.getClassfileContent(this.file, this.entryName, this.module);
                if (content != null) break block4;
                return null;
            }
            try {
                return new ClassFileReader(content, this.entryName.toCharArray());
            }
            catch (ClassFormatException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return reader;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return Util.getCharContents(this, ignoreEncodingErrors, JRTUtil.getClassfileContent(this.file, this.entryName, this.module), this.charset.name());
        }

        @Override
        public long getLastModified() {
            return 0L;
        }

        @Override
        public String getName() {
            return this.path.toString();
        }

        @Override
        public InputStream openInputStream() throws IOException {
            return Files.newInputStream(this.path, new OpenOption[0]);
        }

        @Override
        public OutputStream openOutputStream() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Writer openWriter() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public URI toUri() {
            try {
                return new URI("JRT:" + this.file.toURI().getPath() + "!" + this.entryName);
            }
            catch (URISyntaxException uRISyntaxException) {
                return null;
            }
        }

        @Override
        public String toString() {
            return String.valueOf(this.file.getAbsolutePath()) + "[" + this.entryName + "]";
        }
    }
}

