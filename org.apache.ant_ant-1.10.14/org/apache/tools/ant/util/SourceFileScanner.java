/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.io.File;
import java.util.stream.Stream;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceFactory;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.ResourceUtils;

public class SourceFileScanner
implements ResourceFactory {
    protected Task task;
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private File destDir;

    public SourceFileScanner(Task task) {
        this.task = task;
    }

    public String[] restrict(String[] files, File srcDir, File destDir, FileNameMapper mapper) {
        return this.restrict(files, srcDir, destDir, mapper, FILE_UTILS.getFileTimestampGranularity());
    }

    public String[] restrict(String[] files, File srcDir, File destDir, FileNameMapper mapper, long granularity) {
        this.destDir = destDir;
        Resource[] sourceResources = (Resource[])Stream.of(files).map(f -> new FileResource(srcDir, (String)f, (String)f){
            final /* synthetic */ String val$f;
            {
                this.val$f = string;
                super(b, name);
            }

            @Override
            public String getName() {
                return this.val$f;
            }
        }).toArray(Resource[]::new);
        return (String[])Stream.of(ResourceUtils.selectOutOfDateSources((ProjectComponent)this.task, sourceResources, mapper, (ResourceFactory)this, granularity)).map(Resource::getName).toArray(String[]::new);
    }

    public File[] restrictAsFiles(String[] files, File srcDir, File destDir, FileNameMapper mapper) {
        return this.restrictAsFiles(files, srcDir, destDir, mapper, FILE_UTILS.getFileTimestampGranularity());
    }

    public File[] restrictAsFiles(String[] files, File srcDir, File destDir, FileNameMapper mapper, long granularity) {
        return (File[])Stream.of(this.restrict(files, srcDir, destDir, mapper, granularity)).map(name -> new File(srcDir, (String)name)).toArray(File[]::new);
    }

    @Override
    public Resource getResource(String name) {
        return new FileResource(this.destDir, name);
    }
}

