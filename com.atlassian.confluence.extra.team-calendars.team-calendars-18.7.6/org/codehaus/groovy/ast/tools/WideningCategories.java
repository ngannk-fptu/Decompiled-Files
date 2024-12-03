/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.tools.GenericsUtils;

public class WideningCategories {
    private static final List<ClassNode> EMPTY_CLASSNODE_LIST = Collections.emptyList();
    private static final Map<ClassNode, Integer> NUMBER_TYPES_PRECEDENCE = Collections.unmodifiableMap(new HashMap<ClassNode, Integer>(){
        {
            this.put(ClassHelper.double_TYPE, 0);
            this.put(ClassHelper.float_TYPE, 1);
            this.put(ClassHelper.long_TYPE, 2);
            this.put(ClassHelper.int_TYPE, 3);
            this.put(ClassHelper.short_TYPE, 4);
            this.put(ClassHelper.byte_TYPE, 5);
        }
    });
    private static final Comparator<ClassNode> INTERFACE_CLASSNODE_COMPARATOR = new Comparator<ClassNode>(){

        @Override
        public int compare(ClassNode o1, ClassNode o2) {
            int methodCountForO2;
            int interfaceCountForO2;
            int interfaceCountForO1 = o1.getInterfaces().length;
            if (interfaceCountForO1 > (interfaceCountForO2 = o2.getInterfaces().length)) {
                return -1;
            }
            if (interfaceCountForO1 < interfaceCountForO2) {
                return 1;
            }
            int methodCountForO1 = o1.getMethods().size();
            if (methodCountForO1 > (methodCountForO2 = o2.getMethods().size())) {
                return -1;
            }
            if (methodCountForO1 < methodCountForO2) {
                return 1;
            }
            return o1.getName().compareTo(o2.getName());
        }
    };

    public static boolean isInt(ClassNode type) {
        return ClassHelper.int_TYPE == type;
    }

    public static boolean isDouble(ClassNode type) {
        return ClassHelper.double_TYPE == type;
    }

    public static boolean isFloat(ClassNode type) {
        return ClassHelper.float_TYPE == type;
    }

    public static boolean isIntCategory(ClassNode type) {
        return type == ClassHelper.byte_TYPE || type == ClassHelper.char_TYPE || type == ClassHelper.int_TYPE || type == ClassHelper.short_TYPE;
    }

    public static boolean isLongCategory(ClassNode type) {
        return type == ClassHelper.long_TYPE || WideningCategories.isIntCategory(type);
    }

    public static boolean isBigIntCategory(ClassNode type) {
        return type == ClassHelper.BigInteger_TYPE || WideningCategories.isLongCategory(type);
    }

    public static boolean isBigDecCategory(ClassNode type) {
        return type == ClassHelper.BigDecimal_TYPE || WideningCategories.isBigIntCategory(type);
    }

    public static boolean isDoubleCategory(ClassNode type) {
        return type == ClassHelper.float_TYPE || type == ClassHelper.double_TYPE || WideningCategories.isBigDecCategory(type);
    }

    public static boolean isFloatingCategory(ClassNode type) {
        return type == ClassHelper.float_TYPE || type == ClassHelper.double_TYPE;
    }

    public static boolean isNumberCategory(ClassNode type) {
        return WideningCategories.isBigDecCategory(type) || type.isDerivedFrom(ClassHelper.Number_TYPE);
    }

    public static ClassNode lowestUpperBound(List<ClassNode> nodes) {
        if (nodes.size() == 1) {
            return nodes.get(0);
        }
        return WideningCategories.lowestUpperBound(nodes.get(0), WideningCategories.lowestUpperBound(nodes.subList(1, nodes.size())));
    }

