/*
 * WindowsApplicationPathProviderTest.java
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
import org.junitpioneer.jupiter.ClearEnvironmentVariable;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.junitpioneer.jupiter.SetSystemProperty;

@SuppressWarnings("nls")
class WindowsApplicationPathProviderTest extends ApplicationPathProviderTestBase<WindowsApplicationPathProvider> {

    WindowsApplicationPathProviderTest() {
        super(WindowsApplicationPathProvider::new);
    }

    @Nested
    @DisplayName("userData")
    @SetSystemProperty(key = "user.home", value = "/Users/test")
    class UserData {

        @Nested
        @DisplayName("non-local")
        class NonLocal {

            @Test
            @DisplayName("APPDATA is set and exists")
            @SetEnvironmentVariable(key = "APPDATA", value = "/Users/test/Custom-AppData/Roaming")
            void testExistingAppData() {
                createdirectories("/Users/test/Custom-AppData/Roaming");

                assertUserData("/Users/test/Custom-AppData/Roaming/app", "app", (UserDataOption) null);
            }

            @Nested
            @DisplayName("APPDATA is set but does not exist")
            @SetEnvironmentVariable(key = "APPDATA", value = "/non-existent")
            class NonExistingAppData {

                @Test
                @DisplayName("AppData/Roaming exists")
                void testExistingRoamingAppData() {
                    createdirectories("/Users/test/AppData/Roaming");

                    assertUserData("/Users/test/AppData/Roaming/app", "app", (UserDataOption) null);
                }

                @Test
                @DisplayName("AppData/Roaming does not exists")
                void testNonExistingRoamingAppData() {

                    assertUserData("/Users/test/Application Data/app", "app", (UserDataOption) null);
                }
            }

            @Nested
            @DisplayName("APPDATA is not set")
            @ClearEnvironmentVariable(key = "APPDATA")
            class NoAppData {

                @Test
                @DisplayName("AppData/Roaming exists")
                void testExistingRoamingAppData() {
                    createdirectories("/Users/test/AppData/Roaming");

                    assertUserData("/Users/test/AppData/Roaming/app", "app", (UserDataOption) null);
                }

                @Test
                @DisplayName("AppData/Roaming does not exists")
                void testNonExistingRoamingAppData() {

                    assertUserData("/Users/test/Application Data/app", "app", (UserDataOption) null);
                }
            }
        }

        @Nested
        @DisplayName("local")
        class Local {

            @Test
            @DisplayName("LOCALAPPDATA is set and exists")
            @SetEnvironmentVariable(key = "LOCALAPPDATA", value = "/Users/test/Custom-AppData/Local")
            void testExistingLocalAppData() {
                createdirectories("/Users/test/Custom-AppData/Local");

                assertUserData("/Users/test/Custom-AppData/Local/app", "app", UserDataOption.LOCAL);
            }

            @Nested
            @DisplayName("LOCALAPPDATA is set but does not exist")
            @SetEnvironmentVariable(key = "LOCALAPPDATA", value = "/non-existent")
            class NonExistingLocalAppData {

                @Test
                @DisplayName("AppData/Local exists")
                void testExistingLocalAppDataDir() {
                    createdirectories("/Users/test/AppData/Local");

                    assertUserData("/Users/test/AppData/Local/app", "app", UserDataOption.LOCAL);
                }

                @Nested
                @DisplayName("AppData/Local does not exist")
                class NonExistingLocalAppDataDir {

                    @Test
                    @DisplayName("LOCALAPPDATA is set and exists")
                    @SetEnvironmentVariable(key = "LOCALAPPDATA", value = "/Users/test/Custom-AppData/Local")
                    void testExistingAppData() {
                        createdirectories("/Users/test/Custom-AppData/Local");

                        assertUserData("/Users/test/Custom-AppData/Local/app", "app", UserDataOption.LOCAL);
                    }

                    @Nested
                    @DisplayName("APPDATA is set but does not exist")
                    @SetEnvironmentVariable(key = "APPDATA", value = "/non-existent")
                    class NonExistingAppData {

                        @Test
                        @DisplayName("AppData/Roaming exists")
                        void testExistingRoamingAppData() {
                            createdirectories("/Users/test/AppData/Roaming");

                            assertUserData("/Users/test/AppData/Roaming/app", "app", UserDataOption.LOCAL);
                        }

                        @Test
                        @DisplayName("AppData/Roaming does not exists")
                        void testNonExistingRoamingAppData() {

                            assertUserData("/Users/test/Application Data/app", "app", UserDataOption.LOCAL);
                        }
                    }

                    @Nested
                    @DisplayName("APPDATA is not set")
                    @ClearEnvironmentVariable(key = "APPDATA")
                    class NoAppData {

                        @Test
                        @DisplayName("AppData/Roaming exists")
                        void testExistingRoamingAppData() {
                            createdirectories("/Users/test/AppData/Roaming");

                            assertUserData("/Users/test/AppData/Roaming/app", "app", UserDataOption.LOCAL);
                        }

                        @Test
                        @DisplayName("AppData/Roaming does not exists")
                        void testNonExistingRoamingAppData() {

                            assertUserData("/Users/test/Application Data/app", "app", UserDataOption.LOCAL);
                        }
                    }
                }
            }

            @Nested
            @DisplayName("LOCALAPPDATA is not set")
            @ClearEnvironmentVariable(key = "LOCALAPPDATA")
            class NoLocalAppData {

                @Test
                @DisplayName("AppData/Local exists")
                void testExistingLocalAppDataDir() {
                    createdirectories("/Users/test/AppData/Local");

                    assertUserData("/Users/test/AppData/Local/app", "app", UserDataOption.LOCAL);
                }

                @Nested
                @DisplayName("AppData/Local does not exist")
                class NonExistingLocalAppDataDir {

                    @Test
                    @DisplayName("LOCALAPPDATA is set and exists")
                    @SetEnvironmentVariable(key = "LOCALAPPDATA", value = "/Users/test/Custom-AppData/Local")
                    void testExistingAppData() {
                        createdirectories("/Users/test/Custom-AppData/Local");

                        assertUserData("/Users/test/Custom-AppData/Local/app", "app", UserDataOption.LOCAL);
                    }

                    @Nested
                    @DisplayName("APPDATA is set but does not exist")
                    @SetEnvironmentVariable(key = "APPDATA", value = "/non-existent")
                    class NonExistingAppData {

                        @Test
                        @DisplayName("AppData/Roaming exists")
                        void testExistingRoamingAppData() {
                            createdirectories("/Users/test/AppData/Roaming");

                            assertUserData("/Users/test/AppData/Roaming/app", "app", UserDataOption.LOCAL);
                        }

                        @Test
                        @DisplayName("AppData/Roaming does not exists")
                        void testNonExistingRoamingAppData() {

                            assertUserData("/Users/test/Application Data/app", "app", UserDataOption.LOCAL);
                        }
                    }

                    @Nested
                    @DisplayName("APPDATA is not set")
                    @ClearEnvironmentVariable(key = "APPDATA")
                    class NoAppData {

                        @Test
                        @DisplayName("AppData/Roaming exists")
                        void testExistingRoamingAppData() {
                            createdirectories("/Users/test/AppData/Roaming");

                            assertUserData("/Users/test/AppData/Roaming/app", "app", UserDataOption.LOCAL);
                        }

                        @Test
                        @DisplayName("AppData/Roaming does not exists")
                        void testNonExistingRoamingAppData() {

                            assertUserData("/Users/test/Application Data/app", "app", UserDataOption.LOCAL);
                        }
                    }
                }
            }
        }
    }

    @Test
    @DisplayName("test non-mocked")
    @EnabledOnOs(OS.WINDOWS)
    void testNonMocked() {
        WindowsApplicationPathProvider provider = new WindowsApplicationPathProvider();
        assertEquals(Paths.get(System.getProperty("user.home")).resolve("AppData/Roaming/App"), provider.userData("app"));
    }
}
