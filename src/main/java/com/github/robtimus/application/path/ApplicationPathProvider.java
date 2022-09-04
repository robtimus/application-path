/*
 * ApplicationPathProvider.java
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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.function.Function;

abstract class ApplicationPathProvider {

    private final Function<String, Path> pathFactory;
    private final Path userHome;

    ApplicationPathProvider() {
        this(Paths::get);
    }

    ApplicationPathProvider(Function<String, Path> pathFactory) {
        this.pathFactory = pathFactory;
        this.userHome = pathFactory.apply(System.getProperty("user.home")); //$NON-NLS-1$
    }

    abstract Path userData(String folderName, UserDataOption... options);

    final Path getPath(String path) {
        return pathFactory.apply(path);
    }

    final Path userHome() {
        return userHome;
    }

    static ApplicationPathProvider current() {
        String osName = System.getProperty("os.name"); //$NON-NLS-1$
        if (osName == null) {
            return new GenericApplicationPathProvider();
        }

        osName = osName.toLowerCase(Locale.ENGLISH);
        if (osName.contains("mac")) { //$NON-NLS-1$
            return new MacApplicationPathProvider();
        }
        if (osName.contains("win")) { //$NON-NLS-1$
            return new WindowsApplicationPathProvider();
        }
        return new GenericApplicationPathProvider();
    }
}
