
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
        if( next < 1000 ) {
            data[next] = v;
            next++;
        }
    }

    T pop() {
        if( next == 0 )
            throw new StackOverflowError();

        next--;
        return data[next];
    }

    public static void main( String[] argv ) {
        MyStack<Integer> s = new MyStack<>(10 );
        s.push(1);
        s.push(2);
        s.push(3);
        s.push(4);
        s.push(5);
        int out = s.pop();
        out = s.pop();
        out = s.pop();
        out = s.pop();
        out = s.pop();
        out = s.pop();
    }
}
