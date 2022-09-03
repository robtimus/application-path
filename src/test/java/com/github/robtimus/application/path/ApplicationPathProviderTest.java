/*
 * ApplicationPathProviderTest.java
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

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.OS;
import org.junitpioneer.jupiter.ClearSystemProperty;
import org.junitpioneer.jupiter.SetSystemProperty;

class ApplicationPathProviderTest {

    @Nested
    @DisplayName("current")
    class Current {

        @Test
        @DisplayName("no os.name")
        @ClearSystemProperty(key = "os.name")
        void testNoOsName() {
            assertInstanceOf(GenericApplicationPathProvider.class, ApplicationPathProvider.current());
        }

        @Test
        @DisplayName("MAC os.name")
        @SetSystemProperty(key = "os.name", value = "macos")
        void testMacOsName() {
            assertInstanceOf(MacApplicationPathProvider.class, ApplicationPathProvider.current());
        }

        @Test
        @DisplayName("Windows os.name")
        @SetSystemProperty(key = "os.name", value = "Windows 10")
        void testWindowsOsName() {
            assertInstanceOf(WindowsApplicationPathProvider.class, ApplicationPathProvider.current());
        }

        @Test
        @DisplayName("other os.name")
        @SetSystemProperty(key = "os.name", value = "custom")
        void testOtherOsName() {
            assertInstanceOf(GenericApplicationPathProvider.class, ApplicationPathProvider.current());
        }

        @Test
        @DisplayName("default os.name")
        void testDefaultOsName() {
            assertInstanceOf(expectedImplementation(), ApplicationPathProvider.current());
        }

        private Class<? extends ApplicationPathProvider> expectedImplementation() {
            switch (OS.current()) {
                case MAC:
                    return MacApplicationPathProvider.class;
                case WINDOWS:
                    return WindowsApplicationPathProvider.class;
                default:
                    return GenericApplicationPathProvider.class;
            }
        }
    }
}
