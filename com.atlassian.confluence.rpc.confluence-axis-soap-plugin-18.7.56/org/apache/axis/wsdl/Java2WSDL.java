/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import org.apache.axis.encoding.TypeMappingRegistryImpl;
import org.apache.axis.utils.CLArgsParser;
import org.apache.axis.utils.CLOption;
import org.apache.axis.utils.CLOptionDescriptor;
import org.apache.axis.utils.CLUtil;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.fromJava.Emitter;

public class Java2WSDL {
    protected static final int INHERITED_CLASS_OPT = 97;
    protected static final int SOAPACTION_OPT = 65;
    protected static final int BINDING_NAME_OPT = 98;
    protected static final int STOP_CLASSES_OPT = 99;
    protected static final int IMPORT_SCHEMA_OPT = 67;
    protected static final int EXTRA_CLASSES_OPT = 101;
    protected static final int HELP_OPT = 104;
    protected static final int IMPL_CLASS_OPT = 105;
    protected static final int INPUT_OPT = 73;
    protected static final int LOCATION_OPT = 108;
    protected static final int LOCATION_IMPORT_OPT = 76;
    protected static final int METHODS_ALLOWED_OPT = 109;
    protected static final int NAMESPACE_OPT = 110;
    protected static final int NAMESPACE_IMPL_OPT = 78;
    protected static final int OUTPUT_OPT = 111;
    protected static final int OUTPUT_IMPL_OPT = 79;
    protected static final int PACKAGE_OPT = 112;
    protected static final int PORTTYPE_NAME_OPT = 80;
    protected static final int SERVICE_PORT_NAME_OPT = 115;
    protected static final int SERVICE_ELEMENT_NAME_OPT = 83;
    protected static final int TYPEMAPPING_OPT = 84;
    protected static final int USE_OPT = 117;
    protected static final int OUTPUT_WSDL_MODE_OPT = 119;
    protected static final int METHODS_NOTALLOWED_OPT = 120;
    protected static final int CLASSPATH_OPT = 88;
    protected static final int STYLE_OPT = 121;
    protected static final int DEPLOY_OPT = 100;
    protected CLOptionDescriptor[] options = new CLOptionDescriptor[]{new CLOptionDescriptor("help", 8, 104, Messages.getMessage("j2wopthelp00")), new CLOptionDescriptor("input", 2, 73, Messages.getMessage("j2woptinput00")), new CLOptionDescriptor("output", 2, 111, Messages.getMessage("j2woptoutput00")), new CLOptionDescriptor("location", 2, 108, Messages.getMessage("j2woptlocation00")), new CLOptionDescriptor("portTypeName", 2, 80, Messages.getMessage("j2woptportTypeName00")), new CLOptionDescriptor("bindingName", 2, 98, Messages.getMessage("j2woptbindingName00")), new CLOptionDescriptor("serviceElementName", 2, 83, Messages.getMessage("j2woptserviceElementName00")), new CLOptionDescriptor("servicePortName", 2, 115, Messages.getMessage("j2woptservicePortName00")), new CLOptionDescriptor("namespace", 2, 110, Messages.getMessage("j2woptnamespace00")), new CLOptionDescriptor("PkgtoNS", 48, 112, Messages.getMessage("j2woptPkgtoNS00")), new CLOptionDescriptor("methods", 34, 109, Messages.getMessage("j2woptmethods00")), new CLOptionDescriptor("all", 8, 97, Messages.getMessage("j2woptall00")), new CLOptionDescriptor("outputWsdlMode", 2, 119, Messages.getMessage("j2woptoutputWsdlMode00")), new CLOptionDescriptor("locationImport", 2, 76, Messages.getMessage("j2woptlocationImport00")), new CLOptionDescriptor("namespaceImpl", 2, 78, Messages.getMessage("j2woptnamespaceImpl00")), new CLOptionDescriptor("outputImpl", 2, 79, Messages.getMessage("j2woptoutputImpl00")), new CLOptionDescriptor("implClass", 2, 105, Messages.getMessage("j2woptimplClass00")), new CLOptionDescriptor("exclude", 34, 120, Messages.getMessage("j2woptexclude00")), new CLOptionDescriptor("stopClasses", 34, 99, Messages.getMessage("j2woptstopClass00")), new CLOptionDescriptor("typeMappingVersion", 2, 84, Messages.getMessage("j2wopttypeMapping00")), new CLOptionDescriptor("soapAction", 2, 65, Messages.getMessage("j2woptsoapAction00")), new CLOptionDescriptor("style", 2, 121, Messages.getMessage("j2woptStyle00")), new CLOptionDescriptor("use", 2, 117, Messages.getMessage("j2woptUse00")), new CLOptionDescriptor("extraClasses", 34, 101, Messages.getMessage("j2woptExtraClasses00")), new CLOptionDescriptor("importSchema", 4, 67, Messages.getMessage("j2woptImportSchema00")), new CLOptionDescriptor("classpath", 4, 88, Messages.getMessage("optionClasspath")), new CLOptionDescriptor("deploy", 8, 100, Messages.getMessage("j2woptDeploy00"))};
    protected Emitter emitter;
    protected String className = null;
    protected String wsdlFilename = null;
    protected String wsdlImplFilename = null;
    protected HashMap namespaceMap = new HashMap();
    protected int mode = 0;
    boolean locationSet = false;
    protected String typeMappingVersion = "1.2";
    protected boolean isDeploy = false;