    public static ClassNode lowestUpperBound(ClassNode a, ClassNode b) {
        ClassNode lub = WideningCategories.lowestUpperBound(a, b, null, null);
        if (lub == null || !lub.isUsingGenerics()) {
            return lub;
        }
        if (lub instanceof LowestUpperBoundClassNode) {
            ClassNode superClass = lub.getSuperClass();
            ClassNode psc = superClass.isUsingGenerics() ? WideningCategories.parameterizeLowestUpperBound(superClass, a, b, lub) : superClass;
            ClassNode[] interfaces = lub.getInterfaces();
            ClassNode[] pinterfaces = new ClassNode[interfaces.length];
            for (ClassNode icn : interfaces) {
                pinterfaces[i] = icn.isUsingGenerics() ? WideningCategories.parameterizeLowestUpperBound(icn, a, b, lub) : icn;
            }
            return new LowestUpperBoundClassNode(((LowestUpperBoundClassNode)lub).name, psc, pinterfaces);
        }
        return WideningCategories.parameterizeLowestUpperBound(lub, a, b, lub);
    }

    private static ClassNode parameterizeLowestUpperBound(ClassNode lub, ClassNode a, ClassNode b, ClassNode fallback) {
        if (!lub.isUsingGenerics()) {
            return lub;
        }
        ClassNode holderForA = WideningCategories.findGenericsTypeHolderForClass(a, lub);
        ClassNode holderForB = WideningCategories.findGenericsTypeHolderForClass(b, lub);
        GenericsType[] agt = holderForA.getGenericsTypes();
        GenericsType[] bgt = holderForB.getGenericsTypes();
        if (agt == null || bgt == null || agt.length != bgt.length) {
            return lub;
        }
        GenericsType[] lubgt = new GenericsType[agt.length];
        for (int i = 0; i < agt.length; ++i) {
            ClassNode t1 = agt[i].getType();
            ClassNode t2 = bgt[i].getType();
            ClassNode basicType = WideningCategories.areEqualWithGenerics(t1, a) && WideningCategories.areEqualWithGenerics(t2, b) ? fallback : WideningCategories.lowestUpperBound(t1, t2);
            lubgt[i] = t1.equals(t2) ? new GenericsType(basicType) : GenericsUtils.buildWildcardType(basicType);
        }
        ClassNode plain = lub.getPlainNodeReference();
        plain.setGenericsTypes(lubgt);
        return plain;
    }

    private static ClassNode findGenericsTypeHolderForClass(ClassNode source, ClassNode type) {
        ClassNode superClass;
        if (ClassHelper.isPrimitiveType(source)) {
            source = ClassHelper.getWrapper(source);
        }
        if (source.equals(type)) {
            return source;
        }
        if (type.isInterface()) {
            for (ClassNode interfaceNode : source.getAllInterfaces()) {
                if (!interfaceNode.equals(type)) continue;
                ClassNode parameterizedInterface = GenericsUtils.parameterizeType(source, interfaceNode);
                return parameterizedInterface;
            }
        }
        if ((superClass = source.getUnresolvedSuperClass()) != null && superClass.isUsingGenerics()) {
            Map<String, GenericsType> genericsTypeMap = GenericsUtils.extractPlaceholders(source);
            GenericsType[] genericsTypes = superClass.getGenericsTypes();
            if (genericsTypes != null) {
                GenericsType[] copyTypes = new GenericsType[genericsTypes.length];
                for (int i = 0; i < genericsTypes.length; ++i) {
                    GenericsType genericsType = genericsTypes[i];
                    copyTypes[i] = genericsType.isPlaceholder() && genericsTypeMap.containsKey(genericsType.getName()) ? genericsTypeMap.get(genericsType.getName()) : genericsType;
                }
                superClass = superClass.getPlainNodeReference();
                superClass.setGenericsTypes(copyTypes);
            }
        }
        if (superClass != null) {
            return WideningCategories.findGenericsTypeHolderForClass(superClass, type);
        }
        return null;
    }

