/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.io.FileType;
import groovy.io.FileVisitResult;
import groovy.io.GroovyPrintWriter;
import groovy.lang.Closure;
import groovy.lang.MetaClass;
import groovy.lang.Writable;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.FromString;
import groovy.transform.stc.SimpleType;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.DefaultGroovyMethodsSupport;
import org.codehaus.groovy.runtime.IOGroovyMethods;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.WritablePath;
import org.codehaus.groovy.runtime.callsite.BooleanReturningMethodInvoker;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class NioGroovyMethods
extends DefaultGroovyMethodsSupport {
    public static long size(Path self) throws IOException {
        return Files.size(self);
    }

    public static ObjectOutputStream newObjectOutputStream(Path self) throws IOException {
        return new ObjectOutputStream(Files.newOutputStream(self, new OpenOption[0]));
    }

    public static <T> T withObjectOutputStream(Path self, @ClosureParams(value=SimpleType.class, options={"java.io.ObjectOutputStream"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withStream(NioGroovyMethods.newObjectOutputStream(self), closure);
    }

    public static ObjectInputStream newObjectInputStream(Path self) throws IOException {
        return new ObjectInputStream(Files.newInputStream(self, new OpenOption[0]));
    }

    public static ObjectInputStream newObjectInputStream(Path self, ClassLoader classLoader) throws IOException {
        return IOGroovyMethods.newObjectInputStream(Files.newInputStream(self, new OpenOption[0]), classLoader);
    }

    public static void eachObject(Path self, Closure closure) throws IOException, ClassNotFoundException {
        IOGroovyMethods.eachObject(NioGroovyMethods.newObjectInputStream(self), closure);
    }

    public static <T> T withObjectInputStream(Path path, @ClosureParams(value=SimpleType.class, options={"java.io.ObjectInputStream"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withStream(NioGroovyMethods.newObjectInputStream(path), closure);
    }

    public static <T> T withObjectInputStream(Path self, ClassLoader classLoader, @ClosureParams(value=SimpleType.class, options={"java.io.ObjectInputStream"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withStream(NioGroovyMethods.newObjectInputStream(self, classLoader), closure);
    }

    public static <T> T eachLine(Path self, @ClosureParams(value=FromString.class, options={"String", "String,Integer"}) Closure<T> closure) throws IOException {
        return NioGroovyMethods.eachLine(self, 1, closure);
    }

    public static <T> T eachLine(Path self, String charset, @ClosureParams(value=FromString.class, options={"String", "String,Integer"}) Closure<T> closure) throws IOException {
        return NioGroovyMethods.eachLine(self, charset, 1, closure);
    }

    public static <T> T eachLine(Path self, int firstLine, @ClosureParams(value=FromString.class, options={"String", "String,Integer"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.eachLine(NioGroovyMethods.newReader(self), firstLine, closure);
    }

    public static <T> T eachLine(Path self, String charset, int firstLine, @ClosureParams(value=FromString.class, options={"String", "String,Integer"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.eachLine(NioGroovyMethods.newReader(self, charset), firstLine, closure);
    }

    public static <T> T splitEachLine(Path self, String regex, @ClosureParams(value=FromString.class, options={"List<String>"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.splitEachLine((Reader)NioGroovyMethods.newReader(self), regex, closure);
    }

    public static <T> T splitEachLine(Path self, Pattern pattern, @ClosureParams(value=FromString.class, options={"List<String>"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.splitEachLine((Reader)NioGroovyMethods.newReader(self), pattern, closure);
    }

    public static <T> T splitEachLine(Path self, String regex, String charset, @ClosureParams(value=FromString.class, options={"List<String>"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.splitEachLine((Reader)NioGroovyMethods.newReader(self, charset), regex, closure);
    }

    public static <T> T splitEachLine(Path self, Pattern pattern, String charset, @ClosureParams(value=FromString.class, options={"List<String>"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.splitEachLine((Reader)NioGroovyMethods.newReader(self, charset), pattern, closure);
    }

    public static List<String> readLines(Path self) throws IOException {
        return IOGroovyMethods.readLines(NioGroovyMethods.newReader(self));
    }

    public static List<String> readLines(Path self, String charset) throws IOException {
        return IOGroovyMethods.readLines(NioGroovyMethods.newReader(self, charset));
    }

    public static String getText(Path self, String charset) throws IOException {
        return IOGroovyMethods.getText(NioGroovyMethods.newReader(self, charset));
    }

    public static String getText(Path self) throws IOException {
        return IOGroovyMethods.getText(NioGroovyMethods.newReader(self));
    }

    public static byte[] getBytes(Path self) throws IOException {
        return IOGroovyMethods.getBytes(Files.newInputStream(self, new OpenOption[0]));
    }

    public static void setBytes(Path self, byte[] bytes) throws IOException {
        IOGroovyMethods.setBytes(Files.newOutputStream(self, new OpenOption[0]), bytes);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void write(Path self, String text) throws IOException {
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(Files.newOutputStream(self, new OpenOption[0]), Charset.defaultCharset());
            writer.write(text);
            ((Writer)writer).flush();
            OutputStreamWriter temp = writer;
            writer = null;
            ((Writer)temp).close();
        }
        catch (Throwable throwable) {
            NioGroovyMethods.closeWithWarning(writer);
            throw throwable;
        }
        NioGroovyMethods.closeWithWarning(writer);
    }

    public static void setText(Path self, String text) throws IOException {
        NioGroovyMethods.write(self, text);
    }

    public static void setText(Path self, String text, String charset) throws IOException {
        NioGroovyMethods.write(self, text, charset);
    }

    public static Path leftShift(Path self, Object text) throws IOException {
        NioGroovyMethods.append(self, text);
        return self;
    }

    public static Path leftShift(Path self, byte[] bytes) throws IOException {
        NioGroovyMethods.append(self, bytes);
        return self;
    }

    public static Path leftShift(Path path, InputStream data) throws IOException {
        NioGroovyMethods.append(path, data);
        return path;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void write(Path self, String text, String charset) throws IOException {
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(Files.newOutputStream(self, new OpenOption[0]), Charset.forName(charset));
            writer.write(text);
            ((Writer)writer).flush();
            OutputStreamWriter temp = writer;
            writer = null;
            ((Writer)temp).close();
        }
        catch (Throwable throwable) {
            NioGroovyMethods.closeWithWarning(writer);
            throw throwable;
        }
        NioGroovyMethods.closeWithWarning(writer);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void append(Path self, Object text) throws IOException {
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(Files.newOutputStream(self, StandardOpenOption.CREATE, StandardOpenOption.APPEND), Charset.defaultCharset());
            InvokerHelper.write(writer, text);
            ((Writer)writer).flush();
            OutputStreamWriter temp = writer;
            writer = null;
            ((Writer)temp).close();
        }
        catch (Throwable throwable) {
            NioGroovyMethods.closeWithWarning(writer);
            throw throwable;
        }
        NioGroovyMethods.closeWithWarning(writer);
    }

    public static void append(Path file, Reader reader) throws IOException {
        NioGroovyMethods.appendBuffered(file, reader);
    }

    public static void append(Path file, Writer writer) throws IOException {
        NioGroovyMethods.appendBuffered(file, writer);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void appendBuffered(Path file, Object text) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = NioGroovyMethods.newWriter(file, true);
            InvokerHelper.write(writer, text);
            writer.flush();
            BufferedWriter temp = writer;
            writer = null;
            ((Writer)temp).close();
        }
        finally {
            NioGroovyMethods.closeWithWarning(writer);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void append(Path self, byte[] bytes) throws IOException {
        OutputStream stream = null;
        try {
            stream = Files.newOutputStream(self, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            stream.write(bytes, 0, bytes.length);
            stream.flush();
            OutputStream temp = stream;
            stream = null;
            temp.close();
        }
        catch (Throwable throwable) {
            NioGroovyMethods.closeWithWarning(stream);
            throw throwable;
        }
        NioGroovyMethods.closeWithWarning(stream);
    }

    public static void append(Path self, InputStream stream) throws IOException {
        OutputStream out = Files.newOutputStream(self, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        try {
            IOGroovyMethods.leftShift(out, stream);
        }
        finally {
            NioGroovyMethods.closeWithWarning(out);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void append(Path self, Object text, String charset) throws IOException {
        OutputStreamWriter writer = null;
        try {
            OutputStream out = Files.newOutputStream(self, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            writer = new OutputStreamWriter(out, Charset.forName(charset));
            InvokerHelper.write(writer, text);
            ((Writer)writer).flush();
            OutputStreamWriter temp = writer;
            writer = null;
            ((Writer)temp).close();
        }
        catch (Throwable throwable) {
            NioGroovyMethods.closeWithWarning(writer);
            throw throwable;
        }
        NioGroovyMethods.closeWithWarning(writer);
    }

    public static void append(Path file, Writer writer, String charset) throws IOException {
        NioGroovyMethods.appendBuffered(file, writer, charset);
    }

    public static void append(Path file, Reader reader, String charset) throws IOException {
        NioGroovyMethods.appendBuffered(file, reader, charset);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void appendBuffered(Path file, Object text, String charset) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = NioGroovyMethods.newWriter(file, charset, true);
            InvokerHelper.write(writer, text);
            writer.flush();
            BufferedWriter temp = writer;
            writer = null;
            ((Writer)temp).close();
        }
        catch (Throwable throwable) {
            NioGroovyMethods.closeWithWarning(writer);
            throw throwable;
        }
        NioGroovyMethods.closeWithWarning(writer);
    }

    private static void checkDir(Path self) throws FileNotFoundException, IllegalArgumentException {
        if (!Files.exists(self, new LinkOption[0])) {
            throw new FileNotFoundException(self.toAbsolutePath().toString());
        }
        if (!Files.isDirectory(self, new LinkOption[0])) {
            throw new IllegalArgumentException("The provided Path object is not a directory: " + self.toAbsolutePath());
        }
    }

    public static void eachFile(Path self, FileType fileType, @ClosureParams(value=SimpleType.class, options={"java.nio.file.Path"}) Closure closure) throws IOException {
        NioGroovyMethods.checkDir(self);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(self);){
            for (Path path : stream) {
                if (fileType != FileType.ANY && (fileType == FileType.FILES || !Files.isDirectory(path, new LinkOption[0])) && (fileType == FileType.DIRECTORIES || !Files.isRegularFile(path, new LinkOption[0]))) continue;
                closure.call((Object)path);
            }
        }
    }

    public static void eachFile(Path self, Closure closure) throws IOException {
        NioGroovyMethods.eachFile(self, FileType.ANY, closure);
    }

    public static void eachDir(Path self, @ClosureParams(value=SimpleType.class, options={"java.nio.file.Path"}) Closure closure) throws IOException {
        NioGroovyMethods.eachFile(self, FileType.DIRECTORIES, closure);
    }

    public static void eachFileRecurse(Path self, FileType fileType, @ClosureParams(value=SimpleType.class, options={"java.nio.file.Path"}) Closure closure) throws IOException {
        NioGroovyMethods.checkDir(self);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(self);){
            for (Path path : stream) {
                if (Files.isDirectory(path, new LinkOption[0])) {
                    if (fileType != FileType.FILES) {
                        closure.call((Object)path);
                    }
                    NioGroovyMethods.eachFileRecurse(path, fileType, closure);
                    continue;
                }
                if (fileType == FileType.DIRECTORIES) continue;
                closure.call((Object)path);
            }
        }
    }

    public static void traverse(Path self, Map<String, Object> options, @ClosureParams(value=SimpleType.class, options={"java.nio.file.Path"}) Closure closure) throws IOException {
        Object closureResult;
        Number maxDepthNumber = DefaultGroovyMethods.asType(options.remove("maxDepth"), Number.class);
        int maxDepth = maxDepthNumber == null ? -1 : maxDepthNumber.intValue();
        Boolean visitRoot = DefaultGroovyMethods.asType(DefaultGroovyMethods.get(options, "visitRoot", false), Boolean.class);
        Boolean preRoot = DefaultGroovyMethods.asType(DefaultGroovyMethods.get(options, "preRoot", false), Boolean.class);
        Boolean postRoot = DefaultGroovyMethods.asType(DefaultGroovyMethods.get(options, "postRoot", false), Boolean.class);
        Closure pre = (Closure)options.get("preDir");
        Closure post = (Closure)options.get("postDir");
        FileType type = (FileType)((Object)options.get("type"));
        Object filter = options.get("filter");
        Object nameFilter = options.get("nameFilter");
        Object excludeFilter = options.get("excludeFilter");
        Object excludeNameFilter = options.get("excludeNameFilter");
        Object preResult = null;
        if (preRoot.booleanValue() && pre != null) {
            preResult = pre.call((Object)self);
        }
        if (preResult == FileVisitResult.TERMINATE || preResult == FileVisitResult.SKIP_SUBTREE) {
            return;
        }
        FileVisitResult terminated = NioGroovyMethods.traverse(self, options, closure, maxDepth);
        if (type != FileType.FILES && visitRoot.booleanValue() && closure != null && NioGroovyMethods.notFiltered(self, filter, nameFilter, excludeFilter, excludeNameFilter) && (closureResult = closure.call((Object)self)) == FileVisitResult.TERMINATE) {
            return;
        }
        if (postRoot.booleanValue() && post != null && terminated != FileVisitResult.TERMINATE) {
            post.call((Object)self);
        }
    }

    private static boolean notFiltered(Path path, Object filter, Object nameFilter, Object excludeFilter, Object excludeNameFilter) {
        if (filter == null && nameFilter == null && excludeFilter == null && excludeNameFilter == null) {
            return true;
        }
        if (filter != null && nameFilter != null) {
            throw new IllegalArgumentException("Can't set both 'filter' and 'nameFilter'");
        }
        if (excludeFilter != null && excludeNameFilter != null) {
            throw new IllegalArgumentException("Can't set both 'excludeFilter' and 'excludeNameFilter'");
        }
        Object filterToUse = null;
        Object filterParam = null;
        if (filter != null) {
            filterToUse = filter;
            filterParam = path;
        } else if (nameFilter != null) {
            filterToUse = nameFilter;
            filterParam = path.getFileName().toString();
        }
        Object excludeFilterToUse = null;
        Object excludeParam = null;
        if (excludeFilter != null) {
            excludeFilterToUse = excludeFilter;
            excludeParam = path;
        } else if (excludeNameFilter != null) {
            excludeFilterToUse = excludeNameFilter;
            excludeParam = path.getFileName().toString();
        }
        MetaClass filterMC = filterToUse == null ? null : InvokerHelper.getMetaClass(filterToUse);
        MetaClass excludeMC = excludeFilterToUse == null ? null : InvokerHelper.getMetaClass(excludeFilterToUse);
        boolean included = filterToUse == null || DefaultTypeTransformation.castToBoolean(filterMC.invokeMethod(filterToUse, "isCase", filterParam));
        boolean excluded = excludeFilterToUse != null && DefaultTypeTransformation.castToBoolean(excludeMC.invokeMethod(excludeFilterToUse, "isCase", excludeParam));
        return included && !excluded;
    }

    public static void traverse(Path self, @ClosureParams(value=SimpleType.class, options={"java.nio.file.Path"}) Closure closure) throws IOException {
        NioGroovyMethods.traverse(self, new HashMap<String, Object>(), closure);
    }

    public static void traverse(Path self, Map<String, Object> options) throws IOException {
        Closure visit = (Closure)options.remove("visit");
        NioGroovyMethods.traverse(self, options, visit);
    }

    private static FileVisitResult traverse(Path self, Map<String, Object> options, @ClosureParams(value=SimpleType.class, options={"java.nio.file.Path"}) Closure closure, int maxDepth) throws IOException {
        NioGroovyMethods.checkDir(self);
        Closure pre = (Closure)options.get("preDir");
        Closure post = (Closure)options.get("postDir");
        FileType type = (FileType)((Object)options.get("type"));
        Object filter = options.get("filter");
        Object nameFilter = options.get("nameFilter");
        Object excludeFilter = options.get("excludeFilter");
        Object excludeNameFilter = options.get("excludeNameFilter");
        Closure sort = (Closure)options.get("sort");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(self);){
            Iterator<Path> itr = stream.iterator();
            List<Path> files = new LinkedList<Path>();
            while (itr.hasNext()) {
                files.add(itr.next());
            }
            if (sort != null) {
                files = DefaultGroovyMethods.sort(files, sort);
            }
            for (Path path : files) {
                FileVisitResult fileVisitResult;
                Object closureResult;
                if (Files.isDirectory(path, new LinkOption[0])) {
                    FileVisitResult terminated;
                    if (type != FileType.FILES && closure != null && NioGroovyMethods.notFiltered(path, filter, nameFilter, excludeFilter, excludeNameFilter)) {
                        closureResult = closure.call((Object)path);
                        if (closureResult == FileVisitResult.SKIP_SIBLINGS) break;
                        if (closureResult == FileVisitResult.TERMINATE) {
                            fileVisitResult = FileVisitResult.TERMINATE;
                            return fileVisitResult;
                        }
                    }
                    if (maxDepth == 0) continue;
                    Object preResult = null;
                    if (pre != null) {
                        preResult = pre.call((Object)path);
                    }
                    if (preResult == FileVisitResult.SKIP_SIBLINGS) break;
                    if (preResult == FileVisitResult.TERMINATE) {
                        fileVisitResult = FileVisitResult.TERMINATE;
                        return fileVisitResult;
                    }
                    if (preResult != FileVisitResult.SKIP_SUBTREE && (terminated = NioGroovyMethods.traverse(path, options, closure, maxDepth - 1)) == FileVisitResult.TERMINATE) {
                        FileVisitResult fileVisitResult2 = terminated;
                        return fileVisitResult2;
                    }
                    Object postResult = null;
                    if (post != null) {
                        postResult = post.call((Object)path);
                    }
                    if (postResult == FileVisitResult.SKIP_SIBLINGS) break;
                    if (postResult != FileVisitResult.TERMINATE) continue;
                    FileVisitResult fileVisitResult3 = FileVisitResult.TERMINATE;
                    return fileVisitResult3;
                }
                if (type == FileType.DIRECTORIES || closure == null || !NioGroovyMethods.notFiltered(path, filter, nameFilter, excludeFilter, excludeNameFilter)) continue;
                closureResult = closure.call((Object)path);
                if (closureResult == FileVisitResult.SKIP_SIBLINGS) break;
                if (closureResult != FileVisitResult.TERMINATE) continue;
                fileVisitResult = FileVisitResult.TERMINATE;
                return fileVisitResult;
            }
            Object object = FileVisitResult.CONTINUE;
            return object;
        }
    }

    public static void eachFileRecurse(Path self, @ClosureParams(value=SimpleType.class, options={"java.nio.file.Path"}) Closure closure) throws IOException {
        NioGroovyMethods.eachFileRecurse(self, FileType.ANY, closure);
    }

    public static void eachDirRecurse(Path self, @ClosureParams(value=SimpleType.class, options={"java.nio.file.Path"}) Closure closure) throws IOException {
        NioGroovyMethods.eachFileRecurse(self, FileType.DIRECTORIES, closure);
    }

    public static void eachFileMatch(Path self, FileType fileType, Object nameFilter, @ClosureParams(value=SimpleType.class, options={"java.nio.file.Path"}) Closure closure) throws IOException {
        NioGroovyMethods.checkDir(self);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(self);){
            Iterator<Path> itr = stream.iterator();
            BooleanReturningMethodInvoker bmi = new BooleanReturningMethodInvoker("isCase");
            while (itr.hasNext()) {
                Path currentPath = itr.next();
                if ((fileType == FileType.FILES || !Files.isDirectory(currentPath, new LinkOption[0])) && (fileType == FileType.DIRECTORIES || !Files.isRegularFile(currentPath, new LinkOption[0])) || !bmi.invoke(nameFilter, currentPath.getFileName().toString())) continue;
                closure.call((Object)currentPath);
            }
        }
    }

    public static void eachFileMatch(Path self, Object nameFilter, @ClosureParams(value=SimpleType.class, options={"java.nio.file.Path"}) Closure closure) throws IOException {
        NioGroovyMethods.eachFileMatch(self, FileType.ANY, nameFilter, closure);
    }

    public static void eachDirMatch(Path self, Object nameFilter, @ClosureParams(value=SimpleType.class, options={"java.nio.file.Path"}) Closure closure) throws IOException {
        NioGroovyMethods.eachFileMatch(self, FileType.DIRECTORIES, nameFilter, closure);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static boolean deleteDir(Path self) {
        if (!Files.exists(self, new LinkOption[0])) {
            return true;
        }
        if (!Files.isDirectory(self, new LinkOption[0])) {
            return false;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(self);){
            for (Path path : stream) {
                if (Files.isDirectory(path, new LinkOption[0])) {
                    if (NioGroovyMethods.deleteDir(path)) continue;
                    boolean bl = false;
                    return bl;
                }
                Files.delete(path);
            }
            Files.delete(self);
            boolean bl = true;
            return bl;
        }
        catch (IOException e) {
            return false;
        }
    }

    public static boolean renameTo(Path self, String newPathName) {
        try {
            Files.move(self, Paths.get(newPathName, new String[0]), new CopyOption[0]);
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }

    public static boolean renameTo(Path self, URI newPathName) {
        try {
            Files.move(self, Paths.get(newPathName), new CopyOption[0]);
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }

    public static Path asWritable(Path self) {
        return new WritablePath(self);
    }

    public static <T> T asType(Path path, Class<T> c) {
        if (c == Writable.class) {
            return (T)NioGroovyMethods.asWritable(path);
        }
        return DefaultGroovyMethods.asType((Object)path, c);
    }

    public static Path asWritable(Path self, String encoding) {
        return new WritablePath(self, encoding);
    }

    public static BufferedReader newReader(Path self) throws IOException {
        return Files.newBufferedReader(self, Charset.defaultCharset());
    }

    public static BufferedReader newReader(Path self, String charset) throws IOException {
        return Files.newBufferedReader(self, Charset.forName(charset));
    }

    public static <T> T withReader(Path self, @ClosureParams(value=SimpleType.class, options={"java.io.Reader"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withReader(NioGroovyMethods.newReader(self), closure);
    }

    public static <T> T withReader(Path self, String charset, @ClosureParams(value=SimpleType.class, options={"java.io.Reader"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withReader(NioGroovyMethods.newReader(self, charset), closure);
    }

    public static BufferedOutputStream newOutputStream(Path self) throws IOException {
        return new BufferedOutputStream(Files.newOutputStream(self, new OpenOption[0]));
    }

    public static DataOutputStream newDataOutputStream(Path self) throws IOException {
        return new DataOutputStream(Files.newOutputStream(self, new OpenOption[0]));
    }

    public static Object withOutputStream(Path self, @ClosureParams(value=SimpleType.class, options={"java.io.OutputStream"}) Closure closure) throws IOException {
        return IOGroovyMethods.withStream(NioGroovyMethods.newOutputStream(self), closure);
    }

    public static Object withInputStream(Path self, @ClosureParams(value=SimpleType.class, options={"java.io.InputStream"}) Closure closure) throws IOException {
        return IOGroovyMethods.withStream(NioGroovyMethods.newInputStream(self), closure);
    }

    public static <T> T withDataOutputStream(Path self, @ClosureParams(value=SimpleType.class, options={"java.io.DataOutputStream"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withStream(NioGroovyMethods.newDataOutputStream(self), closure);
    }

    public static <T> T withDataInputStream(Path self, @ClosureParams(value=SimpleType.class, options={"java.io.DataInputStream"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withStream(NioGroovyMethods.newDataInputStream(self), closure);
    }

    public static BufferedWriter newWriter(Path self) throws IOException {
        return Files.newBufferedWriter(self, Charset.defaultCharset(), new OpenOption[0]);
    }

    public static BufferedWriter newWriter(Path self, boolean append) throws IOException {
        if (append) {
            return Files.newBufferedWriter(self, Charset.defaultCharset(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
        return Files.newBufferedWriter(self, Charset.defaultCharset(), new OpenOption[0]);
    }

    public static BufferedWriter newWriter(Path self, String charset, boolean append) throws IOException {
        if (append) {
            return Files.newBufferedWriter(self, Charset.forName(charset), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
        return Files.newBufferedWriter(self, Charset.forName(charset), new OpenOption[0]);
    }

    public static BufferedWriter newWriter(Path self, String charset) throws IOException {
        return NioGroovyMethods.newWriter(self, charset, false);
    }

    public static <T> T withWriter(Path self, @ClosureParams(value=SimpleType.class, options={"java.io.Writer"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withWriter(NioGroovyMethods.newWriter(self), closure);
    }

    public static <T> T withWriter(Path self, String charset, @ClosureParams(value=SimpleType.class, options={"java.io.Writer"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withWriter(NioGroovyMethods.newWriter(self, charset), closure);
    }

    public static <T> T withWriterAppend(Path self, String charset, @ClosureParams(value=SimpleType.class, options={"java.io.Writer"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withWriter(NioGroovyMethods.newWriter(self, charset, true), closure);
    }

    public static <T> T withWriterAppend(Path self, @ClosureParams(value=SimpleType.class, options={"java.io.Writer"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withWriter(NioGroovyMethods.newWriter(self, true), closure);
    }

    public static PrintWriter newPrintWriter(Path self) throws IOException {
        return new GroovyPrintWriter(NioGroovyMethods.newWriter(self));
    }

    public static PrintWriter newPrintWriter(Path self, String charset) throws IOException {
        return new GroovyPrintWriter(NioGroovyMethods.newWriter(self, charset));
    }

    public static <T> T withPrintWriter(Path self, @ClosureParams(value=SimpleType.class, options={"java.io.PrintWriter"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withWriter(NioGroovyMethods.newPrintWriter(self), closure);
    }

    public static <T> T withPrintWriter(Path self, String charset, @ClosureParams(value=SimpleType.class, options={"java.io.PrintWriter"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withWriter(NioGroovyMethods.newPrintWriter(self, charset), closure);
    }

    public static BufferedInputStream newInputStream(Path self) throws IOException {
        return new BufferedInputStream(Files.newInputStream(self, new OpenOption[0]));
    }

    public static DataInputStream newDataInputStream(Path self) throws IOException {
        return new DataInputStream(Files.newInputStream(self, new OpenOption[0]));
    }

    public static void eachByte(Path self, @ClosureParams(value=SimpleType.class, options={"byte"}) Closure closure) throws IOException {
        BufferedInputStream is = NioGroovyMethods.newInputStream(self);
        IOGroovyMethods.eachByte(is, closure);
    }

    public static void eachByte(Path self, int bufferLen, @ClosureParams(value=FromString.class, options={"byte[],Integer"}) Closure closure) throws IOException {
        BufferedInputStream is = NioGroovyMethods.newInputStream(self);
        IOGroovyMethods.eachByte(is, bufferLen, closure);
    }

    public static Writable filterLine(Path self, @ClosureParams(value=SimpleType.class, options={"java.lang.String"}) Closure closure) throws IOException {
        return IOGroovyMethods.filterLine(NioGroovyMethods.newReader(self), closure);
    }

    public static Writable filterLine(Path self, String charset, @ClosureParams(value=SimpleType.class, options={"java.lang.String"}) Closure closure) throws IOException {
        return IOGroovyMethods.filterLine(NioGroovyMethods.newReader(self, charset), closure);
    }

    public static void filterLine(Path self, Writer writer, @ClosureParams(value=SimpleType.class, options={"java.lang.String"}) Closure closure) throws IOException {
        IOGroovyMethods.filterLine(NioGroovyMethods.newReader(self), writer, closure);
    }

    public static void filterLine(Path self, Writer writer, String charset, @ClosureParams(value=SimpleType.class, options={"java.lang.String"}) Closure closure) throws IOException {
        IOGroovyMethods.filterLine(NioGroovyMethods.newReader(self, charset), writer, closure);
    }

    public static byte[] readBytes(Path self) throws IOException {
        return Files.readAllBytes(self);
    }

    @Deprecated
    public static <T> T withCloseable(Closeable self, @ClosureParams(value=SimpleType.class, options={"java.io.Closeable"}) Closure<T> action) throws IOException {
        return IOGroovyMethods.withCloseable(self, action);
    }
}

