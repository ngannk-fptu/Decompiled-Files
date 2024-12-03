/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.exporter.subsystem;

import aQute.bnd.annotation.plugin.BndPlugin;
import aQute.bnd.build.Container;
import aQute.bnd.build.Project;
import aQute.bnd.osgi.Domain;
import aQute.bnd.osgi.EmbeddedResource;
import aQute.bnd.osgi.FileResource;
import aQute.bnd.osgi.Instructions;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.JarResource;
import aQute.bnd.osgi.Resource;
import aQute.bnd.osgi.Verifier;
import aQute.bnd.service.export.Exporter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

@BndPlugin(name="subsystem")
public class SubsystemExporter
implements Exporter {
    private static final String OSGI_INF_SUBSYSTEM_MF = "OSGI-INF/SUBSYSTEM.MF";
    private static final String SUBSYSTEM_SYMBOLIC_NAME = "Subsystem-SymbolicName";
    private static final String OSGI_SUBSYSTEM_APPLICATION = "osgi.subsystem.application";
    private static final String OSGI_SUBSYSTEM_FEATURE = "osgi.subsystem.feature";
    private static final String OSGI_SUBSYSTEM_COMPOSITE = "osgi.subsystem.composite";
    private static final String SUBSYSTEM_TYPE = "Subsystem-Type";
    private static final String SUBSYSTEM_CONTENT = "Subsystem-Content";

    @Override
    public String[] getTypes() {
        return new String[0];
    }

    @Override
    public Map.Entry<String, Resource> export(String type, Project project, Map<String, String> options) throws Exception {
        Jar jar = new Jar(".");
        project.addClose(jar);
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().putValue("Manifest-Version", "1.0");
        manifest.getMainAttributes().putValue("Subsystem-ManifestVersion", "1");
        ArrayList<File> files = new ArrayList<File>();
        for (Container c : project.getRunbundles()) {
            switch (c.getType()) {
                case ERROR: {
                    break;
                }
                case PROJECT: 
                case EXTERNAL: 
                case REPO: {
                    files.add(c.getFile());
                    break;
                }
                case LIBRARY: {
                    c.contributeFiles(files, project);
                }
            }
        }
        for (File file : files) {
            Domain domain = Domain.domain(file);
            String bsn = domain.getBundleSymbolicName().getKey();
            String version = domain.getBundleVersion();
            String path = bsn + "-" + version + ".jar";
            jar.putResource(path, new FileResource(file));
        }
        this.headers(project, manifest.getMainAttributes());
        this.set(manifest.getMainAttributes(), SUBSYSTEM_TYPE, OSGI_SUBSYSTEM_FEATURE);
        String ssn = project.getName();
        Collection<String> bsns = project.getBsns();
        if (bsns.size() > 0) {
            ssn = bsns.iterator().next();
        }
        this.set(manifest.getMainAttributes(), SUBSYSTEM_SYMBOLIC_NAME, ssn);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        manifest.write(bout);
        jar.putResource(OSGI_INF_SUBSYSTEM_MF, new EmbeddedResource(bout.toByteArray(), 0L));
        final JarResource jarResource = new JarResource(jar);
        final String name = ssn + ".esa";
        return new Map.Entry<String, Resource>(){

            @Override
            public String getKey() {
                return name;
            }

            @Override
            public Resource getValue() {
                return jarResource;
            }

            @Override
            public Resource setValue(Resource arg0) {
                throw new UnsupportedOperationException();
            }
        };
    }

    private void headers(Project project, Attributes application) {
        for (String key : project.getPropertyKeys(true)) {
            char c;
            String value;
            if (!Verifier.HEADER_PATTERN.matcher(key).matches() || application.getValue(key) != null || (value = project.getProperty(key)) == null || (value = value.trim()).isEmpty() || "<<EMPTY>>".equals(value) || !Character.isUpperCase(c = value.charAt(0))) continue;
            application.putValue(key, value);
        }
        Instructions instructions = new Instructions(project.mergeProperties("-removeheaders"));
        Collection<Object> result = instructions.select(application.keySet(), false);
        application.keySet().removeAll(result);
    }

    private void set(Attributes application, String key, String ... values) {
        if (application.getValue(key) != null) {
            return;
        }
        for (String value : values) {
            if (value == null) continue;
            application.putValue(key, value);
            return;
        }
    }
}

