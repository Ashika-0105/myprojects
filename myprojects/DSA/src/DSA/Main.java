package DSA;

class Node<T> {

	T key;
	Object value;
	int hash;
	Node<T> next;
	Node<T> before;
	Node<T> after;

	public Node(T data) {
		Object V = new Object();
		this.key = data;
		this.value = V;
		this.hash = (data == null) ? 0 : data.hashCode();
		this.next = null;
		this.before = null;
		this.after = null;

	}
}

class LinkedHashSet<T> {
	private Node[] table = new Node[16];

	private Node<T> head = null;
	private Node<T> tail = null;

	public void add(T data) {
		int index = data.hashCode() & table.length - 1;
		if (head == null) {
			Node<T> node = new Node(data);
			head = node;
			tail = node;
			table[index] = node;
		} else {
			Node<T> current = table[index];

			if (current == null) {
				Node<T> newNode = new Node<>(data);
				table[index] = newNode;

				newNode.before = tail;
				tail.after = newNode;
				tail = newNode;
			}

			else {
				while (current != null) {
					if (current.key.equals(data))
						return;

					if (current.next == null)
						break;
					current = current.next;
				}

				Node<T> newNode = new Node<>(data);
				current.next = newNode;

				newNode.before = tail;
				tail.after = newNode;
				tail = newNode;
			}
		}
	}

	public void print() {
		Node<T> current = head;
		System.out.print("[");

		while (current != null) {
			System.out.print(current.key);

			current = current.after;

			if (current != null) {
				System.out.print(", ");
			}
		}

		System.out.println("]");
	}

	public void remove(T data) {
		int index = data.hashCode() & (table.length - 1);
		Node<T> current = table[index];
		Node<T> prevInBucket = null;
		while (current != null) {
			if (current.key.equals(data)) {
				if (prevInBucket == null) {
					table[index] = current.next;
				} else {
					prevInBucket.next = current.next;

				}

				if (current.before != null) {
					current.before.after = current.after;
				} else {
					head = current.after;
				}

				if (current.after != null) {
					current.after.before = current.before;
				} else {
					tail = current.before;
				}

				return;
			}
			prevInBucket = current;
			current = current.next;
		}
	}

	public void addAll(LinkedHashSet<T> sets) {
		Node<T> current = sets.head;
		while (current != null) {
			add(current.key);
			current = current.after;
		}

	}

	public void addFirst(T data) {
		Node<T> currentele = new Node(data);
		Node previos = head;
		head.before = currentele;
		currentele.after = head;
		head = currentele;
	}

	public void addLast(T data) {
		Node<T> currentele = new Node(data);
		tail.after = currentele;
		currentele.before = tail;
		tail = currentele;
	}

	public boolean contains(T data) {
		Node<T> currentele = head;
		while (currentele != null) {
			if ((currentele.key).equals(data)) {
				return true;
			}
			currentele = currentele.after;
		}

		return false;
	}

	public boolean update(T olddata, T newdata) {
		if (contains(olddata)) {
			remove(olddata);
			add(newdata);
			return true;
		}
		return false;

	}

	public T getFirst() {

		return head.key;
	}

	public T getLast() {
		return tail.key;
	}

	public void sort() {
	    if (head == null || head.after == null) return;

	    boolean swapped;
	    do {
	        swapped = false;
	        Node<T> current = head;

	        while (current != null && current.after != null) {
	            Comparable<T> k1 = (Comparable<T>) current.key;
	            T k2 = current.after.key;

	            if (k1.compareTo(k2) > 0) {
	                	                T temp = current.key;
	                current.key = current.after.key;
	                current.after.key = temp;
	                
	                
	                swapped = true;
	            }
	            current = current.after;
	        }
	    } while (swapped); 
	}

	public class Main {
		public static void main(String[] args) {
			LinkedHashSet<Integer> sets = new LinkedHashSet<>();
			sets.add(1);
			sets.add(2);
			sets.add(3);
			sets.add(4);
			sets.print();
			LinkedHashSet<Integer> sets1 = new LinkedHashSet<>();
			sets1.add(5);
			sets1.add(6);
			sets1.add(7);
			sets1.add(8);
			sets1.print();
			sets.addAll(sets1);

			sets.print();
			sets.addFirst(267);
			sets.print();
			sets.addLast(2557);
			sets.print();
			System.out.println(sets.contains(2557));

		}
	}
}
