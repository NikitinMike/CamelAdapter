package camel.msg.adapter;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class Combiner {

    int amount;
    int[][] comb;
    String[] words;

    public Combiner(String str) {
        words = str.trim().split("\\s+");
        comb = new int[factorial(words.length)][words.length];
        IntStream.range(0, words.length).forEach(i -> comb[0][i] = i);
        amount = combiner(words.length);
    }

    int[] swap(int[] in, int a, int b) {
        int[] out = in.clone();
        out[b] = in[a];
        out[a] = in[b];
        return out;
    }

    public static <T> void swap(T[] a, int i, int j) {
        T t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    String out(int[] a) {
        return Arrays.stream(a).mapToObj(j -> words[j] + ' ').collect(Collectors.joining());
    }

    int combiner(int n) {
        if (n > 2) {
            int nf = combiner(n - 1);
            for (int i = 0; i < nf; i++)
                for (int j = 1; j < n; j++)
                    comb[nf * j + i] = swap(comb[nf * (j - 1) + i], n - j, n - j - 1);
            return nf * n;
        }
        // N=2
        comb[1] = swap(comb[0], 0, 1);
        return 2;
    }

    public String randomOut() {
//        for (String s:words) System.out.print(s+",");System.out.println(words.length);
        if (words.length > 3) return out(comb[(int) (Math.random() * words.length)]);
        return out(comb[(int) (Math.random() * 2)]);
//        return out(comb[(int)(Math.random()*amount)]);
    }

    int factorial(int n) {
        if (n > 0) return factorial(n - 1) * n;
        return 1;
    }

    void fullOut() { // System.out.println(comb.length);
//        for (int i = 0; i < comb.length; i++) log.info(i + " : " + out(comb[i]));
        for (int[] ints : comb) log.info(out(ints));
    }

}
