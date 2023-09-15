//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.example.assignment1deepspace;

public class UnionFind {
    public UnionFind() {
    }

    public static int find(int[] a, int id) {
        if (a[id] < 0) {
            return a[id];
        } else {
            return a[id] == id ? id : find(a, a[id]);
        }
    }

    public static void union(int[] a, int p, int q) {
        a[find(a, q)] = find(a, p);
    }
}
