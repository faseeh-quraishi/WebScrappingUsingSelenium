package WordCompletion;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

//Class representing the AVL Tree structure for storing words and their frequencies
class AVLTree {
	AVLNode root; // Root node of the AVL tree

	 // Get height of a node (returns 0 if node is null)
	 int height(AVLNode n) {
	     return n == null ? 0 : n.height;
	 }

	 // Get balance factor of a node: height(left subtree) - height(right subtree)
	 int getBalance(AVLNode n) {
	     return n == null ? 0 : height(n.left) - height(n.right);
	 }

	 // Right rotate the subtree rooted at y
	 AVLNode rotateRight(AVLNode y) {
	     AVLNode x = y.left;       // new root
	     AVLNode T2 = x.right;     // temp to hold subtree
	
	     // Perform rotation
	     x.right = y;
	     y.left = T2;
	
	     // Update heights
	     y.height = Math.max(height(y.left), height(y.right)) + 1;
	     x.height = Math.max(height(x.left), height(x.right)) + 1;
	
	     // Return new root
	     return x;
	 }

	// Left rotate the subtree rooted at x
	 AVLNode rotateLeft(AVLNode x) {
	     AVLNode y = x.right;      // new root
	     AVLNode T2 = y.left;      // temp to hold subtree
	
	     // Perform rotation
	     y.left = x;
	     x.right = T2;
	
	     // Update heights
	     x.height = Math.max(height(x.left), height(x.right)) + 1;
	     y.height = Math.max(height(y.left), height(y.right)) + 1;
	
	     // Return new root
	     return y;
	 }

	 // Recursive insertion of a word into the AVL tree
	 AVLNode insert(AVLNode node, String word) {
	     // Base case: insert new node
	     if (node == null)
	         return new AVLNode(word);
	
	     // Compare input word with current node's word
	     int cmp = word.compareTo(node.word);
	     if (cmp < 0) {
	         // Insert in left subtree
	         node.left = insert(node.left, word);
	     } else if (cmp > 0) {
	         // Insert in right subtree
	         node.right = insert(node.right, word);
	     } else {
	         // Word already exists, increment its frequency
	         node.frequency++;
	         return node;
	     }
	
	     // Update height of this ancestor node
	     node.height = 1 + Math.max(height(node.left), height(node.right));
	
	     // Get balance factor to check whether node became unbalanced
	     int balance = getBalance(node);
	
	     // Case 1: Left Left (LL)
	     if (balance > 1 && word.compareTo(node.left.word) < 0)
	         return rotateRight(node);
	
	     // Case 2: Right Right (RR)
	     if (balance < -1 && word.compareTo(node.right.word) > 0)
	         return rotateLeft(node);
	
	     // Case 3: Left Right (LR)
	     if (balance > 1 && word.compareTo(node.left.word) > 0) {
	         node.left = rotateLeft(node.left);
	         return rotateRight(node);
	     }
	
	     // Case 4: Right Left (RL)
	     if (balance < -1 && word.compareTo(node.right.word) < 0) {
	         node.right = rotateRight(node.right);
	         return rotateLeft(node);
	     }
	
	     // Return the unchanged node pointer
	     return node;
	 }

	 // Public method to insert a word into the AVL tree
	 void insert(String word) {
	     root = insert(root, word);
	 }
	
	 /**
	  * Recursively collects words starting with the given prefix and adds them
	  * into a min-heap of fixed size k to get top-k suggestions by frequency.
	  */
	 void collectWordsWithPrefix(AVLNode node, String prefix, PriorityQueue<WordFrequency> heap, int k) {
	     if (node == null) return;
	
	     // If node's word matches the prefix, consider adding to heap
	     if (node.word.startsWith(prefix)) {
	         heap.offer(new WordFrequency(node.word, node.frequency));
	         // Keep heap size limited to k (min-heap keeps least frequent on top)
	         if (heap.size() > k) heap.poll();
	     }
	
	     // Traverse left subtree if prefix is <= current node's word
	     if (prefix.compareTo(node.word) <= 0)
	         collectWordsWithPrefix(node.left, prefix, heap, k);
	
	     // Traverse right subtree if prefix is >= current node's word
	     if (prefix.compareTo(node.word) >= 0)
	         collectWordsWithPrefix(node.right, prefix, heap, k);
	 }

	 /**
	  * Returns a list of up to 'k' words that start with the given prefix,
	  * sorted by frequency in descending order.
	  */
	 List<WordFrequency> getSuggestions(String prefix, int k) {
	     PriorityQueue<WordFrequency> heap = new PriorityQueue<>(); // min-heap by default
	     collectWordsWithPrefix(root, prefix, heap, k); // fill heap with matching words
	
	     // Convert heap to list and sort in descending order of frequency
	     List<WordFrequency> result = new ArrayList<>(heap);
	     result.sort((a, b) -> Integer.compare(b.frequency, a.frequency)); // highest freq first
	     return result;
	 }
}
