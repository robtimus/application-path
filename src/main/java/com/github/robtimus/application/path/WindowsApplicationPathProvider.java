/*
 * WindowsApplicationPathProvider.java
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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

final class WindowsApplicationPathProvider extends ApplicationPathProvider {

    private final GenericApplicationPathProvider fallback;

    WindowsApplicationPathProvider() {
        fallback = new GenericApplicationPathProvider();
    }

    WindowsApplicationPathProvider(Function<String, Path> pathFactory) {
        super(pathFactory);
        fallback = new GenericApplicationPathProvider(pathFactory);
    }

    @Override
    public Path userData(String application, UserDataOption... options) {
        AppData appData = containsLocal(options)
                ? localAppData()
                : roamingAppData();

        return appData.exists
                ? appData.folder.resolve(application)
                : fallback.userData(application, options);
    }

    private AppData localAppData() {
        AppData localAppData = localAppDataOnly();
        if (localAppData.exists) {
            return localAppData;
        }

        // cannot find the local app data; fall back to roaming
        AppData roamingAppData = roamingAppData();
        if (roamingAppData.exists) {
            return roamingAppData;
        }

        return localAppData;
    }

    @SuppressWarnings("nls")
    private AppData localAppDataOnly() {
        return appData("LOCALAPPDATA", "AppData/Local", "Local Settings");
    }

    @SuppressWarnings("nls")
    private AppData roamingAppData() {
        return appData("APPDATA", "AppData/Roaming", "Application Data");
    }

    private AppData appData(String environmentVariable, String appDataPath, String legacyPath) {
        String environmentValue = System.getenv(environmentVariable);
        Path environmentPath = null;
        if (environmentValue != null) {
            environmentPath = getPath(environmentValue);
            if (Files.isDirectory(environmentPath)) {
                return AppData.of(environmentPath);
            }
        }

        Path appData = userHome().resolve(appDataPath);
        if (Files.isDirectory(appData)) {
            return AppData.of(appData);
        }

        Path legacy = userHome().resolve(legacyPath);
        if (Files.isDirectory(legacy)) {
            return AppData.of(legacy);
        }

        return AppData.NON_EXISTENT;
    }

    private boolean containsLocal(UserDataOption... options) {
        for (UserDataOption option : options) {
            if (option == UserDataOption.LOCAL) {
                return true;
            }
        }
        return false;
    }

    private static final class AppData {

        private static final AppData NON_EXISTENT = new AppData(null, false);

        private final Path folder;
        private final boolean exists;

        private AppData(Path folder, boolean exists) {
            this.folder = folder;
            this.exists = exists;
        }

        private static AppData of(Path folder) {
            return new AppData(folder, true);
        }
    }
}
