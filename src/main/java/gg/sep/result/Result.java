/*
 * Copyright (c) 2019 sep.gg <seputaes@sep.gg>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package gg.sep.result;

import gg.sep.result.annotations.Experimental;

import java.util.Optional;
import java.util.function.Function;

/**
 * Result is a basic Java implementation of the Rust language's {@code std::result::Result} type.
 *
 * <p>Result represents either a success ({@link Ok}&lt;T&gt; or failure {@link Err}&lt;E&gt;.
 *
 * <p>It is a functional approach to situations where errors are expected and
 * recoverable, and an alternative to complicated try/catch blocks. It is
 * useful for when the type of the error returned is both known
 * and consistent, such as an Error message that should be displayed to the
 * end user or an exit code to pass to {@code System.exit()}.
 *
 * <p>{@link Result} will typically be the return type of a method where the
 * result will either be an {@link Ok}&lt;T&gt; or {@link Err}&lt;E&gt;.
 *
 * <p>A simple method returning a {@link Result} might be:
 *
 * <pre>{@code
 *     public Result<String, String> getFileContents(final File file) {
 *         if (file.isDirectory()) {
 *             return Err.of("The path specified must be a file.");
 *         } else if (!file.exists()) {
 *             return Err.of("That file path does not exist.");
 *         }
 *
 *         final String fileContents = readFileContents(file);
 *         return Ok.of(fileContents);
 *     }
 * }</pre>
 *
 * <p>And the usage of this might look like:
 *
 * <pre>{@code
 *     final Result<String, String> fileReadResult = getFileContents(file);
 *
 *     if (fileReadResult.isErr()) {
 *         displayMessageToUser(fileReadResult.unwrapErr()); // unwrapErr() returns value of "E", in this case String
 *         // retry getting the path from the user
 *     } else {
 *         final String contentString = fileReadResult.unwrap()
 *     }
 * }</pre>
 *
 * <p>This example simplifies the case where you would like to give the user
 * the opportunity to re-enter the file path, but don't want to have to
 * handle the many different types of {@code IOException} that could be
 * thrown in a typical use of this code. Your ultimate goal
 * is to just display a {@code String} to the user in the case of an error.
 *
 * <p><strong>Rust reference:</strong>
 * <ul>
 *     <li>API Reference: <a href="https://doc.rust-lang.org/std/result/enum.Result.html">
 *         https://doc.rust-lang.org/std/result/enum.Result.html</a></li>
 *     <li>Module Reference: <a href="https://doc.rust-lang.org/std/result/index.html">
 *         https://doc.rust-lang.org/std/result/index.html</a></li>
 * </ul>
 *
 * @param <T> Type of the success value of the result.
 * @param <E> Type of the error value of the result.
 */
public interface Result<T, E> extends Iterable<T> {

    /**
     * Returns {@code true} if the result is {@link Ok}.
     *
     * @return {@code true} if the result is {@link Ok}.
     */
    boolean isOk();

    /**
     * Returns {@code true} if the result is {@link Err}.
     *
     * @return {@code true} if the result is {@link Err}.
     */
    boolean isErr();

    /**
     * Returns {@code true} if the result is an {@link Ok} value containing the given value.
     *
     * @param x Value to compare against the {@link Ok} value.
     * @return {@code true} if the result is an {@link Ok} value containing the given value.
     *
     * @since 1.1
     */
    @Experimental
    boolean contains(T x);

    /**
     * Returns {@code true} if the result is an {@link Err} value containing the given value.
     *
     * @param f Value to compare against the {@link Err} value.
     * @return {@code true} if the result is an {@link Err} value containing the given value.
     *
     * @since 1.1
     */
    @Experimental
    boolean containsErr(E f);

