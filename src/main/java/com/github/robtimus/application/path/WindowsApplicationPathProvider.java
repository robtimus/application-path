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

    WindowsApplicationPathProvider() {
    }

    WindowsApplicationPathProvider(Function<String, Path> pathFactory) {
        super(pathFactory);
    }

    @Override
    public Path userData(String application, UserDataOption... options) {
        Path appData = containsLocal(options)
                ? localAppData()
                : roamingAppData();
        return appData.resolve(application);
    }

    private Path localAppData() {
        String localAppDataPath = System.getenv("LOCALAPPDATA"); //$NON-NLS-1$
        if (localAppDataPath != null) {
            Path localAppData = getPath(localAppDataPath);
            if (Files.exists(localAppData)) {
                return localAppData;
            }
        }

        Path userHome = userHome();
        Path localAppData = userHome.resolve("AppData/Local"); //$NON-NLS-1$
        if (Files.exists(localAppData)) {
            return localAppData;
        }

        // cannot find the local app data; fall back to roaming
        return appData(userHome);
    }

    private Path roamingAppData() {
        String appDataPath = System.getenv("APPDATA"); //$NON-NLS-1$
        if (appDataPath != null) {
            Path appData = getPath(appDataPath);
            if (Files.exists(appData)) {
                return appData;
            }
        }

        Path userHome = userHome();
        return appData(userHome);
    }

    private Path appData(Path userHome) {
        Path appData = userHome.resolve("AppData/Roaming"); //$NON-NLS-1$
        if (Files.exists(appData)) {
            return appData;
        }

        return userHome.resolve("Application Data"); //$NON-NLS-1$
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