    private static ClassNode lowestUpperBound(ClassNode a, ClassNode b, List<ClassNode> interfacesImplementedByA, List<ClassNode> interfacesImplementedByB) {
        if (a == null || b == null) {
            return null;
        }
        if (a.isArray() && b.isArray()) {
            return WideningCategories.lowestUpperBound(a.getComponentType(), b.getComponentType(), interfacesImplementedByA, interfacesImplementedByB).makeArray();
        }
        if (a.equals(ClassHelper.OBJECT_TYPE) || b.equals(ClassHelper.OBJECT_TYPE)) {
            GenericsType[] gta = a.getGenericsTypes();
            GenericsType[] gtb = b.getGenericsTypes();
            if (gta != null && gtb != null && gta.length == 1 && gtb.length == 1 && gta[0].getName().equals(gtb[0].getName())) {
                return a;
            }
            return ClassHelper.OBJECT_TYPE;
        }
        if (a.equals(ClassHelper.VOID_TYPE) || b.equals(ClassHelper.VOID_TYPE)) {
            if (!b.equals(a)) {
                return ClassHelper.OBJECT_TYPE;
            }
            return ClassHelper.VOID_TYPE;
        }
        boolean isPrimitiveA = ClassHelper.isPrimitiveType(a);
        boolean isPrimitiveB = ClassHelper.isPrimitiveType(b);
        if (isPrimitiveA && !isPrimitiveB) {
            return WideningCategories.lowestUpperBound(ClassHelper.getWrapper(a), b, null, null);
        }
        if (isPrimitiveB && !isPrimitiveA) {
            return WideningCategories.lowestUpperBound(a, ClassHelper.getWrapper(b), null, null);
        }
        if (isPrimitiveA && isPrimitiveB) {
            Integer pa = NUMBER_TYPES_PRECEDENCE.get(a);
            Integer pb = NUMBER_TYPES_PRECEDENCE.get(b);
            if (pa != null && pb != null) {
                if (pa <= pb) {
                    return a;
                }
                return b;
            }
            return a.equals(b) ? a : WideningCategories.lowestUpperBound(ClassHelper.getWrapper(a), ClassHelper.getWrapper(b), null, null);
        }
        if (ClassHelper.isNumberType(a.redirect()) && ClassHelper.isNumberType(b.redirect())) {
            ClassNode ua = ClassHelper.getUnwrapper(a);
            ClassNode ub = ClassHelper.getUnwrapper(b);
            Integer pa = NUMBER_TYPES_PRECEDENCE.get(ua);
            Integer pb = NUMBER_TYPES_PRECEDENCE.get(ub);
            if (pa != null && pb != null) {
                if (pa <= pb) {
                    return a;
                }
                return b;
            }
        }
        boolean isInterfaceA = a.isInterface();
        boolean isInterfaceB = b.isInterface();
        if (isInterfaceA && isInterfaceB) {
            if (a.equals(b)) {
                return a;
            }
            if (b.implementsInterface(a)) {
                return a;
            }
            if (a.implementsInterface(b)) {
                return b;
            }
            ClassNode[] interfacesFromA = a.getInterfaces();
            ClassNode[] interfacesFromB = b.getInterfaces();
            HashSet<ClassNode> common = new HashSet<ClassNode>();
            Collections.addAll(common, interfacesFromA);
            HashSet fromB = new HashSet();
            Collections.addAll(fromB, interfacesFromB);
            common.retainAll(fromB);
            if (common.size() == 1) {
                return (ClassNode)common.iterator().next();
            }
            if (common.size() > 1) {
                return WideningCategories.buildTypeWithInterfaces(a, b, common);
            }
            return ClassHelper.OBJECT_TYPE;
        }
        if (isInterfaceB) {
            return WideningCategories.lowestUpperBound(b, a, null, null);
        }
        if (isInterfaceA) {
            LinkedList<ClassNode> matchingInterfaces = new LinkedList<ClassNode>();
            WideningCategories.extractMostSpecificImplementedInterfaces(b, a, matchingInterfaces);
            if (matchingInterfaces.isEmpty()) {
                return ClassHelper.OBJECT_TYPE;
            }
            if (matchingInterfaces.size() == 1) {
                return (ClassNode)matchingInterfaces.get(0);
            }
            return WideningCategories.buildTypeWithInterfaces(a, b, matchingInterfaces);
        }
        if (a.equals(b)) {
            return WideningCategories.buildTypeWithInterfaces(a, b, WideningCategories.keepLowestCommonInterfaces(interfacesImplementedByA, interfacesImplementedByB));
        }
        if (a.isDerivedFrom(b) || b.isDerivedFrom(a)) {
            return WideningCategories.buildTypeWithInterfaces(a, b, WideningCategories.keepLowestCommonInterfaces(interfacesImplementedByA, interfacesImplementedByB));
        }
        ClassNode sa = a.getUnresolvedSuperClass();
        ClassNode sb = b.getUnresolvedSuperClass();
        HashSet<ClassNode> ifa = new HashSet<ClassNode>();
        WideningCategories.extractInterfaces(a, ifa);
        HashSet<ClassNode> ifb = new HashSet<ClassNode>();
        WideningCategories.extractInterfaces(b, ifb);
        interfacesImplementedByA = interfacesImplementedByA == null ? new LinkedList<ClassNode>(ifa) : interfacesImplementedByA;
        LinkedList<ClassNode> linkedList = interfacesImplementedByB = interfacesImplementedByB == null ? new LinkedList<ClassNode>(ifb) : interfacesImplementedByB;
        if (sa == null || sb == null) {
            return WideningCategories.buildTypeWithInterfaces(ClassHelper.OBJECT_TYPE, ClassHelper.OBJECT_TYPE, WideningCategories.keepLowestCommonInterfaces(interfacesImplementedByA, interfacesImplementedByB));
        }
        if (sa.isDerivedFrom(sb) || sb.isDerivedFrom(sa)) {
            return WideningCategories.buildTypeWithInterfaces(sa, sb, WideningCategories.keepLowestCommonInterfaces(interfacesImplementedByA, interfacesImplementedByB));
        }
        return WideningCategories.lowestUpperBound(sa, sb, interfacesImplementedByA, interfacesImplementedByB);
    }

