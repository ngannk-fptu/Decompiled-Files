/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.pipes;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.tika.config.Field;
import org.apache.tika.config.Initializable;
import org.apache.tika.config.InitializableProblemHandler;
import org.apache.tika.config.Param;
import org.apache.tika.exception.TikaConfigException;
import org.apache.tika.pipes.PipesReporter;
import org.apache.tika.pipes.PipesResult;

public abstract class PipesReporterBase
extends PipesReporter
implements Initializable {
    private final Set<PipesResult.STATUS> includes = new HashSet<PipesResult.STATUS>();
    private final Set<PipesResult.STATUS> excludes = new HashSet<PipesResult.STATUS>();
    private StatusFilter statusFilter;

    @Override
    public void initialize(Map<String, Param> params) throws TikaConfigException {
        this.statusFilter = this.buildStatusFilter(this.includes, this.excludes);
    }

    private StatusFilter buildStatusFilter(Set<PipesResult.STATUS> includes, Set<PipesResult.STATUS> excludes) throws TikaConfigException {
        if (includes.size() > 0 && excludes.size() > 0) {
            throw new TikaConfigException("Only one of includes and excludes may have any contents");
        }
        if (includes.size() > 0) {
            return new IncludesFilter(includes);
        }
        if (excludes.size() > 0) {
            return new ExcludesFilter(excludes);
        }
        return new AcceptAllFilter();
    }

    @Override
    public void checkInitialization(InitializableProblemHandler problemHandler) throws TikaConfigException {
    }

    public boolean accept(PipesResult.STATUS status) {
        return this.statusFilter.accept(status);
    }

    @Field
    public void setIncludes(List<String> includes) throws TikaConfigException {
        for (String s : includes) {
            try {
                PipesResult.STATUS status = PipesResult.STATUS.valueOf(s);
                this.includes.add(status);
            }
            catch (IllegalArgumentException e) {
                String optionString = this.getOptionString();
                throw new TikaConfigException("I regret I don't recognize " + s + ". I only understand: " + optionString, e);
            }
        }
    }

    @Field
    public void setExcludes(List<String> excludes) throws TikaConfigException {
        for (String s : excludes) {
            try {
                PipesResult.STATUS status = PipesResult.STATUS.valueOf(s);
                this.excludes.add(status);
            }
            catch (IllegalArgumentException e) {
                String optionString = this.getOptionString();
                throw new TikaConfigException("I regret I don't recognize " + s + ". I only understand: " + optionString, e);
            }
        }
    }

    private String getOptionString() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (PipesResult.STATUS status : PipesResult.STATUS.values()) {
            if (++i > 1) {
                sb.append(", ");
            }
            sb.append(status.name());
        }
        return sb.toString();
    }

    private static class AcceptAllFilter
    extends StatusFilter {
        private AcceptAllFilter() {
        }

        @Override
        boolean accept(PipesResult.STATUS status) {
            return true;
        }
    }

    private static class ExcludesFilter
    extends StatusFilter {
        private final Set<PipesResult.STATUS> excludes;

        ExcludesFilter(Set<PipesResult.STATUS> excludes) {
            this.excludes = excludes;
        }

        @Override
        boolean accept(PipesResult.STATUS status) {
            return !this.excludes.contains((Object)status);
        }
    }

    private static class IncludesFilter
    extends StatusFilter {
        private final Set<PipesResult.STATUS> includes;

        private IncludesFilter(Set<PipesResult.STATUS> includes) {
            this.includes = includes;
        }

        @Override
        boolean accept(PipesResult.STATUS status) {
            return this.includes.contains((Object)status);
        }
    }

    private static abstract class StatusFilter {
        private StatusFilter() {
        }

        abstract boolean accept(PipesResult.STATUS var1);
    }
}

