/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.taskdefs.optional.native2ascii.Native2AsciiAdapter;
import org.apache.tools.ant.taskdefs.optional.native2ascii.Native2AsciiAdapterFactory;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.IdentityMapper;
import org.apache.tools.ant.util.SourceFileScanner;
import org.apache.tools.ant.util.facade.FacadeTaskHelper;
import org.apache.tools.ant.util.facade.ImplementationSpecificArgument;

public class Native2Ascii
extends MatchingTask {
    private boolean reverse = false;
    private String encoding = null;
    private File srcDir = null;
    private File destDir = null;
    private String extension = null;
    private Mapper mapper;
    private FacadeTaskHelper facade = new FacadeTaskHelper(Native2AsciiAdapterFactory.getDefault());
    private Native2AsciiAdapter nestedAdapter = null;

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public boolean getReverse() {
        return this.reverse;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setSrc(File srcDir) {
        this.srcDir = srcDir;
    }

    public void setDest(File destDir) {
        this.destDir = destDir;
    }

    public void setExt(String ext) {
        this.extension = ext;
    }

    public void setImplementation(String impl) {
        if ("default".equals(impl)) {
            this.facade.setImplementation(Native2AsciiAdapterFactory.getDefault());
        } else {
            this.facade.setImplementation(impl);
        }
    }

    public Mapper createMapper() throws BuildException {
        if (this.mapper != null) {
            throw new BuildException("Cannot define more than one mapper", this.getLocation());
        }
        this.mapper = new Mapper(this.getProject());
        return this.mapper;
    }

    public void add(FileNameMapper fileNameMapper) {
        this.createMapper().add(fileNameMapper);
    }

    public ImplementationSpecificArgument createArg() {
        ImplementationSpecificArgument arg = new ImplementationSpecificArgument();
        this.facade.addImplementationArgument(arg);
        return arg;
    }

    public Path createImplementationClasspath() {
        return this.facade.getImplementationClasspath(this.getProject());
    }

    public void add(Native2AsciiAdapter adapter) {
        if (this.nestedAdapter != null) {
            throw new BuildException("Can't have more than one native2ascii adapter");
        }
        this.nestedAdapter = adapter;
    }

    @Override
    public void execute() throws BuildException {
        DirectoryScanner scanner = null;
        if (this.srcDir == null) {
            this.srcDir = this.getProject().resolveFile(".");
        }
        if (this.destDir == null) {
            throw new BuildException("The dest attribute must be set.");
        }
        if (this.srcDir.equals(this.destDir) && this.extension == null && this.mapper == null) {
            throw new BuildException("The ext attribute or a mapper must be set if src and dest dirs are the same.");
        }
        FileNameMapper m = this.mapper == null ? (this.extension == null ? new IdentityMapper() : new ExtMapper()) : this.mapper.getImplementation();
        scanner = this.getDirectoryScanner(this.srcDir);
        String[] files = scanner.getIncludedFiles();
        SourceFileScanner sfs = new SourceFileScanner(this);
        files = sfs.restrict(files, this.srcDir, this.destDir, m);
        int count = files.length;
        if (count == 0) {
            return;
        }
        String message = "Converting " + count + " file" + (count != 1 ? "s" : "") + " from ";
        this.log(message + this.srcDir + " to " + this.destDir);
        for (String file : files) {
            String[] dest = m.mapFileName(file);
            if (dest == null || dest.length <= 0) continue;
            this.convert(file, dest[0]);
        }
    }

    private void convert(String srcName, String destName) throws BuildException {
        Native2AsciiAdapter ad;
        File parentFile;
        File srcFile = new File(this.srcDir, srcName);
        File destFile = new File(this.destDir, destName);
        if (srcFile.equals(destFile)) {
            throw new BuildException("file %s would overwrite itself", srcFile);
        }
        String parentName = destFile.getParent();
        if (!(parentName == null || (parentFile = new File(parentName)).exists() || parentFile.mkdirs() || parentFile.isDirectory())) {
            throw new BuildException("cannot create parent directory %s", parentName);
        }
        this.log("converting " + srcName, 3);
        Native2AsciiAdapter native2AsciiAdapter = ad = this.nestedAdapter != null ? this.nestedAdapter : Native2AsciiAdapterFactory.getAdapter(this.facade.getImplementation(), this, this.createImplementationClasspath());
        if (!ad.convert(this, srcFile, destFile)) {
            throw new BuildException("conversion failed");
        }
    }

    public String[] getCurrentArgs() {
        return this.facade.getArgs();
    }

    private class ExtMapper
    implements FileNameMapper {
        private ExtMapper() {
        }

        @Override
        public void setFrom(String s) {
        }

        @Override
        public void setTo(String s) {
        }

        @Override
        public String[] mapFileName(String fileName) {
            int lastDot = fileName.lastIndexOf(46);
            if (lastDot >= 0) {
                return new String[]{fileName.substring(0, lastDot) + Native2Ascii.this.extension};
            }
            return new String[]{fileName + Native2Ascii.this.extension};
        }
    }
}

