import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;

public class MyStack<T>
{
    T[] data;
    int size;
    int next;

    MyStack( int size ) {
        this.size = size;
        this.data = (T[]) new Object[size];
        this.next = 0;
    }

    void push( T v ) {
        if( next < size ) {
            data[next] = v;
            next++;
        }
        else
            throw new BufferOverflowException();
    }

    T pop() {
        if( next == 0 )
            throw new BufferUnderflowException();

        next--;
        return data[next];
    }
}
