/*
 * MacApplicationPathProviderTest.java
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
import java.nio.file.Paths;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junitpioneer.jupiter.SetSystemProperty;

@SuppressWarnings("nls")
class MacApplicationPathProviderTest extends ApplicationPathProviderTestBase<MacApplicationPathProvider> {

    MacApplicationPathProviderTest() {
        super(MacApplicationPathProvider::new);
    }

    @Nested
    @DisplayName("userData")
    @SetSystemProperty(key = "user.home", value = "/Users/test")
    class UserData {

        @Test
        @DisplayName("Library/Application Support exists")
        void testExistingLibraryApplicationSupport() {
            createdirectories("/Users/test/Library/Application Support");

            assertUserData("/Users/test/Library/Application Support/app", "app");
        }

        @Test
        @DisplayName("Library/Application Support does not exists")
        void testNonExistingLibraryApplicationSupport() {
            assertUserData("/Users/test/.app", "app");
        }
    }

    @Test
    @DisplayName("with actual file system")
    @EnabledOnOs(OS.MAC)
    void testWithActualFileSystem() {
        MacApplicationPathProvider provider = new MacApplicationPathProvider();
        assertEquals(Paths.get(System.getProperty("user.home")).resolve("Library/Application Support/app"), provider.userData("app"));
    }
}
