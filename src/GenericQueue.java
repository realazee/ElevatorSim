import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.lang.IllegalStateException;

public class GenericQueue<E> {
	private final int MAX_QUEUE_SIZE;
	private LinkedList<E> queue = new LinkedList<>();

	public GenericQueue(int queueSize) {
		MAX_QUEUE_SIZE = queueSize;
	}

	/*
	 * Implement the following methods:
	 * 1) boolean isEmpty()
	 * 2) int getSize()
	 * 3) boolean add(E o)
	 * 4) boolean offer(E o)
	 * 5) E remove()
	 * 6) E poll()
	 * 7) E element()
	 * 8) E peek()
	 */
	
	public boolean isEmpty() {
		return queue.isEmpty();
	}
	
	public int getSize() {
		return queue.size();
	}
	
	public boolean add(E o) throws IllegalStateException {
		
		if(queue.size() == MAX_QUEUE_SIZE) {
			throw new IllegalStateException();	
		}
		
		return queue.add(o);
	}
	
	public boolean offer(E o) {
		if (queue.size() < MAX_QUEUE_SIZE) {
	            queue.add(o);
		} else {
			return false;
		}
		return true;
	}
	
	public E remove() {
		return queue.remove();
	}
	
	public E poll() {
		return queue.poll();
	}
	
	public E element() {
		return queue.element();
	}
	
	public E peek() {
		return queue.peek();
	}
	
	public ListIterator<E> getListIterator() {
		return queue.listIterator(0);
	}
	
	
	
	
        // Do NOT TOUCH THIS!!
	@Override
	public String toString() {
		String str = "queue: [";
		ListIterator<E> list = queue.listIterator(0);
		if (list != null) {
			while (list.hasNext()) {
				str += list.next();
				if (list.hasNext()) str += ",";
			}
		}
		str += "]";
		return str;
	}

}
