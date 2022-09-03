# application-path

Provides utility classes for querying application specific paths.

For user data or configuration, platform independent applications tend to use `<user.home>/.<application>` regardless of the platform. While this works fine on Linux, on Windows (and to a lesser extend macOS), this is messy. This library makes it easy to use a single API and still use platform specific paths. As a result, on Windows the user's own `AppData\Roaming` or `AppData\Local` folder will be used.

Currently, only the path to store user data or configuration is supported. This will use the following paths:

* `$HOME/.<application>` or `$HOME/.<company>/<application>` for Linux and Unix
* `$HOME/Library/Application Support/<application>` or `$HOME/Library/Application Support/<company>/<application>` for macOS
* `%HOME%\AppData\Roaming\<application>`, `%HOME%\AppData\Roaming\<company>\<application>`, `%HOME%\AppData\Local\<application>` or `%HOME%\AppData\Local\<company>\<application>` for Windows

To retrieve the path for an application's user data or configuration, simply call `ApplicationPath.userData`:

```
Path userData1 = ApplicationPath.userData("My App");
// userData1 is $HOME/.My App on Linux, %HOME%\AppData\Roaming\My App on Windows
Path userData2 = ApplicationPath.userData("My Company", "My App");
// userData2 is $HOME/.My Company/My App on Linux, %HOME%\AppData\Roaming\My Company\My App on Windows
```

To use `%HOME%\AppData\Local` on Windows, add the `LOCAL` option:

```
Path userData1 = ApplicationPath.userData("My App", UserDataOption.LOCAL);
// userData1 is $HOME/.My App on Linux, %HOME%\AppData\Local\My App on Windows
Path userData2 = ApplicationPath.userData("My Company", "My App", UserDataOption.LOCAL);
// userData2 is $HOME/.My Company/My App on Linux, %HOME%\AppData\Local\My Company\My App on Windows
```

Note that the `userData` methods will not create any files or directories. It is the responsibility of the caller to create the path as necessary. This can usually be done using `Files.createDirectories`.
