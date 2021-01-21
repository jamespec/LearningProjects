
public class MyLinkedList<T>
{
    private static class Node<T> {
        T value;
        Node<T> next;

        Node(T value, Node<T> next) {
            this.value = value;
            this.next = next;
        }
    }

    Node<T> head=null;

    void insertFromFront( T v, int pos ) {
        if( pos == 0 ) {
            head = new Node<>(v, head);
        }
        else {
            Node<T> prior = head;
            for (int i = 1; i < pos && prior != null; i++)
                prior = prior.next;

            if( prior == null )
                throw new IndexOutOfBoundsException();

            prior.next = new Node<>(v, prior.next);
        }
    }

    // Append to end of the list
    void append( T v ) {
        Node<T> newNode = new Node<>(v, null);

        if( head == null ) {
            head = newNode;
        }
        else {
            Node<T> current = head;
            while (current.next != null)
                current = current.next;

            current.next = newNode;
        }
    }

    // Get the value at index
    T get( int index ) {
        Node<T> current=head;
        for( int i=0; i<index && current != null; i++ )
            current = current.next;

        return (current != null ? current.value : null);
    }
}