    /**
     * Converts from {@code Result<T, E>} to {@link Optional}&lt;T&gt;.
     *
     * Converts the {@link Result} into an {@link Optional}, consuming the value ({@code T}) and discarding
     * the error, if any.
     *
     * @return {@link Optional}&lt;T&gt; if {@code this} is {@link Ok}, otherwise empty.
     */
    Optional<T> getOk();

    /**
     * Converts from {@code Result<T, E>} to {@link Optional}&lt;E&gt;.
     *
     * Converts the {@link Result} into an {@link Optional}, consuming the error ({@code E}) and discarding
     * the Ok value, if any.
     *
     * @return {@link Optional}&lt;E&gt; if {@code this} is {@link Err}, otherwise empty.
     */
    Optional<E> getErr();

    /**
     * Unwraps a result, yielding the content of an {@link Ok}.
     *
     * @return The content of an {@link Ok}.
     * @throws ResultException Thrown if the value is an {@link Err}, with a message provided by
     *                         the {@link Err}'s value.
     */
    T unwrap() throws ResultException;

    /**
     * Unwraps a result, yielding the content of an {@link Err}.
     *
     * @return The content of an {@link Err}.
     * @throws ResultException Thrown if the value is an {@link Ok}, with a message provided by the {@link Ok}'s value.
     */
    E unwrapErr() throws ResultException;

    /**
     * Unwraps a result, yielding the content of an {@link Ok}. Else, it returns {@code optb}.
     *
     * Arguments passed to {@code unwrap_or} are eagerly evaluated; if you are passing the result of a function call,
     * it is recommended to use {@link #unwrapOrElse(Function)}, which is lazily evaluated.
     *
     * @param optb The value to return if {@code this} is an {@link Err}.
     * @return The content of an {@link Ok}. Else, it returns {@code optb}.
     */
    T unwrapOr(T optb);

    /**
     * Unwraps a result, yielding the content of an {@link Ok}. If the value is an {@link Err},
     * then it calls {@code op} with its value.

     * @param op The function to call on the value of {@link Err} if {@code this} is an {@link Err}.
     * @return The content of an {@link Ok}. If the value is an {@link Err},
     *         then it returns the result of {@code} called on the value of that {@link Err}.
     */
    T unwrapOrElse(Function<E, T> op);

    /**
     * Unwraps a result, yielding the content of an {@link Ok}.
     *
     * @param msg Message to be passed to the {@link ResultException} if this is an {@link Err}.
     * @return The content of an {@link Ok}.
     * @throws ResultException Thrown if the value is an {@link Err}, with a message provided by the
     *                         value of {@code msg}, plus the value of the {@link Err}.
     */
    T expect(String msg) throws ResultException;

    /**
     * Unwraps a result, yielding the content of an {@link Err}.
     *
     * @param msg Message to be passed to the {@link ResultException} if this is an {@link Ok}.
     * @return The content of an {@link Err}.
     * @throws ResultException Thrown if the value is an {@link Ok}, with a message provided by the
     *                         value of {@code msg}, plus the value of the {@link Ok}.
     */
    E expectErr(String msg) throws ResultException;

    /**
     * Returns {@code res} if {@code this} is an {@link Ok},
     * otherwise returns the {@link Err} value of this result converted to {@code Result<U, E>}.
     *
     * Arguments passed to {@code and} are eagerly evaluated; if you are passing the result of a function call,
     * it is recommended to use {@link #andThen(Function)}, which is lazily evaluated.
     *
     * @param res The {@link Result} to return if {@code this} is an {@link Ok}.
     * @param <U> The type of the {@link Ok} value for the other {@link Result}.
     * @return Returns {@code res} if {@code this} is an {@link Ok}, otherwise returns
     *         the {@link Err} value of this result converted to {@code Result<U, E>}.
     */
    <U> Result<U, E> and(Result<U, E> res);

