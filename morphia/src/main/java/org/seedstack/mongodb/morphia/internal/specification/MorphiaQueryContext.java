package org.seedstack.mongodb.morphia.internal.specification;

import org.mongodb.morphia.query.FieldEnd;
import org.mongodb.morphia.query.Query;

import static com.google.common.base.Preconditions.checkState;

public class MorphiaQueryContext<T> {
    private FieldEnd<? extends Query<T>> fieldEnd;

    public FieldEnd<? extends Query<T>> pickFieldEnd() {
        checkState(this.fieldEnd != null, "No field has been set");
        FieldEnd<? extends Query<T>> result = this.fieldEnd;
        this.fieldEnd = null;
        return result;
    }

    public void setFieldEnd(FieldEnd<? extends Query<T>> fieldEnd) {
        checkState(this.fieldEnd == null, "A field is already set");
        this.fieldEnd = fieldEnd;
    }
}
