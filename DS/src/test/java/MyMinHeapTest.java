import org.junit.jupiter.api.Test;

public class MyMinHeapTest
{
    @Test
    void basicTests() {
        MyMinHeap h = new MyMinHeap(11);
        h.insertKey(3);
        h.insertKey(2);
        h.deleteKey(1);
        h.insertKey(15);
        h.insertKey(5);
        h.insertKey(4);
        h.insertKey(45);
        System.out.print(h.extractMin() + " ");
        System.out.print(h.getMin() + " ");
        h.decreaseKey(2, 1);
        System.out.print(h.getMin());
    }
}
