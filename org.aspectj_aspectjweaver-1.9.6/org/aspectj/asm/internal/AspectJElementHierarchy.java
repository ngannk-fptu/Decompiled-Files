/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.asm.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.aspectj.asm.AsmManager;
import org.aspectj.asm.IHierarchy;
import org.aspectj.asm.IProgramElement;
import org.aspectj.asm.internal.ProgramElement;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.SourceLocation;

public class AspectJElementHierarchy
implements IHierarchy {
    private static final long serialVersionUID = 6462734311117048620L;
    private transient AsmManager asm;
    protected IProgramElement root = null;
    protected String configFile = null;
    private Map<String, IProgramElement> fileMap = null;
    private Map<String, IProgramElement> handleMap = new HashMap<String, IProgramElement>();
    private Map<String, IProgramElement> typeMap = null;

    public AspectJElementHierarchy(AsmManager asm) {
        this.asm = asm;
    }

    @Override
    public IProgramElement getElement(String handle) {
        return this.findElementForHandleOrCreate(handle, false);
    }

    public void setAsmManager(AsmManager asm) {
        this.asm = asm;
    }

    @Override
    public IProgramElement getRoot() {
        return this.root;
    }

    public String toSummaryString() {
        StringBuilder s = new StringBuilder();
        s.append("FileMap has " + this.fileMap.size() + " entries\n");
        s.append("HandleMap has " + this.handleMap.size() + " entries\n");
        s.append("TypeMap has " + this.handleMap.size() + " entries\n");
        s.append("FileMap:\n");
        for (Map.Entry<String, IProgramElement> fileMapEntry : this.fileMap.entrySet()) {
            s.append(fileMapEntry).append("\n");
        }
        s.append("TypeMap:\n");
        for (Map.Entry<String, IProgramElement> typeMapEntry : this.typeMap.entrySet()) {
            s.append(typeMapEntry).append("\n");
        }
        s.append("HandleMap:\n");
        for (Map.Entry<String, IProgramElement> handleMapEntry : this.handleMap.entrySet()) {
            s.append(handleMapEntry).append("\n");
        }
        return s.toString();
    }

    @Override
    public void setRoot(IProgramElement root) {
        this.root = root;
        this.handleMap = new HashMap<String, IProgramElement>();
        this.typeMap = new HashMap<String, IProgramElement>();
    }

    @Override
    public void addToFileMap(String key, IProgramElement value) {
        this.fileMap.put(key, value);
    }

    @Override
    public boolean removeFromFileMap(String canonicalFilePath) {
        return this.fileMap.remove(canonicalFilePath) != null;
    }

    @Override
    public void setFileMap(HashMap<String, IProgramElement> fileMap) {
        this.fileMap = fileMap;
    }

    @Override
    public Object findInFileMap(Object key) {
        return this.fileMap.get(key);
    }

    @Override
    public Set<Map.Entry<String, IProgramElement>> getFileMapEntrySet() {
        return this.fileMap.entrySet();
    }

    @Override
    public boolean isValid() {
        return this.root != null && this.fileMap != null;
    }

    @Override
    public IProgramElement findElementForSignature(IProgramElement parent, IProgramElement.Kind kind, String signature) {
        for (IProgramElement node : parent.getChildren()) {
            if (node.getKind() == kind && signature.equals(node.toSignatureString())) {
                return node;
            }
            IProgramElement childSearch = this.findElementForSignature(node, kind, signature);
            if (childSearch == null) continue;
            return childSearch;
        }
        return null;
    }

    @Override
    public IProgramElement findElementForLabel(IProgramElement parent, IProgramElement.Kind kind, String label) {
        for (IProgramElement node : parent.getChildren()) {
            if (node.getKind() == kind && label.equals(node.toLabelString())) {
                return node;
            }
            IProgramElement childSearch = this.findElementForLabel(node, kind, label);
            if (childSearch == null) continue;
            return childSearch;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public IProgramElement findElementForType(String packageName, String typeName) {
        AspectJElementHierarchy aspectJElementHierarchy = this;
        synchronized (aspectJElementHierarchy) {
            StringBuilder keyb = packageName == null ? new StringBuilder() : new StringBuilder(packageName);
            keyb.append(".").append(typeName);
            String key = keyb.toString();
            IProgramElement cachedValue = this.typeMap.get(key);
            if (cachedValue != null) {
                return cachedValue;
            }
            List<IProgramElement> packageNodes = this.findMatchingPackages(packageName);
            for (IProgramElement pkg : packageNodes) {
                for (IProgramElement fileNode : pkg.getChildren()) {
                    IProgramElement cNode = this.findClassInNodes(fileNode.getChildren(), typeName, typeName);
                    if (cNode == null) continue;
                    this.typeMap.put(key, cNode);
                    return cNode;
                }
            }
        }
        return null;
    }

    public List<IProgramElement> findMatchingPackages(String packagename) {
        List<IProgramElement> children = this.root.getChildren();
        if (children.size() == 0) {
            return Collections.emptyList();
        }
        if (children.get(0).getKind() == IProgramElement.Kind.SOURCE_FOLDER) {
            String searchPackageName = packagename == null ? "" : packagename;
            ArrayList<IProgramElement> matchingPackageNodes = new ArrayList<IProgramElement>();
            for (IProgramElement sourceFolder : children) {
                List<IProgramElement> possiblePackageNodes = sourceFolder.getChildren();
                for (IProgramElement possiblePackageNode : possiblePackageNodes) {
                    if (possiblePackageNode.getKind() != IProgramElement.Kind.PACKAGE || !possiblePackageNode.getName().equals(searchPackageName)) continue;
                    matchingPackageNodes.add(possiblePackageNode);
                }
            }
            return matchingPackageNodes;
        }
        if (packagename == null) {
            ArrayList<IProgramElement> result = new ArrayList<IProgramElement>();
            result.add(this.root);
            return result;
        }
        ArrayList<IProgramElement> result = new ArrayList<IProgramElement>();
        block2: for (IProgramElement possiblePackage : children) {
            if (possiblePackage.getKind() == IProgramElement.Kind.PACKAGE && possiblePackage.getName().equals(packagename)) {
                result.add(possiblePackage);
            }
            if (possiblePackage.getKind() != IProgramElement.Kind.SOURCE_FOLDER || !possiblePackage.getName().equals("binaries")) continue;
            for (IProgramElement possiblePackage2 : possiblePackage.getChildren()) {
                if (possiblePackage2.getKind() != IProgramElement.Kind.PACKAGE || !possiblePackage2.getName().equals(packagename)) continue;
                result.add(possiblePackage2);
                continue block2;
            }
        }
        if (result.isEmpty()) {
            return Collections.emptyList();
        }
        return result;
    }

    private IProgramElement findClassInNodes(Collection<IProgramElement> nodes, String name, String typeName) {
        String innerName;
        String baseName;
        int dollar = name.indexOf(36);
        if (dollar == -1) {
            baseName = name;
            innerName = null;
        } else {
            baseName = name.substring(0, dollar);
            innerName = name.substring(dollar + 1);
        }
        for (IProgramElement classNode : nodes) {
            IProgramElement node;
            if (!classNode.getKind().isType()) {
                IProgramElement node2;
                List<IProgramElement> kids = classNode.getChildren();
                if (kids == null || kids.isEmpty() || (node2 = this.findClassInNodes(kids, name, typeName)) == null) continue;
                return node2;
            }
            if (baseName.equals(classNode.getName())) {
                if (innerName == null) {
                    return classNode;
                }
                return this.findClassInNodes(classNode.getChildren(), innerName, typeName);
            }
            if (name.equals(classNode.getName())) {
                return classNode;
            }
            if (typeName.equals(classNode.getBytecodeSignature())) {
                return classNode;
            }
            if (classNode.getChildren() == null || classNode.getChildren().isEmpty() || (node = this.findClassInNodes(classNode.getChildren(), name, typeName)) == null) continue;
            return node;
        }
        return null;
    }

    @Override
    public IProgramElement findElementForSourceFile(String sourceFile) {
        try {
            if (!this.isValid() || sourceFile == null) {
                return IHierarchy.NO_STRUCTURE;
            }
            String correctedPath = this.asm.getCanonicalFilePath(new File(sourceFile));
            IProgramElement node = (IProgramElement)this.findInFileMap(correctedPath);
            if (node != null) {
                return node;
            }
            return this.createFileStructureNode(correctedPath);
        }
        catch (Exception e) {
            return IHierarchy.NO_STRUCTURE;
        }
    }

    @Override
    public IProgramElement findElementForSourceLine(ISourceLocation location) {
        try {
            return this.findElementForSourceLine(this.asm.getCanonicalFilePath(location.getSourceFile()), location.getLine());
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public IProgramElement findElementForSourceLine(String sourceFilePath, int lineNumber) {
        String canonicalSFP = this.asm.getCanonicalFilePath(new File(sourceFilePath));
        IProgramElement node = this.findNodeForSourceFile(this.root, canonicalSFP);
        if (node == null) {
            return this.createFileStructureNode(sourceFilePath);
        }
        IProgramElement closernode = this.findCloserMatchForLineNumber(node, lineNumber);
        if (closernode == null) {
            return node;
        }
        return closernode;
    }

    @Override
    public IProgramElement findNodeForSourceFile(IProgramElement node, String sourcefilePath) {
        if (node.getKind().isSourceFile() && !node.getName().equals("<root>") || node.getKind().isFile()) {
            ISourceLocation nodeLoc = node.getSourceLocation();
            if (nodeLoc != null && this.asm.getCanonicalFilePath(nodeLoc.getSourceFile()).equals(sourcefilePath)) {
                return node;
            }
            return null;
        }
        for (IProgramElement child : node.getChildren()) {
            IProgramElement foundit = this.findNodeForSourceFile(child, sourcefilePath);
            if (foundit == null) continue;
            return foundit;
        }
        return null;
    }

    @Override
    public IProgramElement findElementForOffSet(String sourceFilePath, int lineNumber, int offSet) {
        String canonicalSFP = this.asm.getCanonicalFilePath(new File(sourceFilePath));
        IProgramElement node = this.findNodeForSourceLineHelper(this.root, canonicalSFP, lineNumber, offSet);
        if (node != null) {
            return node;
        }
        return this.createFileStructureNode(sourceFilePath);
    }

    private IProgramElement createFileStructureNode(String sourceFilePath) {
        int lastSlash = sourceFilePath.lastIndexOf(92);
        if (lastSlash == -1) {
            lastSlash = sourceFilePath.lastIndexOf(47);
        }
        int i = sourceFilePath.lastIndexOf(33);
        int j = sourceFilePath.indexOf(".class");
        if (i > lastSlash && i != -1 && j != -1) {
            lastSlash = i;
        }
        String fileName = sourceFilePath.substring(lastSlash + 1);
        ProgramElement fileNode = new ProgramElement(this.asm, fileName, IProgramElement.Kind.FILE_JAVA, new SourceLocation(new File(sourceFilePath), 1, 1), 0, null, null);
        fileNode.addChild(NO_STRUCTURE);
        return fileNode;
    }

    @Override
    public IProgramElement findCloserMatchForLineNumber(IProgramElement node, int lineno) {
        if (node == null || node.getChildren() == null) {
            return null;
        }
        for (IProgramElement child : node.getChildren()) {
            IProgramElement evenCloserMatch;
            ISourceLocation childLoc = child.getSourceLocation();
            if (childLoc == null) continue;
            if (childLoc.getLine() <= lineno && childLoc.getEndLine() >= lineno) {
                evenCloserMatch = this.findCloserMatchForLineNumber(child, lineno);
                if (evenCloserMatch == null) {
                    return child;
                }
                return evenCloserMatch;
            }
            if (!child.getKind().isType() || (evenCloserMatch = this.findCloserMatchForLineNumber(child, lineno)) == null) continue;
            return evenCloserMatch;
        }
        return null;
    }

    private IProgramElement findNodeForSourceLineHelper(IProgramElement node, String sourceFilePath, int lineno, int offset) {
        if (this.matches(node, sourceFilePath, lineno, offset) && !this.hasMoreSpecificChild(node, sourceFilePath, lineno, offset)) {
            return node;
        }
        if (node != null) {
            for (IProgramElement child : node.getChildren()) {
                IProgramElement foundNode = this.findNodeForSourceLineHelper(child, sourceFilePath, lineno, offset);
                if (foundNode == null) continue;
                return foundNode;
            }
        }
        return null;
    }

    private boolean matches(IProgramElement node, String sourceFilePath, int lineNumber, int offSet) {
        ISourceLocation nodeSourceLocation = node != null ? node.getSourceLocation() : null;
        return node != null && nodeSourceLocation != null && nodeSourceLocation.getSourceFile().getAbsolutePath().equals(sourceFilePath) && (offSet != -1 && nodeSourceLocation.getOffset() == offSet || offSet == -1) && (nodeSourceLocation.getLine() <= lineNumber && nodeSourceLocation.getEndLine() >= lineNumber || lineNumber <= 1 && node.getKind().isSourceFile());
    }

    private boolean hasMoreSpecificChild(IProgramElement node, String sourceFilePath, int lineNumber, int offSet) {
        for (IProgramElement child : node.getChildren()) {
            if (!this.matches(child, sourceFilePath, lineNumber, offSet)) continue;
            return true;
        }
        return false;
    }

    @Override
    public String getConfigFile() {
        return this.configFile;
    }

    @Override
    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    @Override
    public IProgramElement findElementForHandle(String handle) {
        return this.findElementForHandleOrCreate(handle, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public IProgramElement findElementForHandleOrCreate(String handle, boolean create) {
        IProgramElement ipe = null;
        AspectJElementHierarchy aspectJElementHierarchy = this;
        synchronized (aspectJElementHierarchy) {
            ipe = this.handleMap.get(handle);
            if (ipe != null) {
                return ipe;
            }
            ipe = this.findElementForHandle(this.root, handle);
            if (ipe == null && create) {
                ipe = this.createFileStructureNode(this.getFilename(handle));
            }
            if (ipe != null) {
                this.cache(handle, ipe);
            }
        }
        return ipe;
    }

    private IProgramElement findElementForHandle(IProgramElement parent, String handle) {
        for (IProgramElement node : parent.getChildren()) {
            IProgramElement childSearch;
            String nodeHid = node.getHandleIdentifier();
            if (handle.equals(nodeHid)) {
                return node;
            }
            if (!handle.startsWith(nodeHid) || (childSearch = this.findElementForHandle(node, handle)) == null) continue;
            return childSearch;
        }
        return null;
    }

    protected void cache(String handle, IProgramElement pe) {
        if (!AsmManager.isCompletingTypeBindings()) {
            this.handleMap.put(handle, pe);
        }
    }

    @Override
    public void flushTypeMap() {
        this.typeMap.clear();
    }

    @Override
    public void flushHandleMap() {
        this.handleMap.clear();
    }

    public void flushFileMap() {
        this.fileMap.clear();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void forget(IProgramElement compilationUnitNode, IProgramElement typeNode) {
        String k = null;
        AspectJElementHierarchy aspectJElementHierarchy = this;
        synchronized (aspectJElementHierarchy) {
            for (Map.Entry<String, IProgramElement> typeMapEntry : this.typeMap.entrySet()) {
                if (typeMapEntry.getValue() != typeNode) continue;
                k = typeMapEntry.getKey();
                break;
            }
            if (k != null) {
                this.typeMap.remove(k);
            }
        }
        if (compilationUnitNode != null) {
            k = null;
            for (Map.Entry entry : this.fileMap.entrySet()) {
                if (entry.getValue() != compilationUnitNode) continue;
                k = (String)entry.getKey();
                break;
            }
            if (k != null) {
                this.fileMap.remove(k);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void updateHandleMap(Set<String> deletedFiles) {
        ArrayList<String> forRemoval = new ArrayList<String>();
        Set<String> k = null;
        Iterator iterator = this;
        synchronized (iterator) {
            IProgramElement ipe;
            k = this.handleMap.keySet();
            for (String handle : k) {
                ipe = this.handleMap.get(handle);
                if (ipe == null) {
                    System.err.println("handleMap expectation not met, where is the IPE for " + handle);
                }
                if (ipe != null && !deletedFiles.contains(this.getCanonicalFilePath(ipe))) continue;
                forRemoval.add(handle);
            }
            for (String handle : forRemoval) {
                this.handleMap.remove(handle);
            }
            forRemoval.clear();
            k = this.typeMap.keySet();
            for (String typeName : k) {
                ipe = this.typeMap.get(typeName);
                if (!deletedFiles.contains(this.getCanonicalFilePath(ipe))) continue;
                forRemoval.add(typeName);
            }
            for (String typeName : forRemoval) {
                this.typeMap.remove(typeName);
            }
            forRemoval.clear();
        }
        for (Map.Entry entry : this.fileMap.entrySet()) {
            String filePath = (String)entry.getKey();
            if (!deletedFiles.contains(this.getCanonicalFilePath((IProgramElement)entry.getValue()))) continue;
            forRemoval.add(filePath);
        }
        for (String string : forRemoval) {
            this.fileMap.remove(string);
        }
    }

    private String getFilename(String hid) {
        return this.asm.getHandleProvider().getFileForHandle(hid);
    }

    private String getCanonicalFilePath(IProgramElement ipe) {
        if (ipe.getSourceLocation() != null) {
            return this.asm.getCanonicalFilePath(ipe.getSourceLocation().getSourceFile());
        }
        return "";
    }
}

