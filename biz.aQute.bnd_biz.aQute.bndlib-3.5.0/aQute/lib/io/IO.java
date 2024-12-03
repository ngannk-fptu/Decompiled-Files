/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.io;

import aQute.lib.io.ByteBufferInputStream;
import aQute.lib.io.CharBufferReader;
import aQute.libg.glob.Glob;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutput;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.regex.Pattern;

public class IO {
    static final int BUFFER_SIZE = 65536;
    private static final int DIRECT_MAP_THRESHOLD = 65536;
    private static final boolean isWindows = File.separatorChar == '\\';
    public static final File work = new File(System.getProperty("user.dir"));
    public static final File home;
    private static final EnumSet<StandardOpenOption> writeOptions;
    private static final EnumSet<StandardOpenOption> readOptions;
    public static final OutputStream nullStream;
    public static final Writer nullWriter;
    static final Pattern RESERVED_WINDOWS_P;

    public static String getExtension(String fileName, String deflt) {
        int n = fileName.lastIndexOf(46);
        if (n < 0) {
            return deflt;
        }
        return fileName.substring(n + 1);
    }

    public static Collection<File> tree(File current) {
        LinkedHashSet<File> files = new LinkedHashSet<File>();
        IO.traverse(files, current, null);
        return files;
    }

    public static Collection<File> tree(File current, String glob) {
        LinkedHashSet<File> files = new LinkedHashSet<File>();
        IO.traverse(files, current, glob == null ? null : new Glob(glob));
        return files;
    }

    private static void traverse(Collection<File> files, File current, Glob glob) {
        if (current.isFile() && (glob == null || glob.matcher(current.getName()).matches())) {
            files.add(current);
        } else if (current.isDirectory()) {
            for (File sub : current.listFiles()) {
                IO.traverse(files, sub, glob);
            }
        }
    }

    public static File copy(byte[] data, File file) throws IOException {
        IO.copy(data, file.toPath());
        return file;
    }

    public static Path copy(byte[] data, Path path) throws IOException {
        try (FileChannel out = IO.writeChannel(path);){
            ByteBuffer bb = ByteBuffer.wrap(data);
            while (bb.hasRemaining()) {
                out.write(bb);
            }
        }
        return path;
    }

    public static Writer copy(byte[] data, Writer w) throws IOException {
        w.write(new String(data, 0, data.length, StandardCharsets.UTF_8));
        return w;
    }

