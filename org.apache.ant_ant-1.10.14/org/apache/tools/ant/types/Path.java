/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Stack;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.PathTokenizer;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileResourceIterator;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.JavaEnvUtils;

public class Path
extends DataType
implements Cloneable,
ResourceCollection {
    public static Path systemClasspath = new Path(null, System.getProperty("java.class.path"));
    public static final Path systemBootClasspath = new Path(null, System.getProperty("sun.boot.class.path"));
    private Boolean preserveBC;
    private Union union = null;
    private boolean cache = false;

    public Path(Project p, String path) {
        this(p);
        this.createPathElement().setPath(path);
    }

    public Path(Project project) {
        this.setProject(project);
    }

    public void setLocation(File location) throws BuildException {
        this.checkAttributesAllowed();
        this.createPathElement().setLocation(location);
    }

    public void setPath(String path) throws BuildException {
        this.checkAttributesAllowed();
        this.createPathElement().setPath(path);
    }

    @Override
    public void setRefid(Reference r) throws BuildException {
        if (this.union != null) {
            throw this.tooManyAttributes();
        }
        super.setRefid(r);
    }

    public PathElement createPathElement() throws BuildException {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        PathElement pe = new PathElement();
        this.add(pe);
        return pe;
    }

    public void addFileset(FileSet fs) throws BuildException {
        if (fs.getProject() == null) {
            fs.setProject(this.getProject());
        }
        this.add(fs);
    }

    public void addFilelist(FileList fl) throws BuildException {
        if (fl.getProject() == null) {
            fl.setProject(this.getProject());
        }
        this.add(fl);
    }

    public void addDirset(DirSet dset) throws BuildException {
        if (dset.getProject() == null) {
            dset.setProject(this.getProject());
        }
        this.add(dset);
    }

    public void add(Path path) throws BuildException {
        if (path == this) {
            throw this.circularReference();
        }
        if (path.getProject() == null) {
            path.setProject(this.getProject());
        }
        this.add((ResourceCollection)path);
    }

    public void add(ResourceCollection c) {
        this.checkChildrenAllowed();
        if (c == null) {
            return;
        }
        if (this.union == null) {
            this.union = new Union();
            this.union.setProject(this.getProject());
            this.union.setCache(this.cache);
        }
        this.union.add(c);
        this.setChecked(false);
    }

    public Path createPath() throws BuildException {
        Path p = new Path(this.getProject());
        this.add(p);
        return p;
    }

    public void append(Path other) {
        if (other == null) {
            return;
        }
        this.add(other);
    }

    public void addExisting(Path source) {
        this.addExisting(source, false);
    }

    public void addExisting(Path source, boolean tryUserDir) {
        File userDir = tryUserDir ? new File(System.getProperty("user.dir")) : null;
        for (String name : source.list()) {
            File f = Path.resolveFile(this.getProject(), name);
            if (tryUserDir && !f.exists()) {
                f = new File(userDir, name);
            }
            if (f.exists()) {
                this.setLocation(f);
                continue;
            }
            if (f.getParentFile() != null && f.getParentFile().exists() && Path.containsWildcards(f.getName())) {
                this.setLocation(f);
                this.log("adding " + f + " which contains wildcards and may not do what you intend it to do depending on your OS or version of Java", 3);
                continue;
            }
            this.log("dropping " + f + " from path as it doesn't exist", 3);
        }
    }

    public void setCache(boolean b) {
        this.checkAttributesAllowed();
        this.cache = b;
        if (this.union != null) {
            this.union.setCache(b);
        }
    }

    public String[] list() {
        if (this.isReference()) {
            return this.getRef().list();
        }
        return this.assertFilesystemOnly(this.union) == null ? new String[]{} : this.union.list();
    }

    @Override
    public String toString() {
        return this.isReference() ? this.getRef().toString() : (this.union == null ? "" : this.union.toString());
    }

    public static String[] translatePath(Project project, String source) {
        if (source == null) {
            return new String[0];
        }
        ArrayList<String> result = new ArrayList<String>();
        PathTokenizer tok = new PathTokenizer(source);
        while (tok.hasMoreTokens()) {
            StringBuffer element = new StringBuffer();
            String pathElement = tok.nextToken();
            try {
                element.append(Path.resolveFile(project, pathElement).getPath());
            }
            catch (BuildException e) {
                project.log("Dropping path element " + pathElement + " as it is not valid relative to the project", 3);
            }
            for (int i = 0; i < element.length(); ++i) {
                Path.translateFileSep(element, i);
            }
            result.add(element.toString());
        }
        return result.toArray(new String[0]);
    }

    public static String translateFile(String source) {
        if (source == null) {
            return "";
        }
        StringBuffer result = new StringBuffer(source);
        for (int i = 0; i < result.length(); ++i) {
            Path.translateFileSep(result, i);
        }
        return result.toString();
    }

    protected static boolean translateFileSep(StringBuffer buffer, int pos) {
        if (buffer.charAt(pos) == '/' || buffer.charAt(pos) == '\\') {
            buffer.setCharAt(pos, File.separatorChar);
            return true;
        }
        return false;
    }

    @Override
    public synchronized int size() {
        if (this.isReference()) {
            return this.getRef().size();
        }
        this.dieOnCircularReference();
        return this.union == null ? 0 : this.assertFilesystemOnly(this.union).size();
    }

    @Override
    public Object clone() {
        try {
            Path result = (Path)super.clone();
            result.union = this.union == null ? this.union : (Union)this.union.clone();
            return result;
        }
        catch (CloneNotSupportedException e) {
            throw new BuildException(e);
        }
    }

    @Override
    protected synchronized void dieOnCircularReference(Stack<Object> stk, Project p) throws BuildException {
        if (this.isChecked()) {
            return;
        }
        if (this.isReference()) {
            super.dieOnCircularReference(stk, p);
        } else {
            if (this.union != null) {
                Path.pushAndInvokeCircularReferenceCheck(this.union, stk, p);
            }
            this.setChecked(true);
        }
    }

    private static File resolveFile(Project project, String relativeName) {
        return FileUtils.getFileUtils().resolveFile(project == null ? null : project.getBaseDir(), relativeName);
    }

    public Path concatSystemClasspath() {
        return this.concatSystemClasspath("last");
    }

    public Path concatSystemClasspath(String defValue) {
        return this.concatSpecialPath(defValue, systemClasspath);
    }

    public Path concatSystemBootClasspath(String defValue) {
        return this.concatSpecialPath(defValue, systemBootClasspath);
    }

    private Path concatSpecialPath(String defValue, Path p) {
        String o;
        Path result = new Path(this.getProject());
        String order = defValue;
        String string = o = this.getProject() != null ? this.getProject().getProperty("build.sysclasspath") : System.getProperty("build.sysclasspath");
        if (o != null) {
            order = o;
        }
        if ("only".equals(order)) {
            result.addExisting(p, true);
        } else if ("first".equals(order)) {
            result.addExisting(p, true);
            result.addExisting(this);
        } else if ("ignore".equals(order)) {
            result.addExisting(this);
        } else {
            if (!"last".equals(order)) {
                this.log("invalid value for build.sysclasspath: " + order, 1);
            }
            result.addExisting(this);
            result.addExisting(p, true);
        }
        return result;
    }

    public void addJavaRuntime() {
        if (JavaEnvUtils.isKaffe()) {
            File kaffeShare = new File(JavaEnvUtils.getJavaHome() + File.separator + "share" + File.separator + "kaffe");
            if (kaffeShare.isDirectory()) {
                FileSet kaffeJarFiles = new FileSet();
                kaffeJarFiles.setDir(kaffeShare);
                kaffeJarFiles.setIncludes("*.jar");
                this.addFileset(kaffeJarFiles);
            }
        } else if ("GNU libgcj".equals(System.getProperty("java.vm.name"))) {
            this.addExisting(systemBootClasspath);
        }
        if (System.getProperty("java.vendor").toLowerCase(Locale.ENGLISH).contains("microsoft")) {
            FileSet msZipFiles = new FileSet();
            msZipFiles.setDir(new File(JavaEnvUtils.getJavaHome() + File.separator + "Packages"));
            msZipFiles.setIncludes("*.ZIP");
            this.addFileset(msZipFiles);
        } else {
            this.addExisting(new Path(null, JavaEnvUtils.getJavaHome() + File.separator + "lib" + File.separator + "rt.jar"));
            this.addExisting(new Path(null, JavaEnvUtils.getJavaHome() + File.separator + "jre" + File.separator + "lib" + File.separator + "rt.jar"));
            for (String secJar : Arrays.asList("jce", "jsse")) {
                this.addExisting(new Path(null, JavaEnvUtils.getJavaHome() + File.separator + "lib" + File.separator + secJar + ".jar"));
                this.addExisting(new Path(null, JavaEnvUtils.getJavaHome() + File.separator + ".." + File.separator + "Classes" + File.separator + secJar + ".jar"));
            }
            for (String ibmJar : Arrays.asList("core", "graphics", "security", "server", "xml")) {
                this.addExisting(new Path(null, JavaEnvUtils.getJavaHome() + File.separator + "lib" + File.separator + ibmJar + ".jar"));
            }
            this.addExisting(new Path(null, JavaEnvUtils.getJavaHome() + File.separator + ".." + File.separator + "Classes" + File.separator + "classes.jar"));
            this.addExisting(new Path(null, JavaEnvUtils.getJavaHome() + File.separator + ".." + File.separator + "Classes" + File.separator + "ui.jar"));
        }
    }

    public void addExtdirs(Path extdirs) {
        if (extdirs == null) {
            String extProp = System.getProperty("java.ext.dirs");
            if (extProp != null) {
                extdirs = new Path(this.getProject(), extProp);
            } else {
                return;
            }
        }
        for (String d : extdirs.list()) {
            File dir = Path.resolveFile(this.getProject(), d);
            if (!dir.exists() || !dir.isDirectory()) continue;
            FileSet fs = new FileSet();
            fs.setDir(dir);
            fs.setIncludes("*");
            this.addFileset(fs);
        }
    }

    @Override
    public final synchronized Iterator<Resource> iterator() {
        if (this.isReference()) {
            return this.getRef().iterator();
        }
        this.dieOnCircularReference();
        if (this.getPreserveBC()) {
            return new FileResourceIterator(this.getProject(), null, this.list());
        }
        return this.union == null ? Collections.emptySet().iterator() : this.assertFilesystemOnly(this.union).iterator();
    }

    @Override
    public synchronized boolean isFilesystemOnly() {
        if (this.isReference()) {
            return this.getRef().isFilesystemOnly();
        }
        this.dieOnCircularReference();
        this.assertFilesystemOnly(this.union);
        return true;
    }

    protected ResourceCollection assertFilesystemOnly(ResourceCollection rc) {
        if (rc != null && !rc.isFilesystemOnly()) {
            throw new BuildException("%s allows only filesystem resources.", this.getDataTypeName());
        }
        return rc;
    }

    protected boolean delegateIteratorToList() {
        if (this.getClass().equals(Path.class)) {
            return false;
        }
        try {
            Method listMethod = this.getClass().getMethod("list", new Class[0]);
            return !listMethod.getDeclaringClass().equals(Path.class);
        }
        catch (Exception e) {
            return false;
        }
    }

    private synchronized boolean getPreserveBC() {
        if (this.preserveBC == null) {
            this.preserveBC = this.delegateIteratorToList() ? Boolean.TRUE : Boolean.FALSE;
        }
        return this.preserveBC;
    }

    private static boolean containsWildcards(String path) {
        return path != null && (path.contains("*") || path.contains("?"));
    }

    private Path getRef() {
        return this.getCheckedRef(Path.class);
    }

    public class PathElement
    implements ResourceCollection {
        private String[] parts;

        public void setLocation(File loc) {
            this.parts = new String[]{Path.translateFile(loc.getAbsolutePath())};
        }

        public void setPath(String path) {
            this.parts = Path.translatePath(Path.this.getProject(), path);
        }

        public String[] getParts() {
            return this.parts;
        }

        @Override
        public Iterator<Resource> iterator() {
            return new FileResourceIterator(Path.this.getProject(), null, this.parts);
        }

        @Override
        public boolean isFilesystemOnly() {
            return true;
        }

        @Override
        public int size() {
            return this.parts == null ? 0 : this.parts.length;
        }
    }
}

