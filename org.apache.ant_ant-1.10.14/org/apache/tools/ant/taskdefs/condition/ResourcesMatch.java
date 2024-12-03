/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.condition;

import java.io.IOException;
import java.util.Iterator;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.util.ResourceUtils;

public class ResourcesMatch
implements Condition {
    private Union resources = null;
    private boolean asText = false;

    public void setAsText(boolean asText) {
        this.asText = asText;
    }

    public void add(ResourceCollection rc) {
        if (rc == null) {
            return;
        }
        this.resources = this.resources == null ? new Union() : this.resources;
        this.resources.add(rc);
    }

    @Override
    public boolean eval() throws BuildException {
        if (this.resources == null) {
            throw new BuildException("You must specify one or more nested resource collections");
        }
        if (this.resources.size() > 1) {
            Iterator<Resource> i = this.resources.iterator();
            Resource r1 = i.next();
            while (i.hasNext()) {
                Resource r2 = i.next();
                try {
                    if (!ResourceUtils.contentEquals(r1, r2, this.asText)) {
                        return false;
                    }
                }
                catch (IOException ioe) {
                    throw new BuildException("when comparing resources " + r1.toString() + " and " + r2.toString(), ioe);
                }
                r1 = r2;
            }
        }
        return true;
    }
}

