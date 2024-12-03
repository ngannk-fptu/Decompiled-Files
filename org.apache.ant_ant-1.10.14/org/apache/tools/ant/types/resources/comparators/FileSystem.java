/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources.comparators;

import java.io.File;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.comparators.ResourceComparator;
import org.apache.tools.ant.util.FileUtils;

public class FileSystem
extends ResourceComparator {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();

    @Override
    protected int resourceCompare(Resource foo, Resource bar) {
        return this.compare(this.file(foo), this.file(bar));
    }

    private File file(Resource r) {
        return r.asOptional(FileProvider.class).orElseThrow(() -> new ClassCastException(r.getClass() + " doesn't provide files")).getFile();
    }

    @Override
    private int compare(File f1, File f2) {
        if (Objects.equals(f1, f2)) {
            return 0;
        }
        if (FILE_UTILS.isLeadingPath(f1, f2)) {
            return -1;
        }
        if (FILE_UTILS.isLeadingPath(f2, f1)) {
            return 1;
        }
        return Comparator.comparing(((Function<File, String>)File::getAbsolutePath).andThen(FILE_UTILS::normalize)).compare(f1, f2);
    }
}

