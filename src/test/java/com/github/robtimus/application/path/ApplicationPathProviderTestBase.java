/*
 * ApplicationPathProviderTestBase.java
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import com.github.robtimus.filesystems.memory.MemoryFileSystemProvider;

@SuppressWarnings("nls")
abstract class ApplicationPathProviderTestBase<P extends ApplicationPathProvider> {

    private final Function<Function<String, Path>, P> providerFactory;

    private P provider;

    ApplicationPathProviderTestBase(Function<Function<String, Path>, P> providerFactory) {
        this.providerFactory = providerFactory;
    }

    @BeforeEach
    void initProvider() {
        MemoryFileSystemProvider.clear();

        provider = providerFactory.apply(this::getPath);
    }

    Path getPath(String path) {
        String encodedPath = assertDoesNotThrow(() -> URLEncoder.encode(path, "UTF-8").replace("+", "%20"));
        URI uri = URI.create("memory:" + encodedPath);
        return Paths.get(uri);
    }

    Path createdirectories(String path) {
        Path result = getPath(path);
        assertDoesNotThrow(() -> Files.createDirectories(result));
        return result;
    }

    void assertUserData(String expected, String application, UserDataOption... options) {
        assertEquals(getPath(expected), provider.userData(application, options));
    }
}
