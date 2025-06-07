package WordCompletion;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

class AVLNode {
    String word;
    int frequency;
    int height;
    AVLNode left, right;

    AVLNode(String word) {
        this.word = word;
        this.frequency = 1;
        this.height = 1;
    }
}

