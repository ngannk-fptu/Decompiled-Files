/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.Touchable;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.util.DateUtils;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;

public class Touch
extends Task {
    public static final DateFormatFactory DEFAULT_DF_FACTORY = new DateFormatFactory(){

        @Override
        public DateFormat getPrimaryFormat() {
            return DateUtils.EN_US_DATE_FORMAT_MIN.get();
        }

        @Override
        public DateFormat getFallbackFormat() {
            return DateUtils.EN_US_DATE_FORMAT_SEC.get();
        }
    };
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private File file;
    private long millis = -1L;
    private String dateTime;
    private List<FileSet> filesets = new Vector<FileSet>();
    private Union resources;
    private boolean dateTimeConfigured;
    private boolean mkdirs;
    private boolean verbose = true;
    private FileNameMapper fileNameMapper = null;
    private DateFormatFactory dfFactory = DEFAULT_DF_FACTORY;

    public void setFile(File file) {
        this.file = file;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }

    public void setDatetime(String dateTime) {
        if (this.dateTime != null) {
            this.log("Resetting datetime attribute to " + dateTime, 3);
        }
        this.dateTime = dateTime;
        this.dateTimeConfigured = false;
    }

    public void setMkdirs(boolean mkdirs) {
        this.mkdirs = mkdirs;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void setPattern(final String pattern) {
        this.dfFactory = new DateFormatFactory(){

            @Override
            public DateFormat getPrimaryFormat() {
                return new SimpleDateFormat(pattern);
            }

            @Override
            public DateFormat getFallbackFormat() {
                return null;
            }
        };
    }

    public void addConfiguredMapper(Mapper mapper) {
        this.add(mapper.getImplementation());
    }

    public void add(FileNameMapper fileNameMapper) throws BuildException {
        if (this.fileNameMapper != null) {
            throw new BuildException("Only one mapper may be added to the %s task.", this.getTaskName());
        }
        this.fileNameMapper = fileNameMapper;
    }

    public void addFileset(FileSet set) {
        this.filesets.add(set);
        this.add(set);
    }

    public void addFilelist(FileList list) {
        this.add(list);
    }

    public synchronized void add(ResourceCollection rc) {
        this.resources = this.resources == null ? new Union() : this.resources;
        this.resources.add(rc);
    }

    protected synchronized void checkConfiguration() throws BuildException {
        if (this.file == null && this.resources == null) {
            throw new BuildException("Specify at least one source--a file or resource collection.");
        }
        if (this.file != null && this.file.exists() && this.file.isDirectory()) {
            throw new BuildException("Use a resource collection to touch directories.");
        }
        if (this.dateTime != null && !this.dateTimeConfigured) {
            long workmillis = this.millis;
            if ("now".equalsIgnoreCase(this.dateTime)) {
                workmillis = System.currentTimeMillis();
            } else {
                DateFormat df = this.dfFactory.getPrimaryFormat();
                ParseException pe = null;
                try {
                    workmillis = df.parse(this.dateTime).getTime();
                }
                catch (ParseException peOne) {
                    df = this.dfFactory.getFallbackFormat();
                    if (df == null) {
                        pe = peOne;
                    }
                    try {
                        workmillis = df.parse(this.dateTime).getTime();
                    }
                    catch (ParseException peTwo) {
                        pe = peTwo;
                    }
                }
                if (pe != null) {
                    throw new BuildException(pe.getMessage(), pe, this.getLocation());
                }
                if (workmillis < 0L) {
                    throw new BuildException("Date of %s results in negative milliseconds value relative to epoch (January 1, 1970, 00:00:00 GMT).", this.dateTime);
                }
            }
            this.log("Setting millis to " + workmillis + " from datetime attribute", this.millis < 0L ? 4 : 3);
            this.setMillis(workmillis);
            this.dateTimeConfigured = true;
        }
    }

    @Override
    public void execute() throws BuildException {
        this.checkConfiguration();
        this.touch();
    }

    protected void touch() throws BuildException {
        long defaultTimestamp = this.getTimestamp();
        if (this.file != null) {
            this.touch(new FileResource(this.file.getParentFile(), this.file.getName()), defaultTimestamp);
        }
        if (this.resources == null) {
            return;
        }
        for (Resource r : this.resources) {
            Touchable t = r.as(Touchable.class);
            if (t == null) {
                throw new BuildException("Can't touch " + r);
            }
            this.touch(r, defaultTimestamp);
        }
        for (FileSet fs : this.filesets) {
            DirectoryScanner ds = fs.getDirectoryScanner(this.getProject());
            File fromDir = fs.getDir(this.getProject());
            for (String srcDir : ds.getIncludedDirectories()) {
                this.touch(new FileResource(fromDir, srcDir), defaultTimestamp);
            }
        }
    }

    @Deprecated
    protected void touch(File file) {
        this.touch(file, this.getTimestamp());
    }

    private long getTimestamp() {
        return this.millis < 0L ? System.currentTimeMillis() : this.millis;
    }

    private void touch(Resource r, long defaultTimestamp) {
        if (this.fileNameMapper == null) {
            FileProvider fp = r.as(FileProvider.class);
            if (fp != null) {
                this.touch(fp.getFile(), defaultTimestamp);
            } else {
                r.as(Touchable.class).touch(defaultTimestamp);
            }
        } else {
            String[] mapped = this.fileNameMapper.mapFileName(r.getName());
            if (mapped != null && mapped.length > 0) {
                long modTime = defaultTimestamp;
                if (this.millis < 0L && r.isExists()) {
                    modTime = r.getLastModified();
                }
                for (String fileName : mapped) {
                    this.touch(this.getProject().resolveFile(fileName), modTime);
                }
            }
        }
    }

    private void touch(File file, long modTime) {
        if (!file.exists()) {
            this.log("Creating " + file, this.verbose ? 2 : 3);
            try {
                FILE_UTILS.createNewFile(file, this.mkdirs);
            }
            catch (IOException ioe) {
                throw new BuildException("Could not create " + file, ioe, this.getLocation());
            }
        }
        if (!file.canWrite()) {
            throw new BuildException("Can not change modification date of read-only file %s", file);
        }
        FILE_UTILS.setFileLastModified(file, modTime);
    }

    public static interface DateFormatFactory {
        public DateFormat getPrimaryFormat();

        public DateFormat getFallbackFormat();
    }
}

