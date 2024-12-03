/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.i18n;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.LineTokenizer;

public class Translate
extends MatchingTask {
    private static final int BUNDLE_SPECIFIED_LANGUAGE_COUNTRY_VARIANT = 0;
    private static final int BUNDLE_SPECIFIED_LANGUAGE_COUNTRY = 1;
    private static final int BUNDLE_SPECIFIED_LANGUAGE = 2;
    private static final int BUNDLE_NOMATCH = 3;
    private static final int BUNDLE_DEFAULT_LANGUAGE_COUNTRY_VARIANT = 4;
    private static final int BUNDLE_DEFAULT_LANGUAGE_COUNTRY = 5;
    private static final int BUNDLE_DEFAULT_LANGUAGE = 6;
    private static final int BUNDLE_MAX_ALTERNATIVES = 7;
    private String bundle;
    private String bundleLanguage;
    private String bundleCountry;
    private String bundleVariant;
    private File toDir;
    private String srcEncoding;
    private String destEncoding;
    private String bundleEncoding;
    private String startToken;
    private String endToken;
    private boolean forceOverwrite;
    private List<FileSet> filesets = new Vector<FileSet>();
    private Map<String, String> resourceMap = new Hashtable<String, String>();
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private long[] bundleLastModified = new long[7];
    private long srcLastModified;
    private long destLastModified;
    private boolean loaded = false;

    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    public void setBundleLanguage(String bundleLanguage) {
        this.bundleLanguage = bundleLanguage;
    }

    public void setBundleCountry(String bundleCountry) {
        this.bundleCountry = bundleCountry;
    }

    public void setBundleVariant(String bundleVariant) {
        this.bundleVariant = bundleVariant;
    }

    public void setToDir(File toDir) {
        this.toDir = toDir;
    }

    public void setStartToken(String startToken) {
        this.startToken = startToken;
    }

    public void setEndToken(String endToken) {
        this.endToken = endToken;
    }

    public void setSrcEncoding(String srcEncoding) {
        this.srcEncoding = srcEncoding;
    }

    public void setDestEncoding(String destEncoding) {
        this.destEncoding = destEncoding;
    }

    public void setBundleEncoding(String bundleEncoding) {
        this.bundleEncoding = bundleEncoding;
    }

    public void setForceOverwrite(boolean forceOverwrite) {
        this.forceOverwrite = forceOverwrite;
    }

    public void addFileset(FileSet set) {
        this.filesets.add(set);
    }

    @Override
    public void execute() throws BuildException {
        Locale l;
        if (this.bundle == null) {
            throw new BuildException("The bundle attribute must be set.", this.getLocation());
        }
        if (this.startToken == null) {
            throw new BuildException("The starttoken attribute must be set.", this.getLocation());
        }
        if (this.endToken == null) {
            throw new BuildException("The endtoken attribute must be set.", this.getLocation());
        }
        if (this.bundleLanguage == null) {
            l = Locale.getDefault();
            this.bundleLanguage = l.getLanguage();
        }
        if (this.bundleCountry == null) {
            this.bundleCountry = Locale.getDefault().getCountry();
        }
        if (this.bundleVariant == null) {
            l = new Locale(this.bundleLanguage, this.bundleCountry);
            this.bundleVariant = l.getVariant();
        }
        if (this.toDir == null) {
            throw new BuildException("The todir attribute must be set.", this.getLocation());
        }
        if (!this.toDir.exists()) {
            this.toDir.mkdirs();
        } else if (this.toDir.isFile()) {
            throw new BuildException("%s is not a directory", this.toDir);
        }
        if (this.srcEncoding == null) {
            this.srcEncoding = System.getProperty("file.encoding");
        }
        if (this.destEncoding == null) {
            this.destEncoding = this.srcEncoding;
        }
        if (this.bundleEncoding == null) {
            this.bundleEncoding = this.srcEncoding;
        }
        this.loadResourceMaps();
        this.translate();
    }

    private void loadResourceMaps() throws BuildException {
        Locale locale = new Locale(this.bundleLanguage, this.bundleCountry, this.bundleVariant);
        String language = locale.getLanguage().isEmpty() ? "" : "_" + locale.getLanguage();
        String country = locale.getCountry().isEmpty() ? "" : "_" + locale.getCountry();
        String variant = locale.getVariant().isEmpty() ? "" : "_" + locale.getVariant();
        this.processBundle(this.bundle + language + country + variant, 0, false);
        this.processBundle(this.bundle + language + country, 1, false);
        this.processBundle(this.bundle + language, 2, false);
        this.processBundle(this.bundle, 3, false);
        locale = Locale.getDefault();
        language = locale.getLanguage().isEmpty() ? "" : "_" + locale.getLanguage();
        country = locale.getCountry().isEmpty() ? "" : "_" + locale.getCountry();
        variant = locale.getVariant().isEmpty() ? "" : "_" + locale.getVariant();
        this.bundleEncoding = System.getProperty("file.encoding");
        this.processBundle(this.bundle + language + country + variant, 4, false);
        this.processBundle(this.bundle + language + country, 5, false);
        this.processBundle(this.bundle + language, 6, true);
    }

    private void processBundle(String bundleFile, int i, boolean checkLoaded) throws BuildException {
        block2: {
            File propsFile = this.getProject().resolveFile(bundleFile + ".properties");
            InputStream ins = null;
            try {
                ins = Files.newInputStream(propsFile.toPath(), new OpenOption[0]);
                this.loaded = true;
                this.bundleLastModified[i] = propsFile.lastModified();
                this.log("Using " + propsFile, 4);
                this.loadResourceMap(ins);
            }
            catch (IOException ioe) {
                this.log(propsFile + " not found.", 4);
                if (this.loaded || !checkLoaded) break block2;
                throw new BuildException(ioe.getMessage(), this.getLocation());
            }
        }
    }

    private void loadResourceMap(InputStream ins) throws BuildException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(ins, this.bundleEncoding));){
            String line;
            while ((line = in.readLine()) != null) {
                if (line.trim().length() <= 1 || '#' == line.charAt(0) || '!' == line.charAt(0)) continue;
                int sepIndex = line.indexOf(61);
                if (-1 == sepIndex) {
                    sepIndex = line.indexOf(58);
                }
                if (-1 == sepIndex) {
                    for (int k = 0; k < line.length(); ++k) {
                        if (!Character.isSpaceChar(line.charAt(k))) continue;
                        sepIndex = k;
                        break;
                    }
                }
                if (-1 == sepIndex) continue;
                String key = line.substring(0, sepIndex).trim();
                String value = line.substring(sepIndex + 1).trim();
                while (value.endsWith("\\")) {
                    value = value.substring(0, value.length() - 1);
                    line = in.readLine();
                    if (line == null) break;
                    value = value + line.trim();
                }
                if (key.isEmpty()) continue;
                this.resourceMap.putIfAbsent(key, value);
            }
        }
        catch (IOException ioe) {
            throw new BuildException(ioe.getMessage(), this.getLocation());
        }
    }

    private void translate() throws BuildException {
        int filesProcessed = 0;
        for (FileSet fs : this.filesets) {
            DirectoryScanner ds = fs.getDirectoryScanner(this.getProject());
            for (String srcFile : ds.getIncludedFiles()) {
                try {
                    boolean needsWork;
                    File dest = FILE_UTILS.resolveFile(this.toDir, srcFile);
                    try {
                        File destDir = new File(dest.getParent());
                        if (!destDir.exists()) {
                            destDir.mkdirs();
                        }
                    }
                    catch (Exception e) {
                        this.log("Exception occurred while trying to check/create  parent directory.  " + e.getMessage(), 4);
                    }
                    this.destLastModified = dest.lastModified();
                    File src = FILE_UTILS.resolveFile(ds.getBasedir(), srcFile);
                    this.srcLastModified = src.lastModified();
                    boolean bl = needsWork = this.forceOverwrite || this.destLastModified < this.srcLastModified;
                    if (!needsWork) {
                        for (int icounter = 0; icounter < 7; ++icounter) {
                            boolean bl2 = needsWork = this.destLastModified < this.bundleLastModified[icounter];
                            if (needsWork) break;
                        }
                    }
                    if (needsWork) {
                        this.log("Processing " + srcFile, 4);
                        this.translateOneFile(src, dest);
                        ++filesProcessed;
                        continue;
                    }
                    this.log("Skipping " + srcFile + " as destination file is up to date", 3);
                }
                catch (IOException ioe) {
                    throw new BuildException(ioe.getMessage(), this.getLocation());
                }
            }
        }
        this.log("Translation performed on " + filesProcessed + " file(s).", 4);
    }

    private void translateOneFile(File src, File dest) throws IOException {
        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(dest.toPath(), new OpenOption[0]), this.destEncoding));
             BufferedReader in = new BufferedReader(new InputStreamReader(Files.newInputStream(src.toPath(), new OpenOption[0]), this.srcEncoding));){
            LineTokenizer lineTokenizer = new LineTokenizer();
            lineTokenizer.setIncludeDelims(true);
            String line = lineTokenizer.getToken(in);
            while (line != null) {
                int startIndex = line.indexOf(this.startToken);
                while (startIndex >= 0 && startIndex + this.startToken.length() <= line.length()) {
                    String replace = null;
                    int endIndex = line.indexOf(this.endToken, startIndex + this.startToken.length());
                    if (endIndex < 0) {
                        ++startIndex;
                    } else {
                        String token = line.substring(startIndex + this.startToken.length(), endIndex);
                        boolean validToken = true;
                        for (int k = 0; k < token.length() && validToken; ++k) {
                            char c = token.charAt(k);
                            if (c != ':' && c != '=' && !Character.isSpaceChar(c)) continue;
                            validToken = false;
                        }
                        if (!validToken) {
                            ++startIndex;
                        } else {
                            if (this.resourceMap.containsKey(token)) {
                                replace = this.resourceMap.get(token);
                            } else {
                                this.log("Replacement string missing for: " + token, 3);
                                replace = this.startToken + token + this.endToken;
                            }
                            line = line.substring(0, startIndex) + replace + line.substring(endIndex + this.endToken.length());
                            startIndex += replace.length();
                        }
                    }
                    startIndex = line.indexOf(this.startToken, startIndex);
                }
                out.write(line);
                line = lineTokenizer.getToken(in);
            }
        }
    }
}

