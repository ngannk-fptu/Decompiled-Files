/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.asm.internal;

import java.io.File;
import java.util.List;
import org.aspectj.asm.AsmManager;
import org.aspectj.asm.IElementHandleProvider;
import org.aspectj.asm.IProgramElement;
import org.aspectj.asm.internal.CharOperation;
import org.aspectj.asm.internal.HandleProviderDelimiter;
import org.aspectj.asm.internal.NameConvertor;
import org.aspectj.bridge.ISourceLocation;

public class JDTLikeHandleProvider
implements IElementHandleProvider {
    private final AsmManager asm;
    private static final char[] empty = new char[0];
    private static final char[] countDelim = new char[]{HandleProviderDelimiter.COUNT.getDelimiter()};
    private static final String backslash = "\\";
    private static final String emptyString = "";

    public JDTLikeHandleProvider(AsmManager asm) {
        this.asm = asm;
    }

    @Override
    public void initialize() {
    }

    @Override
    public String createHandleIdentifier(IProgramElement ipe) {
        if (ipe == null || ipe.getKind().equals(IProgramElement.Kind.FILE_JAVA) && ipe.getName().equals("<root>")) {
            return emptyString;
        }
        if (ipe.getHandleIdentifier(false) != null) {
            return ipe.getHandleIdentifier();
        }
        if (ipe.getKind().equals(IProgramElement.Kind.FILE_LST)) {
            String configFile = this.asm.getHierarchy().getConfigFile();
            int start = configFile.lastIndexOf(File.separator);
            int end = configFile.lastIndexOf(".lst");
            configFile = end != -1 ? configFile.substring(start + 1, end) : new StringBuffer("=").append(configFile.substring(start + 1)).toString();
            ipe.setHandleIdentifier(configFile);
            return configFile;
        }
        if (ipe.getKind() == IProgramElement.Kind.SOURCE_FOLDER) {
            StringBuffer sb = new StringBuffer();
            sb.append(this.createHandleIdentifier(ipe.getParent())).append("/");
            String folder = ipe.getName();
            if (folder.endsWith("/")) {
                folder = folder.substring(0, folder.length() - 1);
            }
            if (folder.indexOf("/") != -1) {
                folder = folder.replace("/", "\\/");
            }
            sb.append(folder);
            String handle = sb.toString();
            ipe.setHandleIdentifier(handle);
            return handle;
        }
        IProgramElement parent = ipe.getParent();
        if (parent != null && parent.getKind().equals(IProgramElement.Kind.IMPORT_REFERENCE)) {
            parent = ipe.getParent().getParent();
        }
        StringBuffer handle = new StringBuffer();
        handle.append(this.createHandleIdentifier(parent));
        handle.append(HandleProviderDelimiter.getDelimiter(ipe));
        if (!(ipe.getKind().equals(IProgramElement.Kind.INITIALIZER) || ipe.getKind() == IProgramElement.Kind.CLASS && ipe.getName().endsWith("{..}"))) {
            if (ipe.getKind() == IProgramElement.Kind.INTER_TYPE_CONSTRUCTOR) {
                handle.append(ipe.getName()).append("_new").append(this.getParameters(ipe));
            } else if (ipe.getKind().isDeclareAnnotation()) {
                handle.append("declare \\@").append(ipe.getName().substring(9)).append(this.getParameters(ipe));
            } else {
                if (ipe.getFullyQualifiedName() != null) {
                    handle.append(ipe.getFullyQualifiedName());
                } else {
                    handle.append(ipe.getName());
                }
                handle.append(this.getParameters(ipe));
            }
        }
        handle.append(this.getCount(ipe));
        ipe.setHandleIdentifier(handle.toString());
        return handle.toString();
    }

    private String getParameters(IProgramElement ipe) {
        if (ipe.getParameterSignatures() == null || ipe.getParameterSignatures().isEmpty()) {
            return emptyString;
        }
        List<String> sourceRefs = ipe.getParameterSignaturesSourceRefs();
        List<char[]> parameterTypes = ipe.getParameterSignatures();
        StringBuffer sb = new StringBuffer();
        if (sourceRefs != null) {
            for (int i = 0; i < sourceRefs.size(); ++i) {
                String sourceRef = sourceRefs.get(i);
                sb.append(HandleProviderDelimiter.getDelimiter(ipe));
                sb.append(sourceRef);
            }
        } else {
            for (char[] element : parameterTypes) {
                sb.append(HandleProviderDelimiter.getDelimiter(ipe));
                sb.append(NameConvertor.createShortName(element, false, false));
            }
        }
        return sb.toString();
    }

    private char[] getCount(IProgramElement ipe) {
        char[] byteCodeName = ipe.getBytecodeName().toCharArray();
        if (ipe.getKind().isInterTypeMember()) {
            int count = 1;
            List<IProgramElement> kids = ipe.getParent().getChildren();
            for (IProgramElement object : kids) {
                if (object.equals(ipe)) break;
                if (!object.getKind().isInterTypeMember() || !object.getName().equals(ipe.getName()) || !this.getParameters(object).equals(this.getParameters(ipe))) continue;
                String existingHandle = object.getHandleIdentifier();
                int suffixPosition = existingHandle.indexOf(33);
                if (suffixPosition != -1) {
                    count = new Integer(existingHandle.substring(suffixPosition + 1)) + 1;
                    continue;
                }
                if (count != 1) continue;
                count = 2;
            }
            if (count > 1) {
                return CharOperation.concat(countDelim, new Integer(count).toString().toCharArray());
            }
        } else if (ipe.getKind().isDeclare()) {
            int count = this.computeCountBasedOnPeers(ipe);
            if (count > 1) {
                return CharOperation.concat(countDelim, new Integer(count).toString().toCharArray());
            }
        } else if (ipe.getKind().equals(IProgramElement.Kind.ADVICE)) {
            int count = 1;
            List<IProgramElement> kids = ipe.getParent().getChildren();
            String ipeSig = ipe.getBytecodeSignature();
            int idx = 0;
            ipeSig = this.shortenIpeSig(ipeSig);
            for (IProgramElement object : kids) {
                if (object.equals(ipe)) break;
                if (object.getKind() != ipe.getKind() || !object.getName().equals(ipe.getName())) continue;
                String sig1 = object.getBytecodeSignature();
                if (sig1 != null && (idx = sig1.indexOf(")")) != -1) {
                    sig1 = sig1.substring(0, idx);
                }
                if (sig1 != null && sig1.indexOf("Lorg/aspectj/lang") != -1) {
                    if (sig1.endsWith("Lorg/aspectj/lang/JoinPoint$StaticPart;")) {
                        sig1 = sig1.substring(0, sig1.lastIndexOf("Lorg/aspectj/lang/JoinPoint$StaticPart;"));
                    }
                    if (sig1.endsWith("Lorg/aspectj/lang/JoinPoint;")) {
                        sig1 = sig1.substring(0, sig1.lastIndexOf("Lorg/aspectj/lang/JoinPoint;"));
                    }
                    if (sig1.endsWith("Lorg/aspectj/lang/JoinPoint$StaticPart;")) {
                        sig1 = sig1.substring(0, sig1.lastIndexOf("Lorg/aspectj/lang/JoinPoint$StaticPart;"));
                    }
                }
                if ((sig1 != null || ipeSig != null) && (sig1 == null || !sig1.equals(ipeSig))) continue;
                String existingHandle = object.getHandleIdentifier();
                int suffixPosition = existingHandle.indexOf(33);
                if (suffixPosition != -1) {
                    count = new Integer(existingHandle.substring(suffixPosition + 1)) + 1;
                    continue;
                }
                if (count != 1) continue;
                count = 2;
            }
            if (count > 1) {
                return CharOperation.concat(countDelim, new Integer(count).toString().toCharArray());
            }
        } else {
            if (ipe.getKind().equals(IProgramElement.Kind.INITIALIZER)) {
                int count = 1;
                List<IProgramElement> kids = ipe.getParent().getChildren();
                String ipeSig = ipe.getBytecodeSignature();
                int idx = 0;
                ipeSig = this.shortenIpeSig(ipeSig);
                for (IProgramElement object : kids) {
                    if (object.equals(ipe)) break;
                    if (object.getKind() != ipe.getKind() || !object.getName().equals(ipe.getName())) continue;
                    String sig1 = object.getBytecodeSignature();
                    if (sig1 != null && (idx = sig1.indexOf(")")) != -1) {
                        sig1 = sig1.substring(0, idx);
                    }
                    if (sig1 != null && sig1.indexOf("Lorg/aspectj/lang") != -1) {
                        if (sig1.endsWith("Lorg/aspectj/lang/JoinPoint$StaticPart;")) {
                            sig1 = sig1.substring(0, sig1.lastIndexOf("Lorg/aspectj/lang/JoinPoint$StaticPart;"));
                        }
                        if (sig1.endsWith("Lorg/aspectj/lang/JoinPoint;")) {
                            sig1 = sig1.substring(0, sig1.lastIndexOf("Lorg/aspectj/lang/JoinPoint;"));
                        }
                        if (sig1.endsWith("Lorg/aspectj/lang/JoinPoint$StaticPart;")) {
                            sig1 = sig1.substring(0, sig1.lastIndexOf("Lorg/aspectj/lang/JoinPoint$StaticPart;"));
                        }
                    }
                    if ((sig1 != null || ipeSig != null) && (sig1 == null || !sig1.equals(ipeSig))) continue;
                    String existingHandle = object.getHandleIdentifier();
                    int suffixPosition = existingHandle.indexOf(33);
                    if (suffixPosition != -1) {
                        count = new Integer(existingHandle.substring(suffixPosition + 1)) + 1;
                        continue;
                    }
                    if (count != 1) continue;
                    count = 2;
                }
                return new Integer(count).toString().toCharArray();
            }
            if (ipe.getKind().equals(IProgramElement.Kind.CODE)) {
                int index = CharOperation.lastIndexOf('!', byteCodeName);
                if (index != -1) {
                    return this.convertCount(CharOperation.subarray(byteCodeName, index + 1, byteCodeName.length));
                }
            } else if (ipe.getKind() == IProgramElement.Kind.CLASS) {
                int count = 1;
                List<IProgramElement> kids = ipe.getParent().getChildren();
                if (ipe.getName().endsWith("{..}")) {
                    for (IProgramElement object : kids) {
                        if (object.equals(ipe)) break;
                        if (object.getKind() != ipe.getKind() || !object.getName().endsWith("{..}")) continue;
                        String existingHandle = object.getHandleIdentifier();
                        int suffixPosition = existingHandle.lastIndexOf(33);
                        int lastSquareBracket = existingHandle.lastIndexOf(91);
                        if (suffixPosition != -1 && lastSquareBracket < suffixPosition) {
                            count = new Integer(existingHandle.substring(suffixPosition + 1)) + 1;
                            continue;
                        }
                        if (count != 1) continue;
                        count = 2;
                    }
                } else {
                    for (IProgramElement object : kids) {
                        if (object.equals(ipe)) break;
                        if (object.getKind() != ipe.getKind() || !object.getName().equals(ipe.getName())) continue;
                        String existingHandle = object.getHandleIdentifier();
                        int suffixPosition = existingHandle.lastIndexOf(33);
                        int lastSquareBracket = existingHandle.lastIndexOf(91);
                        if (suffixPosition != -1 && lastSquareBracket < suffixPosition) {
                            count = new Integer(existingHandle.substring(suffixPosition + 1)) + 1;
                            continue;
                        }
                        if (count != 1) continue;
                        count = 2;
                    }
                }
                if (count > 1) {
                    return CharOperation.concat(countDelim, new Integer(count).toString().toCharArray());
                }
            }
        }
        return empty;
    }

    private String shortenIpeSig(String ipeSig) {
        int idx;
        if (ipeSig != null && (idx = ipeSig.indexOf(")")) != -1) {
            ipeSig = ipeSig.substring(0, idx);
        }
        if (ipeSig != null && ipeSig.indexOf("Lorg/aspectj/lang") != -1) {
            if (ipeSig.endsWith("Lorg/aspectj/lang/JoinPoint$StaticPart;")) {
                ipeSig = ipeSig.substring(0, ipeSig.lastIndexOf("Lorg/aspectj/lang/JoinPoint$StaticPart;"));
            }
            if (ipeSig.endsWith("Lorg/aspectj/lang/JoinPoint;")) {
                ipeSig = ipeSig.substring(0, ipeSig.lastIndexOf("Lorg/aspectj/lang/JoinPoint;"));
            }
            if (ipeSig.endsWith("Lorg/aspectj/lang/JoinPoint$StaticPart;")) {
                ipeSig = ipeSig.substring(0, ipeSig.lastIndexOf("Lorg/aspectj/lang/JoinPoint$StaticPart;"));
            }
        }
        return ipeSig;
    }

    private int computeCountBasedOnPeers(IProgramElement ipe) {
        int count = 1;
        for (IProgramElement object : ipe.getParent().getChildren()) {
            if (object.equals(ipe)) break;
            if (object.getKind() != ipe.getKind() || !object.getKind().toString().equals(ipe.getKind().toString())) continue;
            String existingHandle = object.getHandleIdentifier();
            int suffixPosition = existingHandle.indexOf(33);
            if (suffixPosition != -1) {
                count = new Integer(existingHandle.substring(suffixPosition + 1)) + 1;
                continue;
            }
            if (count != 1) continue;
            count = 2;
        }
        return count;
    }

    private char[] convertCount(char[] c) {
        if (c.length == 1 && c[0] != ' ' && c[0] != '1' || c.length > 1) {
            return CharOperation.concat(countDelim, c);
        }
        return empty;
    }

    @Override
    public String getFileForHandle(String handle) {
        IProgramElement node = this.asm.getHierarchy().getElement(handle);
        if (node != null) {
            return this.asm.getCanonicalFilePath(node.getSourceLocation().getSourceFile());
        }
        if (handle.charAt(0) == HandleProviderDelimiter.ASPECT_CU.getDelimiter() || handle.charAt(0) == HandleProviderDelimiter.COMPILATIONUNIT.getDelimiter()) {
            return backslash + handle.substring(1);
        }
        return emptyString;
    }

    @Override
    public int getLineNumberForHandle(String handle) {
        IProgramElement node = this.asm.getHierarchy().getElement(handle);
        if (node != null) {
            return node.getSourceLocation().getLine();
        }
        if (handle.charAt(0) == HandleProviderDelimiter.ASPECT_CU.getDelimiter() || handle.charAt(0) == HandleProviderDelimiter.COMPILATIONUNIT.getDelimiter()) {
            return 1;
        }
        return -1;
    }

    @Override
    public int getOffSetForHandle(String handle) {
        IProgramElement node = this.asm.getHierarchy().getElement(handle);
        if (node != null) {
            return node.getSourceLocation().getOffset();
        }
        if (handle.charAt(0) == HandleProviderDelimiter.ASPECT_CU.getDelimiter() || handle.charAt(0) == HandleProviderDelimiter.COMPILATIONUNIT.getDelimiter()) {
            return 0;
        }
        return -1;
    }

    @Override
    public String createHandleIdentifier(ISourceLocation location) {
        IProgramElement node = this.asm.getHierarchy().findElementForSourceLine(location);
        if (node != null) {
            return this.createHandleIdentifier(node);
        }
        return null;
    }

    @Override
    public String createHandleIdentifier(File sourceFile, int line, int column, int offset) {
        IProgramElement node = this.asm.getHierarchy().findElementForOffSet(sourceFile.getAbsolutePath(), line, offset);
        if (node != null) {
            return this.createHandleIdentifier(node);
        }
        return null;
    }

    public boolean dependsOnLocation() {
        return false;
    }
}

