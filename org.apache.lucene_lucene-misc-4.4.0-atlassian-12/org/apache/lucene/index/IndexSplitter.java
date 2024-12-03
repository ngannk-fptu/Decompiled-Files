/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.SegmentInfo
 *  org.apache.lucene.index.SegmentInfoPerCommit
 *  org.apache.lucene.index.SegmentInfos
 *  org.apache.lucene.store.Directory
 *  org.apache.lucene.store.FSDirectory
 */
package org.apache.lucene.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.index.SegmentInfoPerCommit;
import org.apache.lucene.index.SegmentInfos;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class IndexSplitter {
    public SegmentInfos infos;
    FSDirectory fsDir;
    File dir;
    private static final byte[] copyBuffer = new byte[32768];

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: IndexSplitter <srcDir> -l (list the segments and their sizes)");
            System.err.println("IndexSplitter <srcDir> <destDir> <segments>+");
            System.err.println("IndexSplitter <srcDir> -d (delete the following segments)");
            return;
        }
        File srcDir = new File(args[0]);
        IndexSplitter is = new IndexSplitter(srcDir);
        if (!srcDir.exists()) {
            throw new Exception("srcdir:" + srcDir.getAbsolutePath() + " doesn't exist");
        }
        if (args[1].equals("-l")) {
            is.listSegments();
        } else if (args[1].equals("-d")) {
            ArrayList<String> segs = new ArrayList<String>();
            for (int x = 2; x < args.length; ++x) {
                segs.add(args[x]);
            }
            is.remove(segs.toArray(new String[0]));
        } else {
            File targetDir = new File(args[1]);
            ArrayList<String> segs = new ArrayList<String>();
            for (int x = 2; x < args.length; ++x) {
                segs.add(args[x]);
            }
            is.split(targetDir, segs.toArray(new String[0]));
        }
    }

    public IndexSplitter(File dir) throws IOException {
        this.dir = dir;
        this.fsDir = FSDirectory.open((File)dir);
        this.infos = new SegmentInfos();
        this.infos.read((Directory)this.fsDir);
    }

    public void listSegments() throws IOException {
        DecimalFormat formatter = new DecimalFormat("###,###.###", DecimalFormatSymbols.getInstance(Locale.ROOT));
        for (int x = 0; x < this.infos.size(); ++x) {
            SegmentInfoPerCommit info = this.infos.info(x);
            String sizeStr = formatter.format(info.sizeInBytes());
            System.out.println(info.info.name + " " + sizeStr);
        }
    }

    private int getIdx(String name) {
        for (int x = 0; x < this.infos.size(); ++x) {
            if (!name.equals(this.infos.info((int)x).info.name)) continue;
            return x;
        }
        return -1;
    }

    private SegmentInfoPerCommit getInfo(String name) {
        for (int x = 0; x < this.infos.size(); ++x) {
            if (!name.equals(this.infos.info((int)x).info.name)) continue;
            return this.infos.info(x);
        }
        return null;
    }

    public void remove(String[] segs) throws IOException {
        for (String n : segs) {
            int idx = this.getIdx(n);
            this.infos.remove(idx);
        }
        this.infos.changed();
        this.infos.commit((Directory)this.fsDir);
    }

    public void split(File destDir, String[] segs) throws IOException {
        destDir.mkdirs();
        FSDirectory destFSDir = FSDirectory.open((File)destDir);
        SegmentInfos destInfos = new SegmentInfos();
        destInfos.counter = this.infos.counter;
        for (String n : segs) {
            SegmentInfoPerCommit infoPerCommit = this.getInfo(n);
            SegmentInfo info = infoPerCommit.info;
            SegmentInfo newInfo = new SegmentInfo((Directory)destFSDir, info.getVersion(), info.name, info.getDocCount(), info.getUseCompoundFile(), info.getCodec(), info.getDiagnostics(), info.attributes());
            destInfos.add(new SegmentInfoPerCommit(newInfo, infoPerCommit.getDelCount(), infoPerCommit.getDelGen()));
            Collection files = infoPerCommit.files();
            for (String srcName : files) {
                File srcFile = new File(this.dir, srcName);
                File destFile = new File(destDir, srcName);
                IndexSplitter.copyFile(srcFile, destFile);
            }
        }
        destInfos.changed();
        destInfos.commit((Directory)destFSDir);
    }

    private static void copyFile(File src, File dst) throws IOException {
        int len;
        FileInputStream in = new FileInputStream(src);
        FileOutputStream out = new FileOutputStream(dst);
        while ((len = ((InputStream)in).read(copyBuffer)) > 0) {
            ((OutputStream)out).write(copyBuffer, 0, len);
        }
        ((InputStream)in).close();
        ((OutputStream)out).close();
    }
}

