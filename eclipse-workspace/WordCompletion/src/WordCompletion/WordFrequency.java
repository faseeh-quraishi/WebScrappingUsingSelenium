package WordCompletion;

//Class to represent a word and its frequency of occurrence
public class WordFrequency implements Comparable<WordFrequency> {
	 String word;      // The word itself
	 int frequency;    // The number of times the word appears
	
	 // Constructor to initialize a WordFrequency object with a word and its frequency
	 public WordFrequency(String word, int frequency) {
	     this.word = word;
	     this.frequency = frequency;
	 }
	
	 /**
	  * Defines the natural ordering of WordFrequency objects.
	  * This is used by data structures like PriorityQueue (min-heap by default).
	  *
	  * Words with lower frequency will come first.
	  * If used in a PriorityQueue, the word with the lowest frequency will be at the head.
	  */
	 @Override
	 public int compareTo(WordFrequency other) {
	     return Integer.compare(this.frequency, other.frequency); // Ascending order by frequency
	 }
}


