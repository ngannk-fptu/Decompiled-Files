/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core.support;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import org.springframework.ldap.core.DirContextProcessor;

public class AggregateDirContextProcessor
implements DirContextProcessor {
    private List<DirContextProcessor> dirContextProcessors = new LinkedList<DirContextProcessor>();

    public void addDirContextProcessor(DirContextProcessor processor) {
        this.dirContextProcessors.add(processor);
    }

    public List<DirContextProcessor> getDirContextProcessors() {
        return this.dirContextProcessors;
    }

    public void setDirContextProcessors(List<DirContextProcessor> dirContextProcessors) {
        this.dirContextProcessors = new ArrayList<DirContextProcessor>(dirContextProcessors);
    }

    @Override
    public void preProcess(DirContext ctx) throws NamingException {
        for (DirContextProcessor processor : this.dirContextProcessors) {
            processor.preProcess(ctx);
        }
    }

    @Override
    public void postProcess(DirContext ctx) throws NamingException {
        for (DirContextProcessor processor : this.dirContextProcessors) {
            processor.postProcess(ctx);
        }
    }
}

