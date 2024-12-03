/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.tools;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;

public class ClassNodeUtils {
    public static void addInterfaceMethods(ClassNode cnode, Map<String, MethodNode> methodsMap) {
        for (ClassNode iface : cnode.getInterfaces()) {
            Map<String, MethodNode> ifaceMethodsMap = iface.getDeclaredMethodsMap();
            for (String methSig : ifaceMethodsMap.keySet()) {
                if (methodsMap.containsKey(methSig)) continue;
                MethodNode methNode = ifaceMethodsMap.get(methSig);
                methodsMap.put(methSig, methNode);
            }
        }
    }

    public static Map<String, MethodNode> getDeclaredMethodMapsFromInterfaces(ClassNode classNode) {
        ClassNode[] interfaces;
        HashMap<String, MethodNode> result = new HashMap<String, MethodNode>();
        for (ClassNode iface : interfaces = classNode.getInterfaces()) {
            result.putAll(iface.getDeclaredMethodsMap());
        }
        return result;
    }

    public static void addDeclaredMethodMapsFromSuperInterfaces(ClassNode cn, Map<String, MethodNode> allInterfaceMethods) {
        List<ClassNode> cnInterfaces = Arrays.asList(cn.getInterfaces());
        for (ClassNode sn = cn.getSuperClass(); sn != null && !sn.equals(ClassHelper.OBJECT_TYPE); sn = sn.getSuperClass()) {
            ClassNode[] interfaces;
            for (ClassNode iface : interfaces = sn.getInterfaces()) {
                if (cnInterfaces.contains(iface)) continue;
                allInterfaceMethods.putAll(iface.getDeclaredMethodsMap());
            }
        }
    }
}

