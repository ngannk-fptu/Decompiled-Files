/*
 * Decompiled with CFR 0.152.
 */
package freemarker.cache;

import freemarker.cache.MruCacheStorage;
import freemarker.cache.TemplateLoader;
import freemarker.cache.TemplateLoaderUtils;
import freemarker.log.Logger;
import freemarker.template.utility.SecurityUtilities;
import freemarker.template.utility.StringUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public class FileTemplateLoader
implements TemplateLoader {
    public static String SYSTEM_PROPERTY_NAME_EMULATE_CASE_SENSITIVE_FILE_SYSTEM;
    private static final boolean EMULATE_CASE_SENSITIVE_FILE_SYSTEM_DEFAULT;
    private static final int CASE_CHECH_CACHE_HARD_SIZE = 50;
    private static final int CASE_CHECK_CACHE__SOFT_SIZE = 1000;
    private static final boolean SEP_IS_SLASH;
    private static final Logger LOG;
    public final File baseDir;
    private final String canonicalBasePath;
    private boolean emulateCaseSensitiveFileSystem;
    private MruCacheStorage correctCasePaths;

    @Deprecated
    public FileTemplateLoader() throws IOException {
        this(new File(SecurityUtilities.getSystemProperty("user.dir")));
    }

    public FileTemplateLoader(File baseDir) throws IOException {
        this(baseDir, false);
    }

    public FileTemplateLoader(final File baseDir, final boolean disableCanonicalPathCheck) throws IOException {
        try {
            Object[] retval = AccessController.doPrivileged(new PrivilegedExceptionAction<Object[]>(){

                @Override
                public Object[] run() throws IOException {
                    if (!baseDir.exists()) {
                        throw new FileNotFoundException(baseDir + " does not exist.");
                    }
                    if (!baseDir.isDirectory()) {
                        throw new IOException(baseDir + " is not a directory.");
                    }
                    Object[] retval = new Object[2];
                    if (disableCanonicalPathCheck) {
                        retval[0] = baseDir;
                        retval[1] = null;
                    } else {
                        retval[0] = baseDir.getCanonicalFile();
                        String basePath = ((File)retval[0]).getPath();
                        if (!basePath.endsWith(File.separator)) {
                            basePath = basePath + File.separatorChar;
                        }
                        retval[1] = basePath;
                    }
                    return retval;
                }
            });
            this.baseDir = (File)retval[0];
            this.canonicalBasePath = (String)retval[1];
            this.setEmulateCaseSensitiveFileSystem(this.getEmulateCaseSensitiveFileSystemDefault());
        }
        catch (PrivilegedActionException e) {
            throw (IOException)e.getException();
        }
    }

    @Override
    public Object findTemplateSource(final String name) throws IOException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<File>(){

                @Override
                public File run() throws IOException {
                    String normalized;
                    File source = new File(FileTemplateLoader.this.baseDir, SEP_IS_SLASH ? name : name.replace('/', File.separatorChar));
                    if (!source.isFile()) {
                        return null;
                    }
                    if (FileTemplateLoader.this.canonicalBasePath != null && !(normalized = source.getCanonicalPath()).startsWith(FileTemplateLoader.this.canonicalBasePath)) {
                        throw new SecurityException(source.getAbsolutePath() + " resolves to " + normalized + " which  doesn't start with " + FileTemplateLoader.this.canonicalBasePath);
                    }
                    if (FileTemplateLoader.this.emulateCaseSensitiveFileSystem && !FileTemplateLoader.this.isNameCaseCorrect(source)) {
                        return null;
                    }
                    return source;
                }
            });
        }
        catch (PrivilegedActionException e) {
            throw (IOException)e.getException();
        }
    }

    @Override
    public long getLastModified(final Object templateSource) {
        return AccessController.doPrivileged(new PrivilegedAction<Long>(){

            @Override
            public Long run() {
                return ((File)templateSource).lastModified();
            }
        });
    }

    @Override
    public Reader getReader(final Object templateSource, final String encoding) throws IOException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<Reader>(){

                @Override
                public Reader run() throws IOException {
                    if (!(templateSource instanceof File)) {
                        throw new IllegalArgumentException("templateSource wasn't a File, but a: " + templateSource.getClass().getName());
                    }
                    return new InputStreamReader((InputStream)new FileInputStream((File)templateSource), encoding);
                }
            });
        }
        catch (PrivilegedActionException e) {
            throw (IOException)e.getException();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean isNameCaseCorrect(File source) throws IOException {
        String sourcePath = source.getPath();
        MruCacheStorage mruCacheStorage = this.correctCasePaths;
        synchronized (mruCacheStorage) {
            if (this.correctCasePaths.get(sourcePath) != null) {
                return true;
            }
        }
        File parentDir = source.getParentFile();
        if (parentDir != null) {
            if (!this.baseDir.equals(parentDir) && !this.isNameCaseCorrect(parentDir)) {
                return false;
            }
            String[] listing = parentDir.list();
            if (listing != null) {
                int i;
                String fileName = source.getName();
                boolean identicalNameFound = false;
                for (i = 0; !identicalNameFound && i < listing.length; ++i) {
                    if (!fileName.equals(listing[i])) continue;
                    identicalNameFound = true;
                }
                if (!identicalNameFound) {
                    for (i = 0; i < listing.length; ++i) {
                        String listingEntry = listing[i];
                        if (!fileName.equalsIgnoreCase(listingEntry)) continue;
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Emulating file-not-found because of letter case differences to the real file, for: " + sourcePath);
                        }
                        return false;
                    }
                }
            }
        }
        MruCacheStorage mruCacheStorage2 = this.correctCasePaths;
        synchronized (mruCacheStorage2) {
            this.correctCasePaths.put(sourcePath, Boolean.TRUE);
        }
        return true;
    }

    @Override
    public void closeTemplateSource(Object templateSource) {
    }

    public File getBaseDirectory() {
        return this.baseDir;
    }

    public void setEmulateCaseSensitiveFileSystem(boolean nameCaseChecked) {
        if (nameCaseChecked) {
            if (this.correctCasePaths == null) {
                this.correctCasePaths = new MruCacheStorage(50, 1000);
            }
        } else {
            this.correctCasePaths = null;
        }
        this.emulateCaseSensitiveFileSystem = nameCaseChecked;
    }

    public boolean getEmulateCaseSensitiveFileSystem() {
        return this.emulateCaseSensitiveFileSystem;
    }

    protected boolean getEmulateCaseSensitiveFileSystemDefault() {
        return EMULATE_CASE_SENSITIVE_FILE_SYSTEM_DEFAULT;
    }

    public String toString() {
        return TemplateLoaderUtils.getClassNameForToString(this) + "(baseDir=\"" + this.baseDir + "\"" + (this.canonicalBasePath != null ? ", canonicalBasePath=\"" + this.canonicalBasePath + "\"" : "") + (this.emulateCaseSensitiveFileSystem ? ", emulateCaseSensitiveFileSystem=true" : "") + ")";
    }

    static {
        boolean emuCaseSensFS;
        SYSTEM_PROPERTY_NAME_EMULATE_CASE_SENSITIVE_FILE_SYSTEM = "org.freemarker.emulateCaseSensitiveFileSystem";
        String s = SecurityUtilities.getSystemProperty(SYSTEM_PROPERTY_NAME_EMULATE_CASE_SENSITIVE_FILE_SYSTEM, "false");
        try {
            emuCaseSensFS = StringUtil.getYesNo(s);
        }
        catch (Exception e) {
            emuCaseSensFS = false;
        }
        EMULATE_CASE_SENSITIVE_FILE_SYSTEM_DEFAULT = emuCaseSensFS;
        SEP_IS_SLASH = File.separatorChar == '/';
        LOG = Logger.getLogger("freemarker.cache");
    }
}

