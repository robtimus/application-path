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
        Path appData = null;
        if (containsLocal(options)) {
            appData = findLocalAppData();
        }
        if (appData == null) {
            // either LOCAL not given as option, or local app data could not be found
            appData = findRoamingAppData();
        }

        return appData != null
                ? appData.resolve(application)
                : fallback.userData(application, options);
    }

    @SuppressWarnings("nls")
    private Path findLocalAppData() {
        return findAppData("LOCALAPPDATA", "AppData/Local", "Local Settings");
    }

    @SuppressWarnings("nls")
    private Path findRoamingAppData() {
        return findAppData("APPDATA", "AppData/Roaming", "Application Data");
    }

    private Path findAppData(String environmentVariable, String appDataPath, String legacyPath) {
        String environmentValue = System.getenv(environmentVariable);
        Path environmentPath = null;
        if (environmentValue != null) {
            environmentPath = getPath(environmentValue);
            if (Files.isDirectory(environmentPath)) {
                return environmentPath;
            }
        }

        Path appData = userHome().resolve(appDataPath);
        if (Files.isDirectory(appData)) {
            return appData;
        }

        Path legacy = userHome().resolve(legacyPath);
        if (Files.isDirectory(legacy)) {
            return legacy;
        }

        return null;
    }

    private boolean containsLocal(UserDataOption... options) {
        for (UserDataOption option : options) {
            if (option == UserDataOption.LOCAL) {
                return true;
            }
        }
        return false;
    }
}
