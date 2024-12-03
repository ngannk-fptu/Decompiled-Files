/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.io.EncodingAwareBufferedWriter;
import groovy.io.FileType;
import groovy.io.FileVisitResult;
import groovy.io.GroovyPrintWriter;
import groovy.lang.Closure;
import groovy.lang.MetaClass;
import groovy.lang.Writable;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.FromString;
import groovy.transform.stc.SimpleType;
import groovy.util.CharsetToolkit;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.DefaultGroovyMethodsSupport;
import org.codehaus.groovy.runtime.IOGroovyMethods;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.WritableFile;
import org.codehaus.groovy.runtime.callsite.BooleanReturningMethodInvoker;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class ResourceGroovyMethods
extends DefaultGroovyMethodsSupport {
    public static long size(File self) {
        return self.length();
    }

    public static long directorySize(File self) throws IOException, IllegalArgumentException {
        final long[] size = new long[]{0L};
        ResourceGroovyMethods.eachFileRecurse(self, FileType.FILES, new Closure<Void>(null){

            public void doCall(Object[] args) {
                size[0] = size[0] + ((File)args[0]).length();
            }
        });
        return size[0];
    }

    public static ObjectOutputStream newObjectOutputStream(File file) throws IOException {
        return new ObjectOutputStream(new FileOutputStream(file));
    }

    public static <T> T withObjectOutputStream(File file, @ClosureParams(value=SimpleType.class, options={"java.io.ObjectOutputStream"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withStream(ResourceGroovyMethods.newObjectOutputStream(file), closure);
    }

    public static ObjectInputStream newObjectInputStream(File file) throws IOException {
        return new ObjectInputStream(new FileInputStream(file));
    }

    public static ObjectInputStream newObjectInputStream(File file, ClassLoader classLoader) throws IOException {
        return IOGroovyMethods.newObjectInputStream(new FileInputStream(file), classLoader);
    }

    public static void eachObject(File self, Closure closure) throws IOException, ClassNotFoundException {
        IOGroovyMethods.eachObject(ResourceGroovyMethods.newObjectInputStream(self), closure);
    }

    public static <T> T withObjectInputStream(File file, @ClosureParams(value=SimpleType.class, options={"java.io.ObjectInputStream"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withStream(ResourceGroovyMethods.newObjectInputStream(file), closure);
    }

    public static <T> T withObjectInputStream(File file, ClassLoader classLoader, @ClosureParams(value=SimpleType.class, options={"java.io.ObjectInputStream"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withStream(ResourceGroovyMethods.newObjectInputStream(file, classLoader), closure);
    }

    public static <T> T eachLine(File self, @ClosureParams(value=FromString.class, options={"String", "String,Integer"}) Closure<T> closure) throws IOException {
        return ResourceGroovyMethods.eachLine(self, 1, closure);
    }

    public static <T> T eachLine(File self, String charset, @ClosureParams(value=FromString.class, options={"String", "String,Integer"}) Closure<T> closure) throws IOException {
        return ResourceGroovyMethods.eachLine(self, charset, 1, closure);
    }

    public static <T> T eachLine(File self, int firstLine, @ClosureParams(value=FromString.class, options={"String", "String,Integer"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.eachLine(ResourceGroovyMethods.newReader(self), firstLine, closure);
    }

    public static <T> T eachLine(File self, String charset, int firstLine, @ClosureParams(value=FromString.class, options={"String", "String,Integer"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.eachLine(ResourceGroovyMethods.newReader(self, charset), firstLine, closure);
    }

    public static <T> T eachLine(URL url, @ClosureParams(value=FromString.class, options={"String", "String,Integer"}) Closure<T> closure) throws IOException {
        return ResourceGroovyMethods.eachLine(url, 1, closure);
    }

    public static <T> T eachLine(URL url, int firstLine, @ClosureParams(value=FromString.class, options={"String", "String,Integer"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.eachLine(url.openConnection().getInputStream(), firstLine, closure);
    }

    public static <T> T eachLine(URL url, String charset, @ClosureParams(value=FromString.class, options={"String", "String,Integer"}) Closure<T> closure) throws IOException {
        return ResourceGroovyMethods.eachLine(url, charset, 1, closure);
    }

    public static <T> T eachLine(URL url, String charset, int firstLine, @ClosureParams(value=FromString.class, options={"String", "String,Integer"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.eachLine(ResourceGroovyMethods.newReader(url, charset), firstLine, closure);
    }

    public static <T> T splitEachLine(File self, String regex, @ClosureParams(value=SimpleType.class, options={"java.lang.String[]"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.splitEachLine((Reader)ResourceGroovyMethods.newReader(self), regex, closure);
    }

    public static <T> T splitEachLine(File self, Pattern pattern, @ClosureParams(value=SimpleType.class, options={"java.lang.String[]"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.splitEachLine((Reader)ResourceGroovyMethods.newReader(self), pattern, closure);
    }

    public static <T> T splitEachLine(File self, String regex, String charset, @ClosureParams(value=SimpleType.class, options={"java.lang.String[]"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.splitEachLine((Reader)ResourceGroovyMethods.newReader(self, charset), regex, closure);
    }

    public static <T> T splitEachLine(File self, Pattern pattern, String charset, @ClosureParams(value=SimpleType.class, options={"java.lang.String[]"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.splitEachLine((Reader)ResourceGroovyMethods.newReader(self, charset), pattern, closure);
    }

    public static <T> T splitEachLine(URL self, String regex, @ClosureParams(value=SimpleType.class, options={"java.lang.String[]"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.splitEachLine((Reader)ResourceGroovyMethods.newReader(self), regex, closure);
    }

    public static <T> T splitEachLine(URL self, Pattern pattern, @ClosureParams(value=SimpleType.class, options={"java.lang.String[]"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.splitEachLine((Reader)ResourceGroovyMethods.newReader(self), pattern, closure);
    }

    public static <T> T splitEachLine(URL self, String regex, String charset, @ClosureParams(value=SimpleType.class, options={"java.lang.String[]"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.splitEachLine((Reader)ResourceGroovyMethods.newReader(self, charset), regex, closure);
    }

    public static <T> T splitEachLine(URL self, Pattern pattern, String charset, @ClosureParams(value=SimpleType.class, options={"java.lang.String[]"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.splitEachLine((Reader)ResourceGroovyMethods.newReader(self, charset), pattern, closure);
    }

    public static List<String> readLines(File file) throws IOException {
        return IOGroovyMethods.readLines(ResourceGroovyMethods.newReader(file));
    }

    public static List<String> readLines(File file, String charset) throws IOException {
        return IOGroovyMethods.readLines(ResourceGroovyMethods.newReader(file, charset));
    }

    public static List<String> readLines(URL self) throws IOException {
        return IOGroovyMethods.readLines(ResourceGroovyMethods.newReader(self));
    }

    public static List<String> readLines(URL self, String charset) throws IOException {
        return IOGroovyMethods.readLines(ResourceGroovyMethods.newReader(self, charset));
    }

    public static String getText(File file, String charset) throws IOException {
        return IOGroovyMethods.getText(ResourceGroovyMethods.newReader(file, charset));
    }

    public static String getText(File file) throws IOException {
        return IOGroovyMethods.getText(ResourceGroovyMethods.newReader(file));
    }

    public static String getText(URL url) throws IOException {
        return ResourceGroovyMethods.getText(url, CharsetToolkit.getDefaultSystemCharset().name());
    }

    public static String getText(URL url, Map parameters) throws IOException {
        return ResourceGroovyMethods.getText(url, parameters, CharsetToolkit.getDefaultSystemCharset().name());
    }

    public static String getText(URL url, String charset) throws IOException {
        BufferedReader reader = ResourceGroovyMethods.newReader(url, charset);
        return IOGroovyMethods.getText(reader);
    }

    public static String getText(URL url, Map parameters, String charset) throws IOException {
        BufferedReader reader = ResourceGroovyMethods.newReader(url, parameters, charset);
        return IOGroovyMethods.getText(reader);
    }

    public static byte[] getBytes(File file) throws IOException {
        return IOGroovyMethods.getBytes(new FileInputStream(file));
    }

    public static byte[] getBytes(URL url) throws IOException {
        return IOGroovyMethods.getBytes(url.openConnection().getInputStream());
    }

    public static byte[] getBytes(URL url, Map parameters) throws IOException {
        return IOGroovyMethods.getBytes(ResourceGroovyMethods.configuredInputStream(parameters, url));
    }

    public static void setBytes(File file, byte[] bytes) throws IOException {
        IOGroovyMethods.setBytes(new FileOutputStream(file), bytes);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void write(File file, String text) throws IOException {
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(text);
            ((Writer)writer).flush();
            FileWriter temp = writer;
            writer = null;
            ((Writer)temp).close();
        }
        catch (Throwable throwable) {
            ResourceGroovyMethods.closeWithWarning(writer);
            throw throwable;
        }
        ResourceGroovyMethods.closeWithWarning(writer);
    }

    public static void setText(File file, String text) throws IOException {
        ResourceGroovyMethods.write(file, text);
    }

    public static void setText(File file, String text, String charset) throws IOException {
        ResourceGroovyMethods.write(file, text, charset);
    }

    public static File leftShift(File file, Object text) throws IOException {
        ResourceGroovyMethods.append(file, text);
        return file;
    }

    public static File leftShift(File file, byte[] bytes) throws IOException {
        ResourceGroovyMethods.append(file, bytes);
        return file;
    }

    public static File leftShift(File file, InputStream data) throws IOException {
        ResourceGroovyMethods.append(file, data);
        return file;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void write(File file, String text, String charset) throws IOException {
        OutputStreamWriter writer = null;
        try {
            FileOutputStream out = new FileOutputStream(file);
            ResourceGroovyMethods.writeUTF16BomIfRequired(charset, out);
            writer = new OutputStreamWriter((OutputStream)out, charset);
            writer.write(text);
            ((Writer)writer).flush();
            OutputStreamWriter temp = writer;
            writer = null;
            ((Writer)temp).close();
        }
        catch (Throwable throwable) {
            ResourceGroovyMethods.closeWithWarning(writer);
            throw throwable;
        }
        ResourceGroovyMethods.closeWithWarning(writer);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void append(File file, Object text) throws IOException {
        FileWriter writer = null;
        try {
            writer = new FileWriter(file, true);
            InvokerHelper.write(writer, text);
            ((Writer)writer).flush();
            FileWriter temp = writer;
            writer = null;
            ((Writer)temp).close();
        }
        catch (Throwable throwable) {
            ResourceGroovyMethods.closeWithWarning(writer);
            throw throwable;
        }
        ResourceGroovyMethods.closeWithWarning(writer);
    }

    public static void append(File file, Reader reader) throws IOException {
        ResourceGroovyMethods.appendBuffered(file, reader);
    }

    public static void append(File file, Writer writer) throws IOException {
        ResourceGroovyMethods.appendBuffered(file, writer);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void appendBuffered(File file, Object text) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = ResourceGroovyMethods.newWriter(file, true);
            InvokerHelper.write(writer, text);
            writer.flush();
            BufferedWriter temp = writer;
            writer = null;
            ((Writer)temp).close();
        }
        finally {
            ResourceGroovyMethods.closeWithWarning(writer);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void append(File file, byte[] bytes) throws IOException {
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file, true);
            ((OutputStream)stream).write(bytes, 0, bytes.length);
            stream.flush();
            FileOutputStream temp = stream;
            stream = null;
            ((OutputStream)temp).close();
        }
        catch (Throwable throwable) {
            ResourceGroovyMethods.closeWithWarning(stream);
            throw throwable;
        }
        ResourceGroovyMethods.closeWithWarning(stream);
    }

    public static void append(File self, InputStream stream) throws IOException {
        FileOutputStream out = new FileOutputStream(self, true);
        try {
            IOGroovyMethods.leftShift((OutputStream)out, stream);
        }
        finally {
            ResourceGroovyMethods.closeWithWarning(out);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void append(File file, Object text, String charset) throws IOException {
        OutputStreamWriter writer = null;
        try {
            FileOutputStream out = new FileOutputStream(file, true);
            if (!file.exists()) {
                ResourceGroovyMethods.writeUTF16BomIfRequired(charset, out);
            }
            writer = new OutputStreamWriter((OutputStream)out, charset);
            InvokerHelper.write(writer, text);
            ((Writer)writer).flush();
            OutputStreamWriter temp = writer;
            writer = null;
            ((Writer)temp).close();
        }
        catch (Throwable throwable) {
            ResourceGroovyMethods.closeWithWarning(writer);
            throw throwable;
        }
        ResourceGroovyMethods.closeWithWarning(writer);
    }

    public static void append(File file, Writer writer, String charset) throws IOException {
        ResourceGroovyMethods.appendBuffered(file, writer, charset);
    }

    public static void append(File file, Reader reader, String charset) throws IOException {
        ResourceGroovyMethods.appendBuffered(file, reader, charset);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void appendBuffered(File file, Object text, String charset) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = ResourceGroovyMethods.newWriter(file, charset, true);
            InvokerHelper.write(writer, text);
            writer.flush();
            BufferedWriter temp = writer;
            writer = null;
            ((Writer)temp).close();
        }
        catch (Throwable throwable) {
            ResourceGroovyMethods.closeWithWarning(writer);
            throw throwable;
        }
        ResourceGroovyMethods.closeWithWarning(writer);
    }

    private static void checkDir(File dir) throws FileNotFoundException, IllegalArgumentException {
        if (!dir.exists()) {
            throw new FileNotFoundException(dir.getAbsolutePath());
        }
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("The provided File object is not a directory: " + dir.getAbsolutePath());
        }
    }

    public static void eachFile(File self, FileType fileType, @ClosureParams(value=SimpleType.class, options={"java.io.File"}) Closure closure) throws FileNotFoundException, IllegalArgumentException {
        ResourceGroovyMethods.checkDir(self);
        File[] files = self.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (fileType != FileType.ANY && (fileType == FileType.FILES || !file.isDirectory()) && (fileType == FileType.DIRECTORIES || !file.isFile())) continue;
            closure.call((Object)file);
        }
    }

    public static void eachFile(File self, @ClosureParams(value=SimpleType.class, options={"java.io.File"}) Closure closure) throws FileNotFoundException, IllegalArgumentException {
        ResourceGroovyMethods.eachFile(self, FileType.ANY, closure);
    }

    public static void eachDir(File self, @ClosureParams(value=SimpleType.class, options={"java.io.File"}) Closure closure) throws FileNotFoundException, IllegalArgumentException {
        ResourceGroovyMethods.eachFile(self, FileType.DIRECTORIES, closure);
    }

    public static void eachFileRecurse(File self, FileType fileType, @ClosureParams(value=SimpleType.class, options={"java.io.File"}) Closure closure) throws FileNotFoundException, IllegalArgumentException {
        ResourceGroovyMethods.checkDir(self);
        File[] files = self.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                if (fileType != FileType.FILES) {
                    closure.call((Object)file);
                }
                ResourceGroovyMethods.eachFileRecurse(file, fileType, closure);
                continue;
            }
            if (fileType == FileType.DIRECTORIES) continue;
            closure.call((Object)file);
        }
    }

    public static void traverse(File self, Map<String, Object> options, @ClosureParams(value=SimpleType.class, options={"java.io.File"}) Closure closure) throws FileNotFoundException, IllegalArgumentException {
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
        FileVisitResult terminated = ResourceGroovyMethods.traverse(self, options, closure, maxDepth);
        if (type != FileType.FILES && visitRoot.booleanValue() && closure != null && ResourceGroovyMethods.notFiltered(self, filter, nameFilter, excludeFilter, excludeNameFilter) && (closureResult = closure.call((Object)self)) == FileVisitResult.TERMINATE) {
            return;
        }
        if (postRoot.booleanValue() && post != null && terminated != FileVisitResult.TERMINATE) {
            post.call((Object)self);
        }
    }

    private static boolean notFiltered(File file, Object filter, Object nameFilter, Object excludeFilter, Object excludeNameFilter) {
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
            filterParam = file;
        } else if (nameFilter != null) {
            filterToUse = nameFilter;
            filterParam = file.getName();
        }
        Object excludeFilterToUse = null;
        Object excludeParam = null;
        if (excludeFilter != null) {
            excludeFilterToUse = excludeFilter;
            excludeParam = file;
        } else if (excludeNameFilter != null) {
            excludeFilterToUse = excludeNameFilter;
            excludeParam = file.getName();
        }
        MetaClass filterMC = filterToUse == null ? null : InvokerHelper.getMetaClass(filterToUse);
        MetaClass excludeMC = excludeFilterToUse == null ? null : InvokerHelper.getMetaClass(excludeFilterToUse);
        boolean included = filterToUse == null || DefaultTypeTransformation.castToBoolean(filterMC.invokeMethod(filterToUse, "isCase", filterParam));
        boolean excluded = excludeFilterToUse != null && DefaultTypeTransformation.castToBoolean(excludeMC.invokeMethod(excludeFilterToUse, "isCase", excludeParam));
        return included && !excluded;
    }

    public static void traverse(File self, @ClosureParams(value=SimpleType.class, options={"java.io.File"}) Closure closure) throws FileNotFoundException, IllegalArgumentException {
        ResourceGroovyMethods.traverse(self, new HashMap<String, Object>(), closure);
    }

    public static void traverse(File self, Map<String, Object> options) throws FileNotFoundException, IllegalArgumentException {
        Closure visit = (Closure)options.remove("visit");
        ResourceGroovyMethods.traverse(self, options, visit);
    }

    private static FileVisitResult traverse(File self, Map<String, Object> options, Closure closure, int maxDepth) throws FileNotFoundException, IllegalArgumentException {
        ResourceGroovyMethods.checkDir(self);
        Closure pre = (Closure)options.get("preDir");
        Closure post = (Closure)options.get("postDir");
        FileType type = (FileType)((Object)options.get("type"));
        Object filter = options.get("filter");
        Object nameFilter = options.get("nameFilter");
        Object excludeFilter = options.get("excludeFilter");
        Object excludeNameFilter = options.get("excludeNameFilter");
        Closure sort = (Closure)options.get("sort");
        File[] origFiles = self.listFiles();
        if (origFiles != null) {
            List<File> files = Arrays.asList(origFiles);
            if (sort != null) {
                files = DefaultGroovyMethods.sort(files, sort);
            }
            for (File file : files) {
                Object closureResult;
                if (file.isDirectory()) {
                    FileVisitResult terminated;
                    if (type != FileType.FILES && closure != null && ResourceGroovyMethods.notFiltered(file, filter, nameFilter, excludeFilter, excludeNameFilter)) {
                        closureResult = closure.call((Object)file);
                        if (closureResult == FileVisitResult.SKIP_SIBLINGS) break;
                        if (closureResult == FileVisitResult.TERMINATE) {
                            return FileVisitResult.TERMINATE;
                        }
                    }
                    if (maxDepth == 0) continue;
                    Object preResult = null;
                    if (pre != null) {
                        preResult = pre.call((Object)file);
                    }
                    if (preResult == FileVisitResult.SKIP_SIBLINGS) break;
                    if (preResult == FileVisitResult.TERMINATE) {
                        return FileVisitResult.TERMINATE;
                    }
                    if (preResult != FileVisitResult.SKIP_SUBTREE && (terminated = ResourceGroovyMethods.traverse(file, options, closure, maxDepth - 1)) == FileVisitResult.TERMINATE) {
                        return terminated;
                    }
                    Object postResult = null;
                    if (post != null) {
                        postResult = post.call((Object)file);
                    }
                    if (postResult == FileVisitResult.SKIP_SIBLINGS) break;
                    if (postResult != FileVisitResult.TERMINATE) continue;
                    return FileVisitResult.TERMINATE;
                }
                if (type == FileType.DIRECTORIES || closure == null || !ResourceGroovyMethods.notFiltered(file, filter, nameFilter, excludeFilter, excludeNameFilter)) continue;
                closureResult = closure.call((Object)file);
                if (closureResult == FileVisitResult.SKIP_SIBLINGS) break;
                if (closureResult != FileVisitResult.TERMINATE) continue;
                return FileVisitResult.TERMINATE;
            }
        }
        return FileVisitResult.CONTINUE;
    }

    public static void eachFileRecurse(File self, @ClosureParams(value=SimpleType.class, options={"java.io.File"}) Closure closure) throws FileNotFoundException, IllegalArgumentException {
        ResourceGroovyMethods.eachFileRecurse(self, FileType.ANY, closure);
    }

    public static void eachDirRecurse(File self, @ClosureParams(value=SimpleType.class, options={"java.io.File"}) Closure closure) throws FileNotFoundException, IllegalArgumentException {
        ResourceGroovyMethods.eachFileRecurse(self, FileType.DIRECTORIES, closure);
    }

    public static void eachFileMatch(File self, FileType fileType, Object nameFilter, @ClosureParams(value=SimpleType.class, options={"java.io.File"}) Closure closure) throws FileNotFoundException, IllegalArgumentException {
        ResourceGroovyMethods.checkDir(self);
        File[] files = self.listFiles();
        if (files == null) {
            return;
        }
        BooleanReturningMethodInvoker bmi = new BooleanReturningMethodInvoker("isCase");
        for (File currentFile : files) {
            if (fileType != FileType.ANY && (fileType == FileType.FILES || !currentFile.isDirectory()) && (fileType == FileType.DIRECTORIES || !currentFile.isFile()) || !bmi.invoke(nameFilter, currentFile.getName())) continue;
            closure.call((Object)currentFile);
        }
    }

    public static void eachFileMatch(File self, Object nameFilter, @ClosureParams(value=SimpleType.class, options={"java.io.File"}) Closure closure) throws FileNotFoundException, IllegalArgumentException {
        ResourceGroovyMethods.eachFileMatch(self, FileType.ANY, nameFilter, closure);
    }

    public static void eachDirMatch(File self, Object nameFilter, @ClosureParams(value=SimpleType.class, options={"java.io.File"}) Closure closure) throws FileNotFoundException, IllegalArgumentException {
        ResourceGroovyMethods.eachFileMatch(self, FileType.DIRECTORIES, nameFilter, closure);
    }

    public static boolean deleteDir(File self) {
        if (!self.exists()) {
            return true;
        }
        if (!self.isDirectory()) {
            return false;
        }
        File[] files = self.listFiles();
        if (files == null) {
            return false;
        }
        boolean result = true;
        for (File file : files) {
            if (file.isDirectory()) {
                if (ResourceGroovyMethods.deleteDir(file)) continue;
                result = false;
                continue;
            }
            if (file.delete()) continue;
            result = false;
        }
        if (!self.delete()) {
            result = false;
        }
        return result;
    }

    public static boolean renameTo(File self, String newPathName) {
        return self.renameTo(new File(newPathName));
    }

    public static File asWritable(File file) {
        return new WritableFile(file);
    }

    public static <T> T asType(File f, Class<T> c) {
        if (c == Writable.class) {
            return (T)ResourceGroovyMethods.asWritable(f);
        }
        return DefaultGroovyMethods.asType((Object)f, c);
    }

    public static File asWritable(File file, String encoding) {
        return new WritableFile(file, encoding);
    }

    public static BufferedReader newReader(File file) throws IOException {
        CharsetToolkit toolkit = new CharsetToolkit(file);
        return toolkit.getReader();
    }

    public static BufferedReader newReader(File file, String charset) throws FileNotFoundException, UnsupportedEncodingException {
        return new BufferedReader(new InputStreamReader((InputStream)new FileInputStream(file), charset));
    }

    public static <T> T withReader(File file, @ClosureParams(value=SimpleType.class, options={"java.io.BufferedReader"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withReader(ResourceGroovyMethods.newReader(file), closure);
    }

    public static <T> T withReader(File file, String charset, @ClosureParams(value=SimpleType.class, options={"java.io.BufferedReader"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withReader(ResourceGroovyMethods.newReader(file, charset), closure);
    }

    public static BufferedOutputStream newOutputStream(File file) throws IOException {
        return new BufferedOutputStream(new FileOutputStream(file));
    }

    public static DataOutputStream newDataOutputStream(File file) throws IOException {
        return new DataOutputStream(new FileOutputStream(file));
    }

    public static Object withOutputStream(File file, @ClosureParams(value=SimpleType.class, options={"java.io.OutputStream"}) Closure closure) throws IOException {
        return IOGroovyMethods.withStream(ResourceGroovyMethods.newOutputStream(file), closure);
    }

    public static Object withInputStream(File file, @ClosureParams(value=SimpleType.class, options={"java.io.InputStream"}) Closure closure) throws IOException {
        return IOGroovyMethods.withStream(ResourceGroovyMethods.newInputStream(file), closure);
    }

    public static <T> T withInputStream(URL url, @ClosureParams(value=SimpleType.class, options={"java.io.InputStream"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withStream(ResourceGroovyMethods.newInputStream(url), closure);
    }

    public static <T> T withDataOutputStream(File file, @ClosureParams(value=SimpleType.class, options={"java.io.DataOutputStream"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withStream(ResourceGroovyMethods.newDataOutputStream(file), closure);
    }

    public static <T> T withDataInputStream(File file, @ClosureParams(value=SimpleType.class, options={"java.io.DataInputStream"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withStream(ResourceGroovyMethods.newDataInputStream(file), closure);
    }

    public static BufferedWriter newWriter(File file) throws IOException {
        return new BufferedWriter(new FileWriter(file));
    }

    public static BufferedWriter newWriter(File file, boolean append) throws IOException {
        return new BufferedWriter(new FileWriter(file, append));
    }

    public static BufferedWriter newWriter(File file, String charset, boolean append) throws IOException {
        if (append) {
            return new EncodingAwareBufferedWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(file, append), charset));
        }
        FileOutputStream stream = new FileOutputStream(file);
        ResourceGroovyMethods.writeUTF16BomIfRequired(charset, stream);
        return new EncodingAwareBufferedWriter(new OutputStreamWriter((OutputStream)stream, charset));
    }

    private static void writeUTF16BomIfRequired(String charset, OutputStream stream) throws IOException {
        if ("UTF-16BE".equals(Charset.forName(charset).name())) {
            ResourceGroovyMethods.writeUtf16Bom(stream, true);
        } else if ("UTF-16LE".equals(Charset.forName(charset).name())) {
            ResourceGroovyMethods.writeUtf16Bom(stream, false);
        }
    }

    public static BufferedWriter newWriter(File file, String charset) throws IOException {
        return ResourceGroovyMethods.newWriter(file, charset, false);
    }

    private static void writeUtf16Bom(OutputStream stream, boolean bigEndian) throws IOException {
        if (bigEndian) {
            stream.write(-2);
            stream.write(-1);
        } else {
            stream.write(-1);
            stream.write(-2);
        }
    }

    public static <T> T withWriter(File file, @ClosureParams(value=SimpleType.class, options={"java.io.BufferedWriter"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withWriter(ResourceGroovyMethods.newWriter(file), closure);
    }

    public static <T> T withWriter(File file, String charset, @ClosureParams(value=SimpleType.class, options={"java.io.BufferedWriter"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withWriter(ResourceGroovyMethods.newWriter(file, charset), closure);
    }

    public static <T> T withWriterAppend(File file, String charset, @ClosureParams(value=SimpleType.class, options={"java.io.BufferedWriter"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withWriter(ResourceGroovyMethods.newWriter(file, charset, true), closure);
    }

    public static <T> T withWriterAppend(File file, @ClosureParams(value=SimpleType.class, options={"java.io.BufferedWriter"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withWriter(ResourceGroovyMethods.newWriter(file, true), closure);
    }

    public static PrintWriter newPrintWriter(File file) throws IOException {
        return new GroovyPrintWriter(ResourceGroovyMethods.newWriter(file));
    }

    public static PrintWriter newPrintWriter(File file, String charset) throws IOException {
        return new GroovyPrintWriter(ResourceGroovyMethods.newWriter(file, charset));
    }

    public static <T> T withPrintWriter(File file, @ClosureParams(value=SimpleType.class, options={"java.io.PrintWriter"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withWriter(ResourceGroovyMethods.newPrintWriter(file), closure);
    }

    public static <T> T withPrintWriter(File file, String charset, @ClosureParams(value=SimpleType.class, options={"java.io.PrintWriter"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withWriter(ResourceGroovyMethods.newPrintWriter(file, charset), closure);
    }

    public static <T> T withReader(URL url, @ClosureParams(value=SimpleType.class, options={"java.io.Reader"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withReader(url.openConnection().getInputStream(), closure);
    }

    public static <T> T withReader(URL url, String charset, @ClosureParams(value=SimpleType.class, options={"java.io.Reader"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withReader(url.openConnection().getInputStream(), charset, closure);
    }

    public static BufferedInputStream newInputStream(File file) throws FileNotFoundException {
        return new BufferedInputStream(new FileInputStream(file));
    }

    private static InputStream configuredInputStream(Map parameters, URL url) throws IOException {
        URLConnection connection = url.openConnection();
        if (parameters != null) {
            if (parameters.containsKey("connectTimeout")) {
                connection.setConnectTimeout(DefaultGroovyMethods.asType(parameters.get("connectTimeout"), Integer.class));
            }
            if (parameters.containsKey("readTimeout")) {
                connection.setReadTimeout(DefaultGroovyMethods.asType(parameters.get("readTimeout"), Integer.class));
            }
            if (parameters.containsKey("useCaches")) {
                connection.setUseCaches(DefaultGroovyMethods.asType(parameters.get("useCaches"), Boolean.class));
            }
            if (parameters.containsKey("allowUserInteraction")) {
                connection.setAllowUserInteraction(DefaultGroovyMethods.asType(parameters.get("allowUserInteraction"), Boolean.class));
            }
            if (parameters.containsKey("requestProperties")) {
                Map properties = (Map)parameters.get("requestProperties");
                for (Map.Entry entry : properties.entrySet()) {
                    connection.setRequestProperty((String)entry.getKey(), ((CharSequence)entry.getValue()).toString());
                }
            }
        }
        return connection.getInputStream();
    }

    public static BufferedInputStream newInputStream(URL url) throws MalformedURLException, IOException {
        return new BufferedInputStream(ResourceGroovyMethods.configuredInputStream(null, url));
    }

    public static BufferedInputStream newInputStream(URL url, Map parameters) throws MalformedURLException, IOException {
        return new BufferedInputStream(ResourceGroovyMethods.configuredInputStream(parameters, url));
    }

    public static BufferedReader newReader(URL url) throws MalformedURLException, IOException {
        return IOGroovyMethods.newReader(ResourceGroovyMethods.configuredInputStream(null, url));
    }

    public static BufferedReader newReader(URL url, Map parameters) throws MalformedURLException, IOException {
        return IOGroovyMethods.newReader(ResourceGroovyMethods.configuredInputStream(parameters, url));
    }

    public static BufferedReader newReader(URL url, String charset) throws MalformedURLException, IOException {
        return new BufferedReader(new InputStreamReader(ResourceGroovyMethods.configuredInputStream(null, url), charset));
    }

    public static BufferedReader newReader(URL url, Map parameters, String charset) throws MalformedURLException, IOException {
        return new BufferedReader(new InputStreamReader(ResourceGroovyMethods.configuredInputStream(parameters, url), charset));
    }

    public static DataInputStream newDataInputStream(File file) throws FileNotFoundException {
        return new DataInputStream(new FileInputStream(file));
    }

    public static void eachByte(File self, @ClosureParams(value=SimpleType.class, options={"byte"}) Closure closure) throws IOException {
        BufferedInputStream is = ResourceGroovyMethods.newInputStream(self);
        IOGroovyMethods.eachByte(is, closure);
    }

    public static void eachByte(File self, int bufferLen, @ClosureParams(value=FromString.class, options={"byte[],Integer"}) Closure closure) throws IOException {
        BufferedInputStream is = ResourceGroovyMethods.newInputStream(self);
        IOGroovyMethods.eachByte(is, bufferLen, closure);
    }

    public static void eachByte(URL url, @ClosureParams(value=SimpleType.class, options={"byte"}) Closure closure) throws IOException {
        InputStream is = url.openConnection().getInputStream();
        IOGroovyMethods.eachByte(is, closure);
    }

    public static void eachByte(URL url, int bufferLen, @ClosureParams(value=FromString.class, options={"byte[],Integer"}) Closure closure) throws IOException {
        InputStream is = url.openConnection().getInputStream();
        IOGroovyMethods.eachByte(is, bufferLen, closure);
    }

    public static Writable filterLine(File self, @ClosureParams(value=SimpleType.class, options={"java.lang.String"}) Closure closure) throws IOException {
        return IOGroovyMethods.filterLine(ResourceGroovyMethods.newReader(self), closure);
    }

    public static Writable filterLine(File self, String charset, @ClosureParams(value=SimpleType.class, options={"java.lang.String"}) Closure closure) throws IOException {
        return IOGroovyMethods.filterLine(ResourceGroovyMethods.newReader(self, charset), closure);
    }

    public static void filterLine(File self, Writer writer, @ClosureParams(value=SimpleType.class, options={"java.lang.String"}) Closure closure) throws IOException {
        IOGroovyMethods.filterLine(ResourceGroovyMethods.newReader(self), writer, closure);
    }

    public static void filterLine(File self, Writer writer, String charset, @ClosureParams(value=SimpleType.class, options={"java.lang.String"}) Closure closure) throws IOException {
        IOGroovyMethods.filterLine(ResourceGroovyMethods.newReader(self, charset), writer, closure);
    }

    public static Writable filterLine(URL self, @ClosureParams(value=SimpleType.class, options={"java.lang.String"}) Closure predicate) throws IOException {
        return IOGroovyMethods.filterLine(ResourceGroovyMethods.newReader(self), predicate);
    }

    public static Writable filterLine(URL self, String charset, @ClosureParams(value=SimpleType.class, options={"java.lang.String"}) Closure predicate) throws IOException {
        return IOGroovyMethods.filterLine(ResourceGroovyMethods.newReader(self, charset), predicate);
    }

    public static void filterLine(URL self, Writer writer, @ClosureParams(value=SimpleType.class, options={"java.lang.String"}) Closure predicate) throws IOException {
        IOGroovyMethods.filterLine(ResourceGroovyMethods.newReader(self), writer, predicate);
    }

    public static void filterLine(URL self, Writer writer, String charset, @ClosureParams(value=SimpleType.class, options={"java.lang.String"}) Closure predicate) throws IOException {
        IOGroovyMethods.filterLine(ResourceGroovyMethods.newReader(self, charset), writer, predicate);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] readBytes(File file) throws IOException {
        byte[] bytes = new byte[(int)file.length()];
        FileInputStream fileInputStream = new FileInputStream(file);
        DataInputStream dis = new DataInputStream(fileInputStream);
        try {
            dis.readFully(bytes);
            DataInputStream temp = dis;
            dis = null;
            ((InputStream)temp).close();
        }
        finally {
            ResourceGroovyMethods.closeWithWarning(dis);
        }
        return bytes;
    }

    public static URI toURI(CharSequence self) throws URISyntaxException {
        return new URI(self.toString());
    }

    public static URI toURI(String self) throws URISyntaxException {
        return new URI(self);
    }

    public static URL toURL(CharSequence self) throws MalformedURLException {
        return new URL(self.toString());
    }

    public static URL toURL(String self) throws MalformedURLException {
        return new URL(self);
    }
}

