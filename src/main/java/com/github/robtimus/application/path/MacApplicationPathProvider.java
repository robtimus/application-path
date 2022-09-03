/*
 * MacApplicationPathProvider.java
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
import java.util.function.Function;

final class MacApplicationPathProvider extends ApplicationPathProvider {

    MacApplicationPathProvider() {
    }

    MacApplicationPathProvider(Function<String, Path> pathFactory) {
        super(pathFactory);
    }

    @Override
    public Path userData(String application, UserDataOption... options) {
        return userHome().resolve("Library/Application Support").resolve(application); //$NON-NLS-1$
    }
}
