import java.util.ArrayList;


public class Chromosome implements Comparable<Chromosome> {
	int chromosomes[] = new int[63];
	double phenotype[] = new double[21];
	ArrayList<Neighbour> neighbourList = new ArrayList<Neighbour>();
	double fitness;
	
	@Override
	public int compareTo(Chromosome o) {
		// TODO Auto-generated method stub
		return (int) ((this.fitness - o.fitness)*100000);
	}
}
