/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.FileScanner;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceFactory;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.selectors.FileSelector;
import org.apache.tools.ant.types.selectors.SelectorScanner;
import org.apache.tools.ant.types.selectors.SelectorUtils;
import org.apache.tools.ant.types.selectors.TokenizedPath;
import org.apache.tools.ant.types.selectors.TokenizedPattern;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.VectorSet;

public class DirectoryScanner
implements FileScanner,
SelectorScanner,
ResourceFactory {
    private static final boolean ON_VMS = Os.isFamily("openvms");
    @Deprecated
    protected static final String[] DEFAULTEXCLUDES = new String[]{"**/*~", "**/#*#", "**/.#*", "**/%*%", "**/._*", "**/CVS", "**/CVS/**", "**/.cvsignore", "**/SCCS", "**/SCCS/**", "**/vssver.scc", "**/.svn", "**/.svn/**", "**/.git", "**/.git/**", "**/.gitattributes", "**/.gitignore", "**/.gitmodules", "**/.hg", "**/.hg/**", "**/.hgignore", "**/.hgsub", "**/.hgsubstate", "**/.hgtags", "**/.bzr", "**/.bzr/**", "**/.bzrignore", "**/.DS_Store"};
    public static final int MAX_LEVELS_OF_SYMLINKS = 5;
    public static final String DOES_NOT_EXIST_POSTFIX = " does not exist.";
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private static final Set<String> defaultExcludes = new HashSet<String>();
    protected File basedir;
    protected String[] includes;
    protected String[] excludes;
    protected FileSelector[] selectors = null;
    protected Vector<String> filesIncluded;
    protected Vector<String> filesNotIncluded;
    protected Vector<String> filesExcluded;
    protected Vector<String> dirsIncluded;
    protected Vector<String> dirsNotIncluded;
    protected Vector<String> dirsExcluded;
    protected Vector<String> filesDeselected;
    protected Vector<String> dirsDeselected;
    protected boolean haveSlowResults = false;
    protected boolean isCaseSensitive = true;
    protected boolean errorOnMissingDir = true;
    private boolean followSymlinks = true;
    protected boolean everythingIncluded = true;
    private final Set<String> scannedDirs = new HashSet<String>();
    private final Map<String, TokenizedPath> includeNonPatterns = new HashMap<String, TokenizedPath>();
    private final Map<String, TokenizedPath> excludeNonPatterns = new HashMap<String, TokenizedPath>();
    private TokenizedPattern[] includePatterns;
    private TokenizedPattern[] excludePatterns;
    private boolean areNonPatternSetsReady = false;
    private boolean scanning = false;
    private final Object scanLock = new Object();
    private boolean slowScanning = false;
    private final Object slowScanLock = new Object();
    private IllegalStateException illegal = null;
    private int maxLevelsOfSymlinks = 5;
    private final Set<String> notFollowedSymlinks = new HashSet<String>();

    protected static boolean matchPatternStart(String pattern, String str) {
        return SelectorUtils.matchPatternStart(pattern, str);
    }

    protected static boolean matchPatternStart(String pattern, String str, boolean isCaseSensitive) {
        return SelectorUtils.matchPatternStart(pattern, str, isCaseSensitive);
    }

    protected static boolean matchPath(String pattern, String str) {
        return SelectorUtils.matchPath(pattern, str);
    }

    protected static boolean matchPath(String pattern, String str, boolean isCaseSensitive) {
        return SelectorUtils.matchPath(pattern, str, isCaseSensitive);
    }

    public static boolean match(String pattern, String str) {
        return SelectorUtils.match(pattern, str);
    }

    protected static boolean match(String pattern, String str, boolean isCaseSensitive) {
        return SelectorUtils.match(pattern, str, isCaseSensitive);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String[] getDefaultExcludes() {
        Set<String> set = defaultExcludes;
        synchronized (set) {
            return defaultExcludes.toArray(new String[0]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean addDefaultExclude(String s) {
        Set<String> set = defaultExcludes;
        synchronized (set) {
            return defaultExcludes.add(s);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean removeDefaultExclude(String s) {
        Set<String> set = defaultExcludes;
        synchronized (set) {
            return defaultExcludes.remove(s);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void resetDefaultExcludes() {
        Set<String> set = defaultExcludes;
        synchronized (set) {
            defaultExcludes.clear();
            Collections.addAll(defaultExcludes, DEFAULTEXCLUDES);
        }
    }

    @Override
    public void setBasedir(String basedir) {
        this.setBasedir(basedir == null ? null : new File(basedir.replace('/', File.separatorChar).replace('\\', File.separatorChar)));
    }

    @Override
    public synchronized void setBasedir(File basedir) {
        this.basedir = basedir;
    }

    @Override
    public synchronized File getBasedir() {
        return this.basedir;
    }

    public synchronized boolean isCaseSensitive() {
        return this.isCaseSensitive;
    }

    @Override
    public synchronized void setCaseSensitive(boolean isCaseSensitive) {
        this.isCaseSensitive = isCaseSensitive;
    }

    public void setErrorOnMissingDir(boolean errorOnMissingDir) {
        this.errorOnMissingDir = errorOnMissingDir;
    }

    public synchronized boolean isFollowSymlinks() {
        return this.followSymlinks;
    }

    public synchronized void setFollowSymlinks(boolean followSymlinks) {
        this.followSymlinks = followSymlinks;
    }

    public void setMaxLevelsOfSymlinks(int max) {
        this.maxLevelsOfSymlinks = max;
    }

    @Override
    public synchronized void setIncludes(String[] includes) {
        this.includes = includes == null ? null : (String[])Stream.of(includes).map(DirectoryScanner::normalizePattern).toArray(String[]::new);
    }

    @Override
    public synchronized void setExcludes(String[] excludes) {
        this.excludes = excludes == null ? null : (String[])Stream.of(excludes).map(DirectoryScanner::normalizePattern).toArray(String[]::new);
    }

    public synchronized void addExcludes(String[] excludes) {
        if (excludes != null && excludes.length > 0) {
            if (this.excludes == null || this.excludes.length == 0) {
                this.setExcludes(excludes);
            } else {
                this.excludes = (String[])Stream.concat(Stream.of(this.excludes), Stream.of(excludes).map(DirectoryScanner::normalizePattern)).toArray(String[]::new);
            }
        }
    }

    private static String normalizePattern(String p) {
        String pattern = p.replace('/', File.separatorChar).replace('\\', File.separatorChar);
        if (pattern.endsWith(File.separator)) {
            pattern = pattern + "**";
        }
        return pattern;
    }

    @Override
    public synchronized void setSelectors(FileSelector[] selectors) {
        this.selectors = selectors;
    }

    public synchronized boolean isEverythingIncluded() {
        return this.everythingIncluded;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    @Override
    public void scan() throws IllegalStateException {
        block42: {
            var1_1 = this.scanLock;
            synchronized (var1_1) {
                if (this.scanning) {
                    while (this.scanning) {
                        try {
                            this.scanLock.wait();
                        }
                        catch (InterruptedException var2_2) {}
                    }
                    if (this.illegal != null) {
                        throw this.illegal;
                    }
                    return;
                }
                this.scanning = true;
            }
            savedBase = this.basedir;
            var2_3 = this;
            synchronized (var2_3) {
                this.illegal = null;
                this.clearResults();
                v0 = nullIncludes = this.includes == null;
                if (nullIncludes) {
                    v1 = new String[1];
                    v2 = v1;
                    v1[0] = "**";
                } else {
                    v2 = this.includes;
                }
                this.includes = v2;
                nullExcludes = this.excludes == null;
                v3 = this.excludes = nullExcludes != false ? new String[]{} : this.excludes;
                if (this.basedir != null && !this.followSymlinks && Files.isSymbolicLink(this.basedir.toPath())) {
                    this.notFollowedSymlinks.add(this.basedir.getAbsolutePath());
                    this.basedir = null;
                }
                if (this.basedir != null) ** break block40
                if (!nullIncludes) ** GOTO lbl77
                // MONITOREXIT @DISABLED, blocks:[2, 3, 12] lbl44 : MonitorExitStatement: MONITOREXIT : var2_3
                this.basedir = savedBase;
                var5_7 = this.scanLock;
            }
            synchronized (var5_7) {
                this.scanning = false;
                this.scanLock.notifyAll();
            }
            return;
            {
                block43: {
                    if (this.basedir.exists()) ** break block41
                    if (!this.errorOnMissingDir) break block43;
                    this.illegal = new IllegalStateException("basedir " + this.basedir + " does not exist.");
                    ** GOTO lbl75
                }
                // MONITOREXIT @DISABLED, blocks:[5, 12] lbl62 : MonitorExitStatement: MONITOREXIT : var2_3
                this.basedir = savedBase;
                var5_8 = this.scanLock;
            }
            synchronized (var5_8) {
                this.scanning = false;
                this.scanLock.notifyAll();
            }
            return;
            {
                if (!this.basedir.isDirectory()) {
                    this.illegal = new IllegalStateException("basedir " + this.basedir + " is not a directory.");
                }
lbl75:
                // 4 sources

                if (this.illegal != null) {
                    throw this.illegal;
                }
lbl77:
                // 3 sources

                if (this.isIncluded(TokenizedPath.EMPTY_PATH)) {
                    if (this.isExcluded(TokenizedPath.EMPTY_PATH)) {
                        this.dirsExcluded.addElement("");
                    } else if (this.isSelected("", this.basedir)) {
                        this.dirsIncluded.addElement("");
                    } else {
                        this.dirsDeselected.addElement("");
                    }
                } else {
                    this.dirsNotIncluded.addElement("");
                }
                this.checkIncludePatterns();
                this.clearCaches();
                this.includes = nullIncludes != false ? null : this.includes;
                this.excludes = nullExcludes != false ? null : this.excludes;
                break block42;
                {
                    catch (Throwable var8_11) {
                        throw var8_11;
                    }
                }
            }
            {
                finally {
                    this.basedir = savedBase;
                    var2_3 = this.scanLock;
                    synchronized (var2_3) {
                        this.scanning = false;
                        this.scanLock.notifyAll();
                    }
                }
            }
        }
    }

    private void checkIncludePatterns() {
        this.ensureNonPatternSetsReady();
        HashMap<TokenizedPath, String> newroots = new HashMap<TokenizedPath, String>();
        for (TokenizedPattern includePattern : this.includePatterns) {
            String pattern = includePattern.toString();
            if (this.shouldSkipPattern(pattern)) continue;
            newroots.put(includePattern.rtrimWildcardTokens(), pattern);
        }
        for (Map.Entry entry : this.includeNonPatterns.entrySet()) {
            String pattern = (String)entry.getKey();
            if (this.shouldSkipPattern(pattern)) continue;
            newroots.put((TokenizedPath)entry.getValue(), pattern);
        }
        if (newroots.containsKey(TokenizedPath.EMPTY_PATH) && this.basedir != null) {
            this.scandir(this.basedir, "", true);
        } else {
            File canonBase = null;
            if (this.basedir != null) {
                try {
                    canonBase = this.basedir.getCanonicalFile();
                }
                catch (IOException iOException) {
                    throw new BuildException(iOException);
                }
            }
            for (Map.Entry entry : newroots.entrySet()) {
                File f;
                TokenizedPath currentPath = (TokenizedPath)entry.getKey();
                String currentelement = currentPath.toString();
                if (this.basedir == null && !FileUtils.isAbsolutePath(currentelement)) continue;
                File myfile = new File(this.basedir, currentelement);
                if (myfile.exists()) {
                    try {
                        String path;
                        String string = path = this.basedir == null ? myfile.getCanonicalPath() : FILE_UTILS.removeLeadingPath(canonBase, myfile.getCanonicalFile());
                        if ((!path.equals(currentelement) || ON_VMS) && (myfile = currentPath.findFile(this.basedir, true)) != null && this.basedir != null) {
                            currentelement = FILE_UTILS.removeLeadingPath(this.basedir, myfile);
                            if (!currentPath.toString().equals(currentelement)) {
                                currentPath = new TokenizedPath(currentelement);
                            }
                        }
                    }
                    catch (IOException ex) {
                        throw new BuildException(ex);
                    }
                }
                if (!(myfile != null && myfile.exists() || this.isCaseSensitive() || (f = currentPath.findFile(this.basedir, false)) == null || !f.exists())) {
                    currentelement = this.basedir == null ? f.getAbsolutePath() : FILE_UTILS.removeLeadingPath(this.basedir, f);
                    myfile = f;
                    currentPath = new TokenizedPath(currentelement);
                }
                if (myfile == null || !myfile.exists()) continue;
                if (!this.followSymlinks && currentPath.isSymlink(this.basedir)) {
                    this.accountForNotFollowedSymlink(currentPath, myfile);
                    continue;
                }
                if (myfile.isDirectory()) {
                    if (this.isIncluded(currentPath) && !currentelement.isEmpty()) {
                        this.accountForIncludedDir(currentPath, myfile, true);
                        continue;
                    }
                    this.scandir(myfile, currentPath, true);
                    continue;
                }
                if (!myfile.isFile()) continue;
                String originalpattern = (String)entry.getValue();
                boolean included = this.isCaseSensitive() ? originalpattern.equals(currentelement) : originalpattern.equalsIgnoreCase(currentelement);
                if (!included) continue;
                this.accountForIncludedFile(currentPath, myfile);
            }
        }
    }

    private boolean shouldSkipPattern(String pattern) {
        if (FileUtils.isAbsolutePath(pattern)) {
            return this.basedir != null && !SelectorUtils.matchPatternStart(pattern, this.basedir.getAbsolutePath(), this.isCaseSensitive());
        }
        return this.basedir == null;
    }

    protected synchronized void clearResults() {
        this.filesIncluded = new VectorSet<String>();
        this.filesNotIncluded = new VectorSet<String>();
        this.filesExcluded = new VectorSet<String>();
        this.filesDeselected = new VectorSet<String>();
        this.dirsIncluded = new VectorSet<String>();
        this.dirsNotIncluded = new VectorSet<String>();
        this.dirsExcluded = new VectorSet<String>();
        this.dirsDeselected = new VectorSet<String>();
        this.everythingIncluded = this.basedir != null;
        this.scannedDirs.clear();
        this.notFollowedSymlinks.clear();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void slowScan() {
        Object object = this.slowScanLock;
        synchronized (object) {
            if (this.haveSlowResults) {
                return;
            }
            if (this.slowScanning) {
                while (this.slowScanning) {
                    try {
                        this.slowScanLock.wait();
                    }
                    catch (InterruptedException interruptedException) {}
                }
                return;
            }
            this.slowScanning = true;
        }
        try {
            object = this;
            synchronized (object) {
                String[] stringArray;
                boolean nullIncludes;
                boolean bl = nullIncludes = this.includes == null;
                if (nullIncludes) {
                    String[] stringArray2 = new String[1];
                    stringArray = stringArray2;
                    stringArray2[0] = "**";
                } else {
                    stringArray = this.includes;
                }
                this.includes = stringArray;
                boolean nullExcludes = this.excludes == null;
                this.excludes = nullExcludes ? new String[]{} : this.excludes;
                Object[] excl = new String[this.dirsExcluded.size()];
                this.dirsExcluded.copyInto(excl);
                Object[] notIncl = new String[this.dirsNotIncluded.size()];
                this.dirsNotIncluded.copyInto(notIncl);
                this.ensureNonPatternSetsReady();
                this.processSlowScan((String[])excl);
                this.processSlowScan((String[])notIncl);
                this.clearCaches();
                this.includes = nullIncludes ? null : this.includes;
                this.excludes = nullExcludes ? null : this.excludes;
            }
        }
        finally {
            object = this.slowScanLock;
            synchronized (object) {
                this.haveSlowResults = true;
                this.slowScanning = false;
                this.slowScanLock.notifyAll();
            }
        }
    }

    private void processSlowScan(String[] arr) {
        for (String element : arr) {
            TokenizedPath path = new TokenizedPath(element);
            if (this.scanDuringFastScan(path)) continue;
            this.scandir(new File(this.basedir, element), path, false);
        }
    }

    private boolean scanDuringFastScan(TokenizedPath path) {
        return this.couldHoldIncluded(path) && !this.contentsExcluded(path);
    }

    protected void scandir(File dir, String vpath, boolean fast) {
        this.scandir(dir, new TokenizedPath(vpath), fast);
    }

    private void scandir(File dir, TokenizedPath path, boolean fast) {
        if (dir == null) {
            throw new BuildException("dir must not be null.");
        }
        String[] newfiles = dir.list();
        if (newfiles == null) {
            if (!dir.exists()) {
                throw new BuildException(dir + DOES_NOT_EXIST_POSTFIX);
            }
            if (!dir.isDirectory()) {
                throw new BuildException("%s is not a directory.", dir);
            }
            throw new BuildException("IO error scanning directory '%s'", dir.getAbsolutePath());
        }
        this.scandir(dir, path, fast, newfiles, new LinkedList<String>());
    }

    private void scandir(File dir, TokenizedPath path, boolean fast, String[] newFiles, Deque<String> directoryNamesFollowed) {
        String vpath = path.toString();
        if (!vpath.isEmpty() && !vpath.endsWith(File.separator)) {
            vpath = vpath + File.separator;
        }
        if (fast && this.hasBeenScanned(vpath)) {
            return;
        }
        if (!this.followSymlinks) {
            ArrayList<String> noLinks = new ArrayList<String>();
            String[] stringArray = newFiles;
            int n = stringArray.length;
            for (int i = 0; i < n; ++i) {
                String newFile = stringArray[i];
                Path filePath = dir == null ? Paths.get(newFile, new String[0]) : Paths.get(dir.toPath().toString(), newFile);
                if (Files.isSymbolicLink(filePath)) {
                    String name = vpath + newFile;
                    File file = new File(dir, newFile);
                    if (file.isDirectory()) {
                        this.dirsExcluded.addElement(name);
                    } else if (file.isFile()) {
                        this.filesExcluded.addElement(name);
                    }
                    this.accountForNotFollowedSymlink(name, file);
                    continue;
                }
                noLinks.add(newFile);
            }
            newFiles = noLinks.toArray(new String[0]);
        } else {
            directoryNamesFollowed.addFirst(dir.getName());
        }
        for (String newFile : newFiles) {
            String name = vpath + newFile;
            TokenizedPath newPath = new TokenizedPath(path, newFile);
            File file = new File(dir, newFile);
            String[] children = fast && file.isDirectory() && !this.scanDuringFastScan(newPath) ? new String[]{} : file.list();
            if (children == null || children.length == 0 && file.isFile()) {
                if (this.isIncluded(newPath)) {
                    this.accountForIncludedFile(newPath, file);
                    continue;
                }
                this.everythingIncluded = false;
                this.filesNotIncluded.addElement(name);
                continue;
            }
            if (!file.isDirectory()) continue;
            if (this.followSymlinks && this.causesIllegalSymlinkLoop(newFile, dir, directoryNamesFollowed)) {
                System.err.println("skipping symbolic link " + file.getAbsolutePath() + " -- too many levels of symbolic links.");
                this.notFollowedSymlinks.add(file.getAbsolutePath());
                continue;
            }
            if (this.isIncluded(newPath)) {
                this.accountForIncludedDir(newPath, file, fast, children, directoryNamesFollowed);
            } else {
                this.everythingIncluded = false;
                this.dirsNotIncluded.addElement(name);
                if (fast && this.scanDuringFastScan(newPath)) {
                    this.scandir(file, newPath, fast, children, directoryNamesFollowed);
                }
            }
            if (fast) continue;
            this.scandir(file, newPath, fast, children, directoryNamesFollowed);
        }
        if (this.followSymlinks) {
            directoryNamesFollowed.removeFirst();
        }
    }

    private void accountForIncludedFile(TokenizedPath name, File file) {
        this.processIncluded(name, file, this.filesIncluded, this.filesExcluded, this.filesDeselected);
    }

    private void accountForIncludedDir(TokenizedPath name, File file, boolean fast) {
        this.processIncluded(name, file, this.dirsIncluded, this.dirsExcluded, this.dirsDeselected);
        if (fast && this.scanDuringFastScan(name)) {
            this.scandir(file, name, fast);
        }
    }

    private void accountForIncludedDir(TokenizedPath name, File file, boolean fast, String[] children, Deque<String> directoryNamesFollowed) {
        this.processIncluded(name, file, this.dirsIncluded, this.dirsExcluded, this.dirsDeselected);
        if (fast && this.scanDuringFastScan(name)) {
            this.scandir(file, name, fast, children, directoryNamesFollowed);
        }
    }

    private void accountForNotFollowedSymlink(String name, File file) {
        this.accountForNotFollowedSymlink(new TokenizedPath(name), file);
    }

    private void accountForNotFollowedSymlink(TokenizedPath name, File file) {
        if (!this.isExcluded(name) && (this.isIncluded(name) || file.isDirectory() && this.scanDuringFastScan(name))) {
            this.notFollowedSymlinks.add(file.getAbsolutePath());
        }
    }

    private void processIncluded(TokenizedPath path, File file, List<String> inc, List<String> exc, List<String> des) {
        String name = path.toString();
        if (inc.contains(name) || exc.contains(name) || des.contains(name)) {
            return;
        }
        boolean included = false;
        if (this.isExcluded(path)) {
            exc.add(name);
        } else if (this.isSelected(name, file)) {
            included = true;
            inc.add(name);
        } else {
            des.add(name);
        }
        this.everythingIncluded &= included;
    }

    protected boolean isIncluded(String name) {
        return this.isIncluded(new TokenizedPath(name));
    }

    private boolean isIncluded(TokenizedPath path) {
        this.ensureNonPatternSetsReady();
        String toMatch = path.toString();
        if (!this.isCaseSensitive()) {
            toMatch = toMatch.toUpperCase();
        }
        return this.includeNonPatterns.containsKey(toMatch) || Stream.of(this.includePatterns).anyMatch(p -> p.matchPath(path, this.isCaseSensitive()));
    }

    protected boolean couldHoldIncluded(String name) {
        return this.couldHoldIncluded(new TokenizedPath(name));
    }

    private boolean couldHoldIncluded(TokenizedPath tokenizedName) {
        return Stream.concat(Stream.of(this.includePatterns), this.includeNonPatterns.values().stream().map(TokenizedPath::toPattern)).anyMatch(pat -> this.couldHoldIncluded(tokenizedName, (TokenizedPattern)pat));
    }

    private boolean couldHoldIncluded(TokenizedPath tokenizedName, TokenizedPattern tokenizedInclude) {
        return tokenizedInclude.matchStartOf(tokenizedName, this.isCaseSensitive()) && this.isMorePowerfulThanExcludes(tokenizedName.toString()) && this.isDeeper(tokenizedInclude, tokenizedName);
    }

    private boolean isDeeper(TokenizedPattern pattern, TokenizedPath name) {
        return pattern.containsPattern("**") || pattern.depth() > name.depth();
    }

    private boolean isMorePowerfulThanExcludes(String name) {
        String soughtexclude = name + File.separatorChar + "**";
        return Stream.of(this.excludePatterns).map(Object::toString).noneMatch(Predicate.isEqual(soughtexclude));
    }

    boolean contentsExcluded(TokenizedPath path) {
        return Stream.of(this.excludePatterns).filter(p -> p.endsWith("**")).map(TokenizedPattern::withoutLastToken).anyMatch(wlt -> wlt.matchPath(path, this.isCaseSensitive()));
    }

    protected boolean isExcluded(String name) {
        return this.isExcluded(new TokenizedPath(name));
    }

    private boolean isExcluded(TokenizedPath name) {
        this.ensureNonPatternSetsReady();
        String toMatch = name.toString();
        if (!this.isCaseSensitive()) {
            toMatch = toMatch.toUpperCase();
        }
        return this.excludeNonPatterns.containsKey(toMatch) || Stream.of(this.excludePatterns).anyMatch(p -> p.matchPath(name, this.isCaseSensitive()));
    }

    protected boolean isSelected(String name, File file) {
        return this.selectors == null || Stream.of(this.selectors).allMatch(sel -> sel.isSelected(this.basedir, name, file));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String[] getIncludedFiles() {
        Object[] files;
        DirectoryScanner directoryScanner = this;
        synchronized (directoryScanner) {
            if (this.filesIncluded == null) {
                throw new IllegalStateException("Must call scan() first");
            }
            files = this.filesIncluded.toArray(new String[0]);
        }
        Arrays.sort(files);
        return files;
    }

    public synchronized int getIncludedFilesCount() {
        if (this.filesIncluded == null) {
            throw new IllegalStateException("Must call scan() first");
        }
        return this.filesIncluded.size();
    }

    @Override
    public synchronized String[] getNotIncludedFiles() {
        this.slowScan();
        return this.filesNotIncluded.toArray(new String[0]);
    }

    @Override
    public synchronized String[] getExcludedFiles() {
        this.slowScan();
        return this.filesExcluded.toArray(new String[0]);
    }

    @Override
    public synchronized String[] getDeselectedFiles() {
        this.slowScan();
        return this.filesDeselected.toArray(new String[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String[] getIncludedDirectories() {
        Object[] directories;
        DirectoryScanner directoryScanner = this;
        synchronized (directoryScanner) {
            if (this.dirsIncluded == null) {
                throw new IllegalStateException("Must call scan() first");
            }
            directories = this.dirsIncluded.toArray(new String[0]);
        }
        Arrays.sort(directories);
        return directories;
    }

    public synchronized int getIncludedDirsCount() {
        if (this.dirsIncluded == null) {
            throw new IllegalStateException("Must call scan() first");
        }
        return this.dirsIncluded.size();
    }

    @Override
    public synchronized String[] getNotIncludedDirectories() {
        this.slowScan();
        return this.dirsNotIncluded.toArray(new String[0]);
    }

    @Override
    public synchronized String[] getExcludedDirectories() {
        this.slowScan();
        return this.dirsExcluded.toArray(new String[0]);
    }

    @Override
    public synchronized String[] getDeselectedDirectories() {
        this.slowScan();
        return this.dirsDeselected.toArray(new String[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized String[] getNotFollowedSymlinks() {
        Object[] links;
        DirectoryScanner directoryScanner = this;
        synchronized (directoryScanner) {
            links = this.notFollowedSymlinks.toArray(new String[0]);
        }
        Arrays.sort(links);
        return links;
    }

    @Override
    public synchronized void addDefaultExcludes() {
        Stream<String> s = Stream.of(DirectoryScanner.getDefaultExcludes()).map(p -> p.replace('/', File.separatorChar).replace('\\', File.separatorChar));
        if (this.excludes != null) {
            s = Stream.concat(Stream.of(this.excludes), s);
        }
        this.excludes = (String[])s.toArray(String[]::new);
    }

    @Override
    public synchronized Resource getResource(String name) {
        return new FileResource(this.basedir, name);
    }

    private boolean hasBeenScanned(String vpath) {
        return !this.scannedDirs.add(vpath);
    }

    Set<String> getScannedDirs() {
        return this.scannedDirs;
    }

    private synchronized void clearCaches() {
        this.includeNonPatterns.clear();
        this.excludeNonPatterns.clear();
        this.includePatterns = null;
        this.excludePatterns = null;
        this.areNonPatternSetsReady = false;
    }

    synchronized void ensureNonPatternSetsReady() {
        if (!this.areNonPatternSetsReady) {
            this.includePatterns = this.fillNonPatternSet(this.includeNonPatterns, this.includes);
            this.excludePatterns = this.fillNonPatternSet(this.excludeNonPatterns, this.excludes);
            this.areNonPatternSetsReady = true;
        }
    }

    private TokenizedPattern[] fillNonPatternSet(Map<String, TokenizedPath> map, String[] patterns) {
        ArrayList<TokenizedPattern> al = new ArrayList<TokenizedPattern>(patterns.length);
        for (String pattern : patterns) {
            if (SelectorUtils.hasWildcards(pattern)) {
                al.add(new TokenizedPattern(pattern));
                continue;
            }
            String s = this.isCaseSensitive() ? pattern : pattern.toUpperCase();
            map.put(s, new TokenizedPath(s));
        }
        return al.toArray(new TokenizedPattern[0]);
    }

    private boolean causesIllegalSymlinkLoop(String dirName, File parent, Deque<String> directoryNamesFollowed) {
        try {
            Path dirPath = parent == null ? Paths.get(dirName, new String[0]) : Paths.get(parent.toPath().toString(), dirName);
            if (directoryNamesFollowed.size() >= this.maxLevelsOfSymlinks && Collections.frequency(directoryNamesFollowed, dirName) >= this.maxLevelsOfSymlinks && Files.isSymbolicLink(dirPath)) {
                ArrayList<String> files = new ArrayList<String>();
                File f = FILE_UTILS.resolveFile(parent, dirName);
                String target = f.getCanonicalPath();
                files.add(target);
                StringBuilder relPath = new StringBuilder();
                for (String dir : directoryNamesFollowed) {
                    relPath.append("../");
                    if (!dirName.equals(dir)) continue;
                    f = FILE_UTILS.resolveFile(parent, relPath + dir);
                    files.add(f.getCanonicalPath());
                    if (files.size() <= this.maxLevelsOfSymlinks || Collections.frequency(files, target) <= this.maxLevelsOfSymlinks) continue;
                    return true;
                }
            }
            return false;
        }
        catch (IOException ex) {
            throw new BuildException("Caught error while checking for symbolic links", ex);
        }
    }

    static {
        DirectoryScanner.resetDefaultExcludes();
    }
}

