import java.nio.BufferUnderflowException;

public class MyLinkedPriorityQueue<T>
{
    private static class Node<T> {
        int priority;
        T value;
        Node<T> next;

        Node(T value, int priority, Node<T> next) {
            this.priority = priority;
            this.value = value;
            this.next = next;
        }
    }

    Node<T> head =null;

    // Add an item to the Priority Queue by Priority
    // It will be placed as the last of a group with the same priority.
    void enqueue( T v, int priority ) {
        if( head == null || priority < head.priority ) {
            head = new Node<>(v, priority, head);
        }
        else {
            Node<T> current = head;
            while (current.next != null && current.next.priority > priority)
                current = current.next;

            current.next = new Node<>(v, priority, current.next);
        }
    }

    // Return and remove the highest priority item from the Priority Queue.
    T dequeue() {
        if( head == null )
            throw new BufferUnderflowException();

        Node<T> first = head;
        head = head.next;

        return first.value;
    }

    // Get the value at index
    T peek( int index ) {
        Node<T> current= head;
        for( int i=0; i<index && current != null; i++ )
            current = current.next;

        return (current != null ? current.value : null);
    }
}
