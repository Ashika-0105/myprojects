package DSA;

import java.util.LinkedHashSet;

public class main1 {
 
	public static void main(String[]args) {
		LinkedHashSet<Integer> sets = new LinkedHashSet<>();
		LinkedHashSet<Integer> sets1 = new LinkedHashSet<>();
		sets.add(1);
		sets.add(2);
		sets.add(3);
		sets.add(4);
		sets1.add(5);
		sets1.add(6);
		sets1.add(7);
		sets1.add(8);
		sets.addAll(sets1);
		System.out.println(sets);
		sets.addFirst(87);
		System.out.println(sets);
		System.out.println(sets.contains(1));
		System.out.println(sets.equals(sets1));
		System.out.println(sets.getFirst());
    }
}
