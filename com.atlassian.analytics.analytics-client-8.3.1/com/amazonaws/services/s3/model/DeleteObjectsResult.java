/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.internal.S3RequesterChargedResult;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DeleteObjectsResult
implements Serializable,
S3RequesterChargedResult {
    private final List<DeletedObject> deletedObjects = new ArrayList<DeletedObject>();
    private boolean isRequesterCharged;

    public DeleteObjectsResult(List<DeletedObject> deletedObjects) {
        this(deletedObjects, false);
    }

    public DeleteObjectsResult(List<DeletedObject> deletedObjects, boolean isRequesterCharged) {
        this.deletedObjects.addAll(deletedObjects);
        this.setRequesterCharged(isRequesterCharged);
    }

    public List<DeletedObject> getDeletedObjects() {
        return this.deletedObjects;
    }

    @Override
    public boolean isRequesterCharged() {
        return this.isRequesterCharged;
    }

    @Override
    public void setRequesterCharged(boolean isRequesterCharged) {
        this.isRequesterCharged = isRequesterCharged;
    }

    public static class DeletedObject
    implements Serializable {
        private String key;
        private String versionId;
        private boolean deleteMarker;
        private String deleteMarkerVersionId;

        public String getKey() {
            return this.key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getVersionId() {
            return this.versionId;
        }

        public void setVersionId(String versionId) {
            this.versionId = versionId;
        }

        public boolean isDeleteMarker() {
            return this.deleteMarker;
        }

        public void setDeleteMarker(boolean deleteMarker) {
            this.deleteMarker = deleteMarker;
        }

        public String getDeleteMarkerVersionId() {
            return this.deleteMarkerVersionId;
        }

        public void setDeleteMarkerVersionId(String deleteMarkerVersionId) {
            this.deleteMarkerVersionId = deleteMarkerVersionId;
        }
    }
}

