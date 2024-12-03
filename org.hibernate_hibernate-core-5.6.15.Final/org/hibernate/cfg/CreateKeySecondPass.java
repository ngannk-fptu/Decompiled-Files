/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cfg;

import java.util.Map;
import org.hibernate.MappingException;
import org.hibernate.cfg.SecondPass;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.RootClass;

public class CreateKeySecondPass
implements SecondPass {
    private RootClass rootClass;
    private JoinedSubclass joinedSubClass;

    public CreateKeySecondPass(RootClass rootClass) {
        this.rootClass = rootClass;
    }

    public CreateKeySecondPass(JoinedSubclass joinedSubClass) {
        this.joinedSubClass = joinedSubClass;
    }

    @Override
    public void doSecondPass(Map persistentClasses) throws MappingException {
        if (this.rootClass != null) {
            this.rootClass.createPrimaryKey();
        } else if (this.joinedSubClass != null) {
            this.joinedSubClass.createPrimaryKey();
            this.joinedSubClass.createForeignKey();
        } else {
            throw new AssertionError((Object)"rootClass and joinedSubClass are null");
        }
    }
}

