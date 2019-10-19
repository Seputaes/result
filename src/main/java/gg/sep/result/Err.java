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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Contains the error value of a {@link Result}.
 *
 * @param <T> Type of the success value of the result.
 * @param <E> Type of the error value of the result.
 */
public final class Err<T, E> implements Result<T, E> {

    private final E error;

    private Err(final E error) {
        this.error = error;
    }

    /**
     * Constructs a new {@link Err} result with the provided error value.
     * @param error Error value.
     * @param <T> Type of the success value.
     * @param <E> Type of the error value.
     * @return New {@link Err} result with the provided error value.
     */
    public static <T, E> Err<T, E> of(final E error) {
        Objects.requireNonNull(error);
        return new Err<>(error);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOk() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isErr() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<T> getOk() {
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<E> getErr() {
        return Optional.of(error);
    }

    @Override
    public T unwrap() throws ResultException {
        throw new ResultException(String.format("%s", error));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E unwrapErr() throws ResultException {
        return error;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T unwrapOr(final T optb) {
        return optb;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T unwrapOrElse(final Function<E, T> op) {
        return op.apply(error);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T expect(final String msg) throws ResultException {
        throw new ResultException(String.format("%s: %s", msg, error));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E expectErr(final String msg) throws ResultException {
        return error;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <U> Result<U, E> and(final Result<U, E> res) {

        return (Result<U, E>) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <U> Result<U, E> andThen(final Function<T, Result<U, E>> op) {
        return (Result<U, E>) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <U> Result<U, E> map(final Function<T, U> op) {
        return (Result<U, E>) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <F> Result<T, F> mapErr(final Function<E, F> op) {
        return Err.of(op.apply(error));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <U> U mapOrElse(final Function<E, U> fallback, final Function<T, U> op) {
        return fallback.apply(error);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <F> Result<T, F> or(final Result<T, F> res) {
        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <F> Result<T, F> orElse(final Function<E, Result<T, F>> op) {
        return op.apply(error);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public T next() {
                throw new NoSuchElementException("No elements contained in Err.");
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Err) {
            return ((Err<?, ?>) obj).error.equals(error);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(error);
    }
}
