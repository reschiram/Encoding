package data;

public class Queue<ContentType> {
	
	private class Node{
		ContentType Object;
		Node next;
		
		public Node(ContentType Object){
			this.Object = Object;
		}
	}
	
	private Node head = null;
	private Node tail = null;
	
	/**
	 * add an Object to the Queue
	 * @param Object the Object to be added
	 */
	public void add(ContentType Object){
		Node n = new Node(Object);
		if(tail!=null)tail.next = n;
		tail = n;
		if(head==null)head = n;
	}
	
	/**
	 * selects the first object in the Queue. If the Queue is empty a NullPointerExeption will be thrown
	 * @return the first object in the Queue
	 */
	public ContentType get(){
		return head.Object;
	}
	
	/**
	 * removes the first Object in the Queue
	 */
	public void remove(){
		if(head!=null){
			head=head.next;
			if(head==null)tail=null;
		}
	}
	
	/**
	 * checks whether the Queue has an object
	 * @return true if the Queue does not contain any objects, else false
	 */
	public boolean isEmpty(){
		return head==null;
	}

}
