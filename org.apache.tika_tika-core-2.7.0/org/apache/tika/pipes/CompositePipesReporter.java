/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.pipes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.tika.config.Field;
import org.apache.tika.config.Initializable;
import org.apache.tika.config.InitializableProblemHandler;
import org.apache.tika.config.Param;
import org.apache.tika.exception.TikaConfigException;
import org.apache.tika.pipes.FetchEmitTuple;
import org.apache.tika.pipes.PipesReporter;
import org.apache.tika.pipes.PipesResult;
import org.apache.tika.pipes.pipesiterator.TotalCountResult;

public class CompositePipesReporter
extends PipesReporter
implements Initializable {
    private List<PipesReporter> pipesReporters = new ArrayList<PipesReporter>();

    @Override
    public void report(FetchEmitTuple t, PipesResult result, long elapsed) {
        for (PipesReporter reporter : this.pipesReporters) {
            reporter.report(t, result, elapsed);
        }
    }

    @Override
    public void report(TotalCountResult totalCountResult) {
        for (PipesReporter reporter : this.pipesReporters) {
            reporter.report(totalCountResult);
        }
    }

    @Override
    public boolean supportsTotalCount() {
        for (PipesReporter reporter : this.pipesReporters) {
            if (!reporter.supportsTotalCount()) continue;
            return true;
        }
        return false;
    }

    @Override
    public void error(Throwable t) {
        for (PipesReporter reporter : this.pipesReporters) {
            reporter.error(t);
        }
    }

    @Override
    public void error(String msg) {
        for (PipesReporter reporter : this.pipesReporters) {
            reporter.error(msg);
        }
    }

    @Field
    @Deprecated
    public void setPipesReporters(List<PipesReporter> pipesReporters) {
        this.pipesReporters = pipesReporters;
    }

    @Field
    public void addPipesReporter(PipesReporter pipesReporter) {
        this.pipesReporters.add(pipesReporter);
    }

    public List<PipesReporter> getPipesReporters() {
        return this.pipesReporters;
    }

    @Override
    public void initialize(Map<String, Param> params) throws TikaConfigException {
    }

    @Override
    public void checkInitialization(InitializableProblemHandler problemHandler) throws TikaConfigException {
        if (this.pipesReporters == null) {
            throw new TikaConfigException("must specify 'pipesReporters'");
        }
        if (this.pipesReporters.size() == 0) {
            throw new TikaConfigException("must specify at least one pipes reporter");
        }
    }

    @Override
    public void close() throws IOException {
        IOException ex = null;
        for (PipesReporter pipesReporter : this.pipesReporters) {
            try {
                pipesReporter.close();
            }
            catch (IOException e) {
                ex = e;
            }
        }
        if (ex != null) {
            throw ex;
        }
    }
}

