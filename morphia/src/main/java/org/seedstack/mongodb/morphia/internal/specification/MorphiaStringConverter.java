/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.mongodb.morphia.internal.specification;

import org.mongodb.morphia.query.CriteriaContainer;
import org.seedstack.business.specification.StringSpecification;
import org.seedstack.business.spi.specification.SpecificationConverter;
import org.seedstack.business.spi.specification.SpecificationTranslator;

import java.util.regex.Pattern;

public abstract class MorphiaStringConverter<T, S extends StringSpecification> implements SpecificationConverter<S, MorphiaQueryContext<T>, CriteriaContainer> {
    @Override
    public CriteriaContainer convert(S specification, MorphiaQueryContext<T> context, SpecificationTranslator<MorphiaQueryContext<T>, CriteriaContainer> translator) {
        if (specification.getExpectedString() == null) {
            return context.pickFieldEnd().doesNotExist();
        } else {
            StringSpecification.Options options = specification.getOptions();
            if (hasNoOption(options) && !isRegex()) {
                // We avoid using equal() because Morphia optimizes it without operator ("someAttr": "someVal")
                // Thus generating an invalid query when trying to negate it ("$not": "someVal")
                return context.pickFieldEnd().not().notEqual(specification.getExpectedString());
            } else {
                return context.pickFieldEnd().equal(buildRegex(options, specification.getExpectedString()));
            }

        }
    }

    private Pattern buildRegex(StringSpecification.Options options, String expectedString) {
        StringBuilder sb = new StringBuilder();
        sb.append("^");
        if (options.isTrimmed() || options.isLeftTrimmed()) {
            sb.append("\\s*");
        }
        sb.append(buildRegexMatchingPart(expectedString));
        if (options.isTrimmed() || options.isRightTrimmed()) {
            sb.append("\\s*");
        }
        sb.append("$");
        return Pattern.compile(sb.toString(), options.isIgnoringCase() ? Pattern.CASE_INSENSITIVE : 0);
    }

    private boolean hasNoOption(StringSpecification.Options options) {
        return !options.isLeftTrimmed() && !options.isRightTrimmed() && !options.isTrimmed() && !options.isIgnoringCase();
    }

    protected abstract String buildRegexMatchingPart(String value);

    protected abstract boolean isRegex();
}
