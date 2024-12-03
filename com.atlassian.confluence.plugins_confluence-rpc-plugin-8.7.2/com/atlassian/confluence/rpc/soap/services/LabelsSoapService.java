/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.Addressable
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.PartialList
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.labels.LabelParser
 *  com.atlassian.confluence.labels.LabelPermissionEnforcer
 *  com.atlassian.confluence.labels.LabelPermissionSupport
 *  com.atlassian.confluence.labels.Labelable
 *  com.atlassian.confluence.labels.ParsedLabelName
 *  com.atlassian.confluence.labels.persistence.dao.LabelSearchResult
 *  com.atlassian.confluence.rpc.NotPermittedException
 *  com.atlassian.confluence.rpc.RemoteException
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceDescription
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.LabelUtil
 *  com.atlassian.renderer.WikiStyleRenderer
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.rpc.soap.services;

import com.atlassian.confluence.core.Addressable;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.PartialList;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.LabelParser;
import com.atlassian.confluence.labels.LabelPermissionEnforcer;
import com.atlassian.confluence.labels.LabelPermissionSupport;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.labels.ParsedLabelName;
import com.atlassian.confluence.labels.persistence.dao.LabelSearchResult;
import com.atlassian.confluence.rpc.NotPermittedException;
import com.atlassian.confluence.rpc.RemoteException;
import com.atlassian.confluence.rpc.soap.beans.RemoteLabel;
import com.atlassian.confluence.rpc.soap.beans.RemoteSearchResult;
import com.atlassian.confluence.rpc.soap.beans.RemoteSpace;
import com.atlassian.confluence.rpc.soap.services.SoapServiceHelper;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.LabelUtil;
import com.atlassian.renderer.WikiStyleRenderer;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class LabelsSoapService {
    LabelManager labelManager;
    ContentEntityManager contentEntityManager;
    PermissionManager permissionManager;
    WikiStyleRenderer wikiStyleRenderer;
    SoapServiceHelper soapServiceHelper;
    LabelPermissionEnforcer labelPermissionEnforcer;
    public static final String __PARANAMER_DATA = "addLabelById long,long labelId,objectId \naddLabelByName java.lang.String,long labelName,objectId \naddLabelByNameToSpace java.lang.String,java.lang.String labelName,spaceKey \naddLabelByObject com.atlassian.confluence.rpc.soap.beans.RemoteLabel,long labelObject,objectId \ngetLabelContentById long labelId \ngetLabelContentByName java.lang.String labelName \ngetLabelContentByObject com.atlassian.confluence.rpc.soap.beans.RemoteLabel labelObject \ngetLabelsByDetail java.lang.String,java.lang.String,java.lang.String,java.lang.String labelName,namespace,spaceKey,owner \ngetLabelsById long objectId \ngetMostPopularLabels int maxCount \ngetMostPopularLabelsInSpace java.lang.String,int spaceKey,maxCount \ngetRecentlyUsedLabels int maxCount \ngetRecentlyUsedLabelsInSpace java.lang.String,int spaceKey,maxCount \ngetRelatedLabels java.lang.String,int labelName,maxCount \ngetRelatedLabelsInSpace java.lang.String,java.lang.String,int labelName,spaceKey,maxCount \ngetSpacesContainingContentWithLabel java.lang.String labelName \ngetSpacesWithLabel java.lang.String labelName \nremoveLabelById long,long labelId,objectId \nremoveLabelByName java.lang.String,long labelReferences,objectId \nremoveLabelByNameFromSpace java.lang.String,java.lang.String labelName,spaceKey \nremoveLabelByObject com.atlassian.confluence.rpc.soap.beans.RemoteLabel,long labelObject,objectId \nsetContentEntityManager com.atlassian.confluence.core.ContentEntityManager contentEntityManager \nsetLabelManager com.atlassian.confluence.labels.LabelManager labelManager \nsetLabelPermissionEnforcer com.atlassian.confluence.labels.LabelPermissionEnforcer labelPermissionEnforcer \nsetPermissionManager com.atlassian.confluence.security.PermissionManager permissionManager \nsetSoapServiceHelper com.atlassian.confluence.rpc.soap.services.SoapServiceHelper soapServiceHelper \nsetWikiStyleRenderer com.atlassian.renderer.WikiStyleRenderer wikiStyleRenderer \n";

    public void setLabelManager(LabelManager labelManager) {
        this.labelManager = labelManager;
    }

    public void setContentEntityManager(ContentEntityManager contentEntityManager) {
        this.contentEntityManager = contentEntityManager;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public void setWikiStyleRenderer(WikiStyleRenderer wikiStyleRenderer) {
        this.wikiStyleRenderer = wikiStyleRenderer;
    }

    public void setSoapServiceHelper(SoapServiceHelper soapServiceHelper) {
        this.soapServiceHelper = soapServiceHelper;
    }

    public void setLabelPermissionEnforcer(LabelPermissionEnforcer labelPermissionEnforcer) {
        this.labelPermissionEnforcer = labelPermissionEnforcer;
    }

    private RemoteLabel[] generateRemoteLabelArray(List list) {
        if (list == null) {
            return null;
        }
        RemoteLabel[] remoteLabelArray = new RemoteLabel[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            RemoteLabel remoteLabel;
            Label label = this.getLabelFromUnknownLabelLikeObject(list.get(i));
            remoteLabelArray[i] = remoteLabel = new RemoteLabel(label);
        }
        return remoteLabelArray;
    }

    private RemoteLabel[] generateRemoteLabelCountArray(List list) {
        if (list == null) {
            return null;
        }
        RemoteLabel[] remoteLabelArray = new RemoteLabel[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            RemoteLabel remoteLabel;
            Label label = this.getLabelFromUnknownLabelLikeObject(list.get(i));
            remoteLabelArray[i] = remoteLabel = new RemoteLabel(label);
        }
        return remoteLabelArray;
    }

    private Label getLabelFromUnknownLabelLikeObject(Object o) {
        if (o instanceof LabelSearchResult) {
            o = ((LabelSearchResult)o).getLabel();
        }
        return (Label)o;
    }

    public RemoteLabel[] getLabelsById(long objectId) throws RemoteException {
        ContentEntityObject object = this.contentEntityManager.getById(objectId);
        this.assertObjectExists(objectId, object);
        this.assertUserCanViewObject((Labelable)object);
        List labelsArray = object.getLabels();
        List filteredList = LabelPermissionSupport.filterVisibleLabels((List)labelsArray, (User)AuthenticatedUserThreadLocal.get(), (boolean)true);
        return this.generateRemoteLabelArray(filteredList);
    }

    public RemoteLabel[] getMostPopularLabels(int maxCount) throws RemoteException {
        List labels = this.labelManager.getMostPopularLabels(maxCount);
        List filteredLabelsList = LabelPermissionSupport.filterVisibleLabels((List)labels, (User)AuthenticatedUserThreadLocal.get(), (boolean)true);
        return this.generateRemoteLabelArray(filteredLabelsList);
    }

    public RemoteLabel[] getMostPopularLabelsInSpace(String spaceKey, int maxCount) throws RemoteException {
        this.soapServiceHelper.retrieveSpace(spaceKey);
        List labelsList = this.labelManager.getMostPopularLabelsInSpace(spaceKey, maxCount);
        List filteredLabelsList = LabelPermissionSupport.filterVisibleLabels((List)labelsList, (User)AuthenticatedUserThreadLocal.get(), (boolean)true);
        return this.generateRemoteLabelCountArray(filteredLabelsList);
    }

    private RemoteSearchResult[] getFilteredContentForLabelObject(Label label) throws RemoteException {
        if (label == null) {
            throw new RemoteException("The given label ID or name was not valid");
        }
        PartialList contentPage = this.labelManager.getContentForLabel(0, -1, label);
        List content = contentPage.getList();
        ArrayList<RemoteSearchResult> remoteContent = new ArrayList<RemoteSearchResult>();
        for (ContentEntityObject object : content) {
            if (!this.labelPermissionEnforcer.userCanViewObject((Labelable)object)) continue;
            RemoteSearchResult remoteObject = new RemoteSearchResult((Addressable)object);
            remoteContent.add(remoteObject);
        }
        return remoteContent.toArray(new RemoteSearchResult[remoteContent.size()]);
    }

    public RemoteSearchResult[] getLabelContentById(long labelId) throws RemoteException {
        Label label = this.labelManager.getLabel(labelId);
        return this.getFilteredContentForLabelObject(label);
    }

    public RemoteSearchResult[] getLabelContentByName(String labelName) throws RemoteException {
        Label label = this.validateAndGetLabel(labelName);
        return this.getFilteredContentForLabelObject(label);
    }

    public RemoteSearchResult[] getLabelContentByObject(RemoteLabel labelObject) throws RemoteException {
        if (labelObject == null) {
            throw new RemoteException("The RemoteLabel object must be non-null");
        }
        return this.getLabelContentById(labelObject.getId());
    }

    public RemoteLabel[] getRecentlyUsedLabels(int maxCount) {
        List labelsList = this.labelManager.getRecentlyUsedLabels(maxCount);
        List filteredLabelsList = LabelPermissionSupport.filterVisibleLabels((List)labelsList, (User)AuthenticatedUserThreadLocal.get(), (boolean)true);
        return this.generateRemoteLabelArray(filteredLabelsList);
    }

    public RemoteLabel[] getRecentlyUsedLabelsInSpace(String spaceKey, int maxCount) throws RemoteException {
        this.soapServiceHelper.retrieveSpace(spaceKey);
        List labelsList = this.labelManager.getRecentlyUsedLabelsInSpace(spaceKey, maxCount);
        List filteredLabelsList = LabelPermissionSupport.filterVisibleLabels((List)labelsList, (User)AuthenticatedUserThreadLocal.get(), (boolean)true);
        return this.generateRemoteLabelArray(filteredLabelsList);
    }

    public RemoteSpace[] getSpacesWithLabel(String labelName) throws RemoteException {
        Label label = this.validateAndGetLabel(labelName);
        List spacesList = this.labelManager.getSpacesWithLabel(label);
        List permittedSpacesList = this.permissionManager.getPermittedEntities((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, spacesList);
        RemoteSpace[] permittedSpacesArray = new RemoteSpace[permittedSpacesList.size()];
        for (int i = 0; i < permittedSpacesList.size(); ++i) {
            Object o = permittedSpacesList.get(i);
            if (!(o instanceof Space)) {
                throw new RemoteException("Expected Space object in list, but was " + o.getClass());
            }
            permittedSpacesArray[i] = new RemoteSpace((Space)o, this.wikiStyleRenderer);
        }
        return permittedSpacesArray;
    }

    public RemoteLabel[] getRelatedLabels(String labelName, int maxCount) throws RemoteException {
        Label label = this.validateAndGetLabel(labelName);
        List relatedLabelsList = this.labelManager.getRelatedLabels(label, maxCount);
        List filteredLabelsList = LabelPermissionSupport.filterVisibleLabels((List)relatedLabelsList, (User)AuthenticatedUserThreadLocal.get(), (boolean)true);
        return this.generateRemoteLabelArray(filteredLabelsList);
    }

    public RemoteLabel[] getRelatedLabelsInSpace(String labelName, String spaceKey, int maxCount) throws RemoteException {
        Label label = this.validateAndGetLabel(labelName);
        this.soapServiceHelper.retrieveSpace(spaceKey);
        List relatedLabelsList = this.labelManager.getRelatedLabelsInSpace(label, spaceKey, maxCount);
        List filteredLabelsList = LabelPermissionSupport.filterVisibleLabels((List)relatedLabelsList, (User)AuthenticatedUserThreadLocal.get(), (boolean)true);
        return this.generateRemoteLabelArray(filteredLabelsList);
    }

    public RemoteSpace[] getSpacesContainingContentWithLabel(String labelName) throws RemoteException {
        Label label = this.validateAndGetLabel(labelName);
        List spacesList = this.labelManager.getSpacesContainingContentWithLabel(label);
        List permittedSpacesList = this.permissionManager.getPermittedEntities((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, spacesList);
        RemoteSpace[] permittedSpacesArray = new RemoteSpace[permittedSpacesList.size()];
        for (int i = 0; i < permittedSpacesList.size(); ++i) {
            Object o = permittedSpacesList.get(i);
            if (!(o instanceof Space)) {
                throw new RemoteException("Expected Space object in list, but was " + o.getClass());
            }
            permittedSpacesArray[i] = new RemoteSpace((Space)o, this.wikiStyleRenderer);
        }
        return permittedSpacesArray;
    }

    public RemoteLabel[] getLabelsByDetail(String labelName, String namespace, String spaceKey, String owner) throws RemoteException {
        if (StringUtils.isNotEmpty((CharSequence)labelName)) {
            if (!StringUtils.isNotEmpty((CharSequence)labelName)) {
                throw new RemoteException("Label name must be non-null");
            }
            ParsedLabelName parsedLabel = LabelParser.parse((String)labelName);
            if (parsedLabel == null) {
                throw new RemoteException("The label name '" + labelName + "' is not valid.");
            }
        }
        if (StringUtils.isNotEmpty((CharSequence)spaceKey)) {
            this.soapServiceHelper.retrieveSpace(spaceKey);
        }
        if (StringUtils.isNotEmpty((CharSequence)owner)) {
            this.soapServiceHelper.retrieveUser(owner);
        }
        List labelsList = this.labelManager.getLabelsByDetail(labelName, namespace, spaceKey, owner);
        List filteredLabelsList = LabelPermissionSupport.filterVisibleLabels((List)labelsList, (User)AuthenticatedUserThreadLocal.get(), (boolean)true);
        return this.generateRemoteLabelArray(filteredLabelsList);
    }

    private boolean addLabelByLabelObject(Label label, long objectId) throws NotPermittedException, RemoteException {
        if (label == null) {
            throw new RemoteException("The given label ID or name was not valid");
        }
        ContentEntityObject object = this.contentEntityManager.getById(objectId);
        this.assertObjectExists(objectId, object);
        this.assertUserCanEditLabels(label, (Labelable)object);
        this.labelManager.addLabel((Labelable)object, label);
        return true;
    }

    public boolean addLabelByName(String labelName, long objectId) throws NotPermittedException, RemoteException {
        if (!StringUtils.isNotEmpty((CharSequence)labelName)) {
            throw new RemoteException("Label name must be non-null");
        }
        ContentEntityObject object = this.contentEntityManager.getById(objectId);
        this.assertObjectExists(objectId, object);
        List labelNameArray = LabelUtil.split((String)labelName);
        ArrayList<ParsedLabelName> labelsList = new ArrayList<ParsedLabelName>();
        for (String tempLabelName : labelNameArray) {
            if (!LabelUtil.isValidLabelName((String)tempLabelName)) {
                throw new RemoteException("Label name is invalid: " + tempLabelName);
            }
            ParsedLabelName pln = LabelParser.parse((String)tempLabelName);
            if (pln == null) continue;
            this.assertUserCanEditLabels(pln.toLabel(), (Labelable)object);
            labelsList.add(pln);
        }
        for (ParsedLabelName pln : labelsList) {
            pln.addLabel((Labelable)object, this.labelManager);
        }
        return true;
    }

    public boolean addLabelById(long labelId, long objectId) throws NotPermittedException, RemoteException {
        Label label = this.labelManager.getLabel(labelId);
        return this.addLabelByLabelObject(label, objectId);
    }

    public boolean addLabelByObject(RemoteLabel labelObject, long objectId) throws NotPermittedException, RemoteException {
        if (labelObject == null) {
            throw new RemoteException("RemoteLabel object must be non-null");
        }
        return this.addLabelById(labelObject.getId(), objectId);
    }

    public boolean addLabelByNameToSpace(String labelName, String spaceKey) throws RemoteException {
        if (!StringUtils.isNotEmpty((CharSequence)labelName)) {
            throw new RemoteException("Label name must be non-null");
        }
        Space space = this.soapServiceHelper.retrieveSpace(spaceKey);
        SpaceDescription object = space.getDescription();
        List labelNameArray = LabelUtil.split((String)labelName);
        ArrayList<ParsedLabelName> labelsList = new ArrayList<ParsedLabelName>();
        for (String tempLabelName : labelNameArray) {
            ParsedLabelName pln = LabelParser.parse((String)tempLabelName);
            if (pln == null) {
                throw new RemoteException("Label name is invalid: " + tempLabelName);
            }
            if (!this.labelPermissionEnforcer.userCanEditLabel(pln, (Labelable)object)) {
                throw new NotPermittedException("You do not have the permission to add the label");
            }
            labelsList.add(pln);
        }
        for (ParsedLabelName pln : labelsList) {
            pln.addLabel((Labelable)object, this.labelManager);
        }
        return true;
    }

    public boolean removeLabelByName(String labelReferences, long objectId) throws NotPermittedException, RemoteException {
        if (!StringUtils.isNotEmpty((CharSequence)labelReferences)) {
            throw new RemoteException("Label name must be non-null");
        }
        ContentEntityObject object = this.contentEntityManager.getById(objectId);
        this.assertObjectExists(objectId, object);
        List splitLabels = LabelUtil.split((String)labelReferences);
        ArrayList<Label> labelsList = new ArrayList<Label>();
        for (String labelReference : splitLabels) {
            Label label = this.labelManager.getLabel(LabelParser.parse((String)labelReference));
            if (label == null) {
                throw new RemoteException("The given label does not exist: " + labelReference);
            }
            this.assertUserCanRemoveLabel(label, (Labelable)object);
            labelsList.add(label);
        }
        this.labelManager.removeLabels((Labelable)object, labelsList);
        return true;
    }

    public boolean removeLabelById(long labelId, long objectId) throws NotPermittedException, RemoteException {
        Label label = this.labelManager.getLabel(labelId);
        if (label == null) {
            throw new RemoteException("The given label ID or name was not valid");
        }
        ContentEntityObject object = this.contentEntityManager.getById(objectId);
        this.assertObjectExists(objectId, object);
        this.assertUserCanRemoveLabel(label, (Labelable)object);
        this.labelManager.removeLabel((Labelable)object, label);
        return true;
    }

    public boolean removeLabelByObject(RemoteLabel labelObject, long objectId) throws NotPermittedException, RemoteException {
        if (labelObject == null) {
            throw new RemoteException("RemoteLabel object must be non-null");
        }
        return this.removeLabelById(labelObject.getId(), objectId);
    }

    public boolean removeLabelByNameFromSpace(String labelName, String spaceKey) throws RemoteException {
        if (!StringUtils.isNotEmpty((CharSequence)labelName)) {
            throw new RemoteException("Label name must be non-null");
        }
        Space space = this.soapServiceHelper.retrieveSpace(spaceKey);
        this.soapServiceHelper.assertCanModifyObject(space, "spaces");
        SpaceDescription object = space.getDescription();
        List splitLabels = LabelUtil.split((String)labelName);
        ArrayList<Label> labelsList = new ArrayList<Label>();
        for (String labelReference : splitLabels) {
            Label label = this.labelManager.getLabel(LabelParser.parse((String)labelReference));
            if (label == null) {
                throw new RemoteException("The given label does not exist: " + labelReference);
            }
            this.assertUserCanRemoveLabel(label, (Labelable)object);
            labelsList.add(label);
        }
        this.labelManager.removeLabels((Labelable)object, labelsList);
        return true;
    }

    private void assertUserCanViewObject(Labelable object) throws RemoteException {
        if (!this.labelPermissionEnforcer.userCanViewObject(object)) {
            throw new RemoteException("You're not allowed to view that ContentEntityObject, or it does not exist.");
        }
    }

    private void assertUserCanEditLabels(Label label, Labelable object) throws NotPermittedException, RemoteException {
        if (!this.labelPermissionEnforcer.userCanEditLabel(label, object)) {
            throw new NotPermittedException("You do not have permissions to add labels to this object.");
        }
    }

    private void assertUserCanRemoveLabel(Label label, Labelable object) throws NotPermittedException {
        if (!this.labelPermissionEnforcer.userCanEditLabel(label, object)) {
            throw new NotPermittedException("You do not have permission to remove the label '" + label.getName() + "'");
        }
    }

    private void assertObjectExists(long id, ContentEntityObject object) throws RemoteException {
        if (object == null) {
            throw new RemoteException("The object with content id '" + id + "' does not exist.");
        }
    }

    private Label validateAndGetLabel(String labelName) throws RemoteException {
        if (!StringUtils.isNotEmpty((CharSequence)labelName)) {
            throw new RemoteException("Label name must be non-null");
        }
        ParsedLabelName parsedLabel = LabelParser.parse((String)labelName);
        if (parsedLabel == null) {
            throw new RemoteException("The label name '" + labelName + "' is not valid.");
        }
        Label label = this.labelManager.getLabel(parsedLabel);
        if (label == null) {
            throw new RemoteException("The label '" + labelName + "' does not exist.");
        }
        return label;
    }
}