    protected Java2WSDL() {
        this.emitter = this.createEmitter();
    }

    protected Emitter createEmitter() {
        return new Emitter();
    }

    protected void addOptions(CLOptionDescriptor[] newOptions) {
        if (newOptions != null && newOptions.length > 0) {
            CLOptionDescriptor[] allOptions = new CLOptionDescriptor[this.options.length + newOptions.length];
            System.arraycopy(this.options, 0, allOptions, 0, this.options.length);
            System.arraycopy(newOptions, 0, allOptions, this.options.length, newOptions.length);
            this.options = allOptions;
        }
    }

    protected boolean parseOption(CLOption option) {
        boolean status = true;
        switch (option.getId()) {
            case 0: {
                if (this.className != null) {
                    System.out.println(Messages.getMessage("j2wDuplicateClass00", this.className, option.getArgument()));
                    this.printUsage();
                    status = false;
                }
                this.className = option.getArgument();
                break;
            }
            case 109: {
                this.emitter.setAllowedMethods(option.getArgument());
                break;
            }
            case 97: {
                this.emitter.setUseInheritedMethods(true);
                break;
            }
            case 105: {
                this.emitter.setImplCls(option.getArgument());
                break;
            }
            case 104: {
                this.printUsage();
                status = false;
                break;
            }
            case 119: {
                String modeArg = option.getArgument();
                if ("All".equalsIgnoreCase(modeArg)) {
                    this.mode = 0;
                    break;
                }
                if ("Interface".equalsIgnoreCase(modeArg)) {
                    this.mode = 1;
                    break;
                }
                if ("Implementation".equalsIgnoreCase(modeArg)) {
                    this.mode = 2;
                    break;
                }
                this.mode = 0;
                System.err.println(Messages.getMessage("j2wmodeerror", modeArg));
                break;
            }
            case 111: {
                this.wsdlFilename = option.getArgument();
                break;
            }
            case 73: {
                this.emitter.setInputWSDL(option.getArgument());
                break;
            }
            case 79: {
                this.wsdlImplFilename = option.getArgument();
                break;
            }
            case 112: {
                String packageName = option.getArgument(0);
                String namespace = option.getArgument(1);
                this.namespaceMap.put(packageName, namespace);
                break;
            }
            case 110: {
                this.emitter.setIntfNamespace(option.getArgument());
                break;
            }
            case 78: {
                this.emitter.setImplNamespace(option.getArgument());
                break;
            }
            case 83: {
                this.emitter.setServiceElementName(option.getArgument());
                break;
            }
            case 115: {
                this.emitter.setServicePortName(option.getArgument());
                break;
            }
            case 108: {
                this.emitter.setLocationUrl(option.getArgument());
                this.locationSet = true;
                break;
            }
            case 76: {
                this.emitter.setImportUrl(option.getArgument());
                break;
            }
            case 120: {
                this.emitter.setDisallowedMethods(option.getArgument());
                break;
            }
            case 80: {
                this.emitter.setPortTypeName(option.getArgument());
                break;
            }
            case 98: {
                this.emitter.setBindingName(option.getArgument());
                break;
            }
            case 99: {
                this.emitter.setStopClasses(option.getArgument());
                break;
            }
            case 84: {
                String value;
                this.typeMappingVersion = value = option.getArgument();
                break;
            }
            case 65: {
                String value = option.getArgument();
                if (value.equalsIgnoreCase("DEFAULT")) {
                    this.emitter.setSoapAction("DEFAULT");
                    break;
                }
                if (value.equalsIgnoreCase("OPERATION")) {
                    this.emitter.setSoapAction("OPERATION");
                    break;
                }
                if (value.equalsIgnoreCase("NONE")) {
                    this.emitter.setSoapAction("NONE");
                    break;
                }
                System.out.println(Messages.getMessage("j2wBadSoapAction00"));
                status = false;
                break;
            }
            case 121: {
                String value = option.getArgument();
                if (value.equalsIgnoreCase("DOCUMENT") || value.equalsIgnoreCase("RPC") || value.equalsIgnoreCase("WRAPPED")) {
                    this.emitter.setStyle(value);
                    break;
                }
                System.out.println(Messages.getMessage("j2woptBadStyle00"));
                status = false;
                break;
            }
            case 117: {
                String value = option.getArgument();
                if (value.equalsIgnoreCase("LITERAL") || value.equalsIgnoreCase("ENCODED")) {
                    this.emitter.setUse(value);
                    break;
                }
                System.out.println(Messages.getMessage("j2woptBadUse00"));
                status = false;
                break;
            }
            case 101: {
                try {
                    this.emitter.setExtraClasses(option.getArgument());
                }
                catch (ClassNotFoundException e) {
                    System.out.println(Messages.getMessage("j2woptBadClass00", e.toString()));
                    status = false;
                }
                break;
            }
            case 67: {
                this.emitter.setInputSchema(option.getArgument());
                break;
            }
            case 88: {
                ClassUtils.setDefaultClassLoader(ClassUtils.createClassLoader(option.getArgument(), this.getClass().getClassLoader()));
                break;
            }
            case 100: {
                this.isDeploy = true;
                break;
            }
        }
        return status;
    }

