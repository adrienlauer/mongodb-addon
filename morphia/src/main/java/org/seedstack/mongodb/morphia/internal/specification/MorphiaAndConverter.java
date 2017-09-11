/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.mongodb.morphia.internal.specification;

import org.mongodb.morphia.query.CriteriaContainer;
import org.seedstack.business.specification.AndSpecification;
import org.seedstack.business.spi.specification.SpecificationConverter;
import org.seedstack.business.spi.specification.SpecificationTranslator;

import java.util.Arrays;

public class MorphiaAndConverter<T, V> implements SpecificationConverter<AndSpecification<V>, MorphiaQueryContext<T>, CriteriaContainer> {
    @Override
    public CriteriaContainer convert(AndSpecification<V> specification, MorphiaQueryContext<T> context, SpecificationTranslator<MorphiaQueryContext<T>, CriteriaContainer> translator) {
        return context.getQuery().and(
                Arrays.stream(specification.getSpecifications())
                        .map(spec -> translator.translate(spec, context))
                        .toArray(CriteriaContainer[]::new)
        );
    }
}
