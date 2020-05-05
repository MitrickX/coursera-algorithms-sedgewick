/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import java.util.Arrays;
import java.util.Iterator;

public class Deque<Item> implements Iterable<Item> {

    // helper linked list class
    private static class Node<Item> {
        private Item item;
        private Node<Item> next;
        private Node<Item> prev;
    }

    private Node<Item> first = null;
    private Node<Item> last = null;
    private int n = 0;

    // construct an empty deque
    public Deque() {
    }

    // is the deque empty?
    public boolean isEmpty() {
        return n == 0;
    }

    // return the number of items on the deque
    public int size() {
        return n;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null) {
            throw new java.lang.IllegalArgumentException();
        }
        Node<Item> node = new Node<Item>();
        node.item = item;

        if (isEmpty()) {
            first = node;
            last = node;
        } else {
            node.next = first;
            first.prev = node;
            first = node;
        }

        n++;
    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null) {
            throw new java.lang.IllegalArgumentException();
        }
        Node<Item> node = new Node<Item>();
        node.item = item;

        if (isEmpty()) {
            first = node;
            last = node;
        } else {
            node.prev = last;
            last.next = node;
            last = node;
        }

        n++;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (isEmpty()) {
            throw new java.util.NoSuchElementException();
        }
        Node<Item> node = first;

        // move first
        first = first.next;
        // unlink first with prev item if first is not null
        if (first != null) {
            first.prev = null;
        } else {
            last = null;
        }

        // unlink current node with first item
        node.next = null;
        n--;

        return node.item;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (isEmpty()) {
            throw new java.util.NoSuchElementException();
        }

        Node<Item> node = last;

        // move back last
        last = last.prev;
        // unlink last with next item if last is not null
        if (last != null) {
            last.next = null;
        } else {
            first = null;
        }

        // unlink current node with last item
        node.prev = null;
        n--;

        return node.item;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new DequeIterator(first);
    }

    private class DequeIterator implements Iterator<Item> {

        private Node<Item> current;

        public DequeIterator(Node<Item> first) {
            current = first;
        }

        public boolean hasNext() {
            return current != null;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Item next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException();
            }
            Item item = current.item;
            current = current.next;
            return item;
        }
    }

    // unit testing (required)
    public static void main(String[] args)
    {
        Deque<Integer> deque = new Deque<Integer>();

        if (!deque.isEmpty()) {
            System.out.println("FAIL: New Deque must be empty!");
        }
        if (deque.size() != 0) {
            System.out.println("FAIL: New Deque must has size == 0");
        }

        deque.addLast(1);
        deque.addLast(2);
        deque.addFirst(3);
        deque.addFirst(4);
        deque.addLast(5);

        if (deque.size() != 5) {
            System.out.println("FAIL: Deque size must has size 5");
        }

        if (deque.isEmpty()) {
            System.out.println("FAIL: Deque must not be empty (cause size == 5)");
        }

        // 4 3 1 2 5
        int[] expected = new int[] {4, 3, 1, 2, 5};
        int[] result = new int[deque.size()];
        int i = 0;
        for (int v : deque) {
            result[i] = v;
            i++;
        }

        if (!Arrays.equals(result, expected)) {
            System.out.print("FAIL: result array it not match");
            System.out.print("\texpected {");
            for (int v : expected) {
                System.out.print(v);
                System.out.print(",");
            }
            System.out.print("}\n");
            System.out.print("\tresult {");
            for (int v : expected) {
                System.out.print(v);
                System.out.print(",");
            }
            System.out.print("}\n");
        }

        deque.removeLast();
        deque.removeFirst();

        if (deque.size() != 3) {
            System.out.println("FAIL: Deque size must has size 3");
        }

        if (deque.isEmpty()) {
            System.out.println("FAIL: Deque must not be empty (cause size == 3)");
        }

        expected = new int[] {3, 1, 2};
        result = new int[deque.size()];
        i = 0;
        for (int v : deque) {
            result[i] = v;
            i++;
        }

        if (!Arrays.equals(result, expected)) {
            System.out.print("FAIL: result array it not match");
            System.out.print("\texpected {");
            for (int v : expected) {
                System.out.print(v);
                System.out.print(",");
            }
            System.out.print("}\n");
            System.out.print("\tresult {");
            for (int v : result) {
                System.out.print(v);
                System.out.print(",");
            }
            System.out.print("}\n");
        }

        // drain all rest items
        deque.removeLast();
        deque.removeFirst();
        deque.removeFirst();

        if (deque.size() != 0) {
            System.out.printf("FAIL: Deque size must be 0 instead of %d\n", deque.size());
        }

        if (!deque.isEmpty()) {
            System.out.println("FAIL: Deque must be empty (cause size == 0)");
        }

        boolean catched;

        catched = false;
        try {
            deque.removeLast();
        } catch (java.util.NoSuchElementException e) {
            catched = true;
        }
        if (!catched) {
            System.out.println("FAIL: removeLast from empty deque must throw NoSuchElementException");
        }

        catched = false;
        try {
            deque.removeFirst();
        } catch (java.util.NoSuchElementException e) {
            catched = true;
        }
        if (!catched) {
            System.out.println("FAIL: removeLast from empty deque must throw NoSuchElementException");
        }

        catched = false;
        try {
            deque.addFirst(null);
        } catch (java.lang.IllegalArgumentException e) {
            catched = true;
        }
        if (!catched) {
            System.out.println("FAIL: addFirst(null) must throw IllegalArgumentException");
        }

        catched = false;
        try {
            deque.addLast(null);
        } catch (java.lang.IllegalArgumentException e) {
            catched = true;
        }
        if (!catched) {
            System.out.println("FAIL: addLast(null) must throw IllegalArgumentException");
        }


        // TEST 2

        Deque<Integer> deque2 = new Deque<Integer>();

        deque2.addFirst(1);
        deque2.removeLast();


        // TEST 3
        Deque<Integer> deque3 = new Deque<Integer>();
        deque3.addFirst(1);
        deque3.addLast(2);
        deque3.addFirst(3);
        deque3.removeFirst();
        deque3.removeLast();
        deque3.addLast(6);
        deque3.removeLast();
        deque3.removeLast();

        for (int v : deque3){
            System.out.println(v);
        }

    }

}