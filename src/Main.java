import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws IOException {
            InputStream inputStream = System.in;
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String[] inputArrayStr = reader.readLine().split(" ");
            int[] inputArray = new int[inputArrayStr.length];
            for (int i = 0; i < inputArrayStr.length; i++) {
                inputArray[i] = Integer.parseInt(inputArrayStr[i]);
            }
            ImplementerConundrumSolver implementerConundrumSolver = new ImplementerConundrumSolver();
            for (int i : implementerConundrumSolver.resolve(inputArray)) {
                System.out.print(i + " ");
            }

//            System.out.println();
//            System.out.println(implementerConundrumSolver.toString());
    }
}