    protected boolean validateOptions() {
        if (this.className == null) {
            System.out.println(Messages.getMessage("j2wMissingClass00"));
            this.printUsage();
            return false;
        }
        if (!(this.locationSet || this.mode != 0 && this.mode != 2)) {
            System.out.println(Messages.getMessage("j2wMissingLocation00"));
            this.printUsage();
            return false;
        }
        return true;
    }

    protected int run(String[] args) {
        CLArgsParser argsParser = new CLArgsParser(args, this.options);
        if (null != argsParser.getErrorString()) {
            System.err.println(Messages.getMessage("j2werror00", argsParser.getErrorString()));
            this.printUsage();
            return 1;
        }
        Vector clOptions = argsParser.getArguments();
        int size = clOptions.size();
        try {
            for (int i = 0; i < size; ++i) {
                if (this.parseOption((CLOption)clOptions.get(i))) continue;
                return 1;
            }
            if (!this.validateOptions()) {
                return 1;
            }
            if (!this.namespaceMap.isEmpty()) {
                this.emitter.setNamespaceMap(this.namespaceMap);
            }
            TypeMappingRegistryImpl tmr = new TypeMappingRegistryImpl();
            tmr.doRegisterFromVersion(this.typeMappingVersion);
            this.emitter.setTypeMappingRegistry(tmr);
            this.emitter.setCls(this.className);
            if (this.wsdlImplFilename == null) {
                this.emitter.emit(this.wsdlFilename, this.mode);
            } else {
                this.emitter.emit(this.wsdlFilename, this.wsdlImplFilename);
            }
            if (this.isDeploy) {
                this.generateServerSide(this.emitter, this.wsdlImplFilename != null ? this.wsdlImplFilename : this.wsdlFilename);
            }
            return 0;
        }
        catch (Throwable t) {
            t.printStackTrace();
            return 1;
        }
    }

    protected void generateServerSide(Emitter j2w, String wsdlFileName) throws Exception {
        org.apache.axis.wsdl.toJava.Emitter w2j = new org.apache.axis.wsdl.toJava.Emitter();
        File wsdlFile = new File(wsdlFileName);
        w2j.setServiceDesc(j2w.getServiceDesc());
        w2j.setQName2ClassMap(j2w.getQName2ClassMap());
        w2j.setOutputDir(wsdlFile.getParent());
        w2j.setServerSide(true);
        w2j.setHelperWanted(true);
        String ns = j2w.getIntfNamespace();
        String pkg = j2w.getCls().getPackage().getName();
        w2j.getNamespaceMap().put(ns, pkg);
        Map nsmap = j2w.getNamespaceMap();
        if (nsmap != null) {
            Iterator i = nsmap.keySet().iterator();
            while (i.hasNext()) {
                pkg = (String)i.next();
                ns = (String)nsmap.get(pkg);
                w2j.getNamespaceMap().put(ns, pkg);
            }
        }
        w2j.setDeploy(true);
        if (j2w.getImplCls() != null) {
            w2j.setImplementationClassName(j2w.getImplCls().getName());
        } else if (!j2w.getCls().isInterface()) {
            w2j.setImplementationClassName(j2w.getCls().getName());
        } else {
            throw new Exception("implementation class is not specified.");
        }
        w2j.run(wsdlFileName);
    }

    protected void printUsage() {
        String lSep = System.getProperty("line.separator");
        StringBuffer msg = new StringBuffer();
        msg.append("Java2WSDL " + Messages.getMessage("j2wemitter00")).append(lSep);
        msg.append(Messages.getMessage("j2wusage00", "java " + this.getClass().getName() + " [options] class-of-portType")).append(lSep);
        msg.append(Messages.getMessage("j2woptions00")).append(lSep);
        msg.append(CLUtil.describeOptions(this.options).toString());
        msg.append(Messages.getMessage("j2wdetails00")).append(lSep);
        System.out.println(msg.toString());
    }

    public static void main(String[] args) {
        Java2WSDL java2wsdl = new Java2WSDL();
        System.exit(java2wsdl.run(args));
    }
}

