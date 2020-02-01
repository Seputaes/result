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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Unit tests for the {@link Result} type.
 */
class ResultTest {

    private static Stream<Object> resultDataTypes() {
        return Stream.of("", 0, -1, 10, 1D, 1L, new RuntimeException("foo"));
    }

    @ParameterizedTest
    @MethodSource("resultDataTypes")
    void ok_StaticConstructor_NonNullValueEquals(final Object okValue) {
        final Result<?, ?> ok = Ok.of(okValue);
        assertEquals(okValue, ok.unwrap());
    }

    @ParameterizedTest
    @MethodSource("resultDataTypes")
    void err_StaticConstructor_NonNullValueEquals(final Object errValue) {
        final Result<?, ?> err = Err.of(errValue);
        assertEquals(errValue, err.unwrapErr());
    }

    @Test
    void ok_StaticConstructor_DoesNotAllowNull() {
        assertThrows(NullPointerException.class, () -> Ok.of(null));
    }

    @Test
    void err_StaticConstructor_DoesNotAllowNull() {
        assertThrows(NullPointerException.class, () -> Err.of(null));
    }

    @Test
    void isOk_OkIsTrue_ErrIsFalse() {
        assertTrue(Ok.of(100).isOk());
        assertFalse(Err.of("sadface").isOk());
    }

    @ParameterizedTest
    @MethodSource("resultDataTypes")
    void contains_OkMatchingValues_ErrAlwaysFalse(final Object value) {
        assertTrue(Ok.of(value).contains(value));
        assertFalse(Err.of(value).contains(value));
        assertFalse(Ok.<Object, Object>of("someOtherValue").contains(value));
    }

    @ParameterizedTest
    @MethodSource("resultDataTypes")
    void containsErr_OkAlwaysFalse_ErrMatchingValues(final Object value) {
        assertFalse(Ok.of(value).containsErr(value));
        assertTrue(Err.of(value).containsErr(value));
        assertFalse(Err.<Object, Object>of("someOtherValue").containsErr(value));
    }

    @Test
    void isErr_OkIsFalse_ErrIsTrue() {
        assertFalse(Ok.of(100).isErr());
        assertTrue(Err.of("sadface").isErr());
    }

    @Test
    void getOk_OkReturnsValue_ErrReturnsEmpty() {
        assertEquals(Optional.of(100), Ok.of(100).getOk());
        assertEquals(Optional.empty(), Err.of("sadface").getOk());
    }

    @Test
    void getErr_OkReturnsEmpty_ErrReturnsValue() {
        assertEquals(Optional.empty(), Ok.of(100).getErr());
        assertEquals(Optional.of("sadface"), Err.of("sadface").getErr());
    }

    @Test
    void unwrap_OkReturnsValue_ErrThrowsException() {
        assertEquals(100, Ok.of(100).unwrap());
        assertThrows(ResultException.class, () -> Err.of("sadface").unwrap());
    }

    @Test
    void unwrapErr_OkThrowsException_ErrReturnsValue() {
        assertThrows(ResultException.class, () -> Ok.of(100).unwrapErr());
        assertEquals("sadface", Err.of("sadface").unwrapErr());
    }

    @Test
    void unwrapOr_OkReturnsValue_ErrReturnsOptB() {
        assertEquals(100, Ok.of(100).unwrapOr(50));
        assertEquals(50, Err.of("Err").unwrapOr(50));
    }

    @Test
    void unwrapOrElse_OkReturnsValue_ErrAppliesFunction() {
        final AtomicInteger shouldIncrement = new AtomicInteger(0);
        assertEquals(100, Ok.of(100).unwrapOrElse(e -> 50));
        assertEquals(1, Err.of("Err").unwrapOrElse(e -> shouldIncrement.incrementAndGet()));
    }

    @Test
    void expect_OkReturnsValue_ErrThrowsExceptionWithMessage() {
        assertEquals(100, Ok.of(100).expect("Unexpected error"));
        final ResultException resultException = assertThrows(ResultException.class,
            () -> Err.of(50).expect("err except message"));
        assertTrue(resultException.getMessage().contains("err except message"));
    }

