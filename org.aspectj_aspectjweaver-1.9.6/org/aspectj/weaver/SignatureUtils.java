/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import org.aspectj.weaver.BCException;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.MemberKind;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;

public class SignatureUtils {
    public static String getSignatureString(Member m, World world) {
        MemberKind kind = m.getKind();
        if (kind == Member.METHOD) {
            return SignatureUtils.getMethodSignatureString(m, world);
        }
        if (kind == Member.CONSTRUCTOR) {
            return SignatureUtils.getConstructorSignatureString(m, world);
        }
        if (kind == Member.FIELD) {
            return SignatureUtils.getFieldSignatureString(m, world);
        }
        if (kind == Member.HANDLER) {
            return SignatureUtils.getHandlerSignatureString(m, world);
        }
        if (kind == Member.STATIC_INITIALIZATION) {
            return SignatureUtils.getStaticInitializationSignatureString(m, world);
        }
        if (kind == Member.ADVICE) {
            return SignatureUtils.getAdviceSignatureString(m, world);
        }
        if (kind == Member.MONITORENTER || kind == Member.MONITOREXIT) {
            return SignatureUtils.getMonitorSignatureString(m, world);
        }
        throw new BCException("Do not know the signature string for MemberKind " + kind);
    }

    public static String getSignatureMakerName(Member m) {
        MemberKind kind = m.getKind();
        if (kind == Member.METHOD) {
            return "makeMethodSig";
        }
        if (kind == Member.CONSTRUCTOR) {
            return "makeConstructorSig";
        }
        if (kind == Member.FIELD) {
            return "makeFieldSig";
        }
        if (kind == Member.HANDLER) {
            return "makeCatchClauseSig";
        }
        if (kind == Member.STATIC_INITIALIZATION) {
            return "makeInitializerSig";
        }
        if (kind == Member.ADVICE) {
            return "makeAdviceSig";
        }
        if (kind == Member.MONITORENTER) {
            return "makeLockSig";
        }
        if (kind == Member.MONITOREXIT) {
            return "makeUnlockSig";
        }
        throw new BCException("Do not know the signature maker name for MemberKind " + kind);
    }

    public static String getSignatureType(Member m) {
        MemberKind kind = m.getKind();
        if (m.getName().equals("<clinit>") && kind != Member.STATIC_INITIALIZATION) {
            throw new BCException();
        }
        if (kind == Member.METHOD) {
            return "org.aspectj.lang.reflect.MethodSignature";
        }
        if (kind == Member.CONSTRUCTOR) {
            return "org.aspectj.lang.reflect.ConstructorSignature";
        }
        if (kind == Member.FIELD) {
            return "org.aspectj.lang.reflect.FieldSignature";
        }
        if (kind == Member.HANDLER) {
            return "org.aspectj.lang.reflect.CatchClauseSignature";
        }
        if (kind == Member.STATIC_INITIALIZATION) {
            return "org.aspectj.lang.reflect.InitializerSignature";
        }
        if (kind == Member.ADVICE) {
            return "org.aspectj.lang.reflect.AdviceSignature";
        }
        if (kind == Member.MONITORENTER) {
            return "org.aspectj.lang.reflect.LockSignature";
        }
        if (kind == Member.MONITOREXIT) {
            return "org.aspectj.lang.reflect.UnlockSignature";
        }
        throw new BCException("Do not know the signature type for MemberKind " + kind);
    }

    private static String getHandlerSignatureString(Member m, World world) {
        StringBuffer buf = new StringBuffer();
        buf.append(SignatureUtils.makeString(0));
        buf.append('-');
        buf.append('-');
        buf.append(SignatureUtils.makeString(m.getDeclaringType()));
        buf.append('-');
        buf.append(SignatureUtils.makeString(m.getParameterTypes()[0]));
        buf.append('-');
        String pName = "<missing>";
        String[] names = m.getParameterNames(world);
        if (names != null) {
            pName = names[0];
        }
        buf.append(pName);
        buf.append('-');
        return buf.toString();
    }

    private static String getStaticInitializationSignatureString(Member m, World world) {
        StringBuffer buf = new StringBuffer();
        buf.append(SignatureUtils.makeString(m.getModifiers(world)));
        buf.append('-');
        buf.append('-');
        buf.append(SignatureUtils.makeString(m.getDeclaringType()));
        buf.append('-');
        return buf.toString();
    }

