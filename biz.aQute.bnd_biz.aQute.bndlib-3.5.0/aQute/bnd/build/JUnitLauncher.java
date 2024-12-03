/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package aQute.bnd.build;

import aQute.bnd.build.Classpath;
import aQute.bnd.build.Container;
import aQute.bnd.build.Project;
import aQute.bnd.build.ProjectLauncher;
import aQute.bnd.osgi.Processor;
import aQute.libg.command.Command;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JUnitLauncher
extends ProjectLauncher {
    private static final Logger logger = LoggerFactory.getLogger(JUnitLauncher.class);
    boolean junit4Main;
    final Project project;
    private Classpath cp;
    private Command java;
    private long timeout;
    private List<String> fqns = new ArrayList<String>();

    public JUnitLauncher(Project project) throws Exception {
        super(project);
        this.project = project;
    }

    @Override
    public void prepare() throws Exception {
        Pattern tests = Pattern.compile(this.project.getProperty("-testsources", "(.*).java"));
        String testDirName = this.project.getProperty("testsrc", "test");
        File testSrc = this.project.getFile(testDirName).getAbsoluteFile();
        if (!testSrc.isDirectory()) {
            logger.debug("no test src directory");
            return;
        }
        if (!this.traverse(this.fqns, testSrc, "", tests)) {
            logger.debug("no test files found in {}", (Object)testSrc);
            return;
        }
        this.timeout = Processor.getDuration(this.project.getProperty("-runtimeout"), 0L);
        this.cp = new Classpath(this.project, "junit");
        this.addClasspath(this.project.getTestpath());
        File output = this.project.getOutput();
        if (output.exists()) {
            this.addClasspath(new Container(this.project, output));
        }
        this.addClasspath(this.project.getBuildpath());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int launch() throws Exception {
        this.java = new Command();
        this.java.add(this.project.getProperty("java", "java"));
        this.java.add("-cp");
        this.java.add(this.cp.toString());
        this.java.addAll(this.project.getRunVM());
        this.java.add(this.getMainTypeName());
        this.java.addAll(this.fqns);
        if (this.timeout != 0L) {
            this.java.setTimeout(this.timeout + 1000L, TimeUnit.MILLISECONDS);
        }
        logger.debug("cmd line {}", (Object)this.java);
        try {
            int result = this.java.execute(System.in, (Appendable)System.err, (Appendable)System.err);
            if (result == Integer.MIN_VALUE) {
                int n = -3;
                return n;
            }
            this.reportResult(result);
            int n = result;
            return n;
        }
        finally {
            this.cleanup();
        }
    }

    private boolean traverse(List<String> fqns, File testSrc, String prefix, Pattern filter) {
        String name;
        Matcher m;
        boolean added = false;
        if (testSrc.isDirectory()) {
            int i$ = 0;
            File[] arr$ = testSrc.listFiles();
            int len$ = arr$.length;
            if (i$ < len$) {
                File sub = arr$[i$];
                return this.traverse(fqns, sub, prefix + sub.getName() + ".", filter) || added;
            }
        } else if (testSrc.isFile() && (m = filter.matcher(name = testSrc.getName())).matches()) {
            fqns.add(m.group(1));
            added = true;
        }
        return added;
    }

    @Override
    public String getMainTypeName() {
        return "aQute.junit.Activator";
    }

    @Override
    public void update() throws Exception {
    }
}

