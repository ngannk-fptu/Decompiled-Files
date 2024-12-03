/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.unix;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.dispatch.DispatchTask;
import org.apache.tools.ant.dispatch.DispatchUtils;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.apache.tools.ant.types.FileSet;

public class Symlink
extends DispatchTask {
    private String resource;
    private String link;
    private List<FileSet> fileSets = new ArrayList<FileSet>();
    private String linkFileName;
    private boolean overwrite;
    private boolean failonerror;
    private boolean executing = false;

    @Override
    public void init() throws BuildException {
        super.init();
        this.setDefaults();
    }

    @Override
    public synchronized void execute() throws BuildException {
        if (this.executing) {
            throw new BuildException("Infinite recursion detected in Symlink.execute()");
        }
        try {
            this.executing = true;
            DispatchUtils.execute(this);
        }
        finally {
            this.executing = false;
        }
    }

    public void single() throws BuildException {
        try {
            if (this.resource == null) {
                this.handleError("Must define the resource to symlink to!");
                return;
            }
            if (this.link == null) {
                this.handleError("Must define the link name for symlink!");
                return;
            }
            this.doLink(this.resource, this.link);
        }
        finally {
            this.setDefaults();
        }
    }

    public void delete() throws BuildException {
        try {
            if (this.link == null) {
                this.handleError("Must define the link name for symlink!");
                return;
            }
            Path linkPath = Paths.get(this.link, new String[0]);
            if (!Files.isSymbolicLink(linkPath)) {
                this.log("Skipping deletion of " + linkPath + " since it's not a symlink", 3);
                return;
            }
            this.log("Removing symlink: " + this.link);
            Symlink.deleteSymLink(linkPath);
        }
        catch (IOException ioe) {
            this.handleError(ioe.getMessage());
        }
        finally {
            this.setDefaults();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void recreate() throws BuildException {
        try {
            if (this.fileSets.isEmpty()) {
                this.handleError("File set identifying link file(s) required for action recreate");
                return;
            }
            Properties links = this.loadLinks(this.fileSets);
            for (String link : links.stringPropertyNames()) {
                String resource;
                block10: {
                    resource = links.getProperty(link);
                    try {
                        if (Files.isSymbolicLink(Paths.get(link, new String[0])) && new File(link).getCanonicalPath().equals(new File(resource).getCanonicalPath())) {
                            this.log("not recreating " + link + " as it points to the correct target already", 4);
                        }
                        break block10;
                    }
                    catch (IOException e) {
                        String errMessage = "Failed to check if path " + link + " is a symbolic link, linking to " + resource;
                        if (this.failonerror) {
                            throw new BuildException(errMessage, e);
                        }
                        this.log(errMessage, 2);
                    }
                    continue;
                }
                this.doLink(resource, link);
            }
        }
        finally {
            this.setDefaults();
        }
    }

    public void record() throws BuildException {
        try {
            if (this.fileSets.isEmpty()) {
                this.handleError("Fileset identifying links to record required");
                return;
            }
            if (this.linkFileName == null) {
                this.handleError("Name of file to record links in required");
                return;
            }
            HashMap<File, List> byDir = new HashMap<File, List>();
            this.findLinks(this.fileSets).forEach(link -> byDir.computeIfAbsent(link.getParentFile(), k -> new ArrayList()).add(link));
            byDir.forEach((dir, linksInDir) -> {
                Properties linksToStore = new Properties();
                for (File link : linksInDir) {
                    try {
                        linksToStore.put(link.getName(), link.getCanonicalPath());
                    }
                    catch (IOException ioe) {
                        this.handleError("Couldn't get canonical name of parent link");
                    }
                }
                this.writePropertyFile(linksToStore, (File)dir);
            });
        }
        finally {
            this.setDefaults();
        }
    }

    private void setDefaults() {
        this.resource = null;
        this.link = null;
        this.linkFileName = null;
        this.failonerror = true;
        this.overwrite = false;
        this.setAction("single");
        this.fileSets.clear();
    }

    public void setOverwrite(boolean owrite) {
        this.overwrite = owrite;
    }

    public void setFailOnError(boolean foe) {
        this.failonerror = foe;
    }

    @Override
    public void setAction(String action) {
        super.setAction(action);
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setResource(String src) {
        this.resource = src;
    }

    public void setLinkfilename(String lf) {
        this.linkFileName = lf;
    }

    public void addFileset(FileSet set) {
        this.fileSets.add(set);
    }

    @Deprecated
    public static void deleteSymlink(String path) throws IOException {
        Symlink.deleteSymlink(Paths.get(path, new String[0]).toFile());
    }

    @Deprecated
    public static void deleteSymlink(File linkfil) throws IOException {
        if (!Files.isSymbolicLink(linkfil.toPath())) {
            return;
        }
        Symlink.deleteSymLink(linkfil.toPath());
    }

    private void writePropertyFile(Properties properties, File dir) throws BuildException {
        try (BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(new File(dir, this.linkFileName).toPath(), new OpenOption[0]));){
            properties.store(bos, "Symlinks from " + dir);
        }
        catch (IOException ioe) {
            throw new BuildException(ioe, this.getLocation());
        }
    }

    private void handleError(String msg) {
        if (this.failonerror) {
            throw new BuildException(msg);
        }
        this.log(msg);
    }

    private void doLink(String resource, String link) throws BuildException {
        Path linkPath = Paths.get(link, new String[0]);
        Path target = Paths.get(resource, new String[0]);
        boolean alreadyExists = Files.exists(linkPath, LinkOption.NOFOLLOW_LINKS);
        if (!alreadyExists) {
            try {
                this.log("creating symlink " + linkPath + " -> " + target, 4);
                Files.createSymbolicLink(linkPath, target, new FileAttribute[0]);
            }
            catch (IOException e) {
                if (this.failonerror) {
                    throw new BuildException("Failed to create symlink " + link + " to target " + resource, e);
                }
                this.log("Unable to create symlink " + link + " to target " + resource, e, 2);
            }
            return;
        }
        if (!this.overwrite) {
            this.log("Skipping symlink creation, since file at " + link + " already exists and overwrite is set to false", 2);
            return;
        }
        boolean existingFileDeleted = linkPath.toFile().delete();
        if (!existingFileDeleted) {
            this.handleError("Deletion of file at " + link + " failed, while trying to overwrite it with a symlink");
            return;
        }
        try {
            this.log("creating symlink " + linkPath + " -> " + target + " after removing original", 4);
            Files.createSymbolicLink(linkPath, target, new FileAttribute[0]);
        }
        catch (IOException e) {
            if (this.failonerror) {
                throw new BuildException("Failed to create symlink " + link + " to target " + resource, e);
            }
            this.log("Unable to create symlink " + link + " to target " + resource, e, 2);
        }
    }

    private Set<File> findLinks(List<FileSet> fileSets) {
        HashSet<File> result = new HashSet<File>();
        for (FileSet fs : fileSets) {
            DirectoryScanner ds = fs.getDirectoryScanner(this.getProject());
            File dir = fs.getDir(this.getProject());
            Stream.of(ds.getIncludedFiles(), ds.getIncludedDirectories()).flatMap(Stream::of).forEach(path -> {
                try {
                    File f = new File(dir, (String)path);
                    File pf = f.getParentFile();
                    String name = f.getName();
                    File parentDirCanonicalizedFile = new File(pf.getCanonicalPath(), name);
                    if (Files.isSymbolicLink(parentDirCanonicalizedFile.toPath())) {
                        result.add(parentDirCanonicalizedFile);
                    }
                }
                catch (IOException e) {
                    this.handleError("IOException: " + path + " omitted");
                }
            });
        }
        return result;
    }

    private Properties loadLinks(List<FileSet> fileSets) {
        Properties finalList = new Properties();
        for (FileSet fs : fileSets) {
            DirectoryScanner ds = new DirectoryScanner();
            fs.setupDirectoryScanner(ds, this.getProject());
            ds.setFollowSymlinks(false);
            ds.scan();
            File dir = fs.getDir(this.getProject());
            for (String name : ds.getIncludedFiles()) {
                File inc = new File(dir, name);
                File pf = inc.getParentFile();
                Properties links = new Properties();
                try (BufferedInputStream is = new BufferedInputStream(Files.newInputStream(inc.toPath(), new OpenOption[0]));){
                    links.load(is);
                    pf = pf.getCanonicalFile();
                }
                catch (FileNotFoundException fnfe) {
                    this.handleError("Unable to find " + name + "; skipping it.");
                    continue;
                }
                catch (IOException ioe) {
                    this.handleError("Unable to open " + name + " or its parent dir; skipping it.");
                    continue;
                }
                try {
                    links.store(new PrintStream(new LogOutputStream(this, 2)), "listing properties");
                }
                catch (IOException ex) {
                    this.log("failed to log unshortened properties");
                    links.list(new PrintStream(new LogOutputStream(this, 2)));
                }
                for (String key : links.stringPropertyNames()) {
                    finalList.put(new File(pf, key).getAbsolutePath(), links.getProperty(key));
                }
            }
        }
        return finalList;
    }

    private static void deleteSymLink(Path path) throws IOException {
        boolean deleted = path.toFile().delete();
        if (!deleted) {
            throw new IOException("Could not delete symlink at " + path);
        }
    }
}

