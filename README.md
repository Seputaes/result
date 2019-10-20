# std::result::Result - For Java | [![Rust Version](https://img.shields.io/badge/rust-1.38.0-b7410e)](https://doc.rust-lang.org/1.38.0/std/result/)

[![Build Status](https://travis-ci.org/Seputaes/result.svg?branch=master)](https://travis-ci.org/Seputaes/result)
[![MIT license](https://img.shields.io/badge/License-MIT-blue.svg)](https://lbesson.mit-license.org/)
[![made-with-java](https://img.shields.io/badge/Made%20with-Java-1f425f.svg)](https://www.python.org/)
[![GitHub issues](https://img.shields.io/github/issues/Seputaes/result.svg)](https://GitHub.com/seputaes/result/issues/)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)

A Java implementation of rustlang's 
[std::result::Result](https://doc.rust-lang.org/std/result/enum.Result.html) 
enum type.

To quote Rust's 
[documentation](https://doc.rust-lang.org/rust-by-example/error/result.html):

> `Result<T, E>` is the type used for returning and propagating errors. It is an 
> enum with the variants, `Ok(T)`, representing success and containing a value, 
> and `Err(E)`, representing error and containing an error value.
>
> Result is a richer version of the 
> [Option](https://doc.rust-lang.org/std/option/enum.Option.html) 
> type that describes possible error instead of possible *absence*.
>
> That is, `Result<T, E>` could have one of two outcomes:
> - `Ok<T>`: An element T was found
> - `Err<E>`: An error was found with element E
>    
> By convention, the expected outcome is `Ok` while the unexpected outcome 
> is `Err`.

In Java, this can be useful as a means of propagating errors back up the call
layers--for example, to display an error message to a user--without needing
to throw an exception. While similar things can be accomplished via exception
handling, Result provides a cleaner and functional-interface method of 
**doing something** with the error value as opposed to just throwing it 
verbatim.

An example of how this might look in practice might might
be along the lines of:

```java
public Result<Config, String> loadConfig(final File configFile) {
    if (configFile.isDirectory()) {
        return Err.of("The path to the config cannot be a directory.");
    }
    // ...
    // other validation

    final String fileContents = loadFileContents(configFile);
    return parseConfigToModel(fileContents);
}

private Result<Config, String> parseConfigToModel(final String fileContents) {
    try {
        final Config config = parseConfig(fileContents);
        return Ok.of(config);
    } catch (final ParseException e) {
        return Err.of("Failed to parse the config file.");
    }
}
```

The caller, who is responsible for either proceeding with the loaded config, or
displaying a message to the user might look like:


```java
final Result<Config, String> configResult = configLoader.loadConfig(file);
final Config config = configLoader.loadConfig(file).unwrapOrElse(errorMsg -> {
    throw new RuntimeException(errorMsg);
});
```

## Development

[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)
[![GitHub issues](https://img.shields.io/github/issues/Seputaes/result.svg)](https://GitHub.com/seputaes/result/issues/)
[![Rust Version](https://img.shields.io/badge/rust-1.38.0-b7410e)](https://doc.rust-lang.org/1.38.0/std/result/)

This library attempts to maintain as much feature parity with Rust's 
implementation as possible. As is obvious, due to the significant differences
in style between the two languages, some things don't make sense to implement
in Java, or should be implemented differently.

Currently, this library tracks **v1.38.0** of Rust's implementation.

Certain features of `Result` are nightly-only in Rust's implementation. These
are marked accordingly with an `Experiemntal` annotation in this library, and
are subject to breaking changes or removal in future versions, depending on
changes to Rust's `std::result` APIs.

Pull requests are welcome!