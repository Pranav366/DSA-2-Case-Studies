import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShortBTreeSplit {
    
    static class Page {
        int id;
        List<String> keys = new ArrayList<>();
        Page next; // Pointer to next leaf page

        Page(int id) { this.id = id; }
    }

    public static void main(String[] args) {
        // 1. Create a simulated full page with 4 keys (Simulating max capacity)
        Page leftPage = new Page(101);
        leftPage.keys.addAll(Arrays.asList("Alpha", "Bravo", "Delta", "Echo"));
        String newKey = "Charlie"; // The key that forces the split

        System.out.println("Before Split (Page 101): " + leftPage.keys);

        // 2. Combine and sort everything together
        List<String> allKeys = new ArrayList<>(leftPage.keys);
        allKeys.add(newKey);
        allKeys.sort(String::compareTo); 

        // 3. Allocate a new page and split the data 50/50
        Page rightPage = new Page(102);
        int mid = allKeys.size() / 2;

        leftPage.keys = new ArrayList<>(allKeys.subList(0, mid));
        rightPage.keys = new ArrayList<>(allKeys.subList(mid, allKeys.size()));

        // 4. Realign the leaf pointers (Doubly-linked list adjustment)
        rightPage.next = leftPage.next;
        leftPage.next = rightPage;

        // 5. Output the results
        System.out.println("\n--- AFTER SPLIT ---");
        System.out.println("Left Page (101)  : " + leftPage.keys);
        System.out.println("Right Page (102) : " + rightPage.keys);
        System.out.println("Next Page Pointer: Page 101 now points to Page " + leftPage.next.id);
        System.out.println("Parent High Key  : \"" + rightPage.keys.get(0) + "\" (Sent to parent index node)");
    }
}