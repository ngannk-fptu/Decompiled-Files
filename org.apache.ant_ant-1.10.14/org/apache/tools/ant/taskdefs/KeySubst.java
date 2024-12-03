/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.StringUtils;

@Deprecated
public class KeySubst
extends Task {
    private File source = null;
    private File dest = null;
    private String sep = "*";
    private Hashtable<String, String> replacements = new Hashtable();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void execute() throws BuildException {
        this.log("!! KeySubst is deprecated. Use Filter + Copy instead. !!");
        this.log("Performing Substitutions");
        if (this.source == null || this.dest == null) {
            this.log("Source and destinations must not be null");
            return;
        }
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            br = new BufferedReader(new FileReader(this.source));
            this.dest.delete();
            bw = new BufferedWriter(new FileWriter(this.dest));
            String line = null;
            String newline = null;
            line = br.readLine();
            while (line != null) {
                if (!line.isEmpty()) {
                    newline = KeySubst.replace(line, this.replacements);
                    bw.write(newline);
                }
                bw.newLine();
                line = br.readLine();
            }
            bw.flush();
        }
        catch (IOException ioe) {
            try {
                this.log(StringUtils.getStackTrace(ioe), 0);
            }
            catch (Throwable throwable) {
                FileUtils.close(bw);
                FileUtils.close(br);
                throw throwable;
            }
            FileUtils.close(bw);
            FileUtils.close(br);
        }
        FileUtils.close(bw);
        FileUtils.close(br);
    }

    public void setSrc(File s) {
        this.source = s;
    }

    public void setDest(File dest) {
        this.dest = dest;
    }

    public void setSep(String sep) {
        this.sep = sep;
    }

    public void setKeys(String keys) {
        if (keys != null && !keys.isEmpty()) {
            StringTokenizer tok = new StringTokenizer(keys, this.sep, false);
            while (tok.hasMoreTokens()) {
                String token = tok.nextToken().trim();
                StringTokenizer itok = new StringTokenizer(token, "=", false);
                String name = itok.nextToken();
                String value = itok.nextToken();
                this.replacements.put(name, value);
            }
        }
    }

    public static void main(String[] args) {
        try {
            Hashtable<String, String> hash = new Hashtable<String, String>();
            hash.put("VERSION", "1.0.3");
            hash.put("b", "ffff");
            System.out.println(KeySubst.replace("$f ${VERSION} f ${b} jj $", hash));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String replace(String origString, Hashtable<String, String> keys) throws BuildException {
        StringBuilder finalString = new StringBuilder();
        int index = 0;
        int i = 0;
        String key = null;
        while ((index = origString.indexOf("${", i)) > -1) {
            key = origString.substring(index + 2, origString.indexOf("}", index + 3));
            finalString.append(origString, i, index);
            if (keys.containsKey(key)) {
                finalString.append(keys.get(key));
            } else {
                finalString.append("${");
                finalString.append(key);
                finalString.append("}");
            }
            i = index + 3 + key.length();
        }
        finalString.append(origString.substring(i));
        return finalString.toString();
    }
}

