/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  junit.framework.Test
 *  junit.framework.TestCase
 *  junit.framework.TestSuite
 */
package groovy.util;

import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;
import groovy.util.IFileNameFinder;
import java.io.File;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.logging.Logger;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.runtime.ScriptTestAdapter;

public class AllTestSuite
extends TestSuite {
    public static final String SYSPROP_TEST_DIR = "groovy.test.dir";
    public static final String SYSPROP_TEST_PATTERN = "groovy.test.pattern";
    public static final String SYSPROP_TEST_EXCLUDES_PATTERN = "groovy.test.excludesPattern";
    private static final Logger LOG = Logger.getLogger(AllTestSuite.class.getName());
    private static final ClassLoader JAVA_LOADER = AllTestSuite.class.getClassLoader();
    private static final GroovyClassLoader GROOVY_LOADER = AccessController.doPrivileged(new PrivilegedAction<GroovyClassLoader>(){

        @Override
        public GroovyClassLoader run() {
            return new GroovyClassLoader(JAVA_LOADER);
        }
    });
    private static final String[] EMPTY_ARGS = new String[0];
    private static IFileNameFinder finder = null;

    public static Test suite() {
        String basedir = System.getProperty(SYSPROP_TEST_DIR, "./test/");
        String pattern = System.getProperty(SYSPROP_TEST_PATTERN, "**/*Test.groovy");
        String excludesPattern = System.getProperty(SYSPROP_TEST_EXCLUDES_PATTERN, "");
        return AllTestSuite.suite(basedir, pattern, excludesPattern);
    }

    public static Test suite(String basedir, String pattern) {
        return AllTestSuite.suite(basedir, pattern, "");
    }

    public static Test suite(String basedir, String pattern, String excludesPattern) {
        AllTestSuite suite = new AllTestSuite();
        List<String> filenames = excludesPattern.length() > 0 ? finder.getFileNames(basedir, pattern, excludesPattern) : finder.getFileNames(basedir, pattern);
        for (String filename : filenames) {
            LOG.finest("trying to load " + filename);
            try {
                suite.loadTest(filename);
            }
            catch (CompilationFailedException cfe) {
                cfe.printStackTrace();
                throw new RuntimeException("CompilationFailedException when loading " + filename, cfe);
            }
            catch (IOException ioe) {
                throw new RuntimeException("IOException when loading " + filename, ioe);
            }
        }
        return suite;
    }

    protected void loadTest(String filename) throws CompilationFailedException, IOException {
        Class type = this.compile(filename);
        if (TestCase.class.isAssignableFrom(type)) {
            this.addTestSuite(type);
        } else if (Script.class.isAssignableFrom(type)) {
            this.addTest(new ScriptTestAdapter(type, EMPTY_ARGS));
        } else {
            throw new RuntimeException("Don't know how to treat " + filename + " as a JUnit test");
        }
    }

    protected Class compile(String filename) throws CompilationFailedException, IOException {
        return GROOVY_LOADER.parseClass(new File(filename));
    }

    static {
        try {
            Class<?> finderClass = Class.forName("groovy.util.FileNameFinder");
            finder = (IFileNameFinder)finderClass.newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException("Cannot find and instantiate class FileNameFinder", e);
        }
    }
}

