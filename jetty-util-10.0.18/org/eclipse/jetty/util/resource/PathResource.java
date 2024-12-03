/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.util.resource;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathResource
extends Resource {
    private static final Logger LOG = LoggerFactory.getLogger(PathResource.class);
    private static final LinkOption[] NO_FOLLOW_LINKS = new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
    private static final LinkOption[] FOLLOW_LINKS = new LinkOption[0];
    private final Path path;
    private final Path alias;
    private final URI uri;
    private final boolean belongsToDefaultFileSystem;

    private Path checkAliasPath() {
        Path normal;
        Path abs = this.path;
        if (!URIUtil.equalsIgnoreEncodings(this.uri, this.path.toUri())) {
            try {
                return Paths.get(this.uri).toRealPath(FOLLOW_LINKS);
            }
            catch (IOException ignored) {
                LOG.trace("IGNORED", (Throwable)ignored);
            }
        }
        if (!abs.isAbsolute()) {
            abs = this.path.toAbsolutePath();
        }
        if (!PathResource.isSameName(abs, normal = this.path.normalize())) {
            return normal;
        }
        try {
            Path real;
            if (Files.isSymbolicLink(this.path)) {
                return this.path.getParent().resolve(Files.readSymbolicLink(this.path));
            }
            if (Files.exists(this.path, new LinkOption[0]) && !PathResource.isSameName(abs, real = abs.toRealPath(FOLLOW_LINKS))) {
                return real;
            }
        }
        catch (IOException e) {
            LOG.trace("IGNORED", (Throwable)e);
        }
        catch (Exception e) {
            LOG.warn("bad alias ({} {}) for {}", new Object[]{e.getClass().getName(), e.getMessage(), this.path});
        }
        return null;
    }

    public static boolean isSameName(Path pathA, Path pathB) {
        int bCount;
        int aCount = pathA.getNameCount();
        if (aCount != (bCount = pathB.getNameCount())) {
            return false;
        }
        int i = bCount;
        while (i-- > 0) {
            if (pathA.getName(i).toString().equals(pathB.getName(i).toString())) continue;
            return false;
        }
        return true;
    }

    public PathResource(File file) {
        this(file.toPath());
    }

    public PathResource(Path path) {
        Path absPath;
        block2: {
            absPath = path;
            try {
                absPath = path.toRealPath(NO_FOLLOW_LINKS);
            }
            catch (IOError | IOException e) {
                if (!LOG.isDebugEnabled()) break block2;
                LOG.debug("Unable to get real/canonical path for {}", (Object)path, (Object)e);
            }
        }
        this.path = absPath;
        this.assertValidPath(path);
        this.uri = this.path.toUri();
        this.alias = this.checkAliasPath();
        this.belongsToDefaultFileSystem = this.path.getFileSystem() == FileSystems.getDefault();
    }

    private PathResource(PathResource parent, String childPath) {
        this.path = parent.path.getFileSystem().getPath(parent.path.toString(), new String[]{childPath});
        if (this.isDirectory() && !((String)childPath).endsWith("/")) {
            childPath = (String)childPath + "/";
        }
        this.uri = URIUtil.addPath(parent.uri, (String)childPath);
        this.alias = this.checkAliasPath();
        this.belongsToDefaultFileSystem = this.path.getFileSystem() == FileSystems.getDefault();
    }

    public PathResource(URI uri) throws IOException {
        Path path;
        if (!uri.isAbsolute()) {
            throw new IllegalArgumentException("not an absolute uri");
        }
        if (!uri.getScheme().equalsIgnoreCase("file")) {
            throw new IllegalArgumentException("not file: scheme");
        }
        try {
            path = Paths.get(uri);
        }
        catch (IllegalArgumentException e) {
            throw e;
        }
        catch (Exception e) {
            LOG.trace("IGNORED", (Throwable)e);
            throw new IOException("Unable to build Path from: " + uri, e);
        }
        this.path = path.toAbsolutePath();
        this.uri = path.toUri();
        this.alias = this.checkAliasPath();
        this.belongsToDefaultFileSystem = this.path.getFileSystem() == FileSystems.getDefault();
    }

    public PathResource(URL url) throws IOException, URISyntaxException {
        this(url.toURI());
    }

    @Override
    public boolean isSame(Resource resource) {
        block3: {
            try {
                if (resource instanceof PathResource) {
                    Path path = ((PathResource)resource).getPath();
                    return Files.isSameFile(this.getPath(), path);
                }
            }
            catch (IOException e) {
                if (!LOG.isDebugEnabled()) break block3;
                LOG.debug("ignored", (Throwable)e);
            }
        }
        return false;
    }

    @Override
    public Resource addPath(String subPath) throws IOException {
        if (URIUtil.canonicalPath(subPath) == null) {
            throw new MalformedURLException(subPath);
        }
        if ("/".equals(subPath)) {
            return this;
        }
        return new PathResource(this, subPath);
    }

    private void assertValidPath(Path path) {
        String str = path.toString();
        int idx = StringUtil.indexOfControlChars(str);
        if (idx >= 0) {
            throw new InvalidPathException(str, "Invalid Character at index " + idx);
        }
    }

    @Override
    public void close() {
    }

    @Override
    public boolean delete() throws SecurityException {
        try {
            return Files.deleteIfExists(this.path);
        }
        catch (IOException e) {
            LOG.trace("IGNORED", (Throwable)e);
            return false;
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        PathResource other = (PathResource)obj;
        return !(this.path == null ? other.path != null : !this.path.equals(other.path));
    }

    @Override
    public boolean exists() {
        return Files.exists(this.path, NO_FOLLOW_LINKS);
    }

    @Override
    public File getFile() throws IOException {
        if (!this.belongsToDefaultFileSystem) {
            return null;
        }
        return this.path.toFile();
    }

    public Path getPath() {
        return this.path;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return Files.newInputStream(this.path, StandardOpenOption.READ);
    }

    @Override
    public String getName() {
        return this.path.toAbsolutePath().toString();
    }

    @Override
    public ReadableByteChannel getReadableByteChannel() throws IOException {
        return this.newSeekableByteChannel();
    }

    public SeekableByteChannel newSeekableByteChannel() throws IOException {
        return Files.newByteChannel(this.path, StandardOpenOption.READ);
    }

    @Override
    public URI getURI() {
        return this.uri;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.path == null ? 0 : this.path.hashCode());
        return result;
    }

    @Override
    public boolean isContainedIn(Resource r) {
        try {
            PathResource pr = (PathResource)PathResource.class.cast(r);
            return this.path.startsWith(pr.getPath());
        }
        catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public boolean isDirectory() {
        return Files.isDirectory(this.path, FOLLOW_LINKS);
    }

    @Override
    public long lastModified() {
        try {
            FileTime ft = Files.getLastModifiedTime(this.path, FOLLOW_LINKS);
            return ft.toMillis();
        }
        catch (IOException e) {
            LOG.trace("IGNORED", (Throwable)e);
            return 0L;
        }
    }

    @Override
    public long length() {
        try {
            return Files.size(this.path);
        }
        catch (IOException e) {
            return 0L;
        }
    }

    @Override
    public boolean isAlias() {
        return this.alias != null;
    }

    public Path getAliasPath() {
        return this.alias;
    }

    @Override
    public URI getAlias() {
        return this.alias == null ? null : this.alias.toUri();
    }

    @Override
    public String[] list() {
        block12: {
            String[] stringArray;
            block11: {
                DirectoryStream<Path> dir = Files.newDirectoryStream(this.path);
                try {
                    ArrayList<String> entries = new ArrayList<String>();
                    for (Path entry : dir) {
                        Object name = entry.getFileName().toString();
                        if (Files.isDirectory(entry, new LinkOption[0])) {
                            name = (String)name + "/";
                        }
                        entries.add((String)name);
                    }
                    int size = entries.size();
                    stringArray = entries.toArray(new String[size]);
                    if (dir == null) break block11;
                }
                catch (Throwable throwable) {
                    try {
                        if (dir != null) {
                            try {
                                dir.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    catch (DirectoryIteratorException e) {
                        LOG.debug("Directory list failure", (Throwable)e);
                        break block12;
                    }
                    catch (IOException e) {
                        LOG.debug("Directory list access failure", (Throwable)e);
                    }
                }
                dir.close();
            }
            return stringArray;
        }
        return null;
    }

    @Override
    public boolean renameTo(Resource dest) throws SecurityException {
        if (dest instanceof PathResource) {
            PathResource destRes = (PathResource)dest;
            try {
                Path result = Files.move(this.path, destRes.path, new CopyOption[0]);
                return Files.exists(result, NO_FOLLOW_LINKS);
            }
            catch (IOException e) {
                LOG.trace("IGNORED", (Throwable)e);
                return false;
            }
        }
        return false;
    }

    @Override
    public void copyTo(File destination) throws IOException {
        if (this.isDirectory()) {
            IO.copyDir(this.path.toFile(), destination);
        } else {
            Files.copy(this.path, destination.toPath(), new CopyOption[0]);
        }
    }

    public String toString() {
        return this.uri.toASCIIString();
    }
}