    @Test
    void expectErr_OkThrowsExceptionWithMessage_ErrReturnsValue() {
        final ResultException resultException = assertThrows(ResultException.class,
            () -> Ok.of(100).expectErr("ok exceptErr message"));
        assertTrue(resultException.getMessage().contains("ok exceptErr message"));
        assertEquals("sadface", Err.of("sadface").expectErr("Unexpected error"));
    }

    /**
     * Test cases from Rust Docs.
     * See: https://doc.rust-lang.org/std/result/enum.Result.html#method.and
     */
    @Test
    void and_AndLogicCombinations() {
        final Result<Integer, String> okInt = Ok.of(2);
        final Result<String, String> okStr = Ok.of("foo");
        final Result<String, String> okDiffType = Ok.of("different result type");

        final Result<Integer, String> errInt = Err.of("not a 2");
        final Result<String, String> earlyError = Err.of("early error");
        final Result<String, String> lateError = Err.of("late error");

        assertSame(lateError, okInt.and(lateError));
        assertSame(earlyError, earlyError.and(okStr));
        assertSame(errInt, errInt.and(lateError));
        assertSame(okDiffType, okInt.and(okDiffType));
    }

    /**
     * Test cases from Rust Docs.
     * See: https://doc.rust-lang.org/std/result/enum.Result.html#method.and_then
     */
    @Test
    void andThen_LogicCombinationsAppliesFunction() {
        final Function<Integer, Result<Integer, Integer>> resOkMultiply = i -> Ok.of(i * i);
        final Function<Integer, Result<Integer, Integer>> resErrSame = i -> Err.of(i);

        assertEquals(Ok.of(16),
            Ok.<Integer, Integer>of(2)
                .andThen(resOkMultiply) // succeeds: Ok(4)
                .andThen(resOkMultiply)); // succeeds: Ok(16)

        assertEquals(Err.of(4),
            Ok.<Integer, Integer>of(2)
                .andThen(resOkMultiply) // succeeds: Ok(4)
                .andThen(resErrSame)); // recasts: Err(4)

        assertEquals(Err.of(2),
            Ok.<Integer, Integer>of(2)
                .andThen(resErrSame) // fails: Err(2)
                .andThen(resOkMultiply) // recasts: Err(2)
        );

        assertEquals(Err.of(3),
            Err.<Integer, Integer>of(3)
                .andThen(resOkMultiply) // recasts: Err(3)
                .andThen(resOkMultiply)); // recasts: Err(3)
    }

    @Test
    void map_OkAppliesFunction_ErrDoesNotApply() {
        final Result<Integer, String> err = Err.of("sadface");
        assertEquals(Ok.of(16), Ok.of(2).map(i -> i * i).map(i -> i * i));
        assertSame(err, err.map(i -> i).map(i -> i * i));
    }

    @Test
    void mapErr_OkDoesNotApply_ErrAppliesFunction() {
        final Result<Integer, Integer> ok = Ok.of(2);
        assertEquals(Err.of(16), Err.of(2).mapErr(i -> i * i).mapErr(i -> i * i));
        assertSame(ok, ok.mapErr(i -> i).mapErr(i -> i));
    }

    @Test
    void mapOr_OkAppliesFunction_ErrReturnsDefault() {
        final Result<String, ?> ok = Ok.of("foo");
        assertEquals(3, ok.mapOr(42, String::length));

        final Result<String, ?> err = Err.of("error");
        assertEquals(42, err.mapOr(42, String::length));
    }

    @Test
    void mapOrElse_AppliesRespectiveFunctions() {
        final Function<Integer, Integer> addThree = i -> i + 3;
        final Function<Integer, Integer> multiplyBySelf = i -> i * i;
        assertEquals(Integer.valueOf(4), Ok.<Integer, Integer>of(2).mapOrElse(addThree, multiplyBySelf));
        assertEquals(Integer.valueOf(5), Err.<Integer, Integer>of(2).mapOrElse(addThree, multiplyBySelf));
    }

