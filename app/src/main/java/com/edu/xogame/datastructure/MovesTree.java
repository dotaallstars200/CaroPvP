package com.edu.xogame.datastructure;

import com.edu.xogame.views.Cell;

import java.util.ArrayList;

public class MovesTree {

    private Node root;

    public MovesTree(Cell cell, boolean isMAX) {
        int point = isMAX ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        root = new Node(cell, point, null, isMAX);
    }

    public void setRoot(Node node) {
        node.parent = null;
        root = node;
    }

    public static class Node {
        private Cell cell;
        private int point;
        private Node parent;
        private boolean isMAX;
        private ArrayList<Node> children;

        public Node(Cell cell, int point, Node parent, boolean isMAX) {
            this.cell = cell;
            this.point = point;
            this.parent = parent;
            this.isMAX = isMAX;
            this.children = new ArrayList<>();
        }

        public void addNode(Node node) {
            this.children.add(node);
        }
    }
}
