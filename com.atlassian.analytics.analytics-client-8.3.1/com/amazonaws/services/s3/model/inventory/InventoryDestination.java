/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.inventory;

import com.amazonaws.services.s3.model.inventory.InventoryS3BucketDestination;
import java.io.Serializable;

public class InventoryDestination
implements Serializable {
    private InventoryS3BucketDestination S3BucketDestination;

    public InventoryS3BucketDestination getS3BucketDestination() {
        return this.S3BucketDestination;
    }

    public void setS3BucketDestination(InventoryS3BucketDestination s3BucketDestination) {
        this.S3BucketDestination = s3BucketDestination;
    }

    public InventoryDestination withS3BucketDestination(InventoryS3BucketDestination s3BucketDestination) {
        this.setS3BucketDestination(s3BucketDestination);
        return this;
    }
}

