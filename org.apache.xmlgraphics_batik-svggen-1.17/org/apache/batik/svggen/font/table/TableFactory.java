/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.batik.svggen.font.table.CmapTable;
import org.apache.batik.svggen.font.table.CvtTable;
import org.apache.batik.svggen.font.table.DirectoryEntry;
import org.apache.batik.svggen.font.table.FpgmTable;
import org.apache.batik.svggen.font.table.GlyfTable;
import org.apache.batik.svggen.font.table.GposTable;
import org.apache.batik.svggen.font.table.GsubTable;
import org.apache.batik.svggen.font.table.HeadTable;
import org.apache.batik.svggen.font.table.HheaTable;
import org.apache.batik.svggen.font.table.HmtxTable;
import org.apache.batik.svggen.font.table.KernTable;
import org.apache.batik.svggen.font.table.LocaTable;
import org.apache.batik.svggen.font.table.MaxpTable;
import org.apache.batik.svggen.font.table.NameTable;
import org.apache.batik.svggen.font.table.Os2Table;
import org.apache.batik.svggen.font.table.PostTable;
import org.apache.batik.svggen.font.table.PrepTable;
import org.apache.batik.svggen.font.table.Table;

public class TableFactory {
    public static Table create(DirectoryEntry de, RandomAccessFile raf) throws IOException {
        Table t = null;
        switch (de.getTag()) {
            case 1111577413: {
                break;
            }
            case 1128678944: {
                break;
            }
            case 1146308935: {
                break;
            }
            case 0x45424454: {
                break;
            }
            case 1161972803: {
                break;
            }
            case 1161974595: {
                break;
            }
            case 1195656518: {
                break;
            }
            case 1196445523: {
                t = new GposTable(de, raf);
                break;
            }
            case 1196643650: {
                t = new GsubTable(de, raf);
                break;
            }
            case 1246975046: {
                break;
            }
            case 1280594760: {
                break;
            }
            case 1296909912: {
                break;
            }
            case 1296913220: {
                break;
            }
            case 1330851634: {
                t = new Os2Table(de, raf);
                break;
            }
            case 1346587732: {
                break;
            }
            case 1447316824: {
                break;
            }
            case 1668112752: {
                t = new CmapTable(de, raf);
                break;
            }
            case 1668707360: {
                t = new CvtTable(de, raf);
                break;
            }
            case 1718642541: {
                t = new FpgmTable(de, raf);
                break;
            }
            case 1719034226: {
                break;
            }
            case 1734439792: {
                break;
            }
            case 1735162214: {
                t = new GlyfTable(de, raf);
                break;
            }
            case 1751412088: {
                break;
            }
            case 1751474532: {
                t = new HeadTable(de, raf);
                break;
            }
            case 1751672161: {
                t = new HheaTable(de, raf);
                break;
            }
            case 1752003704: {
                t = new HmtxTable(de, raf);
                break;
            }
            case 1801810542: {
                t = new KernTable(de, raf);
                break;
            }
            case 1819239265: {
                t = new LocaTable(de, raf);
                break;
            }
            case 1835104368: {
                t = new MaxpTable(de, raf);
                break;
            }
            case 1851878757: {
                t = new NameTable(de, raf);
                break;
            }
            case 1886545264: {
                t = new PrepTable(de, raf);
                break;
            }
            case 1886352244: {
                t = new PostTable(de, raf);
                break;
            }
            case 1986553185: {
                break;
            }
        }
        return t;
    }
}

