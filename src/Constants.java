import java.util.ArrayList;
import java.util.Arrays;


public class Constants {
	public static ArrayList<String> occupation = new ArrayList<String>(Arrays.asList("Doctor", "Teacher",
			"Engineer","Lawyer","Business Man","Miscellaneous"));
	public static ArrayList<String> gender = new ArrayList<String>(Arrays.asList("Male", "Female"));
	public static int alleleValues[] = {0,1};
	public static int Age = 70;
	public static int nf = 5;
	public static double mutation_probability = 1/63;
	public static double crossover_probability = 0.8;
}
