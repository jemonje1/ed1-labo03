package ed.lab;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class E01TopKFrequentElements {
    public int[] topKFrequent(int[] nums, int k) {
        int[] ans = new int[k];
        Map<Integer, Integer> map = new HashMap<>();
        PriorityQueue<Map.Entry<Integer, Integer>> minHeap = new PriorityQueue<>((a, b) -> Integer.compare(a. getValue(), b.getValue()));
        for (int num : nums) {
            map.put(num, map.getOrDefault(num, 0) + 1);
        }
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            minHeap.offer(entry);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }
        for (int i = k - 1; i >= 0; i--) {
            ans[i] = minHeap.poll().getKey();
        }
        return ans;
    }
}
