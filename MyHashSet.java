package com;
public class MyHashSet {
    int n;
    MyLinkedList[] set;

    public MyHashSet() {
        n = 100;
        set = new MyLinkedList[n];
        for (int i = 0; i < n; i++) {
            set[i] = new MyLinkedList();
        }
    }

    private boolean needRehash() {
        int nMoreTwo = 0;
        for (MyLinkedList list : set) {
            if (list.size() >= 2) nMoreTwo++;
        }
        return nMoreTwo / n >= 0.75;
    }

    private void rehash() {
        int newN = n * 2;
        MyLinkedList[] newSet = new MyLinkedList[newN];
        for (MyLinkedList list : set) {
            for (int i = 0; i < list.size(); i++) {
                Object element = list.get(i);
                int newIndex = element.hashCode() % newN;
                if (newSet[newIndex] == null) {
                    newSet[newIndex] = new MyLinkedList();
                }
                newSet[newIndex].add(element);
            }
        }
        set = newSet;
        n = newN;
    }

    private int getHashCode(Object object) {
        return object.hashCode() % n;
    }

    public void add(Object object) {
        if (needRehash()) rehash();
        int h = getHashCode(object);
        if (!set[h].contains(object)) {
            set[h].add(object);
        }
    }

    public boolean contains(Object object) {
        int hashCode = getHashCode(object);
        return set[hashCode].contains(object);
    }

    public void remove(Object object) {
        int hashCode = getHashCode(object);
        MyLinkedList list = set[hashCode];
        if (list.contains(object)) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).equals(object)) {
                    list.remove(i);
                }
            }
        }
    }

}
