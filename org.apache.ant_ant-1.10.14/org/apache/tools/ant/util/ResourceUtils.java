/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Vector;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.filters.util.ChainReaderHelper;
import org.apache.tools.ant.types.FilterChain;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.ResourceFactory;
import org.apache.tools.ant.types.TimeComparison;
import org.apache.tools.ant.types.resources.Appendable;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.types.resources.Restrict;
import org.apache.tools.ant.types.resources.StringResource;
import org.apache.tools.ant.types.resources.Touchable;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.types.resources.selectors.Date;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;
import org.apache.tools.ant.types.selectors.SelectorUtils;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.LineTokenizer;

public class ResourceUtils {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    @Deprecated
    public static final String ISO_8859_1 = "ISO-8859-1";
    private static final long MAX_IO_CHUNK_SIZE = 0x1000000L;

    public static Resource[] selectOutOfDateSources(ProjectComponent logTo, Resource[] source, FileNameMapper mapper, ResourceFactory targets) {
        return ResourceUtils.selectOutOfDateSources(logTo, source, mapper, targets, FILE_UTILS.getFileTimestampGranularity());
    }

    public static Resource[] selectOutOfDateSources(ProjectComponent logTo, Resource[] source, FileNameMapper mapper, ResourceFactory targets, long granularity) {
        Union u = new Union();
        u.addAll(Arrays.asList(source));
        ResourceCollection rc = ResourceUtils.selectOutOfDateSources(logTo, u, mapper, targets, granularity);
        return rc.size() == 0 ? new Resource[]{} : ((Union)rc).listResources();
    }

    public static ResourceCollection selectOutOfDateSources(ProjectComponent logTo, ResourceCollection source, FileNameMapper mapper, ResourceFactory targets, long granularity) {
        ResourceUtils.logFuture(logTo, source, granularity);
        return ResourceUtils.selectSources(logTo, source, mapper, targets, sr -> target -> SelectorUtils.isOutOfDate(sr, target, granularity));
    }

    public static ResourceCollection selectSources(ProjectComponent logTo, ResourceCollection source, FileNameMapper mapper, ResourceFactory targets, ResourceSelectorProvider selector) {
        if (source.isEmpty()) {
            logTo.log("No sources found.", 3);
            return Resources.NONE;
        }
        source = Union.getInstance(source);
        Union result = new Union();
        for (Resource sr : source) {
            String srName = sr.getName();
            if (srName != null) {
                srName = srName.replace('/', File.separatorChar);
            }
            String[] targetnames = null;
            try {
                targetnames = mapper.mapFileName(srName);
            }
            catch (Exception e) {
                logTo.log("Caught " + e + " mapping resource " + sr, 3);
            }
            if (targetnames == null || targetnames.length == 0) {
                logTo.log(sr + " skipped - don't know how to handle it", 3);
                continue;
            }
            Union targetColl = new Union();
            for (String targetname : targetnames) {
                if (targetname == null) {
                    targetname = "(no name)";
                }
                targetColl.add(targets.getResource(targetname.replace(File.separatorChar, '/')));
            }
            Restrict r = new Restrict();
            r.add(selector.getTargetSelectorForSource(sr));
            r.add(targetColl);
            if (r.size() > 0) {
                result.add(sr);
                Resource t = r.iterator().next();
                logTo.log(sr.getName() + " added as " + t.getName() + (t.isExists() ? " is outdated." : " doesn't exist."), 3);
                continue;
            }
            logTo.log(sr.getName() + " omitted as " + targetColl.toString() + (targetColl.size() == 1 ? " is" : " are ") + " up to date.", 3);
        }
        return result;
    }

    public static void copyResource(Resource source, Resource dest) throws IOException {
        ResourceUtils.copyResource(source, dest, null);
    }

    public static void copyResource(Resource source, Resource dest, Project project) throws IOException {
        ResourceUtils.copyResource(source, dest, null, null, false, false, null, null, project);
    }

    public static void copyResource(Resource source, Resource dest, FilterSetCollection filters, Vector<FilterChain> filterChains, boolean overwrite, boolean preserveLastModified, String inputEncoding, String outputEncoding, Project project) throws IOException {
        ResourceUtils.copyResource(source, dest, filters, filterChains, overwrite, preserveLastModified, false, inputEncoding, outputEncoding, project);
    }

    public static void copyResource(Resource source, Resource dest, FilterSetCollection filters, Vector<FilterChain> filterChains, boolean overwrite, boolean preserveLastModified, boolean append, String inputEncoding, String outputEncoding, Project project) throws IOException {
        ResourceUtils.copyResource(source, dest, filters, filterChains, overwrite, preserveLastModified, append, inputEncoding, outputEncoding, project, false);
    }