    private static void extractInterfaces(ClassNode node, Set<ClassNode> interfaces) {
        if (node == null) {
            return;
        }
        Collections.addAll(interfaces, node.getInterfaces());
        WideningCategories.extractInterfaces(node.getSuperClass(), interfaces);
    }

    private static List<ClassNode> keepLowestCommonInterfaces(List<ClassNode> fromA, List<ClassNode> fromB) {
        if (fromA == null || fromB == null) {
            return EMPTY_CLASSNODE_LIST;
        }
        HashSet<ClassNode> common = new HashSet<ClassNode>(fromA);
        common.retainAll(fromB);
        ArrayList<ClassNode> result = new ArrayList<ClassNode>(common.size());
        for (ClassNode classNode : common) {
            WideningCategories.addMostSpecificInterface(classNode, result);
        }
        return result;
    }

    private static void addMostSpecificInterface(ClassNode interfaceNode, List<ClassNode> nodes) {
        if (nodes.isEmpty()) {
            nodes.add(interfaceNode);
        }
        int nodesSize = nodes.size();
        for (int i = 0; i < nodesSize; ++i) {
            ClassNode node = nodes.get(i);
            if (node.equals(interfaceNode) || node.implementsInterface(interfaceNode)) {
                return;
            }
            if (!interfaceNode.implementsInterface(node)) continue;
            nodes.set(i, interfaceNode);
            return;
        }
        nodes.add(interfaceNode);
    }

    private static void extractMostSpecificImplementedInterfaces(ClassNode type, ClassNode inode, List<ClassNode> result) {
        if (type.implementsInterface(inode)) {
            result.add(inode);
        } else {
            ClassNode[] interfaces;
            for (ClassNode interfaceNode : interfaces = inode.getInterfaces()) {
                if (!type.implementsInterface(interfaceNode)) continue;
                result.add(interfaceNode);
            }
            if (result.isEmpty() && interfaces.length > 0) {
                for (ClassNode interfaceNode : interfaces) {
                    WideningCategories.extractMostSpecificImplementedInterfaces(type, interfaceNode, result);
                }
            }
        }
    }

