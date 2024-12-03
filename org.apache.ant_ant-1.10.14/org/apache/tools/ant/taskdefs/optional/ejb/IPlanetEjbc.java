/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.stream.Stream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.tools.ant.util.StringUtils;
import org.xml.sax.AttributeList;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class IPlanetEjbc {
    private static final int MIN_NUM_ARGS = 2;
    private static final int MAX_NUM_ARGS = 8;
    private static final int NUM_CLASSES_WITH_IIOP = 15;
    private static final int NUM_CLASSES_WITHOUT_IIOP = 9;
    private static final String ENTITY_BEAN = "entity";
    private static final String STATELESS_SESSION = "stateless";
    private static final String STATEFUL_SESSION = "stateful";
    private File stdDescriptor;
    private File iasDescriptor;
    private File destDirectory;
    private String classpath;
    private String[] classpathElements;
    private boolean retainSource = false;
    private boolean debugOutput = false;
    private File iasHomeDir;
    private SAXParser parser;
    private EjbcHandler handler = new EjbcHandler();
    private Hashtable<String, File> ejbFiles = new Hashtable();
    private String displayName;

    public IPlanetEjbc(File stdDescriptor, File iasDescriptor, File destDirectory, String classpath, SAXParser parser) {
        this.stdDescriptor = stdDescriptor;
        this.iasDescriptor = iasDescriptor;
        this.destDirectory = destDirectory;
        this.classpath = classpath;
        this.parser = parser;
        if (classpath != null) {
            StringTokenizer st = new StringTokenizer(classpath, File.pathSeparator);
            int count = st.countTokens();
            this.classpathElements = Collections.list(st).toArray(new String[count]);
        }
    }

    public void setRetainSource(boolean retainSource) {
        this.retainSource = retainSource;
    }

    public void setDebugOutput(boolean debugOutput) {
        this.debugOutput = debugOutput;
    }

    public void registerDTD(String publicID, String location) {
        this.handler.registerDTD(publicID, location);
    }

    public void setIasHomeDir(File iasHomeDir) {
        this.iasHomeDir = iasHomeDir;
    }

    public Hashtable<String, File> getEjbFiles() {
        return this.ejbFiles;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String[] getCmpDescriptors() {
        return (String[])Stream.of(this.handler.getEjbs()).map(EjbInfo::getCmpDescriptors).flatMap(Collection::stream).toArray(String[]::new);
    }

    public static void main(String[] args) {
        File destDirectory = null;
        String classpath = null;
        SAXParser parser = null;
        boolean debug = false;
        boolean retainSource = false;
        if (args.length < 2 || args.length > 8) {
            IPlanetEjbc.usage();
            return;
        }
        File stdDescriptor = new File(args[args.length - 2]);
        File iasDescriptor = new File(args[args.length - 1]);
        for (int i = 0; i < args.length - 2; ++i) {
            if ("-classpath".equals(args[i])) {
                classpath = args[++i];
                continue;
            }
            if ("-d".equals(args[i])) {
                destDirectory = new File(args[++i]);
                continue;
            }
            if ("-debug".equals(args[i])) {
                debug = true;
                continue;
            }
            if ("-keepsource".equals(args[i])) {
                retainSource = true;
                continue;
            }
            IPlanetEjbc.usage();
            return;
        }
        if (classpath == null) {
            Properties props = System.getProperties();
            classpath = props.getProperty("java.class.path");
        }
        if (destDirectory == null) {
            Properties props = System.getProperties();
            destDirectory = new File(props.getProperty("user.dir"));
        }
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        parserFactory.setValidating(true);
        try {
            parser = parserFactory.newSAXParser();
        }
        catch (Exception e) {
            System.out.println("An exception was generated while trying to ");
            System.out.println("create a new SAXParser.");
            e.printStackTrace();
            return;
        }
        IPlanetEjbc ejbc = new IPlanetEjbc(stdDescriptor, iasDescriptor, destDirectory, classpath, parser);
        ejbc.setDebugOutput(debug);
        ejbc.setRetainSource(retainSource);
        try {
            ejbc.execute();
        }
        catch (IOException e) {
            System.out.println("An IOException has occurred while reading the XML descriptors (" + e.getMessage() + ").");
        }
        catch (SAXException e) {
            System.out.println("A SAXException has occurred while reading the XML descriptors (" + e.getMessage() + ").");
        }
        catch (EjbcException e) {
            System.out.println("An error has occurred while executing the ejbc utility (" + e.getMessage() + ").");
        }
    }

    private static void usage() {
        System.out.println("java org.apache.tools.ant.taskdefs.optional.ejb.IPlanetEjbc \\");
        System.out.println("  [OPTIONS] [EJB 1.1 descriptor] [iAS EJB descriptor]");
        System.out.println();
        System.out.println("Where OPTIONS are:");
        System.out.println("  -debug -- for additional debugging output");
        System.out.println("  -keepsource -- to retain Java source files generated");
        System.out.println("  -classpath [classpath] -- classpath used for compilation");
        System.out.println("  -d [destination directory] -- directory for compiled classes");
        System.out.println();
        System.out.println("If a classpath is not specified, the system classpath");
        System.out.println("will be used.  If a destination directory is not specified,");
        System.out.println("the current working directory will be used (classes will");
        System.out.println("still be placed in subfolders which correspond to their");
        System.out.println("package name).");
        System.out.println();
        System.out.println("The EJB home interface, remote interface, and implementation");
        System.out.println("class must be found in the destination directory.  In");
        System.out.println("addition, the destination will look for the stubs and skeletons");
        System.out.println("in the destination directory to ensure they are up to date.");
    }

    public void execute() throws EjbcException, IOException, SAXException {
        EjbInfo[] ejbs;
        this.checkConfiguration();
        for (EjbInfo ejb : ejbs = this.getEjbs()) {
            this.log("EJBInfo...");
            this.log(ejb.toString());
        }
        for (EjbInfo ejb : ejbs) {
            ejb.checkConfiguration(this.destDirectory);
            if (ejb.mustBeRecompiled(this.destDirectory)) {
                this.log(ejb.getName() + " must be recompiled using ejbc.");
                this.callEjbc(this.buildArgumentList(ejb));
                continue;
            }
            this.log(ejb.getName() + " is up to date.");
        }
    }

    private void callEjbc(String[] arguments) {
        String command = this.iasHomeDir == null ? "" : this.iasHomeDir.toString() + File.separator + "bin" + File.separator;
        command = command + "ejbc ";
        String args = String.join((CharSequence)" ", arguments);
        this.log(command + args);
        try {
            Process p = Runtime.getRuntime().exec(command + args);
            RedirectOutput output = new RedirectOutput(p.getInputStream());
            RedirectOutput error = new RedirectOutput(p.getErrorStream());
            output.start();
            error.start();
            p.waitFor();
            p.destroy();
        }
        catch (IOException e) {
            this.log("An IOException has occurred while trying to execute ejbc.");
            this.log(StringUtils.getStackTrace(e));
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }

    protected void checkConfiguration() throws EjbcException {
        StringBuilder msg = new StringBuilder();
        if (this.stdDescriptor == null) {
            msg.append("A standard XML descriptor file must be specified.  ");
        }
        if (this.iasDescriptor == null) {
            msg.append("An iAS-specific XML descriptor file must be specified.  ");
        }
        if (this.classpath == null) {
            msg.append("A classpath must be specified.    ");
        }
        if (this.parser == null) {
            msg.append("An XML parser must be specified.    ");
        }
        if (this.destDirectory == null) {
            msg.append("A destination directory must be specified.  ");
        } else if (!this.destDirectory.exists()) {
            msg.append("The destination directory specified does not exist.  ");
        } else if (!this.destDirectory.isDirectory()) {
            msg.append("The destination specified is not a directory.  ");
        }
        if (msg.length() > 0) {
            throw new EjbcException(msg.toString());
        }
    }

    private EjbInfo[] getEjbs() throws IOException, SAXException {
        this.parser.parse(this.stdDescriptor, (HandlerBase)this.handler);
        this.parser.parse(this.iasDescriptor, (HandlerBase)this.handler);
        return this.handler.getEjbs();
    }

    private String[] buildArgumentList(EjbInfo ejb) {
        ArrayList<String> arguments = new ArrayList<String>();
        if (this.debugOutput) {
            arguments.add("-debug");
        }
        if (ejb.getBeantype().equals(STATELESS_SESSION)) {
            arguments.add("-sl");
        } else if (ejb.getBeantype().equals(STATEFUL_SESSION)) {
            arguments.add("-sf");
        }
        if (ejb.getIiop()) {
            arguments.add("-iiop");
        }
        if (ejb.getCmp()) {
            arguments.add("-cmp");
        }
        if (this.retainSource) {
            arguments.add("-gs");
        }
        if (ejb.getHasession()) {
            arguments.add("-fo");
        }
        arguments.add("-classpath");
        arguments.add(this.classpath);
        arguments.add("-d");
        arguments.add(this.destDirectory.toString());
        arguments.add(ejb.getHome().getQualifiedClassName());
        arguments.add(ejb.getRemote().getQualifiedClassName());
        arguments.add(ejb.getImplementation().getQualifiedClassName());
        return arguments.toArray(new String[0]);
    }

    private void log(String msg) {
        if (this.debugOutput) {
            System.out.println(msg);
        }
    }

    private class EjbcHandler
    extends HandlerBase {
        private static final String PUBLICID_EJB11 = "-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 1.1//EN";
        private static final String PUBLICID_IPLANET_EJB_60 = "-//Sun Microsystems, Inc.//DTD iAS Enterprise JavaBeans 1.0//EN";
        private static final String DEFAULT_IAS60_EJB11_DTD_LOCATION = "ejb-jar_1_1.dtd";
        private static final String DEFAULT_IAS60_DTD_LOCATION = "IASEjb_jar_1_0.dtd";
        private Map<String, String> resourceDtds = new HashMap<String, String>();
        private Map<String, String> fileDtds = new HashMap<String, String>();
        private Map<String, EjbInfo> ejbs = new HashMap<String, EjbInfo>();
        private EjbInfo currentEjb;
        private boolean iasDescriptor = false;
        private String currentLoc = "";
        private String currentText;
        private String ejbType;

        public EjbcHandler() {
            this.registerDTD(PUBLICID_EJB11, DEFAULT_IAS60_EJB11_DTD_LOCATION);
            this.registerDTD(PUBLICID_IPLANET_EJB_60, DEFAULT_IAS60_DTD_LOCATION);
        }

        public EjbInfo[] getEjbs() {
            return this.ejbs.values().toArray(new EjbInfo[0]);
        }

        public String getDisplayName() {
            return IPlanetEjbc.this.displayName;
        }

        public void registerDTD(String publicID, String location) {
            IPlanetEjbc.this.log("Registering: " + location);
            if (publicID == null || location == null) {
                return;
            }
            if (ClassLoader.getSystemResource(location) != null) {
                IPlanetEjbc.this.log("Found resource: " + location);
                this.resourceDtds.put(publicID, location);
            } else {
                File dtdFile = new File(location);
                if (dtdFile.exists() && dtdFile.isFile()) {
                    IPlanetEjbc.this.log("Found file: " + location);
                    this.fileDtds.put(publicID, location);
                }
            }
        }

        @Override
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
            InputStream inputStream = null;
            try {
                String location = this.resourceDtds.get(publicId);
                if (location != null) {
                    inputStream = ClassLoader.getSystemResource(location).openStream();
                } else {
                    location = this.fileDtds.get(publicId);
                    if (location != null) {
                        inputStream = Files.newInputStream(Paths.get(location, new String[0]), new OpenOption[0]);
                    }
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
            if (inputStream == null) {
                return super.resolveEntity(publicId, systemId);
            }
            return new InputSource(inputStream);
        }

        @Override
        public void startElement(String name, AttributeList atts) throws SAXException {
            this.currentLoc = this.currentLoc + "\\" + name;
            this.currentText = "";
            if ("\\ejb-jar".equals(this.currentLoc)) {
                this.iasDescriptor = false;
            } else if ("\\ias-ejb-jar".equals(this.currentLoc)) {
                this.iasDescriptor = true;
            }
            if ("session".equals(name) || IPlanetEjbc.ENTITY_BEAN.equals(name)) {
                this.ejbType = name;
            }
        }

        @Override
        public void characters(char[] ch, int start, int len) throws SAXException {
            this.currentText = this.currentText + new String(ch).substring(start, start + len);
        }

        @Override
        public void endElement(String name) throws SAXException {
            if (this.iasDescriptor) {
                this.iasCharacters(this.currentText);
            } else {
                this.stdCharacters(this.currentText);
            }
            int nameLength = name.length() + 1;
            int locLength = this.currentLoc.length();
            this.currentLoc = this.currentLoc.substring(0, locLength - nameLength);
        }

        private void stdCharacters(String value) {
            if ("\\ejb-jar\\display-name".equals(this.currentLoc)) {
                IPlanetEjbc.this.displayName = value;
                return;
            }
            String base = "\\ejb-jar\\enterprise-beans\\" + this.ejbType;
            if ((base + "\\ejb-name").equals(this.currentLoc)) {
                this.currentEjb = this.ejbs.computeIfAbsent(value, x$0 -> new EjbInfo((String)x$0));
            } else if ((base + "\\home").equals(this.currentLoc)) {
                this.currentEjb.setHome(value);
            } else if ((base + "\\remote").equals(this.currentLoc)) {
                this.currentEjb.setRemote(value);
            } else if ((base + "\\ejb-class").equals(this.currentLoc)) {
                this.currentEjb.setImplementation(value);
            } else if ((base + "\\prim-key-class").equals(this.currentLoc)) {
                this.currentEjb.setPrimaryKey(value);
            } else if ((base + "\\session-type").equals(this.currentLoc)) {
                this.currentEjb.setBeantype(value);
            } else if ((base + "\\persistence-type").equals(this.currentLoc)) {
                this.currentEjb.setCmp(value);
            }
        }

        private void iasCharacters(String value) {
            String base = "\\ias-ejb-jar\\enterprise-beans\\" + this.ejbType;
            if ((base + "\\ejb-name").equals(this.currentLoc)) {
                this.currentEjb = this.ejbs.computeIfAbsent(value, x$0 -> new EjbInfo((String)x$0));
            } else if ((base + "\\iiop").equals(this.currentLoc)) {
                this.currentEjb.setIiop(value);
            } else if ((base + "\\failover-required").equals(this.currentLoc)) {
                this.currentEjb.setHasession(value);
            } else if ((base + "\\persistence-manager\\properties-file-location").equals(this.currentLoc)) {
                this.currentEjb.addCmpDescriptor(value);
            }
        }
    }

    private class EjbInfo {
        private String name;
        private Classname home;
        private Classname remote;
        private Classname implementation;
        private Classname primaryKey;
        private String beantype = "entity";
        private boolean cmp = false;
        private boolean iiop = false;
        private boolean hasession = false;
        private List<String> cmpDescriptors = new ArrayList<String>();

        public EjbInfo(String name) {
            this.name = name;
        }

        public String getName() {
            if (this.name == null) {
                if (this.implementation == null) {
                    return "[unnamed]";
                }
                return this.implementation.getClassName();
            }
            return this.name;
        }

        public void setHome(String home) {
            this.setHome(new Classname(home));
        }

        public void setHome(Classname home) {
            this.home = home;
        }

        public Classname getHome() {
            return this.home;
        }

        public void setRemote(String remote) {
            this.setRemote(new Classname(remote));
        }

        public void setRemote(Classname remote) {
            this.remote = remote;
        }

        public Classname getRemote() {
            return this.remote;
        }

        public void setImplementation(String implementation) {
            this.setImplementation(new Classname(implementation));
        }

        public void setImplementation(Classname implementation) {
            this.implementation = implementation;
        }

        public Classname getImplementation() {
            return this.implementation;
        }

        public void setPrimaryKey(String primaryKey) {
            this.setPrimaryKey(new Classname(primaryKey));
        }

        public void setPrimaryKey(Classname primaryKey) {
            this.primaryKey = primaryKey;
        }

        public Classname getPrimaryKey() {
            return this.primaryKey;
        }

        public void setBeantype(String beantype) {
            this.beantype = beantype.toLowerCase();
        }

        public String getBeantype() {
            return this.beantype;
        }

        public void setCmp(boolean cmp) {
            this.cmp = cmp;
        }

        public void setCmp(String cmp) {
            this.setCmp("Container".equals(cmp));
        }

        public boolean getCmp() {
            return this.cmp;
        }

        public void setIiop(boolean iiop) {
            this.iiop = iiop;
        }

        public void setIiop(String iiop) {
            this.setIiop(Boolean.parseBoolean(iiop));
        }

        public boolean getIiop() {
            return this.iiop;
        }

        public void setHasession(boolean hasession) {
            this.hasession = hasession;
        }

        public void setHasession(String hasession) {
            this.setHasession(Boolean.parseBoolean(hasession));
        }

        public boolean getHasession() {
            return this.hasession;
        }

        public void addCmpDescriptor(String descriptor) {
            this.cmpDescriptors.add(descriptor);
        }

        public List<String> getCmpDescriptors() {
            return this.cmpDescriptors;
        }

        private void checkConfiguration(File buildDir) throws EjbcException {
            if (this.home == null) {
                throw new EjbcException("A home interface was not found for the " + this.name + " EJB.");
            }
            if (this.remote == null) {
                throw new EjbcException("A remote interface was not found for the " + this.name + " EJB.");
            }
            if (this.implementation == null) {
                throw new EjbcException("An EJB implementation class was not found for the " + this.name + " EJB.");
            }
            if (!(this.beantype.equals(IPlanetEjbc.ENTITY_BEAN) || this.beantype.equals(IPlanetEjbc.STATELESS_SESSION) || this.beantype.equals(IPlanetEjbc.STATEFUL_SESSION))) {
                throw new EjbcException("The beantype found (" + this.beantype + ") isn't valid in the " + this.name + " EJB.");
            }
            if (this.cmp && !this.beantype.equals(IPlanetEjbc.ENTITY_BEAN)) {
                System.out.println("CMP stubs and skeletons may not be generated for a Session Bean -- the \"cmp\" attribute will be ignoredfor the " + this.name + " EJB.");
            }
            if (this.hasession && !this.beantype.equals(IPlanetEjbc.STATEFUL_SESSION)) {
                System.out.println("Highly available stubs and skeletons may only be generated for a Stateful Session Bean-- the \"hasession\" attribute will be ignored for the " + this.name + " EJB.");
            }
            if (!this.remote.getClassFile(buildDir).exists()) {
                throw new EjbcException("The remote interface " + this.remote.getQualifiedClassName() + " could not be found.");
            }
            if (!this.home.getClassFile(buildDir).exists()) {
                throw new EjbcException("The home interface " + this.home.getQualifiedClassName() + " could not be found.");
            }
            if (!this.implementation.getClassFile(buildDir).exists()) {
                throw new EjbcException("The EJB implementation class " + this.implementation.getQualifiedClassName() + " could not be found.");
            }
        }

        public boolean mustBeRecompiled(File destDir) {
            long sourceModified = this.sourceClassesModified(destDir);
            long destModified = this.destClassesModified(destDir);
            return destModified < sourceModified;
        }

        private long sourceClassesModified(File buildDir) {
            File pkFile;
            File remoteFile = this.remote.getClassFile(buildDir);
            long modified = remoteFile.lastModified();
            if (modified == -1L) {
                System.out.println("The class " + this.remote.getQualifiedClassName() + " couldn't be found on the classpath");
                return -1L;
            }
            long latestModified = modified;
            File homeFile = this.home.getClassFile(buildDir);
            modified = homeFile.lastModified();
            if (modified == -1L) {
                System.out.println("The class " + this.home.getQualifiedClassName() + " couldn't be found on the classpath");
                return -1L;
            }
            latestModified = Math.max(latestModified, modified);
            if (this.primaryKey != null) {
                pkFile = this.primaryKey.getClassFile(buildDir);
                modified = pkFile.lastModified();
                if (modified == -1L) {
                    System.out.println("The class " + this.primaryKey.getQualifiedClassName() + "couldn't be found on the classpath");
                    return -1L;
                }
                latestModified = Math.max(latestModified, modified);
            } else {
                pkFile = null;
            }
            File implFile = this.implementation.getClassFile(buildDir);
            modified = implFile.lastModified();
            if (modified == -1L) {
                System.out.println("The class " + this.implementation.getQualifiedClassName() + " couldn't be found on the classpath");
                return -1L;
            }
            String pathToFile = this.remote.getQualifiedClassName();
            pathToFile = pathToFile.replace('.', File.separatorChar) + ".class";
            IPlanetEjbc.this.ejbFiles.put(pathToFile, remoteFile);
            pathToFile = this.home.getQualifiedClassName();
            pathToFile = pathToFile.replace('.', File.separatorChar) + ".class";
            IPlanetEjbc.this.ejbFiles.put(pathToFile, homeFile);
            pathToFile = this.implementation.getQualifiedClassName();
            pathToFile = pathToFile.replace('.', File.separatorChar) + ".class";
            IPlanetEjbc.this.ejbFiles.put(pathToFile, implFile);
            if (pkFile != null) {
                pathToFile = this.primaryKey.getQualifiedClassName();
                pathToFile = pathToFile.replace('.', File.separatorChar) + ".class";
                IPlanetEjbc.this.ejbFiles.put(pathToFile, pkFile);
            }
            return latestModified;
        }

        private long destClassesModified(File destDir) {
            String[] classnames = this.classesToGenerate();
            long destClassesModified = Instant.now().toEpochMilli();
            boolean allClassesFound = true;
            for (String classname : classnames) {
                String pathToClass = classname.replace('.', File.separatorChar) + ".class";
                File classFile = new File(destDir, pathToClass);
                IPlanetEjbc.this.ejbFiles.put(pathToClass, classFile);
                boolean bl = allClassesFound = allClassesFound && classFile.exists();
                if (!allClassesFound) continue;
                long fileMod = classFile.lastModified();
                destClassesModified = Math.min(destClassesModified, fileMod);
            }
            return allClassesFound ? destClassesModified : -1L;
        }

        private String[] classesToGenerate() {
            String[] classnames = this.iiop ? new String[15] : new String[9];
            String remotePkg = this.remote.getPackageName() + ".";
            String remoteClass = this.remote.getClassName();
            String homePkg = this.home.getPackageName() + ".";
            String homeClass = this.home.getClassName();
            String implPkg = this.implementation.getPackageName() + ".";
            String implFullClass = this.implementation.getQualifiedWithUnderscores();
            int index = 0;
            classnames[index++] = implPkg + "ejb_fac_" + implFullClass;
            classnames[index++] = implPkg + "ejb_home_" + implFullClass;
            classnames[index++] = implPkg + "ejb_skel_" + implFullClass;
            classnames[index++] = remotePkg + "ejb_kcp_skel_" + remoteClass;
            classnames[index++] = homePkg + "ejb_kcp_skel_" + homeClass;
            classnames[index++] = remotePkg + "ejb_kcp_stub_" + remoteClass;
            classnames[index++] = homePkg + "ejb_kcp_stub_" + homeClass;
            classnames[index++] = remotePkg + "ejb_stub_" + remoteClass;
            classnames[index++] = homePkg + "ejb_stub_" + homeClass;
            if (!this.iiop) {
                return classnames;
            }
            classnames[index++] = "org.omg.stub." + remotePkg + "_" + remoteClass + "_Stub";
            classnames[index++] = "org.omg.stub." + homePkg + "_" + homeClass + "_Stub";
            classnames[index++] = "org.omg.stub." + remotePkg + "_ejb_RmiCorbaBridge_" + remoteClass + "_Tie";
            classnames[index++] = "org.omg.stub." + homePkg + "_ejb_RmiCorbaBridge_" + homeClass + "_Tie";
            classnames[index++] = remotePkg + "ejb_RmiCorbaBridge_" + remoteClass;
            classnames[index++] = homePkg + "ejb_RmiCorbaBridge_" + homeClass;
            return classnames;
        }

        public String toString() {
            StringBuilder s = new StringBuilder("EJB name: " + this.name + "\n\r              home:      " + this.home + "\n\r              remote:    " + this.remote + "\n\r              impl:      " + this.implementation + "\n\r              primaryKey: " + this.primaryKey + "\n\r              beantype:  " + this.beantype + "\n\r              cmp:       " + this.cmp + "\n\r              iiop:      " + this.iiop + "\n\r              hasession: " + this.hasession);
            for (String cmpDescriptor : this.cmpDescriptors) {
                s.append("\n\r              CMP Descriptor: ").append(cmpDescriptor);
            }
            return s.toString();
        }
    }

    public class EjbcException
    extends Exception {
        private static final long serialVersionUID = 1L;

        public EjbcException(String msg) {
            super(msg);
        }
    }

    private static class RedirectOutput
    extends Thread {
        private InputStream stream;

        public RedirectOutput(InputStream stream) {
            this.stream = stream;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.stream));){
                String text;
                while ((text = reader.readLine()) != null) {
                    System.out.println(text);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class Classname {
        private String qualifiedName;
        private String packageName;
        private String className;

        public Classname(String qualifiedName) {
            if (qualifiedName == null) {
                return;
            }
            this.qualifiedName = qualifiedName;
            int index = qualifiedName.lastIndexOf(46);
            if (index == -1) {
                this.className = qualifiedName;
                this.packageName = "";
            } else {
                this.packageName = qualifiedName.substring(0, index);
                this.className = qualifiedName.substring(index + 1);
            }
        }

        public String getQualifiedClassName() {
            return this.qualifiedName;
        }

        public String getPackageName() {
            return this.packageName;
        }

        public String getClassName() {
            return this.className;
        }

        public String getQualifiedWithUnderscores() {
            return this.qualifiedName.replace('.', '_');
        }

        public File getClassFile(File directory) {
            String pathToFile = this.qualifiedName.replace('.', File.separatorChar) + ".class";
            return new File(directory, pathToFile);
        }

        public String toString() {
            return this.getQualifiedClassName();
        }
    }
}