    public static void copyResource(Resource source, Resource dest, FilterSetCollection filters, Vector<FilterChain> filterChains, boolean overwrite, boolean preserveLastModified, boolean append, String inputEncoding, String outputEncoding, Project project, boolean force) throws IOException {
        if (!overwrite && !SelectorUtils.isOutOfDate(source, dest, FileUtils.getFileUtils().getFileTimestampGranularity())) {
            return;
        }
        boolean filterSetsAvailable = filters != null && filters.hasFilters();
        boolean filterChainsAvailable = filterChains != null && !filterChains.isEmpty();
        String effectiveInputEncoding = source.asOptional(StringResource.class).map(StringResource::getEncoding).orElse(inputEncoding);
        File destFile = dest.asOptional(FileProvider.class).map(FileProvider::getFile).orElse(null);
        if (destFile != null && destFile.isFile() && !destFile.canWrite()) {
            if (!force) {
                throw new ReadOnlyTargetFileException(destFile);
            }
            if (!FILE_UTILS.tryHardToDelete(destFile)) {
                throw new IOException("failed to delete read-only destination file " + destFile);
            }
        }
        if (filterSetsAvailable) {
            ResourceUtils.copyWithFilterSets(source, dest, filters, filterChains, append, effectiveInputEncoding, outputEncoding, project);
        } else if (filterChainsAvailable || effectiveInputEncoding != null && !effectiveInputEncoding.equals(outputEncoding) || effectiveInputEncoding == null && outputEncoding != null) {
            ResourceUtils.copyWithFilterChainsOrTranscoding(source, dest, filterChains, append, effectiveInputEncoding, outputEncoding, project);
        } else {
            boolean copied = false;
            if (source.as(FileProvider.class) != null && destFile != null && !append) {
                File sourceFile = source.as(FileProvider.class).getFile();
                try {
                    ResourceUtils.copyUsingFileChannels(sourceFile, destFile, project);
                    copied = true;
                }
                catch (IOException ex) {
                    String msg = "Attempt to copy " + sourceFile + " to " + destFile + " using NIO Channels failed due to '" + ex.getMessage() + "'.  Falling back to streams.";
                    if (project != null) {
                        project.log(msg, 1);
                    }
                    System.err.println(msg);
                }
            }
            if (!copied) {
                ResourceUtils.copyUsingStreams(source, dest, append, project);
            }
        }
        if (preserveLastModified) {
            dest.asOptional(Touchable.class).ifPresent(t -> ResourceUtils.setLastModified(t, source.getLastModified()));
        }
    }

    public static void setLastModified(Touchable t, long time) {
        t.touch(time < 0L ? System.currentTimeMillis() : time);
    }

    public static boolean contentEquals(Resource r1, Resource r2, boolean text) throws IOException {
        if (r1.isExists() != r2.isExists()) {
            return false;
        }
        if (!r1.isExists()) {
            return true;
        }
        if (r1.isDirectory() || r2.isDirectory()) {
            return false;
        }
        if (r1.equals(r2)) {
            return true;
        }
        if (!text) {
            long s1 = r1.getSize();
            long s2 = r2.getSize();
            if (s1 != -1L && s2 != -1L && s1 != s2) {
                return false;
            }
        }
        return ResourceUtils.compareContent(r1, r2, text) == 0;
    }

    public static int compareContent(Resource r1, Resource r2, boolean text) throws IOException {
        if (r1.equals(r2)) {
            return 0;
        }
        boolean e1 = r1.isExists();
        boolean e2 = r2.isExists();
        if (!e1 && !e2) {
            return 0;
        }
        if (e1 != e2) {
            return e1 ? 1 : -1;
        }
        boolean d1 = r1.isDirectory();
        boolean d2 = r2.isDirectory();
        if (d1 && d2) {
            return 0;
        }
        if (d1 || d2) {
            return d1 ? -1 : 1;
        }
        return text ? ResourceUtils.textCompare(r1, r2) : ResourceUtils.binaryCompare(r1, r2);
    }

    public static FileResource asFileResource(FileProvider fileProvider) {
        if (fileProvider instanceof FileResource || fileProvider == null) {
            return (FileResource)fileProvider;
        }
        return new FileResource(Project.getProject(fileProvider), fileProvider.getFile());
    }

    /*
     * Exception decompiling
     */
    private static int binaryCompare(Resource r1, Resource r2) throws IOException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [12[WHILELOOP]], but top level block is 3[TRYBLOCK]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
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
     * Exception decompiling
     */
    private static int textCompare(Resource r1, Resource r2) throws IOException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [14[WHILELOOP]], but top level block is 5[TRYBLOCK]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
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

    private static void logFuture(ProjectComponent logTo, ResourceCollection rc, long granularity) {
        long now = System.currentTimeMillis() + granularity;
        Date sel = new Date();
        sel.setMillis(now);
        sel.setWhen(TimeComparison.AFTER);
        Restrict future = new Restrict();
        future.add(sel);
        future.add(rc);
        for (Resource r : future) {
            logTo.log("Warning: " + r.getName() + " modified in the future.", 1);
        }
    }

