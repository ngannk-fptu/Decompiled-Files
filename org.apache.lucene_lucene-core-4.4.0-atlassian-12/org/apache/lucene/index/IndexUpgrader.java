/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.KeepOnlyLastCommitDeletionPolicy;
import org.apache.lucene.index.UpgradeIndexMergePolicy;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.CommandLineUtil;
import org.apache.lucene.util.Constants;
import org.apache.lucene.util.InfoStream;
import org.apache.lucene.util.Version;

public final class IndexUpgrader {
    private final Directory dir;
    private final IndexWriterConfig iwc;
    private final boolean deletePriorCommits;

    private static void printUsage() {
        System.err.println("Upgrades an index so all segments created with a previous Lucene version are rewritten.");
        System.err.println("Usage:");
        System.err.println("  java " + IndexUpgrader.class.getName() + " [-delete-prior-commits] [-verbose] [-dir-impl X] indexDir");
        System.err.println("This tool keeps only the last commit in an index; for this");
        System.err.println("reason, if the incoming index has more than one commit, the tool");
        System.err.println("refuses to run by default. Specify -delete-prior-commits to override");
        System.err.println("this, allowing the tool to delete all but the last commit.");
        System.err.println("Specify a " + FSDirectory.class.getSimpleName() + " implementation through the -dir-impl option to force its use. If no package is specified the " + FSDirectory.class.getPackage().getName() + " package will be used.");
        System.err.println("WARNING: This tool may reorder document IDs!");
        System.exit(1);
    }

    public static void main(String[] args) throws IOException {
        String path = null;
        boolean deletePriorCommits = false;
        PrintStream out = null;
        String dirImpl = null;
        for (int i = 0; i < args.length; ++i) {
            String arg = args[i];
            if ("-delete-prior-commits".equals(arg)) {
                deletePriorCommits = true;
                continue;
            }
            if ("-verbose".equals(arg)) {
                out = System.out;
                continue;
            }
            if (path == null) {
                path = arg;
                continue;
            }
            if ("-dir-impl".equals(arg)) {
                if (i == args.length - 1) {
                    System.out.println("ERROR: missing value for -dir-impl option");
                    System.exit(1);
                }
                dirImpl = args[++i];
                continue;
            }
            IndexUpgrader.printUsage();
        }
        if (path == null) {
            IndexUpgrader.printUsage();
        }
        FSDirectory dir = null;
        dir = dirImpl == null ? FSDirectory.open(new File(path)) : CommandLineUtil.newFSDirectory(dirImpl, new File(path));
        new IndexUpgrader(dir, Version.LUCENE_CURRENT, out, deletePriorCommits).upgrade();
    }

    public IndexUpgrader(Directory dir, Version matchVersion) {
        this(dir, new IndexWriterConfig(matchVersion, null), false);
    }

    public IndexUpgrader(Directory dir, Version matchVersion, PrintStream infoStream, boolean deletePriorCommits) {
        this(dir, new IndexWriterConfig(matchVersion, null).setInfoStream(infoStream), deletePriorCommits);
    }

    public IndexUpgrader(Directory dir, IndexWriterConfig iwc, boolean deletePriorCommits) {
        this.dir = dir;
        this.iwc = iwc;
        this.deletePriorCommits = deletePriorCommits;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void upgrade() throws IOException {
        List<IndexCommit> commits;
        if (!DirectoryReader.indexExists(this.dir)) {
            throw new IndexNotFoundException(this.dir.toString());
        }
        if (!this.deletePriorCommits && (commits = DirectoryReader.listCommits(this.dir)).size() > 1) {
            throw new IllegalArgumentException("This tool was invoked to not delete prior commit points, but the following commits were found: " + commits);
        }
        IndexWriterConfig c = this.iwc.clone();
        c.setMergePolicy(new UpgradeIndexMergePolicy(c.getMergePolicy()));
        c.setIndexDeletionPolicy(new KeepOnlyLastCommitDeletionPolicy());
        try (IndexWriter w = new IndexWriter(this.dir, c);){
            InfoStream infoStream = c.getInfoStream();
            if (infoStream.isEnabled("IndexUpgrader")) {
                infoStream.message("IndexUpgrader", "Upgrading all pre-" + Constants.LUCENE_MAIN_VERSION + " segments of index directory '" + this.dir + "' to version " + Constants.LUCENE_MAIN_VERSION + "...");
            }
            w.forceMerge(1);
            if (infoStream.isEnabled("IndexUpgrader")) {
                infoStream.message("IndexUpgrader", "All segments upgraded to version " + Constants.LUCENE_MAIN_VERSION);
            }
        }
    }
}