    /**
     * Calls and returns {@code op} if {@code this} is an {@link Ok}, otherwise returns
     * the {@link Err} value of this result converted to {@code Result<U, E>}.
     *
     * @param op The function to call on the value of {@link Ok} if {@code this} is an {@link Ok}.
     * @param <U> The type of the {@link Ok} value for the other {@link Result} evaluated from {@code op}.
     * @return Returns the value of the {@code op} call if {@code this} is an {@link Ok},
     *         otherwise returns the {@link Err} value of this result converted to {@code Result<U, E>}.
     */
    <U> Result<U, E> andThen(Function<T, Result<U, E>> op);

    /**
     * Maps a {@code Result<T, E>} to {@code Result<U, E>} by applying a function to a contained
     * {@link Ok} value, leaving the {@link Err} value untouched, if any.
     *
     * @param op The function to call on the value of {@link Ok} if {@code this} is an {@link Ok}.
     * @param <U> The type of the {@link Ok} value for the result of {@code op}.
     * @return Returns the result of the function {@code op} evaluated on the value of {@link Ok},
     *         mapped to a {@code Result<U, E>}. Otherwise, returns {@code this} cast to
     *         {@code Result<U, E>}.
     */
    <U> Result<U, E> map(Function<T, U> op);

    /**
     * Maps a {@code Result<T, E>} to {@code Result< U, E>} by applying a function to a contained
     * {@link Err} value, leaving the {@link Ok} value untouched, if any.
     *
     * @param op The function to call on the value of {@link Err} if {@code this} is an {@link Err}.
     * @param <F> The type of the {@link Err} value for the result of {@code op}.
     * @return Returns the result of the function {@code op} evaluated on the value of {@link Err},
     *         mapped to a {@code Result<T, F>}. Otherwise, returns {@code this} cast to
     *         {@code Result<T, F>}.
     */
    <F> Result<T, F> mapErr(Function<E, F> op);

    /**
     * Maps a {@code Result<T, E>} to {@code U} by applying a function to a contained {@link Ok} value,
     * or a fallback function applied to a contained {@link Err} value.
     *
     * This method can be used to unpack a successful result while handling an error.
     *
     * @param fallback The fallback function to apply to {@link Err} if {@code this} is an {@link Err}.
     * @param op The function to apply to {@link Ok} if {@code this} is an {@link Ok}.
     * @param <U> The return type of the success and failure methods in {@code fallback} and {@code op}.
     * @return Returns a {@code U} by applying a function to a contained {@link Ok} value, or a fallback
     *         function applied to a contained {@link Err} value.
     */
    @Experimental
    <U> U mapOrElse(Function<E, U> fallback, Function<T, U> op);

    /**
     * Returns {@code res} if {@code this} is an {@link Err}, otherwise returns the
     * {@link Ok} value of this result converted to {@code Result<T, F>}.
     *
     * Arguments passed to {@code and} are eagerly evaluated; if you are passing the result of a function call,
     * it is recommended to use {@link #orElse(Function)}, which is lazily evaluated.
     *
     * @param res The {@link Result} to return if {@code this} is an {@link Err}.
     * @param <F> The type of the {@link Err} value for the other {@link Result}.
     * @return Returns {@code res} if {@code this} is an {@link Err}, otherwise returns
     *         the {@link Ok} value of this result converted to {@code Result<T, F>}.
     */
    <F> Result<T, F> or(Result<T, F> res);

    /**
     * Calls and returns {@code op} if {@code this} is an {@link Err}, otherwise returns
     * the {@link Ok} value of this result converted to {@code Result<T, F>}.
     *
     * @param op The function to call on the value of {@link Err} if {@code this} is an {@link Err}.
     * @param <F> The type of the {@link Err} value for the other {@link Result} evaluated from {@code op}.
     * @return Returns the value of the {@code op} call if {@code this} is an {@link Err},
     *         otherwise returns the {@link Ok} value of this result converted to {@code Result<T, F>}.
     */
    <F> Result<T, F> orElse(Function<E, Result<T, F>> op);
}
