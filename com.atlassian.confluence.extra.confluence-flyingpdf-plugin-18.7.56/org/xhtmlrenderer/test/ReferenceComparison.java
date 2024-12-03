/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.swing.BoxRenderer;
import org.xhtmlrenderer.test.Regress;
import org.xhtmlrenderer.util.IOUtil;

public class ReferenceComparison {
    private int width;
    private boolean isVerbose;
    private static final String LINE_SEPARATOR = "\n";

    public static void main(String[] args) throws IOException {
        ReferenceComparison rc = new ReferenceComparison(1024, false);
        File source = new File(args[0]);
        File reference = new File(args[1]);
        File failed = new File(args[2]);
        rc.compareDirectory(source, reference, failed);
    }

    public ReferenceComparison(int width, boolean verbose) {
        this.width = width;
        this.isVerbose = verbose;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void compareDirectory(File sourceDirectory, File referenceDir, File failedDirectory) throws IOException {
        this.checkDirectories(sourceDirectory, referenceDir, failedDirectory);
        this.log("Starting comparison using width " + this.width);
        IOUtil.deleteAllFiles(failedDirectory);
        boolean wasEnabled = this.enableLogging(false);
        try {
            Iterator fileIt = this.listSourceFiles(sourceDirectory);
            CompareStatistics stats = new CompareStatistics();
            while (fileIt.hasNext()) {
                File file = (File)fileIt.next();
                try {
                    this.compareFile(file, referenceDir, failedDirectory, stats);
                }
                catch (IOException e) {
                    stats.failedIOException(e);
                }
            }
            stats.report();
        }
        finally {
            this.enableLogging(wasEnabled);
        }
    }

    private boolean enableLogging(boolean isEnabled) {
        String prop = "xr.util-logging.loggingEnabled";
        boolean orgVal = Boolean.valueOf(System.getProperty("xr.util-logging.loggingEnabled"));
        System.setProperty("xr.util-logging.loggingEnabled", Boolean.valueOf(isEnabled).toString());
        return orgVal;
    }

    private void checkDirectories(File sourceDirectory, File referenceDir, File failedDirectory) {
        if (!sourceDirectory.exists() || !sourceDirectory.isDirectory()) {
            throw new IllegalArgumentException("Source dir. doesn't exist, or not a directory: " + sourceDirectory);
        }
        if (!referenceDir.exists() || !referenceDir.isDirectory()) {
            throw new IllegalArgumentException("Reference dir. doesn't exist, or not a directory: " + referenceDir);
        }
        if (failedDirectory.exists() && !failedDirectory.isDirectory()) {
            throw new IllegalArgumentException("Need directory for failed matches, not a directory: " + failedDirectory);
        }
        if (!failedDirectory.exists() && !failedDirectory.mkdirs()) {
            throw new RuntimeException("Could not create directory path (.mkdirs failed without an exception) " + failedDirectory.getAbsolutePath());
        }
    }

    private boolean verbose() {
        return this.isVerbose;
    }

    private Iterator listSourceFiles(File sourceDirectory) {
        return Arrays.asList(sourceDirectory.listFiles(new FilenameFilter(){

            @Override
            public boolean accept(File file, String s) {
                return Regress.EXTENSIONS.contains(s.substring(s.lastIndexOf(".") + 1));
            }
        })).iterator();
    }

    public void compareFile(File source, File referenceDir, File failedDirectory, CompareStatistics stat) throws IOException {
        String laidOut;
        String refLaidOut;
        Box box;
        this.log("Comparing " + source.getPath());
        stat.checking(source);
        BoxRenderer renderer = new BoxRenderer(source, this.width);
        try {
            this.log("rendering");
            box = renderer.render();
            this.log("rendered");
        }
        catch (Exception e) {
            e.printStackTrace();
            stat.failedToRender(e);
            this.storeFailed(failedDirectory, source);
            this.log("Could not render input file, skipping: " + source + " err: " + e.getMessage());
            return;
        }
        LayoutContext layoutContext = renderer.getLayoutContext();
        String inputFileName = source.getName();
        String refRendered = this.trimTrailingLS(this.readReference(referenceDir, inputFileName, ".render.txt"));
        String rendered = this.trimTrailingLS(box.dump(layoutContext, "", 2));
        if (!this.compareLines(refRendered, rendered, stat)) {
            this.storeFailed(failedDirectory, new File(referenceDir, inputFileName), ".render.txt", rendered);
        }
        if (!this.compareLines(refLaidOut = this.trimTrailingLS(this.readReference(referenceDir, inputFileName, ".layout.txt")), laidOut = this.trimTrailingLS(box.dump(layoutContext, "", 1)), stat)) {
            this.storeFailed(failedDirectory, new File(referenceDir, inputFileName), ".layout.txt", laidOut);
        }
    }

    private String trimTrailingLS(String s) {
        if (s.endsWith(LINE_SEPARATOR)) {
            s = s.substring(0, s.length() - LINE_SEPARATOR.length());
        }
        return s;
    }

    private void storeFailed(File failedDirectory, File refFile, String suffix, String compareTo) {
        this.copyToFailed(failedDirectory, refFile, "");
        this.copyToFailed(failedDirectory, refFile, ".png");
        this.copyToFailed(failedDirectory, refFile, suffix);
        OutputStreamWriter fw = null;
        try {
            fw = new OutputStreamWriter((OutputStream)new FileOutputStream(new File(failedDirectory, refFile.getName() + ".err" + suffix)), "UTF-8");
            BufferedWriter bw = new BufferedWriter(fw);
            try {
                bw.write(compareTo);
                bw.flush();
            }
            catch (IOException e) {
                throw new RuntimeException("unexpected IO exception on writing 'failed' info for test.", e);
            }
            finally {
                try {
                    bw.close();
                }
                catch (IOException iOException) {}
            }
        }
        catch (IOException e) {
            throw new RuntimeException("unexpected IO exception on writing 'failed' info for test.", e);
        }
        finally {
            if (fw != null) {
                try {
                    fw.close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    private void copyToFailed(File failedDirectory, File refFile, String suffix) {
        File source = new File(failedDirectory, refFile.getName() + suffix);
        if (!source.exists()) {
            source = new File(refFile.getAbsoluteFile().getParentFile(), refFile.getName() + suffix);
            try {
                IOUtil.copyFile(source, failedDirectory);
            }
            catch (IOException e) {
                System.err.println("Failed to copy file (reference) " + source + " to failed directory, err " + e.getMessage());
            }
        }
    }

    private boolean compareLines(String refText, String text, CompareStatistics statistics) throws IOException {
        String lineRef;
        this.log("running comparison");
        LineNumberReader lnrRef = new LineNumberReader(new StringReader(refText));
        LineNumberReader lnrOther = new LineNumberReader(new StringReader(text));
        while ((lineRef = lnrRef.readLine()) != null) {
            String lineOther = lnrOther.readLine();
            if (lineOther == null) {
                statistics.failedRefIsLonger();
                return false;
            }
            if (lineRef.equals(lineOther)) continue;
            statistics.failedDontMatch(lineRef, lineOther);
            return false;
        }
        if (lnrOther.readLine() != null) {
            statistics.failedOtherIsLonger();
            return false;
        }
        return true;
    }

    private void storeFailed(File failedDirectory, File sourceFile) {
        try {
            IOUtil.copyFile(sourceFile, failedDirectory);
        }
        catch (IOException e) {
            System.err.println("Failed to copy file to failed directory: " + sourceFile + ", err: " + e.getMessage());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String readReference(File referenceDir, String input, String sfx) throws IOException {
        StringBuffer sb;
        BufferedReader rdr = null;
        File f = new File(referenceDir, input + sfx);
        rdr = new BufferedReader(new InputStreamReader((InputStream)new FileInputStream(f), "UTF-8"));
        try {
            String line;
            sb = new StringBuffer();
            while ((line = rdr.readLine()) != null) {
                sb.append(line);
                sb.append(LINE_SEPARATOR);
            }
        }
        finally {
            try {
                rdr.close();
            }
            catch (IOException iOException) {}
        }
        return sb.toString();
    }

    private void log(String msg) {
        if (this.verbose()) {
            System.out.println(msg);
        }
    }

    private static class CompareStatistics {
        private File currentFile;
        private static final Result OK = new ResultOK();
        private Map files = new HashMap();

        public void failedToRender(Exception e) {
            this.files.put(this.currentFile, new RenderFailed(e));
        }

        public void failedRefIsLonger() {
            this.files.put(this.currentFile, new RefIsLonger());
        }

        public void failedDontMatch(String lineRef, String lineOther) {
            this.files.put(this.currentFile, new LineMismatch(lineRef, lineOther));
        }

        public void failedOtherIsLonger() {
            this.files.put(this.currentFile, new OtherIsLonger());
        }

        public void failedIOException(IOException e) {
            this.files.put(this.currentFile, new FailedIO(e));
        }

        public boolean failed() {
            return this.files.get(this.currentFile) instanceof FailedResult;
        }

        public void checking(File source) {
            this.currentFile = source;
            this.files.put(this.currentFile, OK);
        }

        public boolean succeeded() {
            return this.files.get(this.currentFile) instanceof ResultOK;
        }

        public void report() {
            int failed = 0;
            for (File file : this.files.keySet()) {
                Result result = (Result)this.files.get(file);
                if (!(result instanceof FailedResult)) continue;
                ++failed;
                System.out.println(result.describe(file));
            }
            System.out.println("Checked " + this.files.keySet().size() + " files, " + (failed > 0 ? failed + " failed." : "all OK."));
        }

        private static class ResultOK
        implements Result {
            private ResultOK() {
            }

            @Override
            public String describe(File file) {
                return "OK: " + file.getName();
            }
        }

        private static interface FailedResult
        extends Result {
        }

        private static interface Result {
            public String describe(File var1);
        }

        private static class FailedIO
        implements FailedResult {
            private final IOException exception;

            public FailedIO(IOException e) {
                this.exception = e;
            }

            @Override
            public String describe(File file) {
                return "FAIL: IOException when comparing: " + file + " (err: " + this.exception.getMessage();
            }
        }

        private static class OtherIsLonger
        implements FailedResult {
            private OtherIsLonger() {
            }

            @Override
            public String describe(File file) {
                return "FAIL: new rendered output is longer (more lines): " + file.getName();
            }
        }

        private static class LineMismatch
        implements FailedResult {
            private final String lineRef;
            private final String lineOther;

            public LineMismatch(String lineRef, String lineOther) {
                this.lineRef = lineRef;
                this.lineOther = lineOther;
            }

            @Override
            public String describe(File file) {
                return "FAIL: line content doesn't match for " + file.getName() + ReferenceComparison.LINE_SEPARATOR + "ref: " + this.lineRef + ReferenceComparison.LINE_SEPARATOR + "other: " + this.lineOther;
            }
        }

        private static class RefIsLonger
        implements FailedResult {
            private RefIsLonger() {
            }

            @Override
            public String describe(File file) {
                return "FAIL: reference is longer (more lines): " + file.getName();
            }
        }

        private static class RenderFailed
        implements Result {
            private final Exception exception;

            public RenderFailed(Exception exception) {
                this.exception = exception;
            }

            @Override
            public String describe(File file) {
                return "FAIL: Render operation threw exception for " + file.getName() + ", err " + this.exception.getMessage();
            }
        }
    }
}

