/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.util.Iterator;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.TimeComparison;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.types.resources.Restrict;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.types.resources.comparators.ResourceComparator;
import org.apache.tools.ant.types.resources.comparators.Reverse;
import org.apache.tools.ant.types.resources.selectors.Date;
import org.apache.tools.ant.types.resources.selectors.Exists;
import org.apache.tools.ant.types.resources.selectors.Not;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;
import org.apache.tools.ant.util.StreamUtils;

public class DependSet
extends MatchingTask {
    private static final ResourceSelector NOT_EXISTS = new Not(new Exists());
    private static final ResourceComparator DATE = new org.apache.tools.ant.types.resources.comparators.Date();
    private static final ResourceComparator REVERSE_DATE = new Reverse(DATE);
    private Union sources = null;
    private Path targets = null;
    private boolean verbose;

    public synchronized Union createSources() {
        this.sources = this.sources == null ? new Union() : this.sources;
        return this.sources;
    }

    public void addSrcfileset(FileSet fs) {
        this.createSources().add(fs);
    }

    public void addSrcfilelist(FileList fl) {
        this.createSources().add(fl);
    }

    public synchronized Path createTargets() {
        this.targets = this.targets == null ? new Path(this.getProject()) : this.targets;
        return this.targets;
    }

    public void addTargetfileset(FileSet fs) {
        this.createTargets().add(new HideMissingBasedir(fs));
    }

    public void addTargetfilelist(FileList fl) {
        this.createTargets().add(fl);
    }

    public void setVerbose(boolean b) {
        this.verbose = b;
    }

    @Override
    public void execute() throws BuildException {
        if (this.sources == null) {
            throw new BuildException("At least one set of source resources must be specified");
        }
        if (this.targets == null) {
            throw new BuildException("At least one set of target files must be specified");
        }
        if (!(this.sources.isEmpty() || this.targets.isEmpty() || this.uptodate(this.sources, this.targets))) {
            this.log("Deleting all target files.", 3);
            if (this.verbose) {
                for (String t : this.targets.list()) {
                    this.log("Deleting " + t);
                }
            }
            Delete delete = new Delete();
            delete.bindToOwner(this);
            delete.add(this.targets);
            delete.perform();
        }
    }

    private boolean uptodate(ResourceCollection src, ResourceCollection target) {
        Date datesel = new Date();
        datesel.setMillis(System.currentTimeMillis());
        datesel.setWhen(TimeComparison.AFTER);
        datesel.setGranularity(0L);
        this.logFuture(this.targets, datesel);
        NonExistent missingTargets = new NonExistent(this.targets);
        int neTargets = missingTargets.size();
        if (neTargets > 0) {
            this.log(neTargets + " nonexistent targets", 3);
            this.logMissing(missingTargets, "target");
            return false;
        }
        Resource oldestTarget = this.getOldest(this.targets);
        this.logWithModificationTime(oldestTarget, "oldest target file");
        this.logFuture(this.sources, datesel);
        NonExistent missingSources = new NonExistent(this.sources);
        int neSources = missingSources.size();
        if (neSources > 0) {
            this.log(neSources + " nonexistent sources", 3);
            this.logMissing(missingSources, "source");
            return false;
        }
        Resource newestSource = this.getNewest(this.sources);
        this.logWithModificationTime(newestSource, "newest source");
        return oldestTarget.getLastModified() >= newestSource.getLastModified();
    }

    private void logFuture(ResourceCollection rc, ResourceSelector rsel) {
        Restrict r = new Restrict();
        r.add(rsel);
        r.add(rc);
        for (Resource res : r) {
            this.log("Warning: " + res + " modified in the future.", 1);
        }
    }

    private Resource getXest(ResourceCollection rc, ResourceComparator c) {
        return StreamUtils.iteratorAsStream(rc.iterator()).max(c).orElse(null);
    }

    private Resource getOldest(ResourceCollection rc) {
        return this.getXest(rc, REVERSE_DATE);
    }

    private Resource getNewest(ResourceCollection rc) {
        return this.getXest(rc, DATE);
    }

    private void logWithModificationTime(Resource r, String what) {
        this.log(r.toLongString() + " is " + what + ", modified at " + new java.util.Date(r.getLastModified()), this.verbose ? 2 : 3);
    }

    private void logMissing(ResourceCollection missing, String what) {
        if (this.verbose) {
            for (Resource r : missing) {
                this.log("Expected " + what + " " + r.toLongString() + " is missing.");
            }
        }
    }

    private static final class HideMissingBasedir
    implements ResourceCollection {
        private FileSet fs;

        private HideMissingBasedir(FileSet fs) {
            this.fs = fs;
        }

        @Override
        public Iterator<Resource> iterator() {
            return this.basedirExists() ? this.fs.iterator() : Resources.EMPTY_ITERATOR;
        }

        @Override
        public int size() {
            return this.basedirExists() ? this.fs.size() : 0;
        }

        @Override
        public boolean isFilesystemOnly() {
            return true;
        }

        private boolean basedirExists() {
            File basedir = this.fs.getDir();
            return basedir == null || basedir.exists();
        }
    }

    private static final class NonExistent
    extends Restrict {
        private NonExistent(ResourceCollection rc) {
            super.add(rc);
            super.add(NOT_EXISTS);
        }
    }
}