    public static OutputStream copy(byte[] data, OutputStream out) throws IOException {
        out.write(data, 0, data.length);
        return out;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Writer copy(Reader r, Writer w) throws IOException {
        try {
            int size;
            char[] buffer = new char[65536];
            while ((size = r.read(buffer, 0, buffer.length)) > 0) {
                w.write(buffer, 0, size);
            }
            Writer writer = w;
            return writer;
        }
        finally {
            r.close();
        }
    }

    public static OutputStream copy(Reader r, OutputStream out) throws IOException {
        return IO.copy(r, out, StandardCharsets.UTF_8);
    }

    public static OutputStream copy(Reader r, OutputStream out, String charset) throws IOException {
        return IO.copy(r, out, Charset.forName(charset));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static OutputStream copy(Reader r, OutputStream out, Charset charset) throws IOException {
        PrintWriter w = IO.writer(out, charset);
        try {
            IO.copy(r, (Writer)w);
            OutputStream outputStream = out;
            return outputStream;
        }
        finally {
            ((Writer)w).flush();
        }
    }

    public static Writer copy(InputStream in, Writer w) throws IOException {
        return IO.copy(in, w, StandardCharsets.UTF_8);
    }

    public static Writer copy(InputStream in, Writer w, String charset) throws IOException {
        return IO.copy(in, w, Charset.forName(charset));
    }

    public static Writer copy(InputStream in, Writer w, Charset charset) throws IOException {
        return IO.copy((Reader)IO.reader(in, charset), w);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static OutputStream copy(InputStream in, OutputStream out) throws IOException {
        try {
            int size;
            byte[] buffer = new byte[65536];
            while ((size = in.read(buffer, 0, buffer.length)) > 0) {
                out.write(buffer, 0, size);
            }
            OutputStream outputStream = out;
            return outputStream;
        }
        finally {
            in.close();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static DataOutput copy(InputStream in, DataOutput out) throws IOException {
        try {
            int size;
            byte[] buffer = new byte[65536];
            while ((size = in.read(buffer, 0, buffer.length)) > 0) {
                out.write(buffer, 0, size);
            }
            DataOutput dataOutput = out;
            return dataOutput;
        }
        finally {
            in.close();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static WritableByteChannel copy(ReadableByteChannel in, WritableByteChannel out) throws IOException {
        try {
            ByteBuffer bb = ByteBuffer.allocateDirect(65536);
            while (in.read(bb) > 0) {
                bb.flip();
                out.write(bb);
                bb.compact();
            }
            bb.flip();
            while (bb.hasRemaining()) {
                out.write(bb);
            }
            WritableByteChannel writableByteChannel = out;
            return writableByteChannel;
        }
        finally {
            in.close();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ByteBuffer copy(InputStream in, ByteBuffer bb) throws IOException {
        try {
            if (bb.hasArray()) {
                int size;
                byte[] buffer = bb.array();
                int offset = bb.arrayOffset();
                while (bb.hasRemaining() && (size = in.read(buffer, offset + bb.position(), bb.remaining())) > 0) {
                    bb.position(bb.position() + size);
                }
            } else {
                int size;
                int length = Math.min(bb.remaining(), 65536);
                byte[] buffer = new byte[length];
                while (length > 0 && (size = in.read(buffer, 0, length)) > 0) {
                    bb.put(buffer, 0, size);
                    length = Math.min(bb.remaining(), buffer.length);
                }
            }
            ByteBuffer byteBuffer = bb;
            return byteBuffer;
        }
        finally {
            in.close();
        }
    }

    public static byte[] copy(InputStream in, byte[] data) throws IOException {
        return IO.copy(in, data, 0, data.length);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] copy(InputStream in, byte[] data, int off, int len) throws IOException {
        try {
            int size;
            int remaining;
            while ((remaining = len - off) > 0 && (size = in.read(data, off, remaining)) > 0) {
                off += size;
            }
            byte[] byArray = data;
            return byArray;
        }
        finally {
            in.close();
        }
    }

    public static OutputStream copy(ByteBuffer bb, OutputStream out) throws IOException {
        if (bb.hasArray()) {
            out.write(bb.array(), bb.arrayOffset() + bb.position(), bb.remaining());
            bb.position(bb.limit());
        } else {
            int length = Math.min(bb.remaining(), 65536);
            byte[] buffer = new byte[length];
            while (length > 0) {
                bb.get(buffer, 0, length);
                out.write(buffer, 0, length);
                length = Math.min(bb.remaining(), buffer.length);
            }
        }
        return out;
    }

    public static MessageDigest copy(URL url, MessageDigest md) throws IOException {
        return IO.copy(IO.stream(url), md);
    }

    public static MessageDigest copy(File file, MessageDigest md) throws IOException {
        return IO.copy(file.toPath(), md);
    }

    public static MessageDigest copy(Path path, MessageDigest md) throws IOException {
        return IO.copy((ReadableByteChannel)IO.readChannel(path), md);
    }

    public static MessageDigest copy(URLConnection conn, MessageDigest md) throws IOException {
        return IO.copy(conn.getInputStream(), md);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static MessageDigest copy(InputStream in, MessageDigest md) throws IOException {
        try {
            int size;
            byte[] buffer = new byte[65536];
            while ((size = in.read(buffer, 0, buffer.length)) > 0) {
                md.update(buffer, 0, size);
            }
            MessageDigest messageDigest = md;
            return messageDigest;
        }
        finally {
            in.close();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static MessageDigest copy(ReadableByteChannel in, MessageDigest md) throws IOException {
        try {
            ByteBuffer bb = ByteBuffer.allocate(65536);
            while (in.read(bb) > 0) {
                bb.flip();
                md.update(bb);
                bb.compact();
            }
            bb.flip();
            while (bb.hasRemaining()) {
                md.update(bb);
            }
            MessageDigest messageDigest = md;
            return messageDigest;
        }
        finally {
            in.close();
        }
    }

    public static File copy(URL url, File file) throws IOException {
        return IO.copy(IO.stream(url), file);
    }

    public static File copy(URLConnection conn, File file) throws IOException {
        return IO.copy(conn.getInputStream(), file);
    }

    public static URL copy(InputStream in, URL url) throws IOException {
        return IO.copy(in, url, null);
    }

    /*
     * Loose catch block
     */
    public static URL copy(InputStream in, URL url, String method) throws IOException {
        HttpURLConnection http;
        URLConnection c = url.openConnection();
        HttpURLConnection httpURLConnection = http = c instanceof HttpURLConnection ? (HttpURLConnection)c : null;
        if (http != null && method != null) {
            http.setRequestMethod(method);
        }
        c.setDoOutput(true);
        try {
            try (OutputStream out = c.getOutputStream();){
                IO.copy(in, out);
                URL uRL = url;
                return uRL;
            }
            {
                catch (Throwable throwable) {
                    throw throwable;
                }
            }
        }
        finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }

    public static File copy(File src, File tgt) throws IOException {
        IO.copy(src.toPath(), tgt.toPath());
        return tgt;
    }

    public static Path copy(Path src, Path tgt) throws IOException {
        final Path source = src.toAbsolutePath();
        final Path target = tgt.toAbsolutePath();
        if (Files.isRegularFile(source, new LinkOption[0])) {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            return tgt;
        }
        if (Files.isDirectory(source, new LinkOption[0])) {
            if (Files.notExists(target, new LinkOption[0])) {
                IO.mkdirs(target);
            }
            if (!Files.isDirectory(target, new LinkOption[0])) {
                throw new IllegalArgumentException("target directory for a directory must be a directory: " + target);
            }
            if (target.startsWith(source)) {
                throw new IllegalArgumentException("target directory can not be child of source directory.");
            }
            Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, (FileVisitor<? super Path>)new FileVisitor<Path>(){
                final FileTime now = FileTime.fromMillis(System.currentTimeMillis());

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    block2: {
                        Path targetdir = target.resolve(source.relativize(dir));
                        try {
                            Files.copy(dir, targetdir, new CopyOption[0]);
                        }
                        catch (FileAlreadyExistsException e) {
                            if (Files.isDirectory(targetdir, new LinkOption[0])) break block2;
                            throw e;
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path targetFile = target.resolve(source.relativize(file));
                    Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                    Files.setLastModifiedTime(targetFile, this.now);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    if (exc != null) {
                        throw exc;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (exc != null) {
                        throw exc;
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
            return tgt;
        }
        throw new FileNotFoundException("During copy: " + source.toString());
    }

    public static File copy(InputStream in, File file) throws IOException {
        IO.copy(in, file.toPath());
        return file;
    }

    public static Path copy(InputStream in, Path path) throws IOException {
        try (FileChannel out = IO.writeChannel(path);){
            IO.copy(in, (WritableByteChannel)out);
        }
        return path;
    }

    public static OutputStream copy(File file, OutputStream out) throws IOException {
        return IO.copy(file.toPath(), out);
    }

    public static OutputStream copy(Path path, OutputStream out) throws IOException {
        return IO.copy((ReadableByteChannel)IO.readChannel(path), out);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static WritableByteChannel copy(InputStream in, WritableByteChannel out) throws IOException {
        try {
            int size;
            ByteBuffer bb = ByteBuffer.allocate(65536);
            byte[] buffer = bb.array();
            while ((size = in.read(buffer, bb.position(), bb.remaining())) > 0) {
                bb.position(bb.position() + size);
                bb.flip();
                out.write(bb);
                bb.compact();
            }
            bb.flip();
            while (bb.hasRemaining()) {
                out.write(bb);
            }
            WritableByteChannel writableByteChannel = out;
            return writableByteChannel;
        }
        finally {
            in.close();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static OutputStream copy(ReadableByteChannel in, OutputStream out) throws IOException {
        try {
            ByteBuffer bb = ByteBuffer.allocate(65536);
            byte[] buffer = bb.array();
            while (in.read(bb) > 0) {
                out.write(buffer, 0, bb.position());
                bb.clear();
            }
            OutputStream outputStream = out;
            return outputStream;
        }
        finally {
            in.close();
        }
    }

    public static byte[] read(File file) throws IOException {
        try (FileChannel in = IO.readChannel(file.toPath());){
            ByteBuffer bb = ByteBuffer.allocate((int)in.size());
            while (in.read(bb) > 0) {
            }
            byte[] byArray = bb.array();
            return byArray;
        }
    }

    public static ByteBuffer read(Path path) throws IOException {
        try (FileChannel in = IO.readChannel(path);){
            long size = in.size();
            if (!isWindows && size > 65536L) {
                MappedByteBuffer mappedByteBuffer = in.map(FileChannel.MapMode.READ_ONLY, 0L, size);
                return mappedByteBuffer;
            }
            ByteBuffer bb = ByteBuffer.allocate((int)size);
            while (in.read(bb) > 0) {
            }
            bb.flip();
            ByteBuffer byteBuffer = bb;
            return byteBuffer;
        }
    }

    public static byte[] read(ByteBuffer bb) throws IOException {
        byte[] data = new byte[bb.remaining()];
        bb.get(data, 0, data.length);
        return data;
    }

    public static byte[] read(URL url) throws IOException {
        URLConnection conn = url.openConnection();
        conn.connect();
        int length = conn.getContentLength();
        if (length == -1) {
            return IO.read(conn.getInputStream());
        }
        return IO.copy(conn.getInputStream(), new byte[length]);
    }

    public static byte[] read(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
        IO.copy(in, (OutputStream)out);
        return out.toByteArray();
    }

    public static void write(byte[] data, OutputStream out) throws Exception {
        IO.copy(data, out);
    }

    public static void write(byte[] data, File file) throws Exception {
        IO.copy(data, file);
    }

    public static String collect(File file) throws IOException {
        return IO.collect(file.toPath(), StandardCharsets.UTF_8);
    }

    public static String collect(File file, String encoding) throws IOException {
        return IO.collect(file.toPath(), Charset.forName(encoding));
    }

    public static String collect(File file, Charset encoding) throws IOException {
        return IO.collect(file.toPath(), encoding);
    }

    public static String collect(Path path) throws IOException {
        return IO.collect(path, StandardCharsets.UTF_8);
    }

    public static String collect(Path path, Charset encoding) throws IOException {
        return IO.collect(IO.reader(path, encoding));
    }

    public static String collect(ByteBuffer bb, Charset encoding) throws IOException {
        return IO.decode(bb, encoding).toString();
    }

    public static String collect(URL url, String encoding) throws IOException {
        return IO.collect(IO.stream(url), Charset.forName(encoding));
    }

    public static String collect(URL url, Charset encoding) throws IOException {
        return IO.collect(IO.stream(url), encoding);
    }

    public static String collect(URL url) throws IOException {
        return IO.collect(url, StandardCharsets.UTF_8);
    }

    public static String collect(String path) throws IOException {
        return IO.collect(Paths.get(path, new String[0]), StandardCharsets.UTF_8);
    }

    public static String collect(InputStream in) throws IOException {
        return IO.collect(in, StandardCharsets.UTF_8);
    }

    public static String collect(InputStream in, String encoding) throws IOException {
        return IO.collect(in, Charset.forName(encoding));
    }

    public static String collect(InputStream in, Charset encoding) throws IOException {
        return IO.collect(IO.reader(in, encoding));
    }

    public static String collect(Reader r) throws IOException {
        StringWriter w = new StringWriter();
        IO.copy(r, (Writer)w);
        return w.toString();
    }

    public static File createTempFile(File directory, String pattern, String suffix) throws IllegalArgumentException, IOException {
        if (pattern == null || pattern.length() < 3) {
            throw new IllegalArgumentException("Pattern must be at least 3 characters long, got " + (pattern == null ? "null" : Integer.valueOf(pattern.length())));
        }
        if (directory != null && !directory.isDirectory()) {
            throw new FileNotFoundException("Directory " + directory + " is not a directory");
        }
        return File.createTempFile(pattern, suffix, directory);
    }

    public static File getFile(String filename) {
        return IO.getFile(work, filename);
    }

    public static File getFile(File base, String file) {
        int n;
        if (file.startsWith("~/") && !(file = file.substring(2)).startsWith("~/")) {
            return IO.getFile(home, file);
        }
        if (file.startsWith("~")) {
            file = file.substring(1);
            return IO.getFile(home.getParentFile(), file);
        }
        File f = new File(file);
        if (f.isAbsolute()) {
            return f;
        }
        if (base == null) {
            base = work;
        }
        f = base.getAbsoluteFile();
        while ((n = file.indexOf(47)) > 0) {
            String first = file.substring(0, n);
            file = file.substring(n + 1);
            if (first.equals("..")) {
                f = f.getParentFile();
                continue;
            }
            f = new File(f, first);
        }
        if (file.equals("..")) {
            return f.getParentFile();
        }
        return new File(f, file).getAbsoluteFile();
    }

    public static void delete(File file) {
        IO.delete(file.toPath());
    }

    public static void delete(Path path) {
        try {
            IO.deleteWithException(path);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public static void initialize(File dir) {
        try {
            IO.deleteWithException(dir);
            IO.mkdirs(dir);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteWithException(File file) throws IOException {
        IO.deleteWithException(file.toPath());
    }

    public static void deleteWithException(Path path) throws IOException {
        if (Files.notExists(path = path.toAbsolutePath(), new LinkOption[0]) && !IO.isSymbolicLink(path)) {
            return;
        }
        if (path.equals(path.getRoot())) {
            throw new IllegalArgumentException("Cannot recursively delete root for safety reasons");
        }
        Files.walkFileTree(path, (FileVisitor<? super Path>)new FileVisitor<Path>(){

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                try {
                    Files.delete(file);
                }
                catch (IOException e) {
                    throw exc;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc != null) {
                    throw exc;
                }
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static void rename(File from, File to) throws IOException {
        IO.rename(from.toPath(), to.toPath());
    }

    public static void rename(Path from, Path to) throws IOException {
        Files.move(from, to, StandardCopyOption.REPLACE_EXISTING);
    }

    public static void mkdirs(File dir) throws IOException {
        IO.mkdirs(dir.toPath());
    }

    public static void mkdirs(Path dir) throws IOException {
        Files.createDirectories(dir, new FileAttribute[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static long drain(InputStream in) throws IOException {
        try {
            int size;
            long result = 0L;
            byte[] buffer = new byte[65536];
            while ((size = in.read(buffer, 0, buffer.length)) > 0) {
                result += (long)size;
            }
            long l = result;
            return l;
        }
        finally {
            in.close();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static OutputStream copy(Collection<?> c, OutputStream out) throws IOException {
        PrintWriter pw = IO.writer(out);
        try {
            for (Object o : c) {
                pw.println(o);
            }
            OutputStream outputStream = out;
            return outputStream;
        }
        finally {
            pw.flush();
        }
    }

    public static Throwable close(Closeable in) {
        try {
            if (in != null) {
                in.close();
            }
        }
        catch (Throwable e) {
            return e;
        }
        return null;
    }

    public static URL toURL(String s, File base) throws MalformedURLException {
        int n = s.indexOf(58);
        if (n > 0 && n < 10) {
            return new URL(s);
        }
        return IO.getFile(base, s).toURI().toURL();
    }

    public static void store(Object o, File file) throws IOException {
        IO.store(o, file.toPath(), StandardCharsets.UTF_8);
    }

    public static void store(Object o, File file, String encoding) throws IOException {
        IO.store(o, file.toPath(), Charset.forName(encoding));
    }

    public static void store(Object o, Path path, Charset encoding) throws IOException {
        block24: {
            try (FileChannel ch = IO.writeChannel(path);){
                if (o == null) break block24;
                try (Writer w = Channels.newWriter(ch, encoding.newEncoder(), -1);){
                    w.write(o.toString());
                }
            }
        }
    }

    public static void store(Object o, OutputStream out) throws IOException {
        IO.store(o, out, StandardCharsets.UTF_8);
    }

    public static void store(Object o, OutputStream out, String encoding) throws IOException {
        IO.store(o, out, Charset.forName(encoding));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void store(Object o, OutputStream out, Charset encoding) throws IOException {
        PrintWriter w = IO.writer(out, encoding);
        try {
            IO.store(o, w);
        }
        finally {
            ((Writer)w).flush();
        }
    }

    public static void store(Object o, Writer w) throws IOException {
        if (o != null) {
            w.write(o.toString());
        }
    }

    public static InputStream stream(byte[] data) {
        return new ByteArrayInputStream(data);
    }

    public static InputStream stream(ByteBuffer bb) {
        return new ByteBufferInputStream(bb);
    }

    public static InputStream stream(String s) {
        return IO.stream(s, StandardCharsets.UTF_8);
    }

    public static InputStream stream(String s, String encoding) throws IOException {
        return IO.stream(s, Charset.forName(encoding));
    }

    public static InputStream stream(String s, Charset encoding) {
        return IO.stream(s.getBytes(encoding));
    }

    public static InputStream stream(File file) throws IOException {
        return IO.stream(file.toPath());
    }

    public static InputStream stream(Path path) throws IOException {
        return Files.newInputStream(path, new OpenOption[0]);
    }

    public static InputStream stream(URL url) throws IOException {
        return url.openStream();
    }

    public static FileChannel readChannel(Path path) throws IOException {
        return FileChannel.open(path, readOptions, new FileAttribute[0]);
    }

    public static OutputStream outputStream(File file) throws IOException {
        return IO.outputStream(file.toPath());
    }

    public static OutputStream outputStream(Path path) throws IOException {
        return Files.newOutputStream(path, new OpenOption[0]);
    }

    public static FileChannel writeChannel(Path path) throws IOException {
        return FileChannel.open(path, writeOptions, new FileAttribute[0]);
    }

    public static CharBuffer decode(ByteBuffer bb, Charset encoding) throws IOException {
        return encoding.decode(bb);
    }

    public static ByteBuffer encode(CharBuffer cb, Charset encoding) throws IOException {
        return encoding.encode(cb);
    }

    public static BufferedReader reader(String s) {
        return new BufferedReader(new StringReader(s));
    }

    public static BufferedReader reader(File file) throws IOException {
        return IO.reader(file.toPath(), StandardCharsets.UTF_8);
    }

    public static BufferedReader reader(File file, String encoding) throws IOException {
        return IO.reader(file.toPath(), Charset.forName(encoding));
    }

    public static BufferedReader reader(File file, Charset encoding) throws IOException {
        return IO.reader(file.toPath(), encoding);
    }

    public static BufferedReader reader(Path path, Charset encoding) throws IOException {
        return IO.reader(IO.readChannel(path), encoding);
    }

    public static BufferedReader reader(ByteBuffer bb, Charset encoding) throws IOException {
        return IO.reader((InputStream)new ByteBufferInputStream(bb), encoding);
    }

    public static BufferedReader reader(CharBuffer cb) throws IOException {
        return new BufferedReader(new CharBufferReader(cb));
    }

    public static BufferedReader reader(ReadableByteChannel in, Charset encoding) throws IOException {
        return new BufferedReader(Channels.newReader(in, encoding.newDecoder(), -1));
    }

    public static BufferedReader reader(InputStream in) throws IOException {
        return IO.reader(in, StandardCharsets.UTF_8);
    }

    public static BufferedReader reader(InputStream in, String encoding) throws IOException {
        return IO.reader(in, Charset.forName(encoding));
    }

    public static BufferedReader reader(InputStream in, Charset encoding) throws IOException {
        return new BufferedReader(new InputStreamReader(in, encoding));
    }

    public static PrintWriter writer(File file) throws IOException {
        return IO.writer(file.toPath(), StandardCharsets.UTF_8);
    }

    public static PrintWriter writer(File file, String encoding) throws IOException {
        return IO.writer(file.toPath(), Charset.forName(encoding));
    }

    public static PrintWriter writer(File file, Charset encoding) throws IOException {
        return IO.writer(file.toPath(), encoding);
    }

    public static PrintWriter writer(Path path, Charset encoding) throws IOException {
        return IO.writer(IO.writeChannel(path), encoding);
    }

    public static PrintWriter writer(WritableByteChannel out, Charset encoding) throws IOException {
        return new PrintWriter(Channels.newWriter(out, encoding.newEncoder(), -1));
    }

    public static PrintWriter writer(OutputStream out) throws IOException {
        return IO.writer(out, StandardCharsets.UTF_8);
    }

    public static PrintWriter writer(OutputStream out, String encoding) throws IOException {
        return IO.writer(out, Charset.forName(encoding));
    }

    public static PrintWriter writer(OutputStream out, Charset encoding) throws IOException {
        return new PrintWriter(new OutputStreamWriter(out, encoding));
    }

    public static boolean createSymbolicLink(File link, File target) throws Exception {
        return IO.createSymbolicLink(link.toPath(), target.toPath());
    }

    public static boolean createSymbolicLink(Path link, Path target) throws Exception {
        if (IO.isSymbolicLink(link)) {
            Path linkTarget = Files.readSymbolicLink(link);
            if (target.equals(linkTarget)) {
                return true;
            }
            Files.delete(link);
        }
        try {
            Files.createSymbolicLink(link, target, new FileAttribute[0]);
            return true;
        }
        catch (Exception exception) {
            return false;
        }
    }

    public static boolean isSymbolicLink(File link) {
        return IO.isSymbolicLink(link.toPath());
    }

    public static boolean isSymbolicLink(Path link) {
        return Files.isSymbolicLink(link);
    }

    public static boolean createSymbolicLinkOrCopy(File link, File target) {
        return IO.createSymbolicLinkOrCopy(link.toPath(), target.toPath());
    }

    public static boolean createSymbolicLinkOrCopy(Path link, Path target) {
        try {
            if (isWindows || !IO.createSymbolicLink(link, target)) {
                BasicFileAttributes targetAttrs = Files.readAttributes(target, BasicFileAttributes.class, new LinkOption[0]);
                try {
                    BasicFileAttributes linkAttrs = Files.readAttributes(link, BasicFileAttributes.class, new LinkOption[0]);
                    if (targetAttrs.lastModifiedTime().equals(linkAttrs.lastModifiedTime()) && targetAttrs.size() == linkAttrs.size()) {
                        return true;
                    }
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                IO.copy(target, link);
                Files.setLastModifiedTime(link, targetAttrs.lastModifiedTime());
            }
            return true;
        }
        catch (Exception exception) {
            return false;
        }
    }

    public static String toSafeFileName(String string) {
        StringBuilder sb = new StringBuilder();
        block6: for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            if (c < ' ') continue;
            if (isWindows) {
                switch (c) {
                    case '\"': 
                    case '*': 
                    case '/': 
                    case ':': 
                    case '<': 
                    case '>': 
                    case '\\': 
                    case '|': {
                        sb.append('%');
                        break;
                    }
                    default: {
                        sb.append(c);
                        break;
                    }
                }
                continue;
            }
            switch (c) {
                case '/': {
                    sb.append('%');
                    continue block6;
                }
                default: {
                    sb.append(c);
                }
            }
        }
        if (sb.length() == 0 || isWindows && RESERVED_WINDOWS_P.matcher(sb).matches()) {
            sb.append("_");
        }
        return sb.toString();
    }

    public static boolean isWindows() {
        return isWindows;
    }

    static {
        writeOptions = EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        readOptions = EnumSet.of(StandardOpenOption.READ);
        File tmp = null;
        try {
            tmp = new File(System.getenv("HOME"));
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (tmp == null) {
            tmp = new File(System.getProperty("user.home"));
        }
        home = tmp;
        nullStream = new OutputStream(){

            @Override
            public void write(int var0) throws IOException {
            }

            @Override
            public void write(byte[] var0) throws IOException {
            }

            @Override
            public void write(byte[] var0, int from, int l) throws IOException {
            }
        };
        nullWriter = new Writer(){

            @Override
            public Writer append(char var0) throws IOException {
                return null;
            }

            @Override
            public Writer append(CharSequence var0) throws IOException {
                return null;
            }

            @Override
            public Writer append(CharSequence var0, int var1, int var2) throws IOException {
                return null;
            }

            @Override
            public void write(int var0) throws IOException {
            }

            @Override
            public void write(String var0) throws IOException {
            }

            @Override
            public void write(String var0, int var1, int var2) throws IOException {
            }

            @Override
            public void write(char[] var0) throws IOException {
            }

            @Override
            public void write(char[] var0, int var1, int var2) throws IOException {
            }

            @Override
            public void close() throws IOException {
            }

            @Override
            public void flush() throws IOException {
            }
        };
        RESERVED_WINDOWS_P = Pattern.compile("CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9]");
    }
}

