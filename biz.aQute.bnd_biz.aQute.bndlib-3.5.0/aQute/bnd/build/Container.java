/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build;

import aQute.bnd.build.DownloadBlocker;
import aQute.bnd.build.Project;
import aQute.bnd.header.Attrs;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Constants;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.Resource;
import aQute.bnd.service.Strategy;
import aQute.lib.io.IO;
import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

public class Container {
    private volatile File file;
    private final String path;
    private final TYPE type;
    private final String bsn;
    private final String version;
    private volatile String error;
    private final Project project;
    private volatile DownloadBlocker db;
    private volatile Map<String, String> attributes;
    private long manifestTime;
    private Manifest manifest;
    private volatile File[] bundleClasspathExpansion;
    public String warning = "";

    Container(Project project, String bsn, String version, TYPE type, File source, String error, Map<String, String> attributes, DownloadBlocker db) {
        this.bsn = bsn;
        this.version = version;
        this.type = type;
        this.file = source != null ? source : new File("/" + bsn + ":" + version + ":" + (Object)((Object)type));
        this.path = this.file.getAbsolutePath();
        this.project = project;
        this.error = error;
        if (attributes == null || attributes.isEmpty()) {
            attributes = Collections.emptyMap();
        } else if (attributes.containsKey("expand-bcp")) {
            this.bundleClasspathExpansion = new File[0];
        }
        this.attributes = attributes;
        this.db = db;
    }

    public Container(Project project, File file, Map<String, String> attributes) {
        this(project, file.getName(), "project", TYPE.PROJECT, file, null, attributes, null);
    }

    public Container(Project project, File file) {
        this(project, file, null);
    }

    public Container(File file, DownloadBlocker db) {
        this(null, file.getName(), "project", TYPE.EXTERNAL, file, null, null, db);
    }

    public Container(File file, DownloadBlocker db, Attrs attributes) {
        this(null, file.getName(), "project", TYPE.EXTERNAL, file, null, attributes, db);
    }

    public File getFile() {
        DownloadBlocker blocker = this.db;
        if (blocker != null) {
            File f = blocker.getFile();
            if (blocker.getStage() == DownloadBlocker.Stage.FAILURE) {
                String r = blocker.getReason();
                if (this.error == null) {
                    this.error = r;
                }
                return new File(r + ": " + f);
            }
            this.file = f;
            this.db = null;
        }
        return this.file;
    }

    public boolean contributeFiles(List<File> files, Processor reporter) throws Exception {
        switch (this.type) {
            case EXTERNAL: 
            case REPO: {
                for (File f : this.getBundleClasspathFiles()) {
                    if (files.contains(f)) continue;
                    files.add(f);
                }
                return true;
            }
            case PROJECT: {
                File[] fs = this.project.build();
                reporter.getInfo(this.project);
                if (fs == null) {
                    return false;
                }
                for (File f : fs) {
                    if (files.contains(f)) continue;
                    files.add(f);
                }
                return true;
            }
            case LIBRARY: {
                List<Container> containers = this.getMembers();
                for (Container container : containers) {
                    if (container.contributeFiles(files, reporter)) continue;
                    return false;
                }
                return true;
            }
            case ERROR: {
                reporter.error("%s", this.getError());
                return false;
            }
        }
        return false;
    }

    public String getBundleSymbolicName() {
        return this.bsn;
    }

    public String getVersion() {
        return this.version;
    }

    public TYPE getType() {
        return this.type;
    }

    public String getError() {
        return this.error;
    }

    public boolean equals(Object other) {
        if (other instanceof Container) {
            return this.path.equals(((Container)other).path);
        }
        return false;
    }

    public int hashCode() {
        return this.path.hashCode();
    }

    public Project getProject() {
        return this.project;
    }

    public String toString() {
        if (this.getError() != null) {
            return "/error/" + this.getError();
        }
        return this.getFile().getAbsolutePath().replace(File.separatorChar, '/');
    }

    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    public synchronized void putAttribute(String name, String value) {
        if (this.attributes == Collections.emptyMap()) {
            this.attributes = new HashMap<String, String>(1);
        }
        this.attributes.put(name, value);
    }

