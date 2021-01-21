import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MyLinkedListTest
{
    MyLinkedList<Integer> ll;
    int out;

    @BeforeEach
    void beforeEach() {
        ll = new MyLinkedList<>();
    }

    @Test
    void appendCheck() {
        ll.append(1);
        ll.append(2);
        ll.append(3);

        out = ll.get(0);
        assert( out == 1 );
        out = ll.get(1);
        assert( out == 2 );
        out = ll.get(2);
        assert( out == 3 );
    }

    @Test
    void insertAndCheck() {
        ll.append(1);
        ll.append(2);
        ll.append(3);
        ll.insertFromFront(10, 0);
        ll.insertFromFront(20, 1);
        out = ll.get(0);
        assert( out == 10 );
        out = ll.get(1);
        assert( out == 20 );
        out = ll.get(2);
        assert( out == 1 );
        out = ll.get(3);
        assert( out == 2 );
        out = ll.get(4);
        assert( out == 3 );
    }
}
