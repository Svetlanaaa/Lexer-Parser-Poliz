package com;
public class Item {
    private Object data;
    private Item prev;
    private Item next;

    public Item(Object instance) {
        this.data = instance;
    }

    public void setInstance(Object data) {
        this.data = data;
    }

    public void setPrev(Item prev) {
        this.prev = prev;
    }

    public void setNext(Item next) {
        this.next = next;
    }

    public Object getData() {
        return data;
    }

    public Item getPrev() {
        return prev;
    }

    public Item getNext() {
        return next;
    }

    public String toString() {
        return data.toString();
    }
}
