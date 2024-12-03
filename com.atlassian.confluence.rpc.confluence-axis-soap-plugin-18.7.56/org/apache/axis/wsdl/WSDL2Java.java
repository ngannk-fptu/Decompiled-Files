/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl;

import org.apache.axis.constants.Scope;
import org.apache.axis.utils.CLOption;
import org.apache.axis.utils.CLOptionDescriptor;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.gen.Parser;
import org.apache.axis.wsdl.gen.WSDL2;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.NamespaceSelector;

public class WSDL2Java
extends WSDL2 {
    protected static final int SERVER_OPT = 115;
    protected static final int SKELETON_DEPLOY_OPT = 83;
    protected static final int NAMESPACE_OPT = 78;
    protected static final int NAMESPACE_FILE_OPT = 102;
    protected static final int OUTPUT_OPT = 111;
    protected static final int SCOPE_OPT = 100;
    protected static final int TEST_OPT = 116;
    protected static final int BUILDFILE_OPT = 66;
    protected static final int PACKAGE_OPT = 112;
    protected static final int ALL_OPT = 97;
    protected static final int TYPEMAPPING_OPT = 84;
    protected static final int FACTORY_CLASS_OPT = 70;
    protected static final int HELPER_CLASS_OPT = 72;
    protected static final int USERNAME_OPT = 85;
    protected static final int PASSWORD_OPT = 80;
    protected static final int CLASSPATH_OPT = 88;
    protected boolean bPackageOpt = false;
    protected static final int NS_INCLUDE_OPT = 105;
    protected static final int NS_EXCLUDE_OPT = 120;
    protected static final int IMPL_CLASS_OPT = 99;
    protected static final int ALLOW_INVALID_URL_OPT = 117;
    protected static final int WRAP_ARRAYS_OPT = 119;
    private Emitter emitter = (Emitter)this.parser;
    protected static final CLOptionDescriptor[] options = new CLOptionDescriptor[]{new CLOptionDescriptor("server-side", 8, 115, Messages.getMessage("optionSkel00")), new CLOptionDescriptor("skeletonDeploy", 2, 83, Messages.getMessage("optionSkeletonDeploy00")), new CLOptionDescriptor("NStoPkg", 48, 78, Messages.getMessage("optionNStoPkg00")), new CLOptionDescriptor("fileNStoPkg", 2, 102, Messages.getMessage("optionFileNStoPkg00")), new CLOptionDescriptor("package", 2, 112, Messages.getMessage("optionPackage00")), new CLOptionDescriptor("output", 2, 111, Messages.getMessage("optionOutput00")), new CLOptionDescriptor("deployScope", 2, 100, Messages.getMessage("optionScope00")), new CLOptionDescriptor("testCase", 8, 116, Messages.getMessage("optionTest00")), new CLOptionDescriptor("all", 8, 97, Messages.getMessage("optionAll00")), new CLOptionDescriptor("typeMappingVersion", 2, 84, Messages.getMessage("optionTypeMapping00")), new CLOptionDescriptor("factory", 2, 70, Messages.getMessage("optionFactory00")), new CLOptionDescriptor("helperGen", 8, 72, Messages.getMessage("optionHelper00")), new CLOptionDescriptor("buildFile", 8, 66, Messages.getMessage("optionBuildFile00")), new CLOptionDescriptor("user", 2, 85, Messages.getMessage("optionUsername")), new CLOptionDescriptor("password", 2, 80, Messages.getMessage("optionPassword")), new CLOptionDescriptor("classpath", 4, 88, Messages.getMessage("optionClasspath")), new CLOptionDescriptor("nsInclude", 34, 105, Messages.getMessage("optionNSInclude")), new CLOptionDescriptor("nsExclude", 34, 120, Messages.getMessage("optionNSExclude")), new CLOptionDescriptor("implementationClassName", 2, 99, Messages.getMessage("implementationClassName")), new CLOptionDescriptor("allowInvalidURL", 8, 117, Messages.getMessage("optionAllowInvalidURL")), new CLOptionDescriptor("wrapArrays", 4, 119, Messages.getMessage("optionWrapArrays"))};

    protected WSDL2Java() {
        this.addOptions(options);
    }

    protected Parser createParser() {
        return new Emitter();
    }

    protected void parseOption(CLOption option) {
        switch (option.getId()) {
            case 70: {
                this.emitter.setFactory(option.getArgument());
                break;
            }
            case 72: {
                this.emitter.setHelperWanted(true);
                break;
            }
            case 83: {
                this.emitter.setSkeletonWanted(JavaUtils.isTrueExplicitly(option.getArgument(0)));
            }
            case 115: {
                this.emitter.setServerSide(true);
                break;
            }
            case 78: {
                String namespace = option.getArgument(0);
                String packageName = option.getArgument(1);
                this.emitter.getNamespaceMap().put(namespace, packageName);
                break;
            }
            case 102: {
                this.emitter.setNStoPkg(option.getArgument());
                break;
            }
            case 112: {
                this.bPackageOpt = true;
                this.emitter.setPackageName(option.getArgument());
                break;
            }
            case 111: {
                this.emitter.setOutputDir(option.getArgument());
                break;
            }
            case 100: {
                String arg = option.getArgument();
                Scope scope = Scope.getScope(arg, null);
                if (scope != null) {
                    this.emitter.setScope(scope);
                    break;
                }
                System.err.println(Messages.getMessage("badScope00", arg));
                break;
            }
            case 116: {
                this.emitter.setTestCaseWanted(true);
                break;
            }
            case 66: {
                this.emitter.setBuildFileWanted(true);
                break;
            }
            case 97: {
                this.emitter.setAllWanted(true);
                break;
            }
            case 84: {
                String tmValue = option.getArgument();
                if (tmValue.equals("1.0")) {
                    this.emitter.setTypeMappingVersion("1.0");
                    break;
                }
                if (tmValue.equals("1.1")) {
                    this.emitter.setTypeMappingVersion("1.1");
                    break;
                }
                if (tmValue.equals("1.2")) {
                    this.emitter.setTypeMappingVersion("1.2");
                    break;
                }
                if (tmValue.equals("1.3")) {
                    this.emitter.setTypeMappingVersion("1.3");
                    break;
                }
                System.out.println(Messages.getMessage("badTypeMappingOption00"));
                break;
            }
            case 85: {
                this.emitter.setUsername(option.getArgument());
                break;
            }
            case 80: {
                this.emitter.setPassword(option.getArgument());
                break;
            }
            case 88: {
                ClassUtils.setDefaultClassLoader(ClassUtils.createClassLoader(option.getArgument(), this.getClass().getClassLoader()));
                break;
            }
            case 105: {
                NamespaceSelector include = new NamespaceSelector();
                include.setNamespace(option.getArgument());
                this.emitter.getNamespaceIncludes().add(include);
                break;
            }
            case 120: {
                NamespaceSelector exclude = new NamespaceSelector();
                exclude.setNamespace(option.getArgument());
                this.emitter.getNamespaceExcludes().add(exclude);
                break;
            }
            case 99: {
                this.emitter.setImplementationClassName(option.getArgument());
                break;
            }
            case 117: {
                this.emitter.setAllowInvalidURL(true);
                break;
            }
            case 119: {
                this.emitter.setWrapArrays(true);
                break;
            }
            default: {
                super.parseOption(option);
            }
        }
    }

    protected void validateOptions() {
        super.validateOptions();
        if (this.emitter.isSkeletonWanted() && !this.emitter.isServerSide()) {
            System.out.println(Messages.getMessage("badSkeleton00"));
            this.printUsage();
        }
        if (!this.emitter.getNamespaceMap().isEmpty() && this.bPackageOpt) {
            System.out.println(Messages.getMessage("badpackage00"));
            this.printUsage();
        }
    }

    public static void main(String[] args) {
        WSDL2Java wsdl2java = new WSDL2Java();
        wsdl2java.run(args);
    }
}

