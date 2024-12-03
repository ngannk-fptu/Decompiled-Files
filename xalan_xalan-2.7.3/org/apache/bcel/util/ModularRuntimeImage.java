/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ModularRuntimeImage
implements Closeable {
    static final String MODULES_PATH = File.separator + "modules";
    static final String PACKAGES_PATH = File.separator + "packages";
    private final URLClassLoader classLoader;
    private final FileSystem fileSystem;

    public ModularRuntimeImage() {
        this(null, FileSystems.getFileSystem(URI.create("jrt:/")));
    }

    public ModularRuntimeImage(String javaHome) throws IOException {
        Map emptyMap = Collections.emptyMap();
        Path jrePath = Paths.get(javaHome, new String[0]);
        Path jrtFsPath = jrePath.resolve("lib").resolve("jrt-fs.jar");
        this.classLoader = URLClassLoader.newInstance(new URL[]{jrtFsPath.toUri().toURL()});
        this.fileSystem = FileSystems.newFileSystem(URI.create("jrt:/"), emptyMap, (ClassLoader)this.classLoader);
    }

    private ModularRuntimeImage(URLClassLoader cl, FileSystem fs) {
        this.classLoader = cl;
        this.fileSystem = fs;
    }

    @Override
    public void close() throws IOException {
        if (this.classLoader != null) {
            this.classLoader.close();
        }
        if (this.fileSystem != null) {
            this.fileSystem.close();
        }
    }

    public FileSystem getFileSystem() {
        return this.fileSystem;
    }

    public List<Path> list(Path dirPath) throws IOException {
        ArrayList<Path> list = new ArrayList<Path>();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(dirPath);){
            ds.forEach(list::add);
        }
        return list;
    }

    public List<Path> list(String dirName) throws IOException {
        return this.list(this.fileSystem.getPath(dirName, new String[0]));
    }

    public List<Path> modules() throws IOException {
        return this.list(MODULES_PATH);
    }

    public List<Path> packages() throws IOException {
        return this.list(PACKAGES_PATH);
    }
}