    public List<Container> getMembers() throws Exception {
        List<Container> result = this.project.newList();
        if (this.getType() == TYPE.LIBRARY) {
            try (BufferedReader rd = IO.reader(this.getFile(), Constants.DEFAULT_CHARSET);){
                String line;
                while ((line = rd.readLine()) != null) {
                    if ((line = line.trim()).startsWith("#") || line.length() <= 0) continue;
                    List<Container> list = this.project.getBundles(Strategy.HIGHEST, line, null);
                    result.addAll(list);
                }
            }
        } else {
            result.add(this);
        }
        return result;
    }

    public static void flatten(Container container, List<Container> list) throws Exception {
        if (container.getType() == TYPE.LIBRARY) {
            Container.flatten(container.getMembers(), list);
        } else {
            list.add(container);
        }
    }

    public static List<Container> flatten(Collection<Container> containers) throws Exception {
        ArrayList<Container> list = new ArrayList<Container>();
        Container.flatten(containers, list);
        return list;
    }

    public static void flatten(Collection<Container> containers, List<Container> list) throws Exception {
        if (containers == null) {
            return;
        }
        for (Container container : containers) {
            Container.flatten(container, list);
        }
    }

    public Manifest getManifest() throws Exception {
        if (this.getError() != null || this.getFile() == null) {
            return null;
        }
        if (this.manifestTime < this.getFile().lastModified()) {
            try (JarInputStream jin = new JarInputStream(IO.stream(this.getFile()));){
                this.manifest = jin.getManifest();
            }
            this.manifestTime = this.getFile().lastModified();
        }
        return this.manifest;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private File[] getBundleClasspathFiles() throws Exception {
        String bundleClassPath;
        File[] bce = this.bundleClasspathExpansion;
        if (bce == null) {
            this.bundleClasspathExpansion = new File[]{this.getFile()};
            return this.bundleClasspathExpansion;
        }
        if (bce.length != 0) {
            return bce;
        }
        File file = this.getFile();
        Manifest m = this.getManifest();
        if (m == null || (bundleClassPath = m.getMainAttributes().getValue("Bundle-ClassPath")) == null) {
            this.bundleClasspathExpansion = new File[]{file};
            return this.bundleClasspathExpansion;
        }
        File bundleClasspathDirectory = IO.getFile(file.getParentFile(), "." + file.getName() + "-bcp");
        Parameters header = new Parameters(bundleClassPath, this.project);
        ArrayList<File> files = new ArrayList<File>(header.size());
        IO.mkdirs(bundleClasspathDirectory);
        int n = 0;
        try (Jar jar = null;){
            for (Map.Entry<String, Attrs> entry : header.entrySet()) {
                if (".".equals(entry.getKey())) {
                    files.add(file);
                } else {
                    File member = new File(bundleClasspathDirectory, n + "-" + this.toName(entry.getKey()));
                    if (!this.isCurrent(file, member)) {
                        Resource resource;
                        if (jar == null) {
                            jar = new Jar(file);
                        }
                        if ((resource = jar.getResource(entry.getKey())) == null) {
                            this.warning = this.warning + "Invalid bcp entry: " + entry.getKey() + "\n";
                        } else {
                            IO.copy(resource.openInputStream(), member);
                            member.setLastModified(file.lastModified());
                        }
                    }
                    files.add(member);
                }
                ++n;
            }
        }
        this.bundleClasspathExpansion = files.toArray(bce);
        return this.bundleClasspathExpansion;
    }

    private boolean isCurrent(File file, File member) {
        return member.isFile() && member.lastModified() == file.lastModified();
    }

    private String toName(String key) {
        int n = key.lastIndexOf(47);
        return key.substring(n + 1);
    }

    public String getWarning() {
        return this.warning;
    }

    public static enum TYPE {
        REPO,
        PROJECT,
        EXTERNAL,
        LIBRARY,
        ERROR;

    }
}

