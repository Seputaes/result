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

/**
 * Exception thrown by {@link Result}'s when attempting to unwrap an invalid value.
 *
 * <p>This exception is when attempting to {@link Result#unwrap()} an {@link Err} value, or
 * {@link Result#unwrapErr()} an {@link Ok} value.
 *
 * <p>By extension, this also gets thrown by {@link Result#expect(String)}} and
 * {@link Result#expectErr(String)} as these methods also unwrap.
 *
 * <p>This is the Java version of a "panic" by Rust's Result implementation.
 */
public class ResultException extends RuntimeException {

    /**
     * Constructs a new Result exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message The detail message. The detail message is saved for later retrieval
     *                by the {@link #getMessage()} method.
     */
    public ResultException(final String message) {
        super(message);
    }
}
