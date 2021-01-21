import org.junit.jupiter.api.Test;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MyStackTest
{
    @Test
    void pushPopTest1() {
        MyStack<Integer> s = new MyStack<>(5);
        int out;

        s.push(2);
        out = s.pop();
        assert( out == 2 );
    }

    @Test
    void pushPopTest2() {
        MyStack<Integer> s = new MyStack<>(5);
        int out;

        s.push(10);
        s.push(2);
        out = s.pop();
        assert( out == 2 );
        out = s.pop();
        assert( out == 10 );
    }

    @Test
    void whenPushTooManyThrowsBufferOverflowException() {
        MyStack<Integer> s = new MyStack<>(2);
        s.push(1);
        s.push(2);

        Exception exception = assertThrows(BufferOverflowException.class, () -> {
            s.push(3);
        });
    }

    @Test
    void whenPopTooManyThrowsBufferUnderflowException() {
        MyStack<Integer> s = new MyStack<>(2);
        s.push(1);
        s.push(2);
        s.pop();
        s.pop();
        Exception exception = assertThrows(BufferUnderflowException.class, () -> {
            int v = s.pop();
        });
    }

}