    private static void copyWithFilterSets(Resource source, Resource dest, FilterSetCollection filters, Vector<FilterChain> filterChains, boolean append, String inputEncoding, String outputEncoding, Project project) throws IOException {
        if (ResourceUtils.areSame(source, dest)) {
            ResourceUtils.log(project, "Skipping (self) copy of " + source + " to " + dest);
            return;
        }
        try (Reader in = ResourceUtils.filterWith(project, inputEncoding, filterChains, source.getInputStream());
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(ResourceUtils.getOutputStream(dest, append, project), ResourceUtils.charsetFor(outputEncoding)));){
            LineTokenizer lineTokenizer = new LineTokenizer();
            lineTokenizer.setIncludeDelims(true);
            String line = lineTokenizer.getToken(in);
            while (line != null) {
                if (line.isEmpty()) {
                    out.newLine();
                } else {
                    out.write(filters.replaceTokens(line));
                }
                line = lineTokenizer.getToken(in);
            }
        }
    }

    private static Reader filterWith(Project project, String encoding, Vector<FilterChain> filterChains, InputStream input) {
        Reader r = new InputStreamReader(input, ResourceUtils.charsetFor(encoding));
        if (filterChains != null && !filterChains.isEmpty()) {
            ChainReaderHelper crh = new ChainReaderHelper();
            crh.setBufferSize(8192);
            crh.setPrimaryReader(r);
            crh.setFilterChains(filterChains);
            crh.setProject(project);
            r = crh.getAssembledReader();
        }
        return new BufferedReader(r);
    }

    private static Charset charsetFor(String encoding) {
        return encoding == null ? Charset.defaultCharset() : Charset.forName(encoding);
    }

    private static void copyWithFilterChainsOrTranscoding(Resource source, Resource dest, Vector<FilterChain> filterChains, boolean append, String inputEncoding, String outputEncoding, Project project) throws IOException {
        if (ResourceUtils.areSame(source, dest)) {
            ResourceUtils.log(project, "Skipping (self) copy of " + source + " to " + dest);
            return;
        }
        try (Reader in = ResourceUtils.filterWith(project, inputEncoding, filterChains, source.getInputStream());
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(ResourceUtils.getOutputStream(dest, append, project), ResourceUtils.charsetFor(outputEncoding)));){
            int nRead;
            char[] buffer = new char[8192];
            while ((nRead = in.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, nRead);
            }
        }
    }

    private static void copyUsingFileChannels(File sourceFile, File destFile, Project project) throws IOException {
        if (FileUtils.getFileUtils().areSame(sourceFile, destFile)) {
            ResourceUtils.log(project, "Skipping (self) copy of " + sourceFile + " to " + destFile);
            return;
        }
        File parent = destFile.getParentFile();
        if (!(parent == null || parent.isDirectory() || parent.mkdirs() || parent.isDirectory())) {
            throw new IOException("failed to create the parent directory for " + destFile);
        }
        try (FileChannel srcChannel = FileChannel.open(sourceFile.toPath(), StandardOpenOption.READ);
             FileChannel destChannel = FileChannel.open(destFile.toPath(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);){
            long chunk;
            long count = srcChannel.size();
            for (long position = 0L; position < count; position += destChannel.transferFrom(srcChannel, position, chunk)) {
                chunk = Math.min(0x1000000L, count - position);
            }
        }
    }

    private static void copyUsingStreams(Resource source, Resource dest, boolean append, Project project) throws IOException {
        if (ResourceUtils.areSame(source, dest)) {
            ResourceUtils.log(project, "Skipping (self) copy of " + source + " to " + dest);
            return;
        }
        try (InputStream in = source.getInputStream();
             OutputStream out = ResourceUtils.getOutputStream(dest, append, project);){
            byte[] buffer = new byte[8192];
            int count = 0;
            do {
                out.write(buffer, 0, count);
            } while ((count = in.read(buffer, 0, buffer.length)) != -1);
        }
    }

    private static OutputStream getOutputStream(Resource resource, boolean append, Project project) throws IOException {
        if (append) {
            Appendable a = resource.as(Appendable.class);
            if (a != null) {
                return a.getAppendOutputStream();
            }
            String msg = "Appendable OutputStream not available for non-appendable resource " + resource + "; using plain OutputStream";
            if (project != null) {
                project.log(msg, 3);
            } else {
                System.out.println(msg);
            }
        }
        return resource.getOutputStream();
    }

    private static boolean areSame(Resource resource1, Resource resource2) throws IOException {
        if (resource1 == null || resource2 == null) {
            return false;
        }
        FileProvider fileResource1 = resource1.as(FileProvider.class);
        FileProvider fileResource2 = resource2.as(FileProvider.class);
        return fileResource1 != null && fileResource2 != null && FileUtils.getFileUtils().areSame(fileResource1.getFile(), fileResource2.getFile());
    }

    private static void log(Project project, String message) {
        ResourceUtils.log(project, message, 3);
    }

    private static void log(Project project, String message, int level) {
        if (project == null) {
            System.out.println(message);
        } else {
            project.log(message, level);
        }
    }

    public static interface ResourceSelectorProvider {
        public ResourceSelector getTargetSelectorForSource(Resource var1);
    }

    public static class ReadOnlyTargetFileException
    extends IOException {
        private static final long serialVersionUID = 1L;

        public ReadOnlyTargetFileException(File destFile) {
            super("can't write to read-only destination file " + destFile);
        }
    }
}

