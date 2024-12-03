/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.asm;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.aspectj.asm.AsmManager;
import org.aspectj.asm.HierarchyWalker;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.ISourceLocation;

public interface IProgramElement
extends Serializable {
    public List<IProgramElement> getChildren();

    public void setChildren(List<IProgramElement> var1);

    public void addChild(IProgramElement var1);

    public boolean removeChild(IProgramElement var1);

    public void setExtraInfo(ExtraInformation var1);

    public ExtraInformation getExtraInfo();

    public IProgramElement getParent();

    public void setParent(IProgramElement var1);

    public void setParentTypes(List<String> var1);

    public List<String> getParentTypes();

    public String getName();

    public void setName(String var1);

    public String getDetails();

    public void setDetails(String var1);

    public Kind getKind();

    public void setKind(Kind var1);

    public List<Modifiers> getModifiers();

    public void setModifiers(int var1);

    public Accessibility getAccessibility();

    public String getDeclaringType();

    public String getPackageName();

    public void setCorrespondingType(String var1);

    public String getCorrespondingType();

    public String getCorrespondingType(boolean var1);

    public String toSignatureString();

    public String toSignatureString(boolean var1);

    public void setRunnable(boolean var1);

    public boolean isRunnable();

    public boolean isImplementor();

    public void setImplementor(boolean var1);

    public boolean isOverrider();

    public void setOverrider(boolean var1);

    public IMessage getMessage();

    public void setMessage(IMessage var1);

    public ISourceLocation getSourceLocation();

    public void setSourceLocation(ISourceLocation var1);

    public String toString();

    public String getFormalComment();

    public void setFormalComment(String var1);

    public String toLinkLabelString();

    public String toLinkLabelString(boolean var1);

    public String toLabelString();

    public String toLabelString(boolean var1);

    public List<String> getParameterNames();

    public void setParameterNames(List<String> var1);

    public List<char[]> getParameterSignatures();

    public List<String> getParameterSignaturesSourceRefs();

    public void setParameterSignatures(List<char[]> var1, List<String> var2);

    public List<char[]> getParameterTypes();

    public String getHandleIdentifier();

    public String getHandleIdentifier(boolean var1);

    public void setHandleIdentifier(String var1);

    public String toLongString();

    public String getBytecodeName();

    public String getBytecodeSignature();

    public void setBytecodeName(String var1);

    public void setBytecodeSignature(String var1);

    public String getSourceSignature();

    public void setSourceSignature(String var1);

    public IProgramElement walk(HierarchyWalker var1);

    public AsmManager getModel();

    public int getRawModifiers();

    public void setAnnotationStyleDeclaration(boolean var1);

    public boolean isAnnotationStyleDeclaration();

    public void setAnnotationType(String var1);

    public String getAnnotationType();

    public String[] getRemovedAnnotationTypes();

    public Map<String, List<String>> getDeclareParentsMap();

    public void setDeclareParentsMap(Map<String, List<String>> var1);

    public void addFullyQualifiedName(String var1);

    public String getFullyQualifiedName();

    public void setAnnotationRemover(boolean var1);

    public boolean isAnnotationRemover();

    public String getCorrespondingTypeSignature();

    public static class Kind
    implements Serializable {
        private static final long serialVersionUID = -1963553877479266124L;
        public static final Kind PROJECT = new Kind("project");
        public static final Kind PACKAGE = new Kind("package");
        public static final Kind FILE = new Kind("file");
        public static final Kind FILE_JAVA = new Kind("java source file");
        public static final Kind FILE_ASPECTJ = new Kind("aspect source file");
        public static final Kind FILE_LST = new Kind("build configuration file");
        public static final Kind IMPORT_REFERENCE = new Kind("import reference");
        public static final Kind CLASS = new Kind("class");
        public static final Kind INTERFACE = new Kind("interface");
        public static final Kind ASPECT = new Kind("aspect");
        public static final Kind ENUM = new Kind("enum");
        public static final Kind ENUM_VALUE = new Kind("enumvalue");
        public static final Kind ANNOTATION = new Kind("annotation");
        public static final Kind INITIALIZER = new Kind("initializer");
        public static final Kind INTER_TYPE_FIELD = new Kind("inter-type field");
        public static final Kind INTER_TYPE_METHOD = new Kind("inter-type method");
        public static final Kind INTER_TYPE_CONSTRUCTOR = new Kind("inter-type constructor");
        public static final Kind INTER_TYPE_PARENT = new Kind("inter-type parent");
        public static final Kind CONSTRUCTOR = new Kind("constructor");
        public static final Kind METHOD = new Kind("method");
        public static final Kind FIELD = new Kind("field");
        public static final Kind POINTCUT = new Kind("pointcut");
        public static final Kind ADVICE = new Kind("advice");
        public static final Kind DECLARE_PARENTS = new Kind("declare parents");
        public static final Kind DECLARE_WARNING = new Kind("declare warning");
        public static final Kind DECLARE_ERROR = new Kind("declare error");
        public static final Kind DECLARE_SOFT = new Kind("declare soft");
        public static final Kind DECLARE_PRECEDENCE = new Kind("declare precedence");
        public static final Kind CODE = new Kind("code");
        public static final Kind ERROR = new Kind("error");
        public static final Kind DECLARE_ANNOTATION_AT_CONSTRUCTOR = new Kind("declare @constructor");
        public static final Kind DECLARE_ANNOTATION_AT_FIELD = new Kind("declare @field");
        public static final Kind DECLARE_ANNOTATION_AT_METHOD = new Kind("declare @method");
        public static final Kind DECLARE_ANNOTATION_AT_TYPE = new Kind("declare @type");
        public static final Kind SOURCE_FOLDER = new Kind("source folder");
        public static final Kind PACKAGE_DECLARATION = new Kind("package declaration");
        public static final Kind[] ALL = new Kind[]{PROJECT, PACKAGE, FILE, FILE_JAVA, FILE_ASPECTJ, FILE_LST, IMPORT_REFERENCE, CLASS, INTERFACE, ASPECT, ENUM, ENUM_VALUE, ANNOTATION, INITIALIZER, INTER_TYPE_FIELD, INTER_TYPE_METHOD, INTER_TYPE_CONSTRUCTOR, INTER_TYPE_PARENT, CONSTRUCTOR, METHOD, FIELD, POINTCUT, ADVICE, DECLARE_PARENTS, DECLARE_WARNING, DECLARE_ERROR, DECLARE_SOFT, DECLARE_PRECEDENCE, CODE, ERROR, DECLARE_ANNOTATION_AT_CONSTRUCTOR, DECLARE_ANNOTATION_AT_FIELD, DECLARE_ANNOTATION_AT_METHOD, DECLARE_ANNOTATION_AT_TYPE, SOURCE_FOLDER, PACKAGE_DECLARATION};
        private final String name;
        private static int nextOrdinal = 0;
        private final int ordinal = nextOrdinal++;

        public static Kind getKindForString(String kindString) {
            for (int i = 0; i < ALL.length; ++i) {
                if (!ALL[i].toString().equals(kindString)) continue;
                return ALL[i];
            }
            return ERROR;
        }

        private Kind(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public static List<Kind> getNonAJMemberKinds() {
            ArrayList<Kind> list = new ArrayList<Kind>();
            list.add(METHOD);
            list.add(ENUM_VALUE);
            list.add(FIELD);
            list.add(CONSTRUCTOR);
            return list;
        }

        public boolean isMember() {
            return this == FIELD || this == METHOD || this == CONSTRUCTOR || this == POINTCUT || this == ADVICE || this == ENUM_VALUE;
        }

        public boolean isInterTypeMember() {
            return this == INTER_TYPE_CONSTRUCTOR || this == INTER_TYPE_FIELD || this == INTER_TYPE_METHOD;
        }

        public boolean isType() {
            return this == CLASS || this == INTERFACE || this == ASPECT || this == ANNOTATION || this == ENUM;
        }

        public boolean isSourceFile() {
            return this == FILE_ASPECTJ || this == FILE_JAVA;
        }

        public boolean isFile() {
            return this == FILE;
        }

        public boolean isDeclare() {
            return this.name.startsWith("declare");
        }

        public boolean isDeclareAnnotation() {
            return this.name.startsWith("declare @");
        }

        public boolean isDeclareParents() {
            return this.name.startsWith("declare parents");
        }

        public boolean isDeclareSoft() {
            return this.name.startsWith("declare soft");
        }

        public boolean isDeclareWarning() {
            return this.name.startsWith("declare warning");
        }

        public boolean isDeclareError() {
            return this.name.startsWith("declare error");
        }

        public boolean isDeclarePrecedence() {
            return this.name.startsWith("declare precedence");
        }

        private Object readResolve() throws ObjectStreamException {
            return ALL[this.ordinal];
        }

        public boolean isPackageDeclaration() {
            return this == PACKAGE_DECLARATION;
        }
    }

    public static class Accessibility
    implements Serializable {
        private static final long serialVersionUID = 5371838588180918519L;
        public static final Accessibility PUBLIC = new Accessibility("public");
        public static final Accessibility PACKAGE = new Accessibility("package");
        public static final Accessibility PROTECTED = new Accessibility("protected");
        public static final Accessibility PRIVATE = new Accessibility("private");
        public static final Accessibility PRIVILEGED = new Accessibility("privileged");
        public static final Accessibility[] ALL = new Accessibility[]{PUBLIC, PACKAGE, PROTECTED, PRIVATE, PRIVILEGED};
        private final String name;
        private static int nextOrdinal = 0;
        private final int ordinal = nextOrdinal++;

        private Accessibility(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        private Object readResolve() throws ObjectStreamException {
            return ALL[this.ordinal];
        }
    }

    public static class Modifiers
    implements Serializable {
        private static final long serialVersionUID = -8279300899976607927L;
        public static final Modifiers STATIC = new Modifiers("static", 8);
        public static final Modifiers FINAL = new Modifiers("final", 16);
        public static final Modifiers ABSTRACT = new Modifiers("abstract", 1024);
        public static final Modifiers SYNCHRONIZED = new Modifiers("synchronized", 32);
        public static final Modifiers VOLATILE = new Modifiers("volatile", 64);
        public static final Modifiers STRICTFP = new Modifiers("strictfp", 2048);
        public static final Modifiers TRANSIENT = new Modifiers("transient", 128);
        public static final Modifiers NATIVE = new Modifiers("native", 256);
        public static final Modifiers[] ALL = new Modifiers[]{STATIC, FINAL, ABSTRACT, SYNCHRONIZED, VOLATILE, STRICTFP, TRANSIENT, NATIVE};
        private final String name;
        private final int bit;
        private static int nextOrdinal = 0;
        private final int ordinal = nextOrdinal++;

        private Modifiers(String name, int bit) {
            this.name = name;
            this.bit = bit;
        }

        public String toString() {
            return this.name;
        }

        public int getBit() {
            return this.bit;
        }

        private Object readResolve() throws ObjectStreamException {
            return ALL[this.ordinal];
        }
    }

    public static class ExtraInformation
    implements Serializable {
        private static final long serialVersionUID = -3880735494840820638L;
        private String extraInfo = "";

        public void setExtraAdviceInformation(String string) {
            this.extraInfo = string;
        }

        public String getExtraAdviceInformation() {
            return this.extraInfo;
        }

        public String toString() {
            return "ExtraInformation: [" + this.extraInfo + "]";
        }
    }
}

