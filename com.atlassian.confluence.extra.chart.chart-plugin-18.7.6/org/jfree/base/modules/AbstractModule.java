/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.base.modules;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.jfree.base.modules.DefaultModuleInfo;
import org.jfree.base.modules.Module;
import org.jfree.base.modules.ModuleInfo;
import org.jfree.base.modules.ModuleInitializeException;
import org.jfree.base.modules.ModuleInitializer;
import org.jfree.base.modules.SubSystem;
import org.jfree.util.ObjectUtilities;

public abstract class AbstractModule
extends DefaultModuleInfo
implements Module {
    private ModuleInfo[] requiredModules;
    private ModuleInfo[] optionalModules;
    private String name;
    private String description;
    private String producer;
    private String subsystem;
    static /* synthetic */ Class class$org$jfree$base$modules$AbstractModule;
    static /* synthetic */ Class class$org$jfree$base$modules$ModuleInitializer;

    public AbstractModule() {
        this.setModuleClass(this.getClass().getName());
    }

    protected void loadModuleInfo() throws ModuleInitializeException {
        InputStream in = ObjectUtilities.getResourceRelativeAsStream("module.properties", this.getClass());
        if (in == null) {
            throw new ModuleInitializeException("File 'module.properties' not found in module package.");
        }
        this.loadModuleInfo(in);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void loadModuleInfo(InputStream in) throws ModuleInitializeException {
        if (in == null) {
            throw new NullPointerException("Given InputStream is null.");
        }
        try {
            ArrayList<DefaultModuleInfo> optionalModules = new ArrayList<DefaultModuleInfo>();
            ArrayList<DefaultModuleInfo> dependendModules = new ArrayList<DefaultModuleInfo>();
            ReaderHelper rh = new ReaderHelper(new BufferedReader(new InputStreamReader(in, "ISO-8859-1")));
            try {
                while (rh.hasNext()) {
                    String lastLineRead = rh.next();
                    if (lastLineRead.startsWith("module-info:")) {
                        this.readModuleInfo(rh);
                        continue;
                    }
                    if (lastLineRead.startsWith("depends:")) {
                        dependendModules.add(this.readExternalModule(rh));
                        continue;
                    }
                    if (!lastLineRead.startsWith("optional:")) continue;
                    optionalModules.add(this.readExternalModule(rh));
                }
            }
            finally {
                rh.close();
            }
            this.optionalModules = optionalModules.toArray(new ModuleInfo[optionalModules.size()]);
            this.requiredModules = dependendModules.toArray(new ModuleInfo[dependendModules.size()]);
        }
        catch (IOException ioe) {
            throw new ModuleInitializeException("Failed to load properties", ioe);
        }
    }

    private String readValue(ReaderHelper reader, String firstLine) throws IOException {
        StringBuffer b = new StringBuffer(firstLine.trim());
        boolean newLine = true;
        while (this.isNextLineValueLine(reader)) {
            firstLine = reader.next();
            String trimedLine = firstLine.trim();
            if (trimedLine.length() == 0 && !newLine) {
                b.append("\n");
                newLine = true;
                continue;
            }
            if (!newLine) {
                b.append(" ");
            }
            b.append(this.parseValue(trimedLine));
            newLine = false;
        }
        return b.toString();
    }

    private boolean isNextLineValueLine(ReaderHelper reader) throws IOException {
        if (!reader.hasNext()) {
            return false;
        }
        String firstLine = reader.next();
        if (firstLine == null) {
            return false;
        }
        if (this.parseKey(firstLine) != null) {
            reader.pushBack(firstLine);
            return false;
        }
        reader.pushBack(firstLine);
        return true;
    }

    private void readModuleInfo(ReaderHelper reader) throws IOException {
        while (reader.hasNext()) {
            String lastLineRead = reader.next();
            if (!Character.isWhitespace(lastLineRead.charAt(0))) {
                reader.pushBack(lastLineRead);
                return;
            }
            String line = lastLineRead.trim();
            String key = this.parseKey(line);
            if (key == null) continue;
            String b = this.readValue(reader, this.parseValue(line.trim()));
            if ("name".equals(key)) {
                this.setName(b);
                continue;
            }
            if ("producer".equals(key)) {
                this.setProducer(b);
                continue;
            }
            if ("description".equals(key)) {
                this.setDescription(b);
                continue;
            }
            if ("subsystem".equals(key)) {
                this.setSubSystem(b);
                continue;
            }
            if ("version.major".equals(key)) {
                this.setMajorVersion(b);
                continue;
            }
            if ("version.minor".equals(key)) {
                this.setMinorVersion(b);
                continue;
            }
            if (!"version.patchlevel".equals(key)) continue;
            this.setPatchLevel(b);
        }
    }

    private String parseKey(String line) {
        int idx = line.indexOf(58);
        if (idx == -1) {
            return null;
        }
        return line.substring(0, idx);
    }

    private String parseValue(String line) {
        int idx = line.indexOf(58);
        if (idx == -1) {
            return line;
        }
        if (idx + 1 == line.length()) {
            return "";
        }
        return line.substring(idx + 1);
    }

    private DefaultModuleInfo readExternalModule(ReaderHelper reader) throws IOException {
        DefaultModuleInfo mi = new DefaultModuleInfo();
        while (reader.hasNext()) {
            String lastLineRead = reader.next();
            if (!Character.isWhitespace(lastLineRead.charAt(0))) {
                reader.pushBack(lastLineRead);
                return mi;
            }
            String line = lastLineRead.trim();
            String key = this.parseKey(line);
            if (key == null) continue;
            String b = this.readValue(reader, this.parseValue(line));
            if ("module".equals(key)) {
                mi.setModuleClass(b);
                continue;
            }
            if ("version.major".equals(key)) {
                mi.setMajorVersion(b);
                continue;
            }
            if ("version.minor".equals(key)) {
                mi.setMinorVersion(b);
                continue;
            }
            if (!"version.patchlevel".equals(key)) continue;
            mi.setPatchLevel(b);
        }
        return mi;
    }

    public String getName() {
        return this.name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    public String getProducer() {
        return this.producer;
    }

    protected void setProducer(String producer) {
        this.producer = producer;
    }

    public ModuleInfo[] getRequiredModules() {
        ModuleInfo[] retval = new ModuleInfo[this.requiredModules.length];
        System.arraycopy(this.requiredModules, 0, retval, 0, this.requiredModules.length);
        return retval;
    }

    public ModuleInfo[] getOptionalModules() {
        ModuleInfo[] retval = new ModuleInfo[this.optionalModules.length];
        System.arraycopy(this.optionalModules, 0, retval, 0, this.optionalModules.length);
        return retval;
    }

    protected void setRequiredModules(ModuleInfo[] requiredModules) {
        this.requiredModules = new ModuleInfo[requiredModules.length];
        System.arraycopy(requiredModules, 0, this.requiredModules, 0, requiredModules.length);
    }

    public void setOptionalModules(ModuleInfo[] optionalModules) {
        this.optionalModules = new ModuleInfo[optionalModules.length];
        System.arraycopy(optionalModules, 0, this.optionalModules, 0, optionalModules.length);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Module : ");
        buffer.append(this.getName());
        buffer.append("\n");
        buffer.append("ModuleClass : ");
        buffer.append(this.getModuleClass());
        buffer.append("\n");
        buffer.append("Version: ");
        buffer.append(this.getMajorVersion());
        buffer.append(".");
        buffer.append(this.getMinorVersion());
        buffer.append(".");
        buffer.append(this.getPatchLevel());
        buffer.append("\n");
        buffer.append("Producer: ");
        buffer.append(this.getProducer());
        buffer.append("\n");
        buffer.append("Description: ");
        buffer.append(this.getDescription());
        buffer.append("\n");
        return buffer.toString();
    }

    protected static boolean isClassLoadable(String name) {
        try {
            ClassLoader loader = ObjectUtilities.getClassLoader(class$org$jfree$base$modules$AbstractModule == null ? (class$org$jfree$base$modules$AbstractModule = AbstractModule.class$("org.jfree.base.modules.AbstractModule")) : class$org$jfree$base$modules$AbstractModule);
            if (loader == null) {
                return false;
            }
            loader.loadClass(name);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    protected static boolean isClassLoadable(String name, Class context) {
        try {
            ObjectUtilities.getClassLoader(context).loadClass(name);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void configure(SubSystem subSystem) {
        InputStream in = ObjectUtilities.getResourceRelativeAsStream("configuration.properties", this.getClass());
        if (in == null) {
            return;
        }
        try {
            subSystem.getPackageManager().getPackageConfiguration().load(in);
        }
        finally {
            try {
                in.close();
            }
            catch (IOException iOException) {}
        }
    }

    protected void performExternalInitialize(String classname) throws ModuleInitializeException {
        try {
            ModuleInitializer mi = (ModuleInitializer)ObjectUtilities.loadAndInstantiate(classname, class$org$jfree$base$modules$AbstractModule == null ? (class$org$jfree$base$modules$AbstractModule = AbstractModule.class$("org.jfree.base.modules.AbstractModule")) : class$org$jfree$base$modules$AbstractModule, class$org$jfree$base$modules$ModuleInitializer == null ? (class$org$jfree$base$modules$ModuleInitializer = AbstractModule.class$("org.jfree.base.modules.ModuleInitializer")) : class$org$jfree$base$modules$ModuleInitializer);
            if (mi == null) {
                throw new ModuleInitializeException("Failed to load specified initializer class.");
            }
            mi.performInit();
        }
        catch (ModuleInitializeException mie) {
            throw mie;
        }
        catch (Exception e) {
            throw new ModuleInitializeException("Failed to load specified initializer class.", e);
        }
    }

    protected void performExternalInitialize(String classname, Class context) throws ModuleInitializeException {
        try {
            ModuleInitializer mi = (ModuleInitializer)ObjectUtilities.loadAndInstantiate(classname, context, class$org$jfree$base$modules$ModuleInitializer == null ? (class$org$jfree$base$modules$ModuleInitializer = AbstractModule.class$("org.jfree.base.modules.ModuleInitializer")) : class$org$jfree$base$modules$ModuleInitializer);
            if (mi == null) {
                throw new ModuleInitializeException("Failed to load specified initializer class.");
            }
            mi.performInit();
        }
        catch (ModuleInitializeException mie) {
            throw mie;
        }
        catch (Exception e) {
            throw new ModuleInitializeException("Failed to load specified initializer class.", e);
        }
    }

    public String getSubSystem() {
        if (this.subsystem == null) {
            return this.getName();
        }
        return this.subsystem;
    }

    protected void setSubSystem(String name) {
        this.subsystem = name;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    private static class ReaderHelper {
        private String buffer;
        private final BufferedReader reader;

        protected ReaderHelper(BufferedReader reader) {
            this.reader = reader;
        }

        public boolean hasNext() throws IOException {
            if (this.buffer == null) {
                this.buffer = this.readLine();
            }
            return this.buffer != null;
        }

        public String next() {
            String line = this.buffer;
            this.buffer = null;
            return line;
        }

        public void pushBack(String line) {
            this.buffer = line;
        }

        protected String readLine() throws IOException {
            String line = this.reader.readLine();
            while (line != null && (line.length() == 0 || line.startsWith("#"))) {
                line = this.reader.readLine();
            }
            return line;
        }

        public void close() throws IOException {
            this.reader.close();
        }
    }
}