    /**
     * Test cases from Rust Docs.
     * See: https://doc.rust-lang.org/std/result/enum.Result.html#method.or
     */
    @Test
    void or_OrLogicCombinations() {
        final Result<Integer, String> okInt = Ok.of(2);
        final Result<Integer, String> okDiffValue = Ok.of(100);

        final Result<Integer, String> errInt = Err.of("not a 2");
        final Result<Integer, String> earlyError = Err.of("early error");
        final Result<Integer, String> lateError = Err.of("late error");

        assertSame(okInt, okInt.or(lateError));
        assertSame(okInt, earlyError.or(okInt));
        assertSame(lateError, errInt.or(lateError));
        assertSame(okInt, okInt.or(okDiffValue));
    }

    /**
     * Test cases from Rust Docs.
     * See: https://doc.rust-lang.org/std/result/enum.Result.html#method.or_else
     */
    @Test
    void orElse_LogicCombinationsAppliesFunction() {
        final Function<Integer, Result<Integer, Integer>> resOkMultiply = i -> Ok.of(i * i);
        final Function<Integer, Result<Integer, Integer>> resErrSame = i -> Err.of(i);
        assertEquals(Ok.of(2),
            Ok.<Integer, Integer>of(2)
                .orElse(resOkMultiply) // recast: Ok(2)
                .orElse(resOkMultiply)); // recast: OK(2)

        assertEquals(Ok.of(2),
            Ok.<Integer, Integer>of(2)
                .orElse(resErrSame) // recast: Ok(2)
                .orElse(resOkMultiply)); // recast: Ok(2)

        assertEquals(Ok.of(9),
            Err.<Integer, Integer>of(3)
                .orElse(resOkMultiply) // succeeds: Ok(9)
                .orElse(resErrSame) // recasts: Ok(9)
        );

        assertEquals(Err.of(3),
            Err.<Integer, Integer>of(3)
                .orElse(resErrSame) // recasts: Err(3)
                .orElse(resErrSame)); // recasts: Err(3)
    }

    @Test
    void equals_ComparesInnerValue() {
        final Result<Integer, ?> ok1 = Ok.of(2);
        final Result<Integer, ?> ok2 = Ok.of(4);
        final Result<?, Integer> err1 = Err.of(2);
        final Result<?, Integer> err2 = Err.of(4);

        // same object
        assertEquals(ok1, ok1);
        assertEquals(err1, err1);

        // different object, same value
        assertEquals(Ok.of(2), ok1);
        assertEquals(Err.of(4), err2);

        // different type, same value
        assertNotEquals(err1, ok1);
        assertNotEquals(ok2, err2);

        // different type, different value
        assertNotEquals(err1, ok2);
        assertNotEquals(ok2, err1);
    }

    @Test
    void hashCode_HashCodesMatch() {
        final Result<Integer, ?> ok1 = Ok.of(2);
        final Result<Integer, ?> ok2 = Ok.of(4);
        final Result<?, Integer> err1 = Err.of(2);
        final Result<?, Integer> err2 = Err.of(4);

        // same object
        assertEquals(ok1.hashCode(), ok1.hashCode());
        assertEquals(err1.hashCode(), err1.hashCode());

        // different object, same value
        assertEquals(ok1.hashCode(), Ok.of(2).hashCode());
        assertEquals(err2.hashCode(), Err.of(4).hashCode());

        // different type, same value
        assertEquals(ok1.hashCode(), err1.hashCode());
        assertEquals(err2.hashCode(), ok2.hashCode());

        // different type, different value
        assertNotEquals(ok1.hashCode(), err2.hashCode());
        assertNotEquals(err1.hashCode(), ok2.hashCode());
    }

    @Test
    void iterator_OkHasNext_ErrThrowsException() {
        final Result<Integer, Integer> ok = Ok.of(2);
        final Result<Integer, Integer> err = Err.of(3);

        final Iterator<Integer> okIter = ok.iterator();
        final Iterator<Integer> errIter = err.iterator();

        assertTrue(okIter.hasNext());
        assertEquals(2, okIter.next());
        // next should now be false
        assertFalse(okIter.hasNext());
        assertThrows(NoSuchElementException.class, okIter::next);

        assertFalse(errIter.hasNext());
        assertThrows(NoSuchElementException.class, errIter::next);
        assertFalse(errIter.hasNext());
    }

}
