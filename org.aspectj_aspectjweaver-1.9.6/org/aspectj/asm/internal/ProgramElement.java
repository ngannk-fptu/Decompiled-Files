/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.asm.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.aspectj.asm.AsmManager;
import org.aspectj.asm.HierarchyWalker;
import org.aspectj.asm.IProgramElement;
import org.aspectj.asm.internal.CharOperation;
import org.aspectj.asm.internal.NameConvertor;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.ISourceLocation;

public class ProgramElement
implements IProgramElement {
    public transient AsmManager asm;
    private static final long serialVersionUID = 171673495267384449L;
    public static boolean shortITDNames = true;
    private static final String UNDEFINED = "<undefined>";
    private static final int AccPublic = 1;
    private static final int AccPrivate = 2;
    private static final int AccProtected = 4;
    private static final int AccPrivileged = 6;
    private static final int AccStatic = 8;
    private static final int AccFinal = 16;
    private static final int AccSynchronized = 32;
    private static final int AccVolatile = 64;
    private static final int AccTransient = 128;
    private static final int AccNative = 256;
    private static final int AccAbstract = 1024;
    protected String name;
    private IProgramElement.Kind kind;
    protected IProgramElement parent = null;
    protected List<IProgramElement> children = Collections.emptyList();
    public Map<String, Object> kvpairs = Collections.emptyMap();
    protected ISourceLocation sourceLocation = null;
    public int modifiers;
    private String handle = null;

    @Override
    public AsmManager getModel() {
        return this.asm;
    }

    public ProgramElement() {
    }

    public ProgramElement(AsmManager asm, String name, IProgramElement.Kind kind, List<IProgramElement> children) {
        this.asm = asm;
        if (asm == null && !name.equals("<build to view structure>")) {
            throw new RuntimeException();
        }
        this.name = name;
        this.kind = kind;
        if (children != null) {
            this.setChildren(children);
        }
    }

    public ProgramElement(AsmManager asm, String name, IProgramElement.Kind kind, ISourceLocation sourceLocation, int modifiers, String comment, List<IProgramElement> children) {
        this(asm, name, kind, children);
        this.sourceLocation = sourceLocation;
        this.setFormalComment(comment);
        this.modifiers = modifiers;
    }

    @Override
    public int getRawModifiers() {
        return this.modifiers;
    }

    @Override
    public List<IProgramElement.Modifiers> getModifiers() {
        return ProgramElement.genModifiers(this.modifiers);
    }

    @Override
    public IProgramElement.Accessibility getAccessibility() {
        return ProgramElement.genAccessibility(this.modifiers);
    }

    public void setDeclaringType(String t) {
        if (t != null && t.length() > 0) {
            this.fixMap();
            this.kvpairs.put("declaringType", t);
        }
    }

    @Override
    public String getDeclaringType() {
        String dt = (String)this.kvpairs.get("declaringType");
        if (dt == null) {
            return "";
        }
        return dt;
    }

    @Override
    public String getPackageName() {
        if (this.kind == IProgramElement.Kind.PACKAGE) {
            return this.getName();
        }
        if (this.getParent() == null) {
            return "";
        }
        return this.getParent().getPackageName();
    }

    @Override
    public IProgramElement.Kind getKind() {
        return this.kind;
    }

    public boolean isCode() {
        return this.kind.equals(IProgramElement.Kind.CODE);
    }

    @Override
    public ISourceLocation getSourceLocation() {
        return this.sourceLocation;
    }

    @Override
    public void setSourceLocation(ISourceLocation sourceLocation) {
    }

    @Override
    public IMessage getMessage() {
        return (IMessage)this.kvpairs.get("message");
    }

    @Override
    public void setMessage(IMessage message) {
        this.fixMap();
        this.kvpairs.put("message", message);
    }

    @Override
    public IProgramElement getParent() {
        return this.parent;
    }

    @Override
    public void setParent(IProgramElement parent) {
        this.parent = parent;
    }

    public boolean isMemberKind() {
        return this.kind.isMember();
    }

    @Override
    public void setRunnable(boolean value) {
        this.fixMap();
        if (value) {
            this.kvpairs.put("isRunnable", "true");
        } else {
            this.kvpairs.remove("isRunnable");
        }
    }

    @Override
    public boolean isRunnable() {
        return this.kvpairs.get("isRunnable") != null;
    }

    @Override
    public boolean isImplementor() {
        return this.kvpairs.get("isImplementor") != null;
    }

    @Override
    public void setImplementor(boolean value) {
        this.fixMap();
        if (value) {
            this.kvpairs.put("isImplementor", "true");
        } else {
            this.kvpairs.remove("isImplementor");
        }
    }

    @Override
    public boolean isOverrider() {
        return this.kvpairs.get("isOverrider") != null;
    }

    @Override
    public void setOverrider(boolean value) {
        this.fixMap();
        if (value) {
            this.kvpairs.put("isOverrider", "true");
        } else {
            this.kvpairs.remove("isOverrider");
        }
    }

    @Override
    public String getFormalComment() {
        return (String)this.kvpairs.get("formalComment");
    }

    @Override
    public String toString() {
        return this.toLabelString();
    }

    private static List<IProgramElement.Modifiers> genModifiers(int modifiers) {
        ArrayList<IProgramElement.Modifiers> modifiersList = new ArrayList<IProgramElement.Modifiers>();
        if ((modifiers & 8) != 0) {
            modifiersList.add(IProgramElement.Modifiers.STATIC);
        }
        if ((modifiers & 0x10) != 0) {
            modifiersList.add(IProgramElement.Modifiers.FINAL);
        }
        if ((modifiers & 0x20) != 0) {
            modifiersList.add(IProgramElement.Modifiers.SYNCHRONIZED);
        }
        if ((modifiers & 0x40) != 0) {
            modifiersList.add(IProgramElement.Modifiers.VOLATILE);
        }
        if ((modifiers & 0x80) != 0) {
            modifiersList.add(IProgramElement.Modifiers.TRANSIENT);
        }
        if ((modifiers & 0x100) != 0) {
            modifiersList.add(IProgramElement.Modifiers.NATIVE);
        }
        if ((modifiers & 0x400) != 0) {
            modifiersList.add(IProgramElement.Modifiers.ABSTRACT);
        }
        return modifiersList;
    }

    public static IProgramElement.Accessibility genAccessibility(int modifiers) {
        if ((modifiers & 1) != 0) {
            return IProgramElement.Accessibility.PUBLIC;
        }
        if ((modifiers & 2) != 0) {
            return IProgramElement.Accessibility.PRIVATE;
        }
        if ((modifiers & 4) != 0) {
            return IProgramElement.Accessibility.PROTECTED;
        }
        if ((modifiers & 6) != 0) {
            return IProgramElement.Accessibility.PRIVILEGED;
        }
        return IProgramElement.Accessibility.PACKAGE;
    }

    @Override
    public String getBytecodeName() {
        String s = (String)this.kvpairs.get("bytecodeName");
        if (s == null) {
            return UNDEFINED;
        }
        return s;
    }

    @Override
    public void setBytecodeName(String s) {
        this.fixMap();
        this.kvpairs.put("bytecodeName", s);
    }

    @Override
    public void setBytecodeSignature(String s) {
        this.fixMap();
        this.kvpairs.put("bytecodeSignature", s);
    }

    @Override
    public String getBytecodeSignature() {
        String s = (String)this.kvpairs.get("bytecodeSignature");
        return s;
    }

    @Override
    public String getSourceSignature() {
        return (String)this.kvpairs.get("sourceSignature");
    }

    @Override
    public void setSourceSignature(String string) {
        this.fixMap();
        this.kvpairs.put("sourceSignature", string);
    }

    @Override
    public void setKind(IProgramElement.Kind kind) {
        this.kind = kind;
    }

    @Override
    public void setCorrespondingType(String s) {
        this.fixMap();
        this.kvpairs.put("returnType", s);
    }

    @Override
    public void setParentTypes(List<String> ps) {
        this.fixMap();
        this.kvpairs.put("parentTypes", ps);
    }

    @Override
    public List<String> getParentTypes() {
        return (List)(this.kvpairs == null ? null : this.kvpairs.get("parentTypes"));
    }

    @Override
    public void setAnnotationType(String fullyQualifiedAnnotationType) {
        this.fixMap();
        this.kvpairs.put("annotationType", fullyQualifiedAnnotationType);
    }

    @Override
    public void setAnnotationRemover(boolean isRemover) {
        this.fixMap();
        this.kvpairs.put("annotationRemover", isRemover);
    }

    @Override
    public String getAnnotationType() {
        if (this.isAnnotationRemover()) {
            return null;
        }
        return (String)(this.kvpairs == null ? null : this.kvpairs.get("annotationType"));
    }

    @Override
    public boolean isAnnotationRemover() {
        if (this.kvpairs == null) {
            return false;
        }
        Boolean b = (Boolean)this.kvpairs.get("annotationRemover");
        if (b == null) {
            return false;
        }
        return b;
    }

    @Override
    public String[] getRemovedAnnotationTypes() {
        if (!this.isAnnotationRemover()) {
            return null;
        }
        String annotype = (String)(this.kvpairs == null ? null : this.kvpairs.get("annotationType"));
        if (annotype == null) {
            return null;
        }
        return new String[]{annotype};
    }

    @Override
    public String getCorrespondingType() {
        return this.getCorrespondingType(false);
    }

    @Override
    public String getCorrespondingTypeSignature() {
        String typename = (String)this.kvpairs.get("returnType");
        if (typename == null) {
            return null;
        }
        return ProgramElement.nameToSignature(typename);
    }

    public static String nameToSignature(String name) {
        int len = name.length();
        if (len < 8) {
            if (name.equals("byte")) {
                return "B";
            }
            if (name.equals("char")) {
                return "C";
            }
            if (name.equals("double")) {
                return "D";
            }
            if (name.equals("float")) {
                return "F";
            }
            if (name.equals("int")) {
                return "I";
            }
            if (name.equals("long")) {
                return "J";
            }
            if (name.equals("short")) {
                return "S";
            }
            if (name.equals("boolean")) {
                return "Z";
            }
            if (name.equals("void")) {
                return "V";
            }
            if (name.equals("?")) {
                return name;
            }
        }
        if (name.endsWith("[]")) {
            return "[" + ProgramElement.nameToSignature(name.substring(0, name.length() - 2));
        }
        if (len != 0) {
            assert (name.charAt(0) != '[');
            if (name.indexOf("<") == -1) {
                return "L" + name.replace('.', '/') + ';';
            }
            StringBuffer nameBuff = new StringBuffer();
            int nestLevel = 0;
            nameBuff.append("L");
            block6: for (int i = 0; i < name.length(); ++i) {
                char c = name.charAt(i);
                switch (c) {
                    case '.': {
                        nameBuff.append('/');
                        continue block6;
                    }
                    case '<': {
                        nameBuff.append("<");
                        ++nestLevel;
                        StringBuffer innerBuff = new StringBuffer();
                        while (nestLevel > 0) {
                            if ((c = name.charAt(++i)) == '<') {
                                ++nestLevel;
                            }
                            if (c == '>') {
                                --nestLevel;
                            }
                            if (c == ',' && nestLevel == 1) {
                                nameBuff.append(ProgramElement.nameToSignature(innerBuff.toString()));
                                innerBuff = new StringBuffer();
                                continue;
                            }
                            if (nestLevel <= 0) continue;
                            innerBuff.append(c);
                        }
                        nameBuff.append(ProgramElement.nameToSignature(innerBuff.toString()));
                        nameBuff.append('>');
                        continue block6;
                    }
                    case '>': {
                        throw new IllegalStateException("Should by matched by <");
                    }
                    case ',': {
                        throw new IllegalStateException("Should only happen inside <...>");
                    }
                    default: {
                        nameBuff.append(c);
                    }
                }
            }
            nameBuff.append(";");
            return nameBuff.toString();
        }
        throw new IllegalArgumentException("Bad type name: " + name);
    }

    @Override
    public String getCorrespondingType(boolean getFullyQualifiedType) {
        String returnType = (String)this.kvpairs.get("returnType");
        if (returnType == null) {
            returnType = "";
        }
        if (getFullyQualifiedType) {
            return returnType;
        }
        return ProgramElement.trim(returnType);
    }

    public static String trim(String fqname) {
        int i = fqname.indexOf("<");
        if (i == -1) {
            int lastdot = fqname.lastIndexOf(46);
            if (lastdot == -1) {
                return fqname;
            }
            return fqname.substring(lastdot + 1);
        }
        char[] charArray = fqname.toCharArray();
        StringBuilder candidate = new StringBuilder(charArray.length);
        StringBuilder complete = new StringBuilder(charArray.length);
        block4: for (char c : charArray) {
            switch (c) {
                case '.': {
                    candidate.setLength(0);
                    continue block4;
                }
                case ',': 
                case '<': 
                case '>': {
                    complete.append((CharSequence)candidate).append(c);
                    candidate.setLength(0);
                    continue block4;
                }
                default: {
                    candidate.append(c);
                }
            }
        }
        complete.append((CharSequence)candidate);
        return complete.toString();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<IProgramElement> getChildren() {
        return this.children;
    }

    @Override
    public void setChildren(List<IProgramElement> children) {
        this.children = children;
        if (children == null) {
            return;
        }
        Iterator<IProgramElement> it = children.iterator();
        while (it.hasNext()) {
            it.next().setParent(this);
        }
    }

    @Override
    public void addChild(IProgramElement child) {
        if (this.children == null || this.children == Collections.EMPTY_LIST) {
            this.children = new ArrayList<IProgramElement>();
        }
        this.children.add(child);
        child.setParent(this);
    }

    public void addChild(int position, IProgramElement child) {
        if (this.children == null || this.children == Collections.EMPTY_LIST) {
            this.children = new ArrayList<IProgramElement>();
        }
        this.children.add(position, child);
        child.setParent(this);
    }

    @Override
    public boolean removeChild(IProgramElement child) {
        child.setParent(null);
        return this.children.remove(child);
    }

    @Override
    public void setName(String string) {
        this.name = string;
    }

    @Override
    public IProgramElement walk(HierarchyWalker walker) {
        if (this.children != null) {
            for (IProgramElement child : this.children) {
                walker.process(child);
            }
        }
        return this;
    }

    @Override
    public String toLongString() {
        final StringBuffer buffer = new StringBuffer();
        HierarchyWalker walker = new HierarchyWalker(){
            private int depth = 0;

            @Override
            public void preProcess(IProgramElement node) {
                for (int i = 0; i < this.depth; ++i) {
                    buffer.append(' ');
                }
                buffer.append(node.toString());
                buffer.append('\n');
                this.depth += 2;
            }

            @Override
            public void postProcess(IProgramElement node) {
                this.depth -= 2;
            }
        };
        walker.process(this);
        return buffer.toString();
    }

    @Override
    public void setModifiers(int i) {
        this.modifiers = i;
    }

    public void addModifiers(IProgramElement.Modifiers newModifier) {
        this.modifiers |= newModifier.getBit();
    }

    @Override
    public String toSignatureString() {
        return this.toSignatureString(true);
    }

    @Override
    public String toSignatureString(boolean getFullyQualifiedArgTypes) {
        StringBuffer sb = new StringBuffer();
        sb.append(this.name);
        List<char[]> ptypes = this.getParameterTypes();
        if (ptypes != null && (!ptypes.isEmpty() || this.kind.equals(IProgramElement.Kind.METHOD)) || this.kind.equals(IProgramElement.Kind.CONSTRUCTOR) || this.kind.equals(IProgramElement.Kind.ADVICE) || this.kind.equals(IProgramElement.Kind.POINTCUT) || this.kind.equals(IProgramElement.Kind.INTER_TYPE_METHOD) || this.kind.equals(IProgramElement.Kind.INTER_TYPE_CONSTRUCTOR)) {
            sb.append('(');
            Iterator<char[]> it = ptypes.iterator();
            while (it.hasNext()) {
                char[] arg = it.next();
                if (getFullyQualifiedArgTypes) {
                    sb.append(arg);
                } else {
                    int index = CharOperation.lastIndexOf('.', arg);
                    if (index != -1) {
                        sb.append(CharOperation.subarray(arg, index + 1, arg.length));
                    } else {
                        sb.append(arg);
                    }
                }
                if (!it.hasNext()) continue;
                sb.append(",");
            }
            sb.append(')');
        }
        return sb.toString();
    }

    @Override
    public String toLinkLabelString() {
        return this.toLinkLabelString(true);
    }

    @Override
    public String toLinkLabelString(boolean getFullyQualifiedArgTypes) {
        String label;
        if (this.kind == IProgramElement.Kind.CODE || this.kind == IProgramElement.Kind.INITIALIZER) {
            label = this.parent.getParent().getName() + ": ";
        } else if (this.kind.isInterTypeMember()) {
            if (shortITDNames) {
                label = "";
            } else {
                int dotIndex = this.name.indexOf(46);
                if (dotIndex != -1) {
                    return this.parent.getName() + ": " + this.toLabelString().substring(dotIndex + 1);
                }
                label = this.parent.getName() + '.';
            }
        } else {
            label = this.kind == IProgramElement.Kind.CLASS || this.kind == IProgramElement.Kind.ASPECT || this.kind == IProgramElement.Kind.INTERFACE ? "" : (this.kind.equals(IProgramElement.Kind.DECLARE_PARENTS) ? "" : (this.parent != null ? this.parent.getName() + '.' : "injar aspect: "));
        }
        label = label + this.toLabelString(getFullyQualifiedArgTypes);
        return label;
    }

    @Override
    public String toLabelString() {
        return this.toLabelString(true);
    }

    @Override
    public String toLabelString(boolean getFullyQualifiedArgTypes) {
        String label = this.toSignatureString(getFullyQualifiedArgTypes);
        String details = this.getDetails();
        if (details != null) {
            label = label + ": " + details;
        }
        return label;
    }

    @Override
    public String getHandleIdentifier() {
        return this.getHandleIdentifier(true);
    }

    @Override
    public String getHandleIdentifier(boolean create) {
        String h = this.handle;
        if (null == this.handle && create) {
            if (this.asm == null && this.name.equals("<build to view structure>")) {
                h = "<build to view structure>";
            } else {
                try {
                    h = this.asm.getHandleProvider().createHandleIdentifier(this);
                }
                catch (ArrayIndexOutOfBoundsException aioobe) {
                    throw new RuntimeException("AIOOBE whilst building handle for " + this, aioobe);
                }
            }
        }
        this.setHandleIdentifier(h);
        return h;
    }

    @Override
    public void setHandleIdentifier(String handle) {
        this.handle = handle;
    }

    @Override
    public List<String> getParameterNames() {
        List parameterNames = (List)this.kvpairs.get("parameterNames");
        return parameterNames;
    }

    @Override
    public void setParameterNames(List<String> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        this.fixMap();
        this.kvpairs.put("parameterNames", list);
    }

    @Override
    public List<char[]> getParameterTypes() {
        List<char[]> l = this.getParameterSignatures();
        if (l == null || l.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<char[]> params = new ArrayList<char[]>();
        for (char[] param : l) {
            params.add(NameConvertor.convertFromSignature(param));
        }
        return params;
    }

    @Override
    public List<char[]> getParameterSignatures() {
        List parameters = (List)this.kvpairs.get("parameterSigs");
        return parameters;
    }

    @Override
    public List<String> getParameterSignaturesSourceRefs() {
        List parameters = (List)this.kvpairs.get("parameterSigsSourceRefs");
        return parameters;
    }

    @Override
    public void setParameterSignatures(List<char[]> list, List<String> sourceRefs) {
        this.fixMap();
        if (list == null || list.size() == 0) {
            this.kvpairs.put("parameterSigs", Collections.EMPTY_LIST);
        } else {
            this.kvpairs.put("parameterSigs", list);
        }
        if (sourceRefs != null && sourceRefs.size() != 0) {
            this.kvpairs.put("parameterSigsSourceRefs", sourceRefs);
        }
    }

    @Override
    public String getDetails() {
        String details = (String)this.kvpairs.get("details");
        return details;
    }

    @Override
    public void setDetails(String string) {
        this.fixMap();
        this.kvpairs.put("details", string);
    }

    @Override
    public void setFormalComment(String txt) {
        if (txt != null && txt.length() > 0) {
            this.fixMap();
            this.kvpairs.put("formalComment", txt);
        }
    }

    private void fixMap() {
        if (this.kvpairs == Collections.EMPTY_MAP) {
            this.kvpairs = new HashMap<String, Object>();
        }
    }

    @Override
    public void setExtraInfo(IProgramElement.ExtraInformation info) {
        this.fixMap();
        this.kvpairs.put("ExtraInformation", info);
    }

    @Override
    public IProgramElement.ExtraInformation getExtraInfo() {
        return (IProgramElement.ExtraInformation)this.kvpairs.get("ExtraInformation");
    }

    @Override
    public boolean isAnnotationStyleDeclaration() {
        return this.kvpairs.get("annotationStyleDeclaration") != null;
    }

    @Override
    public void setAnnotationStyleDeclaration(boolean b) {
        if (b) {
            this.fixMap();
            this.kvpairs.put("annotationStyleDeclaration", "true");
        }
    }

    @Override
    public Map<String, List<String>> getDeclareParentsMap() {
        Map s = (Map)this.kvpairs.get("declareparentsmap");
        return s;
    }

    @Override
    public void setDeclareParentsMap(Map<String, List<String>> newmap) {
        this.fixMap();
        this.kvpairs.put("declareparentsmap", newmap);
    }

    @Override
    public void addFullyQualifiedName(String fqname) {
        this.fixMap();
        this.kvpairs.put("itdfqname", fqname);
    }

    @Override
    public String getFullyQualifiedName() {
        return (String)this.kvpairs.get("itdfqname");
    }
}