    private static ClassNode buildTypeWithInterfaces(ClassNode baseType1, ClassNode baseType2, Collection<ClassNode> interfaces) {
        String name;
        ClassNode superClass;
        boolean noInterface = interfaces.isEmpty();
        if (noInterface) {
            if (baseType1.equals(baseType2)) {
                return baseType1;
            }
            if (baseType1.isDerivedFrom(baseType2)) {
                return baseType2;
            }
            if (baseType2.isDerivedFrom(baseType1)) {
                return baseType1;
            }
        }
        if (ClassHelper.OBJECT_TYPE.equals(baseType1) && ClassHelper.OBJECT_TYPE.equals(baseType2) && interfaces.size() == 1) {
            if (interfaces instanceof List) {
                return (ClassNode)((List)interfaces).get(0);
            }
            return interfaces.iterator().next();
        }
        if (baseType1.equals(baseType2)) {
            if (ClassHelper.OBJECT_TYPE.equals(baseType1)) {
                superClass = baseType1;
                name = "Virtual$Object";
            } else {
                superClass = baseType1;
                name = "Virtual$" + baseType1.getName();
            }
        } else {
            superClass = ClassHelper.OBJECT_TYPE;
            if (baseType1.isDerivedFrom(baseType2)) {
                superClass = baseType2;
            } else if (baseType2.isDerivedFrom(baseType1)) {
                superClass = baseType1;
            }
            name = "CommonAssignOf$" + baseType1.getName() + "$" + baseType2.getName();
        }
        Iterator<ClassNode> itcn = interfaces.iterator();
        while (itcn.hasNext()) {
            ClassNode next = itcn.next();
            if (!superClass.isDerivedFrom(next) && !superClass.implementsInterface(next)) continue;
            itcn.remove();
        }
        ClassNode[] interfaceArray = interfaces.toArray(new ClassNode[interfaces.size()]);
        Arrays.sort(interfaceArray, INTERFACE_CLASSNODE_COMPARATOR);
        LowestUpperBoundClassNode type = new LowestUpperBoundClassNode(name, superClass, interfaceArray);
        return type;
    }

    private static boolean areEqualWithGenerics(ClassNode a, ClassNode b) {
        if (a == null) {
            return b == null;
        }
        if (!a.equals(b)) {
            return false;
        }
        if (a.isUsingGenerics() && !b.isUsingGenerics()) {
            return false;
        }
        GenericsType[] gta = a.getGenericsTypes();
        GenericsType[] gtb = b.getGenericsTypes();
        if (gta == null && gtb != null) {
            return false;
        }
        if (gtb == null && gta != null) {
            return false;
        }
        if (gta != null && gtb != null) {
            if (gta.length != gtb.length) {
                return false;
            }
            for (int i = 0; i < gta.length; ++i) {
                ClassNode[] upA;
                GenericsType ga = gta[i];
                GenericsType gb = gtb[i];
                boolean result = ga.isPlaceholder() == gb.isPlaceholder() && ga.isWildcard() == gb.isWildcard();
                result = result && ga.isResolved() && gb.isResolved();
                result = result && ga.getName().equals(gb.getName());
                result = result && WideningCategories.areEqualWithGenerics(ga.getType(), gb.getType());
                boolean bl = result = result && WideningCategories.areEqualWithGenerics(ga.getLowerBound(), gb.getLowerBound());
                if (result && (upA = ga.getUpperBounds()) != null) {
                    ClassNode[] upB = gb.getUpperBounds();
                    if (upB == null || upB.length != upA.length) {
                        return false;
                    }
                    for (int j = 0; j < upA.length; ++j) {
                        if (WideningCategories.areEqualWithGenerics(upA[j], upB[j])) continue;
                        return false;
                    }
                }
                if (result) continue;
                return false;
            }
        }
        return true;
    }

