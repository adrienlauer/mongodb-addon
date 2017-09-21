/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.mongodb.morphia.internal.specification;

import org.mongodb.morphia.query.CriteriaContainer;
import org.mongodb.morphia.query.FieldEnd;
import org.mongodb.morphia.query.Query;

import static com.google.common.base.Preconditions.checkState;

public class MorphiaTranslationContext<T> {
    private final Query<T> query;
    private FieldEnd<? extends CriteriaContainer> fieldEnd;
    private boolean not;

    public MorphiaTranslationContext(Query<T> query) {
        this.query = query;
    }

    public FieldEnd<? extends CriteriaContainer> pickFieldEnd() {
        checkState(this.fieldEnd != null, "No field has been set");
        FieldEnd<? extends CriteriaContainer> result = this.fieldEnd;
        this.fieldEnd = null;
        return result;
    }

    public void setFieldEnd(String property) {
        checkState(this.fieldEnd == null, "A field is already set");
        if (not) {
            this.fieldEnd = query.criteria(property).not();
        } else {
            this.fieldEnd = query.criteria(property);
        }
        this.not = false;
    }

    public void not() {
        not = !not;
    }

    public Query<T> getQuery() {
        return query;
    }
}
