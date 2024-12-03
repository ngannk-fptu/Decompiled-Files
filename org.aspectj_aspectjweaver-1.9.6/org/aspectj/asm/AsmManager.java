/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.asm;

import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.aspectj.asm.IElementHandleProvider;
import org.aspectj.asm.IHierarchy;
import org.aspectj.asm.IHierarchyListener;
import org.aspectj.asm.IModelFilter;
import org.aspectj.asm.IProgramElement;
import org.aspectj.asm.IRelationship;
import org.aspectj.asm.IRelationshipMap;
import org.aspectj.asm.internal.AspectJElementHierarchy;
import org.aspectj.asm.internal.HandleProviderDelimiter;
import org.aspectj.asm.internal.JDTLikeHandleProvider;
import org.aspectj.asm.internal.RelationshipMap;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.util.IStructureModel;

public class AsmManager
implements IStructureModel {
    public static boolean recordingLastActiveStructureModel = true;
    public static AsmManager lastActiveStructureModel;
    public static boolean forceSingletonBehaviour;
    public static boolean attemptIncrementalModelRepairs;
    public static boolean dumpModelPostBuild;
    private static boolean dumpModel;
    private static boolean dumpRelationships;
    private static boolean dumpDeltaProcessing;
    private static IModelFilter modelFilter;
    private static String dumpFilename;
    private static boolean reporting;
    private static boolean completingTypeBindings;
    private final List<IHierarchyListener> structureListeners = new ArrayList<IHierarchyListener>();
    protected IHierarchy hierarchy;
    protected Map<File, String> inpathMap;
    private IRelationshipMap mapper;
    private IElementHandleProvider handleProvider;
    private final CanonicalFilePathMap canonicalFilePathMap = new CanonicalFilePathMap();
    private final Set<File> lastBuildChanges = new HashSet<File>();
    final Set<File> aspectsWeavingInLastBuild = new HashSet<File>();

    private AsmManager() {
    }

    public static AsmManager createNewStructureModel(Map<File, String> inpathMap) {
        if (forceSingletonBehaviour && lastActiveStructureModel != null) {
            return lastActiveStructureModel;
        }
        AsmManager asm = new AsmManager();
        asm.inpathMap = inpathMap;
        asm.hierarchy = new AspectJElementHierarchy(asm);
        asm.mapper = new RelationshipMap();
        asm.handleProvider = new JDTLikeHandleProvider(asm);
        asm.handleProvider.initialize();
        asm.resetDeltaProcessing();
        AsmManager.setLastActiveStructureModel(asm);
        return asm;
    }

    public IHierarchy getHierarchy() {
        return this.hierarchy;
    }

    public IRelationshipMap getRelationshipMap() {
        return this.mapper;
    }

    public void fireModelUpdated() {
        this.notifyListeners();
        if (dumpModelPostBuild && this.hierarchy.getConfigFile() != null) {
            this.writeStructureModel(this.hierarchy.getConfigFile());
        }
    }

    public HashMap<Integer, List<IProgramElement>> getInlineAnnotations(String sourceFile, boolean showSubMember, boolean showMemberAndType) {
        if (!this.hierarchy.isValid()) {
            return null;
        }
        HashMap<Integer, List<IProgramElement>> annotations = new HashMap<Integer, List<IProgramElement>>();
        IProgramElement node = this.hierarchy.findElementForSourceFile(sourceFile);
        if (node == IHierarchy.NO_STRUCTURE) {
            return null;
        }
        IProgramElement fileNode = node;
        ArrayList<IProgramElement> peNodes = new ArrayList<IProgramElement>();
        this.getAllStructureChildren(fileNode, peNodes, showSubMember, showMemberAndType);
        for (IProgramElement peNode : peNodes) {
            ArrayList<IProgramElement> entries = new ArrayList<IProgramElement>();
            entries.add(peNode);
            ISourceLocation sourceLoc = peNode.getSourceLocation();
            if (null == sourceLoc) continue;
            Integer hash = new Integer(sourceLoc.getLine());
            List<IProgramElement> existingEntry = annotations.get(hash);
            if (existingEntry != null) {
                entries.addAll(existingEntry);
            }
            annotations.put(hash, entries);
        }
        return annotations;
    }

    private void getAllStructureChildren(IProgramElement node, List<IProgramElement> result, boolean showSubMember, boolean showMemberAndType) {
        List<IProgramElement> children = node.getChildren();
        if (node.getChildren() == null) {
            return;
        }
        for (IProgramElement next : children) {
            List<IRelationship> rels = this.mapper.get(next);
            if (next != null && (next.getKind() == IProgramElement.Kind.CODE && showSubMember || next.getKind() != IProgramElement.Kind.CODE && showMemberAndType) && rels != null && rels.size() > 0) {
                result.add(next);
            }
            this.getAllStructureChildren(next, result, showSubMember, showMemberAndType);
        }
    }

    public void addListener(IHierarchyListener listener) {
        this.structureListeners.add(listener);
    }

    public void removeStructureListener(IHierarchyListener listener) {
        this.structureListeners.remove(listener);
    }

    public void removeAllListeners() {
        this.structureListeners.clear();
    }

    private void notifyListeners() {
        for (IHierarchyListener listener : this.structureListeners) {
            listener.elementsUpdated(this.hierarchy);
        }
    }

    public IElementHandleProvider getHandleProvider() {
        return this.handleProvider;
    }

    public void setHandleProvider(IElementHandleProvider handleProvider) {
        this.handleProvider = handleProvider;
    }

    public void writeStructureModel(String configFilePath) {
        try {
            String filePath = this.genExternFilePath(configFilePath);
            FileOutputStream fos = new FileOutputStream(filePath);
            ObjectOutputStream s = new ObjectOutputStream(fos);
            s.writeObject(this.hierarchy);
            s.writeObject(this.mapper);
            s.flush();
            fos.flush();
            fos.close();
            s.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void readStructureModel(String configFilePath) {
        boolean hierarchyReadOK = false;
        try {
            if (configFilePath == null) {
                this.hierarchy.setRoot(IHierarchy.NO_STRUCTURE);
            } else {
                String filePath = this.genExternFilePath(configFilePath);
                FileInputStream in = new FileInputStream(filePath);
                ObjectInputStream s = new ObjectInputStream(in);
                this.hierarchy = (AspectJElementHierarchy)s.readObject();
                ((AspectJElementHierarchy)this.hierarchy).setAsmManager(this);
                hierarchyReadOK = true;
                this.mapper = (RelationshipMap)s.readObject();
                s.close();
            }
        }
        catch (FileNotFoundException fnfe) {
            this.hierarchy.setRoot(IHierarchy.NO_STRUCTURE);
        }
        catch (EOFException eofe) {
            if (!hierarchyReadOK) {
                System.err.println("AsmManager: Unable to read structure model: " + configFilePath + " because of:");
                eofe.printStackTrace();
                this.hierarchy.setRoot(IHierarchy.NO_STRUCTURE);
            }
        }
        catch (Exception e) {
            this.hierarchy.setRoot(IHierarchy.NO_STRUCTURE);
        }
        finally {
            this.notifyListeners();
        }
    }

    private String genExternFilePath(String configFilePath) {
        if (configFilePath.lastIndexOf(".lst") != -1) {
            configFilePath = configFilePath.substring(0, configFilePath.lastIndexOf(".lst"));
        }
        return configFilePath + ".ajsym";
    }

    public String getCanonicalFilePath(File f) {
        return this.canonicalFilePathMap.get(f);
    }

    public CanonicalFilePathMap getCanonicalFilePathMap() {
        return this.canonicalFilePathMap;
    }

    public static void setReporting(String filename, boolean dModel, boolean dRels, boolean dDeltaProcessing, boolean deletefile) {
        reporting = true;
        dumpModel = dModel;
        dumpRelationships = dRels;
        dumpDeltaProcessing = dDeltaProcessing;
        if (deletefile) {
            new File(filename).delete();
        }
        dumpFilename = filename;
    }

    public static void setReporting(String filename, boolean dModel, boolean dRels, boolean dDeltaProcessing, boolean deletefile, IModelFilter aFilter) {
        AsmManager.setReporting(filename, dModel, dRels, dDeltaProcessing, deletefile);
        modelFilter = aFilter;
    }

    public static boolean isReporting() {
        return reporting;
    }

    public static void setDontReport() {
        reporting = false;
        dumpDeltaProcessing = false;
        dumpModel = false;
        dumpRelationships = false;
    }

    public void reportModelInfo(String reasonForReport) {
        if (!dumpModel && !dumpRelationships) {
            return;
        }
        try {
            FileWriter fw = new FileWriter(dumpFilename, true);
            BufferedWriter bw = new BufferedWriter(fw);
            if (dumpModel) {
                bw.write("=== MODEL STATUS REPORT ========= " + reasonForReport + "\n");
                AsmManager.dumptree(bw, this.hierarchy.getRoot(), 0);
                bw.write("=== END OF MODEL REPORT =========\n");
            }
            if (dumpRelationships) {
                bw.write("=== RELATIONSHIPS REPORT ========= " + reasonForReport + "\n");
                this.dumprels(bw);
                bw.write("=== END OF RELATIONSHIPS REPORT ==\n");
            }
            Properties p = this.summarizeModel().getProperties();
            Enumeration<Object> pkeyenum = p.keys();
            bw.write("=== Properties of the model and relationships map =====\n");
            while (pkeyenum.hasMoreElements()) {
                String pkey = (String)pkeyenum.nextElement();
                bw.write(pkey + "=" + p.getProperty(pkey) + "\n");
            }
            bw.flush();
            fw.close();
        }
        catch (IOException e) {
            System.err.println("InternalError: Unable to report model information:");
            e.printStackTrace();
        }
    }

    public static void dumptree(Writer w, IProgramElement node, int indent) throws IOException {
        for (int i = 0; i < indent; ++i) {
            w.write(" ");
        }
        String loc = "";
        if (node != null && node.getSourceLocation() != null) {
            loc = node.getSourceLocation().toString();
            if (modelFilter != null) {
                loc = modelFilter.processFilelocation(loc);
            }
        }
        w.write(node + "  [" + (node == null ? "null" : node.getKind().toString()) + "] " + loc + "\n");
        if (node != null) {
            for (IProgramElement child : node.getChildren()) {
                AsmManager.dumptree(w, child, indent + 2);
            }
        }
    }

    public static void dumptree(IProgramElement node, int indent) throws IOException {
        for (int i = 0; i < indent; ++i) {
            System.out.print(" ");
        }
        String loc = "";
        if (node != null && node.getSourceLocation() != null) {
            loc = node.getSourceLocation().toString();
        }
        System.out.println(node + "  [" + (node == null ? "null" : node.getKind().toString()) + "] " + loc);
        if (node != null) {
            for (IProgramElement child : node.getChildren()) {
                AsmManager.dumptree(child, indent + 2);
            }
        }
    }

    public void dumprels(Writer w) throws IOException {
        int ctr = 1;
        Set<String> entries = this.mapper.getEntries();
        for (String hid : entries) {
            List<IRelationship> rels = this.mapper.get(hid);
            for (IRelationship ir : rels) {
                List<String> targets = ir.getTargets();
                for (String thid : targets) {
                    StringBuffer sb = new StringBuffer();
                    if (modelFilter == null || modelFilter.wantsHandleIds()) {
                        sb.append("Hid:" + ctr++ + ":");
                    }
                    sb.append("(targets=" + targets.size() + ") " + hid + " (" + ir.getName() + ") " + thid + "\n");
                    w.write(sb.toString());
                }
            }
        }
    }

    private void dumprelsStderr(String key) {
        System.err.println("Relationships dump follows: " + key);
        int ctr = 1;
        Set<String> entries = this.mapper.getEntries();
        for (String hid : entries) {
            for (IRelationship ir : this.mapper.get(hid)) {
                List<String> targets = ir.getTargets();
                for (String thid : targets) {
                    System.err.println("Hid:" + ctr++ + ":(targets=" + targets.size() + ") " + hid + " (" + ir.getName() + ") " + thid);
                }
            }
        }
        System.err.println("End of relationships dump for: " + key);
    }

    public boolean removeStructureModelForFiles(Writer fw, Collection<File> files) throws IOException {
        boolean modelModified = false;
        HashSet<String> deletedNodes = new HashSet<String>();
        for (File fileForCompilation : files) {
            String correctedPath = this.getCanonicalFilePath(fileForCompilation);
            IProgramElement progElem = (IProgramElement)this.hierarchy.findInFileMap(correctedPath);
            if (progElem == null) continue;
            if (dumpDeltaProcessing) {
                fw.write("Deleting " + progElem + " node for file " + fileForCompilation + "\n");
            }
            this.removeNode(progElem);
            this.lastBuildChanges.add(fileForCompilation);
            deletedNodes.add(this.getCanonicalFilePath(progElem.getSourceLocation().getSourceFile()));
            if (!this.hierarchy.removeFromFileMap(correctedPath)) {
                throw new RuntimeException("Whilst repairing model, couldn't remove entry for file: " + correctedPath + " from the filemap");
            }
            modelModified = true;
        }
        if (modelModified) {
            this.hierarchy.updateHandleMap(deletedNodes);
        }
        return modelModified;
    }

    public void processDelta(Collection<File> files_tobecompiled, Set<File> files_added, Set<File> files_deleted) {
        try {
            BufferedWriter fw = null;
            if (dumpDeltaProcessing) {
                FileWriter filew = new FileWriter(dumpFilename, true);
                fw = new BufferedWriter(filew);
                fw.write("=== Processing delta changes for the model ===\n");
                fw.write("Files for compilation:#" + files_tobecompiled.size() + ":" + files_tobecompiled + "\n");
                fw.write("Files added          :#" + files_added.size() + ":" + files_added + "\n");
                fw.write("Files deleted        :#" + files_deleted.size() + ":" + files_deleted + "\n");
            }
            long stime = System.currentTimeMillis();
            this.removeStructureModelForFiles(fw, files_deleted);
            long etime1 = System.currentTimeMillis();
            this.repairRelationships(fw);
            long etime2 = System.currentTimeMillis();
            this.removeStructureModelForFiles(fw, files_tobecompiled);
            if (dumpDeltaProcessing) {
                fw.write("===== Delta Processing timing ==========\n");
                fw.write("Hierarchy=" + (etime1 - stime) + "ms   Relationshipmap=" + (etime2 - etime1) + "ms\n");
                fw.write("===== Traversal ========================\n");
                fw.write("========================================\n");
                ((Writer)fw).flush();
                ((Writer)fw).close();
            }
            this.reportModelInfo("After delta processing");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getTypeNameFromHandle(String handle, Map<String, String> cache) {
        try {
            String typename = cache.get(handle);
            if (typename != null) {
                return typename;
            }
            int hasPackage = handle.indexOf(HandleProviderDelimiter.PACKAGEFRAGMENT.getDelimiter());
            int typeLocation = handle.indexOf(HandleProviderDelimiter.TYPE.getDelimiter());
            if (typeLocation == -1) {
                typeLocation = handle.indexOf(HandleProviderDelimiter.ASPECT_TYPE.getDelimiter());
            }
            if (typeLocation == -1) {
                return "";
            }
            StringBuffer qualifiedTypeNameFromHandle = new StringBuffer();
            if (hasPackage != -1) {
                int classfileLoc = handle.indexOf(HandleProviderDelimiter.CLASSFILE.getDelimiter(), hasPackage);
                qualifiedTypeNameFromHandle.append(handle.substring(hasPackage + 1, classfileLoc));
                qualifiedTypeNameFromHandle.append('.');
            }
            qualifiedTypeNameFromHandle.append(handle.substring(typeLocation + 1));
            typename = qualifiedTypeNameFromHandle.toString();
            cache.put(handle, typename);
            return typename;
        }
        catch (StringIndexOutOfBoundsException sioobe) {
            System.err.println("Handle processing problem, the handle is: " + handle);
            sioobe.printStackTrace(System.err);
            return "";
        }
    }

    public void removeRelationshipsTargettingThisType(String typename) {
        IProgramElement ipe;
        boolean debug = false;
        if (debug) {
            System.err.println(">>removeRelationshipsTargettingThisType " + typename);
        }
        String pkg = null;
        String type = typename;
        int lastSep = typename.lastIndexOf(46);
        if (lastSep != -1) {
            pkg = typename.substring(0, lastSep);
            type = typename.substring(lastSep + 1);
        }
        boolean didsomething = false;
        IProgramElement typeNode = this.hierarchy.findElementForType(pkg, type);
        if (typeNode == null) {
            return;
        }
        HashSet<String> sourcesToRemove = new HashSet<String>();
        HashMap<String, String> handleToTypenameCache = new HashMap<String, String>();
        Set<String> sourcehandlesSet = this.mapper.getEntries();
        ArrayList<IRelationship> relationshipsToRemove = new ArrayList<IRelationship>();
        for (String hid : sourcehandlesSet) {
            IProgramElement sourceElement;
            if (this.isPhantomHandle(hid) && !this.getTypeNameFromHandle(hid, handleToTypenameCache).equals(typename) || (sourceElement = this.hierarchy.getElement(hid)) != null && !this.sameType(hid, sourceElement, typeNode)) continue;
            relationshipsToRemove.clear();
            List<IRelationship> relationships = this.mapper.get(hid);
            for (IRelationship relationship : relationships) {
                if (relationship.getKind() == IRelationship.Kind.USES_POINTCUT || relationship.isAffects()) continue;
                relationshipsToRemove.add(relationship);
            }
            if (relationshipsToRemove.size() <= 0) continue;
            didsomething = true;
            if (relationshipsToRemove.size() == relationships.size()) {
                sourcesToRemove.add(hid);
                continue;
            }
            for (int i = 0; i < relationshipsToRemove.size(); ++i) {
                relationships.remove(relationshipsToRemove.get(i));
            }
        }
        for (String hid : sourcesToRemove) {
            this.mapper.removeAll(hid);
            ipe = this.hierarchy.getElement(hid);
            if (ipe == null || !ipe.getKind().equals(IProgramElement.Kind.CODE)) continue;
            if (debug) {
                System.err.println("  source handle: it was code node, removing that as well... code=" + ipe + " parent=" + ipe.getParent());
            }
            this.removeSingleNode(ipe);
        }
        if (debug) {
            this.dumprelsStderr("after processing 'affectedby'");
        }
        if (didsomething) {
            sourcesToRemove.clear();
            if (debug) {
                this.dumprelsStderr("before processing 'affects'");
            }
            sourcehandlesSet = this.mapper.getEntries();
            for (String hid : sourcehandlesSet) {
                relationshipsToRemove.clear();
                List<IRelationship> relationships = this.mapper.get(hid);
                for (IRelationship rel : relationships) {
                    if (rel.getKind() == IRelationship.Kind.USES_POINTCUT || !rel.isAffects()) continue;
                    List<String> targets = rel.getTargets();
                    ArrayList<String> targetsToRemove = new ArrayList<String>();
                    for (String targethid : targets) {
                        IProgramElement existingTarget;
                        if (this.isPhantomHandle(hid) && !this.getTypeNameFromHandle(hid, handleToTypenameCache).equals(typename) || (existingTarget = this.hierarchy.getElement(targethid)) != null && !this.sameType(targethid, existingTarget, typeNode)) continue;
                        targetsToRemove.add(targethid);
                    }
                    if (targetsToRemove.size() == 0) continue;
                    if (targetsToRemove.size() == targets.size()) {
                        relationshipsToRemove.add(rel);
                        continue;
                    }
                    for (String togo : targetsToRemove) {
                        targets.remove(togo);
                    }
                }
                if (relationshipsToRemove.size() <= 0) continue;
                if (relationshipsToRemove.size() == relationships.size()) {
                    sourcesToRemove.add(hid);
                    continue;
                }
                for (int i = 0; i < relationshipsToRemove.size(); ++i) {
                    relationships.remove(relationshipsToRemove.get(i));
                }
            }
            for (String hid : sourcesToRemove) {
                this.mapper.removeAll(hid);
                ipe = this.hierarchy.getElement(hid);
                if (ipe == null || !ipe.getKind().equals(IProgramElement.Kind.CODE)) continue;
                if (debug) {
                    System.err.println("  source handle: it was code node, removing that as well... code=" + ipe + " parent=" + ipe.getParent());
                }
                this.removeSingleNode(ipe);
            }
            if (debug) {
                this.dumprelsStderr("after processing 'affects'");
            }
        }
        if (debug) {
            System.err.println("<<removeRelationshipsTargettingThisFile");
        }
    }

    private boolean sameType(String hid, IProgramElement target, IProgramElement type) {
        IProgramElement containingType = target;
        if (target == null) {
            throw new RuntimeException("target can't be null!");
        }
        if (type == null) {
            throw new RuntimeException("type can't be null!");
        }
        if (target.getKind().isSourceFile() || target.getKind().isFile()) {
            if (target.getSourceLocation() == null) {
                return false;
            }
            if (type.getSourceLocation() == null) {
                return false;
            }
            if (target.getSourceLocation().getSourceFile() == null) {
                return false;
            }
            if (type.getSourceLocation().getSourceFile() == null) {
                return false;
            }
            return target.getSourceLocation().getSourceFile().equals(type.getSourceLocation().getSourceFile());
        }
        try {
            while (!containingType.getKind().isType()) {
                containingType = containingType.getParent();
            }
        }
        catch (Throwable t) {
            throw new RuntimeException("Exception whilst walking up from target " + target.toLabelString() + " kind=(" + target.getKind() + ") hid=(" + target.getHandleIdentifier() + ")", t);
        }
        return type.equals(containingType);
    }

    private boolean isPhantomHandle(String handle) {
        int phantomMarker = handle.indexOf(HandleProviderDelimiter.PHANTOM.getDelimiter());
        return phantomMarker != -1 && handle.charAt(phantomMarker - 1) == HandleProviderDelimiter.PACKAGEFRAGMENTROOT.getDelimiter();
    }

    private void repairRelationships(Writer fw) {
        try {
            if (dumpDeltaProcessing) {
                fw.write("Repairing relationships map:\n");
            }
            HashSet<String> sourcesToRemove = new HashSet<String>();
            HashSet<String> nonExistingHandles = new HashSet<String>();
            Set<String> keyset = this.mapper.getEntries();
            for (String hid : keyset) {
                if (nonExistingHandles.contains(hid)) {
                    sourcesToRemove.add(hid);
                    continue;
                }
                if (this.isPhantomHandle(hid)) continue;
                IProgramElement existingElement = this.hierarchy.getElement(hid);
                if (dumpDeltaProcessing) {
                    fw.write("Looking for handle [" + hid + "] in model, found: " + existingElement + "\n");
                }
                if (existingElement == null) {
                    sourcesToRemove.add(hid);
                    nonExistingHandles.add(hid);
                    continue;
                }
                List<IRelationship> relationships = this.mapper.get(hid);
                ArrayList<IRelationship> relationshipsToRemove = new ArrayList<IRelationship>();
                for (IRelationship rel : relationships) {
                    List<String> targets = rel.getTargets();
                    ArrayList<String> targetsToRemove = new ArrayList<String>();
                    for (String targethid : targets) {
                        IProgramElement existingTarget;
                        if (nonExistingHandles.contains(targethid)) {
                            if (dumpDeltaProcessing) {
                                fw.write("Target handle [" + targethid + "] for srchid[" + hid + "]rel[" + rel.getName() + "] does not exist\n");
                            }
                            targetsToRemove.add(targethid);
                            continue;
                        }
                        if (this.isPhantomHandle(targethid) || (existingTarget = this.hierarchy.getElement(targethid)) != null) continue;
                        if (dumpDeltaProcessing) {
                            fw.write("Target handle [" + targethid + "] for srchid[" + hid + "]rel[" + rel.getName() + "] does not exist\n");
                        }
                        targetsToRemove.add(targethid);
                        nonExistingHandles.add(targethid);
                    }
                    if (targetsToRemove.size() == 0) continue;
                    if (targetsToRemove.size() == targets.size()) {
                        if (dumpDeltaProcessing) {
                            fw.write("No targets remain for srchid[" + hid + "] rel[" + rel.getName() + "]: removing it\n");
                        }
                        relationshipsToRemove.add(rel);
                        continue;
                    }
                    for (String togo : targetsToRemove) {
                        targets.remove(togo);
                    }
                    if (targets.size() != 0) continue;
                    if (dumpDeltaProcessing) {
                        fw.write("No targets remain for srchid[" + hid + "] rel[" + rel.getName() + "]: removing it\n");
                    }
                    relationshipsToRemove.add(rel);
                }
                if (relationshipsToRemove.size() <= 0) continue;
                if (relationshipsToRemove.size() == relationships.size()) {
                    sourcesToRemove.add(hid);
                    continue;
                }
                for (int i = 0; i < relationshipsToRemove.size(); ++i) {
                    IRelationship irel = (IRelationship)relationshipsToRemove.get(i);
                    AsmManager.verifyAssumption(this.mapper.remove(hid, irel), "Failed to remove relationship " + irel.getName() + " for shid " + hid);
                }
                List<IRelationship> rels = this.mapper.get(hid);
                if (rels != null && rels.size() != 0) continue;
                sourcesToRemove.add(hid);
            }
            for (String hid : sourcesToRemove) {
                this.mapper.removeAll(hid);
                IProgramElement ipe = this.hierarchy.getElement(hid);
                if (ipe == null || !ipe.getKind().equals(IProgramElement.Kind.CODE)) continue;
                this.removeSingleNode(ipe);
            }
        }
        catch (IOException ioe) {
            System.err.println("Failed to repair relationships:");
            ioe.printStackTrace();
        }
    }

    private void removeSingleNode(IProgramElement progElem) {
        if (progElem == null) {
            throw new IllegalStateException("AsmManager.removeNode(): programElement unexpectedly null");
        }
        boolean deleteOK = false;
        IProgramElement parent = progElem.getParent();
        List<IProgramElement> kids = parent.getChildren();
        int max = kids.size();
        for (int i = 0; i < max; ++i) {
            if (!kids.get(i).equals(progElem)) continue;
            kids.remove(i);
            deleteOK = true;
            break;
        }
        if (!deleteOK) {
            System.err.println("unexpectedly failed to delete node from model.  hid=" + progElem.getHandleIdentifier());
        }
    }

    private void removeNode(IProgramElement progElem) {
        try {
            if (progElem == null) {
                throw new IllegalStateException("AsmManager.removeNode(): programElement unexpectedly null");
            }
            IProgramElement parent = progElem.getParent();
            List<IProgramElement> kids = parent.getChildren();
            for (int i = 0; i < kids.size(); ++i) {
                if (!kids.get(i).equals(progElem)) continue;
                kids.remove(i);
                break;
            }
            if (parent.getChildren().size() == 0 && parent.getParent() != null && (parent.getKind().equals(IProgramElement.Kind.CODE) || parent.getKind().equals(IProgramElement.Kind.PACKAGE))) {
                this.removeNode(parent);
            }
        }
        catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }

    public static void verifyAssumption(boolean b, String info) {
        if (!b) {
            System.err.println("=========== ASSERTION IS NOT TRUE =========v");
            System.err.println(info);
            Thread.dumpStack();
            System.err.println("=========== ASSERTION IS NOT TRUE =========^");
            throw new RuntimeException("Assertion is false");
        }
    }

    public static void verifyAssumption(boolean b) {
        if (!b) {
            Thread.dumpStack();
            throw new RuntimeException("Assertion is false");
        }
    }

    public ModelInfo summarizeModel() {
        return new ModelInfo(this.getHierarchy(), this.getRelationshipMap());
    }

    public static void setCompletingTypeBindings(boolean b) {
        completingTypeBindings = b;
    }

    public static boolean isCompletingTypeBindings() {
        return completingTypeBindings;
    }

    public void resetDeltaProcessing() {
        this.lastBuildChanges.clear();
        this.aspectsWeavingInLastBuild.clear();
    }

    public Set<File> getModelChangesOnLastBuild() {
        return this.lastBuildChanges;
    }

    public Set<File> getAspectsWeavingFilesOnLastBuild() {
        return this.aspectsWeavingInLastBuild;
    }

    public void addAspectInEffectThisBuild(File f) {
        this.aspectsWeavingInLastBuild.add(f);
    }

    public static void setLastActiveStructureModel(AsmManager structureModel) {
        if (recordingLastActiveStructureModel) {
            lastActiveStructureModel = structureModel;
        }
    }

    public String getHandleElementForInpath(String binaryPath) {
        return this.inpathMap.get(new File(binaryPath));
    }

    static {
        forceSingletonBehaviour = false;
        attemptIncrementalModelRepairs = false;
        dumpModelPostBuild = false;
        dumpModel = false;
        dumpRelationships = false;
        dumpDeltaProcessing = false;
        modelFilter = null;
        dumpFilename = "";
        reporting = false;
        completingTypeBindings = false;
    }

    public static class ModelInfo {
        private final Hashtable<String, Integer> nodeTypeCount = new Hashtable();
        private final Properties extraProperties = new Properties();

        private ModelInfo(IHierarchy hierarchy, IRelationshipMap relationshipMap) {
            IProgramElement ipe = hierarchy.getRoot();
            this.walkModel(ipe);
            this.recordStat("FileMapSize", new Integer(hierarchy.getFileMapEntrySet().size()).toString());
            this.recordStat("RelationshipMapSize", new Integer(relationshipMap.getEntries().size()).toString());
        }

        private void walkModel(IProgramElement ipe) {
            this.countNode(ipe);
            for (IProgramElement child : ipe.getChildren()) {
                this.walkModel(child);
            }
        }

        private void countNode(IProgramElement ipe) {
            String node = ipe.getKind().toString();
            Integer ctr = this.nodeTypeCount.get(node);
            if (ctr == null) {
                this.nodeTypeCount.put(node, new Integer(1));
            } else {
                ctr = new Integer(ctr + 1);
                this.nodeTypeCount.put(node, ctr);
            }
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("Model node summary:\n");
            Enumeration<String> nodeKeys = this.nodeTypeCount.keys();
            while (nodeKeys.hasMoreElements()) {
                String key = nodeKeys.nextElement();
                Integer ct = this.nodeTypeCount.get(key);
                sb.append(key + "=" + ct + "\n");
            }
            sb.append("Model stats:\n");
            Enumeration<Object> ks = this.extraProperties.keys();
            while (ks.hasMoreElements()) {
                String k = (String)ks.nextElement();
                String v = this.extraProperties.getProperty(k);
                sb.append(k + "=" + v + "\n");
            }
            return sb.toString();
        }

        public Properties getProperties() {
            Properties p = new Properties();
            for (Map.Entry<String, Integer> entry : this.nodeTypeCount.entrySet()) {
                p.setProperty(entry.getKey(), entry.getValue().toString());
            }
            p.putAll((Map<?, ?>)this.extraProperties);
            return p;
        }

        public void recordStat(String string, String string2) {
            this.extraProperties.setProperty(string, string2);
        }
    }

    private static class CanonicalFilePathMap {
        private static final int MAX_SIZE = 4000;
        private final Map<String, String> pathMap = new HashMap<String, String>(20);

        private CanonicalFilePathMap() {
        }

        public String get(File f) {
            String ret = this.pathMap.get(f.getPath());
            if (ret == null) {
                try {
                    ret = f.getCanonicalPath();
                }
                catch (IOException ioEx) {
                    ret = f.getPath();
                }
                this.pathMap.put(f.getPath(), ret);
                if (this.pathMap.size() > 4000) {
                    this.pathMap.clear();
                }
            }
            return ret;
        }
    }
}

