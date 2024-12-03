/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PreservingFileWriter
extends FileWriter {
    protected File target_file;
    protected File tmp_file;

    public PreservingFileWriter(String string) throws IOException {
        super(string + ".antlr.tmp");
        this.target_file = new File(string);
        String string2 = this.target_file.getParent();
        if (string2 != null) {
            File file = new File(string2);
            if (!file.exists()) {
                throw new IOException("destination directory of '" + string + "' doesn't exist");
            }
            if (!file.canWrite()) {
                throw new IOException("destination directory of '" + string + "' isn't writeable");
            }
        }
        if (this.target_file.exists() && !this.target_file.canWrite()) {
            throw new IOException("cannot write to '" + string + "'");
        }
        this.tmp_file = new File(string + ".antlr.tmp");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    public void close() throws IOException {
        block26: {
            block21: {
                var1_1 = null;
                var2_2 = null;
                super.close();
                var3_3 = new char[1024];
                if (this.target_file.length() != this.tmp_file.length()) break block25;
                var6_4 = new char[1024];
                var1_1 = new BufferedReader(new FileReader(this.tmp_file));
                var5_5 = new BufferedReader(new FileReader(this.target_file));
                var9_6 = true;
                block10: while (var9_6) {
                    var7_7 = var1_1.read(var3_3, 0, 1024);
                    if (var7_7 != (var8_8 = var5_5.read(var6_4, 0, 1024))) {
                        var9_6 = false;
                        break;
                    }
                    if (var7_7 == -1) break;
                    for (var10_9 = 0; var10_9 < var7_7; ++var10_9) {
                        if (var3_3[var10_9] == var6_4[var10_9]) continue;
                        var9_6 = false;
                        continue block10;
                    }
                }
                var1_1.close();
                var5_5.close();
                var5_5 = null;
                var1_1 = null;
                if (!var9_6) break block25;
                var12_10 = null;
                if (var1_1 == null) break block21;
                try {
                    var1_1.close();
                }
                catch (IOException var13_13) {
                    // empty catch block
                }
            }
            if (var2_2 != null) {
                try {
                    var2_2.close();
                }
                catch (IOException var13_13) {
                    // empty catch block
                }
            }
            if (this.tmp_file == null || !this.tmp_file.exists()) ** GOTO lbl-1000
            this.tmp_file.delete();
            this.tmp_file = null;
lbl-1000:
            // 2 sources

            {
                block25: {
                    return;
                }
                var1_1 = new BufferedReader(new FileReader(this.tmp_file));
                var2_2 = new BufferedWriter(new FileWriter(this.target_file));
                while ((var4_16 = var1_1.read(var3_3, 0, 1024)) != -1) {
                    var2_2.write(var3_3, 0, var4_16);
                }
            }
            var12_11 = null;
            if (var1_1 != null) {
                try {
                    var1_1.close();
                }
                catch (IOException var13_14) {
                    // empty catch block
                }
            }
            if (var2_2 != null) {
                try {
                    var2_2.close();
                }
                catch (IOException var13_14) {
                    // empty catch block
                }
            }
            if (this.tmp_file != null && this.tmp_file.exists()) {
                this.tmp_file.delete();
                this.tmp_file = null;
            }
            break block26;
            catch (Throwable var11_17) {
                var12_12 = null;
                if (var1_1 != null) {
                    try {
                        var1_1.close();
                    }
                    catch (IOException var13_15) {
                        // empty catch block
                    }
                }
                if (var2_2 != null) {
                    try {
                        var2_2.close();
                    }
                    catch (IOException var13_15) {
                        // empty catch block
                    }
                }
                if (this.tmp_file != null && this.tmp_file.exists()) {
                    this.tmp_file.delete();
                    this.tmp_file = null;
                }
                throw var11_17;
            }
        }
    }
}

