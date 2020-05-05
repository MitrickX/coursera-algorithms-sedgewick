/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.StdRandom;

import java.util.Arrays;
import java.util.Iterator;

public class RandomizedQueue<Item> implements Iterable<Item> {

    private Item[] a;
    private int n = 0;

    // construct an empty randomized queue
    public RandomizedQueue() {
        a = (Item[]) new Object[2];
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return n == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return n;
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null) {
            throw new java.lang.IllegalArgumentException();
        }
        if (n == a.length) {
            resize(2 * n);
        }
        a[n++] = item;
    }

    // resize the underlying array holding the elements
    private void resize(int capacity) {
        assert capacity >= n;
        a = java.util.Arrays.copyOf(a, capacity);
    }

    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) {
            throw new java.util.NoSuchElementException();
        }
        int i = StdRandom.uniform(0, n);
        Item item = a[i];
        a[i] = a[n - 1];
        a[n - 1] = null;    // to avoid loitering
        n--;

        // shrink size of array if necessary
        if (n > 0 && n == a.length/4) {
            resize(a.length/2);
        }

        return item;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (isEmpty()) {
            throw new java.util.NoSuchElementException();
        }
        int i = StdRandom.uniform(0, n);
        return a[i];
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomizedQueueIterator(a, n);
    }

    private class RandomizedQueueIterator implements Iterator<Item> {

        private Item[] slice;
        private int count;

        public RandomizedQueueIterator(Item[] a, int n) {
            slice = (Item[]) new Object[n];
            slice = java.util.Arrays.copyOf(a, n);
            count = n;
        }

        public boolean hasNext() {
            return count > 0;
        }


        public Item next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException();
            }
            int i = StdRandom.uniform(0, count);
            Item item = slice[i];
            slice[i] = slice[count - 1];
            count--;
            return item;
        }


        public void remove() {
            throw new java.lang.UnsupportedOperationException();
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        RandomizedQueue<Integer> queue = new RandomizedQueue<Integer>();

        if (!queue.isEmpty()) {
            System.out.println("FAIL: New RandomizedQueue must be empty!");
        }

        if (queue.size() != 0) {
            System.out.println("FAIL: New RandomizedQueue must has size == 0");
        }

        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        queue.enqueue(4);
        queue.enqueue(5);

        if (queue.size() != 5) {
            System.out.println("FAIL: RandomizedQueue size must has size 5");
        }

        if (queue.isEmpty()) {
            System.out.println("FAIL: RandomizedQueue must not be empty (cause size == 5)");
        }

        int v;

        v = queue.sample();
        System.out.printf("RandomizedQueue return sample %d\n", v);
        if (v <= 0 || v > 5) {
            System.out.printf("FAIL: RandomizedQueue has not value %d\n", v);
        }

        v = queue.sample();
        System.out.printf("RandomizedQueue return sample %d\n", v);
        if (v <= 0 || v > 5) {
            System.out.printf("FAIL: RandomizedQueue has not value %d\n", v);
        }

        if (queue.size() != 5) {
            System.out.println("FAIL: RandomizedQueue size must has size 5");
        }

        if (queue.isEmpty()) {
            System.out.println("FAIL: RandomizedQueue must not be empty (cause size == 5)");
        }

        v = queue.dequeue();
        System.out.printf("RandomizedQueue return dequeued value %d\n", v);
        if (v <= 0 || v > 5) {
            System.out.printf("FAIL: RandomizedQueue has not value %d\n", v);
        }

        int v2;

        v2 = queue.dequeue();
        System.out.printf("RandomizedQueue return dequeued value %d\n", v2);
        if (v2 <= 0 || v2 > 5) {
            System.out.printf("FAIL: RandomizedQueue has not value %d\n", v2);
        }

        if (v2 == v) {
            System.out.printf("FAIL: RandomizedQueue can't return same dequeued values 2 times (v=%d,v2=%d)\n", v, v2);
        }

        if (queue.size() != 3) {
            System.out.println("FAIL: RandomizedQueue size must has size 3");
        }

        if (queue.isEmpty()) {
            System.out.println("FAIL: RandomizedQueue must not be empty (cause size == 3)");
        }

        // back old values
        queue.enqueue(v);
        queue.enqueue(v2);

        if (queue.size() != 5) {
            System.out.println("FAIL: RandomizedQueue size must has size 5");
        }

        if (queue.isEmpty()) {
            System.out.println("FAIL: RandomizedQueue must not be empty (cause size == 4)");
        }

        Iterator<Integer> it1 = queue.iterator();
        Iterator<Integer> it2 = queue.iterator();

        int[] result1 = new int[5];
        int[] result2 = new int[5];

        for (int i = 0; i < 5; i++) {

            int _v1 = it1.next();
            result1[i] = _v1;

            int _v2 = it2.next();
            result2[i] = _v2;

        }

        if (Arrays.equals(result1, result2)) {
            System.out.println("WARNING: Looks like RandomizedQueue iterators are not independent");
        }

        boolean catched;

        catched = false;
        try {
            it1.next();
        } catch (java.util.NoSuchElementException e) {
            catched = true;
        }
        if (!catched) {
            System.out.println("FAIL: it1.next() must throw NoSuchElementException");
        }

        catched = false;
        try {
            it2.next();
        } catch (java.util.NoSuchElementException e) {
            catched = true;
        }
        if (!catched) {
            System.out.println("FAIL: it2.next() must throw NoSuchElementException");
        }

        // drain all items
        queue.dequeue();
        queue.dequeue();
        queue.dequeue();
        queue.dequeue();
        queue.dequeue();

        if (queue.size() != 0) {
            System.out.printf("FAIL: RandomizedQueue size must be 0 instead of %d\n", queue.size());
        }

        if (!queue.isEmpty()) {
            System.out.println("FAIL: RandomizedQueue must be empty (cause size == 0)");
        }

        catched = false;
        try {
            queue.dequeue();
        } catch (java.util.NoSuchElementException e) {
            catched = true;
        }
        if (!catched) {
            System.out.println("FAIL: dequeue() from empty RandomizedQueue must throw NoSuchElementException");
        }

        catched = false;
        try {
            queue.sample();
        } catch (java.util.NoSuchElementException e) {
            catched = true;
        }
        if (!catched) {
            System.out.println("FAIL: sample from empty RandomizedQueue must throw NoSuchElementException");
        }

        catched = false;
        try {
            queue.enqueue(null);
        } catch (java.lang.IllegalArgumentException e) {
            catched = true;
        }
        if (!catched) {
            System.out.println("FAIL: enqueue(null) on RandomizedQueue must throw NoSuchElementException");
        }

    }

}