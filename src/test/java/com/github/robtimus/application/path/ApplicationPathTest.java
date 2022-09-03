/*
 * ApplicationPathTest.java
 * Copyright 2022 Rob Spoor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.robtimus.application.path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.File;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@SuppressWarnings("nls")
class ApplicationPathTest {

    private ApplicationPathProvider current = ApplicationPathProvider.current();

    @Nested
    @DisplayName("userData(String, UserDataOption...)")
    class UserDataWithoutCompany {

        @Test
        @DisplayName("valid application name")
        void testValidApplicationName() {
            assertEquals(current.userData("app"), ApplicationPath.userData("app"));
            assertEquals(current.userData("app", UserDataOption.LOCAL), ApplicationPath.userData("app", UserDataOption.LOCAL));
        }

        @Nested
        @DisplayName("invalid application name")
        class InvalidApplicationName {

            @Test
            @DisplayName("null value")
            void testNullValue() {
                assertThrows(NullPointerException.class, () -> ApplicationPath.userData(null));
                assertThrows(NullPointerException.class, () -> ApplicationPath.userData(null, UserDataOption.LOCAL));
            }

            @ParameterizedTest(name = "{0}")
            @ValueSource(strings = { ".", ".." })
            @DisplayName("preserved value")
            void testPreservedValue(String application) {
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ApplicationPath.userData(application));
                assertEquals(Messages.ApplicationPath.reservedFolderName.get("application", application), exception.getMessage());

                exception = assertThrows(IllegalArgumentException.class, () -> ApplicationPath.userData(application, UserDataOption.LOCAL));
                assertEquals(Messages.ApplicationPath.reservedFolderName.get("application", application), exception.getMessage());
            }

            @Test
            @DisplayName("empty value")
            void testEmptyValue() {
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ApplicationPath.userData(""));
                assertEquals(Messages.ApplicationPath.emptyFolderName.get("application"), exception.getMessage());

                exception = assertThrows(IllegalArgumentException.class, () -> ApplicationPath.userData("", UserDataOption.LOCAL));
                assertEquals(Messages.ApplicationPath.emptyFolderName.get("application"), exception.getMessage());
            }

            @Test
            @DisplayName("starts with whitespace")
            void testStartsWithWhiteSpace() {
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ApplicationPath.userData(" app"));
                assertEquals(Messages.ApplicationPath.folderNameStartsOrEndsWithBlank.get("application", " app"), exception.getMessage());

                exception = assertThrows(IllegalArgumentException.class, () -> ApplicationPath.userData(" app", UserDataOption.LOCAL));
                assertEquals(Messages.ApplicationPath.folderNameStartsOrEndsWithBlank.get("application", " app"), exception.getMessage());
            }

            @Test
            @DisplayName("ends with whitespace")
            void testEndsWithWhiteSpace() {
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ApplicationPath.userData("app "));
                assertEquals(Messages.ApplicationPath.folderNameStartsOrEndsWithBlank.get("application", "app "), exception.getMessage());

                exception = assertThrows(IllegalArgumentException.class, () -> ApplicationPath.userData("app ", UserDataOption.LOCAL));
                assertEquals(Messages.ApplicationPath.folderNameStartsOrEndsWithBlank.get("application", "app "), exception.getMessage());
            }

            @ParameterizedTest(name = "{0}")
            @ValueSource(strings = { "app/path", "app\0path", "app\rpath", "app\npath", "app\tpath" })
            @DisplayName("forbidden character")
            void testForbiddenCharacter(String application) {
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ApplicationPath.userData(application));
                assertEquals(Messages.ApplicationPath.folderNameContainsForbiddenCharacters.get("application", application), exception.getMessage());

                exception = assertThrows(IllegalArgumentException.class, () -> ApplicationPath.userData(application, UserDataOption.LOCAL));
                assertEquals(Messages.ApplicationPath.folderNameContainsForbiddenCharacters.get("application", application), exception.getMessage());
            }

            @Test
            @DisplayName("folder separator")
            void testFolderSeparator() {
                String application = "app" + File.separator + "path";

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ApplicationPath.userData(application));
                assertEquals(Messages.ApplicationPath.folderNameContainsForbiddenCharacters.get("application", application), exception.getMessage());

                exception = assertThrows(IllegalArgumentException.class, () -> ApplicationPath.userData(application, UserDataOption.LOCAL));
                assertEquals(Messages.ApplicationPath.folderNameContainsForbiddenCharacters.get("application", application), exception.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("userData(String, String, UserDataOption...)")
    class UserDataWithCompany {

        @Test
        @DisplayName("valid application name")
        void testValidApplicationName() {
            assertEquals(current.userData("comp").resolve("app"), ApplicationPath.userData("comp", "app"));
            assertEquals(current.userData("comp", UserDataOption.LOCAL).resolve("app"),
                    ApplicationPath.userData("comp", "app", UserDataOption.LOCAL));
        }

        @Nested
        @DisplayName("invalid company name")
        class InvalidCompanyName {

            @Test
            @DisplayName("null value")
            void testNullValue() {
                assertThrows(NullPointerException.class, () -> ApplicationPath.userData(null, "app"));
                assertThrows(NullPointerException.class, () -> ApplicationPath.userData(null, "app", UserDataOption.LOCAL));
            }

            @ParameterizedTest(name = "{0}")
            @ValueSource(strings = { ".", ".." })
            @DisplayName("preserved value")
            void testPreservedValue(String company) {
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ApplicationPath.userData(company, "app"));
                assertEquals(Messages.ApplicationPath.reservedFolderName.get("company", company), exception.getMessage());

                exception = assertThrows(IllegalArgumentException.class, () -> ApplicationPath.userData(company, "app", UserDataOption.LOCAL));
                assertEquals(Messages.ApplicationPath.reservedFolderName.get("company", company), exception.getMessage());
            }

            @Test
            @DisplayName("empty value")
            void testEmptyValue() {
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ApplicationPath.userData("", "app"));
                assertEquals(Messages.ApplicationPath.emptyFolderName.get("company"), exception.getMessage());

                exception = assertThrows(IllegalArgumentException.class, () -> ApplicationPath.userData("", "app", UserDataOption.LOCAL));
                assertEquals(Messages.ApplicationPath.emptyFolderName.get("company"), exception.getMessage());
            }

            @Test
            @DisplayName("starts with whitespace")
            void testStartsWithWhiteSpace() {
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ApplicationPath.userData(" comp", "app"));
                assertEquals(Messages.ApplicationPath.folderNameStartsOrEndsWithBlank.get("company", " comp"), exception.getMessage());

                exception = assertThrows(IllegalArgumentException.class, () -> ApplicationPath.userData(" comp", "app", UserDataOption.LOCAL));
                assertEquals(Messages.ApplicationPath.folderNameStartsOrEndsWithBlank.get("company", " comp"), exception.getMessage());
            }

            @Test
            @DisplayName("ends with whitespace")
            void testEndsWithWhiteSpace() {
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ApplicationPath.userData("comp ", "app"));
                assertEquals(Messages.ApplicationPath.folderNameStartsOrEndsWithBlank.get("company", "comp "), exception.getMessage());

                exception = assertThrows(IllegalArgumentException.class, () -> ApplicationPath.userData("comp ", "app", UserDataOption.LOCAL));
                assertEquals(Messages.ApplicationPath.folderNameStartsOrEndsWithBlank.get("company", "comp "), exception.getMessage());
            }

            @ParameterizedTest(name = "{0}")
            @ValueSource(strings = { "comp/path", "comp\0path", "comp\rpath", "comp\npath", "comp\tpath" })
            @DisplayName("forbidden character")
            void testForbiddenCharacter(String company) {
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ApplicationPath.userData(company, "app"));
                assertEquals(Messages.ApplicationPath.folderNameContainsForbiddenCharacters.get("company", company), exception.getMessage());

                exception = assertThrows(IllegalArgumentException.class, () -> ApplicationPath.userData(company, "app", UserDataOption.LOCAL));
                assertEquals(Messages.ApplicationPath.folderNameContainsForbiddenCharacters.get("company", company), exception.getMessage());
            }

            @Test
            @DisplayName("folder separator")
            void testFolderSeparator() {
                String company = "comp" + File.separator + "path";

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ApplicationPath.userData(company, "app"));
                assertEquals(Messages.ApplicationPath.folderNameContainsForbiddenCharacters.get("company", company), exception.getMessage());

                exception = assertThrows(IllegalArgumentException.class, () -> ApplicationPath.userData(company, "app", UserDataOption.LOCAL));
                assertEquals(Messages.ApplicationPath.folderNameContainsForbiddenCharacters.get("company", company), exception.getMessage());
            }
        }

        @Nested
        @DisplayName("invalid application name")
        class InvalidApplicationName {

            @Test
            @DisplayName("null value")
            void testNullValue() {
                assertThrows(NullPointerException.class, () -> ApplicationPath.userData("comp", (String) null));
                assertThrows(NullPointerException.class, () -> ApplicationPath.userData("comp", (String) null, UserDataOption.LOCAL));
            }

            @ParameterizedTest(name = "{0}")
            @ValueSource(strings = { ".", ".." })
            @DisplayName("preserved value")
            void testPreservedValue(String application) {
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                        () -> ApplicationPath.userData("comp", application));
                assertEquals(Messages.ApplicationPath.reservedFolderName.get("application", application), exception.getMessage());

                exception = assertThrows(IllegalArgumentException.class, () -> ApplicationPath.userData("comp", application, UserDataOption.LOCAL));
                assertEquals(Messages.ApplicationPath.reservedFolderName.get("application", application), exception.getMessage());
            }

            @Test
            @DisplayName("empty value")
            void testEmptyValue() {
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ApplicationPath.userData("comp", ""));
                assertEquals(Messages.ApplicationPath.emptyFolderName.get("application"), exception.getMessage());

                exception = assertThrows(IllegalArgumentException.class, () -> ApplicationPath.userData("comp", "", UserDataOption.LOCAL));
                assertEquals(Messages.ApplicationPath.emptyFolderName.get("application"), exception.getMessage());
            }

            @Test
            @DisplayName("starts with whitespace")
            void testStartsWithWhiteSpace() {
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ApplicationPath.userData("comp", " app"));
                assertEquals(Messages.ApplicationPath.folderNameStartsOrEndsWithBlank.get("application", " app"), exception.getMessage());

                exception = assertThrows(IllegalArgumentException.class, () -> ApplicationPath.userData("comp", " app", UserDataOption.LOCAL));
                assertEquals(Messages.ApplicationPath.folderNameStartsOrEndsWithBlank.get("application", " app"), exception.getMessage());
            }

            @Test
            @DisplayName("ends with whitespace")
            void testEndsWithWhiteSpace() {
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ApplicationPath.userData("comp", "app "));
                assertEquals(Messages.ApplicationPath.folderNameStartsOrEndsWithBlank.get("application", "app "), exception.getMessage());

                exception = assertThrows(IllegalArgumentException.class, () -> ApplicationPath.userData("comp", "app ", UserDataOption.LOCAL));
                assertEquals(Messages.ApplicationPath.folderNameStartsOrEndsWithBlank.get("application", "app "), exception.getMessage());
            }

            @ParameterizedTest(name = "{0}")
            @ValueSource(strings = { "app/path", "app\0path", "app\rpath", "app\npath", "app\tpath" })
            @DisplayName("forbidden character")
            void testForbiddenCharacter(String application) {
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                        () -> ApplicationPath.userData("comp", application));
                assertEquals(Messages.ApplicationPath.folderNameContainsForbiddenCharacters.get("application", application), exception.getMessage());

                exception = assertThrows(IllegalArgumentException.class, () -> ApplicationPath.userData("comp", application, UserDataOption.LOCAL));
                assertEquals(Messages.ApplicationPath.folderNameContainsForbiddenCharacters.get("application", application), exception.getMessage());
            }

            @Test
            @DisplayName("folder separator")
            void testFolderSeparator() {
                String application = "app" + File.separator + "path";

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                        () -> ApplicationPath.userData("comp", application));
                assertEquals(Messages.ApplicationPath.folderNameContainsForbiddenCharacters.get("application", application), exception.getMessage());

                exception = assertThrows(IllegalArgumentException.class, () -> ApplicationPath.userData("comp", application, UserDataOption.LOCAL));
                assertEquals(Messages.ApplicationPath.folderNameContainsForbiddenCharacters.get("application", application), exception.getMessage());
            }
        }
    }
}
