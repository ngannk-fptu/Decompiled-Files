/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.compiler;

import java.util.ArrayList;
import java.util.List;
import org.apache.sling.scripting.jsp.jasper.compiler.SmapStratum;

public class SmapGenerator {
    private String outputFileName;
    private String defaultStratum = "Java";
    private List strata = new ArrayList();
    private List embedded = new ArrayList();
    private boolean doEmbedded = true;

    public synchronized void setOutputFileName(String x) {
        this.outputFileName = x;
    }

    public synchronized void addStratum(SmapStratum stratum, boolean defaultStratum) {
        this.strata.add(stratum);
        if (defaultStratum) {
            this.defaultStratum = stratum.getStratumName();
        }
    }

    public synchronized void addSmap(String smap, String stratumName) {
        this.embedded.add("*O " + stratumName + "\n" + smap + "*C " + stratumName + "\n");
    }

    public void setDoEmbedded(boolean status) {
        this.doEmbedded = status;
    }

    public synchronized String getString() {
        int i;
        if (this.outputFileName == null) {
            throw new IllegalStateException();
        }
        StringBuffer out = new StringBuffer();
        out.append("SMAP\n");
        out.append(this.outputFileName + '\n');
        out.append(this.defaultStratum + '\n');
        if (this.doEmbedded) {
            int nEmbedded = this.embedded.size();
            for (i = 0; i < nEmbedded; ++i) {
                out.append(this.embedded.get(i));
            }
        }
        int nStrata = this.strata.size();
        for (i = 0; i < nStrata; ++i) {
            SmapStratum s = (SmapStratum)this.strata.get(i);
            out.append(s.getString());
        }
        out.append("*E\n");
        return out.toString();
    }

    public String toString() {
        return this.getString();
    }

    public static void main(String[] args) {
        SmapGenerator g = new SmapGenerator();
        g.setOutputFileName("foo.java");
        SmapStratum s = new SmapStratum("JSP");
        s.addFile("foo.jsp");
        s.addFile("bar.jsp", "/foo/foo/bar.jsp");
        s.addLineData(1, "foo.jsp", 1, 1, 1);
        s.addLineData(2, "foo.jsp", 1, 6, 1);
        s.addLineData(3, "foo.jsp", 2, 10, 5);
        s.addLineData(20, "bar.jsp", 1, 30, 1);
        g.addStratum(s, true);
        System.out.print(g);
        System.out.println("---");
        SmapGenerator embedded = new SmapGenerator();
        embedded.setOutputFileName("blargh.tier2");
        s = new SmapStratum("Tier2");
        s.addFile("1.tier2");
        s.addLineData(1, "1.tier2", 1, 1, 1);
        embedded.addStratum(s, true);
        g.addSmap(embedded.toString(), "JSP");
        System.out.println(g);
    }
}

