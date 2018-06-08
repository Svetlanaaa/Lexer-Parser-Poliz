package com;
public class MyLinkedList {
    private Item first;
    private Item last;
    private int count = 0;

    public void add(Object object) {
        if (first == null) {
            first = new Item(object);
            last = first;
        } else {
            Item item = new Item(object);
            item.setPrev(last);
            last.setNext(item);
            last = item;
        }
        count++;
    }

    private Item getNode(int index) throws IndexOutOfBoundsException {
        if (index >= count) {
            throw new IndexOutOfBoundsException();
        }
        int i;
        Item item;
        if (count == 1) {
            item = first;
        } else if (count == 2) {
            item = index == 0 ? first : last;
        } else if (index < count / 2) {
            i = 0;
            item = first;
            while (i < index) {
                item = item.getNext();
                i++;
            }
        } else {
            i = count - 1;
            item = last;
            while (i > index) {
                item = item.getPrev();
                i--;
            }
        }
        return item;
    }

    public Object get(int index) {
        if (index >= count) {
            throw new IndexOutOfBoundsException();
        }
        return getNode(index).getData();
    }

    public void remove(int index) throws IndexOutOfBoundsException {
        if (index >= count) {
            throw new IndexOutOfBoundsException();
        }
        Item item = getNode(index);
        Item prev = item.getPrev();
        Item next = item.getNext();
        if (prev != null) {
            prev.setNext(next);
        } else {
            first = next;
        }
        if (next != null) {
            next.setPrev(prev);
        } else {
            last = prev;
        }
        count--;
    }

    public boolean contains(Object object) {
        for (Item current = first; current != null; current = current.getNext()) {
            if (current.getData().equals(object)) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        return count;
    }
}