    protected static String getAdviceSignatureString(Member m, World world) {
        StringBuffer buf = new StringBuffer();
        buf.append(SignatureUtils.makeString(m.getModifiers(world)));
        buf.append('-');
        buf.append(m.getName());
        buf.append('-');
        buf.append(SignatureUtils.makeString(m.getDeclaringType()));
        buf.append('-');
        buf.append(SignatureUtils.makeString(m.getParameterTypes()));
        buf.append('-');
        buf.append(SignatureUtils.makeString(m.getParameterNames(world)));
        buf.append('-');
        buf.append(SignatureUtils.makeString(m.getExceptions(world)));
        buf.append('-');
        buf.append(SignatureUtils.makeString(m.getReturnType()));
        buf.append('-');
        return buf.toString();
    }

    protected static String getMethodSignatureString(Member m, World world) {
        StringBuffer buf = new StringBuffer();
        buf.append(SignatureUtils.makeString(m.getModifiers(world)));
        buf.append('-');
        buf.append(m.getName());
        buf.append('-');
        buf.append(SignatureUtils.makeString(m.getDeclaringType()));
        buf.append('-');
        buf.append(SignatureUtils.makeString(m.getParameterTypes()));
        buf.append('-');
        buf.append(SignatureUtils.makeString(m.getParameterNames(world)));
        buf.append('-');
        buf.append(SignatureUtils.makeString(m.getExceptions(world)));
        buf.append('-');
        buf.append(SignatureUtils.makeString(m.getReturnType()));
        buf.append('-');
        return buf.toString();
    }

    protected static String getMonitorSignatureString(Member m, World world) {
        StringBuffer buf = new StringBuffer();
        buf.append(SignatureUtils.makeString(8));
        buf.append('-');
        buf.append(m.getName());
        buf.append('-');
        buf.append(SignatureUtils.makeString(m.getDeclaringType()));
        buf.append('-');
        buf.append(SignatureUtils.makeString(m.getParameterTypes()[0]));
        buf.append('-');
        buf.append("");
        buf.append('-');
        return buf.toString();
    }

    protected static String getConstructorSignatureString(Member m, World world) {
        StringBuffer buf = new StringBuffer();
        buf.append(SignatureUtils.makeString(m.getModifiers(world)));
        buf.append('-');
        buf.append('-');
        buf.append(SignatureUtils.makeString(m.getDeclaringType()));
        buf.append('-');
        buf.append(SignatureUtils.makeString(m.getParameterTypes()));
        buf.append('-');
        buf.append(SignatureUtils.makeString(m.getParameterNames(world)));
        buf.append('-');
        buf.append(SignatureUtils.makeString(m.getExceptions(world)));
        buf.append('-');
        return buf.toString();
    }

    protected static String getFieldSignatureString(Member m, World world) {
        StringBuffer buf = new StringBuffer();
        buf.append(SignatureUtils.makeString(m.getModifiers(world)));
        buf.append('-');
        buf.append(m.getName());
        buf.append('-');
        buf.append(SignatureUtils.makeString(m.getDeclaringType()));
        buf.append('-');
        buf.append(SignatureUtils.makeString(m.getReturnType()));
        buf.append('-');
        return buf.toString();
    }

    protected static String makeString(int i) {
        return Integer.toString(i, 16);
    }

    protected static String makeString(UnresolvedType t) {
        if (t.isArray()) {
            return t.getSignature().replace('/', '.');
        }
        return t.getName();
    }

    protected static String makeString(UnresolvedType[] types) {
        if (types == null) {
            return "";
        }
        StringBuffer buf = new StringBuffer();
        int len = types.length;
        for (int i = 0; i < len; ++i) {
            buf.append(SignatureUtils.makeString(types[i]));
            buf.append(':');
        }
        return buf.toString();
    }

    protected static String makeString(String[] names) {
        if (names == null) {
            return "";
        }
        StringBuffer buf = new StringBuffer();
        int len = names.length;
        for (int i = 0; i < len; ++i) {
            buf.append(names[i]);
            buf.append(':');
        }
        return buf.toString();
    }
}

