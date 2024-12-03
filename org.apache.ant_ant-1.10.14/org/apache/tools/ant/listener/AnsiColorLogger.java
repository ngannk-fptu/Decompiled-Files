/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.listener;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.Properties;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.util.FileUtils;

public class AnsiColorLogger
extends DefaultLogger {
    private static final int ATTR_DIM = 2;
    private static final int FG_RED = 31;
    private static final int FG_GREEN = 32;
    private static final int FG_BLUE = 34;
    private static final int FG_MAGENTA = 35;
    private static final int FG_CYAN = 36;
    private static final String PREFIX = "\u001b[";
    private static final String SUFFIX = "m";
    private static final char SEPARATOR = ';';
    private static final String END_COLOR = "\u001b[m";
    private String errColor = "\u001b[2;31m";
    private String warnColor = "\u001b[2;35m";
    private String infoColor = "\u001b[2;36m";
    private String verboseColor = "\u001b[2;32m";
    private String debugColor = "\u001b[2;34m";
    private boolean colorsSet = false;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    private void setColors() {
        InputStream in;
        block9: {
            String userColorFile = System.getProperty("ant.logger.defaults");
            String systemColorFile = "/org/apache/tools/ant/listener/defaults.properties";
            in = null;
            try {
                Properties prop = new Properties();
                in = userColorFile != null ? Files.newInputStream(Paths.get(userColorFile, new String[0]), new OpenOption[0]) : this.getClass().getResourceAsStream(systemColorFile);
                if (in != null) {
                    prop.load(in);
                }
                String errC = prop.getProperty("AnsiColorLogger.ERROR_COLOR");
                String warn = prop.getProperty("AnsiColorLogger.WARNING_COLOR");
                String info = prop.getProperty("AnsiColorLogger.INFO_COLOR");
                String verbose = prop.getProperty("AnsiColorLogger.VERBOSE_COLOR");
                String debug = prop.getProperty("AnsiColorLogger.DEBUG_COLOR");
                if (errC != null) {
                    this.errColor = PREFIX + errC + SUFFIX;
                }
                if (warn != null) {
                    this.warnColor = PREFIX + warn + SUFFIX;
                }
                if (info != null) {
                    this.infoColor = PREFIX + info + SUFFIX;
                }
                if (verbose != null) {
                    this.verboseColor = PREFIX + verbose + SUFFIX;
                }
                if (debug == null) break block9;
                this.debugColor = PREFIX + debug + SUFFIX;
            }
            catch (IOException iOException) {
                FileUtils.close(in);
                catch (Throwable throwable) {
                    FileUtils.close(in);
                    throw throwable;
                }
            }
        }
        FileUtils.close(in);
    }

    @Override
    protected void printMessage(String message, PrintStream stream, int priority) {
        if (message != null && stream != null) {
            if (!this.colorsSet) {
                this.setColors();
                this.colorsSet = true;
            }
            StringBuilder msg = new StringBuilder(message);
            switch (priority) {
                case 0: {
                    msg.insert(0, this.errColor);
                    msg.append(END_COLOR);
                    break;
                }
                case 1: {
                    msg.insert(0, this.warnColor);
                    msg.append(END_COLOR);
                    break;
                }
                case 2: {
                    msg.insert(0, this.infoColor);
                    msg.append(END_COLOR);
                    break;
                }
                case 3: {
                    msg.insert(0, this.verboseColor);
                    msg.append(END_COLOR);
                    break;
                }
                default: {
                    msg.insert(0, this.debugColor);
                    msg.append(END_COLOR);
                }
            }
            String strmessage = msg.toString();
            stream.println(strmessage);
        }
    }
}

