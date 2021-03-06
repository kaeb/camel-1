/*
 * Copyright (C) Scott Cranton, Jakub Korab, and Christian Posta
 * https://github.com/CamelCookbook
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.camelcookbook.extend.typeconverter;

import org.apache.camel.Converter;

@Converter
public final class MyPersonTypeConverter {
    /**
     * Utility classes should not have a public constructor.
     */
    private MyPersonTypeConverter() {
    }

    /**
     * Converts a String in the format of "firstName|lastName" to a {@link MyPerson}.
     */
    @Converter
    public static MyPerson convertStringToMyPerson(String str) {
        final int index = str.indexOf("|");
        if (index > 0) {
            final MyPerson person = new MyPerson();

            person.setFirstName(str.substring(0, index));
            person.setLastName(str.substring(index + 1)); // skip the '|' at index

            return person;
        }

        throw new IllegalArgumentException("String must be in format of '<firstName>|<lastName>'");
    }
}
