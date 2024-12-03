/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build;

import aQute.bnd.build.Project;
import aQute.bnd.build.Workspace;
import aQute.bnd.build.WorkspaceLayout;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.Resource;
import aQute.bnd.service.export.Exporter;
import java.io.File;
import java.util.List;
import java.util.Map;

public class Run
extends Project {
    public static Run createRun(Workspace workspace, File file) throws Exception {
        Processor processor;
        if (workspace != null) {
            Run run = new Run(workspace, file);
            if (run.getProperties().get("-standalone") == null) {
                return run;
            }
            processor = run;
        } else {
            processor = new Processor();
            processor.setProperties(file);
        }
        Workspace standaloneWorkspace = Workspace.createStandaloneWorkspace(processor, file.toURI());
        Run run = new Run(standaloneWorkspace, file);
        return run;
    }

    public Run(Workspace workspace, File projectDir, File propertiesFile) throws Exception {
        super(workspace, projectDir, propertiesFile);
    }

    public Run(Workspace workspace, File propertiesFile) throws Exception {
        super(workspace, propertiesFile == null ? null : propertiesFile.getParentFile(), propertiesFile);
    }

    @Override
    public void report(Map<String, Object> table) throws Exception {
        super.report(table, false);
    }

    @Override
    public String getName() {
        return this.getPropertiesFile().getName();
    }

    public Map.Entry<String, Resource> export(String type, Map<String, String> options) throws Exception {
        Exporter exporter = this.getExporter(type);
        if (exporter == null) {
            this.error("No exporter for %s", type);
            return null;
        }
        return exporter.export(type, this, options);
    }

    private Exporter getExporter(String type) {
        List<Exporter> exporters = this.getPlugins(Exporter.class);
        for (Exporter e : exporters) {
            for (String exporterType : e.getTypes()) {
                if (!type.equals(exporterType)) continue;
                return e;
            }
        }
        return null;
    }

    public boolean isStandalone() {
        return this.getWorkspace().getLayout() == WorkspaceLayout.STANDALONE;
    }
}