    public static boolean implementsInterfaceOrSubclassOf(ClassNode source, ClassNode targetType) {
        if (source.isDerivedFrom(targetType) || source.implementsInterface(targetType)) {
            return true;
        }
        if (targetType instanceof LowestUpperBoundClassNode) {
            LowestUpperBoundClassNode lub = (LowestUpperBoundClassNode)targetType;
            if (WideningCategories.implementsInterfaceOrSubclassOf(source, lub.getSuperClass())) {
                return true;
            }
            for (ClassNode classNode : lub.getInterfaces()) {
                if (!source.implementsInterface(classNode)) continue;
                return true;
            }
        }
        return false;
    }

    public static class LowestUpperBoundClassNode
    extends ClassNode {
        private static final Comparator<ClassNode> CLASS_NODE_COMPARATOR = new Comparator<ClassNode>(){

            @Override
            public int compare(ClassNode o1, ClassNode o2) {
                String n1 = o1 instanceof LowestUpperBoundClassNode ? ((LowestUpperBoundClassNode)o1).name : o1.getName();
                String n2 = o2 instanceof LowestUpperBoundClassNode ? ((LowestUpperBoundClassNode)o2).name : o2.getName();
                return n1.compareTo(n2);
            }
        };
        private final ClassNode compileTimeClassNode;
        private final String name;
        private final String text;
        private final ClassNode upper;
        private final ClassNode[] interfaces;

        public LowestUpperBoundClassNode(String name, ClassNode upper, ClassNode ... interfaces) {
            super(name, 17, upper, interfaces, null);
            this.upper = upper;
            this.interfaces = interfaces;
            Arrays.sort(interfaces, CLASS_NODE_COMPARATOR);
            this.compileTimeClassNode = upper.equals(ClassHelper.OBJECT_TYPE) && interfaces.length > 0 ? interfaces[0] : upper;
            this.name = name;
            boolean usesGenerics = upper.isUsingGenerics();
            LinkedList<GenericsType[]> genericsTypesList = new LinkedList<GenericsType[]>();
            genericsTypesList.add(upper.getGenericsTypes());
            for (ClassNode anInterface : interfaces) {
                usesGenerics |= anInterface.isUsingGenerics();
                genericsTypesList.add(anInterface.getGenericsTypes());
                for (MethodNode methodNode : anInterface.getMethods()) {
                    MethodNode method = this.addMethod(methodNode.getName(), methodNode.getModifiers(), methodNode.getReturnType(), methodNode.getParameters(), methodNode.getExceptions(), methodNode.getCode());
                    method.setDeclaringClass(anInterface);
                }
            }
            this.setUsingGenerics(usesGenerics);
            if (usesGenerics) {
                ArrayList asArrayList = new ArrayList();
                for (GenericsType[] genericsTypes : genericsTypesList) {
                    if (genericsTypes == null) continue;
                    Collections.addAll(asArrayList, genericsTypes);
                }
                this.setGenericsTypes(asArrayList.toArray(new GenericsType[asArrayList.size()]));
            }
            StringBuilder sb = new StringBuilder();
            if (!upper.equals(ClassHelper.OBJECT_TYPE)) {
                sb.append(upper.getName());
            }
            for (ClassNode anInterface : interfaces) {
                if (sb.length() > 0) {
                    sb.append(" or ");
                }
                sb.append(anInterface.getName());
            }
            this.text = sb.toString();
        }

        public String getLubName() {
            return this.name;
        }

        @Override
        public String getName() {
            return this.compileTimeClassNode.getName();
        }

        @Override
        public Class getTypeClass() {
            return this.compileTimeClassNode.getTypeClass();
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (this.name != null ? this.name.hashCode() : 0);
            return result;
        }

        @Override
        public String getText() {
            return this.text;
        }

        @Override
        public ClassNode getPlainNodeReference() {
            ClassNode[] intf;
            ClassNode[] classNodeArray = intf = this.interfaces == null ? null : new ClassNode[this.interfaces.length];
            if (intf != null) {
                for (int i = 0; i < this.interfaces.length; ++i) {
                    intf[i] = this.interfaces[i].getPlainNodeReference();
                }
            }
            LowestUpperBoundClassNode plain = new LowestUpperBoundClassNode(this.name, this.upper.getPlainNodeReference(), intf);
            return plain;
        }
    }
}

