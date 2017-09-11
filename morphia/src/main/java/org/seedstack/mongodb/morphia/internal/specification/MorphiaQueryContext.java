package org.seedstack.mongodb.morphia.internal.specification;

import org.mongodb.morphia.query.CriteriaContainer;
import org.mongodb.morphia.query.FieldEnd;
import org.mongodb.morphia.query.Query;

import static com.google.common.base.Preconditions.checkState;

public class MorphiaQueryContext<T> {
    private final Query<T> query;
    private FieldEnd<? extends CriteriaContainer> fieldEnd;

    public MorphiaQueryContext(Query<T> query) {
        this.query = query;
    }

    FieldEnd<? extends CriteriaContainer> pickFieldEnd() {
        checkState(this.fieldEnd != null, "No field has been set");
        FieldEnd<? extends CriteriaContainer> result = this.fieldEnd;
        this.fieldEnd = null;
        return result;
    }

    void setFieldEnd(String property) {
        checkState(this.fieldEnd == null, "A field is already set");
        this.fieldEnd = query.criteria(property);
    }

    Query<T> getQuery() {
        return query;
    }
}
