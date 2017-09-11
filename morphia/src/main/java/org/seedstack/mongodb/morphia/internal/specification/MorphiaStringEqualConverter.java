/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.mongodb.morphia.internal.specification;

import org.mongodb.morphia.query.CriteriaContainer;
import org.seedstack.business.specification.StringEqualSpecification;
import org.seedstack.business.specification.StringSpecification;
import org.seedstack.business.spi.specification.SpecificationConverter;
import org.seedstack.business.spi.specification.SpecificationTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MorphiaStringEqualConverter<T, V> implements SpecificationConverter<StringEqualSpecification, MorphiaQueryContext<T>, CriteriaContainer> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MorphiaStringEqualConverter.class);

    @Override
    public CriteriaContainer convert(StringEqualSpecification specification, MorphiaQueryContext<T> context, SpecificationTranslator<MorphiaQueryContext<T>, CriteriaContainer> translator) {
        if (specification.getExpectedString() == null) {
            return context.pickFieldEnd().doesNotExist();
        } else {
            StringSpecification.Options options = specification.getOptions();
            if (options.isLeftTrimmed() || options.isRightTrimmed() || options.isTrimmed()) {
                LOGGER.warn("Value trimming is not supported with Morphia");
            }
            if (options.isIgnoringCase()) {
                return context.pickFieldEnd().equalIgnoreCase(specification.getExpectedString());
            } else {
                return context.pickFieldEnd().equal(specification.getExpectedString());
            }
        }
    }
}
