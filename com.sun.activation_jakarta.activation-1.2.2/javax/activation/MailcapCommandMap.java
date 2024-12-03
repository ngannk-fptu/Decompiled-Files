/*
 * Decompiled with CFR 0.152.
 */
package javax.activation;

import com.sun.activation.registries.LogSupport;
import com.sun.activation.registries.MailcapFile;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.activation.CommandInfo;
import javax.activation.CommandMap;
import javax.activation.DataContentHandler;
import javax.activation.SecuritySupport;

public class MailcapCommandMap
extends CommandMap {
    private MailcapFile[] DB;
    private static final int PROG = 0;
    private static final String confDir;

    public MailcapCommandMap() {
        ArrayList<MailcapFile> dbv = new ArrayList<MailcapFile>(5);
        MailcapFile mf = null;
        dbv.add(null);
        LogSupport.log("MailcapCommandMap: load HOME");
        try {
            String path;
            String user_home = System.getProperty("user.home");
            if (user_home != null && (mf = this.loadFile(path = user_home + File.separator + ".mailcap")) != null) {
                dbv.add(mf);
            }
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        LogSupport.log("MailcapCommandMap: load SYS");
        try {
            if (confDir != null && (mf = this.loadFile(confDir + "mailcap")) != null) {
                dbv.add(mf);
            }
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        LogSupport.log("MailcapCommandMap: load JAR");
        this.loadAllResources(dbv, "META-INF/mailcap");
        LogSupport.log("MailcapCommandMap: load DEF");
        mf = this.loadResource("/META-INF/mailcap.default");
        if (mf != null) {
            dbv.add(mf);
        }
        this.DB = new MailcapFile[dbv.size()];
        this.DB = dbv.toArray(this.DB);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private MailcapFile loadResource(String name) {
        InputStream clis = null;
        try {
            clis = SecuritySupport.getResourceAsStream(this.getClass(), name);
            if (clis != null) {
                MailcapFile mf = new MailcapFile(clis);
                if (LogSupport.isLoggable()) {
                    LogSupport.log("MailcapCommandMap: successfully loaded mailcap file: " + name);
                }
                MailcapFile mailcapFile = mf;
                return mailcapFile;
            }
            if (LogSupport.isLoggable()) {
                LogSupport.log("MailcapCommandMap: not loading mailcap file: " + name);
            }
        }
        catch (IOException e) {
            if (LogSupport.isLoggable()) {
                LogSupport.log("MailcapCommandMap: can't load " + name, e);
            }
        }
        catch (SecurityException sex) {
            if (LogSupport.isLoggable()) {
                LogSupport.log("MailcapCommandMap: can't load " + name, sex);
            }
        }
        finally {
            try {
                if (clis != null) {
                    clis.close();
                }
            }
            catch (IOException e) {}
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void loadAllResources(List v, String name) {
        boolean anyLoaded;
        block26: {
            anyLoaded = false;
            try {
                URL[] urls;
                ClassLoader cld = null;
                cld = SecuritySupport.getContextClassLoader();
                if (cld == null) {
                    cld = this.getClass().getClassLoader();
                }
                if ((urls = cld != null ? SecuritySupport.getResources(cld, name) : SecuritySupport.getSystemResources(name)) == null) break block26;
                if (LogSupport.isLoggable()) {
                    LogSupport.log("MailcapCommandMap: getResources");
                }
                for (int i = 0; i < urls.length; ++i) {
                    URL url = urls[i];
                    InputStream clis = null;
                    if (LogSupport.isLoggable()) {
                        LogSupport.log("MailcapCommandMap: URL " + url);
                    }
                    try {
                        clis = SecuritySupport.openStream(url);
                        if (clis != null) {
                            v.add(new MailcapFile(clis));
                            anyLoaded = true;
                            if (!LogSupport.isLoggable()) continue;
                            LogSupport.log("MailcapCommandMap: successfully loaded mailcap file from URL: " + url);
                            continue;
                        }
                        if (!LogSupport.isLoggable()) continue;
                        LogSupport.log("MailcapCommandMap: not loading mailcap file from URL: " + url);
                        continue;
                    }
                    catch (IOException ioex) {
                        if (!LogSupport.isLoggable()) continue;
                        LogSupport.log("MailcapCommandMap: can't load " + url, ioex);
                        continue;
                    }
                    catch (SecurityException sex) {
                        if (!LogSupport.isLoggable()) continue;
                        LogSupport.log("MailcapCommandMap: can't load " + url, sex);
                        continue;
                    }
                    finally {
                        try {
                            if (clis != null) {
                                clis.close();
                            }
                        }
                        catch (IOException ioex) {}
                    }
                }
            }
            catch (Exception ex) {
                if (!LogSupport.isLoggable()) break block26;
                LogSupport.log("MailcapCommandMap: can't load " + name, ex);
            }
        }
        if (!anyLoaded) {
            MailcapFile mf;
            if (LogSupport.isLoggable()) {
                LogSupport.log("MailcapCommandMap: !anyLoaded");
            }
            if ((mf = this.loadResource("/" + name)) != null) {
                v.add(mf);
            }
        }
    }

    private MailcapFile loadFile(String name) {
        MailcapFile mtf = null;
        try {
            mtf = new MailcapFile(name);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return mtf;
    }

    public MailcapCommandMap(String fileName) throws IOException {
        this();
        if (LogSupport.isLoggable()) {
            LogSupport.log("MailcapCommandMap: load PROG from " + fileName);
        }
        if (this.DB[0] == null) {
            this.DB[0] = new MailcapFile(fileName);
        }
    }

    public MailcapCommandMap(InputStream is) {
        this();
        LogSupport.log("MailcapCommandMap: load PROG");
        if (this.DB[0] == null) {
            try {
                this.DB[0] = new MailcapFile(is);
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    @Override
    public synchronized CommandInfo[] getPreferredCommands(String mimeType) {
        Map cmdMap;
        int i;
        ArrayList cmdList = new ArrayList();
        if (mimeType != null) {
            mimeType = mimeType.toLowerCase(Locale.ENGLISH);
        }
        for (i = 0; i < this.DB.length; ++i) {
            if (this.DB[i] == null || (cmdMap = this.DB[i].getMailcapList(mimeType)) == null) continue;
            this.appendPrefCmdsToList(cmdMap, cmdList);
        }
        for (i = 0; i < this.DB.length; ++i) {
            if (this.DB[i] == null || (cmdMap = this.DB[i].getMailcapFallbackList(mimeType)) == null) continue;
            this.appendPrefCmdsToList(cmdMap, cmdList);
        }
        CommandInfo[] cmdInfos = new CommandInfo[cmdList.size()];
        cmdInfos = cmdList.toArray(cmdInfos);
        return cmdInfos;
    }

    private void appendPrefCmdsToList(Map cmdHash, List cmdList) {
        for (String verb : cmdHash.keySet()) {
            if (this.checkForVerb(cmdList, verb)) continue;
            List cmdList2 = (List)cmdHash.get(verb);
            String className = (String)cmdList2.get(0);
            cmdList.add(new CommandInfo(verb, className));
        }
    }

    private boolean checkForVerb(List cmdList, String verb) {
        Iterator ee = cmdList.iterator();
        while (ee.hasNext()) {
            String enum_verb = ((CommandInfo)ee.next()).getCommandName();
            if (!enum_verb.equals(verb)) continue;
            return true;
        }
        return false;
    }

    @Override
    public synchronized CommandInfo[] getAllCommands(String mimeType) {
        Map cmdMap;
        int i;
        ArrayList cmdList = new ArrayList();
        if (mimeType != null) {
            mimeType = mimeType.toLowerCase(Locale.ENGLISH);
        }
        for (i = 0; i < this.DB.length; ++i) {
            if (this.DB[i] == null || (cmdMap = this.DB[i].getMailcapList(mimeType)) == null) continue;
            this.appendCmdsToList(cmdMap, cmdList);
        }
        for (i = 0; i < this.DB.length; ++i) {
            if (this.DB[i] == null || (cmdMap = this.DB[i].getMailcapFallbackList(mimeType)) == null) continue;
            this.appendCmdsToList(cmdMap, cmdList);
        }
        CommandInfo[] cmdInfos = new CommandInfo[cmdList.size()];
        cmdInfos = cmdList.toArray(cmdInfos);
        return cmdInfos;
    }

    private void appendCmdsToList(Map typeHash, List cmdList) {
        for (String verb : typeHash.keySet()) {
            List cmdList2 = (List)typeHash.get(verb);
            for (String cmd : cmdList2) {
                cmdList.add(new CommandInfo(verb, cmd));
            }
        }
    }

    @Override
    public synchronized CommandInfo getCommand(String mimeType, String cmdName) {
        String cmdClassName;
        List v;
        Map cmdMap;
        int i;
        if (mimeType != null) {
            mimeType = mimeType.toLowerCase(Locale.ENGLISH);
        }
        for (i = 0; i < this.DB.length; ++i) {
            if (this.DB[i] == null || (cmdMap = this.DB[i].getMailcapList(mimeType)) == null || (v = (List)cmdMap.get(cmdName)) == null || (cmdClassName = (String)v.get(0)) == null) continue;
            return new CommandInfo(cmdName, cmdClassName);
        }
        for (i = 0; i < this.DB.length; ++i) {
            if (this.DB[i] == null || (cmdMap = this.DB[i].getMailcapFallbackList(mimeType)) == null || (v = (List)cmdMap.get(cmdName)) == null || (cmdClassName = (String)v.get(0)) == null) continue;
            return new CommandInfo(cmdName, cmdClassName);
        }
        return null;
    }

    public synchronized void addMailcap(String mail_cap) {
        LogSupport.log("MailcapCommandMap: add to PROG");
        if (this.DB[0] == null) {
            this.DB[0] = new MailcapFile();
        }
        this.DB[0].appendToMailcap(mail_cap);
    }

    @Override
    public synchronized DataContentHandler createDataContentHandler(String mimeType) {
        String name;
        DataContentHandler dch;
        List v;
        Map cmdMap;
        int i;
        if (LogSupport.isLoggable()) {
            LogSupport.log("MailcapCommandMap: createDataContentHandler for " + mimeType);
        }
        if (mimeType != null) {
            mimeType = mimeType.toLowerCase(Locale.ENGLISH);
        }
        for (i = 0; i < this.DB.length; ++i) {
            if (this.DB[i] == null) continue;
            if (LogSupport.isLoggable()) {
                LogSupport.log("  search DB #" + i);
            }
            if ((cmdMap = this.DB[i].getMailcapList(mimeType)) == null || (v = (List)cmdMap.get("content-handler")) == null || (dch = this.getDataContentHandler(name = (String)v.get(0))) == null) continue;
            return dch;
        }
        for (i = 0; i < this.DB.length; ++i) {
            if (this.DB[i] == null) continue;
            if (LogSupport.isLoggable()) {
                LogSupport.log("  search fallback DB #" + i);
            }
            if ((cmdMap = this.DB[i].getMailcapFallbackList(mimeType)) == null || (v = (List)cmdMap.get("content-handler")) == null || (dch = this.getDataContentHandler(name = (String)v.get(0))) == null) continue;
            return dch;
        }
        return null;
    }

    private DataContentHandler getDataContentHandler(String name) {
        block12: {
            if (LogSupport.isLoggable()) {
                LogSupport.log("    got content-handler");
            }
            if (LogSupport.isLoggable()) {
                LogSupport.log("      class " + name);
            }
            try {
                ClassLoader cld = null;
                cld = SecuritySupport.getContextClassLoader();
                if (cld == null) {
                    cld = this.getClass().getClassLoader();
                }
                Class<?> cl = null;
                try {
                    cl = cld.loadClass(name);
                }
                catch (Exception ex) {
                    cl = Class.forName(name);
                }
                if (cl != null) {
                    return (DataContentHandler)cl.newInstance();
                }
            }
            catch (IllegalAccessException e) {
                if (LogSupport.isLoggable()) {
                    LogSupport.log("Can't load DCH " + name, e);
                }
            }
            catch (ClassNotFoundException e) {
                if (LogSupport.isLoggable()) {
                    LogSupport.log("Can't load DCH " + name, e);
                }
            }
            catch (InstantiationException e) {
                if (!LogSupport.isLoggable()) break block12;
                LogSupport.log("Can't load DCH " + name, e);
            }
        }
        return null;
    }

    @Override
    public synchronized String[] getMimeTypes() {
        ArrayList<String> mtList = new ArrayList<String>();
        for (int i = 0; i < this.DB.length; ++i) {
            String[] ts;
            if (this.DB[i] == null || (ts = this.DB[i].getMimeTypes()) == null) continue;
            for (int j = 0; j < ts.length; ++j) {
                if (mtList.contains(ts[j])) continue;
                mtList.add(ts[j]);
            }
        }
        String[] mts = new String[mtList.size()];
        mts = mtList.toArray(mts);
        return mts;
    }

    public synchronized String[] getNativeCommands(String mimeType) {
        ArrayList<String> cmdList = new ArrayList<String>();
        if (mimeType != null) {
            mimeType = mimeType.toLowerCase(Locale.ENGLISH);
        }
        for (int i = 0; i < this.DB.length; ++i) {
            String[] cmds;
            if (this.DB[i] == null || (cmds = this.DB[i].getNativeCommands(mimeType)) == null) continue;
            for (int j = 0; j < cmds.length; ++j) {
                if (cmdList.contains(cmds[j])) continue;
                cmdList.add(cmds[j]);
            }
        }
        String[] cmds = new String[cmdList.size()];
        cmds = cmdList.toArray(cmds);
        return cmds;
    }

    static {
        String dir = null;
        try {
            dir = (String)AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    String home = System.getProperty("java.home");
                    String newdir = home + File.separator + "conf";
                    File conf = new File(newdir);
                    if (conf.exists()) {
                        return newdir + File.separator;
                    }
                    return home + File.separator + "lib" + File.separator;
                }
            });
        }
        catch (Exception exception) {
            // empty catch block
        }
        confDir = dir;
    }
}

