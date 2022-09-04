/*
 * ApplicationPath.java
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

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

/**
 * A utility class that will retrieve application specific paths.
 *
 * @author Rob Spoor
 */
public final class ApplicationPath {

    private static final ApplicationPathProvider PROVIDER = ApplicationPathProvider.current();

    private ApplicationPath() {
    }

    /**
     * Retrieves the path where user data can be stored for an application. For instance:
     * <ul>
     * <li>{@code $HOME/.<application>} for Linux and Unix</li>
     * <li>{@code $HOME/Library/Application Support/<application>} for macOS</li>
     * <li>{@code %HOME%\AppData\Roaming\<application>} for Windows</li>
     * </ul>
     * <p>
     * If the given options contains {@link UserDataOption#LOCAL}, this method will attempt to return the local user data folder
     * ({@code %HOME%\AppData\Local\<application>}) on Windows instead of the roaming user data folder. For other platforms this option will be
     * ignored.
     * <p>
     * This method will not ensure that the path exists. It is the responsibility of the caller to create the path as necessary.
     *
     * @param application The application for which to retrieve the user data path.
     * @param options The options to use.
     * @return The path where user data can be stored for the given application
     * @throws NullPointerException If the given application is {@code null}.
     * @throws IllegalArgumentException If the given application is equal to {@code .}, {@code ..} or the empty string,
     *                                      or if the given application starts or ends with {@linkplain Character#isWhitespace(int) whitespace},
     *                                      or if the given application contains a (back)slash,
     *                                      or if the given application contains any {@linkplain Character#isISOControl(int) ISO control} characters.
     */
    public static Path userData(String application, UserDataOption... options) {
        validateApplication(application);

        return PROVIDER.userData(application, options);
    }

    /**
     * Retrieves the path where user data can be stored for an application created by a organization. For instance:
     * <ul>
     * <li>{@code $HOME/.<organization>/<application>} for Linux and Unix</li>
     * <li>{@code $HOME/Library/Application Support/<organization>/<application>} for macOS</li>
     * <li>{@code %HOME%\AppData\Roaming\<organization>\<application>} for Windows</li>
     * </ul>
     * <p>
     * If the given options contains {@link UserDataOption#LOCAL}, this method will attempt to return the local user data folder
     * ({@code %HOME%\AppData\Local\<organization>\<application>}) on Windows instead of the roaming user data folder. For other platforms this option
     * will be ignored.
     * <p>
     * This method will not ensure that the path or its parent folder exists. It is the responsibility of the caller to create the path as necessary.
     *
     * @param organization The organization that created the application.
     * @param application The application for which to retrieve the user data path.
     * @param options The options to use.
     * @return The path where user data can be stored for the given application created by the given organization.
     * @throws NullPointerException If the given organization or application is {@code null}.
     * @throws IllegalArgumentException If the given organization or application is equal to {@code .}, {@code ..} or the empty string,
     *                                      or if the given organization or application starts or ends with
     *                                      {@linkplain Character#isWhitespace(int) whitespace},
     *                                      or if the given organization or application contains a (back)slash,
     *                                      or if the given organization or application contains any
     *                                      {@linkplain Character#isISOControl(int) ISO control} characters.
     */
    public static Path userData(String organization, String application, UserDataOption... options) {
        validateOrganization(organization);
        validateApplication(application);

        return PROVIDER.userData(organization, options).resolve(application);
    }

    private static void validateApplication(String application) {
        validateFolderName(application, "application"); //$NON-NLS-1$
    }

    private static void validateOrganization(String organization) {
        validateFolderName(organization, "organization"); //$NON-NLS-1$
    }

    private static void validateFolderName(String folderName, String folderType) {
        Objects.requireNonNull(folderName);

        if (".".equals(folderName) || "..".equals(folderName)) { //$NON-NLS-1$ //$NON-NLS-2$
            throw new IllegalArgumentException(Messages.ApplicationPath.reservedFolderName.get(folderType, folderName));
        }
        if (folderName.isEmpty()) {
            throw new IllegalArgumentException(Messages.ApplicationPath.emptyFolderName.get(folderType));
        }
        if (Character.isWhitespace(folderName.charAt(0)) || Character.isWhitespace(folderName.charAt(folderName.length() - 1))) {
            throw new IllegalArgumentException(Messages.ApplicationPath.folderNameStartsOrEndsWithBlank.get(folderType, folderName));
        }
        if (folderName.codePoints().anyMatch(ApplicationPath::isForbidden)) {
            throw new IllegalArgumentException(Messages.ApplicationPath.folderNameContainsForbiddenCharacters.get(folderType, folderName));
        }
    }

    private static boolean isForbidden(int codePoint) {
        return codePoint == File.separatorChar || codePoint == '/' || Character.isISOControl(codePoint);
    }
}
