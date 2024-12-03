/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build;

import aQute.bnd.build.Project;
import aQute.bnd.build.ProjectBuilder;
import aQute.bnd.build.Workspace;
import aQute.bnd.osgi.Jar;
import aQute.bnd.service.Actionable;
import aQute.bnd.service.RepositoryPlugin;
import aQute.bnd.service.Strategy;
import aQute.bnd.version.Version;
import aQute.bnd.version.VersionRange;
import aQute.lib.collections.SortedList;
import aQute.libg.glob.Glob;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.regex.Matcher;

public class WorkspaceRepository
implements RepositoryPlugin,
Actionable {
    private final Workspace workspace;

    public WorkspaceRepository(Workspace workspace) {
        this.workspace = workspace;
    }

    private File[] get(String bsn, String range) throws Exception {
        Collection<Project> projects = this.workspace.getAllProjects();
        TreeMap<Version, File> foundVersion = new TreeMap<Version, File>();
        for (Project project : projects) {
            Map<String, Version> versions = project.getVersions();
            if (!versions.containsKey(bsn)) continue;
            Version version = versions.get(bsn);
            boolean exact = range.matches("[0-9]+\\.[0-9]+\\.[0-9]+\\..*");
            if (!"latest".equals(range) && !this.matchVersion(range, version, exact)) continue;
            File file = project.getOutputFile(bsn, version.toString());
            if (!file.exists()) {
                ProjectBuilder builder = project.getSubBuilder(bsn);
                Throwable throwable = null;
                try {
                    Jar jar = builder.build();
                    Throwable throwable2 = null;
                    try {
                        if (jar == null) {
                            project.getInfo(builder);
                            continue;
                        }
                        file = project.saveBuild(jar);
                    }
                    catch (Throwable throwable3) {
                        throwable2 = throwable3;
                        throw throwable3;
                    }
                    finally {
                        if (jar == null) continue;
                        if (throwable2 != null) {
                            try {
                                jar.close();
                            }
                            catch (Throwable x2) {
                                throwable2.addSuppressed(x2);
                            }
                            continue;
                        }
                        jar.close();
                        continue;
                    }
                }
                catch (Throwable throwable4) {
                    throwable = throwable4;
                    throw throwable4;
                }
                finally {
                    if (builder == null) continue;
                    if (throwable != null) {
                        try {
                            builder.close();
                        }
                        catch (Throwable x2) {
                            throwable.addSuppressed(x2);
                        }
                        continue;
                    }
                    builder.close();
                    continue;
                }
            }
            foundVersion.put(version, file);
            break;
        }
        File[] result = foundVersion.values().toArray(new File[0]);
        if (!"latest".equals(range)) {
            return result;
        }
        if (result.length > 0) {
            return new File[]{result[0]};
        }
        return new File[0];
    }

    private File get(String bsn, String range, Strategy strategy, Map<String, String> properties) throws Exception {
        File[] files = this.get(bsn, range);
        if (files.length == 0) {
            return null;
        }
        if (strategy == Strategy.EXACT) {
            return files[0];
        }
        if (strategy == Strategy.HIGHEST) {
            return files[files.length - 1];
        }
        if (strategy == Strategy.LOWEST) {
            return files[0];
        }
        return null;
    }

    private boolean matchVersion(String range, Version version, boolean exact) {
        if (range == null || range.trim().length() == 0) {
            return true;
        }
        VersionRange vr = new VersionRange(range);
        boolean result = exact ? (vr.isRange() ? false : vr.getHigh().equals(version)) : vr.includes(version);
        return result;
    }

    @Override
    public boolean canWrite() {
        return false;
    }

    @Override
    public RepositoryPlugin.PutResult put(InputStream stream, RepositoryPlugin.PutOptions options) throws Exception {
        throw new UnsupportedOperationException("Read only repository");
    }

    @Override
    public List<String> list(String pattern) throws Exception {
        ArrayList<String> names = new ArrayList<String>();
        Collection<Project> projects = this.workspace.getAllProjects();
        for (Project project : projects) {
            for (String bsn : project.getBsns()) {
                if (pattern != null) {
                    Glob glob = new Glob(pattern);
                    Matcher matcher = glob.matcher(bsn);
                    if (!matcher.matches() || names.contains(bsn)) continue;
                    names.add(bsn);
                    continue;
                }
                if (names.contains(bsn)) continue;
                names.add(bsn);
            }
        }
        return names;
    }

    @Override
    public SortedSet<Version> versions(String bsn) throws Exception {
        ArrayList<Version> versions = new ArrayList<Version>();
        Collection<Project> projects = this.workspace.getAllProjects();
        for (Project project : projects) {
            Map<String, Version> projectVersions = project.getVersions();
            if (!projectVersions.containsKey(bsn)) continue;
            versions.add(projectVersions.get(bsn));
            break;
        }
        if (versions.isEmpty()) {
            return SortedList.empty();
        }
        return new SortedList<Version>(versions);
    }

    @Override
    public String getName() {
        return "Workspace " + this.workspace.getBase().getName();
    }

    @Override
    public String getLocation() {
        return this.workspace.getBase().getAbsolutePath();
    }

    @Override
    public File get(String bsn, Version version, Map<String, String> properties, RepositoryPlugin.DownloadListener ... listeners) throws Exception {
        File file = this.get(bsn, version.toString(), Strategy.EXACT, properties);
        if (file == null) {
            return null;
        }
        for (RepositoryPlugin.DownloadListener l : listeners) {
            try {
                l.success(file);
            }
            catch (Exception e) {
                this.workspace.exception(e, "Workspace repo listener callback for %s", file);
            }
        }
        return file;
    }

    @Override
    public Map<String, Runnable> actions(Object ... target) throws Exception {
        return null;
    }

    @Override
    public String tooltip(Object ... target) throws Exception {
        return null;
    }

    @Override
    public String title(Object ... target) throws Exception {
        return null;
    }
}

