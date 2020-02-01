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
 * Contains the success value of a {@link Result}.
 *
 * @param <T> Type of the success value of the result.
 * @param <E> Type of the error value of the result.
 */
public final class Ok<T, E> implements Result<T, E> {

    private final T value;

    private Ok(final T value) {
        this.value = value;
    }

    /**
     * Constructs a new {@link Ok} result with the provided success value.
     * @param value Success value.
     * @param <T> Type of the success value.
     * @param <E> Type of the error value.
     * @return New {@link Ok} result with the provided success value.
     */
    public static <T, E> Ok<T, E> of(final T value) {
        Objects.requireNonNull(value);
        return new Ok<>(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOk() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isErr() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(final T x) {
        return value.equals(x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsErr(final E f) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<T> getOk() {
        return Optional.of(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<E> getErr() {
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T unwrap() throws ResultException {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E unwrapErr() throws ResultException {
        throw new ResultException(String.format("%s", value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T unwrapOr(final T optb) {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T unwrapOrElse(final Function<E, T> op) {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T expect(final String msg) throws ResultException {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E expectErr(final String msg) throws ResultException {
        throw new ResultException(String.format("%s: %s", msg, value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <U> Result<U, E> and(final Result<U, E> res) {
        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <U> Result<U, E> andThen(final Function<T, Result<U, E>> op) {
        return op.apply(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <U> Result<U, E> map(final Function<T, U> op) {
        return Ok.of(op.apply(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <F> Result<T, F> mapErr(final Function<E, F> op) {
        return (Result<T, F>) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <U> U mapOr(final U defaultValue, final Function<T, U> op) {
        return op.apply(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <U> U mapOrElse(final Function<E, U> fallback, final Function<T, U> map) {
        return map.apply(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <F> Result<T, F> or(final Result<T, F> res) {
        return (Result<T, F>) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <F> Result<T, F> orElse(final Function<E, Result<T, F>> op) {
        return (Result<T, F>) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private boolean next = true;

            @Override
            public boolean hasNext() {
                return next;
            }

            @Override
            public T next() {
                if (!next) {
                    throw new NoSuchElementException("Ok only contains one element.");
                }
                this.next = false;
                return value;
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
        if (obj instanceof Ok) {
            return ((Ok<?, ?>) obj).value.equals(value);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
