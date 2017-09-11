/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.mongodb.morphia.internal.specification;

import org.mongodb.morphia.query.Criteria;
import org.seedstack.business.specification.EqualSpecification;
import org.seedstack.business.spi.specification.SpecificationConverter;
import org.seedstack.business.spi.specification.SpecificationTranslator;


public class MorphiaEqualConverter<T, V> implements SpecificationConverter<EqualSpecification<V>, MorphiaQueryContext<T>, Criteria> {
    @Override
    public Criteria convert(EqualSpecification<V> specification, MorphiaQueryContext<T> builder, SpecificationTranslator<MorphiaQueryContext<T>, Criteria> translator) {
//        if (specification.getExpectedValue() == null) {
//            return builder.pickFieldEnd().doesNotExist();
//        } else {
//            return builder.pickFieldEnd().equal(specification.getExpectedValue());
//        }
        return null;
    }
}
